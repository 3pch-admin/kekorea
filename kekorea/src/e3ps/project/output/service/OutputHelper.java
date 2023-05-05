package e3ps.project.output.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import e3ps.common.util.CommonUtils;
import e3ps.common.util.FolderUtils;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.project.Project;
import e3ps.project.output.Output;
import e3ps.project.output.OutputDocumentLink;
import e3ps.project.output.OutputProjectLink;
import e3ps.project.output.dto.OutputDTO;
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
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;

public class OutputHelper {

	public static final OutputHelper manager = new OutputHelper();
	public static final OutputService service = ServiceFactory.getService(OutputService.class);

	/**
	 * 산출물 경로
	 */
	public static final String OUTPUT_NEW_ROOT = "/Default/프로젝트";
	public static final String OUTPUT_OLD_ROOT = "/Default/문서/프로젝트";

	/**
	 * 산출물 검색
	 */
	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<OutputDTO> list = new ArrayList<>();

		String name = (String) params.get("name");
		String number = (String) params.get("number");
		String content = (String) params.get("content");
		String kekNumber = (String) params.get("kekNumber");
		String keNumber = (String) params.get("keNumber");
		String description = (String) params.get("description");
		String creatorOid = (String) params.get("creatorOid");
		String createdFrom = (String) params.get("createdFrom");
		String createdTo = (String) params.get("createdTo");
		String state = (String) params.get("state");
		boolean latest = (boolean) params.get("latest");
		String oid = (String) params.get("oid"); // 폴더 OID
		String type = (String) params.get("type");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTDocument.class, true);
		int idx_m = query.appendClassList(WTDocumentMaster.class, false);

		query.setAdvancedQueryEnabled(true);
		query.setDescendantQuery(false);

		QuerySpecUtils.toCI(query, idx, WTDocument.class);
		QuerySpecUtils.toInnerJoin(query, WTDocument.class, WTDocumentMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);

		QuerySpecUtils.toLikeAnd(query, idx, WTDocument.class, WTDocument.NAME, name);
		QuerySpecUtils.toLikeAnd(query, idx, WTDocument.class, WTDocument.NUMBER, number);
		QuerySpecUtils.toLikeAnd(query, idx, WTDocument.class, WTDocument.DESCRIPTION, content);

		if (!StringUtils.isNull(kekNumber) || !StringUtils.isNull(keNumber) || !StringUtils.isNull(description)) {

			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}

			int idx_olink = query.appendClassList(OutputDocumentLink.class, false);
			int idx_plink = query.appendClassList(OutputProjectLink.class, false);
			int idx_o = query.appendClassList(Output.class, false);
			int idx_p = query.appendClassList(Project.class, true);

			query.appendOpenParen();

			ClassAttribute roleAca = new ClassAttribute(Output.class, WTAttributeNameIfc.ID_NAME);
			ClassAttribute roleBca = new ClassAttribute(WTDocument.class, WTAttributeNameIfc.ID_NAME);
			SearchCondition sc = new SearchCondition(
					new ClassAttribute(OutputDocumentLink.class, "roleAObjectRef.key.id"), "=", roleAca);
			query.appendWhere(sc, new int[] { idx_olink, idx_o });
			query.appendAnd();
			sc = new SearchCondition(new ClassAttribute(OutputDocumentLink.class, "roleBObjectRef.key.id"), "=",
					roleBca);
			query.appendWhere(sc, new int[] { idx_olink, idx });
			query.appendAnd();

			roleAca = new ClassAttribute(Output.class, WTAttributeNameIfc.ID_NAME);
			roleBca = new ClassAttribute(Project.class, WTAttributeNameIfc.ID_NAME);

			sc = new SearchCondition(new ClassAttribute(OutputProjectLink.class, "roleAObjectRef.key.id"), "=",
					roleAca);
			query.appendWhere(sc, new int[] { idx_plink, idx_o });
			query.appendAnd();
			sc = new SearchCondition(new ClassAttribute(OutputProjectLink.class, "roleBObjectRef.key.id"), "=",
					roleBca);
			query.appendWhere(sc, new int[] { idx_plink, idx_p });

			query.appendCloseParen();

			QuerySpecUtils.toLikeAnd(query, idx_p, Project.class, Project.KEK_NUMBER, kekNumber);
			QuerySpecUtils.toLikeAnd(query, idx_p, Project.class, Project.KE_NUMBER, keNumber);
			QuerySpecUtils.toLikeAnd(query, idx_p, Project.class, Project.DESCRIPTION, description);
		}

		Folder folder = null;
		if (!StringUtils.isNull(oid)) {
			folder = (Folder) CommonUtils.getObject(oid);
		} else {
			if ("new".equalsIgnoreCase(type)) {
				folder = FolderTaskLogic.getFolder(OUTPUT_NEW_ROOT, CommonUtils.getPDMLinkProductContainer());
			} else if ("old".equals(type)) {
				folder = FolderTaskLogic.getFolder(OUTPUT_OLD_ROOT, CommonUtils.getPDMLinkProductContainer());
			}
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

			query.appendOpenParen();
			long fid = folder.getPersistInfo().getObjectIdentifier().getId();
			query.appendWhere(new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", "=", fid),
					new int[] { f_idx });

			ArrayList<Folder> folders = FolderUtils.recurciveFolder(folder, new ArrayList<Folder>());
			for (int i = 0; i < folders.size(); i++) {
				Folder sub = (Folder) folders.get(i);
				query.appendOr();
				long sfid = sub.getPersistInfo().getObjectIdentifier().getId();
				query.appendWhere(
						new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", "=", sfid),
						new int[] { f_idx });
			}

			query.appendCloseParen();
		}

		if (latest) {
			QuerySpecUtils.toLatest(query, idx, WTDocument.class);
		}

		QuerySpecUtils.creatorQuery(query, idx, WTDocument.class, creatorOid);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, WTDocument.class, WTDocument.CREATE_TIMESTAMP, createdFrom,
				createdTo);
		QuerySpecUtils.toState(query, idx, WTDocument.class, state);

		QuerySpecUtils.toOrderBy(query, idx, WTDocument.class, WTDocument.MODIFY_TIMESTAMP, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WTDocument output = (WTDocument) obj[0];
			OutputDTO dto = new OutputDTO(output);
			list.add(dto);
		}

		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	/**
	 * 산출물과 연결된 프로젝트
	 */
	public JSONArray jsonAuiProject(String oid) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();
		Output output = (Output) CommonUtils.getObject(oid);
		QueryResult result = PersistenceHelper.manager.navigate(output, "project", OutputProjectLink.class);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Project project = (Project) obj[0];
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
	 * 산출물 문서 번호
	 */
	public String getNextNumber() throws Exception {

		Calendar ca = Calendar.getInstance();
//		int day = ca.get(Calendar.DATE);
		int month = ca.get(Calendar.MONTH) + 1;
		int year = ca.get(Calendar.YEAR);
		DecimalFormat df = new DecimalFormat("00");
		String number = "PJ-" + df.format(year).substring(2) + df.format(month) + "-";

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTDocumentMaster.class, true);

		QuerySpecUtils.toLikeRightAnd(query, idx, WTDocumentMaster.class, WTDocumentMaster.NUMBER, number);
		QuerySpecUtils.toOrderBy(query, idx, WTDocumentMaster.class, WTDocumentMaster.NUMBER, true);
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

	/**
	 * 산출물과 관련된 작번들
	 */
	public ArrayList<Project> getProjects(WTDocument document) throws Exception {
		ArrayList<Project> list = new ArrayList<>();
		QueryResult result = PersistenceHelper.manager.navigate(document, "output", OutputDocumentLink.class);
		while (result.hasMoreElements()) {
			Output output = (Output) result.nextElement();

			QueryResult qr = PersistenceHelper.manager.navigate(output, "project", OutputProjectLink.class);
			while (qr.hasMoreElements()) {
				Project project = (Project) qr.nextElement();
				list.add(project);
			}
		}
		return list;
	}

}
