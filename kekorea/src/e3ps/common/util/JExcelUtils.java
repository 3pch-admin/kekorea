package e3ps.common.util;

import java.io.File;

import jxl.Cell;
import jxl.Workbook;

public class JExcelUtils {

	private JExcelUtils() {

	}

	public static Workbook getWorkbook(File file) throws Exception {
		Workbook workbook = null;
		if (file == null) {
			return null;
		}

		if (!file.getName().endsWith(".xls")) {
			return null;
		}

		workbook = Workbook.getWorkbook(file);
		return workbook;
	}

	public static String getContent(Cell[] cell, int idx) throws Exception {
		String value = cell[idx].getContents();
		if (value == null) {
			return "";
		}
		return value.trim();
	}
}