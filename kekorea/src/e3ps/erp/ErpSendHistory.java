package e3ps.erp;

import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;
import com.ptc.windchill.annotations.metadata.TableProperties;

import wt.fc.WTObject;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { Ownable.class },

		tableProperties = @TableProperties(tableName = "J_ERPSENDHISTORY"),

		properties = {

				@GeneratedProperty(name = "name", type = String.class),

				@GeneratedProperty(name = "resultMsg", type = String.class),

				@GeneratedProperty(name = "result", type = Boolean.class, initialValue = "true"),

				@GeneratedProperty(name = "sendType", type = String.class),

				@GeneratedProperty(name = "querys", type = String.class, constraints = @PropertyConstraints(upperLimit = 2000))

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
