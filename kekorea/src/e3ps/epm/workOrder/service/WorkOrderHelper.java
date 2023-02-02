package e3ps.epm.workOrder.service;

import wt.services.ServiceFactory;

public class WorkOrderHelper {

	public static final WorkOrderHelper manager = new WorkOrderHelper();
	public static final WorkOrderService service = ServiceFactory.getService(WorkOrderService.class);
}
