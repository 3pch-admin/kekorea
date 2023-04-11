package e3ps.doc.request.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.doc.meeting.Meeting;
import e3ps.doc.request.RequestDocument;
import e3ps.doc.request.RequestDocumentProjectLink;
import e3ps.doc.request.dto.RequestDocumentDTO;
import e3ps.project.Project;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
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
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(RequestDocument.class, true);

		QuerySpecUtils.toOrderBy(query, idx, RequestDocument.class, Meeting.CREATE_TIMESTAMP, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();

		JSONArray list = new JSONArray();

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			RequestDocument requestDocument = (RequestDocument) obj[0];

			JSONObject node = new JSONObject();
			node.put("oid", requestDocument.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("name", requestDocument.getName());
			QueryResult group = PersistenceHelper.manager.navigate(requestDocument, "project",
					RequestDocumentProjectLink.class, false);
			int isNode = 1;
			JSONArray children = new JSONArray();
			while (group.hasMoreElements()) {
				RequestDocumentProjectLink link = (RequestDocumentProjectLink) group.nextElement();
				RequestDocumentDTO dto = new RequestDocumentDTO(link);
				if (isNode == 1) {
					node.put("poid", dto.getPoid());
					node.put("loid", link.getPersistInfo().getObjectIdentifier().getStringValue());
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
					node.put("creatorId", requestDocument.getCreatorName());
					node.put("createdDate_txt", dto.getCreatedDate_txt());
				} else {
					JSONObject data = new JSONObject();
					data.put("name", dto.getName());
					data.put("oid", dto.getOid());
					data.put("loid", link.getPersistInfo().getObjectIdentifier().getStringValue());
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
					data.put("creatorId", requestDocument.getCreatorName());
					data.put("createdDate_txt", dto.getCreatedDate_txt());
					children.add(data);
				}
				isNode++;
			}
			node.put("children", children);
			list.add(node);
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
