package e3ps.load.common;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import e3ps.common.db.DBCPManager;

public class ChangeToIDA2A2 {

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

		ChangeToIDA2A2 loader = new ChangeToIDA2A2();

		XSSFWorkbook workBook = new XSSFWorkbook(excel);
		// Workbook workBook = JExcelUtils.getWorkbook(excel);

		loader.loadExcel(workBook);
		System.out.println("OID 변경 마이그레이션 완료.");
		System.exit(0);
	}

	private void loadExcel(XSSFWorkbook workBook) {

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			XSSFSheet curSheet;
			XSSFRow curRow;
			XSSFCell curCell;

			// 프로젝트 OID
			curSheet = workBook.getSheetAt(0);

			con = DBCPManager.getConnection("local");

			st = con.createStatement();

			for (int rowIndex = 1; rowIndex < curSheet.getPhysicalNumberOfRows(); rowIndex++) {
				String oid = "";

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
								case 14: // 아이디
									oid = value;
									break;
								default:
									break;
								}
							}
						}
					}
					oid = oid.substring(1);

//					StringBuffer sb = new StringBuffer();

//					sb.append("SELECT * FROM PROJECT WHERE IDA2A2='"+oid+"'");
//					
//					rs = st.executeQuery(sb.toString());
//					
//					if(rs.next()) {
//						System.out.println(oid);
//					}
					
					System.out.println(oid);
					
//					sb.append("UPDATE PROJECT SET IDA2A2='" + oid + "'");
//
//					st.executeUpdate(sb.toString());

//					System.out.println("변경..");

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBCPManager.freeConnection(con, st, rs);
		}
	}
}