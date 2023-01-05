package e3ps.migrator;

import wt.services.ServiceFactory;

public class MigratorHelper {

	/**
	 * access service
	 */
	public static final MigratorService service = ServiceFactory.getService(MigratorService.class);

	/**
	 * access helper
	 */
	public static final MigratorHelper manager = new MigratorHelper();

	public static final String LIFECYCLE_NAME = "기초";

}
