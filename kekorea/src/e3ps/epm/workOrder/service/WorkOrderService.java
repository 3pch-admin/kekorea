package e3ps.epm.workOrder.service;

import java.util.Map;

import wt.method.RemoteInterface;

@RemoteInterface
public interface WorkOrderService {

	public abstract void create(Map<String, Object> params) throws Exception;

}
