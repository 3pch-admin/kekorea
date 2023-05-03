<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="java.util.Map"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<Map<String, String>> list = (ArrayList<Map<String, String>>) request.getAttribute("list");
String toid = (String) request.getAttribute("toid");
String poid = (String) request.getAttribute("poid");
String location = (String) request.getAttribute("location");
JSONArray data = (JSONArray) request.getAttribute("data");
%>
<%@include file="/extcore/jsp/common/tinymce.jsp"%>
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>    
<input type="hidden" name="toid" id="toid" value="<%=toid%>">
<input type="hidden" name="poid" id="poid" value="<%=poid%>">
<input type="hidden" name="location" id="location" value="<%=location%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				회의록 등록
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
		<col width="700">
		<col width="130">
		<col width="700">
	</colgroup>
	<tr>
		<th class="req lb">회의록 제목</th>
		<td class="indent5">
			<input type="text" name="name" id="name" class="width-500">
		</td>
		<th>회의록 템플릿 선택</th>
		<td class="indent5">
			<select name="tiny" id="tiny" class="width-200">
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
		<th class="req lb">KEK 작번</th>
		<td colspan="3">
			<jsp:include page="/extcore/jsp/common/project-include.jsp">
				<jsp:param value="" name="oid" />
				<jsp:param value="create" name="mode" />
			</jsp:include>
		</td>
	</tr>
	<tr>
		<th class="req lb">내용</th>
		<td class="indent5" colspan="3">
			<textarea name="description" id="description" rows="8"></textarea>
		</td>
	</tr>
	<tr>
		<th class="lb">첨부파일</th>
		<td class="indent5" colspan="3">
			<jsp:include page="/extcore/jsp/common/attach-secondary.jsp">
				<jsp:param value="" name="oid" />
				<jsp:param value="create" name="mode" />
			</jsp:include>
		</td>
	</tr>
</table>
<script type="text/javascript">
	// 등록
	function create() {
		const params = new Object();
		const url = getCallUrl("/meeting/create");
		const content = tinymce.activeEditor.getContent();
		const addRows9 = AUIGrid.getAddedRowItems(myGridID9);
		const name = document.getElementById("name");
		params.name = name.value;
		params.content = content;
		params.tiny = document.getElementById("tiny").value;
		params.addRows9 = addRows9;
		params.secondarys = toArray("secondarys");

		if (isNull(params.name)) {
			alert("회의록 제목은 공백을 입력할 수 없습니다.");
			name.focus();
			return false;
		}
		
		if (addRows9.length === 0) {
			alert("최소 하나이상의 작번을 추가하세요.");
			insert9();
			return false;
		}

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
			if (data.result) {
				<%
					if(!StringUtils.isNull(poid)) {
				%>
				opener._reload();
				<%
					} else {
				%>
				opener.loadGridData();
				<%
					}
				%>
				self.close();
			} else {
				closeLayer();
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
	
	function auiBeforeRemoveRow(event) {
		const item = event.items[0];
		const oid = document.getElementById("poid").value;
		if (item.oid === oid) {
			alert("기준 작번은 제거 할 수 없습니다.");
			return false;
		}
		return true;
	}

	document.addEventListener("DOMContentLoaded", function() {
		document.getElementById("name").focus();
		loadTinymce();
		const tinyBox = document.getElementById("tiny");
		$('#tiny').change(function() {
			const value = tinyBox.value;
			const url = getCallUrl("/meeting/getContent?oid=" + value);
			openLayer();
			call(url, null, function(data) {
				if (data.result) {
					tinymce.activeEditor.setContent(data.content);
					closeLayer();
				} else {
					alert(data.msg);
				}
			}, "GET");
		});
		createAUIGrid9(columns9);
		AUIGrid.resize(myGridID9);
		<%
			if(data != null) {
		%>
		AUIGrid.bind(myGridID9, "beforeRemoveRow", auiBeforeRemoveRow);
		AUIGrid.addRow(myGridID9, <%=data%>);
		<%
			}
		%>
		selectbox("tiny");
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID9);
	});
</script>