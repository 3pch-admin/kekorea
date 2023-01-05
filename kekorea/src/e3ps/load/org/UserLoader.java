package e3ps.load.org;

import java.io.FileInputStream;
import java.util.HashMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import e3ps.load.service.LoadHelper;

public class UserLoader {

	public static void main(String[] args) throws Exception {

		if (args.length == 0) {
			System.out.println("엑셀파일 경로 누락");
			System.exit(0);
		}

		FileInputStream excel = null;
		// File excel = null;
		try {
			excel = new FileInputStream(args[0]);
			/*
			 * if (!excel.getName().endsWith(".xlsx")) { System.out.println("엑셀 파일이 아닙니다.");
			 * System.exit(0); }
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}

		UserLoader loader = new UserLoader();

		XSSFWorkbook workBook = new XSSFWorkbook(excel);
		// Workbook workBook = JExcelUtils.getWorkbook(excel);

		loader.loadExcel(workBook);
		System.out.println("사용자 등록 완료.");
		System.exit(0);
	}

	private void loadExcel(XSSFWorkbook workBook) throws Exception {

		String name = "";
		String duty = "";
		String email = "";
		String id = "";
		String department = "";

		// 탐색에 사용할 Sheet, Row, Cell 객체
		XSSFSheet curSheet;
		XSSFRow curRow;
		XSSFCell curCell;

		// Sheet 탐색 for문
		for (int sheetIndex = 0; sheetIndex < workBook.getNumberOfSheets(); sheetIndex++) {
			// 현재 Sheet 반환
			curSheet = workBook.getSheetAt(sheetIndex);
			// row 탐색 for문
			for (int rowIndex = 1; rowIndex < curSheet.getPhysicalNumberOfRows(); rowIndex++) {
				// row 0은 헤더정보이기 때문에 무시
				if (rowIndex != 0) {
					// 현재 row 반환
					curRow = curSheet.getRow(rowIndex);
					// vo = new CustomerVo();
					String value;

					// row의 첫번째 cell값이 비어있지 않은 경우 만 cell탐색
					// if(!"".equals(curRow.getCell(0).getStringCellValue())) {

					// cell 탐색 for 문
					for (int cellIndex = 0; cellIndex < curRow.getPhysicalNumberOfCells(); cellIndex++) {
						curCell = curRow.getCell(cellIndex);

						if (true) {
							value = "";
							// cell 스타일이 다르더라도 String으로 반환 받음
							switch (curCell.getCellType()) {
							case HSSFCell.CELL_TYPE_FORMULA:
								value = curCell.getCellFormula();
								break;
							case HSSFCell.CELL_TYPE_NUMERIC:
								value = curCell.getNumericCellValue() + "";
								break;
							case HSSFCell.CELL_TYPE_STRING:
								value = curCell.getStringCellValue() + "";
								break;
							case HSSFCell.CELL_TYPE_BLANK:
								value = curCell.getBooleanCellValue() + "";
								break;
							case HSSFCell.CELL_TYPE_ERROR:
								value = curCell.getErrorCellValue() + "";
								break;
							default:
								value = new String();
								break;
							}

							// 현재 column index에 따라서 vo에 입력
							switch (cellIndex) {
							case 0: // 아이디
								id = value;
								break;

							case 1: // 이름
								name = value;
								break;

							case 2: // 직책
								duty = value;
								break;

							case 3: // 이메일
								email = value;
								break;

							case 5:
								department = value;
								break;
							default:
								break;
							}
						}
						// temp+=data+"::";
					}
					// cell 탐색 이후 vo 추가
					// list.add(vo);

					// }
				}
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("id", id);
				map.put("name", name);
				map.put("duty", duty);
				map.put("email", email);
				map.put("department", department);
				loadUserInfo(map);
			}
		}
	}

	// private void loadUserInfo(Cell[] cell) throws Exception {
	private void loadUserInfo(HashMap<String, Object> map) throws Exception {

		System.out.println("map=" + map);
		boolean isUser = LoadHelper.service.loadUserFromExcel(map);
		if (!isUser) {
			System.out.println("등록되지 않음");
		} else {
			System.out.println("등록됨");
		}
	}
}