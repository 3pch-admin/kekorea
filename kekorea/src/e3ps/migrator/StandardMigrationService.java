package e3ps.migrator;

import e3ps.common.util.QuerySpecUtils;
import e3ps.project.Project;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardMigrationService extends StandardManager implements MigrationService {

	public static StandardMigrationService newStandardMigrationService() throws WTException {
		StandardMigrationService instance = new StandardMigrationService();
		instance.initialize();
		return instance;
	}

	@Override
	public void projectToMak() throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(Project.class, true);
			QuerySpecUtils.toOrderBy(query, idx, Project.class, Project.CREATE_TIMESTAMP, true);
			QueryResult result = PersistenceHelper.manager.find(query);
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				Project project = (Project) obj[0];
				String mak = project.getMak();
				String[] codes = MigrationHelper.manager.orgToCode(mak);
				String makCode = codes[0];
				String detailCode = codes[1];

			}
			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}
}
