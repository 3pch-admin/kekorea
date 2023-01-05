package e3ps.partlist.beans;

import e3ps.common.util.StringUtils;
import e3ps.partlist.PartListData;

public class PartListDataViewData {

	public PartListData data;
	public String oid;
	public String lotNo;
	public String unitName;
	public String partNo;
	public String partName;
	public String standard;
	public String maker;
	public String customer;
	public String quantity;
	public String unit;
	public String price;
	public String currency;
	public double won;
	public String partListDate;
	public String exchangeRate;
	public String referDrawing;
	public String classification;
	public String note;

	public PartListDataViewData(PartListData data) throws Exception {
		this.data = data;
		this.oid = data.getPersistInfo().getObjectIdentifier().getStringValue();
		this.lotNo = StringUtils.replaceToValue(data.getLotNo());
		this.unitName = StringUtils.replaceToValue(data.getUnitName());
		this.partNo = StringUtils.replaceToValue(data.getPartNo());
		this.partName = StringUtils.replaceToValue(data.getPartName());
		this.standard = StringUtils.replaceToValue(data.getStandard());
		this.maker = StringUtils.replaceToValue(data.getMaker());
		this.customer = StringUtils.replaceToValue(data.getCustomer());
		this.quantity = StringUtils.replaceToValue(data.getQuantity());
		this.unit = StringUtils.replaceToValue(data.getUnit());
		this.price = StringUtils.replaceToValue(data.getPrice());
		this.currency = StringUtils.replaceToValue(data.getCurrency());
		this.won = data.getWon() != null ? data.getWon() : 0D;
		this.partListDate = StringUtils.replaceToValue(data.getPartListDate());
		this.exchangeRate = StringUtils.replaceToValue(data.getExchangeRate());
		this.referDrawing = StringUtils.replaceToValue(data.getReferDrawing());
		this.classification = StringUtils.replaceToValue(data.getClassification());
		this.note = StringUtils.replaceToValue(data.getNote());

	}
}
