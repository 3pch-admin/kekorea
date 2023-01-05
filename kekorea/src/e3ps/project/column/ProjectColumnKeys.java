package e3ps.project.column;

public enum ProjectColumnKeys {
	// No-작번 유형-거래처-설치장소- KEK 작번-KE 작번-USER ID-작업내용-발행일-요구납기일-[모델-SYSTEM INFO]-기계
	// 담당자-전기 담당자-SW 담당자-진행율-진행상태-작번상태

	// default..
	no("NO"), state("진행상태"), pType("작번 유형"), customer("거래처"), ins_location("설치 장소"),
	// 막종
	mak("막종"), kek_number("KEK 작번"), ke_number("KE 작번"), userId("USER ID"), description("작업 내용"), pDate("발행일"),

	completeDate("설계 완료일"),

	endDate("요구 납기일"), model("모델"),

//	customDate("요구 납기일"),

	// systemInfo("SYSTEM INFO"), /* mak("막종"), */
	machine("기계 담당자"), elec("전기 담당자"), soft("SW 담당자"),

	kekProgress("진행률"), kekState("작번상태");

	private final String display;

	public String getDisplay() {
		return display;
	}

	private ProjectColumnKeys(String display) {
		this.display = display;
	}
}
