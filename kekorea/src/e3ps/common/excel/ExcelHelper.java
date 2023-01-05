package e3ps.common.excel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import e3ps.approval.column.AgreeColumnData;
import e3ps.approval.column.ApprovalColumnData;
import e3ps.approval.column.CompleteColumnData;
import e3ps.approval.column.IngColumnData;
import e3ps.approval.column.NoticeColumnData;
import e3ps.approval.column.ReceiveColumnData;
import e3ps.approval.column.ReturnColumnData;
import e3ps.approval.service.NoticeHelper;
import e3ps.common.ModuleKeys;
import e3ps.doc.column.DocumentColumnData;
import e3ps.doc.service.DocumentHelper;
import e3ps.epm.column.EpmLibraryColumnData;
import e3ps.epm.column.EpmProductColumnData;
import e3ps.epm.service.EpmHelper;
import e3ps.org.column.UserColumnData;
import e3ps.part.column.PartLibraryColumnData;
import e3ps.part.column.PartProductColumnData;
import e3ps.part.service.PartHelper;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import wt.util.WTProperties;

public class ExcelHelper {

	public static File excelForm;
	public static String excelFormLoc;
	static {
		try {
			excelFormLoc = WTProperties.getLocalProperties().getProperty("wt.codebase.location") + File.separator
					+ "jsp" + File.separator + "temp" + File.separator + "pdm" + File.separator + "excelForm";

			File loc = new File(excelFormLoc);

			if (!loc.exists()) {
				loc.mkdirs();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static final ExcelHelper manager = new ExcelHelper();

	public File printExcel(Map<String, Object> param) {
		String module = (String) param.get("module");
		String prefix = prefixText(module);
		WritableWorkbook workBook = null;
		try {
			excelForm = new File(excelFormLoc + File.separator + prefix + ".xls");
			workBook = Workbook.createWorkbook(excelForm);
			WritableSheet sheet = workBook.createSheet(prefix, 0);
			printHeader(sheet, param);
			setColumnView(sheet, param);
			printData(sheet, param);
			workBook.write();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (workBook != null)
					workBook.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return excelForm;
	}

	private void setColumnView(WritableSheet sheet, Map<String, Object> param) throws Exception {
		List<String> columns = (List<String>) param.get("columns");
		int idx = 1;
		sheet.setColumnView(idx++, new Integer(30));
		for (int i = 0; i < columns.size(); i++) {
			sheet.setColumnView(idx++, new Integer(30));
		}
	}

	private void printData(WritableSheet sheet, Map<String, Object> param) throws Exception {
		WritableCellFormat format = getCellFormat(Alignment.CENTRE, Colour.WHITE);
		int rows = 1;

		String opt = (String) param.get("opt");

		boolean isAll = false;
		boolean isPage = false;
		boolean isSelect = false;

		if ("all".equals(opt)) {
			isAll = true;
		} else if ("page".equals(opt)) {
			isPage = true;
		} else if ("select".equals(opt)) {
			isSelect = true;
		}

		String module = (String) param.get("module");
		int type = getModule(module);
		Map<String, Object> map = null;
		List<String> columns = (List<String>) param.get("columns");
		if (type == 1) {

			if (isAll) {
				// 전체
				map = QueryExcelHelper.manager.getAllDocumentList();
			} else if (isPage) {
				System.out.println("여기 실행??");
				System.out.println("pa=" + param);
				map = DocumentHelper.manager.find(param);
			} else if (isSelect) {
				map = QueryExcelHelper.manager.getSelectDocument(param);
			}

			ArrayList<DocumentColumnData> list = (ArrayList<DocumentColumnData>) map.get("list");
			int total = list.size();
			for (DocumentColumnData data : list) {
				int idx = 1;
				sheet.addCell(new Label(0, rows, String.valueOf(total), format));
				for (String key : columns) {
					sheet.addCell(new Label(idx++, rows, data.getValue(key), format));
				}
				rows++;
				total--;
			}
			// 라이브러리
		} else if (type == 2) {
			param.put("context", "LIBRARY");

			if (isAll) {
				map = QueryExcelHelper.manager.getAllLibraryWTPart();
			} else if (isPage) {
				map = PartHelper.manager.find(param);
			} else if (isSelect) {
				map = QueryExcelHelper.manager.getSelectLibraryPart(param);
			}

			ArrayList<Object> list = (ArrayList<Object>) map.get("list");
			int total = list.size();
			for (int i = 0; i < list.size(); i++) {
				PartLibraryColumnData data = (PartLibraryColumnData) list.get(i);
				int idx = 1;
				sheet.addCell(new Label(0, rows, String.valueOf(total), format));
				for (String key : columns) {
					if (key.equals("thumnail")) {
						continue;
					}
					sheet.addCell(new Label(idx++, rows, data.getValue(key), format));
				}
				rows++;
				total--;
			}
		} else if (type == 3) {
			param.put("context", "PRODUCT");

			if (isAll) {
				map = QueryExcelHelper.manager.getAllProductWTPart();
			} else if (isPage) {
				map = PartHelper.manager.find(param);
			} else if (isSelect) {
				map = QueryExcelHelper.manager.getSelectProductPart(param);
			}

			ArrayList<Object> list = (ArrayList<Object>) map.get("list");
			int total = list.size();
			for (int i = 0; i < list.size(); i++) {
				PartProductColumnData data = (PartProductColumnData) list.get(i);
				int idx = 1;
				sheet.addCell(new Label(0, rows, String.valueOf(total), format));
				for (String key : columns) {
					if (key.equals("thumnail")) {
						continue;
					}
					sheet.addCell(new Label(idx++, rows, data.getValue(key), format));
				}
				rows++;
				total--;
			}
		} else if (type == 4) {
			param.put("context", "LIBRARY");
			if (isAll) {
				map = QueryExcelHelper.manager.getAllLibraryEPMDocument();
			} else if (isPage) {
				map = EpmHelper.manager.find(param);
			} else if (isSelect) {
				map = QueryExcelHelper.manager.getSelectLibraryEpm(param);
			}
			ArrayList<Object> list = (ArrayList<Object>) map.get("list");
			int total = list.size();
			for (int i = 0; i < list.size(); i++) {
				EpmLibraryColumnData data = (EpmLibraryColumnData) list.get(i);
				int idx = 1;
				sheet.addCell(new Label(0, rows, String.valueOf(total), format));
				for (String key : columns) {
					if (key.equals("thumnail")) {
						continue;
					}
					sheet.addCell(new Label(idx++, rows, data.getValue(key), format));
				}
				rows++;
				total++;
			}
		} else if (type == 5) {
			param.put("context", "PRODUCT");
			if (isAll) {
				map = QueryExcelHelper.manager.getAllProductEPMDocument();
			} else if (isPage) {
				map = EpmHelper.manager.find(param);
			} else if (isSelect) {
				map = QueryExcelHelper.manager.getSelectProductEpm(param);
			}
			ArrayList<Object> list = (ArrayList<Object>) map.get("list");
			int total = list.size();
			for (int i = 0; i < list.size(); i++) {
				EpmProductColumnData data = (EpmProductColumnData) list.get(i);
				int idx = 1;
				sheet.addCell(new Label(0, rows, String.valueOf(total), format));
				for (String key : columns) {
					if (key.equals("thumnail")) {
						continue;
					}
					sheet.addCell(new Label(idx++, rows, data.getValue(key), format));
				}
				rows++;
				total++;
			}
		} else if (type == 6) {
//			// 결재함
//			map = ApprovalHelper.manager.findApprovalList(param);
			if (isAll) {
//				map = QueryExcelHelper.manager.getAllProductEPMDocument();
			} else {
				map = QueryExcelHelper.manager.getSelectApprovalList(param);
			}

			ArrayList<ApprovalColumnData> list = (ArrayList<ApprovalColumnData>) map.get("list");
			int total = list.size();
			for (ApprovalColumnData data : list) {
				int idx = 1;
				sheet.addCell(new Label(0, rows, String.valueOf(total), format));
				for (String key : columns) {
					sheet.addCell(new Label(idx++, rows, data.getValue(key), format));
				}
				rows++;
				total--;
			}
		} else if (type == 7) {
//			// 반려함
			if (isAll) {
//				map = QueryExcelHelper.manager.getAllProductEPMDocument();
			} else {
				map = QueryExcelHelper.manager.getSelectReturnList(param);
			}
			ArrayList<ReturnColumnData> list = (ArrayList<ReturnColumnData>) map.get("list");
			int total = list.size();
			for (ReturnColumnData data : list) {
				int idx = 1;
				sheet.addCell(new Label(0, rows, String.valueOf(total), format));
				for (String key : columns) {
					sheet.addCell(new Label(idx++, rows, data.getValue(key), format));
				}
				rows++;
				total--;
			}
		} else if (type == 8) {
			// 완료함
			if (isAll) {
//				map = QueryExcelHelper.manager.getAllProductEPMDocument();
			} else {
				map = QueryExcelHelper.manager.getSelectCompleteList(param);
			}

			ArrayList<CompleteColumnData> list = (ArrayList<CompleteColumnData>) map.get("list");
			int total = list.size();
			for (CompleteColumnData data : list) {
				int idx = 1;
				sheet.addCell(new Label(0, rows, String.valueOf(total), format));
				for (String key : columns) {
					sheet.addCell(new Label(idx++, rows, data.getValue(key), format));
				}
				rows++;
				total--;
			}
		} else if (type == 9) {
			// 수신함
			if (isAll) {
//				map = QueryExcelHelper.manager.getAllProductEPMDocument();
			} else {
				map = QueryExcelHelper.manager.getSelectReceiveList(param);
			}
			ArrayList<ReceiveColumnData> list = (ArrayList<ReceiveColumnData>) map.get("list");
			int total = list.size();
			for (ReceiveColumnData data : list) {
				int idx = 1;
				sheet.addCell(new Label(0, rows, String.valueOf(total), format));
				for (String key : columns) {
					sheet.addCell(new Label(idx++, rows, data.getValue(key), format));
				}
				rows++;
				total--;
			}
		} else if (type == 10) {
			// 합의함
			if (isAll) {
//				map = QueryExcelHelper.manager.getAllProductEPMDocument();
			} else {
				map = QueryExcelHelper.manager.getSelectAgreeList(param);
			}

			ArrayList<AgreeColumnData> list = (ArrayList<AgreeColumnData>) map.get("list");
			int total = list.size();
			for (AgreeColumnData data : list) {
				int idx = 1;
				sheet.addCell(new Label(0, rows, String.valueOf(total), format));
				for (String key : columns) {
					sheet.addCell(new Label(idx++, rows, data.getValue(key), format));
				}
				rows++;
				total--;
			}
		} else if (type == 11) {
			// 진행함
			if (isAll) {
//				map = QueryExcelHelper.manager.getAllProductEPMDocument();
			} else {
				map = QueryExcelHelper.manager.getSelectIngList(param);
			}
			ArrayList<IngColumnData> list = (ArrayList<IngColumnData>) map.get("list");
			int total = list.size();
			for (IngColumnData data : list) {
				int idx = 1;
				sheet.addCell(new Label(0, rows, String.valueOf(total), format));
				for (String key : columns) {
					sheet.addCell(new Label(idx++, rows, data.getValue(key), format));
				}
				rows++;
				total--;
			}
		} else if (type == 12) {
			if (isAll) {
//				map = QueryExcelHelper.manager.getAllProductEPMDocument();
			} else {
				map = QueryExcelHelper.manager.getSelectUserList(param);
			}
			ArrayList<UserColumnData> list = (ArrayList<UserColumnData>) map.get("list");
			int total = list.size();
			for (UserColumnData data : list) {
				int idx = 1;
				sheet.addCell(new Label(0, rows, String.valueOf(total), format));
				for (String key : columns) {
					sheet.addCell(new Label(idx++, rows, data.getValue(key), format));
				}
				rows++;
				total--;
			}
		} else if (type == 13) {
		} else if (type == 14) {
			// 공지사항

			if (isAll) {
				map = QueryExcelHelper.manager.getAllNotice();
			} else if (isPage) {
				map = NoticeHelper.manager.find(param);
			} else if (isSelect) {
				map = QueryExcelHelper.manager.getSelectNotice(param);
			}

			ArrayList<NoticeColumnData> list = (ArrayList<NoticeColumnData>) map.get("list");
			int total = list.size();
			for (NoticeColumnData data : list) {
				int idx = 1;
				sheet.addCell(new Label(0, rows, String.valueOf(total), format));
				for (String key : columns) {
					sheet.addCell(new Label(idx++, rows, data.getValue(key), format));
				}
				rows++;
				total--;
			}
		} else if (type == 15) {
		}
	}

	private int getModule(String module) {
		int type = 0;

		if ("list_document".equals(module)) {
			// 문서
			type = 1;
		} else if ("list_library_part".equals(module)) {
			// 가공품 부품
			type = 2;
		} else if ("list_product_part".equals(module)) {
			// 구매품 부품
			type = 3;
		} else if ("list_library_epm".equals(module)) {
			// 가공품 도면
			type = 4;
		} else if ("list_product_epm".equals(module)) {
			// 구매품 도면
			type = 5;
		} else if ("list_approval".equals(module)) {
			// 결재함
			type = 6;
		} else if ("list_return".equals(module)) {
			// 반려함
			type = 7;
		} else if ("list_complete".equals(module)) {
			// 완료함
			type = 8;
		} else if ("list_receive".equals(module)) {
			// 수신함
			type = 9;
		} else if ("list_agree".equals(module)) {
			// 합의함
			type = 10;
		} else if ("list_ing".equals(module)) {
			// 진행함
			type = 11;
		} else if ("list_user".equals(module)) {
			// 사용자
			type = 12;
		} else if ("list_login".equals(module)) {
			// 접속이력
			type = 13;
		} else if ("list_notice".equals(module)) {
			// 공지사항
			type = 14;
		} else if ("list_bom".equals(module)) {
			// bom
			type = 15;
		}
		return type;
	}

	private void printHeader(WritableSheet sheet, Map<String, Object> param) throws Exception {
		WritableCellFormat format = getCellFormat(Alignment.CENTRE, Colour.LIGHT_GREEN);
		List<String> columns_name = (List<String>) param.get("columns_name");
		int idx = 1;
		sheet.addCell(new Label(0, 0, "NO", format));
		for (String header : columns_name) {
			if (header.equals("")) {
				continue;
			}
			sheet.addCell(new Label(idx++, 0, header, format));
		}
	}

	private WritableCellFormat getCellFormat(Alignment alignment, Colour color) {
		WritableCellFormat format = null;
		try {
			format = new WritableCellFormat();
			if (color != null) {
				format.setBackground(color);
			}
			format.setBorder(Border.ALL, BorderLineStyle.THIN);

			if (alignment != null) {
				format.setAlignment(alignment);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return format;
	}

	private String prefixText(String module) {
		String prefix = "";

		ModuleKeys[] keys = ModuleKeys.values();
		for (int i = 0; i < keys.length; i++) {
			if (keys[i].name().equals(module)) {
				prefix = keys[i].getDisplay();
				break;
			}
		}
		return prefix;
	}
}
