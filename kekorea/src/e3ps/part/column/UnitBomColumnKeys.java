package e3ps.part.column;

public enum UnitBomColumnKeys {

	no("NO"), ucode("UCODE"), partName("품명"), spec("규격"), unit("기준단위"),

	maker("메이커"), customer("기본구매처"), currency("통화"), price("단가");

	private final String display;

	public String getDisplay() {
		return display;
	}

	private UnitBomColumnKeys(String display) {
		this.display = display;
	}
}
