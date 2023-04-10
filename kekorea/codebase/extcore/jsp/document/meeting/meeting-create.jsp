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
			<input type="text" name="name" id="name" class="AXInput width-500">
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
			<div class="include">
				<input type="button" value="작번 추가" title="작번 추가" class="blue" onclick="_insert();">
				<input type="button" value="작번 삭제" title="작번 삭제" class="red" onclick="_deleteRow();">
				<div id="_grid_wrap" style="height: 150px; border-top: 1px solid #3180c3; margin: 5px;"></div>
				<script type="text/javascript">
					let _myGridID;
					const _columns = [ {
						dataField : "projectType_name",
						headerText : "작번유형",
						dataType : "string",
						width : 80,
						filter : {
							showIcon : true,
							inline : true
						},
					}, {
						dataField : "customer_name",
						headerText : "거래처",
						dataType : "string",
						width : 120,
						filter : {
							showIcon : true,
							inline : true
						},
					}, {
						dataField : "mak_name",
						headerText : "막종",
						dataType : "string",
						width : 120,
						filter : {
							showIcon : true,
							inline : true
						},
					}, {
						dataField : "detail_name",
						headerText : "막종상세",
						dataType : "string",
						width : 120,
						filter : {
							showIcon : true,
							inline : true
						},
					}, {
						dataField : "kekNumber",
						headerText : "KEK 작번",
						dataType : "string",
						width : 100,
						filter : {
							showIcon : true,
							inline : true
						},
					}, {
						dataField : "keNumber",
						headerText : "KE 작번",
						dataType : "string",
						width : 100,
						filter : {
							showIcon : true,
							inline : true
						},
					}, {
						dataField : "description",
						headerText : "작업 내용",
						dataType : "string",
						style : "aui-left",
						filter : {
							showIcon : true,
							inline : true
						},
					}, {
						dataField : "oid",
						headerText : "",
						visible : false
					} ]
					function _createAUIGrid(columnLayout) {
						const props = {
							headerHeight : 30,
							showRowNumColumn : true,
							showRowCheckColumn : true,
							showStateColumn : true,
							rowNumHeaderText : "번호",
							showAutoNoDataMessage : false,
							selectionMode : "singleRow",
							enableSorting : false
						}
						_myGridID = AUIGrid.create("#_grid_wrap", columnLayout, props);
					}

					function _insert() {
						const url = getCallUrl("/project/popup?method=append&multi=true");
						popup(url, 1500, 700);
					}

					function append(data, callBack) {
						for (let i = 0; i < data.length; i++) {
							const item = data[i].item;
							const isUnique = AUIGrid.isUniqueValue(_myGridID, "oid", item.oid);
							if (isUnique) {
								AUIGrid.addRow(_myGridID, item, "first");
							}
						}
						callBack(true);
					}

					function _deleteRow() {
						const checked = AUIGrid.getCheckedRowItems(_myGridID);
						if (checked.length === 0) {
							alert("삭제할 행을 선택하세요.");
							return false;
						}

						for (let i = checked.length - 1; i >= 0; i--) {
							const rowIndex = checked[i].rowIndex;
							AUIGrid.removeRow(_myGridID, rowIndex);
						}
					}
				</script>
			</div>
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

		const params = new Object();
		const url = getCallUrl("/meeting/create");
		const content = tinymce.activeEditor.getContent();
		const _addRows = AUIGrid.getAddedRowItems(_myGridID);
		params.name = document.getElementById("name").value;
		params.content = content;
		params.tiny = document.getElementById("tiny").value;
		params._addRows = _addRows;
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
		// 		$("#tiny").change({
		// 			onchange : function(){
		// 				const value = tinyBox.value;
		// 				$("#description").val({
		// 					ajaxUrl : getCallUrl("/meeting/getContents?oid="+value),
		// 					reversKeys : {
		// 						options : "content",
		// 						optionValue : "value",
		// 						optionText : "name"
		// 					},
		// 					setValue : this.optionValue,
		// 					alwaysOnChange : true,
		// 				})
		// 			}
		// 		});
		$('#tiny').change(function() {
			const tinyBox = document.getElementById("tiny");
			const value = tinyBox.value;
 			const url = getCallUrl("/meeting/getContent?oid=" + value);
			console.log(value + "@@@@@@@@@@@@");
			call(url, null, function(data) {
				console.log("!!!!!!!!!!!!!!!!!!" + typeof data);
				if (data.result) {
					tinymce.activeEditor.setContent(data.content);
				} else {
					alert(data.msg);
				}
			}, "GET");
		});
		// 		tinyBox.addEventListener("change", function() {
		// 			const value = tinyBox.value; console.log("$$$$$$$$$$$"+value);
		// 			const url = getCallUrl("/meeting/getContent?oid=" + value);
		// 			call(url, null, function(data) {console.log("!!!!!!!!!!!!!!!!!!"+typeof data);
		// 				if (data.result) {
		// 					tinymce.activeEditor.setContent(data.content);
		// 				} else {
		// 					alert(data.msg);
		// 				}
		// 			}, "GET");
		// 		})
		_createAUIGrid(_columns);
		AUIGrid.resize(_myGridID);
		selectbox("tiny");
	});

	// 	$('#tiny').change(function(){
	// 		const tinyBox = document.getElementById("tiny");
	// 		const value = tinyBox.value;console.log(value+"@@@@@@@@@@@@")
	// 		$.ajax({
	// 			url: '/meeting/getContents?oid='+value,
	// 			type: 'POST',
	// 			dataType: 'json',
	// 			success: function(data){
	// 				console.log("################"+data);
	// 			},error:function(request,status,error){
	// 				alert("code : "+request.status+"\n"+"message : "+request.responseText+"\n"+"error : "+error);
	// 			}
	// 		})
	// 	});
	// 	const tinyBox = document.getElementById("tiny");
	// 	tinyBox.addEventListener("change", function() {
	// 		const value = tinyBox.value; console.log(value);
	// 		const url = getCallUrl("/meeting/getContent?oid=" + value);
	// 		call(url, null, function(data) {console.log(data);
	// 			if (data.result) {
	// 				tinymce.activeEditor.setContent(data.content);
	// 			} else {
	// 				alert(data.msg);
	// 			}
	// 		}, "GET");
	// 	})

	// 	원본 코드
	// 	document.addEventListener("DOMContentLoaded", function() {
	// 		loadTinymce();
	// 		const tinyBox = document.getElementById("tiny");
	// 		tinyBox.addEventListener("change", function() {
	// 			const value = tinyBox.value; console.log(value);
	// 			const url = getCallUrl("/meeting/getContent?oid=" + value);
	// 			call(url, null, function(data) {console.log(data);
	// 				if (data.result) {
	// 					tinymce.activeEditor.setContent(data.content);
	// 				} else {
	// 					alert(data.msg);
	// 				}
	// 			}, "GET");
	// 		})
	// 		_createAUIGrid(_columns);
	// 		AUIGrid.resize(_myGridID);
	// 		selectbox("tiny");
	// 	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(_myGridID);
	});
</script>