package e3ps.project.service;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

import e3ps.common.util.DateUtils;
import e3ps.project.Project;
import e3ps.project.Task;
import e3ps.project.enums.ProjectStateType;
import e3ps.project.enums.TaskStateType;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;

public class ProjectScheduler implements RemoteAccess, Serializable {

	public static ProjectScheduler manager = new ProjectScheduler();

	public void run() {
		try {
			setStartTask();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setStartTask() {
		Timestamp today = DateUtils.getCurrentTimestamp();
		try {
			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(Project.class, true);

			SearchCondition sc = null;

			Calendar ca = Calendar.getInstance();
			Timestamp start = DateUtils.getCurrentTimestamp();
			ca.setTime(start);
			ca.add(Calendar.MONTH, -6);
			Timestamp end = new Timestamp(ca.getTime().getTime());

			sc = new SearchCondition(Project.class, Project.P_DATE, SearchCondition.GREATER_THAN_OR_EQUAL, end);
			query.appendWhere(sc, new int[] { idx });

			query.appendAnd();
			sc = new SearchCondition(Project.class, Project.P_DATE, SearchCondition.LESS_THAN_OR_EQUAL, start);
			query.appendWhere(sc, new int[] { idx });

			ClassAttribute csa = new ClassAttribute(Project.class, Project.P_DATE);
			OrderBy by = new OrderBy(csa, false);
			query.appendOrderBy(by, new int[] { idx });

			QueryResult result = PersistenceHelper.manager.find(query);

//			int cnt = 0;
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				Project project = (Project) obj[0];

				Timestamp planStartDate = project.getPlanStartDate();

				if (planStartDate != null && (DateUtils.convertStartDate(planStartDate.toString().substring(0, 10))
						.getTime() < today.getTime())) {

					if (project.getStartDate() == null) {
						project.setState(ProjectStateType.INWORK.getDisplay());
						project.setKekState("설계중");
						project.setStartDate(today);
					}

					if (project.getKekState() != null && project.getKekState().equals("준비")) {
						project.setKekState("설계중");
					}

					ArrayList<Task> list = new ArrayList<Task>();

					list = ProjectHelper.manager.getterProjectTask(project, list);

					for (Task tt : list) {

						Timestamp tplanStartDate = tt.getPlanStartDate();

						if (tplanStartDate == null) {
							if (tt.getStartDate() == null) {
								tt.setStartDate(today);
								tt.setState(TaskStateType.INWORK.getDisplay());
								PersistenceServerHelper.manager.update(tt);
							}
						}

						if (tplanStartDate != null
								&& (DateUtils.convertStartDate(tplanStartDate.toString().substring(0, 10))
										.getTime() < today.getTime())) {

							if (tt.getStartDate() == null) {
								tt.setStartDate(today);
								tt.setState(TaskStateType.INWORK.getDisplay());
								PersistenceHelper.manager.modify(tt);
							}
						}
					}
					PersistenceServerHelper.manager.update(project);
					project = (Project) PersistenceHelper.manager.refresh(project);
					ProjectHelper.service.commit(project);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}
}