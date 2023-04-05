package e3ps.bom.partlist.service;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import e3ps.bom.partlist.MasterDataLink;
import e3ps.bom.partlist.PartListData;
import e3ps.bom.partlist.PartListMaster;
import e3ps.bom.partlist.PartListMasterProjectLink;
import e3ps.bom.partlist.dto.PartListDTO;
import e3ps.bom.partlist.dto.PartListDataDTO;
import e3ps.bom.partlist.dto.PartListDataViewData;
import e3ps.bom.tbom.dto.TBOMMasterDTO;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.DateUtils;
import e3ps.common.util.IBAUtils;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.doc.meeting.Meeting;
import e3ps.doc.meeting.MeetingProjectLink;
import e3ps.epm.workOrder.WorkOrderProjectLink;
import e3ps.epm.workOrder.dto.WorkOrderDTO;
import e3ps.org.People;
import e3ps.project.Project;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.query.ClassAttribute;
import wt.query.ColumnExpression;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SQLFunction;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;
import wt.util.WTProperties;
import wt.vc.VersionControlHelper;
import wt.vc.wip.WorkInProgressHelper;

public class PartlistHelper {

	public static final PartlistService service = ServiceFactory.getService(PartlistService.class);
	public static final PartlistHelper manager = new PartlistHelper();

	public static String excelFormLoc;
	static {
		try {
			excelFormLoc = WTProperties.getLocalProperties().getProperty("wt.codebase.location") + File.separator
					+ "jsp" + File.separator + "temp" + File.separator + "pdm" + File.separator + "excelForm";

			File loc = new File(excelFormLoc);

			if (!loc.exists()) {
				loc.mkdirs();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArrayList<PartListMaster> findPartListByProject(Project project, String engType, String pname)
			throws Exception {

		ArrayList<PartListMaster> list = new ArrayList<PartListMaster>();
		QuerySpec query = null;

		try {
			query = new QuerySpec();
			int idx = query.appendClassList(PartListMaster.class, true);
			int idx_link = query.appendClassList(PartListMasterProjectLink.class, false);
			int idx_p = query.appendClassList(Project.class, false);

			SearchCondition sc = null;
			ClassAttribute ca = null;

			ClassAttribute roleAca = new ClassAttribute(PartListMaster.class, WTAttributeNameIfc.ID_NAME);
			ClassAttribute roleBca = new ClassAttribute(Proect.class, WTAttributeNameIfc.ID_NAME);

			sc = new SearchCondition(new ClassAttribute(PartListMasterProjectLink.class, "roleAObjectRef.key.id"), "=",
					roleAca);
			query.appendWhere(sc, new int[] { idx_link, idx });
			query.appendAnd();

			sc = new SearchCondition(new ClassAttribute(PartListMasterProjectLink.class, "roleBObjectRef.key.id"), "=",
					roleBca);
			query.appendWhere(sc, new int[] { idx_link, idx_p });
			query.appendAnd();

			sc = new SearchCondition(PartListMasterProjectLink.class, "roleBObjectRef.key.id", "=",
					project.getPersistInfo().getObjectIdentifier().getId());
			query.appendWhere(sc, new int[] { idx_link, idx_p });
			query.appendAnd();

			if (engType.equals("기계")) {
				query.appendOpenParen();
				sc = new SearchCondition(PartListMaster.class, PartListMaster.ENG_TYPE, "=", engType + "_1차_수배");
				query.appendWhere(sc, new int[] { idx });
				query.appendOr();
				sc = new SearchCondition(PartListMaster.class, PartListMaster.ENG_TYPE, "=", engType + "_2차_수배");
				query.appendWhere(sc, new int[] { idx });
				query.appendCloseParen();
			} else if ("전기".equals(engType)) {
				query.appendOpenParen();
				sc = new SearchCondition(PartListMaster.class, PartListMaster.ENG_TYPE, "=", engType + "_1차_수배");
				query.appendWhere(sc, new int[] { idx });
				query.appendOr();
				sc = new SearchCondition(PartListMaster.class, PartListMaster.ENG_TYPE, "=", engType + "_2차_수배");
				query.appendWhere(sc, new int[] { idx });
				query.appendCloseParen();
			}

			ca = new ClassAttribute(PartListMaster.class, PartListMaster.MODIFY_TIMESTAMP);
			OrderBy by = new OrderBy(ca, true);
			query.appendOrderBy(by, new int[] { idx });

			QueryResult result = PersistenceHelper.manager.find(query);
			System.out.println("query=" + query);
			System.out.println("result=" + result.size());
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				PartListMaster master = (PartListMaster) obj[0];
				list.add(master);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public Map<String, Object> findPartList(Map<String, Object> param) throws Exception {

		Map<String, Object> map = new HashMap<String, Object>();
		List<TBOMMasterDTO> list = new ArrayList<TBOMMasterDTO>();
		QuerySpec query = null;

		// search param
		String kekNumber = (String) param.get("kekNumber");
		String keNumber = (String) param.get("keNumber");
		String description = (String) param.get("description");
		String engType = (String) param.get("engType");
		String mak = (String) param.get("mak");
		String pDescription = (String) param.get("pDescription");
		String creatorsOid = (String) param.get("creatorsOid");

		String name = (String) param.get("name");

		String predate = (String) param.get("predate");
		String postdate = (String) param.get("postdate");

		String modifierOid = (String) param.get("modifierOid");

		String predate_m = (String) param.get("predate_m");
		String postdate_m = (String) param.get("postdate_m");

		String statesDoc = (String) param.get("statesDoc");

		ReferenceFactory rf = new ReferenceFactory();

		try {
			query = new QuerySpec();
			int idx = query.appendClassList(PartListMaster.class, true);
			int idx_link = query.appendClassList(PartListMasterProjectLink.class, true);
			int idx_p = query.appendClassList(Project.class, true);

			SearchCondition sc = null;
			ClassAttribute ca = null;

			ClassAttribute roleAca = new ClassAttribute(PartListMaster.class, WTAttributeNameIfc.ID_NAME);
			ClassAttribute roleBca = new ClassAttribute(Project.class, WTAttributeNameIfc.ID_NAME);

			sc = new SearchCondition(new ClassAttribute(PartListMasterProjectLink.class, "roleAObjectRef.key.id"), "=",
					roleAca);
			query.appendWhere(sc, new int[] { idx_link, idx });
			query.appendAnd();
			sc = new SearchCondition(new ClassAttribute(PartListMasterProjectLink.class, "roleBObjectRef.key.id"), "=",
					roleBca);
			query.appendWhere(sc, new int[] { idx_link, idx_p });

			if (!StringUtils.isNull(name)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(PartListMaster.class, PartListMaster.NAME);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(name);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(description)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(PartListMaster.class, PartListMaster.DESCRIPTION);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(description);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(creatorsOid)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				People user = (People) rf.getReference(creatorsOid).getObject();
				long ids = user.getUser().getPersistInfo().getObjectIdentifier().getId();
				sc = new SearchCondition(PartListMaster.class, "creator.key.id", SearchCondition.EQUAL, ids);
				query.appendWhere(sc, new int[] { idx });
			}

			// 수정자
			if (!StringUtils.isNull(modifierOid)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				People user = (People) rf.getReference(modifierOid).getObject();
				long ids = user.getUser().getPersistInfo().getObjectIdentifier().getId();
				sc = new SearchCondition(PartListMaster.class, "ownership.owner.key.id", SearchCondition.EQUAL, ids);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(statesDoc)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				sc = new SearchCondition(PartListMaster.class, "state.state", SearchCondition.EQUAL, statesDoc);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(predate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp start = DateUtils.convertStartDate(predate);
				sc = new SearchCondition(PartListMaster.class, WTAttributeNameIfc.CREATE_STAMP_NAME,
						SearchCondition.GREATER_THAN_OR_EQUAL, start);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(postdate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp end = DateUtils.convertEndDate(postdate);
				sc = new SearchCondition(PartListMaster.class, WTAttributeNameIfc.CREATE_STAMP_NAME,
						SearchCondition.LESS_THAN_OR_EQUAL, end);
				query.appendWhere(sc, new int[] { idx });
			}

			// 수정일
			if (!StringUtils.isNull(predate_m)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp start = DateUtils.convertStartDate(predate_m);
				sc = new SearchCondition(PartListMaster.class, WTAttributeNameIfc.MODIFY_STAMP_NAME,
						SearchCondition.GREATER_THAN_OR_EQUAL, start);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(postdate_m)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp end = DateUtils.convertEndDate(postdate_m);
				sc = new SearchCondition(PartListMaster.class, WTAttributeNameIfc.MODIFY_STAMP_NAME,
						SearchCondition.LESS_THAN_OR_EQUAL, end);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(kekNumber)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				ca = new ClassAttribute(Project.class, Project.KEK_NUMBER);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(kekNumber);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx_p });
			}

			if (!StringUtils.isNull(pDescription)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				ca = new ClassAttribute(Project.class, Project.DESCRIPTION);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(pDescription);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx_p });
			}

			if (!StringUtils.isNull(mak)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				ca = new ClassAttribute(Project.class, Project.MAK);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(mak);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx_p });
			}

			if (!StringUtils.isNull(keNumber)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				ca = new ClassAttribute(Project.class, Project.KE_NUMBER);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(keNumber);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx_p });
			}

			if (!StringUtils.isNull(engType)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(Project.class, Project.P_TYPE);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(engType);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx_p });
			}

			ca = new ClassAttribute(PartListMaster.class, PartListMaster.MODIFY_TIMESTAMP);
			OrderBy by = new OrderBy(ca, true);
			query.appendOrderBy(by, new int[] { idx });

			PageQueryUtils pager = new PageQueryUtils(param, query);
			PagingQueryResult result = pager.find();
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				PartListMaster master = (PartListMaster) obj[0];
				Project project = (Project) obj[2];
				TBOMMasterDTO data = new TBOMMasterDTO(master, project);
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

	public ArrayList<PartListData> getPartListData(PartListMaster master) throws Exception {
		ArrayList<PartListData> list = new ArrayList<PartListData>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(MasterDataLink.class, true);

		long ids = master.getPersistInfo().getObjectIdentifier().getId();

		SearchCondition sc = new SearchCondition(MasterDataLink.class, "roleAObjectRef.key.id", "=", ids);
		query.appendWhere(sc, new int[] { idx });

		ClassAttribute ca = new ClassAttribute(MasterDataLink.class, MasterDataLink.SORT);
		OrderBy by = new OrderBy(ca, false);
		query.appendOrderBy(by, new int[] { idx });

		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			MasterDataLink link = (MasterDataLink) obj[0];
			list.add(link.getPartListData());
		}
		return list;
	}

	public WTPart getPartByYCode(Map<String, Object> param) throws Exception {
		String yCode = (String) param.get("yCode");
		WTPart part = null;

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTPart.class, true);
		int master = query.appendClassList(WTPartMaster.class, false);

		SearchCondition sc = null;
//		ClassAttribute ca = null;

		sc = WorkInProgressHelper.getSearchCondition_CI(WTPart.class);
		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();

		sc = new SearchCondition(WTPart.class, "masterReference.key.id", WTPartMaster.class,
				"thePersistInfo.theObjectIdentifier.id");
		query.appendWhere(sc, new int[] { idx, master });

		if (!StringUtils.isNull(yCode)) {
			IBAUtils.addIBAConditionLike(query, WTPart.class, idx, "PART_CODE", yCode);
		}

		if (query.getConditionCount() > 0)
			query.appendAnd();
		sc = VersionControlHelper.getSearchCondition(WTPart.class, true);
		query.appendWhere(sc, new int[] { idx });

		CommonUtils.addLastVersionCondition(query, idx);

		QueryResult result = PersistenceHelper.manager.find(query);

		System.out.println("query=" + query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			part = (WTPart) obj[0];
		}
		return part;
	}

	/**
	 * 수배표 가져오는 함수
	 */
	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(PartListMaster.class, true);

		QuerySpecUtils.toOrderBy(query, idx, PartListMaster.class, PartListMaster.CREATE_TIMESTAMP, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();

		net.sf.json.JSONArray list = new net.sf.json.JSONArray();

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			PartListMaster master = (PartListMaster) obj[0];

			JSONObject node = new JSONObject();
			node.put("oid", master.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("name", master.getName());
			QueryResult group = PersistenceHelper.manager.navigate(master, "project", PartListMasterProjectLink.class,
					false);

//			QuerySpec qs = new QuerySpec();
//			int idx_m = qs.appendClassList(PartListMaster.class, true);
//			int idx_link = qs.appendClassList(PartListMasterProjectLink.class, true);
//			QuerySpecUtils.toInnerJoin(qs, PartListMaster.class, PartListMasterProjectLink.class,
//					WTAttributeNameIfc.ID_NAME, "roleAObjectRef.key.id", idx_m, idx_link);
//			QuerySpecUtils.toEqualsAnd(qs, idx_link, PartListMasterProjectLink.class, "roleAObjectRef.key.id",
//					master.getPersistInfo().getObjectIdentifier().getId());
//			QueryResult group = PersistenceHelper.manager.find(qs);

			int isNode = 1;
			net.sf.json.JSONArray children = new net.sf.json.JSONArray();
			while (group.hasMoreElements()) {
				PartListMasterProjectLink link = (PartListMasterProjectLink) group.nextElement();
				PartListDTO dto = new PartListDTO(link);
				if (isNode == 1) {
					node.put("poid", dto.getPoid());
					node.put("loid", link.getPersistInfo().getObjectIdentifier().getStringValue());
					node.put("projectType_name", dto.getProjectType_name());
					node.put("customer_name", dto.getCustomer_name());
					node.put("install_name", dto.getInstall_name());
					node.put("mak_name", dto.getMak_name());
					node.put("detail_name", dto.getDetail_name());
					node.put("kekNumber", dto.getKekNumber());
					node.put("keNumber", dto.getKeNumber());
					node.put("userId", dto.getUserId());
					node.put("description", dto.getDescription());
					node.put("state", dto.getState());
					node.put("model", dto.getModel());
					node.put("pdate_txt", dto.getPdate_txt());
					node.put("creator", dto.getCreator());
					node.put("createdDate_txt", dto.getCreatedDate_txt());
					node.put("modifiedDate_txt", dto.getModifiedDate_txt());
				} else {
					JSONObject data = new JSONObject();
					data.put("name", dto.getName());
					data.put("oid", dto.getOid());
					data.put("poid", dto.getPoid());
					data.put("loid", link.getPersistInfo().getObjectIdentifier().getStringValue());
					data.put("projectType_name", dto.getProjectType_name());
					data.put("customer_name", dto.getCustomer_name());
					data.put("install_name", dto.getInstall_name());
					data.put("mak_name", dto.getMak_name());
					data.put("detail_name", dto.getDetail_name());
					data.put("kekNumber", dto.getKekNumber());
					data.put("keNumber", dto.getKeNumber());
					data.put("userId", dto.getUserId());
					data.put("description", dto.getDescription());
					data.put("state", dto.getState());
					data.put("model", dto.getModel());
					data.put("pdate_txt", dto.getPdate_txt());
					data.put("creator", dto.getCreator());
					data.put("createdDate_txt", dto.getCreatedDate_txt());
					data.put("modifiedDate_txt", dto.getModifiedDate_txt());
					children.add(data);
				}
				isNode++;
			}
			node.put("children", children);
			list.add(node);
		}
		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	/**
	 * 수배표된 데이터들을 JSONArray 형태로 가져오는 함수
	 */
	public JSONArray getData(String oid) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		PartListMaster master = (PartListMaster) CommonUtils.getObject(oid);
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(MasterDataLink.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, MasterDataLink.class, "roleAObjectRef.key.id",
				master.getPersistInfo().getObjectIdentifier().getId());
		QuerySpecUtils.toOrderBy(query, idx, MasterDataLink.class, MasterDataLink.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			MasterDataLink link = (MasterDataLink) obj[0];
			PartListData data = link.getData();

			Map<String, Object> map = new HashMap<>();
			map.put("lotNo", data.getLotNo());
			map.put("unitName", data.getUnitName());
			map.put("partNo", data.getPartNo());
			map.put("partName", data.getPartName());
			map.put("standard", data.getStandard());
			map.put("maker", data.getMaker());
			map.put("customer", data.getCustomer());
			map.put("quantity", data.getQuantity());
			map.put("unit", data.getUnit());
			map.put("price", data.getPrice());
			map.put("currency", data.getCurrency());
			map.put("won", data.getWon());
			map.put("partListDate", data.getPartListDate());
			map.put("exchangeRate", data.getExchangeRate());
			map.put("referDrawing", data.getReferDrawing());
			map.put("classification", data.getClassification());
			map.put("note", data.getNote());
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 수배표된 데이터들을 ArrayList<Map<String, Object>> 형태로 가져오는 함수
	 */
	public ArrayList<Map<String, Object>> getArrayMap(String oid) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		PartListMaster master = (PartListMaster) CommonUtils.getObject(oid);
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(MasterDataLink.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, MasterDataLink.class, "roleAObjectRef.key.id",
				master.getPersistInfo().getObjectIdentifier().getId());
		QuerySpecUtils.toOrderBy(query, idx, MasterDataLink.class, MasterDataLink.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			MasterDataLink link = (MasterDataLink) obj[0];
			PartListData data = link.getData();

			Map<String, Object> map = new HashMap<>();
			map.put("lotNo", data.getLotNo());
			map.put("unitName", data.getUnitName());
			map.put("partNo", data.getPartNo());
			map.put("partName", data.getPartName());
			map.put("standard", data.getStandard());
			map.put("maker", data.getMaker());
			map.put("customer", data.getCustomer());
			map.put("quantity", data.getQuantity());
			map.put("unit", data.getUnit());
			map.put("price", data.getPrice());
			map.put("currency", data.getCurrency());
			map.put("won", data.getWon());
			map.put("partListDate", data.getPartListDate());
			map.put("exchangeRate", data.getExchangeRate());
			map.put("referDrawing", data.getReferDrawing());
			map.put("classification", data.getClassification());
			map.put("note", data.getNote());
			list.add(map);
		}
		return list;
	}

	/**
	 * 수배표 관련 작번 리스트
	 */
	public JSONArray jsonAuiProject(String oid) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();
		PartListMaster master = (PartListMaster) CommonUtils.getObject(oid);

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(PartListMaster.class, true);
		int idx_link = query.appendClassList(PartListMasterProjectLink.class, true);
		QuerySpecUtils.toInnerJoin(query, PartListMaster.class, PartListMasterProjectLink.class,
				WTAttributeNameIfc.ID_NAME, "roleAObjectRef.key.id", idx, idx_link);
		QuerySpecUtils.toEqualsAnd(query, idx_link, PartListMasterProjectLink.class, "roleAObjectRef.key.id", master);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			PartListMasterProjectLink link = (PartListMasterProjectLink) obj[1];
			Project project = link.getProject();
			Map<String, String> map = new HashMap<>();
			map.put("oid", project.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("projectType_name", project.getProjectType() != null ? project.getProjectType().getName() : "");
			map.put("customer_name", project.getCustomer() != null ? project.getCustomer().getName() : "");
			map.put("mak_name", project.getMak() != null ? project.getMak().getName() : "");
			map.put("detail_name", project.getDetail() != null ? project.getDetail().getName() : "");
			map.put("kekNumber", project.getKekNumber());
			map.put("keNumber", project.getKeNumber());
			map.put("description", project.getDescription());
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 수배표 비교
	 */
	public ArrayList<Map<String, Object>> compare(Project p1, ArrayList<Project> destList, String invoke)
			throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		ArrayList<Map<String, Object>> mergedList = new ArrayList<>();
		String[] t = null;
		if ("a".equals(invoke)) {
			t = new String[] { "기계_1차_수배", "기계_2차_수배", "전기_1차_수배", "전기_2차_수배" };
			list = integratedData(p1, t);
		} else if ("m".equals(invoke)) {
			t = new String[] { "기계_1차_수배", "기계_2차_수배" };
			list = integratedData(p1, t);
		} else if ("e".equals(invoke)) {
			t = new String[] { "전기_1차_수배", "전기_2차_수배" };
			list = integratedData(p1, t);
		}

		// list1의 데이터를 먼저 추가
		for (Map<String, Object> data : list) {
			Map<String, Object> mergedData = new HashMap<>();
			mergedData.put("lotNo", data.get("lotNo"));
			mergedData.put("unitName", data.get("unitName"));
			mergedData.put("partNo", data.get("partNo"));
			mergedData.put("partName", data.get("partName"));
			mergedData.put("standard", data.get("standard"));
			mergedData.put("maker", data.get("maker"));
			mergedData.put("customer", data.get("customer"));
			mergedData.put("quantity1", data.get("quantity"));
			// 작번 개수 만큼 입력..
//				for (int i = 0; i < destList.size(); i++) {
//					mergedData.put("quantity" + (2 + i), 0);
//				}
			mergedData.put("unit", data.get("unit"));
			mergedData.put("price", data.get("price"));
			mergedData.put("currency", data.get("currency"));
			mergedData.put("won1", data.get("won"));
			mergedData.put("partListDate_txt", data.get("partListDate_txt"));
			mergedData.put("exchangeRate", data.get("exchangeRate"));
			mergedData.put("referDrawing", data.get("referDrawing"));
			mergedData.put("classification", data.get("classification"));
			mergedData.put("note", data.get("note"));
			mergedList.add(mergedData);
		}

		// 전체 작번 START
		for (int i = 0; i < destList.size(); i++) {
			Project p2 = (Project) destList.get(i);
			ArrayList<Map<String, Object>> _list = integratedData(p2, t);
			for (Map<String, Object> data : _list) {
				String partNo = (String) data.get("partNo");
				String lotNo = (String) data.get("lotNo");
				String key = partNo + "-" + lotNo;
//			String key = partNo;
				boolean isExist = false;

				// mergedList에 partNo가 동일한 데이터가 있는지 확인
				for (Map<String, Object> mergedData : mergedList) {
					String mergedPartNo = (String) mergedData.get("partNo1");
					String mergedLotNo = (String) mergedData.get("lotNo1");
					String _key = mergedPartNo + "-" + mergedLotNo;

					if (key.equals(_key)) {
						// partNo가 동일한 데이터가 있으면 데이터를 업데이트하고 isExist를 true로 변경
						mergedData.put("lotNo2", data.get("lotNo"));
						mergedData.put("unitName", data.get("unitName"));
						mergedData.put("partNo2", data.get("partNo"));
						mergedData.put("partName", data.get("partName"));
						mergedData.put("standard", data.get("standard"));
						mergedData.put("maker", data.get("maker"));
						mergedData.put("customer", data.get("customer"));
						mergedData.put("quantity2", data.get("quantity"));
						mergedData.put("unit", data.get("unit"));
						mergedData.put("price", data.get("price"));
						mergedData.put("currency", data.get("currency"));
						mergedData.put("won2", data.get("won"));
						mergedData.put("partListDate_txt", data.get("partListDate_txt"));
						mergedData.put("exchangeRate", data.get("exchangeRate"));
						mergedData.put("referDrawing", data.get("referDrawing"));
						mergedData.put("classification", data.get("classification"));
						mergedData.put("note", data.get("note"));
						isExist = true;
						break;
					}
				}

				if (!isExist) {
					// partNo가 동일한 데이터가 없으면 mergedList에 데이터를 추가
					Map<String, Object> mergedData = new HashMap<>();
					mergedData.put("partNo1", "");
					mergedData.put("lotNo1", "");
					mergedData.put("partNo2", data.get("partNo"));
					mergedData.put("lotNo2", data.get("lotNo"));
					mergedData.put("quantity2", data.get("quantity"));
					mergedData.put("won2", data.get("won"));
					mergedData.put("partName", data.get("partName"));
					mergedData.put("standard", data.get("standard"));
					mergedData.put("maker", data.get("maker"));
					mergedData.put("customer", data.get("customer"));
					mergedData.put("unit", data.get("unit"));
					mergedData.put("price", data.get("price"));
					mergedData.put("currency", data.get("currency"));
					mergedData.put("won2", data.get("won"));
					mergedData.put("partListDate_txt", data.get("partListDate_txt"));
					mergedData.put("exchangeRate", data.get("exchangeRate"));
					mergedData.put("referDrawing", data.get("referDrawing"));
					mergedData.put("classification", data.get("classification"));
					mergedData.put("note", data.get("note"));
					mergedList.add(mergedData);
				}
			}
		}
		return mergedList;
	}

	/**
	 * 비교할 데이터 가져오기
	 */
	private ArrayList<Map<String, Object>> compareData(String oid, String sort) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		PartListMaster master = (PartListMaster) CommonUtils.getObject(oid);

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(PartListMaster.class, false);
		int idx_link = query.appendClassList(MasterDataLink.class, true);
		int idx_data = query.appendClassList(PartListData.class, false);
		QuerySpecUtils.toInnerJoin(query, PartListMaster.class, MasterDataLink.class, WTAttributeNameIfc.ID_NAME,
				"roleAObjectRef.key.id", idx, idx_link);
		QuerySpecUtils.toInnerJoin(query, PartListData.class, MasterDataLink.class, WTAttributeNameIfc.ID_NAME,
				"roleBObjectRef.key.id", idx_data, idx_link);
		QuerySpecUtils.toEqualsAnd(query, idx_link, MasterDataLink.class, "roleAObjectRef.key.id", master);
//		if("sort".equals(sort)) {
//			QuerySpecUtils.toOrderBy(query, idx_link, MasterDataLink.class, sort, false);
//		} else {
		QuerySpecUtils.toOrderBy(query, idx_data, PartListData.class, sort, false);
//		}

		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			MasterDataLink link = (MasterDataLink) obj[0];
			PartListData data = link.getData();
			Map<String, Object> map = new HashMap<>();
			map.put("lotNo", String.valueOf(data.getLotNo()));
			map.put("unitName", data.getUnitName());
			map.put("partNo", data.getPartNo());
			map.put("partName", data.getPartName());
			map.put("standard", data.getStandard());
			map.put("maker", data.getMaker());
			map.put("customer", data.getCustomer());
			map.put("quantity", data.getQuantity());
			map.put("unit", data.getUnit());
			map.put("price", data.getPrice());
			map.put("currency", data.getCurrency());
			map.put("won", data.getWon());
			map.put("partListDate_txt", CommonUtils.getPersistableTime(data.getPartListDate()));
			map.put("exchangeRate", data.getExchangeRate());
			map.put("referDrawing", data.getReferDrawing());
			map.put("classification", data.getClassification());
			map.put("note", data.getNote());
			list.add(map);
		}

		return list;
	}

	/**
	 * // * 수배표 비교 데이터
	 */
	public ArrayList<Map<String, Object>> integratedData(Project project, String[] t) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(PartListMaster.class, true);
		int idx_link = query.appendClassList(PartListMasterProjectLink.class, false);
		int idx_p = query.appendClassList(Project.class, false);

		QuerySpecUtils.toInnerJoin(query, PartListMaster.class, PartListMasterProjectLink.class,
				WTAttributeNameIfc.ID_NAME, "roleAObjectRef.key.id", idx, idx_link);
		QuerySpecUtils.toInnerJoin(query, Project.class, PartListMasterProjectLink.class, WTAttributeNameIfc.ID_NAME,
				"roleBObjectRef.key.id", idx_p, idx_link);
		QuerySpecUtils.toEqualsAnd(query, idx_link, PartListMasterProjectLink.class, "roleBObjectRef.key.id", project);
		if (t != null && t.length > 0) {
			QuerySpecUtils.toIn(query, idx, PartListMaster.class, PartListMaster.ENG_TYPE, t);
		}
		QuerySpecUtils.toOrderBy(query, idx, PartListMaster.class, PartListMaster.CREATE_TIMESTAMP, false);

		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			PartListMaster master = (PartListMaster) obj[0];

			QuerySpec _query = new QuerySpec();
			int _idx = _query.appendClassList(PartListMaster.class, false);
			int _idx_link = _query.appendClassList(MasterDataLink.class, true);
			int idx_data = _query.appendClassList(PartListData.class, false);
			QuerySpecUtils.toInnerJoin(_query, PartListMaster.class, MasterDataLink.class, WTAttributeNameIfc.ID_NAME,
					"roleAObjectRef.key.id", _idx, _idx_link);
			QuerySpecUtils.toInnerJoin(_query, PartListData.class, MasterDataLink.class, WTAttributeNameIfc.ID_NAME,
					"roleBObjectRef.key.id", idx_data, _idx_link);
			QuerySpecUtils.toEqualsAnd(_query, _idx_link, MasterDataLink.class, "roleAObjectRef.key.id", master);
			QuerySpecUtils.toOrderBy(_query, idx_data, PartListData.class, PartListData.SORT, false);
			QueryResult qr = PersistenceHelper.manager.find(_query);
			while (qr.hasMoreElements()) {
				Object[] oo = (Object[]) qr.nextElement();
				MasterDataLink link = (MasterDataLink) oo[0];
				PartListData data = link.getData();
				Map<String, Object> map = new HashMap<>();
				map.put("lotNo", String.valueOf(data.getLotNo()));
				map.put("unitName", data.getUnitName());
				map.put("partNo", data.getPartNo());
				map.put("partName", data.getPartName());
				map.put("standard", data.getStandard());
				map.put("maker", data.getMaker());
				map.put("customer", data.getCustomer());
				map.put("quantity", data.getQuantity());
				map.put("unit", data.getUnit());
				map.put("price", data.getPrice());
				map.put("currency", data.getCurrency());
				map.put("won", data.getWon());
				map.put("partListDate_txt", CommonUtils.getPersistableTime(data.getPartListDate()));
				map.put("exchangeRate", data.getExchangeRate());
				map.put("referDrawing", data.getReferDrawing());
				map.put("classification", data.getClassification());
				map.put("note", data.getNote());
				list.add(map);
			}
		}
		return list;
	}

	public JSONArray jsonAuiWorkSpaceData(String oid) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();
		PartListMaster master = (PartListMaster) CommonUtils.getObject(oid);
		QueryResult result = PersistenceHelper.manager.navigate(master, "project", PartListMasterProjectLink.class,
				false);
		while (result.hasMoreElements()) {
			PartListMasterProjectLink link = (PartListMasterProjectLink) result.nextElement();
			Project project = link.getProject();
			Map<String, String> map = new HashMap<>();
			map.put("oid", master.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("name", master.getName());
			map.put("state", master.getLifeCycleState().getDisplay());
			map.put("createdDate_txt", CommonUtils.getPersistableTime(master.getModifyTimestamp()));
			map.put("creator", master.getCreatorFullName());
			map.put("projectType_name", project.getProjectType() != null ? project.getProjectType().getName() : "");
			map.put("kekNumber", project.getKekNumber());
			map.put("keNumber", project.getKeNumber());
			map.put("customer_name", project.getCustomer() != null ? project.getCustomer().getName() : "");
			map.put("mak_name", project.getMak() != null ? project.getMak().getName() : "");
			map.put("detail_name", project.getDetail() != null ? project.getDetail().getName() : "");
			map.put("description", project.getDescription());
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 프로젝트 수배표 탭
	 */
	public ArrayList<Map<String, Object>> partlistTab(String oid, String invoke) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		Project project = (Project) CommonUtils.getObject(oid);
		String[] t = null;

		if ("a".equals(invoke)) {
			t = new String[] { "기계_1차_수배", "기계_2차_수배", "전기_1차_수배", "전기_2차_수배" };
		} else if ("m".equals(invoke)) {
			t = new String[] { "기계_1차_수배", "기계_2차_수배" };
		} else if ("e".equals(invoke)) {
			t = new String[] { "전기_1차_수배", "전기_2차_수배" };
		}

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(PartListMaster.class, true);
		int idx_link = query.appendClassList(PartListMasterProjectLink.class, false);
		int idx_p = query.appendClassList(Project.class, false);

		QuerySpecUtils.toInnerJoin(query, PartListMaster.class, PartListMasterProjectLink.class,
				WTAttributeNameIfc.ID_NAME, "roleAObjectRef.key.id", idx, idx_link);
		QuerySpecUtils.toInnerJoin(query, Project.class, PartListMasterProjectLink.class, WTAttributeNameIfc.ID_NAME,
				"roleBObjectRef.key.id", idx_p, idx_link);
		QuerySpecUtils.toEqualsAnd(query, idx_link, PartListMasterProjectLink.class, "roleBObjectRef.key.id", project);
		if (t != null && t.length > 0) {
			QuerySpecUtils.toIn(query, idx, PartListMaster.class, PartListMaster.ENG_TYPE, t);
		}
		QuerySpecUtils.toOrderBy(query, idx, PartListMaster.class, PartListMaster.CREATE_TIMESTAMP, false);

		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			PartListMaster master = (PartListMaster) obj[0];

			QuerySpec _query = new QuerySpec();
			int _idx = _query.appendClassList(PartListMaster.class, false);
			int _idx_link = _query.appendClassList(MasterDataLink.class, true);
			int idx_data = _query.appendClassList(PartListData.class, false);
			QuerySpecUtils.toInnerJoin(_query, PartListMaster.class, MasterDataLink.class, WTAttributeNameIfc.ID_NAME,
					"roleAObjectRef.key.id", _idx, _idx_link);
			QuerySpecUtils.toInnerJoin(_query, PartListData.class, MasterDataLink.class, WTAttributeNameIfc.ID_NAME,
					"roleBObjectRef.key.id", idx_data, _idx_link);
			QuerySpecUtils.toEqualsAnd(_query, _idx_link, MasterDataLink.class, "roleAObjectRef.key.id", master);
			QuerySpecUtils.toOrderBy(_query, idx_data, PartListData.class, PartListData.SORT, false);
			QueryResult qr = PersistenceHelper.manager.find(_query);
			while (qr.hasMoreElements()) {
				Object[] oo = (Object[]) qr.nextElement();
				MasterDataLink link = (MasterDataLink) oo[0];
				PartListData data = link.getData();
				Map<String, Object> map = new HashMap<>();
				map.put("engType", master.getEngType());
				map.put("lotNo", String.valueOf(data.getLotNo()));
				map.put("unitName", data.getUnitName());
				map.put("partNo", data.getPartNo());
				map.put("partName", data.getPartName());
				map.put("standard", data.getStandard());
				map.put("maker", data.getMaker());
				map.put("customer", data.getCustomer());
				map.put("quantity", data.getQuantity());
				map.put("unit", data.getUnit());
				map.put("price", data.getPrice());
				map.put("currency", data.getCurrency());
				map.put("won", data.getWon());
				map.put("partListDate_txt", CommonUtils.getPersistableTime(data.getPartListDate()));
				map.put("exchangeRate", data.getExchangeRate());
				map.put("referDrawing", data.getReferDrawing());
				map.put("classification", data.getClassification());
				map.put("note", data.getNote());
				list.add(map);
			}
		}
		return list;
	}
}
