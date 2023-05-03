<%@page import="e3ps.common.util.CommonUtils"%>
<%@page import="e3ps.project.task.Task"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.project.task.dto.TaskDTO"%>
<%@page import="e3ps.project.template.dto.TemplateDTO"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<Task> list = (ArrayList<Task>) request.getAttribute("list");
TemplateDTO data = (TemplateDTO) request.getAttribute("data");
TaskDTO dto = (TaskDTO) request.getAttribute("dto");
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js?v=1010"></script>
</head>
<body style="margin: 0px 0px 0px 5px;">
	<form>
		<input type="hidden" name="isAdmin" id="isAdmin" value="<%=isAdmin%>">
		<input type="hidden" name="sessionName" id="sessionName" value="<%=sessionUser.getFullName()%>">
		<input type="hidden" name="sessionId" id="sessionId" value="<%=sessionUser.getName()%>">
		<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">

		<table class="view-table">
			<colgroup>
				<col width="150">
				<col width="330">
				<col width="150">
				<col width="330">
			</colgroup>
			<tr>
				<th class="lb">템플릿 이름</th>
				<td class="indent5"><%=data.getName()%></td>
				<th>총 기간</th>
				<td class="indent5"><%=data.getDuration()%>일
				</td>
			</tr>
			<tr>
				<th class="lb">작성자</th>
				<td class="indent5"><%=data.getCreator()%></td>
				<th>작성일</th>
				<td class="indent5"><%=data.getCreatedDate_txt()%></td>
			</tr>
			<tr>
				<th class="lb">수정자</th>
				<td class="indent5"><%=data.getModifier()%></td>
				<th>수정일</th>
				<td class="indent5"><%=data.getModifiedDate_txt()%></td>
			</tr>
			<tr>
				<th class="lb">설명</th>
				<td class="indent5" colspan="3">
					<textarea rows="6" class="description" readonly="readonly"><%=data.getDescription()%></textarea>
				</td>
			</tr>
		</table>

		<div class="info-header">
			<img src="/Windchill/extcore/images/header.png">
			태스트 정보
		</div>

		<table class="view-table">
			<colgroup>
				<col width="150">
				<col width="330">
				<col width="150">
				<col width="330">
			</colgroup>
			<tr>
				<th class="lb">태스크 명</th>
				<td class="indent5"><%=dto.getName()%></td>
				<th>태스크 타입</th>
				<td class="indent5"><%=dto.getTaskType()%></td>
			</tr>
			<tr>
				<th class="lb">기간</th>
				<td class="indent5"><%=dto.getDuration()%>일
				</td>
				<th>할당율</th>
				<td class="indent5"><%=dto.getAllocate()%>%
				</td>
			</tr>
			<tr>
				<th class="lb">작성자</th>
				<td class="indent5"><%=dto.getCreator()%></td>
				<th>작성일</th>
				<td class="indent5"><%=dto.getCreatedDate_txt()%></td>
			</tr>
		</table>

		<div class="info-header">
			<img src="/Windchill/extcore/images/header.png">
			선행 태스크
		</div>

		<table class="view-table">
			<tr>
				<th class="lb rb">태스크 타입</th>
				<th class="rb">태스크 명</th>
				<th class="rb">기간</th>
				<th class="rb">할당율</th>
				<th class="rb">작성자</th>
				<th class="rb">작성일</th>
			</tr>
			<%
			for (Task task : list) {
			%>
			<tr>
				<td><%=task.getTaskType() != null ? task.getTaskType().getName() : ""%></td>
				<td><%=task.getName()%></td>
				<td><%=task.getDuration()%>일
				</td>
				<td><%=task.getAllocate() != null ? task.getAllocate() : 0%>%
				</td>
				<td><%=task.getOwnership().getOwner().getFullName()%></td>
				<td><%=CommonUtils.getPersistableTime(task.getCreateTimestamp())%></td>
			</tr>
			<%
			}
			if (list.isEmpty()) {
			%>
			<tr>
				<td class="center" colspan="6">
					<font color="red">
						<b>정의된 선행태스크가 없습니다.</b>
					</font>
				</td>
			</tr>
			<%
			}
			%>
		</table>

		<script type="text/javascript">
			document.addEventListener("DOMContentLoaded", function() {
				parent.closeLayer();
			})
		</script>
	</form>
</body>
</html>