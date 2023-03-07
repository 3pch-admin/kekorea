package e3ps.epm.workOrder.service;

import java.util.Hashtable;

import e3ps.epm.workOrder.dto.WorkOrderDTO;
import wt.method.RemoteInterface;

@RemoteInterface
public interface WorkOrderService {

	public abstract void create(WorkOrderDTO dto) throws Exception;

}
