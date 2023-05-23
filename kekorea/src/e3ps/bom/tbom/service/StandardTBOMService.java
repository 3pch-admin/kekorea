package e3ps.bom.tbom.service;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import e3ps.bom.tbom.TBOMData;
import e3ps.bom.tbom.TBOMMaster;
import e3ps.bom.tbom.TBOMMasterDataLink;
import e3ps.bom.tbom.TBOMMasterProjectLink;
import e3ps.bom.tbom.dto.TBOMDTO;
import e3ps.common.content.service.CommonContentHelper;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.DateUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.part.kePart.KePart;
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
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardTBOMService extends StandardManager implements TBOMService {

	public static StandardTBOMService newStandardTBOMService() throws WTException {
		StandardTBOMService instance = new StandardTBOMService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(TBOMDTO dto) throws Exception {
		String name = dto.getName();
		String description = dto.getDescription();
		ArrayList<Map<String, Object>> addRows = dto.getAddRows(); // T-BOM
		ArrayList<Map<String, String>> addRows9 = dto.getAddRows9(); // 작번
		ArrayList<Map<String, String>> agreeRows = dto.getAgreeRows();
		ArrayList<Map<String, String>> approvalRows = dto.getApprovalRows();
		ArrayList<Map<String, String>> receiveRows = dto.getReceiveRows();
		ArrayList<String> secondarys = dto.getSecondarys();
		Transaction trs = new Transaction();
		try {
			trs.start();

			String number = TBOMHelper.manager.getNextNumber("T-BOM");
			Folder folder = FolderTaskLogic.getFolder("/Default/프로젝트/T-BOM", CommonUtils.getPDMLinkProductContainer());

			TBOMMaster master = TBOMMaster.newTBOMMaster();
			master.setNumber(number);
			master.setName(name);
			master.setVersion(1);
			master.setLatest(true);
			master.setDescription(description);
			FolderHelper.assignLocation((FolderEntry) master, folder);
			PersistenceHelper.manager.save(master);

			for (Map<String, String> addRow9 : addRows9) { // project
				String oid = (String) addRow9.get("oid");
				Project project = (Project) CommonUtils.getObject(oid);
				TBOMMasterProjectLink link = TBOMMasterProjectLink.newTBOMMasterProjectLink(master, project);
				PersistenceHelper.manager.save(link);

				Task task = ProjectHelper.manager.getTaskByName(project, "T-BOM");
				if (task == null) {
					throw new Exception(project.getKekNumber() + "작번에 T-BOM 태스크가 없습니다.");
				}
				// 산출물
				Output output = Output.newOutput();
				output.setName(master.getName());
				output.setLocation(master.getLocation());
				output.setTask(task);
				output.setProject(project);
				output.setDocument(master);
				output.setOwnership(CommonUtils.sessionOwner());
				output = (Output) PersistenceHelper.manager.save(output);

				// 태스크
				if (task.getStartDate() == null) {
					// 중복적으로 실제 시작일이 변경 되지 않게
					task.setStartDate(DateUtils.getCurrentTimestamp());
					task.setState(TaskStateVariable.INWORK);
				}

				task = (Task) PersistenceHelper.manager.modify(task);

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
			for (Map<String, Object> addRow : addRows) { // tbom
				String koid = (String) addRow.get("oid"); // kepart..
				String unit = (String) addRow.get("unit");
				String code = (String) addRow.get("code");
				int qty = (int) addRow.get("qty");
				int lotNo = (int) addRow.get("lotNo");
				String provide = (String) addRow.get("provide");
				String discontinue = (String) addRow.get("discontinue");

				KePart kePart = (KePart) CommonUtils.getObject(koid);
				TBOMData data = TBOMData.newTBOMData();
				data.setKePart(kePart);
				data.setQty(qty);
				data.setLotNo(lotNo);
				data.setCode(code);
				data.setProvide(provide);
				data.setDiscontinue(discontinue);
				data.setUnit(unit);
				data.setSort(sort);
				PersistenceHelper.manager.save(data);

				TBOMMasterDataLink link = TBOMMasterDataLink.newTBOMMasterDataLink(master, data);
				link.setSort(sort);
				PersistenceHelper.manager.save(link);
				sort++;
			}

			for (int i = 0; secondarys != null && i < secondarys.size(); i++) {
				String cacheId = (String) secondarys.get(i);
				File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
				ApplicationData dd = ApplicationData.newApplicationData(master);
				dd.setRole(ContentRoleType.SECONDARY);
				PersistenceHelper.manager.save(dd);
				ContentServerHelper.service.updateContent(master, dd, vault.getPath());
			}

			// 결재시작
			if (approvalRows.size() > 0) {
				WorkspaceHelper.service.register(master, agreeRows, approvalRows, receiveRows);
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
	public void save(Map<String, Object> params) throws Exception {
		ArrayList<Map<String, Object>> removeRows = (ArrayList<Map<String, Object>>) params.get("removeRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (Map<String, Object> removeRow : removeRows) {
				String oid = (String) removeRow.get("oid");
				TBOMMaster master = (TBOMMaster) CommonUtils.getObject(oid);

				QueryResult result = PersistenceHelper.manager.navigate(master, "data", TBOMMasterDataLink.class,
						false);
				while (result.hasMoreElements()) {
					TBOMMasterDataLink link = (TBOMMasterDataLink) result.nextElement();
					TBOMData data = link.getData();
					PersistenceHelper.manager.delete(data);
					PersistenceHelper.manager.delete(link);
				}

				PersistenceHelper.manager.delete(master);
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
	public void disconnect(Map<String, Object> params) throws Exception {
		ArrayList<String> arr = (ArrayList<String>) params.get("arr");
		String poid = (String) params.get("poid");
		Transaction trs = new Transaction();
		try {
			trs.start();

			Project project = (Project) CommonUtils.getObject(poid);
			for (String oid : arr) {
				TBOMMaster master = (TBOMMaster) CommonUtils.getObject(oid);
				QuerySpec query = new QuerySpec();
				int idx = query.appendClassList(TBOMMasterProjectLink.class, true);
				QuerySpecUtils.toEqualsAnd(query, idx, TBOMMasterProjectLink.class, "roleAObjectRef.key.id", master);
				QuerySpecUtils.toEqualsAnd(query, idx, TBOMMasterProjectLink.class, "roleBObjectRef.key.id", project);
				QueryResult qr = PersistenceHelper.manager.find(query);
				while (qr.hasMoreElements()) {
					TBOMMasterProjectLink link = (TBOMMasterProjectLink) qr.nextElement();
					PersistenceHelper.manager.delete(link);
				}

				QueryResult result = PersistenceHelper.manager.navigate(master, "output", OutputDocumentLink.class);
				while (result.hasMoreElements()) {
					Output output = (Output) result.nextElement();
					PersistenceHelper.manager.delete(output);
				}
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
	public void modify(TBOMDTO dto) throws Exception {
		String name = dto.getName();
		String oid = dto.getOid();
		String description = dto.getDescription();
		ArrayList<Map<String, Object>> addRows = dto.getAddRows(); // T-BOM
		ArrayList<Map<String, String>> addRows9 = dto.getAddRows9(); // 작번
		ArrayList<Map<String, String>> agreeRows = dto.getAgreeRows();
		ArrayList<Map<String, String>> approvalRows = dto.getApprovalRows();
		ArrayList<Map<String, String>> receiveRows = dto.getReceiveRows();
		ArrayList<String> secondarys = dto.getSecondarys();
		Transaction trs = new Transaction();
		try {
			trs.start();

			TBOMMaster master = (TBOMMaster) CommonUtils.getObject(oid);
			master.setName(name);
			master.setDescription(description);
			PersistenceHelper.manager.modify(master);

			// 표지 파일도 다시 생성해야함..
			CommonContentHelper.manager.clearS(master);

			for (int i = 0; secondarys != null && i < secondarys.size(); i++) {
				String cacheId = (String) secondarys.get(i);
				File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
				ApplicationData applicationData = ApplicationData.newApplicationData(master);
				applicationData.setRole(ContentRoleType.SECONDARY);
				PersistenceHelper.manager.save(applicationData);
				ContentServerHelper.service.updateContent(master, applicationData, vault.getPath());
			}

			QueryResult qr = PersistenceHelper.manager.navigate(master, "data", TBOMMasterDataLink.class, false);
			while (qr.hasMoreElements()) {
				TBOMMasterDataLink link = (TBOMMasterDataLink) qr.nextElement();
				TBOMData data = link.getData();
				PersistenceHelper.manager.delete(data);
				PersistenceHelper.manager.delete(link);
			}

			int sort = 0;
			for (Map<String, Object> addRow : addRows) { // tbom
				String koid = (String) addRow.get("oid"); // kepart..
				String unit = (String) addRow.get("unit");
				String code = (String) addRow.get("code");
				int qty = (int) addRow.get("qty");
				int lotNo = (int) addRow.get("lotNo");
				String provide = (String) addRow.get("provide");
				String discontinue = (String) addRow.get("discontinue");

				KePart kePart = (KePart) CommonUtils.getObject(koid);
				TBOMData data = TBOMData.newTBOMData();
				data.setKePart(kePart);
				data.setQty(qty);
				data.setLotNo(lotNo);
				data.setCode(code);
				data.setProvide(provide);
				data.setDiscontinue(discontinue);
				data.setUnit(unit);
				data.setSort(sort);
				PersistenceHelper.manager.save(data);

				TBOMMasterDataLink link = TBOMMasterDataLink.newTBOMMasterDataLink(master, data);
				link.setSort(sort);
				PersistenceHelper.manager.save(link);
				sort++;
			}

			// 기존 작번과 도면일람표 링크 제거
			QueryResult _qr = PersistenceHelper.manager.navigate(master, "project", TBOMMasterProjectLink.class, false);
			while (_qr.hasMoreElements()) {
				TBOMMasterProjectLink link = (TBOMMasterProjectLink) _qr.nextElement();
				PersistenceHelper.manager.delete(link);
			}

			// 기존 산출물 링크도 제거 후 다시 연결
			QueryResult navi = PersistenceHelper.manager.navigate(master, "output", OutputDocumentLink.class);
			while (navi.hasMoreElements()) {
				Output output = (Output) navi.nextElement();
				PersistenceHelper.manager.delete(output);
			}

			for (Map<String, String> addRow9 : addRows9) { // project
				String poid = (String) addRow9.get("oid");
				Project project = (Project) CommonUtils.getObject(poid);
				TBOMMasterProjectLink link = TBOMMasterProjectLink.newTBOMMasterProjectLink(master, project);
				PersistenceHelper.manager.save(link);

				Task task = ProjectHelper.manager.getTaskByName(project, "T-BOM");
				if (task == null) {
					throw new Exception(project.getKekNumber() + "작번에 T-BOM 태스크가 없습니다.");
				}
				// 산출물
				Output output = Output.newOutput();
				output.setName(master.getName());
				output.setLocation(master.getLocation());
				output.setTask(task);
				output.setProject(project);
				output.setDocument(master);
				output.setOwnership(CommonUtils.sessionOwner());
				output = (Output) PersistenceHelper.manager.save(output);

				// 태스크
				if (task.getStartDate() == null) {
					// 중복적으로 실제 시작일이 변경 되지 않게
					task.setStartDate(DateUtils.getCurrentTimestamp());
					task.setState(TaskStateVariable.INWORK);
				}
				task = (Task) PersistenceHelper.manager.modify(task);

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

			// 결재시작
			if (approvalRows.size() > 0) {
				WorkspaceHelper.manager.deleteAllLines(master); // 기존결재 잇으면 삭제 후 작업
				WorkspaceHelper.service.register(master, agreeRows, approvalRows, receiveRows);
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

			TBOMMaster master = (TBOMMaster) CommonUtils.getObject(oid);

			TBOMMaster pre = TBOMHelper.manager.getPreData(master);
			pre.setLatest(true);
			PersistenceHelper.manager.modify(pre);

			QueryResult result = PersistenceHelper.manager.navigate(master, "project", TBOMMasterProjectLink.class,
					false);
			while (result.hasMoreElements()) {
				TBOMMasterProjectLink link = (TBOMMasterProjectLink) result.nextElement();
				PersistenceHelper.manager.delete(link);
			}

			// 기존 산출물 링크도 제거 후 다시 연결
			QueryResult navi = PersistenceHelper.manager.navigate(master, "output", OutputDocumentLink.class);
			while (navi.hasMoreElements()) {
				Output output = (Output) navi.nextElement();
				PersistenceHelper.manager.delete(output);
			}

			result.reset();
			result = PersistenceHelper.manager.navigate(master, "data", TBOMMasterDataLink.class, false);
			while (result.hasMoreElements()) {
				TBOMMasterDataLink link = (TBOMMasterDataLink) result.nextElement();
				TBOMData data = link.getData();
				PersistenceHelper.manager.delete(data);
				PersistenceHelper.manager.delete(link);
			}

			PersistenceHelper.manager.delete(master);

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
	public void revise(TBOMDTO dto) throws Exception {
		String name = dto.getName();
		String description = dto.getDescription();
		ArrayList<Map<String, Object>> addRows = dto.getAddRows(); // T-BOM
		ArrayList<Map<String, String>> addRows9 = dto.getAddRows9(); // 작번
		ArrayList<Map<String, String>> agreeRows = dto.getAgreeRows();
		ArrayList<Map<String, String>> approvalRows = dto.getApprovalRows();
		ArrayList<Map<String, String>> receiveRows = dto.getReceiveRows();
		ArrayList<String> secondarys = dto.getSecondarys();
		Transaction trs = new Transaction();
		try {
			trs.start();

			TBOMMaster pre = (TBOMMaster) CommonUtils.getObject(dto.getOid());
			String preName = pre.getName();
			if(!preName.equals(dto.getName())) {
				pre.setName(dto.getName());
			}
			pre.setLatest(false);
			PersistenceHelper.manager.modify(pre);

			TBOMMaster master = TBOMMaster.newTBOMMaster();
			master.setDescription(description);
			master.setName(name);
			master.setNumber(pre.getNumber());
			master.setVersion(pre.getVersion() + 1);
			master.setLatest(true);
			master.setNote(dto.getNote());

			Folder folder = FolderTaskLogic.getFolder("/Default/프로젝트/T-BOM", CommonUtils.getPDMLinkProductContainer());
			FolderHelper.assignLocation((FolderEntry) master, folder);

			PersistenceHelper.manager.save(master);

			for (Map<String, String> addRow9 : addRows9) { // project
				String oid = (String) addRow9.get("oid");
				Project project = (Project) CommonUtils.getObject(oid);
				TBOMMasterProjectLink link = TBOMMasterProjectLink.newTBOMMasterProjectLink(master, project);
				PersistenceHelper.manager.save(link);

				Task task = ProjectHelper.manager.getTaskByName(project, "T-BOM");
				if (task == null) {
					throw new Exception(project.getKekNumber() + "작번에 T-BOM 태스크가 없습니다.");
				}
				// 산출물
				Output output = Output.newOutput();
				output.setName(master.getName());
				output.setLocation(master.getLocation());
				output.setTask(task);
				output.setProject(project);
				output.setDocument(master);
				output.setOwnership(CommonUtils.sessionOwner());
				output = (Output) PersistenceHelper.manager.save(output);

				// 태스크
				if (task.getStartDate() == null) {
					// 중복적으로 실제 시작일이 변경 되지 않게
					task.setStartDate(DateUtils.getCurrentTimestamp());
					task.setState(TaskStateVariable.INWORK);
				}

				task = (Task) PersistenceHelper.manager.modify(task);

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
			for (Map<String, Object> addRow : addRows) { // tbom
				String koid = (String) addRow.get("oid"); // kepart..
				String unit = (String) addRow.get("unit");
				String code = (String) addRow.get("code");
				int qty = (int) addRow.get("qty");
				int lotNo = (int) addRow.get("lotNo");
				String provide = (String) addRow.get("provide");
				String discontinue = (String) addRow.get("discontinue");

				KePart kePart = (KePart) CommonUtils.getObject(koid);
				TBOMData data = TBOMData.newTBOMData();
				data.setKePart(kePart);
				data.setQty(qty);
				data.setLotNo(lotNo);
				data.setCode(code);
				data.setProvide(provide);
				data.setDiscontinue(discontinue);
				data.setUnit(unit);
				data.setSort(sort);
				PersistenceHelper.manager.save(data);

				TBOMMasterDataLink link = TBOMMasterDataLink.newTBOMMasterDataLink(master, data);
				link.setSort(sort);
				PersistenceHelper.manager.save(link);
				sort++;
			}

			for (int i = 0; secondarys != null && i < secondarys.size(); i++) {
				String cacheId = (String) secondarys.get(i);
				File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
				ApplicationData dd = ApplicationData.newApplicationData(master);
				dd.setRole(ContentRoleType.SECONDARY);
				PersistenceHelper.manager.save(dd);
				ContentServerHelper.service.updateContent(master, dd, vault.getPath());
			}

			// 결재시작
			if (approvalRows.size() > 0) {
				WorkspaceHelper.service.register(master, agreeRows, approvalRows, receiveRows);
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
				TBOMMaster master = (TBOMMaster) CommonUtils.getObject(oid);

				QueryResult result = PersistenceHelper.manager.navigate(master, "output", TBOMMasterProjectLink.class);
				while (result.hasMoreElements()) {
					Output output = (Output) result.nextElement();
					Project p = output.getProject();

					if (p.getPersistInfo().getObjectIdentifier().getStringValue().equals(poid)) {
						trs.rollback();
						map.put("msg",
								"해당 T-BOM이 작번 : " + p.getKekNumber() + "의 태스크 : " + task.getName() + "에 연결이 되어있습니다.");
						map.put("exist", true);
						return map;
					}
				}

				TBOMMasterProjectLink link = TBOMMasterProjectLink.newTBOMMasterProjectLink(master, project);
				PersistenceHelper.manager.save(link);

				Output output = Output.newOutput();
				output.setName(master.getName());
				output.setLocation(master.getLocation());
				output.setTask(task);
				output.setProject(project);
				output.setDocument(master);
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
}
