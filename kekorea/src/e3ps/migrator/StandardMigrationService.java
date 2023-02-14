package e3ps.migrator;

import java.util.HashMap;

import e3ps.admin.commonCode.CommonCode;
import e3ps.project.Project;
import wt.fc.PersistenceHelper;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.util.WTException;

public class StandardMigrationService extends StandardManager implements MigrationService {

	public static StandardMigrationService newStandardMigrationService() throws WTException {
		StandardMigrationService instance = new StandardMigrationService();
		instance.initialize();
		return instance;
	}

	@Override
	public void projectToMak(HashMap<String, Object> map) throws Exception {
		SessionContext prev = SessionContext.newContext();
		Project project = (Project) map.get("project");
		CommonCode makCode = (CommonCode) map.get("makCode");
		CommonCode detailCode = (CommonCode) map.get("detailCode");
		Transaction trs = new Transaction();
		try {
			trs.start();

			SessionHelper.manager.setAdministrator();

			project.setMaks(makCode);
			project.setDetail(detailCode);
			PersistenceHelper.manager.modify(project);

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
			SessionContext.setContext(prev);
		}
	}
}
