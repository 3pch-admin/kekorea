package e3ps.system.service;

import wt.method.RemoteInterface;

@RemoteInterface
public interface ErrorLogService {

	/**
	 * 시스템 에러 로그 기록
	 */
	public abstract void create(String errorMsg, String callUrl, String errorType) throws Exception;

}
