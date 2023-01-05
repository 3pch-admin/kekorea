package e3ps.migrator;

public class StartMigration {

	public static void main(String[] args) throws Exception {

		System.out.println("시작..");
		
//		MigratorHelper.service.setCompleteTask();
		
		MigratorHelper.service.setProjectGateState();

		MigratorHelper.service.setProjectState();

		System.exit(0);
	}
}
