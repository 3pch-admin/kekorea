package e3ps.admin.column;

public enum CodeColumnKeys {

	name("코드 명"), code("코드"), description("설명"), uses("사용여부"), sort("정렬");

	private final String display;

	public String getDisplay() {
		return display;
	}

	private CodeColumnKeys(String display) {
		this.display = display;
	}
}