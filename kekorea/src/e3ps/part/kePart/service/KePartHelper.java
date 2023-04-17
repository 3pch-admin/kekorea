package e3ps.part.kePart.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import e3ps.bom.tbom.TBOMData;
import e3ps.bom.tbom.TBOMMaster;
import e3ps.bom.tbom.TBOMMasterDataLink;
import e3ps.bom.tbom.TBOMMasterProjectLink;
import e3ps.bom.tbom.dto.TBOMDTO;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.epm.keDrawing.KeDrawing;
import e3ps.part.kePart.KePart;
import e3ps.part.kePart.KePartMaster;
import e3ps.part.kePart.beans.KePartDTO;
import e3ps.project.Project;
import net.sf.json.JSONArray;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;

public class KePartHelper {

	public static final KePartHelper manager = new KePartHelper();
	public static final KePartService service = ServiceFactory.getService(KePartService.class);

	/**
	 * KE 부품 조회
	 */
	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<KePartDTO> list = new ArrayList<>();
		boolean latest = (boolean) params.get("latest");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(KePart.class, true);
		int idx_m = query.appendClassList(KePartMaster.class, false);

		QuerySpecUtils.toInnerJoin(query, KePart.class, KePartMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);

		if (latest) {
			QuerySpecUtils.toBooleanAnd(query, idx, KePart.class, KePart.LATEST, true);
		} else {

			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}

			query.appendOpenParen();
			SearchCondition sc = new SearchCondition(KeDrawing.class, KeDrawing.LATEST, SearchCondition.IS_TRUE);
			query.appendWhere(sc, new int[] { idx });
			QuerySpecUtils.toBooleanOr(query, idx, KePart.class, KePart.LATEST, false);
			query.appendCloseParen();
		}

		QuerySpecUtils.toOrderBy(query, idx, KePart.class, KePart.CREATE_TIMESTAMP, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			KePart kePart = (KePart) obj[0];
			KePartDTO column = new KePartDTO(kePart);
			list.add(column);
		}
		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	/**
	 * 마지막 KE 도면인지 확인 하는 함수
	 */
	public boolean isLast(KePartMaster master) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(KePart.class, true);
		int idx_m = query.appendClassList(KePartMaster.class, false);
		QuerySpecUtils.toInnerJoin(query, KePart.class, KePartMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);
		QuerySpecUtils.toEqualsAnd(query, idx, KePart.class, "masterReference.key.id",
				master.getPersistInfo().getObjectIdentifier().getId());
		QueryResult result = PersistenceHelper.manager.find(query);
		return result.size() == 1 ? true : false;
	}

	/**
	 * 현재버전의 KE 부품의 이전 버전의 부품을 가져오는 함수
	 */
	public KePart getPreKePart(KePart kePart) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(KePart.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, KePart.class, KePart.VERSION, kePart.getVersion() - 1);
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			return (KePart) obj[0];
		}
		return null;
	}

	/**
	 * KE 부품 등록시 중복 체크 하는 함수
	 */
	public Map<String, Object> isValid(ArrayList<KePartDTO> addRow, ArrayList<KePartDTO> editRow) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		for (KePartDTO dto : addRow) {
			String keNumber = dto.getKeNumber();
			int lotNo = dto.getLotNo();
			boolean isExist = exist(keNumber, lotNo);
			if (isExist) {
				result.put("isExist", true);
				result.put("msg", "LOT NO가 = " + lotNo + "이고 품번이 = " + keNumber + "가 이미 존재합니다.");
				return result;
			}
		}

		for (KePartDTO dto : editRow) {
			KePartMaster master = (KePartMaster) CommonUtils.getObject(dto.getMoid());
			String orgKeNumber = master.getKeNumber();
			int orgLotNo = master.getLotNo();
			String keNumber = dto.getKeNumber();
			int lotNo = dto.getLotNo();

			if (!orgKeNumber.equals(keNumber) || orgLotNo != lotNo) {
				boolean isExist = exist(keNumber, lotNo);
				if (isExist) {
					result.put("isExist", true);
					result.put("msg", "LOT NO가 = " + lotNo + "이고 도번이 = " + keNumber + "가 이미 존재합니다.");
					return result;
				}
			}
		}
		result.put("isExist", false);
		return result;
	}

	/**
	 * KE 부품 번호+LOT NO으로 중복 있는지 확인
	 */
	private boolean exist(String keNumber, int lotNo) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx_m = query.appendClassList(KePartMaster.class, true);
		int idx = query.appendClassList(KePart.class, true);
		QuerySpecUtils.toInnerJoin(query, KePartMaster.class, KePart.class, WTAttributeNameIfc.ID_NAME,
				"masterReference.key.id", idx_m, idx);
		QuerySpecUtils.toEqualsAnd(query, idx_m, KePartMaster.class, KePartMaster.KE_NUMBER, keNumber);
		QuerySpecUtils.toEqualsAnd(query, idx_m, KePartMaster.class, KePartMaster.LOT_NO, lotNo);
		QueryResult result = PersistenceHelper.manager.find(query);
		return result.size() > 0 ? true : false;
	}

	/**
	 * KE 부품 버전이력 정보 가져오는 함수
	 */
	public JSONArray history(KePartMaster master) throws Exception {
		ArrayList<KePartDTO> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(KePart.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, KePart.class, "masterReference.key.id",
				master.getPersistInfo().getObjectIdentifier().getId());
		QuerySpecUtils.toOrderBy(query, idx, KePart.class, KePart.VERSION, true);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			KePart kePart = (KePart) obj[0];
			KePartDTO dto = new KePartDTO(kePart);
			list.add(dto);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * KE 부품과 관련된 T-BOM 정보를 가져온다
	 */
	public JSONArray jsonArrayAui(String oid) throws Exception {
		KePart kePart = (KePart) CommonUtils.getObject(oid);
		ArrayList<TBOMDTO> list = new ArrayList<>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(TBOMMasterDataLink.class, true);
		int idx_k = query.appendClassList(KePart.class, true);
		int idx_p = query.appendClassList(Project.class, true);
		int idx_t = query.appendClassList(TBOMMaster.class, true);
		int idx_link = query.appendClassList(TBOMMasterProjectLink.class, true);
		int idx_data = query.appendClassList(TBOMData.class, true);

		QuerySpecUtils.toInnerJoin(query, TBOMMasterDataLink.class, TBOMData.class, "roleBObjectRef.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_data);
		QuerySpecUtils.toInnerJoin(query, TBOMMasterDataLink.class, TBOMMaster.class, "roleAObjectRef.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_t);

		QuerySpecUtils.toInnerJoin(query, TBOMMaster.class, TBOMMasterProjectLink.class, WTAttributeNameIfc.ID_NAME,
				"roleAObjectRef.key.id", idx_t, idx_link);
		QuerySpecUtils.toInnerJoin(query, Project.class, TBOMMasterProjectLink.class, WTAttributeNameIfc.ID_NAME,
				"roleBObjectRef.key.id", idx_p, idx_link);

		QuerySpecUtils.toInnerJoin(query, KePart.class, TBOMData.class, WTAttributeNameIfc.ID_NAME,
				"kePartReference.key.id", idx_k, idx_data);
		QuerySpecUtils.toEqualsAnd(query, idx_data, TBOMData.class, "kePartReference.key.id",
				kePart.getPersistInfo().getObjectIdentifier().getId());

		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			TBOMMasterProjectLink link = (TBOMMasterProjectLink) obj[4];
			TBOMDTO dto = new TBOMDTO(link);
			list.add(dto);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * TBOM 등록에 사용 되었는지 여부 확인
	 */
	public Map<String, Object> isTBOM(ArrayList<KePartDTO> removeRow) throws Exception {
		Map<String, Object> result = new HashMap<>();
		for (KePartDTO dto : removeRow) {
			KePart kePart = (KePart) CommonUtils.getObject(dto.getOid());
			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(TBOMData.class, true);
			QuerySpecUtils.toEqualsAnd(query, idx, TBOMData.class, "kePartReference.key.id", kePart);
			QueryResult qr = PersistenceHelper.manager.find(query);
			if (qr.size() > 0) {
				result.put("tbom", true);
				result.put("msg", "TBOM에 사용된 KE 부품 입니다.\n부품번호 = " + dto.getKeNumber());
				return result;
			}
		}
		result.put("tbom", false);
		return result;
	}
}
