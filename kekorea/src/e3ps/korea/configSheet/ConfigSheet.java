package e3ps.korea.configSheet;

import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.content.ContentHolder;
import wt.enterprise.Managed;
import wt.util.WTException;

@GenAsPersistable(superClass = Managed.class, interfaces = { ContentHolder.class },

		properties = {

				@GeneratedProperty(name = "name", type = String.class, javaDoc = "CONFIG SHEET 명"),

				@GeneratedProperty(name = "description", type = String.class, constraints = @PropertyConstraints(upperLimit = 2000)),

				@GeneratedProperty(name = "version", type = Integer.class, javaDoc = "버전"),

				@GeneratedProperty(name = "latest", type = Boolean.class, javaDoc = "최신버전여부"),

				@GeneratedProperty(name = "number", type = String.class, constraints = @PropertyConstraints(required = true), columnProperties = @ColumnProperties(columnName = "WorkOrderNumber")),

				@GeneratedProperty(name = "note", type = String.class, javaDoc = "개정사유", constraints = @PropertyConstraints(upperLimit = 2000)),

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
