package e3ps.workspace.beans;

import java.sql.Timestamp;

import e3ps.workspace.ApprovalLine;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApprovalLineColumnData {

	private String oid;
	private boolean reads;
	private String type;
	private String role;
	private String name;
	private String creator;
	private String state;
	private Timestamp createdDate;
	private String pointer;

	public ApprovalLineColumnData() {

	}

	public ApprovalLineColumnData(ApprovalLine line, String columnType) throws Exception {

		// 결재함
		if ("COLUMN_APPROVAL".equals(columnType)) {
			setOid(line.getPersistInfo().getObjectIdentifier().getStringValue());
			setReads(line.getReads());
			setType(line.getType());
			setRole(line.getRole());
			setName(line.getName());
			setCreator(line.getMaster().getOwnership().getOwner().getFullName());
			setState(line.getState());
			setCreatedDate(line.getCreateTimestamp());
		}
	}
}
