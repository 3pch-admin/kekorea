package e3ps.common.content.service;

import java.io.File;

import wt.services.ServiceFactory;
import wt.util.WTProperties;

public class ContentHelper {

	public static String temp;

	static {
		try {
			temp = WTProperties.getLocalProperties().getProperty("wt.home") + File.separator + "temp" + File.separator
					+ "upload";
			File dir = new File(temp);
			if (!dir.exists()) {
				dir.mkdirs();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static final String ROOT = "/Default/문서";

	/**
	 * access service
	 */
	public static final ContentService service = ServiceFactory.getService(ContentService.class);

	/**
	 * access helper
	 */
	public static final ContentHelper manager = new ContentHelper();

}
