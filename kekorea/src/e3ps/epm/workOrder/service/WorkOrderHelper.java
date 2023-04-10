package e3ps.epm.workOrder.service;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.bom.partlist.dto.PartListDTO;
import e3ps.bom.tbom.TBOMData;
import e3ps.bom.tbom.TBOMMaster;
import e3ps.bom.tbom.TBOMMasterDataLink;
import e3ps.bom.tbom.TBOMMasterProjectLink;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.ContentUtils;
import e3ps.common.util.DateUtils;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.epm.keDrawing.KeDrawing;
import e3ps.epm.keDrawing.KeDrawingMaster;
import e3ps.epm.workOrder.WorkOrder;
import e3ps.epm.workOrder.WorkOrderDataLink;
import e3ps.epm.workOrder.WorkOrderProjectLink;
import e3ps.epm.workOrder.dto.WorkOrderDTO;
import e3ps.project.Project;
import e3ps.project.ProjectUserLink;
import e3ps.project.template.Template;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import wt.fc.PagingQueryResult;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.queue.ProcessingQueue;
import wt.queue.QueueHelper;
import wt.services.ServiceFactory;
import wt.session.SessionHelper;
import wt.util.WTAttributeNameIfc;
import wt.util.WTProperties;

public class WorkOrderHelper {

	public static final WorkOrderHelper manager = new WorkOrderHelper();
	public static final WorkOrderService service = ServiceFactory.getService(WorkOrderService.class);

	private static final String processQueueName = "WorkOrderProcessQueue";
	private static final String className = "e3ps.common.aspose.AsposeUtils";
	private static final String methodName = "attachMergePdf";

	/**
	 * 도면 일람표 조회
	 */
	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();

		String kekNumber = (String) params.get("kekNumber");
		String keNumber = (String) params.get("keNumber");
		String pdateFrom = (String) params.get("pdateFrom");
		String pdateTo = (String) params.get("pdateTo");
		String userId = (String) params.get("userId");
		String kekState = (String) params.get("kekState");
		String model = (String) params.get("model");
		String customer_name = (String) params.get("customer_name");
		String install_name = (String) params.get("install_name");
		String projectType = (String) params.get("projectType");
		String machineOid = (String) params.get("machineOid");
		String elecOid = (String) params.get("elecOid");
		String softOid = (String) params.get("softOid");
		String mak_name = (String) params.get("mak_name");
		String detail_name = (String) params.get("detail_name");
		String template = (String) params.get("template");
		String description = (String) params.get("description");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WorkOrder.class, true);
		QuerySpecUtils.toOrderBy(query, idx, WorkOrder.class, WorkOrder.CREATE_TIMESTAMP, true);
		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();

		JSONArray list = new JSONArray();
		boolean isData = true;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WorkOrder workOrder = (WorkOrder) obj[0];

			JSONObject node = new JSONObject();

			QuerySpec _query = new QuerySpec();
			int _idx = _query.appendClassList(WorkOrder.class, true);
			int _idx_p = _query.appendClassList(Project.class, true);
			int _idx_link = _query.appendClassList(WorkOrderProjectLink.class, true);

			QuerySpecUtils.toEqualsAnd(_query, _idx_link, WorkOrderProjectLink.class, "roleAObjectRef.key.id",
					workOrder.getPersistInfo().getObjectIdentifier().getId());
			QuerySpecUtils.toInnerJoin(_query, WorkOrder.class, WorkOrderProjectLink.class, WTAttributeNameIfc.ID_NAME,
					"roleAObjectRef.key.id", _idx, _idx_link);
			QuerySpecUtils.toInnerJoin(_query, Project.class, WorkOrderProjectLink.class, WTAttributeNameIfc.ID_NAME,
					"roleBObjectRef.key.id", _idx_p, _idx_link);
			QuerySpecUtils.toLikeAnd(_query, _idx_p, Project.class, Project.KEK_NUMBER, kekNumber);
			QuerySpecUtils.toLikeAnd(_query, _idx_p, Project.class, Project.KE_NUMBER, keNumber);
			QuerySpecUtils.toTimeGreaterAndLess(_query, _idx_p, Project.class, Project.P_DATE, pdateFrom, pdateTo);
			QuerySpecUtils.toLikeAnd(_query, _idx_p, Project.class, Project.USER_ID, userId);
			QuerySpecUtils.toLikeAnd(_query, _idx_p, Project.class, Project.KEK_STATE, kekState);
			QuerySpecUtils.toLikeAnd(_query, _idx_p, Project.class, Project.MODEL, model);

			if (!StringUtils.isNull(customer_name)) {
				CommonCode customerCode = (CommonCode) CommonUtils.getObject(customer_name);
				QuerySpecUtils.toEqualsAnd(_query, _idx_p, Project.class, "customerReference.key.id",
						customerCode.getPersistInfo().getObjectIdentifier().getId());
			}

			if (!StringUtils.isNull(install_name)) {
				CommonCode installCode = (CommonCode) CommonUtils.getObject(install_name);
				QuerySpecUtils.toEqualsAnd(_query, _idx_p, Project.class, "installReference.key.id",
						installCode.getPersistInfo().getObjectIdentifier().getId());
			}

			if (!StringUtils.isNull(projectType)) {
				CommonCode projectTypeCode = (CommonCode) CommonUtils.getObject(projectType);
				QuerySpecUtils.toEqualsAnd(_query, _idx_p, Project.class, "projectTypeReference.key.id",
						projectTypeCode.getPersistInfo().getObjectIdentifier().getId());
			}

			if (!StringUtils.isNull(machineOid)) {
				WTUser machine = (WTUser) CommonUtils.getObject(machineOid);
				CommonCode machineCode = CommonCodeHelper.manager.getCommonCode("MACHINE", "USER_TYPE");
				int idx_plink = _query.appendClassList(ProjectUserLink.class, false);
				int idx_u = _query.appendClassList(WTUser.class, false);

				QuerySpecUtils.toInnerJoin(_query, Project.class, ProjectUserLink.class, WTAttributeNameIfc.ID_NAME,
						"roleAObjectRef.key.id", _idx_p, idx_plink);
				QuerySpecUtils.toInnerJoin(_query, WTUser.class, ProjectUserLink.class, WTAttributeNameIfc.ID_NAME,
						"roleBObjectRef.key.id", idx_u, idx_plink);
				QuerySpecUtils.toEqualsAnd(_query, idx_plink, ProjectUserLink.class, "roleBObjectRef.key.id",
						machine.getPersistInfo().getObjectIdentifier().getId());
				QuerySpecUtils.toEqualsAnd(_query, idx_plink, ProjectUserLink.class, "projectUserTypeReference.key.id",
						machineCode.getPersistInfo().getObjectIdentifier().getId());
			}

			if (!StringUtils.isNull(elecOid)) {
				WTUser elec = (WTUser) CommonUtils.getObject(machineOid);
				CommonCode elecCode = CommonCodeHelper.manager.getCommonCode("MACHINE", "USER_TYPE");
				int idx_plink = _query.appendClassList(ProjectUserLink.class, false);
				int idx_u = _query.appendClassList(WTUser.class, false);

				QuerySpecUtils.toInnerJoin(_query, Project.class, ProjectUserLink.class, WTAttributeNameIfc.ID_NAME,
						"roleAObjectRef.key.id", _idx_p, idx_plink);
				QuerySpecUtils.toInnerJoin(_query, WTUser.class, ProjectUserLink.class, WTAttributeNameIfc.ID_NAME,
						"roleBObjectRef.key.id", idx_u, idx_plink);
				QuerySpecUtils.toEqualsAnd(_query, idx_plink, ProjectUserLink.class, "roleBObjectRef.key.id",
						elec.getPersistInfo().getObjectIdentifier().getId());
				QuerySpecUtils.toEqualsAnd(_query, idx_plink, ProjectUserLink.class, "projectUserTypeReference.key.id",
						elecCode.getPersistInfo().getObjectIdentifier().getId());
			}

			if (!StringUtils.isNull(softOid)) {
				WTUser soft = (WTUser) CommonUtils.getObject(machineOid);
				CommonCode softCode = CommonCodeHelper.manager.getCommonCode("MACHINE", "USER_TYPE");
				int idx_plink = _query.appendClassList(ProjectUserLink.class, false);
				int idx_u = _query.appendClassList(WTUser.class, false);

				QuerySpecUtils.toInnerJoin(_query, Project.class, ProjectUserLink.class, WTAttributeNameIfc.ID_NAME,
						"roleAObjectRef.key.id", _idx_p, idx_plink);
				QuerySpecUtils.toInnerJoin(_query, WTUser.class, ProjectUserLink.class, WTAttributeNameIfc.ID_NAME,
						"roleBObjectRef.key.id", idx_u, idx_plink);
				QuerySpecUtils.toEqualsAnd(_query, idx_plink, ProjectUserLink.class, "roleBObjectRef.key.id",
						soft.getPersistInfo().getObjectIdentifier().getId());
				QuerySpecUtils.toEqualsAnd(_query, idx_plink, ProjectUserLink.class, "projectUserTypeReference.key.id",
						softCode.getPersistInfo().getObjectIdentifier().getId());
			}

			if (!StringUtils.isNull(mak_name)) {
				CommonCode makCode = (CommonCode) CommonUtils.getObject(mak_name);
				QuerySpecUtils.toEqualsAnd(_query, _idx_p, Project.class, "makReference.key.id",
						makCode.getPersistInfo().getObjectIdentifier().getId());
			}

			if (!StringUtils.isNull(detail_name)) {
				CommonCode detailCode = (CommonCode) CommonUtils.getObject(detail_name);
				QuerySpecUtils.toEqualsAnd(_query, _idx_p, Project.class, "detailReference.key.id",
						detailCode.getPersistInfo().getObjectIdentifier().getId());
			}

			if (!StringUtils.isNull(template)) {
				Template t = (Template) CommonUtils.getObject(template);
				QuerySpecUtils.toEqualsAnd(_query, _idx_p, Project.class, "templateReference.key.id",
						t.getPersistInfo().getObjectIdentifier().getId());
			}

			QuerySpecUtils.toLikeAnd(_query, _idx_p, Project.class, Project.DESCRIPTION, description);
			QuerySpecUtils.toOrderBy(_query, _idx_p, Project.class, Project.P_DATE, false);
			QueryResult group = PersistenceHelper.manager.find(_query);
			System.out.println("_query=" + _query);
			int isNode = 1;
			JSONArray children = new JSONArray();
			if (group.size() == 0) {
				isData = false;
			}
			while (group.hasMoreElements()) {
				Object[] oo = (Object[]) group.nextElement();
				WorkOrderProjectLink link = (WorkOrderProjectLink) oo[2];
				WorkOrderDTO dto = new WorkOrderDTO(link);
				node.put("oid", workOrder.getPersistInfo().getObjectIdentifier().getStringValue());
				node.put("name", workOrder.getName());
				if (isNode == 1) {
					node.put("poid", dto.getPoid());
					node.put("projectType_name", dto.getProjectType_name());
					node.put("customer_name", dto.getCustomer_name());
					node.put("install_name", dto.getInstall_name());
					node.put("mak_name", dto.getMak_name());
					node.put("detail_name", dto.getDetail_name());
					node.put("kekNumber", dto.getKekNumber());
					node.put("keNumber", dto.getKeNumber());
					node.put("userId", dto.getUserId());
					node.put("description", dto.getDescription());
					node.put("state", dto.getState());
					node.put("model", dto.getModel());
					node.put("pdate_txt", dto.getPdate_txt());
					node.put("creator", dto.getCreator());
					node.put("createdDate_txt", dto.getCreatedDate_txt());
					node.put("cover", dto.getCover());
					node.put("primary", dto.getPrimary());
				} else {
					JSONObject data = new JSONObject();
					data.put("name", dto.getName());
					data.put("oid", dto.getOid());
					data.put("poid", dto.getPoid());
					data.put("projectType_name", dto.getProjectType_name());
					data.put("customer_name", dto.getCustomer_name());
					data.put("install_name", dto.getInstall_name());
					data.put("mak_name", dto.getMak_name());
					data.put("detail_name", dto.getDetail_name());
					data.put("kekNumber", dto.getKekNumber());
					data.put("keNumber", dto.getKeNumber());
					data.put("userId", dto.getUserId());
					data.put("description", dto.getDescription());
					data.put("state", dto.getState());
					data.put("model", dto.getModel());
					data.put("pdate_txt", dto.getPdate_txt());
					data.put("creator", dto.getCreator());
					data.put("createdDate_txt", dto.getCreatedDate_txt());
					data.put("cover", dto.getCover());
					data.put("primary", dto.getPrimary());
					children.add(data);
				}
				isNode++;
			}
			if (isData) {
				node.put("children", children);
				list.add(node);
			}
		}
		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	/**
	 * 도면 일람표 다음 번호
	 */
	public String getNextNumber(String param) throws Exception {
		String preFix = DateUtils.getTodayString();
		String number = param + "-" + preFix + "-";
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WorkOrder.class, true);
		QuerySpecUtils.toLikeAnd(query, idx, WorkOrder.class, WorkOrder.NUMBER, number.toUpperCase());
		QuerySpecUtils.toOrderBy(query, idx, WorkOrder.class, WorkOrder.NUMBER, true);
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WorkOrder workOrder = (WorkOrder) obj[0];

			String s = workOrder.getNumber().substring(workOrder.getNumber().lastIndexOf("-") + 1);

			int ss = Integer.parseInt(s) + 1;
			DecimalFormat d = new DecimalFormat("0000");
			number += d.format(ss);
		} else {
			number += "0001";
		}
		return number;
	}

	/**
	 * 도면 일람표에 추가될 도면 데이터 가져오기
	 */
	public Map<String, Object> getData(String number) throws Exception {
		Map<String, Object> map = new HashMap<>();

		// KEK 도면을 먼저 검색한다

//		QuerySpec first = new QuerySpec();
//		int idx = first.appendClassList(EPMDocument.class, true);
//		int idx_m = first.appendClassList(EPMDocumentMaster.class, false);
//		
//		QuerySpecUtils.toInnerJoin(first, EPMDocument.class, EPMDocumentMaster.class, "masterReference.key.id", WTAttributeNameIfc.ID_NAME, idx, idx_m);
//		QuerySpecUtils.toEqualsAnd(first, idx_m, EPMDocumentMaster.class, EPMDocumentMaster.NUMBER, number);

		QuerySpec query = new QuerySpec();

		int idx = query.appendClassList(KeDrawing.class, true);
		int idx_m = query.appendClassList(KeDrawingMaster.class, true);
		QuerySpecUtils.toInnerJoin(query, KeDrawing.class, KeDrawingMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);
		QuerySpecUtils.toBooleanAnd(query, idx, KeDrawing.class, KeDrawing.LATEST, true);
		QuerySpecUtils.toEqualsAnd(query, idx_m, KeDrawingMaster.class, KeDrawingMaster.KE_NUMBER, number);
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			KeDrawing keDrawing = (KeDrawing) obj[0];
			KeDrawingMaster master = (KeDrawingMaster) obj[1];
			map.put("name", master.getName());
			map.put("rev", keDrawing.getVersion());
			map.put("lotNo", master.getLotNo());
			map.put("current", keDrawing.getVersion());
			map.put("ok", true);
			map.put("preView", ContentUtils.getPreViewBase64(keDrawing));
			map.put("oid", keDrawing.getPersistInfo().getObjectIdentifier().getStringValue());
		}
		return map;
	}

	/**
	 * 도면 일람표 등록시 백그라운드 메소드 서버 큐에서 PDF병합
	 */
	public void postAfterAction(String oid) throws Exception {
		WTPrincipal principal = SessionHelper.manager.getPrincipal();
		ProcessingQueue queue = (ProcessingQueue) QueueHelper.manager.getQueue(processQueueName, ProcessingQueue.class);

		Hashtable<String, String> hash = new Hashtable<>();
		hash.put("oid", oid);

		Class[] argClasses = { Hashtable.class };
		Object[] argObjects = { hash };

		queue.addEntry(principal, methodName, className, argClasses, argObjects);
	}

	/**
	 * AUI 그리드 작번 리스트 INCLUDE 페이지
	 */
	public net.sf.json.JSONArray jsonArrayAui(String oid) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();
		WorkOrder workOrder = (WorkOrder) CommonUtils.getObject(oid);

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WorkOrder.class, true);
		int idx_link = query.appendClassList(WorkOrderProjectLink.class, true);
		QuerySpecUtils.toInnerJoin(query, WorkOrder.class, WorkOrderProjectLink.class, WTAttributeNameIfc.ID_NAME,
				"roleAObjectRef.key.id", idx, idx_link);
		QuerySpecUtils.toEqualsAnd(query, idx_link, WorkOrderProjectLink.class, "roleAObjectRef.key.id",
				workOrder.getPersistInfo().getObjectIdentifier().getId());
		QuerySpecUtils.toOrderBy(query, idx_link, WorkOrderProjectLink.class, WorkOrderProjectLink.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WorkOrderProjectLink link = (WorkOrderProjectLink) obj[1];
			Project project = link.getProject();
			Map<String, String> map = new HashMap<>();
			map.put("oid", project.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("projectType_name", project.getProjectType() != null ? project.getProjectType().getName() : "");
			map.put("customer_name", project.getCustomer() != null ? project.getCustomer().getName() : "");
			map.put("mak_name", project.getMak() != null ? project.getMak().getName() : "");
			map.put("detail_name", project.getDetail() != null ? project.getDetail().getName() : "");
			map.put("kekNumber", project.getKekNumber());
			map.put("keNumber", project.getKeNumber());
			map.put("description", project.getDescription());
			list.add(map);
		}
		return net.sf.json.JSONArray.fromObject(list);
	}

	/**
	 * 도면일람표 표지
	 */
	public Workbook createWorkOrderCover(WorkOrder workOrder, ArrayList<WorkOrderDataLink> list) throws Exception {

		String workOrderPath = WTProperties.getLocalProperties().getProperty("wt.codebase.location") + File.separator
				+ "extcore" + File.separator + "excelTemplate" + File.separator + "WORKORDER-TEMPLATE.xlsx";
		FileInputStream fis = new FileInputStream(workOrderPath);
		Workbook workbook = new XSSFWorkbook(fis);
		Sheet sheet = workbook.getSheetAt(0);

		// name style
		CellStyle nameStyle = workbook.createCellStyle();
		// name font
		Font nameFont = workbook.createFont();
		nameFont.setBold(true);
		nameStyle.setFont(nameFont);

		// 정렬 설정
		nameStyle.setAlignment(HorizontalAlignment.LEFT);
		nameStyle.setVerticalAlignment(VerticalAlignment.CENTER);

		// 경계선 설정
		nameStyle.setBorderTop(BorderStyle.THIN);
		nameStyle.setBorderBottom(BorderStyle.THIN);
		nameStyle.setBorderLeft(BorderStyle.THIN);
		nameStyle.setBorderRight(BorderStyle.THIN);

		// cell style
		CellStyle cellStyle = workbook.createCellStyle();
		// cell font
		Font font = workbook.createFont();
		font.setBold(true);
		cellStyle.setFont(font);
		// 정렬 설정
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

		// 경계선 설정
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);

		int rowIndex = 7;
		int rowNum = 1;
		for (WorkOrderDataLink link : list) {

			String name = "";
			String number = "";
			int version = 0;
			Persistable per = link.getData();
			if (per instanceof KeDrawing) {
				KeDrawing keDrawing = (KeDrawing) per;
				name = keDrawing.getMaster().getName();
				number = keDrawing.getMaster().getKeNumber();
				version = keDrawing.getVersion();
			}

			setCellValue(workbook, sheet.getRow(rowIndex), 0, String.valueOf(rowNum), cellStyle);
			setCellValue(workbook, sheet.getRow(rowIndex), 1, link.getDataType(), cellStyle);
			setCellValue(workbook, sheet.getRow(rowIndex), 2, name, nameStyle);
			setCellValue(workbook, sheet.getRow(rowIndex), 3, number, cellStyle);
			setCellValue(workbook, sheet.getRow(rowIndex), 4, String.valueOf(link.getCurrent()), cellStyle);
			setCellValue(workbook, sheet.getRow(rowIndex), 5, String.valueOf(version), cellStyle);
			setCellValue(workbook, sheet.getRow(rowIndex), 6, String.valueOf(link.getLotNo()), cellStyle);
			setCellValue(workbook, sheet.getRow(rowIndex), 7, link.getNote(), cellStyle);
			rowIndex++;
			rowNum++;
		}
		return workbook;
	}

	/**
	 * 엑셀 데이터 세팅 함수
	 */
	private void setCellValue(Workbook workbook, Row row, int index, String data, CellStyle style) {
		Cell cell = row.getCell(index);
		if (style != null) {
			cell.setCellStyle(style);
		}
		cell.setCellValue(StringUtils.replaceToValue(data));
	}

	/**
	 * 도면 일람표 비교 기능
	 */
	public ArrayList<Map<String, Object>> compare(Project p1, ArrayList<Project> destList) throws Exception {
		ArrayList<Map<String, Object>> list = integratedData(p1);
		ArrayList<Map<String, Object>> mergedList = new ArrayList<>();

		return mergedList;
	}

	/**
	 * 비교할 데이터 수집
	 */
	private ArrayList<Map<String, Object>> integratedData(Project project) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WorkOrder.class, true);
		int idx_link = query.appendClassList(WorkOrderProjectLink.class, false);
		int idx_p = query.appendClassList(Project.class, false);

		QuerySpecUtils.toInnerJoin(query, WorkOrder.class, WorkOrderProjectLink.class, WTAttributeNameIfc.ID_NAME,
				"roleAObjectRef.key.id", idx, idx_link);
		QuerySpecUtils.toInnerJoin(query, Project.class, WorkOrderProjectLink.class, WTAttributeNameIfc.ID_NAME,
				"roleBObjectRef.key.id", idx_p, idx_link);
		QuerySpecUtils.toEqualsAnd(query, idx_link, WorkOrderProjectLink.class, "roleBObjectRef.key.id", project);

		QuerySpecUtils.toOrderBy(query, idx, WorkOrder.class, WorkOrder.CREATE_TIMESTAMP, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WorkOrder workOrder = (WorkOrder) obj[0];

			QuerySpec _query = new QuerySpec();
			int _idx = _query.appendClassList(TBOMMaster.class, false);
			int _idx_link = _query.appendClassList(TBOMMasterDataLink.class, true);
			int idx_data = _query.appendClassList(TBOMData.class, false);
			QuerySpecUtils.toInnerJoin(_query, TBOMMaster.class, TBOMMasterDataLink.class, WTAttributeNameIfc.ID_NAME,
					"roleAObjectRef.key.id", _idx, _idx_link);
			QuerySpecUtils.toInnerJoin(_query, TBOMData.class, TBOMMasterDataLink.class, WTAttributeNameIfc.ID_NAME,
					"roleBObjectRef.key.id", idx_data, _idx_link);
			QuerySpecUtils.toEqualsAnd(_query, _idx_link, TBOMMasterDataLink.class, "roleAObjectRef.key.id", master);
			QuerySpecUtils.toOrderBy(_query, idx_data, TBOMData.class, TBOMData.SORT, false);
			QueryResult qr = PersistenceHelper.manager.find(_query);
			while (qr.hasMoreElements()) {
				Object[] oo = (Object[]) qr.nextElement();
				TBOMMasterDataLink link = (TBOMMasterDataLink) oo[0];
				TBOMData data = link.getData();
				Map<String, Object> map = new HashMap<>();
				map.put("lotNo", String.valueOf(data.getLotNo()));
				map.put("code", data.getKePart().getMaster().getCode());
				map.put("name", data.getKePart().getMaster().getName());
				map.put("model", data.getKePart().getMaster().getModel());
				map.put("keNumber", data.getKePart().getMaster().getKeNumber());
				map.put("qty", data.getQty());
				map.put("unit", data.getUnit());
				map.put("provide", data.getProvide());
				map.put("discontinue", data.getDiscontinue());
				list.add(map);
			}
		}
		return list;
	}
}
