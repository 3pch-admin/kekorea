package e3ps.project.enums;

public enum ProjectUserType {

	PM("총괄 책임자"), SUB_PM("세부일정 책임자"), MACHINE("기계"), ELEC("전기"), SOFT("SW");

	public final static String PM_ID = "yspark";

	public final static String SUB_PM_ID = "19940009";

	private final String display;

	public String getDisplay() {
		return display;
	}

	private ProjectUserType(String display) {
		this.display = display;
	}
}
