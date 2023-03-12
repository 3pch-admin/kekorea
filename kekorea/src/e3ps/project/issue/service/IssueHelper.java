package e3ps.project.issue.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.project.Project;
import e3ps.project.issue.Issue;
import e3ps.project.issue.IssueProjectLink;
import e3ps.project.issue.beans.IssueDTO;
import e3ps.workspace.ApprovalMaster;
import wt.fc.PagingQueryResult;
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
}
