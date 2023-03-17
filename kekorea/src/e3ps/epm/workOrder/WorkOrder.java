package e3ps.epm.workOrder;

import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.content.ContentHolder;
import wt.content.FormatContentHolder;
import wt.fc.Item;
import wt.fc.WTObject;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = Item.class, interfaces = { ContentHolder.class, Ownable.class },

		properties = {

				@GeneratedProperty(name = "number", type = String.class, javaDoc = "도면일람표 번호", constraints = @PropertyConstraints(required = true), columnProperties = @ColumnProperties(unique = true, columnName = "workOrderNumber")),

				@GeneratedProperty(name = "name", type = String.class, javaDoc = "도면일람표 이름", constraints = @PropertyConstraints(required = true), columnProperties = @ColumnProperties(index = true)),

				@GeneratedProperty(name = "description", type = String.class, javaDoc = "설명", constraints = @PropertyConstraints(upperLimit = 2000)),

				@GeneratedProperty(name = "state", type = String.class, javaDoc = "상태", constraints = @PropertyConstraints(required = true))

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
