<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- tinymce -->
<%@include file="/extcore/jsp/common/tinymce.jsp"%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				회의록 템플릿 등록
			</div>
		</td>
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
		<th class="req lb">회의록 템플릿 제목</th>
		<td class="indent5">
			<input type="text" name="name" id="name" class="width-500">
		</td>
	</tr>
	<tr>
		<th class="req lb">회의록 양식</th>
		<td class="indent5">
			<textarea name="description" id="description" rows="8"></textarea>
		</td>
	</tr>
</table>
<script type="text/javascript">
	function create() {
		const params = new Object();
		const url = getCallUrl("/meeting/format");
		const content = tinymce.activeEditor.getContent();
		const name = document.getElementById("name");
		params.name = name.value;
		params.content = content;
		if (isNull(name.value)) {
			alert("회의록 템플릿 제목은 공백을 입력할 수 없습니다.");
			name.focus();
			return false;
		}
		if (isNull(content)) {
			alert("회의록 양식은 공백을 입력할 수 없습니다.");
			tinymce.activeEditor.focus();
			return false;
		}
		if (!confirm("등록 하시겠습니까?")) {
			return false;
		}
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
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
		document.getElementById("name").focus();
		loadTinymce();
	});
</script>