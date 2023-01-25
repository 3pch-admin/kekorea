package e3ps.admin.sheetvariable;

import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.fc.WTObject;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class,

		properties = {

				@GeneratedProperty(name = "name", type = String.class, constraints = @PropertyConstraints(upperLimit = 2000)),

				@GeneratedProperty(name = "sort", type = Integer.class),

				@GeneratedProperty(name = "latest", type = Boolean.class, initialValue = "true"),

				@GeneratedProperty(name = "version", type = Integer.class, initialValue = "1"),
				
				@GeneratedProperty(name = "enable", type = boolean.class, initialValue = "true")


		}

)

public class Category extends _Category {
	static final long serialVersionUID = 1;

	public static Category newCategory() throws WTException {
		Category instance = new Category();
		instance.initialize();
		return instance;
	}
}
