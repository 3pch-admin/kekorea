package e3ps.project;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.GeneratedRole;

import wt.fc.ObjectToObjectLink;
import wt.org.WTUser;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(name = "project", type = Project.class),

		roleB = @GeneratedRole(name = "user", type = WTUser.class),

		properties = {

				@GeneratedProperty(name = "userType", type = String.class)

		}

)
public class ProjectUserLink extends _ProjectUserLink {
	static final long serialVersionUID = 1;

	public static ProjectUserLink newProjectUserLink(Project project, WTUser user) throws WTException {
		ProjectUserLink instance = new ProjectUserLink();
		instance.initialize(project, user);
		return instance;
	}
}
