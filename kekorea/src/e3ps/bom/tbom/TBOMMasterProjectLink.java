package e3ps.bom.tbom;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedRole;

import e3ps.project.Project;
import wt.fc.ObjectToObjectLink;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(name = "master", type = TBOMMaster.class),

		roleB = @GeneratedRole(name = "project", type = Project.class)

)
public class TBOMMasterProjectLink extends _TBOMMasterProjectLink {

	static final long serialVersionUID = 1;

	public static TBOMMasterProjectLink newTBOMMasterProjectLink(TBOMMaster tbomMaster, Project project)
			throws WTException {
		TBOMMasterProjectLink instance = new TBOMMasterProjectLink();
		instance.initialize(tbomMaster, project);
		return instance;
	}
}
