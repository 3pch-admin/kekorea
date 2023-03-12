package e3ps.korea.history.service;

import java.util.ArrayList;
import java.util.Map;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.util.CommonUtils;
import e3ps.korea.history.History;
import e3ps.korea.history.HistoryOptionLink;
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
	public void save(Map<String, ArrayList<Map<String, String>>> params) throws Exception {
		ArrayList<Map<String, String>> addRows = (ArrayList<Map<String, String>>) params.get("addRows");
		ArrayList<Map<String, String>> removeRows = (ArrayList<Map<String, String>>) params.get("removeRows");
		ArrayList<Map<String, String>> editRows = (ArrayList<Map<String, String>>) params.get("editRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (Map<String, String> addRow : addRows) {
				String tuv = addRow.get("tuv");
				String poid = addRow.get("poid");

				Project project = (Project) CommonUtils.getObject(poid);

				History history = History.newHistory();
				history.setTuv(tuv);
				history.setProject(project);
				PersistenceHelper.manager.save(history);

				// 어차피 뱅그르르르...
				ArrayList<Map<String, String>> headers = CommonCodeHelper.manager.getArrayKeyValueMap("SPEC");
				for (Map<String, String> header : headers) {
					String dataField = header.get("key");
					String code = addRow.get(header.get("key")); // option value....
					CommonCode optionCode = CommonCodeHelper.manager.getCommonCode(code, "OPTION");
					HistoryOptionLink link = HistoryOptionLink.newHistoryOptionLink(history, optionCode);
					link.setDataField(dataField);
					PersistenceHelper.manager.save(link);
				}
			}

			for (Map<String, String> removeRow : removeRows) {

			}
			for (Map<String, String> editRow : editRows) {

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
