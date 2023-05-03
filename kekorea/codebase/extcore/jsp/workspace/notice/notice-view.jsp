<%@page import="wt.content.ContentHelper"%>
<%@page import="wt.content.ContentRoleType"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@page import="e3ps.workspace.notice.Notice"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.log4j.SystemOutFacade"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.common.util.ContentUtils"%>
<%@page import="e3ps.workspace.notice.dto.NoticeDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
NoticeDTO dto = (NoticeDTO) request.getAttribute("dto");
%>
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				공지사항 정보
			</div>
		</td>
		<td class="right">
			<input type="button" value="수정" title="수정" class="green" onclick="modify();">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>
<table class="view-table">
	<colgroup>
		<col width="150">
		<col width="300">
		<col width="150">
		<col width="300">
	</colgroup>
	<tr>
		<th class="lb">공지사항 제목</th>
		<td class="indent5" colspan="3"><%=dto.getName()%></td>
	</tr>
	<tr>
		<th class="lb">작성자</th>
		<td class="indent5"><%=dto.getCreator()%></td>
		<th class="lb">작성일</th>
		<td class="indent5"><%=dto.getCreatedDate().toString().substring(0, 10)%></td>
	</tr>
	<tr>
		<th class="lb">설명</th>
		<td colspan="3" class="indent5">
			<textarea name="descriptionNotice" id="descriptionNotice" rows="12" cols="" readonly="readonly"><%=dto.getDescription()%></textarea>
		</td>
	</tr>
	<tr>
		<th class="lb">주 첨부파일</th>
		<td class="indent5" colspan="3">
			<jsp:include page="/extcore/jsp/common/primary-view.jsp">
				<jsp:param value="<%=dto.getOid()%>" name="oid" />
			</jsp:include>
		</td>
	</tr>
	<tr>
		<th class="lb">첨부파일</th>
		<td class="indent5" colspan="3">
			<jsp:include page="/extcore/jsp/common/secondary-view.jsp">
				<jsp:param value="<%=dto.getOid()%>" name="oid" />
			</jsp:include>
		</td>
	</tr>
</table>
<script type="text/javascript">
	document.addEventListener("DOMContentLoaded", function() {
	})

	function modify() {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/notice/modify?oid=" + oid);
		document.location.href = url;
	}
</script>