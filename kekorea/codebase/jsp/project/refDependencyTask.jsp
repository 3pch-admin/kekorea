<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="e3ps.project.service.ProjectHelper"%>
<%@page import="wt.epm.EPMDocument"%>
<%@page import="e3ps.project.dto.TaskViewData"%>
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
	ArrayList<TargetTaskSourceTaskLink> list = ProjectHelper.manager.getTargetTaskSourceTaskLinkByTarget(task);
	int size = list.size();
%>
<table class="project_table">
	<colgroup>
		<col width="40">
		<col width="200">
		<col width="200">
		<col width="200">
		<col width="200">
		<col width="100">
	</colgroup>
	<thead>
		<tr>
			<th>
				<input type="checkbox" name="allParts" id="allParts">
			</th>
			<th>태스크 타입</th>
			<th>태스크 명</th>
			<th>총기간[공수](일)</th>
			<th>할당율</th>
			<th>진행률/적정</th>
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
		<td class="center"><%=dd.duration %>[<font color="red"><%=dd.holiday %></font>](일)</td>	
		<td class="center"><%=dd.allocate %>%</td>
		<td class="center"><%=dd.progress %>%/<%=StringUtils.numberFormat(dd.comp, "###") %>%</td>	
	</tr>
	<%
		}
		if(list.size() == 0) {
	%>
	<tr>
		<td class="nodata" colspan="6">선행 태스크가 없습니다.</td>
	</tr>
	<%
		}
	%>
</table>				