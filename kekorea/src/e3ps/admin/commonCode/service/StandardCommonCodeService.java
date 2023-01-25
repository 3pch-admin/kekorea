package e3ps.admin.commonCode.service;

import java.util.ArrayList;
import java.util.Map;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.CommonCodeType;
import e3ps.common.util.CommonUtils;
import wt.fc.PersistenceHelper;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardCommonCodeService extends StandardManager implements CommonCodeService {

	public static StandardCommonCodeService newStandardCommonCodeService() throws WTException {
		StandardCommonCodeService instance = new StandardCommonCodeService();
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
				String name = (String) addRow.get("name");
				String code = (String) addRow.get("code");
				String codeType = (String) addRow.get("codeType");
				String description = (String) addRow.get("description");
				String uses = (String) addRow.get("uses");

				CommonCode commonCode = CommonCode.newCommonCode();
				commonCode.setName(name);
				commonCode.setCode(code);
				commonCode.setCodeType(CommonCodeType.toCommonCodeType(codeType));
				commonCode.setDescription(description);
				commonCode.setEnable(true);
				PersistenceHelper.manager.save(commonCode);
			}

			for (Map<String, Object> removeRow : removeRows) {
				String oid = (String) removeRow.get("oid");
				CommonCode commonCode = (CommonCode) CommonUtils.getObject(oid);
				PersistenceHelper.manager.delete(commonCode);
			}

			for (Map<String, Object> editRow : editRows) {
				String name = (String) editRow.get("name");
				String code = (String) editRow.get("code");
				String codeType = (String) editRow.get("codeType");
				String description = (String) editRow.get("description");
				String uses = (String) editRow.get("uses");
				String oid = (String) editRow.get("oid");

				CommonCode commonCode = (CommonCode) CommonUtils.getObject(oid);
				commonCode.setName(name);
				commonCode.setCode(code);
				commonCode.setCodeType(CommonCodeType.toCommonCodeType(codeType));
				commonCode.setDescription(description);
				commonCode.setEnable(true);
				PersistenceHelper.manager.modify(commonCode);
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
