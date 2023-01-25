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

				@GeneratedProperty(name = "latest", type = Boolean.class, initialValue = "true"),

				@GeneratedProperty(name = "version", type = Integer.class, initialValue = "1"),

				@GeneratedProperty(name = "enable", type = boolean.class, initialValue = "true"),

				@GeneratedProperty(name = "config", type = boolean.class, initialValue = "true"),

				@GeneratedProperty(name = "histroy", type = boolean.class, initialValue = "true"), }

)
public class Spec extends _Spec {

	static final long serialVersionUID = 1;

	public static Spec newSpec() throws WTException {
		Spec instance = new Spec();
		instance.initialize();
		return instance;
	}
}