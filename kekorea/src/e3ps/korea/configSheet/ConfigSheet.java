package e3ps.korea.configSheet;

import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.content.ContentHolder;
import wt.fc.WTObject;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { ContentHolder.class, Ownable.class },

		properties = {

				@GeneratedProperty(name = "name", type = String.class, javaDoc = "CONFIG SHEET 명"),

				@GeneratedProperty(name = "number", type = String.class, columnProperties = @ColumnProperties(columnName = "sheetNumber")),

				@GeneratedProperty(name = "description", type = String.class, constraints = @PropertyConstraints(upperLimit = 2000))

		}

)
public class ConfigSheet extends _ConfigSheet {

	static final long serialVersionUID = 1;

	public static ConfigSheet newConfigSheet() throws WTException {
		ConfigSheet instance = new ConfigSheet();
		instance.initialize();
		return instance;
	}
}
