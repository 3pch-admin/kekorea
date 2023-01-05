package e3ps.load.org;

import java.io.File;
import java.util.HashMap;

import e3ps.common.util.JExcelUtils;
import e3ps.load.service.LoadHelper;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class DepartmentLoader {

	public static void main(String[] args) throws Exception {

		if (args.length == 0) {
			System.out.println("엑셀파일 경로 누락");
			System.exit(0);
		}

		File excel = null;
		try {
			excel = new File(args[0]);
			if (!excel.getName().endsWith(".xls")) {
				System.out.println("엑셀 파일이 아닙니다.");
				System.exit(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		DepartmentLoader loader = new DepartmentLoader();

		Workbook workBook = JExcelUtils.getWorkbook(excel);
		loader.loadExcel(workBook);
		System.out.println("부서 등록 완료.");
		System.exit(0);
	}

	private void loadExcel(Workbook workBook) throws Exception {
		Sheet[] sheets = workBook.getSheets();
		int rows = sheets[0].getRows();
		for (int i = 1; i < rows; i++) {
			loadDepartmentInfo(sheets[0].getRow(i));
		}
	}

	private void loadDepartmentInfo(Cell[] cell) throws Exception {
		String name = JExcelUtils.getContent(cell, 0); // 부서명
		String code = JExcelUtils.getContent(cell, 1); // 부서 코드
		String pcode = JExcelUtils.getContent(cell, 2); // 부서 부모 코드
		String sort = JExcelUtils.getContent(cell, 3); // 정렬
		String depth = JExcelUtils.getContent(cell, 4); // depth

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("name", name);
		map.put("code", code);
		map.put("pcode", pcode);
		map.put("sort", sort);
		map.put("depth", depth);

		boolean isDepartment = LoadHelper.service.loadDepartmentFromExcel(map);
		if (isDepartment) {
			System.out.println("부서 명 : " + name + "은 이미 등록 되어있습니다.");
		} else {
			System.out.println("등록된 부서 명 : " + name);
			System.out.println("등록된 부서 코드 : " + code);
			System.out.println();
		}
	}
}
