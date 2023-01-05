package e3ps.approval.column;

public enum ErrorReportColumnKeys {
	// default..
	no("NO"), name("에러제목"), description("내용"), creator("작성자"), completeTime("완료시간"), complete("완료여부");

	private final String display;

	public String getDisplay() {
		return display;
	}

	private ErrorReportColumnKeys(String display) {
		this.display = display;
	}
}
