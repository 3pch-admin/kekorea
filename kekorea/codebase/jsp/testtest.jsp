<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="e3ps.common.util.DateUtils"%>
<%@page import="e3ps.project.service.ProjectHelper"%>
<%@page import="e3ps.project.service.TemplateHelper"%>
<%@page import="wt.query.SearchCondition"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.util.Calendar"%>
<%@page import="e3ps.project.Task"%>
<%@page import="e3ps.project.Project"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.query.QuerySpec"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	QuerySpec query = new QuerySpec();
	int idx = query.appendClassList(Task.class, true);

	SearchCondition sc = new SearchCondition(Task.class, Task.NAME, "=", "이력관리");
	query.appendWhere(sc, new int[] { idx });
	query.appendAnd();

	sc = new SearchCondition(Task.class, "templateReference.key.id", "<>", 0L);
	query.appendWhere(sc, new int[] { idx });
	query.appendAnd();

	sc = new SearchCondition(Task.class, "projectReference.key.id", "=", 0L);
	query.appendWhere(sc, new int[] { idx });

	// 	Timestamp end = DateUtils.convertEndDate("2022-07-10");
	// 	SearchCondition sc = new SearchCondition(Project.class, Project.CREATE_TIMESTAMP,
	// 			SearchCondition.GREATER_THAN_OR_EQUAL, end);
// 	query.appendWhere(sc, new int[] { idx });
	QueryResult result = PersistenceHelper.manager.find(query);
	// 	System.out.println("R="+result.size());
	int i = 0;
	while (result.hasMoreElements()) {
		Object[] obj = (Object[]) result.nextElement();
		Task t = (Task) obj[0];

		Calendar ca = Calendar.getInstance();
		ca.setTime(t.getTemplate().getPlanStartDate());
		ca.add(Calendar.DATE, 1);

		Timestamp newEnd = new Timestamp(ca.getTime().getTime());
		
		t.setPlanStartDate(t.getTemplate().getPlanStartDate());
		t.setPlanEndDate(newEnd);
		PersistenceHelper.manager.modify(t);
	}

	// 		QuerySpec qs = new QuerySpec();
	// 		int idx2 = qs.appendClassList(Task.class, true);

	// 		SearchCondition sc2 = new SearchCondition(Task.class, Task.NAME, "=", "이력관리");
	// 		qs.appendWhere(sc2, new int[] { idx2 });
	// 		qs.appendAnd();

	// 		sc2 = new SearchCondition(Task.class, "templateReference.key.id", "=", 0L);
	// 		qs.appendWhere(sc2, new int[] { idx2 });
	// 		qs.appendAnd();

	// 		sc2 = new SearchCondition(Task.class, "projectReference.key.id", "=",
	// 				t.getPersistInfo().getObjectIdentifier().getId());
	// 		qs.appendWhere(sc2, new int[] { idx2 });
	// 		QueryResult qr = PersistenceHelper.manager.find(qs);
	// 		out.println("qr=" + qr.size());
	// 		ProjectHelper.service.commit(t);
	// 		while (qr.hasMoreElements()) {
	// 			Object[] oo = (Object[]) qr.nextElement();
	// 			Task tt = (Task) oo[0];

	// 			tt.setPlanStartDate(t.getPlanStartDate());
	// 			tt.setPlanEndDate(newEnd);
	// 			PersistenceHelper.manager.modify(tt);

	// 			out.println("k=" + i + ", name=" + tt.getName() + ", start=" + tt.getPlanStartDate() + ", enddate ="
	// 					+ tt.getPlanEndDate() + "<br>");
	// 			i++;
	// 		}
	// 		ReferenceFactory rf = new ReferenceFactory();

	// 		String oid = "e3ps.project.Task:109269765";
	// 		Task t = (Task) rf.getReference(oid).getObject();

	// 		Timestamp plan = t.getProject().getPlanStartDate();
	// 		Calendar ca = Calendar.getInstance();
	// 		ca.setTimeInMillis(plan.getTime());
	// 		ca.add(Calendar.DATE, 1);

	// 		Timestamp end = new Timestamp(ca.getTime().getTime());

	// 		t.setPlanStartDate(t.getProject().getPlanStartDate());
	// 		t.setPlanEndDate(end);
	// 		PersistenceHelper.manager.modify(t);

	System.out.println("끝");
	// 	}
%>