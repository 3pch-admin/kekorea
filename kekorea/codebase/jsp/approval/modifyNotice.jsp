<%@page import="e3ps.approval.beans.NoticeViewData"%>
<%@page import="e3ps.part.column.PartProductColumnData"%>
<%@page import="wt.part.WTPart"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	NoticeViewData data = (NoticeViewData) request.getAttribute("data");
	String popup = (String) request.getParameter("popup");
	boolean isPopup = Boolean.parseBoolean(popup);
%>
<td valign="top">
	<!-- script -->
	<script type="text/javascript">
	$(document).ready(function() {
		upload.pageStart("<%=data.oid%>", "", "primary");
		upload.pageStart("<%=data.oid%>", "", "secondary");
		
		$("input").checks();
		
		var len = "<%=data.description.length()%>";
		$("#descNoticeCnt").text(len);
	})
	</script>
	<input type="hidden" name="oid" value="<%=data.oid %>">
	<input type="hidden" name="popup" id="popup" value="<%=isPopup %>">
	<!-- create header title -->
	<div class="header_title">
		<i class="axi axi-subtitles"></i><span>공지사항 수정</span>
		<!-- req msg -->
		<font class="reqMsg">(빨간색 속성 값은 필수 입력 값입니다.)</font>
	</div>
	<%
		if(isPopup) {
	%>
	<div class="right">
		<input type="button" value="수정" id="modifyNoticeBtnAction" title="수정"> 
		<input type="button" value="닫기" id="closeNoticeBtn" title="닫기" class="redBtn">
	</div>	
	<%
		}
	%>
	
	<table class="create_table">
		<tr>
			<th class="min-wid200"><font class="req">공지사항 제목</font></th>
			<td colspan="3">
				<input value="<%=data.name %>" type="text" name="name" id="name" class="AXInput wid300">
			</td>
		</tr>		
		<tr>
			<th>작성자</th>
			<td><%=data.creator %></td>
			<th>작성일</th>
			<td><%=data.createDate %></td>			
		</tr>			
		<tr>
			<th>설명<br><span id="descNoticeCnt">0</span>/1000</th>
			<td colspan="3">
				<!-- rows 세로, cols 가로 -->
				<textarea class="AXTextarea" name="descriptionNotice" id="descriptionNotice" rows="3" cols=""><%=data.description %></textarea>
			</td>			
		</tr>	
		<tr>
			<th><font class="req">주 첨부파일</font>
			<td colspan="3">
				<!-- upload.js see -->
				<div class="AXUpload5" id="primary_layer"></div>
			</td>
		</tr>
		<tr>
			<th>첨부파일<span id="fileCount"></span></th>
			<td colspan="3">
				<!-- upload.js see -->
				<div class="AXUpload5" id="secondary_layer"></div>
				<div class="AXUpload5QueueBox_list" id="uploadQueueBox"></div>
			</td>
		</tr>				
	</table>
	
	<%
		if(!isPopup) {
	%>
	<table class="btn_table">
		<tr>
			<td class="center">
				<input type="button" value="수정" id="modifyNoticeBtnAction" title="수정"> 
				<input type="button" value="뒤로" id="backNoticeBtn" title="뒤로" class="blueBtn" data-oid="<%=data.oid %>">
			</td>
		</tr>
	</table>	
	<%
		}
	%>
</td>