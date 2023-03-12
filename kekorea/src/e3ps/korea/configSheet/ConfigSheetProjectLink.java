package e3ps.korea.configSheet;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedRole;

import e3ps.project.Project;
import wt.fc.ObjectToObjectLink;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(name = "configSheet", type = ConfigSheet.class),

		roleB = @GeneratedRole(name = "project", type = Project.class)

)
public class ConfigSheetProjectLink extends _ConfigSheetProjectLink {
	static final long serialVersionUID = 1;

	public static ConfigSheetProjectLink newConfigSheetProjectLink(ConfigSheet configSheet, Project project)
			throws WTException {
		ConfigSheetProjectLink instance = new ConfigSheetProjectLink();
		instance.initialize(configSheet, project);
		return instance;
	}
}
