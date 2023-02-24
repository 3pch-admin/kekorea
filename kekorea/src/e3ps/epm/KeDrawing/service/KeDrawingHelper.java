package e3ps.epm.KeDrawing.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.epm.KeDrawing.KeDrawing;
import e3ps.epm.KeDrawing.KeDrawingMaster;
import e3ps.epm.KeDrawing.beans.KeDrawingColumnData;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;

public class KeDrawingHelper {

	public static final KeDrawingHelper manager = new KeDrawingHelper();
	public static final KeDrawingService service = ServiceFactory.getService(KeDrawingService.class);

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		ArrayList<KeDrawingColumnData> list = new ArrayList<>();
		Map<String, Object> map = new HashMap<String, Object>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(KeDrawing.class, true);

		QuerySpecUtils.toOrderBy(query, idx, KeDrawing.class, KeDrawing.CREATE_TIMESTAMP, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			KeDrawing keDrawing = (KeDrawing) obj[0];
			KeDrawingColumnData column = new KeDrawingColumnData(keDrawing);
			list.add(column);
		}

		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	public boolean isLast(KeDrawingMaster master) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(KeDrawing.class, true);
		int idx_m = query.appendClassList(KeDrawingMaster.class, false);
		QuerySpecUtils.toInnerJoin(query, KeDrawing.class, KeDrawingMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);
		QuerySpecUtils.toEqualsAnd(query, idx, KeDrawing.class, "masterReference.key.id",
				master.getPersistInfo().getObjectIdentifier().getId());
		QueryResult result = PersistenceHelper.manager.find(query);
		return result.size() == 1 ? true : false;
	}
}
