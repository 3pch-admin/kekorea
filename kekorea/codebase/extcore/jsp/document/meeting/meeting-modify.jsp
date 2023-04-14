<%@page import="e3ps.doc.meeting.service.MeetingHelper"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.doc.meeting.dto.MeetingDTO"%>
<%@page import="java.util.Map"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<Map<String, String>> list = (ArrayList<Map<String, String>>) request.getAttribute("list");
%>
<%
MeetingDTO dto = (MeetingDTO) request.getAttribute("dto");
String oid = dto.getOid();
JSONArray data = MeetingHelper.manager.jsonArrayAui(oid);
%>
<!-- tinymce -->
<%@include file="/extcore/include/tinymce.jsp"%>
<!-- AUIGrid -->
<%@include file="/extcore/include/auigrid.jsp"%>
<input type="hidden" name="oid" id="oid" value="<%=dto.getLoid()%>">
<input type="hidden" name="poid" id="poid" value="<%=dto.getPoid()%>">
<input type="hidden" name="loid" id="loid" value="<%=dto.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				회의록 수정
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
		<col width="130">
		<col width="700">
		<col width="130">
		<col width="700">
	</colgroup>
	<tr>
		<th class="req lb">회의록 제목</th>
		<td class="indent5">
			<input type="text" name="name" id="name" class="AXInput width-500"value="<%=dto.getName()%>">
		</td>
		<th>회의록 템플릿 선택</th>
		<td class="indent5">
			<select name="tiny" id="tiny" class="width-200">
				<option value="">선택</option>
				<%
				for (Map<String, String> map : list) {
					String value = map.get("oid");
					String name = map.get("name");
					if(dto.getT_name().equals(map.get("name"))){
						%>
						<option value="<%=value%>"  selected="selected"><%=name%></option>
						<%
					}else{
						%>
						<option value="<%=value%>" ><%=name%></option>
						<%
					}
				}
				%>
			</select>
		</td>
	</tr>
	<tr>
		<th class="req lb">KEK 작번</th>

 		<td class="indent5" colspan="3">
 			<jsp:include page="/extcore/include/project-include.jsp">
 				<jsp:param value="<%=dto.getOid()%>" name="oid" />
				<jsp:param value="update" name="mode" />
 				<jsp:param value="true" name="multi" />
 				<jsp:param value="meeting" name="obj" />
 				<jsp:param value="150" name="height" />
 			</jsp:include>
 		</td>
	</tr>
	<tr>
		<th class="req lb">내용</th>
		<td class="indent5" colspan="3">
			<textarea name="description" id="description" rows="8"><%=dto.getContent()%></textarea>
		</td>
	</tr>
	<tr>
		<th class="lb">첨부파일</th>
		<td class="indent5" colspan="3">
			<jsp:include page="/extcore/include/secondary-include.jsp">
				<jsp:param value="<%=dto.getOid()%>" name="oid" />
				<jsp:param value="create" name="mode" />
			</jsp:include>
		</td>
	</tr>
</table>

<script type="text/javascript">

	// 등록
	function modify() {

		const params = new Object();
		const url = getCallUrl("/meeting/meetingModify");
		const oid = document.getElementById("poid").value;
		const content = tinymce.activeEditor.getContent();
		const _addRows = AUIGrid.getAddedRowItems(_myGridID);
		const _removeRows = AUIGrid.getRemoveRowItems(_myGridID);
		params.name = document.getElementById("name").value;
		params.content = content;
		params.tiny = document.getElementById("tiny").value;
		params._addRows = _addRows;
		params._removeRows = _removeRows;
		params.secondarys = toArray("secondarys");
		
		if (isNull(params.name)) {
			alert("회의록 제목은 공백을 입력할 수 없습니다.");
			document.getElementById("name").focus();
			return false;
		}
		if (_addRows.length === 0) {
			alert("KEK 작번은 공백을 입력할 수 없습니다.");
			return false;
		}
		_addRows.sort(function(a, b) {
			return a.sort - b.sort;
		});
		if (isNull(params.content)) {
			alert("내용은 공백을 입력할 수 없습니다.");
			tinymce.activeEditor.focus();
			return fasle;
		}
		if (!confirm("등록 하시겠습니까?")) {
			return false;
		}
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			console.log(data);
			if (data.result) {
				opener.loadGridData();
				self.close();
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
		loadTinymce();
		const tinyBox = document.getElementById("tiny");
		$('#tiny').change(function() {
			const value = tinyBox.value;
 			const url = getCallUrl("/meeting/getContent?oid=" + value);
			call(url, null, function(data) {
				if (data.result) {
					tinymce.activeEditor.setContent(data.content);
				} else {
					alert(data.msg);
				}
			}, "GET");
		});
		_createAUIGrid(_columns);
		AUIGrid.resize(_myGridID);
		selectbox("tiny");
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(_myGridID);
	});
</script>