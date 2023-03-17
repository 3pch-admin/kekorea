package e3ps.korea.history.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import e3ps.common.util.QuerySpecUtils;
import e3ps.korea.history.History;
import e3ps.korea.history.HistoryOptionLink;
import e3ps.project.Project;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;

public class HistoryHelper {

	public static final HistoryHelper manager = new HistoryHelper();
	public static final HistoryService service = ServiceFactory.getService(HistoryService.class);

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<Map<String, Object>> dataList = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(History.class, true);
		int idx_p = query.appendClassList(Project.class, true);

		SearchCondition sc = new SearchCondition(History.class, "projectReference.key.id", Project.class,
				WTAttributeNameIfc.ID_NAME);
		sc.setFromIndicies(new int[] { idx, idx_p }, 0);
		sc.setOuterJoin(1);
		query.appendWhere(sc, new int[] { idx, idx_p });

		QuerySpecUtils.toOrderBy(query, idx, History.class, History.CREATE_TIMESTAMP, false);
		QueryResult result = PersistenceHelper.manager.find(query);

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			History history = (History) obj[0];
			Project project = (Project) obj[1];
			ArrayList<HistoryOptionLink> data = getLinks(history);
			Map<String, Object> dataMap = new HashMap<>();
			dataMap.put("kekNumber", project.getKekNumber());
			dataMap.put("keNumber", project.getKeNumber());
			dataMap.put("install", project.getInstall() != null ? project.getInstall().getName() : "");
			dataMap.put("pdate", project.getPDate());
			dataMap.put("tuv", history != null ? history.getTuv() : "");
			dataMap.put("poid", project.getPersistInfo().getObjectIdentifier().getStringValue());
			dataMap.put("oid", history != null ? history.getPersistInfo().getObjectIdentifier().getStringValue() : "");
			for (HistoryOptionLink link : data) {
				String dataField = link.getDataField();
				String value = link.getOption().getCode();
				dataMap.put(dataField, value);
			}
			dataList.add(dataMap);
		}
		map.put("list", dataList);
		return map;
	}

	public ArrayList<HistoryOptionLink> getLinks(History history) throws Exception {
		ArrayList<HistoryOptionLink> list = new ArrayList<HistoryOptionLink>();
		if (history == null) {
			return list;
		}

		QueryResult result = PersistenceHelper.manager.navigate(history, "option", HistoryOptionLink.class, false);
		while (result.hasMoreElements()) {
			HistoryOptionLink link = (HistoryOptionLink) result.nextElement();
			list.add(link);
		}
		return list;
	}
}
