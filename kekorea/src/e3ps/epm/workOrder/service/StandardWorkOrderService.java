package e3ps.epm.workOrder.service;

import java.util.ArrayList;
import java.util.Map;

import e3ps.common.util.CommonUtils;
import e3ps.epm.workOrder.WorkOrder;
import e3ps.epm.workOrder.WorkOrderDataLink;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardWorkOrderService extends StandardManager implements WorkOrderService {

	public static StandardWorkOrderService newStandardWorkOrderService() throws WTException {
		StandardWorkOrderService instance = new StandardWorkOrderService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(Map<String, Object> params) throws Exception {
		String name = (String) params.get("name");
		ArrayList<Map<String, Object>> addRows = (ArrayList<Map<String, Object>>) params.get("addRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			WorkOrder workOrder = WorkOrder.newWorkOrder();
			workOrder.setName(name);
			workOrder.setNumber(WorkOrderHelper.manager.getNextNumber("WORK-"));
			workOrder.setOwnership(CommonUtils.sessionOwner());

			PersistenceHelper.manager.save(workOrder);

			for (Map<String, Object> addRow : addRows) {
				String oid = (String) addRow.get("oid");
				int current = (int) addRow.get("current");
				Persistable persistable = (Persistable) CommonUtils.getObject(oid);
				WorkOrderDataLink link = WorkOrderDataLink.newWorkOrderDataLink(workOrder, persistable);
				link.setCurrent(current);
				PersistenceHelper.manager.save(link);
			}

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}

	}

}
