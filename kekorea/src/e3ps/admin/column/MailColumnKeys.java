package e3ps.admin.column;

public enum MailColumnKeys {

	no("NO"), isUse("사용여부"), type("타입"), description("설명");

	private final String display;

	public String getDisplay() {
		return display;
	}

	private MailColumnKeys(String display) {
		this.display = display;
	}
}
