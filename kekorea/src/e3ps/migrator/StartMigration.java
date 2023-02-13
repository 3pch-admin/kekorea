package e3ps.migrator;

import java.util.Date;
import java.sql.Timestamp;

public class StartMigration {

	public static void main(String[] args) throws Exception {

		Timestamp start = new Timestamp(new Date().getTime());
		System.out.println("시작 = " + start);

		MigrationHelper.service.projectToMak();

		Timestamp end = new Timestamp(new Date().getTime());
		System.out.println("종료 = " + end);

		System.exit(0);

	}
}
