package e3ps.workspace.dto;

import java.util.ArrayList;

import com.amazonaws.services.dynamodbv2.model.QueryResult;

import e3ps.workspace.ApprovalLine;
import e3ps.workspace.ApprovalMaster;
import e3ps.workspace.service.WorkspaceHelper;
import wt.org.WTUser;

public class ApprovalMasterViewData {

	public ApprovalMaster approvalMaster;
	public String oid;
	public String name;
	public String state;
	public String startTime;
	public String completeTime;
	public String iconPath;
	public String creator;
	public String createDate;

	public WTUser ingUser;

	public QueryResult result;

	public ApprovalLineViewData ingData;
	public ApprovalLineViewData returnData;

	public ArrayList<ApprovalLine> appLines = new ArrayList<ApprovalLine>();
	public ArrayList<ApprovalLine> agreeLines = new ArrayList<ApprovalLine>();
	public ArrayList<ApprovalLine> receiveLines = new ArrayList<ApprovalLine>();

	public int size = 0;

	public ApprovalMasterViewData(ApprovalMaster approvalMaster) throws Exception {
		this.approvalMaster = approvalMaster;
		this.oid = approvalMaster.getPersistInfo().getObjectIdentifier().getStringValue();
		this.name = approvalMaster.getName();
		this.startTime = approvalMaster.getStartTime() != null
				? approvalMaster.getStartTime().toString().substring(0, 16)
				: "";
		this.completeTime = approvalMaster.getCompleteTime() != null
				? approvalMaster.getCompleteTime().toString().substring(0, 16)
				: "";
		this.iconPath = "/Windchill/jsp/images/approved.gif";
		this.creator = approvalMaster.getOwnership().getOwner().getFullName();
		this.createDate = approvalMaster.getCreateTimestamp().toString().substring(0, 16);
		this.state = approvalMaster.getState();

		this.appLines = WorkspaceHelper.manager.getAppLines(this.approvalMaster);
		this.agreeLines = WorkspaceHelper.manager.getAgreeLines(this.approvalMaster);
		this.receiveLines = WorkspaceHelper.manager.getReceiveLines(this.approvalMaster);

		this.size = appLines.size() + agreeLines.size() + receiveLines.size();

		// setIngUser();
		this.ingData = getIngLineData();
		this.returnData = getReturnData();
	}

	public String getValue(String key) throws Exception {
		String value = "";
		if (key.equals("name")) {
			value = this.name;
		} else if (key.equals("state")) {
			value = this.state;
		} else if (key.equals("receiveTime")) {
			value = this.createDate;
		} else if (key.equals("completeTime")) {
			value = this.completeTime;
		} else if (key.equals("createDate")) {
			value = this.createDate;
		} else if (key.equals("ingPoint")) {
			value = getIngHTML();
		} else if (key.equals("returnPoint")) {
			value = getReturnHTML();
		}
		return value;
	}

	private String getReturnHTML() throws Exception {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < appLines.size(); i++) {
			ApprovalLine aLine = (ApprovalLine) appLines.get(i);
			ApprovalLineViewData data = new ApprovalLineViewData(aLine);
			boolean returnPoint = data.returnPoint;

			sb.append("<span ");
			if (returnPoint) {
				sb.append("class=\"checkPoint\"></span>");
			} else {
				sb.append("class=\"sign-left\"></span>");
			}
			sb.append("<span class=\"sign bg-sign\"><span class=\"in_span\">");
			sb.append(data.creator);
			sb.append("</span></span>");
			sb.append("<span class=\"sign-right\"></span>");

			if (i != (appLines.size() - 1)) {
				sb.append("<i class=\"axi axi-arrow-forward forward\"></i>");
			}
		}
		return sb.toString();
	}

	private ApprovalLineViewData getIngLineData() throws Exception {
		ApprovalLineViewData dd = null;

		for (int i = 0; i < appLines.size(); i++) {
			ApprovalLine aLine = (ApprovalLine) appLines.get(i);
			ApprovalLineViewData data = new ApprovalLineViewData(aLine);
			boolean ingPoint = data.ingPoint;

			if (ingPoint) {
				dd = data;
			}
		}

		if (dd == null) {

			for (int i = 0; i < agreeLines.size(); i++) {
				ApprovalLine aLine = (ApprovalLine) agreeLines.get(i);
				ApprovalLineViewData data = new ApprovalLineViewData(aLine);
				boolean ingPoint = data.ingPoint;

				if (ingPoint) {
					dd = data;
				}
			}
		}
		return dd;
	}

	private ApprovalLineViewData getReturnData() throws Exception {
		ApprovalLineViewData dd = null;
		for (int i = 0; i < appLines.size(); i++) {
			ApprovalLine aLine = (ApprovalLine) appLines.get(i);
			ApprovalLineViewData data = new ApprovalLineViewData(aLine);
			boolean returnPoint = data.returnPoint;
			if (returnPoint) {
				dd = data;
			}
		}
		return dd;
	}

	private String getIngHTML() throws Exception {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < appLines.size(); i++) {
			ApprovalLine aLine = (ApprovalLine) appLines.get(i);
			ApprovalLineViewData data = new ApprovalLineViewData(aLine);
			boolean ingPoint = data.ingPoint;

			sb.append("<span ");
			if (ingPoint) {
				sb.append("class=\"checkPoint\"></span>");
			} else {
				sb.append("class=\"sign-left\"></span>");
			}
			sb.append("<span class=\"sign bg-sign\"><span class=\"in_span\">");
			sb.append(data.creator);
			sb.append("</span></span>");
			sb.append("<span class=\"sign-right\"></span>");
			if (i != (appLines.size() - 1)) {
				sb.append("<i class=\"axi axi-arrow-forward forward\"></i>");
			}
		}
		return sb.toString();
	}
}
