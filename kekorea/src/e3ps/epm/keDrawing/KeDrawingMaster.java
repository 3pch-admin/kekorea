
package e3ps.epm.keDrawing;

import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.fc.WTObject;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { Ownable.class },

		properties = {

				@GeneratedProperty(name = "keNumber", type = String.class, javaDoc = "KE 도면번호", constraints = @PropertyConstraints(required = true), columnProperties = @ColumnProperties(index = true)),

				@GeneratedProperty(name = "name", type = String.class, javaDoc = "KE 도면명", constraints = @PropertyConstraints(required = true), columnProperties = @ColumnProperties(index = true)),

				@GeneratedProperty(name = "lotNo", type = Integer.class, javaDoc = "LOT NO", constraints = @PropertyConstraints(required = true)),

		}

)
public class KeDrawingMaster extends _KeDrawingMaster {

	static final long serialVersionUID = 1;

	public static KeDrawingMaster newKeDrawingMaster() throws WTException {
		KeDrawingMaster instance = new KeDrawingMaster();
		instance.initialize();
		return instance;
	}
}
