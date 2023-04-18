package e3ps.epm.keDrawing.dto;

import java.sql.Timestamp;

import e3ps.common.util.AUIGridUtils;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.ContentUtils;
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
	private String creatorId;
	private Timestamp createdDate;
	private String createdDate_txt;
	private String modifier;
	private Timestamp modifiedDate;
	private String modifiedDate_txt;
	private boolean latest;
	private String primary;
	private String note;
	private String preView;
	private String state;
	private boolean isNew;

	// 변수 담기용
	private String cacheId;
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
		setCreatorId(keDrawing.getOwnership().getOwner().getName());
		setCreatedDate(master.getCreateTimestamp());
		setCreatedDate_txt(CommonUtils.getPersistableTime(master.getCreateTimestamp()));
		setModifier(keDrawing.getOwnership().getOwner().getFullName());
		setModifiedDate(keDrawing.getModifyTimestamp());
		setModifiedDate_txt(CommonUtils.getPersistableTime(keDrawing.getModifyTimestamp()));
		setPrimary(AUIGridUtils.primaryTemplate(keDrawing));
		setPreView(ContentUtils.getPreViewBase64(keDrawing));
		setNote(keDrawing.getNote());
		setState(keDrawing.getState());
		setNew(false);
	}
}
