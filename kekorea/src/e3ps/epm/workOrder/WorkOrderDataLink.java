package e3ps.epm.workOrder;

import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.GeneratedRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.fc.ObjectToObjectLink;
import wt.fc.Persistable;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(name = "workOrder", type = WorkOrder.class),

		roleB = @GeneratedRole(name = "data", type = Persistable.class),

		properties = {

				@GeneratedProperty(name = "current", type = Integer.class, constraints = @PropertyConstraints(required = true), columnProperties = @ColumnProperties(columnName = "version")),

				@GeneratedProperty(name = "sort", type = Integer.class, constraints = @PropertyConstraints(required = true), initialValue = "1"),

				@GeneratedProperty(name = "dataType", type = String.class, constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "lotNo", type = Integer.class, constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "note", type = String.class, constraints = @PropertyConstraints(upperLimit = 2000))

		}

)

public class WorkOrderDataLink extends _WorkOrderDataLink {

	static final long serialVersionUID = 1;

	public static WorkOrderDataLink newWorkOrderDataLink(WorkOrder workOrder, Persistable persistable)
			throws WTException {
		WorkOrderDataLink instance = new WorkOrderDataLink();
		instance.initialize(workOrder, persistable);
		return instance;
	}
}
