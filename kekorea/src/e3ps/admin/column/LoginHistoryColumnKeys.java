package e3ps.admin.column;

public enum LoginHistoryColumnKeys {

	ip("아이피"), id("아이디"), creator("접속자"), createDate("접속일"), lastDate("최종 접속일");

	private final String display;

	public String getDisplay() {
		return display;
	}

	private LoginHistoryColumnKeys(String display) {
		this.display = display;
	}
}
