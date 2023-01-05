package e3ps.common;

public enum ModuleKeys {

	list_code("코드 목록"), add_list_project("작번 조회"), add_eplan_part("EPLAN 부품 추가"), add_old_output(
			"구 산출물 추가"), list_old_output("구 산출물 목록"),

	list_output("산출물 목록"), list_document("문서 목록"), list_product_part("가공품 부품 목록"),

	list_elec_part("전장품 부품 목록"), list_library_part("구매품 부품 목록"), list_eplan_part("EPLAN 부품 목록"),

	list_product_epm("가공품 도면 목록"), list_user("사용자 목록"), add_list_user("사용자 추가"),

	list_library_epm("구매품 도면 목록"), list_approval("결재함"), list_return("반려함"), list_viewer("뷰어 배포"),

	list_complete("완료함"), list_receive("수신함"), list_agree("합의함"), list_ing("진행함"),

	list_login("접속이력"), list_mail("메일관리"), list_bom("BOM"), list_ycode("ycode 목록"), list_unit_bom("UNIT BOM 목록"),

	add_product_part("가공품 부품 추가"), add_library_part("구매품 부품 추가"),

	add_elec_part("전장품 부품 추가"),

	add_list_document("문서 추가"), add_product_epm("가공품 도면 추가"), add_list_old_document("구 문서 추가"),

	add_library_epm("구매품 도면 추가"), list_notice("공지사항 목록"),

	list_ebom("EBOM 목록"), list_ecn("ECN 목록"), list_stn("STN 목록"),

	contents_list("파일 목록"), list_partlist("수배표 통합 조회"), list_request_document("의뢰서 조회"), list_issue("특이사항 조회"),

	// 프로젝트
	list_project("작번 조회"), list_template("템플릿 조회");

	private final String display;

	public String getDisplay() {
		return display;
	}

	private ModuleKeys(String display) {
		this.display = display;
	}
}
