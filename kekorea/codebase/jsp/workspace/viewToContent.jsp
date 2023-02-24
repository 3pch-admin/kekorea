<%@page import="e3ps.common.util.CommonUtils"%>
<%@page import="oracle.net.aso.s"%>
<%@page import="java.util.Vector"%>
<%@page import="e3ps.approval.beans.NoticeViewData"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String oid = (String) request.getParameter("oid");
	String popup = (String) request.getParameter("popup");
	boolean isPopup = Boolean.parseBoolean(popup);
	NoticeViewData data = (NoticeViewData) request.getAttribute("data");
%>
<td valign="top"><script type="text/javascript">
	$(document).ready(function() {

		$("#modifyBtn").click(function() {
			
			$(document).onLayer();
			document.location.href = "/Windchill/plm/approval/modifyNotice?oid=<%=data.oid%>&popup=<%=isPopup%>";
		})

		$("#deleteBtn").click(function() {
			var dialogs = $(document).setOpen();
			dialogs.confirm({
				theme : "info",
				title : "공지사항 삭제",
				msg : "공지사항을 삭제 하시겠습니까?\n삭제한 데이터는 복구가 불가능합니다."
			}, function() {
				if (this.key == "ok") {
					var url = "/Windchill/plm/approval/deleteNoticeAction";
					var params = new Object();
					var array = new Array();
					array.push("<%=data.oid%>");
					params.list = array;
					$(document).ajaxCallServer(url, params, function(data) {
						dialogs.alert({
							theme : "alert",
							title : "결과",
							msg : data.msg
						}, function() {
							if (this.key == "ok") {
								document.location.href = data.url;
							}
						})
					}, true);
				}
			})
		})
		
		$(".noticeView").click(function() {
			
			$(document).onLayer();
			document.location.href = "/Windchill/plm/approval/viewNotice?oid=<%=oid%>&popup=<%=isPopup%>";
		})
		
		$(".viewToContent").click(function() {
			
			$(document).onLayer();
			document.location.href = "/Windchill/plm/approval/viewToContent?oid=<%=oid%>&popup=<%=isPopup%>";
		})
	})
</script> <%
 	if (isPopup) {
 %>
	<table class="btn_table top_margin0">
		<tr>
			<td class="left">
				<div class="AXTabs">
					<div class="AXTabsTray">
						<a class="AXTab noticeView" title="공지사항 정보">공지사항 정보</a> 
						<a class="AXTab on viewToContent" title="첨부파일">첨부파일</a>
						<div class="ax-clear"></div>
					</div>
				</div>
			</td>
			<td class="right">
				<input type="button" class="redBtn" value="닫기 (C)" id="closeBtn" title="닫기 (C)">
			</td>
		</tr>
	</table> <%
 	} else {
 %>
	<div class="AXTabs">
		<div class="AXTabsTray">
			<a class="AXTab noticeView" title="공지사항 정보">공지사항 정보</a> 
			<a class="AXTab on viewToContent" title="첨부파일">첨부파일</a>
			<div class="ax-clear"></div>
		</div>
	</div> <%
 	}
 %>
	<table class="view_table">
		<colgroup>
			<col width="240">
			<col width="400">
			<col width="240">
			<col width="400">
		</colgroup>
		<tr>
			<th>공지사항 제목</th>
			<td colspan="3"><%=data.name%></td>
		</tr>
		<tr>
			<th>작성자</th>
			<td><%=data.creator%></td>
			<th>작성일</th>
			<td><%=data.createDate%></td>
		</tr>
		<tr>
			<th>설명</th>
			<td colspan="3"><textarea readonly="readonly" rows="3" cols="" class="AXTextarea border_none"><%=data.description != null ? data.description : ""%> </textarea></td>
		</tr>
	</table>
	<div class="header_title margin_top10">
		<i class="axi axi-subtitles"></i><span>첨부파일</span>
	</div>

	<div class="clear5"></div> <jsp:include page="/jsp/common/secondary.jsp">
		<jsp:param value="<%=data.oid%>" name="oid" />
	</jsp:include> 
</td>