package e3ps.korea.configSheet;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.GeneratedRole;

import wt.fc.ObjectToObjectLink;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(name = "column", type = ConfigSheetColumnData.class),

		roleB = @GeneratedRole(name = "variable", type = ConfigSheetVariable.class),

		properties = {

				@GeneratedProperty(name = "sort", type = Integer.class),

				@GeneratedProperty(name = "last", type = Boolean.class)
		}

)
public class ColumnVariableLink extends _ColumnVariableLink {

	static final long serialVersionUID = 1;

	public static ColumnVariableLink newColumnVariableLink(ConfigSheetColumnData column, ConfigSheetVariable variable)
			throws WTException {
		ColumnVariableLink instance = new ColumnVariableLink();
		instance.initialize(column, variable);
		return instance;
	}
}
