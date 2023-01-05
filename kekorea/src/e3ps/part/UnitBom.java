package e3ps.part;

import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;

import wt.fc.WTObject;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { Ownable.class },

		properties = {

				@GeneratedProperty(name = "uCode", type = String.class),

				@GeneratedProperty(name = "partName", type = String.class),

				@GeneratedProperty(name = "partNo", type = String.class),

				@GeneratedProperty(name = "spec", type = String.class),

				@GeneratedProperty(name = "unit", type = String.class),

				@GeneratedProperty(name = "maker", type = String.class),

				@GeneratedProperty(name = "customer", type = String.class),

				@GeneratedProperty(name = "currency", type = String.class),

				@GeneratedProperty(name = "price", type = String.class),

		})
public class UnitBom extends _UnitBom {

	static final long serialVersionUID = 1;

	public static UnitBom newUnitBom() throws WTException {
		UnitBom instance = new UnitBom();
		instance.initialize();
		return instance;
	}
}
