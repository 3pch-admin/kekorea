package e3ps.epm.workOrder;

import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.content.FormatContentHolder;
import wt.fc.WTObject;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { FormatContentHolder.class, Ownable.class },

		properties = {

				@GeneratedProperty(name = "number", type = String.class, javaDoc = "작업지시서 번호", constraints = @PropertyConstraints(required = true), columnProperties = @ColumnProperties(unique = true, columnName = "workOrderNumber")),

				@GeneratedProperty(name = "name", type = String.class, javaDoc = "작업지시서 이름", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "description", type = String.class, javaDoc = "설명", constraints = @PropertyConstraints(upperLimit = 2000))

		}

)
public class WorkOrder extends _WorkOrder {

	static final long serialVersionUID = 1;

	public static WorkOrder newWorkOrder() throws WTException {
		WorkOrder instance = new WorkOrder();
		instance.initialize();
		return instance;
	}

}
