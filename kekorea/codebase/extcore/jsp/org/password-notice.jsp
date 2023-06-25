<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.org.dto.UserDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	int gap = (int) request.getAttribute("gap");
	UserDTO dto = (UserDTO) request.getAttribute("dto");
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				비밀번호 변경
			</div>
		</td>
		<td class="right">
			<input type="button" value="수정" title="수정" onclick="modify();">
			<input type="button" value="닫기" title="닫기" class="red" onclick="self.close();">
		</td>
	</tr>
</table>

<table class="create-table">
	<colgroup>
		<col width="150">
		<col width="*">
	</colgroup>
	<tr>
		<th class="lb req">비밀번호</th>
		<td class="indent5">
			<input type="password" name="password" id="password" class="width-200">
		</td>
	</tr>
	<tr>
		<th class="lb req">비밀번호 확인</th>
		<td class="indent5">
			<input type="password" name="cpassword" id="cpassword" class="width-200">
		</td>
	</tr>
</table>
<br>
<b><font color="red">비밀번호 변경 기간 초과</font></b>
<br>
<b>최종 비밀번호 변경일 : <font color="red"><%=dto.getLast_txt() %></font></b>
<br>
<b>최종 비밀번호 변경일로 부터 지난 기간 : <font color="red"><%=gap %>일</font></b>
<br>
<b>비밀번호 변경 기간 설정 값 :  <font color="red"><%=dto.getGap() %>일</font></b>
<br>
<b>시스템 사용을 위해선 비밀번호를 변경 해주시기 바랍니다.</b>

<script type="text/javascript">
	function modify() {
		const password = document.getElementById("password");
		const cpassword = document.getElementById("cpassword");

		if (isNull(password.value)) {
			alert("변경할 비밀번호를 입력하세요.");
			password.focus();
			return false;
		}

		if (!isNull(password.value)) {

			if (isNull(cpassword.value)) {
				alert("비밀번호 확인을 입력하세요.");
				cpassword.focus();
				return false;
			}

			if (password.value !== cpassword.value) {
				alert("입력한 비밀번호와 비밀번호 확인 값이 일치하지 않습니다.");
				password.value = "";
				cpassword.value = "";
				password.focus();
				return false;
			}
		}

		if (!confirm("수정 하시겠습니까?")) {
			return false;
		}
		const params = new Object();
		const url = getCallUrl("/org/password");
		params.password = password.value;
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.logout();
				self.close();
			} else {
				closeLayer();
			}
		})

	}

	document.addEventListener("DOMContentLoaded", function() {
		toFocus("password");
	})
</script>