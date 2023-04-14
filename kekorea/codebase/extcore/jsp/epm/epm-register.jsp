<%@page import="java.util.Map"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/include/css.jsp"%>
<%@include file="/extcore/include/script.jsp"%>
<%@include file="/extcore/include/auigrid.jsp"%>
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
				<th class="req lb">결재</th>
				<td>
					<div class="include">
						<input type="button" value="결재선 지정" title="결재선 지정" class="blue" onclick="_register();">
						<input type="button" value="결재선 삭제" title="결재선 삭제" class="red" onclick="_deleteRow_();">
						<div id="_grid_wrap_" style="height: 200px; border-top: 1px solid #3180c3; margin: 5px;"></div>
						<script type="text/javascript">
							let _myGridID_;
							const _columns_ = [ {
								dataField : "sort",
								headerText : "순서",
								dataType : "numeric",
								width : 100
							}, {
								dataField : "type",
								headerText : "결재타입",
								dataType : "string",
								width : 150,
							}, {
								dataField : "name",
								headerText : "이름",
								dataType : "string",
								width : 150,
							}, {
								dataField : "id",
								headerText : "아이디",
								dataType : "string",
								width : 150,
							}, {
								dataField : "duty",
								headerText : "직급",
								dataType : "string",
								width : 150,
							}, {
								dataField : "department_name",
								headerText : "부서",
								dataType : "string",
							} ]

							function _createAUIGrid_(columnLayout) {
								const props = {
									headerHeight : 30,
									rowHeight : 30,
									showRowNumColumn : true,
									rowNumHeaderText : "번호",
									selectionMode : "multipleCells",
									fillColumnSizeMode : true,
									showStateColumn : true,
									softRemoveRowMode : false,
									showRowCheckColumn : true,
									showAutoNoDataMessage : false,
									enableRowCheckShiftKey : true,
									showDragKnobColumn : true,
									enableDrag : true,
									enableMultipleDrag : true,
									enableDrop : true,
									useContextMenu : true,
									enableSorting : false,
									contextMenuItems : [ {
										label : "선택된 행 삭제",
										callback : contextItemHandler
									} ],
								}
								_myGridID_ = AUIGrid.create("#_grid_wrap_", columnLayout, props);
								AUIGrid.bind(_myGridID_, "removeRow", auiRemoveRow);
							}

							function auiRemoveRow(event) {
								const data = AUIGrid.getGridData(_myGridID_);
								// 			const removeRows = AUIGrid.getRemovedItems(_myGridID_);
								// 			for (let i = 0; i < data.length; i++) {
								// 				const rowIndex = AUIGrid.rowIdToIndex(_myGridID_, data[i]._$uid);
								// 				const sort = data[i].sort;
								// 				const item = {
								// 					sort : (sort - removeRows.length)
								// 				}
								// 				AUIGrid.updateRow(_myGridID_, item, rowIndex);
								// 			}
							}

							function _register() {
								const url = getCallUrl("/workspace/popup");
								popup(url, 1200, 700);
							}

							function contextItemHandler(event) {
								const item = new Object();
								switch (event.contextIndex) {
								case 0:
									const selectedItems = AUIGrid.getSelectedItems(event.pid);
									for (let i = selectedItems.length - 1; i >= 0; i--) {
										const rowIndex = selectedItems[i].rowIndex;
										AUIGrid.removeRow(event.pid, rowIndex);
									}
									break;
								}
							}

							function _deleteRow_() {
								const checked = AUIGrid.getCheckedRowItems(_myGridID_);
								for (let i = checked.length - 1; i >= 0; i--) {
									const rowIndex = checked[i].rowIndex;
									AUIGrid.removeRow(_myGridID_, rowIndex);
								}
							}

							function setLine(agree, approval, receive) {
								AUIGrid.clearGridData(_myGridID_);

								for (let i = receive.length - 1; i >= 0; i--) {
									const item = receive[i];
									item.type = "수신";
									AUIGrid.addRow(_myGridID_, item, "first");
								}

								let sort = approval.length;
								for (let i = approval.length - 1; i >= 0; i--) {
									const item = approval[i];
									item.type = "결재";
									item.sort = sort;
									AUIGrid.addRow(_myGridID_, item, "first");
									sort--;
								}

								for (let i = agree.length - 1; i >= 0; i--) {
									const item = agree[i];
									item.type = "검토";
									AUIGrid.addRow(_myGridID_, item, "first");
								}
							}
						</script>
					</div>
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
								headerText : "파일이름",
								dataType : "string",
								width : 400,
								renderer : {
									type : "LinkRenderer",
									baseUrl : "javascript", // 자바스크립 함수 호출로 사용하고자 하는 경우에 baseUrl 에 "javascript" 로 설정
									// baseUrl 에 javascript 로 설정한 경우, 링크 클릭 시 callback 호출됨.
									jsCallback : function(rowIndex, columnIndex, value, item) {
										const oid = item.oid;
										alert(oid);
									}
								},
							}, {
								dataField : "part_code",
								headerText : "품번",
								dataType : "string",
								width : 150
							}, {
								dataField : "name_of_parts",
								headerText : "품명",
								dataType : "string",
								width : 150
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
								dataField : "modifier",
								headerText : "수정자",
								dataType : "string",
								width : 100
							} ]

							function _createAUIGrid(columnLayout) {
								const props = {
									headerHeight : 30,
									rowHeight : 30,
									showRowNumColumn : true,
									rowNumHeaderText : "번호",
									fillColumnSizeMode : true,
									showStateColumn : true, // 상태표시 행 출력 여부
									softRemoveRowMode : false,
									showRowCheckColumn : true, // 체크 박스 출력
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
					} else{
						closeLayer();
					}
				})
			}

			document.addEventListener("DOMContentLoaded", function() {
				document.getElementById("name").focus();
				_createAUIGrid(_columns);
				_createAUIGrid_(_columns_);
			});

			window.addEventListener("resize", function() {
				AUIGrid.bind(_myGridID);
				AUIGrid.bind(_myGridID_);
			});
		</script>
	</form>
</body>
</html>