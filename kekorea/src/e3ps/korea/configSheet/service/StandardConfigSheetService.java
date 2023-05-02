package e3ps.korea.configSheet.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.admin.configSheetCode.ConfigSheetCode;
import e3ps.admin.configSheetCode.service.ConfigSheetCodeHelper;
import e3ps.common.Constants;
import e3ps.common.content.service.CommonContentHelper;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.DateUtils;
import e3ps.common.util.StringUtils;
import e3ps.epm.workOrder.WorkOrderProjectLink;
import e3ps.korea.configSheet.ConfigSheet;
import e3ps.korea.configSheet.ConfigSheetProjectLink;
import e3ps.korea.configSheet.ConfigSheetVariable;
import e3ps.korea.configSheet.ConfigSheetVariableLink;
import e3ps.korea.configSheet.beans.ConfigSheetDTO;
import e3ps.project.Project;
import e3ps.project.output.Output;
import e3ps.project.service.ProjectHelper;
import e3ps.project.task.Task;
import e3ps.project.task.variable.TaskStateVariable;
import e3ps.project.variable.ProjectStateVariable;
import e3ps.workspace.service.WorkspaceHelper;
import wt.clients.folder.FolderTaskLogic;
import wt.content.ApplicationData;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.fc.PersistenceHelper;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.method.MethodServerMain;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardConfigSheetService extends StandardManager implements ConfigSheetService {

	private static final String taskName = "사양체크리스트";

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
		ArrayList<Map<String, String>> addRows9 = dto.getAddRows9();
		ArrayList<Map<String, String>> agreeRows = dto.getAgreeRows();
		ArrayList<Map<String, String>> approvalRows = dto.getApprovalRows();
		ArrayList<Map<String, String>> receiveRows = dto.getReceiveRows();
		int progress = dto.getProgress();
		Transaction trs = new Transaction();
		try {
			trs.start();

			String number = ConfigSheetHelper.manager.getNextNumber();

			ConfigSheet configSheet = ConfigSheet.newConfigSheet();
			configSheet.setName(name);
			configSheet.setNumber(number);
			configSheet.setDescription(description != null ? description : name);
			configSheet.setLatest(true);
			configSheet.setVersion(1);

			Folder folder = FolderTaskLogic.getFolder("/Default/프로젝트/" + taskName,
					CommonUtils.getPDMLinkProductContainer());
			FolderHelper.assignLocation((FolderEntry) configSheet, folder);

			PersistenceHelper.manager.save(configSheet);

			for (int i = 0; secondarys != null && i < secondarys.size(); i++) {
				String cacheId = (String) secondarys.get(i);
				File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
				ApplicationData applicationData = ApplicationData.newApplicationData(configSheet);
				applicationData.setRole(ContentRoleType.SECONDARY);
				PersistenceHelper.manager.save(applicationData);
				ContentServerHelper.service.updateContent(configSheet, applicationData, vault.getPath());
			}

			for (Map<String, String> addRow9 : addRows9) {
				String oid = addRow9.get("oid");
				Project project = (Project) CommonUtils.getObject(oid);

				// 기계_수배표 전기_수배표
				Task t = ProjectHelper.manager.getTaskByName(project, taskName);
				if (t == null) {
					throw new Exception(project.getKekNumber() + "작번에 " + taskName + " 태스크가 존재하지 않습니다.");
				}

				ConfigSheetProjectLink link = ConfigSheetProjectLink.newConfigSheetProjectLink(configSheet, project);
				PersistenceHelper.manager.save(link);

				// 산출물
				Output output = Output.newOutput();
				output.setName(configSheet.getName());
				output.setLocation(configSheet.getLocation());
				output.setTask(t);
				output.setProject(project);
				output.setDocument(configSheet);
				output.setOwnership(CommonUtils.sessionOwner());
				output = (Output) PersistenceHelper.manager.save(output);

				// 태스크
				if (t.getStartDate() == null) {
					// 중복적으로 실제 시작일이 변경 되지 않게
					t.setStartDate(DateUtils.getCurrentTimestamp());
				}

				if (progress >= 100) {
					t.setEndDate(DateUtils.getCurrentTimestamp());
					t.setState(TaskStateVariable.COMPLETE);
					t.setProgress(100);
				} else {
					t.setState(TaskStateVariable.INWORK);
					t.setProgress(progress);
				}
				t = (Task) PersistenceHelper.manager.modify(t);

				// 시작이 된 흔적이 없을 경우
				if (project.getStartDate() == null) {
					project.setStartDate(DateUtils.getCurrentTimestamp());
					project.setKekState(ProjectStateVariable.KEK_DESIGN_INWORK);
					project.setState(ProjectStateVariable.INWORK);
					project = (Project) PersistenceHelper.manager.modify(project);
				}
				ProjectHelper.service.calculation(project);
				ProjectHelper.service.commit(project);
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
