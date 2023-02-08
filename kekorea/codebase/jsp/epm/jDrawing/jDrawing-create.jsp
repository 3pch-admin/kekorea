<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<CommonCode> customers = (ArrayList<CommonCode>) request.getAttribute("customers");
ArrayList<CommonCode> projectTypes = (ArrayList<CommonCode>) request.getAttribute("projectTypes");
ArrayList<CommonCode> installs = (ArrayList<CommonCode>) request.getAttribute("installs");
%>
<!-- auigrid -->
<%@include file="/jsp/include/auigrid.jsp"%>
<table class="btn_table">
	<tr>
		<td>
			<div class="header_title">
				<i class="axi axi-subtitles"></i>
				<span>KE도면 등록</span>
			</div>
		</td>
		<td class="right">
			<input type="button" value="추가" class="redBtn" id="addRowBtn" title="추가">
			<input type="button" value="삭제" class="orangeBtn" id="deleteRowBtn" title="삭제">
			<input type="button" value="등록" id="createBtn" title="등록">
			<input type="button" value="닫기" id="closeBtn" title="닫기" class="blueBtn">
		</td>
	</tr>
</table>

<div id="grid_wrap" style="height: 490px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
	let myGridID;
	let recentGridItem;
	const columns = [ {
		dataField : "name",
		headerText : "DRAWING TITLE",
		dataType : "string",
	}, {
		dataField : "number",
		headerText : "DWG. NO",
		dataType : "string",
		width : 200,
		editable : false
	}, {
		dataField : "lot",
		headerText : "LOT",
		dataType : "string",
	}, {
		dataField : "version",
		headerText : "버전",
		dataType : "numeric",
		formatString : "###0",
		editRenderer : {
			type : "InputEditRenderer",
			onlyNumeric : true, // 0~9만 입력가능
		},
		width : 100,
		editable : false
	}, {
		dataField : "file",
		headerText : "도면파일",
		width : 130,
		editable : false,
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
				let url = getCallUrl("/aui/primary?method=attach");
				popup(url, 1000, 200);
			}
		}
	}, {
		dataField : "primaryPath",
		headerText : "",
		dataType : "string",
		visible : false
	} ]

	function createAUIGrid(columnLayout) {
		const props = {
			rowIdField : "rowId",
			headerHeight : 30,
			rowHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showRowCheckColumn : true, // 체크 박스 출력,
			fillColumnSizeMode : true,
			editable : true,
			showStateColumn : true,
		};

		myGridID = AUIGrid.create("#grid_wrap", columns, props);
		AUIGrid.bind(myGridID, "addRowFinish", auiAddRowHandler);
	}

	function auiAddRowHandler(event) {
		let selected = AUIGrid.getSelectedIndex(myGridID);
		if (selected.length <= 0) {
			return;
		}

		let rowIndex = selected[0];
		let colIndex = AUIGrid.getColumnIndexByDataField(myGridID, "name");
		AUIGrid.setSelectionByIndex(myGridID, rowIndex, colIndex); // ISBN 으로 선택자 이동
		AUIGrid.openInputer(myGridID);
	}

	function attach(data) {
		let name = data.name;
		let start = name.indexOf("-");
		let end = name.lastIndexOf(".");
		let number = name.substring(0, start);
		let version = name.substring(start + 1, end);
		AUIGrid.updateRowsById(myGridID, {
			rowId : recentGridItem.rowId,
			number : number,
			version : Number(version),
			file : name,
			primaryPath : data.fullPath
		});
	}

	$(function() {
		createAUIGrid(columns);

		$("#closeBtn").click(function() {
			self.close();
		})

		$("#addRowBtn").click(function() {
			let item = new Object();
			AUIGrid.addRow(myGridID, item, "first");
		})

		$("#deleteRowBtn").click(function() {
			let checkedItems = AUIGrid.getCheckedRowItems(myGridID);
			for (let i = checkedItems.length - 1; i >= 0; i--) {
				let rowIndex = checkedItems[i].rowIndex;
				AUIGrid.removeRow(myGridID, rowIndex);
			}
		})

		$("#createBtn").click(function() {
			let url = getCallUrl("/jDrawing/create");
			let addRows = AUIGrid.getAddedRowItems(myGridID);
			let params = new Object();
			params.addRows = addRows;
			openLayer();
			call(url, params, function(data) {
				alert(data.msg);
				if (data.result) {
					opener.loadGridData();
					self.close();
				} else {
					close();
				}
			}, "POST");
		})
	})
</script>