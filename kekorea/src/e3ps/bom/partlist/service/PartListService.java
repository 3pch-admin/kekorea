package e3ps.bom.partlist.service;

import java.util.Map;

import wt.method.RemoteInterface;
import wt.util.WTException;

@RemoteInterface
public interface PartListService {

	/**
	 * 수배표 등록
	 * 
	 * @param params
	 * @throws Exception
	 */
	public abstract void create(Map<String, Object> params) throws Exception;

	/**
	 * 수배표 삭제
	 * 
	 * @param oid
	 * @throws Exception
	 */
	public abstract void delete(String oid) throws Exception;

	/**
	 * 수배표 수정
	 * 
	 * @param params
	 * @throws Exception
	 */
	public abstract void modify(Map<String, Object> params) throws Exception;

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
