package e3ps.event;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Hashtable;

import e3ps.common.convert.ProcessOutputThread;
import e3ps.common.util.CommonUtils;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.epm.EPMDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTPrincipal;
import wt.queue.ProcessingQueue;
import wt.queue.QueueHelper;
import wt.services.ServiceFactory;
import wt.session.SessionHelper;
import wt.util.WTProperties;

public class EventHelper {

	public static final EventService service = ServiceFactory.getService(EventService.class);
	public static final EventHelper manager = new EventHelper();

	private static final String processQueueName = "ConvertToPdfProcessQueue";
	private static final String className = "e3ps.event.EventHelper";
	private static final String methodName = "convertAutoCADToPdf";

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

	/**
	 * 오토캐드 PDF 변환 큐 작업
	 */
	public void autoCadAfterAction(EPMDocument epm) throws Exception {
		WTPrincipal principal = SessionHelper.manager.getPrincipal();
		ProcessingQueue queue = (ProcessingQueue) QueueHelper.manager.getQueue(processQueueName, ProcessingQueue.class);
		Hashtable<String, String> hash = new Hashtable<>();
		hash.put("oid", epm.getPersistInfo().getObjectIdentifier().getStringValue());

		Class[] argClasses = { Hashtable.class };
		Object[] argObjects = { hash };

		queue.addEntry(principal, methodName, className, argClasses, argObjects);
	}

	/**
	 * 오토 캐드 PDF 변환 큐 실행
	 */
	public static void convertAutoCADToPdf(Hashtable<String, String> hash) throws Exception {
		String oid = hash.get("oid");
		EPMDocument epm = (EPMDocument) CommonUtils.getObject(oid);

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

		String version = CommonUtils.getFullVersion(epm);
		String dwgFilePath = savePath + File.separator + epm.getCADName();
		String pdfFileName = epm.getCADName().substring(0, epm.getCADName().lastIndexOf(".")) + version + ".pdf";

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
		PersistenceHelper.manager.save(dd);
		ContentServerHelper.service.updateContent(epm, dd, savePath + File.separator + pdfFileName);
	}
}
