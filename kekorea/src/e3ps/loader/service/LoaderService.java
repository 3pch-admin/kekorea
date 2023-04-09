package e3ps.loader.service;

import wt.method.RemoteInterface;

@RemoteInterface
public interface LoaderService {

	/**
	 * 막종, 막종 상세 로더
	 * 
	 * @param mak    : 막종 코드
	 * @param detail : 막종 상세 코드
	 * @throws Exception
	 */
	public abstract void loaderMak(String mak, String detail) throws Exception;

	/**
	 * 거래처, 설치장소 로더
	 * 
	 * @param customer : 거래처 코드
	 * @param install  : 설치장소 코드
	 * @throws Exception
	 */
	public abstract void loadeInstall(String customer, String install) throws Exception;

	/**
	 * 부서 로더
	 */
	public abstract void loaderDepartment() throws Exception;

	/**
	 * 프로젝트 유저 타입 로더
	 */
	public abstract void loaderProjectUserType() throws Exception;

	/**
	 * 태스크 타입 로더
	 */
	public abstract void loaderTaskType() throws Exception;

}
