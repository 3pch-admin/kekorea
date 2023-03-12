package e3ps.org.service;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.admin.commonCode.CommonCode;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.org.Department;
import e3ps.org.People;
import e3ps.org.PeopleInstallLink;
import e3ps.org.PeopleMakLink;
import e3ps.org.PeopleWTUserLink;
import e3ps.org.dto.UserDTO;
import e3ps.workspace.ApprovalUserLine;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import wt.clients.folder.FolderTaskLogic;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
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
import wt.session.SessionHelper;
import wt.util.WTAttributeNameIfc;

public class OrgHelper {

	public static final String[] dutys = new String[] { "사장", "전무", "수석연구원", "책임연구원", "선임연구원", "전임연구원", "주임연구원", "연구원",
			"상무", "상무(보)", "차장", "부장" };

	public static final String[] ranks = new String[] { "사장", "전무", "수석연구원", "책임연구원", "선임연구원", "전임연구원", "주임연구원", "연구원",
			"상무", "상무(보)", "차장", "부장" };

	public static final OrgService service = ServiceFactory.getService(OrgService.class);
	public static final OrgHelper manager = new OrgHelper();

	public static final String DEPARTMENT_ROOT = "국제엘렉트릭코리아";

	public boolean isDeptUser(String name) throws Exception {
		boolean isDeptUser = false;
		WTUser user = (WTUser) SessionHelper.manager.getPrincipal();

		Department department = getDepartment(user);

		if (department != null && name.equals(department.getName())) {
			isDeptUser = true;
		}
		return isDeptUser;
	}

	public Department getRoot() throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Department.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, Department.class, Department.CODE, "ROOT");
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Department root = (Department) obj[0];
			return root;
		}
		return null;
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

	/**
	 * 부서코드로 부서 객체를 가져오는 함수
	 * 
	 * @param code : 부서코드
	 * @return Department
	 * @throws Exception
	 */
	public Department getDepartment(String code) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Department.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, Department.class, Department.CODE, code);
		QueryResult result = PersistenceHelper.manager.find((StatementSpec) query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Department department = (Department) obj[0];
			return department;
		}
		return null;
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

		QuerySpecUtils.toBooleanAnd(query, idx, People.class, People.RESIGN, SearchCondition.IS_FALSE);
		QuerySpecUtils.toOrderBy(query, idx, People.class, People.NAME, false);

		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			People user = (People) obj[0];
			Map<String, Object> userMap = new HashMap<String, Object>();
			userMap.put("name", user.getName() + " [" + user.getId() + "]");
			userMap.put("value", user.getWtUser().getPersistInfo().getObjectIdentifier().getStringValue());
			list.add(userMap);
		}

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
		List<UserDTO> list = new ArrayList<UserDTO>();
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
				UserDTO data = new UserDTO(user);
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

	public People getUser(String userId) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(People.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, People.class, People.ID, userId);
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			return (People) obj[0];
		}
		return null;
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

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<UserDTO> list = new ArrayList<>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(People.class, true);

		QuerySpecUtils.toBooleanAnd(query, idx, People.class, People.RESIGN, false);
		QuerySpecUtils.toOrderBy(query, idx, People.class, People.NAME, false);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			People people = (People) obj[0];
			UserDTO column = new UserDTO(people);
			list.add(column);
		}
		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());

		return map;
	}

	/**
	 * 부서 데이터를 key-value 형태로 만들어 배열에 담아서 반환 oid-name
	 * 
	 * @return ArrayList<HashMap<String, String>>
	 * @throws Exception
	 */
	public ArrayList<HashMap<String, String>> getDepartmentMap() throws Exception {
		ArrayList<HashMap<String, String>> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Department.class, true);
		QuerySpecUtils.toOrderBy(query, idx, Department.class, Department.DEPTH, false);
		QuerySpecUtils.toOrderBy(query, idx, Department.class, Department.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Department department = (Department) obj[0];
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("name", department.getName());
			map.put("oid", department.getPersistInfo().getObjectIdentifier().getStringValue());
			list.add(map);
		}
		return list;
	}

	/**
	 * 부서별 사용자를 가져온다 AUIGrid 에서 편집 용도
	 * 
	 * @param code : 부서코드
	 * @return org.json.JSONArray
	 * @throws Exception
	 */
	public org.json.JSONArray getDepartmentUser(String code) throws Exception {
		ArrayList<HashMap<String, String>> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTUser.class, true);
		int idx_d = query.appendClassList(Department.class, false);
		int idx_p = query.appendClassList(People.class, false);

		QuerySpecUtils.toInnerJoin(query, WTUser.class, People.class, WTAttributeNameIfc.ID_NAME,
				"wtUserReference.key.id", idx, idx_p);
		QuerySpecUtils.toInnerJoin(query, People.class, Department.class, "departmentReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx_p, idx_d);

		QuerySpecUtils.toEqualsAnd(query, idx_d, Department.class, Department.CODE, code);
		QuerySpecUtils.toOrderBy(query, idx, WTUser.class, WTUser.FULL_NAME, false);

		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WTUser wtUser = (WTUser) obj[0];
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("oid", wtUser.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("name", wtUser.getFullName());
			list.add(map);
		}
		return new org.json.JSONArray(list);
	}

	/**
	 * 그리드에 표현할 막종 목록
	 * 
	 * @param people : 사용자 객체
	 * @return String
	 * @throws Exception
	 */
	public String getGridMaks(People people) throws Exception {
		String mak = "";
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(People.class, true);
		int idx_link = query.appendClassList(PeopleMakLink.class, true);
		QuerySpecUtils.toInnerJoin(query, People.class, PeopleMakLink.class, WTAttributeNameIfc.ID_NAME,
				"roleAObjectRef.key.id", idx, idx_link);
		QuerySpecUtils.toEqualsAnd(query, idx_link, PeopleMakLink.class, "roleAObjectRef.key.id",
				people.getPersistInfo().getObjectIdentifier().getId());
		QueryResult result = PersistenceHelper.manager.find(query);
		int last = 0;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			PeopleMakLink link = (PeopleMakLink) obj[1];
			if (last == (result.size() - 1)) {
				mak += link.getMak().getCode();
			} else {
				mak += link.getMak().getCode() + ", ";
			}
			last++;
		}
		return mak;
	}

	/**
	 * 그리드에 표현할 설치장소 목록
	 * 
	 * @param people : 사용자 객체
	 * @return String
	 * @throws Exception
	 */
	public String getGridInstalls(People people) throws Exception {
		String mak = "";
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(People.class, true);
		int idx_link = query.appendClassList(PeopleInstallLink.class, true);
		QuerySpecUtils.toInnerJoin(query, People.class, PeopleInstallLink.class, WTAttributeNameIfc.ID_NAME,
				"roleAObjectRef.key.id", idx, idx_link);
		QuerySpecUtils.toEqualsAnd(query, idx_link, PeopleInstallLink.class, "roleAObjectRef.key.id",
				people.getPersistInfo().getObjectIdentifier().getId());
		QueryResult result = PersistenceHelper.manager.find(query);
		int last = 0;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			PeopleInstallLink link = (PeopleInstallLink) obj[1];
			if (last == (result.size() - 1)) {
				mak += link.getInstall().getCode();
			} else {
				mak += link.getInstall().getCode() + ", ";
			}
			last++;
		}
		return mak;
	}

	/**
	 * 사용자가 설정한 막종을 배열로 들고 오는 함수
	 * 
	 * @param sessionUser : 접속한 사용자의 WTUser 객체
	 * @return ArrayList<CommonCode>
	 */
	public ArrayList<CommonCode> getUserMaks(WTUser sessionUser) throws Exception {
		ArrayList<CommonCode> list = new ArrayList<>();
		People people = null;
		QueryResult result = PersistenceHelper.manager.navigate(sessionUser, "people", PeopleWTUserLink.class);
		if (result.hasMoreElements()) {
			people = (People) result.nextElement();
		}

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(People.class, true);
		int idx_link = query.appendClassList(PeopleMakLink.class, true);
		QuerySpecUtils.toInnerJoin(query, People.class, PeopleMakLink.class, WTAttributeNameIfc.ID_NAME,
				"roleAObjectRef.key.id", idx, idx_link);
		QuerySpecUtils.toEqualsAnd(query, idx_link, PeopleMakLink.class, "roleAObjectRef.key.id",
				people.getPersistInfo().getObjectIdentifier().getId());
		QueryResult qr = PersistenceHelper.manager.find(query);
		while (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			PeopleMakLink link = (PeopleMakLink) obj[1];
			list.add(link.getMak());
		}
		return list;
	}

	/**
	 * 사용자가 설정한 막종 링크
	 * 
	 * @param people : 사용자 객체
	 * @return ArrayList<PeopleMakLink>
	 * @throws Exception
	 */
	public ArrayList<PeopleMakLink> getMakLinks(People people) throws Exception {
		ArrayList<PeopleMakLink> list = new ArrayList<>();
		// 기존 막종 링크 모두 제거
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(People.class, true);
		int idx_link = query.appendClassList(PeopleMakLink.class, true);
		QuerySpecUtils.toInnerJoin(query, People.class, PeopleMakLink.class, WTAttributeNameIfc.ID_NAME,
				"roleAObjectRef.key.id", idx, idx_link);
		QuerySpecUtils.toEqualsAnd(query, idx_link, PeopleMakLink.class, "roleAObjectRef.key.id",
				people.getPersistInfo().getObjectIdentifier().getId());
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			PeopleMakLink link = (PeopleMakLink) obj[1];
			list.add(link);
		}
		return list;
	}

	/**
	 * 사용자가 설정한 설치장소 링크
	 * 
	 * @param people : 사용자 객체
	 * @return ArrayList<PeopleInstallLink>
	 * @throws Exception
	 */
	public ArrayList<PeopleInstallLink> getInstallLinks(People people) throws Exception {
		ArrayList<PeopleInstallLink> list = new ArrayList<>();
		// 기존 막종 링크 모두 제거
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(People.class, true);
		int idx_link = query.appendClassList(PeopleInstallLink.class, true);
		QuerySpecUtils.toInnerJoin(query, People.class, PeopleInstallLink.class, WTAttributeNameIfc.ID_NAME,
				"roleAObjectRef.key.id", idx, idx_link);
		QuerySpecUtils.toEqualsAnd(query, idx_link, PeopleInstallLink.class, "roleAObjectRef.key.id",
				people.getPersistInfo().getObjectIdentifier().getId());
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			PeopleInstallLink link = (PeopleInstallLink) obj[1];
			list.add(link);
		}
		return list;
	}

	public ArrayList<CommonCode> getUserInstalls(WTUser sessionUser) throws Exception {
		ArrayList<CommonCode> list = new ArrayList<>();
		People people = null;
		QueryResult result = PersistenceHelper.manager.navigate(sessionUser, "people", PeopleWTUserLink.class);
		if (result.hasMoreElements()) {
			people = (People) result.nextElement();
		}

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(People.class, true);
		int idx_link = query.appendClassList(PeopleInstallLink.class, true);
		QuerySpecUtils.toInnerJoin(query, People.class, PeopleInstallLink.class, WTAttributeNameIfc.ID_NAME,
				"roleAObjectRef.key.id", idx, idx_link);
		QuerySpecUtils.toEqualsAnd(query, idx_link, PeopleInstallLink.class, "roleAObjectRef.key.id",
				people.getPersistInfo().getObjectIdentifier().getId());
		QueryResult qr = PersistenceHelper.manager.find(query);
		while (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			PeopleInstallLink link = (PeopleInstallLink) obj[1];
			list.add(link.getInstall());
		}
		return list;
	}

	public JSONArray loadDepartmentTree(Map<String, String> params) throws Exception {
		Department root = getRoot();

		JSONArray list = new JSONArray();
		JSONObject rootNode = new JSONObject();
		rootNode.put("oid", root.getPersistInfo().getObjectIdentifier().getStringValue());
		rootNode.put("name", root.getName());

		JSONArray children = new JSONArray();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Department.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, Department.class, "parentReference.key.id",
				root.getPersistInfo().getObjectIdentifier().getId());
		QuerySpecUtils.toOrderBy(query, idx, Department.class, Department.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Department child = (Department) obj[0];
			JSONObject node = new JSONObject();
			node.put("oid", child.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("name", child.getName());
			loadDepartmentTree(child, node);
			children.add(node);
		}
		rootNode.put("children", children);
		list.add(rootNode);
		return list;
	}

	private void loadDepartmentTree(Department parent, JSONObject parentNode) throws Exception {
		JSONArray children = new JSONArray();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Department.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, Department.class, "parentReference.key.id",
				parent.getPersistInfo().getObjectIdentifier().getId());
		QuerySpecUtils.toOrderBy(query, idx, Department.class, Department.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Department child = (Department) obj[0];
			JSONObject node = new JSONObject();
			node.put("oid", child.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("name", child.getName());
			loadDepartmentTree(child, node);
			children.add(node);
		}
		parentNode.put("children", children);
	}

	public ArrayList<UserDTO> loadDepartmentUser(String oid) throws Exception {
		ArrayList<UserDTO> list = new ArrayList<>();
		Department department = null;

		if (!StringUtils.isNull(oid)) {
			department = (Department) CommonUtils.getObject(oid);
		} else {
			department = getRoot();
		}

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(People.class, true);
		int idx_d = query.appendClassList(Department.class, false);
		int idx_w = query.appendClassList(WTUser.class, false);

		QuerySpecUtils.toInnerJoin(query, People.class, WTUser.class, "wtUserReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_w);
		QuerySpecUtils.toInnerJoin(query, People.class, Department.class, "departmentReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_d);
		QuerySpecUtils.toEqualsAnd(query, idx, People.class, "departmentReference.key.id",
				department.getPersistInfo().getObjectIdentifier().getId());
		QuerySpecUtils.toOrderBy(query, idx, People.class, People.NAME, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			People people = (People) obj[0];
			UserDTO dto = new UserDTO(people);
			list.add(dto);
		}
		return list;
	}
}
