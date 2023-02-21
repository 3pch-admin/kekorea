package e3ps.bom.tbom.beans;

import e3ps.bom.tbom.TBOMData;
import e3ps.part.kePart.KePart;
import e3ps.part.kePart.KePartMaster;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TBOMColumnData {

	private String oid;
	private String lotNo;
	private String code;
	private String kePartNumber;
	private String kePartName;
	private String model;
	private int qty;
	private String unit;
	private String provide;
	private String discontinue;

	public TBOMColumnData() {

	}

	public TBOMColumnData(TBOMData data) throws Exception {
		KePartMaster master = data.getKePart().getKePartMaster();
		setOid(data.getPersistInfo().getObjectIdentifier().getStringValue());
		setLotNo(master.getLotNo());
		setCode(master.getCode());
		setKePartNumber(master.getKePartNumber());
		setKePartName(master.getKePartName());
		setModel(master.getModel());
		setQty(data.getQty());
		setUnit(data.getUnit());
		setProvide(data.getProvide());
		setDiscontinue(data.getDiscontinue());
	}
}
