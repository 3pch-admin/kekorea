package e3ps.admin.spec.service;

import wt.services.ServiceFactory;

public class OptionsHelper {

	public static final OptionsHelper manager = new OptionsHelper();
	public static final OptionsService service = ServiceFactory.getService(OptionsService.class);
}
