<%@page import="wt.epm.EPMDocument"%>
<%@page import="e3ps.project.beans.TaskViewData"%>
<%@page import="e3ps.project.service.TemplateHelper"%>
<%@page import="e3ps.project.TargetTaskSourceTaskLink"%>
<%@page import="e3ps.project.Task"%>
<%@page import="wt.part.WTPart"%>
<%@page import="java.util.ArrayList"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="e3ps.common.util.ContentUtils"%>
<%@page import="wt.content.ContentHolder"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String oid = request.getParameter("oid");
	ReferenceFactory rf = new ReferenceFactory();
	Task task = (Task)rf.getReference(oid).getObject();
	ArrayList<TargetTaskSourceTaskLink> list = TemplateHelper.manager.getTargetTaskSourceTaskLinkByTarget(task);
	int size = list.size();
%>
<table class="project_table">
	<colgroup>
		<col width="40">
		<col width="280">
		<col width="280">
		<col width="280">
	</colgroup>
	<thead>
		<tr>
			<th>
				<input type="checkbox" name="allParts" id="allParts">
			</th>
			<th>태스크 타입</th>
			<th>태스크 명</th>
			<th>기간</th>
			<th>할당율</th>
		</tr>		
	</thead>
	<%
		for (TargetTaskSourceTaskLink link : list) {
			Task targetTask = link.getTargetTask();
			TaskViewData dd = new TaskViewData(targetTask);
	%>
	<tr>
		<td>&nbsp;</td>
		<td class="center"><%=dd.taskType %></td>
		<td class="center"><%=dd.name %></td>
		<td class="center"><%=dd.duration %>일</td>	
		<td class="center"><%=dd.allocate %></td>	
	</tr>
	<%
		}
		if(list.size() == 0) {
	%>
	<tr>
		<td class="nodata" colspan="5">선행 태스크가 없습니다.</td>
	</tr>
	<%
		}
	%>
</table>				