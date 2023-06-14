package e3ps.project.task.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

import e3ps.common.util.CommonUtils;
import e3ps.common.util.DateUtils;
import e3ps.project.Project;
import e3ps.project.service.ProjectHelper;
import e3ps.project.task.ParentTaskChildTaskLink;
import e3ps.project.task.Task;
import e3ps.project.task.variable.TaskStateVariable;
import e3ps.project.variable.ProjectStateVariable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardTaskService extends StandardManager implements TaskService {

	public static StandardTaskService newStandardTaskService() throws WTException {
		StandardTaskService instance = new StandardTaskService();
		instance.initialize();
		return instance;
	}

	@Override
	public void calculation(ArrayList<Task> list) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (int i = list.size() - 1; i >= 0; i--) {
				Task task = (Task) list.get(i);

				// 상위 계획 시작
				Timestamp start = null;
				// 상위 계획 종료
				Timestamp end = null;

				boolean edit = false;

				// 하위 태스크가 없을 경우 일정 정리 필요가 없음
				QueryResult result = PersistenceHelper.manager.navigate(task, "childTask",
						ParentTaskChildTaskLink.class);
				while (result.hasMoreElements()) {
					Task child = (Task) result.nextElement();

					// 하위 계획 시작
					Timestamp cstart = child.getPlanStartDate();
					// 하위 계획 종료
					Timestamp cend = child.getPlanEndDate();
					// 상위 계획 시작일이 null
					// 2000-01-02 2000-01-01
					// 계획 시작일이 늦은 쪽으로 세팅 한다.
					if (start == null || (start.getTime() > cstart.getTime())) {
						start = cstart;
						edit = true;
					}

					if (end == null || (end.getTime() < cend.getTime())) {
						end = cend;
						edit = true;
					}
				}

				if (edit) {
					task.setPlanStartDate(start);
					task.setPlanEndDate(end);

					int duration = DateUtils.getDuration(start, end);
					task.setDuration(duration);

					task = (Task) PersistenceHelper.manager.modify(task);
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
	public void editProgress(Map<String, Object> params) throws Exception {
		String oid = (String) params.get("oid");
		int progress = (int) params.get("progress");
		Transaction trs = new Transaction();
		try {
			trs.start();

			Task task = (Task) CommonUtils.getObject(oid);
			if (progress > 100) {
				progress = 100;
			}

			// 태스크
			if (task.getStartDate() == null) {
				// 중복적으로 실제 시작일이 변경 되지 않게
				task.setStartDate(DateUtils.getCurrentTimestamp());
			}

			if (progress >= 100) {
				task.setEndDate(DateUtils.getCurrentTimestamp());
				task.setState(TaskStateVariable.COMPLETE);
			} else {
				task.setState(TaskStateVariable.INWORK);
			}
			task.setProgress(progress);
			task = (Task) PersistenceHelper.manager.modify(task);

			Project project = task.getProject();

			// 시작이 된 흔적이 없을 경우
			if (project.getStartDate() == null) {
				project.setStartDate(DateUtils.getCurrentTimestamp());
				project.setKekState(ProjectStateVariable.KEK_DESIGN_INWORK);
				project.setState(ProjectStateVariable.INWORK);
				project = (Project) PersistenceHelper.manager.modify(project);
			}

//			ProjectHelper.service.calculation(project);
//			ProjectHelper.service.commit(project);

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
