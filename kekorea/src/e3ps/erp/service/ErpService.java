package e3ps.erp.service;

import wt.method.RemoteInterface;

@RemoteInterface
public interface ErpService {

	/**
	 * ERP 로그 작성 샘플
	 */
	public abstract void writeLog(String name, String query, String msg, boolean isResult, String sendType)
			throws Exception;

}
