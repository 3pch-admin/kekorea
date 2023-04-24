package e3ps.admin.service;

import wt.services.StandardManager;
import wt.util.WTException;

public class StandardAdminService extends StandardManager implements AdminService {

	public static StandardAdminService newStandardAdminService() throws WTException {
		StandardAdminService instance = new StandardAdminService();
		instance.initialize();
		return instance;
	}
}
