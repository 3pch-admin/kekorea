package e3ps.admin.dto;

import e3ps.admin.LoginHistory;
import e3ps.admin.service.AdminHelper;
import e3ps.common.util.ContentUtils;
import wt.org.OrganizationServicesMgr;

public class LoginHistoryColumnViewData {

	public String ip;
	public String id;
	public String oid;
	public LoginHistory loginHistory;
	public String creator;
	public String createDate;
	public String lastDate;
	public String iconPath;

	public LoginHistoryColumnViewData(LoginHistory loginHistory) throws Exception {
		this.ip = loginHistory.getIp();
		this.id = loginHistory.getId();
		this.creator = OrganizationServicesMgr.getUser(this.id).getFullName();
		this.createDate = loginHistory.getCreateTimestamp().toString().substring(0, 16);
		this.lastDate = AdminHelper.manager.getLastConnectTime();
		this.loginHistory = loginHistory;
		this.oid = loginHistory.getPersistInfo().getObjectIdentifier().getStringValue();
		this.iconPath = ContentUtils.getOpenIcon(this.loginHistory);
	}
}
