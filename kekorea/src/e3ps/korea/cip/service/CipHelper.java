package e3ps.korea.cip.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.common.util.PageQueryUtils;
import e3ps.korea.cip.beans.CipColumnData;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;

public class CipHelper {

	public static final CipHelper manager = new CipHelper();
	public static final CipService service = ServiceFactory.getService(CipService.class);
	
	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		List<CipColumnData> list = new ArrayList<CipColumnData>();
		
		map.put("list", list);
		
		QuerySpec query = new QuerySpec();
		PageQueryUtils pager = new PageQueryUtils(params, query);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}
	
//	public Map<String, Object> create(Map<String, Object> params) throws Exception {
//		Map<String, Object> map = new HashMap<String, Object>();
////		List<CipColumnData> list = new ArrayList<CipColumnData>();
//		
////		map.put("list", list);
//		
//		QuerySpec query = new QuerySpec();
//		PageQueryUtils pager = new PageQueryUtils(params, query);
//		map.put("sessionid", pager.getSessionId());
//		map.put("curPage", pager.getCpage());
//		return map;
//	}
}
