package e3ps.epm.workOrder.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.aspose.cells.FileFormatType;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.content.service.CommonContentHelper;
import e3ps.common.util.AUIGridUtils;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.ContentUtils;
import e3ps.common.util.DateUtils;
import e3ps.common.util.IBAUtils;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.epm.keDrawing.KeDrawing;
import e3ps.epm.keDrawing.KeDrawingMaster;
import e3ps.epm.keDrawing.service.KeDrawingHelper;
import e3ps.epm.workOrder.WorkOrder;
import e3ps.epm.workOrder.WorkOrderDataLink;
import e3ps.epm.workOrder.WorkOrderProjectLink;
import e3ps.epm.workOrder.dto.WorkOrderDTO;
import e3ps.project.Project;
import e3ps.project.ProjectUserLink;
import e3ps.project.variable.ProjectUserTypeVariable;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import wt.content.ApplicationData;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
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

	private static String codebase = null;
	static {
		try {
			codebase = WTProperties.getServerProperties().getProperty("wt.codebase.location");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 도면 일람표 조회
	 */
	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();

		String name = (String) params.get("name");
		String kekNumber = (String) params.get("kekNumber");
		String keNumber = (String) params.get("keNumber");
		String pdateFrom = (String) params.get("pdateFrom");
		String pdateTo = (String) params.get("pdateTo");
		String customer_name = (String) params.get("customer_name");
		String install_name = (String) params.get("install_name");
		String projectType = (String) params.get("projectType");
		String machineOid = (String) params.get("machineOid");
		String elecOid = (String) params.get("elecOid");
		String softOid = (String) params.get("softOid");
		String mak_name = (String) params.get("mak_name");
		String detail_name = (String) params.get("detail_name");
		String description = (String) params.get("description");
		String creatorOid = (String) params.get("creatorOid");
		String createdFrom = (String) params.get("createdFrom");
		String createdTo = (String) params.get("createdTo");
		boolean latest = (boolean) params.get("latest");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WorkOrder.class, true);
		if (latest) {
			QuerySpecUtils.toBooleanAnd(query, idx, WorkOrder.class, WorkOrder.LATEST, true);
		} else {
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			query.appendOpenParen();
			SearchCondition sc = new SearchCondition(WorkOrder.class, WorkOrder.LATEST, SearchCondition.IS_TRUE);
			query.appendWhere(sc, new int[] { idx });
			QuerySpecUtils.toBooleanOr(query, idx, WorkOrder.class, WorkOrder.LATEST, false);
			query.appendCloseParen();
		}

		QuerySpecUtils.toLikeAnd(query, idx, WorkOrder.class, WorkOrder.NAME, name);
		QuerySpecUtils.toCreator(query, idx, WorkOrder.class, creatorOid);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, WorkOrder.class, WorkOrder.CREATE_TIMESTAMP, createdFrom,
				createdTo);

		QuerySpecUtils.toOrderBy(query, idx, WorkOrder.class, WorkOrder.CREATE_TIMESTAMP, true);
		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();

		JSONArray list = new JSONArray();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WorkOrder workOrder = (WorkOrder) obj[0];

			QuerySpec _query = new QuerySpec();
			int _idx = _query.appendClassList(WorkOrder.class, true);
			int _idx_p = _query.appendClassList(Project.class, true);
			int _idx_link = _query.appendClassList(WorkOrderProjectLink.class, true);

			QuerySpecUtils.toEqualsAnd(_query, _idx_link, WorkOrderProjectLink.class, "roleAObjectRef.key.id",
					workOrder);
			QuerySpecUtils.toInnerJoin(_query, WorkOrder.class, WorkOrderProjectLink.class, WTAttributeNameIfc.ID_NAME,
					"roleAObjectRef.key.id", _idx, _idx_link);
			QuerySpecUtils.toInnerJoin(_query, Project.class, WorkOrderProjectLink.class, WTAttributeNameIfc.ID_NAME,
					"roleBObjectRef.key.id", _idx_p, _idx_link);
			QuerySpecUtils.toLikeAnd(_query, _idx_p, Project.class, Project.KEK_NUMBER, kekNumber);
			QuerySpecUtils.toLikeAnd(_query, _idx_p, Project.class, Project.KE_NUMBER, keNumber);
			QuerySpecUtils.toTimeGreaterAndLess(_query, _idx_p, Project.class, Project.P_DATE, pdateFrom, pdateTo);

			if (!StringUtils.isNull(customer_name)) {
				CommonCode customerCode = (CommonCode) CommonUtils.getObject(customer_name);
				QuerySpecUtils.toEqualsAnd(_query, _idx_p, Project.class, "customerReference.key.id", customerCode);
			}

			if (!StringUtils.isNull(install_name)) {
				CommonCode installCode = (CommonCode) CommonUtils.getObject(install_name);
				QuerySpecUtils.toEqualsAnd(_query, _idx_p, Project.class, "installReference.key.id", installCode);
			}

			if (!StringUtils.isNull(projectType)) {
				CommonCode projectTypeCode = (CommonCode) CommonUtils.getObject(projectType);
				QuerySpecUtils.toEqualsAnd(_query, _idx_p, Project.class, "projectTypeReference.key.id",
						projectTypeCode);
			}

			if (!StringUtils.isNull(machineOid)) {
				WTUser machine = (WTUser) CommonUtils.getObject(machineOid);
				CommonCode machineCode = CommonCodeHelper.manager.getCommonCode(ProjectUserTypeVariable.MACHINE,
						"USER_TYPE");
				int idx_plink = _query.appendClassList(ProjectUserLink.class, false);
				int idx_u = _query.appendClassList(WTUser.class, false);

				QuerySpecUtils.toInnerJoin(_query, Project.class, ProjectUserLink.class, WTAttributeNameIfc.ID_NAME,
						"roleAObjectRef.key.id", _idx_p, idx_plink);
				QuerySpecUtils.toInnerJoin(_query, WTUser.class, ProjectUserLink.class, WTAttributeNameIfc.ID_NAME,
						"roleBObjectRef.key.id", idx_u, idx_plink);
				QuerySpecUtils.toEqualsAnd(_query, idx_plink, ProjectUserLink.class, "roleBObjectRef.key.id", machine);
				QuerySpecUtils.toEqualsAnd(_query, idx_plink, ProjectUserLink.class, "userTypeReference.key.id",
						machineCode);
			}

			if (!StringUtils.isNull(elecOid)) {
				WTUser elec = (WTUser) CommonUtils.getObject(elecOid);
				CommonCode elecCode = CommonCodeHelper.manager.getCommonCode(ProjectUserTypeVariable.ELEC, "USER_TYPE");
				int idx_plink = _query.appendClassList(ProjectUserLink.class, false);
				int idx_u = _query.appendClassList(WTUser.class, false);

				QuerySpecUtils.toInnerJoin(_query, Project.class, ProjectUserLink.class, WTAttributeNameIfc.ID_NAME,
						"roleAObjectRef.key.id", _idx_p, idx_plink);
				QuerySpecUtils.toInnerJoin(_query, WTUser.class, ProjectUserLink.class, WTAttributeNameIfc.ID_NAME,
						"roleBObjectRef.key.id", idx_u, idx_plink);
				QuerySpecUtils.toEqualsAnd(_query, idx_plink, ProjectUserLink.class, "roleBObjectRef.key.id", elec);
				QuerySpecUtils.toEqualsAnd(_query, idx_plink, ProjectUserLink.class, "userTypeReference.key.id",
						elecCode);
			}

			if (!StringUtils.isNull(softOid)) {
				WTUser soft = (WTUser) CommonUtils.getObject(softOid);
				CommonCode softCode = CommonCodeHelper.manager.getCommonCode(ProjectUserTypeVariable.SOFT, "USER_TYPE");
				int idx_plink = _query.appendClassList(ProjectUserLink.class, false);
				int idx_u = _query.appendClassList(WTUser.class, false);

				QuerySpecUtils.toInnerJoin(_query, Project.class, ProjectUserLink.class, WTAttributeNameIfc.ID_NAME,
						"roleAObjectRef.key.id", _idx_p, idx_plink);
				QuerySpecUtils.toInnerJoin(_query, WTUser.class, ProjectUserLink.class, WTAttributeNameIfc.ID_NAME,
						"roleBObjectRef.key.id", idx_u, idx_plink);
				QuerySpecUtils.toEqualsAnd(_query, idx_plink, ProjectUserLink.class, "roleBObjectRef.key.id", soft);
				QuerySpecUtils.toEqualsAnd(_query, idx_plink, ProjectUserLink.class, "userTypeReference.key.id",
						softCode);
			}

			if (!StringUtils.isNull(mak_name)) {
				CommonCode makCode = (CommonCode) CommonUtils.getObject(mak_name);
				QuerySpecUtils.toEqualsAnd(_query, _idx_p, Project.class, "makReference.key.id", makCode);
			}

			if (!StringUtils.isNull(detail_name)) {
				CommonCode detailCode = (CommonCode) CommonUtils.getObject(detail_name);
				QuerySpecUtils.toEqualsAnd(_query, _idx_p, Project.class, "detailReference.key.id", detailCode);
			}

			QuerySpecUtils.toLikeAnd(_query, _idx_p, Project.class, Project.DESCRIPTION, description);
			QueryResult group = PersistenceHelper.manager.find(_query);

			int isNode = 1;
			JSONArray children = new JSONArray();
			JSONObject node = new JSONObject();
			while (group.hasMoreElements()) {
				Object[] oo = (Object[]) group.nextElement();
				WorkOrderProjectLink link = (WorkOrderProjectLink) oo[2];
				WorkOrderDTO dto = new WorkOrderDTO(link);
				node.put("oid", workOrder.getPersistInfo().getObjectIdentifier().getStringValue());
				node.put("name", workOrder.getName());
				node.put("number", workOrder.getNumber());
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
					node.put("primary", dto.getPrimary());
					node.put("thumbnail", dto.getThumbnail());
					node.put("icons", dto.getIcons());
					node.put("version", dto.getVersion());
					node.put("latest", dto.isLatest());
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
					data.put("primary", dto.getPrimary());
					data.put("thumbnail", dto.getThumbnail());
					data.put("icons", dto.getIcons());
					data.put("version", dto.getVersion());
					data.put("latest", dto.isLatest());
					children.add(data);
				}
				isNode++;
			}
			node.put("children", children);

			if (group.size() > 0) {
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
			WorkOrder master = (WorkOrder) obj[0];

			String s = master.getNumber().substring(master.getNumber().lastIndexOf("-") + 1);

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

		QuerySpec first = new QuerySpec();

		int idx1 = first.appendClassList(EPMDocument.class, true);
		int idx2 = first.appendClassList(EPMDocumentMaster.class, false);

		QuerySpecUtils.toCI(first, idx1, EPMDocument.class);
		QuerySpecUtils.toInnerJoin(first, EPMDocument.class, EPMDocumentMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx1, idx2);
		QuerySpecUtils.toLatest(first, idx1, EPMDocument.class);
		QuerySpecUtils.toEqualsAnd(first, idx1, EPMDocument.class, EPMDocument.DOC_TYPE, "CADDRAWING");
		QuerySpecUtils.toIBAEqualsAnd(first, EPMDocument.class, idx1, "DWG_NO", number);
		QueryResult qr = PersistenceHelper.manager.find(first);
		if (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			EPMDocument epm = (EPMDocument) obj[0];
			map.put("number", number);
			map.put("name", IBAUtils.getStringValue(epm, "NAME_OF_PARTS"));
			map.put("rev", Integer.parseInt(epm.getVersionIdentifier().getSeries().getValue()));
			map.put("lotNo", "");
			map.put("current", epm.getVersionIdentifier().getSeries().getValue());
			map.put("ok", true);
			map.put("preView", ContentUtils.getPreViewBase64(epm));
			map.put("doid", epm.getPersistInfo().getObjectIdentifier().getStringValue());
			return map;
		}

		QuerySpec second = new QuerySpec();
		int _idx1 = second.appendClassList(EPMDocument.class, true);
		int _idx2 = second.appendClassList(EPMDocumentMaster.class, false);

		QuerySpecUtils.toCI(second, _idx1, EPMDocument.class);
		QuerySpecUtils.toInnerJoin(second, EPMDocument.class, EPMDocumentMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, _idx1, _idx2);
		QuerySpecUtils.toLatest(second, _idx1, EPMDocument.class);
		QuerySpecUtils.toEqualsAnd(second, _idx1, EPMDocument.class, EPMDocument.AUTHORING_APPLICATION, "ACAD");
		QuerySpecUtils.toEqualsAnd(second, _idx1, EPMDocument.class, EPMDocument.DOC_TYPE, "CADCOMPONENT");
		QuerySpecUtils.toIBAEqualsAnd(second, EPMDocument.class, _idx1, "DWG_No", number);
		QueryResult rs = PersistenceHelper.manager.find(second);
		if (rs.hasMoreElements()) {
			Object[] obj = (Object[]) rs.nextElement();
			EPMDocument epm = (EPMDocument) obj[0];
			map.put("number", number);
			map.put("name", IBAUtils.getStringValue(epm, "NAME_OF_PARTS"));
			map.put("rev", Integer.parseInt(epm.getVersionIdentifier().getSeries().getValue()));
			map.put("lotNo", "");
			map.put("current", epm.getVersionIdentifier().getSeries().getValue());
			map.put("ok", true);
			map.put("preView", ContentUtils.getPreViewBase64(epm));
			map.put("doid", epm.getPersistInfo().getObjectIdentifier().getStringValue());
			return map;
		}

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
			map.put("number", number);
			map.put("name", master.getName());
			map.put("rev", keDrawing.getVersion());
			map.put("lotNo", master.getLotNo());
			map.put("current", keDrawing.getVersion());
			map.put("ok", true);
			map.put("preView", ContentUtils.getPreViewBase64(keDrawing));
			map.put("doid", keDrawing.getPersistInfo().getObjectIdentifier().getStringValue());
			return map;
		}
		map.put("number", "서버에 없는 DWG NO 입니다.");
		map.put("ok", false);
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
	 * 도면일람표와 역인 작번 리스트
	 */
	public JSONArray jsonAuiProject(String oid) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();
		WorkOrder workOrder = (WorkOrder) CommonUtils.getObject(oid);
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WorkOrder.class, true);
		int idx_link = query.appendClassList(WorkOrderProjectLink.class, true);
		QuerySpecUtils.toInnerJoin(query, WorkOrder.class, WorkOrderProjectLink.class, WTAttributeNameIfc.ID_NAME,
				"roleAObjectRef.key.id", idx, idx_link);
		QuerySpecUtils.toEqualsAnd(query, idx_link, WorkOrderProjectLink.class, "roleAObjectRef.key.id", workOrder);
		QuerySpecUtils.toOrderBy(query, idx_link, WorkOrderProjectLink.class, WorkOrderProjectLink.CREATE_TIMESTAMP,
				true);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WorkOrderProjectLink link = (WorkOrderProjectLink) obj[1];
			Project project = link.getProject();
			Map<String, String> map = new HashMap<>();
			map.put("oid", project.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("projectType_name", project.getProjectType() != null ? project.getProjectType().getName() : "");
			map.put("customer_name", project.getCustomer() != null ? project.getCustomer().getName() : "");
			map.put("install_name", project.getInstall() != null ? project.getInstall().getName() : "");
			map.put("mak_name", project.getMak() != null ? project.getMak().getName() : "");
			map.put("detail_name", project.getDetail() != null ? project.getDetail().getName() : "");
			map.put("kekNumber", project.getKekNumber());
			map.put("keNumber", project.getKeNumber());
			map.put("description", project.getDescription());
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 도면일람표 표지
	 */
	public Workbook createWorkOrderCover(WorkOrder workOrder, ArrayList<WorkOrderDataLink> list) throws Exception {

		WorkOrder first = getFirst(workOrder);

		Workbook workbook = new XSSFWorkbook();

		// 헤더 스타일
		CellStyle headerStyle = workbook.createCellStyle();
		// name font
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerFont.setUnderline(Font.U_SINGLE);
		headerFont.setFontHeightInPoints((short) 40);
		headerStyle.setFont(headerFont);

		Font dotFont = workbook.createFont();
		dotFont.setBold(true);
		dotFont.setFontHeightInPoints((short) 10);

		// 헤더 스타일
		CellStyle headerDotStyle = workbook.createCellStyle();
		headerDotStyle.setFont(dotFont);

		// 정렬 설정
		headerStyle.setAlignment(HorizontalAlignment.CENTER);
		headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		// 경계선 설정
		headerStyle.setBorderTop(BorderStyle.NONE);
		headerStyle.setBorderBottom(BorderStyle.NONE);
		headerStyle.setBorderLeft(BorderStyle.NONE);
		headerStyle.setBorderRight(BorderStyle.NONE);

		// 정렬 설정
		headerDotStyle.setAlignment(HorizontalAlignment.CENTER);
		headerDotStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		// 경계선 설정
		headerDotStyle.setBorderTop(BorderStyle.DASH_DOT);
		headerDotStyle.setBorderBottom(BorderStyle.DASH_DOT);
		headerDotStyle.setBorderLeft(BorderStyle.DASH_DOT);
		headerDotStyle.setBorderRight(BorderStyle.DASH_DOT);

		CellStyle noneBorderStyle = workbook.createCellStyle();

		// 경계선 설정
		noneBorderStyle.setBorderTop(BorderStyle.NONE);
		noneBorderStyle.setBorderBottom(BorderStyle.NONE);
		noneBorderStyle.setBorderLeft(BorderStyle.NONE);
		noneBorderStyle.setBorderRight(BorderStyle.NONE);

		// 데이터 헤더 스타일
		CellStyle dataHeaderStyle = workbook.createCellStyle();
		dataHeaderStyle.setWrapText(true);
		dataHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
		dataHeaderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		dataHeaderStyle.setBorderTop(BorderStyle.MEDIUM);
		dataHeaderStyle.setBorderBottom(BorderStyle.MEDIUM);
		dataHeaderStyle.setBorderLeft(BorderStyle.MEDIUM);
		dataHeaderStyle.setBorderRight(BorderStyle.MEDIUM);

		CellStyle dataLeftStyle = workbook.createCellStyle();
		dataLeftStyle.setWrapText(true);
		dataLeftStyle.setAlignment(HorizontalAlignment.LEFT);
		dataLeftStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		dataLeftStyle.setBorderTop(BorderStyle.MEDIUM);
		dataLeftStyle.setBorderBottom(BorderStyle.MEDIUM);
		dataLeftStyle.setBorderLeft(BorderStyle.MEDIUM);
		dataLeftStyle.setBorderRight(BorderStyle.MEDIUM);

		CellStyle rightStyle = workbook.createCellStyle();
		rightStyle.setWrapText(true);
		rightStyle.setAlignment(HorizontalAlignment.LEFT);
		rightStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		rightStyle.setBorderTop(BorderStyle.MEDIUM);
		rightStyle.setBorderBottom(BorderStyle.MEDIUM);
		rightStyle.setBorderLeft(BorderStyle.MEDIUM);
		rightStyle.setBorderRight(BorderStyle.NONE);

		CellStyle leftStyle = workbook.createCellStyle();
		leftStyle.setWrapText(true);
		leftStyle.setAlignment(HorizontalAlignment.LEFT);
		leftStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		leftStyle.setBorderTop(BorderStyle.MEDIUM);
		leftStyle.setBorderBottom(BorderStyle.MEDIUM);
		leftStyle.setBorderLeft(BorderStyle.NONE);
		leftStyle.setBorderRight(BorderStyle.MEDIUM);

		CellStyle topStyle = workbook.createCellStyle();
		topStyle.setWrapText(true);
		topStyle.setAlignment(HorizontalAlignment.CENTER);
		topStyle.setVerticalAlignment(VerticalAlignment.TOP);
		topStyle.setBorderTop(BorderStyle.MEDIUM);
		topStyle.setBorderBottom(BorderStyle.MEDIUM);
		topStyle.setBorderLeft(BorderStyle.MEDIUM);
		topStyle.setBorderRight(BorderStyle.MEDIUM);

		Font dataFont = workbook.createFont();
		dataFont.setBold(true);
		dataHeaderStyle.setFont(dataFont);
		dataLeftStyle.setFont(dataFont);
		topStyle.setFont(dataFont);
		leftStyle.setFont(dataFont);
		rightStyle.setFont(dataFont);

		int size = list.size();
		int loop = size / 45;
		int gap = size % 45;
		if (gap > 0) {
			loop = loop + 1;
		}

		Project project = null;
		QueryResult result = PersistenceHelper.manager.navigate(workOrder, "project", WorkOrderProjectLink.class);
		if (result.hasMoreElements()) {
			project = (Project) result.nextElement();
		}

		String mak = project.getMak() != null ? project.getMak().getName() : "";
		String detail = project.getDetail() != null ? project.getDetail().getName() : "";
		String customer = project.getCustomer() != null ? project.getCustomer().getName() : "";

		// 데이터 FOR 문 변수
		int start = 0;
		int end = 45;

		if (list.size() < 45) {
			end = 45;
		}

		for (int i = 0; i < loop; i++) {
			String sheetName = workOrder.getNumber() + " (" + (i + 1) + ")";

			Sheet sheet = workbook.createSheet(sheetName);
			sheet.setDisplayGridlines(false);

			PrintSetup printSetup = sheet.getPrintSetup();
			printSetup.setPaperSize(PrintSetup.A4_PAPERSIZE);

			sheet.setMargin(Sheet.LeftMargin, 0.3);
			sheet.setMargin(Sheet.RightMargin, 0.2);
			sheet.setMargin(Sheet.TopMargin, 0.8);
			sheet.setMargin(Sheet.BottomMargin, 0.4);
			sheet.setFitToPage(true);

			Header header = sheet.getHeader();
			Footer footer = sheet.getFooter();

//			// 헤더 폰트
//			Font headerLeftFont = workbook.createFont();
//			headerLeftFont.setBold(true);
//			
//			CellStyle headerRightStyle = workbook.createCellStyle();
//			Font headerRightFont = workbook.createFont();
//			headerRightFont.setBold(true);
//			headerRightFont.setColor(IndexedColors.RED.getIndex());
//			headerRightFont.setUnderline(Font.U_SINGLE);
//			headerRightStyle.setFont(headerRightFont);

			header.setLeft("KOKUSAI ELECTRIC");
			header.setRight("CONFIDENTIAL");
			footer.setRight("국제엘렉트릭코리아(주)");

//			File file = new File(codebase + File.separator + "extcore" + File.separator + "images" + File.separator
//					+ "confidential.jpg");
//			InputStream imageStream = new FileInputStream(file);
//			byte[] imageBytes = IOUtils.toByteArray(imageStream);
//
//
//            // 이미지 삽입
//            int pictureIdx = workbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_JPEG);
//            CreationHelper helper = workbook.getCreationHelper();
////            Sheet sheet = workbook.createSheet("Sheet1");
//            Drawing<?> drawing = sheet.createDrawingPatriarch();
//            ClientAnchor anchor = helper.createClientAnchor();
//            anchor.setCol1(0); // 이미지를 삽입할 열 인덱스
//            anchor.setRow1(0); // 이미지를 삽입할 행 인덱스
//            Picture picture = drawing.createPicture(anchor, pictureIdx);
//            picture.resize(); // 이미지 크기 조정 (선택 사항)

			// 헤더 머지
			CellRangeAddress headerMerge = new CellRangeAddress(0, 0, 5, 15);
			sheet.addMergedRegion(headerMerge);

			// 데이터 헤더 머지
			CellRangeAddress dataHeaderMerge = new CellRangeAddress(2, 2, 2, 9);
			CellRangeAddress dataHeaderMerge1 = new CellRangeAddress(2, 2, 10, 12);
			CellRangeAddress dataHeaderMerge2 = new CellRangeAddress(2, 2, 16, 17);
			sheet.addMergedRegion(dataHeaderMerge);
			sheet.addMergedRegion(dataHeaderMerge1);
			sheet.addMergedRegion(dataHeaderMerge2);

			sheet.setColumnWidth(0, 50);
			sheet.setColumnWidth(18, 50);
			sheet.setDefaultColumnWidth(8);
//			sheet.setDefaultRowHeight((short) 22);

			Row row = null;
			Cell cell = null;

			// 1행 1열
			row = sheet.createRow(0);
			row.setHeight((short) 700);
			cell = row.createCell(0);
			cell.setCellValue("");

			CellRangeAddress makMerge = new CellRangeAddress(0, 0, 1, 4);
			sheet.addMergedRegion(makMerge);

			// 1행 헤더
			cell = row.createCell(1);
			cell.setCellStyle(headerDotStyle);
			cell.setCellValue(mak + "/" + detail);
			cell = row.createCell(2);
			cell.setCellStyle(headerDotStyle);
			cell = row.createCell(3);
			cell.setCellStyle(headerDotStyle);
			cell = row.createCell(4);
			cell.setCellStyle(headerDotStyle);

			cell = row.createCell(5);
			cell.setCellStyle(headerStyle);
			cell.setCellValue("ALL DRAWING");
			cell = row.createCell(6);
			cell.setCellStyle(headerStyle);
			cell = row.createCell(7);
			cell.setCellStyle(headerStyle);
			cell = row.createCell(8);
			cell.setCellStyle(headerStyle);
			cell = row.createCell(9);
			cell.setCellStyle(headerStyle);
			cell = row.createCell(10);
			cell.setCellStyle(headerStyle);
			cell = row.createCell(11);
			cell.setCellStyle(headerStyle);
			cell = row.createCell(12);
			cell.setCellStyle(headerStyle);
			cell = row.createCell(13);
			cell.setCellStyle(headerStyle);
			cell = row.createCell(14);
			cell.setCellStyle(headerStyle);
			cell = row.createCell(15);
			cell.setCellStyle(headerStyle);

			CellRangeAddress customerMerge = new CellRangeAddress(0, 0, 16, 17);
			sheet.addMergedRegion(customerMerge);

			cell = row.createCell(16);
			cell.setCellStyle(headerDotStyle);
			cell.setCellValue(customer);
			cell = row.createCell(17);
			cell.setCellStyle(headerDotStyle);

			cell = row.createCell(18);
			cell.setCellValue("");

			// 2행
			row = sheet.createRow(1);
			row.setHeight((short) 70);
			cell = row.createCell(0);
			cell.setCellValue("");

			// 데이터 컬럼 행
			row = sheet.createRow(2);
			row.setHeight((short) 800);

			cell = row.createCell(1);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("NO");

			cell = row.createCell(2);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("DRAWING TITLE");

			// ??
			cell = row.createCell(3);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(4);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(5);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(6);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(7);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(8);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(9);
			cell.setCellStyle(dataHeaderStyle);

			cell = row.createCell(10);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("DWG. NO.");

			cell = row.createCell(11);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(12);
			cell.setCellStyle(dataHeaderStyle);

			cell = row.createCell(13);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("REV");

			cell = row.createCell(14);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("Current\nREV");

			cell = row.createCell(15);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("LOT");

			cell = row.createCell(16);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("NOTE");

			cell = row.createCell(17);
			cell.setCellStyle(dataHeaderStyle);

			int rowIndex = 3;

			// for start
			for (int j = start; j < end; j++) {
				// 데이터 머지

				if ((list.size() - 1) > j) {
					WorkOrderDataLink data = (WorkOrderDataLink) list.get(j);
					Persistable per = data.getData();
					String name = "";
					String number = "";
					String current = "";
					String lotNo = "";
					if (per instanceof KeDrawing) {
						KeDrawing keDrawing = (KeDrawing) per;
						name = keDrawing.getMaster().getName();
						number = keDrawing.getMaster().getKeNumber();
						current = String.valueOf(keDrawing.getVersion());
						lotNo = String.valueOf(keDrawing.getMaster().getLotNo());
					}

					CellRangeAddress dataValueMerge = new CellRangeAddress(rowIndex, rowIndex, 2, 9);
					CellRangeAddress dataValueMerge1 = new CellRangeAddress(rowIndex, rowIndex, 10, 12);
					CellRangeAddress dataValueMerge2 = new CellRangeAddress(rowIndex, rowIndex, 16, 17);
					sheet.addMergedRegion(dataValueMerge);
					sheet.addMergedRegion(dataValueMerge1);
					sheet.addMergedRegion(dataValueMerge2);

					// 데이터 값 행
					row = sheet.createRow(rowIndex);
					cell = row.createCell(1);
					row.setHeightInPoints(21.7f);
					cell.setCellStyle(dataHeaderStyle);
					cell.setCellValue((j + 1));

					cell = row.createCell(2);
					row.setHeightInPoints(21.7f);
					cell.setCellStyle(dataLeftStyle);
					cell.setCellValue(name);
					cell = row.createCell(3);
					row.setHeightInPoints(21.7f);
					cell.setCellStyle(dataLeftStyle);
					cell = row.createCell(4);
					row.setHeightInPoints(21.7f);
					cell.setCellStyle(dataLeftStyle);
					cell = row.createCell(5);
					row.setHeightInPoints(21.7f);
					cell.setCellStyle(dataLeftStyle);
					cell = row.createCell(6);
					row.setHeightInPoints(21.7f);
					cell.setCellStyle(dataLeftStyle);
					cell = row.createCell(7);
					row.setHeightInPoints(21.7f);
					cell.setCellStyle(dataLeftStyle);
					cell = row.createCell(8);
					row.setHeightInPoints(21.7f);
					cell.setCellStyle(dataLeftStyle);
					cell = row.createCell(9);
					row.setHeightInPoints(21.7f);
					cell.setCellStyle(dataLeftStyle);

					cell = row.createCell(10);
					row.setHeightInPoints(21.7f);
					cell.setCellStyle(dataLeftStyle);
					cell.setCellValue(number);
					cell = row.createCell(11);
					row.setHeightInPoints(21.7f);
					cell.setCellStyle(dataLeftStyle);
					cell = row.createCell(12);
					row.setHeightInPoints(21.7f);
					cell.setCellStyle(dataLeftStyle);

					cell = row.createCell(13);
					row.setHeightInPoints(21.7f);
					cell.setCellStyle(dataHeaderStyle);
					cell.setCellValue(data.getRev());

					cell = row.createCell(14);
					row.setHeightInPoints(21.7f);
					cell.setCellStyle(dataHeaderStyle);
					cell.setCellValue(current);

					cell = row.createCell(15);
					row.setHeightInPoints(21.7f);
					cell.setCellStyle(dataHeaderStyle);
					cell.setCellValue(lotNo);

					cell = row.createCell(16);
					row.setHeightInPoints(21.7f);
					cell.setCellStyle(dataHeaderStyle);
					cell.setCellValue(data.getNote());

					cell = row.createCell(17);
					cell.setCellStyle(dataHeaderStyle);

					rowIndex++;
				} else {
					String name = "";
					String number = "";
					String current = "";
					String lotNo = "";
					CellRangeAddress dataValueMerge = new CellRangeAddress(rowIndex, rowIndex, 2, 9);
					CellRangeAddress dataValueMerge1 = new CellRangeAddress(rowIndex, rowIndex, 10, 12);
					CellRangeAddress dataValueMerge2 = new CellRangeAddress(rowIndex, rowIndex, 16, 17);
					sheet.addMergedRegion(dataValueMerge);
					sheet.addMergedRegion(dataValueMerge1);
					sheet.addMergedRegion(dataValueMerge2);

					// 데이터 값 행
					row = sheet.createRow(rowIndex);
					cell = row.createCell(1);
					row.setHeightInPoints(21.7f);
					cell.setCellStyle(dataHeaderStyle);
					cell.setCellValue((j + 1));

					cell = row.createCell(2);
					row.setHeightInPoints(21.7f);
					cell.setCellStyle(dataLeftStyle);
					cell.setCellValue(name);
					cell = row.createCell(3);
					row.setHeightInPoints(21.7f);
					cell.setCellStyle(dataLeftStyle);
					cell = row.createCell(4);
					row.setHeightInPoints(21.7f);
					cell.setCellStyle(dataLeftStyle);
					cell = row.createCell(5);
					row.setHeightInPoints(21.7f);
					cell.setCellStyle(dataLeftStyle);
					cell = row.createCell(6);
					row.setHeightInPoints(21.7f);
					cell.setCellStyle(dataLeftStyle);
					cell = row.createCell(7);
					row.setHeightInPoints(21.7f);
					cell.setCellStyle(dataLeftStyle);
					cell = row.createCell(8);
					row.setHeightInPoints(21.7f);
					cell.setCellStyle(dataLeftStyle);
					cell = row.createCell(9);
					row.setHeightInPoints(21.7f);
					cell.setCellStyle(dataLeftStyle);

					cell = row.createCell(10);
					row.setHeightInPoints(21.7f);
					cell.setCellStyle(dataLeftStyle);
					cell.setCellValue(number);
					cell = row.createCell(11);
					row.setHeightInPoints(21.7f);
					cell.setCellStyle(dataLeftStyle);
					cell = row.createCell(12);
					row.setHeightInPoints(21.7f);
					cell.setCellStyle(dataLeftStyle);

					cell = row.createCell(13);
					row.setHeightInPoints(21.7f);
					cell.setCellStyle(dataHeaderStyle);
					cell.setCellValue("");

					cell = row.createCell(14);
					row.setHeightInPoints(21.7f);
					cell.setCellStyle(dataHeaderStyle);
					cell.setCellValue(current);

					cell = row.createCell(15);
					row.setHeightInPoints(21.7f);
					cell.setCellStyle(dataHeaderStyle);
					cell.setCellValue(lotNo);
					row.setHeightInPoints(21.7f);

					cell = row.createCell(16);
					row.setHeightInPoints(21.7f);
					cell.setCellStyle(dataHeaderStyle);
					cell.setCellValue("");

					cell = row.createCell(17);
					row.setHeightInPoints(21.7f);
					cell.setCellStyle(dataHeaderStyle);

					rowIndex++;
				}
			}

			if (i == loop - 2) {
				start += 45;
				end += 45;
			} else {
				// 어차피 안돈다?
				start += 45;
				end += 45;
			}

			row = sheet.createRow(48);
			row.setHeight((short) 70);
			cell = row.createCell(0);
			cell.setCellValue("");

			// 푸터
			int footerIndex = 49;
			int numericValue = 1;
			for (int j = 0; j < 3; j++) {

				CellRangeAddress footerMerge = new CellRangeAddress(footerIndex, footerIndex, 2, 4);
				CellRangeAddress footerMerge1 = new CellRangeAddress(footerIndex, footerIndex, 5, 9);
				CellRangeAddress footerMerge2 = new CellRangeAddress(footerIndex, footerIndex, 11, 13);
				CellRangeAddress footerMerge3 = new CellRangeAddress(footerIndex, footerIndex, 14, 17);
				sheet.addMergedRegion(footerMerge);
				sheet.addMergedRegion(footerMerge1);
				sheet.addMergedRegion(footerMerge2);
				sheet.addMergedRegion(footerMerge3);

				// 푸터

				WorkOrder v1 = getVersionData(workOrder, numericValue);

				row = sheet.createRow(footerIndex);
				cell = row.createCell(1);
				row.setHeightInPoints(21.7f);
				cell.setCellStyle(dataHeaderStyle);
				cell.setCellValue("▲" + numericValue);

				cell = row.createCell(2);
				row.setHeightInPoints(21.7f);
				cell.setCellStyle(dataHeaderStyle);
				cell.setCellValue(v1 != null ? CommonUtils.getPersistableTime(v1.getCreateTimestamp()) : "");
				cell = row.createCell(3);
				row.setHeightInPoints(21.7f);
				cell.setCellStyle(dataHeaderStyle);
				cell = row.createCell(4);
				row.setHeightInPoints(21.7f);
				cell.setCellStyle(dataHeaderStyle);

				cell = row.createCell(5);
				row.setHeightInPoints(21.7f);
				cell.setCellStyle(dataLeftStyle);
				cell.setCellValue(v1 != null ? v1.getNote() : "");
				cell = row.createCell(6);
				row.setHeightInPoints(21.7f);
				cell.setCellStyle(dataLeftStyle);
				cell = row.createCell(7);
				row.setHeightInPoints(21.7f);
				cell.setCellStyle(dataLeftStyle);
				cell = row.createCell(8);
				row.setHeightInPoints(21.7f);
				cell.setCellStyle(dataLeftStyle);
				cell = row.createCell(9);
				row.setHeightInPoints(21.7f);
				cell.setCellStyle(dataLeftStyle);

				WorkOrder v2 = getVersionData(workOrder, (numericValue + 3));

				cell = row.createCell(10);
				row.setHeightInPoints(21.7f);
				cell.setCellStyle(dataHeaderStyle);
				cell.setCellValue("▲" + (numericValue + 3));

				cell = row.createCell(11);
				cell.setCellStyle(dataHeaderStyle);
				cell.setCellValue(v2 != null ? CommonUtils.getPersistableTime(v2.getCreateTimestamp()) : "");
				row.setHeightInPoints(21.7f);
				cell = row.createCell(12);
				row.setHeightInPoints(21.7f);
				cell.setCellStyle(dataHeaderStyle);
				cell = row.createCell(13);
				row.setHeightInPoints(21.7f);
				cell.setCellStyle(dataHeaderStyle);

				cell = row.createCell(14);
				row.setHeightInPoints(21.7f);
				cell.setCellStyle(dataLeftStyle);
				cell.setCellValue(v2 != null ? v2.getNote() : "");
				cell = row.createCell(15);
				row.setHeightInPoints(21.7f);
				cell.setCellStyle(dataLeftStyle);
				cell = row.createCell(16);
				row.setHeightInPoints(21.7f);
				cell.setCellStyle(dataLeftStyle);
				cell = row.createCell(17);
				row.setHeightInPoints(21.7f);
				cell.setCellStyle(dataLeftStyle);

				numericValue++;
				footerIndex++;
			} // end for

			// 52행 시작
			CellRangeAddress toleranceMerge = new CellRangeAddress(52, 52, 1, 3);
			sheet.addMergedRegion(toleranceMerge);
			row = sheet.createRow(52);
			cell = row.createCell(1);
			cell.setCellStyle(dataLeftStyle);
			cell.setCellValue("TOLERANCE");
			cell = row.createCell(2);
			cell.setCellStyle(dataLeftStyle);
			cell = row.createCell(3);
			cell.setCellStyle(rightStyle);

			CellRangeAddress toleranceValuedwMerge = new CellRangeAddress(52, 52, 4, 5);
			sheet.addMergedRegion(toleranceValuedwMerge);
			cell = row.createCell(4);
			cell.setCellStyle(leftStyle);
			cell = row.createCell(5);
			cell.setCellStyle(leftStyle);

			CellRangeAddress scaleMerge = new CellRangeAddress(52, 52, 6, 8);
			sheet.addMergedRegion(scaleMerge);
			cell = row.createCell(6);
			cell.setCellStyle(dataLeftStyle);
			cell.setCellValue("SCALE          FREE");
			cell = row.createCell(7);
			cell.setCellStyle(dataLeftStyle);
			cell = row.createCell(8);
			cell.setCellStyle(dataLeftStyle);

			CellRangeAddress unitMerge = new CellRangeAddress(52, 52, 9, 10);
			sheet.addMergedRegion(unitMerge);
			cell = row.createCell(9);
			cell.setCellStyle(dataLeftStyle);
			cell.setCellValue("UNIT");
			cell = row.createCell(10);
			cell.setCellStyle(dataLeftStyle);

			CellRangeAddress issueMerge = new CellRangeAddress(52, 57, 11, 12);
			sheet.addMergedRegion(issueMerge);
			cell = row.createCell(11);
			cell.setCellStyle(topStyle);
			cell.setCellValue("ISSUE");
			cell = row.createCell(12);
			cell.setCellStyle(topStyle);

			CellRangeAddress mfgMerge = new CellRangeAddress(52, 52, 13, 15);
			sheet.addMergedRegion(mfgMerge);
			cell = row.createCell(13);
			cell.setCellStyle(dataLeftStyle);
			cell.setCellValue("MFG NO.");
			cell = row.createCell(14);
			cell.setCellStyle(dataLeftStyle);
			cell = row.createCell(15);
			cell.setCellStyle(dataLeftStyle);

			CellRangeAddress orderMerge = new CellRangeAddress(52, 52, 16, 17);
			sheet.addMergedRegion(orderMerge);
			cell = row.createCell(16);
			cell.setCellStyle(dataLeftStyle);
			cell.setCellValue("ORDER NO.");
			cell = row.createCell(17);
			cell.setCellStyle(dataLeftStyle);
			// 52행 끝

			// 53행 시작
			CellRangeAddress modelMerge = new CellRangeAddress(53, 53, 1, 2);
			sheet.addMergedRegion(modelMerge);
			row = sheet.createRow(53);
			cell = row.createCell(1);
			cell.setCellStyle(dataLeftStyle);
			cell.setCellValue("MODEL");
			cell = row.createCell(2);
			cell.setCellStyle(rightStyle);

			CellRangeAddress modelValueMerge = new CellRangeAddress(53, 53, 3, 6);
			sheet.addMergedRegion(modelValueMerge);
			cell = row.createCell(3);
			cell.setCellStyle(leftStyle);
			cell.setCellValue(project.getModel());
			cell = row.createCell(4);
			cell.setCellStyle(leftStyle);
			cell = row.createCell(5);
			cell.setCellStyle(leftStyle);
			cell = row.createCell(6);
			cell.setCellStyle(leftStyle);

			CellRangeAddress eqptMerge = new CellRangeAddress(53, 53, 7, 8);
			sheet.addMergedRegion(eqptMerge);
			cell = row.createCell(7);
			cell.setCellStyle(dataLeftStyle);
			cell.setCellValue("EQPT");
			cell = row.createCell(8);
			cell.setCellStyle(dataLeftStyle);

			CellRangeAddress eqptValueMerge = new CellRangeAddress(53, 53, 9, 10);
			sheet.addMergedRegion(eqptValueMerge);
			cell = row.createCell(9);
			cell.setCellStyle(dataLeftStyle);
			cell.setCellValue("Q2LN");
			cell = row.createCell(10);
			cell.setCellStyle(dataLeftStyle);

			cell = row.createCell(11);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(12);
			cell.setCellStyle(dataHeaderStyle);

			cell = row.createCell(13);
			cell.setCellStyle(dataLeftStyle);
			cell.setCellValue("TITLE");

			CellRangeAddress titleValueMerge = new CellRangeAddress(53, 53, 14, 17);
			sheet.addMergedRegion(titleValueMerge);
			cell = row.createCell(14);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("ALL DRAWING");
			cell = row.createCell(15);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(16);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(17);
			cell.setCellStyle(dataHeaderStyle);
			// 53 행 끝

			// 54행 시작
			CellRangeAddress approvedMerge = new CellRangeAddress(54, 54, 1, 2);
			sheet.addMergedRegion(approvedMerge);
			row = sheet.createRow(54);
			cell = row.createCell(1);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("APPROVED");
			cell = row.createCell(2);
			cell.setCellStyle(dataHeaderStyle);

			CellRangeAddress judgedMerge = new CellRangeAddress(54, 54, 3, 4);
			sheet.addMergedRegion(judgedMerge);
			cell = row.createCell(3);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("JUDGED");
			cell = row.createCell(4);
			cell.setCellStyle(dataHeaderStyle);

			CellRangeAddress designedMerge = new CellRangeAddress(54, 54, 5, 6);
			sheet.addMergedRegion(designedMerge);
			cell = row.createCell(5);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("DESIGNED");
			cell = row.createCell(6);
			cell.setCellStyle(dataHeaderStyle);

			CellRangeAddress drawnMerge = new CellRangeAddress(54, 54, 7, 8);
			sheet.addMergedRegion(drawnMerge);
			cell = row.createCell(7);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("DRAWN");
			cell = row.createCell(8);
			cell.setCellStyle(dataHeaderStyle);

			CellRangeAddress checkedMerge = new CellRangeAddress(54, 54, 9, 10);
			sheet.addMergedRegion(checkedMerge);
			cell = row.createCell(9);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("CHECKED");
			cell = row.createCell(10);
			cell.setCellStyle(dataHeaderStyle);

			cell = row.createCell(11);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(12);
			cell.setCellStyle(dataHeaderStyle);

			CellRangeAddress dwgNoMergre = new CellRangeAddress(54, 56, 13, 13);
			sheet.addMergedRegion(dwgNoMergre);
			cell = row.createCell(13);
			cell.setCellStyle(dataLeftStyle);
			cell.setCellValue("DWG\nNo.");

			CellRangeAddress dwgNoValueMergre = new CellRangeAddress(54, 56, 14, 16);
			sheet.addMergedRegion(dwgNoValueMergre);
			cell = row.createCell(14);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue(workOrder.getNumber() + "(" + (i + 1) + "/" + loop + ")");
			cell = row.createCell(15);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(16);
			cell.setCellStyle(dataHeaderStyle);

			CellRangeAddress revMerge = new CellRangeAddress(54, 57, 17, 17);
			sheet.addMergedRegion(revMerge);
			cell = row.createCell(17);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("REV\n" + workOrder.getVersion());

			// 55행 시작
			CellRangeAddress approvedValueMerge = new CellRangeAddress(55, 56, 1, 2);
			sheet.addMergedRegion(approvedValueMerge);
			row = sheet.createRow(55);
			cell = row.createCell(1);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue(workOrder.getApproved() != null ? workOrder.getApproved() : "");
			cell = row.createCell(2);
			cell.setCellStyle(dataHeaderStyle);

			CellRangeAddress judgedValueMerge = new CellRangeAddress(55, 56, 3, 4);
			sheet.addMergedRegion(judgedValueMerge);
			cell = row.createCell(3);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("");
			cell = row.createCell(4);
			cell.setCellStyle(dataHeaderStyle);

			CellRangeAddress designedValueMerge = new CellRangeAddress(55, 56, 5, 6);
			sheet.addMergedRegion(designedValueMerge);
			cell = row.createCell(5);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue(workOrder.getCreatorFullName());
			cell = row.createCell(6);
			cell.setCellStyle(dataHeaderStyle);

			CellRangeAddress drawnValueMerge = new CellRangeAddress(55, 56, 7, 8);
			sheet.addMergedRegion(drawnValueMerge);
			cell = row.createCell(7);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue(workOrder.getCreatorFullName());
			cell = row.createCell(8);
			cell.setCellStyle(dataHeaderStyle);

			CellRangeAddress checkedValueMerge = new CellRangeAddress(55, 56, 9, 10);
			sheet.addMergedRegion(checkedValueMerge);
			cell = row.createCell(9);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("");
			cell = row.createCell(10);
			cell.setCellStyle(dataHeaderStyle);

			cell = row.createCell(11);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(12);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(13);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(14);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(15);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(16);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(17);
			cell.setCellStyle(dataHeaderStyle);

			// 55 행 끝

			// 56 행 시작
			row = sheet.createRow(56);
			cell = row.createCell(1);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(2);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(3);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(4);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(5);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(6);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(7);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(8);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(9);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(10);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(11);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(12);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(13);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(14);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(15);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(16);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(17);
			cell.setCellStyle(dataHeaderStyle);
			// 56행 끝

			// 57 행 시작
			CellRangeAddress kekMerge = new CellRangeAddress(57, 57, 1, 10);
			sheet.addMergedRegion(kekMerge);
			row = sheet.createRow(57);
			cell = row.createCell(1);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue("KOOKJE ELECTRIC KOREA.CO.,LTD");
			cell = row.createCell(2);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(3);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(4);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(5);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(6);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(7);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(8);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(9);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(10);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(11);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(12);
			cell.setCellStyle(dataHeaderStyle);

			CellRangeAddress timeMerge = new CellRangeAddress(57, 57, 13, 16);
			sheet.addMergedRegion(timeMerge);
			cell = row.createCell(13);
			cell.setCellStyle(dataHeaderStyle);
			cell.setCellValue(CommonUtils.getPersistableTime(first.getCreateTimestamp()));
			cell = row.createCell(14);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(15);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(16);
			cell.setCellStyle(dataHeaderStyle);
			cell = row.createCell(17);
			cell.setCellStyle(dataHeaderStyle);
		}
		return workbook;
	}

	/**
	 * 도면 일람표 비교 기능
	 */
	public ArrayList<Map<String, Object>> compare(Project p1, ArrayList<Project> destList) throws Exception {
		System.out.println("도면 일람표 비교 START = " + new Timestamp(new Date().getTime()));
		ArrayList<Map<String, Object>> list = integratedData(p1);
		ArrayList<Map<String, Object>> mergedList = new ArrayList<>();

		Map<String, Object> makList = new HashMap<>();
		Map<String, Object> customerList = new HashMap<>();
		Map<String, Object> keList = new HashMap<>();
		Map<String, Object> pdateList = new HashMap<>();

		makList.put("lotNo", "막종 / 막종상세");
		customerList.put("lotNo", "고객사 / 설치장소");
		keList.put("lotNo", "KE 작번");
		pdateList.put("lotNo", "발행일");

		makList.put("name", "막종 / 막종상세");
		customerList.put("name", "고객사 / 설치장소");
		keList.put("name", "KE 작번");
		pdateList.put("name", "발행일");

		makList.put("number", "막종 / 막종상세");
		customerList.put("number", "고객사 / 설치장소");
		keList.put("number", "KE 작번");
		pdateList.put("number", "발행일");

		destList.add(0, p1);
		for (int i = 0; i < destList.size(); i++) {
			Project project = (Project) destList.get(i);
			String oid = project.getPersistInfo().getObjectIdentifier().getStringValue();
			makList.put("oid", oid);
			customerList.put("oid", oid);
			keList.put("oid", oid);
			pdateList.put("oid", oid);

			String mak = project.getMak() != null ? project.getMak().getName() : "";
			String detail = project.getDetail() != null ? project.getDetail().getName() : "";
			String customer = project.getCustomer() != null ? project.getCustomer().getName() : "";
			String install = project.getInstall() != null ? project.getInstall().getName() : "";

			makList.put("rev" + (i + 1), mak + " / " + detail);
			customerList.put("rev" + (i + 1), customer + " / " + install);
			keList.put("rev" + (i + 1), project.getKeNumber());
			pdateList.put("rev" + (i + 1), CommonUtils.getPersistableTime(project.getPDate()));
		}

		mergedList.add(makList);
		mergedList.add(customerList);
		mergedList.add(keList);
		mergedList.add(pdateList);

		destList.remove(0);

		// list1의 데이터를 먼저 추가
		for (Map<String, Object> data : list) {
			Map<String, Object> mergedData = new HashMap<>();
			mergedData.put("lotNo", data.get("lotNo"));
			mergedData.put("name", data.get("name"));
			mergedData.put("number", data.get("number"));
			mergedData.put("version", data.get("version"));
			mergedData.put("oid", data.get("doid"));
			mergedData.put("rev1", data.get("rev"));
			mergedList.add(mergedData);
		}

		for (int i = 0; i < destList.size(); i++) {
			Project p2 = (Project) destList.get(i);
			ArrayList<Map<String, Object>> _list = integratedData(p2);
			for (Map<String, Object> data : _list) {
				String partNo = (String) data.get("partNo");
				String lotNo = (String) data.get("lotNo");
//				String version = (String) data.get("version");
				String key = partNo + "-" + lotNo;
				boolean isExist = false;

				// mergedList에 partNo가 동일한 데이터가 있는지 확인
				for (Map<String, Object> mergedData : mergedList) {
					String mergedPartNo = (String) mergedData.get("partNo");
					String mergedLotNo = (String) mergedData.get("lotNo");
//					String mergedVersion = (String) mergedData.get("version");
					String _key = mergedPartNo + "-" + mergedLotNo;

					if (key.equals(_key)) {
						// partNo가 동일한 데이터가 있으면 데이터를 업데이트하고 isExist를 true로 변경
						mergedData.put("rev" + (2 + i), data.get("rev"));
						isExist = true;
						break;
					}
				}

				if (!isExist) {
					// partNo가 동일한 데이터가 없으면 mergedList에 데이터를 추가
					Map<String, Object> mergedData = new HashMap<>();
					mergedData.put("rev" + (2 + i), data.get("rev"));
					mergedData.put("lotNo", data.get("lotNo"));
					mergedData.put("name", data.get("name"));
					mergedData.put("number", data.get("number"));
					mergedData.put("version", data.get("version"));
					mergedData.put("oid", data.get("doid"));
					mergedList.add(mergedData);
				}
			}
		}
		System.out.println("도면 일람표비교 END = " + new Timestamp(new Date().getTime()));
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
		QuerySpecUtils.toBooleanAnd(query, idx, WorkOrder.class, WorkOrder.LATEST, true);
		QuerySpecUtils.toOrderBy(query, idx, WorkOrder.class, WorkOrder.CREATE_TIMESTAMP, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WorkOrder workOrder = (WorkOrder) obj[0];

			QuerySpec _query = new QuerySpec();
			int _idx = _query.appendClassList(WorkOrder.class, false);
			int _idx_link = _query.appendClassList(WorkOrderDataLink.class, true);
			QuerySpecUtils.toInnerJoin(_query, WorkOrder.class, WorkOrderDataLink.class, WTAttributeNameIfc.ID_NAME,
					"roleAObjectRef.key.id", _idx, _idx_link);
			QuerySpecUtils.toEqualsAnd(_query, _idx_link, WorkOrderDataLink.class, "roleAObjectRef.key.id", workOrder);
			QuerySpecUtils.toOrderBy(_query, _idx_link, WorkOrderDataLink.class, WorkOrderDataLink.SORT, false);
			QueryResult qr = PersistenceHelper.manager.find(_query);
			while (qr.hasMoreElements()) {
				Object[] oo = (Object[]) qr.nextElement();
				WorkOrderDataLink link = (WorkOrderDataLink) oo[0];
				Map<String, Object> map = new HashMap();

				map.put("oid", workOrder.getPersistInfo().getObjectIdentifier().getStringValue());
				map.put("lotNo", String.valueOf(link.getLotNo()));
				map.put("rev", String.valueOf(link.getRev())); // 등록당시
				map.put("version", String.valueOf(link.getRev()));
				map.put("createdData_txt", CommonUtils.getPersistableTime(link.getCreateTimestamp()));
				map.put("note", link.getNote());

				Persistable per = link.getData();
				if (per instanceof KeDrawing) {
					KeDrawing keDrawing = (KeDrawing) per;
					KeDrawing latest = KeDrawingHelper.manager.getLatest(keDrawing);
					map.put("name", keDrawing.getMaster().getName());
					map.put("number", keDrawing.getMaster().getKeNumber());
					map.put("current", latest.getVersion()); // 최신버전
					map.put("preView", ContentUtils.getPreViewBase64(keDrawing));
					map.put("primary", AUIGridUtils.primaryTemplate(keDrawing));
					map.put("doid", keDrawing.getPersistInfo().getObjectIdentifier().getStringValue());
				} else if (per instanceof EPMDocument) {
					EPMDocument epm = (EPMDocument) per;
					map.put("name", IBAUtils.getStringValue(epm, "NAME_OF_PARTS"));
					map.put("number", IBAUtils.getStringValue(epm, "DWG_NO"));
					map.put("current", epm.getVersionIdentifier().getSeries().getValue());
					map.put("preView", ContentUtils.getPreViewBase64(epm));
					map.put("doid", epm.getPersistInfo().getObjectIdentifier().getStringValue());
//					map.put("primary", AUIGridUtils.primaryTemplate(keDrawing)); // pdf...
				}
				list.add(map);
			}
		}
		return list;
	}

	/**
	 * 프로젝트 도면일람표 탭
	 */
	public JSONArray workOrderTab(String oid) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();

		Project project = (Project) CommonUtils.getObject(oid);

		QueryResult qr = PersistenceHelper.manager.navigate(project, "workOrder", WorkOrderProjectLink.class);
		while (qr.hasMoreElements()) {
			WorkOrder workOrder = (WorkOrder) qr.nextElement();
			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(WorkOrder.class, true);
			int idx_l = query.appendClassList(WorkOrderDataLink.class, true);

			QuerySpecUtils.toInnerJoin(query, WorkOrder.class, WorkOrderDataLink.class, WTAttributeNameIfc.ID_NAME,
					"roleAObjectRef.key.id", idx, idx_l);
			QuerySpecUtils.toEqualsAnd(query, idx_l, WorkOrderDataLink.class, "roleAObjectRef.key.id", workOrder);
			QuerySpecUtils.toOrderBy(query, idx_l, WorkOrderDataLink.class, WorkOrderDataLink.SORT, false);
			QueryResult result = PersistenceHelper.manager.find(query);
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				WorkOrder order = (WorkOrder) obj[0];
				WorkOrderDataLink link = (WorkOrderDataLink) obj[1];
				Map<String, Object> map = new HashMap();

				map.put("oid", order.getPersistInfo().getObjectIdentifier().getStringValue());
				map.put("workOrderType", workOrder.getWorkOrderType());
				map.put("lotNo", link.getLotNo());
				map.put("rev", link.getRev());
				map.put("createdData_txt", CommonUtils.getPersistableTime(link.getCreateTimestamp()));
				map.put("note", link.getNote());
				Persistable per = link.getData();
				if (per instanceof KeDrawing) {
					KeDrawing keDrawing = (KeDrawing) per;
					KeDrawing latest = KeDrawingHelper.manager.getLatest(keDrawing);
					map.put("doid", keDrawing.getPersistInfo().getObjectIdentifier().getStringValue());
					map.put("name", keDrawing.getMaster().getName());
					map.put("number", keDrawing.getMaster().getKeNumber());
					map.put("current", latest.getVersion());
					map.put("preView", ContentUtils.getPreViewBase64(keDrawing));
					map.put("primary", AUIGridUtils.primaryTemplate(keDrawing));
				} else if (per instanceof EPMDocument) {
					EPMDocument epm = (EPMDocument) per;
					map.put("doid", epm.getPersistInfo().getObjectIdentifier().getStringValue());
					map.put("name", IBAUtils.getStringValue(epm, "NAME_OF_PARTS"));
					map.put("number", IBAUtils.getStringValue(epm, "DWG_NO"));
					map.put("current", epm.getVersionIdentifier().getSeries().getValue());
					map.put("preView", ContentUtils.getPreViewBase64(epm));
//					map.put("primary", AUIGridUtils.primaryTemplate(keDrawing)); // pdf...
				}
				list.add(map);
			}
		}

		return JSONArray.fromObject(list);
	}

	/**
	 * 최신 도면일람표 도면
	 */
	public WorkOrder getLatest(WorkOrder workOrder) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WorkOrder.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, WorkOrder.class, WorkOrder.NUMBER, workOrder.getNumber());
		QuerySpecUtils.toBooleanAnd(query, idx, WorkOrder.class, WorkOrder.LATEST, true);
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WorkOrder latest = (WorkOrder) obj[0];
			return latest;
		}
		return null;
	}

	/**
	 * 도면 일람표 버전 이력
	 */
	public JSONArray history(WorkOrder workOrder) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WorkOrder.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, WorkOrder.class, WorkOrder.NUMBER, workOrder.getNumber());
		QuerySpecUtils.toOrderBy(query, idx, WorkOrder.class, WorkOrder.VERSION, true);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WorkOrder order = (WorkOrder) obj[0];
			Map<String, Object> map = new HashMap();
			map.put("oid", order.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("name", order.getName());
			map.put("number", order.getNumber());
			map.put("description", order.getDescription());
			map.put("version", order.getVersion());
			map.put("createdDate_txt", CommonUtils.getPersistableTime(order.getCreateTimestamp()));
			map.put("creator", order.getCreatorFullName());
			map.put("state", order.getLifeCycleState().getDisplay());
			map.put("latest", order.getLatest());
			map.put("primary", AUIGridUtils.primaryTemplate(order));
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 도면 일람표 프로젝트 가져오기
	 */
	public JSONArray getProjects(WorkOrder workOrder) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();
		QueryResult result = PersistenceHelper.manager.navigate(workOrder, "project", WorkOrderProjectLink.class);
		while (result.hasMoreElements()) {
			Project project = (Project) result.nextElement();
			Map<String, String> map = new HashMap<>();map.put("oid", project.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("kekNumber", project.getKekNumber());
			map.put("keNumber", project.getKeNumber());
			map.put("projectType", project.getProjectType().getName());
			map.put("customer", project.getCustomer() != null ? project.getCustomer().getName() : "");
			map.put("install", project.getInstall() != null ? project.getInstall().getName() : "");
			map.put("mak", project.getMak() != null ? project.getMak().getName() : "");
			map.put("detail", project.getDetail() != null ? project.getDetail().getName() : "");
			map.put("description", project.getDescription());
			map.put("pDate_txt", project.getPDate() != null ? project.getPDate().toString().substring(0, 10) : "");
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 결재 완료후 페이지 및 PDF 병합 처리
	 */
	public void afterAction(WorkOrder workOrder) throws Exception {
		// 기존 도면 일람표 링크 모두제거
		QueryResult qr = PersistenceHelper.manager.navigate(workOrder, "data", WorkOrderDataLink.class, false);
		ArrayList<WorkOrderDataLink> list = new ArrayList<>();
		while (qr.hasMoreElements()) {
			WorkOrderDataLink link = (WorkOrderDataLink) qr.nextElement();
			list.add(link);
		}

		CommonContentHelper.manager.clear(workOrder);

		Workbook cover = WorkOrderHelper.manager.createWorkOrderCover(workOrder, list);
		File excelFile = ContentUtils.getTempFile(workOrder.getName() + "_표지.xlsx");
		FileOutputStream fos = new FileOutputStream(excelFile);
		cover.write(fos);

		ApplicationData data = ApplicationData.newApplicationData(workOrder);
		data.setRole(ContentRoleType.PRIMARY);
		PersistenceHelper.manager.save(data);
		ContentServerHelper.service.updateContent(workOrder, data, excelFile.getAbsolutePath());

		// pdf 표지
		File pdfFile = ContentUtils.getTempFile(workOrder.getName() + "_표지.pdf");
		com.aspose.cells.Workbook wb = new com.aspose.cells.Workbook(new FileInputStream(excelFile));
		FileOutputStream fospdf = new FileOutputStream(pdfFile);
		wb.save(fospdf, FileFormatType.PDF);

		ApplicationData dd = ApplicationData.newApplicationData(workOrder);
		dd.setRole(ContentRoleType.ADDITIONAL_FILES);
		PersistenceHelper.manager.save(dd);
		ContentServerHelper.service.updateContent(workOrder, dd, pdfFile.getAbsolutePath());

		// PDF 병합
		WorkOrderHelper.manager.postAfterAction(workOrder.getPersistInfo().getObjectIdentifier().getStringValue());
	}

	/**
	 * 최초 도면일람표
	 */
	public WorkOrder getFirst(WorkOrder workOrder) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WorkOrder.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, WorkOrder.class, WorkOrder.NUMBER, workOrder.getNumber());
		QuerySpecUtils.toEqualsAnd(query, idx, WorkOrder.class, WorkOrder.VERSION, 0);
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WorkOrder first = (WorkOrder) obj[0];
			return first;
		}
		return null;
	}

	/**
	 * 버전에 맞는 도면 일람표 가져오기
	 */
	public WorkOrder getVersionData(WorkOrder workOrder, int version) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WorkOrder.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, WorkOrder.class, WorkOrder.NUMBER, workOrder.getNumber());
		QuerySpecUtils.toEqualsAnd(query, idx, WorkOrder.class, WorkOrder.VERSION, version);
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WorkOrder first = (WorkOrder) obj[0];
			return first;
		}
		return null;
	}
}
