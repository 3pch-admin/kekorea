package e3ps.org.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.approval.ApprovalUserLine;
import e3ps.common.util.MessageHelper;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.org.Department;
import e3ps.org.People;
import e3ps.org.UserTableSet;
import e3ps.org.WTUserPeopleLink;
import e3ps.org.column.UserColumnData;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.org.WTUser;
import wt.pds.StatementSpec;
import wt.query.ClassAttribute;
import wt.query.ColumnExpression;
import wt.query.ConstantExpression;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SQLFunction;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.util.WTAttributeNameIfc;

public class OrgHelper implements MessageHelper {

	/**
	 * 직급
	 */
	// public static final String[] dutys = new String[] { "대표이사", "TW연구소 소장",
	// "TW연구소 부소장", "그룹장", "팀장", "팀원" };
	public static final String[] dutys = new String[] { "사장", "전무", "수석연구원", "책임연구원", "선임연구원", "전임연구원", "주임연구원", "연구원",
			"상무", "상무(보)", "차장", "부장" };

	/**
	 * 직위, 직책..
	 */
	public static final String[] ranks = new String[] { "사장", "전무", "수석연구원", "책임연구원", "선임연구원", "전임연구원", "주임연구원", "연구원",
			"상무", "상무(보)", "차장", "부장" };

	/**
	 * access service
	 */
	public static final OrgService service = ServiceFactory.getService(OrgService.class);

	/**
	 * access helper
	 */
	public static final OrgHelper manager = new OrgHelper();

	public static final String DEPARTMENT_ROOT = "국제엘렉트릭코리아";

	private static final String DEFAULT_PAGING = "50";

	private static final String DEFAULT_THUMNAIL_PAGING = "16";

	public boolean isDeptUser(String name) throws Exception {
		boolean isDeptUser = false;
		WTUser user = (WTUser) SessionHelper.manager.getPrincipal();

		Department department = getDepartment(user);

		if (department != null && name.equals(department.getName())) {
			isDeptUser = true;
		}
		return isDeptUser;
	}

	public Department getRoot() {
		Department root = null;
		QuerySpec query = null;
		try {
			query = new QuerySpec();
			int idx = query.appendClassList(Department.class, true);

			SearchCondition sc = new SearchCondition(Department.class, Department.CODE, "=", "ROOT");
			query.appendWhere(sc, new int[] { idx });

			QueryResult result = PersistenceHelper.manager.find(query);
			if (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				root = (Department) obj[0];
			} else {
				root = OrgHelper.service.makeRoot();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return root;
	}

	public Department getDepartment(WTUser wtuser) {
		Department department = null;
		try {

			QueryResult result = PersistenceHelper.manager.navigate(wtuser, "people", WTUserPeopleLink.class);
			if (result.hasMoreElements()) {
				People user = (People) result.nextElement();
				department = user.getDepartment();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return department;
	}

	public Department getDepartment(String code) {
		Department department = null;
		try {
			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(Department.class, true);

			SearchCondition sc = new SearchCondition(Department.class, Department.CODE, "=", code);
			query.appendWhere(sc, new int[] { idx });

			QueryResult result = PersistenceHelper.manager.find((StatementSpec) query);
			if (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				department = (Department) obj[0];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return department;
	}

	public Department getDepartmentByName(String name) {
		Department department = null;
		try {
			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(Department.class, true);

			SearchCondition sc = new SearchCondition(Department.class, Department.NAME, "=", name);
			query.appendWhere(sc, new int[] { idx });

			QueryResult result = PersistenceHelper.manager.find((StatementSpec) query);
			if (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				department = (Department) obj[0];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return department;
	}

	public boolean duplicate(String code) {
		SessionContext prev = SessionContext.newContext();
		boolean isDuplicate = false;
		try {

			SessionHelper.manager.setAdministrator();

			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(Department.class, true);

			SearchCondition sc = new SearchCondition(Department.class, Department.CODE, "=", code);
			query.appendWhere(sc, new int[] { idx });

			QueryResult result = PersistenceHelper.manager.find(query);

			if (result.hasMoreElements()) {
				isDuplicate = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SessionContext.setContext(prev);
		}
		return isDuplicate;
	}

	public List<Map<Object, Object>> userList(Map<String, Object> param) {
		List<Map<Object, Object>> list = new ArrayList<Map<Object, Object>>();
		String key = (String) param.get("key");
		try {

			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(WTUser.class, true);

			query.appendOpenParen();

			SearchCondition sc = new SearchCondition(WTUser.class, WTUser.FULL_NAME, SearchCondition.LIKE,
					"%" + key + "%");
			query.appendWhere(sc, new int[] { idx });
			query.appendOr();

			sc = new SearchCondition(WTUser.class, WTUser.NAME, SearchCondition.LIKE, "%" + key + "%");
			query.appendWhere(sc, new int[] { idx });
			query.appendCloseParen();

			query.appendAnd();

			sc = new SearchCondition(WTUser.class, WTUser.DISABLED, SearchCondition.IS_FALSE);
			query.appendWhere(sc, new int[] { idx });

			ClassAttribute ca = new ClassAttribute(WTUser.class, WTUser.FULL_NAME);
			OrderBy orderBy = new OrderBy(ca, false);
			query.appendOrderBy(orderBy, new int[] { idx });

			QueryResult result = PersistenceHelper.manager.find((StatementSpec) query);
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				WTUser user = (WTUser) obj[0];
				Map<Object, Object> dataMap = new HashMap<Object, Object>();
				dataMap.put("id", user.getName());
				dataMap.put("oid", user.getPersistInfo().getObjectIdentifier().getStringValue());
				dataMap.put("name", user.getFullName());
				dataMap.put("email", user.getEMail() != null ? user.getEMail() : "미등록");
				list.add(dataMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public People getUserByName(String name) {
		People people = null;

		try {

			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(People.class, true);
			SearchCondition sc = new SearchCondition(People.class, People.NAME, "=", name);

			query.appendWhere(sc, new int[] { idx });
			QueryResult result = PersistenceHelper.manager.find(query);
			if (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				people = (People) obj[0];
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return people;
	}

	public Map<String, Object> getUser(Map<String, Object> param) {
		String oid = (String) param.get("oid");
		ReferenceFactory rf = new ReferenceFactory();
		WTUser user = null;
		Map<String, Object> dataMap = new HashMap<String, Object>();
		try {
			user = (WTUser) rf.getReference(oid).getObject();

			String name = user.getFullName();
			String id = user.getName();
			String email = user.getEMail() != null ? user.getEMail() : "등록안됨";

			String rtn = user.getPersistInfo().getObjectIdentifier().getStringValue() + "&" + id + "&" + name + "&"
					+ email;

			dataMap.put("oid", user.getPersistInfo().getObjectIdentifier().getStringValue());
			dataMap.put("id", user.getName());
			dataMap.put("name", user.getFullName());
			dataMap.put("email", user.getEMail() != null ? user.getEMail() : "");
			dataMap.put("rtn", rtn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataMap;
	}

	public ArrayList<Department> getDerpartment(int depth) {
		ArrayList<Department> list = new ArrayList<Department>();
		QuerySpec query = null;
		try {
			query = new QuerySpec();
			int idx = query.appendClassList(Department.class, true);

			SearchCondition sc = new SearchCondition(Department.class, Department.DEPTH, "=", depth);
			query.appendWhere(sc, new int[] { idx });

			ClassAttribute ca = new ClassAttribute(Department.class, Department.SORT);
			OrderBy orderBy = new OrderBy(ca, false);
			query.appendOrderBy(orderBy, new int[] { idx });

			QueryResult result = PersistenceHelper.manager.find(query);
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				Department department = (Department) obj[0];
				list.add(department);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public Map<String, Object> getUserBind(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>(); // json
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(); // data
		String value = (String) params.get("value");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(People.class, true);

		if (!StringUtils.isNull(value)) {
			query.appendOpenParen();
			QuerySpecUtils.toLikeOr(query, idx, People.class, People.NAME, value);
			QuerySpecUtils.toLikeOr(query, idx, People.class, People.ID, value);
			query.appendCloseParen();
		}

		QuerySpecUtils.toBoolean(query, idx, People.class, People.RESIGN, SearchCondition.IS_FALSE);
		QuerySpecUtils.toOrderBy(query, idx, People.class, People.NAME, false);

		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			People user = (People) obj[0];
			Map<String, Object> userMap = new HashMap<String, Object>();
			userMap.put("name", user.getName() + " [" + user.getId() + "]");
			userMap.put("value", user.getUser().getPersistInfo().getObjectIdentifier().getStringValue());
			list.add(userMap);
		}

		map.put("result", SUCCESS);
		map.put("list", list);
		return map;
	}

	public Map<String, Object> getUserList(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>(); // json
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(); // data
		String key = (String) param.get("key");
		QuerySpec query = null;
		try {

			query = new QuerySpec();
			int idx = query.appendClassList(People.class, true);

			SearchCondition sc = null;
			ColumnExpression ce = null;
			ClassAttribute ca = null;
			SQLFunction function = null;
			if (!StringUtils.isNull(key)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				query.appendOpenParen();

				ca = new ClassAttribute(People.class, People.NAME);
				ce = ConstantExpression.newExpression("%" + key.toUpperCase() + "%");
				function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });

				query.appendOr();

				ca = new ClassAttribute(People.class, People.ID);
				ce = ConstantExpression.newExpression("%" + key.toUpperCase() + "%");
				function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });

				query.appendCloseParen();
			}

			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}

			sc = new SearchCondition(People.class, People.RESIGN, SearchCondition.IS_FALSE);
			query.appendWhere(sc, new int[] { idx });

			ca = new ClassAttribute(People.class, People.ID);
			OrderBy orderBy = new OrderBy(ca, false);
			query.appendOrderBy(orderBy, new int[] { idx });

			ca = new ClassAttribute(People.class, People.RESIGN);
			orderBy = new OrderBy(ca, false);
			query.appendOrderBy(orderBy, new int[] { idx });
			System.out.println("유저검색 쿼리 : " + query);
			QueryResult result = PersistenceHelper.manager.find(query);
			System.out.println("유저 수  : " + result.size());
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				People user = (People) obj[0];
				Map<String, Object> userMap = new HashMap<String, Object>();
				String oid = user.getPersistInfo().getObjectIdentifier().getStringValue();
				userMap.put("oid", oid);
				userMap.put("name", user.getName());
				userMap.put("id", user.getId());
				userMap.put("duty", user.getName());
				userMap.put("department", user.getDepartment() != null ? user.getDepartment().getName() : "지정안됨");
				String deptName = user.getDepartment() != null ? user.getDepartment().getName() : "지정안됨";
				String rank = user.getRank() != null ? user.getRank() : "지정안됨";
				userMap.put("deptName", deptName);
				String[] value = new String[] {
						oid + "&" + user.getName() + "&" + user.getId() + "&" + rank + "&" + deptName };
				userMap.put("value", value);
				System.out.println("유저  : " + userMap);
				list.add(userMap);
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

	public UserTableSet getUserTableSet(String module) {
		UserTableSet userTableSet = null;
		try {
			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();

			QuerySpec query = new QuerySpec();

			int idx = query.appendClassList(UserTableSet.class, true);
			long ids = user.getPersistInfo().getObjectIdentifier().getId();
			SearchCondition sc = new SearchCondition(UserTableSet.class, "wtuserReference.key.id", "=", ids);
			query.appendWhere(sc, new int[] { idx });
			query.appendAnd();

			sc = new SearchCondition(UserTableSet.class, UserTableSet.MODULE, "=", module);
			query.appendWhere(sc, new int[] { idx });

			QueryResult result = PersistenceHelper.manager.find(query);
			if (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				userTableSet = (UserTableSet) obj[0];
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return userTableSet;
	}

	public boolean isUserTableSet(String module) {
		boolean isUserTableSet = false;
		try {
			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();

			QuerySpec query = new QuerySpec();

			int idx = query.appendClassList(UserTableSet.class, true);
			long ids = user.getPersistInfo().getObjectIdentifier().getId();
			SearchCondition sc = new SearchCondition(UserTableSet.class, "wtuserReference.key.id", "=", ids);
			query.appendWhere(sc, new int[] { idx });
			query.appendAnd();

			sc = new SearchCondition(UserTableSet.class, UserTableSet.MODULE, "=", module);
			query.appendWhere(sc, new int[] { idx });

			QueryResult result = PersistenceHelper.manager.find(query);
			if (result.hasMoreElements()) {
				isUserTableSet = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return isUserTableSet;
	}

	public String[] getUserTableKeys(String module) {
		String[] keys = null;
		try {
			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();

			QuerySpec query = new QuerySpec();

			int idx = query.appendClassList(UserTableSet.class, true);
			long ids = user.getPersistInfo().getObjectIdentifier().getId();
			SearchCondition sc = new SearchCondition(UserTableSet.class, "wtuserReference.key.id", "=", ids);
			query.appendWhere(sc, new int[] { idx });
			query.appendAnd();

			sc = new SearchCondition(UserTableSet.class, UserTableSet.MODULE, "=", module);
			query.appendWhere(sc, new int[] { idx });

			QueryResult result = PersistenceHelper.manager.find(query);
			if (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				UserTableSet data = (UserTableSet) obj[0];
				keys = data.getKeys();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return keys;
	}

	public String[] getUserTableStyles(String module) {
		String[] styles = null;
		try {
			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();

			QuerySpec query = new QuerySpec();

			int idx = query.appendClassList(UserTableSet.class, true);
			long ids = user.getPersistInfo().getObjectIdentifier().getId();
			SearchCondition sc = new SearchCondition(UserTableSet.class, "wtuserReference.key.id", "=", ids);
			query.appendWhere(sc, new int[] { idx });
			query.appendAnd();

			sc = new SearchCondition(UserTableSet.class, UserTableSet.MODULE, "=", module);
			query.appendWhere(sc, new int[] { idx });

			QueryResult result = PersistenceHelper.manager.find(query);
			if (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				UserTableSet data = (UserTableSet) obj[0];
				styles = data.getStyles();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return styles;
	}

	public String[] getUserTableHeaders(String module) {
		String[] displays = null;
		try {
			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();

			QuerySpec query = new QuerySpec();

			int idx = query.appendClassList(UserTableSet.class, true);
			long ids = user.getPersistInfo().getObjectIdentifier().getId();
			SearchCondition sc = new SearchCondition(UserTableSet.class, "wtuserReference.key.id", "=", ids);
			query.appendWhere(sc, new int[] { idx });
			query.appendAnd();

			sc = new SearchCondition(UserTableSet.class, UserTableSet.MODULE, "=", module);
			query.appendWhere(sc, new int[] { idx });

			QueryResult result = PersistenceHelper.manager.find(query);
			if (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				UserTableSet data = (UserTableSet) obj[0];
				displays = data.getHeaders();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return displays;
	}

	public String[] getUserTableCols(String module) {
		String[] cols = null;
		try {
			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();

			QuerySpec query = new QuerySpec();

			int idx = query.appendClassList(UserTableSet.class, true);
			long ids = user.getPersistInfo().getObjectIdentifier().getId();
			SearchCondition sc = new SearchCondition(UserTableSet.class, "wtuserReference.key.id", "=", ids);
			query.appendWhere(sc, new int[] { idx });
			query.appendAnd();

			sc = new SearchCondition(UserTableSet.class, UserTableSet.MODULE, "=", module);
			query.appendWhere(sc, new int[] { idx });

			QueryResult result = PersistenceHelper.manager.find(query);
			if (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				UserTableSet data = (UserTableSet) obj[0];
				cols = data.getCols();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return cols;
	}

	public Map<String, Object> getUserLine(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>(); // json
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(); // data
		String lineName = (String) param.get("lineName");
		QuerySpec query = null;
		try {

			query = new QuerySpec();
			int idx = query.appendClassList(ApprovalUserLine.class, true);

			SearchCondition sc = null;
			ColumnExpression ce = null;
			ClassAttribute ca = null;
			SQLFunction function = null;
			if (!StringUtils.isNull(lineName)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(ApprovalUserLine.class, ApprovalUserLine.NAME);
				ce = ConstantExpression.newExpression("%" + lineName.toUpperCase() + "%");
				function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			if (query.getConditionCount() > 0)
				query.appendAnd();
			sc = new SearchCondition(ApprovalUserLine.class, "ownership.owner.key.id", "=",
					user.getPersistInfo().getObjectIdentifier().getId());
			query.appendWhere(sc, new int[] { idx });

			ca = new ClassAttribute(ApprovalUserLine.class, ApprovalUserLine.NAME);
			// true a b c d ....
			// false z x y ...
			OrderBy orderBy = new OrderBy(ca, true);
			query.appendOrderBy(orderBy, new int[] { idx });
			QueryResult result = PersistenceHelper.manager.find(query);
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				ApprovalUserLine lines = (ApprovalUserLine) obj[0];
				Map<String, Object> lineMap = new HashMap<String, Object>();
				String oid = lines.getPersistInfo().getObjectIdentifier().getStringValue();
				lineMap.put("oid", oid);
				lineMap.put("name", lines.getName());
				lineMap.put("lineType", lines.getLineType());
				lineMap.put("appList", lines.getApprovalList());
				lineMap.put("agreeList", lines.getAgreeList());
				lineMap.put("receiveList", lines.getReceiveList());
				String[] value = new String[] { oid + "&" + lines.getName() };
				lineMap.put("value", value);
				list.add(lineMap);
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

	public String getUserPaging(String module) {
		String paging = DEFAULT_PAGING;

		if (StringUtils.isNull(module)) {
			return paging;
		}

		try {
			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();

			QuerySpec query = new QuerySpec();

			int idx = query.appendClassList(UserTableSet.class, true);
			long ids = user.getPersistInfo().getObjectIdentifier().getId();
			SearchCondition sc = new SearchCondition(UserTableSet.class, "wtuserReference.key.id", "=", ids);
			query.appendWhere(sc, new int[] { idx });
			query.appendAnd();

			sc = new SearchCondition(UserTableSet.class, UserTableSet.MODULE, "=", module);
			query.appendWhere(sc, new int[] { idx });

			QueryResult result = PersistenceHelper.manager.find(query);
			if (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				UserTableSet data = (UserTableSet) obj[0];
				paging = data.getPsize();

				if (StringUtils.isNull(paging)) {
					if (module.equals("part_product_thumnail") || module.equals("part_library_thumnail")
							|| module.equals("epm_product_thumnail") || module.equals("epm_library_thumnail")) {
						paging = DEFAULT_THUMNAIL_PAGING;
					} else {
						paging = DEFAULT_PAGING;
					}
				}
			} else {
				if (module.equals("part_product_thumnail") || module.equals("part_library_thumnail")
						|| module.equals("epm_product_thumnail") || module.equals("epm_library_thumnail")) {
					paging = DEFAULT_THUMNAIL_PAGING;
				} else {
					paging = DEFAULT_PAGING;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return paging;
	}

	public JSONArray getDeptTree(Map<String, Object> param) throws Exception {

		Department root = getRoot();

		JSONArray jsonArray = new JSONArray();
		JSONObject rootNode = new JSONObject();
		rootNode.put("id", root.getPersistInfo().getObjectIdentifier().getStringValue());
		rootNode.put("title", root.getName());
		rootNode.put("code", root.getCode());
		rootNode.put("expanded", true);
		rootNode.put("folder", true);
		getSubDept(root, rootNode);
		jsonArray.add(rootNode);
		return jsonArray;
	}

	private static void getSubDept(Department root, JSONObject rootNode) throws Exception {

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Department.class, true);
		SearchCondition sc = new SearchCondition(Department.class, "parentReference.key.id", "=",
				root.getPersistInfo().getObjectIdentifier().getId());
		query.appendWhere(sc, new int[] { idx });

		ClassAttribute ca = new ClassAttribute(Department.class, Department.SORT);
		OrderBy orderBy = new OrderBy(ca, false);
		query.appendOrderBy(orderBy, new int[] { idx });

		QueryResult result = PersistenceHelper.manager.find(query);

		JSONArray jsonChildren = new JSONArray();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Department child = (Department) obj[0];

			JSONObject node = new JSONObject();
			node.put("id", child.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("title", child.getName());
			node.put("expanded", false);
			node.put("code", child.getCode());
			node.put("folder", true);
			getSubDept(child, node);

			jsonChildren.add(node);
		}
		rootNode.put("children", jsonChildren);
	}

	public Map<String, Object> getUserForDept(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>(); // json
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(); // data
		String oid = (String) param.get("oid");
		Department department = null;
		ReferenceFactory rf = new ReferenceFactory();
		QuerySpec query = null;
		try {

			query = new QuerySpec();
			int idx = query.appendClassList(People.class, true);

			SearchCondition sc = null;
			ClassAttribute ca = null;

			if (query.getConditionCount() > 0)
				query.appendAnd();

			department = (Department) rf.getReference(oid).getObject();
			long ids = department.getPersistInfo().getObjectIdentifier().getId();

			query.appendOpenParen();

			sc = new SearchCondition(People.class, "departmentReference.key.id", "=", ids);
			query.appendWhere(sc, new int[] { idx });

			ArrayList<Department> deptList = OrgHelper.manager.getSubDepartment(department,
					new ArrayList<Department>());
			for (int i = 0; i < deptList.size(); i++) {
				Department sub = (Department) deptList.get(i);
				query.appendOr();
				long sfid = sub.getPersistInfo().getObjectIdentifier().getId();
				query.appendWhere(new SearchCondition(People.class, "departmentReference.key.id", "=", sfid),
						new int[] { idx });
			}
			query.appendCloseParen();

			ca = new ClassAttribute(People.class, People.NAME);
			OrderBy by = new OrderBy(ca, false);
			query.appendOrderBy(by, new int[] { idx });

			QueryResult result = PersistenceHelper.manager.find(query);

			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				People user = (People) obj[0];
				Map<String, Object> userMap = new HashMap<String, Object>();
				userMap.put("oid", user.getPersistInfo().getObjectIdentifier().getStringValue());
				userMap.put("name", user.getName());
				userMap.put("id", user.getId());
				String deptName = user.getDepartment() != null ? user.getDepartment().getName() : "지정안됨";
				String duty = user.getDuty() != null ? user.getDuty() : "지정안됨";
				userMap.put("duty", duty);
				userMap.put("deptName", deptName);
				String[] value = new String[] { user.getPersistInfo().getObjectIdentifier().getStringValue() + "&"
						+ user.getName() + "&" + user.getId() + "&" + duty + "&" + deptName };
				userMap.put("value", value);
				list.add(userMap);
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

	public boolean isDepartment(String code) throws Exception {
		boolean isDepartment = false;
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Department.class, true);
		SearchCondition sc = new SearchCondition(Department.class, Department.CODE, "=", code);
		query.appendWhere(sc, new int[] { idx });
		QueryResult result = PersistenceHelper.manager.find((StatementSpec) query);
		if (result.hasMoreElements()) {
			isDepartment = true;
		}
		return isDepartment;
	}

	public Map<String, Object> find(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<UserColumnData> list = new ArrayList<UserColumnData>();
		QuerySpec query = null;
		// 전체 검색
		String id = (String) param.get("id");
		String name = (String) param.get("name");
		String resigns = (String) param.get("resigns");

		// 정렬
		String sort = (String) param.get("sort");
		String sortKey = (String) param.get("sortKey");
		String seSortKey = (String) param.get("seSortKey");

		if ("master>name".equals(sortKey)) {
			sortKey = "name";
		}

		String sub_folder = (String) param.get("sub_folder");
		String oid = (String) param.get("deptOid");
		ReferenceFactory rf = new ReferenceFactory();
		Department department = null;

		try {
			query = new QuerySpec();
			int idx = query.appendClassList(People.class, true);

			SearchCondition sc = null;
			ClassAttribute ca = null;

			if (!StringUtils.isNull(name)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(People.class, People.NAME);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(name);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			// 대소문자 구분
			if (!StringUtils.isNull(id)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				ca = new ClassAttribute(People.class, People.ID);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(id);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(resigns)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				if (resigns.equals("true")) {
					sc = new SearchCondition(People.class, People.RESIGN, SearchCondition.IS_TRUE);
				} else {
					sc = new SearchCondition(People.class, People.RESIGN, SearchCondition.IS_FALSE);
				}
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(oid)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				department = (Department) rf.getReference(oid).getObject();
				long ids = department.getPersistInfo().getObjectIdentifier().getId();

				query.appendOpenParen();

				sc = new SearchCondition(People.class, "departmentReference.key.id", "=", ids);
				query.appendWhere(sc, new int[] { idx });

				if (!StringUtils.isNull(sub_folder)) {
					ArrayList<Department> deptList = OrgHelper.manager.getSubDepartment(department,
							new ArrayList<Department>());
					for (int i = 0; i < deptList.size(); i++) {
						Department sub = (Department) deptList.get(i);
						query.appendOr();
						long sfid = sub.getPersistInfo().getObjectIdentifier().getId();
						query.appendWhere(new SearchCondition(People.class, "departmentReference.key.id", "=", sfid),
								new int[] { idx });
					}
				}
				query.appendCloseParen();
			}

			if (StringUtils.isNull(sort)) {
				sort = "false";
			}

			if (StringUtils.isNull(sortKey)) {
				sortKey = People.ID;
			}

			if (StringUtils.isNull(seSortKey)) {
				seSortKey = People.RESIGN;
			}

			ca = new ClassAttribute(People.class, seSortKey);
			OrderBy orderBy = new OrderBy(ca, Boolean.parseBoolean(sort));
			query.appendOrderBy(orderBy, new int[] { idx });

			ca = new ClassAttribute(People.class, sortKey);
			orderBy = new OrderBy(ca, Boolean.parseBoolean(sort));
			query.appendOrderBy(orderBy, new int[] { idx });

			PageQueryUtils pager = new PageQueryUtils(param, query);
			PagingQueryResult result = pager.find();
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				People user = (People) obj[0];
				UserColumnData data = new UserColumnData(user);
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

	public QueryResult findSubDepartments(Department root) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Department.class, true);

		long ids = root.getPersistInfo().getObjectIdentifier().getId();
		SearchCondition sc = new SearchCondition(Department.class, "parentReference.key.id", "=", ids);
		query.appendWhere(sc, new int[] { idx });
		return PersistenceHelper.manager.find(query);
	}

	public ArrayList<Department> getSubDepartments(Department root, ArrayList<Department> departments)
			throws Exception {
		QueryResult result = findSubDepartments(root);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Department sub = (Department) obj[0];
			departments.add(sub);
			getSubDepartments(sub, departments);
		}
		return departments;
	}

	public People getUser(String id) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(People.class, true);
		SearchCondition sc = new SearchCondition(People.class, People.ID, "=", id);
		query.appendWhere(sc, new int[] { idx });
		QueryResult result = PersistenceHelper.manager.find(query);
		People user = null;
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			user = (People) obj[0];
		}
		return user;
	}

	public ArrayList<Department> getSubDepartment(Department dd, ArrayList<Department> departments) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Department.class, true);

		SearchCondition sc = new SearchCondition(Department.class,
				Department.PARENT_REFERENCE + "." + WTAttributeNameIfc.REF_OBJECT_ID, "=",
				dd.getPersistInfo().getObjectIdentifier().getId());

		query.appendWhere(sc, new int[] { idx });
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Department sub = (Department) obj[0];
			departments.add(sub);
			getSubDepartment(sub, departments);
		}
		return departments;
	}

	public ArrayList<People> getRegUserListByDuty(String duty) {
		ArrayList<People> list = new ArrayList<People>();
		QuerySpec query = null;
		try {

			query = new QuerySpec();
			int idx = query.appendClassList(People.class, true);

			SearchCondition sc = new SearchCondition(People.class, People.RESIGN, false);
			query.appendWhere(sc, new int[] { idx });

			if (!StringUtils.isNull(duty)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				sc = new SearchCondition(People.class, People.DUTY, "=", duty);
				query.appendWhere(sc, new int[] { idx });
			}

			ClassAttribute ca = new ClassAttribute(People.class, People.NAME);
			OrderBy by = new OrderBy(ca, false);
			query.appendOrderBy(by, new int[] { idx });
			QueryResult result = PersistenceHelper.manager.find(query);
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				People user = (People) obj[0];
				list.add(user);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public ArrayList<People> getNoRegUserListByDuty(String duty) {
		ArrayList<People> list = new ArrayList<People>();
		QuerySpec query = null;
		try {

			query = new QuerySpec();
			int idx = query.appendClassList(People.class, true);

			SearchCondition sc = new SearchCondition(People.class, People.RESIGN, false);
			query.appendWhere(sc, new int[] { idx });

			if (!StringUtils.isNull(duty)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				query.appendOpenParen();
				sc = new SearchCondition(People.class, People.DUTY, SearchCondition.NOT_EQUAL, duty);
				query.appendWhere(sc, new int[] { idx });
				query.appendOr();

				ClassAttribute ca = new ClassAttribute(People.class, People.DUTY);

				sc = new SearchCondition(ca, SearchCondition.IS_NULL);
				query.appendWhere(sc, new int[] { idx });
				query.appendCloseParen();
			}

			ClassAttribute ca = new ClassAttribute(People.class, People.NAME);
			OrderBy by = new OrderBy(ca, false);
			query.appendOrderBy(by, new int[] { idx });
			QueryResult result = PersistenceHelper.manager.find(query);
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				People user = (People) obj[0];
				list.add(user);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public ArrayList<People> getRegUserListByDept(String oid) {
		ArrayList<People> list = new ArrayList<People>();
		ReferenceFactory rf = new ReferenceFactory();
		Department dept = null;
		QuerySpec query = null;
		try {

			query = new QuerySpec();
			int idx = query.appendClassList(People.class, true);

			SearchCondition sc = new SearchCondition(People.class, People.RESIGN, false);
			query.appendWhere(sc, new int[] { idx });
			if (!StringUtils.isNull(oid)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				dept = (Department) rf.getReference(oid).getObject();
				sc = new SearchCondition(People.class, "departmentReference.key.id", "=",
						dept.getPersistInfo().getObjectIdentifier().getId());
				query.appendWhere(sc, new int[] { idx });
			}

			ClassAttribute ca = new ClassAttribute(People.class, People.NAME);
			OrderBy by = new OrderBy(ca, false);
			query.appendOrderBy(by, new int[] { idx });
			QueryResult result = PersistenceHelper.manager.find(query);
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				People user = (People) obj[0];
				list.add(user);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public ArrayList<People> getNoRegUserListByDept(String oid) {
		ArrayList<People> list = new ArrayList<People>();
		ReferenceFactory rf = new ReferenceFactory();
		Department dept = null;
		QuerySpec query = null;
		try {

			query = new QuerySpec();
			int idx = query.appendClassList(People.class, true);

			SearchCondition sc = new SearchCondition(People.class, People.RESIGN, false);
			query.appendWhere(sc, new int[] { idx });

			if (!StringUtils.isNull(oid)) {

				if (query.getConditionCount() > 0)
					query.appendAnd();

				dept = (Department) rf.getReference(oid).getObject();

				query.appendOpenParen();
				sc = new SearchCondition(People.class, "departmentReference.key.id", SearchCondition.NOT_EQUAL,
						dept.getPersistInfo().getObjectIdentifier().getId());
				query.appendWhere(sc, new int[] { idx });
				query.appendOr();

				sc = new SearchCondition(People.class, "departmentReference.key.id", SearchCondition.EQUAL, 0L);
				query.appendWhere(sc, new int[] { idx });
				query.appendCloseParen();
			}

			ClassAttribute ca = new ClassAttribute(People.class, People.NAME);
			OrderBy by = new OrderBy(ca, false);
			query.appendOrderBy(by, new int[] { idx });
			QueryResult result = PersistenceHelper.manager.find(query);
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				People user = (People) obj[0];
				list.add(user);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public String[] getUserTableIndexs(String module) {
		String[] indexs = null;
		try {
			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();

			QuerySpec query = new QuerySpec();

			int idx = query.appendClassList(UserTableSet.class, true);
			long ids = user.getPersistInfo().getObjectIdentifier().getId();
			SearchCondition sc = new SearchCondition(UserTableSet.class, "wtuserReference.key.id", "=", ids);
			query.appendWhere(sc, new int[] { idx });
			query.appendAnd();

			sc = new SearchCondition(UserTableSet.class, UserTableSet.MODULE, "=", module);
			query.appendWhere(sc, new int[] { idx });

			QueryResult result = PersistenceHelper.manager.find(query);
			if (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				UserTableSet data = (UserTableSet) obj[0];
				indexs = data.getTabIndex();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return indexs;
	}
}
