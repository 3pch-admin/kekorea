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

<div id="grid_wrap" style="height: 325px; border-top: 1px solid #3180c3;"></div>
<br>
<table class="create_table">
	<colgroup>
		<col width="130">
		<col width="*">
	</colgroup>
	<tr>
		<th>첨부파일</th>
		<td colspan="1">
			<!-- upload.js see -->
			<div class="AXUpload5" id="allUpload_layer"></div>
			<div class="AXUpload5QueueBox_list" id="uploadQueueBox" style="height: 300px;"></div>
		</td>
	</tr>
</table>
<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "name",
		headerText : "DRAWING TITLE",
		dataType : "string",
	}, {
		dataField : "number",
		headerText : "DWG. NO",
		dataType : "string",
		width : 200,
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
		width : 100
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
			editable : true
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
		let colIndex = AUIGrid.getColumnIndexByDataField(myGridID, "numberr");
		AUIGrid.setSelectionByIndex(myGridID, rowIndex, colIndex); // ISBN 으로 선택자 이동
		AUIGrid.openInputer(myGridID);
	}

	$(function() {
		createAUIGrid(columns);
		upload.pageStart(null, null, "all");

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
			let arr = new Array();
			let dd = $("input[name*=allContent]");
			let gridData = AUIGrid.getGridData(myGridID);
			if (dd.length != gridData.length) {
				alert("첨부파일 개수와 그리드의 데이터 개수를 확인 하세요.");
				return false;
			}

			for (let i = 0; i < dd.length; i++) {
				arr.push(dd[i].value.split("&")[0]);
			}

			let url = getCallUrl("/jDrawing/create");
			let addRows = AUIGrid.getAddedRowItems(myGridID);
			let params = new Object();
			params.arr = arr;
			params.addRows = addRows;
			console.log(params);
			open();
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