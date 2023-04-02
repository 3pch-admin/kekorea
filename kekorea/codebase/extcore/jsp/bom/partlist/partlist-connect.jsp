<%@page import="e3ps.org.Department"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@page import="e3ps.org.People"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="java.util.Map"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String poid = (String) request.getAttribute("poid");
String toid = (String) request.getAttribute("toid");
String engType = (String) request.getAttribute("engType");
JSONArray list = (JSONArray) request.getAttribute("list");
%>
<%@include file="/extcore/include/auigrid.jsp"%>
<input type="hidden" name="toid" id="toid" value="<%=toid %>">
<input type="hidden" name="poid" id="poid" value="<%=poid %>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				수배표 등록
			</div>
		</td>
		<td class="right">
			<input type="button" value="등록" title="등록" onclick="create();">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>

<div id="tabs">
	<ul>
		<li>
			<a href="#tabs-1">기본정보</a>
		</li>
		<li>
			<a href="#tabs-2">수배표</a>
		</li>
	</ul>
	<div id="tabs-1">
		<table class="create-table">
			<colgroup>
				<col width="130">
				<col width="400">
				<col width="130">
				<col width="400">
				<col width="130">
				<col width="400">
			</colgroup>
			<tr>
				<th class="req lb">수배표 제목</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-400">
				</td>
				<th>설계구분</th>
				<td class="indent5">
					<select name="engType" id="engType" class="width-200">
						<option value="">선택</option>
						<option value="기계">기계</option>
						<option value="전기">전기</option>
					</select>
				</td>
				<th>진행율</th>
				<td class="indent5">
					<input type="number" name="progress" id="progress" class="width-300">
				</td>
			</tr>
			<tr>
				<th class="req lb">KEK 작번</th>
				<td colspan="5">
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
								width : 130,
								filter : {
									showIcon : true,
									inline : true
								},
							}, {
								dataField : "keNumber",
								headerText : "KE 작번",
								dataType : "string",
								width : 130,
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
								AUIGrid.setGridData(_myGridID, <%=list%>);
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
				<td class="indent5" colspan="5">
					<textarea name="description" id="description" rows="8"></textarea>
				</td>
			</tr>
			<tr>
				<th class="req lb">결재</th>
				<td colspan="5">
					<jsp:include page="/extcore/include/register-include.jsp"></jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">첨부파일</th>
				<td class="indent5" colspan="5">
					<jsp:include page="/extcore/include/primary-include.jsp">
						<jsp:param value="" name="oid" />
						<jsp:param value="create" name="mode" />
					</jsp:include>
				</td>
			</tr>
		</table>
	</div>
	<div id="tabs-2">
		<table class="button-table">
			<tr>
				<td class="left">
					<input type="button" value="행 추가(이전)" title="행 추가(이전)" class="blue" onclick="addBeforeRow();">
					<input type="button" value="행 추가(이후)" title="행 추가(이후)" class="orange" onclick="addAfterRow();">
					<input type="button" value="행 삭제" title="행 삭제" class="red" onclick="deleteRow();">
				</td>
			</tr>
		</table>
		<div id="grid_wrap" style="height: 780px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID;
			const columns = [ {
				dataField : "lotNo",
				headerText : "LOT_NO",
				dataType : "numeric",
				width : 80,
				editRenderer : {
					type : "InputEditRenderer",
					onlyNumeric : true,
					maxlength : 3,
				},
			}, {
				dataField : "unitName",
				headerText : "UNIT NAME",
				dataType : "string",
				width : 120
			}, {
				dataField : "partNo",
				headerText : "부품번호",
				dataType : "string",
				width : 130,
			}, {
				dataField : "partName",
				headerText : "부품명",
				dataType : "string",
				width : 200,
			}, {
				dataField : "standard",
				headerText : "규격",
				dataType : "string",
				width : 250,
			}, {
				dataField : "maker",
				headerText : "MAKER",
				dataType : "string",
				width : 130,
			}, {
				dataField : "customer",
				headerText : "거래처",
				dataType : "string",
				width : 130,
			}, {
				dataField : "quantity",
				headerText : "수량",
				dataType : "numeric",
				width : 60,
				formatString : "###0",
				editRenderer : {
					type : "InputEditRenderer",
					onlyNumeric : true,
				},
			}, {
				dataField : "unit",
				headerText : "단위",
				dataType : "string",
				width : 80,
			}, {
				dataField : "price",
				headerText : "단가",
				dataType : "numeric",
				width : 120,
			}, {
				dataField : "currency",
				headerText : "화폐",
				dataType : "string",
				width : 60,
			}, {
				dataField : "won",
				headerText : "원화금액",
				dataType : "numeric",
				width : 120,
			}, {
				dataField : "partListDate",
				headerText : "수배일자",
				dataType : "date",
				formatString : "yyyy-mm-dd",
				width : 100,
				editable : false
			}, {
				dataField : "exchangeRate",
				headerText : "환율",
				dataType : "numeric",
				width : 80,
				formatString : "#,##0.0000"
			}, {
				dataField : "referDrawing",
				headerText : "참고도면",
				dataType : "string",
				width : 120,
			}, {
				dataField : "classification",
				headerText : "조달구분",
				dataType : "string",
				width : 120,
			}, {
				dataField : "note",
				headerText : "비고",
				dataType : "string",
				width : 250,
			} ];

			function createAUIGrid(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : true,
					showRowCheckColumn : true,
					showStateColumn : true,
					rowNumHeaderText : "번호",
					selectionMode : "multipleCells",
					showDragKnobColumn : true,
					enableDrag : true,
					enableMultipleDrag : true,
					enableDrop : true,
					enableSorting : false,
					$compaEventOnPaste : true,
					editable : true,
					enableRowCheckShiftKey : true,
					useContextMenu : true,
					enableRightDownFocus : true,
					contextMenuItems : [ {
						label : "선택된 행 이전 추가",
						callback : contextItemHandler
					}, {
						label : "선택된 행 이후 추가",
						callback : contextItemHandler
					}, {
						label : "_$line"
					}, {
						label : "선택된 행 삭제",
						callback : contextItemHandler
					} ],
				};
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				readyHandler();
				AUIGrid.bind(myGridID, "cellEditEnd", auiCellEditEndHandler);
				AUIGrid.bind(myGridID, "beforeRemoveRow", auiBeforeRemoveRow);
			}

			function auiCellEditEndHandler(event) {
				const rowIndex = event.rowIndex;
				const item = {
					sort : event.rowIndex,
				}
				AUIGrid.updateRow(myGridID, item, event.rowIndex);
			}

			function contextItemHandler(event) {
				const item = new Object();
				switch (event.contextIndex) {
				case 0:
					item.createdDate = new Date();
					AUIGrid.addRow(myGridID, item, "selectionUp");
					break;
				case 1:
					item.createdDate = new Date();
					AUIGrid.addRow(myGridID, item, "selectionDown");
					break;
				case 3:
					const selectedItems = AUIGrid.getSelectedItems(myGridID);
					const rows = AUIGrid.getRowCount(myGridID);
					if (rows === 1) {
						alert("최 소 하나의 행이 존재해야합니다.");
						return false;
					}
					for (let i = selectedItems.length - 1; i >= 0; i--) {
						const rowIndex = selectedItems[i].rowIndex;
						AUIGrid.removeRow(myGridID, rowIndex);
					}
					break;
				}
			}

			function auiBeforeRemoveRow(event) {
				const rows = AUIGrid.getRowCount(myGridID);
				if (rows === 1) {
					alert("최 소 하나의 행이 존재해야합니다.");
					return false;
				}
				return true;
			}

			function deleteRow() {
				const checked = AUIGrid.getCheckedRowItems(myGridID);
				const rows = AUIGrid.getRowCount(myGridID);
				if (rows === 1) {
					alert("최 소 하나의 행이 존재해야합니다.");
					return false;
				}

				if (checked.length === 0) {
					alert("삭제할 행을 선택하세요.");
					return false;
				}
				for (let i = checked.length - 1; i >= 0; i--) {
					const rowIndex = checked[i].rowIndex;
					AUIGrid.removeRow(myGridID, rowIndex);
				}
			};

			function readyHandler() {
				const item = new Object();
				item.partListDate = new Date();
				AUIGrid.addRow(myGridID, item, "last");
			}

			function addBeforeRow() {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				if (checkedItems.length === 0) {
					alert("추가하려는 행의 기준이 되는 행을 선택하세요.");
					return false;
				}
				if (checkedItems.length > 1) {
					alert("하나의 행만 선택하세요.");
					return false;
				}
				const item = new Object();
				const rowIndex = checkedItems[0].rowIndex;
				item.partListDate = new Date();
				AUIGrid.addRow(myGridID, item, rowIndex);
			}

			function addAfterRow() {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				if (checkedItems.length === 0) {
					alert("추가하려는 행의 기준이 되는 행을 선택하세요.");
					return false;
				}
				if (checkedItems.length > 1) {
					alert("하나의 행만 선택하세요.");
					return false;
				}
				const item = new Object();
				const rowIndex = checkedItems[0].rowIndex;
				item.partListDate = new Date();
				AUIGrid.addRow(myGridID, item, rowIndex + 1);
			}
		</script>
	</div>
</div>

<script type="text/javascript">
	function create() {

		const params = new Object();
		const url = getCallUrl("/partlist/create");
		const addRows = AUIGrid.getAddedRowItems(myGridID);
		const _addRows = AUIGrid.getGridData(_myGridID);
		const _addRows_ = AUIGrid.getAddedRowItems(_myGridID_);
		const name = document.getElementById("name").value;
		const engType = document.getElementById("engType").value;
		const description = document.getElementById("description").value;
		const progress = document.getElementById("progress").value;
		const toid = document.getElementById("toid").value;
		const poid = document.getElementById("poid").value;
		addRows.sort(function(a, b) {
			return a.sort - b.sort;
		});

		if (!confirm("등록 하시겠습니까?")) {
			return false;
		}
		params.addRows = addRows;
		params._addRows = _addRows;
		params._addRows_ = _addRows_;
		params.name = name;
		params.engType = engType;
		params.description = description;
		params.primarys = toArray("primarys");
		params.progress = Number(progress);
		params.toid = toid;
		params.poid = poid;
		toRegister(params, _addRows_);
		console.log(params);
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.document.location.reload();
				self.close();
			}
		})
	}

	document.addEventListener("DOMContentLoaded", function() {
		$("#tabs").tabs({
			active : 0,
			create : function(event, ui) {
				const tabId = ui.panel.prop("id");
				switch (tabId) {
				case "tabs-1":
					_createAUIGrid(_columns);
					AUIGrid.resize(_myGridID);
					_createAUIGrid_(_columns_);
					AUIGrid.resize(_myGridID_);
					break;
				case "tabs-2":
					createAUIGrid(columns);
					AUIGrid.resize(myGridID);
					break;
				}
			},
			activate : function(event, ui) {
				var tabId = ui.newPanel.prop("id");
				switch (tabId) {
				case "tabs-1":
					const _isCreated = AUIGrid.isCreated(_myGridID);
					const _isCreated_ = AUIGrid.isCreated(_myGridID_);
					if (_isCreated) {
						AUIGrid.resize(_myGridID);
					} else {
						_createAUIGrid(_columns);
					}

					if (_isCreated_) {
						AUIGrid.resize(_myGridID_);
					} else {
						_createAUIGrid_(_columns_);
					}
					selectbox("engType");
					$("#engType").bindSelectDisabled(true);
					break;
				case "tabs-2":
					const isCreated = AUIGrid.isCreated(myGridID);
					if (isCreated) {
						AUIGrid.resize(myGridID);
					} else {
						createAUIGrid(columns);
					}
					break;
				}
			}
		});
		selectbox("engType");
		$("#engType").bindSelectSetValue("<%=engType%>");
		$("#engType").bindSelectDisabled(true);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
		AUIGrid.resize(_myGridID_);
		AUIGrid.resize(_myGridID);
	});
</script>