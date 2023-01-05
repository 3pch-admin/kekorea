package e3ps.common.content.column;

public enum ContentsColumnKeys {

	// default..
	no("NO"), filename("파일이름"), name("문서제목"), number("문서번호"), description("설명"),/* modelName("MODEL_NAME"), */ version("버전"), state("상태"),
	modifier("수정자"), modifyDate("수정일"), primary("파일");

	private final String display;

	public String getDisplay() {
		return display;
	}

	private ContentsColumnKeys(String display) {
		this.display = display;
	}
}
