package e3ps.common.aspose;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import com.aspose.pdf.Document;
import com.aspose.pdf.License;

import e3ps.common.util.CommonUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.epm.keDrawing.KeDrawing;
import e3ps.epm.workOrder.WorkOrder;
import e3ps.epm.workOrder.WorkOrderDataLink;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.util.WTAttributeNameIfc;
import wt.util.WTProperties;

public class AsposeUtils {

	private static final String licPath = "D:\\ptc\\lic\\Aspose.Pdf.Java.lic";

	private AsposeUtils() {

	}

	public static void setAsposeLic() throws Exception {
		License license = new License();
		license.setLicense(licPath);
	}

	public static void attachMergePdf(Hashtable<String, String> hash) throws Exception {

		System.out.println("도면 일람표 PDF 병합 시작 = " + new Timestamp(new Date().getTime()));

		String tempDir = WTProperties.getLocalProperties().getProperty("wt.temp");
		String mergePath = tempDir + File.separator + "merge";
		File mergeDir = new File(mergePath);
		if (!mergeDir.exists()) {
			mergeDir.mkdirs();
		}

		String oid = hash.get("oid");
		WorkOrder workOrder = (WorkOrder) CommonUtils.getObject(oid);

		// pdf 경로..
		ArrayList<String> list = new ArrayList<>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WorkOrder.class, true);
		int idx_link = query.appendClassList(WorkOrderDataLink.class, true);

		QuerySpecUtils.toInnerJoin(query, WorkOrder.class, WorkOrderDataLink.class, WTAttributeNameIfc.ID_NAME,
				"roleAObjectRef.key.id", idx, idx_link);
		QuerySpecUtils.toEqualsAnd(query, idx_link, WorkOrderDataLink.class, "roleAObjectRef.key.id",
				workOrder.getPersistInfo().getObjectIdentifier().getId());
		QuerySpecUtils.toOrderBy(query, idx_link, WorkOrderDataLink.class, WorkOrderDataLink.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WorkOrderDataLink link = (WorkOrderDataLink) obj[1];
			Persistable per = link.getData();
			// 객체 구별..

			if (per instanceof KeDrawing) {
				KeDrawing keDrawing = (KeDrawing) per;
				QueryResult qr = ContentHelper.service.getContentsByRole(keDrawing, ContentRoleType.PRIMARY);
				if (qr.hasMoreElements()) {
					ApplicationData data = (ApplicationData) qr.nextElement();
					byte[] buffer = new byte[10240];
					InputStream is = ContentServerHelper.service.findLocalContentStream(data);
					String name = data.getFileName();
					String fullPath = mergePath + File.separator + name;
					File file = new File(fullPath);
					list.add(fullPath); // 경로 추가
					FileOutputStream fos = new FileOutputStream(file);
					int j = 0;
					while ((j = is.read(buffer, 0, 10240)) > 0) {
						fos.write(buffer, 0, j);
					}
					fos.close();
					is.close();
				}
			}
		}

		setAsposeLic(); // set lic..
		// pdf merge after attach file...

		String first = list.get(0);
		Document firstPdf = new Document(first);
		list.remove(0);

		for (String path : list) {
			Document pdf = new Document(path);
			firstPdf.getPages().add(pdf.getPages());
			pdf.close();
		}

		String mergePdfPath = mergePath + File.separator + workOrder.getName() + ".pdf";
		firstPdf.save(mergePdfPath);

		ApplicationData dd = ApplicationData.newApplicationData(workOrder);
		dd.setRole(ContentRoleType.PRIMARY);
		PersistenceHelper.manager.save(dd);
		ContentServerHelper.service.updateContent(workOrder, dd, mergePdfPath);

		firstPdf.close();

		File[] files = mergeDir.listFiles();
		for (File ff : files) {
			System.out.println("삭제되는 파일들 = " + ff.getName());
			ff.delete();
		}
		System.out.println("도면 일람표 PDF 병합 종료 = " + new Timestamp(new Date().getTime()));
	}
}
