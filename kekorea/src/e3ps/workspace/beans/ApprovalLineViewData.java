package e3ps.workspace.beans;

import e3ps.workspace.ApprovalContract;
import e3ps.workspace.ApprovalContractPersistableLink;
import e3ps.workspace.ApprovalLine;
import e3ps.workspace.ApprovalMaster;
import e3ps.workspace.service.ApprovalHelper;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;

public class ApprovalLineViewData {

	public ApprovalLine approvalLine;
	public ApprovalMaster master;
	public String mOid;
	public String oid;
	public String name;
	public String type;
	public String role;
	public String state;
	public String description;
	public String read;
	public int sort;
	public String completeTime;
	public String startTime;

	public String iconPath;
	public String submiter;
	public String creator;
	public String createDate;

	public boolean ingPoint; // 결재 진행중 여부
	public boolean returnPoint;

	public boolean absenceBtn = false;
	public boolean appBtn = false;
	public boolean agreeBtn = false;
	public boolean receiveBtn = false;
	public boolean isLineComplete = false;
	public boolean isNextLine = false;

	public boolean completeView = false;

	public Persistable per;
	public QueryResult result;

	public ApprovalLineViewData(ApprovalLine approvalLine) throws Exception {
		this.master = approvalLine.getMaster();
		this.per = this.master.getPersist();
		this.mOid = this.master.getPersistInfo().getObjectIdentifier().getStringValue();
		this.approvalLine = approvalLine;
		this.oid = approvalLine.getPersistInfo().getObjectIdentifier().getStringValue();
		this.name = approvalLine.getName();
		this.type = approvalLine.getType();
		this.role = approvalLine.getRole();
		this.state = approvalLine.getState();
		this.description = approvalLine.getDescription() != null ? approvalLine.getDescription() : "";
		this.read = approvalLine.isReads() == true ? "확인" : "확인안함";
		this.sort = approvalLine.getSort();
		this.completeTime = approvalLine.getCompleteTime() != null
				? approvalLine.getCompleteTime().toString().substring(0, 16)
				: "";
		this.startTime = approvalLine.getStartTime() != null ? approvalLine.getStartTime().toString().substring(0, 16)
				: "";
		this.iconPath = "/Windchill/jsp/images/approved.gif";
		// 마스터 라인 .. 기안자
		this.submiter = this.master.getOwnership().getOwner().getFullName();
		this.creator = approvalLine.getOwnership().getOwner().getFullName();
		this.createDate = approvalLine.getCreateTimestamp().toString().substring(0, 16);

		this.ingPoint = ApprovalHelper.manager.isIngPoint(this.state);
		this.returnPoint = ApprovalHelper.manager.isReturnPoint(this.state);

		if (this.per instanceof ApprovalContract) {
			ApprovalContract contract = (ApprovalContract) this.per;
			this.result = PersistenceHelper.manager.navigate(contract, "persist",
					ApprovalContractPersistableLink.class);
		}

		setBtn();
		isLineComplete();
		completeView();
	}

	private void isLineComplete() {
		this.isLineComplete = this.state.equals(ApprovalHelper.LINE_APPROVAL_COMPLETE);
	}

	private void setBtn() {

		if (this.type.equals(ApprovalHelper.APP_LINE)) {
			this.appBtn = true;
		}

		if (this.type.equals(ApprovalHelper.AGREE_LINE)) {
			this.agreeBtn = true;
		}

		if (this.type.equals(ApprovalHelper.RECEIVE_LINE)
				&& (this.master.getState().equals(ApprovalHelper.MASTER_APPROVING))) {
			this.receiveBtn = true;
		}

		// 결재가 진행중일 경우.. 부재중 버튼 보이게
		if (this.state.equals(ApprovalHelper.LINE_APPROVING)) {
			this.absenceBtn = true;
		}

		if (ApprovalHelper.manager.isNextLine(this.master, this.sort)) {
			this.isNextLine = true;
		}
	}

	public String getValue(String key) {
		String value = "";
		if (key.equals("read")) {
			value = this.read;
		} else if (key.equals("type")) {
			value = this.type;
		} else if (key.equals("role")) {
			value = this.role;
		} else if (key.equals("name")) {
			value = this.name;
		} else if (key.equals("submiter")) {
			value = this.submiter;
		} else if (key.equals("state")) {
			value = this.state;
		} else if (key.equals("receiveTime")) {
			value = this.createDate;
		} else if (key.equals("completeTime")) {
			value = this.completeTime;
		}
		return value;
	}

	private boolean completeView() {
		// 추가
		if (this.state.equals(ApprovalHelper.LINE_RECEIVE_COMPLETE)) {
			this.completeView = true;
		}

		if (this.state.equals(ApprovalHelper.LINE_AGREE_COMPLETE)) {
			this.completeView = true;
		}

		if (this.state.equals(ApprovalHelper.LINE_AGREE_REJECT)) {
			this.completeView = true;
		}

		if (this.state.equals(ApprovalHelper.LINE_RETURN_COMPLETE)) {
			this.completeView = true;
		}

		if (this.state.equals(ApprovalHelper.LINE_APPROVAL_COMPLETE)) {
			this.completeView = true;
		}

		return this.completeView;
	}
}
