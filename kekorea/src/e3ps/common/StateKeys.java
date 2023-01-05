package e3ps.common;

public enum StateKeys {

	INWORK("작업 중"), RETURN("반려됨"), UNDERAPPROVAL("승인 중"), APPROVED("승인됨"), WITHDRAWN("폐기");
	// UNDERAPPROVAL APPROVING
	private final String display;

	public String getDisplay() {
		return display;
	}

	private StateKeys(String display) {
		this.display = display;
	}
}
