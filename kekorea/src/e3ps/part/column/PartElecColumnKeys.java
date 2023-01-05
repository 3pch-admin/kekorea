package e3ps.part.column;

public enum PartElecColumnKeys {

	// default..
	no("NO"), number("전장품 부품번호"), thumnail(""), name("전장품 부품명"), state("상태"), version("버전"), creator("등록자"), createDate("등록일");

	private final String display;

	public String getDisplay() {
		return display;
	}

	private PartElecColumnKeys(String display) {
		this.display = display;
	}
}