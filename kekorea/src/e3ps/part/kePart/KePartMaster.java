package e3ps.part.kePart;

import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.fc.WTObject;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { Ownable.class },

		properties = {

				@GeneratedProperty(name = "kePartName", type = String.class, constraints = @PropertyConstraints(required = true), columnProperties = @ColumnProperties(unique = true, index = true)),

				@GeneratedProperty(name = "kePartNumber", type = String.class, constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "lotNo", type = String.class, constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "code", type = String.class, constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "model", type = String.class, constraints = @PropertyConstraints(required = true)),

		}

)
public class KePartMaster extends _KePartMaster {

	static final long serialVersionUID = 1;

	public static KePartMaster newKePartMaster() throws WTException {
		KePartMaster instance = new KePartMaster();
		instance.initialize();
		return instance;
	}
}
