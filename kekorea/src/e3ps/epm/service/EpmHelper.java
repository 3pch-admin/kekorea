
package e3ps.epm.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.common.util.CommonUtils;
import e3ps.common.util.DateUtils;
import e3ps.common.util.FolderUtils;
import e3ps.common.util.IBAUtils;
import e3ps.common.util.MessageHelper;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.epm.ViewerData;
import e3ps.epm.column.EpmLibraryColumnData;
import e3ps.epm.column.EpmProductColumnData;
import e3ps.epm.column.ViewerColumnData;
import e3ps.epm.dto.EpmDTO;
import e3ps.org.People;
import e3ps.workspace.ApprovalContract;
import e3ps.workspace.ApprovalContractPersistableLink;
import e3ps.workspace.service.WorkspaceHelper;
import net.sf.json.JSONArray;
import wt.clients.folder.FolderTaskLogic;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.build.EPMBuildHistory;
import wt.epm.build.EPMBuildRule;
import wt.epm.structure.EPMReferenceLink;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.folder.IteratedFolderMemberLink;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.query.ClassAttribute;
import wt.query.ColumnExpression;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SQLFunction;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;
import wt.vc.Mastered;
import wt.vc.VersionControlHelper;
import wt.vc.wip.WorkInProgressHelper;

public class EpmHelper {

	/**
	 * 도면 저장 기본위치 제품 - 라이브러리 동일
	 */
	public static final String DEFAULT_ROOT = "/Default/도면";

	/**
	 * 제품, 라이브러리 컨데이터 구분 변수
	 */
	public static final String PRODUCT_CONTAINER = "PRODUCT";
	public static final String LIBRARY_CONTAINER = "LIBRARY";

	public static final EpmService service = ServiceFactory.getService(EpmService.class);
	public static final EpmHelper manager = new EpmHelper();

	public WTPart getPart(EPMDocument epm) throws Exception {
		WTPart part = null;
		if (epm == null) {
			return part;
		}

		QueryResult result = null;
		if (VersionControlHelper.isLatestIteration(epm)) {
			result = PersistenceHelper.manager.navigate(epm, "buildTarget", EPMBuildRule.class);
		} else {
			result = PersistenceHelper.manager.navigate(epm, "built", EPMBuildHistory.class);
		}

		while (result.hasMoreElements()) {
			part = (WTPart) result.nextElement();
		}
		return part;
	}

	public boolean isNumber(String number) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(EPMDocumentMaster.class, true);
		SearchCondition sc = new SearchCondition(EPMDocumentMaster.class, EPMDocumentMaster.NUMBER, "=",
				number.toUpperCase().trim());
		query.appendWhere(sc, new int[] { idx });
		QueryResult result = PersistenceHelper.manager.find(query);
		boolean isNumber = result.size() > 0 ? true : false;
		return isNumber;
	}

	public EPMDocument getLatestEPM(String number) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(EPMDocument.class, true);
		SearchCondition sc = new SearchCondition(EPMDocument.class, EPMDocument.NUMBER, "=",
				number.toUpperCase().trim());
		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();

		sc = VersionControlHelper.getSearchCondition(EPMDocument.class, true);
		query.appendWhere(sc, new int[] { idx });

		CommonUtils.addLastVersionCondition(query, idx);

		QueryResult result = PersistenceHelper.manager.find(query);
		EPMDocument e = null;
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			e = (EPMDocument) obj[0];
		}
		return e;
	}

	public EPMDocument getLatestEPMByName(String name) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(EPMDocument.class, true);
		SearchCondition sc = new SearchCondition(EPMDocument.class, EPMDocument.NAME, "=", name.toUpperCase().trim());
		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();

		sc = VersionControlHelper.getSearchCondition(EPMDocument.class, true);
		query.appendWhere(sc, new int[] { idx });

		CommonUtils.addLastVersionCondition(query, idx);

		QueryResult result = PersistenceHelper.manager.find(query);
		EPMDocument e = null;
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			e = (EPMDocument) obj[0];
		}
		return e;
	}

	public Map<String, Object> checkDrawing(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		// String oid = (String) param.get("oid");

		List<String> list = (List<String>) param.get("list");

		ReferenceFactory rf = new ReferenceFactory();
		EPMDocument epm = null;
		boolean is2D = false;
		try {

			for (String oid : list) {
				epm = (EPMDocument) rf.getReference(oid).getObject();

				if (epm.getDocType().toString().equals("CADDRAWING")) {
					is2D = true;
					break;
				}
			}

			if (is2D) {
				map.put("msg", "도면 검증에 실패 하였습니다\n시스템 관리자에게 문의하세요.");
			} else {
				map.put("msg", "2D 도면 파일이 아니여서 출력이 불가능합니다.");
			}

			map.put("is2D", is2D);
		} catch (Exception e) {
			map.put("msg", "도면 검증에 실패 하였습니다\n시스템 관리자에게 문의하세요.");
			e.printStackTrace();
		}
		return map;
	}

	public EPMDocument getEPM2D(EPMDocument ee) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(EPMReferenceLink.class, true);

		EPMDocumentMaster master = (EPMDocumentMaster) ee.getMaster();
		long id = master.getPersistInfo().getObjectIdentifier().getId();

		SearchCondition sc = new SearchCondition(EPMReferenceLink.class, "roleBObjectRef.key.id", "=", id);
		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();

		sc = new SearchCondition(EPMReferenceLink.class, EPMReferenceLink.REFERENCE_TYPE, "=", "DRAWING");
		query.appendWhere(sc, new int[] { idx });

		EPMDocument epm2d = null;
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			EPMReferenceLink link = (EPMReferenceLink) obj[0];
			epm2d = link.getReferencedBy();
		}
		return epm2d;
	}

	public boolean checkPLMYCode(String itemNo) throws Exception {
		QuerySpec query = null;

		boolean isYcode = false;

		try {
			query = new QuerySpec();

			int idx = query.appendClassList(EPMDocument.class, true);

//			SearchCondition sc = null;
//			ClassAttribute ca = null;

			if (!StringUtils.isNull(itemNo)) {
				IBAUtils.addIBAConditionEquals(query, EPMDocument.class, idx, "PART_CODE", itemNo);
			}

			QueryResult result = PersistenceHelper.manager.find(query);

			if (result.size() > 0) {
				isYcode = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return isYcode;
	}

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		System.out.println("검색 START = " + new Timestamp(new Date().getTime()));
		Map<String, Object> map = new HashMap<String, Object>();
		List<EpmDTO> list = new ArrayList<EpmDTO>();

		boolean latest = (boolean) params.get("latest");
		String oid = (String) params.get("oid"); // 폴더 OID
		String container = (String) params.get("container");

		QuerySpec query = new QuerySpec();

		int idx = query.appendClassList(EPMDocument.class, true);
		int idx_m = query.appendClassList(EPMDocumentMaster.class, false);

		QuerySpecUtils.toCI(query, idx, EPMDocument.class);
		QuerySpecUtils.toInnerJoin(query, EPMDocument.class, EPMDocumentMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);

		Folder folder = null;
		if (!StringUtils.isNull(oid)) {
			folder = (Folder) CommonUtils.getObject(oid);
		} else {
			if (container.equals(PRODUCT_CONTAINER)) {
				folder = FolderTaskLogic.getFolder(DEFAULT_ROOT, CommonUtils.getPDMLinkProductContainer());
			} else if (container.equalsIgnoreCase(LIBRARY_CONTAINER)) {
				folder = FolderTaskLogic.getFolder(DEFAULT_ROOT, CommonUtils.getWTLibraryContainer());
			}
		}

		if (folder != null) {
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			int f_idx = query.appendClassList(IteratedFolderMemberLink.class, false);
			ClassAttribute fca = new ClassAttribute(IteratedFolderMemberLink.class, "roleBObjectRef.key.branchId");
			SearchCondition fsc = new SearchCondition(fca, "=",
					new ClassAttribute(EPMDocument.class, "iterationInfo.branchId"));
			fsc.setFromIndicies(new int[] { f_idx, idx }, 0);
			fsc.setOuterJoin(0);
			query.appendWhere(fsc, new int[] { f_idx, idx });
			query.appendAnd();
			long fid = folder.getPersistInfo().getObjectIdentifier().getId();
			query.appendWhere(new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", "=", fid),
					new int[] { f_idx });
		}

		if (latest) {
			QuerySpecUtils.toLatest(query, idx, EPMDocument.class);
		}

		QuerySpecUtils.toOrderBy(query, idx, EPMDocument.class, EPMDocument.MODIFY_TIMESTAMP, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			EPMDocument epm = (EPMDocument) obj[0];
			EpmDTO column = new EpmDTO(epm);
			list.add(column);
		}
		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		System.out.println("검색 END = " + new Timestamp(new Date().getTime()));
		return map;
	}

	// 버전이력
	public JSONArray history(Mastered master) throws Exception {
		ArrayList<EpmDTO> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(EPMDocument.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, EPMDocument.class, "masterReference.key.id",
				master.getPersistInfo().getObjectIdentifier().getId());
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			EPMDocument epm = (EPMDocument) obj[0];
			EpmDTO dto = new EpmDTO(epm);
			list.add(dto);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 관련 작번 CONFIG SHEET 랑 연결..
	 */
	public JSONArray jsonAuiProject(String oid) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
