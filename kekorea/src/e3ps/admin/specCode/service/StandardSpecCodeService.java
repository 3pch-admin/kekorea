package e3ps.admin.specCode.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import e3ps.admin.specCode.SpecCode;
import e3ps.admin.specCode.SpecCodeType;
import e3ps.admin.specCode.dto.SpecCodeDTO;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.StringUtils;
import wt.fc.PersistenceHelper;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardSpecCodeService extends StandardManager implements SpecCodeService {

	public static StandardSpecCodeService newStandardSpecCodeService() throws WTException {
		StandardSpecCodeService instance = new StandardSpecCodeService();
		instance.initialize();
		return instance;
	}

	@Override
	public void save(HashMap<String, List<SpecCodeDTO>> dataMap) throws Exception {
		List<SpecCodeDTO> addRows = dataMap.get("addRows");
		List<SpecCodeDTO> editRows = dataMap.get("editRows");
		List<SpecCodeDTO> removeRows = dataMap.get("removeRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (SpecCodeDTO dto : addRows) {
				String name = dto.getName();
				String code = dto.getCode();
				String codeType = dto.getCodeType();
				String description = dto.getDescription();
				boolean enable = dto.isEnable();
				int sort = dto.getSort();
				String _$parent = dto.get_$parent();
				ArrayList<SpecCodeDTO> children = dto.getChildren();

				SpecCode parent = null;
				if (!StringUtils.isNull(_$parent) && _$parent.indexOf("SpecCode") > -1) {
					parent = (SpecCode) CommonUtils.getObject(_$parent);
				}

				SpecCode specCode = SpecCode.newSpecCode();
				specCode.setName(name);
				specCode.setCode(code);
				specCode.setCodeType(SpecCodeType.toSpecCodeType(codeType));
				specCode.setDescription(StringUtils.replaceToValue(description, name));
				specCode.setSort(sort);
				specCode.setParent(parent);
				specCode.setEnable(enable);
				PersistenceHelper.manager.save(specCode);
				save(specCode, children);
			}

			for (SpecCodeDTO dto : editRows) {
				String oid = dto.getOid();
				String name = dto.getName();
				String code = dto.getCode();
				String description = dto.getDescription();
				boolean enable = dto.isEnable();
				int sort = dto.getSort();
				SpecCode specCode = (SpecCode) CommonUtils.getObject(oid);
				specCode.setName(name);
				specCode.setCode(code);
				specCode.setSort(sort);
				specCode.setDescription(StringUtils.replaceToValue(description, name));
				specCode.setEnable(enable);
				PersistenceHelper.manager.modify(specCode);
			}

			for (SpecCodeDTO dto : removeRows) {
				String oid = dto.getOid();
				SpecCode specCode = (SpecCode) CommonUtils.getObject(oid);
				PersistenceHelper.manager.delete(specCode);
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
	private void save(SpecCode parentCode, ArrayList<SpecCodeDTO> list) throws Exception {
		for (SpecCodeDTO dto : list) {
			String name = dto.getName();
			String code = dto.getCode();
			String codeType = dto.getCodeType();
			String description = dto.getDescription();
			boolean enable = dto.isEnable();
			int sort = dto.getSort();
			ArrayList<SpecCodeDTO> children = dto.getChildren();
			SpecCode specCode = SpecCode.newSpecCode();
			specCode.setName(name);
			specCode.setCode(code);
			specCode.setCodeType(SpecCodeType.toSpecCodeType(codeType));
			specCode.setDescription(StringUtils.replaceToValue(description, name));
			specCode.setSort(sort);
			specCode.setParent(parentCode);
			specCode.setEnable(enable);
			PersistenceHelper.manager.save(specCode);
			save(specCode, children);
		}
	}
}