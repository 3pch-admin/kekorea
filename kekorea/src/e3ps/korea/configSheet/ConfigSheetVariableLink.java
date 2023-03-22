package e3ps.korea.configSheet;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.GeneratedRole;

import wt.fc.ObjectToObjectLink;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(name = "configSheet", type = ConfigSheet.class),

		roleB = @GeneratedRole(name = "variable", type = ConfigSheetVariable.class),

		properties = {

				@GeneratedProperty(name = "sort", type = Integer.class)

		}

)
public class ConfigSheetVariableLink extends _ConfigSheetVariableLink {

	static final long serialVersionUID = 1;

	public static ConfigSheetVariableLink newConfigSheetVariableLink(ConfigSheet configSheet,
			ConfigSheetVariable variable) throws WTException {
		ConfigSheetVariableLink instance = new ConfigSheetVariableLink();
		instance.initialize(configSheet, variable);
		return instance;
	}
}
