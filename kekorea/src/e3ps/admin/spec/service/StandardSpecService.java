package e3ps.admin.spec.service;

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

public class StandardSpecService extends StandardManager implements SpecService {

	public static StandardSpecService newStandardSpecService() throws WTException {
		StandardSpecService instance = new StandardSpecService();
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
				String _$parent = dto.get_$parent();
				int sort = dto.getSort();
				ArrayList<CommonCodeDTO> children = dto.getChildren();

				if ("SPEC".equals(codeType)) {
					CommonCode parent = CommonCode.newCommonCode();
					parent.setName(name);
					parent.setCode(code);
					parent.setDescription(description);
					parent.setEnable(enable);
					parent.setSort(sort);
					parent.setCodeType(CommonCodeType.toCommonCodeType(codeType));
					PersistenceHelper.manager.save(parent);

					for (int i = 0; i < children.size(); i++) {
						CommonCodeDTO dd = (CommonCodeDTO) children.get(i);
						CommonCode child = CommonCode.newCommonCode();
						child.setName(dd.getName());
						child.setCode(dd.getCode());
						child.setCodeType(CommonCodeType.toCommonCodeType(dd.getCodeType()));
						child.setDescription(dd.getDescription());
						child.setParent(parent);
						child.setSort(dd.getSort());
						child.setEnable(dd.isEnable());
						PersistenceHelper.manager.save(child);
					}
				} else if ("OPTION".equals(codeType)) {
					if (_$parent.indexOf("CommonCode") > -1) {
						CommonCode parent = (CommonCode) CommonUtils.getObject(_$parent);
						CommonCode optionCode = CommonCode.newCommonCode();
						optionCode.setName(name);
						optionCode.setCode(code);
						optionCode.setDescription(description);
						optionCode.setEnable(enable);
						optionCode.setParent(parent);
						optionCode.setSort(sort);
						optionCode.setCodeType(CommonCodeType.toCommonCodeType(codeType));
						PersistenceHelper.manager.save(optionCode);
					}
				}
			}

			for (CommonCodeDTO dto : editRows) {
				String oid = dto.getOid();
				String name = dto.getName();
				String code = dto.getCode();
				String description = dto.getDescription();
				boolean enable = dto.isEnable();
				int sort = dto.getSort();
				String parent_oid = dto.getParent_oid();
				CommonCode commonCode = (CommonCode) CommonUtils.getObject(oid);
				commonCode.setName(name);
				commonCode.setCode(code);
				commonCode.setSort(sort);
				commonCode.setDescription(description);
				commonCode.setEnable(enable);

				if (!StringUtils.isNull(parent_oid)) {
					CommonCode parent = (CommonCode) CommonUtils.getObject(parent_oid);
					commonCode.setParent(parent);
				}

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
}