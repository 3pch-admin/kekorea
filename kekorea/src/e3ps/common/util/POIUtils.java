package e3ps.common.util;

import java.io.File;
import java.io.FileInputStream;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class POIUtils {

	private POIUtils() {

	}

	public static XSSFWorkbook getWorkbook(String path) throws Exception {
		File file = new File(path);
		return getWorkbook(file);
	}

	public static XSSFWorkbook getWorkbook(File file) throws Exception {
		XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(file));
		return workbook;
	}

	public static XSSFSheet getSheet(XSSFWorkbook workbook) throws Exception {
		return getSheet(0, workbook);
	}

	public static XSSFSheet getSheet(int index, XSSFWorkbook workbook) throws Exception {
		XSSFSheet sheet = workbook.getSheetAt(index);
		return sheet;
	}

	public static int getRows(XSSFWorkbook workbook) throws Exception {
		return getRows(0, workbook);
	}

	public static int getRows(int index, XSSFWorkbook workbook) throws Exception {
		XSSFSheet sheet = getSheet(index, workbook);
		int rows = sheet.getPhysicalNumberOfRows();
		return rows;
	}

	public static XSSFRow getRow(XSSFSheet sheet, int index) throws Exception {
		XSSFRow row = sheet.getRow(index);
		return row;
	}

	public static int getCells(XSSFRow row) throws Exception {
		int cells = row.getPhysicalNumberOfCells();
		return cells;
	}
}
