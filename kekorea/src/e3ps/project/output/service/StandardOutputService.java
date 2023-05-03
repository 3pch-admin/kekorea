package e3ps.project.output.service;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import e3ps.common.content.service.CommonContentHelper;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.IBAUtils;
import e3ps.epm.numberRule.NumberRule;
import e3ps.epm.numberRule.NumberRulePersistableLink;
import e3ps.project.Project;
import e3ps.project.output.Output;
import e3ps.project.output.OutputDocumentLink;
import e3ps.project.output.dto.OutputDTO;
import e3ps.project.service.ProjectHelper;
import e3ps.project.task.Task;
import e3ps.project.task.variable.TaskStateVariable;
import e3ps.workspace.service.WorkspaceHelper;
import wt.clients.folder.FolderTaskLogic;
import wt.clients.vc.CheckInOutTaskLogic;
import wt.content.ApplicationData;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.doc.WTDocumentMasterIdentity;
import wt.fc.IdentityHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.org.WTUser;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.wip.CheckoutLink;
import wt.vc.wip.WorkInProgressHelper;

public class StandardOutputService extends StandardManager implements OutputService {

	public static StandardOutputService newStandardOutputService() throws WTException {
		StandardOutputService instance = new StandardOutputService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(OutputDTO dto) throws Exception {
		String name = dto.getName();
		String number = dto.getNumber();
		String location = dto.getLocation();
		String description = dto.getDescription();
		int progress = dto.getProgress();
		String toid = dto.getToid();
		ArrayList<Map<String, String>> addRows9 = dto.getAddRows9();
		ArrayList<Map<String, Object>> addRows11 = dto.getAddRows11();
		ArrayList<Map<String, String>> agreeRows = dto.getAgreeRows();
		ArrayList<Map<String, String>> approvalRows = dto.getApprovalRows();
		ArrayList<Map<String, String>> receiveRows = dto.getReceiveRows();
		ArrayList<String> primarys = dto.getPrimarys();
		// 태스크에서 바로 연결 시킬떄 생각..
		Transaction trs = new Transaction();
		try {
			trs.start();

			Task task = (Task) CommonUtils.getObject(toid);

			WTDocument document = WTDocument.newWTDocument();
			document.setName(name);
			document.setNumber(number);
			document.setDescription(description);

			Folder folder = FolderHelper.service.getFolder(location, CommonUtils.getPDMLinkProductContainer());
			FolderHelper.assignLocation((FolderEntry) document, folder);
			document = (WTDocument) PersistenceHelper.manager.save(document);

			// 도번 추가
			for (Map<String, Object> addRow11 : addRows11) {
				String oid = (String) addRow11.get("oid");
				NumberRule numberRule = (NumberRule) CommonUtils.getObject(oid);
				numberRule.setPersist(document.getMaster());
				PersistenceHelper.manager.modify(numberRule);
				IBAUtils.createIBA(document, "s", "NUMBER_RULE", numberRule.getMaster().getNumber());
				IBAUtils.createIBA(document, "s", "NUMBER_RULE_VERSION", String.valueOf(numberRule.getVersion()));
			}

			for (int i = 0; i < primarys.size(); i++) {
				String cacheId = (String) primarys.get(i);
				File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
				ApplicationData applicationData = ApplicationData.newApplicationData(document);
				if (i == 0) {
					applicationData.setRole(ContentRoleType.PRIMARY);
				} else {
					applicationData.setRole(ContentRoleType.SECONDARY);
				}
				PersistenceHelper.manager.save(applicationData);
				ContentServerHelper.service.updateContent(document, applicationData, vault.getPath());
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
				output.setTask(t);
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
				ProjectHelper.service.commit(project);
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
				WTDocument document = (WTDocument) CommonUtils.getObject(oid);

				QueryResult result = PersistenceHelper.manager.navigate(document, "output", OutputDocumentLink.class);
				while (result.hasMoreElements()) {
					Output output = (Output) result.nextElement();
					Project p = output.getProject();

					if (p.getPersistInfo().getObjectIdentifier().getStringValue().equals(oid)) {
						map.put("msg",
								"해당 산출물이 작번 : " + p.getKekNumber() + "의 태스크 : " + task.getName() + "에 연결이 되어있습니다.");
						map.put("exist", true);
						return map;
					}
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
	public void delete(String oid) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			WTDocument document = (WTDocument) CommonUtils.getObject(oid);

			QueryResult qr = PersistenceHelper.manager.navigate(document, "output", OutputDocumentLink.class, false);
			while (qr.hasMoreElements()) {
				OutputDocumentLink link = (OutputDocumentLink) qr.nextElement();
				Output output = link.getOutput();
				PersistenceHelper.manager.delete(output);
			}

			QueryResult result = PersistenceHelper.manager.navigate(document.getMaster(), "numberRule",
					NumberRulePersistableLink.class);
			while (result.hasMoreElements()) {
				NumberRule numberRule = (NumberRule) result.nextElement();
				numberRule.setPersist(null);
				PersistenceHelper.manager.modify(numberRule);
			}

			WorkspaceHelper.manager.deleteAllLines(document);

			PersistenceHelper.manager.delete(document);

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
		Transaction trs = new Transaction();
		try {
			trs.start();
			for (String oid : arr) {
				Output output = (Output) CommonUtils.getObject(oid);
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

	@Override
	public void modify(OutputDTO dto) throws Exception {
		String oid = dto.getOid();
		String name = dto.getName();
		String description = dto.getDescription();
		String location = dto.getLocation();
		boolean isSelf = dto.isSelf();
		int progress = dto.getProgress();
		ArrayList<String> primarys = dto.getPrimarys();
		ArrayList<Map<String, String>> addRows9 = dto.getAddRows9();
		ArrayList<Map<String, String>> agreeRows = dto.getAgreeRows();
		ArrayList<Map<String, String>> approvalRows = dto.getApprovalRows();
		ArrayList<Map<String, String>> receiveRows = dto.getReceiveRows();
		ArrayList<Map<String, Object>> addRows11 = dto.getAddRows11();
		Transaction trs = new Transaction();
		try {
			trs.start();

			WTDocument output = (WTDocument) CommonUtils.getObject(oid);

			Folder cFolder = CheckInOutTaskLogic.getCheckoutFolder();
			CheckoutLink clink = WorkInProgressHelper.service.checkout(output, cFolder, "산출물 수정 체크 아웃");
			WTDocument workCopy = (WTDocument) clink.getWorkingCopy();
			workCopy.setDescription(description);

			WTDocumentMaster master = (WTDocumentMaster) workCopy.getMaster();
			WTDocumentMasterIdentity identity = (WTDocumentMasterIdentity) master.getIdentificationObject();
			identity.setName(name);
			master = (WTDocumentMaster) IdentityHelper.service.changeIdentity(master, identity);

			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			String msg = user.getFullName() + " 사용자가 문서를 수정 하였습니다.";
			// 필요하면 수정 사유로 대체
			workCopy = (WTDocument) WorkInProgressHelper.service.checkin(workCopy, msg);

			Folder folder = FolderTaskLogic.getFolder(location, CommonUtils.getPDMLinkProductContainer());
			FolderHelper.service.changeFolder((FolderEntry) workCopy, folder);

			QueryResult result = PersistenceHelper.manager.navigate(master, "numberRule",
					NumberRulePersistableLink.class);
			while (result.hasMoreElements()) {
				NumberRule numberRule = (NumberRule) result.nextElement();
				numberRule.setPersist(null);
				PersistenceHelper.manager.modify(numberRule);
			}

			// 도번 추가
			for (Map<String, Object> addRow11 : addRows11) {
				NumberRule numberRule = (NumberRule) CommonUtils.getObject((String) addRow11.get("oid"));
				numberRule.setPersist(workCopy.getMaster());
				PersistenceHelper.manager.modify(numberRule);
				IBAUtils.createIBA(workCopy, "s", "NUMBER_RULE", numberRule.getMaster().getNumber());
				IBAUtils.createIBA(workCopy, "s", "NUMBER_RULE_VERSION", String.valueOf(numberRule.getVersion()));
			}

			// ???
			CommonContentHelper.manager.clear(workCopy);

			for (int i = 0; i < primarys.size(); i++) {
				String cacheId = (String) primarys.get(i);
				File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
				ApplicationData applicationData = ApplicationData.newApplicationData(workCopy);
				if (i == 0) {
					applicationData.setRole(ContentRoleType.PRIMARY);
				} else {
					applicationData.setRole(ContentRoleType.SECONDARY);
				}
				PersistenceHelper.manager.save(applicationData);
				ContentServerHelper.service.updateContent(workCopy, applicationData, vault.getPath());
			}

			// 프로젝트
			for (Map<String, String> addRow9 : addRows9) {
				Project project = (Project) CommonUtils.getObject(addRow9.get("oid"));
				// 폴더 이름이 태스크 명
//				Task t = ProjectHelper.manager.getTaskByName(project, task.getName());
				Task t = ProjectHelper.manager.getTaskByName(project, folder.getName());

				// 강제 에러 처리
				if (t == null) {
					throw new Exception(project.getKekNumber() + "작번에 태스크 명(" + folder.getName() + ")이 존재하지 않습니다.");
				}

				Output oo = Output.newOutput();
				oo.setName(workCopy.getName());
				oo.setLocation(workCopy.getLocation());
				oo.setTask(t);
				oo.setProject(project);
				oo.setDocument(workCopy);
				oo.setOwnership(CommonUtils.sessionOwner());
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
				ProjectHelper.service.commit(project);
			}

			if (isSelf) {
				WorkspaceHelper.service.self(workCopy.getPersistInfo().getObjectIdentifier().getStringValue());
			} else {
				// 결재시작
				if (approvalRows.size() > 0) {
					WorkspaceHelper.service.register(workCopy, agreeRows, approvalRows, receiveRows);
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
	public void revise(OutputDTO dto) throws Exception {
		String oid = dto.getOid();
		String name = dto.getName();
		String description = dto.getDescription();
		String location = dto.getLocation();
		boolean isSelf = dto.isSelf();
		int progress = dto.getProgress();
		ArrayList<String> primarys = dto.getPrimarys();
		ArrayList<Map<String, String>> addRows9 = dto.getAddRows9();
		ArrayList<Map<String, String>> agreeRows = dto.getAgreeRows();
		ArrayList<Map<String, String>> approvalRows = dto.getApprovalRows();
		ArrayList<Map<String, String>> receiveRows = dto.getReceiveRows();
		ArrayList<Map<String, Object>> addRows11 = dto.getAddRows11();
		Transaction trs = new Transaction();
		try {
			trs.start();

			WTDocument output = (WTDocument) CommonUtils.getObject(oid);

			WTDocument newDoc = (WTDocument) VersionControlHelper.service.newVersion(output);
			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			String msg = user.getFullName() + " 사용자가 문서를 개정 하였습니다.";
			VersionControlHelper.setNote(newDoc, msg);
			newDoc.setDescription(description);
			WTDocumentMaster master = (WTDocumentMaster) newDoc.getMaster();
			WTDocumentMasterIdentity identity = (WTDocumentMasterIdentity) master.getIdentificationObject();
			identity.setName(name);
			master = (WTDocumentMaster) IdentityHelper.service.changeIdentity(master, identity);

			// 필요하면 수정 사유로 대체
//			newDoc = (WTDocument) WorkInProgressHelper.service.checkin(newDoc, msg);
			PersistenceHelper.manager.save(newDoc);

			Folder folder = FolderTaskLogic.getFolder(location, CommonUtils.getPDMLinkProductContainer());
			FolderHelper.service.changeFolder((FolderEntry) newDoc, folder);

			QueryResult result = PersistenceHelper.manager.navigate(master, "numberRule",
					NumberRulePersistableLink.class);
			while (result.hasMoreElements()) {
				NumberRule numberRule = (NumberRule) result.nextElement();
				numberRule.setPersist(null);
				PersistenceHelper.manager.modify(numberRule);
			}

			// 도번 추가
			for (Map<String, Object> addRow11 : addRows11) {
				NumberRule numberRule = (NumberRule) CommonUtils.getObject((String) addRow11.get("oid"));
				numberRule.setPersist(newDoc.getMaster());
				PersistenceHelper.manager.modify(numberRule);
				IBAUtils.createIBA(newDoc, "s", "NUMBER_RULE", numberRule.getMaster().getNumber());
				IBAUtils.createIBA(newDoc, "s", "NUMBER_RULE_VERSION", String.valueOf(numberRule.getVersion()));
			}

			// ???
			CommonContentHelper.manager.clear(newDoc);

			for (int i = 0; i < primarys.size(); i++) {
				String cacheId = (String) primarys.get(i);
				File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
				ApplicationData applicationData = ApplicationData.newApplicationData(newDoc);
				if (i == 0) {
					applicationData.setRole(ContentRoleType.PRIMARY);
				} else {
					applicationData.setRole(ContentRoleType.SECONDARY);
				}
				PersistenceHelper.manager.save(applicationData);
				ContentServerHelper.service.updateContent(newDoc, applicationData, vault.getPath());
			}

			// 프로젝트
			for (Map<String, String> addRow9 : addRows9) {
				Project project = (Project) CommonUtils.getObject(addRow9.get("oid"));
				// 폴더 이름이 태스크 명
//				Task t = ProjectHelper.manager.getTaskByName(project, task.getName());
				Task t = ProjectHelper.manager.getTaskByName(project, folder.getName());

				// 강제 에러 처리
				if (t == null) {
					throw new Exception(project.getKekNumber() + "작번에 태스크 명(" + folder.getName() + ")이 존재하지 않습니다.");
				}

				Output oo = Output.newOutput();
				oo.setName(newDoc.getName());
				oo.setLocation(newDoc.getLocation());
				oo.setTask(t);
				oo.setProject(project);
				oo.setDocument(newDoc);
				oo.setOwnership(CommonUtils.sessionOwner());
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
				ProjectHelper.service.commit(project);
			}

			if (isSelf) {
				WorkspaceHelper.service.self(newDoc.getPersistInfo().getObjectIdentifier().getStringValue());
			} else {
				// 결재시작
				if (approvalRows.size() > 0) {
					WorkspaceHelper.service.register(newDoc, agreeRows, approvalRows, receiveRows);
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
}
