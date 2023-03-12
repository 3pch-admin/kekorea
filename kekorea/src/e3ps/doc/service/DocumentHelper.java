package e3ps.doc.service;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.bom.partlist.PartListMaster;
import e3ps.common.content.Contents;
import e3ps.common.content.ContentsPersistablesLink;
import e3ps.common.content.column.ContentsColumnData;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.DateUtils;
import e3ps.common.util.FolderUtils;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.SearchUtils;
import e3ps.common.util.StringUtils;
import e3ps.doc.E3PSDocumentMaster;
import e3ps.doc.PRJDocument;
import e3ps.doc.WTDocumentWTPartLink;
import e3ps.doc.column.DocumentColumnData;
import e3ps.doc.column.OldOutputColumnData;
import e3ps.doc.column.OutputColumnData;
import e3ps.doc.column.RequestDocumentColumnData;
import e3ps.doc.dto.DocumentDTO;
import e3ps.doc.request.RequestDocument;
import e3ps.org.People;
import e3ps.project.Project;
import e3ps.project.output.DocumentOutputLink;
import e3ps.project.output.Output;
import e3ps.project.output.ProjectOutputLink;
import wt.clients.folder.FolderTaskLogic;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.folder.IteratedFolderMemberLink;
import wt.part.WTPart;
import wt.query.ClassAttribute;
import wt.query.ColumnExpression;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SQLFunction;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;
import wt.vc.VersionControlHelper;
import wt.vc.wip.WorkInProgressHelper;

/**
 * 
 * @author JunHo
 * @since 2018-11-28
 * @version 1.0
 */
public class DocumentHelper {

	public static final String[] DOC_STATE_DISPLAY = new String[] { "작업 중", "승인 중", "승인됨", "반려됨" };

	public static final String[] DOC_STATE_VALUE = new String[] { "INWORK", "UNDERAPPROVAL", "RELEASED", "RETURN" };

	public static final String DEFAULT = "/Default";

	public static final String ROOT = "/Default/문서";

	public static final String OUTPUT_ROOT = "/Default/프로젝트";

	public static final String OLDOUTPUT_ROOT = "/Default/문서/프로젝트";

	public static final String ELEC_OUTPUT_ROOT = "/Default/프로젝트/전기_수배표";

	public static final String MACHINE_OUTPUT_ROOT = "/Default/프로젝트/기계_수배표";

	public static final String REQUEST_ROOT = "/Default/프로젝트/의뢰서";

	public static final String SPEC = "/Default/프로젝트/제작사양서";

	public static final String OLDSPEC = "/Default/문서/프로젝트/제작사양서";

	/**
	 * access service
	 */
	public static final DocumentService service = ServiceFactory.getService(DocumentService.class);

	/**
	 * access helper
	 */
	public static final DocumentHelper manager = new DocumentHelper();

	public Map<String, Object> findContents(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<ContentsColumnData> list = new ArrayList<ContentsColumnData>();
		QuerySpec query = null;

		// search param
		String number = (String) param.get("number");
		String name = (String) param.get("name");
		String fileName = (String) param.get("fileName");
		String creatorsOid = (String) param.get("creatorsOid");
		String modifierOid = (String) param.get("modifierOid");
		String statesDoc = (String) param.get("statesDoc");

		String predate = (String) param.get("predate");
		String postdate = (String) param.get("postdate");

		String predate_m = (String) param.get("predate_m");
		String postdate_m = (String) param.get("postdate_m");

		String sub_folder = (String) param.get("sub_folder");

		String latest = (String) param.get("latest");
		if (StringUtils.isNull(latest)) {
			latest = "true";
		}

		String location = (String) param.get("location");
		ReferenceFactory rf = new ReferenceFactory();
		// 정렬
		String sort = (String) param.get("sort");
		String sortKey = (String) param.get("sortKey");

		try {
			query = new QuerySpec();
			int idx_d = query.appendClassList(WTDocument.class, true);
			// int idx_d = query.appendClassList(Persistable.class, true);
			int idx_c = query.appendClassList(Contents.class, true);
			int idx_l = query.appendClassList(ContentsPersistablesLink.class, true);

			SearchCondition sc = null;
			ClassAttribute ca = null;

			sc = new SearchCondition(Contents.class, "thePersistInfo.theObjectIdentifier.id",
					ContentsPersistablesLink.class, "roleAObjectRef.key.id");
			query.appendWhere(sc, new int[] { idx_c, idx_l });

			query.appendAnd();

			sc = new SearchCondition(WTDocument.class, "thePersistInfo.theObjectIdentifier.id",
					ContentsPersistablesLink.class, "roleBObjectRef.key.id");
			query.appendWhere(sc, new int[] { idx_d, idx_l });

			if (!StringUtils.isNull(fileName)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(Contents.class, Contents.FILE_NAME);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(fileName);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx_c });
			}

			if (!StringUtils.isNull(name)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(WTDocument.class, WTDocument.NAME);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(name);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx_d });
			}

			// 대소문자 구분
			if (!StringUtils.isNull(number)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				ca = new ClassAttribute(WTDocument.class, WTDocument.NUMBER);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(number);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx_d });
			}

			if (!StringUtils.isNull(creatorsOid)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				People user = (People) rf.getReference(creatorsOid).getObject();
				long ids = user.getUser().getPersistInfo().getObjectIdentifier().getId();
				sc = new SearchCondition(WTDocument.class, "iterationInfo.creator.key.id", SearchCondition.EQUAL, ids);
				query.appendWhere(sc, new int[] { idx_d });
			}
			// 수정자
			if (!StringUtils.isNull(modifierOid)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				People user = (People) rf.getReference(modifierOid).getObject();
				long ids = user.getUser().getPersistInfo().getObjectIdentifier().getId();
				sc = new SearchCondition(WTDocument.class, "iterationInfo.modifier.key.id", SearchCondition.EQUAL, ids);
				query.appendWhere(sc, new int[] { idx_d });
			}

			if (!StringUtils.isNull(predate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp start = DateUtils.convertStartDate(predate);
				sc = new SearchCondition(WTDocument.class, WTAttributeNameIfc.CREATE_STAMP_NAME,
						SearchCondition.GREATER_THAN_OR_EQUAL, start);
				query.appendWhere(sc, new int[] { idx_d });
			}

			if (!StringUtils.isNull(postdate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp end = DateUtils.convertEndDate(postdate);
				sc = new SearchCondition(WTDocument.class, WTAttributeNameIfc.CREATE_STAMP_NAME,
						SearchCondition.LESS_THAN_OR_EQUAL, end);
				query.appendWhere(sc, new int[] { idx_d });
			}

			// 수정일
			if (!StringUtils.isNull(predate_m)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp start = DateUtils.convertStartDate(predate_m);
				sc = new SearchCondition(WTDocument.class, WTAttributeNameIfc.MODIFY_STAMP_NAME,
						SearchCondition.GREATER_THAN_OR_EQUAL, start);
				query.appendWhere(sc, new int[] { idx_d });
			}

			if (!StringUtils.isNull(postdate_m)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp end = DateUtils.convertEndDate(postdate_m);
				sc = new SearchCondition(WTDocument.class, WTAttributeNameIfc.MODIFY_STAMP_NAME,
						SearchCondition.LESS_THAN_OR_EQUAL, end);
				query.appendWhere(sc, new int[] { idx_d });
			}

			if (!StringUtils.isNull(statesDoc)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				sc = new SearchCondition(WTDocument.class, "state.state", SearchCondition.EQUAL, statesDoc);
				query.appendWhere(sc, new int[] { idx_d });
			}

			Folder folder = null;
			if (StringUtils.isNull(location)) {
				location = ROOT;
			}
			folder = FolderTaskLogic.getFolder(location, CommonUtils.getContainer());

			if (!StringUtils.isNull(folder)) {

				if (query.getConditionCount() > 0)
					query.appendAnd();
				int f_idx = query.appendClassList(IteratedFolderMemberLink.class, false);
				ClassAttribute fca = new ClassAttribute(IteratedFolderMemberLink.class, "roleBObjectRef.key.branchId");
				SearchCondition fsc = new SearchCondition(fca, "=",
						new ClassAttribute(WTDocument.class, "iterationInfo.branchId"));
				fsc.setFromIndicies(new int[] { f_idx, idx_d }, 0);
				fsc.setOuterJoin(0);
				query.appendWhere(fsc, new int[] { f_idx, idx_d });
				query.appendAnd();

				query.appendOpenParen();
				long fid = folder.getPersistInfo().getObjectIdentifier().getId();
				query.appendWhere(
						new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", "=", fid),
						new int[] { f_idx });

				if (!StringUtils.isNull(sub_folder)) {
					ArrayList<Folder> folders = FolderUtils.getSubFolders(folder, new ArrayList<Folder>());
					for (int i = 0; i < folders.size(); i++) {
						Folder sub = (Folder) folders.get(i);
						query.appendOr();
						long sfid = sub.getPersistInfo().getObjectIdentifier().getId();
						query.appendWhere(
								new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", "=", sfid),
								new int[] { f_idx });
					}
				}
				query.appendCloseParen();
			}

			if ("true".equals(latest)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				sc = VersionControlHelper.getSearchCondition(WTDocument.class, true);
				query.appendWhere(sc, new int[] { idx_d });

				CommonUtils.latestQuery(query, WTDocument.class, idx_d);
			}

			if (StringUtils.isNull(sort)) {
				sort = "true";
			}

			if (StringUtils.isNull(sortKey)) {
				// sortKey = WTAttributeNameIfc.CREATE_STAMP_NAME;
				sortKey = WTAttributeNameIfc.MODIFY_STAMP_NAME;
			}

			ca = new ClassAttribute(WTDocument.class, sortKey);
			OrderBy orderBy = new OrderBy(ca, Boolean.parseBoolean(sort));
			query.appendOrderBy(orderBy, new int[] { idx_d });

			query.setAdvancedQueryEnabled(true);
			query.setDescendantQuery(false);

			PageQueryUtils pager = new PageQueryUtils(param, query);
			PagingQueryResult result = pager.find();
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				Contents contents = (Contents) obj[1];
				ContentsColumnData data = new ContentsColumnData(contents);
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

	public Map<String, Object> find(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		List<DocumentColumnData> list = new ArrayList<DocumentColumnData>();

		String number = (String) params.get("number");
		String name = (String) params.get("name");
		String state = (String) params.get("state");
		String creatorsOid = (String) params.get("creatorsOid");
		String modifierOid = (String) params.get("modifierOid");
		String description = (String) params.get("description");
		String predate = (String) params.get("predate");
		String postdate = (String) params.get("postdate");
		String predate_m = (String) params.get("predate_m");
		String postdate_m = (String) params.get("postdate_m");
		String latest = (String) params.get("latest");
		String location = (String) params.get("location");
		if (StringUtils.isNull(latest)) {
			latest = "true";
		}

		ReferenceFactory rf = new ReferenceFactory();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTDocument.class, true);
		int master = query.appendClassList(WTDocumentMaster.class, false);

		SearchCondition sc = null;
		ClassAttribute ca = null;

		sc = WorkInProgressHelper.getSearchCondition_CI(WTDocument.class);
		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();

		sc = new SearchCondition(WTDocument.class, "masterReference.key.id", WTDocumentMaster.class,
				"thePersistInfo.theObjectIdentifier.id");
		query.appendWhere(sc, new int[] { idx, master });

		if (!StringUtils.isNull(name)) {
			if (query.getConditionCount() > 0)
				query.appendAnd();

			ca = new ClassAttribute(WTDocument.class, WTDocument.NAME);
			ColumnExpression ce = StringUtils.getUpperColumnExpression(name);
			SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
			sc = new SearchCondition(function, SearchCondition.LIKE, ce);
			query.appendWhere(sc, new int[] { idx });
		}

		// 대소문자 구분
		if (!StringUtils.isNull(number)) {
			if (query.getConditionCount() > 0)
				query.appendAnd();
			ca = new ClassAttribute(WTDocument.class, WTDocument.NUMBER);
			ColumnExpression ce = StringUtils.getUpperColumnExpression(number);
			SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
			sc = new SearchCondition(function, SearchCondition.LIKE, ce);
			query.appendWhere(sc, new int[] { idx });
		}

		if (!StringUtils.isNull(description)) {
			if (query.getConditionCount() > 0)
				query.appendAnd();

			ca = new ClassAttribute(WTDocument.class, WTDocument.DESCRIPTION);
			ColumnExpression ce = StringUtils.getUpperColumnExpression(description);
			SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
			sc = new SearchCondition(function, SearchCondition.LIKE, ce);
			query.appendWhere(sc, new int[] { idx });
		}

		if (!StringUtils.isNull(creatorsOid)) {
			if (query.getConditionCount() > 0)
				query.appendAnd();
			People user = (People) rf.getReference(creatorsOid).getObject();
			long ids = user.getUser().getPersistInfo().getObjectIdentifier().getId();
			sc = new SearchCondition(WTDocument.class, "iterationInfo.creator.key.id", SearchCondition.EQUAL, ids);
			query.appendWhere(sc, new int[] { idx });
		}

		// 수정자
		if (!StringUtils.isNull(modifierOid)) {
			if (query.getConditionCount() > 0)
				query.appendAnd();
			People user = (People) rf.getReference(modifierOid).getObject();
			long ids = user.getUser().getPersistInfo().getObjectIdentifier().getId();
			sc = new SearchCondition(WTDocument.class, "iterationInfo.modifier.key.id", SearchCondition.EQUAL, ids);
			query.appendWhere(sc, new int[] { idx });
		}

		if (!StringUtils.isNull(state)) {
			if (query.getConditionCount() > 0)
				query.appendAnd();

			sc = new SearchCondition(WTDocument.class, "state.state", SearchCondition.EQUAL, state);
			query.appendWhere(sc, new int[] { idx });
		}

		if (!StringUtils.isNull(predate)) {
			if (query.getConditionCount() > 0)
				query.appendAnd();
			Timestamp start = DateUtils.convertStartDate(predate);
			sc = new SearchCondition(WTDocument.class, WTAttributeNameIfc.CREATE_STAMP_NAME,
					SearchCondition.GREATER_THAN_OR_EQUAL, start);
			query.appendWhere(sc, new int[] { idx });
		}

		if (!StringUtils.isNull(postdate)) {
			if (query.getConditionCount() > 0)
				query.appendAnd();
			Timestamp end = DateUtils.convertEndDate(postdate);
			sc = new SearchCondition(WTDocument.class, WTAttributeNameIfc.CREATE_STAMP_NAME,
					SearchCondition.LESS_THAN_OR_EQUAL, end);
			query.appendWhere(sc, new int[] { idx });
		}

		// 수정일
		if (!StringUtils.isNull(predate_m)) {
			if (query.getConditionCount() > 0)
				query.appendAnd();
			Timestamp start = DateUtils.convertStartDate(predate_m);
			sc = new SearchCondition(WTDocument.class, WTAttributeNameIfc.MODIFY_STAMP_NAME,
					SearchCondition.GREATER_THAN_OR_EQUAL, start);
			query.appendWhere(sc, new int[] { idx });
		}

		if (!StringUtils.isNull(postdate_m)) {
			if (query.getConditionCount() > 0)
				query.appendAnd();
			Timestamp end = DateUtils.convertEndDate(postdate_m);
			sc = new SearchCondition(WTDocument.class, WTAttributeNameIfc.MODIFY_STAMP_NAME,
					SearchCondition.LESS_THAN_OR_EQUAL, end);
			query.appendWhere(sc, new int[] { idx });
		}

		Folder folder = null;
		if (StringUtils.isNull(location)) {
			location = ROOT;
		}

		folder = FolderTaskLogic.getFolder(location, CommonUtils.getContainer());

		if (!StringUtils.isNull(folder)) {
			if (query.getConditionCount() > 0)
				query.appendAnd();
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

			ArrayList<Folder> folders = FolderUtils.getSubFolders(folder, new ArrayList<Folder>());
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

		if ("true".equals(latest)) {
			if (query.getConditionCount() > 0)
				query.appendAnd();
			sc = VersionControlHelper.getSearchCondition(WTDocument.class, true);
			query.appendWhere(sc, new int[] { idx });

			CommonUtils.latestQuery(query, WTDocument.class, idx);
		}

		SearchUtils.appendOrderBy(query, WTDocument.class, WTDocument.NUMBER, idx, false);

		query.setAdvancedQueryEnabled(true);
		query.setDescendantsIncluded(false, master);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WTDocument document = (WTDocument) obj[0];
			DocumentColumnData column = new DocumentColumnData(document);
			list.add(column);
		}

		map.put("list", list);
		map.put("topListCount", pager.getTotal());
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		map.put("pageSize", pager.getPsize());
		map.put("total", pager.getTotalSize());
		map.put("result", "SUCCESS");
		return map;
	}

	public ArrayList<ProjectOutputLink> getProjectOutputLink(WTDocument doc) throws Exception {

		ArrayList<ProjectOutputLink> list = new ArrayList<ProjectOutputLink>();

		// QueryResult result = PersistenceHelper.manager.navigate(task, "output",
		// TaskOutputLink.class);
		QueryResult result = PersistenceHelper.manager.navigate(doc, "output", DocumentOutputLink.class);
		while (result.hasMoreElements()) {

			Output output = (Output) result.nextElement();

			QueryResult qr = PersistenceHelper.manager.navigate(output, "project", ProjectOutputLink.class, false);
			if (qr.hasMoreElements()) {
				ProjectOutputLink link = (ProjectOutputLink) qr.nextElement();
				list.add(link);
			}
		}
		return list;
	}

	public ArrayList<ReqDocumentProjectLink> getProjectReqLink(RequestDocument doc) throws Exception {

		ArrayList<ReqDocumentProjectLink> list = new ArrayList<ReqDocumentProjectLink>();
		QueryResult result = PersistenceHelper.manager.navigate(doc, "project", ReqDocumentProjectLink.class, false);
		while (result.hasMoreElements()) {
			ReqDocumentProjectLink link = (ReqDocumentProjectLink) result.nextElement();
			list.add(link);
		}
		return list;
	}

	public ArrayList<WTPart> getWTPart(WTDocument document) {
		ArrayList<WTPart> list = new ArrayList<WTPart>();
		try {

			QueryResult result = PersistenceHelper.manager.navigate(document, "part", WTDocumentWTPartLink.class);
			while (result.hasMoreElements()) {
				WTPart refPart = (WTPart) result.nextElement();
				list.add(refPart);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public ContentsPersistablesLink getWTDOcument(Contents content) {
		ContentsPersistablesLink link = null;
		try {
			QueryResult result = PersistenceHelper.manager.navigate(content, "persistables",
					ContentsPersistablesLink.class, false);
			// result.hasMoreElements()
			while (result.hasMoreElements()) {
				link = (ContentsPersistablesLink) result.nextElement();
			}
			// WTPart refPart = (WTPart) result.nextElement();
			// list.add(refPart);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return link;
	}

	public ArrayList<WTDocumentWTPartLink> getWTDocumentWTPartLink(WTDocument document) {
		ArrayList<WTDocumentWTPartLink> list = new ArrayList<WTDocumentWTPartLink>();
		try {

			QueryResult result = PersistenceHelper.manager.navigate(document, "part", WTDocumentWTPartLink.class,
					false);
			while (result.hasMoreElements()) {
				WTDocumentWTPartLink link = (WTDocumentWTPartLink) result.nextElement();
				list.add(link);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

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

	public Map<String, Object> findOldOutput(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<OldOutputColumnData> list = new ArrayList<OldOutputColumnData>();
		QuerySpec query = null;

		// search param
		String number = (String) param.get("number");
		String name = (String) param.get("name");
		String statesDoc = (String) param.get("statesDoc");
		String creatorsOid = (String) param.get("creatorsOid");
		String modifierOid = (String) param.get("modifierOid");
		String description = (String) param.get("description");

		String keNumber = (String) param.get("keNumber");
		String kekNumber = (String) param.get("kekNumber");
		String mak = (String) param.get("mak");
		String kek_description = (String) param.get("kek_description");

		String predate = (String) param.get("predate");
		String postdate = (String) param.get("postdate");

		String predate_m = (String) param.get("predate_m");
		String postdate_m = (String) param.get("postdate_m");

		String sub_folder = (String) param.get("sub_folder");

		String latest = (String) param.get("latest");
		if (StringUtils.isNull(latest)) {
			latest = "true";
		}

		String location = (String) param.get("location");
		ReferenceFactory rf = new ReferenceFactory();
		// 정렬
		String sort = (String) param.get("sort");
		String sortKey = (String) param.get("sortKey");

		try {
			query = new QuerySpec();

			int idx = query.appendClassList(PRJDocument.class, true);
			int master = query.appendClassList(E3PSDocumentMaster.class, false);

			SearchCondition sc = null;
			ClassAttribute ca = null;

			sc = new SearchCondition(PRJDocument.class, "masterReference.key.id", E3PSDocumentMaster.class,
					"thePersistInfo.theObjectIdentifier.id");
			query.appendWhere(sc, new int[] { idx, master });

			if (!StringUtils.isNull(name)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(PRJDocument.class, PRJDocument.NAME);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(name);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			// 대소문자 구분
			if (!StringUtils.isNull(number)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				ca = new ClassAttribute(PRJDocument.class, PRJDocument.NUMBER);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(number);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(description)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(PRJDocument.class, PRJDocument.DESCRIPTION);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(description);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(creatorsOid)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				People user = (People) rf.getReference(creatorsOid).getObject();
				long ids = user.getUser().getPersistInfo().getObjectIdentifier().getId();
				sc = new SearchCondition(PRJDocument.class, "iterationInfo.creator.key.id", SearchCondition.EQUAL, ids);
				query.appendWhere(sc, new int[] { idx });
			}

			// 수정자
			if (!StringUtils.isNull(modifierOid)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				People user = (People) rf.getReference(modifierOid).getObject();
				long ids = user.getUser().getPersistInfo().getObjectIdentifier().getId();
				sc = new SearchCondition(PRJDocument.class, "iterationInfo.modifier.key.id", SearchCondition.EQUAL,
						ids);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(statesDoc)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				sc = new SearchCondition(PRJDocument.class, "state.state", SearchCondition.EQUAL, statesDoc);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(predate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp start = DateUtils.convertStartDate(predate);
				sc = new SearchCondition(PRJDocument.class, WTAttributeNameIfc.CREATE_STAMP_NAME,
						SearchCondition.GREATER_THAN_OR_EQUAL, start);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(postdate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp end = DateUtils.convertEndDate(postdate);
				sc = new SearchCondition(PRJDocument.class, WTAttributeNameIfc.CREATE_STAMP_NAME,
						SearchCondition.LESS_THAN_OR_EQUAL, end);
				query.appendWhere(sc, new int[] { idx });
			}

			// 수정일
			if (!StringUtils.isNull(predate_m)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp start = DateUtils.convertStartDate(predate_m);
				sc = new SearchCondition(PRJDocument.class, WTAttributeNameIfc.MODIFY_STAMP_NAME,
						SearchCondition.GREATER_THAN_OR_EQUAL, start);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(postdate_m)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp end = DateUtils.convertEndDate(postdate_m);
				sc = new SearchCondition(PRJDocument.class, WTAttributeNameIfc.MODIFY_STAMP_NAME,
						SearchCondition.LESS_THAN_OR_EQUAL, end);
				query.appendWhere(sc, new int[] { idx });
			}

			Folder folder = null;
			if (StringUtils.isNull(location)) {
				location = OLDOUTPUT_ROOT;
			}

			folder = FolderTaskLogic.getFolder(location, CommonUtils.getContainer());

			if (!StringUtils.isNull(folder)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				int f_idx = query.appendClassList(IteratedFolderMemberLink.class, false);
				ClassAttribute fca = new ClassAttribute(IteratedFolderMemberLink.class, "roleBObjectRef.key.branchId");
				SearchCondition fsc = new SearchCondition(fca, "=",
						new ClassAttribute(PRJDocument.class, "iterationInfo.branchId"));
				fsc.setFromIndicies(new int[] { f_idx, idx }, 0);
				fsc.setOuterJoin(0);
				query.appendWhere(fsc, new int[] { f_idx, idx });
				query.appendAnd();

				query.appendOpenParen();
				long fid = folder.getPersistInfo().getObjectIdentifier().getId();
				query.appendWhere(
						new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", "=", fid),
						new int[] { f_idx });

				if (!StringUtils.isNull(sub_folder)) {
					ArrayList<Folder> folders = FolderUtils.getSubFolders(folder, new ArrayList<Folder>());
					for (int i = 0; i < folders.size(); i++) {
						Folder sub = (Folder) folders.get(i);
						query.appendOr();
						long sfid = sub.getPersistInfo().getObjectIdentifier().getId();
						query.appendWhere(
								new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", "=", sfid),
								new int[] { f_idx });
					}
				}
				query.appendCloseParen();
			}

			if (!StringUtils.isNull(keNumber)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				int idx_olink = query.appendClassList(DocumentOutputLink.class, false);
				int idx_plink = query.appendClassList(ProjectOutputLink.class, false);
				int idx_o = query.appendClassList(Output.class, false);
				int idx_p = query.appendClassList(Project.class, true);

				ClassAttribute roleAca = null;
				ClassAttribute roleBca = null;

				query.appendOpenParen();

				roleAca = new ClassAttribute(Output.class, WTAttributeNameIfc.ID_NAME);
				roleBca = new ClassAttribute(WTDocument.class, WTAttributeNameIfc.ID_NAME);

				sc = new SearchCondition(new ClassAttribute(DocumentOutputLink.class, "roleAObjectRef.key.id"), "=",
						roleAca);
				query.appendWhere(sc, new int[] { idx_olink, idx_o });
				query.appendAnd();
				sc = new SearchCondition(new ClassAttribute(DocumentOutputLink.class, "roleBObjectRef.key.id"), "=",
						roleBca);
				query.appendWhere(sc, new int[] { idx_olink, idx });

				query.appendAnd();
				roleAca = new ClassAttribute(Output.class, WTAttributeNameIfc.ID_NAME);
				roleBca = new ClassAttribute(Project.class, WTAttributeNameIfc.ID_NAME);

				sc = new SearchCondition(new ClassAttribute(ProjectOutputLink.class, "roleAObjectRef.key.id"), "=",
						roleAca);
				query.appendWhere(sc, new int[] { idx_plink, idx_o });
				query.appendAnd();
				sc = new SearchCondition(new ClassAttribute(ProjectOutputLink.class, "roleBObjectRef.key.id"), "=",
						roleBca);
				query.appendWhere(sc, new int[] { idx_plink, idx_p });

				query.appendCloseParen();

				ca = new ClassAttribute(Project.class, Project.KE_NUMBER);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(keNumber);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx_p });
			}

			if (!StringUtils.isNull(kekNumber)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				int idx_olink = query.appendClassList(DocumentOutputLink.class, false);
				int idx_plink = query.appendClassList(ProjectOutputLink.class, false);
				int idx_o = query.appendClassList(Output.class, false);
				int idx_p = query.appendClassList(Project.class, true);

				ClassAttribute roleAca = null;
				ClassAttribute roleBca = null;

				query.appendOpenParen();

				roleAca = new ClassAttribute(Output.class, WTAttributeNameIfc.ID_NAME);
				roleBca = new ClassAttribute(WTDocument.class, WTAttributeNameIfc.ID_NAME);

				sc = new SearchCondition(new ClassAttribute(DocumentOutputLink.class, "roleAObjectRef.key.id"), "=",
						roleAca);
				query.appendWhere(sc, new int[] { idx_olink, idx_o });
				query.appendAnd();
				sc = new SearchCondition(new ClassAttribute(DocumentOutputLink.class, "roleBObjectRef.key.id"), "=",
						roleBca);
				query.appendWhere(sc, new int[] { idx_olink, idx });

				query.appendAnd();
				roleAca = new ClassAttribute(Output.class, WTAttributeNameIfc.ID_NAME);
				roleBca = new ClassAttribute(Project.class, WTAttributeNameIfc.ID_NAME);

				sc = new SearchCondition(new ClassAttribute(ProjectOutputLink.class, "roleAObjectRef.key.id"), "=",
						roleAca);
				query.appendWhere(sc, new int[] { idx_plink, idx_o });
				query.appendAnd();
				sc = new SearchCondition(new ClassAttribute(ProjectOutputLink.class, "roleBObjectRef.key.id"), "=",
						roleBca);
				query.appendWhere(sc, new int[] { idx_plink, idx_p });

				query.appendCloseParen();
				query.appendAnd();
				ca = new ClassAttribute(Project.class, Project.KEK_NUMBER);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(kekNumber);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx_p });
			}

			if (!StringUtils.isNull(kek_description)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				int idx_olink = query.appendClassList(DocumentOutputLink.class, false);
				int idx_plink = query.appendClassList(ProjectOutputLink.class, false);
				int idx_o = query.appendClassList(Output.class, false);
				int idx_p = query.appendClassList(Project.class, true);

				ClassAttribute roleAca = null;
				ClassAttribute roleBca = null;

				query.appendOpenParen();

				roleAca = new ClassAttribute(Output.class, WTAttributeNameIfc.ID_NAME);
				roleBca = new ClassAttribute(WTDocument.class, WTAttributeNameIfc.ID_NAME);

				sc = new SearchCondition(new ClassAttribute(DocumentOutputLink.class, "roleAObjectRef.key.id"), "=",
						roleAca);
				query.appendWhere(sc, new int[] { idx_olink, idx_o });
				query.appendAnd();
				sc = new SearchCondition(new ClassAttribute(DocumentOutputLink.class, "roleBObjectRef.key.id"), "=",
						roleBca);
				query.appendWhere(sc, new int[] { idx_olink, idx });

				query.appendAnd();
				roleAca = new ClassAttribute(Output.class, WTAttributeNameIfc.ID_NAME);
				roleBca = new ClassAttribute(Project.class, WTAttributeNameIfc.ID_NAME);

				sc = new SearchCondition(new ClassAttribute(ProjectOutputLink.class, "roleAObjectRef.key.id"), "=",
						roleAca);
				query.appendWhere(sc, new int[] { idx_plink, idx_o });
				query.appendAnd();
				sc = new SearchCondition(new ClassAttribute(ProjectOutputLink.class, "roleBObjectRef.key.id"), "=",
						roleBca);
				query.appendWhere(sc, new int[] { idx_plink, idx_p });

				query.appendCloseParen();

				query.appendAnd();
				ca = new ClassAttribute(Project.class, Project.DESCRIPTION);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(kek_description);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx_p });
			}

			if (!StringUtils.isNull(mak)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				int idx_olink = query.appendClassList(DocumentOutputLink.class, false);
				int idx_plink = query.appendClassList(ProjectOutputLink.class, false);
				int idx_o = query.appendClassList(Output.class, false);
				int idx_p = query.appendClassList(Project.class, true);

				ClassAttribute roleAca = null;
				ClassAttribute roleBca = null;

				query.appendOpenParen();

				roleAca = new ClassAttribute(Output.class, WTAttributeNameIfc.ID_NAME);
				roleBca = new ClassAttribute(WTDocument.class, WTAttributeNameIfc.ID_NAME);

				sc = new SearchCondition(new ClassAttribute(DocumentOutputLink.class, "roleAObjectRef.key.id"), "=",
						roleAca);
				query.appendWhere(sc, new int[] { idx_olink, idx_o });
				query.appendAnd();
				sc = new SearchCondition(new ClassAttribute(DocumentOutputLink.class, "roleBObjectRef.key.id"), "=",
						roleBca);
				query.appendWhere(sc, new int[] { idx_olink, idx });

				query.appendAnd();
				roleAca = new ClassAttribute(Output.class, WTAttributeNameIfc.ID_NAME);
				roleBca = new ClassAttribute(Project.class, WTAttributeNameIfc.ID_NAME);

				sc = new SearchCondition(new ClassAttribute(ProjectOutputLink.class, "roleAObjectRef.key.id"), "=",
						roleAca);
				query.appendWhere(sc, new int[] { idx_plink, idx_o });
				query.appendAnd();
				sc = new SearchCondition(new ClassAttribute(ProjectOutputLink.class, "roleBObjectRef.key.id"), "=",
						roleBca);
				query.appendWhere(sc, new int[] { idx_plink, idx_p });

				query.appendCloseParen();

				query.appendAnd();
				ca = new ClassAttribute(Project.class, Project.MAK);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(mak);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx_p });
			}

			if ("true".equals(latest)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				sc = VersionControlHelper.getSearchCondition(PRJDocument.class, true);
				query.appendWhere(sc, new int[] { idx });

				CommonUtils.latestQuery(query, PRJDocument.class, idx);
			}

			if (StringUtils.isNull(sort)) {
				sort = "true";
			}

			if (StringUtils.isNull(sortKey)) {
				sortKey = WTAttributeNameIfc.MODIFY_STAMP_NAME;
			}

			System.out.println("query=" + query);

			ca = new ClassAttribute(PRJDocument.class, sortKey);
			OrderBy orderBy = new OrderBy(ca, Boolean.parseBoolean(sort));
			query.appendOrderBy(orderBy, new int[] { idx });

			query.setAdvancedQueryEnabled(true);
			query.setDescendantQuery(false);

			PageQueryUtils pager = new PageQueryUtils(param, query);
			PagingQueryResult result = pager.find();
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				PRJDocument document = (PRJDocument) obj[0];
				// Project project = (Project) obj[1];
				OldOutputColumnData data = new OldOutputColumnData(document);
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

	public Map<String, Object> findOutput(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<OutputColumnData> list = new ArrayList<OutputColumnData>();
		QuerySpec query = null;

		// search param
		String number = (String) param.get("number");
		String name = (String) param.get("name");
		String statesDoc = (String) param.get("statesDoc");
		String creatorsOid = (String) param.get("creatorsOid");
		String modifierOid = (String) param.get("modifierOid");
		String description = (String) param.get("description");

		String keNumber = (String) param.get("keNumber");
		String kekNumber = (String) param.get("kekNumber");
		String mak = (String) param.get("mak");
		String kek_description = (String) param.get("kek_description");

		String predate = (String) param.get("predate");
		String postdate = (String) param.get("postdate");

		String predate_m = (String) param.get("predate_m");
		String postdate_m = (String) param.get("postdate_m");

		String sub_folder = (String) param.get("sub_folder");

		String latest = (String) param.get("latest");
		if (StringUtils.isNull(latest)) {
			latest = "true";
		}

		String location = (String) param.get("location");
		ReferenceFactory rf = new ReferenceFactory();
		// 정렬
		String sort = (String) param.get("sort");
		String sortKey = (String) param.get("sortKey");

		try {
			query = new QuerySpec();

			int idx = query.appendClassList(WTDocument.class, true);
			int master = query.appendClassList(WTDocumentMaster.class, false);

			SearchCondition sc = null;
			ClassAttribute ca = null;

			sc = WorkInProgressHelper.getSearchCondition_CI(WTDocument.class);
			query.appendWhere(sc, new int[] { idx });
			query.appendAnd();
			sc = new SearchCondition(WTDocument.class, "masterReference.key.id", WTDocumentMaster.class,
					"thePersistInfo.theObjectIdentifier.id");
			query.appendWhere(sc, new int[] { idx, master });

			if (!StringUtils.isNull(name)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(WTDocument.class, WTDocument.NAME);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(name);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			// 대소문자 구분
			if (!StringUtils.isNull(number)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				ca = new ClassAttribute(WTDocument.class, WTDocument.NUMBER);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(number);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(description)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(WTDocument.class, WTDocument.DESCRIPTION);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(description);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(creatorsOid)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				People user = (People) rf.getReference(creatorsOid).getObject();
				long ids = user.getUser().getPersistInfo().getObjectIdentifier().getId();
				sc = new SearchCondition(WTDocument.class, "iterationInfo.creator.key.id", SearchCondition.EQUAL, ids);
				query.appendWhere(sc, new int[] { idx });
			}

			// 수정자
			if (!StringUtils.isNull(modifierOid)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				People user = (People) rf.getReference(modifierOid).getObject();
				long ids = user.getUser().getPersistInfo().getObjectIdentifier().getId();
				sc = new SearchCondition(WTDocument.class, "iterationInfo.modifier.key.id", SearchCondition.EQUAL, ids);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(statesDoc)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				sc = new SearchCondition(WTDocument.class, "state.state", SearchCondition.EQUAL, statesDoc);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(predate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp start = DateUtils.convertStartDate(predate);
				sc = new SearchCondition(WTDocument.class, WTAttributeNameIfc.CREATE_STAMP_NAME,
						SearchCondition.GREATER_THAN_OR_EQUAL, start);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(postdate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp end = DateUtils.convertEndDate(postdate);
				sc = new SearchCondition(WTDocument.class, WTAttributeNameIfc.CREATE_STAMP_NAME,
						SearchCondition.LESS_THAN_OR_EQUAL, end);
				query.appendWhere(sc, new int[] { idx });
			}

			// 수정일
			if (!StringUtils.isNull(predate_m)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp start = DateUtils.convertStartDate(predate_m);
				sc = new SearchCondition(WTDocument.class, WTAttributeNameIfc.MODIFY_STAMP_NAME,
						SearchCondition.GREATER_THAN_OR_EQUAL, start);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(postdate_m)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp end = DateUtils.convertEndDate(postdate_m);
				sc = new SearchCondition(WTDocument.class, WTAttributeNameIfc.MODIFY_STAMP_NAME,
						SearchCondition.LESS_THAN_OR_EQUAL, end);
				query.appendWhere(sc, new int[] { idx });
			}

			Folder folder = null;
			if (StringUtils.isNull(location)) {
				location = OUTPUT_ROOT;
			}

			folder = FolderTaskLogic.getFolder(location, CommonUtils.getContainer());

			if (!StringUtils.isNull(folder)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
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
				query.appendWhere(
						new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", "=", fid),
						new int[] { f_idx });

				if (!StringUtils.isNull(sub_folder)) {
					ArrayList<Folder> folders = FolderUtils.getSubFolders(folder, new ArrayList<Folder>());
					for (int i = 0; i < folders.size(); i++) {
						Folder sub = (Folder) folders.get(i);
						query.appendOr();
						long sfid = sub.getPersistInfo().getObjectIdentifier().getId();
						query.appendWhere(
								new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", "=", sfid),
								new int[] { f_idx });
					}
				}
				query.appendCloseParen();
			}

			if (!StringUtils.isNull(keNumber)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				int idx_olink = query.appendClassList(DocumentOutputLink.class, false);
				int idx_plink = query.appendClassList(ProjectOutputLink.class, false);
				int idx_o = query.appendClassList(Output.class, false);
				int idx_p = query.appendClassList(Project.class, true);

				ClassAttribute roleAca = null;
				ClassAttribute roleBca = null;

				query.appendOpenParen();

				roleAca = new ClassAttribute(Output.class, WTAttributeNameIfc.ID_NAME);
				roleBca = new ClassAttribute(WTDocument.class, WTAttributeNameIfc.ID_NAME);

				sc = new SearchCondition(new ClassAttribute(DocumentOutputLink.class, "roleAObjectRef.key.id"), "=",
						roleAca);
				query.appendWhere(sc, new int[] { idx_olink, idx_o });
				query.appendAnd();
				sc = new SearchCondition(new ClassAttribute(DocumentOutputLink.class, "roleBObjectRef.key.id"), "=",
						roleBca);
				query.appendWhere(sc, new int[] { idx_olink, idx });

				query.appendAnd();
				roleAca = new ClassAttribute(Output.class, WTAttributeNameIfc.ID_NAME);
				roleBca = new ClassAttribute(Project.class, WTAttributeNameIfc.ID_NAME);

				sc = new SearchCondition(new ClassAttribute(ProjectOutputLink.class, "roleAObjectRef.key.id"), "=",
						roleAca);
				query.appendWhere(sc, new int[] { idx_plink, idx_o });
				query.appendAnd();
				sc = new SearchCondition(new ClassAttribute(ProjectOutputLink.class, "roleBObjectRef.key.id"), "=",
						roleBca);
				query.appendWhere(sc, new int[] { idx_plink, idx_p });

				query.appendCloseParen();
				query.appendAnd();
				ca = new ClassAttribute(Project.class, Project.KE_NUMBER);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(keNumber);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx_p });
			}

			if (!StringUtils.isNull(kekNumber)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				int idx_olink = query.appendClassList(DocumentOutputLink.class, false);
				int idx_plink = query.appendClassList(ProjectOutputLink.class, false);
				int idx_o = query.appendClassList(Output.class, false);
				int idx_p = query.appendClassList(Project.class, true);

				ClassAttribute roleAca = null;
				ClassAttribute roleBca = null;

				query.appendOpenParen();

				roleAca = new ClassAttribute(Output.class, WTAttributeNameIfc.ID_NAME);
				roleBca = new ClassAttribute(WTDocument.class, WTAttributeNameIfc.ID_NAME);

				sc = new SearchCondition(new ClassAttribute(DocumentOutputLink.class, "roleAObjectRef.key.id"), "=",
						roleAca);
				query.appendWhere(sc, new int[] { idx_olink, idx_o });
				query.appendAnd();
				sc = new SearchCondition(new ClassAttribute(DocumentOutputLink.class, "roleBObjectRef.key.id"), "=",
						roleBca);
				query.appendWhere(sc, new int[] { idx_olink, idx });

				query.appendAnd();
				roleAca = new ClassAttribute(Output.class, WTAttributeNameIfc.ID_NAME);
				roleBca = new ClassAttribute(Project.class, WTAttributeNameIfc.ID_NAME);

				sc = new SearchCondition(new ClassAttribute(ProjectOutputLink.class, "roleAObjectRef.key.id"), "=",
						roleAca);
				query.appendWhere(sc, new int[] { idx_plink, idx_o });
				query.appendAnd();
				sc = new SearchCondition(new ClassAttribute(ProjectOutputLink.class, "roleBObjectRef.key.id"), "=",
						roleBca);
				query.appendWhere(sc, new int[] { idx_plink, idx_p });

				query.appendCloseParen();
				query.appendAnd();
				ca = new ClassAttribute(Project.class, Project.KEK_NUMBER);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(kekNumber);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx_p });
			}

			if (!StringUtils.isNull(kek_description)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				int idx_olink = query.appendClassList(DocumentOutputLink.class, false);
				int idx_plink = query.appendClassList(ProjectOutputLink.class, false);
				int idx_o = query.appendClassList(Output.class, false);
				int idx_p = query.appendClassList(Project.class, true);

				ClassAttribute roleAca = null;
				ClassAttribute roleBca = null;

				query.appendOpenParen();

				roleAca = new ClassAttribute(Output.class, WTAttributeNameIfc.ID_NAME);
				roleBca = new ClassAttribute(WTDocument.class, WTAttributeNameIfc.ID_NAME);

				sc = new SearchCondition(new ClassAttribute(DocumentOutputLink.class, "roleAObjectRef.key.id"), "=",
						roleAca);
				query.appendWhere(sc, new int[] { idx_olink, idx_o });
				query.appendAnd();
				sc = new SearchCondition(new ClassAttribute(DocumentOutputLink.class, "roleBObjectRef.key.id"), "=",
						roleBca);
				query.appendWhere(sc, new int[] { idx_olink, idx });

				query.appendAnd();
				roleAca = new ClassAttribute(Output.class, WTAttributeNameIfc.ID_NAME);
				roleBca = new ClassAttribute(Project.class, WTAttributeNameIfc.ID_NAME);

				sc = new SearchCondition(new ClassAttribute(ProjectOutputLink.class, "roleAObjectRef.key.id"), "=",
						roleAca);
				query.appendWhere(sc, new int[] { idx_plink, idx_o });
				query.appendAnd();
				sc = new SearchCondition(new ClassAttribute(ProjectOutputLink.class, "roleBObjectRef.key.id"), "=",
						roleBca);
				query.appendWhere(sc, new int[] { idx_plink, idx_p });

				query.appendCloseParen();

				query.appendAnd();
				ca = new ClassAttribute(Project.class, Project.DESCRIPTION);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(kek_description);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx_p });
			}

			if (!StringUtils.isNull(mak)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				int idx_olink = query.appendClassList(DocumentOutputLink.class, false);
				int idx_plink = query.appendClassList(ProjectOutputLink.class, false);
				int idx_o = query.appendClassList(Output.class, false);
				int idx_p = query.appendClassList(Project.class, true);

				ClassAttribute roleAca = null;
				ClassAttribute roleBca = null;

				query.appendOpenParen();

				roleAca = new ClassAttribute(Output.class, WTAttributeNameIfc.ID_NAME);
				roleBca = new ClassAttribute(WTDocument.class, WTAttributeNameIfc.ID_NAME);

				sc = new SearchCondition(new ClassAttribute(DocumentOutputLink.class, "roleAObjectRef.key.id"), "=",
						roleAca);
				query.appendWhere(sc, new int[] { idx_olink, idx_o });
				query.appendAnd();
				sc = new SearchCondition(new ClassAttribute(DocumentOutputLink.class, "roleBObjectRef.key.id"), "=",
						roleBca);
				query.appendWhere(sc, new int[] { idx_olink, idx });

				query.appendAnd();
				roleAca = new ClassAttribute(Output.class, WTAttributeNameIfc.ID_NAME);
				roleBca = new ClassAttribute(Project.class, WTAttributeNameIfc.ID_NAME);

				sc = new SearchCondition(new ClassAttribute(ProjectOutputLink.class, "roleAObjectRef.key.id"), "=",
						roleAca);
				query.appendWhere(sc, new int[] { idx_plink, idx_o });
				query.appendAnd();
				sc = new SearchCondition(new ClassAttribute(ProjectOutputLink.class, "roleBObjectRef.key.id"), "=",
						roleBca);
				query.appendWhere(sc, new int[] { idx_plink, idx_p });

				query.appendCloseParen();

				query.appendAnd();
				ca = new ClassAttribute(Project.class, Project.MAK);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(mak);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx_p });
			}

			if ("true".equals(latest)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				sc = VersionControlHelper.getSearchCondition(WTDocument.class, true);
				query.appendWhere(sc, new int[] { idx });

				CommonUtils.latestQuery(query, WTDocument.class, idx);
			}

			if (StringUtils.isNull(sort)) {
				sort = "true";
			}

			if (StringUtils.isNull(sortKey)) {
				sortKey = WTDocument.MODIFY_TIMESTAMP;
			}

			System.out.println("query=" + query);

			/*
			 * ca = new ClassAttribute(WTDocument.class, sortKey); OrderBy orderBy = new
			 * OrderBy(ca, Boolean.parseBoolean(sort)); query.appendOrderBy(orderBy, new
			 * int[] { idx });
			 */
			SearchUtils.appendOrderBy(query, WTDocument.class, sortKey, idx, Boolean.parseBoolean(sort));
			query.setAdvancedQueryEnabled(true);
			// query.setDescendantQuery(false);
			query.setDescendantsIncluded(false, master);

			PageQueryUtils pager = new PageQueryUtils(param, query);
			PagingQueryResult result = pager.find();
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				WTDocument document = (WTDocument) obj[0];
				OutputColumnData data = null;
				if (!StringUtils.isNull(kek_description) || !StringUtils.isNull(kekNumber)
						|| !StringUtils.isNull(keNumber) || !StringUtils.isNull(mak)) {
					Project project = (Project) obj[1];
					data = new OutputColumnData(document, project);
				} else {
					data = new OutputColumnData(document, null);
				}

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

	public Map<String, Object> findRequestDocument(Map<String, Object> param) throws Exception {

		Map<String, Object> map = new HashMap<String, Object>();
		List<RequestDocumentColumnData> list = new ArrayList<RequestDocumentColumnData>();
		QuerySpec query = null;

		String kekNumber = (String) param.get("kekNumber");
		String keNumber = (String) param.get("keNumber");
		String description = (String) param.get("description");
		String engType = (String) param.get("engType");
		String mak = (String) param.get("mak");
		String pdescription = (String) param.get("pdescription");
//		String name = (String) param.get("name");
		String creatorsOid = (String) param.get("creatorsOid");
		String modifierOid = (String) param.get("modifierOid");

		String ins_location = (String) param.get("ins_location");
		String customer = (String) param.get("customer");
		String userId = (String) param.get("userId");

		String predate = (String) param.get("predate");
		String postdate = (String) param.get("postdate");

		String predate_m = (String) param.get("predate_m");
		String postdate_m = (String) param.get("postdate_m");

		String latest = (String) param.get("latest");
		if (StringUtils.isNull(latest)) {
			latest = "true";
		}

		ReferenceFactory rf = new ReferenceFactory();
		// 정렬
		String sort = (String) param.get("sort");
		String sortKey = (String) param.get("sortKey");

		try {
			query = new QuerySpec();

			int idx = query.appendClassList(RequestDocument.class, true);
			int idx_link = query.appendClassList(ReqDocumentProjectLink.class, true);
			int idx_p = query.appendClassList(Project.class, true);

			SearchCondition sc = null;
			ClassAttribute ca = null;

			ClassAttribute roleAca = new ClassAttribute(RequestDocument.class, WTAttributeNameIfc.ID_NAME);
			ClassAttribute roleBca = new ClassAttribute(Project.class, WTAttributeNameIfc.ID_NAME);

			sc = new SearchCondition(new ClassAttribute(ReqDocumentProjectLink.class, "roleAObjectRef.key.id"), "=",
					roleAca);
			query.appendWhere(sc, new int[] { idx_link, idx });
			query.appendAnd();
			sc = new SearchCondition(new ClassAttribute(ReqDocumentProjectLink.class, "roleBObjectRef.key.id"), "=",
					roleBca);
			query.appendWhere(sc, new int[] { idx_link, idx_p });

			if (!StringUtils.isNull(kekNumber)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(Project.class, Project.KEK_NUMBER);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(kekNumber);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx_p });
			}

			if (!StringUtils.isNull(keNumber)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(Project.class, Project.KE_NUMBER);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(keNumber);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx_p });
			}

			if (!StringUtils.isNull(engType)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(Project.class, Project.P_TYPE);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(engType);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx_p });
			}

			if (!StringUtils.isNull(customer)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(Project.class, Project.CUSTOMER);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(customer);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx_p });
			}

			if (!StringUtils.isNull(customer)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(Project.class, Project.CUSTOMER);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(customer);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx_p });
			}

			if (!StringUtils.isNull(ins_location)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(Project.class, Project.INS_LOCATION);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(ins_location);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx_p });
			}

			if (!StringUtils.isNull(userId)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(Project.class, Project.USER_ID);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(userId);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx_p });
			}

			if (!StringUtils.isNull(predate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp start = DateUtils.convertStartDate(predate);
				sc = new SearchCondition(Project.class, WTAttributeNameIfc.CREATE_STAMP_NAME,
						SearchCondition.GREATER_THAN_OR_EQUAL, start);
				query.appendWhere(sc, new int[] { idx_p });
			}

			if (!StringUtils.isNull(postdate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp end = DateUtils.convertEndDate(postdate);
				sc = new SearchCondition(Project.class, WTAttributeNameIfc.CREATE_STAMP_NAME,
						SearchCondition.LESS_THAN_OR_EQUAL, end);
				query.appendWhere(sc, new int[] { idx_p });
			}

			// 수정일
			if (!StringUtils.isNull(predate_m)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp start = DateUtils.convertStartDate(predate_m);
				sc = new SearchCondition(Project.class, WTAttributeNameIfc.MODIFY_STAMP_NAME,
						SearchCondition.GREATER_THAN_OR_EQUAL, start);
				query.appendWhere(sc, new int[] { idx_p });
			}

			if (!StringUtils.isNull(postdate_m)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp end = DateUtils.convertEndDate(postdate_m);
				sc = new SearchCondition(Project.class, WTAttributeNameIfc.MODIFY_STAMP_NAME,
						SearchCondition.LESS_THAN_OR_EQUAL, end);
				query.appendWhere(sc, new int[] { idx_p });
			}

			if (!StringUtils.isNull(description)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(RequestDocument.class, RequestDocument.DESCRIPTION);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(description);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(mak)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(Project.class, Project.MAK);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(mak);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx_p });
			}

			if (!StringUtils.isNull(pdescription)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(Project.class, Project.DESCRIPTION);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(pdescription);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx_p });
			}

			// if (!StringUtils.isNull(engType)) {
			// if (query.getConditionCount() > 0)
			// query.appendAnd();
			//
			// ca = new ClassAttribute(Project.class, Project.);
			// query.appendWhere(sc, new int[] { idx });
			// }

			if (!StringUtils.isNull(creatorsOid)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				People user = (People) rf.getReference(creatorsOid).getObject();
				long ids = user.getUser().getPersistInfo().getObjectIdentifier().getId();
				sc = new SearchCondition(RequestDocument.class, "iterationInfo.creator.key.id", SearchCondition.EQUAL,
						ids);
				query.appendWhere(sc, new int[] { idx });
			}

			// 수정자
			if (!StringUtils.isNull(modifierOid)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				People user = (People) rf.getReference(modifierOid).getObject();
				long ids = user.getUser().getPersistInfo().getObjectIdentifier().getId();
				sc = new SearchCondition(RequestDocument.class, "iterationInfo.modifier.key.id", SearchCondition.EQUAL,
						ids);
				query.appendWhere(sc, new int[] { idx });
			}

			if ("true".equals(latest)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				sc = VersionControlHelper.getSearchCondition(RequestDocument.class, true);
				query.appendWhere(sc, new int[] { idx });

				CommonUtils.addLastVersionCondition(query, idx);
			}

			System.out.println("query=" + query);

			if (StringUtils.isNull(sort)) {
				sort = "true";
			}

			if (StringUtils.isNull(sortKey)) {
				sortKey = WTAttributeNameIfc.MODIFY_STAMP_NAME;
			}

			ca = new ClassAttribute(RequestDocument.class, sortKey);
			OrderBy orderBy = new OrderBy(ca, Boolean.parseBoolean(sort));
			query.appendOrderBy(orderBy, new int[] { idx });
			// SearchUtils.appendOrderBy(query, requestdocument.class, sortKey, idx,
			// Boolean.parseBoolean(sort));

			query.setAdvancedQueryEnabled(true);
			query.setDescendantQuery(false);
			// query.setDescendantsIncluded(false, master);

			PageQueryUtils pager = new PageQueryUtils(param, query);
			PagingQueryResult result = pager.find();
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				RequestDocument document = (RequestDocument) obj[0];
				Project project = (Project) obj[2];
				RequestDocumentColumnData data = new RequestDocumentColumnData(document, project);
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

	public ReqDocumentProjectLink getReqDocumentProjectLink(String oid) throws Exception {
		ReferenceFactory rf = new ReferenceFactory();
		RequestDocument reqDoc = (RequestDocument) rf.getReference(oid).getObject();
		return getReqDocumentProjectLink(reqDoc);
	}

	public ReqDocumentProjectLink getReqDocumentProjectLink(RequestDocument reqDoc) throws Exception {
		QuerySpec query = new QuerySpec();

		int idx = query.appendClassList(RequestDocument.class, true);
		int idx_link = query.appendClassList(ReqDocumentProjectLink.class, true);

		SearchCondition sc = null;

		ClassAttribute roleAca = new ClassAttribute(RequestDocument.class, WTAttributeNameIfc.ID_NAME);

		sc = new SearchCondition(new ClassAttribute(ReqDocumentProjectLink.class, "roleAObjectRef.key.id"), "=",
				roleAca);
		query.appendWhere(sc, new int[] { idx_link, idx });
		query.appendAnd();

		sc = new SearchCondition(ReqDocumentProjectLink.class, "roleAObjectRef.key.id", "=",
				reqDoc.getPersistInfo().getObjectIdentifier().getId());
		query.appendWhere(sc, new int[] { idx_link });

		QueryResult result = PersistenceHelper.manager.find(query);
		ReqDocumentProjectLink link = null;
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			link = (ReqDocumentProjectLink) obj[1];
		}
		return link;
	}

	public String getJsonList(RequestDocument reqDoc) throws Exception {
		String jsonList = "[";

		QueryResult result = PersistenceHelper.manager.navigate(reqDoc, "project", ReqDocumentProjectLink.class);

		while (result.hasMoreElements()) {
			Project project = (Project) result.nextElement();
			String oid = project.getPersistInfo().getObjectIdentifier().getStringValue();
			jsonList += "['" + oid + "', '" + StringUtils.replaceToValue(project.getPType()) + "', '"
					+ StringUtils.replaceToValue(project.getCustomer()) + "', '"
					+ StringUtils.replaceToValue(project.getIns_location()) + "','"
					+ StringUtils.replaceToValue(project.getMak()) + "', '"
					+ StringUtils.replaceToValue(project.getKekNumber()) + "', '"
					+ StringUtils.replaceToValue(project.getKeNumber()) + "', '"
					+ StringUtils.replaceToValue(project.getUserId()) + "','"
					+ StringUtils.replaceToValue(
							project.getCustomDate() != null ? project.getCustomDate().toString().substring(0, 10) : "")
					+ "', '" + StringUtils.replaceToValue(project.getDescription()) + "', '"
					+ StringUtils.replaceToValue(project.getModel()) + "', '" + StringUtils.replaceToValue(
							project.getPDate() != null ? project.getPDate().toString().substring(0, 10) : "")
					+ "'], ";

		}

		jsonList += "]";
		return jsonList;
	}

	public Map<String, Object> findOldSpec(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<DocumentColumnData> list = new ArrayList<DocumentColumnData>();
		QuerySpec query = null;

		// search param
		String number = (String) param.get("number");
		String name = (String) param.get("name");
		String statesDoc = (String) param.get("statesDoc");
		String creatorsOid = (String) param.get("creatorsOid");
		String modifierOid = (String) param.get("modifierOid");
		String description = (String) param.get("description");

		String predate = (String) param.get("predate");
		String postdate = (String) param.get("postdate");

		String predate_m = (String) param.get("predate_m");
		String postdate_m = (String) param.get("postdate_m");

		String sub_folder = (String) param.get("sub_folder");

		String latest = (String) param.get("latest");
		if (StringUtils.isNull(latest)) {
			latest = "true";
		}

		String location = (String) param.get("location");
		ReferenceFactory rf = new ReferenceFactory();
		// 정렬
		String sort = (String) param.get("sort");
		String sortKey = (String) param.get("sortKey");

		try {
			query = new QuerySpec();

			int idx = query.appendClassList(PRJDocument.class, true);
			int master = query.appendClassList(E3PSDocumentMaster.class, false);

			SearchCondition sc = null;
			ClassAttribute ca = null;

			sc = new SearchCondition(PRJDocument.class, "masterReference.key.id", E3PSDocumentMaster.class,
					"thePersistInfo.theObjectIdentifier.id");
			query.appendWhere(sc, new int[] { idx, master });

			if (!StringUtils.isNull(name)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(PRJDocument.class, PRJDocument.NAME);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(name);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			// 대소문자 구분
			if (!StringUtils.isNull(number)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				ca = new ClassAttribute(PRJDocument.class, PRJDocument.NUMBER);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(number);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(description)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(PRJDocument.class, PRJDocument.DESCRIPTION);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(description);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(creatorsOid)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				People user = (People) rf.getReference(creatorsOid).getObject();
				long ids = user.getUser().getPersistInfo().getObjectIdentifier().getId();
				sc = new SearchCondition(PRJDocument.class, "iterationInfo.creator.key.id", SearchCondition.EQUAL, ids);
				query.appendWhere(sc, new int[] { idx });
			}

			// 수정자
			if (!StringUtils.isNull(modifierOid)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				People user = (People) rf.getReference(modifierOid).getObject();
				long ids = user.getUser().getPersistInfo().getObjectIdentifier().getId();
				sc = new SearchCondition(PRJDocument.class, "iterationInfo.modifier.key.id", SearchCondition.EQUAL,
						ids);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(statesDoc)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				sc = new SearchCondition(PRJDocument.class, "state.state", SearchCondition.EQUAL, statesDoc);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(predate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp start = DateUtils.convertStartDate(predate);
				sc = new SearchCondition(PRJDocument.class, WTAttributeNameIfc.CREATE_STAMP_NAME,
						SearchCondition.GREATER_THAN_OR_EQUAL, start);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(postdate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp end = DateUtils.convertEndDate(postdate);
				sc = new SearchCondition(PRJDocument.class, WTAttributeNameIfc.CREATE_STAMP_NAME,
						SearchCondition.LESS_THAN_OR_EQUAL, end);
				query.appendWhere(sc, new int[] { idx });
			}

			// 수정일
			if (!StringUtils.isNull(predate_m)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp start = DateUtils.convertStartDate(predate_m);
				sc = new SearchCondition(PRJDocument.class, WTAttributeNameIfc.MODIFY_STAMP_NAME,
						SearchCondition.GREATER_THAN_OR_EQUAL, start);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(postdate_m)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp end = DateUtils.convertEndDate(postdate_m);
				sc = new SearchCondition(PRJDocument.class, WTAttributeNameIfc.MODIFY_STAMP_NAME,
						SearchCondition.LESS_THAN_OR_EQUAL, end);
				query.appendWhere(sc, new int[] { idx });
			}

			Folder folder = null;
			if (StringUtils.isNull(location)) {
				location = OLDSPEC;
			}

//			/Default/문서/프로젝트/제작사양서

			folder = FolderTaskLogic.getFolder(location, CommonUtils.getContainer());

			if (!StringUtils.isNull(folder)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				int f_idx = query.appendClassList(IteratedFolderMemberLink.class, false);
				ClassAttribute fca = new ClassAttribute(IteratedFolderMemberLink.class, "roleBObjectRef.key.branchId");
				SearchCondition fsc = new SearchCondition(fca, "=",
						new ClassAttribute(PRJDocument.class, "iterationInfo.branchId"));
				fsc.setFromIndicies(new int[] { f_idx, idx }, 0);
				fsc.setOuterJoin(0);
				query.appendWhere(fsc, new int[] { f_idx, idx });
				query.appendAnd();

				query.appendOpenParen();
				long fid = folder.getPersistInfo().getObjectIdentifier().getId();
				query.appendWhere(
						new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", "=", fid),
						new int[] { f_idx });

				if (!StringUtils.isNull(sub_folder)) {
					ArrayList<Folder> folders = FolderUtils.getSubFolders(folder, new ArrayList<Folder>());
					for (int i = 0; i < folders.size(); i++) {
						Folder sub = (Folder) folders.get(i);
						query.appendOr();
						long sfid = sub.getPersistInfo().getObjectIdentifier().getId();
						query.appendWhere(
								new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", "=", sfid),
								new int[] { f_idx });
					}
				}
				query.appendCloseParen();
			}
//			folder = FolderTaskLogic.getFolder(location, CommonUtils.getContainer());
//			if (query.getConditionCount() > 0)
//				query.appendAnd();
//			int f_idx = query.appendClassList(IteratedFolderMemberLink.class, false);
//			ClassAttribute fca = new ClassAttribute(IteratedFolderMemberLink.class, "roleBObjectRef.key.branchId");
//			SearchCondition fsc = new SearchCondition(fca, "=",
//					new ClassAttribute(PRJDocument.class, "iterationInfo.branchId"));
//			fsc.setFromIndicies(new int[] { f_idx, idx }, 0);
//			fsc.setOuterJoin(0);
//			query.appendWhere(fsc, new int[] { f_idx, idx });
//			query.appendAnd();
//
//			query.appendOpenParen();
//			long fid = folder.getPersistInfo().getObjectIdentifier().getId();
//			query.appendWhere(new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", "=", fid),
//					new int[] { f_idx });
//			query.appendCloseParen();

			if ("true".equals(latest)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				sc = VersionControlHelper.getSearchCondition(PRJDocument.class, true);
				query.appendWhere(sc, new int[] { idx });

				CommonUtils.latestQuery(query, PRJDocument.class, idx);
			}

			if (StringUtils.isNull(sort)) {
				sort = "true";
			}

			if (StringUtils.isNull(sortKey)) {
				sortKey = WTAttributeNameIfc.MODIFY_STAMP_NAME;
			}

			ca = new ClassAttribute(PRJDocument.class, sortKey);
			OrderBy orderBy = new OrderBy(ca, Boolean.parseBoolean(sort));
			query.appendOrderBy(orderBy, new int[] { idx });

			query.setAdvancedQueryEnabled(true);
			query.setDescendantQuery(false);

			PageQueryUtils pager = new PageQueryUtils(param, query);
			PagingQueryResult result = pager.find();
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				WTDocument document = (WTDocument) obj[0];
				DocumentColumnData data = new DocumentColumnData(document);
				list.add(data);
			}

			map.put("list", list);
			map.put("lastPage", pager.getLastPage());
			map.put("topListCount", pager.getTotal());
			map.put("sessionid", pager.getSessionId());
			map.put("curPage", pager.getCpage());
			map.put("total", pager.getTotalSize());
			map.put("result", "SUCCESS");
		} catch (

		Exception e) {
			map.put("result", "FAIL");
			e.printStackTrace();
		}
		return map;
	}

	public Map<String, Object> findSpec(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<DocumentColumnData> list = new ArrayList<DocumentColumnData>();
		QuerySpec query = null;

		// search param
		String number = (String) param.get("number");
		String name = (String) param.get("name");
		String statesDoc = (String) param.get("statesDoc");
		String creatorsOid = (String) param.get("creatorsOid");
		String modifierOid = (String) param.get("modifierOid");
		String description = (String) param.get("description");

		String predate = (String) param.get("predate");
		String postdate = (String) param.get("postdate");

		String predate_m = (String) param.get("predate_m");
		String postdate_m = (String) param.get("postdate_m");

//		String sub_folder = (String) param.get("sub_folder");

		String latest = (String) param.get("latest");
		if (StringUtils.isNull(latest)) {
			latest = "true";
		}

		String location = (String) param.get("location");
		ReferenceFactory rf = new ReferenceFactory();
		// 정렬
		String sort = (String) param.get("sort");
		String sortKey = (String) param.get("sortKey");

		try {
			query = new QuerySpec();

			int idx = query.appendClassList(WTDocument.class, true);
			int master = query.appendClassList(WTDocumentMaster.class, false);

			SearchCondition sc = null;
			ClassAttribute ca = null;

			sc = WorkInProgressHelper.getSearchCondition_CI(WTDocument.class);
			query.appendWhere(sc, new int[] { idx });
			query.appendAnd();

			sc = new SearchCondition(WTDocument.class, "masterReference.key.id", WTDocumentMaster.class,
					"thePersistInfo.theObjectIdentifier.id");
			query.appendWhere(sc, new int[] { idx, master });

			if (!StringUtils.isNull(name)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(WTDocument.class, WTDocument.NAME);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(name);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			// 대소문자 구분
			if (!StringUtils.isNull(number)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				ca = new ClassAttribute(WTDocument.class, WTDocument.NUMBER);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(number);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(description)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(WTDocument.class, WTDocument.DESCRIPTION);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(description);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(creatorsOid)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				People user = (People) rf.getReference(creatorsOid).getObject();
				long ids = user.getUser().getPersistInfo().getObjectIdentifier().getId();
				sc = new SearchCondition(WTDocument.class, "iterationInfo.creator.key.id", SearchCondition.EQUAL, ids);
				query.appendWhere(sc, new int[] { idx });
			}

			// 수정자
			if (!StringUtils.isNull(modifierOid)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				People user = (People) rf.getReference(modifierOid).getObject();
				long ids = user.getUser().getPersistInfo().getObjectIdentifier().getId();
				sc = new SearchCondition(WTDocument.class, "iterationInfo.modifier.key.id", SearchCondition.EQUAL, ids);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(statesDoc)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				sc = new SearchCondition(WTDocument.class, "state.state", SearchCondition.EQUAL, statesDoc);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(predate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp start = DateUtils.convertStartDate(predate);
				sc = new SearchCondition(WTDocument.class, WTAttributeNameIfc.CREATE_STAMP_NAME,
						SearchCondition.GREATER_THAN_OR_EQUAL, start);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(postdate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp end = DateUtils.convertEndDate(postdate);
				sc = new SearchCondition(WTDocument.class, WTAttributeNameIfc.CREATE_STAMP_NAME,
						SearchCondition.LESS_THAN_OR_EQUAL, end);
				query.appendWhere(sc, new int[] { idx });
			}

			// 수정일
			if (!StringUtils.isNull(predate_m)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp start = DateUtils.convertStartDate(predate_m);
				sc = new SearchCondition(WTDocument.class, WTAttributeNameIfc.MODIFY_STAMP_NAME,
						SearchCondition.GREATER_THAN_OR_EQUAL, start);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(postdate_m)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp end = DateUtils.convertEndDate(postdate_m);
				sc = new SearchCondition(WTDocument.class, WTAttributeNameIfc.MODIFY_STAMP_NAME,
						SearchCondition.LESS_THAN_OR_EQUAL, end);
				query.appendWhere(sc, new int[] { idx });
			}

			Folder folder1 = null;
			Folder folder2 = null;
			if (StringUtils.isNull(location)) {
				location = ROOT;
			}

			String loc1 = "/Default/프로젝트/기계_제작사양서";
			String loc2 = "/Default/프로젝트/전기_제작사양서";
//			/Default/문서/프로젝트/제작사양서
			folder1 = FolderTaskLogic.getFolder(loc1, CommonUtils.getContainer());
			folder2 = FolderTaskLogic.getFolder(loc2, CommonUtils.getContainer());
			if (query.getConditionCount() > 0)
				query.appendAnd();
			int f_idx = query.appendClassList(IteratedFolderMemberLink.class, false);
			ClassAttribute fca = new ClassAttribute(IteratedFolderMemberLink.class, "roleBObjectRef.key.branchId");
			SearchCondition fsc = new SearchCondition(fca, "=",
					new ClassAttribute(WTDocument.class, "iterationInfo.branchId"));
			fsc.setFromIndicies(new int[] { f_idx, idx }, 0);
			fsc.setOuterJoin(0);
			query.appendWhere(fsc, new int[] { f_idx, idx });
			query.appendAnd();

			query.appendOpenParen();
			long fid = folder1.getPersistInfo().getObjectIdentifier().getId();
			query.appendWhere(new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", "=", fid),
					new int[] { f_idx });
			query.appendOr();

			long sfid = folder2.getPersistInfo().getObjectIdentifier().getId();
			query.appendWhere(new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", "=", sfid),
					new int[] { f_idx });
			query.appendCloseParen();

			if ("true".equals(latest)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				sc = VersionControlHelper.getSearchCondition(WTDocument.class, true);
				query.appendWhere(sc, new int[] { idx });

				CommonUtils.latestQuery(query, WTDocument.class, idx);

			}

			if (StringUtils.isNull(sort)) {
				sort = "true";
			}

			if (StringUtils.isNull(sortKey)) {
				sortKey = WTAttributeNameIfc.MODIFY_STAMP_NAME;
			}

			ca = new ClassAttribute(WTDocument.class, sortKey);
			OrderBy orderBy = new OrderBy(ca, Boolean.parseBoolean(sort));
			query.appendOrderBy(orderBy, new int[] { idx });

			query.setAdvancedQueryEnabled(true);
			query.setDescendantQuery(false);

			PageQueryUtils pager = new PageQueryUtils(param, query);
			PagingQueryResult result = pager.find();
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				WTDocument document = (WTDocument) obj[0];
				DocumentColumnData data = new DocumentColumnData(document);
				list.add(data);
			}

			map.put("list", list);
			map.put("lastPage", pager.getLastPage());
			map.put("topListCount", pager.getTotal());
			map.put("sessionid", pager.getSessionId());
			map.put("curPage", pager.getCpage());
			map.put("total", pager.getTotalSize());
			map.put("result", "SUCCESS");
		} catch (

		Exception e) {
			map.put("result", "FAIL");
			e.printStackTrace();
		}
		return map;
	}

	public Map<String, Object> setNumber(Map<String, Object> param) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String loc = (String) param.get("loc");

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

		try {
			Calendar ca = Calendar.getInstance();
			int month = ca.get(Calendar.MONTH) + 1;
			int year = ca.get(Calendar.YEAR);
			DecimalFormat df = new DecimalFormat("00");
			String number = preFix + "-" + df.format(year).substring(2) + df.format(month) + "-";

			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(WTDocument.class, true);

			SearchCondition sc = new SearchCondition(WTDocument.class, WTDocument.NUMBER, "LIKE",
					number.toUpperCase() + "%");
			query.appendWhere(sc, new int[] { idx });

			ClassAttribute attr = new ClassAttribute(WTDocument.class, WTDocument.NUMBER);
			OrderBy orderBy = new OrderBy(attr, true);
			query.appendOrderBy(orderBy, new int[] { idx });

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

			map.put("number", number);
			map.put("result", SUCCESS);

		} catch (Exception e) {
			e.printStackTrace();
			map.put("result", FAIL);
		}
		return map;
	}

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<DocumentDTO> list = new ArrayList<>();

		// 검색 변수
		boolean latest = (boolean) params.get("latest");
		// 폴더 OID
		String oid = (String) params.get("oid");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTDocument.class, true);
		int idx_master = query.appendClassList(WTDocumentMaster.class, false);

		QuerySpecUtils.toInnerJoin(query, WTDocument.class, WTDocumentMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_master);

		if(!StringUtils.isNull(oid)) {
			
		}
		
		
		// 최신 이터레이션.
		if (latest) {
			QuerySpecUtils.toIteration(query, idx, WTDocument.class);
			QuerySpecUtils.toLatest(query, idx, WTDocument.class);
		}

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
}