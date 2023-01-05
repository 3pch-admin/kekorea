package e3ps.partlist.service;

import java.util.Map;

import wt.method.RemoteInterface;
import wt.util.WTException;

@RemoteInterface
public interface PartListMasterService {
	
	/**
	 * 수배표 삭제
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> deletePartListMasterAction(Map<String, Object> param) throws WTException;

	/**
	 * 수배표 등록
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> createPartListMasterAction(Map<String, Object> param) throws WTException;

	 /**
		 * 수배표 수정
		 * 
		 * @param param
		 * @return Map<String, Object>
		 * @throws WTException
		 */
		public abstract Map<String, Object> modifyPartListAction(Map<String, Object> param) throws WTException;
}
