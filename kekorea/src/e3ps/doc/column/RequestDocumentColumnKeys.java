package e3ps.doc.column;

public enum RequestDocumentColumnKeys {

	// No-작번유형-의뢰서 제목-거래처-설치장소-막종-KEK 작번-KE 작번-[USER ID]-작업내용-버전-상태-[모델-SYSTEM
	// INFO-발행일-작성자-작성일]-수정자-수정일

	no("NO"), pjtType("작번유형"), name("의뢰서 제목"), customer("거래처"), ins_location("설치장소"), mak("막종"), kekNumber(
			"KEK 작번"), keNumber("KE 작번"), user_id("USER ID"), pdescription("작업내용"), ingPoint("검토자"), version("버전"), state("상태"), model(
					"모델"), pDate("발행일"), creator("작성자"), createDate("작성일"), modifier("수정자"), modifyDate("수정일");

	private final String display;

	public String getDisplay() {
		return display;
	}

	private RequestDocumentColumnKeys(String display) {
		this.display = display;
	}
}
