package e3ps.project.issue;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedRole;

import e3ps.project.Project;
import wt.fc.ObjectToObjectLink;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(name = "issue", type = Issue.class),

		roleB = @GeneratedRole(name = "project", type = Project.class)

)
public class IssueProjectLink extends _IssueProjectLink {

	static final long serialVersionUID = 1;

	public static IssueProjectLink newIssueProjectLink(Issue issue, Project project) throws WTException {
		IssueProjectLink instance = new IssueProjectLink();
		instance.initialize(issue, project);
		return instance;
	}
}