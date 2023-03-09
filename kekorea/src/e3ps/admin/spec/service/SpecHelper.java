package e3ps.admin.spec.service;

import java.util.HashMap;
import java.util.Map;

import e3ps.admin.commonCode.CommonCode;
import e3ps.common.util.QuerySpecUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;

public class SpecHelper {

	public static final SpecHelper manager = new SpecHelper();
	public static final SpecService service = ServiceFactory.getService(SpecService.class);

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();

		JSONArray root = new JSONArray();
		JSONObject rootNode = new JSONObject();
		rootNode.put("oid", "0");
		rootNode.put("name", "사양관리");
		rootNode.put("code", "ROOT");
		rootNode.put("codeType", "");
		rootNode.put("description", "사양관리");
		rootNode.put("enable", true);

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(CommonCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, CommonCode.class, CommonCode.CODE_TYPE, "SPEC");
		QuerySpecUtils.toBooleanAnd(query, idx, CommonCode.class, CommonCode.ENABLE, true);
		QuerySpecUtils.toOrderBy(query, idx, CommonCode.class, CommonCode.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);

		JSONArray children = new JSONArray();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			CommonCode commonCode = (CommonCode) obj[0];
			JSONObject node = new JSONObject();
			node.put("oid", commonCode.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("name", commonCode.getName());
			node.put("code", commonCode.getCode());
			node.put("codeType", commonCode.getCodeType());
			node.put("description", commonCode.getDescription());
			node.put("enable", commonCode.getEnable());
			children.add(node);
		}
		rootNode.put("children", children);
		root.add(rootNode);
		map.put("list", root);
		return map;
	}
}