package e3ps.admin.service;

import wt.services.ServiceFactory;

public class AdminHelper {

	public static final AdminHelper manager = new AdminHelper();
	public static final AdminService service = ServiceFactory.getService(AdminService.class);
}
