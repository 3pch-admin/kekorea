package e3ps.workspace.dto;

import java.sql.Timestamp;

import e3ps.workspace.ApprovalLine;
import e3ps.workspace.ApprovalMaster;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApprovalLineDTO {

	private String oid;
	private boolean reads;
	private String type;
	private String role;
	private String name;
	private String creator;
	private String state;
	private String submiter;
	private Timestamp createdDate;
	private Timestamp receiveTime;
	private Timestamp completeTime;
	
	private String point; // 표시..

	public ApprovalLineDTO() {

	}

	public ApprovalLineDTO(ApprovalLine line, String columnType) throws Exception {
		ApprovalMaster master = line.getMaster();
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
		} else if ("COLUMN_AGREE".equals(columnType)) {
			// 검토함
			setOid(line.getPersistInfo().getObjectIdentifier().getStringValue());
			setReads(line.getReads());
			setType(line.getType());
			setRole(line.getRole());
			setName(line.getName());
			setSubmiter(master.getOwnership().getOwner().getFullName());
			setReceiveTime(line.getCreateTimestamp());
			setState(line.getState());
		} else if ("COLUMN_RECEIVE".equals(columnType)) {
			// 수신함
			setOid(line.getPersistInfo().getObjectIdentifier().getStringValue());
			setReads(line.getReads());
			setType(line.getType());
			setName(line.getName());
			setRole(line.getRole());
			setSubmiter(master.getOwnership().getOwner().getFullName());
			setState(line.getState());
			setReceiveTime(line.getCreateTimestamp());
		}
	}

	public ApprovalLineDTO(ApprovalMaster master, String columnType) throws Exception {
		// 완료함
		if ("COLUMN_COMPLETE".equals(columnType)) {
			setOid(master.getPersistInfo().getObjectIdentifier().getStringValue());
			setType(master.getType());
			setName(master.getName());
			setState(master.getState());
			setReceiveTime(master.getCreateTimestamp());
			setCompleteTime(master.getCompleteTime());
			setSubmiter(master.getOwnership().getOwner().getFullName());
		} else if ("COLUMN_PROGRESS".equals(columnType)) {
			// 진행함
			setOid(master.getPersistInfo().getObjectIdentifier().getStringValue());
			setName(master.getName());
			setCreatedDate(master.getCreateTimestamp());
		} else if ("COLUMN_REJECT".equals(columnType)) {
			// 반려함
			setOid(master.getPersistInfo().getObjectIdentifier().getStringValue());
			setName(master.getName());
			setCreatedDate(master.getCreateTimestamp());
			setCompleteTime(master.getCompleteTime());
		}
	}
}
