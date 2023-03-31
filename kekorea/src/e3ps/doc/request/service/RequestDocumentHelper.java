package e3ps.doc.request.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.doc.request.RequestDocument;
import e3ps.doc.request.RequestDocumentProjectLink;
import e3ps.doc.request.dto.RequestDocumentDTO;
import e3ps.project.Project;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;

public class RequestDocumentHelper {

	public static final RequestDocumentHelper manager = new RequestDocumentHelper();
	public static final RequestDocumentService service = ServiceFactory.getService(RequestDocumentService.class);

	// 의뢰서 저장폴더
	public static final String REQUEST_DOCUMENT_ROOT = "/Default/프로젝트/의뢰서";

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<RequestDocumentDTO> list = new ArrayList<>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(RequestDocument.class, true);
		int idx_link = query.appendClassList(RequestDocumentProjectLink.class, true);
		int idx_p = query.appendClassList(Project.class, true);

		QuerySpecUtils.toInnerJoin(query, RequestDocumentProjectLink.class, RequestDocument.class,
				"roleAObjectRef.key.id", WTAttributeNameIfc.ID_NAME, idx_link, idx);
		QuerySpecUtils.toInnerJoin(query, RequestDocumentProjectLink.class, Project.class, "roleBObjectRef.key.id",
				WTAttributeNameIfc.ID_NAME, idx_link, idx_p);

		QuerySpecUtils.toEqualsAnd(query, idx, RequestDocument.class, RequestDocument.DOC_TYPE, "$$Request");

		QuerySpecUtils.toOrderBy(query, idx, RequestDocument.class, RequestDocument.CREATE_TIMESTAMP, false);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			RequestDocumentProjectLink link = (RequestDocumentProjectLink) obj[1];
			RequestDocumentDTO column = new RequestDocumentDTO(link);
			list.add(column);
		}

		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	public ArrayList<RequestDocumentProjectLink> getLinks(RequestDocument requestDocument) throws Exception {
		ArrayList<RequestDocumentProjectLink> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(RequestDocument.class, true);
		int idx_link = query.appendClassList(RequestDocumentProjectLink.class, true);
		QuerySpecUtils.toInnerJoin(query, RequestDocument.class, RequestDocumentProjectLink.class,
				WTAttributeNameIfc.ID_NAME, "roleAObjectRef.key.id", idx, idx_link);
		QuerySpecUtils.toEqualsAnd(query, idx_link, RequestDocumentProjectLink.class, "roleAObjectRef.key.id",
				requestDocument.getPersistInfo().getObjectIdentifier().getId());
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			RequestDocumentProjectLink link = (RequestDocumentProjectLink) obj[1];
			list.add(link);
		}
		return list;
	}

	/**
	 * 의뢰시 작번 내용 입력시 검증
	 */
	public Map<String, Object> validate(Map<String, String> params) throws Exception {
		Map<String, Object> result = new HashMap<>();
		String kekNumber = params.get("kekNumber");
		String projectType_code = params.get("projectType_code");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Project.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, Project.class, Project.KEK_NUMBER, kekNumber);
		CommonCode projectTypeCode = CommonCodeHelper.manager.getCommonCode(projectType_code, "PROJECT_TYPE");
		QuerySpecUtils.toEqualsAnd(query, idx, Project.class, "projectTypeReference.key.id",
				projectTypeCode.getPersistInfo().getObjectIdentifier().getId());
		QueryResult qr = PersistenceHelper.manager.find(query);
		result.put("validate", qr.size() > 0 ? true : false);
		return result;
	}
}
