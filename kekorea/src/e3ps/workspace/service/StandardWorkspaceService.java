package e3ps.workspace.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.bom.partlist.PartListMaster;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.DateUtils;
import e3ps.common.util.IBAUtils;
import e3ps.common.util.StringUtils;
import e3ps.doc.PRJDocument;
import e3ps.doc.ReqDocumentProjectLink;
import e3ps.doc.RequestDocument;
import e3ps.epm.service.EpmHelper;
import e3ps.erp.service.ErpHelper;
import e3ps.migrator.MigratorHelper;
import e3ps.org.People;
import e3ps.org.WTUserPeopleLink;
import e3ps.org.dto.UserViewData;
import e3ps.part.beans.PartViewData;
import e3ps.part.service.PartHelper;
import e3ps.project.Project;
import e3ps.project.ProjectUserLink;
import e3ps.project.enums.ProjectStateType;
import e3ps.project.enums.ProjectUserType;
import e3ps.project.enums.TaskStateType;
import e3ps.project.output.DocumentOutputLink;
import e3ps.project.output.Output;
import e3ps.project.output.TaskOutputLink;
import e3ps.project.service.ProjectHelper;
import e3ps.project.task.Task;
import e3ps.workspace.ApprovalContract;
import e3ps.workspace.ApprovalContractPersistableLink;
import e3ps.workspace.ApprovalLine;
import e3ps.workspace.ApprovalMaster;
import e3ps.workspace.notification.service.NotificationHelper;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.epm.structure.EPMReferenceLink;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.LifeCycleTemplate;
import wt.lifecycle.State;
import wt.org.WTUser;
import wt.ownership.Ownership;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.WTException;

public class StandardWorkspaceService extends StandardManager implements WorkspaceService {

	private static final long serialVersionUID = -3473333219484101189L;

	public static StandardWorkspaceService newStandardApprovalService() throws WTException {
		StandardWorkspaceService instance = new StandardWorkspaceService();
		instance.initialize();
		return instance;
	}

	@Override
	public void submitApp(Persistable per, Map<String, Object> param) throws WTException {
		List<String> appList = (List<String>) param.get("appList"); // 결재
		List<String> agreeList = (List<String>) param.get("agreeList"); // 검토
		List<String> receiveList = (List<String>) param.get("receiveList"); // 수신

		ApprovalMaster master = null;
		Timestamp startTime = new Timestamp(new Date().getTime());
		ReferenceFactory rf = new ReferenceFactory();

		// 검토가있음..
		boolean isAgree = !StringUtils.isNull(agreeList);

		Transaction trs = new Transaction();
		try {
			trs.start();

			// 결재 이름
			String name = WorkspaceHelper.manager.getLineName(per);

			// 검토가 있던 없던 기안자는 무조건 생성..
			// 기안자..
			Ownership ownership = Ownership.newOwnership(SessionHelper.manager.getPrincipal());

			WTUser completeUser = (WTUser) SessionHelper.manager.getPrincipal();

			String desc = null;

			if (per instanceof PartListMaster) {
				PartListMaster d = (PartListMaster) per;
				desc = StringUtils.replaceToValue(d.getDescription());
			} else if (per instanceof WTDocument) {
				WTDocument d = (WTDocument) per;
				desc = StringUtils.replaceToValue(d.getDescription());
			} else if (per instanceof PRJDocument) {
				PRJDocument d = (PRJDocument) per;
				desc = StringUtils.replaceToValue(d.getDescription());
			} else if (per instanceof ApprovalContract) {
				ApprovalContract contract = (ApprovalContract) per;
				desc = StringUtils.replaceToValue(contract.getDescription());
			}

			if (StringUtils.isNull(desc)) {
				desc = ownership.getOwner().getFullName() + " 사용자가 결재를 제출 하였습니다.";
			}

			// 기안자생성
			master = ApprovalMaster.newApprovalMaster();
			master.setName(name);
			master.setCompleteTime(null);
			master.setOwnership(ownership);
			master.setPersist(per);
			master.setStartTime(startTime);

			if (isAgree) {
				master.setState(WorkspaceHelper.LINE_AGREE_STAND);
			} else {
				master.setState(WorkspaceHelper.LINE_APPROVING);
			}
			master.setCompleteUserID(completeUser.getName());
			master = (ApprovalMaster) PersistenceHelper.manager.save(master);

			// 검토가 있을 경우..
			ApprovalLine startLine = ApprovalLine.newApprovalLine();
			startLine.setName(master.getName());
			startLine.setOwnership(ownership);
			startLine.setMaster(master);
			startLine.setReads(true);
			startLine.setSort(-50);
			startLine.setStartTime(startTime);
			startLine.setType(WorkspaceHelper.APP_LINE);
			// 기안자
			startLine.setRole(WorkspaceHelper.WORKING_SUBMIT);

			// startLine.setDescription(ownership.getOwner().getFullName() + " 사용자가 결재를 제출
			// 하였습니다.");
			startLine.setDescription(desc);
			startLine.setCompleteUserID(completeUser.getName());
			startLine.setState(WorkspaceHelper.LINE_SUBMIT_COMPLETE);
			startLine.setCompleteTime(startTime);

			startLine = (ApprovalLine) PersistenceHelper.manager.save(startLine);

			if (isAgree) {

				for (int i = 0; i < agreeList.size(); i++) {
					String oid = (String) agreeList.get(i);
					People user = (People) rf.getReference(oid).getObject();

					// 검토 라인 생성
					ApprovalLine agreeLine = ApprovalLine.newApprovalLine();
					agreeLine.setName(master.getName());
					agreeLine.setOwnership(Ownership.newOwnership(user.getUser()));
					agreeLine.setCompleteTime(null);
					agreeLine.setDescription(null);
					agreeLine.setMaster(master);
					agreeLine.setReads(false);
					agreeLine.setSort(0);
					// agreeLine.setDescription(ownership.getOwner().getFullName() + " 사용자가 검토요청을
					// 하였습니다.");
					agreeLine.setType(WorkspaceHelper.AGREE_LINE);
					agreeLine.setRole(WorkspaceHelper.WORKING_AGREE);
					agreeLine.setStartTime(startTime);
					agreeLine.setState(WorkspaceHelper.LINE_AGREE_STAND);
					agreeLine = (ApprovalLine) PersistenceHelper.manager.save(agreeLine);
				}
			}

			int sort = 0;
			if (isAgree) {
				sort = 1;
			}

			for (int i = 0; appList != null && i < appList.size(); i++) {
				// oid.. user oid not wtuser oid
				String oid = (String) appList.get(i);
				People user = (People) rf.getReference(oid).getObject();
				ApprovalLine appLine = ApprovalLine.newApprovalLine();
				appLine.setName(master.getName());
				appLine.setOwnership(Ownership.newOwnership(user.getUser()));
				appLine.setCompleteTime(null);
				appLine.setDescription(null);
				appLine.setMaster(master);
				appLine.setType(WorkspaceHelper.APP_LINE);
				appLine.setReads(false);
				appLine.setRole(WorkspaceHelper.WORKING_APPROVAL);
				appLine.setSort(sort);
				// if (i == 0) {
				if (!isAgree) {
					// 직렬 단순
					if (i == 0) {
						appLine.setStartTime(startTime);
						appLine.setState(WorkspaceHelper.LINE_APPROVING);
					} else {
						appLine.setStartTime(null);
						appLine.setState(WorkspaceHelper.LINE_STAND);
					}
					appLine = (ApprovalLine) PersistenceHelper.manager.save(appLine);
				} else {
					appLine.setStartTime(null);
					appLine.setState(WorkspaceHelper.LINE_STAND);
					appLine = (ApprovalLine) PersistenceHelper.manager.save(appLine);
				}
				sort += 1;
			}

			for (int i = 0; receiveList != null && i < receiveList.size(); i++) {
				// oid.. user oid not wtuser oid
				String oid = (String) receiveList.get(i);
				People user = (People) rf.getReference(oid).getObject();
				ApprovalLine receiveLine = ApprovalLine.newApprovalLine();
				receiveLine.setName(master.getName());
				receiveLine.setOwnership(Ownership.newOwnership(user.getUser()));
				receiveLine.setCompleteTime(null);
				receiveLine.setDescription(null);
				receiveLine.setMaster(master);
				receiveLine.setType(WorkspaceHelper.RECEIVE_LINE);
				receiveLine.setReads(false);
				receiveLine.setSort(0);
				receiveLine.setRole(WorkspaceHelper.WORKING_RECEIVE);
				// 수신 바로 시작
				receiveLine.setStartTime(startTime);
				receiveLine.setState(WorkspaceHelper.LINE_RECEIVE_STAND);
				receiveLine = (ApprovalLine) PersistenceHelper.manager.save(receiveLine);

			}

			if (per instanceof LifeCycleManaged) {
				LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) per, State.toState("UNDERAPPROVAL"));
			} else if (per instanceof ApprovalContract) {
				ApprovalContract contract = (ApprovalContract) per;
				QueryResult result = PersistenceHelper.manager.navigate(contract, "persist",
						ApprovalContractPersistableLink.class, false);
				while (result.hasMoreElements()) {
					ApprovalContractPersistableLink link = (ApprovalContractPersistableLink) result.nextElement();
					Persistable pers = link.getPersist();

					if (pers instanceof LifeCycleManaged) {

						LifeCycleTemplate lct = LifeCycleHelper.service
								.getLifeCycleTemplate(MigratorHelper.LIFECYCLE_NAME, CommonUtils.getContainer());
						LifeCycleHelper.service.reassign((LifeCycleManaged) pers, lct.getLifeCycleTemplateReference());

						System.out.println("변경 결재..");
						pers = (Persistable) PersistenceHelper.manager.refresh(pers);

						if (pers instanceof WTPart) {
							WTPart part = (WTPart) pers;
							part = (WTPart) CommonUtils.getLatestVersion(part);
							State s = State.toState("UNDERAPPROVAL");
							LifeCycleHelper.service.setLifeCycleState(part, s);
						} else if (pers instanceof EPMDocument) {
							EPMDocument epm = (EPMDocument) pers;
							epm = (EPMDocument) CommonUtils.getLatestVersion(epm);

							System.out.println("epm=" + epm.getNumber());

							State s = State.toState("UNDERAPPROVAL");
							LifeCycleHelper.service.setLifeCycleState(epm, s);

							WTPart part = EpmHelper.manager.getPart(epm);
							if (part != null) {
								part = (WTPart) CommonUtils.getLatestVersion(part);
								LifeCycleHelper.service.setLifeCycleState(part, s);
							}
						} else if (pers instanceof WTDocument) {
							WTDocument doc = (WTDocument) pers;
							doc = (WTDocument) CommonUtils.getLatestVersion(doc);
							State s = State.toState("UNDERAPPROVAL");
							LifeCycleHelper.service.setLifeCycleState(doc, s);
						}
					}
				}
			}

			// 메일링 서비스...

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();

		}

	}

	@Override
	public Map<String, Object> approvalAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		ReferenceFactory rf = new ReferenceFactory();
		ApprovalLine line = null;
		String oid = (String) param.get("oid");
		String description = (String) param.get("description");
		Transaction trs = new Transaction();
		try {
			trs.start();

			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();

			line = (ApprovalLine) rf.getReference(oid).getObject();
			ApprovalMaster master = line.getMaster();
			Persistable per = master.getPersist();

			Timestamp completeTime = new Timestamp(new Date().getTime());

			if (StringUtils.isNull(description)) {
				description = "승인합니다.";
			}

			line.setDescription(description);
			line.setCompleteTime(completeTime);
			line.setCompleteUserID(user.getName());
			line.setState(WorkspaceHelper.LINE_APPROVAL_COMPLETE);
			line = (ApprovalLine) PersistenceHelper.manager.modify(line);

			ArrayList<ApprovalLine> appLines = WorkspaceHelper.manager.getAppLines(master);
			for (ApprovalLine appLine : appLines) {
				int sort = appLine.getSort();
				if (sort == 1) {
					appLine.setStartTime(completeTime);
					appLine.setState(WorkspaceHelper.LINE_APPROVING);
				}

				appLine.setSort(sort - 1);
				appLine = (ApprovalLine) PersistenceHelper.manager.modify(appLine);
			}

			master.setState(WorkspaceHelper.LINE_APPROVING);
			master = (ApprovalMaster) PersistenceHelper.manager.modify(master);
			// master = (ApprovalMaster) PersistenceHelper.manager.refresh(master);

			// 0 을 기준..
			boolean isLastAppLine = WorkspaceHelper.manager.isLastAppLine(master, 0);
			if (isLastAppLine) {
				master.setCompleteTime(completeTime);
				master.setState(WorkspaceHelper.MASTER_APPROVAL_COMPLETE);
				PersistenceHelper.manager.modify(master);

				if (per instanceof LifeCycleManaged) {
					State state = State.toState("APPROVED");
					per = (Persistable) LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) per, state);
					if (per instanceof RequestDocument) {
						RequestDocument req = (RequestDocument) per;
						setProjectRoleUser(req, master);
					} else if (per instanceof WTDocument) {
						setTaskStateCheck(per);
					}
					sendToERP(per);
				} else if (per instanceof ApprovalContract) {
					ApprovalContract contract = (ApprovalContract) per;
					approvalContract(contract);
				}
			}

			// 메일...

			map.put("reload", true);
			map.put("result", SUCCESS);
			map.put("msg", "결재가 승인 되었습니다.");
			map.put("url", "/Windchill/plm/approval/listApproval");
			trs.commit();
			trs = null;
		} catch (

		Exception e) {
			map.put("reload", false);
			map.put("result", FAIL);
			map.put("msg", "결재 승인 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요.");
			map.put("url", "/Windchill/plm/approval/listApproval");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	private void setTaskStateCheck(Persistable per) throws Exception {

		WTDocument document = (WTDocument) per;

		QueryResult result = PersistenceHelper.manager.navigate(document, "output", DocumentOutputLink.class);

		while (result.hasMoreElements()) {
			Output output = (Output) result.nextElement();

			QueryResult qr = PersistenceHelper.manager.navigate(output, "task", TaskOutputLink.class);

			while (qr.hasMoreElements()) {
				Task tt = (Task) qr.nextElement();

				boolean isPass = false;

				if (tt.getTaskType().equals("공통") || tt.getTaskType().equals("일반")) {
					isPass = true;
				}

				// if (tt.getAllocate() == 0) {
				// isPass = true;
				// } else if (tt.getAllocate() != 0) {
				// if (tt.getTaskType().equals("공통")) {
				// isPass = true;
				// }
				// }
				if (isPass) {
					if (tt.getEndDate() == null) {
						tt.setEndDate(DateUtils.getCurrentTimestamp());
					}

					if (tt.getProgress() != 100) {
						tt.setProgress(100);
					}

					if (!tt.getState().equals(TaskStateType.COMPLETE.getDisplay())) {
						tt.setState(TaskStateType.COMPLETE.getDisplay());
					}
					tt = (Task) PersistenceHelper.manager.modify(tt);
				}
			}
		}
	}

	private void setProjectRoleUser(RequestDocument req, ApprovalMaster master) throws Exception {

		QueryResult result = PersistenceHelper.manager.navigate(req, "project", RequestDocumentProjectLink.class);

		while (result.hasMoreElements()) {
			Project project = (Project) result.nextElement();

			Timestamp start = new Timestamp(new Date().getTime());

			project.setStartDate(start);
			project.setKekState("설계중");
			project.setState(ProjectStateType.INWORK.getDisplay());
			project = (Project) PersistenceHelper.manager.modify(project);

			Task tt = ProjectHelper.manager.getProjectTaskByName(project, "/의뢰서");
			tt.setState(TaskStateType.COMPLETE.getDisplay());
			tt.setEndDate(DateUtils.getCurrentTimestamp());
			PersistenceHelper.manager.modify(tt);

			ArrayList<ApprovalLine> agreeUsers = WorkspaceHelper.manager.getAgreeLines(master);

			for (ApprovalLine lines : agreeUsers) {
				WTUser user = (WTUser) lines.getOwnership().getOwner().getPrincipal();

				QueryResult qr = PersistenceHelper.manager.navigate(user, "people", WTUserPeopleLink.class);

				if (qr.hasMoreElements()) {
					People pp = (People) qr.nextElement();
					UserViewData dd = new UserViewData(pp);

					String deptName = dd.departmentName;

					if ("기계설계".equals(deptName)) {
						ProjectUserLink userLink = ProjectUserLink.newProjectUserLink(project, user);
						userLink.setUserType(ProjectUserType.MACHINE.name());
						PersistenceHelper.manager.save(userLink);
					} else if ("전기설계".equals(deptName)) {
						ProjectUserLink userLink = ProjectUserLink.newProjectUserLink(project, user);
						userLink.setUserType(ProjectUserType.ELEC.name());
						PersistenceHelper.manager.save(userLink);
					} else if ("SW설계".equals(deptName)) {
						ProjectUserLink userLink = ProjectUserLink.newProjectUserLink(project, user);
						userLink.setUserType(ProjectUserType.SOFT.name());
						PersistenceHelper.manager.save(userLink);
					}
				}
			}
		}
	}

	private void approvalContract(ApprovalContract contract) throws Exception {
		QueryResult result = PersistenceHelper.manager.navigate(contract, "persist",
				ApprovalContractPersistableLink.class, false);
		while (result.hasMoreElements()) {
			ApprovalContractPersistableLink link = (ApprovalContractPersistableLink) result.nextElement();
			Persistable per = link.getPersist();

			if (per instanceof LifeCycleManaged) {

				if (per instanceof WTPart) {
					WTPart part = (WTPart) per;
					part = (WTPart) CommonUtils.getLatestVersion(part);
					State s = State.toState("APPROVED");
					LifeCycleHelper.service.setLifeCycleState(part, s);
				} else if (per instanceof EPMDocument) {
					EPMDocument epm = (EPMDocument) per;
					epm = (EPMDocument) CommonUtils.getLatestVersion(epm);
					State s = State.toState("APPROVED");
					LifeCycleHelper.service.setLifeCycleState(epm, s);

					WTPart part = EpmHelper.manager.getPart(epm);
					if (part != null) {
						part = (WTPart) CommonUtils.getLatestVersion(part);
						LifeCycleHelper.service.setLifeCycleState(part, s);
					}

					// 나머지 변경..

				} else if (per instanceof WTDocument) {
					WTDocument doc = (WTDocument) per;
					doc = (WTDocument) CommonUtils.getLatestVersion(doc);
					State s = State.toState("APPROVED");
					LifeCycleHelper.service.setLifeCycleState(doc, s);
				}

				sendToERP(per);
			}
		}
	}

	private void sendToERP(Persistable per) throws Exception {

		// 문서 일 경우 = 산출물
		if (per instanceof WTDocument) {
			WTDocument document = (WTDocument) per;
			document = (WTDocument) CommonUtils.getLatestVersion(document);
			ErpHelper.service.sendOutputToERP(document);

		} else if (per instanceof EPMDocument) {
//			EPMDocument epm = (EPMDocument) per;
			// erp로 전송
			// ErpHelper.service.sendPartToERP(epm);
			// ycode 리턴
		} else if (per instanceof WTPart) {
//			WTPart part = (WTPart) per;
			// ErpHelper.service.sendPartToERP(part);
		} else if (per instanceof PartListMaster) {
			PartListMaster mm = (PartListMaster) per;
			ErpHelper.service.sendPartListToERP(mm);
		}
	}

	@Override
	public Map<String, Object> returnAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		ReferenceFactory rf = new ReferenceFactory();
		ApprovalLine line = null;
		String oid = (String) param.get("oid");
		String description = (String) param.get("description");
		Transaction trs = new Transaction();
		try {
			trs.start();

			Timestamp completeTime = new Timestamp(new Date().getTime());
			line = (ApprovalLine) rf.getReference(oid).getObject();

			if (StringUtils.isNull(description)) {
				description = "반려합니다.";
			}

			line.setDescription(description);
			line.setCompleteTime(completeTime);
			line.setState(WorkspaceHelper.LINE_RETURN_COMPLETE);
			line = (ApprovalLine) PersistenceHelper.manager.modify(line);

			ApprovalMaster master = line.getMaster();
			master.setCompleteTime(completeTime);
			master.setState(WorkspaceHelper.MASTER_RETURN);
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
					LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) pp, State.toState("RETURN"));
				}
			}

			// MailUtils.sendReturnMail(master, line);

			// 메일...

			map.put("reload", true);
			map.put("result", SUCCESS);
			map.put("msg", "결재가 반려 되었습니다.");
			map.put("url", "/Windchill/plm/approval/listApproval");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("reload", false);
			map.put("result", FAIL);
			map.put("msg", "결재 반려 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요.");
			map.put("url", "/Windchill/plm/approval/listApproval");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> receiveAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		ReferenceFactory rf = new ReferenceFactory();
		ApprovalLine line = null;
		String oid = (String) param.get("oid");
		String description = (String) param.get("description");
		Transaction trs = new Transaction();
		try {
			trs.start();

			Timestamp completeTime = new Timestamp(new Date().getTime());
			line = (ApprovalLine) rf.getReference(oid).getObject();
			line.setDescription(description);
			line.setCompleteTime(completeTime);
			line.setState(WorkspaceHelper.LINE_RECEIVE_COMPLETE);
			line = (ApprovalLine) PersistenceHelper.manager.modify(line);

			// 메일...

			map.put("relaod", true);
			map.put("result", SUCCESS);
			map.put("msg", "결재가 수신확인 되었습니다.");
			map.put("url", "/Windchill/plm/approval/listReceive");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("relaod", false);
			map.put("result", FAIL);
			map.put("msg", "결재 수신확인 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요.");
			map.put("url", "/Windchill/plm/approval/listReceive");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> unagreeAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		ReferenceFactory rf = new ReferenceFactory();
		ApprovalLine line = null;
		String oid = (String) param.get("oid");
		String description = (String) param.get("description");
		Transaction trs = new Transaction();
		try {
			trs.start();

			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			Timestamp completeTime = new Timestamp(new Date().getTime());
			line = (ApprovalLine) rf.getReference(oid).getObject();
			line.setDescription(description);
			line.setCompleteTime(completeTime);
			line.setCompleteUserID(user.getName());
			line.setState(WorkspaceHelper.LINE_AGREE_REJECT);
			line = (ApprovalLine) PersistenceHelper.manager.modify(line);

			ApprovalMaster master = line.getMaster();
			master.setCompleteTime(completeTime);
			master.setState(WorkspaceHelper.LINE_AGREE_REJECT);
			master = (ApprovalMaster) PersistenceHelper.manager.modify(master);

//			Persistable per = master.getPersist();

			// if (per instanceof LifeCycleManaged) {
			// LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) per,
			// State.toState("RETURN"));
			// } else if (per instanceof ApprovalContract) {
			// ApprovalContract contract = (ApprovalContract) per;
			//
			// QueryResult result = PersistenceHelper.manager.navigate(contract, "persist",
			// ApprovalContractPersistableLink.class);
			// while (result.hasMoreElements()) {
			// Persistable pp = (Persistable) result.nextElement();
			// LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) pp,
			// State.toState("RETURN"));
			// }
			// }

			// 메일...

			map.put("reload", true);
			map.put("result", SUCCESS);
			map.put("msg", "결재가 검토반려 되었습니다.");
			map.put("url", "/Windchill/plm/approval/listAgree");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("reload", false);
			map.put("result", FAIL);
			map.put("msg", "결재 검토반려 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요.");
			map.put("url", "/Windchill/plm/approval/listAgree");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> agreeAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		ReferenceFactory rf = new ReferenceFactory();
		ApprovalLine line = null;
		String oid = (String) param.get("oid");
		String description = (String) param.get("description");
		Transaction trs = new Transaction();
		try {
			trs.start();

			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();

			Timestamp completeTime = new Timestamp(new Date().getTime());
			line = (ApprovalLine) rf.getReference(oid).getObject();
			line.setDescription(description);
			line.setCompleteTime(completeTime);
			line.setState(WorkspaceHelper.LINE_AGREE_COMPLETE);
			line.setCompleteUserID(user.getName());
			line = (ApprovalLine) PersistenceHelper.manager.modify(line);

			// 메일...

			// 모든검토가 끝나면 결재가 진행...

			boolean isEndAgree = WorkspaceHelper.manager.isEndAgree(line.getMaster());
			if (isEndAgree) {
				ArrayList<ApprovalLine> appLines = WorkspaceHelper.manager.getAppLines(line.getMaster());
				for (ApprovalLine appLine : appLines) {
					int sort = appLine.getSort();
					appLine.setSort(sort - 1);
					appLine = (ApprovalLine) PersistenceHelper.manager.modify(appLine);

					appLine = (ApprovalLine) PersistenceHelper.manager.refresh(appLine);

					if (appLine.getSort() == 0) {
						// start
						appLine.setStartTime(completeTime);
						appLine.setState(WorkspaceHelper.LINE_APPROVING);
						PersistenceHelper.manager.modify(appLine);
					}

				}
			}

			map.put("reload", true);
			map.put("result", SUCCESS);
			map.put("msg", "결재가 검토완료 되었습니다.");
			map.put("url", "/Windchill/plm/approval/listAgree");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("reload", false);
			map.put("result", FAIL);
			map.put("msg", "결재 검토완료 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요.");
			map.put("url", "/Windchill/plm/approval/listAgree");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> setAbsenceAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String oid = (String) param.get("oid");
		ReferenceFactory rf = new ReferenceFactory();
		ApprovalLine line = null;
		Transaction trs = new Transaction();

		try {
			trs.start();

			Timestamp st = new Timestamp(new Date().getTime());

			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();

			String msg = user.getFullName() + " 사용자가 해당 결재를 부재중 처리 하였습니다.";

			line = (ApprovalLine) rf.getReference(oid).getObject();
			line.setState(WorkspaceHelper.LINE_ABSENCE);
			line.setCompleteTime(st);
			line.setDescription(msg);
			PersistenceHelper.manager.modify(line);

			ApprovalMaster master = line.getMaster();

			ArrayList<ApprovalLine> appLines = WorkspaceHelper.manager.getAppLines(master);
			for (ApprovalLine appLine : appLines) {
				// -1.. 0
				appLine.setSort(appLine.getSort() - 1);
				PersistenceHelper.manager.modify(appLine);

				int sort = appLine.getSort();
				if (sort == 0) {
					appLine.setState(WorkspaceHelper.LINE_APPROVING);
					appLine.setStartTime(new Timestamp(new Date().getTime()));
					PersistenceHelper.manager.modify(appLine);
					break;
				}
			}

			map.put("result", SUCCESS);
			map.put("msg", "부재중 처리가 완료 되었습니다.\n다음 결재로 진행 되어집니다.");
			map.put("url", "/Windchill/plm/approval/listApproval");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "부재중 처리 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요.");
			map.put("url", "/Windchill/plm/approval/infoApproval?oid=" + oid);
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> recoveryAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> list = (List<String>) param.get("list");
		// master oid
		ReferenceFactory rf = new ReferenceFactory();
		ApprovalMaster master = null;
		Transaction trs = new Transaction();

		try {
			trs.start();

			for (String oid : list) {
				master = (ApprovalMaster) rf.getReference(oid).getObject();

				ArrayList<ApprovalLine> appLines = WorkspaceHelper.manager.getAppLines(master);
				for (ApprovalLine line : appLines) {
					// 모든 결재 라인 삭제..
					PersistenceHelper.manager.delete(line);
				}

				ArrayList<ApprovalLine> agreeLines = WorkspaceHelper.manager.getAgreeLines(master);
				for (ApprovalLine line : agreeLines) {
					// 모든 결재 라인 삭제..
					PersistenceHelper.manager.delete(line);
				}

				ArrayList<ApprovalLine> receiveLines = WorkspaceHelper.manager.getReceiveLines(master);
				for (ApprovalLine line : receiveLines) {
					// 모든 결재 라인 삭제..
					PersistenceHelper.manager.delete(line);
				}

				Persistable per = master.getPersist();

				// 마스터 라인 삭제..
				PersistenceHelper.manager.delete(master);

				// 객체 상태값 변경..
				if (per instanceof LifeCycleManaged) {
					LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) per, State.toState("INWORK"));
				} else if (per instanceof ApprovalContract) {
					ApprovalContract contract = (ApprovalContract) per;

					QueryResult result = PersistenceHelper.manager.navigate(contract, "persist",
							ApprovalContractPersistableLink.class);
					while (result.hasMoreElements()) {
						Persistable pp = (Persistable) result.nextElement();
						LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) pp, State.toState("INWORK"));
					}
				}
			}

			map.put("result", SUCCESS);
			map.put("msg", "결재가 회수 되었습니다.");
			map.put("url", "/Windchill/plm/approval/listIng");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "결재 회수 처리 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요.");
			map.put("url", "/Windchill/plm/approval/listIng");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public void approvalPersist(Persistable per) throws WTException {
		Transaction trs = new Transaction();
		try {
			trs.start();

			if (per instanceof ApprovalContract) {
				ApprovalContract contract = (ApprovalContract) per;

				QueryResult result = PersistenceHelper.manager.navigate(contract, "persist",
						ApprovalContractPersistableLink.class);

				while (result.hasMoreElements()) {
					Persistable pp = (Persistable) result.nextElement();

					// if... erp part
					if (pp instanceof WTPart) {
						WTPart part = (WTPart) pp;
						PartViewData data = new PartViewData((WTPart) pp);

						if (ErpHelper.isSendERP) {
							ErpHelper.service.sendERPPARTAction(data);
						}

						IBAUtils.createIBA(part, "s", "ERP_CODE", "테스트값 입력");

						EPMDocument epm = PartHelper.manager.getEPMDocument(part);
						if (epm != null) {
							LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) epm,
									State.toState("RELEASED"));
							// 2d
							QueryResult qr = PersistenceHelper.manager.navigate(epm.getMaster(), "referencedBy",
									EPMReferenceLink.class);
							while (qr.hasMoreElements()) {
								EPMDocument epm2d = (EPMDocument) qr.nextElement();
								LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) epm2d,
										State.toState("RELEASED"));
							}
						}
					}
					LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) pp, State.toState("RELEASED"));
				}
			}
			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}

	}

	@Override
	public void deleteAllLine(Persistable per) throws WTException {
		Transaction trs = new Transaction();
		try {
			trs.start();
			ApprovalMaster master = null;
			if (per != null) {
				master = WorkspaceHelper.manager.getMaster(per);
			}

			System.out.println("master=" + master);

			// 결재 이력이 없어졋을수도..
			if (master != null) {

				ArrayList<ApprovalLine> agreeLines = WorkspaceHelper.manager.getAgreeLines(master);

				for (int i = 0; i < agreeLines.size(); i++) {
					ApprovalLine line = (ApprovalLine) agreeLines.get(i);
					PersistenceHelper.manager.delete(line);
				}

				ArrayList<ApprovalLine> appLines = WorkspaceHelper.manager.getAppLines(master);
				for (int i = 0; i < appLines.size(); i++) {
					ApprovalLine line = (ApprovalLine) appLines.get(i);
					PersistenceHelper.manager.delete(line);
				}

				ArrayList<ApprovalLine> receiveLines = WorkspaceHelper.manager.getReceiveLines(master);
				for (int i = 0; i < receiveLines.size(); i++) {
					ApprovalLine line = (ApprovalLine) receiveLines.get(i);
					PersistenceHelper.manager.delete(line);
				}

				PersistenceHelper.manager.delete(master);
			}

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public Map<String, Object> deleteLines(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> list = (List<String>) param.get("list");
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (String oid : list) {
				Persistable per = (Persistable) rf.getReference(oid).getObject();
				if (per instanceof ApprovalLine) {
					ApprovalLine line = (ApprovalLine) per;
					PersistenceHelper.manager.delete(line);

					ApprovalMaster master = line.getMaster();
					PersistenceHelper.manager.delete(master);

				} else if (per instanceof ApprovalMaster) {
					ApprovalMaster master = (ApprovalMaster) per;
					deleteAllLine(master);
				}
			}

			map.put("result", SUCCESS);
			map.put("msg", "결재라인이 " + DELETE_OK);
			map.put("url", "/Windchill/plm/approval/listApproval");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "결재라인이 " + DELETE_FAIL);
			map.put("url", "/Windchill/plm/approval/listApproval");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public void deleteAllLine(ApprovalMaster master) throws WTException {
		Transaction trs = new Transaction();
		try {
			trs.start();
			if (master.getPersist() != null) {
				master = WorkspaceHelper.manager.getMaster(master.getPersist());
			}

			ArrayList<ApprovalLine> agreeLines = WorkspaceHelper.manager.getAgreeLines(master);
			for (int i = 0; i < agreeLines.size(); i++) {
				ApprovalLine line = (ApprovalLine) agreeLines.get(i);
				PersistenceHelper.manager.delete(line);
			}

			ArrayList<ApprovalLine> appLines = WorkspaceHelper.manager.getAppLines(master);
			for (int i = 0; i < appLines.size(); i++) {
				ApprovalLine line = (ApprovalLine) appLines.get(i);
				PersistenceHelper.manager.delete(line);
			}

			ArrayList<ApprovalLine> receiveLines = WorkspaceHelper.manager.getReceiveLines(master);
			for (int i = 0; i < receiveLines.size(); i++) {
				ApprovalLine line = (ApprovalLine) receiveLines.get(i);
				PersistenceHelper.manager.delete(line);
			}

			PersistenceHelper.manager.delete(master);

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public Map<String, Object> skipApproval(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String oid = (String) param.get("oid");
		ApprovalLine line = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			line = (ApprovalLine) rf.getReference(oid).getObject();
			ApprovalMaster master = line.getMaster();

			Timestamp completeTime = new Timestamp(new Date().getTime());

			WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();

			String description = sessionUser.getFullName() + " 사용자가 해당 결재라인을 스킵하였습니다.(문구..??)";

			line.setDescription(description);
			line.setCompleteTime(completeTime);
			line.setState(WorkspaceHelper.LINE_APPROVAL_COMPLETE);
			line = (ApprovalLine) PersistenceHelper.manager.modify(line);

			// if (line.getLineType().equals(ApprovalHelper.SERIES)) {
			// ArrayList<ApprovalLine> appLines =
			// ApprovalHelper.manager.getAppLines(master);
			// for (ApprovalLine appLine : appLines) {
			// // -1.. 0
			// appLine.setSort(appLine.getSort() - 1);
			// PersistenceHelper.manager.modify(appLine);
			// }
			// } else if (line.getLineType().equals(ApprovalHelper.PARALLEL)) {
			// line.setSort(line.getSort() - 1);
			// PersistenceHelper.manager.modify(line);
			// }

			ArrayList<ApprovalLine> list = WorkspaceHelper.manager.getAppLines(master);
			for (ApprovalLine appLine : list) {
				int sort = appLine.getSort();
				if (sort == 0) {
					appLine.setState(WorkspaceHelper.LINE_APPROVING);
					appLine.setStartTime(new Timestamp(new Date().getTime()));
					PersistenceHelper.manager.modify(appLine);
					break;
				}
			}

			map.put("result", SUCCESS);
			map.put("msg", "결재라인이 스킵 되었습니다.??");
			map.put("url", "/Windchill/plm/approval/listIng");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "결재라인이 스킵 에러..");
			map.put("url", "/Windchill/plm/approval/listIng");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> reassignApproval(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String oid = (String) param.get("oid");
		String reassignUser = (String) param.get("reassignUser");
		ApprovalLine line = null;
		People user = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		// 수신 시작 변경 및 확인 여부 false
		try {
			trs.start();

			line = (ApprovalLine) rf.getReference(oid).getObject();
			user = (People) rf.getReference(reassignUser).getObject();

			Ownership ownership = Ownership.newOwnership(user.getUser());
			line.setOwnership(ownership);
			line.setReads(false);
			line = (ApprovalLine) PersistenceHelper.manager.modify(line);

			String toMail = user.getUser().getEMail();

			if (!StringUtils.isNull(toMail)) {
				System.out.println("메일이 있을경우만..");
				// MailUtils.sendReassignMail(toMail, line);
			}

			map.put("result", SUCCESS);
			map.put("msg", "결재라인이 " + user.getName() + " 사용자에게 위임 되었습니다.");
			map.put("url", "/Windchill/plm/approval/listIng");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "결재라인이 위임 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요.");
			map.put("url", "/Windchill/plm/approval/listIng");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public void selfApproval(Persistable per) throws WTException {

		ApprovalMaster master = null;
		Timestamp startTime = new Timestamp(new Date().getTime());

		Transaction trs = new Transaction();
		try {
			trs.start();

			String name = WorkspaceHelper.manager.getLineName(per);

			// 검토가 있던 없던 기안자는 무조건 생성..
			// 기안자..
			Ownership ownership = Ownership.newOwnership(SessionHelper.manager.getPrincipal());

			WTUser completeUser = (WTUser) SessionHelper.manager.getPrincipal();

			// 기안자생성
			master = ApprovalMaster.newApprovalMaster();
			master.setName(name);
			master.setCompleteTime(startTime);
			master.setOwnership(ownership);
			master.setPersist(per);
			master.setStartTime(startTime);
			master.setState(WorkspaceHelper.LINE_SUBMIT_COMPLETE);
			master.setCompleteUserID(completeUser.getName());
			master = (ApprovalMaster) PersistenceHelper.manager.save(master);

			// 검토가 있을 경우..
			ApprovalLine startLine = ApprovalLine.newApprovalLine();
			startLine.setName(master.getName());
			startLine.setOwnership(ownership);
			startLine.setMaster(master);
			startLine.setReads(true);
			startLine.setSort(-50);
			startLine.setStartTime(startTime);
			startLine.setType(WorkspaceHelper.APP_LINE);
			// 기안자
			startLine.setRole(WorkspaceHelper.WORKING_SUBMIT);
			startLine.setDescription(ownership.getOwner().getFullName() + " 사용자가 결재를 제출 하였습니다.");
			startLine.setCompleteUserID(completeUser.getName());
			startLine.setState(WorkspaceHelper.LINE_SUBMIT_COMPLETE);
			startLine.setCompleteTime(startTime);

			startLine = (ApprovalLine) PersistenceHelper.manager.save(startLine);

			ApprovalLine appLine = ApprovalLine.newApprovalLine();
			appLine.setName(master.getName());
			appLine.setOwnership(ownership);
			appLine.setCompleteTime(startTime);
			appLine.setDescription("자가 결재");
			appLine.setMaster(master);
			appLine.setType(WorkspaceHelper.APP_LINE);
			appLine.setReads(true);
			appLine.setRole(WorkspaceHelper.WORKING_APPROVAL);
			appLine.setSort(0);
			appLine.setStartTime(startTime);
			appLine.setState(WorkspaceHelper.LINE_APPROVAL_COMPLETE);
			appLine = (ApprovalLine) PersistenceHelper.manager.save(appLine);

			if (per instanceof LifeCycleManaged) {
				State state = State.toState("APPROVED");
				per = (Persistable) LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) per, state);
			} else if (per instanceof ApprovalContract) {
				ApprovalContract contract = (ApprovalContract) per;
				approvalContract(contract);
			}

			trs.commit();
			trs = null;

		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public Map<String, Object> initApprovalAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> list = (List<String>) param.get("list");
		ApprovalMaster master = null;
		ReferenceFactory rf = new ReferenceFactory();
		State state = State.toState("INWORK");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (String oid : list) {
				QueryResult result = null;
				master = (ApprovalMaster) rf.getReference(oid).getObject();

				ArrayList<ApprovalLine> lines = WorkspaceHelper.manager.getAppLines(master);

				for (ApprovalLine l : lines) {
					PersistenceHelper.manager.delete(l);
				}

				Persistable per = master.getPersist();

				if (per instanceof ApprovalContract) {
					ApprovalContract contract = (ApprovalContract) per;
					result = PersistenceHelper.manager.navigate(contract, "persist",
							ApprovalContractPersistableLink.class);
					while (result.hasMoreElements()) {
						Persistable per2 = (Persistable) result.nextElement();
						// State state = State.toState("APPROVED");
						// State state = State.toState("INWORK");
						per2 = (Persistable) LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) per2, state);
					}
				} else if (per instanceof LifeCycleManaged) {
					per = (Persistable) LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) per, state);
				}
				PersistenceHelper.manager.delete(master);
			}
			map.put("result", SUCCESS);
			map.put("msg", "결재가 " + MODIFY_OK);
			map.put("url", "/Windchill/plm/approval/listIng");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "결재가 " + MODIFY_FAIL);
			map.put("url", "/Windchill/plm/approval/listIng");
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> initApprovalLineAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> list = (List<String>) param.get("list");
		ApprovalMaster master = null;
		ApprovalLine line = null;
		ReferenceFactory rf = new ReferenceFactory();
		State state = State.toState("INWORK");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (String oid : list) {
				QueryResult result = null;

				Persistable pers = (Persistable) rf.getReference(oid).getObject();
				if (pers instanceof ApprovalLine) {
					line = (ApprovalLine) pers;
					master = line.getMaster();
				} else if (pers instanceof ApprovalMaster) {
					master = (ApprovalMaster) rf.getReference(oid).getObject();
				}

				ArrayList<ApprovalLine> lines = WorkspaceHelper.manager.getAppLines(master);

				for (ApprovalLine l : lines) {
					PersistenceHelper.manager.delete(l);
				}

				Persistable per = master.getPersist();

				if (per instanceof ApprovalContract) {
					ApprovalContract contract = (ApprovalContract) per;
					result = PersistenceHelper.manager.navigate(contract, "persist",
							ApprovalContractPersistableLink.class);
					while (result.hasMoreElements()) {
						Persistable per2 = (Persistable) result.nextElement();
						// State state = State.toState("APPROVED");
						// State state = State.toState("INWORK");
						per2 = (Persistable) LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) per2, state);
					}
				} else if (per instanceof LifeCycleManaged) {
					per = (Persistable) LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) per, state);
				}
				PersistenceHelper.manager.delete(master);
			}
			map.put("result", SUCCESS);
			map.put("msg", "결재가 " + MODIFY_OK);
			map.put("url", "/Windchill/plm/approval/listIng");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "결재가 " + MODIFY_FAIL);
			map.put("url", "/Windchill/plm/approval/listIng");
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> deleteReturnAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> list = (List<String>) param.get("list");
		ApprovalMaster master = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();
			System.out.println("=====반려 삭제 시작=====");
			for (String oid : list) {
				System.out.println("ViewDisabled is true, ApprovalMaster OID : " + oid);
				master = (ApprovalMaster) rf.getReference(oid).getObject();

				master.setViewDisabled(true);

				PersistenceHelper.manager.modify(master);
			}
			System.out.println("=====반려 삭제 성공=====");
			map.put("result", SUCCESS);
			map.put("msg", "반려된 결재정보가 " + DELETE_OK);
			map.put("url", "/Windchill/plm/approval/listReturn");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "반려된 결재정보 " + DELETE_FAIL);
			map.put("url", "/Windchill/plm/approval/listReturn");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public void register(Persistable persistable, ArrayList<Map<String, String>> agreeRows,
			ArrayList<Map<String, String>> approvalRows, ArrayList<Map<String, String>> receiveRows) throws Exception {
		boolean isAgree = agreeRows.size() > 0;
		Transaction trs = new Transaction();
		try {
			trs.start();

			WTUser sessionUser = CommonUtils.sessionUser();
			Timestamp startTime = new Timestamp(new Date().getTime());
			Ownership ownership = CommonUtils.sessionOwner();
			String name = WorkspaceHelper.manager.getName(persistable);
			String description = WorkspaceHelper.manager.getDescription(persistable);

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
					
					NotificationHelper.service.sendTo("", "", wtuser);
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
