package e3ps.load.project;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import e3ps.load.service.LoadHelper;
import e3ps.project.Output;
import e3ps.project.Project;
import e3ps.project.Task;

public class ProjectLoader {

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

		ProjectLoader loader = new ProjectLoader();

		XSSFWorkbook workBook = new XSSFWorkbook(excel);
		// Workbook workBook = JExcelUtils.getWorkbook(excel);

		loader.loadExcel(workBook);
		System.out.println("프로젝트 마이그레이션 완료.");
		System.exit(0);
	}

	private void loadExcel(XSSFWorkbook workBook) throws Exception {

		HashMap<String, Object> info = getterProjectRefInfo(workBook);

		ArrayList<String> project = (ArrayList<String>) info.get("project");
		ArrayList<String> program = (ArrayList<String>) info.get("program");
		ArrayList<String> extend = (ArrayList<String>) info.get("extend");

		// 탐색에 사용할 Sheet, Row, Cell 객체
		XSSFSheet curSheet;
		XSSFRow curRow;
		XSSFCell curCell;
		int count = 0;
		// 현재 Sheet 반환
		for (int i = 0; i < project.size(); i++) {

			String projectOID = (String) project.get(i);
			String programOID = (String) program.get(i);
			String extendOID = (String) extend.get(i);

			HashMap<String, Object> map = new HashMap<String, Object>();

			// 프로젝트 OID
			curSheet = workBook.getSheetAt(0);

			HashMap<String, Task> taskOid = new HashMap<String, Task>();

			for (int rowIndex = 1; rowIndex < curSheet.getPhysicalNumberOfRows(); rowIndex++) {
				String customer = "";
				String description = "";
				String equipment = "";
				String itemcode = ""; // 개조 양산...
				String line = "";
				String keNumber = "";
				String model = "";
				String kekNumber = "";
				String process = "";

				String CREATESTAMPA2 = ""; // 10
				String MODIFYSTAMPA2 = ""; // 12
				String UPDATESTAMPA2 = ""; // 16

				String userid = "";

				String compProgramclass = "";
				String compProgramOid = "";

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
								case 0: // 아이디
									customer = value;
									break;
								case 1: // 이름
									description = value;
									break;
								case 2: // 직책
									equipment = value;
									break;
								case 3: // 이메일
									itemcode = value;
									break;
								case 4:
									line = value;
									break;
								case 5:
									keNumber = value;
									break;
								case 6:
									model = value;
									break;
								case 7:
									kekNumber = value;
									break;
								case 8:
									process = value;
									break;
								case 10:
									CREATESTAMPA2 = value;
									break;
								case 12:
									MODIFYSTAMPA2 = value;
									break;

								case 13:
									compProgramclass = value;
									break;

								case 14:
									compProgramOid = value;
									break;

								case 16:
									UPDATESTAMPA2 = value;
									break;
								case 18:
									userid = value;
									break;
								default:
									break;
								}
							}
						}
					}
				}

				String sumV = compProgramclass + ":" + compProgramOid.substring(1);
				if (!sumV.equals(programOID)) {
					continue;
				} else {
					map.put("customer", customer);
					map.put("description", description);
					map.put("equipment", equipment);
					map.put("itemcode", itemcode);
					map.put("line", line);
					map.put("keNumber", keNumber);

					map.put("model", model);
					map.put("kekNumber", kekNumber);
					map.put("process", process);
					map.put("CREATESTAMPA2", "20" + CREATESTAMPA2);
					map.put("MODIFYSTAMPA2", MODIFYSTAMPA2);
					map.put("UPDATESTAMPA2", UPDATESTAMPA2);

					map.put("userid", userid);
				}
			} // end sheet 0

			curSheet = workBook.getSheetAt(2);

			// row 탐색 for문
			for (int rowIndex = 1; rowIndex < curSheet.getPhysicalNumberOfRows(); rowIndex++) {
				// row 0은 헤더정보이기 때문에 무시

				String startDate = "";
//				String endDate = "";
				String planEndDate = "";
				String planStartDate = ""; // 개조 양산...
				String compExtendclass = "";
				String compExtendOid = "";

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
								case 0: // 아이디
//									endDate = value;
									break;
								case 1: // 이름
									startDate = value;
									break;
								case 2: // 직책
									planEndDate = value;
									break;
								case 3: // 이메일
									planStartDate = value;
									break;
								case 4:
									compExtendclass = value;
									break;
								case 5:
									compExtendOid = value;
									break;
								default:
									break;
								}
							}
						}
					}
				}

				String sumV = compExtendclass + ":" + compExtendOid.substring(1);

				if (!sumV.equals(extendOID)) {
					continue;
				} else {
					map.put("planEndDate", "20" + planEndDate);
					map.put("planStartDate", "20" + planStartDate);
					map.put("startDate", "20" + startDate);
//					map.put("endDate", "20" + endDate);
				}
			} // end sheet 1

			Project projects = loadProjectInfo(map);
			count++;
			System.out.println("count=" + count);
			System.out.println("project=" + projects.getKekNumber());

			curSheet = workBook.getSheetAt(3);
			// row 탐색 for문

			// task sheet

			for (int rowIndex = 1; rowIndex < curSheet.getPhysicalNumberOfRows(); rowIndex++) {
				// row 0은 헤더정보이기 때문에 무시

				String progress = ""; // 1
				String name = "";
				String compProjectclass = "";
				String compProjectOid = "";

				String compTaskclass = "";
				String compTaskOid = "";

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
								case 0: // 아이디
									progress = value;
									break;
								case 1: // 이름
									name = value;
									break;
								case 16: // 아이디
									compTaskclass = value;
									break;
								case 17: // 이름
									compTaskOid = value;
									break;
								case 10: // 직책
									compProjectclass = value;
									break;
								case 11: // 직책
									compProjectOid = value;
									break;
								default:
									break;
								}
							}
						}
					}
				}

				String sumV = compProjectclass + ":" + compProjectOid.substring(1);

				if (!sumV.equals(projectOID)) {
					continue;
				} else {
					map.put("name", name);
					map.put("progress", progress.substring(0, progress.lastIndexOf(".")));
					Task task = LoadHelper.service.loadTaskFromExcel(map, projects);
					taskOid.put(compTaskclass + ":" + compTaskOid.substring(1), task);

				}
			}

			Iterator<String> it = taskOid.keySet().iterator();
			curSheet = workBook.getSheetAt(4);

			while (it.hasNext()) {
				String key = (String) it.next();

//				System.out.println("실222행.");
				// row 탐색 for문
				for (int rowIndex = 1; rowIndex < curSheet.getPhysicalNumberOfRows(); rowIndex++) {
					// row 0은 헤더정보이기 때문에 무시

					String dclass = "";
					String doid = "";

					String name = "";

					String compProjectclass = "";
					String compProjectOid = "";

					String compTclass = "";
					String compToid = "";

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
									case 0:
										dclass = value;
										break;
									case 1: // 직책
										doid = value;
										break;
									case 3:
										name = value;
										break;
									case 4: // 직책
										compTclass = value;
										break;
									case 5: // 직책
										compToid = value;
										break;
									case 7: // 직책
										compProjectclass = value;
										break;
									case 8: // 직책
										compProjectOid = value;
										break;
									default:
										break;
									}
								}
							}
						}
					}

					
//					System.out.println("compProjectOid="+compProjectOid);
					
					String sumT = "";
					String sumV = "";

					if( compToid.length()> 1) {
						sumT = compTclass + ":" + compToid.substring(1);
					}
					
					if( compProjectOid.length()> 1) {
						sumV = compProjectclass + ":" + compProjectOid.substring(1);
					}
					
					
//					System.out.println("key=" + key + ",===" + sumT);
//					System.out.println("sumV===" + sumV);

//					System.out.println("key=" + key + ",===" + sumT);
					if (!key.equals(sumT)) {
						continue;
					} else {

						if (!sumV.equals(projectOID)) {
							continue;
						} else {
							map.put("name", name);

							Output output = LoadHelper.service.loadOutputFromExcel(map, projects);

//							System.out.println("key=" + key + ",===" + sumT);
							System.out.println("등록");

							Task tt = (Task) taskOid.get(key);
//							System.out.println("tt=" + tt);

							String att = dclass + ":" + doid.substring(1);

							LoadHelper.service.setOutput(tt, output, att);
						}
					}
				}
			}
		}
		// sheet

	} // end for array project

//	private ArrayList<String> afterPlanDate(XSSFWorkbook workBook) {
//
//		ArrayList<String> list = new ArrayList<String>();
//		// 탐색에 사용할 Sheet, Row, Cell 객체
//		XSSFSheet curSheet;
//		XSSFRow curRow;
//		XSSFCell curCell;
//
//		curSheet = workBook.getSheetAt(3);
//
//		// row 탐색 for문
//		for (int rowsIndex = 1; rowsIndex < curSheet.getPhysicalNumberOfRows(); rowsIndex++) {
//			// row 0은 헤더정보이기 때문에 무시
//
//			String compExtendclass2 = "";
//			String compExtendoid2 = "";
//
//			if (rowsIndex != 0) {
//				// 현재 row 반환
//				curRow = curSheet.getRow(rowsIndex);
//				// vo = new CustomerVo();
//				String value;
//				// cell 탐색 for 문
//				for (int cellIndex = 0; cellIndex < curRow.getPhysicalNumberOfCells(); cellIndex++) {
//					curCell = curRow.getCell(cellIndex);
//
//					if (curCell != null) {
//
//						if (true) {
//							value = "";
//							// cell 스타일이 다르더라도 String으로 반환 받음
//							switch (curCell.getCellType()) {
//							case HSSFCell.CELL_TYPE_FORMULA:
//								value = curCell.getCellFormula();
//								break;
//							case HSSFCell.CELL_TYPE_NUMERIC:
//								value = curCell.getNumericCellValue() + "";
//								break;
//							case HSSFCell.CELL_TYPE_STRING:
//								value = curCell.getStringCellValue() + "";
//								break;
//							case HSSFCell.CELL_TYPE_BLANK:
//								value = curCell.getBooleanCellValue() + "";
//								break;
//							case HSSFCell.CELL_TYPE_ERROR:
//								value = curCell.getErrorCellValue() + "";
//								break;
//							default:
//								value = new String();
//								break;
//							}
//
//							// 현재 column index에 따라서 vo에 입력
//							switch (cellIndex) {
//							case 5: // 아이디
//								compExtendclass2 = value;
//								break;
//							case 6: // 이름
//								compExtendoid2 = value;
//								break;
//							default:
//								break;
//							}
//						}
//					}
//				}
//			}
//
//			String extendOID = compExtendclass2 + ":" + compExtendoid2.substring(1);
//			list.add(extendOID);
//		} // end sheet 1
//		return list;
//	}

//	private HashMap<String, Object> prePlanDate(XSSFWorkbook workBook) throws Exception {
//
//		// 탐색에 사용할 Sheet, Row, Cell 객체
//		XSSFSheet curSheet;
//		XSSFRow curRow;
//		XSSFCell curCell;
//
//		curSheet = workBook.getSheetAt(2);
//
//		HashMap<String, Object> map = new HashMap<String, Object>();
//
//		ArrayList<String> list = new ArrayList<String>();
//
//		// row 탐색 for문
//		for (int rowIndex = 1; rowIndex < curSheet.getPhysicalNumberOfRows(); rowIndex++) {
//			// row 0은 헤더정보이기 때문에 무시
//
//			String startDate = "";
//			String endDate = "";
//			String planEndDate = "";
//			String planStartDate = ""; // 개조 양산...
//			String compExtendclass = "";
//			String compExtendOid = "";
//
//			if (rowIndex != 0) {
//				// 현재 row 반환
//				curRow = curSheet.getRow(rowIndex);
//				// vo = new CustomerVo();
//				String value;
//
//				// row의 첫번째 cell값이 비어있지 않은 경우 만 cell탐색
//				// if(!"".equals(curRow.getCell(0).getStringCellValue())) {
//
//				// cell 탐색 for 문
//				for (int cellIndex = 0; cellIndex < curRow.getPhysicalNumberOfCells(); cellIndex++) {
//					curCell = curRow.getCell(cellIndex);
//
//					if (curCell != null) {
//
//						if (true) {
//							value = "";
//							// cell 스타일이 다르더라도 String으로 반환 받음
//							switch (curCell.getCellType()) {
//							case HSSFCell.CELL_TYPE_FORMULA:
//								value = curCell.getCellFormula();
//								break;
//							case HSSFCell.CELL_TYPE_NUMERIC:
//								value = curCell.getNumericCellValue() + "";
//								break;
//							case HSSFCell.CELL_TYPE_STRING:
//								value = curCell.getStringCellValue() + "";
//								break;
//							case HSSFCell.CELL_TYPE_BLANK:
//								value = curCell.getBooleanCellValue() + "";
//								break;
//							case HSSFCell.CELL_TYPE_ERROR:
//								value = curCell.getErrorCellValue() + "";
//								break;
//							default:
//								value = new String();
//								break;
//							}
//
//							// 현재 column index에 따라서 vo에 입력
//							switch (cellIndex) {
//							case 0: // 아이디
//								endDate = value;
//								break;
//							case 1: // 이름
//								startDate = value;
//								break;
//							case 2: // 직책
//								planEndDate = value;
//								break;
//							case 3: // 이메일
//								planStartDate = value;
//								break;
//							case 4:
//								compExtendclass = value;
//								break;
//							case 5:
//								compExtendOid = value;
//								break;
//							default:
//								break;
//							}
//						}
//					}
//				}
//			}
//
//			map.put("startDate", startDate);
//			map.put("endDate", endDate);
//			map.put("planEndDate", planEndDate);
//			map.put("planStartDate", planStartDate);
//			String extendOID = compExtendclass + ":" + compExtendOid.substring(1);
//			list.add(extendOID);
//
//			map.put("list", list);
//		} // end sheet 1
//		return map;
//	} // end method

	// private void loadUserInfo(Cell[] cell) throws Exception {
	private Project loadProjectInfo(HashMap<String, Object> map) throws Exception {
		Project project = LoadHelper.service.loadProjectFromExcel(map);
		return project;
	}

	private HashMap<String, Object> getterProjectRefInfo(XSSFWorkbook workBook) throws Exception {

		HashMap<String, Object> map = new HashMap<String, Object>();

		ArrayList<String> program = new ArrayList<String>();
		ArrayList<String> project = new ArrayList<String>();
		ArrayList<String> extend = new ArrayList<String>();

		String programclass = "";
		String programoid = "";

		String extendclass = "";
		String extendoid = "";

		String projectclass = "";
		String projectoid = "";

		// 탐색에 사용할 Sheet, Row, Cell 객체
		XSSFSheet curSheet = workBook.getSheetAt(1);
		XSSFRow curRow;
		XSSFCell curCell;

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
					// System.out.println("=" + cellIndex);
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
							case 0:
								programclass = value;
								break;
							case 1:
								programoid = value;
								break;
							case 3:
								extendclass = value;
								break;
							case 4:
								extendoid = value;
								break;
							case 9:
								projectclass = value;
								break;
							case 10:
								projectoid = value;
								break;
							default:
								break;
							}
						}
					}
				}

				int idx = programoid.indexOf("'");
				if (idx > -1) {
					program.add(programclass + ":" + programoid.substring(1));
				} else {
					program.add(programclass + ":" + programoid);
				}

				int idx2 = extendoid.indexOf("'");
				if (idx2 > -1) {
					extend.add(extendclass + ":" + extendoid.substring(1));
				} else {
					extend.add(extendclass + ":" + extendoid);
				}

				int idx3 = projectoid.indexOf("'");
				if (idx3 > -1) {
					project.add(projectclass + ":" + projectoid.substring(1));
				} else {
					project.add(projectclass + ":" + projectoid);
				}
			}
		}

		map.put("project", project);
		map.put("program", program);
		map.put("extend", extend);

		return map;
	}
}
