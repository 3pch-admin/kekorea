package e3ps.part.service;

import java.util.Map;

import wt.method.RemoteInterface;
import wt.util.WTException;

@RemoteInterface
public interface PartService {

	public abstract void test() throws WTException;

	/**
	 * EPLAN 결재
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> modifyPartAction(Map<String, Object> param) throws WTException;

	/**
	 * EPLAN 결재
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> approvalEplanAction(Map<String, Object> param) throws WTException;

	/**
	 * 코드 생성
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> createCodeAction(Map<String, Object> param) throws WTException;

	/**
	 * 구매품 결재
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> approvalLibraryPartAction(Map<String, Object> param) throws WTException;

	/**
	 * 부품 추가
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> addPartAction(Map<String, Object> param) throws WTException;

	/**
	 * 구매풍 등록
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> createLibraryPartAction(Map<String, Object> param) throws WTException;

	/**
	 * 전장품 결재
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> approvalElecPartAction(Map<String, Object> param) throws WTException;

	/**
	 * 전장품 등록
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> createElecPartAction(Map<String, Object> param) throws WTException;

	/**
	 * 가공품 등록
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> createProductPartAction(Map<String, Object> param) throws WTException;

	/**
	 * 부품 삭제
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> deletePartAction(Map<String, Object> param) throws WTException;

	/**
	 * 부품 새이름으로 저장
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> saveAsPartAction(Map<String, Object> param) throws WTException;

	/**
	 * 부품 일괄 등록
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> createAllPartsAction(Map<String, Object> param) throws WTException;

	/**
	 * 구매품 삭제
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> deleteLibraryPartAction(Map<String, Object> param) throws WTException;

	/**
	 * 가공품 삭제
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> deleteProductPartAction(Map<String, Object> param) throws WTException;

	/**
	 * 옵션 BOM 생성
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> createBomAction(Map<String, Object> param) throws WTException;

	/**
	 * BOM 부품 연결 끊기
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> deleteBomPartAction(Map<String, Object> param) throws WTException;

	/**
	 * BOM 부품 연결
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> insertBomPartAction(Map<String, Object> param) throws WTException;

	/**
	 * BOM 부품 체크인
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> checkinBomPartAction(Map<String, Object> param) throws WTException;

	/**
	 * BOM 부품 체크아웃
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> checkoutBomPartAction(Map<String, Object> param) throws WTException;

	/**
	 * BOM 부품 체크아웃 취소
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> undocheckoutBomPartAction(Map<String, Object> param) throws WTException;

	/**
	 * BOM 부품 위치변경
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> setDndUrlAction(Map<String, Object> param) throws WTException;

	/**
	 * BOM 부품 위치변경
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> setIndentUrlAction(Map<String, Object> param) throws WTException;

	/**
	 * BOM 부품 위치변경
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> setOutdentUrlAction(Map<String, Object> param) throws WTException;

	/**
	 * 구매품 수정
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> modifyLibraryPartAction(Map<String, Object> param) throws WTException;

	/**
	 * 부품 일괄 등록
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> createBundlePartAction(Map<String, Object> param) throws WTException;

	/**
	 * 제품사양서 등록
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> createProductSpecAction(Map<String, Object> param) throws WTException;

	/**
	 * UNIT BOM 등록
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> createUnitBomAction(Map<String, Object> param) throws WTException;

	/**
	 * UNIT BOM 재전송
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> reSendAction(Map<String, Object> param) throws WTException;

	/**
	 * 유닛 봄 추가
	 * 
	 * @param param
	 * @return
	 * @throws WTException
	 */
	public abstract Map<String, Object> addUnitBomAction(Map<String, Object> param) throws WTException;

	/**
	 * 유닛 봄 코드 생성
	 * 
	 * @param param
	 * @return
	 * @throws WTException
	 */
	public abstract Map<String, Object> createUnitCodeAction(Map<String, Object> param) throws WTException;

	/**
	 * 부품 일괄 등록
	 */
	public abstract void bundle(Map<String, Object> params) throws Exception;
}
