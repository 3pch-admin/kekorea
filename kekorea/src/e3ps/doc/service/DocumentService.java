package e3ps.doc.service;

import java.util.Map;

import wt.method.RemoteInterface;
import wt.util.WTException;
import wt.vc.Versioned;

@RemoteInterface
public interface DocumentService {

	/**
	 * 문서 삭제
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> deleteDocumentAction(Map<String, Object> param) throws WTException;

	/**
	 * 문서 등록
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> createDocumentAction(Map<String, Object> param) throws WTException;
	
	/**
	 * 산출물 수정
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> modifyOutputAction(Map<String, Object> param) throws WTException;

	/**
	 * 문서 수정
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> modifyDocumentAction(Map<String, Object> param) throws WTException;
	

	/**
	 * 문서 추가
	 * 
	 * @param param
	 * @return Map<String, Object>
	 */
	public abstract Map<String, Object> addDocumentAction(Map<String, Object> param) throws WTException;

	/**
	 * 문서 결재
	 * 
	 * @param param
	 * @return Map<String, Object>
	 */
	public abstract Map<String, Object> approvalDocumentAction(Map<String, Object> param) throws WTException;

	/**
	 * 의뢰서 등록
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> createRequestDocumentAction(Map<String, Object> param) throws WTException;

	/**
	 * 의뢰서 삭제
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> deleteRequestDocumentAction(Map<String, Object> param) throws WTException;

	/**
	 * 의뢰서 수정
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> modifyRequestDocumentAction(Map<String, Object> param) throws WTException;
	
	/**
	 * 산출물 등록
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> createOutputAction(Map<String, Object> param) throws WTException;
	
	/**
	 * 산출물 개정
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract void reviseOutput(Map<String, Object> param ,Versioned versioned) throws WTException;

	public abstract void register(Map<String, Object> params) throws Exception;
	

}
