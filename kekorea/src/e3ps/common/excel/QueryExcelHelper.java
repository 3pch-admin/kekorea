package e3ps.common.excel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.approval.column.AgreeColumnData;
import e3ps.approval.column.ApprovalColumnData;
import e3ps.approval.column.CompleteColumnData;
import e3ps.approval.column.IngColumnData;
import e3ps.approval.column.NoticeColumnData;
import e3ps.approval.column.ReceiveColumnData;
import e3ps.approval.column.ReturnColumnData;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.FolderUtils;
import e3ps.common.util.StringUtils;
import e3ps.doc.PRJDocument;
import e3ps.doc.column.DocumentColumnData;
import e3ps.doc.service.DocumentHelper;
import e3ps.epm.column.EpmLibraryColumnData;
import e3ps.epm.column.EpmProductColumnData;
import e3ps.epm.service.EpmHelper;
import e3ps.org.People;
import e3ps.org.column.UserColumnData;
import e3ps.part.column.PartLibraryColumnData;
import e3ps.part.column.PartProductColumnData;
import e3ps.part.service.PartHelper;
import e3ps.workspace.ApprovalLine;
import e3ps.workspace.ApprovalMaster;
import e3ps.workspace.Notice;
import wt.clients.folder.FolderTaskLogic;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.folder.IteratedFolderMemberLink;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTAttributeNameIfc;
import wt.vc.VersionControlHelper;
import wt.vc.wip.WorkInProgressHelper;

public class QueryExcelHelper {

	public static final QueryExcelHelper manager = new QueryExcelHelper();

	public Map<String, Object> getSelectDocument(Map<String, Object> param) {
		ReferenceFactory rf = new ReferenceFactory();
		List<String> dataList = (List<String>) param.get("list");
		PRJDocument doc = null;
		Map<String, Object> map = new HashMap<String, Object>();
		List<DocumentColumnData> list = new ArrayList<DocumentColumnData>();
		try {
			for (String oid : dataList) {
				doc = (PRJDocument) rf.getReference(oid).getObject();
				DocumentColumnData data = new DocumentColumnData(doc);
				list.add(data);
			}
			map.put("list", list);
			map.put("result", "SUCCESS");
		} catch (Exception e) {
			map.put("result", "FAIL");
			e.printStackTrace();
		}
		return map;
	}

	public Map<String, Object> getAllDocumentList() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		List<DocumentColumnData> list = new ArrayList<DocumentColumnData>();

		try {
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

			Folder folder = FolderTaskLogic.getFolder(DocumentHelper.ROOT, CommonUtils.getContainer());

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

			if (query.getConditionCount() > 0)
				query.appendAnd();
			sc = VersionControlHelper.getSearchCondition(WTDocument.class, true);
			query.appendWhere(sc, new int[] { idx });

			CommonUtils.addLastVersionCondition(query, idx);

			String sortKey = WTAttributeNameIfc.MODIFY_STAMP_NAME;
			String sort = "true";

			ca = new ClassAttribute(WTDocument.class, sortKey);
			OrderBy orderBy = new OrderBy(ca, Boolean.parseBoolean(sort));
			query.appendOrderBy(orderBy, new int[] { idx });

			query.setAdvancedQueryEnabled(true);
			query.setDescendantQuery(false);

			QueryResult result = PersistenceHelper.manager.find(query);

			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				PRJDocument document = (PRJDocument) obj[0];
				DocumentColumnData data = new DocumentColumnData(document);
				list.add(data);
			}
			map.put("list", list);
			map.put("result", "SUCCESS");
		} catch (Exception e) {
			map.put("result", "FAIL");
			e.printStackTrace();
		}
		return map;
	}

	public Map<String, Object> getAllLibraryWTPart() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		List<PartLibraryColumnData> list = new ArrayList<PartLibraryColumnData>();

		try {
			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(WTPart.class, true);
			int master = query.appendClassList(WTPartMaster.class, false);

			SearchCondition sc = null;
			ClassAttribute ca = null;

			sc = WorkInProgressHelper.getSearchCondition_CI(WTPart.class);
			query.appendWhere(sc, new int[] { idx });
			query.appendAnd();

			sc = new SearchCondition(WTPart.class, "masterReference.key.id", WTPartMaster.class,
					"thePersistInfo.theObjectIdentifier.id");
			query.appendWhere(sc, new int[] { idx, master });

			Folder folder = FolderTaskLogic.getFolder(PartHelper.LIBRARY_ROOT, CommonUtils.getLibrary());

			if (!StringUtils.isNull(folder)) {

				if (query.getConditionCount() > 0)
					query.appendAnd();
				int f_idx = query.appendClassList(IteratedFolderMemberLink.class, false);
				ClassAttribute fca = new ClassAttribute(IteratedFolderMemberLink.class, "roleBObjectRef.key.branchId");
				SearchCondition fsc = new SearchCondition(fca, "=",
						new ClassAttribute(WTPart.class, "iterationInfo.branchId"));
				fsc.setFromIndicies(new int[] { f_idx, idx }, 0);
				fsc.setOuterJoin(0);
				query.appendWhere(fsc, new int[] { f_idx, idx });
				query.appendAnd();

				query.appendOpenParen();
				long fid = folder.getPersistInfo().getObjectIdentifier().getId();
				query.appendWhere(
						new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", "=", fid),
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

			if (query.getConditionCount() > 0)
				query.appendAnd();
			sc = VersionControlHelper.getSearchCondition(WTPart.class, true);
			query.appendWhere(sc, new int[] { idx });

			CommonUtils.addLastVersionCondition(query, idx);

			String sortKey = WTAttributeNameIfc.MODIFY_STAMP_NAME;
			String sort = "true";

			ca = new ClassAttribute(WTPart.class, sortKey);
			OrderBy orderBy = new OrderBy(ca, Boolean.parseBoolean(sort));
			query.appendOrderBy(orderBy, new int[] { idx });

			query.setAdvancedQueryEnabled(true);
			query.setDescendantQuery(false);

			QueryResult result = PersistenceHelper.manager.find(query);

			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				WTPart part = (WTPart) obj[0];
				PartLibraryColumnData data = new PartLibraryColumnData(part);
				list.add(data);
			}
			map.put("list", list);
			map.put("result", "SUCCESS");
		} catch (Exception e) {
			map.put("result", "FAIL");
			e.printStackTrace();
		}
		return map;
	}

	public Map<String, Object> getAllProductWTPart() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		List<PartProductColumnData> list = new ArrayList<PartProductColumnData>();

		try {
			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(WTPart.class, true);
			int master = query.appendClassList(WTPartMaster.class, false);

			SearchCondition sc = null;
			ClassAttribute ca = null;

			sc = WorkInProgressHelper.getSearchCondition_CI(WTPart.class);
			query.appendWhere(sc, new int[] { idx });
			query.appendAnd();

			sc = new SearchCondition(WTPart.class, "masterReference.key.id", WTPartMaster.class,
					"thePersistInfo.theObjectIdentifier.id");
			query.appendWhere(sc, new int[] { idx, master });

			Folder folder = FolderTaskLogic.getFolder(PartHelper.PRODUCT_ROOT, CommonUtils.getContainer());

			if (!StringUtils.isNull(folder)) {

				if (query.getConditionCount() > 0)
					query.appendAnd();
				int f_idx = query.appendClassList(IteratedFolderMemberLink.class, false);
				ClassAttribute fca = new ClassAttribute(IteratedFolderMemberLink.class, "roleBObjectRef.key.branchId");
				SearchCondition fsc = new SearchCondition(fca, "=",
						new ClassAttribute(WTPart.class, "iterationInfo.branchId"));
				fsc.setFromIndicies(new int[] { f_idx, idx }, 0);
				fsc.setOuterJoin(0);
				query.appendWhere(fsc, new int[] { f_idx, idx });
				query.appendAnd();

				query.appendOpenParen();
				long fid = folder.getPersistInfo().getObjectIdentifier().getId();
				query.appendWhere(
						new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", "=", fid),
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

			if (query.getConditionCount() > 0)
				query.appendAnd();
			sc = VersionControlHelper.getSearchCondition(WTPart.class, true);
			query.appendWhere(sc, new int[] { idx });

			CommonUtils.addLastVersionCondition(query, idx);

			String sortKey = WTAttributeNameIfc.MODIFY_STAMP_NAME;
			String sort = "true";

			ca = new ClassAttribute(WTPart.class, sortKey);
			OrderBy orderBy = new OrderBy(ca, Boolean.parseBoolean(sort));
			query.appendOrderBy(orderBy, new int[] { idx });

			query.setAdvancedQueryEnabled(true);
			query.setDescendantQuery(false);

			QueryResult result = PersistenceHelper.manager.find(query);

			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				WTPart part = (WTPart) obj[0];
				PartProductColumnData data = new PartProductColumnData(part);
				list.add(data);
			}
			map.put("list", list);
			map.put("result", "SUCCESS");
		} catch (Exception e) {
			map.put("result", "FAIL");
			e.printStackTrace();
		}
		return map;
	}

	public Map<String, Object> getAllLibraryEPMDocument() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		List<EpmLibraryColumnData> list = new ArrayList<EpmLibraryColumnData>();

		try {
			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(EPMDocument.class, true);
			int master = query.appendClassList(EPMDocumentMaster.class, false);

			SearchCondition sc = null;
			ClassAttribute ca = null;

			sc = WorkInProgressHelper.getSearchCondition_CI(EPMDocument.class);
			query.appendWhere(sc, new int[] { idx });
			query.appendAnd();

			sc = new SearchCondition(EPMDocument.class, "masterReference.key.id", EPMDocumentMaster.class,
					"thePersistInfo.theObjectIdentifier.id");
			query.appendWhere(sc, new int[] { idx, master });

			Folder folder = FolderTaskLogic.getFolder(EpmHelper.LIBRARY_ROOT, CommonUtils.getLibrary());

			if (!StringUtils.isNull(folder)) {

				if (query.getConditionCount() > 0)
					query.appendAnd();
				int f_idx = query.appendClassList(IteratedFolderMemberLink.class, false);
				ClassAttribute fca = new ClassAttribute(IteratedFolderMemberLink.class, "roleBObjectRef.key.branchId");
				SearchCondition fsc = new SearchCondition(fca, "=",
						new ClassAttribute(EPMDocument.class, "iterationInfo.branchId"));
				fsc.setFromIndicies(new int[] { f_idx, idx }, 0);
				fsc.setOuterJoin(0);
				query.appendWhere(fsc, new int[] { f_idx, idx });
				query.appendAnd();

				query.appendOpenParen();
				long fid = folder.getPersistInfo().getObjectIdentifier().getId();
				query.appendWhere(
						new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", "=", fid),
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

			if (query.getConditionCount() > 0)
				query.appendAnd();
			sc = VersionControlHelper.getSearchCondition(EPMDocument.class, true);
			query.appendWhere(sc, new int[] { idx });

			CommonUtils.addLastVersionCondition(query, idx);

			String sortKey = WTAttributeNameIfc.MODIFY_STAMP_NAME;
			String sort = "true";

			ca = new ClassAttribute(EPMDocument.class, sortKey);
			OrderBy orderBy = new OrderBy(ca, Boolean.parseBoolean(sort));
			query.appendOrderBy(orderBy, new int[] { idx });

			query.setAdvancedQueryEnabled(true);
			query.setDescendantQuery(false);

			QueryResult result = PersistenceHelper.manager.find(query);

			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				EPMDocument epm = (EPMDocument) obj[0];
				EpmLibraryColumnData data = new EpmLibraryColumnData(epm);
				list.add(data);
			}
			map.put("list", list);
			map.put("result", "SUCCESS");
		} catch (Exception e) {
			map.put("result", "FAIL");
			e.printStackTrace();
		}
		return map;
	}

	public Map<String, Object> getAllProductEPMDocument() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		List<EpmProductColumnData> list = new ArrayList<EpmProductColumnData>();

		try {
			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(EPMDocument.class, true);
			int master = query.appendClassList(EPMDocumentMaster.class, false);

			SearchCondition sc = null;
			ClassAttribute ca = null;

			sc = WorkInProgressHelper.getSearchCondition_CI(EPMDocument.class);
			query.appendWhere(sc, new int[] { idx });
			query.appendAnd();

			sc = new SearchCondition(EPMDocument.class, "masterReference.key.id", EPMDocumentMaster.class,
					"thePersistInfo.theObjectIdentifier.id");
			query.appendWhere(sc, new int[] { idx, master });

			Folder folder = FolderTaskLogic.getFolder(EpmHelper.PRODUCT_ROOT, CommonUtils.getContainer());

			if (!StringUtils.isNull(folder)) {

				if (query.getConditionCount() > 0)
					query.appendAnd();
				int f_idx = query.appendClassList(IteratedFolderMemberLink.class, false);
				ClassAttribute fca = new ClassAttribute(IteratedFolderMemberLink.class, "roleBObjectRef.key.branchId");
				SearchCondition fsc = new SearchCondition(fca, "=",
						new ClassAttribute(EPMDocument.class, "iterationInfo.branchId"));
				fsc.setFromIndicies(new int[] { f_idx, idx }, 0);
				fsc.setOuterJoin(0);
				query.appendWhere(fsc, new int[] { f_idx, idx });
				query.appendAnd();

				query.appendOpenParen();
				long fid = folder.getPersistInfo().getObjectIdentifier().getId();
				query.appendWhere(
						new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", "=", fid),
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

			if (query.getConditionCount() > 0)
				query.appendAnd();
			sc = VersionControlHelper.getSearchCondition(EPMDocument.class, true);
			query.appendWhere(sc, new int[] { idx });

			CommonUtils.addLastVersionCondition(query, idx);

			String sortKey = WTAttributeNameIfc.MODIFY_STAMP_NAME;
			String sort = "true";

			ca = new ClassAttribute(EPMDocument.class, sortKey);
			OrderBy orderBy = new OrderBy(ca, Boolean.parseBoolean(sort));
			query.appendOrderBy(orderBy, new int[] { idx });

			query.setAdvancedQueryEnabled(true);
			query.setDescendantQuery(false);

			QueryResult result = PersistenceHelper.manager.find(query);

			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				EPMDocument epm = (EPMDocument) obj[0];
				EpmProductColumnData data = new EpmProductColumnData(epm);
				list.add(data);
			}
			map.put("list", list);
			map.put("result", "SUCCESS");
		} catch (Exception e) {
			map.put("result", "FAIL");
			e.printStackTrace();
		}
		return map;
	}

	public Map<String, Object> getSelectLibraryPart(Map<String, Object> param) {
		ReferenceFactory rf = new ReferenceFactory();
		List<String> dataList = (List<String>) param.get("list");
		WTPart part = null;
		Map<String, Object> map = new HashMap<String, Object>();
		List<PartLibraryColumnData> list = new ArrayList<PartLibraryColumnData>();
		try {
			for (String oid : dataList) {
				part = (WTPart) rf.getReference(oid).getObject();
				PartLibraryColumnData data = new PartLibraryColumnData(part);
				list.add(data);
			}
			map.put("list", list);
			map.put("result", "SUCCESS");
		} catch (Exception e) {
			map.put("result", "FAIL");
			e.printStackTrace();
		}
		return map;
	}

	public Map<String, Object> getSelectProductPart(Map<String, Object> param) {
		ReferenceFactory rf = new ReferenceFactory();
		List<String> dataList = (List<String>) param.get("list");
		WTPart part = null;
		Map<String, Object> map = new HashMap<String, Object>();
		List<PartProductColumnData> list = new ArrayList<PartProductColumnData>();
		try {
			for (String oid : dataList) {
				part = (WTPart) rf.getReference(oid).getObject();
				PartProductColumnData data = new PartProductColumnData(part);
				list.add(data);
			}
			map.put("list", list);
			map.put("result", "SUCCESS");
		} catch (Exception e) {
			map.put("result", "FAIL");
			e.printStackTrace();
		}
		return map;
	}

	public Map<String, Object> getSelectLibraryEpm(Map<String, Object> param) {
		ReferenceFactory rf = new ReferenceFactory();
		List<String> dataList = (List<String>) param.get("list");
		EPMDocument epm = null;
		Map<String, Object> map = new HashMap<String, Object>();
		List<EpmLibraryColumnData> list = new ArrayList<EpmLibraryColumnData>();
		try {
			for (String oid : dataList) {
				epm = (EPMDocument) rf.getReference(oid).getObject();
				EpmLibraryColumnData data = new EpmLibraryColumnData(epm);
				list.add(data);
			}
			map.put("list", list);
			map.put("result", "SUCCESS");
		} catch (Exception e) {
			map.put("result", "FAIL");
			e.printStackTrace();
		}
		return map;
	}

	public Map<String, Object> getSelectApprovalList(Map<String, Object> param) {
		ReferenceFactory rf = new ReferenceFactory();
		List<String> dataList = (List<String>) param.get("list");
		ApprovalLine line = null;
		Map<String, Object> map = new HashMap<String, Object>();
		List<ApprovalColumnData> list = new ArrayList<ApprovalColumnData>();
		try {
			for (String oid : dataList) {
				line = (ApprovalLine) rf.getReference(oid).getObject();
				ApprovalColumnData data = new ApprovalColumnData(line);
				list.add(data);
			}
			map.put("list", list);
			map.put("result", "SUCCESS");
		} catch (Exception e) {
			map.put("result", "FAIL");
			e.printStackTrace();
		}
		return map;
	}

	public Map<String, Object> getSelectCompleteList(Map<String, Object> param) {
		ReferenceFactory rf = new ReferenceFactory();
		List<String> dataList = (List<String>) param.get("list");
		ApprovalLine line = null;
		Map<String, Object> map = new HashMap<String, Object>();
		List<CompleteColumnData> list = new ArrayList<CompleteColumnData>();
		try {
			for (String oid : dataList) {
				line = (ApprovalLine) rf.getReference(oid).getObject();
				CompleteColumnData data = new CompleteColumnData(line);
				list.add(data);
			}
			map.put("list", list);
			map.put("result", "SUCCESS");
		} catch (Exception e) {
			map.put("result", "FAIL");
			e.printStackTrace();
		}
		return map;
	}

	public Map<String, Object> getSelectAgreeList(Map<String, Object> param) {
		ReferenceFactory rf = new ReferenceFactory();
		List<String> dataList = (List<String>) param.get("list");
		ApprovalLine line = null;
		Map<String, Object> map = new HashMap<String, Object>();
		List<AgreeColumnData> list = new ArrayList<AgreeColumnData>();
		try {
			for (String oid : dataList) {
				line = (ApprovalLine) rf.getReference(oid).getObject();
				AgreeColumnData data = new AgreeColumnData(line);
				list.add(data);
			}
			map.put("list", list);
			map.put("result", "SUCCESS");
		} catch (Exception e) {
			map.put("result", "FAIL");
			e.printStackTrace();
		}
		return map;
	}

	public Map<String, Object> getSelectReceiveList(Map<String, Object> param) {
		ReferenceFactory rf = new ReferenceFactory();
		List<String> dataList = (List<String>) param.get("list");
		ApprovalLine line = null;
		Map<String, Object> map = new HashMap<String, Object>();
		List<ReceiveColumnData> list = new ArrayList<ReceiveColumnData>();
		try {
			for (String oid : dataList) {
				line = (ApprovalLine) rf.getReference(oid).getObject();
				ReceiveColumnData data = new ReceiveColumnData(line);
				list.add(data);
			}
			map.put("list", list);
			map.put("result", "SUCCESS");
		} catch (Exception e) {
			map.put("result", "FAIL");
			e.printStackTrace();
		}
		return map;
	}

	public Map<String, Object> getSelectReturnList(Map<String, Object> param) {
		ReferenceFactory rf = new ReferenceFactory();
		List<String> dataList = (List<String>) param.get("list");
		ApprovalMaster master = null;
		Map<String, Object> map = new HashMap<String, Object>();
		List<ReturnColumnData> list = new ArrayList<ReturnColumnData>();
		try {
			for (String oid : dataList) {
				master = (ApprovalMaster) rf.getReference(oid).getObject();
				ReturnColumnData data = new ReturnColumnData(master);
				list.add(data);
			}
			map.put("list", list);
			map.put("result", "SUCCESS");
		} catch (Exception e) {
			map.put("result", "FAIL");
			e.printStackTrace();
		}
		return map;
	}

	public Map<String, Object> getSelectIngList(Map<String, Object> param) {
		ReferenceFactory rf = new ReferenceFactory();
		List<String> dataList = (List<String>) param.get("list");
		ApprovalMaster master = null;
		Map<String, Object> map = new HashMap<String, Object>();
		List<IngColumnData> list = new ArrayList<IngColumnData>();
		try {
			for (String oid : dataList) {
				master = (ApprovalMaster) rf.getReference(oid).getObject();
				IngColumnData data = new IngColumnData(master);
				list.add(data);
			}
			map.put("list", list);
			map.put("result", "SUCCESS");
		} catch (Exception e) {
			map.put("result", "FAIL");
			e.printStackTrace();
		}
		return map;
	}

	public Map<String, Object> getSelectProductEpm(Map<String, Object> param) {
		ReferenceFactory rf = new ReferenceFactory();
		List<String> dataList = (List<String>) param.get("list");
		EPMDocument epm = null;
		Map<String, Object> map = new HashMap<String, Object>();
		List<EpmProductColumnData> list = new ArrayList<EpmProductColumnData>();
		try {
			for (String oid : dataList) {
				epm = (EPMDocument) rf.getReference(oid).getObject();
				EpmProductColumnData data = new EpmProductColumnData(epm);
				list.add(data);
			}
			map.put("list", list);
			map.put("result", "SUCCESS");
		} catch (Exception e) {
			map.put("result", "FAIL");
			e.printStackTrace();
		}
		return map;
	}

	public Map<String, Object> getSelectNotice(Map<String, Object> param) {
		ReferenceFactory rf = new ReferenceFactory();
		List<String> dataList = (List<String>) param.get("list");
		Notice notice = null;
		Map<String, Object> map = new HashMap<String, Object>();
		List<NoticeDTO> list = new ArrayList<NoticeDTO>();
		try {
			for (String oid : dataList) {
				notice = (Notice) rf.getReference(oid).getObject();
				NoticeDTO data = new NoticeDTO(notice);
				list.add(data);
			}
			map.put("list", list);
			map.put("result", "SUCCESS");
		} catch (Exception e) {
			map.put("result", "FAIL");
			e.printStackTrace();
		}
		return map;
	}

	public Map<String, Object> getSelectUserList(Map<String, Object> param) {
		ReferenceFactory rf = new ReferenceFactory();
		List<String> dataList = (List<String>) param.get("list");
		People user = null;
		Map<String, Object> map = new HashMap<String, Object>();
		List<UserColumnData> list = new ArrayList<UserColumnData>();
		try {
			for (String oid : dataList) {
				user = (People) rf.getReference(oid).getObject();
				UserColumnData data = new UserColumnData(user);
				list.add(data);
			}
			map.put("list", list);
			map.put("result", "SUCCESS");
		} catch (Exception e) {
			map.put("result", "FAIL");
			e.printStackTrace();
		}
		return map;
	}

	public Map<String, Object> getAllNotice() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		List<NoticeDTO> list = new ArrayList<NoticeDTO>();

		try {
			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(Notice.class, true);

			ClassAttribute ca = null;

			String sortKey = WTAttributeNameIfc.MODIFY_STAMP_NAME;
			String sort = "true";

			ca = new ClassAttribute(Notice.class, sortKey);
			OrderBy orderBy = new OrderBy(ca, Boolean.parseBoolean(sort));
			query.appendOrderBy(orderBy, new int[] { idx });

			query.setAdvancedQueryEnabled(true);
			query.setDescendantQuery(false);

			QueryResult result = PersistenceHelper.manager.find(query);

			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				Notice notice = (Notice) obj[0];
				NoticeDTO data = new NoticeDTO(notice);
				list.add(data);
			}
			map.put("list", list);
			map.put("result", "SUCCESS");
		} catch (Exception e) {
			map.put("result", "FAIL");
			e.printStackTrace();
		}
		return map;
	}
}
