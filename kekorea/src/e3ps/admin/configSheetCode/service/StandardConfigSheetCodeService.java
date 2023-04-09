package e3ps.admin.configSheetCode.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.CommonCodeType;
import e3ps.admin.commonCode.dto.CommonCodeDTO;
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
	public void save(HashMap<String, List<CommonCodeDTO>> dataMap) throws Exception {
		List<CommonCodeDTO> addRows = dataMap.get("addRows");
		List<CommonCodeDTO> editRows = dataMap.get("editRows");
		List<CommonCodeDTO> removeRows = dataMap.get("removeRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (CommonCodeDTO dto : addRows) {
				String name = dto.getName();
				String code = dto.getCode();
				String codeType = dto.getCodeType();
				String description = dto.getDescription();
				boolean enable = dto.isEnable();
				int sort = dto.getSort();
				String _$parent = dto.get_$parent();
				ArrayList<CommonCodeDTO> children = dto.getChildren();

				CommonCode parent = null;
				if (!StringUtils.isNull(_$parent) && _$parent.indexOf("CommonCode") > -1) {
					parent = (CommonCode) CommonUtils.getObject(_$parent);
				}

				CommonCode commonCode = CommonCode.newCommonCode();
				commonCode.setName(name);
				commonCode.setCode(code);
				commonCode.setCodeType(CommonCodeType.toCommonCodeType(codeType));
				commonCode.setDescription(description);
				commonCode.setSort(sort);
				commonCode.setParent(parent);
				commonCode.setEnable(enable);
				PersistenceHelper.manager.save(commonCode);
				save(commonCode, children);
			}

			for (CommonCodeDTO dto : editRows) {
				// 단순 수정만..
				String oid = dto.getOid();
				String name = dto.getName();
				String code = dto.getCode();
				String description = dto.getDescription();
				boolean enable = dto.isEnable();
				int sort = dto.getSort();
				CommonCode commonCode = (CommonCode) CommonUtils.getObject(oid);
				commonCode.setName(name);
				commonCode.setCode(code);
				commonCode.setSort(sort);
				commonCode.setDescription(description);
				commonCode.setEnable(enable);
				PersistenceHelper.manager.modify(commonCode);
			}

			for (CommonCodeDTO dto : removeRows) {
				String oid = dto.getOid();
				CommonCode commonCode = (CommonCode) CommonUtils.getObject(oid);
				PersistenceHelper.manager.delete(commonCode);
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
	private void save(CommonCode parentCode, ArrayList<CommonCodeDTO> list) throws Exception {
		for (CommonCodeDTO dto : list) {
			String name = dto.getName();
			String code = dto.getCode();
			String codeType = dto.getCodeType();
			String description = dto.getDescription();
			boolean enable = dto.isEnable();
			int sort = dto.getSort();
			ArrayList<CommonCodeDTO> children = dto.getChildren();
			CommonCode commonCode = CommonCode.newCommonCode();
			commonCode.setName(name);
			commonCode.setCode(code);
			commonCode.setCodeType(CommonCodeType.toCommonCodeType(codeType));
			commonCode.setDescription(description);
			commonCode.setSort(sort);
			commonCode.setParent(parentCode);
			commonCode.setEnable(enable);
			PersistenceHelper.manager.save(commonCode);
			save(commonCode, children);
		}
	}
}
