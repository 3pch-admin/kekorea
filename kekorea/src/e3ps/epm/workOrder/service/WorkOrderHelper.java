package e3ps.epm.workOrder.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import e3ps.common.util.DateUtils;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.epm.KeDrawing.KeDrawing;
import e3ps.epm.KeDrawing.KeDrawingMaster;
import e3ps.epm.workOrder.WorkOrder;
import e3ps.epm.workOrder.beans.WorkOrderColumnData;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;

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

	public String getNextNumber(String param) throws Exception {
		String preFix = DateUtils.getTodayString();
		String number = param + "-" + preFix + "-";
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WorkOrder.class, true);

		SearchCondition sc = new SearchCondition(WorkOrder.class, WorkOrder.NUMBER, "LIKE", number.toUpperCase() + "%");
		query.appendWhere(sc, new int[] { idx });

		ClassAttribute attr = new ClassAttribute(WorkOrder.class, WorkOrder.NUMBER);
		OrderBy orderBy = new OrderBy(attr, true);
		query.appendOrderBy(orderBy, new int[] { idx });

		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WorkOrder workOrder = (WorkOrder) obj[0];

			String s = workOrder.getNumber().substring(workOrder.getNumber().lastIndexOf("-") + 1);

			int ss = Integer.parseInt(s) + 1;
			DecimalFormat d = new DecimalFormat("0000");
			number += d.format(ss);
		} else {
			number += "0001";
		}
		return number;
	}

	public Map<String, Object> getData(String number) throws Exception {
		Map<String, Object> map = new HashMap<>();
		boolean isKek = number.startsWith("K");
		QuerySpec query = new QuerySpec();

		if (isKek) {

		} else {
			int idx = query.appendClassList(KeDrawing.class, true);
			int idx_m = query.appendClassList(KeDrawingMaster.class, true);
			QuerySpecUtils.toInnerJoin(query, KeDrawing.class, KeDrawingMaster.class, "masterReference.key.id",
					WTAttributeNameIfc.ID_NAME, idx, idx_m);
			QuerySpecUtils.toBoolean(query, idx, KeDrawing.class, KeDrawing.LATEST, SearchCondition.IS_TRUE);
			QuerySpecUtils.toEqualsAnd(query, idx_m, KeDrawingMaster.class, KeDrawingMaster.KE_NUMBER, number);
			QueryResult result = PersistenceHelper.manager.find(query);
			if (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				KeDrawing keDrawing = (KeDrawing) obj[0];
				KeDrawingMaster master = (KeDrawingMaster) obj[1];
				map.put("name", master.getName());
				map.put("rev", keDrawing.getVersion());
				map.put("lotNo", master.getLotNo());
				map.put("current", keDrawing.getVersion());
				map.put("ok", true);
			}
		}
		return map;
	}
}
