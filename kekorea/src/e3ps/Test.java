package e3ps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Test {

	public static void main(String[] args) throws Exception {

//		String numberRulePath = WTProperties.getLocalProperties().getProperty("wt.codebase.location") + File.separator
//				+ "extcore" + File.separator + "excelTemplate" + File.separator + "WORKORDER-TEMPLATE.xlsx";
//		FileInputStream fis = new FileInputStream(numberRulePath);
//		Workbook workbook = new XSSFWorkbook(fis);

		Workbook workbook = new XSSFWorkbook();

		// 헤더 스타일
		CellStyle headerStyle = workbook.createCellStyle();
		// name font
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerFont.setUnderline(Font.U_SINGLE);
		headerFont.setFontHeightInPoints((short) 20);
		headerStyle.setFont(headerFont);

		// 정렬 설정
		headerStyle.setAlignment(HorizontalAlignment.CENTER);
		headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		// 경계선 설정
		headerStyle.setBorderTop(BorderStyle.NONE);
		headerStyle.setBorderBottom(BorderStyle.NONE);
		headerStyle.setBorderLeft(BorderStyle.NONE);
		headerStyle.setBorderRight(BorderStyle.NONE);

		CellStyle noneBorderStyl = workbook.createCellStyle();

		// 경계선 설정
		noneBorderStyl.setBorderTop(BorderStyle.NONE);
		noneBorderStyl.setBorderBottom(BorderStyle.NONE);
		noneBorderStyl.setBorderLeft(BorderStyle.NONE);
		noneBorderStyl.setBorderRight(BorderStyle.NONE);

		// 데이터 헤더 스타일
		CellStyle dataHeaderStyle = workbook.createCellStyle();
		dataHeaderStyle.setWrapText(true);
		dataHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
		dataHeaderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		dataHeaderStyle.setBorderTop(BorderStyle.MEDIUM);
		dataHeaderStyle.setBorderBottom(BorderStyle.MEDIUM);
		dataHeaderStyle.setBorderLeft(BorderStyle.MEDIUM);
		dataHeaderStyle.setBorderRight(BorderStyle.MEDIUM);

		Font dataHeaderFont = workbook.createFont();
		dataHeaderFont.setBold(true);
		dataHeaderStyle.setFont(dataHeaderFont);

		int columnWidth = 150;
		// 엑셀 시트 생성
		for (int i = 1; i <= 5; i++) {
			String sheetName = "Sheet" + i;

			Sheet sheet = workbook.createSheet(sheetName);
			sheet.setDisplayGridlines(false);
			// 헤더 머지
			CellRangeAddress headerMerge = new CellRangeAddress(0, 0, 1, 17);
			sheet.addMergedRegion(headerMerge);

			// 데이터 헤더 머지
			CellRangeAddress dataHeaderMerge = new CellRangeAddress(2, 2, 2, 9);
			CellRangeAddress dataHeaderMerge1 = new CellRangeAddress(2, 2, 10, 12);
			CellRangeAddress dataHeaderMerge2 = new CellRangeAddress(2, 2, 16, 17);
			sheet.addMergedRegion(dataHeaderMerge);
			sheet.addMergedRegion(dataHeaderMerge1);
			sheet.addMergedRegion(dataHeaderMerge2);

			sheet.setColumnWidth(0, columnWidth * 8);
			sheet.setColumnWidth(18, 50);

			Row row = null;
			Cell cell = null;

			// 1행 1열
			row = sheet.createRow(0);
			row.setHeight((short) 500);
			cell = row.createCell(0);
			cell.setCellValue("");

			// 1행 헤더
			cell = row.createCell(1);
			cell.setCellStyle(headerStyle);
			cell.setCellValue("ALL DRAWING TABLE");

			cell = row.createCell(18);
			cell.setCellValue("");

			// 2행
			row = sheet.createRow(1);
			row.setHeight((short) 70);
			cell = row.createCell(0);
			cell.setCellValue("");

			// 데이터 컬럼 행
			row = sheet.createRow(2);
			row.setHeight((short) 800);

			cell = row.createCell(1);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("NO");

			cell = row.createCell(2);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("DRAWING TITLE");

			// ??
			cell = row.createCell(3);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(4);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(5);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(6);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(7);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(8);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(9);
			cell.setCellStyle(dataHeaderStyle);

			cell = row.createCell(10);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("DWG. NO.");

			cell = row.createCell(11);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(12);
			cell.setCellStyle(dataHeaderStyle);

			cell = row.createCell(13);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("REV");

			cell = row.createCell(14);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("Current\nREV");

			cell = row.createCell(15);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("LOT");

			cell = row.createCell(16);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("NOTE");

			cell = row.createCell(17);
			cell.setCellStyle(dataHeaderStyle);

			int rowIndex = 3;
			for (int j = 1; j < 46; j++) {
				// 데이터 머지

				CellRangeAddress dataValueMerge = new CellRangeAddress(rowIndex, rowIndex, 2, 9);
				CellRangeAddress dataValueMerge1 = new CellRangeAddress(rowIndex, rowIndex, 10, 12);
				CellRangeAddress dataValueMerge2 = new CellRangeAddress(rowIndex, rowIndex, 16, 17);
				sheet.addMergedRegion(dataValueMerge);
				sheet.addMergedRegion(dataValueMerge1);
				sheet.addMergedRegion(dataValueMerge2);

				// 데이터 값 행
				row = sheet.createRow(rowIndex);
				cell = row.createCell(1);
				cell.setCellStyle(dataHeaderStyle);
				cell.setCellValue(j);

				rowIndex++;
			}

			row = sheet.createRow(48);
			row.setHeight((short) 70);
			cell = row.createCell(0);
			cell.setCellValue("");

			// 푸터
			int footerIndex = 49;
			int numericValue = 1;
			for (int j = 0; j < 3; j++) {

				CellRangeAddress footerMerge = new CellRangeAddress(footerIndex, footerIndex, 2, 4);
				CellRangeAddress footerMerge1 = new CellRangeAddress(footerIndex, footerIndex, 5, 9);
				CellRangeAddress footerMerge2 = new CellRangeAddress(footerIndex, footerIndex, 11, 13);
				CellRangeAddress footerMerge3 = new CellRangeAddress(footerIndex, footerIndex, 14, 17);
				sheet.addMergedRegion(footerMerge);
				sheet.addMergedRegion(footerMerge1);
				sheet.addMergedRegion(footerMerge2);
				sheet.addMergedRegion(footerMerge3);

				// 푸터
				row = sheet.createRow(footerIndex);
				row.setHeight((short) 350);
				cell = row.createCell(1);
				cell.setCellStyle(dataHeaderStyle);
				cell.setCellValue("▲" + numericValue);

				cell = row.createCell(2);
				cell.setCellStyle(dataHeaderStyle);
				cell.setCellValue("");
				cell = row.createCell(3);
				cell.setCellStyle(dataHeaderStyle);
				cell = row.createCell(4);
				cell.setCellStyle(dataHeaderStyle);

				cell = row.createCell(5);
				cell.setCellStyle(dataHeaderStyle);
				cell.setCellValue("");
				cell = row.createCell(6);
				cell.setCellStyle(dataHeaderStyle);
				cell = row.createCell(7);
				cell.setCellStyle(dataHeaderStyle);
				cell = row.createCell(8);
				cell.setCellStyle(dataHeaderStyle);
				cell = row.createCell(9);
				cell.setCellStyle(dataHeaderStyle);

				cell = row.createCell(10);
				cell.setCellStyle(dataHeaderStyle);
				cell.setCellValue("▲" + (numericValue + 3));

				cell = row.createCell(11);
				cell.setCellStyle(dataHeaderStyle);
				cell.setCellValue("");
				cell = row.createCell(12);
				cell.setCellStyle(dataHeaderStyle);
				cell = row.createCell(13);
				cell.setCellStyle(dataHeaderStyle);

				cell = row.createCell(14);
				cell.setCellStyle(dataHeaderStyle);
				cell.setCellValue("");
				cell = row.createCell(15);
				cell.setCellStyle(dataHeaderStyle);
				cell = row.createCell(16);
				cell.setCellStyle(dataHeaderStyle);
				cell = row.createCell(17);
				cell.setCellStyle(dataHeaderStyle);

				numericValue++;
				footerIndex++;
			}

			// 52행 시작
			CellRangeAddress toleranceMerge = new CellRangeAddress(52, 52, 1, 3);
			sheet.addMergedRegion(toleranceMerge);
			row = sheet.createRow(52);
			cell = row.createCell(1);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("TOLERANCE");
			cell = row.createCell(2);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(3);
			cell.setCellStyle(dataHeaderStyle);

			CellRangeAddress toleranceValueMerge = new CellRangeAddress(52, 52, 4, 5);
			sheet.addMergedRegion(toleranceValueMerge);
			cell = row.createCell(4);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("");
			cell = row.createCell(5);
			cell.setCellStyle(dataHeaderStyle);

			CellRangeAddress scaleMerge = new CellRangeAddress(52, 52, 6, 8);
			sheet.addMergedRegion(scaleMerge);
			cell = row.createCell(6);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("SCALE   FREE");
			cell = row.createCell(7);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(8);
			cell.setCellStyle(dataHeaderStyle);

			CellRangeAddress unitMerge = new CellRangeAddress(52, 52, 9, 10);
			sheet.addMergedRegion(unitMerge);
			cell = row.createCell(9);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("UNIT");
			cell = row.createCell(10);
			cell.setCellStyle(dataHeaderStyle);

			CellRangeAddress issueMerge = new CellRangeAddress(52, 57, 11, 12);
			sheet.addMergedRegion(issueMerge);
			cell = row.createCell(11);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("ISSUE");
			cell = row.createCell(12);
			cell.setCellStyle(dataHeaderStyle);

			CellRangeAddress mfgMerge = new CellRangeAddress(52, 52, 13, 15);
			sheet.addMergedRegion(mfgMerge);
			cell = row.createCell(13);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("MFG NO.");
			cell = row.createCell(14);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(15);
			cell.setCellStyle(dataHeaderStyle);

			CellRangeAddress orderMerge = new CellRangeAddress(52, 52, 16, 17);
			sheet.addMergedRegion(orderMerge);
			cell = row.createCell(16);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("ORDER NO.");
			cell = row.createCell(17);
			cell.setCellStyle(dataHeaderStyle);
			// 52행 끝

			// 53행 시작
			CellRangeAddress modelMerge = new CellRangeAddress(53, 53, 1, 2);
			sheet.addMergedRegion(modelMerge);
			row = sheet.createRow(53);
			cell = row.createCell(1);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("MODEL");
			cell = row.createCell(2);
			cell.setCellStyle(dataHeaderStyle);

			CellRangeAddress modelValueMerge = new CellRangeAddress(53, 53, 3, 6);
			sheet.addMergedRegion(modelValueMerge);
			cell = row.createCell(3);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("");
			cell = row.createCell(4);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(5);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(6);
			cell.setCellStyle(dataHeaderStyle);

			CellRangeAddress eqptMerge = new CellRangeAddress(53, 53, 7, 8);
			sheet.addMergedRegion(eqptMerge);
			cell = row.createCell(7);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("EQPT");
			cell = row.createCell(8);
			cell.setCellStyle(dataHeaderStyle);

			CellRangeAddress eqptValueMerge = new CellRangeAddress(53, 53, 9, 10);
			sheet.addMergedRegion(eqptValueMerge);
			cell = row.createCell(9);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("");
			cell = row.createCell(10);
			cell.setCellStyle(dataHeaderStyle);

			cell = row.createCell(11);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(12);
			cell.setCellStyle(dataHeaderStyle);

			cell = row.createCell(13);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("TITLE");

			CellRangeAddress titleValueMerge = new CellRangeAddress(53, 53, 14, 17);
			sheet.addMergedRegion(titleValueMerge);
			cell = row.createCell(14);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("");
			cell = row.createCell(15);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(16);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(17);
			cell.setCellStyle(dataHeaderStyle);
			// 53 행 끝

			// 54행 시작
			CellRangeAddress approvedMerge = new CellRangeAddress(54, 54, 1, 2);
			sheet.addMergedRegion(approvedMerge);
			row = sheet.createRow(54);
			cell = row.createCell(1);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("APPROVED");
			cell = row.createCell(2);
			cell.setCellStyle(dataHeaderStyle);

			CellRangeAddress judgedMerge = new CellRangeAddress(54, 54, 3, 4);
			sheet.addMergedRegion(judgedMerge);
			cell = row.createCell(3);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("JUDGED");
			cell = row.createCell(4);
			cell.setCellStyle(dataHeaderStyle);

			CellRangeAddress designedMerge = new CellRangeAddress(54, 54, 5, 6);
			sheet.addMergedRegion(designedMerge);
			cell = row.createCell(5);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("DESIGNED");
			cell = row.createCell(6);
			cell.setCellStyle(dataHeaderStyle);

			CellRangeAddress drawnMerge = new CellRangeAddress(54, 54, 7, 8);
			sheet.addMergedRegion(drawnMerge);
			cell = row.createCell(7);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("DRAWN");
			cell = row.createCell(8);
			cell.setCellStyle(dataHeaderStyle);

			CellRangeAddress checkedMerge = new CellRangeAddress(54, 54, 9, 10);
			sheet.addMergedRegion(checkedMerge);
			cell = row.createCell(9);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("CHECKED");
			cell = row.createCell(10);
			cell.setCellStyle(dataHeaderStyle);

			cell = row.createCell(11);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(12);
			cell.setCellStyle(dataHeaderStyle);

			CellRangeAddress dwgNoMergre = new CellRangeAddress(54, 56, 13, 13);
			sheet.addMergedRegion(dwgNoMergre);
			cell = row.createCell(13);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("DWG\nNo.");

			CellRangeAddress dwgNoValueMergre = new CellRangeAddress(54, 56, 14, 16);
			sheet.addMergedRegion(dwgNoValueMergre);
			cell = row.createCell(14);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("");
			cell = row.createCell(15);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(16);
			cell.setCellStyle(dataHeaderStyle);

			CellRangeAddress revMerge = new CellRangeAddress(54, 57, 17, 17);
			sheet.addMergedRegion(revMerge);
			cell = row.createCell(17);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("REV\n0");

			// 55행 시작
			CellRangeAddress approvedValueMerge = new CellRangeAddress(55, 56, 1, 2);
			sheet.addMergedRegion(approvedValueMerge);
			row = sheet.createRow(55);
			cell = row.createCell(1);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("");
			cell = row.createCell(2);
			cell.setCellStyle(dataHeaderStyle);

			CellRangeAddress judgedValueMerge = new CellRangeAddress(55, 56, 3, 4);
			sheet.addMergedRegion(judgedValueMerge);
			cell = row.createCell(3);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("");
			cell = row.createCell(4);
			cell.setCellStyle(dataHeaderStyle);

			CellRangeAddress designedValueMerge = new CellRangeAddress(55, 56, 5, 6);
			sheet.addMergedRegion(designedValueMerge);
			cell = row.createCell(5);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("");
			cell = row.createCell(6);
			cell.setCellStyle(dataHeaderStyle);

			CellRangeAddress drawnValueMerge = new CellRangeAddress(55, 56, 7, 8);
			sheet.addMergedRegion(drawnValueMerge);
			cell = row.createCell(7);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("");
			cell = row.createCell(8);
			cell.setCellStyle(dataHeaderStyle);

			CellRangeAddress checkedValueMerge = new CellRangeAddress(55, 56, 9, 10);
			sheet.addMergedRegion(checkedValueMerge);
			cell = row.createCell(9);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("");
			cell = row.createCell(10);
			cell.setCellStyle(dataHeaderStyle);

			cell = row.createCell(11);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(12);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(13);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(14);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(15);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(16);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(17);
			cell.setCellStyle(dataHeaderStyle);

			// 55 행 끝

			// 56 행 시작
			row = sheet.createRow(56);
			cell = row.createCell(1);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(2);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(3);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(4);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(5);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(6);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(7);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(8);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(9);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(10);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(11);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(12);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(13);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(14);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(15);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(16);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(17);
			cell.setCellStyle(dataHeaderStyle);
			// 56행 끝

			// 57 행 시작
			CellRangeAddress kekMerge = new CellRangeAddress(57, 57, 1, 10);
			sheet.addMergedRegion(kekMerge);
			row = sheet.createRow(57);
			cell = row.createCell(1);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("KOOKJE ELECTRIC KOREA.CO.,LTD");
			cell = row.createCell(2);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(3);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(4);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(5);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(6);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(7);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(8);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(9);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(10);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(11);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(12);
			cell.setCellStyle(dataHeaderStyle);

			CellRangeAddress timeMerge = new CellRangeAddress(57, 57, 13, 16);
			sheet.addMergedRegion(timeMerge);
			cell = row.createCell(13);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("오늘이다");
			cell = row.createCell(14);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(15);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(16);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(17);
			cell.setCellStyle(dataHeaderStyle);
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