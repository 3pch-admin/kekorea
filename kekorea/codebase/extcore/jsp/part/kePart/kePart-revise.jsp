<%@page import="org.json.JSONArray"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>    
<table class="button-table">
	<tr>
		<td class="right">
			<input type="button" value="개정" title="개정" onclick="revise();">
			<input type="button" value="행 삭제" title="행 삭제" class="red" onclick="remove();">
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
		width : 80,
		formatString : "###0",
		editable : false
	}, {
		dataField : "code",
		headerText : "중간코드",
		dataType : "string",
		width : 100,
		editable : false
	}, {
		dataField : "keNumber",
		headerText : "부품번호",
		dataType : "string",
		width : 100,
		editable : false
	}, {
		dataField : "name",
		headerText : "부품명",
		dataType : "string",
		width : 200,
		editable : false
	}, {
		dataField : "model",
		headerText : "KokusaiModel",
		dataType : "string",
		width : 300,
		editable : false
	}, {
		dataField : "version",
		headerText : "버전(개정전)",
		dataType : "numeric",
		formatString : "###0",
		width : 80,
		editable : false
	}, {
		dataField : "next",
		headerText : "버전(개정후)",
		dataType : "numeric",
		formatString : "###0",
		width : 80,
		editRenderer : {
			type : "InputEditRenderer",
			onlyNumeric : true, 		
		}
	}, {
		dataField : "state",
		headerText : "상태",
		dataType : "string",
		width : 100,
		editable : false
	}, {
		dataField : "note",
		headerText : "개정사유",
		dateType : "string",
		style : "aui-left",
	}, {
		dataField : "primary",
		headerText : "첨부파일",
		dataType : "string",
		width : 100,
		renderer : {
			type : "TemplateRenderer",
		},
		editable : false
	}, {
		width : 100,
		renderer : {
			type : "ButtonRenderer",
			labelText : "파일선택",
			onclick : function(rowIndex, columnIndex, value, item) {
				recentGridItem = item;
				const _$uid = item._$uid;
				const url = getCallUrl("/aui/primary?oid=" + _$uid + "&method=attach");
				popup(url, 1000, 200);
			}
		},
		editable : false
	} ]
	function createAUIGrid(columnLayout) {
		const props = {
			headerHeight : 30, 
			showRowNumColumn : true, 
			showStateColumn : true, 
			rowNumHeaderText : "번호", 
			selectionMode : "multipleCells",
			showRowCheckColumn : true,
			editable : true
		};
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		readyHandler();
	}

	function readyHandler() {
		for (let i = 0; i < data.length; i++) {
			AUIGrid.addRow(myGridID, data[i].item, "last");
		}
	}

	function attach(data) {
		const template = "<img src='" + data.icon + "' style='position: relative; top: 2px;'>";
		AUIGrid.updateRowsById(myGridID, {
			_$uid : recentGridItem._$uid,
			primary : template,
			cacheId : data.cacheId
		});
	}

	function remove() {
		const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
		for (let i = checkedItems.length - 1; i >= 0; i--) {
			const rowIndex = checkedItems[i].rowIndex;
			AUIGrid.removeRow(myGridID, rowIndex);
		}
	}

	function revise() {

		const addRows = AUIGrid.getAddedRowItems(myGridID);
		for (let i = 0; i < addRows.length; i++) {
			const item = addRows[i];
			const version = item.version;
			const next = item.next;

			if (version >= next) {
				AUIGrid.showToastMessage(myGridID, i, 6, "개정후 부품의 버전이 개정전 부품의 버전과 같거나 혹은 더 낮습니다.");
				return false;
			}

			if (isNull(item.note)) {
				AUIGrid.showToastMessage(myGridID, i, 8, "개정사유를 입력하세요.");
				return false;
			}
		}

		if (!confirm("개정 하시겠습니까?")) {
			return false;
		}

		const params = new Object();
		const url = getCallUrl("/kePart/revise");
		params.addRows = addRows;
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			} else {
				closeLayer();
			}
		}); 
	}

	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid(columns);
		AUIGrid.resize(myGridID);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
	});
</script>