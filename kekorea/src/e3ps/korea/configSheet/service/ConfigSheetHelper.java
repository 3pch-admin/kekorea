package e3ps.korea.configSheet.service;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import e3ps.admin.configSheetCode.ConfigSheetCode;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.korea.configSheet.ColumnVariableLink;
import e3ps.korea.configSheet.ConfigSheet;
import e3ps.korea.configSheet.ConfigSheetColumnData;
import e3ps.korea.configSheet.ConfigSheetProjectLink;
import e3ps.korea.configSheet.ConfigSheetVariable;
import e3ps.korea.configSheet.ConfigSheetVariableLink;
import e3ps.korea.configSheet.beans.ConfigSheetDTO;
import e3ps.project.Project;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
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

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ConfigSheet.class, true);
		QuerySpecUtils.toOrderBy(query, idx, ConfigSheet.class, ConfigSheet.CREATE_TIMESTAMP, true);
		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();

		JSONArray list = new JSONArray();

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ConfigSheet configSheet = (ConfigSheet) obj[0];
			JSONObject node = new JSONObject();
			node.put("oid", configSheet.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("name", configSheet.getName());
			QueryResult group = PersistenceHelper.manager.navigate(configSheet, "project", ConfigSheetProjectLink.class,
					false);
			int isNode = 1;
			JSONArray children = new JSONArray();
			while (group.hasMoreElements()) {
				ConfigSheetProjectLink link = (ConfigSheetProjectLink) group.nextElement();
				ConfigSheetDTO dto = new ConfigSheetDTO(link);
				if (isNode == 1) {
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
					node.put("createdDate_txt", dto.getCreatedDate_txt());
				} else {
					JSONObject data = new JSONObject();
					data.put("name", dto.getName());
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
							mergedList.put("P" + key, variable.getSpec());
							key++;
						}
					}
				}

				list.add(mergedList);
			}
		}
		destList.remove(0);
		System.out.println("CONFIG  SHEET비교 END = " + new Timestamp(new Date().getTime()));
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
			String spec = variable.getSpec();
			int sort = link.getSort();
			Map<String, Object> map = new HashMap<>();
			map.put("category_code", category != null ? category.getCode() : "");
			map.put("category_name", category != null ? category.getName() : "");
			map.put("item_code", item != null ? item.getCode() : "");
			map.put("item_name", item != null ? item.getName() : "");
			map.put("spec", spec);
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