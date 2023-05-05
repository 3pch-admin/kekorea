package e3ps.admin.numberRuleCode.service;

import java.util.HashMap;
import java.util.List;

import e3ps.admin.numberRuleCode.NumberRuleCode;
import e3ps.admin.numberRuleCode.NumberRuleCodeType;
import e3ps.admin.numberRuleCode.dto.NumberRuleCodeDTO;
import e3ps.common.util.CommonUtils;
import wt.fc.PersistenceHelper;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardNumberRuleCodeService extends StandardManager implements NumberRuleCodeService {

	public static StandardNumberRuleCodeService newStandardNumberRuleCodeService() throws WTException {
		StandardNumberRuleCodeService instance = new StandardNumberRuleCodeService();
		instance.initialize();
		return instance;
	}

	@Override
	public void save(HashMap<String, List<NumberRuleCodeDTO>> dataMap) throws Exception {
		List<NumberRuleCodeDTO> addRows = dataMap.get("addRows");
		List<NumberRuleCodeDTO> editRows = dataMap.get("editRows");
		List<NumberRuleCodeDTO> removeRows = dataMap.get("removeRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (NumberRuleCodeDTO dto : addRows) {
				String name = dto.getName();
				String code = dto.getCode();
				String codeType = dto.getCodeType();
				String description = dto.getDescription();
				boolean enable = dto.isEnable();
				int sort = dto.getSort();

				NumberRuleCode numberRuleCode = NumberRuleCode.newNumberRuleCode();
				numberRuleCode.setName(name);
				numberRuleCode.setDescription(description);
				numberRuleCode.setEnable(enable);
				numberRuleCode.setCodeType(NumberRuleCodeType.toNumberRuleCodeType(codeType));
				numberRuleCode.setCode(code);
				numberRuleCode.setSort(sort);
				PersistenceHelper.manager.save(numberRuleCode);
			}

			for (NumberRuleCodeDTO dto : removeRows) {
				String oid = dto.getOid();
				NumberRuleCode numberRuleCode = (NumberRuleCode) CommonUtils.getObject(oid);
				PersistenceHelper.manager.delete(numberRuleCode);
			}

			for (NumberRuleCodeDTO dto : editRows) {
				String oid = dto.getOid();
				String name = dto.getName();
				String code = dto.getCode();
				String codeType = dto.getCodeType();
				String description = dto.getDescription();
				boolean enable = dto.isEnable();
				int sort = dto.getSort();

				NumberRuleCode numberRuleCode = (NumberRuleCode) CommonUtils.getObject(oid);
				numberRuleCode.setName(name);
				numberRuleCode.setDescription(description);
				numberRuleCode.setEnable(enable);
				numberRuleCode.setCodeType(NumberRuleCodeType.toNumberRuleCodeType(codeType));
				numberRuleCode.setCode(code);
				numberRuleCode.setSort(sort);
				PersistenceHelper.manager.modify(numberRuleCode);
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
