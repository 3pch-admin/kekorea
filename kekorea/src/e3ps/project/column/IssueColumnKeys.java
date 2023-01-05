package e3ps.project.column;

public enum IssueColumnKeys {
	// No-작번 유형-거래처-설치장소- KEK 작번-KE 작번-USER ID-작업내용-발행일-요구납기일-[모델-SYSTEM INFO]-기계
	// 담당자-전기 담당자-SW 담당자-진행율-진행상태-작번상태

	// default..
	no("NO"), name("제목"), description("설명"), kek_number("KEK 작번"), ke_number("KE 작번"),
	pDescription("작업 내용"), mak("막종"), creator("작성자"), createDate("작성일");

	private final String display;

	public String getDisplay() {
		return display;
	}

	private IssueColumnKeys(String display) {
		this.display = display;
	}
}
