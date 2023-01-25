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

				@GeneratedProperty(name = "enable", type = boolean.class, initialValue = "true")

		}

)
public class Items extends _Items {

	static final long serialVersionUID = 1;

	public static Items newItems() throws WTException {
		Items instance = new Items();
		instance.initialize();
		return instance;
	}
}
