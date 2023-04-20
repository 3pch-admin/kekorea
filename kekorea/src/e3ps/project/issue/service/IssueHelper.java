package e3ps.project.issue.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import e3ps.common.util.AUIGridUtils;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.doc.meeting.Meeting;
import e3ps.doc.meeting.MeetingProjectLink;
import e3ps.project.Project;
import e3ps.project.issue.Issue;
import e3ps.project.issue.IssueProjectLink;
import e3ps.project.issue.beans.IssueDTO;
import e3ps.workspace.ApprovalMaster;
import net.sf.json.JSONArray;
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
		ArrayList<IssueDTO> list = new ArrayList<>();

		String issueName = (String) params.get("issueName");
		String description = (String) params.get("description");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Issue.class, true);
		int idx_link = query.appendClassList(IssueProjectLink.class, true);
		int idx_p = query.appendClassList(Project.class, true);

		QuerySpecUtils.toInnerJoin(query, Issue.class, IssueProjectLink.class, WTAttributeNameIfc.ID_NAME,
				"roleAObjectRef.key.id", idx, idx_link);
		QuerySpecUtils.toInnerJoin(query, Project.class, IssueProjectLink.class, WTAttributeNameIfc.ID_NAME,
				"roleBObjectRef.key.id", idx_p, idx_link);

		QuerySpecUtils.toOrderBy(query, idx, Issue.class, Issue.CREATE_TIMESTAMP, true);

		if (!StringUtils.isNull(issueName)) {
			QuerySpecUtils.toLikeAnd(query, idx, Issue.class, Issue.NAME, issueName);
		}
		if (!StringUtils.isNull(description)) {
			QuerySpecUtils.toLikeAnd(query, idx, Issue.class, Issue.DESCRIPTION, description);
		}
		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			IssueProjectLink link = (IssueProjectLink) obj[1];
			IssueDTO column = new IssueDTO(link);
			list.add(column);
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
		int idx = query.appendClassList(Issue.class, true);
		int idx_link = query.appendClassList(IssueProjectLink.class, true);
		int idx_p = query.appendClassList(Project.class, true);

		QuerySpecUtils.toInnerJoin(query, Issue.class, IssueProjectLink.class, WTAttributeNameIfc.ID_NAME,
				"roleAObjectRef.key.id", idx, idx_link);
		QuerySpecUtils.toInnerJoin(query, Project.class, IssueProjectLink.class, WTAttributeNameIfc.ID_NAME,
				"roleBObjectRef.key.id", idx_p, idx_link);
		QuerySpecUtils.toEqualsAnd(query, idx_link, IssueProjectLink.class, "roleBObjectRef.key.id", project);

		QuerySpecUtils.toOrderBy(query, idx, Issue.class, Issue.CREATE_TIMESTAMP, true);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			IssueProjectLink link = (IssueProjectLink) obj[1];
			Issue issue = link.getIssue();
			Map<String, String> map = new HashMap();
			map.put("name", issue.getName());
			map.put("description", issue.getDescription());
			map.put("creator", issue.getOwnership().getOwner().getFullName());
			map.put("createdDate_txt", CommonUtils.getPersistableTime(issue.getCreateTimestamp()));
			map.put("icons", AUIGridUtils.secondaryTemplate(issue));
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}
}
