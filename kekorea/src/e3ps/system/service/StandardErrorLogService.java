package e3ps.system.service;

import e3ps.common.util.CommonUtils;
import e3ps.system.ErrorLog;
import wt.fc.PersistenceHelper;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardErrorLogService extends StandardManager implements ErrorLogService {

	public static StandardErrorLogService newStandardErrorLogService() throws WTException {
		StandardErrorLogService instance = new StandardErrorLogService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(String errorMsg, String callUrl, String errorType) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			ErrorLog log = ErrorLog.newErrorLog();
			log.setLogType(errorType);
			log.setCallUrl(callUrl);
			log.setErrorMsg(errorMsg);
			log.setOwnership(CommonUtils.sessionOwner());
			PersistenceHelper.manager.save(log);

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
