package e3ps.doc.column;

public enum OutputColumnKeys {

//	No-문서제목-[문서번호-설명-문서분류]-KEK 작번-작업내용-막종-상태-버전-[작성자-작성일]수정자-수정일-파일
	
	// default..
	no("NO"), name("산출물 제목"), number("산출물 번호"), description("설명"),
	
	location("산출물 분류"), 
	
//	ke_number("KE 작번"),kek_number("KEK 작번"), kek_description("작업내용"),
	
//	mak("막종"),

	state("상태"), version("버전"), creator("작성자"), createDate("작성일"),

	modifier("수정자"), modifyDate("수정일"), primary("파일");

	private final String display;

	public String getDisplay() {
		return display;
	}

	private OutputColumnKeys(String display) {
		this.display = display;
	}
}
