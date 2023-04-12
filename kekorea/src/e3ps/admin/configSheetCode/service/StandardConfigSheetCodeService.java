package e3ps.admin.configSheetCode.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import e3ps.admin.configSheetCode.ConfigSheetCode;
import e3ps.admin.configSheetCode.ConfigSheetCodeType;
import e3ps.admin.configSheetCode.dto.ConfigSheetCodeDTO;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.StringUtils;
import wt.fc.PersistenceHelper;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardConfigSheetCodeService extends StandardManager implements ConfigSheetCodeService {

	public static StandardConfigSheetCodeService newStandardConfigSheetCodeService() throws WTException {
		StandardConfigSheetCodeService instance = new StandardConfigSheetCodeService();
		instance.initialize();
		return instance;
	}

	@Override
	public void save(HashMap<String, List<ConfigSheetCodeDTO>> dataMap) throws Exception {
		List<ConfigSheetCodeDTO> addRows = dataMap.get("addRows");
		List<ConfigSheetCodeDTO> editRows = dataMap.get("editRows");
		List<ConfigSheetCodeDTO> removeRows = dataMap.get("removeRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (ConfigSheetCodeDTO dto : addRows) {
				String name = dto.getName();
				String code = dto.getCode();
				String codeType = dto.getCodeType();
				String description = dto.getDescription();
				boolean enable = dto.isEnable();
				int sort = dto.getSort();
				String _$parent = dto.get_$parent();
				ArrayList<ConfigSheetCodeDTO> children = dto.getChildren();

				ConfigSheetCode parent = null;
				if (!StringUtils.isNull(_$parent) && _$parent.indexOf("ConfigSheetCode") > -1) {
					parent = (ConfigSheetCode) CommonUtils.getObject(_$parent);
				}

				ConfigSheetCode configSheetCode = ConfigSheetCode.newConfigSheetCode();
				configSheetCode.setName(name);
				configSheetCode.setCode(code);
				configSheetCode.setCodeType(ConfigSheetCodeType.toConfigSheetCodeType(codeType));
				configSheetCode.setDescription(description);
				configSheetCode.setSort(sort);
				configSheetCode.setParent(parent);
				configSheetCode.setEnable(enable);
				PersistenceHelper.manager.save(configSheetCode);
				save(configSheetCode, children);
			}

			for (ConfigSheetCodeDTO dto : editRows) {
				// 단순 수정만..
				String oid = dto.getOid();
				String name = dto.getName();
				String code = dto.getCode();
				String description = dto.getDescription();
				boolean enable = dto.isEnable();
				int sort = dto.getSort();
				ConfigSheetCode configSheetCode = (ConfigSheetCode) CommonUtils.getObject(oid);
				configSheetCode.setName(name);
				configSheetCode.setCode(code);
				configSheetCode.setSort(sort);
				configSheetCode.setDescription(description);
				configSheetCode.setEnable(enable);
				PersistenceHelper.manager.modify(configSheetCode);
			}

			for (ConfigSheetCodeDTO dto : removeRows) {
				String oid = dto.getOid();
				ConfigSheetCode configSheetCode = (ConfigSheetCode) CommonUtils.getObject(oid);
				PersistenceHelper.manager.delete(configSheetCode);
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

	// 어차피 도는 부분.. no transaction
	private void save(ConfigSheetCode parentCode, ArrayList<ConfigSheetCodeDTO> list) throws Exception {
		for (ConfigSheetCodeDTO dto : list) {
			String name = dto.getName();
			String code = dto.getCode();
			String codeType = dto.getCodeType();
			String description = dto.getDescription();
			boolean enable = dto.isEnable();
			int sort = dto.getSort();
			ArrayList<ConfigSheetCodeDTO> children = dto.getChildren();
			ConfigSheetCode configSheetCode = ConfigSheetCode.newConfigSheetCode();
			configSheetCode.setName(name);
			configSheetCode.setCode(code);
			configSheetCode.setCodeType(ConfigSheetCodeType.toConfigSheetCodeType(codeType));
			configSheetCode.setDescription(description);
			configSheetCode.setSort(sort);
			configSheetCode.setParent(parentCode);
			configSheetCode.setEnable(enable);
			PersistenceHelper.manager.save(configSheetCode);
			save(configSheetCode, children);
		}
	}
}
