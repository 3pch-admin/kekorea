package e3ps.project;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedRole;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import e3ps.admin.commonCode.CommonCode;
import wt.fc.ObjectToObjectLink;
import wt.org.WTUser;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(name = "project", type = Project.class),

		roleB = @GeneratedRole(name = "user", type = WTUser.class),

		foreignKeys = { @GeneratedForeignKey(name = "ProjectUserTypeLink",

				foreignKeyRole = @ForeignKeyRole(name = "userType", type = CommonCode.class,

						constraints = @PropertyConstraints(required = true)),

				myRole = @MyRole(name = "userLink", cardinality = Cardinality.ONE)), })

public class ProjectUserLink extends _ProjectUserLink {
	static final long serialVersionUID = 1;

	public static ProjectUserLink newProjectUserLink(Project project, WTUser user) throws WTException {
		ProjectUserLink instance = new ProjectUserLink();
		instance.initialize(project, user);
		return instance;
	}
}
