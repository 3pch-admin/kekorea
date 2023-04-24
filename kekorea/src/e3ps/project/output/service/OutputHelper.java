package e3ps.project.output.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import e3ps.common.util.CommonUtils;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.project.output.dto.OutputDTO;
import wt.clients.folder.FolderTaskLogic;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.epm.EPMDocument;
import wt.fc.PagingQueryResult;
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

		// 검색 변수
		boolean latest = (boolean) params.get("latest");
		// 폴더 OID
		String oid = (String) params.get("oid"); // 폴더 OID
		String type = (String) params.get("type");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTDocument.class, true);
		int idx_m = query.appendClassList(WTDocumentMaster.class, false);

		QuerySpecUtils.toCI(query, idx, WTDocument.class);
		QuerySpecUtils.toInnerJoin(query, WTDocument.class, WTDocumentMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);

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
			System.out.println("loc=" + folder.getLocation());
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

		if (latest) {
			QuerySpecUtils.toLatest(query, idx, WTDocument.class);
		}

		QuerySpecUtils.toOrderBy(query, idx, WTDocument.class, WTDocument.MODIFY_TIMESTAMP, true);
		System.out.println(query);

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
}
