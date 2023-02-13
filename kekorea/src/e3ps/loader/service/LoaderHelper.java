package e3ps.loader.service;

import wt.services.ServiceFactory;

public class LoaderHelper {

	public static final LoaderHelper manager = new LoaderHelper();
	public static final LoaderService service = ServiceFactory.getService(LoaderService.class);
}
