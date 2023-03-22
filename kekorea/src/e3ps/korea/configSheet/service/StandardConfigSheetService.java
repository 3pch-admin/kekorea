package e3ps.korea.configSheet.service;

import java.util.ArrayList;
import java.util.Map;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.Constants;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.StringUtils;
import e3ps.korea.configSheet.ConfigSheet;
import e3ps.korea.configSheet.ConfigSheetProjectLink;
import e3ps.korea.configSheet.ConfigSheetVariable;
import e3ps.korea.configSheet.ConfigSheetVariableLink;
import e3ps.korea.configSheet.beans.ConfigSheetDTO;
import e3ps.project.Project;
import wt.content.ApplicationData;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.fc.PersistenceHelper;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardConfigSheetService extends StandardManager implements ConfigSheetService {

	public static StandardConfigSheetService newStandardConfigSheetService() throws WTException {
		StandardConfigSheetService instance = new StandardConfigSheetService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(ConfigSheetDTO dto) throws Exception {
		String name = dto.getName();
		String description = dto.getDescription();
		ArrayList<String> secondarys = dto.getSecondarys();
		ArrayList<Map<String, String>> addRows = dto.getAddRows();
		ArrayList<Map<String, String>> _addRows = dto.get_addRows();
		ArrayList<Map<String, String>> _addRows_ = dto.get_addRows_();
		Transaction trs = new Transaction();
		try {
			trs.start();

			ConfigSheet configSheet = ConfigSheet.newConfigSheet();
			configSheet.setName(name);
			configSheet.setDescription(description);
			configSheet.setState(Constants.State.INWORK);
			configSheet.setOwnership(CommonUtils.sessionOwner());

			PersistenceHelper.manager.save(configSheet);

			for (String secondary : secondarys) {
				ApplicationData data = ApplicationData.newApplicationData(configSheet);
				data.setRole(ContentRoleType.SECONDARY);
				PersistenceHelper.manager.save(data);
				ContentServerHelper.service.updateContent(configSheet, data, secondary);
			}

			for (Map<String, String> addRow : _addRows) {
				String oid = addRow.get("oid");
				Project project = (Project) CommonUtils.getObject(oid);
				ConfigSheetProjectLink link = ConfigSheetProjectLink.newConfigSheetProjectLink(configSheet, project);
				PersistenceHelper.manager.save(link);
			}

			int sort = 0;
			for (Map<String, String> addRow : addRows) {
				String category_code = addRow.get("category_code");
				String item_code = addRow.get("item_code");
				String spec_code = addRow.get("spec_code");
				String note = addRow.get("note");
				String apply = addRow.get("apply");

				CommonCode category = null;
				CommonCode item = null;
				CommonCode spec = null;
				if (!StringUtils.isNull(category_code)) {
					category = CommonCodeHelper.manager.getCommonCode(category_code, "CATEGORY");
				}
				if (!StringUtils.isNull(item_code)) {
					item = CommonCodeHelper.manager.getCommonCode(item_code, "CATEGORY_ITEM");
				}
				if (!StringUtils.isNull(spec_code)) {
					spec = CommonCodeHelper.manager.getCommonCode(spec_code, "CATEGORY_SPEC");
				}

				ConfigSheetVariable variable = ConfigSheetVariable.newConfigSheetVariable();
				variable.setCategory(category);
				variable.setItem(item);
				variable.setSpec(spec);
				variable.setNote(note);
				variable.setApply(apply);
				PersistenceHelper.manager.save(variable);

				ConfigSheetVariableLink link = ConfigSheetVariableLink.newConfigSheetVariableLink(configSheet,
						variable);
				link.setSort(sort);
				PersistenceHelper.manager.save(link);
				sort++;
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
