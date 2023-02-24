package e3ps.admin.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.admin.LoginHistory;
import e3ps.admin.PasswordSetting;
import e3ps.admin.column.CodeColumnData;
import e3ps.admin.column.LoginHistoryColumnData;
import e3ps.admin.commonCode.CommonCode;
import e3ps.common.util.DateUtils;
import e3ps.common.util.MessageHelper;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.StringUtils;
import e3ps.org.People;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.query.ClassAttribute;
import wt.query.ColumnExpression;
import wt.query.ConstantExpression;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SQLFunction;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;

public class AdminHelper {

	// master_type==erp
	public static final String[] INIT_CODE_LIST = new String[] { "MACHINE_TYPE", "MAKER", "TREATMENT" };

	/**
	 * access service
	 */
	public static final AdminService service = ServiceFactory.getService(AdminService.class);

	/**
	 * access helper
	 */
	public static final AdminHelper manager = new AdminHelper();

	public CommonCode getCodeForCodeValue(String codeType, String codeValue) throws Exception {
		CommonCode code = null;

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(CommonCode.class, true);

		SearchCondition sc = new SearchCondition(CommonCode.class, CommonCode.NAME, "=", codeValue);
		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();

		sc = new SearchCondition(CommonCode.class, CommonCode.CODE_TYPE, "=", codeType);
		query.appendWhere(sc, new int[] { idx });

		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			code = (CommonCode) obj[0];
		}
		return code;
	}

	public Map<String, Object> find(Map<String, Object> param) {

		Map<String, Object> map = new HashMap<String, Object>();
		List<LoginHistoryColumnData> list = new ArrayList<LoginHistoryColumnData>();
		QuerySpec query = null;

		String creatorsOid = (String) param.get("creatorsOid");
		String id = (String) param.get("id");
		String ip = (String) param.get("ip");

		// 정렬R
		String sort = (String) param.get("sort");
		String sortKey = (String) param.get("sortKey");
		String predate = (String) param.get("predate");
		String postdate = (String) param.get("postdate");
		ReferenceFactory rf = new ReferenceFactory();

		try {
			query = new QuerySpec();
//			int idx_user = query.appendClassList(WTUser.class, true);
			int idx = query.appendClassList(LoginHistory.class, true);

			ClassAttribute ca = null;
			SearchCondition sc = null;

			if (!StringUtils.isNull(id)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(LoginHistory.class, LoginHistory.ID);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(id);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(ip)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(LoginHistory.class, LoginHistory.IP);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(ip);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(creatorsOid)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				People user = (People) rf.getReference(creatorsOid).getObject();
				sc = new SearchCondition(LoginHistory.class, LoginHistory.ID, "=", user.getId());
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(predate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp start = DateUtils.convertStartDate(predate);
				sc = new SearchCondition(LoginHistory.class, WTAttributeNameIfc.CREATE_STAMP_NAME,
						SearchCondition.GREATER_THAN_OR_EQUAL, start);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(postdate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp end = DateUtils.convertEndDate(postdate);
				sc = new SearchCondition(LoginHistory.class, WTAttributeNameIfc.CREATE_STAMP_NAME,
						SearchCondition.LESS_THAN_OR_EQUAL, end);
				query.appendWhere(sc, new int[] { idx });
			}

			if (StringUtils.isNull(sort)) {
				sort = "true";
			}

			if (StringUtils.isNull(sortKey)) {
				sortKey = WTAttributeNameIfc.CREATE_STAMP_NAME;
			}

			ca = new ClassAttribute(LoginHistory.class, sortKey);
			OrderBy orderBy = new OrderBy(ca, Boolean.parseBoolean(sort));
			query.appendOrderBy(orderBy, new int[] { idx });

			PageQueryUtils pager = new PageQueryUtils(param, query);
			PagingQueryResult result = pager.find();
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				LoginHistory history = (LoginHistory) obj[0];
				LoginHistoryColumnData data = new LoginHistoryColumnData(history);
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

	public String getLastConnectTime() {
		String lastTime = null;
		QuerySpec query = null;
		try {

			query = new QuerySpec();
			int idx = query.appendClassList(LoginHistory.class, true);

			ClassAttribute ca = new ClassAttribute(LoginHistory.class, WTAttributeNameIfc.CREATE_STAMP_NAME);
			OrderBy by = new OrderBy(ca, true);
			query.appendOrderBy(by, new int[] { idx });
			QueryResult result = PersistenceHelper.manager.find(query);
			if (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				LoginHistory loginHistory = (LoginHistory) obj[0];
				lastTime = loginHistory.getCreateTimestamp().toString().substring(0, 16);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return lastTime;
	}

	public JSONArray getCodeTree(Map<String, Object> param) throws Exception {

		String codeType = (String) param.get("codeType");

		CommonCode root = getRoot(codeType);

		JSONArray jsonArray = new JSONArray();
		JSONObject rootNode = new JSONObject();
		rootNode.put("id", root.getPersistInfo().getObjectIdentifier().getStringValue());
		rootNode.put("title", root.getName());
		rootNode.put("codes", root.getCode());
		rootNode.put("depth", root.getDepth());
		rootNode.put("sort", root.getSort());
		rootNode.put("codeType", root.getCodeType());
		rootNode.put("expanded", true);
		rootNode.put("folder", true);
		getSubCode(root, rootNode);
		jsonArray.add(rootNode);
		return jsonArray;
	}

	private static void getSubCode(CommonCode root, JSONObject rootNode) throws Exception {

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(CommonCode.class, true);
		SearchCondition sc = new SearchCondition(CommonCode.class, "parentReference.key.id", "=",
				root.getPersistInfo().getObjectIdentifier().getId());
		query.appendWhere(sc, new int[] { idx });

		ClassAttribute ca = new ClassAttribute(CommonCode.class, CommonCode.SORT);
		OrderBy orderBy = new OrderBy(ca, false);
		query.appendOrderBy(orderBy, new int[] { idx });

		QueryResult result = PersistenceHelper.manager.find(query);

		JSONArray jsonChildren = new JSONArray();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			CommonCode child = (CommonCode) obj[0];

			JSONObject node = new JSONObject();
			node.put("id", child.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("title", child.getName());
			node.put("expanded", false);
			node.put("depth", child.getDepth());
			node.put("sort", child.getSort());
			node.put("codes", child.getCode());
			node.put("codeType", child.getCodeType());
			node.put("folder", true);
			getSubCode(child, node);

			jsonChildren.add(node);
		}
		rootNode.put("children", jsonChildren);
	}

	public CommonCode getRoot(String codeType) throws Exception {
		CommonCode root = null;
		QuerySpec query = null;
		try {
			query = new QuerySpec();
			int idx = query.appendClassList(CommonCode.class, true);

			SearchCondition sc = new SearchCondition(CommonCode.class, CommonCode.CODE_TYPE, "=", codeType);
			query.appendWhere(sc, new int[] { idx });
			query.appendAnd();

			sc = new SearchCondition(CommonCode.class, CommonCode.SORT, "=", 0);
			query.appendWhere(sc, new int[] { idx });
			query.appendAnd();

			sc = new SearchCondition(CommonCode.class, CommonCode.DEPTH, "=", 0);
			query.appendWhere(sc, new int[] { idx });

			QueryResult result = PersistenceHelper.manager.find(query);
			if (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				root = (CommonCode) obj[0];
			} else {
				root = AdminHelper.service.makeRoot(codeType);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return root;
	}

	public boolean isCode(String codeType, String code, int depth) throws Exception {
		boolean isCode = false;
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(CommonCode.class, true);

		SearchCondition sc = new SearchCondition(CommonCode.class, CommonCode.CODE_TYPE, "=", codeType);
		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();

		sc = new SearchCondition(CommonCode.class, CommonCode.CODE, "=", code);
		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();

		sc = new SearchCondition(CommonCode.class, CommonCode.DEPTH, "=", depth);
		query.appendWhere(sc, new int[] { idx });

		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			isCode = true;
		}
		return isCode;
	}

	public Map<String, Object> getCodeType(Map<String, Object> param) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>(); // json
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(); // data
		String key = (String) param.get("key");
		String values = (String) param.get("value");
		try {

			Map<String, Object> emptyMap = new HashMap<String, Object>();
			emptyMap.put("name", "선택");
			emptyMap.put("value", "");
			list.add(0, emptyMap);

			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(CommonCode.class, true);

			SearchCondition sc = new SearchCondition(CommonCode.class, CommonCode.CODE_TYPE, "=", key);
			query.appendWhere(sc, new int[] { idx });
			query.appendAnd();

			sc = new SearchCondition(CommonCode.class, CommonCode.DEPTH, "=", 1);
			query.appendWhere(sc, new int[] { idx });

			if (!StringUtils.isNull(values)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ClassAttribute ca = new ClassAttribute(CommonCode.class, CommonCode.NAME);
				ColumnExpression ce = ConstantExpression.newExpression("%" + values.trim().toUpperCase() + "%");
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			ClassAttribute ca = new ClassAttribute(CommonCode.class, CommonCode.CODE);
			OrderBy by = new OrderBy(ca, false);
			query.appendOrderBy(by, new int[] { idx });

			QueryResult result = PersistenceHelper.manager.find(query);
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				CommonCode c = (CommonCode) obj[0];
				String value = c.getCode();
				String display = c.getName() + " [" + c.getCode() + "]";

				Map<String, Object> ptMap = new HashMap<String, Object>();
				ptMap.put("name", display);
				ptMap.put("value", value);
				list.add(ptMap);
			}

			map.put("result", SUCCESS);
			map.put("list", list);
		} catch (Exception e) {
			e.printStackTrace();
			map.put("result", FAIL);
			map.put("msg", FAIL_DATA_LOAD);
		}
		return map;
	}

	public Map<String, Object> listCode(Map<String, Object> param) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		List<CodeColumnData> list = new ArrayList<CodeColumnData>();
		QuerySpec query = null;

		// search param
		String name = (String) param.get("name");
		String code = (String) param.get("code");
		String uses = (String) param.get("uses");
		String codeOid = (String) param.get("codeOid");

		String codeType = StringUtils.checkReplaceStr((String) param.get("codeType"), "CUSTOMER");

		ReferenceFactory rf = new ReferenceFactory();
		// 정렬
		CommonCode pCode = null;
		try {
			query = new QuerySpec();
			int idx = query.appendClassList(CommonCode.class, true);

			ClassAttribute ca = null;
			SearchCondition sc = null;

			sc = new SearchCondition(CommonCode.class, CommonCode.CODE_TYPE, "=", codeType);
			query.appendWhere(sc, new int[] { idx });

			if (!StringUtils.isNull(codeOid)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				pCode = (CommonCode) rf.getReference(codeOid).getObject();
				sc = new SearchCondition(CommonCode.class, "parentReference.key.id", "=",
						pCode.getPersistInfo().getObjectIdentifier().getId());
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(uses)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				if (uses.equals("true")) {
					sc = new SearchCondition(CommonCode.class, CommonCode.USES, SearchCondition.IS_TRUE);
				} else {
					sc = new SearchCondition(CommonCode.class, CommonCode.USES, SearchCondition.IS_FALSE);
				}
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(name)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(CommonCode.class, CommonCode.NAME);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(name);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(code)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(CommonCode.class, CommonCode.CODE);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(code);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			System.out.println("query=" + query);

			ca = new ClassAttribute(CommonCode.class, CommonCode.CODE);
			OrderBy orderBy = new OrderBy(ca, false);
			query.appendOrderBy(orderBy, new int[] { idx });

			PageQueryUtils pager = new PageQueryUtils(param, query);
			PagingQueryResult result = pager.find();
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				CommonCode c = (CommonCode) obj[0];
				CodeColumnData data = new CodeColumnData(c);
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

	public PasswordSetting getPasswordSetting() throws Exception {
		PasswordSetting ps = null;

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(PasswordSetting.class, true);

		OrderBy orderBy = new OrderBy(new ClassAttribute(PasswordSetting.class, PasswordSetting.CREATE_TIMESTAMP),
				false);
		query.appendOrderBy(orderBy, new int[] { idx });

		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ps = (PasswordSetting) obj[0];

		} else {
			ps = PasswordSetting.newPasswordSetting();
			ps.setComplex(true);
			ps.setLength(true);
			ps.setPrange(12);
			ps.setReset(3);

			ps = (PasswordSetting) PersistenceHelper.manager.save(ps);
		}
		return ps;
	}

}
