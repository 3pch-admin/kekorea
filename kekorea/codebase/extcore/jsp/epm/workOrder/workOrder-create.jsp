<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/extcore/include/auigrid.jsp"%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				도면일람표 등록
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
			<a href="#tabs-2">도면 일람표</a>
		</li>
	</ul>
	<div id="tabs-1">
		<table class="create-table">
			<colgroup>
				<col width="160">
				<col width="*">
			</colgroup>
			<tr>
				<th class="req lb">도면 일람표 명</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="AXInput width-500">
				</td>
			</tr>
			<tr>
				<th class="req lb">KEK 작번</th>
				<td>
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
								style : "left indent10",
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
									rowHeight : 30,
									showRowNumColumn : true,
									showRowCheckColumn : true,
									showStateColumn : true,
									rowNumHeaderText : "번호",
									showAutoNoDataMessage : false,
									selectionMode : "multipleCells",
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
				<th class="req lb">작업 내용</th>
				<td class="indent5">
					<textarea name="description" id="description" rows="8"></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">첨부파일</th>
				<td class="indent5">
					<jsp:include page="/extcore/include/secondary-include.jsp">
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
		<div id="grid_wrap" style="height: 800px; border-top: 1px solid #3180c3;"></div>
	</div>
</div>

<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "ok",
		headerText : "검증",
		dataType : "boolean",
		width : 80,
		renderer : {
			type : "CheckBoxEditRenderer",
		},
	}, {
		dataField : "preView",
		headerText : "미리보기",
		width : 80,
		editable : false,
		renderer : {
			type : "ImageRenderer",
			altField : null,
			imgHeight : 34,
		},
	}, {
		dataField : "dataType",
		headerText : "파일유형",
		dataType : "string",
		width : 100,
		editable : false,
	}, {
		dataField : "name",
		headerText : "DRAWING TITLE",
		dataType : "string",
		editable : false,
		style : "left indent10"
	}, {
		dataField : "number",
		headerText : "DWG. NO",
		dataType : "string",
		width : 200,
		editRenderer : {
			type : "InputEditRenderer",
			maxlength : 10,
			regExp : "^[a-zA-Z0-9]+$",
			autoUpperCase : true,
		},
		headerTooltip : {
			show : true,
			tooltipHtml : "품번 입력시 서버의 데이터를 가져와서 정보를 입력합니다.<br>같은 도번이 존재할시 데이터의 우선순위는 KEK의 도면 데이터를 가져옵니다."
		},
	}, {
		dataField : "current",
		headerText : "CURRENT VER",
		dataType : "string",
		width : 130,
		editable : false
	}, {
		dataField : "rev",
		headerText : "REV",
		dataType : "string",
		width : 130,
		editable : false
	}, {
		dataField : "lotNo",
		headerText : "LOT",
		dataType : "numeric",
		width : 100,
		formatString : "###0",
		editRenderer : {
			type : "InputEditRenderer",
			onlyNumeric : true,
			maxlength : 3,
		},
	}, {
		dataField : "createdDate",
		headerText : "등록일",
		dataType : "date",
		formatString : "yyyy-mm-dd",
		width : 100,
		editable : false
	}, {
		dataField : "note",
		headerText : "NOTE",
		dataType : "string",
		width : 350,
	} ]

	function createAUIGrid(columnLayout) {
		const props = {
			headerHeight : 30,
			rowHeight : 30,
			showRowNumColumn : true,
			showRowCheckColumn : true,
			showStateColumn : true,
			rowNumHeaderText : "번호",
			selectionMode : "multipleCells",
			showDragKnobColumn : true,
			enableDrag : true,
			enableMultipleDrag : true,
			enableDrop : true,
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
		AUIGrid.bind(myGridID, "cellClick", auiCellClickHandler);
	}

	function auiCellClickHandler(event) {
		const dataField = event.dataField;
		const oid = event.item.oid;
		const preView = event.item.preView;
		if (dataField === "preView") {
			if (oid !== undefined && preView !== undefined) {
				const url = getCallUrl("/aui/thumbnail?oid=" + oid);
				popup(url);
			}
		}
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
		item.createdDate = new Date();
		AUIGrid.addRow(myGridID, item, "last");
	}

	function auiCellEditEndHandler(event) {
		const dataField = event.dataField;
		if (dataField === "number") {
			const number = event.item.number;
			const url = getCallUrl("/workOrder/getData?number=" + number);
			call(url, null, function(data) {
				if (data.ok) {
					const item = {
						ok : data.ok,
						name : data.name,
						rev : data.rev,
						current : data.current,
						lotNo : data.lotNo,
						oid : data.oid,
						dataType : "KE도면",
						sort : event.rowIndex,
						createdDate : new Date(),
						preView : data.preView
					}
					AUIGrid.updateRow(myGridID, item, event.rowIndex);
				} else {
					const item = {
						ok : data.ok,
					}
				}
			}, "GET");
		}
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
		item.createdDate = new Date();
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
		item.createdDate = new Date();
		AUIGrid.addRow(myGridID, item, rowIndex + 1);
	}

	function create() {

		const params = new Object();
		const addRows = AUIGrid.getAddedRowItems(myGridID);
		const _addRows = AUIGrid.getAddedRowItems(_myGridID);
		const url = getCallUrl("/workOrder/create");
		
		_addRows.sort(function(a, b) {
			return a.sort - b.sort;
		});

		addRows.sort(function(a, b) {
			return a.sort - b.sort;
		});

		if (!confirm("등록 하시겠습니까?")) {
			return false;
		}

		params.name = document.getElementById("name").value;
		params.addRows = addRows;
		params._addRows = _addRows;
		params.secondarys = toArray("secondarys");
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
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
					const _isCreated = AUIGrid.isCreated(_myGridID_);
					if (_isCreated_ && _isCreated) {
						AUIGrid.resize(_myGridID);
					} else {
						_createAUIGrid(_columns);
					}
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
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
		AUIGrid.resize(_myGridID);
	});
</script>