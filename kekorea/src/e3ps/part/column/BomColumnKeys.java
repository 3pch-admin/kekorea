package e3ps.part.column;

public enum BomColumnKeys {

	level("레벨"), number("부품번호"), name("부품명"), amount("수량"), main_assy("MAIN_ASSY"), version("버전"),
	product_name("PRODUCT_NAME"), machine_type("MACHINE_TYPE"), parallel("PARALLEL"), min_temp("MIN_TEMP"),
	max_temp("MAX_TEMP"), material("MATERIAL"), color_finish("COLOR_FINISH"), dimension("DIMENSION"),
	treatment("TREATMENT"), bom("BOM"), master_type("MASTER_TYPE"), maker("MAKER"), minus_qty("MINUS_QTY"),
	minus_flag("MINUS_FLAG");

	private final String display;

	public String getDisplay() {
		return display;
	}

	private BomColumnKeys(String display) {
		this.display = display;
	}
}
