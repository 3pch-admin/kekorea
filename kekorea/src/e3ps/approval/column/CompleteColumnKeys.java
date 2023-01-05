package e3ps.approval.column;

public enum CompleteColumnKeys {

	// default..
	no("NO"),

	name("결재제목"), ingPoint("진행단계"), submiter("기안자"),  state("상태"), receiveTime("수신일"), 

	completeTime("완료일");

	private final String display;

	public String getDisplay() {
		return display;
	}

	private CompleteColumnKeys(String display) {
		this.display = display;
	}
}
