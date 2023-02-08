package e3ps.korea.cssheet.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import e3ps.common.util.PageQueryUtils;
import e3ps.epm.numberRule.beans.NumberRuleColumnData;
import e3ps.korea.cssheet.CSSheet;
import e3ps.korea.cssheet.beans.CSSheetColumnData;
import wt.fc.PagingQueryResult;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;

public class CSSheetHelper {

	public static final CSSheetHelper manager = new CSSheetHelper();
	public static final CSSheetService service = ServiceFactory.getService(CSSheetService.class);

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<CSSheetColumnData> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(CSSheet.class, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			CSSheet csSheet = (CSSheet) obj[0];
			CSSheetColumnData column = new CSSheetColumnData(csSheet);
			list.add(column);
		}

		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}
}
