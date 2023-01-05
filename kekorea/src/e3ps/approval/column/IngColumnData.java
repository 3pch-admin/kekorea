package e3ps.approval.column;

import java.util.ArrayList;

import e3ps.approval.ApprovalLine;
import e3ps.approval.ApprovalMaster;
import e3ps.approval.service.ApprovalHelper;

public class IngColumnData {

	// 목록에서 보여 줄것만해서
	// no("NO"), name("결재제목"), ingPoint("진행단계"), objType("양식"), createDate("기안일");
	public String oid;
	public String name;
	public String ingPoint;
	public String createDate;
	// 기타
	public String iconPath;

	public IngColumnData(ApprovalMaster master) throws Exception {
		this.oid = master.getPersistInfo().getObjectIdentifier().getStringValue();
		this.name = master.getName();
		this.ingPoint = getIngPoint(master);
		this.createDate = master.getCreateTimestamp().toString().substring(0, 16);
		this.iconPath = "/Windchill/jsp/images/approved.gif";
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

	public String getValue(String key) {
		String value = "";
		if (key.equals("name")) {
			value = this.name;
		} else if (key.equals("createDate")) {
			value = this.createDate;
		}
		return value;
	}
}
