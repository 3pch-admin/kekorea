package e3ps.workspace.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import e3ps.bom.partlist.PartListData;
import e3ps.bom.partlist.PartListMaster;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.DateUtils;
import e3ps.common.util.IBAUtils;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.doc.request.RequestDocument;
import e3ps.org.People;
import e3ps.workspace.ApprovalContract;
import e3ps.workspace.ApprovalContractPersistableLink;
import e3ps.workspace.ApprovalImpl;
import e3ps.workspace.ApprovalLine;
import e3ps.workspace.ApprovalMaster;
import e3ps.workspace.PersistableLineMasterLink;
import e3ps.workspace.dto.ApprovalLineDTO;
import e3ps.workspace.notice.Notice;
import net.sf.json.JSONArray;
import wt.doc.WTDocument;
import wt.enterprise.Managed;
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

public class WorkspaceHelper {

	/**
	 * 결재 타입 상수 모음
	 */
	public static final String APPROVAL_LINE = "결재";
	public static final String AGREE_LINE = "검토";
	public static final String RECEIVE_LINE = "수신";
	public static final String SUBMIT_LINE = "기안";

	public static final String STATE_MASTER_APPROVING = "승인중";
	public static final String STATE_MASTER_APPROVAL_COMPLETE = "결재완료";
	public static final String STATE_MASTER_REJECT = "반려됨";
	public static final String STATE_MASTER_AGREE_REJECT = "검토반려";

	/**
	 * 기안 라인 상태값 상수
	 */
	public static final String STATE_SUBMIT_COMPLETE = "제출됨";

	/**
	 * 결재 라인 상태값 상수
	 */
	public static final String STATE_APPROVAL_READY = "대기중";
	public static final String STATE_APPROVAL_APPROVING = "승인중";
	public static final String STATE_APPROVAL_COMPLETE = "결재완료";
	public static final String STATE_APPROVAL_RETURN = "반려됨";

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
			PartListMaster master = (PartListMaster) per;
			return master.getName();
		}
		return "";
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
		QuerySpecUtils.toOrderBy(query, idx, ApprovalLine.class, ApprovalLine.MODIFY_TIMESTAMP, false);

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
		boolean isAdmin = CommonUtils.isAdmin();
		String name = (String) params.get("name");
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
		QuerySpecUtils.toTimeGreaterEqualsThan(query, idx, ApprovalLine.class, ApprovalLine.CREATE_TIMESTAMP,
				receiveFrom);
		QuerySpecUtils.toTimeLessEqualsThan(query, idx, ApprovalLine.class, ApprovalLine.CREATE_TIMESTAMP, receiveTo);
		QuerySpecUtils.toCreator(query, idx, ApprovalLine.class, submiterOid);
		QuerySpecUtils.toOrderBy(query, idx, ApprovalLine.class, ApprovalLine.CREATE_TIMESTAMP, true);

		if (!isAdmin) {
			WTUser sessionUser = CommonUtils.sessionUser();
			QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, "ownership.owner.key.id",
					sessionUser.getPersistInfo().getObjectIdentifier().getId());
		}

		if (!StringUtils.isNull(submiterOid)) {
			WTUser wtUser = (WTUser) CommonUtils.getObject(submiterOid);
			QuerySpecUtils.toEqualsAnd(query, idx, ApprovalMaster.class, "ownership.owner.key.id",
					wtUser.getPersistInfo().getObjectIdentifier().getId());
		}

		if (!StringUtils.isNull(approvalTitle)) {
			QuerySpecUtils.toLikeAnd(query, idx, ApprovalLine.class, ApprovalLine.NAME, approvalTitle);
		}

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

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ApprovalLine.class, true);
		int idx_master = query.appendClassList(ApprovalMaster.class, true);

		QuerySpecUtils.toInnerJoin(query, ApprovalLine.class, ApprovalMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_master);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.STATE, STATE_RECEIVE_READY);
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, ApprovalLine.TYPE, RECEIVE_LINE);

		WTUser sessionUser = CommonUtils.sessionUser();
		if (!CommonUtils.isAdmin()) {
			QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, "ownership.owner.key.id",
					sessionUser.getPersistInfo().getObjectIdentifier().getId());
		}
		QuerySpecUtils.toOrderBy(query, idx, ApprovalLine.class, ApprovalLine.MODIFY_TIMESTAMP, false);

		if (!StringUtils.isNull(approvalTitle)) {
			QuerySpecUtils.toLikeAnd(query, idx, ApprovalLine.class, ApprovalLine.NAME, approvalTitle);
		}

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

		WTUser sessionUser = CommonUtils.sessionUser();
		if (!CommonUtils.isAdmin()) {
			QuerySpecUtils.toEqualsAnd(query, idx, ApprovalMaster.class, "ownership.owner.key.id",
					sessionUser.getPersistInfo().getObjectIdentifier().getId());
		}
		query.appendOpenParen();
		QuerySpecUtils.toEqualsOr(query, idx, ApprovalMaster.class, ApprovalMaster.STATE, STATE_APPROVAL_APPROVING);
		QuerySpecUtils.toEqualsOr(query, idx, ApprovalMaster.class, ApprovalMaster.STATE, STATE_AGREE_READY);
		query.appendCloseParen();
		QuerySpecUtils.toOrderBy(query, idx, ApprovalMaster.class, ApprovalMaster.MODIFY_TIMESTAMP, false);

		if (!StringUtils.isNull(approvalTitle)) {
			QuerySpecUtils.toLikeAnd(query, idx, ApprovalMaster.class, ApprovalMaster.NAME, approvalTitle);
		}

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

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ApprovalMaster.class, true);

		WTUser sessionUser = CommonUtils.sessionUser();
		if (!CommonUtils.isAdmin()) {
			QuerySpecUtils.toEqualsAnd(query, idx, ApprovalMaster.class, "ownership.owner.key.id",
					sessionUser.getPersistInfo().getObjectIdentifier().getId());
		}

		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalMaster.class, ApprovalMaster.STATE,
				STATE_MASTER_APPROVAL_COMPLETE);

		QuerySpecUtils.toOrderBy(query, idx, ApprovalMaster.class, ApprovalMaster.MODIFY_TIMESTAMP, false);

		if (!StringUtils.isNull(approvalTitle)) {
			QuerySpecUtils.toLikeAnd(query, idx, ApprovalMaster.class, ApprovalMaster.NAME, approvalTitle);
		}
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

		WTUser sessionUser = CommonUtils.sessionUser();
		if (!CommonUtils.isAdmin()) {
			QuerySpecUtils.toEqualsAnd(query, idx, ApprovalMaster.class, "ownership.owner.key.id",
					sessionUser.getPersistInfo().getObjectIdentifier().getId());
		}

		query.appendOpenParen();
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalMaster.class, ApprovalMaster.STATE, STATE_MASTER_AGREE_REJECT);
		QuerySpecUtils.toEqualsOr(query, idx, ApprovalMaster.class, ApprovalMaster.STATE, STATE_MASTER_REJECT);
		query.appendCloseParen();

		QuerySpecUtils.toOrderBy(query, idx, ApprovalMaster.class, ApprovalMaster.MODIFY_TIMESTAMP, false);

		if (!StringUtils.isNull(approvalTitle)) {
			QuerySpecUtils.toLikeAnd(query, idx, ApprovalMaster.class, ApprovalMaster.NAME, approvalTitle);
		}

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
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, "masterReference.key.id",
				master.getPersistInfo().getObjectIdentifier().getId());
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
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, "masterReference.key.id",
				master.getPersistInfo().getObjectIdentifier().getId());
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
		QuerySpecUtils.toEqualsAnd(query, idx, ApprovalLine.class, "masterReference.key.id",
				master.getPersistInfo().getObjectIdentifier().getId());
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
				map.put("completeDate_txt", agreeLine.getCompleteTime().toString().substring(0, 16));
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
				map.put("receiveDate_txt", approvalLine.getStartTime().toString().substring(0, 16));
				map.put("completeDate_txt", approvalLine.getCompleteTime().toString().substring(0, 16));
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
				map.put("completeDate_txt", receiveLine.getCompleteTime().toString().substring(0, 16));
				map.put("description", receiveLine.getDescription());
				list.add(map);
			}
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 객체에 따른 결재 의견 입력
	 */
	public String getDescription(Persistable persistable) {
		// TODO Auto-generated method stub
		return null;
	}
}