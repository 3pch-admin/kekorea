package e3ps.epm.workOrder;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.content.ContentHolder;
import wt.fc.Item;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = Item.class, interfaces = { ContentHolder.class, Ownable.class },

		properties = {

				@GeneratedProperty(name = "version", type = Integer.class, javaDoc = "버전", initialValue = "1", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "latest", type = Boolean.class, javaDoc = "최신여부", initialValue = "true", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "description", type = String.class, javaDoc = "설명", constraints = @PropertyConstraints(upperLimit = 2000)),

				@GeneratedProperty(name = "state", type = String.class, javaDoc = "상태", constraints = @PropertyConstraints(required = true))

		},

		foreignKeys = {

				@GeneratedForeignKey(name = "WorkOrderMasterLink",

						foreignKeyRole = @ForeignKeyRole(name = "master", type = WorkOrderMaster.class,

								constraints = @PropertyConstraints(required = true)),

						myRole = @MyRole(name = "iteration", cardinality = Cardinality.ONE)

				)

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
