package e3ps.korea.configSheet;

import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;

import wt.fc.WTObject;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class,

		properties = {

				@GeneratedProperty(name = "value", type = String.class),

				@GeneratedProperty(name = "dataField", type = String.class),

				@GeneratedProperty(name = "last", type = Boolean.class)

		}

)
public class ConfigSheetColumnData extends _ConfigSheetColumnData {

	static final long serialVersionUID = 1;

	public static ConfigSheetColumnData newConfigSheetColumnData() throws WTException {
		ConfigSheetColumnData instance = new ConfigSheetColumnData();
		instance.initialize();
		return instance;
	}
}
