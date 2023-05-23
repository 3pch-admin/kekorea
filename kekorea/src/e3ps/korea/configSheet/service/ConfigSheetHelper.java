package e3ps.korea.configSheet.service;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.admin.configSheetCode.ConfigSheetCode;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.korea.configSheet.ColumnVariableLink;
import e3ps.korea.configSheet.ConfigSheet;
import e3ps.korea.configSheet.ConfigSheetColumnData;
import e3ps.korea.configSheet.ConfigSheetProjectLink;
import e3ps.korea.configSheet.ConfigSheetVariable;
import e3ps.korea.configSheet.ConfigSheetVariableLink;
import e3ps.korea.configSheet.beans.ConfigSheetDTO;
import e3ps.project.Project;
import e3ps.project.ProjectUserLink;
import e3ps.project.variable.ProjectUserTypeVariable;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;

public class ConfigSheetHelper {

	public static final ConfigSheetHelper manager = new ConfigSheetHelper();
	public static final ConfigSheetService service = ServiceFactory.getService(ConfigSheetService.class);

	/**
	 * CONFIG SHEET 검색
	 */
	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();

		String name = (String) params.get("name");
		String kekNumber = (String) params.get("kekNumber");
		String keNumber = (String) params.get("keNumber");
		String pdateFrom = (String) params.get("pdateFrom");
		String pdateTo = (String) params.get("pdateTo");
		String customer_name = (String) params.get("customer_name");
		String install_name = (String) params.get("install_name");
		String projectType = (String) params.get("projectType");
		String machineOid = (String) params.get("machineOid");
		String elecOid = (String) params.get("elecOid");
		String softOid = (String) params.get("softOid");
		String mak_name = (String) params.get("mak_name");
		String detail_name = (String) params.get("detail_name");
		String description = (String) params.get("description");
		String creatorOid = (String) params.get("creatorOid");
		String createdFrom = (String) params.get("createdFrom");
		String createdTo = (String) params.get("createdTo");
		boolean latest = (boolean) params.get("latest");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ConfigSheet.class, true);
		if (latest) {
			QuerySpecUtils.toBooleanAnd(query, idx, ConfigSheet.class, ConfigSheet.LATEST, true);
		} else {
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			query.appendOpenParen();
			SearchCondition sc = new SearchCondition(ConfigSheet.class, ConfigSheet.LATEST, SearchCondition.IS_TRUE);
			query.appendWhere(sc, new int[] { idx });
			QuerySpecUtils.toBooleanOr(query, idx, ConfigSheet.class, ConfigSheet.LATEST, false);
			query.appendCloseParen();
		}

		QuerySpecUtils.toLikeAnd(query, idx, ConfigSheet.class, ConfigSheet.NAME, name);
		QuerySpecUtils.toCreator(query, idx, ConfigSheet.class, creatorOid);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, ConfigSheet.class, ConfigSheet.CREATE_TIMESTAMP, createdFrom,
				createdTo);

		QuerySpecUtils.toOrderBy(query, idx, ConfigSheet.class, ConfigSheet.CREATE_TIMESTAMP, true);
		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();

		JSONArray list = new JSONArray();

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ConfigSheet configSheet = (ConfigSheet) obj[0];

			QuerySpec _query = new QuerySpec();
			int _idx = _query.appendClassList(ConfigSheet.class, true);
			int _idx_p = _query.appendClassList(Project.class, true);
			int _idx_link = _query.appendClassList(ConfigSheetProjectLink.class, true);

			QuerySpecUtils.toEqualsAnd(_query, _idx_link, ConfigSheetProjectLink.class, "roleAObjectRef.key.id",
					configSheet);
			QuerySpecUtils.toInnerJoin(_query, ConfigSheet.class, ConfigSheetProjectLink.class,
					WTAttributeNameIfc.ID_NAME, "roleAObjectRef.key.id", _idx, _idx_link);
			QuerySpecUtils.toInnerJoin(_query, Project.class, ConfigSheetProjectLink.class, WTAttributeNameIfc.ID_NAME,
					"roleBObjectRef.key.id", _idx_p, _idx_link);
			QuerySpecUtils.toLikeAnd(_query, _idx_p, Project.class, Project.KEK_NUMBER, kekNumber);
			QuerySpecUtils.toLikeAnd(_query, _idx_p, Project.class, Project.KE_NUMBER, keNumber);
			QuerySpecUtils.toTimeGreaterAndLess(_query, _idx_p, Project.class, Project.P_DATE, pdateFrom, pdateTo);

			if (!StringUtils.isNull(customer_name)) {
				CommonCode customerCode = (CommonCode) CommonUtils.getObject(customer_name);
				QuerySpecUtils.toEqualsAnd(_query, _idx_p, Project.class, "customerReference.key.id", customerCode);
			}

			if (!StringUtils.isNull(install_name)) {
				CommonCode installCode = (CommonCode) CommonUtils.getObject(install_name);
				QuerySpecUtils.toEqualsAnd(_query, _idx_p, Project.class, "installReference.key.id", installCode);
			}

			if (!StringUtils.isNull(projectType)) {
				CommonCode projectTypeCode = (CommonCode) CommonUtils.getObject(projectType);
				QuerySpecUtils.toEqualsAnd(_query, _idx_p, Project.class, "projectTypeReference.key.id",
						projectTypeCode);
			}

			if (!StringUtils.isNull(machineOid)) {
				WTUser machine = (WTUser) CommonUtils.getObject(machineOid);
				CommonCode machineCode = CommonCodeHelper.manager.getCommonCode(ProjectUserTypeVariable.MACHINE,
						"USER_TYPE");
				int idx_plink = _query.appendClassList(ProjectUserLink.class, false);
				int idx_u = _query.appendClassList(WTUser.class, false);

				QuerySpecUtils.toInnerJoin(_query, Project.class, ProjectUserLink.class, WTAttributeNameIfc.ID_NAME,
						"roleAObjectRef.key.id", _idx_p, idx_plink);
				QuerySpecUtils.toInnerJoin(_query, WTUser.class, ProjectUserLink.class, WTAttributeNameIfc.ID_NAME,
						"roleBObjectRef.key.id", idx_u, idx_plink);
				QuerySpecUtils.toEqualsAnd(_query, idx_plink, ProjectUserLink.class, "roleBObjectRef.key.id", machine);
				QuerySpecUtils.toEqualsAnd(_query, idx_plink, ProjectUserLink.class, "userTypeReference.key.id",
						machineCode);
			}

			if (!StringUtils.isNull(elecOid)) {
				WTUser elec = (WTUser) CommonUtils.getObject(elecOid);
				CommonCode elecCode = CommonCodeHelper.manager.getCommonCode(ProjectUserTypeVariable.ELEC, "USER_TYPE");
				int idx_plink = _query.appendClassList(ProjectUserLink.class, false);
				int idx_u = _query.appendClassList(WTUser.class, false);

				QuerySpecUtils.toInnerJoin(_query, Project.class, ProjectUserLink.class, WTAttributeNameIfc.ID_NAME,
						"roleAObjectRef.key.id", _idx_p, idx_plink);
				QuerySpecUtils.toInnerJoin(_query, WTUser.class, ProjectUserLink.class, WTAttributeNameIfc.ID_NAME,
						"roleBObjectRef.key.id", idx_u, idx_plink);
				QuerySpecUtils.toEqualsAnd(_query, idx_plink, ProjectUserLink.class, "roleBObjectRef.key.id", elec);
				QuerySpecUtils.toEqualsAnd(_query, idx_plink, ProjectUserLink.class, "userTypeReference.key.id",
						elecCode);
			}

			if (!StringUtils.isNull(softOid)) {
				WTUser soft = (WTUser) CommonUtils.getObject(softOid);
				CommonCode softCode = CommonCodeHelper.manager.getCommonCode(ProjectUserTypeVariable.SOFT, "USER_TYPE");
				int idx_plink = _query.appendClassList(ProjectUserLink.class, false);
				int idx_u = _query.appendClassList(WTUser.class, false);

				QuerySpecUtils.toInnerJoin(_query, Project.class, ProjectUserLink.class, WTAttributeNameIfc.ID_NAME,
						"roleAObjectRef.key.id", _idx_p, idx_plink);
				QuerySpecUtils.toInnerJoin(_query, WTUser.class, ProjectUserLink.class, WTAttributeNameIfc.ID_NAME,
						"roleBObjectRef.key.id", idx_u, idx_plink);
				QuerySpecUtils.toEqualsAnd(_query, idx_plink, ProjectUserLink.class, "roleBObjectRef.key.id", soft);
				QuerySpecUtils.toEqualsAnd(_query, idx_plink, ProjectUserLink.class, "userTypeReference.key.id",
						softCode);
			}

			if (!StringUtils.isNull(mak_name)) {
				CommonCode makCode = (CommonCode) CommonUtils.getObject(mak_name);
				QuerySpecUtils.toEqualsAnd(_query, _idx_p, Project.class, "makReference.key.id", makCode);
			}

			if (!StringUtils.isNull(detail_name)) {
				CommonCode detailCode = (CommonCode) CommonUtils.getObject(detail_name);
				QuerySpecUtils.toEqualsAnd(_query, _idx_p, Project.class, "detailReference.key.id", detailCode);
			}

			QuerySpecUtils.toLikeAnd(_query, _idx_p, Project.class, Project.DESCRIPTION, description);
			QueryResult group = PersistenceHelper.manager.find(_query);

			int isNode = 1;
			JSONArray children = new JSONArray();
			JSONObject node = new JSONObject();
			while (group.hasMoreElements()) {
				Object[] oo = (Object[]) group.nextElement();
				ConfigSheetProjectLink link = (ConfigSheetProjectLink) oo[2];
				ConfigSheetDTO dto = new ConfigSheetDTO(link);
				node.put("oid", configSheet.getPersistInfo().getObjectIdentifier().getStringValue());
				node.put("name", configSheet.getName());
				node.put("number", configSheet.getNumber());
				if (isNode == 1) {
					node.put("version", configSheet.getVersion());
					node.put("latest", configSheet.getLatest());
					node.put("poid", dto.getPoid());
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
					node.put("primary", dto.getPrimary());
					node.put("createdDate_txt", dto.getCreatedDate_txt());
				} else {
					JSONObject data = new JSONObject();
					data.put("name", dto.getName());
					data.put("version", configSheet.getVersion());
					data.put("latest", configSheet.getLatest());
					data.put("oid", dto.getOid());
					data.put("poid", dto.getPoid());
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
					data.put("primary", dto.getPrimary());
					children.add(data);
				}
				isNode++;
			}
			node.put("children", children);
			if (group.size() > 0) {
				list.add(node);
			}
		}
		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	/**
	 * CONFIG SHEET 사양들 불러오기
	 */
	public JSONArray loadBaseGridData() throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();

		QuerySpec query = new QuerySpec();
		int idx_q = query.appendClassList(ConfigSheetCode.class, true);
		int idx_i = query.appendClassList(ConfigSheetCode.class, true);

		QuerySpecUtils.toEqualsAnd(query, idx_q, ConfigSheetCode.class, ConfigSheetCode.CODE_TYPE, "CATEGORY");
		query.appendAnd();
		SearchCondition sc = new SearchCondition(ConfigSheetCode.class, WTAttributeNameIfc.ID_NAME,
				ConfigSheetCode.class, "parentReference.key.id");
		sc.setFromIndicies(new int[] { idx_q, idx_i }, 0);
		sc.setOuterJoin(2);
		query.appendWhere(sc, new int[] { idx_q, idx_i });

		QuerySpecUtils.toOrderBy(query, idx_q, ConfigSheetCode.class, ConfigSheetCode.SORT, false);
		QuerySpecUtils.toOrderBy(query, idx_i, ConfigSheetCode.class, ConfigSheetCode.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		int sort = 0;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ConfigSheetCode category = (ConfigSheetCode) obj[0];
			ConfigSheetCode item = (ConfigSheetCode) obj[1];
			Map<String, Object> map = new HashMap<>();
			map.put("category_code", category != null ? category.getCode() : "");
			map.put("item_code", item != null ? item.getCode() : "");
			map.put("sort", sort);
			sort++;
			list.add(map);
		}
//		return new org.json.JSONArray(list);
		return JSONArray.fromObject(list);
	}

	/**
	 * 등록된 CONFIG SHEET 정보 가져오기
	 */
	public JSONArray loadBaseGridData(String oid) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		ConfigSheet configSheet = (ConfigSheet) CommonUtils.getObject(oid);

		ArrayList<String> dataFields = configSheet.getDataFields();

		QueryResult rs = PersistenceHelper.manager.navigate(configSheet, "project", ConfigSheetProjectLink.class);
		Project project = null;
		if (rs.hasMoreElements()) {
			project = (Project) rs.nextElement();
			Map<String, Object> makList = new HashMap<>();
			Map<String, Object> customerList = new HashMap<>();
			Map<String, Object> keList = new HashMap<>();
			Map<String, Object> pdateList = new HashMap<>();

			makList.put("category_name", "막종 / 막종상세");
			customerList.put("category_name", "고객사 / 설치장소");
			keList.put("category_name", "KE 작번");
			pdateList.put("category_name", "발행일");

			makList.put("item_name", "막종 / 막종상세");
			customerList.put("item_name", "고객사 / 설치장소");
			keList.put("item_name", "KE 작번");
			pdateList.put("item_name", "발행일");

			String mak = project.getMak() != null ? project.getMak().getName() : "";
			String detail = project.getDetail() != null ? project.getDetail().getName() : "";
			String customer = project.getCustomer() != null ? project.getCustomer().getName() : "";
			String install = project.getInstall() != null ? project.getInstall().getName() : "";

			makList.put("spec", mak + " / " + detail);
			customerList.put("spec", customer + " / " + install);
			keList.put("spec", project.getKeNumber());
			pdateList.put("spec", CommonUtils.getPersistableTime(project.getPDate()));

			for (int i = 0; i < dataFields.size(); i++) {
				makList.put("spec" + i, mak + " / " + detail);
				customerList.put("spec" + i, customer + " / " + install);
				keList.put("spec" + i, project.getKeNumber());
				pdateList.put("spec" + i, CommonUtils.getPersistableTime(project.getPDate()));
			}

			list.add(makList);
			list.add(customerList);
			list.add(keList);
			list.add(pdateList);
		}

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ConfigSheet.class, true);
		int idx_link = query.appendClassList(ConfigSheetVariableLink.class, true);
		QuerySpecUtils.toInnerJoin(query, ConfigSheet.class, ConfigSheetVariableLink.class, WTAttributeNameIfc.ID_NAME,
				"roleAObjectRef.key.id", idx, idx_link);
		QuerySpecUtils.toEqualsAnd(query, idx_link, ConfigSheetVariableLink.class, "roleAObjectRef.key.id",
				configSheet);
		QuerySpecUtils.toOrderBy(query, idx_link, ConfigSheetVariableLink.class, ConfigSheetVariableLink.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ConfigSheetVariableLink link = (ConfigSheetVariableLink) obj[1];
			ConfigSheetVariable variable = link.getVariable();
			ConfigSheetCode category = variable.getCategory();
			ConfigSheetCode item = variable.getItem();
			int sort = link.getSort();
			Map<String, Object> map = new HashMap<>();
			map.put("category_code", category != null ? category.getCode() : "");
			map.put("category_name", category != null ? category.getName() : "");
			map.put("item_code", item != null ? item.getCode() : "");
			map.put("item_name", item != null ? item.getName() : "");

			QueryResult qr = PersistenceHelper.manager.navigate(variable, "column", ColumnVariableLink.class);
			while (qr.hasMoreElements()) {
				ConfigSheetColumnData column = (ConfigSheetColumnData) qr.nextElement();
				map.put(column.getDataField(), column.getValue());
			}

			map.put("note", variable.getNote());
			map.put("apply", variable.getApply());
			map.put("sort", sort);
			list.add(map);
		}

		return JSONArray.fromObject(list);
	}

	/**
	 * CONFIG SHEET 관련 작번
	 */
	public JSONArray jsonAuiProject(String oid) throws Exception {
		ConfigSheet configSheet = (ConfigSheet) CommonUtils.getObject(oid);
		ArrayList<Map<String, String>> list = new ArrayList<>();
		QueryResult result = PersistenceHelper.manager.navigate(configSheet, "project", ConfigSheetProjectLink.class);
		while (result.hasMoreElements()) {
			Project project = (Project) result.nextElement();
			Map<String, String> map = new HashMap<>();
			map.put("oid", project.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("projectType_name", project.getProjectType() != null ? project.getProjectType().getName() : "");
			map.put("customer_name", project.getCustomer() != null ? project.getCustomer().getName() : "");
			map.put("install_name", project.getInstall() != null ? project.getInstall().getName() : "");
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
	 * CONFIG SHEET 비교
	 */
	public ArrayList<Map<String, Object>> compare(Project p1, ArrayList<Project> destList,
			ArrayList<ConfigSheetCode> fixedList) throws Exception {
		System.out.println("CONFIG SHEET 비교 START = " + new Timestamp(new Date().getTime()));
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		destList.add(0, p1);

		Map<String, Object> makList = new HashMap<>();
		Map<String, Object> customerList = new HashMap<>();
		Map<String, Object> keList = new HashMap<>();
		Map<String, Object> pdateList = new HashMap<>();

		makList.put("category_name", "막종 / 막종상세");
		customerList.put("category_name", "고객사 / 설치장소");
		keList.put("category_name", "KE 작번");
		pdateList.put("category_name", "발행일");

		makList.put("item_name", "막종 / 막종상세");
		customerList.put("item_name", "고객사 / 설치장소");
		keList.put("item_name", "KE 작번");
		pdateList.put("item_name", "발행일");

		for (int i = 0; i < destList.size(); i++) {
			Project project = (Project) destList.get(i);
			String oid = project.getPersistInfo().getObjectIdentifier().getStringValue();
			long id = project.getPersistInfo().getObjectIdentifier().getId();
			makList.put("oid", oid);
			customerList.put("oid", oid);
			keList.put("oid", oid);
			pdateList.put("oid", oid);
			makList.put("id", id);
			customerList.put("id", id);
			keList.put("id", id);
			pdateList.put("id", id);

			String mak = project.getMak() != null ? project.getMak().getName() : "";
			String detail = project.getDetail() != null ? project.getDetail().getName() : "";
			String customer = project.getCustomer() != null ? project.getCustomer().getName() : "";
			String install = project.getInstall() != null ? project.getInstall().getName() : "";

			makList.put("P" + (i + 1), mak + " / " + detail);
			customerList.put("P" + (i + 1), customer + " / " + install);
			keList.put("P" + (i + 1), project.getKeNumber());
			pdateList.put("P" + (i + 1), CommonUtils.getPersistableTime(project.getPDate()));
		}

		list.add(makList);
		list.add(customerList);
		list.add(keList);
		list.add(pdateList);

		for (ConfigSheetCode fix : fixedList) {

			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(ConfigSheetCode.class, true);
			QuerySpecUtils.toEqualsAnd(query, idx, ConfigSheetCode.class, ConfigSheetCode.CODE_TYPE, "CATEGORY_ITEM");
			QuerySpecUtils.toEqualsAnd(query, idx, ConfigSheetCode.class, "parentReference.key.id", fix);
			QuerySpecUtils.toOrderBy(query, idx, ConfigSheetCode.class, ConfigSheetCode.SORT, false);
			QueryResult result = PersistenceHelper.manager.find(query);

			while (result.hasMoreElements()) {
				Map<String, Object> mergedList = new HashMap<>();
				Object[] obj = (Object[]) result.nextElement();
				ConfigSheetCode itemCode = (ConfigSheetCode) obj[0];

				mergedList.put("category_name", fix.getName());
				mergedList.put("category_code", fix.getCode());
				mergedList.put("item_name", itemCode.getName());

				int key = 1;
				for (int i = 0; i < destList.size(); i++) {
					Project project = (Project) destList.get(i);
					QueryResult qr = PersistenceHelper.manager.navigate(project, "configSheet",
							ConfigSheetProjectLink.class);
					// 시트랑 프로젝트 연결 하나 일까???

					if (qr.hasMoreElements()) {
						ConfigSheet configSheet = (ConfigSheet) qr.nextElement();

						QuerySpec _query = new QuerySpec();
						int idx_sheet = _query.appendClassList(ConfigSheet.class, false);
						int idx_variable = _query.appendClassList(ConfigSheetVariable.class, true);
						int idx_link = _query.appendClassList(ConfigSheetVariableLink.class, false);
						QuerySpecUtils.toInnerJoin(_query, ConfigSheetVariable.class, ConfigSheetVariableLink.class,
								WTAttributeNameIfc.ID_NAME, "roleBObjectRef.key.id", idx_variable, idx_link);
						QuerySpecUtils.toInnerJoin(_query, ConfigSheet.class, ConfigSheetVariableLink.class,
								WTAttributeNameIfc.ID_NAME, "roleAObjectRef.key.id", idx_sheet, idx_link);
						QuerySpecUtils.toEqualsAnd(_query, idx_link, ConfigSheetVariableLink.class,
								"roleAObjectRef.key.id", configSheet);
						QuerySpecUtils.toEqualsAnd(_query, idx_variable, ConfigSheetVariable.class,
								"itemReference.key.id", itemCode);
						QuerySpecUtils.toOrderBy(_query, idx_link, ConfigSheetVariableLink.class,
								ConfigSheetVariableLink.SORT, false);
						QueryResult _qr = PersistenceHelper.manager.find(_query);

						while (_qr.hasMoreElements()) {
							Object[] o = (Object[]) _qr.nextElement();
							ConfigSheetVariable variable = (ConfigSheetVariable) o[0];

							QuerySpec qs = new QuerySpec();
							int idx_l = qs.appendClassList(ColumnVariableLink.class, true);
							QuerySpecUtils.toEqualsAnd(qs, idx_l, ColumnVariableLink.class, "roleBObjectRef.key.id",
									variable);
							QuerySpecUtils.toBooleanAnd(qs, idx_l, ColumnVariableLink.class, ColumnVariableLink.LAST,
									true);

							QueryResult rs = PersistenceHelper.manager.find(qs);

							if (rs.hasMoreElements()) {
								Object[] oo = (Object[]) rs.nextElement();
								ColumnVariableLink ll = (ColumnVariableLink) oo[0];
								mergedList.put("P" + key, ll.getColumn().getValue());
							}
							key++;
						}
					}
				}
				list.add(mergedList);
			}
		}
		destList.remove(0);
		System.out.println("CONFIG SHEET 비교 END = " + new Timestamp(new Date().getTime()));
		return list;
	}

	/**
	 * 선택한 작번 CONFIG SHEET 복사
	 */
	public ArrayList<Map<String, Object>> copyBaseData(ConfigSheet configSheet) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ConfigSheet.class, true);
		int idx_link = query.appendClassList(ConfigSheetVariableLink.class, true);
		QuerySpecUtils.toInnerJoin(query, ConfigSheet.class, ConfigSheetVariableLink.class, WTAttributeNameIfc.ID_NAME,
				"roleAObjectRef.key.id", idx, idx_link);
		QuerySpecUtils.toEqualsAnd(query, idx_link, ConfigSheetVariableLink.class, "roleAObjectRef.key.id",
				configSheet);
		QuerySpecUtils.toOrderBy(query, idx_link, ConfigSheetVariableLink.class, ConfigSheetVariableLink.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ConfigSheetVariableLink link = (ConfigSheetVariableLink) obj[1];
			ConfigSheetVariable variable = link.getVariable();
			ConfigSheetCode category = variable.getCategory();
			ConfigSheetCode item = variable.getItem();
			int sort = link.getSort();
			Map<String, Object> map = new HashMap<>();
			map.put("category_code", category != null ? category.getCode() : "");
			map.put("category_name", category != null ? category.getName() : "");
			map.put("item_code", item != null ? item.getCode() : "");
			map.put("item_name", item != null ? item.getName() : "");
			QueryResult qr = PersistenceHelper.manager.navigate(variable, "column", ColumnVariableLink.class);
			while (qr.hasMoreElements()) {
				ConfigSheetColumnData column = (ConfigSheetColumnData) qr.nextElement();
				map.put(column.getDataField(), column.getValue() != null ? column.getValue() : "");
			}
			map.put("note", variable.getNote());
			map.put("apply", variable.getApply());
			map.put("sort", sort);
			list.add(map);
		}
		return list;
	}

	/**
	 * CONFIG SHEET 번호
	 */
	public String getNextNumber() throws Exception {

		Calendar ca = Calendar.getInstance();
		int month = ca.get(Calendar.MONTH) + 1;
		int year = ca.get(Calendar.YEAR);
		DecimalFormat df = new DecimalFormat("00");
		String number = "CS-" + df.format(year).substring(2) + df.format(month) + "-";

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ConfigSheet.class, true);

		QuerySpecUtils.toLikeRightAnd(query, idx, ConfigSheet.class, ConfigSheet.NUMBER, number);
		QuerySpecUtils.toOrderBy(query, idx, ConfigSheet.class, ConfigSheet.NUMBER, true);

		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ConfigSheet configSheet = (ConfigSheet) obj[0];

			String s = configSheet.getNumber().substring(configSheet.getNumber().lastIndexOf("-") + 1);

			int ss = Integer.parseInt(s) + 1;
			DecimalFormat d = new DecimalFormat("0000");
			number += d.format(ss);
		} else {
			number += "0001";
		}
		return number;
	}

	/**
	 * CONFIG SHEET 프로젝트 가져오기
	 */
	public ArrayList<Project> getProjects(ConfigSheet configSheet) throws Exception {
		ArrayList<Project> list = new ArrayList<>();
		QueryResult result = PersistenceHelper.manager.navigate(configSheet, "project", ConfigSheetProjectLink.class);
		while (result.hasMoreElements()) {
			Project project = (Project) result.nextElement();
			list.add(project);
		}
		return list;
	}
}