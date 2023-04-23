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
					<input type="text" name="name" id="name" class="AXInput width-700">
				</td>
			</tr>
			<tr>
				<th class="req lb">결재 도면</th>
				<td>
					<div class="include">
						<input type="button" value="도면 추가" title="도면 추가" class="blue" onclick="_insert();">
						<input type="button" value="도면 삭제" title="도면 삭제" class="red" onclick="_deleteRow();">
						<div id="_grid_wrap" style="height: 200px; border-top: 1px solid #3180c3; margin: 3px 5px 3px 5px;"></div>
						<script type="text/javascript">
							let _myGridID;
							const _columns = [ {
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
								dataField : "createdDate",
								headerText : "작성일",
								dataType : "string",
								width : 100
							} ]

							function _createAUIGrid(columnLayout) {
								const props = {
										headerHeight : 30,
										showRowNumColumn : true,
										rowNumHeaderText : "번호",
										showStateColumn : true,
										showRowCheckColumn : true,
									}
								_myGridID = AUIGrid.create("#_grid_wrap", columnLayout, props);
							}

							function _insert() {
								const url = getCallUrl("/epm/popup?method=append&multi=false");
								popup(url, 1400, 600);
							}
							function append(data) {
								// 			rowIdField : "oid",
								for (let i = 0; i < data.length; i++) {
									let item = data[i].item;
									let isUnique = AUIGrid.isUniqueValue(_myGridID, "oid", item.oid);
									// 				alert(isUnique);
									if (isUnique) {
										AUIGrid.addRow(_myGridID, item, "first");
									}
								}
							}
							// 행 삭제
							function _deleteRow() {
								let checked = AUIGrid.getCheckedRowItems(_myGridID);
								for (let i = checked.length - 1; i >= 0; i--) {
									let rowIndex = checked[i].rowIndex;
									AUIGrid.removeRow(_myGridID, rowIndex);
								}
							}
						</script>
					</div>
				</td>
			</tr>
			<tr>
				<th class="req lb">결재</th>
				<td>
					<jsp:include page="/extcore/include/register-include.jsp"></jsp:include>
				</td>
			</tr>
		</table>
		<script type="text/javascript">
			function registerLine() {
				const url = getCallUrl("/epm/register");
				const params = new Object();
				const _addRows = AUIGrid.getAddedRowItems(_myGridID); // 문서
				const _addRows_ = AUIGrid.getAddedRowItems(_myGridID_); // 결재
				const name = document.getElementById("name");

				if (isNull(name.value)) {
					alert("결재 제목을 입력하세요.");
					name.focus();
					return false;
				}

				if (_addRows_.length === 0) {
					alert("결재선을 지정하세요.");
					_register();
					return false;
				}

				if (_addRows.length === 0) {
					alert("결재할 도면을 추가하세요.");
					_insert();
					return false;
				}

				if (!confirm("도면결재를 등록하시겠습니까?")) {
					return false;
				}
				params.name = name.value;
				params._addRows = _addRows;
				params._addRows_ = _addRows_;
				toRegister(params, _addRows_);
				parent.openLayer();
				call(url, params, function(data) {
					if (data.result) {
						document.location.href = getCallUrl("/workspace/approval");
					} else {
						closeLayer();
					}
				})
			}

			document.addEventListener("DOMContentLoaded", function() {
				document.getElementById("name").focus();
				_createAUIGrid(_columns);
				_createAUIGrid_(_columns_);
				AUIGrid.resize(_myGridID);
				AUIGrid.resize(_myGridID_);
			});

			window.addEventListener("resize", function() {
				AUIGrid.bind(_myGridID);
				AUIGrid.bind(_myGridID_);
			});
		</script>
	</form>
</body>
</html>