package e3ps.approval.notice.service;

import java.util.Map;

public interface NoticeService {

	/**
	 * AUIGrid 데이터 저장
	 * 
	 * @param params : AUIGrid 데이터
	 * @throws Exception
	 */
	public abstract void save(Map<String, Object> params) throws Exception;
}
