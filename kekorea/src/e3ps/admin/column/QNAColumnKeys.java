package e3ps.admin.column;

public enum QNAColumnKeys {

	name("제목"), description("설명"), creator("작성자"), createDate("작성일");

	private final String display;

	public String getDisplay() {
		return display;
	}

	private QNAColumnKeys(String display) {
		this.display = display;
	}
}