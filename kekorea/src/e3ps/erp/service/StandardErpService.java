package e3ps.erp.service;

import e3ps.common.util.CommonUtils;
import e3ps.erp.ErpSendHistory;
import wt.fc.PersistenceHelper;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardErpService extends StandardManager implements ErpService {

	public static StandardErpService newStandardErpService() throws WTException {
		StandardErpService instance = new StandardErpService();
		instance.initialize();
		return instance;
	}

	@Override
	public void writeLog(String name, String query, String msg, boolean isResult, String sendType) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			System.out.println("query=" + query);

			ErpSendHistory erpSendHistory = ErpSendHistory.newErpSendHistory();
			erpSendHistory.setName(name);
			erpSendHistory.setResult(isResult);
			erpSendHistory.setResultMsg(msg);
			erpSendHistory.setSendType(sendType);
			erpSendHistory.setSendQuery(query);
			erpSendHistory.setOwnership(CommonUtils.sessionOwner());

			PersistenceHelper.manager.save(erpSendHistory);

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