
package e3ps.epm.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.common.util.CommonUtils;
import e3ps.common.util.DateUtils;
import e3ps.common.util.FolderUtils;
import e3ps.common.util.IBAUtils;
import e3ps.common.util.MessageHelper;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.epm.ViewerData;
import e3ps.epm.column.EpmLibraryColumnData;
import e3ps.epm.column.EpmProductColumnData;
import e3ps.epm.column.ViewerColumnData;
import e3ps.epm.dto.EpmDTO;
import e3ps.org.People;
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
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.folder.IteratedFolderMemberLink;
import wt.part.WTPart;
import wt.query.ClassAttribute;
import wt.query.ColumnExpression;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SQLFunction;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;
import wt.vc.Mastered;
import wt.vc.VersionControlHelper;
import wt.vc.wip.WorkInProgressHelper;

public class EpmHelper {

	public static final String[] CADTYPE_DISPLAY = new String[] { "어셈블리 (ASSEMBLY)", "파트 (PART)", "도면 (DRAWING)" };

	public static final String[] CADTYPE_VALUE = new String[] { "CADASSEMBLY", "CADCOMPONENT", "CADDRAWING" };

	public static final String[] EPM_STATE_DISPLAY = new String[] { "작업 중", "승인 중", "승인됨", "반려됨", "폐기" };

	public static final String[] EPM_STATE_VALUE = new String[] { "INWORK", "UNDERAPPROVAL", "RELEASED", "RETURN",
			"WITHDRAWN" };

	public static final String ELEC_ROOT = "/Default/도면/전장품";

	public static final String LIBRARY_ROOT = "/Default/도면";

	public static final String PRODUCT_ROOT = "/Default/도면";

	public static final String PRODUCT_CONTEXT = "PRODUCT";

	public static final String LIBRARY_CONTEXT = "LIBRARY";

	public static final String ELEC_CONTEXT = "ELEC";

	/**
	 * access service
	 */
	public static final EpmService service = ServiceFactory.getService(EpmService.class);

	/**
	 * access helper
	 */
	public static final EpmHelper manager = new EpmHelper();

	public Map<String, Object> findViewer(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Object> list = new ArrayList<Object>();
		QuerySpec query = null;

		// search param
		String name = (String) param.get("name");
		String number = (String) param.get("number");
		// String partName = (String) param.get("partName");
		String fileName = (String) param.get("fileName");
		String creatorsOid = (String) param.get("creatorsOid");
		String predate = (String) param.get("predate");
		String postdate = (String) param.get("postdate");
		ReferenceFactory rf = new ReferenceFactory();
		// 정렬
		String sort = (String) param.get("sort");
		String sortKey = (String) param.get("sortKey");

		try {
			query = new QuerySpec();

			int idx = query.appendClassList(ViewerData.class, true);

			SearchCondition sc = null;
			ClassAttribute ca = null;

			// sc = WorkInProgressHelper.getSearchCondition_CI(ViewerData.class);
			// query.appendWhere(sc, new int[] { idx });
			// query.appendAnd();

			// 대소문자 구분
			if (!StringUtils.isNull(number)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				ca = new ClassAttribute(ViewerData.class, ViewerData.NUMBER);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(number);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			// 대소문자 구분
			if (!StringUtils.isNull(name)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				ca = new ClassAttribute(ViewerData.class, ViewerData.NAME);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(name);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			// 대소문자 구분
			if (!StringUtils.isNull(fileName)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				ca = new ClassAttribute(ViewerData.class, ViewerData.FILE_NAME);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(fileName);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(creatorsOid)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				People user = (People) rf.getReference(creatorsOid).getObject();
				long ids = user.getUser().getPersistInfo().getObjectIdentifier().getId();
				sc = new SearchCondition(ViewerData.class, "ownership.owner.key.id", SearchCondition.EQUAL, ids);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(predate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp start = DateUtils.convertStartDate(predate);
				sc = new SearchCondition(ViewerData.class, WTAttributeNameIfc.CREATE_STAMP_NAME,
						SearchCondition.GREATER_THAN_OR_EQUAL, start);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(postdate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp end = DateUtils.convertEndDate(postdate);
				sc = new SearchCondition(ViewerData.class, WTAttributeNameIfc.CREATE_STAMP_NAME,
						SearchCondition.LESS_THAN_OR_EQUAL, end);
				query.appendWhere(sc, new int[] { idx });
			}

			if (StringUtils.isNull(sort)) {
				sort = "true";
			}

			if (StringUtils.isNull(sortKey)) {
				// sortKey = WTAttributeNameIfc.CREATE_STAMP_NAME;
				sortKey = WTAttributeNameIfc.MODIFY_STAMP_NAME;
			}

			ca = new ClassAttribute(ViewerData.class, sortKey);
			OrderBy orderBy = new OrderBy(ca, Boolean.parseBoolean(sort));
			query.appendOrderBy(orderBy, new int[] { idx });

			PageQueryUtils pager = new PageQueryUtils(param, query);
			PagingQueryResult result = pager.find();

			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				ViewerData viewerData = (ViewerData) obj[0];
				ViewerColumnData data = new ViewerColumnData(viewerData);
				list.add(data);
			}

			map.put("list", list);
			map.put("lastPage", pager.getLastPage());
			map.put("topListCount", pager.getTotal());
			map.put("sessionid", pager.getSessionId());
			map.put("curPage", pager.getCpage());
			map.put("total", pager.getTotalSize());
			map.put("result", "SUCCESS");
		} catch (Exception e) {
			map.put("result", "FAIL");
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * @param param
	 * @return QuerySpec
	 */
	public Map<String, Object> find(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Object> list = new ArrayList<Object>();
		QuerySpec query = null;

		// search param
		String number = (String) param.get("number");
		String partName = (String) param.get("partName");
		String statesEpm = (String) param.get("statesEpm");
		String creatorsOid = (String) param.get("creatorsOid");
		String modifierOid = (String) param.get("modifierOid");
		String fileName = (String) param.get("fileName");

		String predate = (String) param.get("predate");
		String postdate = (String) param.get("postdate");

		String predate_m = (String) param.get("predate_m");
		String postdate_m = (String) param.get("postdate_m");

		String sub_folder = (String) param.get("sub_folder");

		String latest = (String) param.get("latest");
		if (StringUtils.isNull(latest)) {
			latest = "true";
		}

		String location = (String) param.get("location");
		ReferenceFactory rf = new ReferenceFactory();
		// 정렬
		String sort = (String) param.get("sort");
		String sortKey = (String) param.get("sortKey");

		// context
		String context = (String) param.get("context");

		String epmTypes = (String) param.get("epmTypes");

		String SPEC = (String) param.get("SPEC");
		String MAKER = (String) param.get("MAKER");
		String MASTER_TYPE = (String) param.get("MASTER_TYPE");

		String material = (String) param.get("material");

		String remark = (String) param.get("remark");
		String partCode = (String) param.get("partCode");

		String referenceDrwing = (String) param.get("referenceDrwing");

		try {
			query = new QuerySpec();

			int idx = query.appendClassList(EPMDocument.class, true);
			int master = query.appendClassList(EPMDocumentMaster.class, false);

			SearchCondition sc = null;
			ClassAttribute ca = null;

			sc = WorkInProgressHelper.getSearchCondition_CI(EPMDocument.class);
			query.appendWhere(sc, new int[] { idx });
			query.appendAnd();

			sc = new SearchCondition(EPMDocument.class, "masterReference.key.id", EPMDocumentMaster.class,
					"thePersistInfo.theObjectIdentifier.id");
			query.appendWhere(sc, new int[] { idx, master });

			if (!StringUtils.isNull(number)) {
				IBAUtils.queryNumber(query, EPMDocument.class, idx, number);
			}

			if (!StringUtils.isNull(partName)) {
				IBAUtils.queryName(query, EPMDocument.class, idx, partName);
			}

			// 대소문자 구분
			if (!StringUtils.isNull(fileName)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				ca = new ClassAttribute(EPMDocument.class, EPMDocument.NAME);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(fileName);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(creatorsOid)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				People user = (People) rf.getReference(creatorsOid).getObject();
				long ids = user.getUser().getPersistInfo().getObjectIdentifier().getId();
				System.out.println("ids=" + ids);
				sc = new SearchCondition(EPMDocument.class, "iterationInfo.creator.key.id", SearchCondition.EQUAL, ids);
				query.appendWhere(sc, new int[] { idx });
			}

			// 수정자
			if (!StringUtils.isNull(modifierOid)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				People user = (People) rf.getReference(modifierOid).getObject();
				long ids = user.getUser().getPersistInfo().getObjectIdentifier().getId();
				sc = new SearchCondition(EPMDocument.class, "iterationInfo.modifier.key.id", SearchCondition.EQUAL,
						ids);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(statesEpm)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				sc = new SearchCondition(EPMDocument.class, "state.state", SearchCondition.EQUAL, statesEpm);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(predate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp start = DateUtils.convertStartDate(predate);
				sc = new SearchCondition(EPMDocument.class, WTAttributeNameIfc.CREATE_STAMP_NAME,
						SearchCondition.GREATER_THAN_OR_EQUAL, start);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(postdate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp end = DateUtils.convertEndDate(postdate);
				sc = new SearchCondition(EPMDocument.class, WTAttributeNameIfc.CREATE_STAMP_NAME,
						SearchCondition.LESS_THAN_OR_EQUAL, end);
				query.appendWhere(sc, new int[] { idx });
			}

			// 수정일
			if (!StringUtils.isNull(predate_m)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp start = DateUtils.convertStartDate(predate_m);
				sc = new SearchCondition(EPMDocument.class, WTAttributeNameIfc.MODIFY_STAMP_NAME,
						SearchCondition.GREATER_THAN_OR_EQUAL, start);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(postdate_m)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp end = DateUtils.convertEndDate(postdate_m);
				sc = new SearchCondition(EPMDocument.class, WTAttributeNameIfc.MODIFY_STAMP_NAME,
						SearchCondition.LESS_THAN_OR_EQUAL, end);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(epmTypes)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				sc = new SearchCondition(EPMDocument.class, EPMDocument.DOC_TYPE, "=", epmTypes);
				query.appendWhere(sc, new int[] { idx });
			}

			Folder folder = null;
			if (context.equalsIgnoreCase(PRODUCT_CONTEXT)) {
				if (StringUtils.isNull(location)) {
					location = PRODUCT_ROOT;
				}
				folder = FolderTaskLogic.getFolder(location, CommonUtils.getContainer());
			} else if (context.equalsIgnoreCase(LIBRARY_CONTEXT)) {
				if (StringUtils.isNull(location)) {
					location = LIBRARY_ROOT;
				}
				folder = FolderTaskLogic.getFolder(location, CommonUtils.getLibrary());
			} else if (context.equalsIgnoreCase(ELEC_CONTEXT)) {
				if (StringUtils.isNull(location)) {
					location = ELEC_ROOT;
				}
				folder = FolderTaskLogic.getFolder(location, CommonUtils.getContainer());
			}

			if (!StringUtils.isNull(folder)) {

				if (query.getConditionCount() > 0)
					query.appendAnd();
				int f_idx = query.appendClassList(IteratedFolderMemberLink.class, false);
				ClassAttribute fca = new ClassAttribute(IteratedFolderMemberLink.class, "roleBObjectRef.key.branchId");
				SearchCondition fsc = new SearchCondition(fca, "=",
						new ClassAttribute(EPMDocument.class, "iterationInfo.branchId"));
				fsc.setFromIndicies(new int[] { f_idx, idx }, 0);
				fsc.setOuterJoin(0);
				query.appendWhere(fsc, new int[] { f_idx, idx });
				query.appendAnd();

				query.appendOpenParen();
				long fid = folder.getPersistInfo().getObjectIdentifier().getId();
				query.appendWhere(
						new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", "=", fid),
						new int[] { f_idx });

				if (!StringUtils.isNull(sub_folder)) {
					ArrayList<Folder> folders = FolderUtils.getSubFolders(folder, new ArrayList<Folder>());
					for (int i = 0; i < folders.size(); i++) {
						Folder sub = (Folder) folders.get(i);
						query.appendOr();
						long sfid = sub.getPersistInfo().getObjectIdentifier().getId();
						query.appendWhere(
								new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", "=", sfid),
								new int[] { f_idx });
					}
				}
				query.appendCloseParen();
			}

			if (!StringUtils.isNull(partCode)) {
				IBAUtils.addIBAConditionLike(query, EPMDocument.class, idx, "PART_CODE", partCode);
			}

			if (!StringUtils.isNull(material)) {
				IBAUtils.addIBAConditionLike(query, EPMDocument.class, idx, "MATERIAL", material);
			}

			if (!StringUtils.isNull(remark)) {
				IBAUtils.addIBAConditionLike(query, EPMDocument.class, idx, "REMARKS", remark);
			}

			if (!StringUtils.isNull(referenceDrwing)) {
				IBAUtils.addIBAConditionLike(query, EPMDocument.class, idx, "REF_NO", referenceDrwing);
			}

			if (!StringUtils.isNull(SPEC)) {
				IBAUtils.addIBAConditionLike(query, EPMDocument.class, idx, "SPEC", SPEC);
			}

			if (!StringUtils.isNull(MAKER)) {
				IBAUtils.addIBAConditionLike(query, EPMDocument.class, idx, "MAKER", MAKER);
			}

			if (!StringUtils.isNull(MASTER_TYPE)) {
				IBAUtils.addIBAConditionLike(query, EPMDocument.class, idx, "MASTER_TYPE", MASTER_TYPE);
			}

			if ("true".equals(latest)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				sc = VersionControlHelper.getSearchCondition(EPMDocument.class, true);
				query.appendWhere(sc, new int[] { idx });

				CommonUtils.addLastVersionCondition(query, idx);
			}

			if (StringUtils.isNull(sort)) {
				sort = "true";
			}

			if (StringUtils.isNull(sortKey)) {
				// sortKey = WTAttributeNameIfc.CREATE_STAMP_NAME;
				sortKey = WTAttributeNameIfc.MODIFY_STAMP_NAME;
			}

			ca = new ClassAttribute(EPMDocument.class, sortKey);
			OrderBy orderBy = new OrderBy(ca, Boolean.parseBoolean(sort));
			query.appendOrderBy(orderBy, new int[] { idx });

			PageQueryUtils pager = new PageQueryUtils(param, query);
			PagingQueryResult result = pager.find();

			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				EPMDocument epm = (EPMDocument) obj[0];

				if (epm.getContainer().getName().equalsIgnoreCase("Commonspace")) {
					EpmProductColumnData data = new EpmProductColumnData(epm);
					list.add(data);
				} else if (epm.getContainer().getName().equalsIgnoreCase("LIBRARY")) {
					EpmLibraryColumnData data = new EpmLibraryColumnData(epm);
					list.add(data);
				}
			}

			map.put("list", list);
			map.put("lastPage", pager.getLastPage());
			map.put("topListCount", pager.getTotal());
			map.put("sessionid", pager.getSessionId());
			map.put("curPage", pager.getCpage());
			map.put("total", pager.getTotalSize());
			map.put("result", "SUCCESS");
		} catch (Exception e) {
			map.put("result", "FAIL");
			e.printStackTrace();
		}
		return map;
	}

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

	public EPMDocument getLatestEPM(String number) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(EPMDocument.class, true);
		SearchCondition sc = new SearchCondition(EPMDocument.class, EPMDocument.NUMBER, "=",
				number.toUpperCase().trim());
		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();

		sc = VersionControlHelper.getSearchCondition(EPMDocument.class, true);
		query.appendWhere(sc, new int[] { idx });

		CommonUtils.addLastVersionCondition(query, idx);

		QueryResult result = PersistenceHelper.manager.find(query);
		EPMDocument e = null;
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			e = (EPMDocument) obj[0];
		}
		return e;
	}

	public EPMDocument getLatestEPMByName(String name) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(EPMDocument.class, true);
		SearchCondition sc = new SearchCondition(EPMDocument.class, EPMDocument.NAME, "=", name.toUpperCase().trim());
		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();

		sc = VersionControlHelper.getSearchCondition(EPMDocument.class, true);
		query.appendWhere(sc, new int[] { idx });

		CommonUtils.addLastVersionCondition(query, idx);

		QueryResult result = PersistenceHelper.manager.find(query);
		EPMDocument e = null;
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			e = (EPMDocument) obj[0];
		}
		return e;
	}

	public Map<String, Object> checkDrawing(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		// String oid = (String) param.get("oid");

		List<String> list = (List<String>) param.get("list");

		ReferenceFactory rf = new ReferenceFactory();
		EPMDocument epm = null;
		boolean is2D = false;
		try {

			for (String oid : list) {
				epm = (EPMDocument) rf.getReference(oid).getObject();

				if (epm.getDocType().toString().equals("CADDRAWING")) {
					is2D = true;
					break;
				}
			}

			if (is2D) {
				map.put("msg", "도면 검증에 실패 하였습니다\n시스템 관리자에게 문의하세요.");
			} else {
				map.put("msg", "2D 도면 파일이 아니여서 출력이 불가능합니다.");
			}

			map.put("is2D", is2D);
		} catch (Exception e) {
			map.put("msg", "도면 검증에 실패 하였습니다\n시스템 관리자에게 문의하세요.");
			e.printStackTrace();
		}
		return map;
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
		Map<String, Object> map = new HashMap<String, Object>();
		List<EpmDTO> list = new ArrayList<EpmDTO>();
		QuerySpec query = new QuerySpec();

		int idx = query.appendClassList(EPMDocument.class, true);
		int idx_m = query.appendClassList(EPMDocumentMaster.class, false);

		QuerySpecUtils.toCI(query, idx, EPMDocument.class);
		QuerySpecUtils.toInnerJoin(query, EPMDocument.class, EPMDocumentMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);
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
		return map;
	}

	//버전이력
	public JSONArray history(Mastered master) throws Exception {
		ArrayList<EpmDTO> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(EPMDocument.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, EPMDocument.class, "masterReference.key.id", master.getPersistInfo().getObjectIdentifier().getId());
		QueryResult result = PersistenceHelper.manager.find(query);
		while(result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			EPMDocument epm = (EPMDocument) obj[0];
			EpmDTO dto = new EpmDTO(epm);
			list.add(dto);
		}
		return JSONArray.fromObject(list);
	}

	//작번
	public JSONArray jsonArrayAui(String oid) throws Exception {
		
		return null;
	}
}
