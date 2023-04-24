package e3ps.doc.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import e3ps.bom.partlist.PartListMaster;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.doc.dto.DocumentDTO;
import net.sf.json.JSONArray;
import wt.clients.folder.FolderTaskLogic;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.IteratedFolderMemberLink;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;
import wt.vc.Mastered;

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

	public String getNextNumber(String number) throws Exception {

		Calendar ca = Calendar.getInstance();
//		int day = ca.get(Calendar.DATE);
		int month = ca.get(Calendar.MONTH) + 1;
		int year = ca.get(Calendar.YEAR);
		DecimalFormat df = new DecimalFormat("00");
		number = number + df.format(year).substring(2) + df.format(month) + "-";

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

		// 검색 변수
		boolean latest = (boolean) params.get("latest");
		// 폴더 OID
		String oid = (String) params.get("oid");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTDocument.class, true);
		int idx_m = query.appendClassList(WTDocumentMaster.class, false);

		QuerySpecUtils.toInnerJoin(query, WTDocument.class, WTDocumentMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);

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
		System.out.println(query);

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

	public String setNumber(Map<String, Object> params) throws Exception {
		String oid = (String) params.get("oid");
		Folder folder = (Folder) CommonUtils.getObject(oid);
		String loc = folder.getLocation();
		String preFix = "KEK";

		String[] aa = loc.split("/");
		System.out.println("===" + loc);
		for (String nn : aa) {
			System.out.println("n=" + nn);
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

	public JSONArray history(Mastered master) throws Exception {

		ArrayList<DocumentDTO> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTDocument.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, WTDocument.class, "masterReference.key.id",
				master.getPersistInfo().getObjectIdentifier().getId());
//		QuerySpecUtils.toOrderBy(query, idx, WTDocument.class, CommonUtils.getFullVersion(null), true);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WTDocument document = (WTDocument) obj[0];
			DocumentDTO dto = new DocumentDTO(document);
			list.add(dto);
		}
		return JSONArray.fromObject(list);
	}

//	public JSONArray jsonArrayAui(String oid) throws Exception {
//		ArrayList<Map<String, String>> list = new ArrayList<>();
//		WTDocument document = (WTDocument) CommonUtils.getObject(oid);
//
//		QuerySpec query = new QuerySpec();
//		int idx = query.appendClassList(WTDocument.class, true);
//		int idx_link = query.appendClassList(WTDocumentWTPartLink.class, true);
//		QuerySpecUtils.toInnerJoin(query, WTDocument.class, WTDocumentWTPartLink.class,
//				WTAttributeNameIfc.ID_NAME, "roleAObjectRef.key.id", idx, idx_link);
//		QuerySpecUtils.toEqualsAnd(query, idx_link, WTDocumentWTPartLink.class, "roleAObjectRef.key.id",
//				document.getPersistInfo().getObjectIdentifier().getId());
//		QueryResult result = PersistenceHelper.manager.find(query);
//		while (result.hasMoreElements()) {
//			Object[] obj = (Object[]) result.nextElement();
//			WTDocumentWTPartLink link = (WTDocumentWTPartLink) obj[1];
//			Project project = link.getProject();
//			Map<String, String> map = new HashMap<>();
//			map.put("oid", project.getPersistInfo().getObjectIdentifier().getStringValue());
//			map.put("projectType_name", project.getProjectType() != null ? project.getProjectType().getName() : "");
//			map.put("customer_name", project.getCustomer() != null ? project.getCustomer().getName() : "");
//			map.put("mak_name", project.getMak() != null ? project.getMak().getName() : "");
//			map.put("detail_name", project.getDetail() != null ? project.getDetail().getName() : "");
//			map.put("kekNumber", project.getKekNumber());
//			map.put("keNumber", project.getKeNumber());
//			map.put("description", project.getDescription());
//			list.add(map);
//		}
//		return JSONArray.fromObject(list);
//	}
}