package e3ps.load.project;

import java.io.FileInputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import e3ps.migrator.MigratorHelper;

public class ProjectUserLoader {

	public static void main(String[] args) throws Exception {

		if (args.length == 0) {
			System.out.println("엑셀파일 경로 누락");
			System.exit(0);
		}

		FileInputStream excel = null;
		// File excel = null;
		try {
			excel = new FileInputStream(args[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}

		ProjectUserLoader loader = new ProjectUserLoader();

		XSSFWorkbook workBook = new XSSFWorkbook(excel);
		// Workbook workBook = JExcelUtils.getWorkbook(excel);

		loader.loadExcel(workBook);
		System.out.println("프로젝트 마이그레이션 완료.");
		System.exit(0);
	}

	private void loadExcel(XSSFWorkbook workBook) throws Exception {
		Timestamp start = new Timestamp(new Date().getTime());
		System.out.println("시작 start = " + start);
		// 탐색에 사용할 Sheet, Row, Cell 객체
		XSSFSheet curSheet;
		XSSFRow curRow;
		XSSFCell curCell;
//		int count = 0;
		// 현재 Sheet 반환

		HashMap<String, Object> map = new HashMap<String, Object>();

		// 프로젝트 OID
		curSheet = workBook.getSheetAt(0);

//		HashMap<String, Task> taskOid = new HashMap<String, Task>();

		for (int rowIndex = 1; rowIndex < curSheet.getPhysicalNumberOfRows(); rowIndex++) {

			String pType = ""; // 0
			String kekNumber = ""; // 1
			String machine = ""; // 3
			String elec = ""; // 4
			String sw = "윤순근"; // 5
			String pDate = ""; // 2

			// row 0은 헤더정보이기 때문에 무시
			if (rowIndex != 0) {
				// 현재 row 반환
				curRow = curSheet.getRow(rowIndex);
				// vo = new CustomerVo();
				String value;

				// cell 탐색 for 문
				for (int cellIndex = 0; cellIndex < curRow.getPhysicalNumberOfCells(); cellIndex++) {
					curCell = curRow.getCell(cellIndex);

					if (curCell != null) {

						if (true) {
							value = "";
							// cell 스타일이 다르더라도 String으로 반환 받음
							switch (curCell.getCellType()) {
							case HSSFCell.CELL_TYPE_FORMULA:
								value = curCell.getCellFormula();
								break;
							case HSSFCell.CELL_TYPE_NUMERIC:
								// value = curCell.getNumericCellValue() + "";
								// value = curCell.getDateCellValue().toString();
								Date date = curCell.getDateCellValue();
								value = new SimpleDateFormat("yyyy-MM-dd").format(date);
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
								pType = value;
								break;
							case 1: // 이름
								kekNumber = value;
								break;
							case 2: // 직책
								pDate = value;
								break;
							case 3: // 직책
								machine = value;
								break;
							case 4: // 직책
								elec = value;
								break;
							default:
								break;
							}
						}
					}
				}
			}

			map.put("pType", pType);
			map.put("kekNumber", kekNumber);
			map.put("machine", machine);
			map.put("elec", elec);
			map.put("pDate", pDate);
			map.put("sw", sw);
			System.out.println("pType" + pType);
			System.out.println("kekNumber" + kekNumber);
			System.out.println("machine" + machine);
			System.out.println("elec" + elec);
			System.out.println("gg" + pDate);
			MigratorHelper.service.setProjectInfo(map);
		}
		Timestamp end = new Timestamp(new Date().getTime());
		System.out.println("끝 end == " + end);
	}
}