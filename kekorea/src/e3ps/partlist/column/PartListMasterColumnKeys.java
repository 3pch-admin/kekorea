package e3ps.partlist.column;

public enum PartListMasterColumnKeys {

	// No-설계 구분-수배표 제목-막종-KEK 작번-[KE 작번-USER ID]-작업내용-거래처-설치장소-[발행일-모델-SYSTEM
	// INFO-설명-작성자-작성일]-수정자-수정일-버전-상태

	no("NO"),

	pjtType("설계 구분"),
	
	info(""),

	name("수배표 제목"),

	mak("막종"),

	kekNumber("KEK 작번"),

	keNumber("KE 작번"),

	user_id("USER ID"),

	kek_description("작업내용"),

	customer("거래처"), ins_location("설치 장소"), pDate("발행일"), model("모델"),

	// description("설명"),

	creator("작성자"), createDate("작성일"),

	//

	modifyDate("수정일"), /* version("버전"), */ state("상태");// , classify("분류");

	// KEK 작번 KE 작번 작업내용 고객 설치 장소 상태 버전 작성자 수정자 수정일 분류

	private final String display;

	public String getDisplay() {
		return display;
	}

	private PartListMasterColumnKeys(String display) {
		this.display = display;
	}
}
