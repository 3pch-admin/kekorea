package e3ps.workspace.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import e3ps.common.util.CommonUtils;
import e3ps.workspace.ApprovalContract;
import e3ps.workspace.ApprovalLine;
import e3ps.workspace.ApprovalMaster;
import e3ps.workspace.notification.service.NotificationHelper;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.org.WTUser;
import wt.ownership.Ownership;
import wt.pom.Transaction;
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
