package e3ps.epm.workOrder.service;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;

import e3ps.common.content.service.CommonContentHelper;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.ContentUtils;
import e3ps.common.util.DateUtils;
import e3ps.common.util.StringUtils;
import e3ps.epm.workOrder.WorkOrder;
import e3ps.epm.workOrder.WorkOrderDataLink;
import e3ps.epm.workOrder.WorkOrderProjectLink;
import e3ps.epm.workOrder.dto.WorkOrderDTO;
import e3ps.project.Project;
import e3ps.project.output.Output;
import e3ps.project.output.OutputDocumentLink;
import e3ps.project.service.ProjectHelper;
import e3ps.project.task.Task;
import e3ps.project.task.variable.TaskStateVariable;
import e3ps.project.variable.ProjectStateVariable;
import e3ps.workspace.service.WorkspaceHelper;
import wt.clients.folder.FolderTaskLogic;
import wt.content.ApplicationData;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardWorkOrderService extends StandardManager implements WorkOrderService {

	public static StandardWorkOrderService newStandardWorkOrderService() throws WTException {
		StandardWorkOrderService instance = new StandardWorkOrderService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(WorkOrderDTO dto) throws Exception {
		String toid = dto.getToid();
		String name = dto.getName();
		String description = dto.getDescription();
		int progress = dto.getProgress();
		String workOrderType = dto.getWorkOrderType();
		ArrayList<Map<String, Object>> addRows = dto.getAddRows(); // 도면 일람표
		ArrayList<Map<String, String>> addRows9 = dto.getAddRows9();
		ArrayList<Map<String, String>> agreeRows = dto.getAgreeRows();
		ArrayList<Map<String, String>> approvalRows = dto.getApprovalRows();
		ArrayList<Map<String, String>> receiveRows = dto.getReceiveRows();
		ArrayList<String> secondarys = dto.getSecondarys();
		String location = "/Default/프로젝트/" + workOrderType + "_도면일람표";
		Transaction trs = new Transaction();
		try {
			trs.start();

			WorkOrder workOrder = WorkOrder.newWorkOrder();
			workOrder.setDescription(description);
			workOrder.setName(name);
			workOrder.setWorkOrderType(workOrderType);
			workOrder.setNumber(WorkOrderHelper.manager.getNextNumber("WORK-"));
			workOrder.setVersion(1);
			workOrder.setLatest(true);

			Folder folder = FolderTaskLogic.getFolder(location, CommonUtils.getPDMLinkProductContainer());
			FolderHelper.assignLocation((FolderEntry) workOrder, folder);

			PersistenceHelper.manager.save(workOrder);

			int sort = 0;
			ArrayList<WorkOrderDataLink> list = new ArrayList<>();
			for (int i = 0; i < addRows.size(); i++) {
				Map<String, Object> addRow = addRows.get(i);
				String oid = (String) addRow.get("doid");
				int rev = (int) addRow.get("rev");
				int lotNo = (int) addRow.get("lotNo");
				String note = (String) addRow.get("note");
				Persistable persistable = (Persistable) CommonUtils.getObject(oid);
				WorkOrderDataLink link = WorkOrderDataLink.newWorkOrderDataLink(workOrder, persistable);
				link.setSort(sort);
				link.setLotNo(lotNo);
				link.setNote(note);
				link.setRev(rev);
				PersistenceHelper.manager.save(link);
				sort++;
				list.add(link);
			}

			for (int i = 0; secondarys != null && i < secondarys.size(); i++) {
				String cacheId = (String) secondarys.get(i);
				File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
				ApplicationData applicationData = ApplicationData.newApplicationData(workOrder);
				applicationData.setRole(ContentRoleType.SECONDARY);
				PersistenceHelper.manager.save(applicationData);
				ContentServerHelper.service.updateContent(workOrder, applicationData, vault.getPath());
			}

			Workbook cover = WorkOrderHelper.manager.createWorkOrderCover(workOrder, list);
			File tempFile = ContentUtils.getTempFile(workOrder.getName() + "_표지.xlsx");
			FileOutputStream fos = new FileOutputStream(tempFile);
			cover.write(fos);

			ApplicationData data = ApplicationData.newApplicationData(workOrder);
			data.setRole(ContentRoleType.PRIMARY);
			PersistenceHelper.manager.save(data);
			ContentServerHelper.service.updateContent(workOrder, data, tempFile.getAbsolutePath());

			for (Map<String, String> addRow9 : addRows9) {
				String oid = addRow9.get("oid");
				Project project = (Project) CommonUtils.getObject(oid);

				String taskName = "";
				if (!StringUtils.isNull(toid)) {
					Task task = (Task) CommonUtils.getObject(toid);
					taskName = task.getName();
				} else {
					if ("기계".equals(workOrderType)) {
						taskName = "기계_도면일람표";
					} else if ("전기".equals(workOrderType)) {
						taskName = "전기_도면일람표";
					}
				}

				// 기계_수배표 전기_수배표
				Task t = ProjectHelper.manager.getTaskByName(project, taskName);
				if (t == null) {
					throw new Exception(project.getKekNumber() + "작번에 " + taskName + " 태스크가 존재하지 않습니다.");
				}

				WorkOrderProjectLink link = WorkOrderProjectLink.newWorkOrderProjectLink(workOrder, project);
				PersistenceHelper.manager.save(link);

				// 산출물
				Output output = Output.newOutput();
				output.setName(workOrder.getName());
				output.setLocation(workOrder.getLocation());
				output.setTask(t);
				output.setProject(project);
				output.setDocument(workOrder);
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

			WorkOrderHelper.manager.postAfterAction(workOrder.getPersistInfo().getObjectIdentifier().getStringValue());

			// 결재시작
			if (approvalRows.size() > 0) {
				WorkspaceHelper.service.register(workOrder, agreeRows, approvalRows, receiveRows);
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
	public void modify(WorkOrderDTO dto) throws Exception {
		String name = dto.getName();
		String description = dto.getDescription();
		int progress = dto.getProgress();
		String workOrderType = dto.getWorkOrderType();
		ArrayList<Map<String, Object>> addRows = dto.getAddRows(); // 도면 일람표
		ArrayList<Map<String, String>> addRows9 = dto.getAddRows9();
		ArrayList<Map<String, String>> agreeRows = dto.getAgreeRows();
		ArrayList<Map<String, String>> approvalRows = dto.getApprovalRows();
		ArrayList<Map<String, String>> receiveRows = dto.getReceiveRows();
		ArrayList<String> secondarys = dto.getSecondarys();
		Transaction trs = new Transaction();
		try {
			trs.start();

			WorkOrder workOrder = (WorkOrder) CommonUtils.getObject(dto.getOid());
			workOrder.setName(name);
			workOrder.setDescription(description);
			workOrder.setWorkOrderType(workOrderType);
			PersistenceHelper.manager.modify(workOrder);

			// 기존 도면 일람표 링크 모두제거
			QueryResult qr = PersistenceHelper.manager.navigate(workOrder, "data", WorkOrderDataLink.class, false);
			while (qr.hasMoreElements()) {
				WorkOrderDataLink link = (WorkOrderDataLink) qr.nextElement();
				PersistenceHelper.manager.delete(link);
			}

			int sort = 0;
			ArrayList<WorkOrderDataLink> list = new ArrayList<>();
			for (int i = 0; i < addRows.size(); i++) {
				Map<String, Object> addRow = addRows.get(i);
				String oid = (String) addRow.get("doid"); // 객체 링크 OID...
				int rev = (int) addRow.get("rev");
				int lotNo = (int) addRow.get("lotNo");
				String note = (String) addRow.get("note");
				Persistable persistable = (Persistable) CommonUtils.getObject(oid);
				WorkOrderDataLink link = WorkOrderDataLink.newWorkOrderDataLink(workOrder, persistable);
				link.setSort(sort);
				link.setLotNo(lotNo);
				link.setNote(note);
				link.setRev(rev);
				PersistenceHelper.manager.save(link);
				sort++;
				list.add(link);
			}

			// 표지 파일도 다시 생성해야함..
			CommonContentHelper.manager.clear(workOrder);

			for (int i = 0; secondarys != null && i < secondarys.size(); i++) {
				String cacheId = (String) secondarys.get(i);
				File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
				ApplicationData applicationData = ApplicationData.newApplicationData(workOrder);
				applicationData.setRole(ContentRoleType.SECONDARY);
				PersistenceHelper.manager.save(applicationData);
				ContentServerHelper.service.updateContent(workOrder, applicationData, vault.getPath());
			}

			Workbook cover = WorkOrderHelper.manager.createWorkOrderCover(workOrder, list);
			File tempFile = ContentUtils.getTempFile(workOrder.getName() + "_표지.xlsx");
			FileOutputStream fos = new FileOutputStream(tempFile);
			cover.write(fos);

			ApplicationData data = ApplicationData.newApplicationData(workOrder);
			data.setRole(ContentRoleType.PRIMARY);
			PersistenceHelper.manager.save(data);
			ContentServerHelper.service.updateContent(workOrder, data, tempFile.getAbsolutePath());

			// 기존 작번과 도면일람표 링크 제거
			QueryResult _qr = PersistenceHelper.manager.navigate(workOrder, "project", WorkOrderProjectLink.class,
					false);
			while (_qr.hasMoreElements()) {
				WorkOrderProjectLink link = (WorkOrderProjectLink) _qr.nextElement();
				PersistenceHelper.manager.delete(link);
			}

			// 기존 산출물 링크도 제거 후 다시 연결
			QueryResult navi = PersistenceHelper.manager.navigate(workOrder, "output", OutputDocumentLink.class);
			while (navi.hasMoreElements()) {
				Output output = (Output) navi.nextElement();
				PersistenceHelper.manager.delete(output);
			}

			for (Map<String, String> addRow9 : addRows9) {
				String oid = addRow9.get("oid");
				Project project = (Project) CommonUtils.getObject(oid);

				String taskName = "";
				if ("기계".equals(workOrderType)) {
					taskName = "기계_도면일람표";
				} else if ("전기".equals(workOrderType)) {
					taskName = "전기_도면일람표";
				}

				// 기계_수배표 전기_수배표
				Task t = ProjectHelper.manager.getTaskByName(project, taskName);
				if (t == null) {
					throw new Exception(project.getKekNumber() + "작번에 " + taskName + " 태스크가 존재하지 않습니다.");
				}

				WorkOrderProjectLink link = WorkOrderProjectLink.newWorkOrderProjectLink(workOrder, project);
				PersistenceHelper.manager.save(link);

				// 산출물
				Output output = Output.newOutput();
				output.setName(workOrder.getName());
				output.setLocation(workOrder.getLocation());
				output.setTask(t);
				output.setProject(project);
				output.setDocument(workOrder);
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

			// PDF 병합
			WorkOrderHelper.manager.postAfterAction(workOrder.getPersistInfo().getObjectIdentifier().getStringValue());

			// 결재시작
			if (approvalRows.size() > 0) {
				WorkspaceHelper.manager.deleteAllLines(workOrder); // 기존결재 잇으면 삭제 후 작업
				WorkspaceHelper.service.register(workOrder, agreeRows, approvalRows, receiveRows);
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

			WorkOrder workOrder = (WorkOrder) CommonUtils.getObject(oid);

			QueryResult result = PersistenceHelper.manager.navigate(workOrder, "project", WorkOrderProjectLink.class,
					false);
			while (result.hasMoreElements()) {
				WorkOrderProjectLink link = (WorkOrderProjectLink) result.nextElement();
				PersistenceHelper.manager.delete(link);
			}

			result.reset();
			result = PersistenceHelper.manager.navigate(workOrder, "data", WorkOrderDataLink.class, false);
			while (result.hasMoreElements()) {
				WorkOrderDataLink link = (WorkOrderDataLink) result.nextElement();
				PersistenceHelper.manager.delete(link);
			}

			PersistenceHelper.manager.delete(workOrder);

			// 버전 되돌려야함..

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
	public void revise(WorkOrderDTO dto) throws Exception {
		String name = dto.getName();
		String description = dto.getDescription();
		int progress = dto.getProgress();
		String workOrderType = dto.getWorkOrderType();
		ArrayList<Map<String, Object>> addRows = dto.getAddRows(); // 도면 일람표
		ArrayList<Map<String, String>> addRows9 = dto.getAddRows9();
		ArrayList<Map<String, String>> agreeRows = dto.getAgreeRows();
		ArrayList<Map<String, String>> approvalRows = dto.getApprovalRows();
		ArrayList<Map<String, String>> receiveRows = dto.getReceiveRows();
		ArrayList<String> secondarys = dto.getSecondarys();
		String location = "/Default/프로젝트/" + workOrderType + "_도면일람표";
		Transaction trs = new Transaction();
		try {
			trs.start();

			WorkOrder pre = (WorkOrder) CommonUtils.getObject(dto.getOid());
			pre.setLatest(false);
			PersistenceHelper.manager.modify(pre);

			WorkOrder workOrder = WorkOrder.newWorkOrder();
			workOrder.setDescription(description);
			workOrder.setName(name);
			workOrder.setWorkOrderType(workOrderType);
			workOrder.setNumber(pre.getNumber());
			workOrder.setVersion(pre.getVersion() + 1);
			workOrder.setLatest(true);
			workOrder.setNote(dto.getNote());
			workOrder.setNote(dto.getNote());

			Folder folder = FolderTaskLogic.getFolder(location, CommonUtils.getPDMLinkProductContainer());
			FolderHelper.assignLocation((FolderEntry) workOrder, folder);

			PersistenceHelper.manager.save(workOrder);

			int sort = 0;
			ArrayList<WorkOrderDataLink> list = new ArrayList<>();
			for (int i = 0; i < addRows.size(); i++) {
				Map<String, Object> addRow = addRows.get(i);
				String oid = (String) addRow.get("doid");
				int rev = (int) addRow.get("rev");
				int lotNo = (int) addRow.get("lotNo");
				String note = (String) addRow.get("note");
				Persistable persistable = (Persistable) CommonUtils.getObject(oid);
				WorkOrderDataLink link = WorkOrderDataLink.newWorkOrderDataLink(workOrder, persistable);
				link.setSort(sort);
				link.setLotNo(lotNo);
				link.setNote(note);
				link.setRev(rev);
				PersistenceHelper.manager.save(link);
				sort++;
				list.add(link);
			}

			for (int i = 0; secondarys != null && i < secondarys.size(); i++) {
				String cacheId = (String) secondarys.get(i);
				File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
				ApplicationData applicationData = ApplicationData.newApplicationData(workOrder);
				applicationData.setRole(ContentRoleType.SECONDARY);
				PersistenceHelper.manager.save(applicationData);
				ContentServerHelper.service.updateContent(workOrder, applicationData, vault.getPath());
			}

			Workbook cover = WorkOrderHelper.manager.createWorkOrderCover(workOrder, list);
			File tempFile = ContentUtils.getTempFile(workOrder.getName() + "_도면일람표.xlsx");
			FileOutputStream fos = new FileOutputStream(tempFile);
			cover.write(fos);

			ApplicationData data = ApplicationData.newApplicationData(workOrder);
			data.setRole(ContentRoleType.PRIMARY);
			PersistenceHelper.manager.save(data);
			ContentServerHelper.service.updateContent(workOrder, data, tempFile.getAbsolutePath());

			for (Map<String, String> addRow9 : addRows9) {
				String oid = addRow9.get("oid");
				Project project = (Project) CommonUtils.getObject(oid);

				String taskName = "";
				if ("기계".equals(workOrderType)) {
					taskName = "기계_도면일람표";
				} else if ("전기".equals(workOrderType)) {
					taskName = "전기_도면일람표";
				}

				// 기계_수배표 전기_수배표
				Task t = ProjectHelper.manager.getTaskByName(project, taskName);
				if (t == null) {
					throw new Exception(project.getKekNumber() + "작번에 " + taskName + " 태스크가 존재하지 않습니다.");
				}

				WorkOrderProjectLink link = WorkOrderProjectLink.newWorkOrderProjectLink(workOrder, project);
				PersistenceHelper.manager.save(link);

				// 산출물
				Output output = Output.newOutput();
				output.setName(workOrder.getName());
				output.setLocation(workOrder.getLocation());
				output.setTask(t);
				output.setProject(project);
				output.setDocument(workOrder);
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

			WorkOrderHelper.manager.postAfterAction(workOrder.getPersistInfo().getObjectIdentifier().getStringValue());

			// 결재시작
			if (approvalRows.size() > 0) {
				WorkspaceHelper.service.register(workOrder, agreeRows, approvalRows, receiveRows);
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
	public Map<String, Object> connect(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		String poid = (String) params.get("poid");
		String toid = (String) params.get("toid");
		ArrayList<String> arr = (ArrayList<String>) params.get("arr");
		Transaction trs = new Transaction();
		try {
			trs.start();

			Task task = (Task) CommonUtils.getObject(toid);
			Project project = (Project) CommonUtils.getObject(poid);
			for (String oid : arr) {
				WorkOrder workOrder = (WorkOrder) CommonUtils.getObject(oid);

				QueryResult result = PersistenceHelper.manager.navigate(workOrder, "project",
						WorkOrderProjectLink.class);
				while (result.hasMoreElements()) {
					Project p = (Project) result.nextElement();

					if (p.getPersistInfo().getObjectIdentifier().getStringValue().equals(poid)) {
						trs.rollback();
						map.put("msg",
								"해당 도면일람표가 작번 : " + p.getKekNumber() + "의 태스크 : " + task.getName() + "에 연결이 되어있습니다.");
						map.put("exist", true);
						return map;
					}
				}

				WorkOrderProjectLink link = WorkOrderProjectLink.newWorkOrderProjectLink(workOrder, project);
				PersistenceHelper.manager.save(link);

				Output output = Output.newOutput();
				output.setName(workOrder.getName());
				output.setLocation(workOrder.getLocation());
				output.setTask(task);
				output.setProject(project);
				output.setDocument(workOrder);
				output.setOwnership(CommonUtils.sessionOwner());
				PersistenceHelper.manager.save(output);

				// 의뢰서는 아에 다른 페이지에서 작동하므로 소스 간결 연결된 태스트 상태 변경
				// 추가적인 산출물 등록시 실제 시작일이 변경 안되도록 처리한다.
				if (task.getStartDate() == null) {
					task.setStartDate(new Timestamp(new Date().getTime()));
				}
				task.setState(TaskStateVariable.INWORK);
				PersistenceHelper.manager.modify(task);

				// 프로젝트 전체 진행율 조정
				ProjectHelper.service.calculation(project);
				ProjectHelper.service.commit(project);
			}

			map.put("exist", false);

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
		return map;
	}

	@Override
	public void disconnect(String oid) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			WorkOrder workOrder = (WorkOrder) CommonUtils.getObject(oid);
			QueryResult qr = PersistenceHelper.manager.navigate(workOrder, "project", WorkOrderProjectLink.class,
					false);
			while (qr.hasMoreElements()) {
				WorkOrderProjectLink link = (WorkOrderProjectLink) qr.nextElement();
				PersistenceHelper.manager.delete(link);
			}

			QueryResult result = PersistenceHelper.manager.navigate(workOrder, "output", OutputDocumentLink.class);
			while (result.hasMoreElements()) {
				Output output = (Output) result.nextElement();
				PersistenceHelper.manager.delete(output);
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
