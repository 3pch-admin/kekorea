package e3ps.org.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import e3ps.admin.commonCode.CommonCode;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.org.Department;
import e3ps.org.People;
import e3ps.org.PeopleMakLink;
import e3ps.org.PeopleWTUserLink;
import e3ps.org.dto.UserDTO;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;

public class OrgHelper {

	public static final String[] dutys = new String[] { "사장", "전무", "수석연구원", "책임연구원", "선임연구원", "전임연구원", "주임연구원", "연구원",
			"상무", "상무(보)", "차장", "부장" };

	public static final String[] ranks = new String[] { "사장", "전무", "수석연구원", "책임연구원", "선임연구원", "전임연구원", "주임연구원", "연구원",
			"상무", "상무(보)", "차장", "부장" };

	public static final OrgService service = ServiceFactory.getService(OrgService.class);
	public static final OrgHelper manager = new OrgHelper();

	public static final String DEPARTMENT_ROOT = "국제엘렉트릭코리아";

	/**
	 * 루트 부서 호출
	 */
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

	/**
	 * 부서코드로 부서 객체를 가져오는 함수
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

	/**
	 * 부서 데이터를 key-value 형태로 만들어 배열에 담아서 반환 oid-name
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
	 */
	public JSONArray getDepartmentUser(String code) throws Exception {
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
		return JSONArray.fromObject(list);
	}

	/**
	 * 그리드에 표현할 막종 목록
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
	 * 사용자가 설정한 막종을 배열로 들고 오는 함수
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
	 * 부서 트리 호출
	 */
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

	/**
	 * 부서 하위 트리 호출
	 */
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

	/**
	 * 결재선 지정시 부서별 사용자 검색
	 */
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
		QuerySpecUtils.toBooleanAnd(query, idx, People.class, People.RESIGN, false);

		query.appendAnd();
		query.appendOpenParen();
		SearchCondition sc = new SearchCondition(People.class, "departmentReference.key.id", "=",
				department.getPersistInfo().getObjectIdentifier().getId());
		query.appendWhere(sc, new int[] { idx });

		ArrayList<Department> departments = OrgHelper.manager.getSubDepartment(department, new ArrayList<Department>());
		for (int i = 0; i < departments.size(); i++) {
			Department sub = (Department) departments.get(i);
			query.appendOr();
			long sfid = sub.getPersistInfo().getObjectIdentifier().getId();
			query.appendWhere(new SearchCondition(People.class, "departmentReference.key.id", "=", sfid),
					new int[] { idx });
		}
		query.appendCloseParen();
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

	/**
	 * 하위 부서 찾아오는 함수
	 */
	private ArrayList<Department> getSubDepartment(Department parent, ArrayList<Department> list) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Department.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, Department.class, "parentReference.key.id", parent);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Department child = (Department) obj[0];
			list.add(child);
			getSubDepartment(child, list);
		}
		return list;
	}

	/**
	 * 사용자 검색 후 KEY-VALUE 값 ArrayList 에 담아서 리턴
	 */
	public ArrayList<Map<String, String>> keyValue(Map<String, String> params) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();
		String value = params.get("value");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(People.class, true);
		query.appendOpenParen();
		QuerySpecUtils.toLikeAnd(query, idx, People.class, People.NAME, value);
		QuerySpecUtils.toLikeOr(query, idx, People.class, People.ID, value);
		query.appendCloseParen();
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			People people = (People) obj[0];
			Map<String, String> map = new HashMap<>();
			map.put("oid", people.getWtUser().getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("name", people.getName());
			list.add(map);
		}
		return list;
	}

	/**
	 * 조직도 검색 함수
	 */
	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<UserDTO> list = new ArrayList<>();

		String userName = (String) params.get("userName");
		String userId = (String) params.get("userId");
		boolean resign = (boolean) params.get("resign");
		String oid = (String) params.get("oid");
		Department department = null;
		if (!StringUtils.isNull(oid)) {
			department = (Department) CommonUtils.getObject(oid);
		} else {
			department = getRoot();
		}

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(People.class, true);
		QuerySpecUtils.toLikeAnd(query, idx, People.class, People.NAME, userName);
		QuerySpecUtils.toLikeAnd(query, idx, People.class, People.ID, userId);

		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}

		query.appendOpenParen();
		SearchCondition sc = new SearchCondition(People.class, "departmentReference.key.id", "=",
				department.getPersistInfo().getObjectIdentifier().getId());
		query.appendWhere(sc, new int[] { idx });

		ArrayList<Department> departments = OrgHelper.manager.getSubDepartment(department, new ArrayList<Department>());
		for (int i = 0; i < departments.size(); i++) {
			Department sub = (Department) departments.get(i);
			query.appendOr();
			long sfid = sub.getPersistInfo().getObjectIdentifier().getId();
			query.appendWhere(new SearchCondition(People.class, "departmentReference.key.id", "=", sfid),
					new int[] { idx });
		}
		query.appendCloseParen();

		QuerySpecUtils.toBooleanAnd(query, idx, People.class, People.RESIGN, resign);
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

}
