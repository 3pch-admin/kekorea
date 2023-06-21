package e3ps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.aspose.cells.FileFormatType;

public class Test2 {

	public static void main(String[] args) throws Exception {

		try {
			// 엑셀 파일 읽기
			FileInputStream excelFile = new FileInputStream(new File("D:" + File.separator + "test1.xls"));
			File pdfFile = new File("D:" + File.separator + "output.pdf");
			com.aspose.cells.Workbook wb = new com.aspose.cells.Workbook(excelFile);
			FileOutputStream fospdf = new FileOutputStream(pdfFile);
			wb.save(fospdf, FileFormatType.PDF);

			System.out.println("Excel 파일이 PDF로 변환되었습니다.");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}