<%@page import="java.util.Map"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js?v=1010"></script>
</head>
<body>
	<form>
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						도면결재 등록
					</div>
				</td>
				<td class="right">
					<input type="button" value="결재등록" title="결재등록" onclick="registerLine();">
				</td>
			</tr>
		</table>
		<table class="create-table">
			<colgroup>
				<col width="130">
				<col width="*">
			</colgroup>
			<tr>
				<th class="req lb">결재 제목</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-700">
				</td>
			</tr>
			<tr>
				<th class="req lb">결재 의견</th>
				<td class="indent5">
					<textarea id="description" name="description" rows="5"></textarea>
				</td>
			</tr>
			<tr>
				<th class="req lb">결재 도면</th>
				<td>
					<div class="include">
						<input type="button" value="도면 추가" title="도면 추가" class="blue" onclick="insert();">
						<input type="button" value="도면 삭제" title="도면 삭제" class="red" onclick="deleteRow();">
						<div id="grid_wrap" style="height: 300px; border-top: 1px solid #3180c3; margin: 3px 5px 3px 5px;"></div>
						<script type="text/javascript">
							let myGridID;
							const columns = [ {
								dataField : "name",
								headerText : "NAME",
								dataType : "string",
							}, {
								dataField : "dwg_no",
								headerText : "DWG NO",
								dataType : "string",
								width : 250
							}, {
								dataField : "name_of_parts",
								headerText : "NAME_OF_PARTS",
								dataType : "string",
								width : 250
							}, {
								dataField : "version",
								headerText : "버전",
								dataType : "string",
								width : 100,
							}, {
								dataField : "state",
								headerText : "상태",
								dataType : "string",
								width : 100
							}, {
								dataField : "creator",
								headerText : "작성자",
								dataType : "date",
								width : 100
							}, {
								dataField : "createdDate_txt",
								headerText : "작성일",
								dataType : "string",
								width : 100
							}, {
								dataField : "oid",
								visible : false,
								dataType : "string"
							} ]

							function createAUIGrid(columnLayout) {
								const props = {
									headerHeight : 30,
									showRowNumColumn : true,
									rowNumHeaderText : "번호",
									showStateColumn : true,
									showRowCheckColumn : true,
									enableSorting : false
								}
								myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
							}

							function insert() {
								const url = getCallUrl("/epm/popup?method=append&multi=false");
								popup(url, 1600, 700);
							}

							function append(data, callBack) {
								for (let i = 0; i < data.length; i++) {
									const item = data[i].item;
									const isUnique = AUIGrid.isUniqueValue(myGridID, "oid", item.oid);
									if (isUnique) {
										AUIGrid.addRow(myGridID, item, "first");
									}
								}
								callBack(true);
							}

							// 행 삭제
							function deleteRow() {
								const checked = AUIGrid.getCheckedRowItems(myGridID);
								for (let i = checked.length - 1; i >= 0; i--) {
									const rowIndex = checked[i].rowIndex;
									AUIGrid.removeRow(myGridID, rowIndex);
								}
							}
						</script>
					</div>
				</td>
			</tr>
			<tr>
				<th class="req lb">결재</th>
				<td>
					<jsp:include page="/extcore/jsp/common/approval-register.jsp">
						<jsp:param value="" name="oid" />
						<jsp:param value="create" name="mode" />
					</jsp:include>
				</td>
			</tr>
		</table>
		<script type="text/javascript">
			function registerLine() {
				const url = getCallUrl("/epm/register");
				const params = new Object();
				const addRows = AUIGrid.getAddedRowItems(myGridID); // 문서
				const addRows8 = AUIGrid.getAddedRowItems(myGridID8); // 결재
				const name = document.getElementById("name");
				const description = document.getElementById("description").value;

				if (isNull(name.value)) {
					alert("결재 제목을 입력하세요.");
					name.focus();
					return false;
				}

				if (addRows8.length === 0) {
					alert("결재선을 지정하세요.");
					_register();
					return false;
				}

				if (addRows.length === 0) {
					alert("결재할 도면을 추가하세요.");
					insert();
					return false;
				}

				if (!confirm("도면결재를 등록하시겠습니까?")) {
					return false;
				}
				params.name = name.value;
				params.description = description;
				params.addRows = addRows;
				toRegister(params, addRows8);
				parent.openLayer();
				call(url, params, function(data) {
					alert(data.msg);
					if (data.result) {
						document.location.href = getCallUrl("/workspace/approval");
					} else {
						parent.closeLayer();
					}
				})
			}

			document.addEventListener("DOMContentLoaded", function() {
				document.getElementById("name").focus();
				createAUIGrid(columns);
				createAUIGrid8(columns8);
				AUIGrid.resize(myGridID);
				AUIGrid.resize(myGridID8);
			});

			window.addEventListener("resize", function() {
				AUIGrid.bind(myGridID);
				AUIGrid.resize(myGridID8);
			});
		</script>
	</form>
</body>
</html>