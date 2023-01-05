package e3ps.load.service;

import wt.services.ServiceFactory;

public class LoadHelper {

//	public static final String ROOT = "/Windchill/jsp/문서";

	/**
	 * access service
	 */
	public static final LoadService service = ServiceFactory.getService(LoadService.class);

	/**
	 * access helper
	 */
	public static final LoadHelper manager = new LoadHelper();
}
