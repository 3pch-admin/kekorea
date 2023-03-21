<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- AUIGrid -->
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

<table class="create-table">
	<colgroup>
		<col width="130">
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
			<jsp:include page="/extcore/include/project-include.jsp">
				<jsp:param value="" name="oid" />
				<jsp:param value="create" name="mode" />
				<jsp:param value="true" name="multi" />
				<jsp:param value="" name="obj" />
				<jsp:param value="150" name="height" />
			</jsp:include>
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
<table class="button-table">
	<tr>
		<td class="left">
			<input type="button" value="행 추가" title="행 추가" class="blue" onclick="addRow();">
			<input type="button" value="행 삭제" title="행 삭제" class="red" onclick="deleteRow();">
		</td>
	</tr>
</table>
<div id="grid_wrap" style="height: 400px; border-top: 1px solid #3180c3;"></div>
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
			onlyNumeric : true, // 0~9만 입력가능
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
			// 복사 후 편집 이벤트 발생하는 속성
			$compaEventOnPaste : true,
			editable : true
		};
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		readyHandler();
		AUIGrid.bind(myGridID, "cellEditEnd", auiCellEditEndHandler);
	}

	// 행 삭제
	function deleteRow() {
		const checked = AUIGrid.getCheckedRowItems(myGridID);
		for (let i = checked.length - 1; i >= 0; i--) {
			const rowIndex = checked[i].rowIndex;
			AUIGrid.removeRow(myGridID, rowIndex);
		}
	};

	function readyHandler() {
		const item = new Object();
		item.createdDate = new Date();
		AUIGrid.addRow(myGridID, item, "last"); // 끝에??
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

	// 행 추가
	function addRow() {
		const item = new Object();
		item.createdDate = new Date();
		AUIGrid.addRow(myGridID, item, "last");
	}

	// 등록
	function create() {

		if (!confirm("등록 하시겠습니까?")) {
			return false;
		}
		const params = new Object();
		const addRows = AUIGrid.getAddedRowItems(myGridID); // 도면 일람표
		const _addRows = AUIGrid.getAddedRowItems(_myGridID); // 프로젝트
		
		_addRows.sort(function(a, b) {
			return a.sort - b.sort;
		});

		addRows.sort(function(a, b) {
			return a.sort - b.sort;
		});

		params.name = document.getElementById("name").value;
		params.addRows = addRows;
		params._addRows = _addRows;
		params.secondarys = toArray("secondarys");
		console.log(params);
		const url = getCallUrl("/workOrder/create");
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			} else {
				// 실패시 처리할 부분..
			}
		})
	}

	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid(columns);
		_createAUIGrid(_columns);
		AUIGrid.resize(myGridID);
		AUIGrid.resize(_myGridID);
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
		AUIGrid.resize(_myGridID);
	});
</script>