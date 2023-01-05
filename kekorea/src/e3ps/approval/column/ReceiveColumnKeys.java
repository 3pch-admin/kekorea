package e3ps.approval.column;

public enum ReceiveColumnKeys {

	// default..
	no("NO"), read("확인"), type("구분"),

	role("역할"), name("결재제목"), ingPoint("진행단계"), submiter("기안자"), state("상태"), receiveTime("수신일");

	private final String display;

	public String getDisplay() {
		return display;
	}

	private ReceiveColumnKeys(String display) {
		this.display = display;
	}
}
