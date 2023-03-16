package e3ps.korea.history.service;

import java.util.ArrayList;
import java.util.Map;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.korea.history.History;
import e3ps.korea.history.HistoryOptionLink;
import e3ps.project.Project;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.services.StandardManager;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;

public class StandardHistoryService extends StandardManager implements HistoryService {

	public static StandardHistoryService newStandardHistoryService() throws WTException {
		StandardHistoryService instance = new StandardHistoryService();
		instance.initialize();
		return instance;
	}

	@Override
	public void save(Map<String, ArrayList<Map<String, String>>> params) throws Exception {
		ArrayList<Map<String, String>> removeRows = (ArrayList<Map<String, String>>) params.get("removeRows");
		ArrayList<Map<String, String>> editRows = (ArrayList<Map<String, String>>) params.get("editRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (Map<String, String> removeRow : removeRows) {
				String oid = removeRow.get("oid");
				History history = (History) CommonUtils.getObject(oid);
				PersistenceHelper.manager.delete(history);
			}

			for (Map<String, String> editRow : editRows) {
				String tuv = editRow.get("tuv");
				String poid = editRow.get("poid");

				Project project = (Project) CommonUtils.getObject(poid);

				QuerySpec query = new QuerySpec();
				int idx = query.appendClassList(Project.class, true);
				int idx_h = query.appendClassList(History.class, true);

				QuerySpecUtils.toInnerJoin(query, Project.class, History.class, WTAttributeNameIfc.ID_NAME,
						"projectReference.key.id", idx, idx_h);
				QuerySpecUtils.toEqualsAnd(query, idx_h, History.class, "projectReference.key.id",
						project.getPersistInfo().getObjectIdentifier().getId());
				;
				QueryResult qr = PersistenceHelper.manager.find(query);
				History history = null;
				if (qr.hasMoreElements()) {
					Object[] obj = (Object[]) qr.nextElement();
					history = (History) obj[1];
					history.setTuv(tuv);
					PersistenceHelper.manager.modify(history);
				} else {
					history = History.newHistory();
					history.setTuv(tuv);
					history.setProject(project);
					PersistenceHelper.manager.save(history);
				}
				// 어차피 뱅그르르르...
				ArrayList<Map<String, String>> headers = CommonCodeHelper.manager.getArrayKeyValueMap("SPEC");
				for (Map<String, String> header : headers) {
					String dataField = header.get("key");
					String code = editRow.get(header.get("key")); // option value....

					if (!StringUtils.isNull(code)) {
						System.out.println("code=" + code);
						CommonCode optionCode = CommonCodeHelper.manager.getCommonCode(code, "OPTION");

						QueryResult result = PersistenceHelper.manager.navigate(optionCode, "history",
								HistoryOptionLink.class, false);
						if (result.hasMoreElements()) {
							HistoryOptionLink link = (HistoryOptionLink) result.nextElement();
							link.setDataField(dataField);
							PersistenceHelper.manager.modify(link);
						} else {
							HistoryOptionLink link = HistoryOptionLink.newHistoryOptionLink(history, optionCode);
							link.setDataField(dataField);
							PersistenceHelper.manager.save(link);
						}
					}
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
