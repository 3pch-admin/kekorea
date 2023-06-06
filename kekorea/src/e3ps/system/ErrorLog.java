package e3ps.system;

import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.fc.WTObject;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { Ownable.class },

		properties = {

				@GeneratedProperty(name = "callUrl", type = String.class, constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "errorMsg", type = String.class, constraints = @PropertyConstraints(upperLimit = 4000, required = true)),

				@GeneratedProperty(name = "logType", type = String.class, constraints = @PropertyConstraints(required = true)) }

)
public class ErrorLog extends _ErrorLog {

	public static final long serialVersionUID = 1;

	public static ErrorLog newErrorLog() throws WTException {
		ErrorLog instance = new ErrorLog();
		instance.initialize();
		return instance;
	}
}
