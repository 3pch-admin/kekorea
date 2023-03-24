package e3ps.part.kePart.beans;

import java.sql.Timestamp;

import e3ps.common.util.AUIGridUtils;
import e3ps.common.util.CommonUtils;
import e3ps.part.kePart.KePart;
import e3ps.part.kePart.KePartMaster;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KePartDTO {

	private String oid;
	private String moid;
	private String keNumber;
	private String name;
	private int lotNo;
	private String code;
	private String model;
	private String state;
	private int version;
	private String creator;
	private String creatorId;
	private Timestamp createdDate;
	private String createdDate_txt;
	private String modifier;
	private String modifierId;
	private Timestamp modifiedDate;
	private String modifiedDate_txt;
	private boolean latest;
	private String primary;
	private String note;

	// 변수 담기용
	private String primaryPath;
	private int next;

	public KePartDTO() {

	}

	public KePartDTO(KePart kePart) throws Exception {
		KePartMaster master = kePart.getMaster();
		setOid(kePart.getPersistInfo().getObjectIdentifier().getStringValue());
		setMoid(master.getPersistInfo().getObjectIdentifier().getStringValue());
		setKeNumber(master.getKeNumber());
		setName(master.getName());
		setLotNo(master.getLotNo());
		setCode(master.getCode());
		setModel(master.getModel());
		setState(kePart.getState());
		setVersion(kePart.getVersion());
		setCreator(master.getOwnership().getOwner().getFullName());
		setCreatorId(master.getOwnership().getOwner().getName());
		setCreatedDate(master.getCreateTimestamp());
		setCreatedDate_txt(CommonUtils.getPersistableTime(master.getCreateTimestamp()));
		setModifier(kePart.getOwnership().getOwner().getFullName());
		setModifierId(kePart.getOwnership().getOwner().getName());
		setModifiedDate(kePart.getCreateTimestamp());
		setModifiedDate_txt(CommonUtils.getPersistableTime(kePart.getCreateTimestamp()));
		setLatest(kePart.getLatest());
		setPrimary(AUIGridUtils.primaryTemplate(kePart));
		setNote(kePart.getNote());
	}
}
