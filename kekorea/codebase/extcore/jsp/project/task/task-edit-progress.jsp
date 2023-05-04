<%@page import="e3ps.project.task.Task"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = (String) request.getAttribute("oid");
Task task = (Task) request.getAttribute("task");
%>
<input type="hidden" name="oid" id="oid" value="<%=oid%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				태스크 진행율 수정
			</div>
		</td>
		<td class="right">
			<input type="button" value="수정" title="수정" onclick="modify();">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>

<table class="create-table">
	<colgroup>
		<col width="150">
		<col width="*">
	</colgroup>
	<tr>
		<th class="lb">진행율</th>
		<td class="indent5">
			<input type="text" name="progress" id="progress" value="<%=task.getProgress() != null ? task.getProgress() : 0%>">
		</td>
	</tr>
</table>
<script type="text/javascript">
	function modify() {
		if (!confirm("진행율을 수정 하시겠습니까?")) {
			return false;
		}
		const oid = document.getElementById("oid").value;
		const progress = document.getElementById("progress").value;
		const url = getCallUrl("/task/editProgress");
		const params = new Object();
		params.oid = oid;
		params.progress = Number(progress);
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener._reload();
				self.close();
			} else {
				closeLayer();
			}
		})
	}

	document.addEventListener("DOMContentLoaded", function() {
		const field = document.getElementById("progress");
		field.addEventListener("input", function(event) {
			const value = event.target.value;
			if (value.slice(0, 1) === "0") {
				alert("첫째 자리의 값은 0을 입력 할 수 없습니다.");
				event.target.value = "";
			}

			if (value.length > 3 || !/^[1-9][0-9]{0,2}$|^0$/.test(value)) {
				event.target.value = value.replace(/[^\d]/g, '').slice(0, 3);
			}

			if (value > 100) {
				event.target.value = 100;
			}
		})
	})
</script>