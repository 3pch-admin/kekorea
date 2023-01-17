package e3ps.common.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import e3ps.approval.service.ApprovalHelper;
import wt.enterprise.Master;
import wt.enterprise.RevisionControlled;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.collections.WTHashSet;
import wt.fc.collections.WTValuedHashMap;
import wt.inf.container.WTContainerRef;
import wt.inf.library.WTLibrary;
import wt.introspection.ClassInfo;
import wt.introspection.WTIntrospector;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.LifeCycleTemplate;
import wt.lifecycle.State;
import wt.org.WTUser;
import wt.part.PartType;
import wt.pdmlink.PDMLinkProduct;
import wt.pds.StatementSpec;
import wt.pom.Transaction;
import wt.query.ClassAttribute;
import wt.query.KeywordExpression;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.TableExpression;
import wt.session.SessionHelper;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.ControlBranch;
import wt.vc.Iterated;
import wt.vc.VersionControlHelper;
import wt.vc.VersionReference;
import wt.vc.views.ViewManageable;

public class CommonUtils implements MessageHelper {

	private CommonUtils() {

	}

	private static ReferenceFactory rf = null;

	public static boolean isAdmin() throws Exception {
		return isMember("Administrators");
	}

	public static boolean isMember(String group) throws Exception {
		WTUser user = (wt.org.WTUser) SessionHelper.manager.getPrincipal();
		return isMember(group, user);
	}

	public static boolean isMember(String group, WTUser user) throws Exception {
		Enumeration en = user.parentGroupNames();
		while (en.hasMoreElements()) {
			String st = (String) en.nextElement();
			if (st.equals(group))
				return true;
		}
		return false;
	}

	private static WTLibrary getEPLANContext() throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTLibrary.class, true);
		SearchCondition sc = new SearchCondition(WTLibrary.class, WTLibrary.NAME, "=", "EPLAN");
		query.appendWhere(sc, new int[] { idx });
		QueryResult result = PersistenceHelper.manager.find((StatementSpec) query);
		WTLibrary context = null;
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			context = (WTLibrary) obj[0];
		}
		return context;
	}

	private static WTLibrary getLibraryContext() throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTLibrary.class, true);
		SearchCondition sc = new SearchCondition(WTLibrary.class, WTLibrary.NAME, "=", "LIBRARY");
		query.appendWhere(sc, new int[] { idx });
		QueryResult result = PersistenceHelper.manager.find((StatementSpec) query);
		WTLibrary context = null;
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			context = (WTLibrary) obj[0];
		}
		return context;
	}

	private static PDMLinkProduct getPDMLinkContext() throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(PDMLinkProduct.class, true);
		SearchCondition sc = new SearchCondition(PDMLinkProduct.class, PDMLinkProduct.NAME, "=", "Commonspace");
		query.appendWhere(sc, new int[] { idx });
		QueryResult result = PersistenceHelper.manager.find((StatementSpec) query);
		PDMLinkProduct context = null;
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			context = (PDMLinkProduct) obj[0];
		}
		return context;
	}

	public static WTContainerRef getEPLAN() throws Exception {
		WTLibrary eplan = getEPLANContext();
		WTContainerRef container = WTContainerRef.newWTContainerRef(eplan);
		return container;
	}

	public static WTContainerRef getLibrary() throws Exception {
		WTLibrary library = getLibraryContext();
		WTContainerRef container = WTContainerRef.newWTContainerRef(library);
		return container;
	}

	public static WTContainerRef getContainer() throws Exception {
		PDMLinkProduct product = getPDMLinkContext();
		WTContainerRef container = WTContainerRef.newWTContainerRef(product);
		return container;
	}

	public static Map<String, Object> getStateBind(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>(); // json
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(); // data
		String key = (String) param.get("key"); // 상태값
		try {

			LifeCycleTemplate lct = LifeCycleHelper.service.getLifeCycleTemplate(key, getContainer());

			Vector vector = LifeCycleHelper.service.findStates(lct);

			Map<String, Object> emptyMap = new HashMap<String, Object>();
			emptyMap.put("name", "선택");
			emptyMap.put("value", "");
			list.add(0, emptyMap);

			for (int i = 0; i < vector.size(); i++) {
				State state = (State) vector.get(i);
				Map<String, Object> ptMap = new HashMap<String, Object>();
				ptMap.put("name", state.getDisplay(SessionHelper.getLocale()));
				ptMap.put("value", state.toString());
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

	public static boolean isLatestVersion(String oid) throws Exception {
		ReferenceFactory rf = new ReferenceFactory();
		Persistable persistable = (Persistable) rf.getReference(oid).getObject();
		return isLatestVersion(persistable);
	}

	public static boolean isLatestVersion(Persistable persistable) throws Exception {
		WTHashSet set = new WTHashSet();
		set.add(persistable);

		WTValuedHashMap map = (WTValuedHashMap) VersionControlHelper.service.getLatestRevisions(set);
		VersionReference reference = (VersionReference) map.get(persistable);

		boolean isLatestRevision = reference.references(persistable);
		boolean isLatestIteration = VersionControlHelper.isLatestIteration((Iterated) persistable);

		return (isLatestIteration && isLatestRevision);
	}

	public static Persistable getLatestVersion(String oid) throws Exception {
		ReferenceFactory rf = new ReferenceFactory();
		Persistable persistable = (Persistable) rf.getReference(oid).getObject();
		WTHashSet set = new WTHashSet();
		set.add(persistable);

		WTValuedHashMap map = (WTValuedHashMap) VersionControlHelper.service.getLatestRevisions(set);
		VersionReference reference = (VersionReference) map.get(persistable);
		return reference.getObject();
	}

	public static Persistable getLatestVersion(Persistable persistable) throws Exception {
		WTHashSet set = new WTHashSet();
		set.add(persistable);

		WTValuedHashMap map = (WTValuedHashMap) VersionControlHelper.service.getLatestRevisions(set);
		VersionReference reference = (VersionReference) map.get(persistable);
		return reference.getObject();
	}

	public static String getURL(String oid) throws Exception {
		String url = "";

		if (oid.indexOf("WTDocument") > -1) {
			url = "/Windchill/plm/document/listDocument";
		} else if (oid.indexOf("EPMDocument") > -1) {
			url = "/Windchill/plm/epm/listEpm";
		} else if (oid.indexOf("WTPart") > -1) {
			url = "/Windchill/plm/part/listPart";
		}
		return url;
	}

	public static boolean isCreator(RevisionControlled rc) throws Exception {
		WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
		return isCreator(rc, user);
	}

	public static boolean isCreator(RevisionControlled rc, WTUser user) {
		String id = rc.getCreatorName();
		if (id.equals(user.getName())) {
			return true;
		}
		return false;
	}

	public static boolean isModifier(RevisionControlled rc) throws Exception {
		WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
		return isModifier(rc, user);
	}

	public static boolean isModifier(RevisionControlled rc, WTUser user) {
		String id = rc.getModifierName();
		if (id.equals(user.getName())) {
			return true;
		}
		return false;
	}

	public static void addLastVersionCondition(QuerySpec query, int idx) throws Exception {

		TableExpression tableExpr = query.getFromClause().getTableExpressionAt(idx);
		Class target = tableExpr.getTableClass();

		ClassInfo var2 = WTIntrospector.getClassInfo(target);
		String tableName = var2.getDatabaseInfo().getBaseTableInfo().getTablename();
		String columnName = var2.getDatabaseInfo().getBaseTableInfo()
				.getColumnDescriptor("versionInfo.identifier.versionSortId").getColumnName();
		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}
		query.appendWhere(new SearchCondition(
				new KeywordExpression(query.getFromClause().getAliasAt(idx) + "." + columnName), "=",
				new KeywordExpression("(SELECT MAX(" + columnName + ") FROM " + tableName + " WHERE "
						+ query.getFromClause().getAliasAt(idx) + ".IDA3MASTERREFERENCE = IDA3MASTERREFERENCE)")));

	}

	public static void latestQuery(QuerySpec qs, Class targetClass, int idx) throws WTException {
		try {
			int branchIdx = qs.appendClassList(ControlBranch.class, false);
			int childBranchIdx = qs.appendClassList(ControlBranch.class, false);

			if (qs.getConditionCount() > 0)
				qs.appendAnd();
			qs.appendWhere(new SearchCondition(targetClass, RevisionControlled.BRANCH_IDENTIFIER, ControlBranch.class,
					WTAttributeNameIfc.ID_NAME), new int[] { idx, branchIdx });

			if (qs.getConditionCount() > 0)
				qs.appendAnd();
			SearchCondition outerJoinSc = new SearchCondition(ControlBranch.class, WTAttributeNameIfc.ID_NAME,
					ControlBranch.class, "predecessorReference.key.id");
			outerJoinSc.setOuterJoin(SearchCondition.RIGHT_OUTER_JOIN);
			qs.appendWhere(outerJoinSc, new int[] { branchIdx, childBranchIdx });

			ClassAttribute childBranchIdNameCa = new ClassAttribute(ControlBranch.class, WTAttributeNameIfc.ID_NAME);
			qs.appendSelect(childBranchIdNameCa, new int[] { childBranchIdx }, false);

			if (qs.getConditionCount() > 0)
				qs.appendAnd();
			qs.appendWhere(new SearchCondition(childBranchIdNameCa, SearchCondition.IS_NULL),
					new int[] { childBranchIdx });
		} catch (WTPropertyVetoException e) {
			throw new WTException(e);
		}
	}

	public static Map<String, Object> setStateObjAction(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		String list = (String) param.get("items");
		String state = (String) param.get("state");
		Persistable per = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			String[] oids = list.split(",");

			for (String oid : oids) {
				per = (Persistable) rf.getReference(oid).getObject();

				if (ApprovalHelper.manager.isAppLineCheck(per)) {
					map.put("result", FAIL);
					map.put("msg", "선택한 객체는 결재가 진행 중에 있습니다.\n상태값을 변경 할 수 없습니다.");
					return map;
				}

				if (!(per instanceof LifeCycleManaged)) {
					map.put("result", FAIL);
					map.put("msg", "선택한 객체는 라이프 사이클 객체가 아닙니다.");
					return map;
				} else {
					LifeCycleManaged lcm = (LifeCycleManaged) per;
					LifeCycleHelper.service.setLifeCycleState(lcm, State.toState(state));
				}
			}

			map.put("result", SUCCESS);
			map.put("msg", "선택한 객체의 상태값이 변경 되었습니다.");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "선택한 객체의 상태값 변경 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요.");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	public static Map<String, Object> getPartTypeBind(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>(); // json
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(); // data
		try {

			PartType[] partTypes = PartType.getPartTypeSet();
			Map<String, Object> emptyMap = new HashMap<String, Object>();
			emptyMap.put("name", "선택");
			emptyMap.put("value", "");
			list.add(0, emptyMap);

			for (PartType types : partTypes) {
				String value = types.toString();
				String display = types.getDisplay();
				if ("inseparable".equals(value)) {
					continue;
				}

				if ("separable".equals(value)) {
					display = "어셈블리 (ASSEMBLY)";
				}

				if ("component".equals(value)) {
					display = "부품 (PART)";
				}

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

	public static RevisionControlled getLatestObject(Master master) throws WTException {
		return getLatestObject(master, null);
	}

	public static RevisionControlled getLatestObject(Master master, String _viewName) throws WTException {
		RevisionControlled rc = null;
		QueryResult qr = wt.vc.VersionControlHelper.service.allVersionsOf(master);

		while (qr.hasMoreElements()) {
			RevisionControlled obj = ((RevisionControlled) qr.nextElement());

			if (_viewName != null) {
				if (!_viewName.equals(((ViewManageable) obj).getViewName()))
					continue;
			}

			if (rc == null
					|| obj.getVersionIdentifier().getSeries().greaterThan(rc.getVersionIdentifier().getSeries())) {
				rc = obj;
			}
		}
		if (rc != null)
			return (RevisionControlled) VersionControlHelper.getLatestIteration(rc, false);
		else
			return rc;
	}

	public static RevisionControlled getLatestVersion(RevisionControlled object) throws WTException {
		return getLatestObject((Master) object.getMaster(), null);
	}

	public static long getOIDLongValue(String oid) {
		String tempoid = oid;
		tempoid = tempoid.substring(tempoid.lastIndexOf(":") + 1);
		return Long.parseLong(tempoid);
	}

	public static long getOIDLongValue(Persistable per) {
		String tempoid = getOIDString(per);
		tempoid = tempoid.substring(tempoid.lastIndexOf(":") + 1);
		return Long.parseLong(tempoid);
	}

	public static boolean checkString(String str) {
		return str != null && str.length() > 0 && !str.equals("null");
	}

	public static String getOIDString(Persistable per) {
		if (per == null)
			return null;
		return PersistenceHelper.getObjectIdentifier(per).getStringValue();
	}

	public static Persistable getObject(String oid) {
		if (oid == null)
			return null;
		try {
			if (rf == null)
				rf = new ReferenceFactory();
			return rf.getReference(oid).getObject();
		} catch (Exception e) {
			// e.printStackTrace();
			return null;
		}
	}

	public static String getVersion(RevisionControlled rc) throws Exception {
		return rc.getVersionIdentifier().getSeries().getValue();
	}

	public static String getIteration(RevisionControlled rc) throws Exception {
		return rc.getIterationIdentifier().getSeries().getValue();
	}

	public static String getFullVersion(RevisionControlled rc) throws Exception {
		return rc.getVersionIdentifier().getSeries().getValue() + "."
				+ rc.getIterationIdentifier().getSeries().getValue();
	}

	public static String getPersistableTime(Timestamp time) throws Exception {
		return getPersistableTime(time, 10);
	}

	public static String getPersistableTime(Timestamp time, int index) throws Exception {
		return time.toString().substring(0, index);
	}
}