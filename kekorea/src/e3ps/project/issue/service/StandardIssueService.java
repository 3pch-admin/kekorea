package e3ps.project.issue.service;

import wt.services.StandardManager;
import wt.util.WTException;

public class StandardIssueService extends StandardManager implements IssueService {

	public static StandardIssueService newStandardIssueService() throws WTException {
		StandardIssueService instance = new StandardIssueService();
		instance.initialize();
		return instance;
	}
}
