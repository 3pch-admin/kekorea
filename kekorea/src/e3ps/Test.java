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

import e3ps.common.util.CommonUtils;
import e3ps.common.util.IBAUtils;
import wt.epm.EPMDocument;

public class Test {

	public static void main(String[] args) throws Exception {

		EPMDocument epm = (EPMDocument)CommonUtils.getObject("wt.epm.EPMDocument:1284710");
		
		IBAUtils.createIBA(epm, "s", "DWG_No", "12345");

		System.exit(0);
	}
}