package e3ps.erp.service;

import wt.method.RemoteInterface;

@RemoteInterface
public interface ErpService {

	/**
	 * ERP 전송 로그 저장 개별적 트랙잭션
	 */
	public abstract void save(String name, boolean result, String sendQuery, String snedType) throws Exception;

}
