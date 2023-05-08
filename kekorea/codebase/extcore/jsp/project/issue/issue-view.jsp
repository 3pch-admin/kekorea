<%@page import="wt.log4j.SystemOutFacade"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="e3ps.common.util.ContentUtils"%>
<%@page import="e3ps.project.issue.beans.IssueDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
IssueDTO dto = (IssueDTO) request.getAttribute("dto");
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
%>
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				특이사항 정보
			</div>
		</td>
		<td class="right">
			<%
			if (isAdmin) {
			%>
			<input type="button" value="삭제" title="삭제" class="red" onclick="_delete();">
			<%
			}
			%>
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>
<div id="tabs">
	<ul>
		<li>
			<a href="#tabs-1">기본정보</a>
		</li>
	</ul>
	<div id="tabs-1">
		<table class="view-table">
			<colgroup>
				<col width="150">
				<col width="400">
				<col width="150">
				<col width="400">
			</colgroup>
			<tr>
				<th class="lb">특이사항 제목</th>
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
					<textarea rows="6" readonly="readonly"><%=dto.getContent()%></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">KEK 작번</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/jsp/common/project-include.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
						<jsp:param value="view" name="mode" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">주 첨부파일</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/jsp/common/secondary-view.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
					</jsp:include>
				</td>
			</tr>
		</table>
	</div>
</div>
<script type="text/javascript">
	function _delete() {
		if (!confirm("삭제 하시겠습니까?")) {
			return false;
		}

		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/issue/delete?oid=" + oid);
		openLayer();
		call(url, null, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.document.location.reload();
				self.close();
			} else {
				closeLayer();
			}
		}, "GET");

	}

	document.addEventListener("DOMContentLoaded", function() {
		$("#tabs").tabs();
		createAUIGrid9(columns9);
		AUIGrid.resize(myGridID9);
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID9);
	});
</script>