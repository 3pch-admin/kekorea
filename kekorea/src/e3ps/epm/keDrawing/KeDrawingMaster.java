
package e3ps.epm.keDrawing;

import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;
import com.ptc.windchill.annotations.metadata.TableProperties;

import wt.fc.WTObject;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { Ownable.class },

		properties = {
				@GeneratedProperty(name = "keNumber", type = String.class, javaDoc = "도면번호", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "name", type = String.class, javaDoc = "도면명", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "lotNo", type = Integer.class, javaDoc = "LOT", constraints = @PropertyConstraints(required = true)),

		},

		tableProperties = @TableProperties(compositeIndex1 = "+ keNumber + lotNo")

)
public class KeDrawingMaster extends _KeDrawingMaster {

	static final long serialVersionUID = 1;

	public static KeDrawingMaster newKeDrawingMaster() throws WTException {
		KeDrawingMaster instance = new KeDrawingMaster();
		instance.initialize();
		return instance;
	}
}
