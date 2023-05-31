<%@page import="java.util.Map"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				공지사항 등록
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
		<th class="req lb">공지사항 제목</th>
		<td class="indent5">
			<input type="text" name="name" id="name" class="width-500" tabindex="1">
		</td>
	</tr>
	<tr>
		<th class="req lb">내용</th>
		<td class="indent5">
			<textarea name="description" id="description" rows="10" tabindex="2"></textarea>
		</td>
	</tr>
	<tr>
		<th class="lb">첨부파일</th>
		<td class="indent5">
			<jsp:include page="/extcore/jsp/common/attach-primary.jsp">
				<jsp:param value="" name="oid" />
			</jsp:include>
		</td>
	</tr>
</table>
<script type="text/javascript">
	function create() {
		const params = new Object();
		const url = getCallUrl("/notice/create");
		const name = document.getElementById("name");
		const description = document.getElementById("description");
		params.name = name.value;
		params.description = description.value;
		params.primarys = toArray("primarys");
		if (isNull(params.name)) {
			alert("공지사항 제목 값은 공백을 입력 할 수 없습니다.");
			name.focus();
			return false;
		}
		if (isNull(params.description)) {
			alert("내용 값은 공백을 입력 할 수 없습니다.");
			description.focus();
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
			} else {
				closeLayer();
			}
		})
	}

	document.addEventListener("DOMContentLoaded", function() {
		toFocus("name");
	})
</script>