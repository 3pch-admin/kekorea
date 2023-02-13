package e3ps.project.issue.service;

import wt.services.ServiceFactory;

public class IssueHelper {

	public static final IssueHelper manager = new IssueHelper();
	public static final IssueService service = ServiceFactory.getService(IssueService.class);
}
