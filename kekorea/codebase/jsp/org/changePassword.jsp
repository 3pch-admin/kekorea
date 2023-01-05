<%@page import="e3ps.admin.service.AdminHelper"%>
<%@page import="e3ps.admin.PasswordSetting"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	WTUser user = (WTUser) request.getAttribute("user");
	PasswordSetting ps = AdminHelper.manager.getPasswordSetting();
%>    
<td valign="top">
	<!-- script area -->
	<script type="text/javascript">
	$(document).ready(function() {
		
		$("#changePasswordBtn").click(function() {
			var dialogs = $(document).setOpen();
			var url = this.changePasswordActionUrl;
			$password = $("input[name=password]");
			$repassword = $("input[name=repassword]");

			var regExp = /^(?=(.*\d){2})/;
			var regExp2 = /^(?=(.*[a-zA-Z]){2})/;
			var regExp3 = /^(?=(.*[!%&'()._:;]){2})/;
			// var regExp = /^[0-9]{2,}$/;

			<%
				if(ps.isComplex()) {
			%>
			if (!regExp.exec($("input[name=password]").val())) {
				dialogs.alert({
					theme : "alert",
					title : "경고",
					msg : "숫자 2개이상 입력하세요"
				})
				return false;
			}

			if (!regExp2.exec($("input[name=password]").val())) {
				dialogs.alert({
					theme : "alert",
					title : "경고",
					msg : "문자 2개이상 입력하세요"
				})
				return false;
			}

			if (!regExp3.exec($("input[name=password]").val())) {
				dialogs.alert({
					theme : "alert",
					title : "경고",
					msg : "특수문자[!%&'()._:;] 2개이상 입력하세요"
				})
				return false;
			}
			
			<%
				}
			%>

			if ($password.val() == "") {
				dialogs.alert({
					theme : "alert",
					title : "경고",
					msg : "변경할 비밀번호를 입력하세요"
				}, function() {
					if (this.key == "ok" || this.state == "close") {
						$password.focus();
					}
				})
				return false;
			}

			if ($repassword.val() == "") {
				dialogs.alert({
					theme : "alert",
					title : "경고",
					msg : "비밀번호 확인을 입력하세요"
				}, function() {
					if (this.key == "ok" || this.state == "close") {
						$repassword.focus();
					}
				})
				return false;
			}

			if ($password.val() != $repassword.val()) {
				dialogs.alert({
					theme : "alert",
					title : "경고",
					msg : "변경 비밀번호 값이 일치 하지 않습니다."
				}, function() {
					if (this.key == "ok" || this.state == "close") {
						$password.val("");
						$repassword.val("");
						$password.focus();
					}
				})
				return false;
			}

			<%
				if(ps.isLength()) {
			%>
			if ($password.val().length < <%=ps.getPrange()%> || $repassword.val().length < <%=ps.getPrange() %>) {
				dialogs.alert({
					theme : "alert",
					title : "경고",
					msg : "비밀번호는 최소 <%=ps.getPrange()%>자리 이상이어야 합니다.",
					width : 350
				})
				return false;
			}
			
			<%
				}
			%>

			var box = $(document).setNonOpen();
			box.confirm({
				theme : "info",
				title : "확인",
				msg : "비밀번호를 변경 하시겠습니까?"
			}, function() {

				if (this.key == "ok") {
					// 등록 진행
					// 일반 문서 값
					var params = $(document).getFormParams();
					// 관련 부품
					$(document).ajaxCallServer(url, params, function(data) {
						dialogs.alert({
							theme : "alert",
							title : "결과",
							msg : data.msg
						}, function() {
							// 버튼 클릭 ok, esc
							if (this.key == "ok" || this.state == "close") {
								document.location.href = data.url;
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
	
	<!-- create header title -->
	<div class="header_title">
		<i class="axi axi-subtitles"></i><span>비밀번호 변경</span>
		<!-- req msg -->
		<font class="reqMsg">(빨간색 속성 값은 필수 입력 값입니다.)</font>
	</div>


	<!-- create table -->
	<table class="create_table">
		<!-- colgroup -->
		<colgroup>
			<col width="230">
			<col>
		</colgroup>
		<tr>
			<th><font class="req">변경 비밀번호</font></th>
			<td>
				<input type="password" name="password" id="password" class="AXInput wid300">
			</td>
		</tr>
		<tr>
			<th><font class="req">비밀번호 확인</font></th>
			<td>
				<input type="password" name="repassword" id="repassword" class="AXInput wid300">
			</td>	
		</tr>	
	</table>
	
	<table class="btn_table">
		<tr>
			<td class="center">
				<input type="button" value="변경" id="changePasswordBtn" title="변경"> 
				<input type="button" value="뒤로" id="backBtn" title="뒤로" class="blueBtn">
			</td>
		</tr>
	</table>
</td>