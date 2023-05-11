<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String workOrderType = (String) request.getAttribute("workOrderType");
String toid = (String) request.getAttribute("toid");
%>
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>
<style type="text/css">
.preView {
	background-color: #caf4fd;
	cursor: pointer;
}
</style>
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
				<col width="150">
				<col width="350">
				<col width="150">
				<col width="350">
				<col width="150">
				<col width="350">
			</colgroup>
			<tr>
				<th class="req lb">도면 일람표 명</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-200">
				</td>
				<th>설계구분</th>
				<td class="indent5">
					<select name="workOrderType" id="workOrderType" class="width-100">
						<option value="">선택</option>
						<option value="기계">기계</option>
						<option value="전기">전기</option>
					</select>
				</td>
				<th>진행율</th>
				<td class="indent5">
					<input type="text" name="progress" id="progress" class="width-200">
				</td>
			</tr>
			<tr>
				<th class="lb">내용</th>
				<td class="indent5" colspan="5">
					<textarea name="description" id="description" rows="5"></textarea>
				</td>
			</tr>
			<tr>
				<th class="req lb">KEK 작번</th>
				<td colspan="5">
					<jsp:include page="/extcore/jsp/common/project-include.jsp">
						<jsp:param value="" name="oid" />
						<jsp:param value="create" name="mode" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">첨부파일</th>
				<td class="indent5" colspan="5">
					<jsp:include page="/extcore/jsp/common/attach-secondary.jsp">
						<jsp:param value="" name="oid" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="req lb">결재</th>
				<td colspan="5">
					<jsp:include page="/extcore/jsp/common/approval-register.jsp">
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
		<div id="grid_wrap" style="height: 670px; border-top: 1px solid #3180c3;"></div>
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
		style :  "preView",
		renderer : {
			type : "ImageRenderer",
			altField : null,
			imgHeight : 34,
		},
	}, {
		dataField : "name",
		headerText : "DRAWING TITLE",
		dataType : "string",
// 		editable : false,
		style : "aui-left"
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
	}, {
		dataField : "current",
		headerText : "CURRENT VER",
		dataType : "numeric",
		width : 120,
		editable : false
	}, {
		dataField : "rev",
		headerText : "REV",
		dataType : "numeric",
		width : 80,
		editable : false
	}, {
		dataField : "lotNo",
		headerText : "LOT",
		dataType : "numeric",
		width : 80,
		formatString : "###0",
		editRenderer : {
			type : "InputEditRenderer",
			onlyNumeric : true,
			maxlength : 3,
		},
	}, {
		dataField : "note",
		headerText : "NOTE",
		dataType : "string",
		width : 350,
	} ]

	function createAUIGrid(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			showRowCheckColumn : true,
			showStateColumn : true,
			rowNumHeaderText : "번호",
			showDragKnobColumn : true,
			enableDrag : true,
			selectionMode : "multipleCells",
			enableMultipleDrag : true,
			enableDrop : true,
			$compaEventOnPaste : true,
			editable : true,
			enableRowCheckShiftKey : true,
			useContextMenu : true,
			enableRightDownFocus : true,
			enableSorting : false,
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
		const doid = event.item.doid;
		const preView = event.item.preView;
		if (dataField === "preView") {
			if (doid !== undefined && preView !== undefined) {
				const url = getCallUrl("/aui/thumbnail?oid=" + doid);
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
		const number = event.item.number;
		if (dataField === "number" && !isNull(number)) {
			const url = getCallUrl("/workOrder/getData?number=" + number);
			call(url, null, function(data) {
				if (data.ok) {
					const item = {
						ok : data.ok,
						name : data.name,
						number : data.number,
						rev : data.rev,
						current : data.current,
						lotNo : data.lotNo,
						doid : data.doid,
						sort : event.rowIndex,
						createdDate : new Date(),
						preView : data.preView
					}
					AUIGrid.updateRow(myGridID, item, event.rowIndex);
				} else {
					const item = {
						ok : data.ok,
						number : data.number,
					}
					AUIGrid.updateRow(myGridID, item, event.rowIndex);
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
		const name = document.getElementById("name");
		const description = document.getElementById("description").value;
		const workOrderType = document.getElementById("workOrderType").value;
		const progress = document.getElementById("progress").value;
		const addRows = AUIGrid.getAddedRowItems(myGridID);
		const addRows9 = AUIGrid.getAddedRowItems(myGridID9);
		const addRows8 = AUIGrid.getAddedRowItems(myGridID8);
		const url = getCallUrl("/workOrder/create");

		addRows.sort(function(a, b) {
			return a.sort - b.sort;
		});

		if (isNull(name.value)) {
			alert("도면일람표 제목을 입력하세요.");
			name.focus();
			return false;
		}

		if (addRows9.length === 0) {
			alert("최소 하나이상의 작번을 추가하세요.");
			_insert();
			return false;
		}

		if (addRows8.length === 0) {
			alert("결재선을 지정하세요.");
			_register();
			return false;
		}

		for (let i = 0; i < addRows.length; i++) {
			const item = addRows[i];
			const rowIndex = AUIGrid.rowIdToIndex(myGridID, item._$uid);
			
			if (isNull(item.number) || item.number === 0) {
				alert("DWG. NO 값은 공백을 입력 할 수 없습니다.\n도면일람표 탭으로 이동하여 확인 해주세요.");
				AUIGrid.showToastMessage(myGridID, rowIndex, 2, "DWG. NO 값은 공백을 입력 할 수 없습니다.");
				return false;
			}
			
			if (isNull(item.lotNo) || item.lotNo === 0) {
				alert("LOT NO의 값은 0혹은 공백을 입력 할 수 없습니다.\n도면일람표 탭으로 이동하여 확인 해주세요.");
				AUIGrid.showToastMessage(myGridID, rowIndex, 6, "LOT NO의 값은 0혹은 공백을 입력 할 수 없습니다.");
				return false;
			}
		}


		if (!confirm("등록 하시겠습니까?")) {
			return false;
		}

		params.name = name.value;
		params.description = description;
		params.workOrderType = workOrderType;
		params.progress = Number(progress);
		params.addRows = addRows;
		params.addRows9 = addRows9;
		params.secondarys = toArray("secondarys");
		toRegister(params, addRows8);
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
		document.getElementById("name").focus();
		$("#tabs").tabs({
			active : 0,
			activate : function(event, ui) {
				var tabId = ui.newPanel.prop("id");
				switch (tabId) {
				case "tabs-1":
					const isCreated9 = AUIGrid.isCreated(myGridID9);
					if (isCreated9) {
						AUIGrid.resize(myGridID9);
					} else {
						createAUIGrid9(columns9);
					}
					const isCreated8 = AUIGrid.isCreated(myGridID8);
					if (isCreated8) {
						AUIGrid.resize(myGridID8);
					} else {
						createAUIGrid8(columns8);
					}
					selectbox("workOrderType");
					$("#workOrderType").bindSelectDisabled(true);
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
		selectbox("workOrderType");
		$("#workOrderType").bindSelectSetValue("<%=workOrderType%>");
		$("#workOrderType").bindSelectDisabled(true);
		createAUIGrid(columns);
		createAUIGrid9(columns9);
		createAUIGrid8(columns8);
		AUIGrid.resize(myGridID9);
		AUIGrid.resize(myGridID8);

		// 진행율
		const field = document.getElementById("progress");
		field.addEventListener("input", function(event) {
			const value = event.target.value;
			if (value.slice(0, 1) === "0") {
				alert("첫째 자리의 값은 0을 입력 할 수 없습니다.");
				event.target.value = "";
			}

			if (value.length > 3 || !/^[1-9][0-9]{0,2}$|^0$/.test(value)) {
				event.target.value = value.replace(/[^\d]/g, '').slice(0, 3);
			}

			if (value > 100) {
				event.target.value = 100;
			}
		})
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
		AUIGrid.resize(myGridID9);
		AUIGrid.resize(myGridID8);
	});
</script>