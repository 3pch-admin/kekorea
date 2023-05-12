package e3ps.event;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.ptc.wvs.server.publish.PublishServiceEvent;

import e3ps.common.convert.ProcessOutputThread;
import e3ps.project.output.Output;
import e3ps.project.output.OutputDocumentLink;
import e3ps.project.output.OutputTaskLink;
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
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.LifeCycleServiceEvent;
import wt.pom.Transaction;
import wt.services.ManagerException;
import wt.services.StandardManager;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.VersionControlServiceEvent;
import wt.vc.wip.WorkInProgressServiceEvent;

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

	private static String savePath = null;
	static {
		try {
			if (savePath == null) {
				savePath = WTProperties.getServerProperties().getProperty("wt.temp") + File.separator + "kekorea";
				File tempFolder = new File(savePath);
				if (!tempFolder.exists()) {
					tempFolder.mkdirs();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static StandardEventService newStandardEventService() throws WTException {
		StandardEventService instance = new StandardEventService();
		instance.initialize();
		return instance;
	}

	protected synchronized void performStartupProcess() throws ManagerException {
		super.performStartupProcess();
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
	public void detectTask(LifeCycleManaged lcm) throws Exception {
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
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public void convertAutoCADToPDF(EPMDocument epm) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			QueryResult result = ContentHelper.service.getContentsByRole(epm, ContentRoleType.PRIMARY);
			// dwg 파일 템프 폴더 다운로드..
			if (result.hasMoreElements()) {
				ApplicationData data = (ApplicationData) result.nextElement();
				byte[] buffer = new byte[10240];
				InputStream is = ContentServerHelper.service.findLocalContentStream(data);
				File write = new File(savePath + File.separator + epm.getCADName());
				FileOutputStream fos = new FileOutputStream(write);
				int j = 0;
				while ((j = is.read(buffer, 0, 10240)) > 0) {
					fos.write(buffer, 0, j);
				}
				fos.close();
				is.close();
			}

			String dwgFilePath = savePath + File.separator + epm.getCADName();
			String pdfFileName = epm.getCADName().substring(0, epm.getCADName().lastIndexOf(".")) + ".pdf";

			Runtime rt = Runtime.getRuntime();

			String exec1 = "C:\\Program Files (x86)\\AutoDWG\\AutoDWG DWG to PDF Converter 2023\\d2p.exe /InFile";
			String conFile = "C:\\\\Program Files (x86)\\\\AutoDWG\\\\AutoDWG DWG to PDF Converter 2023\\\\AutoDWGPdf.ddp";

			String exec = exec1 + " " + dwgFilePath + " /OutFile " + pdfFileName + " /InConfigFile " + conFile;
			System.out.println(exec);

			Process p = rt.exec(exec);
			ProcessOutputThread o = new ProcessOutputThread(p.getInputStream(), new StringBuffer());
			o.start();
			p.waitFor();

			ApplicationData dd = ApplicationData.newApplicationData(epm);
			dd.setRole(ContentRoleType.SECONDARY);
			ContentServerHelper.service.updateContent(epm, dd, savePath + File.separator + pdfFileName);

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}
}