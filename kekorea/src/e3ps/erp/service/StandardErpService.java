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
	public void save(String name, boolean result, String sendQuery, String sendType) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			ErpSendHistory sendHistory = ErpSendHistory.newErpSendHistory();
			sendHistory.setResult(result);
			sendHistory.setName(name);
			sendHistory.setSendType(sendType);
			sendHistory.setSendQuery(sendQuery);
			sendHistory.setOwnership(CommonUtils.sessionOwner()); // 전송자
			PersistenceHelper.manager.save(sendHistory);

			System.out.println("독립적 실행 안되나?");

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			System.out.println("독립적 실행 안되나!!!!?" + trs);
			if (trs != null)
				trs.rollback();
		}

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