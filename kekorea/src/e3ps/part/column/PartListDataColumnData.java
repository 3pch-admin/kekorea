package e3ps.part.column;

import e3ps.common.util.StringUtils;
import e3ps.partlist.PartListData;
import e3ps.project.Project;

public class PartListDataColumnData {

	public String oid;
	public String kekNumber;
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
	public String won;
	public String partListDate;
	public String exchangeRate;
	public String referDrawing;
	public String classification;
	public String note;

	public PartListDataColumnData(PartListData data, Project project) throws Exception {
		this.oid = data.getPersistInfo().getObjectIdentifier().getStringValue();
		this.kekNumber = project != null ? project.getKekNumber() : "";
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
		this.won = data.getWon() != null ? StringUtils.numberFormat(data.getWon(), "#,##") : 0 + "";
		this.partListDate = StringUtils.replaceToValue(data.getPartListDate());
		this.exchangeRate = StringUtils.replaceToValue(data.getExchangeRate());
		this.referDrawing = StringUtils.replaceToValue(data.getReferDrawing());
		this.classification = StringUtils.replaceToValue(data.getClassification());
		this.note = StringUtils.replaceToValue(data.getNote());
	}
}
