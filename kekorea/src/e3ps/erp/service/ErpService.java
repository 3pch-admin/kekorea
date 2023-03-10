package e3ps.erp.service;

import java.util.ArrayList;
import java.util.Map;

import e3ps.bom.partlist.PartListMaster;
import e3ps.part.UnitBom;
import e3ps.part.UnitSubPart;
import e3ps.part.beans.PartViewData;
import e3ps.project.Project;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.method.RemoteInterface;
import wt.part.WTPart;
import wt.util.WTException;

@RemoteInterface
public interface ErpService {

	/**
	 * 수배리스트 전송
	 * 
	 * @param master
	 * @return
	 * @throws WTException
	 */
	public abstract Map<String, Object> sendPartListToERP(PartListMaster master) throws WTException;

	/**
	 * 산출물 전송
	 * 
	 * @param document
	 * @return
	 * @throws WTException
	 */
	public abstract Map<String, Object> sendOutputToERP(WTDocument document) throws WTException;

	/**
	 * 산출물 물리파일
	 * 
	 * @param document
	 * @param project
	 * @return
	 * @throws WTException
	 */
	public abstract Map<String, Object> sendOutputToERPFile(WTDocument document, Project project) throws WTException;

	/**
	 * 구매품 ERP 전송
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> sendLibraryPart(Map<String, Object> param) throws WTException;

	/**
	 * ERP 부품 전송 결재 완료시
	 * 
	 * @param data
	 * @throws WTException
	 */
	public abstract void sendERPPARTAction(PartViewData data) throws WTException;

	/**
	 * ERP 부품 전송
	 * 
	 * @param param
	 * @throws WTException
	 */
	public abstract Map<String, Object> sendERPPARTAction(Map<String, Object> param) throws WTException;

	/**
	 * BOM 정보 ERP 전송
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> sendERPBOMAction(Map<String, Object> param) throws WTException;

	/**
	 * 도면 전송
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> sendERPDRWAction(Map<String, Object> param) throws WTException;

	/**
	 * PDF 전송
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> sendERPPDFAction(Map<String, Object> param) throws WTException;

	/**
	 * YCODE 값 세팅
	 * 
	 * @param number
	 * @throws WTException
	 */
	public abstract String setYCode(String number, Persistable per) throws WTException;

	/**
	 * 품목전송
	 * 
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public abstract String sendPartToERP(WTPart part) throws WTException;

	/**
	 * 품목전송
	 * 
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public abstract Map<String, Object> sendPartToERP(WTPart part, String rev) throws WTException;

	/**
	 * 제작 사양서
	 * 
	 * @param part
	 * @return
	 * @throws WTException
	 */
	public abstract String sendSpecPartToERP(WTPart part) throws WTException;

	/**
	 * 유닛 봄 전송
	 * 
	 * @param unitBom
	 * @throws WTException
	 */
	public abstract void sendUnitBomToERP(UnitBom unitBom) throws WTException;
	
	/**
	 * 유닛 봄 전송
	 * 
	 * @param unitBom
	 * @throws WTException
	 */
	public abstract void sendUnitBomToERP(UnitBom unitBom, ArrayList<UnitSubPart> childs) throws WTException;
	
	/**
	 * 유닛 봄 중폭확인
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> checkUnitBom(Map<String, Object> param) throws WTException;

}
