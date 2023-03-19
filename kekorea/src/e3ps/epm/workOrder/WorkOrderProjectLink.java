package e3ps.epm.workOrder;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.GeneratedRole;

import e3ps.project.Project;
import wt.fc.ObjectToObjectLink;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(name = "workOrder", type = WorkOrder.class),

		roleB = @GeneratedRole(name = "project", type = Project.class),

		properties = {

				@GeneratedProperty(name = "sort", type = Integer.class, javaDoc = "정렬", initialValue = "1")

		}

)
public class WorkOrderProjectLink extends _WorkOrderProjectLink {

	static final long serialVersionUID = 1;

	public static WorkOrderProjectLink newWorkOrderProjectLink(WorkOrder workOrder, Project project)
			throws WTException {
		WorkOrderProjectLink instance = new WorkOrderProjectLink();
		instance.initialize(workOrder, project);
		return instance;
	}

}
