package e3ps.part.column;

import java.util.ArrayList;

import e3ps.common.util.ContentUtils;
import e3ps.common.util.IBAUtils;
import e3ps.epm.dto.CADAttr;
import e3ps.epm.dto.PRODUCTAttr;
import e3ps.part.beans.BOMAttr;
import wt.part.WTPart;
import wt.part.WTPartUsageLink;

public class BomColumnData {

	public WTPart part;
	public String oid;
	public int level;
	public String number;
	public String name;
	public WTPartUsageLink link;
	public double amount = 1;

	public String state;
	public String stateKey;
	// bom 속성
	public String main_assy;
	public String version;
	public String product_name;
	public String machine_type;
	public String parallel;
	public String min_temp;
	public String max_temp;

	public String material;
	public String spec;
	public String color_finish;
	public String dimension;
	public String treatment;
	public String bom;
	public String master_type;
	public String maker;
	public String minus_qty;
	public String minus_flag;

	public String iconPath;

	public boolean isLibrary = false;
	public boolean isProduct = false;

	public ArrayList<BomColumnData> children = new ArrayList<BomColumnData>();
	public BomColumnData parent = null;

	public BomColumnData(WTPart part) throws Exception {
		this(part, null, 0);
	}

	public BomColumnData(WTPart part, WTPartUsageLink link, int level) throws Exception {
		this.part = part;
		this.oid = part.getPersistInfo().getObjectIdentifier().getStringValue();
		this.level = level;
		this.number = part.getNumber();

		if (link != null) {
			this.link = link;
			this.amount = link.getQuantity().getAmount();
			this.minus_qty = IBAUtils.getStringValue(this.link, BOMAttr.MINUS_QTY.name());
			this.minus_flag = IBAUtils.getStringValue(this.link, BOMAttr.MINUS_FLAG.name());
		}

		this.state = part.getLifeCycleState().getDisplay();
		this.stateKey = part.getLifeCycleState().toString();
		this.name = part.getName();
		this.version = part.getVersionIdentifier().getSeries().getValue() + "."
				+ part.getIterationIdentifier().getSeries().getValue();

		this.machine_type = IBAUtils.getStringValue(part, PRODUCTAttr.MACHINE_TYPE.name());
		this.parallel = IBAUtils.getStringValue(part, PRODUCTAttr.PARALLEL.name());
		this.min_temp = IBAUtils.getStringValue(part, PRODUCTAttr.MIN_TEMP.name());
		this.max_temp = IBAUtils.getStringValue(part, PRODUCTAttr.MAX_TEMP.name());

		this.color_finish = IBAUtils.getStringValue(part, CADAttr.COLOR_FINISH.name());
		this.spec = IBAUtils.getStringValue(part, "SPEC");
		this.main_assy = IBAUtils.getStringValue(part, CADAttr.MAIN_ASSY.name());
		this.maker = IBAUtils.getStringValue(part, "MAKER");
		this.material = IBAUtils.getStringValue(part, CADAttr.MATERIAL.name());
		this.product_name = IBAUtils.getStringValue(part, CADAttr.PRODUCT_NAME.name());
		this.treatment = IBAUtils.getStringValue(part, CADAttr.TREATMENT.name());
		this.dimension = IBAUtils.getStringValue(part, CADAttr.DIMENSION.name());
		this.bom = IBAUtils.getStringValue(part, CADAttr.BOM.name());
		this.master_type = IBAUtils.getStringValue(part, "MASTER_TYPE");

		this.iconPath = ContentUtils.getStandardIcon(part);

		if (this.part.getContainer().getName().equalsIgnoreCase("demo")) {
			this.isProduct = true;
		} else if (this.part.getContainer().getName().equalsIgnoreCase("LIBRARY")) {
			this.isLibrary = true;
		}
	}

	public String getValue(String key) {
		String value = "";
		if (key.equals("name")) {
			value = this.name;
		} else if (key.equals("number")) {
			value = this.number;
		} else if (key.equals("version")) {
			value = this.version;
		} else if (key.equals("amount")) {
			value = String.valueOf(this.amount);
		} else if (key.equals("level")) {
			value = String.valueOf(this.level);
		} else if (key.equals("machine_type")) {
			value = this.machine_type;
		} else if (key.equals("parallel")) {
			value = this.parallel;
		} else if (key.equals("min_temp")) {
			value = this.min_temp;
		} else if (key.equals("max_temp")) {
			value = this.max_temp;
		} else if (key.equals("color_finish")) {
			value = this.color_finish;
		} else if (key.equals("main_assy")) {
			value = this.main_assy;
		} else if (key.equals("maker")) {
			value = this.main_assy;
		} else if (key.equals("material")) {
			value = this.main_assy;
		} else if (key.equals("product_name")) {
			value = this.product_name;
		} else if (key.equals("treatment")) {
			value = this.treatment;
		} else if (key.equals("dimension")) {
			value = this.dimension;
		} else if (key.equals("bom")) {
			value = this.bom;
		} else if (key.equals("master_type")) {
			value = this.master_type;
		} else if (key.equals("spec")) {
			value = this.spec;
		} else if (key.equals("minus_qty")) {
			value = this.minus_qty != null ? this.minus_qty : "";
		} else if (key.equals("minus_flag")) {
			if (this.minus_flag != null && this.minus_flag.equalsIgnoreCase("false")) {
				value = "<img class=\"pos3\" src=\"/Windchill/jsp/images/remove16x16.gif\">";
			} else {
//				value = "<img class=\"pos3\" src=\"/Windchill/jsp/images/remove16x16.gif\">";
				value = "";
			}
		}
		return value;
	}
}
