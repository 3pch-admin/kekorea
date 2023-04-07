package e3ps.epm.service;

import java.util.ArrayList;
import java.util.Map;

import e3ps.workspace.ApprovalContract;
import wt.epm.EPMDocument;
import wt.method.RemoteInterface;
import wt.util.WTException;

@RemoteInterface
public interface EpmService {
	/**
	 * 도면 결재
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> approvalEpmAction(Map<String, Object> param) throws WTException;

	/**
	 * 도면 추가
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> addEpmAction(Map<String, Object> param) throws WTException;

	/**
	 * PDF, DWG 출력
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> printDrw(Map<String, Object> param) throws WTException;

	/**
	 * DRW 다운로드
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException proe 2d 3d
	 */
	public abstract Map<String, Object> downDrw(Map<String, Object> param) throws WTException;

	/**
	 * DWG 다운로드
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> downDwg(Map<String, Object> param) throws WTException;

	/**
	 * PDF 다운로드
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> downPdf(Map<String, Object> param) throws WTException;

	/**
	 * 도면 및 모든 변환 파일들 다운로드
	 * 
	 * @param param
	 * @return Map<String, Object>
	 */
	public abstract Map<String, Object> downAll(Map<String, Object> param) throws WTException;

	/**
	 * 도면 파일 ERP 전송등등..
	 * 
	 * @param epm
	 * @throws WTException
	 */
	public abstract void sendContent(EPMDocument epm) throws WTException;

	/**
	 * 뷰어 생성
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> doPublishView(Map<String, Object> param) throws WTException;

	/**
	 * 뷰어 삭제
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> deletePublishView(Map<String, Object> param) throws WTException;

	/**
	 * AUTOCAD -> PDF
	 * 
	 * @param epm
	 * @throws WTException
	 */
	public abstract void dwgToPDF(EPMDocument epm) throws WTException;

	/**
	 * 속성 업로드
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> uploadEpmAttrAction(Map<String, Object> param) throws WTException;

	/**
	 * 일괄 개정
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> reviseCadDataAction(Map<String, Object> param) throws WTException;

	/**
	 * 코드 생성
	 * 
	 * @param param
	 * @return
	 */
	public abstract Map<String, Object> createPartCodeAction(Map<String, Object> param) throws WTException;

	/**
	 * 단순 DWG만 전송
	 * 
	 * @param param
	 * @return
	 */
	public abstract Map<String, Object> sendDWGAction(Map<String, Object> param) throws WTException;

	/**
	 * 
	 * 단순 PDF만 전송
	 * 
	 * @param param
	 * @return
	 * @throws WTException
	 */
	public abstract Map<String, Object> sendPDFAction(Map<String, Object> param) throws WTException;

	/**
	 * 뷰어생성
	 * 
	 * @param param
	 * @return
	 * @throws WTException
	 */
	public abstract Map<String, Object> createViewerAction(Map<String, Object> param) throws WTException;

	public abstract Map<String, Object> approvalModifyEpmAction(Map<String, Object> param) throws WTException;

	/**
	 * 도면 결재
	 */
	public abstract void register(Map<String, Object> params) throws Exception;

	public abstract void register(ApprovalContract contract, ArrayList<Map<String, String>> approvalRows,
			ArrayList<Map<String, String>> agreeRows, ArrayList<Map<String, String>> receiveRows);

}
