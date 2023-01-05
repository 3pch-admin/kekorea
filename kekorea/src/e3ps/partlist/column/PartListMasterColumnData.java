package e3ps.partlist.column;

import e3ps.common.util.ContentUtils;
import e3ps.common.util.StringUtils;
import e3ps.partlist.PartListMaster;
import e3ps.project.Project;
import wt.session.SessionHelper;

public class PartListMasterColumnData {

	public String oid;
	public String pjtType;
	public String name;
	public String mak;
	public String kekNumber;
	public String keNumber;
	public String user_id;
	public String kek_description;
	public String customer;
	public String ins_location;
	public String pDate;
	public String model;
	// public String description;
	public String creator;
	public String createDate;
	public String modifier;
	public String modifyDate;
//	public String version;
	public String state;
	// public String classify;

	public String info;
	public String iconPath;

	public PartListMasterColumnData(PartListMaster partListMaster, Project project) throws Exception {
		this.oid = partListMaster.getPersistInfo().getObjectIdentifier().getStringValue();
		this.name = partListMaster.getName();
		this.pjtType = project.getPType();
		this.mak = project.getMak();
		this.kekNumber = project.getKekNumber();
		this.keNumber = project.getKeNumber();
		this.user_id = project.getUserId();
		this.kek_description = project.getDescription();
		this.customer = project.getCustomer();
		this.ins_location = project.getIns_location();
		this.pDate = project.getPDate() != null ? project.getPDate().toString().substring(0, 10) : "";
		this.model = project.getModel();
		this.state = partListMaster.getLifeCycleState().getDisplay(SessionHelper.manager.getLocale());
		this.creator = partListMaster.getCreatorFullName();
		this.modifier = StringUtils.replaceToValue(partListMaster.getOwnership().getOwner().getFullName());
		this.modifyDate = partListMaster.getModifyTimestamp().toString().substring(0, 16);
		this.createDate = partListMaster.getCreateTimestamp().toString().substring(0, 16);

		this.info = "/Windchill/jsp/images/details.gif";
		this.iconPath = ContentUtils.getOpenIcon(this.oid);
	}
}
