package e3ps.part.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.IBAUtils;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.doc.WTDocumentWTPartLink;
import e3ps.part.dto.PartDTO;
import net.sf.json.JSONArray;
import wt.clients.folder.FolderTaskLogic;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.epm.build.EPMBuildHistory;
import wt.epm.build.EPMBuildRule;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.IteratedFolderMemberLink;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;
import wt.vc.VersionControlHelper;

public class PartHelper {

	/**
	 * 도면 저장 기본위치 제품 - 라이브러리 동일
	 */
	public static final String DEFAULT_ROOT = "/Default/도면";

	/**
	 * 제품, 라이브러리 컨데이터 구분 변수
	 */
	public static final String PRODUCT_CONTAINER = "PRODUCT";
	public static final String LIBRARY_CONTAINER = "LIBRARY";

	public static final String COMMON_DEFAULT_ROOT = "/Default/도면/부품/일반부품";
	public static final String NEW_DEFAULT_ROOT = "/Default/도면/부품/신규부품";
	public static final String SPEC_DEFAULT_ROOT = "/Default/도면/부품/제작사양서";

	public static final PartService service = ServiceFactory.getService(PartService.class);
	public static final PartHelper manager = new PartHelper();

	/**
	 * 부품 일괄 등록시 PART_CODE IBA 값 검증 있으면 NG 리턴
	 */
	public Map<String, Object> bundleValidatorNumber(String number) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTPart.class, true);
		QuerySpecUtils.toIBAEqualsAnd(query, WTPart.class, idx, "PART_CODE", number);
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			map.put("ycode_check", "NG(YCODE)");
			map.put("ycode", false);
		} else {
			map.put("ycode_check", "OK");
			map.put("ycode", true);
		}
		return map;
	}

	/**
	 * 부품 일괄 등록시 규격(WTPart Number 검증 있을 경우 NG 리턴)
	 */
	public Map<String, Object> bundleValidatorSpec(String spec) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTPartMaster.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, WTPartMaster.class, WTPartMaster.NUMBER, spec);
		QueryResult result = PersistenceHelper.manager.find(query);

		if (result.hasMoreElements()) {
			map.put("dwg_check", "NG(DWG_NO)");
			map.put("dwg", false);
		} else {
			map.put("dwg_check", "OK");
			map.put("dwg", true);
		}
		return map;
	}

	/**
	 * 부품 검색 함수
	 */
	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<PartDTO> list = new ArrayList<PartDTO>();

		boolean latest = (boolean) params.get("latest");
		String oid = (String) params.get("oid"); // 폴더 OID
		String container = (String) params.get("container");
		String name = (String) params.get("name");
		String partCode = (String) params.get("partCode");
		String partName = (String) params.get("partName");
		String number = (String) params.get("number");
		String material = (String) params.get("material");
		String remarks = (String) params.get("remarks");
		String maker = (String) params.get("maker");
		String creatorOid = (String) params.get("creatorOid");
		String createdFrom = (String) params.get("createdFrom");
		String createdTo = (String) params.get("createdTo");
		String modifierOid = (String) params.get("modifierOid");
		String modifiedFrom = (String) params.get("modifiedFrom");
		String modifiedTo = (String) params.get("modifiedTo");
		String state = (String) params.get("state");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTPart.class, true);
		int idx_m = query.appendClassList(WTPartMaster.class, false);

		QuerySpecUtils.toCI(query, idx, WTPart.class);

		QuerySpecUtils.toInnerJoin(query, WTPart.class, WTPartMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);
		QuerySpecUtils.toLikeAnd(query, idx, WTPart.class, WTPart.NAME, name);
		QuerySpecUtils.toLikeAnd(query, idx, WTPart.class, WTPart.NUMBER, number);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, WTPart.class, WTPart.CREATE_TIMESTAMP, createdFrom, createdTo);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, WTPart.class, WTPart.MODIFY_TIMESTAMP, modifiedFrom,
				modifiedTo);
		QuerySpecUtils.toLikeAnd(query, idx, WTPart.class, WTPart.LIFE_CYCLE_STATE, state);

		// 국제 전용 IBA 프로이 오토 캐드 검색용
		QuerySpecUtils.queryLikeNumber(query, WTPart.class, idx, number);
		QuerySpecUtils.queryLikeName(query, WTPart.class, idx, partName);
		QuerySpecUtils.toIBALikeAnd(query, WTPart.class, idx, "PART_CODE", partCode);

		QuerySpecUtils.creatorQuery(query, idx, WTPart.class, creatorOid);
		QuerySpecUtils.modifierQuery(query, idx, WTPart.class, modifierOid);

		QuerySpecUtils.toIBALikeAnd(query, WTPart.class, idx, "REMARKS", remarks);
		QuerySpecUtils.toIBALikeAnd(query, WTPart.class, idx, "MATERIAL", material);
		QuerySpecUtils.toIBALikeAnd(query, WTPart.class, idx, "MAKER", maker);

		Folder folder = null;
		if (!StringUtils.isNull(oid)) {
			folder = (Folder) CommonUtils.getObject(oid);
		} else {
			if (container.equals(PRODUCT_CONTAINER)) {
				folder = FolderTaskLogic.getFolder(DEFAULT_ROOT, CommonUtils.getPDMLinkProductContainer());
			} else if (container.equalsIgnoreCase(LIBRARY_CONTAINER)) {
				folder = FolderTaskLogic.getFolder(DEFAULT_ROOT, CommonUtils.getWTLibraryContainer());
			}
		}

		if (folder != null) {
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			int f_idx = query.appendClassList(IteratedFolderMemberLink.class, false);
			ClassAttribute fca = new ClassAttribute(IteratedFolderMemberLink.class, "roleBObjectRef.key.branchId");
			SearchCondition fsc = new SearchCondition(fca, "=",
					new ClassAttribute(WTPart.class, "iterationInfo.branchId"));
			fsc.setFromIndicies(new int[] { f_idx, idx }, 0);
			fsc.setOuterJoin(0);
			query.appendWhere(fsc, new int[] { f_idx, idx });
			query.appendAnd();
			long fid = folder.getPersistInfo().getObjectIdentifier().getId();
			query.appendWhere(new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", "=", fid),
					new int[] { f_idx });
		}

		if (latest) {
			QuerySpecUtils.toLatest(query, idx, WTPart.class);
		}

		QuerySpecUtils.toOrderBy(query, idx, WTPart.class, WTPart.MODIFY_TIMESTAMP, true);
		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WTPart part = (WTPart) obj[0];
			PartDTO column = new PartDTO(part);
			list.add(column);
		}
		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	// 부품 정보 관련문서
	public JSONArray jsonArrayAui(String oid) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();
//		WTPart part = (WTPart) CommonUtils.getObject(oid);
		QuerySpec query = new QuerySpec();

		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WTDocumentWTPartLink link = (WTDocumentWTPartLink) obj[1];
			WTDocument document = link.getDocument();
			Map<String, String> map = new HashMap<>();
			map.put("oid", document.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("number", document.getNumber());
			map.put("name", document.getName());
			map.put("version", CommonUtils.getFullVersion(document));
			map.put("state", document.getLifeCycleState().getDisplay());
			map.put("modifier", document.getModifierFullName());
			map.put("modifiedDate_txt", CommonUtils.getPersistableTime(document.getModifyTimestamp()));
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	// 버전 이력
	public JSONArray list(WTPartMaster master) throws Exception {
		ArrayList<PartDTO> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTPart.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, WTPart.class, "masterReference.key.id",
				master.getPersistInfo().getObjectIdentifier().getId());
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WTPart part = (WTPart) obj[0];
			PartDTO dto = new PartDTO(part);
			list.add(dto);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 품번으로 최신 부품 있는지 확인 한다.
	 */
	public WTPart getWTPart(String partNo) throws Exception {

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTPartMaster.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, WTPartMaster.class, WTPartMaster.NUMBER, partNo);

		return null;
	}

	/**
	 * 부품과 연결된 도면 찾아오기
	 */
	public EPMDocument getEPMDocument(WTPart part) throws Exception {
		EPMDocument epm = null;
		if (part == null) {
			return epm;
		}

		QueryResult result = null;
		if (VersionControlHelper.isLatestIteration(part)) {
			result = PersistenceHelper.manager.navigate(part, "buildSource", EPMBuildRule.class);
		} else {
			result = PersistenceHelper.manager.navigate(part, "builtBy", EPMBuildHistory.class);
		}
		if (result.hasMoreElements()) {
			epm = (EPMDocument) result.nextElement();
		}
		return epm;
	}
}
