package e3ps.project.output.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import e3ps.common.util.CommonUtils;
import e3ps.doc.service.DocumentHelper;
import e3ps.project.Project;
import e3ps.project.output.Output;
import e3ps.project.output.dto.OutputDTO;
import e3ps.project.service.ProjectHelper;
import e3ps.project.task.Task;
import e3ps.project.task.variable.TaskStateVariable;
import e3ps.workspace.service.WorkspaceHelper;
import wt.content.ApplicationData;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardOutputService extends StandardManager implements OutputService {

	public static StandardOutputService newStandardOutputService() throws WTException {
		StandardOutputService instance = new StandardOutputService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(OutputDTO dto) throws Exception {
		String name = dto.getName();
		String location = dto.getLocation();
		String description = dto.getDescription();
		int progress = dto.getProgress();
		ArrayList<Map<String, String>> addRows9 = dto.getAddRows9();
		ArrayList<Map<String, String>> agreeRows = dto.getAgreeRows();
		ArrayList<Map<String, String>> approvalRows = dto.getApprovalRows();
		ArrayList<Map<String, String>> receiveRows = dto.getReceiveRows();
		ArrayList<String> primarys = dto.getPrimarys();
		String toid = dto.getToid();
		// 태스크에서 바로 연결 시킬떄 생각..
		Transaction trs = new Transaction();
		try {
			trs.start();

			Task task = (Task) CommonUtils.getObject(toid);

			WTDocument document = WTDocument.newWTDocument();
			document.setName(name);
			document.setNumber(DocumentHelper.manager.getNextNumber("PJ-"));
			document.setDescription(description);

			Folder folder = FolderHelper.service.getFolder(location, CommonUtils.getPDMLinkProductContainer());
			FolderHelper.assignLocation((FolderEntry) document, folder);
			document = (WTDocument) PersistenceHelper.manager.save(document);

			for (int i = 0; i < primarys.size(); i++) {
				String primary = (String) primarys.get(i);
				ApplicationData applicationData = ApplicationData.newApplicationData(document);
				if (i == 0) {
					applicationData.setRole(ContentRoleType.PRIMARY);
				} else {
					applicationData.setRole(ContentRoleType.SECONDARY);
				}
				PersistenceHelper.manager.save(applicationData);
				ContentServerHelper.service.updateContent(document, applicationData, primary);
			}

			// 프로젝트
			for (Map<String, String> addRow9 : addRows9) {
				String oid = addRow9.get("oid");
				Project project = (Project) CommonUtils.getObject(oid);
				Task t = ProjectHelper.manager.getTaskByName(project, task.getName());

				// 강제 에러 처리
				if (t == null) {
					throw new Exception(project.getKekNumber() + "작번에 태스크 명(" + task.getName() + ")이 존재하지 않습니다.");
				}

				Output output = Output.newOutput();
				output.setName(document.getName());
				output.setLocation(document.getLocation());
				output.setTask(task);
				output.setProject(project);
				output.setDocument(document);
				output.setOwnership(CommonUtils.sessionOwner());
				PersistenceHelper.manager.save(output);

				// 의뢰서는 아에 다른 페이지에서 작동하므로 소스 간결 연결된 태스트 상태 변경
				// 추가적인 산출물 등록시 실제 시작일이 변경 안되도록 처리한다.
				if (t.getStartDate() == null) {
					t.setStartDate(new Timestamp(new Date().getTime()));
				}
				// 완료 처리
				if (progress == 100) {
					t.setState(TaskStateVariable.COMPLETE);
					t.setEndDate(new Timestamp(new Date().getTime()));
				} else {
					t.setState(TaskStateVariable.INWORK);
				}
				t.setProgress(progress);
				PersistenceHelper.manager.modify(t);

				// 프로젝트 전체 진행율 조정
				ProjectHelper.service.calculation(project);
			}

			// 결재시작
			if (approvalRows.size() > 0) {
				WorkspaceHelper.service.register(document, agreeRows, approvalRows, receiveRows);
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
