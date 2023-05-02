package e3ps.doc.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import e3ps.bom.partlist.PartListMaster;
import e3ps.common.util.AUIGridUtils;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.IBAUtils;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.doc.WTDocumentWTPartLink;
import e3ps.doc.dto.DocumentDTO;
import e3ps.epm.numberRule.NumberRule;
import e3ps.epm.numberRule.NumberRulePersistableLink;
import e3ps.project.Project;
import e3ps.project.output.Output;
import e3ps.project.output.OutputDocumentLink;
import net.sf.json.JSONArray;
import wt.clients.folder.FolderTaskLogic;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.IteratedFolderMemberLink;
import wt.part.WTPart;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;
import wt.vc.Mastered;
import wt.vc.VersionControlHelper;

public class DocumentHelper {

	/**
	 * 문서 기본 위치
	 */
	public static final String DOCUMENT_ROOT = "/Default/문서";

	/**
	 * 신규 제작 사양서 위치
	 */
	public static final String SPEC_NEW_ROOT = "/Default/프로젝트/제작사양서";

	/**
	 * PDM업그레이드 제작사양서 위치
	 */
	public static final String SPEC_OLD_ROOT = "/Default/문서/프로젝트/제작사양서";

	public static final DocumentService service = ServiceFactory.getService(DocumentService.class);
	public static final DocumentHelper manager = new DocumentHelper();

	public String getNextPNumber(String number) throws Exception {

		Calendar ca = Calendar.getInstance();
//		int day = ca.get(Calendar.DATE);
		int month = ca.get(Calendar.MONTH) + 1;
		int year = ca.get(Calendar.YEAR);
		DecimalFormat df = new DecimalFormat("00");
		number = number + df.format(year).substring(2) + df.format(month) + "-";

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(PartListMaster.class, true);

		SearchCondition sc = new SearchCondition(PartListMaster.class, PartListMaster.NUMBER, "LIKE",
				number.toUpperCase() + "%");
		query.appendWhere(sc, new int[] { idx });

		ClassAttribute attr = new ClassAttribute(PartListMaster.class, PartListMaster.NUMBER);
		OrderBy orderBy = new OrderBy(attr, true);
		query.appendOrderBy(orderBy, new int[] { idx });

		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			PartListMaster document = (PartListMaster) obj[0];

			String s = document.getNumber().substring(document.getNumber().lastIndexOf("-") + 1);

			int ss = Integer.parseInt(s) + 1;
			DecimalFormat d = new DecimalFormat("0000");
			number += d.format(ss);
		} else {
			number += "0001";
		}
		return number;
	}

	public String getNextNumber() throws Exception {

		Calendar ca = Calendar.getInstance();
		int day = ca.get(Calendar.DATE);
		int month = ca.get(Calendar.MONTH) + 1;
		int year = ca.get(Calendar.YEAR);
		DecimalFormat df = new DecimalFormat("00");
		String number = df.format(year) + df.format(month) + df.format(day) + "-";

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTDocumentMaster.class, true);

		SearchCondition sc = new SearchCondition(WTDocumentMaster.class, WTDocumentMaster.NUMBER, "LIKE",
				number.toUpperCase() + "%");
		query.appendWhere(sc, new int[] { idx });

		ClassAttribute attr = new ClassAttribute(WTDocumentMaster.class, WTDocumentMaster.NUMBER);
		OrderBy orderBy = new OrderBy(attr, true);
		query.appendOrderBy(orderBy, new int[] { idx });

		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WTDocumentMaster document = (WTDocumentMaster) obj[0];

			String s = document.getNumber().substring(document.getNumber().lastIndexOf("-") + 1);

			int ss = Integer.parseInt(s) + 1;
			DecimalFormat d = new DecimalFormat("000");
			number += d.format(ss);
		} else {
			number += "001";
		}
		return number;
	}

	/**
	 * 문서 검색
	 */
	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<DocumentDTO> list = new ArrayList<>();

		boolean latest = (boolean) params.get("latest");
		String oid = (String) params.get("oid");
		String name = (String) params.get("name");
		String number = (String) params.get("number");
		String description = (String) params.get("description");
		String creatorOid = (String) params.get("creatorOid");
		String state = (String) params.get("state");
		String createdFrom = (String) params.get("createdFrom");
		String createdTo = (String) params.get("createdTo");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTDocument.class, true);
		int idx_m = query.appendClassList(WTDocumentMaster.class, false);

		query.setAdvancedQueryEnabled(true);
		query.setDescendantQuery(false);

		QuerySpecUtils.toInnerJoin(query, WTDocument.class, WTDocumentMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);

		QuerySpecUtils.toLikeAnd(query, idx, WTDocument.class, WTDocument.NAME, name);
		QuerySpecUtils.toLikeAnd(query, idx, WTDocument.class, WTDocument.NUMBER, number);
		QuerySpecUtils.toLikeAnd(query, idx, WTDocument.class, WTDocument.DESCRIPTION, description);
		QuerySpecUtils.toState(query, idx, WTDocument.class, state);
		QuerySpecUtils.creatorQuery(query, idx, WTDocument.class, creatorOid);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, WTDocument.class, WTDocument.CREATE_TIMESTAMP, createdFrom,
				createdTo);

		Folder folder = null;
		if (!StringUtils.isNull(oid)) {
			folder = (Folder) CommonUtils.getObject(oid);
		} else {
			folder = FolderTaskLogic.getFolder(DOCUMENT_ROOT, CommonUtils.getPDMLinkProductContainer());
		}

		if (folder != null) {
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			int f_idx = query.appendClassList(IteratedFolderMemberLink.class, false);
			ClassAttribute fca = new ClassAttribute(IteratedFolderMemberLink.class, "roleBObjectRef.key.branchId");
			SearchCondition fsc = new SearchCondition(fca, "=",
					new ClassAttribute(WTDocument.class, "iterationInfo.branchId"));
			fsc.setFromIndicies(new int[] { f_idx, idx }, 0);
			fsc.setOuterJoin(0);
			query.appendWhere(fsc, new int[] { f_idx, idx });
			query.appendAnd();
			long fid = folder.getPersistInfo().getObjectIdentifier().getId();
			query.appendWhere(new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", "=", fid),
					new int[] { f_idx });
		}

		// 최신 이터레이션.
		if (latest) {
			QuerySpecUtils.toLatest(query, idx, WTDocument.class);
		}

		QuerySpecUtils.toOrderBy(query, idx, WTDocument.class, WTDocument.MODIFY_TIMESTAMP, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WTDocument document = (WTDocument) obj[0];
			DocumentDTO dto = new DocumentDTO(document);
			list.add(dto);
		}

		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	/**
	 * 문서 번호 채번
	 */
	public String setNumber(Map<String, Object> params) throws Exception {
		String loc = (String) params.get("loc");
		String preFix = "KEK";

		String[] aa = loc.split("/");
		for (String nn : aa) {
			if ("공용".equals(nn)) {
				preFix = "CM";
				break;
			} else if ("프로젝트".equals(nn)) {
				preFix = "PJ";
				break;
			} else if ("기술".equals(nn)) {
				preFix = "TE";
				break;
			} else if ("특허".equals(nn)) {
				preFix = "PA";
				break;
			} else if ("설계관리".equals(nn)) {
				preFix = "MA";
				break;
			}
		}

		Calendar ca = Calendar.getInstance();
		int month = ca.get(Calendar.MONTH) + 1;
		int year = ca.get(Calendar.YEAR);
		DecimalFormat df = new DecimalFormat("00");
		String number = preFix + "-" + df.format(year).substring(2) + df.format(month) + "-";

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTDocument.class, true);
		QuerySpecUtils.toLikeRightAnd(query, idx, WTDocument.class, WTDocument.NUMBER, number);
		QuerySpecUtils.toOrderBy(query, idx, WTDocument.class, WTDocument.NUMBER, true);

		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WTDocument document = (WTDocument) obj[0];

			String s = document.getNumber().substring(document.getNumber().lastIndexOf("-") + 1);

			int ss = Integer.parseInt(s) + 1;
			DecimalFormat d = new DecimalFormat("0000");
			number += d.format(ss);
		} else {
			number += "0001";
		}
		return number;
	}

	/**
	 * 문서 버전 이력
	 */
	public JSONArray versionHistory(WTDocument document) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		QueryResult result = VersionControlHelper.service.allIterationsOf(document.getMaster());
		while (result.hasMoreElements()) {
			Map<String, Object> map = new HashMap<>();
			WTDocument dd = (WTDocument) result.nextElement();
			map.put("oid", dd.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("number", dd.getNumber());
			map.put("name", dd.getName());
			map.put("version", CommonUtils.getFullVersion(dd));
			map.put("creator", dd.getCreatorFullName());
			map.put("createdDate_txt", CommonUtils.getPersistableTime(dd.getCreateTimestamp()));
			map.put("modifier", dd.getModifierName());
			map.put("modifiedDate_txt", dd.getModifierFullName());
			map.put("primary", AUIGridUtils.primaryTemplate(dd));
			map.put("secondary", AUIGridUtils.secondaryTemplate(dd));
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 산출물과 연결된 프로젝트
	 */
	public JSONArray jsonAuiProject(String oid) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();
		WTDocument document = (WTDocument) CommonUtils.getObject(oid);
		QueryResult result = PersistenceHelper.manager.navigate(document, "output", OutputDocumentLink.class);
		while (result.hasMoreElements()) {
			Output output = (Output) result.nextElement();
			Project project = output.getProject();
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
	 * 문서와 연결된 부품
	 */
	public JSONArray jsonAuiPart(String oid) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();
		WTDocument document = (WTDocument) CommonUtils.getObject(oid);
		QueryResult result = PersistenceHelper.manager.navigate(document, "part", WTDocumentWTPartLink.class);
		while (result.hasMoreElements()) {
			WTPart part = (WTPart) result.nextElement();
			Map<String, String> map = new HashMap<>();
			map.put("oid", part.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("dwgNo", IBAUtils.getStringValue(part, "DWG_NO"));
			map.put("name", part.getName());
			map.put("nameOfParts", IBAUtils.getStringValue(part, "NAME_OF_PARTS"));
			map.put("version", CommonUtils.getFullVersion(part));
			map.put("state", CommonUtils.getFullVersion(part));
			map.put("creator", part.getCreatorFullName());
			map.put("createdDate_txt", CommonUtils.getPersistableTime(part.getCreateTimestamp()));
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 문서와 연결된 도먼
	 */
	public JSONArray jsonAuiNumberRule(String oid) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();
		WTDocument document = (WTDocument) CommonUtils.getObject(oid);
		QueryResult result = PersistenceHelper.manager.navigate(document.getMaster(), "numberRule",
				NumberRulePersistableLink.class);
		while (result.hasMoreElements()) {
			NumberRule numberRule = (NumberRule) result.nextElement();
			map.put("number", numberRule.getMaster().getNumber());
			map.put("size_txt", numberRule.getMaster().getSize().getName());
			map.put("lotNo", numberRule.getMaster().getLotNo());
			map.put("unitName", numberRule.getMaster().getUnitName());
			map.put("name", numberRule.getMaster().getName());
			map.put("businessSector_txt", numberRule.getMaster().getSector().getName());
			map.put("classificationWritingDepartments_txt", numberRule.getMaster().getDepartment().getName());
			map.put("writtenDocuments_txt", numberRule.getMaster().getDocument().getName());
			map.put("version", numberRule.getVersion());
			map.put("state", numberRule.getState());
			map.put("creator", numberRule.getMaster().getOwnership().getOwner().getFullName());
			map.put("createdDate_txt", CommonUtils.getPersistableTime(numberRule.getMaster().getCreateTimestamp()));
			map.put("modifier", numberRule.getOwnership().getOwner().getFullName());
			map.put("modifiedDate_txt", CommonUtils.getPersistableTime(numberRule.getCreateTimestamp()));
			map.put("oid", numberRule.getPersistInfo().getObjectIdentifier().getStringValue());
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}
}