<%@page import="e3ps.org.dto.UserViewData"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@page import="java.util.Vector"%>
<%-- <%@page import="e3ps.document.beans.DocumentViewData"%> --%>
<%@page import="e3ps.part.beans.PartViewData"%>
<%@page import="wt.part.WTPart"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	UserViewData data = (UserViewData) request.getAttribute("data");
	String oid = (String) request.getAttribute("oid");
	String[] dutys = (String[]) request.getAttribute("dutys");
	String[] ranks = (String[]) request.getAttribute("ranks");
	String popup = (String) request.getParameter("popup");
	boolean isPopup = Boolean.parseBoolean(popup);
%>
<td valign="top"><script type="text/javascript">
	$(document).ready(function() {

		$("#modifyBtn").click(function() {
			var dialogs = $(document).setOpen();
			$name = $("input[name=name]");
			if ($name.val() == "") {
				dialogs.alert({
					title : "사용자 이름 미입력",
					msg : "사용자 이름을 입력하세요."
				}, function() {
					if (this.key == "ok" || this.state == "close") {
						$name.focus();
					}
				})
				return false;
			}
			
			var box = $(document).setNonOpen();
			box.confirm({
				theme : "info",
				title : "사용자 정보 수정",
				msg : "사용자 정보를 수정하시겠습니까?"
			}, function() {
				if (this.key == "ok") {
					var url = "/Windchill/plm/org/modifyUserAction";
					var params = new Object();
					params.oid = "<%=oid%>";
					params = $(document).getFormData(params);
					$(document).ajaxCallServer(url, params, function(data) {
						dialogs.alert({
							theme : "alert",
							title : "결과",
							msg : data.msg
						}, function() {
							if (this.key == "ok") {
								document.location.href = data.url;
								self.close();
							}
						})
					}, true);
				} else if (this.key == "cancel" || this.state == "close") {
					mask.close();
				}
			})
		})

		$("#duty").bindSelect();
		$("#rank").bindSelect();
	})
</script> <%
 	if (isPopup) {
 %>
	<table class="btn_table top_margin0">
		<tr>
			<td class="left">
				<div class="header_title pos5">
					<i class="axi axi-subtitles"></i><span>사용자 정보</span><font class="reqMsg">(빨간색 속성 값은 필수 입력 값입니다.)</font>
				</div>
			</td>
			<td class="right">
				<%
					// 추가 당사자..
						if (CommonUtils.isAdmin()) {
				%> <input type="button" class="redBtn" value="수정 (M)" id="modifyBtn" title="수정 (M)"> <%
 	}
 %> <input type="button" class="redBtn" value="닫기 (C)" id="closeUserBtn" title="닫기 (C)">
			</td>
		</tr>
	</table> <%
 	} else {
 %>
	<div class="header_title">
		<i class="axi axi-subtitles"></i><span>사용자 정보 수정</span>
	</div> <%
 	}
 %>
	<table class="view_table">
		<colgroup>
			<col width="230">
			<col width="400">
			<col width="230">
			<col width="400">
			<col width="288">
		</colgroup>
		<tr>
			<th>아이디</th>
			<td colspan="3"><%=data.id%></td>
			<td class="center" rowspan="8"><img src="/Windchill/jsp/images/user_photo_default.gif"></td>
		</tr>
		<tr>
			<th><font class="req">이름</font></th>
			<td colspan="3"><input class="AXInput" type="text" name="name" value="<%=data.name%>"></td>
		</tr>
		<tr>
			<th>부서</th>
			<td colspan="3"><%=data.departmentName%></td>
		</tr>
		<tr>
			<th><font class="req">직급</font></th>
			<td colspan="3">
<%-- 			<input class="AXInput" type="text" name="duty"  value="<%=data.duty%>"/> --%>
			<select name="duty" class="AXSelect wid150" id="duty">
					<option value="<%=data.duty%>"><%=data.duty%></option>
					<%
						for (int i = 0; i < dutys.length; i++) {
					%>
					<option value="<%=dutys[i]%>"><%=dutys[i]%></option>
					<%
						}
					%>
			</select>
			</td>
		</tr>
		<tr>
			<th><font class="req">직위</font></th>
			<td colspan="3">
<%-- 			<input class="AXInput" type="text" name="rank"  value="<%=data.rank%>"/> --%>
			<select name="rank" class="AXSelect wid150" id="rank">
					<option value="<%=data.duty%>"><%=data.duty%></option>
					<%
						for (int i = 0; i < ranks.length; i++) {
					%>
					<option value="<%=ranks[i]%>"><%=ranks[i]%></option>
					<%
						}
					%>
			</select>
			</td>
		</tr>
		<tr>
			<th>이메일</th>
			<td colspan="3"><input class="AXInput" type="text" name="email"  value="<%=data.email.substring(0, data.email.indexOf("@"))%>"/><span>@kokusai-electric.com</span></td>
		</tr>
		<tr>
			<th>퇴사여부</th>
			<td colspan="3"><%=data.resign%></td>
		</tr>
		<tr>
			<th>전화번호</th>
			<td colspan="3"><%=data.mobile != null ? data.mobile : ""%></td>
		</tr>
	</table> <%
 	if (!isPopup) {
 %>
	<table class="btn_table">
		<tr>
			<td class="center">
				<%
					// 추가 당사자..
						if (CommonUtils.isAdmin()) {
				%> <input type="button" class="redBtn" value="수정 (M)" id="modifyBtn" title="수정 (M)"> <%
 	}
 %> <input type="button" class="blueBtn" value="뒤로 (B)" id="backBtn" title="뒤로 (B)">
			</td>
		</tr>
	</table> <%
 	}
 %></td>