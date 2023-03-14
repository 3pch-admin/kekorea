<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<HashMap<String, String>> list = (ArrayList<HashMap<String, String>>) request.getAttribute("list");
%>
<table class="button-table">
	<tr>
		<td class="right">
			<input type="button" value="등록" title="등록" onclick="create();">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>

<table class="create-table">
	<colgroup>
		<col width="130">
		<col width="*">
	</colgroup>
	<tr>
		<th class="req lb">템플릿 제목</th>
		<td class="indent5">
			<input type="text" name="name" id="name" class="AXInput width-500">
		</td>
	</tr>
	<tr>
		<th class="lb">참조 템플릿</th>
		<td class="indent5">
			<select name="template" id="template" class="AXSelect">
				<option value="">선택</option>
				<%
				for (HashMap<String, String> map : list) {
				%>
				<option value="<%=map.get("key")%>"><%=map.get("value")%></option>
				<%
				}
				%>
			</select>
		</td>
	</tr>
	<tr>
		<th class="req lb">설명</th>
		<td class="indent5">
			<textarea name="description" id="description" rows="8"></textarea>
		</td>
	</tr>
</table>
<script type="text/javascript">
	// 등록
	function create() {

		if (!confirm("등록 하시겠습니까?")) {
			return false;
		}

		const params = new Object();
		const content = tinymce.activeEditor.getContent();
		const _addRows = AUIGrid.getAddedRowItems(_myGridID); // 프로젝트
		const url = getCallUrl("/meeting/create");
		params.name = document.getElementById("name").value;
		params.content = content;
		params.tiny = document.getElementById("tiny").value;
		params._addRows = _addRows
		params.secondarys = toArray("secondarys");
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			} else {
				// 실패시 처리할 부분..
			}
		})
	}
</script>