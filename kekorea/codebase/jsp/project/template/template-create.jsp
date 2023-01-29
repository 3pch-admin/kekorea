<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<table class="btn_table">
	<tr>
		<td>
			<div class="header_title">
				<i class="axi axi-subtitles"></i>
				<span>템플릿 등록</span>
			</div>
		</td>
		<td class="right">
			<input type="button" value="등록" id="createBtn" title="등록">
			<input type="button" value="닫기" id="closeBtn" title="닫기" class="blueBtn">
		</td>
	</tr>
</table>

<table class="create_table">
	<tr>
		<th>
			<font class="req">템플릿 명</font>
		</th>
		<td>
			<input type="text" name="name" id="name" class="AXInput wid300" autofocus="autofocus">
		</td>
	</tr>
	<tr>
		<th>
			<font class="req">사용여부</font>
		</th>
		<td>
			<input type="checkbox" name="enable" id="enable" checked="checked">
		</td>
	</tr>
	<tr>
		<th>작번 템플릿</th>
		<td colspan="3">
			<select name="temp" id="temp" class="AXSelect wid300">
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

		checkBox("enable");
		selectBox("temp");

		$("#closeBtn").click(function() {
			self.close();
		})

		$("#createBtn").click(function() {
			let url = getCallUrl("/template/create");
			let params = new Object();
			params = form(params);
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
</script>m