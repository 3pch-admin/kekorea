
package e3ps.epm.workOrder;

import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.fc.WTObject;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { Ownable.class },

		properties = {

				@GeneratedProperty(name = "workOrderNumber", type = String.class, javaDoc = "도면이람표 번호", constraints = @PropertyConstraints(required = true), columnProperties = @ColumnProperties(index = true)),

				@GeneratedProperty(name = "name", type = String.class, javaDoc = "도면일람표 명", constraints = @PropertyConstraints(required = true), columnProperties = @ColumnProperties(index = true)),

		}

)
public class WorkOrderMaster extends _WorkOrderMaster {

	static final long serialVersionUID = 1;

	public static WorkOrderMaster newWorkOrderMaster() throws WTException {
		WorkOrderMaster instance = new WorkOrderMaster();
		instance.initialize();
		return instance;
	}
}
