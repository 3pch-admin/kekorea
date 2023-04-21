
package e3ps.epm.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.common.util.CommonUtils;
import e3ps.common.util.IBAUtils;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.epm.dto.EpmDTO;
import net.sf.json.JSONArray;
import wt.clients.folder.FolderTaskLogic;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.build.EPMBuildHistory;
import wt.epm.build.EPMBuildRule;
import wt.epm.structure.EPMReferenceLink;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.IteratedFolderMemberLink;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.StringValue;
import wt.part.WTPart;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;
import wt.vc.Mastered;
import wt.vc.VersionControlHelper;

public class EpmHelper {

	/**
	 * 도면 저장 기본위치 제품 - 라이브러리 동일
	 */
	public static final String DEFAULT_ROOT = "/Default/도면";

	/**
	 * 제품, 라이브러리 컨데이터 구분 변수
	 */
	public static final String PRODUCT_CONTAINER = "PRODUCT";
	public static final String LIBRARY_CONTAINER = "LIBRARY";

	public static final EpmService service = ServiceFactory.getService(EpmService.class);
	public static final EpmHelper manager = new EpmHelper();

	public WTPart getPart(EPMDocument epm) throws Exception {
		WTPart part = null;
		if (epm == null) {
			return part;
		}

		QueryResult result = null;
		if (VersionControlHelper.isLatestIteration(epm)) {
			result = PersistenceHelper.manager.navigate(epm, "buildTarget", EPMBuildRule.class);
		} else {
			result = PersistenceHelper.manager.navigate(epm, "built", EPMBuildHistory.class);
		}

		while (result.hasMoreElements()) {
			part = (WTPart) result.nextElement();
		}
		return part;
	}

	public boolean isNumber(String number) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(EPMDocumentMaster.class, true);
		SearchCondition sc = new SearchCondition(EPMDocumentMaster.class, EPMDocumentMaster.NUMBER, "=",
				number.toUpperCase().trim());
		query.appendWhere(sc, new int[] { idx });
		QueryResult result = PersistenceHelper.manager.find(query);
		boolean isNumber = result.size() > 0 ? true : false;
		return isNumber;
	}

	public EPMDocument getEPM2D(EPMDocument ee) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(EPMReferenceLink.class, true);

		EPMDocumentMaster master = (EPMDocumentMaster) ee.getMaster();
		long id = master.getPersistInfo().getObjectIdentifier().getId();

		SearchCondition sc = new SearchCondition(EPMReferenceLink.class, "roleBObjectRef.key.id", "=", id);
		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();

		sc = new SearchCondition(EPMReferenceLink.class, EPMReferenceLink.REFERENCE_TYPE, "=", "DRAWING");
		query.appendWhere(sc, new int[] { idx });

		EPMDocument epm2d = null;
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			EPMReferenceLink link = (EPMReferenceLink) obj[0];
			epm2d = link.getReferencedBy();
		}
		return epm2d;
	}

	public boolean checkPLMYCode(String itemNo) throws Exception {
		QuerySpec query = null;

		boolean isYcode = false;

		try {
			query = new QuerySpec();

			int idx = query.appendClassList(EPMDocument.class, true);

//			SearchCondition sc = null;
//			ClassAttribute ca = null;

			if (!StringUtils.isNull(itemNo)) {
				IBAUtils.addIBAConditionEquals(query, EPMDocument.class, idx, "PART_CODE", itemNo);
			}

			QueryResult result = PersistenceHelper.manager.find(query);

			if (result.size() > 0) {
				isYcode = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return isYcode;
	}

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		System.out.println("검색 START = " + new Timestamp(new Date().getTime()));
		Map<String, Object> map = new HashMap<String, Object>();
		List<EpmDTO> list = new ArrayList<EpmDTO>();

		String fileName = (String) params.get("fileName");
		String partCode = (String) params.get("partCode");
		String number = (String) params.get("number");
		String partName = (String) params.get("partName");
		String cadType = (String) params.get("cadType");
		String material = (String) params.get("material");
		String remark = (String) params.get("remark");
		String reference = (String) params.get("reference");
		String state = (String) params.get("state");
		boolean latest = (boolean) params.get("latest");
		String oid = (String) params.get("oid"); // 폴더 OID
		String container = (String) params.get("container");
		String creatorOid = (String) params.get("creatorOid");
		String modifierOid = (String) params.get("modifierOid");
		QuerySpec query = new QuerySpec();

		int idx = query.appendClassList(EPMDocument.class, true);
		int idx_m = query.appendClassList(EPMDocumentMaster.class, false);

		QuerySpecUtils.toCI(query, idx, EPMDocument.class);
		QuerySpecUtils.toInnerJoin(query, EPMDocument.class, EPMDocumentMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);

		// 캐드파일명
		QuerySpecUtils.toEqualsAnd(query, idx, EPMDocument.class, EPMDocument.NAME, fileName);
		QuerySpecUtils.toIBAEqualsAnd(query, EPMDocument.class, idx, "PART_CODE", partCode);

		// 국제 전용 IBA 프로이 오토 캐드 검색용
		queryNumber(query, EPMDocument.class, idx, number);
		queryName(query, EPMDocument.class, idx, partName);

		// 캐드타입
		QuerySpecUtils.toEqualsAnd(query, idx, EPMDocument.class, EPMDocument.DOC_TYPE, cadType);

		QuerySpecUtils.toIBAEqualsAnd(query, EPMDocument.class, idx, "MATERIAL", material);
		QuerySpecUtils.toIBAEqualsAnd(query, EPMDocument.class, idx, "REMARKS", remark);
		QuerySpecUtils.toIBAEqualsAnd(query, EPMDocument.class, idx, "REF_NO", reference);
		QuerySpecUtils.toState(query, idx, EPMDocument.class, state);
		QuerySpecUtils.creatorQuery(query, idx, EPMDocument.class, creatorOid);
		QuerySpecUtils.modifierQuery(query, idx, EPMDocument.class, modifierOid);

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
					new ClassAttribute(EPMDocument.class, "iterationInfo.branchId"));
			fsc.setFromIndicies(new int[] { f_idx, idx }, 0);
			fsc.setOuterJoin(0);
			query.appendWhere(fsc, new int[] { f_idx, idx });
			query.appendAnd();
			long fid = folder.getPersistInfo().getObjectIdentifier().getId();
			query.appendWhere(new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", "=", fid),
					new int[] { f_idx });
		}

		if (latest) {
			QuerySpecUtils.toLatest(query, idx, EPMDocument.class);
		}

		QuerySpecUtils.toOrderBy(query, idx, EPMDocument.class, EPMDocument.MODIFY_TIMESTAMP, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			EPMDocument epm = (EPMDocument) obj[0];
			EpmDTO column = new EpmDTO(epm);
			list.add(column);
		}
		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		System.out.println("검색 END = " + new Timestamp(new Date().getTime()));
		return map;
	}

	// 버전이력
	public JSONArray history(Mastered master) throws Exception {
		ArrayList<EpmDTO> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(EPMDocument.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, EPMDocument.class, "masterReference.key.id",
				master.getPersistInfo().getObjectIdentifier().getId());
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			EPMDocument epm = (EPMDocument) obj[0];
			EpmDTO dto = new EpmDTO(epm);
			list.add(dto);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 관련 작번 CONFIG SHEET 랑 연결..
	 */
	public JSONArray jsonAuiProject(String oid) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 국제 전용 IBA 검색
	 */
	private void queryNumber(QuerySpec _query, Class _target, int _idx, String number) throws Exception {
		if (StringUtils.isNull(number)) {
			return;
		}
		AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath("DWG_NO");
		AttributeDefDefaultView aview1 = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath("DWG_No");

		if ((aview != null) || (aview1 != null)) {
			if (_query.getConditionCount() > 0)
				_query.appendAnd();

			int idx = _query.appendClassList(StringValue.class, false);
			SearchCondition sc = new SearchCondition(
					new ClassAttribute(StringValue.class, "theIBAHolderReference.key.id"), "=",
					new ClassAttribute(_target, "thePersistInfo.theObjectIdentifier.id"));
			sc.setFromIndicies(new int[] { idx, _idx }, 0);
			sc.setOuterJoin(0);
			_query.appendWhere(sc, new int[] { idx, _idx });
			_query.appendAnd();
			_query.appendOpenParen();
			sc = new SearchCondition(StringValue.class, "definitionReference.hierarchyID", "=",
					aview1.getHierarchyID());

			_query.appendWhere(sc, new int[] { idx, _idx });
			_query.appendOr();
			sc = new SearchCondition(StringValue.class, "definitionReference.hierarchyID", "=", aview.getHierarchyID());

			_query.appendWhere(sc, new int[] { idx });
			_query.appendCloseParen();

			_query.appendAnd();

			String[] str = number.split(";");
			if (str.length == 1) {
				sc = new SearchCondition(StringValue.class, "value2", SearchCondition.LIKE,
						"%" + str[0].toUpperCase() + "%");
				_query.appendWhere(sc, new int[] { idx });
			} else if (str.length >= 2) {
				_query.appendOpenParen();
				sc = new SearchCondition(StringValue.class, "value2", SearchCondition.LIKE,
						"%" + str[0].toUpperCase() + "%");
				_query.appendWhere(sc, new int[] { idx });
				for (int i = 1; i < str.length; i++) {
					_query.appendOr();
					sc = new SearchCondition(StringValue.class, "value2", SearchCondition.LIKE,
							"%" + str[i].toUpperCase() + "%");
					_query.appendWhere(sc, new int[] { idx });
				}
				_query.appendCloseParen();
			}
		}
	}

	/**
	 * 국제 전용 IBA 검색
	 */
	private void queryName(QuerySpec _query, Class _target, int _idx, String name) throws Exception {
		if (StringUtils.isNull(name)) {
			return;
		}

		AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath("TITLE1");
		AttributeDefDefaultView aview1 = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath("TITLE2");
		AttributeDefDefaultView aview2 = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath("NAME_OF_PARTS");

		if ((aview != null) || (aview1 != null) || (aview2 != null)) {
			if (_query.getConditionCount() > 0)
				_query.appendAnd();

			int idx = _query.appendClassList(StringValue.class, false);
			SearchCondition sc = new SearchCondition(
					new ClassAttribute(StringValue.class, "theIBAHolderReference.key.id"), "=",
					new ClassAttribute(_target, "thePersistInfo.theObjectIdentifier.id"));
			sc.setFromIndicies(new int[] { idx, _idx }, 0);
			sc.setOuterJoin(0);
			_query.appendWhere(sc, new int[] { idx, _idx });
			_query.appendAnd();
			_query.appendOpenParen();
			sc = new SearchCondition(StringValue.class, "definitionReference.hierarchyID", "=",
					aview2.getHierarchyID());

			_query.appendWhere(sc, new int[] { idx, _idx });
			_query.appendOr();

			sc = new SearchCondition(StringValue.class, "definitionReference.hierarchyID", "=",
					aview1.getHierarchyID());

			_query.appendWhere(sc, new int[] { idx, _idx });
			_query.appendOr();
			sc = new SearchCondition(StringValue.class, "definitionReference.hierarchyID", "=", aview.getHierarchyID());

			_query.appendWhere(sc, new int[] { idx });
			_query.appendCloseParen();

			_query.appendAnd();

			String[] str = name.split(";");
			if (str.length == 1) {
				sc = new SearchCondition(StringValue.class, "value2", SearchCondition.LIKE,
						"%" + str[0].toUpperCase() + "%");
				_query.appendWhere(sc, new int[] { idx });
			} else if (str.length >= 2) {
				_query.appendOpenParen();
				sc = new SearchCondition(StringValue.class, "value2", SearchCondition.LIKE,
						"%" + str[0].toUpperCase() + "%");
				_query.appendWhere(sc, new int[] { idx });
				for (int i = 1; i < str.length; i++) {
					_query.appendOr();
					sc = new SearchCondition(StringValue.class, "value2", SearchCondition.LIKE,
							"%" + str[i].toUpperCase() + "%");
					_query.appendWhere(sc, new int[] { idx });
				}
				_query.appendCloseParen();
			}
		}
	}
}
