package e3ps.admin.column;

import e3ps.admin.LoginHistory;
import e3ps.admin.service.AdminHelper;
import e3ps.common.util.ContentUtils;
import wt.org.OrganizationServicesMgr;

public class LoginHistoryColumnData {

	public String oid;
	public String ip;
	public String id;
	public String creator;
	public String lastDate;
	public String createDate;
	public String iconPath;

	public LoginHistoryColumnData(LoginHistory loginHistory) throws Exception {
		this.oid = loginHistory.getPersistInfo().getObjectIdentifier().getStringValue();
		this.ip = loginHistory.getIp();
		this.id = loginHistory.getId();
		this.creator = OrganizationServicesMgr.getUser(this.id).getFullName();
		this.createDate = loginHistory.getCreateTimestamp().toString().substring(0, 16);
		this.lastDate = AdminHelper.manager.getLastConnectTime();
		this.iconPath = ContentUtils.getStandardIcon(loginHistory);
	}
}