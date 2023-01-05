package e3ps.partlist;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedRole;

import e3ps.project.Project;
import wt.fc.ObjectToObjectLink;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(name = "partListMaster", type = PartListMaster.class),

		roleB = @GeneratedRole(name = "project", type = Project.class)

)

public class PartListMasterProjectLink extends _PartListMasterProjectLink {

	static final long serialVersionUID = 1;

	public static PartListMasterProjectLink newPartListMasterProjectLink(PartListMaster partListMaster, Project project)
			throws WTException {
		PartListMasterProjectLink instance = new PartListMasterProjectLink();
		instance.initialize(partListMaster, project);
		return instance;
	}
}
