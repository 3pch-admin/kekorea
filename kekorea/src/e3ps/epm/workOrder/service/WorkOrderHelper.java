package e3ps.epm.workOrder.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import e3ps.common.util.PageQueryUtils;
import e3ps.epm.jDrawing.JDrawing;
import e3ps.epm.jDrawing.beans.JDrawingColumnData;
import e3ps.epm.workOrder.WorkOrder;
import e3ps.epm.workOrder.beans.WorkOrderColumnData;
import wt.fc.PagingQueryResult;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;

public class WorkOrderHelper {

	public static final WorkOrderHelper manager = new WorkOrderHelper();
	public static final WorkOrderService service = ServiceFactory.getService(WorkOrderService.class);
	
	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		ArrayList<WorkOrderColumnData> list = new ArrayList<>();
		Map<String, Object> map = new HashMap<String, Object>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WorkOrder.class, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WorkOrder workOrder = (WorkOrder) obj[0];
			WorkOrderColumnData column = new WorkOrderColumnData(workOrder);
			list.add(column);
		}

		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}
}
