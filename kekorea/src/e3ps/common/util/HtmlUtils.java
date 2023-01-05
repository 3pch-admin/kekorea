package e3ps.common.util;

import java.util.HashMap;

import e3ps.admin.FunctionControl;
import e3ps.admin.service.AdminHelper;
import e3ps.common.ModuleKeys;
import e3ps.org.service.OrgHelper;
import wt.util.WTAttributeNameIfc;

public class HtmlUtils {

	private String numberKey = "master>number";
	private String nameKey = "master>name";
	private String createKey = WTAttributeNameIfc.CREATE_STAMP_NAME;

	private String sortKey = WTAttributeNameIfc.CREATE_STAMP_NAME;
	private HashMap<String, Object> param = new HashMap<String, Object>();

	public HtmlUtils() {
	}

	public HtmlUtils(HashMap<String, Object> param) {
		this.param = param;
	}

	public StringBuffer setColgroup(StringBuffer header, String[] cols) {
		header.append("<colgroup class=\"list_col\">\n");

		for (int i = 0; i < cols.length; i++) {
			// header.append("<col width=\"" + cols[i] + "%;\">\n");
			header.append("<col width=\"" + cols[i] + "px;\">\n");
		}
		header.append("</colgroup>");
		return header;
	}

	public String setHeader(boolean isBox, String[] headers, String[] keys, String[] cols, String[] styles,
			String sort) {
		return setHeader(isBox, headers, keys, cols, styles, sort, false);
	}

	public String setHeader(boolean isBox, String[] headers, String[] keys, String[] cols, String[] styles, String sort,
			boolean isMulti) {

		StringBuffer header = new StringBuffer();
		// header = setColgroup(header, cols);
		header.append("<thead>\n");
		header.append("<tr id=\"header_tr\" class=\"dnd-moved\">\n");
		if (isBox) {
			if (isMulti) {
				header.append(
						"<th data-header=\"checkbox\" class=\"header_check\"><input name=\"all\" id=\"all\" type=\"checkbox\"></th>\n");
			} else {
				header.append("<th data-header=\"checkbox\" class=\"header_check\">&nbsp;</th>\n");
			}
		}

		String sortKey = (String) this.param.get("sortKey");
		if (StringUtils.isNull(sortKey)) {
			sortKey = this.sortKey;
		}

		for (int i = 0; i < headers.length; i++) {

			boolean isSort = false;

			String key = revertKey(keys[i]);

			if (sortKey.equals(key) || sortKey.equals(key) || sortKey.equals(key)) {
				isSort = true;
			}

			header.append("<th class=\"header_th\" " + styles[i] + " class=\"" + keys[i] + "\" data-sortkey=\""
					+ keys[i] + "\" data-header=\"" + keys[i] + "\" id=\"" + keys[i] + "\"><span class=\"th_header\">"
					+ headers[i]);

			if (isSort) {
				if (sort.equals("true")) {
					header.append("<font class=\"up\" title=\"" + headers[i]
							+ " 내림차순\">↓</font><span class=\"header_bars\"><i class=\"axi axi-bars\"></i></span></span></th>\n");
				} else if (sort.equals("false")) {
					header.append("<font class=\"down\" title=\"" + headers[i]
							+ " 오름차순\">↑</font><span class=\"header_bars\"><i class=\"axi axi-bars\"></i></span></span></th>\n");
				}
			} else {
				header.append("<span class=\"header_bars\"><i class=\"axi axi-bars\"></i></span></span></th>\n");
			}
		}

		header.append("</tr>");
		header.append("</thead>\n");
		return header.toString();
	}

	private String revertKey(String key) {
		String keys = "";
		if (key.equals("number")) {
			keys = numberKey;
		} else if (key.equals("name")) {
			keys = nameKey;
		} else if (key.equals("createDate")) {
			keys = createKey;
		}
		return keys;
	}

	public String selectBox(String name, String[] boxKey, String[] boxValue) {
		return selectBox(name, boxKey, boxValue, null, "");
	}

	public String selectBox(String name, String[] boxKey, String[] boxValue, String css, String value) {
		StringBuffer selectBox = new StringBuffer();

		selectBox.append("<select name=\"" + name + "\" id=\"" + name + "\" class=\"AXSelect "
				+ (!StringUtils.isNull(css) ? css : "") + "\">\n");
		selectBox.append("<option value=\"\">선택</option>\n");
		for (int i = 0; i < boxKey.length; i++) {

			boolean bool = boxKey[i].equals(value);

			selectBox.append("<option value=\"" + boxKey[i] + "\"" + (bool ? " selected" : "") + ">" + boxValue[i]
					+ "</option>\n");
		}
		selectBox.append("</select>\n");
		return selectBox.toString();
	}

	public String setContextmenu(String module) throws Exception {
		StringBuffer contextMenu = new StringBuffer();
		FunctionControl fc = AdminHelper.manager.getFuntion();
		// false...
		if (fc != null && !fc.isContextMenu() && !CommonUtils.isAdmin()) {
			contextMenu.append("<div id=\"contextmenu\">\n");
			contextMenu.append("<ul>\n");
			contextMenu.append("<li>권한 없음</li>\n");
			contextMenu.append("</ul>\n");
			contextMenu.append("</div>");
			return contextMenu.toString();
		}

		boolean bool = module.contains(ModuleKeys.list_bom.name());
		String[] headers = ColumnUtils.getColumnHeaders(module);
		String[] styles = ColumnUtils.getColumnStyles(module, headers);
		String[] indexs = ColumnUtils.getColumnIndexs(module);

		contextMenu.append("<div id=\"contextmenu\">\n");
		contextMenu.append("<ul>\n");
		contextMenu.append("<li class=\"center quickTop\"><label title=\"컬럼 숨기기\">컬럼 숨기기</label></li>\n");
		for (int i = 0; i < indexs.length; i++) {

			boolean isCheck = false;
			if (!styles[i].contains("none")) {
				isCheck = true;
			}

			contextMenu.append("<li><label title=\"" + headers[i] + " 컬럼 숨기기\">\n");
			if (isCheck) {
				contextMenu.append("<input checked=\"checked\" name=\"hideBox\" data-headers=\"" + headers[i]
						+ "\"  data-column=\"" + indexs[i] + "\" type=\"checkbox\" class=\"hideColumn\">");
			} else {
				contextMenu.append("<input name=\"hideBox\" data-headers=\"" + headers[i] + "\"  data-column=\""
						+ indexs[i] + "\" type=\"checkbox\" class=\"hideColumn\">");
			}
			contextMenu.append(headers[i]);
			contextMenu.append("</label></li>\n");
		}

		if (!bool) {

			contextMenu.append(
					"<li class=\"main_paging\" title=\"목록 개수 세팅\"><img class=\"pos-1\" src=\"/Windchill/jsp/images/list_view.gif\"><span class=\"quick_paging\">목록 개수 세팅</span>");
			contextMenu.append("<i class=\"axi axi-ion-arrow-right-b\"></i>");
			contextMenu.append("<ul class=\"sub_paging\">\n");

			String[] paging = new String[] { "15", "30", "50", "100" };
			String user_paging = OrgHelper.manager.getUserPaging(module);
			for (int i = 0; i < paging.length; i++) {
				String paging_count = paging[i];
				boolean isPaging = paging_count.equals(user_paging);
				if (isPaging) {
					contextMenu.append("<li title=\"" + paging[i] + "개\"><label><input value=\"" + paging_count
							+ "\" checked=\"checked\" name=\"pagingBox\" class=\"pagingBox\" type=\"radio\">"
							+ paging[i] + "개</label></li>\n");
				} else {
					contextMenu.append("<li title=\"" + paging[i] + "개\"><label><input value=\"" + paging_count
							+ "\" name=\"pagingBox\" class=\"pagingBox\" type=\"radio\" >" + paging[i]
							+ "개</label></li>\n");
				}
			}

			contextMenu.append("</ul>\n");
			contextMenu.append("</li>\n");
		}

		contextMenu.append("</ul>\n");
		contextMenu.append("</div>");
		return contextMenu.toString();
	}

	public String setRightMenu(String module) throws Exception {
		StringBuffer quick = new StringBuffer();

		boolean isAdmin = CommonUtils.isAdmin();

		boolean bool = module.indexOf("list_library_epm") > -1 || module.indexOf("list_product_epm") > -1; // 도면
		boolean bool2 = module.indexOf("list_approval") > -1 || module.indexOf("list_complete") > -1
				|| module.indexOf("list_receive") > -1 || module.indexOf("list_agree") > -1
				|| module.indexOf("list_return") > -1;
		boolean bool3 = module.indexOf("list_notice") > -1;
		// 객체
		boolean bool4 = module.indexOf("list_library_part") > -1 || module.indexOf("list_product_part") > -1; // 도면 //
																												// 부품
		boolean bool5 = module.indexOf("list_ing") > -1; // 진행함
		boolean bool6 = module.indexOf("list_user") > -1; // 유저
		boolean bool7 = module.indexOf("list_document") > -1 || module.indexOf("list_output") > -1; // 문서

		boolean bool11 = module.indexOf("list_ecn") > -1 || module.indexOf("list_ebom") > -1
				|| module.indexOf("list_stn") > -1; // 문서

		boolean bool12 = module.indexOf("contents_list") > -1;

		boolean bool13 = module.indexOf("list_project") > -1 || module.indexOf("list_issue") > -1
				|| module.indexOf("list_viewer") > -1 || module.indexOf("list_template") > -1;

		// 버전 객체..
		boolean bool8 = (bool || bool4 || bool7) && isAdmin && !bool12;

		boolean bool14 = module.indexOf("list_partlist") > -1 || module.indexOf("list_request_document") > -1;

		quick.append("<div id=\"quickmenu\" class=\"rightmenu\">\n");
		quick.append("<ul>\n");
		quick.append("<li class=\"quick\">PLM 퀵 메뉴</li>\n");
		quick.append("<li class=\"noneLine\"><span></span></li>\n");

		if (!bool12) {

//			quick.append(
//					"<li class=\"main_info\" data-title=\"정보보기\" title=\"정보보기\"><img class=\"pos-5\" src=\"/Windchill/jsp/images/help_search.gif\"><span class=\"quick_main_info\">정보보기</span>");
//			quick.append("<i class=\"axi axi-ion-arrow-right-b\"></i>");
//
//			quick.append("<ul class=\"sub_info\">\n");
			quick.append(
					"<li title=\"상세정보\" data-key=\"infoObj\" class=\"infoObj\"><img class=\"pos-1\" src=\"/Windchill/jsp/images/details.gif\"><span class=\"quick_info\">상세정보</span></li>\n");

			if (bool4) {

				quick.append(
						"<li data-title=\"BOM 보기\" title=\"BOM 보기\" class=\"infoBom\" data-key=\"infoBom\"><img class=\"pos-1\" src=\"/Windchill/jsp/images/view_bom.png\"><span class=\"quick_bom\">BOM 보기</span></li>\n");

				quick.append(
						"<li data-title=\"제품 보기\" title=\"제품 보기\" class=\"infoEndPart\" data-key=\"infoEndPart\"><img class=\"pos-1\" src=\"/Windchill/jsp/images/prod.gif\"><span class=\"quick_end_part\">제품 보기</span></li>\n");

				quick.append(
						"<li data-title=\"상위부품 보기\" title=\"상위부품 보기\" class=\"infoUpPart\" data-key=\"infoUpPart\"><img class=\"pos-1\" src=\"/Windchill/jsp/images/up_part.gif\"><span class=\"quick_up_part\">상위부품 보기</span></li>\n");

				quick.append(
						"<li data-title=\"하위부품 보기\" title=\"하위부품 보기\" class=\"infoDownPart\" data-key=\"infoDownPart\"><img class=\"pos-1\" src=\"/Windchill/jsp/images/down_part.png\"><span class=\"quick_down_part\">하위부품 보기</span></li>\n");
			}

			if (!bool2 && !bool3 && !bool5 && !bool6 && !bool12 && !bool11 && !bool13 && !bool14) {
				quick.append(
						"<li data-title=\"버전정보\" title=\"버전이력\" data-key=\"infoVersion\" class=\"infoVersion\"><img src=\"/Windchill/jsp/images/version.gif\"><span class=\"quick_version\">버전정보</span></li>\n");
			}

			if (!bool2 && !bool3 && !bool5 && !bool6 && !bool12 && !bool13) {
				quick.append(
						"<li data-title=\"결재이력\" title=\"결재이력\" data-key=\"infoApprovalHistory\" class=\"infoApprovalHistory\"><img src=\"/Windchill/jsp/images/approved.gif\"><span class=\"quick_approved\">결재이력</span></li>\n");
			}

//			quick.append("</ul>");
//			quick.append("</li>");

		}

		if (bool7) {
			quick.append(
					"<li class=\"main_download\" data-title=\"다운로드\" title=\"다운로드\"><img class=\"pos-1\" src=\"/Windchill/jsp/images/save.gif\"><span class=\"quick_main_download\">다운로드</span>");
			quick.append("<i class=\"axi axi-ion-arrow-right-b\"></i>");

			quick.append("<ul class=\"sub_download\">\n");
			quick.append(
					"<li title=\"일괄 다운로드\" data-key=\"downContentAll\" class=\"downContentAll\"><img class=\"pos-1\" src=\"/Windchill/jsp/images/fileicon/file_multifile.gif\"><span class=\"quick_downContentAll\">일괄 다운로드</span></li>\n");
			quick.append(
					"<li title=\"주 첨부파일\" data-key=\"downPrimary\" class=\"downPrimary\"><img class=\"pos-1\" src=\"/Windchill/jsp/images/fileicon/save_as.png\"><span class=\"quick_downPrimary\">주 첨부파일</span></li>\n");
			quick.append(
					"<li title=\"첨부파일\" data-key=\"downSecondary\" class=\"downSecondary\"><img class=\"pos-1\" src=\"/Windchill/jsp/images/fileicon/file_zip.gif\"><span class=\"quick_downSecondary\">첨부파일</span></li>\n");
			quick.append("</ul>");

			quick.append("</li>");

		}

		if (bool) {

			quick.append(
					"<li class=\"main_epm_function\" data-title=\"추가기능\" title=\"추가기능\"><img class=\"pos-1\" src=\"/Windchill/jsp/images/add_function.gif\"><span class=\"quick_main_epm_function\">추가기능</span>");
			quick.append("<i class=\"axi axi-ion-arrow-right-b\"></i>");

			quick.append("<ul class=\"sub_epm_function\">\n");

			quick.append(
					"<li data-title=\"도면 출력\" title=\"도면 출력\" data-key=\"printDrw\" class=\"printDrw\"><img src=\"/Windchill/jsp/images/print.png\"><span class=\"quick_print\">도면 출력</span></li>");
			quick.append(
					"<li data-title=\"CreoView 오픈\" title=\"CreoView 오픈\" data-key=\"creoViewOpen\" class=\"creoViewOpen\"><img class=\"pos-1\" class=\"creoViewOpen\" src=\"/Windchill/jsp/images/creo_view.png\"><span class=\"quick_creoViewOpen\">CreoView 오픈</span></li>");
			quick.append(
					"<li data-title=\"썸네일 생성\" title=\"썸네일 생성\" class=\"publishThum\" data-key=\"publishThum\"><img src=\"/Windchill/jsp/images/fileicon/thumbnail.png\"><span class=\"quick_publishThum\">썸네일 생성</span></li>\n");
			quick.append("</ul>");
			quick.append("</li>");

			quick.append(
					"<li class=\"main_drw\" data-title=\"도면 다운로드\" title=\"도면 다운로드\"><img class=\"pos-1\" src=\"/Windchill/jsp/images/download.gif\"><span class=\"quick_down\">도면 다운로드</span>");
			quick.append("<i class=\"axi axi-ion-arrow-right-b\"></i>");
			quick.append("<ul class=\"sub_drw\">\n");
			quick.append(
					"<li title=\"일괄 다운로드\" data-key=\"downAll\" class=\"downAll\"><img class=\"pos-1\" src=\"/Windchill/jsp/images/fileicon/file_multifile.gif\"><span class=\"quick_all\">일괄 다운로드</span></li>\n");
			quick.append(
					"<li title=\"PDF 다운로드\" data-key=\"downPdf\" class=\"downPdf\"><img class=\"pos-1 left-1\" src=\"/Windchill/jsp/images/fileicon/file_pdf.gif\"><span class=\"quick_pdf\">PDF</span></li>\n");
			quick.append(
					"<li title=\"2D (DWG) 다운로드\" data-key=\"downDwg\" class=\"downDwg\"><img class=\"pos-1\" src=\"/Windchill/jsp/images/fileicon/file_dwg.gif\"><span class=\"quick_dwg\">2D (DWG)</span></li>\n");
			quick.append("</ul>\n");
			quick.append("</li>\n");

			if (isAdmin) {
				quick.append(
						"<li class=\"main_epm_erp\" data-title=\"ERP 전송\" title=\"ERP 전송\"><img class=\"pos-1\" src=\"/Windchill/jsp/images/erp.png\"><span class=\"quick_epm_erp\">ERP 전송</span>");
				quick.append("<i class=\"axi axi-ion-arrow-right-b\"></i>");
				quick.append("<ul class=\"sub_epm_erp\">\n");

				quick.append(
						"<li data-title=\"도면 전송\" title=\"도면 전송\" class=\"sendERPDRW\" data-key=\"sendERPDRW\"><img class=\"pos-1\" src=\"/Windchill/jsp/images/fileicon/file_dwg.gif\"><span class=\"quick_send_drw\">도면 전송</span></li>\n");
				quick.append(
						"<li data-title=\"PDF 전송\" title=\"PDF 전송\" class=\"sendERPPDF\" data-key=\"sendERPPDF\"><img class=\"pos-1\" src=\"/Windchill/jsp/images/fileicon/file_pdf.gif\"><span class=\"quick_send_pdf\">PDF 전송</span></li>\n");
//				quick.append(
//						"<li data-title=\"ERP 전송 이력\" title=\"ERP 전송 이력\" class=\"viewERPHistory\" data-key=\"viewERPHistory\"><img class=\"pos-1\" src=\"/Windchill/jsp/images/fileicon/history.gif\"><span class=\"quick_viewERPHistory\">ERP 전송 이력</span></li>\n");
				quick.append("</ul>\n");
				quick.append("</li>\n");
			}
		}

		if (bool4) {

			quick.append(
					"<li class=\"main_part_function\" data-title=\"추가기능\" title=\"추가기능\"><img class=\"pos-1\" src=\"/Windchill/jsp/images/add_function.gif\"><span class=\"quick_main_part_function\">추가기능</span>");
			quick.append("<i class=\"axi axi-ion-arrow-right-b\"></i>");

			quick.append("<ul class=\"sub_part_function\">\n");

			quick.append(
					"<li data-title=\"CreoView 오픈\" title=\"CreoView 오픈\" data-key=\"creoViewOpen\" class=\"creoViewOpen\"><img class=\"pos-1\" class=\"creoViewOpen\" src=\"/Windchill/jsp/images/creo_view.png\"><span class=\"quick_creoViewOpen\">CreoView 오픈</span></li>");
//			quick.append(
//					"<li data-title=\"BOM 비교\" title=\"BOM 비교\" data-key=\"compareBom\" class=\"compareBom\"><img class=\"pos-1\" class=\"creoViewOpen\" src=\"/Windchill/jsp/images/compareBom.gif\"><span class=\"quick_compareBom\">BOM 비교</span></li>");
//			quick.append(
//					"<li data-title=\"BOM 에디터\" title=\"BOM 에디터\" class=\"bomEditor\" data-key=\"bomEditor\"><img class=\"pos-1\" src=\"/Windchill/jsp/images/bom_editor.gif\"><span class=\"quick_bom_editor\">BOM 에디터</span></li>\n");
//			quick.append(
//					"<li data-title=\"파생품 생성\" title=\"파생품 생성\" class=\"saveAsObject\" data-key=\"saveAsObject\"><img src=\"/Windchill/jsp/images/saveas.gif\"><span class=\"quick_saveas\">파생품 생성</span></li>\n");
//			quick.append(
//					"<li data-title=\"BOM 엑셀\" title=\"BOM 엑셀\" class=\"exportBom\" data-key=\"exportBom\"><img src=\"/Windchill/jsp/images/fileicon/file_excel.gif\"><span class=\"quick_exportBom\">BOM 엑셀</span></li>\n");
			quick.append(
					"<li data-title=\"썸네일 생성\" title=\"썸네일 생성\" class=\"publishThum\" data-key=\"publishThum\"><img src=\"/Windchill/jsp/images/fileicon/thumbnail.png\"><span class=\"quick_publishThum\">썸네일 생성</span></li>\n");
			quick.append("</ul>");
			quick.append("</li>");

			if (isAdmin) {
				quick.append(
						"<li class=\"main_part_erp\" data-title=\"ERP 전송\" title=\"ERP 전송\"><img class=\"pos-1\" src=\"/Windchill/jsp/images/erp.png\"><span class=\"quick_part_erp\">ERP 전송</span>");
				quick.append("<i class=\"axi axi-ion-arrow-right-b\"></i>");
				quick.append("<ul class=\"sub_part_erp\">\n");

				quick.append(
						"<li data-title=\"부품 전송\" title=\"부품 전송\" class=\"sendERPPART\" data-key=\"sendERPPART\"><img class=\"pos-1\" src=\"/Windchill/jsp/images/part.gif\"><span class=\"quick_send_part\">부품 전송</span></li>\n");
				quick.append(
						"<li data-title=\"BOM 전송\" title=\"BOM 전송\" class=\"sendERPBOM\" data-key=\"sendERPBOM\"><img class=\"pos-1\" src=\"/Windchill/jsp/images/view_bom.png\"><span class=\"quick_send_bom\">BOM 전송</span></li>\n");
				quick.append(
						"<li data-title=\"ERP 전송 이력\" title=\"ERP 전송 이력\" class=\"viewERPHistory\" data-key=\"viewERPHistory\"><img class=\"pos-1\" src=\"/Windchill/jsp/images/fileicon/history.gif\"><span class=\"quick_viewERPHistory\">ERP 전송 이력</span></li>\n");
				quick.append("</ul>\n");
				quick.append("</li>\n");
			}
		}

		if (bool12) {
			quick.append(
					"<li data-title=\"다운로드\" title=\"다운로드\" class=\"contentsDown\" data-key=\"contentsDown\"><img src=\"/Windchill/jsp/images/save.gif\"><span class=\"quick_contentsDown\">다운로드</span></li>\n");
		}

		if (bool6 && isAdmin) {
			quick.append(
					"<li data-title=\"퇴사처리\" title=\"퇴사처리\" class=\"resign\" data-key=\"deleteObject\"><img src=\"/Windchill/jsp/images/delete.png\"><span class=\"quick_resignt\">퇴사처리</span></li>\n");
		}

		if (bool8) {
//			quick.append(
//					"<li data-title=\"상태값 설정\" title=\"상태값 설정\" class=\"setState\" data-key=\"setState\"><img src=\"/Windchill/jsp/images/setState.png\"><span class=\"quick_setState\">상태값 설정</span>\n");
//			quick.append("<i class=\"axi axi-ion-arrow-right-b\"></i>");
//			quick.append("<ul class=\"sub_state_info\">\n");
//
//			StateKeys[] stateKeys = StateKeys.values();
//			for (int k = 0; k < stateKeys.length; k++) {
//				String display = stateKeys[k].getDisplay();
//				String keys = stateKeys[k].name();
//				quick.append("<li title=\"" + display + "\"><label class=\"state\"><input value=\"" + keys
//						+ "\" name=\"stateBox\" class=\"stateBox\" data-display=\"" + display + "\" type=\"radio\" >"
//						+ display + "</label></li>\n");
//			}
//			quick.append(
//					"<li class=\"setStateObj center\" title=\"상태값 설정\" data-key=\"setStateObj\"><img src=\"/Windchill/jsp/images/setState.png\"><span>상태값 변경</span></li>");
//			quick.append("</ul>\n");
//			quick.append("</li>\n");
		}

//		if (bool5) {
//			quick.append(
//					"<li data-title=\"메일전송\" title=\"메일전송\" class=\"sendMail\" data-key=\"sendMail\" class=\"sendMail\"><img src=\"/Windchill/jsp/images/posting.gif\"><span class=\"quick_mail\">메일전송</span></li>\n");
//		}

		if (!bool12) {

			String[] display = ColumnUtils.getColumnHeaders(module);
			String[] keys = ColumnUtils.getColumnKeys(module);
			quick.append(
					"<li class=\"main_excel\" title=\"엑셀출력\"><img src=\"/Windchill/jsp/images/fileicon/file_excel.gif\"><span class=\"quick_excel\">엑셀출력</span>");
			quick.append("<i class=\"axi axi-ion-arrow-right-b\"></i>");
			quick.append("<ul class=\"sub_excel\">\n");

			for (int i = 0; i < display.length; i++) {
				String column_key = keys[i];
				if (column_key.equalsIgnoreCase("no") || column_key.equalsIgnoreCase("thumnail")
						|| column_key.equalsIgnoreCase("primary")) {
					continue;
				}
				quick.append("<li title=\"" + display[i] + "\"><label><input value=\"" + column_key
						+ "\" name=\"excelBox\" class=\"excelBox\" data-display=\"" + display[i]
						+ "\" checked=\"checked\" type=\"checkbox\" >" + display[i] + "</label></li>\n");
			}
			quick.append("<li class=\"excelExport center\" data-len=\"" + display.length
					+ "\" title=\"엑셀출력\" data-key=\"exportExcel\" data-opt=\"all\"><img src=\"/Windchill/jsp/images/fileicon/file_excel.gif\"><span>엑셀출력(전체)</span></li>");
			quick.append("<li class=\"excelExport center\" data-len=\"" + display.length
					+ "\" title=\"엑셀출력\" data-key=\"exportExcel\" data-opt=\"page\"><img src=\"/Windchill/jsp/images/fileicon/file_excel.gif\"><span>엑셀출력(현재페이지)</span></li>");
			quick.append("<li class=\"excelExport center\" data-len=\"" + display.length
					+ "\" title=\"엑셀출력\" data-key=\"exportExcel\" data-opt=\"select\"><img src=\"/Windchill/jsp/images/fileicon/file_excel.gif\"><span>엑셀출력(선택항목)</span></li>");
			quick.append("</ul>");
			quick.append("</li>\n");
		}

		if (isAdmin && !bool6 && !bool12) {
			quick.append(
					"<li data-title=\"삭제\" title=\"삭제\" class=\"deleteObject\" data-key=\"deleteObject\"><img src=\"/Windchill/jsp/images/delete.png\"><span class=\"quick_deleteObject\">삭제</span></li>\n");
		}

		// 전체 ul end
		quick.append("</ul>\n");

		quick.append("</div>");
		return quick.toString();
	}

	public String setRightMenuMulti(String module) throws Exception {
		StringBuffer quick = new StringBuffer();

		boolean isAdmin = CommonUtils.isAdmin();

		boolean bool = module.indexOf("list_library_epm") > -1 || module.indexOf("list_product_epm") > -1; // 도면
		// 객체
		boolean bool4 = module.indexOf("list_library_part") > -1 || module.indexOf("list_product_part") > -1; // 도면 //
																												// 부품
		boolean bool7 = module.indexOf("list_document") > -1 || module.indexOf("list_output") > -1; // 문서
		boolean bool6 = module.indexOf("list_user") > -1;

		// boolean bool11 = module.indexOf("list_ecn") > -1 ||
		// module.indexOf("list_ebom") > -1
		// || module.indexOf("list_stn") > -1; // 문서

		// 버전 객체..
		boolean bool8 = (bool || bool4 || bool7) && isAdmin;

		boolean bool9 = module.indexOf("contents_list") > -1;

		quick.append("<div id=\"quickmenu_multi\" class=\"rightmenu_multi\">\n");
		quick.append("<ul>\n");
		quick.append("<li class=\"quick\">PLM 퀵 메뉴</li>\n");
		quick.append("<li class=\"noneLine\"><span></span></li>\n");

		if (bool9) {
			quick.append(
					"<li data-title=\"다운로드\" title=\"다운로드\" class=\"contentsMultiDown\" data-key=\"contentsMultiDown\"><img src=\"/Windchill/jsp/images/save.gif\"><span class=\"quick_contentsMultiDown\">다운로드</span></li>\n");
		}

		if (bool4) {
//			quick.append(
//					"<li data-title=\"파생품 생성\" title=\"파생품 생성\" class=\"saveAsObject\" data-key=\"saveAsObject\"><img src=\"/Windchill/jsp/images/saveas.gif\"><span class=\"quick_saveAsObject\">파생품 생성</span></li>\n");
			quick.append(
					"<li data-title=\"썸네일 생성\" title=\"썸네일 생성\" class=\"publishThumMulti\" data-key=\"publishThumMulti\"><img src=\"/Windchill/jsp/images/fileicon/thumbnail.png\"><span class=\"quick_publishThumMulti\">썸네일 생성</span></li>\n");
		}

		if (bool) {
			quick.append(
					"<li data-title=\"썸네일 생성\" title=\"썸네일 생성\" class=\"publishThumMulti\" data-key=\"publishThumMulti\"><img src=\"/Windchill/jsp/images/fileicon/thumbnail.png\"><span class=\"quick_publishThumMulti\">썸네일 생성</span></li>\n");
			quick.append(
					"<li data-title=\"도면 출력\" title=\"도면 출력\" data-key=\"printDrw\" class=\"printDrw\"><img src=\"/Windchill/jsp/images/print.png\"><span class=\"quick_print\">도면 출력</span></li>");
			quick.append(
					"<li class=\"main_drw\" data-title=\"도면 다운로드\" title=\"도면 다운로드\"><img class=\"pos-1\" src=\"/Windchill/jsp/images/download.gif\"><span class=\"quick_down\">도면 다운로드</span>");
			quick.append("<i class=\"axi axi-ion-arrow-right-b\"></i>");
			quick.append("<ul class=\"sub_drw\">\n");
			quick.append(
					"<li title=\"일괄 다운로드\" data-key=\"downAll\" class=\"downAll\"><img class=\"pos-1\" src=\"/Windchill/jsp/images/fileicon/file_multifile.gif\"><span class=\"quick_all\">일괄 다운로드</span></li>\n");
			quick.append(
					"<li title=\"PDF 다운로드\" data-key=\"downPdf\" class=\"downPdf\"><img class=\"pos-1 left-1\" src=\"/Windchill/jsp/images/fileicon/file_pdf.gif\"><span class=\"quick_pdf\">PDF</span></li>\n");
			quick.append(
					"<li title=\"2D (DWG) 다운로드\" data-key=\"downDwg\" class=\"downDwg\"><img class=\"pos-1\" src=\"/Windchill/jsp/images/fileicon/file_dwg.gif\"><span class=\"quick_dwg\">2D (DWG)</span></li>\n");
			quick.append("</ul>\n");
			quick.append("</li>\n");

			if (isAdmin) {
				quick.append(
						"<li class=\"main_epm_erp\" data-title=\"ERP 전송\" title=\"ERP 전송\"><img class=\"pos-1\" src=\"/Windchill/jsp/images/erp.png\"><span class=\"quick_epm_erp\">ERP 전송</span>");
				quick.append("<i class=\"axi axi-ion-arrow-right-b\"></i>");
				quick.append("<ul class=\"sub_epm_erp\">\n");

				quick.append(
						"<li data-title=\"도면 전송\" title=\"도면 전송\" class=\"sendERPDRW\" data-key=\"sendERPDRW\"><img class=\"pos-1\" src=\"/Windchill/jsp/images/fileicon/file_dwg.gif\"><span class=\"quick_send_drw\">도면 전송</span></li>\n");
				quick.append(
						"<li data-title=\"PDF 전송\" title=\"PDF 전송\" class=\"sendERPPDF\" data-key=\"sendERPPDF\"><img class=\"pos-1\" src=\"/Windchill/jsp/images/fileicon/file_pdf.gif\"><span class=\"quick_send_pdf\">PDF 전송</span></li>\n");
				quick.append("</ul>\n");
				quick.append("</li>\n");
			}
		}

		if (bool7) {
			quick.append(
					"<li class=\"main_download\" data-title=\"다운로드\" title=\"다운로드\"><img class=\"pos-1\" src=\"/Windchill/jsp/images/save.gif\"><span class=\"quick_main_download\">다운로드</span>");
			quick.append("<i class=\"axi axi-ion-arrow-right-b\"></i>");

			quick.append("<ul class=\"sub_download\">\n");
			quick.append(
					"<li title=\"일괄 다운로드\" data-key=\"downContentAll\" class=\"downContentAll\"><img class=\"pos-1\" src=\"/Windchill/jsp/images/fileicon/file_multifile.gif\"><span class=\"quick_downContentAll\">일괄 다운로드</span></li>\n");
			quick.append(
					"<li title=\"주 첨부파일\" data-key=\"downPrimary\" class=\"downPrimary\"><img class=\"pos-1\" src=\"/Windchill/jsp/images/fileicon/save_as.png\"><span class=\"quick_downPrimary\">주 첨부파일</span></li>\n");
			quick.append(
					"<li title=\"첨부파일\" data-key=\"downSecondary\" class=\"downSecondary\"><img class=\"pos-1\" src=\"/Windchill/jsp/images/fileicon/file_zip.gif\"><span class=\"quick_downSecondary\">첨부파일</span></li>\n");
			quick.append("</ul>");

			quick.append("</li>");

		}

		if (bool6 && isAdmin) {
			quick.append(
					"<li data-title=\"퇴사처리\" title=\"퇴사처리\" class=\"resign\" data-key=\"deleteObject\"><img src=\"/Windchill/jsp/images/delete.png\"><span class=\"quick_resignt\">퇴사처리</span></li>\n");
		}

		if (bool8) {
//			quick.append(
//					"<li data-title=\"상태값 설정\" title=\"상태값 설정\" class=\"setState\" data-key=\"setState\"><img src=\"/Windchill/jsp/images/setState.png\"><span class=\"quick_setState\">상태값 설정</span>\n");
//			quick.append("<i class=\"axi axi-ion-arrow-right-b\"></i>");
//			quick.append("<ul class=\"sub_state_info\">\n");
//
//			StateKeys[] stateKeys = StateKeys.values();
//			for (int k = 0; k < stateKeys.length; k++) {
//				String display = stateKeys[k].getDisplay();
//				String keys = stateKeys[k].name();
//				quick.append("<li title=\"" + display + "\"><label class=\"state\"><input value=\"" + keys
//						+ "\" name=\"stateBox\" class=\"stateBox\" data-display=\"" + display + "\" type=\"radio\" >"
//						+ display + "</label></li>\n");
//			}
//			quick.append(
//					"<li class=\"setStateObj center\" title=\"상태값 설정\" data-key=\"setStateObj\"><img src=\"/Windchill/jsp/images/setState.png\"><span>상태값 변경</span></li>");
//			quick.append("</ul>\n");
//			quick.append("</li>\n");
		}

//		if (!bool9) {
//			String[] display = ColumnUtils.getColumnHeaders(module);
//			String[] keys = ColumnUtils.getColumnKeys(module);
//			quick.append(
//					"<li class=\"main_excel\" title=\"엑셀출력\"><img src=\"/Windchill/jsp/images/fileicon/file_excel.gif\"><span class=\"quick_excel\">엑셀출력</span>");
//			quick.append("<i class=\"axi axi-ion-arrow-right-b\"></i>");
//			quick.append("<ul class=\"sub_excel\">\n");
//
//			for (int i = 0; i < display.length; i++) {
//				String column_key = keys[i];
//
//				if (column_key.equalsIgnoreCase("no") || column_key.contentEquals("thumnail")) {
//					continue;
//				}
//
//				quick.append("<li title=\"" + display[i] + "\"><label><input value=\"" + column_key
//						+ "\" name=\"excelBox\" class=\"excelBox\" data-display=\"" + display[i]
//						+ "\" checked=\"checked\" type=\"checkbox\" >" + display[i] + "</label></li>\n");
//			}
//			quick.append("<li class=\"excelExport center\" data-len=\"" + display.length
//					+ "\" title=\"엑셀출력\" data-key=\"exportExcel\" data-opt=\"all\"><img src=\"/Windchill/jsp/images/fileicon/file_excel.gif\"><span>엑셀출력(전체)</span></li>");
//			quick.append("<li class=\"excelExport center\" data-len=\"" + display.length
//					+ "\" title=\"엑셀출력\" data-key=\"exportExcel\" data-opt=\"page\"><img src=\"/Windchill/jsp/images/fileicon/file_excel.gif\"><span>엑셀출력(현재페이지)</span></li>");
//			quick.append("<li class=\"excelExport center\" data-len=\"" + display.length
//					+ "\" title=\"엑셀출력\" data-key=\"exportExcel\" data-opt=\"select\"><img src=\"/Windchill/jsp/images/fileicon/file_excel.gif\"><span>엑셀출력(선택항목)</span></li>");
//			quick.append("</ul>");
//			quick.append("</li>\n");
//		}

		if (isAdmin && !bool6 && !bool9) {
			quick.append(
					"<li data-title=\"삭제\" title=\"삭제\" class=\"deleteObject\" data-key=\"deleteObject\"><img src=\"/Windchill/jsp/images/delete.png\"><span class=\"quick_deleteObject\">삭제</span></li>\n");
		}
		// 전체 ul end
		quick.append("</ul>\n");

		quick.append("</div>");
		return quick.toString();
	}
}
