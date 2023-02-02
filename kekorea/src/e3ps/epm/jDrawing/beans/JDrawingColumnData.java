package e3ps.epm.jDrawing.beans;

import java.sql.Timestamp;

import e3ps.common.util.ContentUtils;
import e3ps.epm.jDrawing.JDrawing;
import e3ps.epm.jDrawing.JDrawingMaster;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JDrawingColumnData {

	private String oid;
	private String name;
	private String number;
	private int version;
	private String lot;
	private String creator;
	private Timestamp createdDate;
	private String modifier;
	private Timestamp modifiedDate;
	private boolean latest;
	private String primary;

	public JDrawingColumnData() {

	}

	public JDrawingColumnData(JDrawing jDrawing) throws Exception {
		JDrawingMaster master = jDrawing.getMaster();
		setOid(jDrawing.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(master.getName());
		setNumber(master.getNumber());
		setVersion(jDrawing.getVersion());
		setLot(jDrawing.getLot());
		setLatest(jDrawing.getLatest());
		setCreator(master.getOwnership().getOwner().getFullName());
		setCreatedDate(master.getCreateTimestamp());
		setModifier(jDrawing.getOwnership().getOwner().getFullName());
		setModifiedDate(jDrawing.getModifyTimestamp());
		setPrimary(ContentUtils.getPrimary(jDrawing)[7]);
	}
}
