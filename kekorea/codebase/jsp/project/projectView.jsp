<%@page import="e3ps.project.beans.ProjectViewData"%>
<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@page import="e3ps.project.task.Task"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.project.template.beans.TemplateViewData"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ProjectViewData data = (ProjectViewData) request.getAttribute("data");
// ArrayList<Task> list = (ArrayList<Task>) request.getAttribute("list");
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/jsp/common/layouts/include_css.jsp"%>
<%@include file="/jsp/common/layouts/include_script.jsp"%>
</head>
<body style="margin: 0 auto;">
	<table class="btn_table">
		<tr>
			<td>
				<div class="header_title">
					<i class="axi axi-subtitles"></i>
					<span>프로젝트 정보</span>
				</div>
			</td>
		</tr>
	</table>
	<table class="project_table">
		<%
		if (!data.isQuotation()) {
		%>
		<tr>
			<th class="min-wid150">KEK 작번</th>
			<th class="min-wid150">거래처</th>
			<th class="min-wid150">설치장소</th>
			<th class="min-wid150">막종</th>
			<th class="min-wid150">발행일</th>
			<th class="min-wid150">요구 납기일</th>
			<th rowspan="4" class="border-none bgnone min-wid20">&nbsp;</th>
			<th rowspan="2" class="border-left min-wid100">진행률</th>
			<td rowspan="2" class="center min-wid100 border-top-blue "><%=data.getKekProgress()%>%
			</td>
		</tr>

		<tr>
			<td class="center"><%=data.getKekNumber()%></td>
			<td class="center"><%=data.getCustomer_name()%></td>
			<td class="center"><%=data.getInstall_name()%></td>
			<td class="center"><%=data.getMak_name()%></td>
			<td class="center"><%=data.getPDate_txt()%></td>
			<td class="center"><%=data.getCustomDate_txt()%></td>
		</tr>
		<tr>
			<th>KE 작번</th>
			<th>USER ID</th>
			<th>작번 유형</th>
			<th>모델</th>
			<th colspan="2">작업 내용</th>
			<th class="border-left">기계</th>
			<td class="center"><%=data.getMachineProgress()%>%
			</td>
		</tr>
		<tr>
			<td class="center"><%=data.getKekNumber()%></td>
			<td class="center"><%=data.getUserID()%>
			<td class="center"><%=data.getProjectType_name()%>
			<td class="center"><%=data.getModel()%>
			<td class="indent10" colspan="2"><%=data.getDescription()%></td>
			<th class="border-left">전기</th>
			<td class="center"><%=data.getElecProgress()%>%
			</td>
		</tr>
		<%
		} else {
		%>
		<tr>
			<th class="min-wid150">KEK 작번</th>
			<th class="min-wid150">거래선</th>
			<th class="min-wid150">설치장소</th>
			<th class="min-wid150">막종</th>
			<th class="min-wid150">발행일</th>
			<th class="min-wid150">요청 납기일</th>
			<th rowspan="4" class="border-none bgnone min-wid20">&nbsp;</th>
			<th rowspan="5" class="border-left min-wid100">진행률</th>
			<td rowspan="5" class="center min-wid100 border-top-blue "><%=data.getKekProgress()%>%
			</td>
		</tr>
		<tr>
			<td class="center"><%=data.getKekNumber()%></td>
			<td class="center"><%=data.getCustomer_name()%></td>
			<td class="center"><%=data.getInstall_name()%></td>
			<td class="center"><%=data.getMak_name()%></td>
			<td class="center"><%=data.getPDate_txt()%></td>
			<td class="center"><%=data.getCustomDate_txt()%></td>
		</tr>
		<tr>
			<th>KE 작번</th>
			<th>USER ID</th>
			<th>작번 유형</th>
			<th colspan="3">작업 내용</th>
		</tr>
		<tr>
			<td class="center"><%=data.getKeNumber()%></td>
			<td class="center"><%=data.getUserID()%>
			<td class="center"><%=data.getProjectType_name()%>
			<td class="indent10" colspan="3"><%=data.getDescription()%></td>
		</tr>
		<%
		}
		%>
	</table>
</body>
</html>