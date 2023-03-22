package e3ps.korea.configSheet.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import e3ps.admin.commonCode.CommonCode;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.korea.configSheet.ConfigSheet;
import e3ps.korea.configSheet.ConfigSheetDTO;
import e3ps.korea.configSheet.ConfigSheetProjectLink;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;

public class ConfigSheetHelper {

	public static final ConfigSheetHelper manager = new ConfigSheetHelper();
	public static final ConfigSheetService service = ServiceFactory.getService(ConfigSheetService.class);

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ConfigSheet.class, true);
		QuerySpecUtils.toOrderBy(query, idx, ConfigSheet.class, ConfigSheet.CREATE_TIMESTAMP, false);
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

	public JSONArray loadBaseGridData() throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();

		QuerySpec query = new QuerySpec();
		int idx_q = query.appendClassList(CommonCode.class, true);
		int idx_i = query.appendClassList(CommonCode.class, true);
		int idx_s = query.appendClassList(CommonCode.class, true);

		QuerySpecUtils.toInnerJoin(query, CommonCode.class, CommonCode.class, WTAttributeNameIfc.ID_NAME,
				"parentReference.key.id", idx_q, idx_i);
		QuerySpecUtils.toInnerJoin(query, CommonCode.class, CommonCode.class, WTAttributeNameIfc.ID_NAME,
				"parentReference.key.id", idx_i, idx_s);
		QuerySpecUtils.toOrderBy(query, idx_q, CommonCode.class, CommonCode.SORT, false);
		System.out.println(query);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			CommonCode spec = (CommonCode) obj[2];
			CommonCode item = spec.getParent();
			CommonCode category = item.getParent();

			Map<String, String> map = new HashMap<>();
			map.put("category_code", category.getCode());
			map.put("item_code", item.getCode());
			map.put("item_name", item.getName());
			map.put("spec_code", spec.getCode());
			map.put("spec_name", spec.getName());
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}
}
