package e3ps.korea.history.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import e3ps.common.util.CommonUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.korea.history.History;
import e3ps.korea.history.HistorySpecLink;
import e3ps.project.Project;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;

public class HistoryHelper {

	public static final HistoryHelper manager = new HistoryHelper();
	public static final HistoryService service = ServiceFactory.getService(HistoryService.class);

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<Map<String, Object>> dataList = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(History.class, true);

		QuerySpecUtils.toOrderBy(query, idx, History.class, History.CREATE_TIMESTAMP, true);

		QueryResult result = PersistenceHelper.manager.find(query);

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			History history = (History) obj[0];
			Project project = history.getProject();
			ArrayList<HistorySpecLink> data = getLinks(history);
			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("kekNumber", project.getKekNumber());
			dataMap.put("keNumber", project.getKeNumber());
			dataMap.put("install", project.getInstall().getName());
			dataMap.put("pDate", CommonUtils.getPersistableTime(project.getCreateTimestamp()));
			dataMap.put("tuv", history.getTuv());
			dataMap.put("poid", project.getPersistInfo().getObjectIdentifier().getStringValue());
			dataMap.put("oid", history.getPersistInfo().getObjectIdentifier().getStringValue());
			for (HistorySpecLink link : data) {
				String columnKey = link.getSpec().getColumnKey();
				String value = link.getValue();
				dataMap.put(columnKey, value);
			}
			dataList.add(dataMap);
		}
		map.put("list", dataList);
		return map;
	}

	public ArrayList<HistorySpecLink> getLinks(History history) throws Exception {
		ArrayList<HistorySpecLink> list = new ArrayList<HistorySpecLink>();

		QueryResult result = PersistenceHelper.manager.navigate(history, "spec", HistorySpecLink.class, false);
		while (result.hasMoreElements()) {
			HistorySpecLink link = (HistorySpecLink) result.nextElement();
			list.add(link);
		}
		return list;
	}

	public ArrayList<Map<String, Object>> view(String oid) throws Exception {
		Project project = (Project) CommonUtils.getObject(oid);
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(History.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, History.class, "projectReference.key.id",
				project.getPersistInfo().getObjectIdentifier().getId());
		QuerySpecUtils.toOrderBy(query, idx, History.class, History.CREATE_TIMESTAMP, true);
		QueryResult result = PersistenceHelper.manager.find(query);

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			History history = (History) obj[0];
			ArrayList<HistorySpecLink> data = getLinks(history);
			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("kekNumber", project.getKekNumber());
			dataMap.put("keNumber", project.getKeNumber());
			dataMap.put("install", project.getInstall().getName());
			dataMap.put("pDate", project.getPDate());
			dataMap.put("tuv", history.getTuv());
			dataMap.put("poid", project.getPersistInfo().getObjectIdentifier().getStringValue());
			dataMap.put("oid", history.getPersistInfo().getObjectIdentifier().getStringValue());
			for (HistorySpecLink link : data) {
				String columnKey = link.getSpec().getColumnKey();
				String value = link.getValue();
				dataMap.put(columnKey, value);
			}
			list.add(dataMap);
		}
		return list;
	}
}
