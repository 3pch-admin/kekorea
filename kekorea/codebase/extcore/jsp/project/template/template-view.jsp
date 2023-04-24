<%@page import="e3ps.project.template.dto.TemplateDTO"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
TemplateDTO dto = (TemplateDTO) request.getAttribute("dto");
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
				<th class="lb">템플릿 명</th>
				<td class="indent5"><%=dto.getName()%></td>
				<th>기간</th>
				<td class="indent5"><%=dto.getDuration()%>일
				</td>
			</tr>
			<tr>
				<th class="lb">작성자</th>
				<td class="indent5"><%=dto.getCreator()%></td>
				<th>작성일</th>
				<td class="indent5"><%=dto.getCreatedDate_txt()%></td>
			</tr>
			<tr>
				<th class="lb">수정자</th>
				<td class="indent5"><%=dto.getModifier()%></td>
				<th>수정일</th>
				<td class="indent5"><%=dto.getModifiedDate_txt()%></td>
			</tr>
			<tr>
				<th class="lb">설명</th>
				<td class="indent5" colspan="3">
					<textarea rows="6" class="description" readonly="readonly"><%=dto.getDescription()%></textarea>
				</td>
			</tr>
		</table>

		<div class="info-header">
			<img src="/Windchill/extcore/images/header.png">
			구성원 정보
		</div>

		<table class="view-table">
			<colgroup>
				<col width="150">
				<col width="330">
				<col width="150">
				<col width="330">
			</colgroup>
			<tr>
				<th class="lb">총괄 책임자</th>
				<td class="indent5">
					<%
					if (isAdmin) {
						String name = dto.getPm() != null ? dto.getPm().getFullName() : "";
						String oid = dto.getPm() != null ? dto.getPm().getPersistInfo().getObjectIdentifier().getStringValue() : "";
					%>
					<input type="text" name="pm" id="pm" value="<%=name%>">
					<input type="hidden" name="pmOid" id="pmOid" value="<%=oid%>">
					<%
					} else {
					%>
					<%=dto.getPm() != null ? dto.getPm().getFullName() : "지정안됨"%>
					<%
					}
					%>
				</td>
				<th>세부일정 책임자</th>
				<td class="indent5">
					<%
					if (isAdmin) {
						String name = dto.getSubPm() != null ? dto.getSubPm().getFullName() : "";
						String oid = dto.getSubPm() != null ? dto.getSubPm().getPersistInfo().getObjectIdentifier().getStringValue() : "";
					%>
					<input type="text" name="subPm" id="subPm" value="<%=name%>">
					<input type="hidden" name="subPmOid" id="subPmOid" value="<%=oid%>">
					<%
					} else {
					%>
					<%=dto.getSubPm() != null ? dto.getSubPm().getFullName() : "지정안됨"%>
					<%
					}
					%>
				</td>
			</tr>
		</table>
		<script type="text/javascript">
			document.addEventListener("DOMContentLoaded", function() {
				finderUser("pm");
				finderUser("subPm");
			})
		</script>
	</form>
</body>
</html>