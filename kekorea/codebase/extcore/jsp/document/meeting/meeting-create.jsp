<%@page import="java.util.Map"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<Map<String, String>> list = (ArrayList<Map<String, String>>) request.getAttribute("list");
%>
<!-- tinymce -->
<%@include file="/extcore/include/tinymce.jsp"%>
<!-- AUIGrid -->
<%@include file="/extcore/include/auigrid.jsp"%>
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
		<col width="700">
		<col width="130">
		<col width="700">
	</colgroup>
	<tr>
		<th class="req">회의록 제목</th>
		<td class="indent5">
			<input type="text" name="name" id="name" class="AXInput width-500">
		</td>
		<th>회의록 템플릿 선택</th>
		<td class="indent5">
			<select name="tiny" id="tiny" class="width-300">
				<option value="">선택</option>
				<%
				for (Map<String, String> map : list) {
					String value = map.get("oid");
					String name = map.get("name");
				%>
				<option value="<%=value%>"><%=name%></option>
				<%
				}
				%>
			</select>
		</td>
	</tr>
	<tr>
		<th class="req">KEK 작번</th>
		<td colspan="3">
			<jsp:include page="/extcore/include/project-include.jsp">
				<jsp:param value="" name="oid" />
				<jsp:param value="create" name="mode" />
				<jsp:param value="true" name="multi" />
				<jsp:param value="" name="obj" />
				<jsp:param value="250" name="height" />
			</jsp:include>
		</td>
	</tr>
	<tr>
		<th class="req">내용</th>
		<td class="indent5" colspan="3">
			<textarea name="description" id="description" rows="8"></textarea>
		</td>
	</tr>
	<tr>
		<th>첨부파일</th>
		<td class="indent5" colspan="3">
			<jsp:include page="/extcore/include/secondary-include.jsp">
				<jsp:param value="" name="oid" />
				<jsp:param value="create" name="mode" />
			</jsp:include>
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

	function loadTinymce() {
		tinymce.init({
			selector : 'textarea',
			height : 500,
			statusbar : false,
			language : 'ko_KR',
			plugins : 'anchor autolink charmap codesample emoticons image link lists media searchreplace table visualblocks wordcount',
			toolbar : 'undo redo | blocks fontfamily fontsize | bold italic underline strikethrough | link image media table | addcomment showcomments | spellcheckdialog a11ycheck typography | align lineheight | checklist numlist bullist indent outdent | emoticons charmap | removeformat',
		});
	}

	document.addEventListener("DOMContentLoaded", function() {
		// DOM이 로드된 후 실행할 코드 작성
		loadTinymce();
		const tinyBox = document.getElementById("tiny");
		tinyBox.addEventListener("change", function() {
			const value = tinyBox.value;
			const url = getCallUrl("/meeting/getContent?oid=" + value);
			call(url, null, function(data) {
				if (data.result) {
					tinymce.activeEditor.setContent(data.content);
				} else {
					alert(data.msg);
				}
			}, "GET");
		})
		// 작번 추가 그리드
		_createAUIGrid(_columns);
		AUIGrid.resize(_myGridID);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(_myGridID);
	});
</script>