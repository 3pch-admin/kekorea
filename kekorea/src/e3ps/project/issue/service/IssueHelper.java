package e3ps.project.issue.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import e3ps.admin.commonCode.CommonCode;
import e3ps.common.util.AUIGridUtils;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.epm.workOrder.WorkOrder;
import e3ps.epm.workOrder.WorkOrderProjectLink;
import e3ps.project.Project;
import e3ps.project.issue.Issue;
import e3ps.project.issue.IssueProjectLink;
import e3ps.project.issue.beans.IssueDTO;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;

public class IssueHelper {

	public static final IssueHelper manager = new IssueHelper();
	public static final IssueService service = ServiceFactory.getService(IssueService.class);

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();

		String kekNumber = (String) params.get("kekNumber");
		String keNumber = (String) params.get("keNumber");
		String name = (String) params.get("name");
		String mak_name = (String) params.get("mak_name");
		String content = (String) params.get("content");
		String description = (String) params.get("description");
		String createdFrom = (String) params.get("createdFrom");
		String createdTo = (String) params.get("createdTo");
		String creatorOid = (String) params.get("creatorOid");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Issue.class, true);

		QuerySpecUtils.toLikeAnd(query, idx, Issue.class, Issue.NAME, name);
		QuerySpecUtils.toLikeAnd(query, idx, Issue.class, Issue.DESCRIPTION, content);

		QuerySpecUtils.toCreator(query, idx, Issue.class, creatorOid);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, Issue.class, Issue.CREATE_TIMESTAMP, createdFrom, createdTo);

		QuerySpecUtils.toOrderBy(query, idx, Issue.class, Issue.CREATE_TIMESTAMP, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		JSONArray list = new JSONArray();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Issue issue = (Issue) obj[0];

			QuerySpec _query = new QuerySpec();
			int _idx = _query.appendClassList(Issue.class, true);
			int _idx_p = _query.appendClassList(Project.class, true);
			int _idx_link = _query.appendClassList(IssueProjectLink.class, true);

			QuerySpecUtils.toEqualsAnd(_query, _idx_link, IssueProjectLink.class, "roleAObjectRef.key.id", issue);
			QuerySpecUtils.toInnerJoin(_query, Issue.class, IssueProjectLink.class, WTAttributeNameIfc.ID_NAME,
					"roleAObjectRef.key.id", _idx, _idx_link);
			QuerySpecUtils.toInnerJoin(_query, Project.class, IssueProjectLink.class, WTAttributeNameIfc.ID_NAME,
					"roleBObjectRef.key.id", _idx_p, _idx_link);
			QuerySpecUtils.toLikeAnd(_query, _idx_p, Project.class, Project.KEK_NUMBER, kekNumber);
			QuerySpecUtils.toLikeAnd(_query, _idx_p, Project.class, Project.KE_NUMBER, keNumber);

			if (!StringUtils.isNull(mak_name)) {
				CommonCode makCode = (CommonCode) CommonUtils.getObject(mak_name);
				QuerySpecUtils.toEqualsAnd(_query, _idx_p, Project.class, "makReference.key.id", makCode);
			}

			QuerySpecUtils.toLikeAnd(_query, _idx_p, Project.class, Project.DESCRIPTION, description);

			JSONObject node = new JSONObject();
			QueryResult group = PersistenceHelper.manager.find(_query);

			int isNode = 1;
			JSONArray children = new JSONArray();
			while (group.hasMoreElements()) {
				Object[] oo = (Object[]) group.nextElement();
				IssueProjectLink link = (IssueProjectLink) oo[2];
				IssueDTO dto = new IssueDTO(link);
				node.put("oid", issue.getPersistInfo().getObjectIdentifier().getStringValue());
				node.put("name", issue.getName());
				node.put("content", issue.getDescription());
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
					node.put("model", dto.getModel());
					node.put("pdate_txt", dto.getPdate_txt());
					node.put("creator", dto.getCreator());
					node.put("creatorId", dto.getCreatorId());
					node.put("createdDate_txt", dto.getCreatedDate_txt());
					node.put("modifiedDate_txt", dto.getModifiedDate_txt());
				} else {
					JSONObject data = new JSONObject();
					data.put("oid", issue.getPersistInfo().getObjectIdentifier().getStringValue());
					data.put("name", issue.getName());
					data.put("content", issue.getDescription());
					data.put("poid", dto.getPoid());
					data.put("loid", link.getPersistInfo().getObjectIdentifier().getStringValue());
					data.put("projectType_name", dto.getProjectType_name());
					data.put("customer_name", dto.getCustomer_name());
					data.put("install_name", dto.getInstall_name());
					data.put("mak_name", dto.getMak_name());
					data.put("detail_name", dto.getDetail_name());
					data.put("kekNumber", dto.getKekNumber());
					data.put("keNumber", dto.getKeNumber());
					data.put("userId", dto.getUserId());
					data.put("description", dto.getDescription());
					data.put("model", dto.getModel());
					data.put("pdate_txt", dto.getPdate_txt());
					data.put("creator", dto.getCreator());
					data.put("creatorId", dto.getCreatorId());
					data.put("createdDate_txt", dto.getCreatedDate_txt());
					data.put("modifiedDate_txt", dto.getModifiedDate_txt());
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

	public JSONArray jsonArrayAui(String oid) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();
		IssueProjectLink issueProjectLink = (IssueProjectLink) CommonUtils.getObject(oid);

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Issue.class, true);
		int idx_link = query.appendClassList(IssueProjectLink.class, true);
		QuerySpecUtils.toInnerJoin(query, Issue.class, IssueProjectLink.class, WTAttributeNameIfc.ID_NAME,
				"roleAObjectRef.key.id", idx, idx_link);
		QuerySpecUtils.toEqualsAnd(query, idx_link, IssueProjectLink.class, "roleAObjectRef.key.id",
				issueProjectLink.getPersistInfo().getObjectIdentifier().getId());
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			IssueProjectLink link = (IssueProjectLink) obj[1];
			Project project = link.getProject();
			Map<String, String> map = new HashMap<>();
			map.put("oid", project.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("projectType_name", project.getProjectType() != null ? project.getProjectType().getName() : "");
			map.put("customer_name", project.getCustomer() != null ? project.getCustomer().getName() : "");
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
	 * 프로젝트 상세보기서 특이사항 탭
	 */
	public JSONArray issueTab(String oid) throws Exception {
		Project project = (Project) CommonUtils.getObject(oid);

		ArrayList<Map<String, String>> list = new ArrayList<>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Issue.class, false);
		int idx_link = query.appendClassList(IssueProjectLink.class, true);
		int idx_p = query.appendClassList(Project.class, false);
		QuerySpecUtils.toInnerJoin(query, Issue.class, IssueProjectLink.class, WTAttributeNameIfc.ID_NAME,
				"roleAObjectRef.key.id", idx, idx_link);
		QuerySpecUtils.toInnerJoin(query, Project.class, IssueProjectLink.class, WTAttributeNameIfc.ID_NAME,
				"roleBObjectRef.key.id", idx_p, idx_link);
		QuerySpecUtils.toEqualsAnd(query, idx_link, IssueProjectLink.class, "roleBObjectRef.key.id", project);
		QuerySpecUtils.toOrderBy(query, idx, Issue.class, Issue.CREATE_TIMESTAMP, true);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			IssueProjectLink link = (IssueProjectLink) obj[0];
			Issue issue = link.getIssue();
			Map<String, String> map = new HashMap();
			map.put("oid", issue.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("loid", link.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("name", issue.getName());
			map.put("description", issue.getDescription());
			map.put("creator", issue.getOwnership().getOwner().getFullName());
			map.put("createdDate_txt", CommonUtils.getPersistableTime(issue.getCreateTimestamp()));
			map.put("icons", AUIGridUtils.secondaryTemplate(issue));
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 특이사항 작번 리스트
	 */
	public JSONArray jsonAuiProject(String oid) throws Exception {
		Issue issue = (Issue) CommonUtils.getObject(oid);
		ArrayList<Map<String, String>> list = new ArrayList<>();
		QueryResult result = PersistenceHelper.manager.navigate(issue, "project", IssueProjectLink.class);
		while (result.hasMoreElements()) {
			Project project = (Project) result.nextElement();
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
}
