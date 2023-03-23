package e3ps.korea.configSheet;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.content.ContentHolder;
import wt.fc.WTObject;
import wt.org.WTUser;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { ContentHolder.class, Ownable.class },

		properties = {

				@GeneratedProperty(name = "name", type = String.class, javaDoc = "CONFIG SHEET 명"),

				@GeneratedProperty(name = "description", type = String.class, constraints = @PropertyConstraints(upperLimit = 2000)),

				@GeneratedProperty(name = "state", type = String.class, constraints = @PropertyConstraints(required = true)),

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
