package e3ps.approval.column;

import java.util.ArrayList;

import e3ps.approval.ApprovalLine;
import e3ps.approval.ApprovalMaster;
import e3ps.approval.service.ApprovalHelper;

public class ReceiveColumnData {

	// 목록에서 보여 줄것만해서
	public String oid;
	public String read;
	public String type;
	public String role;
	public String name;
	public String ingPoint;
	public String submiter;
	public String state;
	public String receiveTime;
	// 기타
	public String iconPath;

	public ReceiveColumnData(ApprovalLine line) throws Exception {
		this.oid = line.getPersistInfo().getObjectIdentifier().getStringValue();
		this.read = line.isReads() == true ? "확인" : "확인안함";
		this.type = line.getType();
		this.role = line.getRole();
		this.name = line.getName();
		this.ingPoint = getIngPoint(line.getMaster());
		this.submiter = line.getMaster().getOwnership().getOwner().getFullName();
		this.state = line.getState();
		this.receiveTime = line.getCreateTimestamp().toString().substring(0, 16);

		this.iconPath = "/Windchill/jsp/images/approved.gif";
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
			value = this.receiveTime;
		}
		return value;
	}
	
	private String getIngPoint(ApprovalMaster master) {
		String s = "";

		ArrayList<ApprovalLine> subMit = ApprovalHelper.manager.getAppLines(master);
		// int count = 0;
		for (ApprovalLine aLine : subMit) {
			boolean bool = false;
			if (aLine.getRole().equals(ApprovalHelper.WORKING_SUBMIT)) {
				s += aLine.getOwnership().getOwner().getFullName() + "&" + bool + ",";
			}
		}

		ArrayList<ApprovalLine> agreeLine = ApprovalHelper.manager.getAgreeLines(master);
		for (ApprovalLine aLine : agreeLine) {
			boolean bool = false;
			if (aLine.getState().equals(ApprovalHelper.LINE_AGREE_STAND)) {
				bool = true;
			}
			s += aLine.getOwnership().getOwner().getFullName() + "&" + bool + ",";
		}

		ArrayList<ApprovalLine> appLine = ApprovalHelper.manager.getAppLines(master);
		// int count = 0;
		for (ApprovalLine aLine : appLine) {
			boolean bool = false;

			if (aLine.getRole().equals(ApprovalHelper.WORKING_SUBMIT)) {
				continue;
			}

			if (aLine.getState().equals(ApprovalHelper.LINE_APPROVING)) {
				bool = true;
			}
			s += aLine.getOwnership().getOwner().getFullName() + "&" + bool + ",";
		}
		return s;
	}
}
