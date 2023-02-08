package e3ps.korea.history.service;

import java.util.ArrayList;
import java.util.Map;

import e3ps.admin.spec.Spec;
import e3ps.admin.spec.service.SpecHelper;
import e3ps.common.util.CommonUtils;
import e3ps.korea.history.History;
import e3ps.korea.history.HistorySpecLink;
import e3ps.project.Project;
import wt.fc.PersistenceHelper;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardHistoryService extends StandardManager implements HistoryService {

	public static StandardHistoryService newStandardHistoryService() throws WTException {
		StandardHistoryService instance = new StandardHistoryService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(Map<String, Object> params) throws Exception {
		ArrayList<Map<String, Object>> addRows = (ArrayList<Map<String, Object>>) params.get("addRows");
		ArrayList<Map<String, Object>> removeRows = (ArrayList<Map<String, Object>>) params.get("removeRows");
		ArrayList<Map<String, Object>> editRows = (ArrayList<Map<String, Object>>) params.get("editRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (Map<String, Object> addRow : addRows) {
				String poid = (String) addRow.get("poid");
				String tuv = (String) addRow.get("tuv");

				Project project = (Project) CommonUtils.getObject(poid);

				History history = History.newHistory();
				history.setProject(project);
				history.setTuv(tuv);

				PersistenceHelper.manager.save(history);

				ArrayList<Spec> list = SpecHelper.manager.getSpecArray();
				for (Spec spec : list) {
					String value = (String) addRow.get(spec.getColumnKey());
					HistorySpecLink link = HistorySpecLink.newHistorySpecLink(history, spec);
					link.setValue(value);
					PersistenceHelper.manager.save(link);
				}
			}

			for (Map<String, Object> removeRow : removeRows) {
				String oid = (String) removeRow.get("oid");
				History history = (History) CommonUtils.getObject(oid);
				// 기존 링크 삭제
				ArrayList<HistorySpecLink> links = HistoryHelper.manager.getLinks(history);
				for (HistorySpecLink link : links) {
					PersistenceHelper.manager.delete(link);
				}
				PersistenceHelper.manager.delete(history);
			}

			for (Map<String, Object> editRow : editRows) {
				String poid = (String) editRow.get("poid");
				String tuv = (String) editRow.get("tuv");
				String oid = (String) editRow.get("oid");

				Project project = (Project) CommonUtils.getObject(poid);

				History history = (History) CommonUtils.getObject(oid);
				history.setProject(project);
				history.setTuv(tuv);
				PersistenceHelper.manager.modify(history);

				// 기존 링크 삭제
				ArrayList<HistorySpecLink> links = HistoryHelper.manager.getLinks(history);
				for (HistorySpecLink link : links) {
					PersistenceHelper.manager.delete(link);
				}

				ArrayList<Spec> list = SpecHelper.manager.getSpecArray();
				for (Spec spec : list) {
					String value = (String) editRow.get(spec.getColumnKey());
					HistorySpecLink link = HistorySpecLink.newHistorySpecLink(history, spec);
					link.setValue(value);
					PersistenceHelper.manager.save(link);
				}
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
