package e3ps.korea.configSheet.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import e3ps.common.util.PageQueryUtils;
import e3ps.korea.configSheet.ConfigSheet;
import e3ps.korea.configSheet.ConfigSheetDTO;
import wt.fc.PagingQueryResult;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;

public class ConfigSheetHelper {

	public static final ConfigSheetHelper manager = new ConfigSheetHelper();
	public static final ConfigSheetService service = ServiceFactory.getService(ConfigSheetService.class);

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<ConfigSheetDTO> list = new ArrayList<>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ConfigSheet.class, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ConfigSheet configSheet = (ConfigSheet) obj[0];
			ConfigSheetDTO column = new ConfigSheetDTO(configSheet);
			list.add(column);
		}
		map.put("list", list);
		map.put("curPage", pager.getCpage());
		map.put("sessionid", pager.getSessionId());
		return map;
	}
}
