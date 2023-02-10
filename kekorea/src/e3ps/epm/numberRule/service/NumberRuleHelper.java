package e3ps.epm.numberRule.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.epm.numberRule.NumberRule;
import e3ps.epm.numberRule.NumberRuleMaster;
import e3ps.epm.numberRule.beans.NumberRuleColumnData;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;

public class NumberRuleHelper {

	public static final NumberRuleHelper manager = new NumberRuleHelper();
	public static final NumberRuleService service = ServiceFactory.getService(NumberRuleService.class);

	public static final String[] alphabet = new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L",
			"M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<NumberRuleColumnData> list = new ArrayList<NumberRuleColumnData>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(NumberRule.class, true);

		QuerySpecUtils.toBoolean(query, idx, NumberRule.class, NumberRule.LATEST, SearchCondition.IS_TRUE);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			NumberRule numberRule = (NumberRule) obj[0];
			NumberRuleColumnData column = new NumberRuleColumnData(numberRule);
			list.add(column);
		}

		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	public Map<String, Object> last(String number) throws Exception {
		Map<String, Object> map = new HashMap<>();
		DecimalFormat df = new DecimalFormat("00000");
		String seq1 = "A";
		String seq2 = df.format(00001);
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(NumberRuleMaster.class, true);
		QuerySpecUtils.toLikeRight(query, idx, NumberRuleMaster.class, NumberRuleMaster.NUMBER, number);
		QuerySpecUtils.toOrderBy(query, idx, NumberRuleMaster.class, NumberRuleMaster.NUMBER, true);
		QueryResult result = PersistenceHelper.manager.find(query);
		String next = "00001";
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			NumberRuleMaster rule = (NumberRuleMaster) obj[0];
			seq1 = rule.getNumber().substring(3, 4);
			seq2 = df.format(Integer.parseInt(rule.getNumber().substring(4, 9))); // 00001
			next = seq1 + df.format(Integer.parseInt(rule.getNumber().substring(4, 9)) + 1); // 00001
			int pos = 0;
			for (int i = 0; i < alphabet.length; i++) {
				if (seq1.equals(alphabet[i])) {
					pos = i;
					break;
				}
			}
			if (Integer.parseInt(seq2) == 99999) {
				next = alphabet[pos + 1] + df.format(00001);
			}
		}
		map.put("next", next);
		map.put("last", number + seq1 + seq2);
		return map;
	}
}
