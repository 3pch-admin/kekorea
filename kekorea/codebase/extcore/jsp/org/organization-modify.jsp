<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.org.dto.UserDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
UserDTO dto = (UserDTO) request.getAttribute("dto");
String[] dutys = (String[]) request.getAttribute("dutys");
ArrayList<HashMap<String, String>> list = (ArrayList<HashMap<String, String>>) request.getAttribute("list");
String duty = dto.getDuty() != null ? dto.getDuty() : "";
String email = dto.getEmail() != null ? dto.getEmail().substring(0, dto.getEmail().lastIndexOf("@")) : "";
%>
<input type="hidden" name="woid" id="woid" value="<%=dto.getWoid()%>">
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				사용자 정보 수정
			</div>
		</td>
		<td class="right">
			<input type="button" value="수정" title="수정" onclick="modify();">
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
		<table class="create-table">
			<colgroup>
				<col width="100">
				<col width="400">
				<col width="100">
				<col width="400">
			</colgroup>
			<tr>
				<th class="lb">이름</th>
				<td class="indent5"><input type="text" name="name" id="name" value="<%=dto.getName()%>"></td>
				<th>아이디</th>
				<td class="indent5"><%=dto.getId()%></td>
			</tr>
			<tr>
				<th class="lb">부서</th>
				<td class="indent5">
					<select name="department" id="department" class="width-200">
						<%
						for (HashMap<String, String> map : list) {
							String o = map.get("oid");
						%>
						<option value="<%=o%>" <%if (o.equals(dto.getDepartment_oid())) {%> selected="selected" <%}%>><%=map.get("name")%></option>
						<%
						}
						%>
					</select>
				</td>
				<th>직급</th>
				<td class="indent5">
					<select name="duty" id="duty" class="width-100">
						<option value="" <%if ("".equals(duty)) {%> selected="selected" <%}%>>지정안됨</option>
						<option value="사장" <%if ("사장".equals(duty)) {%> selected="selected" <%}%>>사장</option>
						<option value="부사장" <%if ("부사장".equals(duty)) {%> selected="selected" <%}%>>부사장</option>
						<option value="TL" <%if ("TL".equals(duty)) {%> selected="selected" <%}%>>TL</option>
						<option value="PL" <%if ("PL".equals(duty)) {%> selected="selected" <%}%>>PL</option>
					</select>
				</td>
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
				<td class="indent5" colspan="3">
					<input type="text" name="email" id="email" value="<%=email%>">
					@kokusai-electric.com
				</td>
			</tr>
		</table>
	</div>
</div>
<script type="text/javascript">
	function modify() {
		const oid = document.getElementById("oid").value;
		const name = document.getElementById("name").value;
		const woid = document.getElementById("woid").value;
		const department = document.getElementById("department").value;
		const duty = document.getElementById("duty").value;
		const email = document.getElementById("email").value;
		if (!confirm("수정 하시겠습니까?")) {
			return false;
		}
		const params = new Object();
		const url = getCallUrl("/org/modify");
		params.oid = oid;
		params.name = name;
		params.woid = woid;
		params.department_oid = department;
		params.duty = duty;
		params.email = email;
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			} else {
				closeLayer();
			}
		})

	}

	document.addEventListener("DOMContentLoaded", function() {
		toFocus("name");
		$("#tabs").tabs({
			active : 0
		});
		selectbox("duty");
		selectbox("department");
	})
</script>