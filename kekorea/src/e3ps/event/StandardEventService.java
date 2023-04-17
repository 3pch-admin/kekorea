package e3ps.event;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import javax.imageio.ImageIO;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.pqscan.pdftoimage.PDFDocument;
import com.ptc.wvs.server.publish.PublishServiceEvent;
import com.ptc.wvs.server.util.PublishUtils;
import com.sun.media.jai.codec.TIFFEncodeParam;
import com.sun.media.jai.codecimpl.TIFFImageEncoder;

import e3ps.common.util.CommonUtils;
import e3ps.common.util.ContentUtils;
import e3ps.common.util.IBAUtils;
import e3ps.project.output.Output;
import e3ps.project.output.OutputDocumentLink;
import e3ps.project.output.OutputTaskLink;
import e3ps.project.service.Schedulers;
import e3ps.project.task.Task;
import e3ps.project.task.variable.TaskStateVariable;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceManagerEvent;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.iba.value.IBAHolder;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.LifeCycleServiceEvent;
import wt.lifecycle.LifeCycleTemplate;
import wt.lifecycle.State;
import wt.part.WTPartUsageLink;
import wt.pom.Transaction;
import wt.representation.Representation;
import wt.services.ManagerException;
import wt.services.StandardManager;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.Mastered;
import wt.vc.VersionControlHelper;
import wt.vc.VersionControlServiceEvent;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.WorkInProgressServiceEvent;
import wt.vc.wip.Workable;

public class StandardEventService extends StandardManager implements EventService {

	private static final long serialVersionUID = 3410926060596786585L;

	private static final String PRE_CHECKIN = WorkInProgressServiceEvent.PRE_CHECKIN;

	private static final String POST_STORE = PersistenceManagerEvent.POST_STORE;
	private static final String POST_CHECKIN = WorkInProgressServiceEvent.POST_CHECKIN;
	private static final String POST_MODIFY = PersistenceManagerEvent.POST_MODIFY;

	private static final String NEW_VERSION = VersionControlServiceEvent.NEW_VERSION;
	private static final String NEW_ITERATION = VersionControlServiceEvent.NEW_ITERATION;

	private static final String PUBLISH_SUCCESSFUL = PublishServiceEvent.PUBLISH_SUCCESSFUL;

	private static final String STATE_CHANGE = LifeCycleServiceEvent.STATE_CHANGE;

	private static String temp = null;

	public static StandardEventService newStandardEventService() throws WTException {
		StandardEventService instance = new StandardEventService();
		instance.initialize();
		return instance;
	}

	protected synchronized void performStartupProcess() throws ManagerException {
		super.performStartupProcess();
		try {
			JobDetail job = JobBuilder.newJob(Schedulers.class).withIdentity("dummyJobName", "group1").build();
			Trigger trigger = TriggerBuilder.newTrigger().withIdentity("dummyTriggerName", "group1")
					.withSchedule(CronScheduleBuilder.cronSchedule("0 0 2 * * ?")).build();

			// schedule it
			Scheduler scheduler = new StdSchedulerFactory().getScheduler();
			scheduler.start();
			scheduler.scheduleJob(job, trigger);
			temp = WTProperties.getLocalProperties().getProperty("wt.home") + File.separator + "temp" + File.separator
					+ "images";
		} catch (Exception e) {
			e.printStackTrace();
		}

		File dir = new File(temp);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		EventListener listener = new EventListener(StandardEventService.class.getName());

		getManagerService().addEventListener(listener, WorkInProgressServiceEvent.generateEventKey(PRE_CHECKIN));
		getManagerService().addEventListener(listener, PersistenceManagerEvent.generateEventKey(POST_STORE));
		getManagerService().addEventListener(listener, WorkInProgressServiceEvent.generateEventKey(POST_CHECKIN));
		getManagerService().addEventListener(listener, PersistenceManagerEvent.generateEventKey(POST_MODIFY));
		getManagerService().addEventListener(listener, VersionControlServiceEvent.generateEventKey(NEW_VERSION));
		getManagerService().addEventListener(listener, VersionControlServiceEvent.generateEventKey(NEW_ITERATION));
		getManagerService().addEventListener(listener, PublishServiceEvent.generateEventKey(PUBLISH_SUCCESSFUL));
		getManagerService().addEventListener(listener, LifeCycleServiceEvent.generateEventKey(STATE_CHANGE));
	}

	@Override
	public void createVersion(Workable workable) throws WTException {

		Transaction trs = new Transaction();
		try {
			trs.start();

			// 복사본 객체 이면 패스

			if (WorkInProgressHelper.isWorkingCopy(workable)) {
				System.out.println("복사본 객체 입니다.");
				// return;
			}

			// 객체가 체크 아웃 되어 있으면 패스
			if (WorkInProgressHelper.isCheckedOut(workable)) {
				System.out.println("객체가 체크아웃 상태 입니다.");
				// return;
			}

			if (!CommonUtils.isLatestVersion(workable)) {
				System.out.println("최신 버전의 객체가 아닙니다.");
				workable = (Workable) CommonUtils.getLatestVersion(workable);
				// return;
			}

			// 이전 + 현재 모든 버전 IBA삭제
			QueryResult result = VersionControlHelper.service.allIterationsOf((Mastered) workable.getMaster());
			while (result.hasMoreElements()) {
				IBAHolder iba = (IBAHolder) result.nextElement();
				IBAUtils.deleteIBA(iba, "LatestVersion", "string");
			}
			// 현재 객체에만 버전 IBA 생성
			workable = (Workable) PersistenceHelper.manager.refresh(workable);
			IBAUtils.createIBA((IBAHolder) workable, "string", "LatestVersion", "true");

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public void publishToImage(Workable workable) throws WTException {
		EPMDocument epm = (EPMDocument) workable;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			if (!epm.getDocType().toString().equals("CADDRAWING")) {
				return;
			}
			Representation representation = PublishUtils.getRepresentation(epm);
			if (representation != null) {

				// 첨부 이미지들 부터 삭제
				Vector<String[]> list = ContentUtils.getImages(epm);
				for (String[] s : list) {
					String oid = s[1];
					ApplicationData data = (ApplicationData) rf.getReference(oid).getObject();
					ContentServerHelper.service.deleteContent(epm, data);
				}

				QueryResult result = ContentHelper.service.getContentsByRole(representation,
						ContentRoleType.ADDITIONAL_FILES);

				if (result.hasMoreElements()) {
					ApplicationData data = (ApplicationData) result.nextElement();

					InputStream is = ContentServerHelper.service.findLocalContentStream(data);
					PDFDocument doc = new PDFDocument();
					doc.loadPDF(is);
					int pageCount = doc.getPageCount();

					String path = "";
					ApplicationData toImage = null;
					for (int i = 0; i < pageCount; i++) {
						BufferedImage image = doc.toImage(i);
						// save image as tiff format
						path = epm.getCADName() + i + ".tif";

						OutputStream out = new FileOutputStream(path);
						TIFFEncodeParam param = new TIFFEncodeParam();
						param.setCompression(TIFFEncodeParam.COMPRESSION_DEFLATE);
						TIFFImageEncoder encoder = new TIFFImageEncoder(out, param);
						encoder.encode(image);
						out.close();

						toImage = ApplicationData.newApplicationData(epm);
						toImage.setRole(ContentRoleType.toContentRoleType("IMAGE"));
						toImage.setCreatedBy(SessionHelper.manager.getPrincipalReference());
						toImage.setCategory("이미지 파일 (TIF)");
						ContentServerHelper.service.updateContent(epm, toImage, path);
					}

					for (int i = 0; i < pageCount; i++) {
						BufferedImage image = doc.toImage(i);
						// save image as jpg format
						path = epm.getCADName() + i;
						ImageIO.write(image, "jpg", new File(path + ".jpg"));

						toImage = ApplicationData.newApplicationData(epm);
						toImage.setRole(ContentRoleType.toContentRoleType("IMAGE"));
						toImage.setCreatedBy(SessionHelper.manager.getPrincipalReference());
						toImage.setCategory("이미지 파일 (JPG)");
						ContentServerHelper.service.updateContent(epm, toImage, path + ".jpg");

						// save image as gif format
						path = epm.getCADName() + i;
						ImageIO.write(image, "gif", new File(path + ".gif"));

						toImage = ApplicationData.newApplicationData(epm);
						toImage.setRole(ContentRoleType.toContentRoleType("IMAGE"));
						toImage.setCreatedBy(SessionHelper.manager.getPrincipalReference());
						toImage.setCategory("이미지 파일 (GIF)");
						ContentServerHelper.service.updateContent(epm, toImage, path + ".gif");

						// save image as bmp format
						path = epm.getCADName() + i;
						ImageIO.write(image, "bmp", new File(path + ".bmp"));

						toImage = ApplicationData.newApplicationData(epm);
						toImage.setRole(ContentRoleType.toContentRoleType("IMAGE"));
						toImage.setCreatedBy(SessionHelper.manager.getPrincipalReference());
						toImage.setCategory("이미지 파일 (BMP)");
						ContentServerHelper.service.updateContent(epm, toImage, path + ".bmp");

						// save image as png format
						path = epm.getCADName() + i;
						ImageIO.write(image, "png", new File(path + ".png"));

						toImage = ApplicationData.newApplicationData(epm);
						toImage.setRole(ContentRoleType.toContentRoleType("IMAGE"));
						toImage.setCreatedBy(SessionHelper.manager.getPrincipalReference());
						toImage.setCategory("이미지 파일 (PNG)");
						ContentServerHelper.service.updateContent(epm, toImage, path + ".png");
					}
				}
			}

			// 삭제..

			File dir = new File(temp);
			File[] fs = dir.listFiles();
			for (int i = 0; i < fs.length; i++) {
				System.out.println("첨부 파일들 삭제..");
				fs[i].delete();
			}

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public void setFindNumber(WTPartUsageLink link) throws WTException {
		Transaction trs = new Transaction();

		try {
			trs.start();

//			WTPart child = link.getUsedBy();
//			WTPartMaster parent = link.getUses();

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public void preCheckInValidate(Workable target) throws WTException {
		Transaction trs = new Transaction();

		EPMDocument epm = (EPMDocument) target;

		try {
			trs.start();

			String first = epm.getNumber().substring(0, 1);
			if (!"A".equalsIgnoreCase(first)) {
				System.out.println("시작..");
				// throw new Exception("규칙 위반 번호가 A로 시작 안함..");
			}

			trs.commit();
			trs = null;
		} catch (Exception e) {
			// throw new WTException("규칙 위반 번호가 A로 시작 안함..");
			// trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public void check(Workable target) throws WTException {
		Transaction trs = new Transaction();

//		EPMDocument epm = (EPMDocument) target;

		try {
			trs.start();

//			String ver = epm.getVersionIdentifier().getSeries().getValue();
//			String iter = epm.getIterationIdentifier().getSeries().getValue();

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public void checkTask(LifeCycleManaged lcm) throws WTException {
		WTDocument document = (WTDocument) lcm;
		boolean isChange = true;
		Transaction trs = new Transaction();
		try {
			trs.start();

			QueryResult result = PersistenceHelper.manager.navigate(document, "output", OutputDocumentLink.class,
					false);
			while (result.hasMoreElements()) {
				OutputDocumentLink link = (OutputDocumentLink) result.nextElement();
				Output output = link.getOutput();
				LifeCycleManaged dd = link.getDocument();

				if (!dd.getLifeCycleState().toString().equals("APPROVED")
						&& !dd.getLifeCycleState().toString().equals("RELEASED")) {
					isChange = false;
					break;
				}

				if (isChange) {
					QueryResult qr = PersistenceHelper.manager.navigate(output, "task", OutputTaskLink.class);
					if (qr.hasMoreElements()) {
						Task task = (Task) qr.nextElement();
						task.setState(TaskStateVariable.COMPLETE);
						PersistenceHelper.manager.modify(task);
					}
				}
			}

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public void changeToName(Workable workable) throws WTException {
		EPMDocument epm = (EPMDocument) workable;
		Transaction trs = new Transaction();
		try {
			trs.start();

			if (!epm.getDocType().toString().equals("CADDRAWING")) {
				return;
			}
			Representation representation = PublishUtils.getRepresentation(epm);
			ApplicationData appData = null;
//			Vector<?> appDatas = null;
			String fileName = null;
			String fileExt = null;
			String postfix = null;
			InputStream is = null;

//			appDatas = ContentHelper.getContentList(representation);

			QueryResult result = ContentHelper.service.getContentsByRole(representation, ContentRoleType.SECONDARY);
			while (result.hasMoreElements()) {
				// ApplicationData data = (ApplicationData) result.nextElement();
				// for (int i = 0; i < appDatas.size(); i++) {
				appData = (ApplicationData) result.nextElement();
				String sFileName = epm.getCADName();

				if (appData.getRole() != null && (appData.getRole().equals(ContentRoleType.ADDITIONAL_FILES)
						|| appData.getRole().equals(ContentRoleType.SECONDARY))) {
					fileName = appData.getFileName();
					postfix = fileName.substring(fileName.lastIndexOf("_") + 1);
					fileExt = fileName.substring(fileName.lastIndexOf(".") + 1);

					if (fileExt.equals("zip")) {
						// sFileName = sFileName.substring(0, sFileName.lastIndexOf(".")) + "_" +
						// iterationInfo + "_" + postfix;
						sFileName = sFileName.substring(0, sFileName.lastIndexOf(".")) + postfix;
					} else {
						// sFileName = sFileName.substring(0, sFileName.lastIndexOf(".")) + "_" +
						// iterationInfo + "." + fileExt;
						sFileName = sFileName.substring(0, sFileName.lastIndexOf(".")) + "." + fileExt;
					}

					appData.setFileName(sFileName.toUpperCase());

					is = ContentServerHelper.service.findContentStream(appData);
					ContentServerHelper.service.updateContent(representation, appData, is);
				}
			}

			result.reset();
			result = ContentHelper.service.getContentsByRole(representation, ContentRoleType.ADDITIONAL_FILES);
			while (result.hasMoreElements()) {
				// ApplicationData data = (ApplicationData) result.nextElement();
				// for (int i = 0; i < appDatas.size(); i++) {
				appData = (ApplicationData) result.nextElement();
				String sFileName = epm.getCADName();

				if (appData.getRole() != null && (appData.getRole().equals(ContentRoleType.ADDITIONAL_FILES)
						|| appData.getRole().equals(ContentRoleType.SECONDARY))) {
					fileName = appData.getFileName();
					postfix = fileName.substring(fileName.lastIndexOf("_") + 1);
					fileExt = fileName.substring(fileName.lastIndexOf(".") + 1);

					if (fileExt.equals("zip")) {
						// sFileName = sFileName.substring(0, sFileName.lastIndexOf(".")) + "_" +
						// iterationInfo + "_" + postfix;
						sFileName = sFileName.substring(0, sFileName.lastIndexOf(".")) + postfix;
					} else {
						// sFileName = sFileName.substring(0, sFileName.lastIndexOf(".")) + "_" +
						// iterationInfo + "." + fileExt;
						sFileName = sFileName.substring(0, sFileName.lastIndexOf(".")) + "." + fileExt;
					}

					appData.setFileName(sFileName.toUpperCase());

					is = ContentServerHelper.service.findContentStream(appData);
					ContentServerHelper.service.updateContent(representation, appData, is);
				}
			}
			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public void setApprovalLine(Workable workable) throws WTException {
		SessionContext prev = SessionContext.newContext();

		Transaction trs = new Transaction();
		try {

			trs.start();

			SessionHelper.manager.setAdministrator();

			trs.commit();
			trs = null;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
			SessionContext.setContext(prev);
		}
	}

	@Override
	public void replaceLifeCycle(Workable workable) throws WTException {
//		SessionContext prev = SessionContext.newContext();
//
//		Transaction trs = new Transaction();
//		try {
//
//			trs.start();
//
//			SessionHelper.manager.setAdministrator();
//
//			EPMDocument epm = (EPMDocument) workable;
//
//			String ss = epm.getLifeCycleState().toString();
//
//			State state = State.toState(ss);
//
//			System.out.println("ss=" + ss);
//
//			boolean isLatest = CommonUtils.isLatestVersion(epm);
//			if (!isLatest) {
//				epm = (EPMDocument) CommonUtils.getLatestVersion(epm);
//				epm = (EPMDocument) PersistenceHelper.manager.refresh(epm);
//			}
//
//			LifeCycleTemplate lct = LifeCycleHelper.service.getLifeCycleTemplate(MigratorHelper.LIFECYCLE_NAME,
//					CommonUtils.getContainer());
//
//			System.out.println("라이프 사이클 변경...");
//			LifeCycleHelper.service.reassign((LifeCycleManaged) epm, lct.getLifeCycleTemplateReference());
//
//			epm = (EPMDocument) PersistenceHelper.manager.refresh(epm);
//			System.out.println("라이프 상태값 변경...");
//			LifeCycleHelper.service.setLifeCycleState(epm, state);
//
//			trs.commit();
//			trs = null;
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (trs != null)
//				trs.rollback();
//			SessionContext.setContext(prev);
//		}
	}
}