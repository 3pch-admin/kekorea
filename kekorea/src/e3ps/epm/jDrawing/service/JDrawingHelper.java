package e3ps.epm.jDrawing.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import e3ps.admin.sheetvariable.Category;
import e3ps.admin.sheetvariable.CategoryItemsLink;
import e3ps.admin.sheetvariable.beans.CategoryColumnData;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.epm.jDrawing.JDrawing;
import e3ps.epm.jDrawing.beans.JDrawingColumnData;
import wt.fc.PagingQueryResult;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;

public class JDrawingHelper {

	public static final JDrawingHelper manager = new JDrawingHelper();
	public static final JDrawingService service = ServiceFactory.getService(JDrawingService.class);

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		ArrayList<JDrawingColumnData> list = new ArrayList<>();
		Map<String, Object> map = new HashMap<String, Object>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(JDrawing.class, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			JDrawing jDrawing = (JDrawing) obj[0];
			JDrawingColumnData column = new JDrawingColumnData(jDrawing);
			list.add(column);
		}

		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}
}