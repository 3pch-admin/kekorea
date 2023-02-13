package e3ps.korea.cip.service;

import java.util.ArrayList;
import java.util.Map;

import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.ContentUtils;
import e3ps.common.util.StringUtils;
import e3ps.korea.cip.Cip;
import wt.fc.PersistenceHelper;
import wt.pom.Transaction;
import wt.services.StandardManager;

public class StandardCipService extends StandardManager implements CipService {

	public static StandardCipService newStandardCipService() throws Exception {
		StandardCipService instance = new StandardCipService();
		return instance;
	}

	@Override
	public void create(Map<String, Object> params) throws Exception {
		ArrayList<Map<String, Object>> addRows = (ArrayList<Map<String, Object>>) params.get("addRows");
		ArrayList<Map<String, Object>> editRows = (ArrayList<Map<String, Object>>) params.get("editRows");
		ArrayList<Map<String, Object>> removeRows = (ArrayList<Map<String, Object>>) params.get("removeRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (Map<String, Object> addRow : addRows) {
				String item = (String) addRow.get("item");
				String improvements = (String) addRow.get("improvements");
				String improvement = (String) addRow.get("improvement");
				String apply = (String) addRow.get("apply");
				String note = (String) addRow.get("note");
				String mak = (String) addRow.get("mak");
				String detail = (String) addRow.get("detail");
				String customer = (String) addRow.get("customer");
				String install = (String) addRow.get("install");
				String preViewPath = (String) addRow.get("preViewPath");
				ArrayList<String> secondaryPaths = (ArrayList<String>) addRow.get("secondaryPaths");

				Cip cip = Cip.newCip();
				cip.setOwnership(CommonUtils.sessionOwner());
				cip.setItem(item);
				cip.setImprovement(improvement);
				cip.setImprovements(improvements);
				cip.setApply(apply);
				cip.setNote(note);
				cip.setMak(CommonCodeHelper.manager.getCommonCode(mak, "MAK"));
				cip.setDetail(CommonCodeHelper.manager.getCommonCode(detail, "MAK_DETAIL"));
				cip.setCustomer(CommonCodeHelper.manager.getCommonCode(customer, "CUSTOMER"));
				cip.setInstall(CommonCodeHelper.manager.getCommonCode(install, "INSTALL"));
				PersistenceHelper.manager.save(cip);

				if (!StringUtils.isNull(preViewPath)) {
					ContentUtils.savePrimary(cip, preViewPath);
				}

				if (secondaryPaths != null && secondaryPaths.size() > 0) {
					ContentUtils.saveSecondary(cip, secondaryPaths);
				}

			}

			for (Map<String, Object> removeRow : removeRows) {
				String oid = (String) removeRow.get("oid");
				Cip cip = (Cip) CommonUtils.getObject(oid);
				PersistenceHelper.manager.delete(cip);
			}

			for (Map<String, Object> editRow : editRows) {
				String item = (String) editRow.get("item");
				String improvements = (String) editRow.get("improvements");
				String improvement = (String) editRow.get("improvement");
				String apply = (String) editRow.get("apply");
				String note = (String) editRow.get("note");
				String mak = (String) editRow.get("mak");
				String detail = (String) editRow.get("detail");
				String customer = (String) editRow.get("customer");
				String install = (String) editRow.get("install");
				String oid = (String) editRow.get("oid");
				String preViewPath = (String) editRow.get("preViewPath");
				ArrayList<String> secondaryPaths = (ArrayList<String>) editRow.get("secondaryPaths");

				Cip cip = (Cip) CommonUtils.getObject(oid);
				cip.setItem(item);
				cip.setImprovement(improvement);
				cip.setImprovements(improvements);
				cip.setApply(apply);
				cip.setNote(note);
				cip.setMak(CommonCodeHelper.manager.getCommonCode(mak, "MAK"));
				cip.setDetail(CommonCodeHelper.manager.getCommonCode(detail, "MAK_DETAIL"));
				cip.setCustomer(CommonCodeHelper.manager.getCommonCode(customer, "CUSTOMER"));
				cip.setInstall(CommonCodeHelper.manager.getCommonCode(install, "INSTALL"));
				PersistenceHelper.manager.modify(cip);
				if (!StringUtils.isNull(preViewPath)) {
					ContentUtils.savePrimary(cip, preViewPath);
				}

				if (secondaryPaths != null && secondaryPaths.size() > 0) {
					ContentUtils.saveSecondary(cip, secondaryPaths);
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
