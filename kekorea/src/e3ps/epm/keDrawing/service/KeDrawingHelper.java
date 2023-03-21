package e3ps.epm.keDrawing.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import e3ps.common.util.AUIGridUtils;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.ContentUtils;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.epm.keDrawing.KeDrawing;
import e3ps.epm.keDrawing.KeDrawingMaster;
import e3ps.epm.keDrawing.KeDrawingMasterLink;
import e3ps.epm.keDrawing.dto.KeDrawingDTO;
import e3ps.epm.workOrder.WorkOrder;
import e3ps.epm.workOrder.WorkOrderDataLink;
import e3ps.epm.workOrder.WorkOrderProjectLink;
import e3ps.epm.workOrder.dto.WorkOrderDTO;
import e3ps.project.Project;
import net.sf.json.JSONArray;
import wt.fc.PagingQueryResult;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTPrincipal;
import wt.query.QuerySpec;
import wt.queue.ProcessingQueue;
import wt.queue.QueueHelper;
import wt.services.ServiceFactory;
import wt.session.SessionHelper;
import wt.util.WTAttributeNameIfc;

public class KeDrawingHelper {

	public static final KeDrawingHelper manager = new KeDrawingHelper();
	public static final KeDrawingService service = ServiceFactory.getService(KeDrawingService.class);

	/**
	 * 큐 관련 상수
	 */
	private static final String processQueueName = "PdfProcessQueue";
	private static final String className = "e3ps.common.aspose.AsposeUtils";
	private static final String methodName = "pdfToImage";

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		ArrayList<KeDrawingDTO> list = new ArrayList<>();
		Map<String, Object> map = new HashMap<String, Object>();
		String keNumber = (String) params.get("keNumber");
		String name = (String) params.get("name");
		int lotNo = (int) params.get("lotNo");
		boolean latest = (boolean) params.get("latest");
		String creator = (String) params.get("creator");
		String modifier = (String) params.get("modifier");
		String createdFrom = (String) params.get("createdFrom");
		String createdTo = (String) params.get("createdTo");
		String modifiedFrom = (String) params.get("modifiedFrom");
		String modifiedTo = (String) params.get("modifiedTo");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(KeDrawing.class, true);
		int idx_m = query.appendClassList(KeDrawingMaster.class, true);

		QuerySpecUtils.toInnerJoin(query, KeDrawing.class, KeDrawingMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);

		// dwg no
		QuerySpecUtils.toLikeAnd(query, idx_m, KeDrawingMaster.class, KeDrawingMaster.KE_NUMBER, keNumber);
		// drawing title
		QuerySpecUtils.toLikeAnd(query, idx_m, KeDrawingMaster.class, KeDrawingMaster.NAME, name);

		// lot no
		if (lotNo != 0) {
			QuerySpecUtils.toEqualsAnd(query, idx_m, KeDrawingMaster.class, KeDrawingMaster.LOT_NO, lotNo);
		}

		// 버전
		if (latest) {
			QuerySpecUtils.toBooleanAnd(query, idx, KeDrawing.class, KeDrawing.LATEST, true);
		} else {

			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}

			query.appendOpenParen();
			QuerySpecUtils.toBoolean(query, idx, KeDrawing.class, KeDrawing.LATEST, true);
			QuerySpecUtils.toBooleanOr(query, idx, KeDrawing.class, KeDrawing.LATEST, false);
			query.appendCloseParen();
		}

		// 작성자
		QuerySpecUtils.toCreator(query, idx_m, KeDrawingMaster.class, creator);
		// 수정자
		QuerySpecUtils.toCreator(query, idx, KeDrawing.class, modifier);
		// 작성일
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, KeDrawing.class, KeDrawing.CREATE_TIMESTAMP, createdFrom,
				createdTo);
		// 작성일
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, KeDrawing.class, KeDrawing.MODIFY_TIMESTAMP, modifiedFrom,
				modifiedTo);

		QuerySpecUtils.toOrderBy(query, idx, KeDrawing.class, KeDrawing.CREATE_TIMESTAMP, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			KeDrawing keDrawing = (KeDrawing) obj[0];
			KeDrawingDTO column = new KeDrawingDTO(keDrawing);
			list.add(column);
		}

		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	/**
	 * 마지막 KE 도면인지 확인 하는 함수
	 */
	public boolean isLast(KeDrawingMaster master) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(KeDrawing.class, true);
		int idx_m = query.appendClassList(KeDrawingMaster.class, false);
		QuerySpecUtils.toInnerJoin(query, KeDrawing.class, KeDrawingMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);
		QuerySpecUtils.toEqualsAnd(query, idx, KeDrawing.class, "masterReference.key.id",
				master.getPersistInfo().getObjectIdentifier().getId());
		QueryResult result = PersistenceHelper.manager.find(query);
		return result.size() == 1 ? true : false;
	}

	/**
	 * 현재버전의 KE 도면의 이전 버전의 도면을 가져오는 함수
	 */
	public KeDrawing getPreKeDrawing(KeDrawing keDrawing) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(KeDrawing.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, KeDrawing.class, KeDrawing.VERSION, keDrawing.getVersion() - 1);
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			return (KeDrawing) obj[0];
		}
		return null;
	}

	/**
	 * KE 도면 등록, 수정중 중복 체크
	 */

	public Map<String, Object> isValid(ArrayList<KeDrawingDTO> addRow, ArrayList<KeDrawingDTO> editRow)
			throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		for (KeDrawingDTO dto : addRow) {
			String keNumber = dto.getKeNumber();
			int lotNo = dto.getLotNo();
			boolean isExist = exist(keNumber, lotNo);
			if (isExist) {
				result.put("isExist", true); // 존재하는거 true
				result.put("msg", "LOT NO가 = " + lotNo + "이고 도번이 = " + keNumber + "가 이미 존재합니다.");
				return result;
			}
		}

		for (KeDrawingDTO dto : editRow) {
			String oid = dto.getOid();
			KeDrawingMaster master = (KeDrawingMaster) CommonUtils.getObject(dto.getMoid());
			String orgKeNumber = master.getKeNumber();
			int orgLotNo = master.getLotNo();
			String keNumber = dto.getKeNumber();
			int lotNo = dto.getLotNo();

			// 원본 도면의 번호 혹은 LON NO 가 변경 될시 체크만한다...
			if (!orgKeNumber.equals(keNumber) || orgLotNo != lotNo) {
				boolean isExist = exist(keNumber, lotNo);
				if (isExist) {
					result.put("isExist", true); // 존재하는거 true
					result.put("msg", "LOT NO가 = " + lotNo + "이고 도번이 = " + keNumber + "가 이미 존재합니다.");
					return result;
				}
			}
		}
		// 아무것도 없다면 false
		result.put("isExist", false);
		return result;
	}

	/**
	 * KE 도면 번호+LOT NO으로 중복 있는지 확인
	 */
	private boolean exist(String keNumber, int lotNo) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx_m = query.appendClassList(KeDrawingMaster.class, true);
		int idx = query.appendClassList(KeDrawing.class, true);
		QuerySpecUtils.toInnerJoin(query, KeDrawingMaster.class, KeDrawing.class, WTAttributeNameIfc.ID_NAME,
				"masterReference.key.id", idx_m, idx);
		QuerySpecUtils.toEqualsAnd(query, idx_m, KeDrawingMaster.class, KeDrawingMaster.KE_NUMBER, keNumber);
		QuerySpecUtils.toEqualsAnd(query, idx_m, KeDrawingMaster.class, KeDrawingMaster.LOT_NO, lotNo);
		QueryResult result = PersistenceHelper.manager.find(query);
		return result.size() > 0 ? true : false;
	}

	/**
	 * KE 도면과 관련된 도면일람표 정보를 가져온다
	 */
	public JSONArray jsonArrayAui(String oid) throws Exception {
		KeDrawing keDrawing = (KeDrawing) CommonUtils.getObject(oid);
		ArrayList<WorkOrderDTO> list = new ArrayList<>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WorkOrderDataLink.class, true);
		int idx_k = query.appendClassList(KeDrawing.class, true);
		int idx_p = query.appendClassList(Project.class, true);
		int idx_w = query.appendClassList(WorkOrder.class, true);
		int idx_link = query.appendClassList(WorkOrderProjectLink.class, true);

		QuerySpecUtils.toInnerJoin(query, WorkOrderDataLink.class, KeDrawing.class, "roleBObjectRef.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_k);
		QuerySpecUtils.toInnerJoin(query, WorkOrderDataLink.class, WorkOrder.class, "roleAObjectRef.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_w);
		QuerySpecUtils.toEqualsAnd(query, idx, WorkOrderDataLink.class, "roleBObjectRef.key.id",
				keDrawing.getPersistInfo().getObjectIdentifier().getId());

		QuerySpecUtils.toInnerJoin(query, WorkOrder.class, WorkOrderProjectLink.class, WTAttributeNameIfc.ID_NAME,
				"roleAObjectRef.key.id", idx_w, idx_link);
		QuerySpecUtils.toInnerJoin(query, Project.class, WorkOrderProjectLink.class, WTAttributeNameIfc.ID_NAME,
				"roleBObjectRef.key.id", idx_p, idx_link);

		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WorkOrderProjectLink link = (WorkOrderProjectLink) obj[4];
			WorkOrderDTO dto = new WorkOrderDTO(link);
			list.add(dto);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * KE 도면 버전이력 정보 가져오는 함수
	 */
	public JSONArray history(KeDrawingMaster master) throws Exception {
		ArrayList<KeDrawingDTO> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(KeDrawing.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, KeDrawing.class, "masterReference.key.id",
				master.getPersistInfo().getObjectIdentifier().getId());
		QuerySpecUtils.toOrderBy(query, idx, KeDrawing.class, KeDrawing.VERSION, true);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			KeDrawing keDrawing = (KeDrawing) obj[0];
			KeDrawingDTO dto = new KeDrawingDTO(keDrawing);
			list.add(dto);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * PDF -> 미리보기 파일 생성 백그라운드 메소드 서버 실행
	 */
	public void postAfterAction(String oid, String pdfPath) throws Exception {
		WTPrincipal principal = SessionHelper.manager.getPrincipal();
		ProcessingQueue queue = (ProcessingQueue) QueueHelper.manager.getQueue(processQueueName, ProcessingQueue.class);

		Hashtable<String, String> hash = new Hashtable<>();
		hash.put("oid", oid);
		hash.put("pdfPath", pdfPath);

		Class[] argClasses = { Hashtable.class };
		Object[] argObjects = { hash };

		queue.addEntry(principal, methodName, className, argClasses, argObjects);
	}

	/**
	 * KE 도면 등록시 CREO, AUTOCAD 규격 체크 ..
	 */
	public Map<String, Object> numberValidate(ArrayList<KeDrawingDTO> addRow, ArrayList<KeDrawingDTO> editRow)
			throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();

		result.put("isExist", false);
		return result;
	}

	/**
	 * 도면 일람표에 사용된 데이터들 가져오기
	 */
	public JSONArray getData(WorkOrder workOrder) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WorkOrder.class, true);
		int idx_l = query.appendClassList(WorkOrderDataLink.class, true);

		QuerySpecUtils.toInnerJoin(query, WorkOrder.class, WorkOrderDataLink.class, WTAttributeNameIfc.ID_NAME,
				"roleAObjectRef.key.id", idx, idx_l);
		QuerySpecUtils.toEqualsAnd(query, idx_l, WorkOrderDataLink.class, "roleAObjectRef.key.id",
				workOrder.getPersistInfo().getObjectIdentifier().getId());
		QuerySpecUtils.toOrderBy(query, idx_l, WorkOrderDataLink.class, WorkOrderDataLink.SORT, true);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WorkOrder order = (WorkOrder) obj[0];
			WorkOrderDataLink link = (WorkOrderDataLink) obj[1];
			Map<String, Object> map = new HashMap();

			map.put("oid", order.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("dataType", link.getDataType());
			map.put("lotNo", link.getLotNo());
			map.put("current", link.getCurrent());
			map.put("createdData_txt", CommonUtils.getPersistableTime(link.getCreateTimestamp()));
			map.put("note", link.getNote());
			Persistable per = link.getData();
			if (per instanceof KeDrawing) {
				KeDrawing keDrawing = (KeDrawing) per;
				map.put("name", keDrawing.getMaster().getName());
				map.put("number", keDrawing.getMaster().getKeNumber());
				map.put("rev", keDrawing.getVersion());
				map.put("preView", ContentUtils.getPreViewBase64(keDrawing));
				map.put("primary", AUIGridUtils.primaryTemplate(keDrawing));
				KeDrawing latest = getLatest(keDrawing);
				map.put("latest", latest.getVersion());
			}
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 최신 KE 도면
	 */
	public KeDrawing getLatest(KeDrawing keDrawing) throws Exception {
		return getLatest(keDrawing.getMaster().getKeNumber());
	}

	public KeDrawing getLatest(String number) throws Exception {

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(KeDrawing.class, true);
		int idx_m = query.appendClassList(KeDrawingMaster.class, false);

		QuerySpecUtils.toInnerJoin(query, KeDrawing.class, KeDrawingMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);
		QuerySpecUtils.toEqualsAnd(query, idx_m, KeDrawingMaster.class, KeDrawingMaster.KE_NUMBER, number);
		QuerySpecUtils.toBooleanAnd(query, idx, KeDrawing.class, KeDrawing.LATEST, true);
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			KeDrawing latest = (KeDrawing) obj[0];
			return latest;
		}
		return null;
	}
}
