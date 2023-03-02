package e3ps.epm.beans;

import java.sql.Timestamp;

import e3ps.common.util.AUIGridUtils;
import e3ps.epm.keDrawing.KeDrawing;
import e3ps.epm.keDrawing.KeDrawingMaster;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeDrawingColumnData {

	private String oid;
	private String name;
	private String keNumber;
	private int version;
	private int lotNo;
	private String creator;
	private Timestamp createdDate;
	private String modifier;
	private Timestamp modifiedDate;
	private boolean latest;
	private String primary;

	public KeDrawingColumnData() {

	}

	public KeDrawingColumnData(KeDrawing keDrawing) throws Exception {
		KeDrawingMaster master = keDrawing.getMaster();
		setOid(keDrawing.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(master.getName());
		setKeNumber(master.getKeNumber());
		setVersion(keDrawing.getVersion());
		setLotNo(master.getLotNo());
		setLatest(keDrawing.getLatest());
		setCreator(master.getOwnership().getOwner().getFullName());
		setCreatedDate(master.getCreateTimestamp());
		setModifier(keDrawing.getOwnership().getOwner().getFullName());
		setModifiedDate(keDrawing.getModifyTimestamp());
		setPrimary(AUIGridUtils.primaryTemplate(keDrawing));
	}
}
