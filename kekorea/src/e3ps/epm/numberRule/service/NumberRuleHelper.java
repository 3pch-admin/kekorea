package e3ps.epm.numberRule.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.epm.numberRule.NumberRule;
import e3ps.epm.numberRule.beans.NumberRuleColumnData;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;

public class NumberRuleHelper {

	public static final NumberRuleHelper manager = new NumberRuleHelper();
	public static final NumberRuleService service = ServiceFactory.getService(NumberRuleService.class);

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<NumberRuleColumnData> list = new ArrayList<NumberRuleColumnData>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(NumberRule.class, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			NumberRule numberRule = (NumberRule)obj[0];
			NumberRuleColumnData column = new NumberRuleColumnData(numberRule);
			list.add(column);
		}

		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	public void last(String number) throws Exception {
		
		QuerySpec query = new QuerySpec(); 
		int idx = query.appendClassList(NumberRule.class, true);
		QuerySpecUtils.toLikeRight(query, idx, NumberRule.class, NumberRule.NUMBER, number);
		QuerySpecUtils.toOrderBy(query, idx, NumberRule.class, NumberRule.NUMBER, true);
		QueryResult result = PersistenceHelper.manager.find(query);
		if(result.hasMoreElements()) {
			
		}
		
		
	}
}
