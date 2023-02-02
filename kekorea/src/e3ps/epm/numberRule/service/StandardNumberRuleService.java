package e3ps.epm.numberRule.service;

import java.util.ArrayList;
import java.util.Map;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.util.CommonUtils;
import e3ps.epm.numberRule.NumberRule;
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

				CommonCode businessCode = CommonCodeHelper.manager.getCommonCode(businessSector, "BUSINESS_SECTOR");
				CommonCode companyCode = CommonCodeHelper.manager.getCommonCode(drawingCompany, "DRAWING_COMPANY");
				CommonCode departmentCode = CommonCodeHelper.manager.getCommonCode(document, "WRITTEN_DOCUMENT");
				CommonCode documentCode = CommonCodeHelper.manager.getCommonCode(department,
						"CLASSIFICATION_WRITING_DEPARTMENT");

				NumberRule numberRule = NumberRule.newNumberRule();
				numberRule.setOwnership(CommonUtils.sessionOwner());
				numberRule.setNumber(number);
				numberRule.setName(name);
				numberRule.setBusinessSector(businessCode);
				numberRule.setDrawingCompany(companyCode);
				numberRule.setDepartment(departmentCode);
				numberRule.setDocument(documentCode);

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
}
