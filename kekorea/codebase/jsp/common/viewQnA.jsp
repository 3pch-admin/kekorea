<%@page import="e3ps.approval.beans.NoticeViewData"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@page import="e3ps.document.beans.DocumentViewData"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	// popup check
	String popup = (String) request.getParameter("popup");
	boolean isPopup = Boolean.parseBoolean(popup);

	// data
	NoticeViewData data = (NoticeViewData) request.getAttribute("data");
	
	// isAdmin
	boolean isAdmin = CommonUtils.isAdmin();
%>

<td valign="top">
	<!-- script -->
	<script type="text/javascript">
	$(document).ready(function() {
		

	})
	</script>
	
	<input type="hidden" name="oid" id="oid" value="<%=data.oid %>">
	<input type="hidden" name="popup" id="popup" value="<%=isPopup %>">

	<div class="AXTabs left">
		<div class="AXTabsTray">
			<a class="AXTab on" title="공지사항 정보">공지사항 정보</a>
			<a class="AXTab content" title="첨부파일">
			첨부파일&nbsp;&nbsp;<img src="/Windchill/jsp/images/remove16x16.gif" class="pos4" id="contentImg">
			</a>
		</div>
	</div>
	<%
		if(isPopup) {
	%>
	<div class="right">
		<%
			if(isAdmin) {
		%>
		<input type="button" value="삭제" id="deleteNoticeBtn" title="삭제" class="redBtn">
		<%
			}
		%>	
		<input type="button" value="수정" id="modifyNoticeBtn" title="수정">
		<input type="button" value="닫기" id="closeNoticeBtn" title="닫기" class="redBtn">
	</div>
	<%
		}
	%>
	
	<table class="view_table">
		<colgroup>
			<col width="250">
			<col width="*">
			<col width="250">
			<col width="*">
		</colgroup>
		<tr>
			<th>문서 제목</th>
			<td colspan="3"><%=data.name %></td>
		</tr>
		<tr>
			<th>작성자</th>
			<td><%=data.creator %></td>
			<th>작성일</th>
			<td><%=data.createDate %></td>			
		</tr>			
		<tr>
			<th>설명</th>
			<td colspan="3">
				<textarea rows="3" cols="" class="AXTextarea" readonly="readonly"><%=data.description %></textarea>
			</td>			
		</tr>					
	</table>
	
	<div class="content_wrap">
		<div class="header_title margin_top10">
			<i class="axi axi-subtitles"></i><span>첨부파일</span>
		</div>
			<jsp:include page="/jsp/common/secondary.jsp">
			<jsp:param value="<%=data.oid %>" name="oid"/>
		</jsp:include>
	</div>
	<%
		if(!isPopup) {
	%>
	<table class="btn_table">
		<tr>
			<td class="center">
				<%
					if(isAdmin) {
				%>
				<input type="button" value="삭제" id="deleteNoticeBtn" title="삭제" class="redBtn">
				<%
					}
				%>
				<input type="button" value="수정" id="modifyNoticeBtn" title="수정">
				<input type="button" value="목록" id="listNoticeBtn" title="목록" class="blueBtn">			
			</td>
		</tr>
	</table>
	<%
		}
	%>
</td>