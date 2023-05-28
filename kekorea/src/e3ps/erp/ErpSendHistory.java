package e3ps.erp;

import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.fc.WTObject;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { Ownable.class },

		properties = {

				@GeneratedProperty(name = "name", type = String.class, constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "resultMsg", type = String.class, constraints = @PropertyConstraints(upperLimit = 4000)),

				@GeneratedProperty(name = "result", type = Boolean.class, initialValue = "true", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "sendType", type = String.class, constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "sendQuery", type = String.class, constraints = @PropertyConstraints(upperLimit = 4000))

		}

)

public class ErpSendHistory extends _ErpSendHistory {
	static final long serialVersionUID = 1;

	public static ErpSendHistory newErpSendHistory() throws WTException {
		ErpSendHistory instance = new ErpSendHistory();
		instance.initialize();
		return instance;

	}
}
