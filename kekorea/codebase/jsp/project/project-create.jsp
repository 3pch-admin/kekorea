<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<CommonCode> customers = (ArrayList<CommonCode>) request.getAttribute("customers");
ArrayList<CommonCode> projectTypes = (ArrayList<CommonCode>) request.getAttribute("projectTypes");
ArrayList<CommonCode> installs = (ArrayList<CommonCode>) request.getAttribute("installs");
%>
<table class="btn_table">
	<tr>
		<td>
			<div class="header_title">
				<i class="axi axi-subtitles"></i>
				<span>작번 등록</span>
			</div>
		</td>
		<td class="right">
			<input type="button" value="등록" id="createBtn" title="등록">
			<input type="button" value="닫기ㅊ" id="closeBtn" title="닫기" class="blueBtn">
		</td>
	</tr>
</table>

<table class="create_table">
	<tr>
		<th>
			<font class="req">KEK 작번</font>
		</th>
		<td>
			<input type="text" name="kekNumber" id="kekNumber" class="AXInput wid300" autofocus="autofocus">
		</td>
		<th class="min-wid200">
			<font class="req">작번 발행일</font>
		</th>
		<td>
			<input type="text" name="pDate" id="pDate" class="AXInput">
		</td>
	</tr>
	<tr>
		<th>
			<font class="req">KE 작번</font>
		</th>
		<td>
			<input type="text" name="keNumber" id="keNumber" class="AXInput wid300">
		</td>
		<th>
			<font class="req">요구납기일</font>
		</th>
		<td>
			<input type="text" name="customDate" id="customDate" class="AXInput">
		</td>
	</tr>
	<tr>
		<th>
			<font class="req">USER ID</font>
		</th>
		<td>
			<input type="text" name="userId" id="userId" class="AXInput wid300">
		</td>
		<th>
			<font class="req">거래처</font>
		</th>
		<td>
			<select name="customer" id="customer" class="AXSelect wid200">
				<option value="">선택</option>
				<%
				for (CommonCode customer : customers) {
				%>
				<option value="<%=customer.getName()%>"><%=customer.getName()%></option>
				<%
				}
				%>
			</select>
		</td>
	</tr>
	<tr>
		<th>
			<font class="req">막종</font>
		</th>
		<td>
			<input type="text" name="mak" id="mak" class="AXInput wid300">
		</td>
		<th>
			<font class="req">설치 장소</font>
		</th>
		<td>
			<select name="install" id="install" class="AXSelect wid200">
				<option value="">선택</option>
				<%
				for (CommonCode install : installs) {
				%>
				<option value="<%=install.getName()%>"><%=install.getName()%></option>
				<%
				}
				%>
			</select>
		</td>
	</tr>
	<tr>
		<th>
			<font class="req">모델</font>
		</th>
		<td>
			<input type="text" name="model" id="model" class="AXInput wid300">
		</td>
		<th>
			<font class="req">작번 유형</font>
		</th>
		<td>
			<select name="projectType" id="projectType" class="AXSelect wid100">
				<option value="">선택</option>
				<%
				for (CommonCode projectType : projectTypes) {
				%>
				<option value="<%=projectType.getName()%>"><%=projectType.getName()%></option>
				<%
				}
				%>
			</select>
		</td>
	</tr>
	<tr>
		<th>작번 템플릿</th>
		<td colspan="3">
			<select name="pTemplate" id="pTemplate" class="AXSelect wid300">
				<option value="">선택</option>
			</select>
		</td>
	</tr>
	<tr>
		<th>
			<font class="req">작업 내용</font>
		</th>
		<td colspan="3">
			<textarea class="description" name="description" id="description"></textarea>
		</td>
	</tr>
</table>
<script type="text/javascript">
	$(function() {

		// select box bind
		selectBox("customer")
		selectBox("install")
		selectBox("projectType")

		$("#closeBtn").click(function() {
			self.close();
		})

		date("pDate");
		date("customDate");

		$("#createBtn").click(function() {
			let url = getCallUrl("/project/create");
			let params = new Object();
			params = form(params, "create_table");
			open();
			call(url, params, function(data) {
				alert(data.msg);
				if (data.result) {
					opener.loadGridData();
					self.close();
				} else {
					close();
				}
			}, "POST");
		})
	})
</script>