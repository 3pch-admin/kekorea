<%@page import="org.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
JSONArray maks = (JSONArray) request.getAttribute("maks");
%>
<!-- auigrid -->
<%@include file="/jsp/include/auigrid.jsp"%>
<table class="btn_table">
	<tr>
		<td class="right">
			<input type="button" value="추가" class="redBtn" id="addRowBtn" title="추가">
			<input type="button" value="삭제" class="orangeBtn" id="deleteRowBtn" title="삭제">
			<input type="button" value="등록" class="" id="createBtn" title="등록">
			<input type="button" value="닫기" class="blueBtn" id="closeBtn" title="닫기">
		</td>
	</tr>
</table>
<div id="grid_wrap" style="height: 740px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
	let myGridID;
	let maks =
<%=maks%>
	const columns = [ {
		dataField : "item",
		headerText : "항목",
		dataType : "string",
		width : 120
	}, {
		dataField : "improvements",
		headerText : "개선내용",
		dataType : "string",
		width : 300
	}, {
		dataField : "improvement",
		headerText : "개선책",
		dataType : "string",
		width : 300
	}, {
		dataField : "apply",
		headerText : "적용/미적용",
		dataType : "string",
		width : 100,
		renderer : {
			type : "DropDownListRenderer",
			list : [ "적용완료", "일부적용", "미적용", "검토중" ]
		}
	}, {
		dataField : "mak",
		headerText : "대상막종",
		dataType : "string",
		width : 100,
		renderer : {
			type : "DropDownListRenderer",
			list : maks, //key-value Object 로 구성된 리스트
			keyField : "key", // key 에 해당되는 필드명
			valueField : "value" // value 에 해당되는 필드명
		},
		labelFunction : function(rowIndex, columnIndex, value, headerText, item) { // key-value 에서 엑셀 내보내기 할 때 value 로 내보내기 위한 정의
			let retStr = ""; // key 값에 맞는 value 를 찾아 반환함.
			for (let i = 0, len = maks.length; i < len; i++) {
				if (jsonList[i]["key"] == value) {
					retStr = maks[i]["value"];
					break;
				}
			}
			return retStr == "" ? value : retStr;
		}
	}, {
		dataField : "note",
		headerText : "비고",
		dataType : "string",
		width : 150
	}, {
		dataField : "",
		headerText : "미리보기",
		dataType : "string",
		width : 100,
	}, {
		dataField : "",
		headerText : "파일첨부",
		dataType : "",
		width : 100,
	} ]

	function createAUIGrid(columnLayout) {
		const props = {
			rowIdField : "rowId",
			headerHeight : 30,
			rowHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			editable : true,
			fillColumnSizeMode : true,
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
		let colIndex = AUIGrid.getColumnIndexByDataField(myGridID, "item");
		AUIGrid.setSelectionByIndex(myGridID, rowIndex, colIndex); // ISBN 으로 선택자 이동
		AUIGrid.openInputer(myGridID);
	}

	$(function() {

		createAUIGrid(columns);

		$("#addRowBtn").click(function() {
			let item = new Object();
			AUIGrid.addRow(myGridID, item, "first");
		})

		$("#createBtn").click(function() {
			let addRows = AUIGrid.getAddedRowItems(myGridID);
			let params = new Object();
			let url = getCallUrl("/cip/create");
			params.addRows = addRows;
			console.log(params);
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
