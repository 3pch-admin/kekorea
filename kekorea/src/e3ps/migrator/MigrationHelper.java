package e3ps.migrator;

import wt.services.ServiceFactory;

public class MigrationHelper {

	public static final MigrationHelper manager = new MigrationHelper();
	public static final MigrationService service = ServiceFactory.getService(MigrationService.class);

}