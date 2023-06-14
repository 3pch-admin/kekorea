<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="e3ps.org.Department"%>
<%@page import="e3ps.doc.service.DocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String toid = (String) request.getAttribute("toid");
String poid = (String) request.getAttribute("poid");
JSONArray data = (JSONArray) request.getAttribute("data");
%>
<!-- AUIGrid -->
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>
<input type="hidden" name="toid" id="toid" value="<%=toid%>">
<input type="hidden" name="poid" id="poid" value="<%=poid%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				T-BOM 등록
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
			<a href="#tabs-2">T-BOM</a>
		</li>
	</ul>
	<div id="tabs-1">
		<table class="create-table">
			<colgroup>
				<col width="130">
				<col width="*">
			</colgroup>
			<tr>
				<th class="req lb">T-BOM 제목</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-500">
				</td>
			</tr>
			<tr>
				<th class="lb">내용</th>
				<td class="indent5">
					<textarea name="description" id="description" rows="5"></textarea>
				</td>
			</tr>
			<tr>
				<th class="req lb">KEK 작번</th>
				<td colspan="3">
					<jsp:include page="/extcore/jsp/common/project-include.jsp">
						<jsp:param value="" name="oid" />
						<jsp:param value="true" name="multi"/>
						<jsp:param value="create" name="mode" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">첨부파일</th>
				<td class="indent5">
					<jsp:include page="/extcore/jsp/common/attach-secondary.jsp">
						<jsp:param value="" name="oid" />
					</jsp:include>
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
		<div id="grid_wrap" style="height: 690px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID;
			const columns = [ {
				dataField : "ok",
				headerText : "검증",
				width : 80,
				renderer : {
					type : "CheckBoxEditRenderer",
				},
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
				dataField : "code",
				headerText : "중간코드",
				dataType : "string",
				width : 130,
			}, {
				dataField : "keNumber",
				headerText : "부품번호",
				dataType : "string",
				width : 150,
			}, {
				dataField : "name",
				headerText : "부품명",
				dataType : "string",
				editable : false
			}, {
				dataField : "model",
				headerText : "KokusaiModel",
				dataType : "string",
				width : 200,
				editable : false
			}, {
				dataField : "qty",
				headerText : "QTY",
				dataType : "numeric",
				width : 100,
				formatString : "###0",
				editRenderer : {
					type : "InputEditRenderer",
					onlyNumeric : true,
				},
			}, {
				dataField : "unit",
				headerText : "UNIT",
				dataType : "string",
				width : 130
			}, {
				dataField : "provide",
				headerText : "PROVIDE",
				dataType : "string",
				width : 130
			}, {
				dataField : "discontinue",
				headerText : "DISCONTINUE",
				dataType : "string",
				width : 200
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
					enableMultipleDrag : true,
					enableDrop : true,
					$compaEventOnPaste : true,
					editable : true,
					enableRowCheckShiftKey : true,
					useContextMenu : true,
					selectionMode : "multipleCells",
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
		</script>
	</div>
</div>

<script type="text/javascript">
	function create() {
		const params = new Object();
		const url = getCallUrl("/tbom/create");
		const name = document.getElementById("name");
		const description = document.getElementById("description").value;
		const addRows = AUIGrid.getAddedRowItems(myGridID);
		const addRows9 = AUIGrid.getAddedRowItems(myGridID9);
		const addRows8 = AUIGrid.getAddedRowItems(myGridID8);
		const toid = document.getElementById("toid").value;
		const poid = document.getElementById("poid").value;

		addRows.sort(function(a, b) {
			return a.sort - b.sort;
		});

		if (isNull(name.value)) {
			alert("T-BOM 제목을 입력하세요.");
			name.focus();
			return false;
		}

		if (addRows9.length === 0) {
			alert("최소 하나 이상의 작번을 추가하세요.");
			insert9();
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

			if (isNull(item.lotNo) || item.lotNo === 0) {
				alert("LOT NO의 값은 0혹은 공백을 입력 할 수 없습니다.\nT-BOM 탭으로 이동하여 확인 해주세요.");
				AUIGrid.showToastMessage(myGridID, rowIndex, 1, "LOT NO의 값은 0혹은 공백을 입력 할 수 없습니다.");
				return false;
			}
			
			if (isNull(item.code)) {
				alert("중간코드는 공백을 입력 할 수 없습니다.\nT-BOM 탭으로 이동하여 확인 해주세요.");
				AUIGrid.showToastMessage(myGridID, rowIndex, 2, "중간코드는 공백을 입력 할 수 없습니다.");
				return false;
			}
			
			if (isNull(item.keNumber)) {
				alert("부품번호를 공백을 입력 할 수 없습니다.\nT-BOM 탭으로 이동하여 확인 해주세요.");
				AUIGrid.showToastMessage(myGridID, rowIndex, 3, "부품번호는 공백을 입력 할 수 없습니다.");
				return false;
			}
			
			if (isNull(item.qty) || item.qty === 0) {
				alert("수량의 값은 0혹은 공백을 입력 할 수 없습니다.\nT-BOM 탭으로 이동하여 확인 해주세요.");
				AUIGrid.showToastMessage(myGridID, rowIndex, 6, "LOT NO의 값은 0혹은 공백을 입력 할 수 없습니다.");
				return false;
			}
		}

		if (!confirm("등록 하시겠습니까?")) {
			return false;
		}

		params.name = name.value;
		params.description = description;
		params.addRows = addRows;
		params.addRows9 = addRows9;
		params.secondarys = toArray("secondarys");
		params.toid = toid;
		params.poid = poid;
		toRegister(params, addRows8);
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				<%
					if(!StringUtils.isNull(toid)) {
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

	function auiCellEditEndHandler(event) {
		const dataField = event.dataField;
		const number = event.item.keNumber;
		if (dataField === "keNumber" && !isNull(number)) {
			const url = getCallUrl("/tbom/getData?number=" + number);
			call(url, null, function(data) {
				if (data.ok) {
					const item = {
						ok : data.ok,
						name : data.name,
						keNumber : data.keNumber,
						lotNo : data.lotNo,
						oid : data.oid,
						// 						code : data.code,
						model : data.model,
						qty : 1,
						unit : "EA",
						createdDate : new Date()
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

		createAUIGrid9(columns9);
		createAUIGrid8(columns8);
		createAUIGrid(columns);
		AUIGrid.resize(myGridID9);
		AUIGrid.resize(myGridID8);
		AUIGrid.resize(myGridID);
		<%
		if(data != null) {
		%>
		AUIGrid.bind(myGridID9, "beforeRemoveRow", auiBeforeRemoveRow);
		AUIGrid.addRow(myGridID9, <%=data%>);
		<%
			}
		%>
	})

	function auiBeforeRemoveRow(event) {
		const item = event.items[0];
		const oid = document.getElementById("poid").value;
		if (item.oid === oid) {
			alert("기준 작번은 제거 할 수 없습니다.");
			return false;
		}
		return true;
	}
	
	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID9);
		AUIGrid.resize(myGridID8);
		AUIGrid.resize(myGridID);
	});
</script>