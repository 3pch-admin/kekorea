<%@page import="e3ps.admin.commonCode.CommonCodeType"%>
<%@page import="org.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/jsp/common/layouts/include_css.jsp"%>
<%@include file="/jsp/common/layouts/include_script.jsp"%>
<!-- auigrid -->
<%@include file="/jsp/include/auigrid.jsp"%>
</head>
<body>
	<input type="hidden" name="sessionid" id="sessionid">
	<input type="hidden" name="curPage" id="curPage">
	<!-- button table -->
	<table class="btn_table">
		<tr>
			<td class="right">
				<input type="button" value="추가" class="redBtn" id="addRowBtn" title="추가">
				<input type="button" value="삭제" class="orangeBtn" id="deleteRowBtn" title="삭제">
				<input type="button" value="저장" class="" id="saveBtn" title="저장">
				<input type="button" value="조회" class="blueBtn" id="searchBtn" title="조회">
			</td>
		</tr>
	</table>
	<div id="grid_wrap" style="height: 740px; border-top: 1px solid #3180c3;"></div>
</body>
<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "",
		headerText : "아이템 등록",
		dataType : "numeric",
		width : 140,
		cellMerge : true,
		mergeRef : "name",
		mergePolicy : "restrict",
		editable : false,
		renderer : {
			type : "ButtonRenderer",
			labelText : "아이템 등록",
			onClick : function(event) {
				let oid = event.item.oid;
				let url = getCallUrl("/options/create?oid=" + oid);
				popup(url, 1200, 660);
			},
		}
	}, {
		dataField : "name",
		headerText : "사양 명",
		dataType : "string",
		cellMerge : true,
	}, {
		dataField : "version",
		headerText : "버전",
		dataType : "numeric",
		formatString : "###0",
		width : 100,
		editable : false,
		cellMerge : true,
		mergeRef : "name",
		mergePolicy : "restrict"
	}, {
		dataField : "sort",
		headerText : "순서",
		dataType : "numeric",
		formatString : "###0",
		width : 100,
		editRenderer : {
			type : "InputEditRenderer",
			onlyNumeric : true, // 0~9만 입력가능
		},
		cellMerge : true,
		mergeRef : "name",
		mergePolicy : "restrict"
	}, {
		dataField : "enable",
		headerText : "사용여부",
		width : 100,
		renderer : {
			type : "CheckBoxEditRenderer",
			editable : true, // 체크박스 편집 활성화 여부(기본값 : false)
		},
		cellMerge : true,
		mergeRef : "name",
		mergePolicy : "restrict"
	}, {
		dataField : "creator",
		headerText : "작성자",
		dataType : "string",
		width : 100,
		editable : false,
		cellMerge : true,
		mergeRef : "name",
		mergePolicy : "restrict"
	}, {
		dataField : "createdDate",
		headerText : "작성일",
		dataType : "date",
		formatString : "yyyy-mm-dd",
		width : 100,
		editable : false,
		cellMerge : true,
		mergeRef : "name",
		mergePolicy : "restrict"
	}, {
		dataField : "oname",
		headerText : "아이템 명",
		dataType : "string",
		width : 350,
		editable : false,
	}, {
		dataField : "osort",
		headerText : "아이템 정렬 순서",
		dataType : "numeric",
		formatString : "###0",
		width : 130,
		editRenderer : {
			type : "InputEditRenderer",
			onlyNumeric : true, // 0~9만 입력가능
		},
		editable : false,
	}, {
		dataField : "oid",
		headerText : "oid",
		dataType : "string",
		visible : false
	} ]

	function createAUIGrid(columns) {
		const props = {
			headerHeight : 30,
			rowHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showRowCheckColumn : true, // 체크 박스 출력
			editable : true,
			showStateColumn : true,
			enableCellMerge : true,
			cellMergePolicy : "withNull",
			noDataMessage : "검색 결과가 없습니다.",
		};
		myGridID = AUIGrid.create("#grid_wrap", columns, props);
		loadGridData();
		AUIGrid.bind(myGridID, "vScrollChange", vScrollChangeHandler);
		AUIGrid.bind(myGridID, "addRowFinish", auiAddRowHandler);
	}
	
	function auiAddRowHandler(event) {
		let selected = AUIGrid.getSelectedIndex(myGridID);
		if (selected.length <= 0) {
			return;
		}

		let rowIndex = selected[0];
		let colIndex = AUIGrid.getColumnIndexByDataField(myGridID, "name");
		AUIGrid.setSelectionByIndex(myGridID, rowIndex, colIndex);
		AUIGrid.openInputer(myGridID);
	}

	function loadGridData() {
		let params = new Object();
		let url = getCallUrl("/spec/list");
		AUIGrid.showAjaxLoader(myGridID);
		call(url, params, function(data) {
			AUIGrid.removeAjaxLoader(myGridID);
			$("input[name=sessionid]").val(data.sessionid);
			$("input[name=curPage]").val(data.curPage);
			AUIGrid.setGridData(myGridID, data.list);
			parent.close();
		})
	}

	let last = false;
	function vScrollChangeHandler(event) {
		if (event.position == event.maxPosition) {
			if (!last) {
				requestAdditionalData();
			}
		}
	}

	function requestAdditionalData() {
		let params = new Object();
		let curPage = $("input[name=curPage]").val();
		params.sessionid = $("input[name=sessionid]").val();
		params.start = (curPage * 30);
		params.end = (curPage * 30) + 30;
		let url = getCallUrl("/appendData");
		AUIGrid.showAjaxLoader(myGridID);
		call(url, params, function(data) {
			if (data.list.length == 0) {
				last = true;
				alert("마지막 데이터 입니다.");
				AUIGrid.removeAjaxLoader(myGridID);
			} else {
				AUIGrid.appendData(myGridID, data.list);
				AUIGrid.removeAjaxLoader(myGridID);
				$("input[name=curPage]").val(parseInt(curPage) + 1);
			}
		})
	}

	$(function() {

		createAUIGrid(columns);
		$("#searchBtn").click(function() {
			loadGridData();
		})

		// 그리드 행 추가
		$("#addRowBtn").click(function() {
			let item = new Object();
			item.enable = true;
			AUIGrid.addRow(myGridID, item, "first");
		})

		$("#saveBtn").click(function() {
			let addRows = AUIGrid.getAddedRowItems(myGridID);
			let removeRows = AUIGrid.getRemovedItems(myGridID);
			let editRows = AUIGrid.getEditedRowItems(myGridID);
			let params = new Object();
			let url = getCallUrl("/spec/create");
			params.addRows = addRows;
			params.removeRows = removeRows;
			params.editRows = editRows;
			parent.open();
			call(url, params, function(data) {
				alert(data.msg);
				if (data.result) {
					loadGridData();
				} else {
					parent.close();
				}

			}, "POST");
		})

		// 그리드 행 삭제
		$("#deleteRowBtn").click(function() {
			let checkedItems = AUIGrid.getCheckedRowItems(myGridID);
			for (let i = checkedItems.length - 1; i >= 0; i--) {
				let rowIndex = checkedItems[i].rowIndex;
				AUIGrid.removeRow(myGridID, rowIndex);
			}
		})

	}).keypress(function(e) {
		let keyCode = e.keyCode;
		if (keyCode == 13) {
			loadGridData();
		}
	})

	$(window).resize(function() {
		AUIGrid.resize(myGridID);
	})
</script>
</html>