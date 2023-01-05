package e3ps.approval.column;

public enum IngColumnKeys {
	// default..
	no("NO"), name("결재제목"), ingPoint("진행단계"), createDate("기안일");

	private final String display;

	public String getDisplay() {
		return display;
	}

	private IngColumnKeys(String display) {
		this.display = display;
	}
}
