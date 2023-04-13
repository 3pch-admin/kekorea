package e3ps.workspace.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.bom.partlist.PartListMaster;
import e3ps.common.Constants;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.doc.request.RequestDocument;
import e3ps.doc.request.RequestDocumentProjectLink;
import e3ps.epm.keDrawing.KeDrawing;
import e3ps.erp.service.ErpHelper;
import e3ps.org.Department;
import e3ps.org.People;
import e3ps.org.PeopleWTUserLink;
import e3ps.part.kePart.KePart;
import e3ps.project.Project;
import e3ps.project.ProjectUserLink;
import e3ps.project.task.Task;
import e3ps.project.task.variable.TaskStateVariable;
import e3ps.project.variable.ProjectStateVariable;
import e3ps.workspace.ApprovalContract;
import e3ps.workspace.ApprovalContractPersistableLink;
import e3ps.workspace.ApprovalLine;
import e3ps.workspace.ApprovalMaster;
import e3ps.workspace.notification.service.NotificationHelper;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.org.WTUser;
import wt.ownership.Ownership;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardWorkspaceService extends StandardManager implements WorkspaceService {

	private static final long serialVersionUID = -3473333219484101189L;

	public static StandardWorkspaceService newStandardWorkspaceService() throws WTException {
		StandardWorkspaceService instance = new StandardWorkspaceService();
		instance.initialize();
		return instance;
	}

	@Override
	public void register(Persistable persistable, ArrayList<Map<String, String>> agreeRows,
			ArrayList<Map<String, String>> approvalRows, ArrayList<Map<String, String>> receiveRows) throws Exception {
		boolean isAgree = !agreeRows.isEmpty();
		Transaction trs = new Transaction();
		try {
			trs.start();

			WTUser sessionUser = CommonUtils.sessionUser();
			Timestamp startTime = new Timestamp(new Date().getTime());
			Ownership ownership = CommonUtils.sessionOwner();
			String name = WorkspaceHelper.manager.getName(persistable);
//			String description = WorkspaceHelper.manager.getDescription(persistable);
			String description = "";

			// 마스터 생성..
			ApprovalMaster master = ApprovalMaster.newApprovalMaster();
			master.setName(name);
			master.setCompleteTime(null);
			master.setOwnership(ownership);
			master.setPersist(persistable);
			master.setStartTime(startTime);
			if (isAgree) {
				master.setState(WorkspaceHelper.STATE_AGREE_READY);
			} else {
				master.setState(WorkspaceHelper.STATE_APPROVAL_APPROVING);
			}
			master.setCompleteUserID(sessionUser.getName());
			master = (ApprovalMaster) PersistenceHelper.manager.save(master);

			// 기안 라인
			ApprovalLine submitLine = ApprovalLine.newApprovalLine();
			submitLine.setName(name);
			submitLine.setOwnership(ownership);
			submitLine.setMaster(master);
			submitLine.setReads(true);
			submitLine.setSort(-50);
			submitLine.setStartTime(startTime);
			submitLine.setType(WorkspaceHelper.SUBMIT_LINE);
			submitLine.setRole(WorkspaceHelper.WORKING_SUBMIT);
			submitLine.setDescription(description);
			submitLine.setCompleteUserID(sessionUser.getName());
			submitLine.setCompleteTime(startTime);
			submitLine.setState(WorkspaceHelper.STATE_SUBMIT_COMPLETE);
			PersistenceHelper.manager.save(submitLine);

			System.out.println("isAgree=" + isAgree);
			int sort = 0;
			if (isAgree) {
				sort = 1;
				for (Map<String, String> agree : agreeRows) {
					String woid = agree.get("woid");
					WTUser wtuser = (WTUser) CommonUtils.getObject(woid);
					// 검토 라인 생성
					ApprovalLine agreeLine = ApprovalLine.newApprovalLine();
					agreeLine.setName(name);
					agreeLine.setOwnership(Ownership.newOwnership(wtuser));
					agreeLine.setMaster(master);
					agreeLine.setReads(true);
					agreeLine.setSort(0);
					agreeLine.setStartTime(startTime);
					agreeLine.setType(WorkspaceHelper.AGREE_LINE);
					agreeLine.setRole(WorkspaceHelper.WORKING_AGREE);
					agreeLine.setDescription(null);
					agreeLine.setCompleteUserID(null);
					agreeLine.setCompleteTime(null);
					agreeLine.setState(WorkspaceHelper.STATE_AGREE_READY);
					PersistenceHelper.manager.save(agreeLine);

					NotificationHelper.service.sendTo("메세지 테스트용", "하잉", wtuser);
				}
			}

			for (Map<String, String> approval : approvalRows) {
				String woid = approval.get("woid");
				WTUser wtuser = (WTUser) CommonUtils.getObject(woid);
				// 결재 라인 생성
				ApprovalLine approvalLine = ApprovalLine.newApprovalLine();
				approvalLine.setName(name);
				approvalLine.setOwnership(Ownership.newOwnership(wtuser));
				approvalLine.setMaster(master);
				approvalLine.setReads(false);
				approvalLine.setSort(sort);
				approvalLine.setType(WorkspaceHelper.APPROVAL_LINE);
				approvalLine.setRole(WorkspaceHelper.WORKING_APPROVAL);
				approvalLine.setDescription(null);

				// 검토가 있을 경우
				if (isAgree) {
					approvalLine.setStartTime(null);
					approvalLine.setState(WorkspaceHelper.STATE_APPROVAL_READY);
					approvalLine.setCompleteTime(null);
					approvalLine.setCompleteUserID(null);
				} else {
					// 검토 없을경우
					if (sort == 0) {
						approvalLine.setStartTime(startTime);
						approvalLine.setState(WorkspaceHelper.STATE_APPROVAL_APPROVING);
						approvalLine.setCompleteTime(null);
						approvalLine.setCompleteUserID(null);
						NotificationHelper.service.sendTo("결재 메세지용", "하1잉", wtuser);
					} else {
						approvalLine.setStartTime(null);
						approvalLine.setState(WorkspaceHelper.STATE_APPROVAL_READY);
						approvalLine.setCompleteTime(null);
						approvalLine.setCompleteUserID(null);
					}
				}
				PersistenceHelper.manager.save(approvalLine);
				sort += 1;
			}

			for (Map<String, String> receive : receiveRows) {
				String woid = receive.get("woid");
				WTUser wtuser = (WTUser) CommonUtils.getObject(woid);
				// 결재 라인 생성
				ApprovalLine receiveLine = ApprovalLine.newApprovalLine();
				receiveLine.setName(name);
				receiveLine.setOwnership(Ownership.newOwnership(wtuser));
				receiveLine.setMaster(master);
				receiveLine.setReads(true);
				receiveLine.setSort(0);
				receiveLine.setStartTime(startTime);
				receiveLine.setType(WorkspaceHelper.RECEIVE_LINE);
				receiveLine.setRole(WorkspaceHelper.WORKING_RECEIVE);
				receiveLine.setDescription(null);
				receiveLine.setCompleteUserID(null);
				receiveLine.setCompleteTime(null);
				receiveLine.setState(WorkspaceHelper.STATE_RECEIVE_READY);
				PersistenceHelper.manager.save(receiveLine);
			}

			// 객체 상태 변경
			if (persistable instanceof LifeCycleManaged) {
				LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) persistable,
						State.toState("UNDERAPPROVAL"));
				// 일괄결재..
			} else if (persistable instanceof ApprovalContract) {

			} else if (persistable instanceof KePart) {
				KePart kePart = (KePart) persistable;
				kePart.setState(Constants.State.APPROVED);
				PersistenceHelper.manager.modify(kePart);
			} else if (persistable instanceof KeDrawing) {
				KeDrawing keDrawing = (KeDrawing) persistable;
				keDrawing.setState(Constants.State.APPROVED);
				PersistenceHelper.manager.modify(keDrawing);
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
	public void _agree(Map<String, Object> params) throws Exception {
		String oid = (String) params.get("oid");
		String description = (String) params.get("description");
		Transaction trs = new Transaction();
		try {
			trs.start();

			Timestamp completeTime = new Timestamp(new Date().getTime());
			ApprovalLine line = (ApprovalLine) CommonUtils.getObject(oid);
			ApprovalMaster master = line.getMaster();
			line.setDescription(description);
			line.setCompleteTime(completeTime);
			line.setState(WorkspaceHelper.STATE_AGREE_COMPLETE);
			line.setCompleteUserID(CommonUtils.sessionUser().getName());
			line = (ApprovalLine) PersistenceHelper.manager.modify(line);

			boolean isEndAgree = WorkspaceHelper.manager.isEndAgree(master);
			if (isEndAgree) {
				ArrayList<ApprovalLine> approvalLines = WorkspaceHelper.manager.getApprovalLines(master);
				for (ApprovalLine approvalLine : approvalLines) {
					int sort = approvalLine.getSort();
					approvalLine.setSort(sort - 1);
					approvalLine = (ApprovalLine) PersistenceHelper.manager.modify(approvalLine);
					approvalLine = (ApprovalLine) PersistenceHelper.manager.refresh(approvalLine);

					if (approvalLine.getSort() == 0) {
						approvalLine.setStartTime(completeTime);
						approvalLine.setState(WorkspaceHelper.STATE_APPROVAL_APPROVING);
						PersistenceHelper.manager.modify(approvalLine);

						// 마스터 상태값도 변경
						master.setState(WorkspaceHelper.STATE_MASTER_APPROVAL_APPROVING);
						PersistenceHelper.manager.modify(master);
					}
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
	public void _unagree(Map<String, Object> params) throws Exception {
		String oid = (String) params.get("oid");
		String description = (String) params.get("description");
		Transaction trs = new Transaction();
		try {
			trs.start();

			Timestamp completeTime = new Timestamp(new Date().getTime());
			ApprovalLine line = (ApprovalLine) CommonUtils.getObject(oid);
			line.setDescription(description);
			line.setCompleteTime(completeTime);
			line.setCompleteUserID(CommonUtils.sessionUser().getName());
			line.setState(WorkspaceHelper.STATE_AGREE_REJECT);
			line = (ApprovalLine) PersistenceHelper.manager.modify(line);

			ApprovalMaster master = line.getMaster();
			master.setCompleteTime(completeTime);
			master.setState(WorkspaceHelper.STATE_MASTER_AGREE_REJECT);
			master = (ApprovalMaster) PersistenceHelper.manager.modify(master);

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
	public void _approval(Map<String, Object> params) throws Exception {
		String oid = (String) params.get("oid");
		String description = (String) params.get("description");
		Transaction trs = new Transaction();
		try {
			trs.start();

			String sessionUserName = CommonUtils.sessionUser().getName();
			Timestamp completeTime = new Timestamp(new Date().getTime());
			ApprovalLine line = (ApprovalLine) CommonUtils.getObject(oid);

			if (StringUtils.isNull(description)) {
				description = "승인합니다.";
			}

			line.setDescription(description);
			line.setCompleteTime(completeTime);
			line.setCompleteUserID(sessionUserName);
			line.setState(WorkspaceHelper.STATE_APPROVAL_COMPLETE);
			line = (ApprovalLine) PersistenceHelper.manager.modify(line);

			ApprovalMaster master = line.getMaster();
			Persistable per = master.getPersist();

			ArrayList<ApprovalLine> approvalLines = WorkspaceHelper.manager.getApprovalLines(master);
			for (ApprovalLine approvalLine : approvalLines) {
				int sort = approvalLine.getSort();
				if (sort == 1) {
					approvalLine.setStartTime(completeTime);
					approvalLine.setState(WorkspaceHelper.STATE_APPROVAL_APPROVING);
				}
				approvalLine.setSort(sort - 1);
				approvalLine = (ApprovalLine) PersistenceHelper.manager.modify(approvalLine);
			}

			master.setState(WorkspaceHelper.STATE_MASTER_APPROVAL_APPROVING);
			master = (ApprovalMaster) PersistenceHelper.manager.modify(master);

			boolean isEndApprovalLine = WorkspaceHelper.manager.isEndApprovalLine(master, 0);
			if (isEndApprovalLine) {
				master.setCompleteTime(completeTime);
				master.setState(WorkspaceHelper.STATE_MASTER_APPROVAL_COMPELTE);
				PersistenceHelper.manager.modify(master);

				if (per instanceof LifeCycleManaged) {
					State state = State.toState("APPROVED");
					per = (Persistable) LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) per, state);

					if (per instanceof RequestDocument) {
						RequestDocument requestDocument = (RequestDocument) per;
						settingUser(requestDocument, master);
					}

					// 최종 결재자 세팅
					if (per instanceof PartListMaster) {
						PartListMaster mm = (PartListMaster) per;
						mm.setLast(sessionUserName);
						PersistenceHelper.manager.modify(mm);
						mm = (PartListMaster) PersistenceHelper.manager.refresh(mm);
					}

//					if (per instanceof RequestDocument) {
//						RequestDocument req = (RequestDocument) per;
//						setProjectRoleUser(req, master);
//					} else if (per instanceof WTDocument) {
//						setTaskStateCheck(per);
//					}
					sendToERP(per);
				} else if (per instanceof ApprovalContract) {
					ApprovalContract contract = (ApprovalContract) per;
//					approvalContract(contract);
				} else if (per instanceof KePart) {
					KePart kePart = (KePart) per;
					kePart.setState(Constants.State.APPROVED);
					PersistenceHelper.manager.modify(kePart);
				} else if (per instanceof KeDrawing) {
					KeDrawing keDrawing = (KeDrawing) per;
					keDrawing.setState(Constants.State.APPROVED);
					PersistenceHelper.manager.modify(keDrawing);
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

	/**
	 * 의뢰서 결재 완료시 작업되는 부분
	 */
	private void settingUser(RequestDocument requestDocument, ApprovalMaster master) throws Exception {

		QueryResult result = PersistenceHelper.manager.navigate(requestDocument, "project",
				RequestDocumentProjectLink.class);
		while (result.hasMoreElements()) {
			Project project = (Project) result.nextElement();

			Timestamp time = new Timestamp(new Date().getTime());
			project.setStartDate(time);
			project.setKekState("설계중");
			project.setState(ProjectStateVariable.INWORK);
			project = (Project) PersistenceHelper.manager.modify(project);

			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(Task.class, true);
			QuerySpecUtils.toEqualsAnd(query, idx, Task.class, "projectReference.key.id", project);
			QuerySpecUtils.toEqualsAnd(query, idx, Task.class, Task.NAME, "의뢰서");
			QueryResult qr = PersistenceHelper.manager.find(query);
			if (qr.hasMoreElements()) {
				Object[] obj = (Object[]) qr.nextElement();
				Task tt = (Task) obj[0];
				tt.setState(TaskStateVariable.COMPLETE);
				tt.setEndDate(time);
				PersistenceHelper.manager.modify(tt);
			}

			ArrayList<ApprovalLine> agreeLines = WorkspaceHelper.manager.getAgreeLines(master);
			for (ApprovalLine agreeLine : agreeLines) {
				WTUser user = (WTUser) agreeLine.getOwnership().getOwner().getPrincipal();

				QueryResult _qr = PersistenceHelper.manager.navigate(user, "people", PeopleWTUserLink.class);

				if (_qr.hasMoreElements()) {
					People pp = (People) _qr.nextElement();
					Department department = pp.getDepartment();
					String name = department.getName();

					if ("기계설계".equals(name)) {
						ProjectUserLink userLink = ProjectUserLink.newProjectUserLink(project, user);
						userLink.setUserType(CommonCodeHelper.manager.getCommonCode("MACHINE", "USER_TYPE"));
						PersistenceHelper.manager.save(userLink);
					} else if ("전기설계".equals(name)) {
						ProjectUserLink userLink = ProjectUserLink.newProjectUserLink(project, user);
						userLink.setUserType(CommonCodeHelper.manager.getCommonCode("ELEC", "USER_TYPE"));
						PersistenceHelper.manager.save(userLink);
					} else if ("SW설계".equals(name)) {
						ProjectUserLink userLink = ProjectUserLink.newProjectUserLink(project, user);
						userLink.setUserType(CommonCodeHelper.manager.getCommonCode("SOFT", "USER_TYPE"));
						PersistenceHelper.manager.save(userLink);
					}
				}
			}
		}
	}

	/**
	 * 산출물 전송
	 */
	private void sendToERP(Persistable per) throws Exception {
		if (per instanceof WTDocument) {
			WTDocument document = (WTDocument) per;
			ErpHelper.manager.sendToErp(document);
		} else if (per instanceof PartListMaster) {
			PartListMaster mm = (PartListMaster) per;
			ErpHelper.manager.sendToErp(mm);
		}
	}

	@Override
	public void _reject(Map<String, Object> params) throws Exception {
		String oid = (String) params.get("oid");
		String description = (String) params.get("description");
		Transaction trs = new Transaction();
		try {
			trs.start();

			Timestamp completeTime = new Timestamp(new Date().getTime());
			ApprovalLine line = (ApprovalLine) CommonUtils.getObject(oid);

			if (StringUtils.isNull(description)) {
				description = "반려합니다.";
			}

			line.setDescription(description);
			line.setCompleteTime(completeTime);
			line.setCompleteUserID(CommonUtils.sessionUser().getName());
			line.setState(WorkspaceHelper.STATE_APPROVAL_REJECT);
			line = (ApprovalLine) PersistenceHelper.manager.modify(line);

			ApprovalMaster master = line.getMaster();
			master.setCompleteTime(completeTime);
			master.setState(WorkspaceHelper.STATE_MASTER_APPROVAL_REJECT);
			master = (ApprovalMaster) PersistenceHelper.manager.modify(master);

			Persistable per = master.getPersist();
			if (per instanceof LifeCycleManaged) {
				LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) per, State.toState("RETURN"));
			} else if (per instanceof ApprovalContract) {
				ApprovalContract contract = (ApprovalContract) per;

				QueryResult result = PersistenceHelper.manager.navigate(contract, "persist",
						ApprovalContractPersistableLink.class);
				while (result.hasMoreElements()) {
					Persistable pp = (Persistable) result.nextElement();
					if (pp instanceof LifeCycleManaged) {
						LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) pp, State.toState("RETURN"));
					} else {

					}
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
	public void _receive(Map<String, Object> params) throws Exception {
		String oid = (String) params.get("oid");
		String description = (String) params.get("description");
		Transaction trs = new Transaction();
		try {
			trs.start();

			Timestamp completeTime = new Timestamp(new Date().getTime());
			ApprovalLine line = (ApprovalLine) CommonUtils.getObject(oid);
			line.setDescription(description);
			line.setCompleteTime(completeTime);
			line.setCompleteUserID(CommonUtils.sessionUser().getName());
			line.setState(WorkspaceHelper.STATE_RECEIVE_COMPLETE);
			line = (ApprovalLine) PersistenceHelper.manager.modify(line);

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
