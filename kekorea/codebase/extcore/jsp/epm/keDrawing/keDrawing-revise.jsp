<%@page import="org.json.JSONArray"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- AUIGrid -->
<%@include file="/extcore/include/auigrid.jsp"%>
<table class="button-table">
	<tr>
		<td class="right">
			<input type="button" value="개정" title="개정" onclick="revise();">
			<input type="button" value="삭제" title="삭제" class="red" onclick="remove();">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>
<div id="grid_wrap" style="height: 490px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
	let myGridID;
	let recentGridItem = null;
	const data = window.list;
	const columns = [ {
		dataField : "lotNo",
		headerText : "LOT",
		dataType : "numeric",
		width : 100,
		formatString : "###0",
		editable : false
	}, {
		dataField : "name",
		headerText : "DRAWING TITLE",
		dataType : "string",
		editable : false
	}, {
		dataField : "keNumber",
		headerText : "DWG NO",
		dataType : "string",
		width : 200,
		editable : false
	}, {
		dataField : "version",
		headerText : "버전",
		dataType : "numeric",
		width : 80,
		formatString : "###0",
		editable : false,
	}, {
		dataField : "next",
		headerText : "개정버전",
		dataType : "numeric",
		width : 80,
		formatString : "###0",
		editable : false,
	}, {
		dataField : "note",
		headerText : "개정사유",
		dataType : "string",
		width : 250
	}, {
		dataField : "primary",
		headerText : "도면파일",
		dataType : "string",
		width : 100,
		renderer : {
			type : "TemplateRenderer",
		},
	}, {
		dataField : "",
		headerText : "",
		width : 100,
		editable : false,
		renderer : {
			type : "ButtonRenderer",
			labelText : "파일선택",
			onclick : function(rowIndex, columnIndex, value, item) {
				recentGridItem = item;
				let rowId = item.rowId; // ... oid 가 있는데 이상하게 안받아와짐.
				let url = getCallUrl("/aui/primary?oid=" + rowId + "&method=attach");
				popup(url, 1000, 200);
			}
		}
	}, ]

	function createAUIGrid(columnLayout) {
		const props = {
			rowIdField : "rowId",
			headerHeight : 30, // 헤더높이
			rowHeight : 30, // 행 높이
			showRowNumColumn : true, // 번호 행 출력 여부
			showStateColumn : true, // 상태표시 행 출력 여부
			rowNumHeaderText : "번호", // 번호 행 텍스트 설정
			selectionMode : "multipleCells",
			editable : true,
			showRowCheckColumn : true,
		};

		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		readyHandler();
	}

	function readyHandler() {
		// 화면에서 받아온 데이터 그리드로 추가
		for (let i = 0; i < data.length; i++) {
			AUIGrid.addRow(myGridID, data[i].item, "last");
		}
	}

	function attach(data) {
		let name = data.name;
		let start = name.indexOf("-");
		let end = name.lastIndexOf(".");
		let number = name.substring(0, start);
		let next = name.substring(start + 1, end);
		let template = "<img src='" + data.icon + "' style='position: relative; top: 2px;'>";
		AUIGrid.updateRowsById(myGridID, {
			rowId : recentGridItem.rowId,
			number : number,
			next : Number(next),
			file : name,
			primary : template,
			primaryPath : data.fullPath
		});
	}

	// 그리드 행 삭제
	function remove() {
		let checkedItems = AUIGrid.getCheckedRowItems(myGridID);
		for (let i = checkedItems.length - 1; i >= 0; i--) {
			let rowIndex = checkedItems[i].rowIndex;
			AUIGrid.removeRow(myGridID, rowIndex);
		}
	}

	// 개정
	function revise() {

		if (!confirm("개정 하시겠습니까?")) {
			return false;
		}

		let addRows = AUIGrid.getAddedRowItems(myGridID);
		let params = new Object();
		let url = getCallUrl("/keDrawing/revise");
		params.addRows = addRows;
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			} else {
				// 실패시 처리 ??
			}
		}); // POST 메소드 생략한다
	}
	
	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid(columns);
	});
</script>