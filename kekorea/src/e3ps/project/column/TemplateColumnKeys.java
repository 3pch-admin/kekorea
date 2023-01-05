package e3ps.project.column;

public enum TemplateColumnKeys {

	// default..
	no("NO"), name("템플릿 이름"), duration("기간"), creator("작성자"), createDate("작성일"), modifier("수정자"), modifyDate("수정일");

	private final String display;

	public String getDisplay() {
		return display;
	}

	private TemplateColumnKeys(String display) {
		this.display = display;
	}
}
