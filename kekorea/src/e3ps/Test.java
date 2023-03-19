package e3ps;

import java.io.File;
import java.io.FileOutputStream;

import com.aspose.pdf.Document;
import com.aspose.pdf.devices.PngDevice;
import com.aspose.pdf.devices.Resolution;

import e3ps.common.aspose.AsposeUtils;

public class Test {

	public static void main(String[] args) throws Exception {

//		PDDocument document = PDDocument.load(new File("D:" + File.separator + "2.pdf"));
//		PDFRenderer renderer = new PDFRenderer(document);
//		BufferedImage image = renderer.renderImage(0);
//		ImageIO.write(image, "PNG", new File("D:" + File.separator + "output.png"));

		AsposeUtils.setAsposeLic();
		Document pdfDocument = new Document("D:" + File.separator + "2.pdf");

		// PDF를 이미지로 변환합니다.
		for (int pageCount = 1; pageCount <= pdfDocument.getPages().size(); pageCount++) {
			FileOutputStream imageStream = new FileOutputStream("D:" + File.separator + "output_" + pageCount + ".png");
			// 페이지를 이미지로 저장합니다.
			Resolution resolution = new Resolution(300);
			PngDevice pngDevice = new PngDevice(resolution);
			pngDevice.process(pdfDocument.getPages().get_Item(pageCount), imageStream);
		}

		pdfDocument.close();
		System.out.println("종료,,,:");
		System.exit(0);
	}
}