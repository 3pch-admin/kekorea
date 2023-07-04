<%@page import="e3ps.org.dto.UserDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
UserDTO dto = (UserDTO) request.getAttribute("dto");
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
boolean isSupervisor = (boolean) request.getAttribute("isSupervisor");
boolean isSessionUser = (boolean) request.getAttribute("isSessionUser");
%>
<input type="hidden" name="isAdmin" id="isAdmin" value="<%=isAdmin%>">
<input type="hidden" name="woid" id="woid" value="<%=dto.getWoid()%>">
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				사용자 정보
			</div>
		</td>
		<td class="right">
			<%
			if (isSupervisor || isSessionUser) {
			%>
			<input type="button" value="수정" title="수정" onclick="modify();">
			<%
			}
			%>
			<%
			if (isSupervisor && !dto.isResign()) {
			%>
			<input type="button" value="퇴사처리" title="퇴사처리" class="red" onclick="fire('true', '퇴사');">
			<%
			}
			if (isSupervisor && dto.isResign()) {
			%>
			<input type="button" value="복직처리" title="복직처리" class="red" onclick="fire('false', '복직');">
			<%
			}
			%>
			<input type="button" value="닫기" title="닫기" class="red" onclick="self.close();">
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
				<col width="100">
				<col width="400">
				<col width="100">
				<col width="400">
			</colgroup>
			<tr>
				<th class="lb">이름</th>
				<td class="indent5"><%=dto.getName()%></td>
				<th>아이디</th>
				<td class="indent5"><%=dto.getId()%></td>
			</tr>
			<tr>
				<th class="lb">부서</th>
				<td class="indent5"><%=dto.getDepartment_name()%></td>
				<th>직급</th>
				<td class="indent5"><%=dto.getDuty() != null ? dto.getDuty() : "지정안됨"%></td>
			</tr>
			<tr>
				<th class="lb">비밀번호 최종 변경일</th>
				<td class="indent5" colspan="3"><%=dto.getLast_txt()%></td>
			</tr>
			<tr>
				<th class="lb">재직여부</th>
				<td class="indent5" colspan="3"><%=dto.isResign() == false ? "재직" : "퇴사"%></td>
			</tr>
			<tr>
				<th class="lb">이메일</th>
				<td class="indent5" colspan="3"><%=dto.getEmail() != null ? dto.getEmail() : ""%></td>
			</tr>
		</table>
	</div>
</div>
<script type="text/javascript">
	function modify() {
		const oid = document.getElementById("woid").value;
		const url = getCallUrl("/org/modify?oid=" + oid);
		openLayer();
		document.location.href = url;
	}

	function fire(fire, msg) {
		if (!confirm(msg + "처리 하시겠습니까?")) {
			return false;
		}
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/org/fire?oid=" + oid + "&fire=" + fire);
		openLayer();
		call(url, null, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			} else {
				closeLayer();
			}
		}, "GET");
	}

	document.addEventListener("DOMContentLoaded", function() {
		$("#tabs").tabs({
			active : 0
		});
	})
</script>