package e3ps.admin.spec;

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
public class Options extends _Options {

	static final long serialVersionUID = 1;

	public static Options newOptions() throws WTException {
		Options instance = new Options();
		instance.initialize();
		return instance;
	}
}
