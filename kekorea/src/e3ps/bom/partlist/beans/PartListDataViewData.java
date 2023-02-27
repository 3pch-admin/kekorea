package e3ps.bom.partlist.beans;

import java.sql.Timestamp;

import e3ps.bom.partlist.PartListData;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartListDataViewData {

	private String oid;
	private String lotNo;
	private String unitName;
	private String partNo;
	private String partName;
	private String standard;
	private String maker;
	private String customer;
	private int quantity;
	private String unit;
	private double price;
	private String currency;
	private double won;
	private Timestamp partListDate;
	private double exchangeRate;
	private String referDrawing;
	private String classification;
	private String note;

	public PartListDataViewData(PartListData data) throws Exception {
		setOid(data.getPersistInfo().getObjectIdentifier().getStringValue());
		setLotNo(data.getLotNo());
		setUnitName(data.getUnitName());
		setPartNo(data.getPartNo());
		setPartName(data.getPartName());
		setStandard(data.getStandard());
		setMaker(data.getMaker());
		setCustomer(data.getCustomer());
		setQuantity(data.getQuantity());
		setUnit(data.getUnit());
		setPrice(data.getPrice());
		setCurrency(data.getCurrency());
		setWon(data.getWon());
		setPartListDate(data.getPartListDate());
		setExchangeRate(data.getExchangeRate());
		setReferDrawing(data.getReferDrawing());
		setClassification(data.getClassification());
		setNote(data.getNote());
	}
}
