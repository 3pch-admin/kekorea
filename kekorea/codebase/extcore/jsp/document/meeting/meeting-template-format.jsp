<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- tinymce -->
<%@include file="/extcore/include/tinymce.jsp"%>
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
		<th class="req">회의록 템플릿 제목</th>
		<td class="indent5">
			<input type="text" name="name" id="name" class="AXInput width-500">
		</td>
	</tr>
	<tr>
		<th class="req">회의록 양식</th>
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
		let params = new Object();
		let content = tinymce.activeEditor.getContent();
		params.name = document.getElementById("name").value;
		params.content = content;
		const url = getCallUrl("/meeting/format");
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
			height : 800,
			statusbar : false,
			language : 'ko_KR',
			plugins : 'anchor autolink charmap codesample emoticons image link lists media searchreplace table visualblocks wordcount',
			toolbar : 'undo redo | blocks fontfamily fontsize | bold italic underline strikethrough | link image media table | addcomment showcomments | spellcheckdialog a11ycheck typography | align lineheight | checklist numlist bullist indent outdent | emoticons charmap | removeformat',
		});
	}

	document.addEventListener("DOMContentLoaded", function() {
		// DOM이 로드된 후 실행할 코드 작성
		loadTinymce();
	});
</script>