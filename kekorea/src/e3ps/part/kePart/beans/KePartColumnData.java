package e3ps.part.kePart.beans;

import java.sql.Timestamp;

import e3ps.common.util.AUIGridUtils;
import e3ps.part.kePart.KePart;
import e3ps.part.kePart.KePartMaster;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KePartColumnData {

	private String oid;
	private String kePartNumber;
	private String kePartName;
	private int lotNo;
	private String code;
	private String model;
	private String state;
	private int version;
	private String creator;
	private Timestamp createdDate;
	private String modifier;
	private Timestamp modifiedDate;
	private boolean latest;
	private String primary;

	public KePartColumnData(KePart kePart) throws Exception {
		KePartMaster master = kePart.getMaster();
		setOid(kePart.getPersistInfo().getObjectIdentifier().getStringValue());
		setKePartNumber(master.getKePartNumber());
		setKePartName(master.getKePartName());
		setLotNo(master.getLotNo());
		setCode(master.getCode());
		setModel(master.getModel());
		setState(kePart.getState());
		setVersion(kePart.getVersion());
		setCreator(master.getOwnership().getOwner().getFullName());
		setCreatedDate(master.getCreateTimestamp());
		setModifier(kePart.getOwnership().getOwner().getFullName());
		setModifiedDate(kePart.getCreateTimestamp());
		setLatest(kePart.getLatest());
		setPrimary(AUIGridUtils.primaryTemplate(kePart));
	}
}
