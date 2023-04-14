package e3ps.korea.configSheet.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.admin.configSheetCode.ConfigSheetCode;
import e3ps.admin.configSheetCode.service.ConfigSheetCodeHelper;
import e3ps.common.Constants;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.StringUtils;
import e3ps.korea.configSheet.ConfigSheet;
import e3ps.korea.configSheet.ConfigSheetProjectLink;
import e3ps.korea.configSheet.ConfigSheetVariable;
import e3ps.korea.configSheet.ConfigSheetVariableLink;
import e3ps.korea.configSheet.beans.ConfigSheetDTO;
import e3ps.project.Project;
import e3ps.workspace.service.WorkspaceHelper;
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
		ArrayList<Map<String, String>> agreeRows = dto.getAgreeRows();
		ArrayList<Map<String, String>> approvalRows = dto.getApprovalRows();
		ArrayList<Map<String, String>> receiveRows = dto.getReceiveRows();
		Transaction trs = new Transaction();
		try {
			trs.start();

			ConfigSheet configSheet = ConfigSheet.newConfigSheet();
			configSheet.setName(name);
			configSheet.setDescription(description != null ? description : name);
			configSheet.setState(Constants.State.INWORK);
			configSheet.setOwnership(CommonUtils.sessionOwner());
			configSheet.setLatest(true);
			configSheet.setVersion(1);
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
				configSheet.setProject(project);
				PersistenceHelper.manager.modify(configSheet);
			}

			int sort = 0;
			for (Map<String, String> addRow : addRows) {
				String category_code = addRow.get("category_code");
				String item_code = addRow.get("item_code");
				String spec = addRow.get("spec");
				String note = addRow.get("note");
				String apply = addRow.get("apply");

				ConfigSheetCode category = null;
				ConfigSheetCode item = null;
//				ConfigSheetCode spec = null;
				if (!StringUtils.isNull(category_code)) {
					category = ConfigSheetCodeHelper.manager.getConfigSheetCode(category_code, "CATEGORY");
				}
				if (!StringUtils.isNull(item_code)) {
					item = ConfigSheetCodeHelper.manager.getConfigSheetCode(item_code, "CATEGORY_ITEM");
				}
//				if (!StringUtils.isNull(spec_code)) {
//					spec = ConfigSheetCodeHelper.manager.getConfigSheetCode(spec_code, "CATEGORY_SPEC");
//				}

				ConfigSheetVariable variable = ConfigSheetVariable.newConfigSheetVariable();
				variable.setCategory(category);
				variable.setItem(item);
				variable.setSpec(spec);
				variable.setNote(note);
				variable.setApply(apply);
				variable.setSort(sort);
				PersistenceHelper.manager.save(variable);

				ConfigSheetVariableLink link = ConfigSheetVariableLink.newConfigSheetVariableLink(configSheet,
						variable);
				link.setSort(sort);
				PersistenceHelper.manager.save(link);
				sort++;
			}

			if (approvalRows.size() > 0) {
				WorkspaceHelper.service.register(configSheet, agreeRows, approvalRows, receiveRows);
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
	public void save(HashMap<String, List<ConfigSheetDTO>> dataMap) throws Exception {
		List<ConfigSheetDTO> removeRows = dataMap.get("removeRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (ConfigSheetDTO dto : removeRows) {
				String oid = dto.getLoid();
				ConfigSheetProjectLink link = (ConfigSheetProjectLink) CommonUtils.getObject(oid);
				PersistenceHelper.manager.delete(link);
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
	public void delete(String oid) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			ConfigSheet configSheet = (ConfigSheet) CommonUtils.getObject(oid);
			PersistenceHelper.manager.delete(configSheet);

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
