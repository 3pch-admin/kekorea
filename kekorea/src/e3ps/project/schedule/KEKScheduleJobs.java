package e3ps.project.schedule;

public class KEKScheduleJobs {

	public static void startBatch() {
		try {
			System.out.println("START!! KEK SCHEDULE");
			startTask();
			System.out.println("END!! KEK SCHEDULE");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void startTask() throws Exception {
//		Timestamp today = DateUtils.getCurrentTimestamp();
//		try {
//			QuerySpec query = new QuerySpec();
//			int idx = query.appendClassList(Project.class, true);
//
//			SearchCondition sc = null;
//
//			Calendar ca = Calendar.getInstance();
//			Timestamp start = DateUtils.getCurrentTimestamp();
//			ca.setTime(start);
//			ca.add(Calendar.MONTH, -6);
//			Timestamp end = new Timestamp(ca.getTime().getTime());
//
//			sc = new SearchCondition(Project.class, Project.P_DATE, SearchCondition.GREATER_THAN_OR_EQUAL, end);
//			query.appendWhere(sc, new int[] { idx });
//
//			query.appendAnd();
//			sc = new SearchCondition(Project.class, Project.P_DATE, SearchCondition.LESS_THAN_OR_EQUAL, start);
//			query.appendWhere(sc, new int[] { idx });
//
//			ClassAttribute csa = new ClassAttribute(Project.class, Project.P_DATE);
//			OrderBy by = new OrderBy(csa, false);
//			query.appendOrderBy(by, new int[] { idx });
//
//			QueryResult result = PersistenceHelper.manager.find(query);
//
////			int cnt = 0;
//			while (result.hasMoreElements()) {
//				Object[] obj = (Object[]) result.nextElement();
//				Project project = (Project) obj[0];
//
//				Timestamp planStartDate = project.getPlanStartDate();
//
//				if (planStartDate != null && (DateUtils.convertStartDate(planStartDate.toString().substring(0, 10))
//						.getTime() < today.getTime())) {
//
//					if (project.getStartDate() == null) {
//						project.setState(ProjectStateType.INWORK.getDisplay());
//						project.setKekState("설계중");
//						project.setStartDate(today);
//					}
//
//					if (project.getKekState() != null && project.getKekState().equals("준비")) {
//						project.setKekState("설계중");
//					}
//
//					ArrayList<Task> list = new ArrayList<Task>();
//
//					list = ProjectHelper.manager.getterProjectTask(project, list);
//
//					for (Task tt : list) {
//
//						Timestamp tplanStartDate = tt.getPlanStartDate();
//
//						if (tplanStartDate == null) {
//							if (tt.getStartDate() == null) {
//								tt.setStartDate(today);
//								tt.setState(TaskStateType.INWORK.getDisplay());
//								PersistenceServerHelper.manager.update(tt);
//							}
//						}
//
//						if (tplanStartDate != null
//								&& (DateUtils.convertStartDate(tplanStartDate.toString().substring(0, 10))
//										.getTime() < today.getTime())) {
//
//							if (tt.getStartDate() == null) {
//								tt.setStartDate(today);
//								tt.setState(TaskStateType.INWORK.getDisplay());
//								PersistenceHelper.manager.modify(tt);
//							}
//						}
//					}
//					PersistenceServerHelper.manager.update(project);
//					project = (Project) PersistenceHelper.manager.refresh(project);
//					ProjectHelper.service.commit(project);
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//		}
	}
}
