package e3ps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import wt.util.WTProperties;

public class Test {

	public static void main(String[] args) throws Exception {

//		String numberRulePath = WTProperties.getLocalProperties().getProperty("wt.codebase.location") + File.separator
//				+ "extcore" + File.separator + "excelTemplate" + File.separator + "WORKORDER-TEMPLATE.xlsx";
//		FileInputStream fis = new FileInputStream(numberRulePath);
//		Workbook workbook = new XSSFWorkbook(fis);
		
		  Workbook workbook = new XSSFWorkbook();

		// 엑셀 시트 생성
		for (int i = 1; i <= 5; i++) {
			String sheetName = "Sheet" + i;
			Sheet sheet = workbook.createSheet(sheetName);

			// 셀에 데이터 작성 예시
			Row row = sheet.createRow(0);
			Cell cell = row.createCell(0);
			cell.setCellValue("Hello, Sheet " + i);
		}

		// 생성된 워크북을 파일로 저장
		try (FileOutputStream outputStream = new FileOutputStream("D:" + File.separator + "output.xlsx")) {
			workbook.write(outputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 워크북 닫기
		try {
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("종료!");
	}
}