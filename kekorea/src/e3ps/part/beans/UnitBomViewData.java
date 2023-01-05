package e3ps.part.beans;

import e3ps.common.util.StringUtils;
import e3ps.part.UnitBom;

public class UnitBomViewData {

	public UnitBom unitBom;
	public String oid;
	public String ucode;
	public String partNo;
	public String partName;
	public String spec;
	public String unit;
	public String maker;
	public String customer;
	public String currency;
	public String price;

	public UnitBomViewData(UnitBom unitBom) throws Exception {
		this.unitBom = unitBom;
		this.oid = unitBom.getPersistInfo().getObjectIdentifier().getStringValue();
		this.ucode = StringUtils.replaceToValue(unitBom.getUCode());
		this.partNo = unitBom.getPartNo();
		this.partName = unitBom.getPartName();
		this.spec = unitBom.getSpec();
		this.unit = unitBom.getUnit();
		this.maker = unitBom.getMaker();
		this.customer = unitBom.getCustomer();
		this.currency = unitBom.getCurrency();
		this.price = unitBom.getPrice();
	}

}
