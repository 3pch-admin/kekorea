package e3ps.workspace.dto;

import java.sql.Timestamp;
import java.util.ArrayList;

import e3ps.workspace.ApprovalLine;
import e3ps.workspace.ApprovalMaster;
import e3ps.workspace.service.WorkspaceHelper;
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
	private String description;
	private Timestamp completeTime;
	private Timestamp createdDate;
	private Timestamp receiveTime;
	private String poid;
	private String moid;
	private String point; // 표시..

	private boolean isAgreeLine = false;
	private boolean isApprovalLine = false;
	private boolean isReceiveLine = false;

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
			setSubmiter(master.getOwnership().getOwner().getFullName());
			setCreatedDate(line.getCreateTimestamp());
			setReceiveTime(line.getStartTime()); // 수신일 = 받은 시간
			setMoid(master.getPersistInfo().getObjectIdentifier().getStringValue());
			setPoid(master.getPersist() != null
					? master.getPersist().getPersistInfo().getObjectIdentifier().getStringValue()
					: "");
			setAgreeLine(line.getType().equals(WorkspaceHelper.AGREE_LINE));
			setApprovalLine(line.getType().equals(WorkspaceHelper.APPROVAL_LINE));
			setReceiveLine(line.getType().equals(WorkspaceHelper.RECEIVE_LINE));
			point(master);
		} else if ("COLUMN_AGREE".equals(columnType)) {
			// 검토함
			setOid(line.getPersistInfo().getObjectIdentifier().getStringValue());
			setReads(line.getReads());
			setType(line.getType());
			setRole(line.getRole());
			setName(line.getName());
			setCreator(line.getMaster().getOwnership().getOwner().getFullName());
			setSubmiter(master.getOwnership().getOwner().getFullName());
			setDescription(line.getDescription() != null ? line.getDescription() : "");
			setReceiveTime(line.getCreateTimestamp());
			setCompleteTime(line.getCompleteTime() != null ? line.getCompleteTime() : null);
			setState(line.getState());
			setMoid(master.getPersistInfo().getObjectIdentifier().getStringValue());
			setPoid(master.getPersist() != null
					? master.getPersist().getPersistInfo().getObjectIdentifier().getStringValue()
					: "");
			setAgreeLine(line.getType().equals(WorkspaceHelper.AGREE_LINE));
			setApprovalLine(line.getType().equals(WorkspaceHelper.APPROVAL_LINE));
			setReceiveLine(line.getType().equals(WorkspaceHelper.RECEIVE_LINE));
			point(master);
		} else if ("COLUMN_RECEIVE".equals(columnType)) {
			// 수신함
			setOid(line.getPersistInfo().getObjectIdentifier().getStringValue());
			setReads(line.getReads());
			setType(line.getType());
			setName(line.getName());
			setCreator(line.getMaster().getOwnership().getOwner().getFullName());
			setRole(line.getRole());
			setSubmiter(master.getOwnership().getOwner().getFullName());
			setState(line.getState());
			setReceiveTime(line.getCreateTimestamp());
			setMoid(master.getPersistInfo().getObjectIdentifier().getStringValue());
			setPoid(master.getPersist() != null
					? master.getPersist().getPersistInfo().getObjectIdentifier().getStringValue()
					: "");
			setAgreeLine(line.getType().equals(WorkspaceHelper.AGREE_LINE));
			setApprovalLine(line.getType().equals(WorkspaceHelper.APPROVAL_LINE));
			setReceiveLine(line.getType().equals(WorkspaceHelper.RECEIVE_LINE));
			point(master);
		}
	}

	public ApprovalLineDTO(ApprovalMaster master, String columnType) throws Exception {
		// 완료함
		if ("COLUMN_COMPLETE".equals(columnType)) {
			setOid(master.getPersistInfo().getObjectIdentifier().getStringValue());
			setType(master.getType());
			setName(master.getName());
			setSubmiter(master.getOwnership().getOwner().getFullName());
			setState(master.getState());
			setReceiveTime(master.getStartTime());
			setCompleteTime(master.getCompleteTime()); // 반드시 완료날짜 잇음
			setMoid(master.getPersistInfo().getObjectIdentifier().getStringValue());
			setPoid(master.getPersist() != null
					? master.getPersist().getPersistInfo().getObjectIdentifier().getStringValue()
					: "");
			point(master);
		} else if ("COLUMN_PROGRESS".equals(columnType)) {
			// 진행함
			setOid(master.getPersistInfo().getObjectIdentifier().getStringValue());
			setName(master.getName());
			setCreatedDate(master.getCreateTimestamp());
			setSubmiter(master.getOwnership().getOwner().getFullName());
			setState(master.getState());
			setReceiveTime(master.getStartTime());
			setMoid(master.getPersistInfo().getObjectIdentifier().getStringValue());
			setPoid(master.getPersist() != null
					? master.getPersist().getPersistInfo().getObjectIdentifier().getStringValue()
					: "");
			point(master);
		} else if ("COLUMN_REJECT".equals(columnType)) {
			// 반려함
			setOid(master.getPersistInfo().getObjectIdentifier().getStringValue());
			setName(master.getName());
			setCreatedDate(master.getCreateTimestamp());
			setSubmiter(master.getOwnership().getOwner().getFullName());
			setState(master.getState());
			setReceiveTime(master.getStartTime());
			setCompleteTime(master.getCompleteTime());
			setMoid(master.getPersistInfo().getObjectIdentifier().getStringValue());
			setPoid(master.getPersist() != null
					? master.getPersist().getPersistInfo().getObjectIdentifier().getStringValue()
					: "");
			reject(master);
		}
	}

	private void reject(ApprovalMaster master) throws Exception {
		ApprovalLine submitLine = WorkspaceHelper.manager.getSubmitLine(master);
		String point = "<img src='/Windchill/extcore/images/process-nleft.gif' class='line'><span class='inactive'><span class='text'>"
				+ submitLine.getOwnership().getOwner().getFullName() + "</span></span>"
				+ "<img src='/Windchill/extcore/images/process-nright.gif' class='line'>";

		point += "<img src='/Windchill/extcore/images/process-line.gif' class='line dot'>";

		ArrayList<ApprovalLine> agreeLines = WorkspaceHelper.manager.getAgreeLines(master);
		for (int i = 0; i < agreeLines.size(); i++) {
			ApprovalLine agreeLine = (ApprovalLine) agreeLines.get(i);
			if (agreeLine.getState().equals(WorkspaceHelper.STATE_AGREE_REJECT)) {
				point += "<img src='/Windchill/extcore/images/process-sleft.gif' class='line'><span class='active'><span class='text'>"
						+ agreeLine.getOwnership().getOwner().getFullName() + "</span></span>"
						+ "<img src='/Windchill/extcore/images/process-sright.gif' class='line'>";
			} else {
				point += "<img src='/Windchill/extcore/images/process-nleft.gif' class='line'><span class='inactive'><span class='text'>"
						+ agreeLine.getOwnership().getOwner().getFullName() + "</span></span>"
						+ "<img src='/Windchill/extcore/images/process-nright.gif' class='line'>";
			}

			if (i != agreeLines.size() - 1) {
				point += "<img src='/Windchill/extcore/images/process-line.gif' class='line dot'>";
			}
		}

		if (agreeLines.size() > 0) {
			point += "<img src='/Windchill/extcore/images/process-line.gif' class='line dot'>";
		}

		ArrayList<ApprovalLine> approvalLines = WorkspaceHelper.manager.getApprovalLines(master);
		for (int i = 0; i < approvalLines.size(); i++) {
			ApprovalLine approvalLine = (ApprovalLine) approvalLines.get(i);
			if (approvalLine.getState().equals(WorkspaceHelper.STATE_APPROVAL_REJECT)) {
				point += "<img src='/Windchill/extcore/images/process-sleft.gif' class='line'><span class='active'><span class='text'>"
						+ approvalLine.getOwnership().getOwner().getFullName() + "</span></span>"
						+ "<img src='/Windchill/extcore/images/process-sright.gif' class='line'>";
			} else {
				point += "<img src='/Windchill/extcore/images/process-nleft.gif' class='line'><span class='inactive'><span class='text'>"
						+ approvalLine.getOwnership().getOwner().getFullName() + "</span></span>"
						+ "<img src='/Windchill/extcore/images/process-nright.gif' class='line'>";
			}

			if (i != approvalLines.size() - 1) {
				point += "<img src='/Windchill/extcore/images/process-line.gif' class='line dot'>";
			}
		}
		setPoint(point);
	}

	private void point(ApprovalMaster master) throws Exception {
		ApprovalLine submitLine = WorkspaceHelper.manager.getSubmitLine(master);
		String point = "<img src='/Windchill/extcore/images/process-nleft.gif' class='line'><span class='inactive'><span class='text'>"
				+ submitLine.getOwnership().getOwner().getFullName() + "</span></span>"
				+ "<img src='/Windchill/extcore/images/process-nright.gif' class='line'>";

		point += "<img src='/Windchill/extcore/images/process-line.gif' class='line dot'>";

		ArrayList<ApprovalLine> agreeLines = WorkspaceHelper.manager.getAgreeLines(master);
		for (int i = 0; i < agreeLines.size(); i++) {
			ApprovalLine agreeLine = (ApprovalLine) agreeLines.get(i);
			if (agreeLine.getState().equals(WorkspaceHelper.STATE_AGREE_READY)) {
				point += "<img src='/Windchill/extcore/images/process-sleft.gif' class='line'><span class='active'><span class='text'>"
						+ agreeLine.getOwnership().getOwner().getFullName() + "</span></span>"
						+ "<img src='/Windchill/extcore/images/process-sright.gif' class='line'>";
			} else {
				point += "<img src='/Windchill/extcore/images/process-nleft.gif' class='line'><span class='inactive'><span class='text'>"
						+ agreeLine.getOwnership().getOwner().getFullName() + "</span></span>"
						+ "<img src='/Windchill/extcore/images/process-nright.gif' class='line'>";
			}

			if (i != agreeLines.size() - 1) {
				point += "<img src='/Windchill/extcore/images/process-line.gif' class='line dot'>";
			}
		}

		if (agreeLines.size() > 0) {
			point += "<img src='/Windchill/extcore/images/process-line.gif' class='line dot'>";
		}

		ArrayList<ApprovalLine> approvalLines = WorkspaceHelper.manager.getApprovalLines(master);
		for (int i = 0; i < approvalLines.size(); i++) {
			ApprovalLine approvalLine = (ApprovalLine) approvalLines.get(i);
			if (approvalLine.getState().equals(WorkspaceHelper.STATE_APPROVAL_APPROVING)) {
				point += "<img src='/Windchill/extcore/images/process-sleft.gif' class='line'><span class='active'><span class='text'>"
						+ approvalLine.getOwnership().getOwner().getFullName() + "</span></span>"
						+ "<img src='/Windchill/extcore/images/process-sright.gif' class='line'>";
			} else {
				point += "<img src='/Windchill/extcore/images/process-nleft.gif' class='line'><span class='inactive'><span class='text'>"
						+ approvalLine.getOwnership().getOwner().getFullName() + "</span></span>"
						+ "<img src='/Windchill/extcore/images/process-nright.gif' class='line'>";
			}

			if (i != approvalLines.size() - 1) {
				point += "<img src='/Windchill/extcore/images/process-line.gif' class='line dot'>";
			}
		}
		setPoint(point);
	}
}
