package e3ps.approval.column;

import java.util.ArrayList;

import e3ps.approval.ApprovalLine;
import e3ps.approval.ApprovalMaster;
import e3ps.approval.service.ApprovalHelper;

public class ReturnColumnData {

	// 목록에서 보여 줄것만해서
	// no("NO"), name("결재제목"), returnPoint("반려단계"), objType("양식"),
	// createDate("기안일"), completeTime("반려일");
	public String oid;
	public String name;
	public String returnPoint;
	public String createDate;
	public String completeTime;
	// 기타
	public String iconPath;

	public ReturnColumnData(ApprovalMaster master) throws Exception {
		this.oid = master.getPersistInfo().getObjectIdentifier().getStringValue();
		this.name = master.getName();
		this.returnPoint = getReturnPoint(master);
		this.completeTime = master.getCompleteTime().toString().substring(0, 16);
		this.createDate = master.getCreateTimestamp().toString().substring(0, 16);
		this.iconPath = "/Windchill/jsp/images/approved.gif";
	}

	private String getReturnPoint(ApprovalMaster master) {
		String s = "";
		ArrayList<ApprovalLine> appLine = ApprovalHelper.manager.getAppLines(master);
		for (ApprovalLine aLine : appLine) {
			boolean bool = false;
			if (aLine.getState().equals(ApprovalHelper.LINE_RETURN_COMPLETE)) {
				bool = true;
			}
			s += aLine.getOwnership().getOwner().getFullName() + "&" + bool + ",";
		}
		return s;
	}

	public String getValue(String key) {
		String value = "";
		if (key.equals("completeTime")) {
			value = this.completeTime;
		} else if (key.equals("createDate")) {
			value = this.createDate;
		}
		return value;
	}
}
