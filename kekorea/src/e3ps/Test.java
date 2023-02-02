package e3ps;

import com.aspose.pdf.Document;

import e3ps.common.aspose.AsposeUtils;

public class Test {

	public static void main(String[] args) throws Exception {

//		AsposeUtils.setAsposeLic();
		
        Document pdfDocument1 = new Document("D:\\1.pdf");
        Document pdfDocument2 = new Document("D:\\2.pdf");
        Document pdfDocument3 = new Document("D:\\3.pdf");
        Document pdfDocument4 = new Document("D:\\4.pdf");
        Document pdfDocument5 = new Document("D:\\5.pdf");
        Document pdfDocument6 = new Document("D:\\6.pdf");

        // Add pages of second document to the first
        pdfDocument1.getPages().add(pdfDocument2.getPages());
        pdfDocument1.getPages().add(pdfDocument3.getPages());
        pdfDocument1.getPages().add(pdfDocument4.getPages());
//        pdfDocument1.getPages().add(pdfDocument5.getPages());
//        pdfDocument1.getPages().add(pdfDocument6.getPages());

        // Save concatenated output file
        pdfDocument1.save("D:\\oncatenatePdfFiles_out.pdf");

        System.out.println("정ㅇ");
	}

}
