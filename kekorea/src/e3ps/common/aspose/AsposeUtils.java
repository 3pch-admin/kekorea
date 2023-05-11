package e3ps.common.aspose;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import com.aspose.pdf.Document;
import com.aspose.pdf.License;
import com.aspose.pdf.Page;
import com.aspose.pdf.devices.PngDevice;
import com.aspose.pdf.devices.Resolution;
import com.ptc.wvs.server.util.PublishUtils;

import e3ps.common.util.CommonUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.epm.keDrawing.KeDrawing;
import e3ps.epm.workOrder.WorkOrder;
import e3ps.epm.workOrder.WorkOrderDataLink;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.representation.Representation;
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

	/**
	 * PDF 병합 기능
	 */
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
		QuerySpecUtils.toEqualsAnd(query, idx_link, WorkOrderDataLink.class, "roleAObjectRef.key.id", workOrder);
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
			} else if (per instanceof EPMDocument) {
				EPMDocument epm = (EPMDocument) per;
				Representation representation = PublishUtils.getRepresentation(epm);
				if (representation != null) {
					QueryResult qr = ContentHelper.service.getContentsByRole(representation, ContentRoleType.SECONDARY);
					if (qr.hasMoreElements()) {
						ApplicationData data = (ApplicationData) qr.nextElement();
						byte[] buffer = new byte[10240];
						InputStream is = ContentServerHelper.service.findLocalContentStream(data);
						String name = epm.getNumber();
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
		dd.setRole(ContentRoleType.THUMBNAIL);
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

	public static void pdfToImage(Hashtable<String, String> hash) throws Exception {
		System.out.println("도면 일람표 PDF 이미지 변환 시작 = " + new Timestamp(new Date().getTime()));

		String oid = hash.get("oid");
		String pdfPath = hash.get("pdfPath");

		KeDrawing keDrawing = (KeDrawing) CommonUtils.getObject(oid);

		Document pdfDocument = null;

		String thumbnailPath = WTProperties.getLocalProperties().getProperty("wt.temp") + File.separator + "thumbnail";
		File thumbnailFolder = new File(thumbnailPath);
		if (!thumbnailFolder.exists()) {
			thumbnailFolder.mkdirs();
		}

		QueryResult result = ContentHelper.service.getContentsByRole(keDrawing, ContentRoleType.THUMBNAIL);
		if (result.hasMoreElements()) {
			ContentItem item = (ContentItem) result.nextElement();
			ContentServerHelper.service.deleteContent(keDrawing, item);
		}

		File pdf = new File(pdfPath);
		AsposeUtils.setAsposeLic();

		pdfDocument = new Document(new FileInputStream(pdf));
		String imagePath = thumbnailPath + File.separator + keDrawing.getMaster().getKeNumber() + ".png";

		// PDF를 이미지로 변환합니다.
		FileOutputStream imageStream = new FileOutputStream(imagePath);
		// 페이지를 이미지로 저장합니다.
		Resolution resolution = new Resolution(300);
		PngDevice pngDevice = new PngDevice(resolution);
		pngDevice.process(pdfDocument.getPages().get_Item(1), imageStream);

		BufferedImage image = ImageIO.read(new File(imagePath));

		Page pdfPage = pdfDocument.getPages().get_Item(1);
		double pdfPageWidth = pdfPage.getPageInfo().getWidth();
		double pdfPageHeight = pdfPage.getPageInfo().getHeight();

		BufferedImage resizedImage = new BufferedImage((int) pdfPageWidth, (int) pdfPageHeight, image.getType());
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(image, 0, 0, (int) pdfPageWidth, (int) pdfPageHeight, null);
		g.dispose();

		// 크기가 변경된 이미지 파일을 저장합니다.
		FileOutputStream outputStream = new FileOutputStream(imagePath);
		ImageIO.write(resizedImage, "png", outputStream);
		outputStream.close();

		ApplicationData data = ApplicationData.newApplicationData(keDrawing);
		data.setRole(ContentRoleType.THUMBNAIL);
		PersistenceHelper.manager.save(data);
		ContentServerHelper.service.updateContent(keDrawing, data, imagePath);

		pdfDocument.close();

		File[] files = thumbnailFolder.listFiles();
		for (File ff : files) {
			System.out.println("삭제되는 파일들 = " + ff.getName());
			ff.delete();
		}

		System.out.println("도면 일람표 PDF 이미지 변환 종료 = " + new Timestamp(new Date().getTime()));
	}
}
