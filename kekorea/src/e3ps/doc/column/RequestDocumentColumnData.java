package e3ps.doc.column;

import java.util.ArrayList;

import e3ps.approval.service.ApprovalHelper;
import e3ps.common.util.ContentUtils;
import e3ps.doc.RequestDocument;
import e3ps.project.Project;
import e3ps.workspace.ApprovalLine;
import e3ps.workspace.ApprovalMaster;
import wt.session.SessionHelper;

public class RequestDocumentColumnData {

	public String oid;
	public String pjtType;//
	public String name;
	public String customer;//
	public String ins_location;//
	public String mak;
	public String kekNumber;
	public String keNumber;
	public String user_id;//
	public String pdescription;
	public String ingPoint;
	public String version;
	public String state;
	public String model;//
	public String system_info;//
	public String pDate;//
	public String creator;
	public String createDate;//
	public String modifier;
	public String modifyDate;

	public String iconPath;

	public RequestDocumentColumnData(RequestDocument requestDocument, Project project) throws Exception {
		this.oid = requestDocument.getPersistInfo().getObjectIdentifier().getStringValue();
		this.pjtType = project.getPType();
		this.name = requestDocument.getName();
		this.customer = project.getCustomer();
		this.ins_location = project.getIns_location();
		this.mak = project.getMak();
		this.kekNumber = project.getKekNumber();
		this.keNumber = project.getKeNumber();
		this.user_id = project.getUserId();// IBAUtils.getStringValue(project, "USER_ID");
		this.pdescription = project.getDescription();
		ApprovalMaster master = ApprovalHelper.manager.getMaster(requestDocument);
		this.ingPoint = getIngPoint(master);
		this.state = requestDocument.getLifeCycleState().getDisplay(SessionHelper.getLocale());
		this.version = requestDocument.getVersionIdentifier().getSeries().getValue() + "."
				+ requestDocument.getIterationIdentifier().getSeries().getValue();
		this.state = requestDocument.getLifeCycleState().getDisplay(SessionHelper.getLocale());
		this.model = project.getModel();
		this.system_info = project.getSystemInfo();
		this.pDate = project.getPDate() != null ? project.getPDate().toString().substring(0, 16) : "";
		this.creator = requestDocument.getCreatorFullName();
		this.createDate = requestDocument.getCreateTimestamp().toString().substring(0, 16);
		this.modifier = requestDocument.getModifierFullName();
		this.modifyDate = requestDocument.getModifyTimestamp().toString().substring(0, 16);

		this.iconPath = ContentUtils.getStandardIcon(this.oid);
	}
	
	private String getIngPoint(ApprovalMaster master) {
		String s = "";

		ArrayList<ApprovalLine> agreeLine = ApprovalHelper.manager.getAgreeLines(master);
		for (ApprovalLine aLine : agreeLine) {
			boolean bool = false;
			if (aLine.getState().equals(ApprovalHelper.LINE_AGREE_STAND)) {
				bool = true;
			}
			s += aLine.getOwnership().getOwner().getFullName() + "&" + bool + ",";
		}
		return s;
	}
}
