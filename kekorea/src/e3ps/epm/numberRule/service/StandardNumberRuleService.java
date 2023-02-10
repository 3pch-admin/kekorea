package e3ps.epm.numberRule.service;

import java.util.ArrayList;
import java.util.Map;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.util.CommonUtils;
import e3ps.epm.numberRule.NumberRule;
import e3ps.epm.numberRule.NumberRuleMaster;
import wt.fc.PersistenceHelper;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardNumberRuleService extends StandardManager implements NumberRuleService {

	public static StandardNumberRuleService newStandardNumberRuleService() throws WTException {
		StandardNumberRuleService instance = new StandardNumberRuleService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(Map<String, Object> params) throws Exception {
		ArrayList<Map<String, Object>> addRows = (ArrayList<Map<String, Object>>) params.get("addRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (Map<String, Object> addRow : addRows) {
				String name = (String) addRow.get("name");
				String number = (String) addRow.get("number");
				String businessSector = (String) addRow.get("businessSector");
				String drawingCompany = (String) addRow.get("drawingCompany");
				String department = (String) addRow.get("department");
				String document = (String) addRow.get("document");
				int version = (int) addRow.get("version");

				CommonCode businessCode = CommonCodeHelper.manager.getCommonCode(businessSector, "BUSINESS_SECTOR");
				CommonCode companyCode = CommonCodeHelper.manager.getCommonCode(drawingCompany, "DRAWING_COMPANY");
				CommonCode departmentCode = CommonCodeHelper.manager.getCommonCode(department,
						"CLASSIFICATION_WRITING_DEPARTMENT");
				CommonCode documentCode = CommonCodeHelper.manager.getCommonCode(document, "WRITTEN_DOCUMENT");

				NumberRuleMaster master = NumberRuleMaster.newNumberRuleMaster();
				master.setNumber(number);
				master.setName(name);
				master.setSector(businessCode);
				master.setCompany(companyCode);
				master.setDepartment(departmentCode);
				master.setDocument(documentCode);
				master.setOwnership(CommonUtils.sessionOwner());
				PersistenceHelper.manager.save(master);

				NumberRule numberRule = NumberRule.newNumberRule();
				numberRule.setOwnership(CommonUtils.sessionOwner());
				numberRule.setVersion(version);
				numberRule.setLatest(true);
				numberRule.setMaster(master);
				PersistenceHelper.manager.save(numberRule);
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

	@Override
	public void revise(Map<String, Object> params) throws Exception {
		ArrayList<Map<String, Object>> addRows = (ArrayList<Map<String, Object>>) params.get("addRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (Map<String, Object> addRow : addRows) {
				String oid = (String) addRow.get("oid");
				String note = (String) addRow.get("note");

				NumberRule pre = (NumberRule) CommonUtils.getObject(oid); // 이전버전..
				pre.setLatest(false);
				pre = (NumberRule) PersistenceHelper.manager.modify(pre);

				NumberRule latest = NumberRule.newNumberRule();
				latest.setLatest(true);
				latest.setMaster(pre.getMaster());
				latest.setOwnership(CommonUtils.sessionOwner());
				latest.setVersion(pre.getVersion() + 1);
				latest.setNote(note);
				PersistenceHelper.manager.save(latest);
			}

			trs.commit();
			trs.rollback();
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
