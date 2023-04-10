package e3ps.workspace.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import e3ps.bom.partlist.PartListMaster;
import e3ps.bom.tbom.TBOMMaster;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.epm.workOrder.WorkOrder;
import e3ps.org.dto.UserDTO;
import e3ps.workspace.ApprovalContract;
import e3ps.workspace.ApprovalLine;
import e3ps.workspace.ApprovalMaster;
import e3ps.workspace.PersistableLineMasterLink;
import e3ps.workspace.dto.ApprovalLineDTO;
import net.sf.json.JSONArray;
import wt.doc.WTDocument;
import wt.enterprise.Managed;
import wt.epm.EPMDocument;
import wt.fc.PagingQueryResult;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;

public class WorkspaceHelper {

	/**
	 * 결재 타입 상수 모음
	 */
	public static final String AGREE_LINE = "검토";
	public static final String APPROVAL_LINE = "결재";
	public static final String RECEIVE_LINE = "수신";
	public static final String SUBMIT_LINE = "기안";

	/**
	 * 결재마스터 상태값 상수 모음
	 */
	public static final String STATE_MASTER_APPROVAL_APPROVING = "승인중";
	public static final String STATE_MASTER_APPROVAL_COMPELTE = "결재완료";
	public static final String STATE_MASTER_AGREE_REJECT = "검토반려";
	public static final String STATE_MASTER_APPROVAL_REJECT = "반려";

	/**
	 * 기안 라인 상태
	 */
	public static final String STATE_SUBMIT_COMPLETE = "제출완료";

	/**
	 * 결재 라인 상태값 상수
	 */
	public static final String STATE_APPROVAL_READY = "대기중";
	public static final String STATE_APPROVAL_APPROVING = "승인중";
	public static final String STATE_APPROVAL_COMPLETE = "결재완료";
	public static final String STATE_APPROVAL_REJECT = "반려됨";

	/**
	 * 검토 라인 상태값 상수
	 */
	public static final String STATE_AGREE_READY = "검토중";
	public static final String STATE_AGREE_COMPLETE = "검토완료";
	public static final String STATE_AGREE_REJECT = "검토반려";

	/**
	 * 수신 라인 상태값 상수
	 */
	public static final String STATE_RECEIVE_READY = "수신확인중";
	public static final String STATE_RECEIVE_COMPLETE = "수신완료";

	/**
	 * 부재중 처리 상태값 상수
	 */
	public static final String STATE_LINE_ABSENCE = "부재중";

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
	private static final String COLUMN_AGREE = "COLUMN_AGREE";
	private static final String COLUMN_RECEIVE = "COLUMN_RECEIVE";
	private static final String COLUMN_COMPLETE = "COLUMN_COMPLETE";
	private static final String COLUMN_REJECT = "COLUMN_REJECT";
	private static final String COLUMN_PROGRESS = "COLUMN_PROGRESS";

	public static final WorkspaceService service = ServiceFactory.getService(WorkspaceService.class);
	public static final WorkspaceHelper manager = new WorkspaceHelper();

	public int getAppObjType(Persistable per) {
		int appObjType = 0;

		return appObjType;
	}

	/**
	 * 객체에 따른 결재제목 가져오기
	 */
	public String getName(Persistable per) {
		if (per instanceof WTDocument) {
			WTDocument document = (WTDocument) per;
			return document.getName();
		} else if (per instanceof WTPart) {
			WTPart part = (WTPart) per;
			return part.getName();
		} else if (per instanceof EPMDocument) {
			EPMDocument epm = (EPMDocument) per;
			return epm.getName();
		} else if (per instanceof ApprovalContract) {
			ApprovalContract contract = (ApprovalContract) per;
			return contract.getName();
		} else if (per instanceof Managed) {
			if (per instanceof PartListMaster) {
				PartListMaster master = (PartListMaster) per;
				return master.getName();
			} else if (per instanceof TBOMMaster) {
				TBOMMaster master = (TBOMMaster) per;
				return master.getName();
			}
		} else if (per instanceof WorkOrder) {
			WorkOrder workOrder = (WorkOrder) per;
			return workOrder.getName();
		}
		return "";
	}

	/**
	 * 검토함
	 */
	public Map<String, Object> agree(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<ApprovalLineDTO> list = new ArrayList<>();

		String approvalTitle = (String) params.get("approvalTitle"); // 결재 제목
		String submiterOid = (String) params.get("submiterOid"); // 작성자
		String receiveFrom = (String) params.get("receiveFrom");
		String receiveTo = (String) params.get("receiveTo");

		// 쿼리문 작성
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ApprovalLine.class, true);
		int idx_master = query.appendClassList(ApprovalMaster.class, true);

		QuerySpecUtils.toInnerJoin(query, ApprovalLine.class, ApprovalMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_master);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.STATE, STATE_AGREE_READY);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.TYPE, AGREE_LINE);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, ApprovalLine.class, ApprovalLine.CREATE_TIMESTAMP, receiveFrom,
				receiveTo);
		QuerySpecUtils.toCreator(query, idx, ApprovalLine.class, submiterOid);
		QuerySpecUtils.toLikeAnd(query, idx, ApprovalLine.class, ApprovalLine.NAME, approvalTitle);
		WTUser sessionUser = CommonUtils.sessionUser();
		if (!CommonUtils.isAdmin()) {
			QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, "ownership.owner.key.id", sessionUser);
		}
		QuerySpecUtils.toOrderBy(query, idx, ApprovalLine.class, ApprovalLine.START_TIME, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ApprovalLine approvalLine = (ApprovalLine) obj[0];
			ApprovalLineDTO column = new ApprovalLineDTO(approvalLine, COLUMN_AGREE);
			list.add(column);
		}
		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	/**
	 * 결재함
	 */
	public Map<String, Object> approval(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<ApprovalLineDTO> list = new ArrayList<>();
		String submiterOid = (String) params.get("submiterOid");
		String receiveFrom = (String) params.get("receiveFrom");
		String receiveTo = (String) params.get("receiveTo");
		String approvalTitle = (String) params.get("approvalTitle");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ApprovalLine.class, true);
		int idx_m = query.appendClassList(ApprovalMaster.class, false);

		QuerySpecUtils.toInnerJoin(query, ApprovalLine.class, ApprovalMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);

		// 쿼리 수정할 예정
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.STATE, STATE_APPROVAL_APPROVING);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.TYPE, APPROVAL_LINE);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, ApprovalLine.class, ApprovalLine.CREATE_TIMESTAMP, receiveFrom,
				receiveTo);
		QuerySpecUtils.toCreator(query, idx, ApprovalLine.class, submiterOid);

		if (!CommonUtils.isAdmin()) {
			WTUser sessionUser = CommonUtils.sessionUser();
			QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, "ownership.owner.key.id", sessionUser);
		}

		QuerySpecUtils.toTimeGreaterAndLess(query, idx, ApprovalLine.class, ApprovalLine.CREATE_TIMESTAMP, receiveFrom,
				receiveTo);
		QuerySpecUtils.toCreator(query, idx, ApprovalLine.class, submiterOid);
		QuerySpecUtils.toLikeAnd(query, idx, ApprovalLine.class, ApprovalLine.NAME, approvalTitle);
		QuerySpecUtils.toOrderBy(query, idx, ApprovalLine.class, ApprovalLine.START_TIME, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ApprovalLine approvalLine = (ApprovalLine) obj[0];
			ApprovalLineDTO column = new ApprovalLineDTO(approvalLine, COLUMN_APPROVAL);
			list.add(column);
		}
		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	/**
	 * 수신함
	 */
	public Map<String, Object> receive(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<ApprovalLineDTO> list = new ArrayList<>();
		String approvalTitle = (String) params.get("approvalTitle");
		String receiveFrom = (String) params.get("receiveFrom");
		String receiveTo = (String) params.get("receiveTo");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ApprovalLine.class, true);
		int idx_master = query.appendClassList(ApprovalMaster.class, true);

		QuerySpecUtils.toInnerJoin(query, ApprovalLine.class, ApprovalMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_master);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.STATE, STATE_RECEIVE_READY);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.TYPE, RECEIVE_LINE);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, ApprovalLine.class, ApprovalLine.CREATE_TIMESTAMP, receiveFrom,
				receiveTo);

		if (!CommonUtils.isAdmin()) {
			WTUser sessionUser = CommonUtils.sessionUser();
			QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, "ownership.owner.key.id", sessionUser);
		}

		QuerySpecUtils.toLikeAnd(query, idx, ApprovalLine.class, ApprovalLine.NAME, approvalTitle);
		QuerySpecUtils.toOrderBy(query, idx, ApprovalLine.class, ApprovalLine.START_TIME, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ApprovalLine line = (ApprovalLine) obj[0];
			ApprovalLineDTO column = new ApprovalLineDTO(line, COLUMN_RECEIVE);
			list.add(column);
		}
		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	/**
	 * 진행함
	 */
	public Map<String, Object> progress(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<ApprovalLineDTO> list = new ArrayList<>();
		String approvalTitle = (String) params.get("approvalTitle");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ApprovalMaster.class, true);

		if (!CommonUtils.isAdmin()) {
			WTUser sessionUser = CommonUtils.sessionUser();
			QuerySpecUtils.toEqualsAnd(query, idx, ApprovalMaster.class, "ownership.owner.key.id", sessionUser);
		}

		query.appendOpenParen();
		QuerySpecUtils.toEqualsOr(query, idx, ApprovalMaster.class, ApprovalMaster.STATE, STATE_APPROVAL_APPROVING);
		QuerySpecUtils.toEqualsOr(query, idx, ApprovalMaster.class, ApprovalMaster.STATE, STATE_AGREE_READY);
		query.appendCloseParen();

		QuerySpecUtils.toLikeAnd(query, idx, ApprovalMaster.class, ApprovalMaster.NAME, approvalTitle);
		QuerySpecUtils.toOrderBy(query, idx, ApprovalMaster.class, ApprovalMaster.START_TIME, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ApprovalMaster master = (ApprovalMaster) obj[0];
			ApprovalLineDTO column = new ApprovalLineDTO(master, COLUMN_PROGRESS);
			list.add(column);
		}
		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	/**
	 * 완료함
	 */
	public Map<String, Object> complete(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<ApprovalLineDTO> list = new ArrayList<>();
		String approvalTitle = (String) params.get("approvalTitle");
		String receiveFrom = (String) params.get("receiveFrom");
		String receiveTo = (String) params.get("receiveTo");
		String type = (String) params.get("type");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ApprovalMaster.class, true);

		if (!CommonUtils.isAdmin()) {
			WTUser sessionUser = CommonUtils.sessionUser();
			QuerySpecUtils.toEqualsAnd(query, idx, ApprovalMaster.class, "ownership.owner.key.id", sessionUser);
		}

		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalMaster.class, ApprovalMaster.STATE,
				STATE_MASTER_APPROVAL_COMPELTE);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalMaster.class, ApprovalMaster.TYPE, type);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, ApprovalMaster.class, ApprovalMaster.CREATE_TIMESTAMP,
				receiveFrom, receiveTo);
		QuerySpecUtils.toLikeAnd(query, idx, ApprovalMaster.class, ApprovalMaster.NAME, approvalTitle);
		QuerySpecUtils.toOrderBy(query, idx, ApprovalMaster.class, ApprovalMaster.START_TIME, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ApprovalMaster master = (ApprovalMaster) obj[0];
			ApprovalLineDTO column = new ApprovalLineDTO(master, COLUMN_COMPLETE);
			list.add(column);
		}
		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	/**
	 * 반려함
	 */
	public Map<String, Object> reject(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<ApprovalLineDTO> list = new ArrayList<>();
		String approvalTitle = (String) params.get("approvalTitle");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ApprovalMaster.class, true);

		if (!CommonUtils.isAdmin()) {
			WTUser sessionUser = CommonUtils.sessionUser();
			QuerySpecUtils.toEqualsAnd(query, idx, ApprovalMaster.class, "ownership.owner.key.id", sessionUser);
		}

		query.appendOpenParen();
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalMaster.class, ApprovalMaster.STATE, STATE_MASTER_AGREE_REJECT);
		QuerySpecUtils.toEqualsOr(query, idx, ApprovalMaster.class, ApprovalMaster.STATE, STATE_MASTER_APPROVAL_REJECT);
		query.appendCloseParen();

		QuerySpecUtils.toLikeAnd(query, idx, ApprovalMaster.class, ApprovalMaster.NAME, approvalTitle);
		QuerySpecUtils.toOrderBy(query, idx, ApprovalMaster.class, ApprovalMaster.START_TIME, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ApprovalMaster master = (ApprovalMaster) obj[0];
			ApprovalLineDTO column = new ApprovalLineDTO(master, COLUMN_REJECT);
			list.add(column);
		}
		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	/**
	 * 기안 라인 가져오기
	 */
	public ApprovalLine getSubmitLine(ApprovalMaster master) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ApprovalLine.class, true);
		int idx_m = query.appendClassList(ApprovalMaster.class, true);

		QuerySpecUtils.toInnerJoin(query, ApprovalLine.class, ApprovalMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, "masterReference.key.id", master);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.ROLE, WORKING_SUBMIT);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.TYPE, SUBMIT_LINE);
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ApprovalLine line = (ApprovalLine) obj[0];
			return line;
		}
		return null;
	}

	/**
	 * 결재 라인 가져오기
	 */
	public ArrayList<ApprovalLine> getApprovalLines(ApprovalMaster master) throws Exception {
		ArrayList<ApprovalLine> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ApprovalLine.class, true);
		int idx_m = query.appendClassList(ApprovalMaster.class, true);

		QuerySpecUtils.toInnerJoin(query, ApprovalLine.class, ApprovalMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, "masterReference.key.id", master);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.ROLE, WORKING_APPROVAL);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.TYPE, APPROVAL_LINE);
		QuerySpecUtils.toOrderBy(query, idx, ApprovalLine.class, ApprovalLine.START_TIME, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ApprovalLine line = (ApprovalLine) obj[0];
			list.add(line);
		}
		return list;
	}

	/**
	 * 검토 라인 가져오기
	 */
	public ArrayList<ApprovalLine> getAgreeLines(ApprovalMaster master) throws Exception {
		ArrayList<ApprovalLine> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ApprovalLine.class, true);
		int idx_m = query.appendClassList(ApprovalMaster.class, true);

		QuerySpecUtils.toInnerJoin(query, ApprovalLine.class, ApprovalMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, "masterReference.key.id", master);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.ROLE, WORKING_AGREE);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.TYPE, AGREE_LINE);
		QuerySpecUtils.toOrderBy(query, idx, ApprovalLine.class, ApprovalLine.START_TIME, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ApprovalLine line = (ApprovalLine) obj[0];
			list.add(line);
		}
		return list;
	}

	/**
	 * 수신 라인 가져오기
	 */
	public ArrayList<ApprovalLine> getReceiveLines(ApprovalMaster master) throws Exception {
		ArrayList<ApprovalLine> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ApprovalLine.class, true);
		int idx_m = query.appendClassList(ApprovalMaster.class, true);

		QuerySpecUtils.toInnerJoin(query, ApprovalLine.class, ApprovalMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, "masterReference.key.id", master);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.ROLE, WORKING_RECEIVE);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.TYPE, RECEIVE_LINE);
		QuerySpecUtils.toOrderBy(query, idx, ApprovalLine.class, ApprovalLine.START_TIME, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ApprovalLine line = (ApprovalLine) obj[0];
			list.add(line);
		}
		return list;
	}

	/**
	 * 결재 마스터 객체 가져오기
	 */
	public ApprovalMaster getMaster(Persistable per) throws Exception {
		QueryResult result = PersistenceHelper.manager.navigate(per, "lineMaster", PersistableLineMasterLink.class);
		if (result.hasMoreElements()) {
			return (ApprovalMaster) result.nextElement();
		}
		return null;
	}

	/**
	 * 결재 이력 그리드용
	 */
	public JSONArray jsonArrayHistory(Persistable per) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();
		ApprovalMaster master = getMaster(per);

		if (master != null) {
			ApprovalLine submit = getSubmitLine(master);
			Map<String, String> data = new HashMap<>();
			data.put("type", submit.getType());
			data.put("role", submit.getRole());
			data.put("name", submit.getName());
			data.put("state", submit.getState());
			data.put("owner", submit.getOwnership().getOwner().getFullName());
			data.put("receiveDate_txt", submit.getStartTime().toString().substring(0, 16));
			data.put("completeDate_txt", submit.getCompleteTime().toString().substring(0, 16));
			data.put("description", submit.getDescription());
			list.add(data);

			ArrayList<ApprovalLine> agreeLines = getAgreeLines(master);
			for (ApprovalLine agreeLine : agreeLines) {
				Map<String, String> map = new HashMap<>();
				map.put("type", agreeLine.getType());
				map.put("role", agreeLine.getRole());
				map.put("name", agreeLine.getName());
				map.put("state", agreeLine.getState());
				map.put("owner", agreeLine.getOwnership().getOwner().getFullName());
				map.put("receiveDate_txt", agreeLine.getStartTime().toString().substring(0, 16));
				map.put("completeDate_txt",
						agreeLine.getCompleteTime() != null ? agreeLine.getCompleteTime().toString().substring(0, 16)
								: "");
				map.put("description", agreeLine.getDescription());
				list.add(map);
			}

			ArrayList<ApprovalLine> approvalLines = getApprovalLines(master);
			for (ApprovalLine approvalLine : approvalLines) {
				Map<String, String> map = new HashMap<>();
				map.put("type", approvalLine.getType());
				map.put("role", approvalLine.getRole());
				map.put("name", approvalLine.getName());
				map.put("state", approvalLine.getState());
				map.put("owner", approvalLine.getOwnership().getOwner().getFullName());
				map.put("receiveDate_txt",
						approvalLine.getStartTime() != null ? approvalLine.getStartTime().toString().substring(0, 16)
								: "");
				map.put("completeDate_txt",
						approvalLine.getCompleteTime() != null
								? approvalLine.getCompleteTime().toString().substring(0, 16)
								: "");
				map.put("description", approvalLine.getDescription());
				list.add(map);
			}

			ArrayList<ApprovalLine> receiveLines = getReceiveLines(master);
			for (ApprovalLine receiveLine : receiveLines) {
				Map<String, String> map = new HashMap<>();
				map.put("type", receiveLine.getType());
				map.put("role", receiveLine.getRole());
				map.put("name", receiveLine.getName());
				map.put("state", receiveLine.getState());
				map.put("owner", receiveLine.getOwnership().getOwner().getFullName());
				map.put("receiveDate_txt", receiveLine.getStartTime().toString().substring(0, 16));
				map.put("completeDate_txt",
						receiveLine.getCompleteTime() != null
								? receiveLine.getCompleteTime().toString().substring(0, 16)
								: "");
				map.put("description", receiveLine.getDescription());
				list.add(map);
			}
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 마스터와 관련된 모든 결재 라인 가져오기
	 */
	public ArrayList<ApprovalLine> getAllLines(ApprovalMaster master) throws Exception {
		ArrayList<ApprovalLine> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ApprovalLine.class, true);
		int idx_m = query.appendClassList(ApprovalMaster.class, false);
		QuerySpecUtils.toInnerJoin(query, ApprovalLine.class, ApprovalMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, "masterReference.key.id", master);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ApprovalLine line = (ApprovalLine) obj[0];
			list.add(line);
		}
		return list;

	}

	/**
	 * 메인페이지에서 보여질 결재 리스트
	 */
	public JSONArray firstPageData(WTUser sessionUser) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ApprovalLine.class, true);
		int idx_m = query.appendClassList(ApprovalMaster.class, true);

		QuerySpecUtils.toInnerJoin(query, ApprovalLine.class, ApprovalMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, "ownership.owner.key.id", sessionUser);

		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}

		query.appendOpenParen();
		SearchCondition sc = new SearchCondition(ApprovalLine.class, ApprovalLine.TYPE, "=", AGREE_LINE);
		query.appendWhere(sc, new int[] { idx });
		query.appendOr();
		sc = new SearchCondition(ApprovalLine.class, ApprovalLine.TYPE, "=", APPROVAL_LINE);
		query.appendWhere(sc, new int[] { idx });
		query.appendCloseParen();

		QuerySpecUtils.toOrderBy(query, idx, ApprovalLine.class, ApprovalLine.CREATE_TIMESTAMP, true);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ApprovalLine approvalLine = (ApprovalLine) obj[0];
			Map<String, String> map = new HashMap<>();
			map.put("name", approvalLine.getName());
			map.put("oid", approvalLine.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("createdDate_txt", CommonUtils.getPersistableTime(approvalLine.getCreateTimestamp()));
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 마지막 검토 라인인지 확인
	 */
	public boolean isEndAgree(ApprovalMaster master) throws Exception {

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ApprovalLine.class, true);
		int idx_m = query.appendClassList(ApprovalMaster.class, true);

		QuerySpecUtils.toInnerJoin(query, ApprovalLine.class, ApprovalMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, "masterReference.key.id", master);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.TYPE, AGREE_LINE);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.STATE, STATE_AGREE_READY);
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.size() > 0) {
			return false;
		}
		return true;
	}

	/**
	 * 마지막 결재 라인인지 체크
	 */
	public boolean isEndApprovalLine(ApprovalMaster master, int sort) throws Exception {

		boolean isEndApprovalLine = true;
		ArrayList<ApprovalLine> list = getApprovalLines(master);
		for (ApprovalLine appLine : list) {
			int compare = appLine.getSort();
			if (sort <= compare) {
				isEndApprovalLine = false;
				break;
			}
		}
		return isEndApprovalLine;
	}

	/**
	 * 모든 결재 라인 삭제
	 */
	public void deleteAllLines(Persistable per) throws Exception {
		ApprovalMaster master = getMaster(per);
		if (master != null) {
			ApprovalLine submitLine = getSubmitLine(master);
			PersistenceHelper.manager.delete(submitLine);

			ArrayList<ApprovalLine> approvalLines = getApprovalLines(master);
			ArrayList<ApprovalLine> agreeLines = getAgreeLines(master);
			ArrayList<ApprovalLine> receiveLines = getReceiveLines(master);

			for (ApprovalLine line : approvalLines) {
				PersistenceHelper.manager.delete(line);
			}
			for (ApprovalLine line : agreeLines) {
				PersistenceHelper.manager.delete(line);
			}
			for (ApprovalLine line : receiveLines) {
				PersistenceHelper.manager.delete(line);
			}
			PersistenceHelper.manager.delete(master);
		}
	}

	public JSONArray loadAllLines(Persistable per) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		ApprovalMaster master = getMaster(per);
		if (master != null) {
			ArrayList<ApprovalLine> approvalLines = getApprovalLines(master);
			ArrayList<ApprovalLine> agreeLines = getAgreeLines(master);
			ArrayList<ApprovalLine> receiveLines = getReceiveLines(master);

			for (ApprovalLine line : agreeLines) {
				WTUser wtUser = (WTUser) line.getOwnership().getOwner().getPrincipal();
				UserDTO dto = new UserDTO(wtUser);
				Map<String, Object> map = new HashMap<>();
				map.put("woid", wtUser.getPersistInfo().getObjectIdentifier().getStringValue());
				map.put("type", "검토");
				map.put("name", dto.getName());
				map.put("id", dto.getId());
				map.put("duty", dto.getDuty());
				map.put("department_name", dto.getDepartment_name());
				map.put("email", dto.getEmail());
				list.add(map);
			}

			int sort = 1;
			for (ApprovalLine line : approvalLines) {
				WTUser wtUser = (WTUser) line.getOwnership().getOwner().getPrincipal();
				UserDTO dto = new UserDTO(wtUser);
				Map<String, Object> map = new HashMap<>();
				map.put("woid", wtUser.getPersistInfo().getObjectIdentifier().getStringValue());
				map.put("sort", sort);
				map.put("type", "결재");
				map.put("name", dto.getName());
				map.put("id", dto.getId());
				map.put("duty", dto.getDuty());
				map.put("department_name", dto.getDepartment_name());
				map.put("email", dto.getEmail());
				list.add(map);
				sort++;
			}

			for (ApprovalLine line : receiveLines) {
				WTUser wtUser = (WTUser) line.getOwnership().getOwner().getPrincipal();
				UserDTO dto = new UserDTO(wtUser);
				Map<String, Object> map = new HashMap<>();
				map.put("woid", wtUser.getPersistInfo().getObjectIdentifier().getStringValue());
				map.put("type", "수신");
				map.put("name", dto.getName());
				map.put("id", dto.getId());
				map.put("duty", dto.getDuty());
				map.put("department_name", dto.getDepartment_name());
				map.put("email", dto.getEmail());
				list.add(map);
			}
		}
		return JSONArray.fromObject(list);
	}
}