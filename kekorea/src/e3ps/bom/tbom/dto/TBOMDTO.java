package e3ps.bom.tbom.dto;

import e3ps.bom.tbom.TBOMData;
import e3ps.part.kePart.KePart;
import e3ps.part.kePart.KePartMaster;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TBOMDTO {

	private String oid;
	private int lotNo;
	private String code;
	private String kePartNumber;
	private String kePartName;
	private String model;
	private int qty;
	private String unit;
	private String provide;
	private String discontinue;

	public TBOMDTO() {

	}

	public TBOMDTO(TBOMData data) throws Exception {
		KePartMaster master = data.getKePart().getMaster();
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
