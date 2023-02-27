package e3ps.workspace.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ptc.wpcfg.engine2.validate.impl.ErrorReport;

import e3ps.bom.partlist.PartListData;
import e3ps.bom.partlist.PartListMaster;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.DateUtils;
import e3ps.common.util.IBAUtils;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.doc.RequestDocument;
import e3ps.org.People;
import e3ps.workspace.ApprovalContract;
import e3ps.workspace.ApprovalContractPersistableLink;
import e3ps.workspace.ApprovalLine;
import e3ps.workspace.ApprovalMaster;
import e3ps.workspace.PersistableLineMasterLink;
import e3ps.workspace.beans.ApprovalLineColumnData;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.PagingQueryResult;
import wt.fc.PagingSessionHelper;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.query.ClassAttribute;
import wt.query.ColumnExpression;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SQLFunction;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.session.SessionHelper;
import wt.util.WTAttributeNameIfc;

public class ApprovalHelper {

	// 직렬 병렬
	public static final String SERIES = "series";
	public static final String PARALLEL = "parallel";

	public static final String MODULE = "approval";

	/**
	 * 결재 타입 상수 모음
	 */
	public static final String APPROVAL_LINE = "결재";
	public static final String AGREE_LINE = "검토";
	public static final String RECEIVE_LINE = "수신";

	public static final String MASTER_APPROVING = "승인중";
	public static final String MASTER_APPROVAL_COMPLETE = "결재완료";
	public static final String MASTER_RETURN = "반려됨";

	/**
	 * 기안 라인 상태값 상수
	 */
	public static final String LINE_SUBMIT_COMPLETE = "제출됨";

	/**
	 * 결재 라인 상태값 상수
	 */
	public static final String APPROVAL_READY = "대기중";
	public static final String APPROVAL_APPROVING = "승인중";
	public static final String APPROVAL_COMPLETE = "결재완료";
	public static final String APPROVAL_RETURN = "반려됨";

	/**
	 * 검토 라인 상태값 상수
	 */
	public static final String AGREE_READY = "검토중";
	public static final String AGREE_COMPLETE = "검토완료";
	public static final String AGREE_REJECT = "검토반려";

	/**
	 * 수신 라인 상태값 상수
	 */
	public static final String RECEIVE_READY = "수신확인중";
	public static final String RECEIVE_COMPLETE = "수신완료";

	/**
	 * 부재중 처리 상태값 상수
	 */
	public static final String LINE_ABSENCE = "부재중";

	/**
	 * 결재자 타입 상수 값
	 */
	public static final String WORKING_SUBMIT = "기안자";
	public static final String WORKING_APPROVAL = "승인자";
	public static final String WORKING_AGREE = "검토자";
	public static final String WORKING_RECEIVE = "수신자";

	/**
	 * ColumnData 구분 상수 값
	 */
	private static final String COLUMN_APPROVAL = "COLUMN_APPROVAL";

	public static final ApprovalService service = ServiceFactory.getService(ApprovalService.class);
	public static final ApprovalHelper manager = new ApprovalHelper();

	public int getAppObjType(Persistable per) {
		int appObjType = 0;

		return appObjType;
	}

	public ApprovalMaster getMaster(Persistable per) {
		ApprovalMaster master = null;
		QueryResult result = null;
		try {
			result = PersistenceHelper.manager.navigate(per, "lineMaster", PersistableLineMasterLink.class);
			if (result.hasMoreElements()) {
				master = (ApprovalMaster) result.nextElement();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return master;
	}

	public ArrayList<ApprovalLine> getAllLines(ApprovalMaster master) {
		ArrayList<ApprovalLine> list = new ArrayList<ApprovalLine>();
		QueryResult result = null;
		ApprovalLine appLine = null;
		try {
			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(ApprovalLine.class, true);
			SearchCondition sc = null;
			// SearchCondition sc = new SearchCondition(ApprovalLine.class,
			// ApprovalLine.TYPE, "=",
			// ApprovalHelper.APP_LINE);
			// query.appendWhere(sc, new int[] { idx });
			// query.appendAnd();

			long ids = master.getPersistInfo().getObjectIdentifier().getId();
			sc = new SearchCondition(ApprovalLine.class, "masterReference.key.id", "=", ids);
			query.appendWhere(sc, new int[] { idx });

			ClassAttribute ca = new ClassAttribute(ApprovalLine.class, ApprovalLine.SORT);
			OrderBy orderBy = new OrderBy(ca, false);
			query.appendOrderBy(orderBy, new int[] { idx });

			result = PersistenceHelper.manager.find(query);
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				appLine = (ApprovalLine) obj[0];
				list.add(appLine);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public ArrayList<ApprovalLine> getAppLines(ApprovalMaster master) {
		ArrayList<ApprovalLine> list = new ArrayList<ApprovalLine>();
		QueryResult result = null;
		ApprovalLine appLine = null;
		try {
			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(ApprovalLine.class, true);
			SearchCondition sc = new SearchCondition(ApprovalLine.class, ApprovalLine.TYPE, "=",
					ApprovalHelper.APP_LINE);
			query.appendWhere(sc, new int[] { idx });
			query.appendAnd();

			long ids = master.getPersistInfo().getObjectIdentifier().getId();
			sc = new SearchCondition(ApprovalLine.class, "masterReference.key.id", "=", ids);
			query.appendWhere(sc, new int[] { idx });

			ClassAttribute ca = new ClassAttribute(ApprovalLine.class, ApprovalLine.SORT);
			OrderBy orderBy = new OrderBy(ca, false);
			query.appendOrderBy(orderBy, new int[] { idx });

			result = PersistenceHelper.manager.find(query);
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				appLine = (ApprovalLine) obj[0];
				list.add(appLine);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public ArrayList<ApprovalLine> getAgreeLines(ApprovalMaster master) {
		ArrayList<ApprovalLine> list = new ArrayList<ApprovalLine>();
		QueryResult result = null;
		ApprovalLine agreeLine = null;
		try {
			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(ApprovalLine.class, true);
			SearchCondition sc = new SearchCondition(ApprovalLine.class, ApprovalLine.TYPE, "=",
					ApprovalHelper.AGREE_LINE);
			query.appendWhere(sc, new int[] { idx });
			query.appendAnd();

			long ids = master.getPersistInfo().getObjectIdentifier().getId();
			sc = new SearchCondition(ApprovalLine.class, "masterReference.key.id", "=", ids);
			query.appendWhere(sc, new int[] { idx });

			ClassAttribute ca = new ClassAttribute(ApprovalLine.class, ApprovalLine.START_TIME);
			OrderBy orderBy = new OrderBy(ca, false);
			query.appendOrderBy(orderBy, new int[] { idx });

			result = PersistenceHelper.manager.find(query);
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				agreeLine = (ApprovalLine) obj[0];
				list.add(agreeLine);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public ArrayList<ApprovalLine> getReceiveLines(ApprovalMaster master) {
		ArrayList<ApprovalLine> list = new ArrayList<ApprovalLine>();
		QueryResult result = null;
		ApprovalLine receiveLine = null;
		try {
			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(ApprovalLine.class, true);
			SearchCondition sc = new SearchCondition(ApprovalLine.class, ApprovalLine.TYPE, "=",
					ApprovalHelper.RECEIVE_LINE);
			query.appendWhere(sc, new int[] { idx });
			query.appendAnd();

			long ids = master.getPersistInfo().getObjectIdentifier().getId();
			sc = new SearchCondition(ApprovalLine.class, "masterReference.key.id", "=", ids);
			query.appendWhere(sc, new int[] { idx });

			ClassAttribute ca = new ClassAttribute(ApprovalLine.class, ApprovalLine.START_TIME);
			OrderBy orderBy = new OrderBy(ca, false);
			query.appendOrderBy(orderBy, new int[] { idx });

			result = PersistenceHelper.manager.find(query);
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				receiveLine = (ApprovalLine) obj[0];
				list.add(receiveLine);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public PagingQueryResult findAgreeAndApprovalList() {
		QuerySpec query = null;
		PagingQueryResult result = null;

		try {

			query = new QuerySpec();
			int idx = query.appendClassList(ApprovalLine.class, true);
			int idx_m = query.appendClassList(ApprovalMaster.class, true);

			WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();

			SearchCondition sc = null;

			ClassAttribute ca = null;
			ClassAttribute ca_m = null;

			ca = new ClassAttribute(ApprovalLine.class, "masterReference.key.id");
			ca_m = new ClassAttribute(ApprovalMaster.class, "thePersistInfo.theObjectIdentifier.id");

			sc = new SearchCondition(ca, "=", ca_m);
			query.appendWhere(sc, new int[] { idx, idx_m });

			if (query.getConditionCount() > 0)
				query.appendAnd();
			long ids = sessionUser.getPersistInfo().getObjectIdentifier().getId();
			sc = new SearchCondition(ApprovalLine.class, "ownership.owner.key.id", "=", ids);
			query.appendWhere(sc, new int[] { idx });

			if (query.getConditionCount() > 0)
				query.appendAnd();

			query.appendOpenParen();

			sc = new SearchCondition(ApprovalLine.class, ApprovalLine.STATE, "=", LINE_APPROVING);
			query.appendWhere(sc, new int[] { idx });
			query.appendOr();

			sc = new SearchCondition(ApprovalLine.class, ApprovalLine.STATE, "=", LINE_AGREE_STAND);
			query.appendWhere(sc, new int[] { idx });

			query.appendCloseParen();

			if (query.getConditionCount() > 0)
				query.appendAnd();

			query.appendOpenParen();

			sc = new SearchCondition(ApprovalLine.class, ApprovalLine.TYPE, "=", AGREE_LINE);
			query.appendWhere(sc, new int[] { idx });
			query.appendOr();

			sc = new SearchCondition(ApprovalLine.class, ApprovalLine.TYPE, "=", APP_LINE);
			query.appendWhere(sc, new int[] { idx });

			query.appendCloseParen();

			ca = new ClassAttribute(ApprovalLine.class, WTAttributeNameIfc.CREATE_STAMP_NAME);
			OrderBy orderBy = new OrderBy(ca, true);
			query.appendOrderBy(orderBy, new int[] { idx });

			result = PagingSessionHelper.openPagingSession(0, 24, query);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public Map<String, Object> findApprovalList(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<ApprovalColumnData> list = new ArrayList<ApprovalColumnData>();
		QuerySpec query = null;

		String name = (String) param.get("name");
		String creatorsOid = (String) param.get("creatorsOid");
		String predate = (String) param.get("predate");
		String postdate = (String) param.get("postdate");

		String sub_data = (String) param.get("sub_data");

		// 정렬
		String sort = (String) param.get("sort");
		String sortKey = (String) param.get("sortKey");
		try {

			query = new QuerySpec();
			int idx = query.appendClassList(ApprovalLine.class, true);
			int idx_m = query.appendClassList(ApprovalMaster.class, true);

			WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();

			SearchCondition sc = null;

			ClassAttribute ca = null;
			ClassAttribute ca_m = null;

			ca = new ClassAttribute(ApprovalLine.class, "masterReference.key.id");
			ca_m = new ClassAttribute(ApprovalMaster.class, "thePersistInfo.theObjectIdentifier.id");

			sc = new SearchCondition(ca, "=", ca_m);
			query.appendWhere(sc, new int[] { idx, idx_m });

			// 대소문자 구분
			if (!StringUtils.isNull(name)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(ApprovalLine.class, ApprovalLine.NAME);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(name);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

//			if (!CommonUtils.isAdmin()) {
			if (!"wcadmin".equals(sessionUser.getFullName())) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				long ids = sessionUser.getPersistInfo().getObjectIdentifier().getId();
				sc = new SearchCondition(ApprovalLine.class, "ownership.owner.key.id", "=", ids);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(sub_data)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				query.appendOpenParen();

				sc = new SearchCondition(ApprovalLine.class, ApprovalLine.STATE, "=", LINE_APPROVAL_COMPLETE);
				query.appendWhere(sc, new int[] { idx });
				query.appendOr();

				sc = new SearchCondition(ApprovalLine.class, ApprovalLine.STATE, "=", LINE_APPROVING);
				query.appendWhere(sc, new int[] { idx });
				query.appendOr();

				// sc = new SearchCondition(ApprovalLine.class, ApprovalLine.STATE, "=",
				// LINE_SUBMIT_COMPLETE);
				// query.appendWhere(sc, new int[] { idx });
				// query.appendOr();

				// sc = new SearchCondition(ApprovalLine.class, ApprovalLine.STATE, "=",
				// LINE_STAND);
				// query.appendWhere(sc, new int[] { idx });

				sc = new SearchCondition(ApprovalLine.class, ApprovalLine.STATE, "=", LINE_RETURN_COMPLETE);
				query.appendWhere(sc, new int[] { idx });

				query.appendCloseParen();

			} else {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				sc = new SearchCondition(ApprovalLine.class, ApprovalLine.STATE, "=", LINE_APPROVING);
				query.appendWhere(sc, new int[] { idx });
			}

			if (query.getConditionCount() > 0)
				query.appendAnd();

			sc = new SearchCondition(ApprovalLine.class, ApprovalLine.TYPE, "=", APP_LINE);
			query.appendWhere(sc, new int[] { idx });

			if (!StringUtils.isNull(creatorsOid)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ReferenceFactory rf = new ReferenceFactory();
				People user = (People) rf.getReference(creatorsOid).getObject();
				long user_ids = user.getUser().getPersistInfo().getObjectIdentifier().getId();
				sc = new SearchCondition(ApprovalMaster.class, "ownership.owner.key.id", SearchCondition.EQUAL,
						user_ids);
				query.appendWhere(sc, new int[] { idx_m });

			}

			if (!StringUtils.isNull(predate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp start = DateUtils.convertStartDate(predate);
				sc = new SearchCondition(ApprovalLine.class, ApprovalLine.CREATE_TIMESTAMP,
						SearchCondition.GREATER_THAN_OR_EQUAL, start);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(postdate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp end = DateUtils.convertEndDate(postdate);
				sc = new SearchCondition(ApprovalLine.class, ApprovalLine.CREATE_TIMESTAMP,
						SearchCondition.LESS_THAN_OR_EQUAL, end);
				query.appendWhere(sc, new int[] { idx });
			}

			if (StringUtils.isNull(sort)) {
				sort = "true";
			}

			if (StringUtils.isNull(sortKey)) {
				// sortKey = WTAttributeNameIfc.MODIFY_STAMP_NAME;
				sortKey = ApprovalLine.CREATE_TIMESTAMP;
			}

			ca = new ClassAttribute(ApprovalLine.class, sortKey);
			OrderBy orderBy = new OrderBy(ca, Boolean.parseBoolean(sort));
			query.appendOrderBy(orderBy, new int[] { idx });

			PageQueryUtils pager = new PageQueryUtils(param, query);
			PagingQueryResult result = pager.find();
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				ApprovalLine line = (ApprovalLine) obj[0];
				ApprovalColumnData data = new ApprovalColumnData(line);
				list.add(data);
			}

			map.put("size", pager.getTotalSize());
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

	public Map<String, Object> findIng(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<IngColumnData> list = new ArrayList<IngColumnData>();
		QuerySpec query = null;
		String name = (String) param.get("name");
		// 정렬
		String sort = (String) param.get("sort");
		String sortKey = (String) param.get("sortKey");
		try {

			query = new QuerySpec();
			int idx = query.appendClassList(ApprovalMaster.class, true);

			SearchCondition sc = null;

			// 대소문자 구분
			if (!StringUtils.isNull(name)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ClassAttribute ca = new ClassAttribute(ApprovalMaster.class, ApprovalMaster.NAME);
				// ColumnExpression ce = ConstantExpression.newExpression("%" +
				// name.toUpperCase() + "%");
				ColumnExpression ce = StringUtils.getUpperColumnExpression(name);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			// if (!StringUtils.isNull(objType)) {
			// if (query.getConditionCount() > 0)
			// query.appendAnd();
			// sc = new SearchCondition(ApprovalMaster.class, ApprovalMaster.OBJ_TYPE, "=",
			// objType);
			// query.appendWhere(sc, new int[] { idx });
			// }

			WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
//			if (!CommonUtils.isAdmin()) {
			if (!"wcadmin".equals(sessionUser.getFullName())) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				long ids = sessionUser.getPersistInfo().getObjectIdentifier().getId();
				sc = new SearchCondition(ApprovalMaster.class, "ownership.owner.key.id", "=", ids);
				query.appendWhere(sc, new int[] { idx });
			}

			if (query.getConditionCount() > 0)
				query.appendAnd();

			query.appendOpenParen();
			sc = new SearchCondition(ApprovalMaster.class, ApprovalMaster.STATE, SearchCondition.EQUAL, LINE_APPROVING);
			query.appendWhere(sc, new int[] { idx });

			query.appendOr();
			sc = new SearchCondition(ApprovalMaster.class, ApprovalMaster.STATE, SearchCondition.EQUAL,
					LINE_AGREE_STAND);
			query.appendWhere(sc, new int[] { idx });

			query.appendCloseParen();

			if (StringUtils.isNull(sort)) {
				sort = "true";
			}

			if (StringUtils.isNull(sortKey)) {
				// sortKey = WTAttributeNameIfc.CREATE_STAMP_NAME;
				sortKey = WTAttributeNameIfc.MODIFY_STAMP_NAME;
			}

			ClassAttribute ca = new ClassAttribute(ApprovalMaster.class, sortKey);
			OrderBy orderBy = new OrderBy(ca, Boolean.parseBoolean(sort));
			query.appendOrderBy(orderBy, new int[] { idx });

			PageQueryUtils pager = new PageQueryUtils(param, query);
			PagingQueryResult result = pager.find();
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				ApprovalMaster master = (ApprovalMaster) obj[0];
				IngColumnData data = new IngColumnData(master);
				list.add(data);
			}

			map.put("size", pager.getTotalSize());
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

	public String[] getContractEpmData(Persistable per) throws Exception {
		String oid = "";
		String name = "";
		String name_of_part = "";
		String dwg_no = "";
		String version = "";
		String state = "";
		String modifier = "";
		String modifyDate = "";

		EPMDocument epm = (EPMDocument) per;
		oid = epm.getPersistInfo().getObjectIdentifier().getStringValue();
		name = epm.getName();
		name_of_part = IBAUtils.getStringValue(epm, "NAME_OF_PARTS");
		dwg_no = IBAUtils.getStringValue(epm, "DWG_NO");
		version = epm.getVersionIdentifier().getSeries().getValue() + "."
				+ epm.getIterationIdentifier().getSeries().getValue();
		state = epm.getLifeCycleState().getDisplay();
		modifier = epm.getModifierFullName();
		modifyDate = epm.getModifyTimestamp().toString().substring(0, 16);

		String[] str = new String[] { oid, name, name_of_part, dwg_no, state, version, modifier, modifyDate };

		return str;
	}

	public String[] getContractObjData(Persistable per) throws Exception {
		String oid = "";
		String name = "";
		String number = "";
		String version = "";
		String state = "";
		String modifier = "";
		String modifyDate = "";

		if (per instanceof WTDocument) {
			WTDocument document = (WTDocument) per;
			oid = document.getPersistInfo().getObjectIdentifier().getStringValue();
			name = document.getName();
			number = document.getNumber();
			version = document.getVersionIdentifier().getSeries().getValue() + "."
					+ document.getIterationIdentifier().getSeries().getValue();
			state = document.getLifeCycleState().getDisplay();
			modifier = document.getModifierFullName();
			modifyDate = document.getModifyTimestamp().toString().substring(0, 16);
		} else if (per instanceof EPMDocument) {
			EPMDocument epm = (EPMDocument) per;
			oid = epm.getPersistInfo().getObjectIdentifier().getStringValue();
			name = epm.getName();
			number = epm.getNumber();
			version = epm.getVersionIdentifier().getSeries().getValue() + "."
					+ epm.getIterationIdentifier().getSeries().getValue();
			state = epm.getLifeCycleState().getDisplay();
			modifier = epm.getModifierFullName();
			modifyDate = epm.getModifyTimestamp().toString().substring(0, 16);
		} else if (per instanceof WTPart) {
			WTPart part = (WTPart) per;
			oid = part.getPersistInfo().getObjectIdentifier().getStringValue();
			name = part.getName();
			number = part.getNumber();
			version = part.getVersionIdentifier().getSeries().getValue() + "."
					+ part.getIterationIdentifier().getSeries().getValue();
			state = part.getLifeCycleState().getDisplay();
			modifier = part.getModifierFullName();
			modifyDate = part.getModifyTimestamp().toString().substring(0, 16);
		} else if (per instanceof PartListMaster) {
			PartListMaster mm = (PartListMaster) per;
			oid = mm.getPersistInfo().getObjectIdentifier().getStringValue();
			name = mm.getName();
			number = mm.getNumber();
			// version = mm.getVersionIdentifier().getSeries().getValue() + "."
			// + mm.getIterationIdentifier().getSeries().getValue();
			state = mm.getLifeCycleState().getDisplay();
			modifier = mm.getOwnership().getOwner().getFullName();
			modifyDate = mm.getModifyTimestamp().toString().substring(0, 16);
		}

		String[] str = new String[] { oid, number, name, state, version, modifier, modifyDate };

		return str;
	}

	public ApprovalContract getContract(Persistable persist) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ApprovalContractPersistableLink.class, true);

		long ids = persist.getPersistInfo().getObjectIdentifier().getId();
		SearchCondition sc = new SearchCondition(ApprovalContractPersistableLink.class, "roleBObjectRef.key.id", "=",
				ids);
		query.appendWhere(sc, new int[] { idx });

		QueryResult result = PersistenceHelper.manager.find(query);
		ApprovalContract contract = null;
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ApprovalContractPersistableLink link = (ApprovalContractPersistableLink) obj[0];
			contract = link.getContract();
		}
		return contract;
	}

	public Persistable getPersist(ApprovalContract contract) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ApprovalContractPersistableLink.class, true);

		long ids = contract.getPersistInfo().getObjectIdentifier().getId();
		SearchCondition sc = new SearchCondition(ApprovalContractPersistableLink.class, "roleAObjectRef.key.id", "=",
				ids);
		query.appendWhere(sc, new int[] { idx });

		QueryResult result = PersistenceHelper.manager.find(query);
		Persistable persist = null;
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ApprovalContractPersistableLink link = (ApprovalContractPersistableLink) obj[0];
			persist = link.getPersist();
		}
		return persist;
	}

	public String getPrefix(Persistable per) throws Exception {
		String prefix = "";
		if (per instanceof ApprovalContract) {
			ApprovalContract contract = (ApprovalContract) per;
			Persistable obj = getPersist(contract);

			if (obj instanceof WTDocument) {
				prefix = "문서";
				if (obj instanceof RequestDocument) {
					prefix = "의뢰서";
				}
			} else if (obj instanceof EPMDocument) {
				prefix = "도면";
			} else if (obj instanceof WTPart) {
				prefix = "부품";
			}
		} else {

			if (per instanceof WTDocument) {
				prefix = "문서";
				if (per instanceof RequestDocument) {
					prefix = "의뢰서";
				}
			} else if (per instanceof EPMDocument) {
				prefix = "도면";
			} else if (per instanceof WTPart) {
				prefix = "부품";
			} else if (per instanceof PartListMaster) {
				prefix = "수배표";
			}
		}
		return prefix;
	}

	public boolean isIngPoint(String state) {
		boolean isCheckPoint = false;

		System.out.println("state=" + state);

		if (state.equals(LINE_APPROVING) || state.equals(LINE_AGREE_STAND)) {
			isCheckPoint = true;
		}
		return isCheckPoint;
	}

	public boolean isReturnPoint(String state) {
		boolean isCheckPoint = false;
		if (state.equals(LINE_RETURN_COMPLETE)) {
			isCheckPoint = true;
		}
		return isCheckPoint;
	}

	public Map<String, Object> findCompleteList(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<CompleteColumnData> list = new ArrayList<CompleteColumnData>();

		QuerySpec query = null;
		String name = (String) param.get("name");
		String creatorsOid = (String) param.get("creatorsOid");
		String predate = (String) param.get("predate");
		String postdate = (String) param.get("postdate");
		String type = (String) param.get("type");
		// 정렬
		String sort = (String) param.get("sort");
		String sortKey = (String) param.get("sortKey");
		try {

			query = new QuerySpec();
			// int idx = query.appendClassList(ApprovalLine.class, true);
			int idx_m = query.appendClassList(ApprovalMaster.class, true);

			WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();

			SearchCondition sc = null;

			ClassAttribute ca = null;
//			ClassAttribute ca_m = null;

			// ca = new ClassAttribute(ApprovalLine.class, "masterReference.key.id");
			// ca_m = new ClassAttribute(ApprovalMaster.class,
			// "thePersistInfo.theObjectIdentifier.id");

			// sc = new SearchCondition(ca, "=", ca_m);
			// query.appendWhere(sc, new int[] { idx, idx_m });

			// 대소문자 구분
			if (!StringUtils.isNull(name)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(ApprovalMaster.class, ApprovalMaster.NAME);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(name);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx_m });
			}

			if (!StringUtils.isNull(type)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(ApprovalMaster.class, ApprovalMaster.TYPE);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(type);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx_m });
			}

//			if (!CommonUtils.isAdmin()) {
			if (!"wcadmin".equals(sessionUser.getFullName())) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				long ids = sessionUser.getPersistInfo().getObjectIdentifier().getId();
				sc = new SearchCondition(ApprovalMaster.class, "ownership.owner.key.id", "=", ids);
				query.appendWhere(sc, new int[] { idx_m });
			}

			if (query.getConditionCount() > 0)
				query.appendAnd();

			// query.appendOpenParen();
			//
			// sc = new SearchCondition(ApprovalLine.class, ApprovalLine.COMPLETE_USER_ID,
			// "=", sessionUser.getName());
			// query.appendWhere(sc, new int[] { idx });
			// query.appendAnd();
			//
			// sc = new SearchCondition(ApprovalLine.class, ApprovalLine.TYPE, "=",
			// APP_LINE);
			// query.appendWhere(sc, new int[] { idx });
			// query.appendAnd();
			//
			// sc = new SearchCondition(ApprovalLine.class, ApprovalLine.ROLE, "=",
			// WORKING_SUBMIT);
			// query.appendWhere(sc, new int[] { idx });
			//
			// query.appendCloseParen();

			sc = new SearchCondition(ApprovalMaster.class, ApprovalMaster.STATE, "=", MASTER_APPROVAL_COMPLETE);
			query.appendWhere(sc, new int[] { idx_m });

			if (!StringUtils.isNull(creatorsOid)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ReferenceFactory rf = new ReferenceFactory();
				People user = (People) rf.getReference(creatorsOid).getObject();
				long user_ids = user.getUser().getPersistInfo().getObjectIdentifier().getId();
				sc = new SearchCondition(ApprovalMaster.class, "ownership.owner.key.id", SearchCondition.EQUAL,
						user_ids);
				query.appendWhere(sc, new int[] { idx_m });

			}

			if (!StringUtils.isNull(predate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp start = DateUtils.convertStartDate(predate);
				sc = new SearchCondition(ApprovalMaster.class, ApprovalMaster.CREATE_TIMESTAMP,
						SearchCondition.GREATER_THAN_OR_EQUAL, start);
				query.appendWhere(sc, new int[] { idx_m });
			}

			if (!StringUtils.isNull(postdate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp end = DateUtils.convertEndDate(postdate);
				sc = new SearchCondition(ApprovalMaster.class, ApprovalMaster.CREATE_TIMESTAMP,
						SearchCondition.LESS_THAN_OR_EQUAL, end);
				query.appendWhere(sc, new int[] { idx_m });
			}

			if (StringUtils.isNull(sort)) {
				sort = "true";
			}

			if (StringUtils.isNull(sortKey)) {
				sortKey = WTAttributeNameIfc.MODIFY_STAMP_NAME;
			}

			ca = new ClassAttribute(ApprovalMaster.class, sortKey);
			OrderBy orderBy = new OrderBy(ca, Boolean.parseBoolean(sort));
			query.appendOrderBy(orderBy, new int[] { idx_m });

			PageQueryUtils pager = new PageQueryUtils(param, query);
			PagingQueryResult result = pager.find();
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				ApprovalMaster master = (ApprovalMaster) obj[0];
				CompleteColumnData data = new CompleteColumnData(master);
				list.add(data);
			}

			map.put("size", pager.getTotalSize());
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

	// 반려함
	public Map<String, Object> findReturnList(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<ReturnColumnData> list = new ArrayList<ReturnColumnData>();
		QuerySpec query = null;
		String name = (String) param.get("name");
		// 내가 결재완료한 라인들...
		// 정렬
		String sort = (String) param.get("sort");
		String sortKey = (String) param.get("sortKey");
		try {

			query = new QuerySpec();
			int idx = query.appendClassList(ApprovalMaster.class, true);

			WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();

			SearchCondition sc = null;

			// 대소문자 구분
			if (!StringUtils.isNull(name)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ClassAttribute ca = new ClassAttribute(ApprovalMaster.class, ApprovalMaster.NAME);
				// ColumnExpression ce = ConstantExpression.newExpression("%" +
				// name.toUpperCase() + "%");
				ColumnExpression ce = StringUtils.getUpperColumnExpression(name);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			// if (!StringUtils.isNull(objType)) {
			// if (query.getConditionCount() > 0)
			// query.appendAnd();
			// sc = new SearchCondition(ApprovalMaster.class, ApprovalMaster.OBJ_TYPE, "=",
			// objType);
			// query.appendWhere(sc, new int[] { idx });
			// }

			// if (!CommonUtils.isAdmin()) {
			if (!"wcadmin".equals(sessionUser.getFullName())) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				long ids = sessionUser.getPersistInfo().getObjectIdentifier().getId();
				sc = new SearchCondition(ApprovalMaster.class, "ownership.owner.key.id", "=", ids);
				query.appendWhere(sc, new int[] { idx });
			}

			if (query.getConditionCount() > 0)
				query.appendAnd();

			query.appendOpenParen();

			sc = new SearchCondition(ApprovalMaster.class, ApprovalMaster.STATE, "=", LINE_RETURN_COMPLETE);
			query.appendWhere(sc, new int[] { idx });
			query.appendOr();
			sc = new SearchCondition(ApprovalMaster.class, ApprovalMaster.STATE, "=", LINE_AGREE_REJECT);
			query.appendWhere(sc, new int[] { idx });
			query.appendCloseParen();

			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			sc = new SearchCondition(ApprovalMaster.class, ApprovalMaster.VIEW_DISABLED, SearchCondition.IS_FALSE);
			query.appendWhere(sc, new int[] { idx });

			if (StringUtils.isNull(sort)) {
				sort = "true";
			}

			if (StringUtils.isNull(sortKey)) {
				// sortKey = WTAttributeNameIfc.CREATE_STAMP_NAME;
				sortKey = WTAttributeNameIfc.MODIFY_STAMP_NAME;
			}

			ClassAttribute ca = new ClassAttribute(ApprovalMaster.class, sortKey);
			OrderBy orderBy = new OrderBy(ca, Boolean.parseBoolean(sort));
			query.appendOrderBy(orderBy, new int[] { idx });

			PageQueryUtils pager = new PageQueryUtils(param, query);
			PagingQueryResult result = pager.find();
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				ApprovalMaster master = (ApprovalMaster) obj[0];
				ReturnColumnData data = new ReturnColumnData(master);
				list.add(data);
			}

			map.put("size", pager.getTotalSize());
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

	// 합의
	public Map<String, Object> findAgreeList(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<AgreeColumnData> list = new ArrayList<AgreeColumnData>();
		QuerySpec query = null;
		String creatorsOid = (String) param.get("creatorsOid");
		String name = (String) param.get("name");
		String predate = (String) param.get("predate");
		String postdate = (String) param.get("postdate");

		String sub_data = (String) param.get("sub_data");

		// 정렬
		String sort = (String) param.get("sort");
		String sortKey = (String) param.get("sortKey");
		try {

			query = new QuerySpec();
			int idx = query.appendClassList(ApprovalLine.class, true);
			int idx_m = query.appendClassList(ApprovalMaster.class, true);

			WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();

			SearchCondition sc = null;

			ClassAttribute ca = null;
			ClassAttribute ca_m = null;

			ca = new ClassAttribute(ApprovalLine.class, "masterReference.key.id");
			ca_m = new ClassAttribute(ApprovalMaster.class, "thePersistInfo.theObjectIdentifier.id");

			sc = new SearchCondition(ca, "=", ca_m);
			query.appendWhere(sc, new int[] { idx, idx_m });

			// 대소문자 구분
			if (!StringUtils.isNull(name)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(ApprovalLine.class, ApprovalLine.NAME);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(name);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!"wcadmin".equals(sessionUser.getFullName())) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				long ids = sessionUser.getPersistInfo().getObjectIdentifier().getId();
				sc = new SearchCondition(ApprovalLine.class, "ownership.owner.key.id", "=", ids);
				query.appendWhere(sc, new int[] { idx });
			}

			// 승인중. 합의중.. 수신확인
			// 체크면..
			if (!StringUtils.isNull(sub_data)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				query.appendOpenParen();

				sc = new SearchCondition(ApprovalLine.class, ApprovalLine.STATE, "=", LINE_AGREE_STAND);
				query.appendWhere(sc, new int[] { idx });
				query.appendOr();

				sc = new SearchCondition(ApprovalLine.class, ApprovalLine.STATE, "=", LINE_AGREE_COMPLETE);
				query.appendWhere(sc, new int[] { idx });
				query.appendOr();

				sc = new SearchCondition(ApprovalLine.class, ApprovalLine.STATE, "=", LINE_AGREE_REJECT);
				query.appendWhere(sc, new int[] { idx });

				query.appendCloseParen();

			} else {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				sc = new SearchCondition(ApprovalLine.class, ApprovalLine.STATE, "=", LINE_AGREE_STAND);
				query.appendWhere(sc, new int[] { idx });
			}

			if (query.getConditionCount() > 0)
				query.appendAnd();

			sc = new SearchCondition(ApprovalLine.class, ApprovalLine.TYPE, "=", AGREE_LINE);
			query.appendWhere(sc, new int[] { idx });

			if (!StringUtils.isNull(creatorsOid)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ReferenceFactory rf = new ReferenceFactory();
				People user = (People) rf.getReference(creatorsOid).getObject();
				long user_ids = user.getUser().getPersistInfo().getObjectIdentifier().getId();
				sc = new SearchCondition(ApprovalMaster.class, "ownership.owner.key.id", SearchCondition.EQUAL,
						user_ids);
				query.appendWhere(sc, new int[] { idx_m });

			}

			if (!StringUtils.isNull(predate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp start = DateUtils.convertStartDate(predate);
				sc = new SearchCondition(ApprovalLine.class, ApprovalLine.CREATE_TIMESTAMP,
						SearchCondition.GREATER_THAN_OR_EQUAL, start);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(postdate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp end = DateUtils.convertEndDate(postdate);
				sc = new SearchCondition(ApprovalLine.class, ApprovalLine.CREATE_TIMESTAMP,
						SearchCondition.LESS_THAN_OR_EQUAL, end);
				query.appendWhere(sc, new int[] { idx });
			}

			if (StringUtils.isNull(sort)) {
				sort = "true";
			}

			if (StringUtils.isNull(sortKey)) {
				// sortKey = WTAttributeNameIfc.MODIFY_STAMP_NAME;
				sortKey = ApprovalLine.CREATE_TIMESTAMP;
			}

			ca = new ClassAttribute(ApprovalLine.class, sortKey);
			OrderBy orderBy = new OrderBy(ca, Boolean.parseBoolean(sort));
			query.appendOrderBy(orderBy, new int[] { idx });

			PageQueryUtils pager = new PageQueryUtils(param, query);
			PagingQueryResult result = pager.find();
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				ApprovalLine line = (ApprovalLine) obj[0];
				AgreeColumnData data = new AgreeColumnData(line);
				list.add(data);
			}

			map.put("size", pager.getTotalSize());
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

	// 수신라인
	public Map<String, Object> findReceiveList(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<ReceiveColumnData> list = new ArrayList<ReceiveColumnData>();
		QuerySpec query = null;
		String name = (String) param.get("name");
		String creatorsOid = (String) param.get("creatorsOid");
		String predate = (String) param.get("predate");
		String postdate = (String) param.get("postdate");

		String sub_data = (String) param.get("sub_data");

		// 정렬
		String sort = (String) param.get("sort");
		String sortKey = (String) param.get("sortKey");
		try {

			query = new QuerySpec();
			int idx = query.appendClassList(ApprovalLine.class, true);
			int idx_m = query.appendClassList(ApprovalMaster.class, true);

			WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();

			SearchCondition sc = null;

			ClassAttribute ca = null;
			ClassAttribute ca_m = null;

			ca = new ClassAttribute(ApprovalLine.class, "masterReference.key.id");
			ca_m = new ClassAttribute(ApprovalMaster.class, "thePersistInfo.theObjectIdentifier.id");

			sc = new SearchCondition(ca, "=", ca_m);
			query.appendWhere(sc, new int[] { idx, idx_m });

			// 대소문자 구분
			if (!StringUtils.isNull(name)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(ApprovalLine.class, ApprovalLine.NAME);
				// ColumnExpression ce = ConstantExpression.newExpression("%" +
				// name.toUpperCase() + "%");
				ColumnExpression ce = StringUtils.getUpperColumnExpression(name);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			// if (!CommonUtils.isAdmin()) {
			if (!"wcadmin".equals(sessionUser.getFullName())) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				long ids = sessionUser.getPersistInfo().getObjectIdentifier().getId();
				sc = new SearchCondition(ApprovalLine.class, "ownership.owner.key.id", "=", ids);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(sub_data)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				query.appendOpenParen();

				sc = new SearchCondition(ApprovalLine.class, ApprovalLine.STATE, "=", LINE_RECEIVE_COMPLETE);
				query.appendWhere(sc, new int[] { idx });
				query.appendOr();

				sc = new SearchCondition(ApprovalLine.class, ApprovalLine.STATE, "=", LINE_RECEIVE_STAND);
				query.appendWhere(sc, new int[] { idx });

				query.appendCloseParen();

			} else {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				sc = new SearchCondition(ApprovalLine.class, ApprovalLine.STATE, "=", LINE_RECEIVE_STAND);
				query.appendWhere(sc, new int[] { idx });
			}

			if (query.getConditionCount() > 0)
				query.appendAnd();
			// 승인중. 합의중.. 수신확인
			sc = new SearchCondition(ApprovalLine.class, ApprovalLine.TYPE, "=", RECEIVE_LINE);
			query.appendWhere(sc, new int[] { idx });

			if (!StringUtils.isNull(creatorsOid)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ReferenceFactory rf = new ReferenceFactory();
				People user = (People) rf.getReference(creatorsOid).getObject();
				long user_ids = user.getUser().getPersistInfo().getObjectIdentifier().getId();
				sc = new SearchCondition(ApprovalMaster.class, "ownership.owner.key.id", SearchCondition.EQUAL,
						user_ids);
				query.appendWhere(sc, new int[] { idx_m });

			}

			if (!StringUtils.isNull(predate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp start = DateUtils.convertStartDate(predate);
				sc = new SearchCondition(ApprovalLine.class, ApprovalLine.CREATE_TIMESTAMP,
						SearchCondition.GREATER_THAN_OR_EQUAL, start);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(postdate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp end = DateUtils.convertEndDate(postdate);
				sc = new SearchCondition(ApprovalLine.class, ApprovalLine.CREATE_TIMESTAMP,
						SearchCondition.LESS_THAN_OR_EQUAL, end);
				query.appendWhere(sc, new int[] { idx });
			}

			if (StringUtils.isNull(sort)) {
				sort = "true";
			}

			if (StringUtils.isNull(sortKey)) {
				sortKey = WTAttributeNameIfc.MODIFY_STAMP_NAME;
			}

			ca = new ClassAttribute(ApprovalLine.class, sortKey);
			OrderBy orderBy = new OrderBy(ca, Boolean.parseBoolean(sort));
			query.appendOrderBy(orderBy, new int[] { idx });

			PageQueryUtils pager = new PageQueryUtils(param, query);
			PagingQueryResult result = pager.find();
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				ApprovalLine line = (ApprovalLine) obj[0];
				ReceiveColumnData data = new ReceiveColumnData(line);
				list.add(data);
			}

			map.put("size", pager.getTotalSize());
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

	public boolean isNextLine(ApprovalMaster master, int sort) {
		QuerySpec query = null;
		boolean isNextLine = false;
		try {
			query = new QuerySpec();
			int idx = query.appendClassList(ApprovalLine.class, true);
			SearchCondition sc = new SearchCondition(ApprovalLine.class, "masterReference.key.id", "=",
					master.getPersistInfo().getObjectIdentifier().getId());
			query.appendWhere(sc, new int[] { idx });
			query.appendAnd();

			sc = new SearchCondition(ApprovalLine.class, ApprovalLine.SORT, "=", sort + 1);
			query.appendWhere(sc, new int[] { idx });
			QueryResult result = PersistenceHelper.manager.find(query);
			if (result.hasMoreElements()) {
				isNextLine = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isNextLine;
	}

	public boolean isLastAppLine(ApprovalMaster master, int sort) {
		boolean isLastAppLine = true;

		ArrayList<ApprovalLine> list = getAppLines(master);
		for (ApprovalLine appLine : list) {
			int compare = appLine.getSort();
			// 0 < -1;-50 -1

			System.out.println("com=" + compare);

			if (sort <= compare) {
				isLastAppLine = false;
				break;
			}
		}
		return isLastAppLine;
	}

	// public boolean isLastLine(ApprovalMaster master) {
	// boolean isLast = true;
	// try {
	// ArrayList<ApprovalLine> list = getAppLines(master);
	// for (ApprovalLine appLine : list) {
	// int sort = appLine.getSort();
	// if (sort == 0) {
	// appLine.setState(LINE_APPROVING);
	// appLine.setStartTime(new Timestamp(new Date().getTime()));
	// PersistenceHelper.manager.modify(appLine);
	// isLast = false;
	//
	// // 메일??
	// MailUtils.sendNextMail(appLine);
	//
	// break;
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return isLast;
	// }

	public ApprovalLine getFirstLine(ApprovalMaster master) throws Exception {
		ArrayList<ApprovalLine> lines = getAppLines(master);

		ApprovalLine first = null;
		for (ApprovalLine line : lines) {
			if (line.getRole().equals(WORKING_SUBMIT)) {
				first = line;
				break;
			}
		}
		return first;
	}

	public String getObjType(Persistable per) throws Exception {
		String objType = "";
		if (per instanceof WTDocument) {
			objType = "DOCUMENT";
		} else if (per instanceof WTPart) {
			objType = "PART";
			// objType = "구매품";
		} else if (per instanceof EPMDocument) {
			objType = "EPM";
		} else if (per instanceof ApprovalContract) {
			ApprovalContract contract = (ApprovalContract) per;
			QueryResult result = PersistenceHelper.manager.navigate(contract, "persist",
					ApprovalContractPersistableLink.class);
			if (result.hasMoreElements()) {
				Persistable p = (Persistable) result.nextElement();

				if (p instanceof WTPart) {
					objType = "PARTAPP";
				} else if (p instanceof WTDocument) {
					objType = "DOCUMENTAPP";
				} else if (p instanceof EPMDocument) {
					objType = "EPMAPP";
				} else {
					objType = "일괄결재";
				}
			}
		}
		return objType;
	}

	public void read(ApprovalLine line) throws Exception {
		line.setReads(true);
		PersistenceHelper.manager.modify(line);
	}

	public int[] getLineCount() throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();

		// 검토함
		int agreeSize = (int) findAgreeList(param).get("size");

		// 결재함
		int appSize = (int) findApprovalList(param).get("size");

		int receiveSize = (int) findReceiveList(param).get("size");
		int returnSize = (int) findReturnList(param).get("size");
		int completeSize = (int) findCompleteList(param).get("size");
		int ingSize = (int) findIng(param).get("size");

		int[] count = new int[] { agreeSize, appSize, receiveSize, ingSize, completeSize, returnSize };
		return count;
	}

	public ArrayList<Persistable> getMasterByPersistable(ApprovalMaster master) throws Exception {
		ArrayList<Persistable> list = new ArrayList<Persistable>();
		Persistable per = master.getPersist();
		if (per instanceof ApprovalContract) {
			ApprovalContract contract = (ApprovalContract) per;
			QueryResult result = PersistenceHelper.manager.navigate(contract, "persist",
					ApprovalContractPersistableLink.class);

			while (result.hasMoreElements()) {
				Persistable pp = (Persistable) result.nextElement();
				list.add(pp);
			}
		}
		return list;
	}

	public boolean isAppLineCheck(String oid) throws Exception {
		ReferenceFactory rf = new ReferenceFactory();
		Persistable per = (Persistable) rf.getReference(oid).getObject();
		return isAppLineCheck(per);
	}

	public boolean isAppLineCheck(Persistable per) throws Exception {

		boolean isAppLine = false;

		QueryResult result = PersistenceHelper.manager.navigate(per, "lineMaster", PersistableLineMasterLink.class);
		if (result.hasMoreElements()) {
			ApprovalMaster master = (ApprovalMaster) result.nextElement();

			// 승인중이면.. 진행 결재 잇음
			String state = master.getState();
			if (state.equals(MASTER_APPROVING)) {
				isAppLine = true;
			}
		}
		return isAppLine;
	}

	public String getLineName(Persistable per) {
		String name = "";
		if (per instanceof WTDocument) {
			if (per instanceof RequestDocument) {
				RequestDocument req = (RequestDocument) per;
				// name = "의뢰서_" + req.getName();
				name = req.getName();
			} else {
				WTDocument document = (WTDocument) per;
				// name = "문서_" + document.getName();
				name = document.getName();
			}
		} else if (per instanceof WTPart) {
			WTPart part = (WTPart) per;
			name = part.getName();
		} else if (per instanceof EPMDocument) {
			EPMDocument epm = (EPMDocument) per;
			name = epm.getName();
		} else if (per instanceof ApprovalContract) {
			ApprovalContract contract = (ApprovalContract) per;
			name = contract.getName();
		} else if (per instanceof PartListMaster) {
			PartListMaster mm = (PartListMaster) per;
			name = mm.getName();
		}
		return name;
	}

	public boolean isEndAgree(ApprovalMaster master) throws Exception {
		boolean isEndAgree = true;
		QuerySpec query = null;
		try {
			query = new QuerySpec();
			int idx = query.appendClassList(ApprovalLine.class, true);
			int idx_m = query.appendClassList(ApprovalMaster.class, true);

			SearchCondition sc = null;

			ClassAttribute ca = null;
			ClassAttribute ca_m = null;

			ca = new ClassAttribute(ApprovalLine.class, "masterReference.key.id");
			ca_m = new ClassAttribute(ApprovalMaster.class, "thePersistInfo.theObjectIdentifier.id");

			sc = new SearchCondition(ca, "=", ca_m);
			query.appendWhere(sc, new int[] { idx, idx_m });
			query.appendAnd();

			sc = new SearchCondition(ApprovalLine.class, "masterReference.key.id", "=",
					master.getPersistInfo().getObjectIdentifier().getId());
			query.appendWhere(sc, new int[] { idx });
			query.appendAnd();

			sc = new SearchCondition(ApprovalLine.class, ApprovalLine.TYPE, "=", AGREE_LINE);
			query.appendWhere(sc, new int[] { idx });
			query.appendAnd();

			sc = new SearchCondition(ApprovalLine.class, ApprovalLine.STATE, "=", LINE_AGREE_STAND);
			query.appendWhere(sc, new int[] { idx });

			QueryResult result = PersistenceHelper.manager.find(query);

			// 결과가 있으면..
			if (result.size() > 0) {
				isEndAgree = false;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return isEndAgree;
	}

	public Map<String, Object> findErrorReport(Map<String, Object> param) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		List<ErrorReportColumnData> list = new ArrayList<ErrorReportColumnData>();
		QuerySpec query = null;

		// search param
////		String number = (String) param.get("number");
//		String name = (String) param.get("name");

//		String dwg_no = (String) param.get("dwg_no");
//		String predate = (String) param.get("predate");
//		String statesPart = (String) param.get("statesPart");
//		String postdate = (String) param.get("postdate");
//		String predate_m = (String) param.get("predate_m");
//		String postdate_m = (String) param.get("postdate_m");

//		String creatorsOid = (String) param.get("creatorsOid");
//		String modifierOid = (String) param.get("modifierOid");

		String latest = (String) param.get("latest");
		if (StringUtils.isNull(latest)) {
			latest = "true";
		}

//		String location = (String) param.get("location");
//		ReferenceFactory rf = new ReferenceFactory();
		// 정렬
		String sort = (String) param.get("sort");
		String sortKey = (String) param.get("sortKey");
//		String fileName = (String) param.get("fileName");
		// context
//		String context = (String) param.get("context");

		// String partTypes = (String) param.get("partTypes");

		try {
			query = new QuerySpec();

			int idx = query.appendClassList(ErrorReport.class, true);

			if (StringUtils.isNull(sort)) {
				sort = "true";
			}

			if (StringUtils.isNull(sortKey)) {
				sortKey = WTAttributeNameIfc.CREATE_STAMP_NAME;
			}

			ClassAttribute ca = new ClassAttribute(PartListData.class, sortKey);
			OrderBy orderBy = new OrderBy(ca, Boolean.parseBoolean(sort));
			query.appendOrderBy(orderBy, new int[] { idx });

			PageQueryUtils pager = new PageQueryUtils(param, query);
			PagingQueryResult result = pager.find();
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				ErrorReport error = (ErrorReport) obj[0];
				ErrorReportColumnData data = new ErrorReportColumnData(error);
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

	public Map<String, Object> agree(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		return map;
	}

	public Map<String, Object> approval(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<ApprovalLineColumnData> list = new ArrayList<>();
		boolean isAdmin = CommonUtils.isAdmin();
		String name = (String) params.get("name");
		String creatorsOid = (String) params.get("creatorsOid");
		String predate = (String) params.get("predate");
		String postdate = (String) params.get("postdate");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ApprovalLine.class, true);
		int idx_m = query.appendClassList(ApprovalMaster.class, false);

		QuerySpecUtils.toInnerJoin(query, ApprovalLine.class, ApprovalMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);

		// 쿼리 수정할 예정
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.STATE, APPROVAL_APPROVING);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.TYPE, APPROVAL_LINE);
		;

		if (!StringUtils.isNull(name)) {
			QuerySpecUtils.toLikeAnd(query, idx, ApprovalLine.class, ApprovalLine.NAME, name);
		}

		if (!isAdmin) {
			WTUser sessionUser = CommonUtils.sessionUser();
			QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, "ownership.owner.key.id",
					sessionUser.getPersistInfo().getObjectIdentifier().getId());
		}

		// 기안자를 찾는거
		if (!StringUtils.isNull(creatorsOid)) {
			WTUser wtUser = (WTUser) CommonUtils.getObject(creatorsOid);
			QuerySpecUtils.toEqualsAnd(query, idx, ApprovalMaster.class, "ownership.owner.key.id",
					wtUser.getPersistInfo().getObjectIdentifier().getId());
		}

		if (!StringUtils.isNull(predate)) {
			Timestamp pre = DateUtils.convertStartDate(predate);
			QuerySpecUtils.toTimeGreaterEqualsThan(query, idx, ApprovalLine.class, ApprovalLine.CREATE_TIMESTAMP, pre);
		}

		if (!StringUtils.isNull(postdate)) {
			Timestamp post = DateUtils.convertStartDate(postdate);
			QuerySpecUtils.toTimeLessEqualsThan(query, idx, ApprovalLine.class, ApprovalLine.CREATE_TIMESTAMP, post);
		}

		QuerySpecUtils.toOrderBy(query, idx, ApprovalLine.class, ApprovalLine.CREATE_TIMESTAMP, true);
		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ApprovalLine approvalLine = (ApprovalLine) obj[0];
			ApprovalLineColumnData column = new ApprovalLineColumnData(approvalLine, COLUMN_APPROVAL);
			list.add(column);
		}
		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	public Map<String, Object> receive(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		return map;
	}

	public Map<String, Object> progress(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		return map;
	}

	public Map<String, Object> complete(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		return map;
	}

	public Map<String, Object> reject(Map<String, Object> params) {
		Map<String, Object> map = new HashMap<String, Object>();
		return map;
	}
}
