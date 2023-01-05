<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="e3ps.common.util.ContentUtils"%>
<%@page import="java.io.File"%>
<%@page import="java.util.Vector"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	Vector<File> list = (Vector<File>) request.getAttribute("list");
	String name = (String) request.getParameter("name");
	String fileType = (String) request.getParameter("fileType");
%>
<td valign="top">
<script type="text/javascript">
	$(document).ready(function() {
		$(".list_table").tableHeadFixer();
// 		$("input").checks();

		$("#searchManualBtn").click(function() {
			$(document).onLayer();
			document.forms[0].submit();
		})
	}).keypress(function(e) {
		var keyCode = e.keyCode;
		if(keyCode == 13 && e.target.tagName != "BUTTON") {
			$(document).onLayer();
			document.forms[0].submit();
		}
	})
</script>
	<!-- list header title -->
	<div class="header_title">
		<i class="axi axi-subtitles"></i><span>설치파일</span>
		<!-- info search -->
		<jsp:include page="/jsp/common/search_info.jsp"></jsp:include>
	</div>
	<table class="search_table">
		<tr>
			<th>파일명</th>
			<td><input type="text" name="name" class="AXInput wid200" value="<%=StringUtils.replaceToValue(name)%>"></td>
			<th>파일양식</th>
			<td><input type="text" class="AXInput wid150" name="fileType" id="fileType" value="<%=fileType != null ? fileType : ""%>"> <i title="삭제 (D)" class="axi axi-ion-close-circled delete-text"
				data-target="fileType"></i></td>
		</tr>
	</table>

	<table class="btn_table">
		<tr>
			<td class="right">
				<input type="button" value="조회" class="blueBtn" id="searchManualBtn" title="조회"> 
				<input type="button" value="초기화" id="initGrid" title="초기화">
			</td>
		</tr>
	</table>
	
	<div class="list_container">
		<table class="list_table indexed sortable-table">
			<colgroup>
<!-- 				<col width="50"> -->
				<col width="*">
				<col width="60">
			</colgroup>
			<thead>
				<tr>
<!-- 					<th><input type="checkbox"></th> -->
					<th>파일명</th>
					<th>&nbsp;</th>
				</tr>
			</thead>
			<tbody>
				<%
					for (int i = 0; i < list.size(); i++) {
						File f = (File) list.get(i);
						String url = "/Windchill/jsp/common/setupFiles/" + f.getName();
				%>
				<tr>
<!-- 					<td><input type="checkbox"></td> -->
					<td class="left indent5"><a href="<%=url%>"><img class="pos2" src="<%=ContentUtils.getFileIcon(f.getName())%>"> &nbsp;<%=f.getName()%></a></td>
					<td><a href="<%=url%>"><img class="pos2" src="<%=ContentUtils.getFileIcon(f.getName())%>"></a></td>
				</tr>
				<%
					}
					if (list.size() == 0) {
				%>
				<tr>
					<td class="nodata_icon" colspan="3"><a class="axi axi-info-outline"></a> <span>조회 결과가 없습니다.</span></td>
				</tr>
				<%
					}
				%>
			</tbody>
		</table>
	</div>
</td>