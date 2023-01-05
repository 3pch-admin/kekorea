package e3ps.partlist.beans;

import java.util.ArrayList;

import e3ps.common.util.StringUtils;
import e3ps.partlist.PartListData;
import e3ps.partlist.PartListMaster;
import e3ps.partlist.service.PartListMasterHelper;
import wt.org.WTUser;
import wt.session.SessionHelper;

public class PartListMasterViewData {

	public PartListMaster master;
	public String oid;
	public String name;
	public String number;
	public String description;
	public String engType;
	
	public boolean isCreator;

	public String jsonList;

	public ArrayList<PartListData> list = new ArrayList<PartListData>();

	public PartListMasterViewData(PartListMaster master) throws Exception {
		this.master = master;
		this.oid = master.getPersistInfo().getObjectIdentifier().getStringValue();
		this.name = master.getName();
		this.number = master.getNumber();
		this.description = StringUtils.replaceToValue(master.getDescription());
		this.engType = master.getEngType();
		this.list = PartListMasterHelper.manager.getPartListData(this.master);

		this.isCreator = isCreator(master);
		
		this.jsonList = PartListMasterHelper.manager.getJsonList(master);
	}
	
	public static boolean isCreator(PartListMaster rc) throws Exception {
		WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
		return isCreator(rc, user);
	}

	public static boolean isCreator(PartListMaster rc, WTUser user) {
		String id = rc.getCreatorName();
		if (id.equals(user.getName())) {
			return true;
		}
		return false;
	}
}
