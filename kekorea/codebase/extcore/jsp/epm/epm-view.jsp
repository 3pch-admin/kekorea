<%@page import="wt.log4j.SystemOutFacade"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.common.util.ContentUtils"%>
<%@page import="e3ps.workspace.notice.dto.NoticeDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
NoticeDTO dto = (NoticeDTO) request.getAttribute("dto");
%>
<%@include file="/extcore/include/auigrid.jsp"%>
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
			<input type="button" value="수정" title="수정" class="green">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>
<div id="tabs">
	<ul>
		<li>
			<a href="#tabs-1">기본정보</a>
		</li>
		<li>
			<a href="#tabs-2">도면속성</a>
		</li>
		<li>
			<a href="#tabs-3">관련작번</a>
		</li>
		<li>
			<a href="#tabs-4">버전이력</a>
		</li>
		<li>
			<a href="#tabs-5">결재이력</a>
		</li>
	</ul>
	<div id="tabs-1">
	</div>
</div>
<script type="text/javascript">
	document.addEventListener("DOMContentLoaded", function() {
		$("#tabs").tabs();
	})
</script>