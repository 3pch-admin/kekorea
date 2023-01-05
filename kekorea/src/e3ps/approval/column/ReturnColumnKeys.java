package e3ps.approval.column;

public enum ReturnColumnKeys {

	// default..
	no("NO"), name("결재제목"), returnPoint("반려단계"), /* objType("양식"), */ createDate("기안일"), completeTime("반려일");

	private final String display;

	public String getDisplay() {
		return display;
	}

	private ReturnColumnKeys(String display) {
		this.display = display;
	}
}
