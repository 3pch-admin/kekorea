package e3ps.epm.keDrawing.dto;

import java.sql.Timestamp;

import e3ps.common.util.AUIGridUtils;
import e3ps.common.util.CommonUtils;
import e3ps.epm.keDrawing.KeDrawing;
import e3ps.epm.keDrawing.KeDrawingMaster;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeDrawingDTO {

	private String oid;
	private String moid;
	private String name;
	private String keNumber;
	private int version;
	private int lotNo;
	private String creator;
	private Timestamp createdDate;
	private String created_txt;
	private String modifier;
	private Timestamp modifiedDate;
	private String modified_txt;
	private boolean latest;
	private String primary;
	private String note;

	// 변수 담기용
	private String primaryPath;
	private int next;

	public KeDrawingDTO() {

	}

	public KeDrawingDTO(KeDrawing keDrawing) throws Exception {
		KeDrawingMaster master = keDrawing.getMaster();
		setOid(keDrawing.getPersistInfo().getObjectIdentifier().getStringValue());
		setMoid(keDrawing.getMaster().getPersistInfo().getObjectIdentifier().getStringValue());
		setName(master.getName());
		setKeNumber(master.getKeNumber());
		setVersion(keDrawing.getVersion());
		setLotNo(master.getLotNo());
		setLatest(keDrawing.getLatest());
		setCreator(master.getOwnership().getOwner().getFullName());
		setCreatedDate(master.getCreateTimestamp());
		setCreated_txt(CommonUtils.getPersistableTime(master.getCreateTimestamp()));
		setModifier(keDrawing.getOwnership().getOwner().getFullName());
		setModifiedDate(keDrawing.getModifyTimestamp());
		setModified_txt(CommonUtils.getPersistableTime(keDrawing.getModifyTimestamp()));
		setPrimary(AUIGridUtils.primaryTemplate(keDrawing));
		setNote(keDrawing.getNote());
	}
}
