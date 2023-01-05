package e3ps.epm.column;

public enum ViewerColumnKeys {

	// default..
	no("NO"), fileName("파일이름"), name("품명"), number("규격"), primary("첨부파일"),

	creator("작성자"), createDate("작성일");

	private final String display;

	public String getDisplay() {
		return display;
	}

	private ViewerColumnKeys(String display) {
		this.display = display;
	}
}