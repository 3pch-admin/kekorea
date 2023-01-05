<%@page import="e3ps.common.code.CommonCode"%>
<%@page import="e3ps.approval.beans.NoticeViewData"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	//popup check
	String popup = (String) request.getParameter("popup");
	boolean isPopup = Boolean.parseBoolean(popup);

	// data
	CommonCode data = (CommonCode) request.getAttribute("data");
%>

<td valign="top">
	<!-- script -->
	<script type="text/javascript">
	$(document).ready(function() {
		
		$("#modifyCodeBtn").click(function() {
			var url = "/Windchill/plm/admin/modifyCodeAction";
			var dialogs = $(document).setOpen();
			var box = $(document).setNonOpen();
			box.confirm({
				theme : "info",
				title : "확인",
				msg : "코드를 수정 하시겠습니까?"
			}, function() {
				if (this.key == "ok") {
					var params = $(document).getFormParams();
					params.oid = $("input[name=oid]").val();
					params.commonName = $("input[name=commonName]").val();
					params.commonCode = $("input[name=commonCode]").val();
					params.description = $("input[name=description]").val();
					params.uses = $("input[id=uses]").is(":checked");
					$(document).ajaxCallServer(url, params, function(data) {
						dialogs.alert({
							theme : "alert",
							title : "결과",
							msg : data.msg
						}, function() {
							// 버튼 클릭 ok, esc
							if (this.key == "ok" || this.state == "close") {
								if (data.reload) {
									self.close();
									opener.document.location.href = data.url;
								}
							}
						})
					}, true);
				}

				if (this.key == "cancel" || this.state == "close") {
					mask.close();
				}
			})
		})
		
		
		
		
		
	})
	</script>
	
	<input type="hidden" name="oid" id="oid" value="<%=CommonUtils.getOIDString(data) %>">
	<input type="hidden" name="popup" id="popup" value="<%=isPopup %>">

	<table class="btn_table">
	<tr>
		<td>
			<div class="header_title">
				<i class="axi axi-subtitles"></i><span>코드 정보</span>
			</div>
		</td>
		<td>
			<div class="right">
				<input type="button" value="수정" id="modifyCodeBtn" title="수정">
				<input type="button" value="닫기" id="closeNoticeBtn" title="목록" class="redBtn">
			</div>
		</td>
	</tr>
	</table>
	<table class="view_table">
		<tr>
			<th class="min-wid200">코드명</th>
			<td><input type="hidden" name="commonName" id="commonName" class="AXInput wid300" value="<%=data.getName()%>"><%=data.getName()%></td>
		</tr>
		<tr>
			<th>코드</th>
			<td><input type="hidden" name="commonCode" id="commonCode" class="AXInput wid300" value="<%=data.getCode()%>"><%=data.getCode() %></td>
		</tr>
		<tr>
			<th>설명</th>
			<td><input type="text" name="description" id="description" class="AXInput wid300" value="<%=data.getDescription()%>"></td>
		</tr>
		<tr>
			<th>사용여부</th>
			<td><input type="checkbox" name="uses" id="uses" value="complex" <%if(data.isUses()) {%> checked="checked" <%} %>></td>
		</tr>
					
	</table>
	
</td>