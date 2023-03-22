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
			onlyNumeric : true, // 0~9만 입력가능		
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
			headerHeight : 30, // 헤더높이
			rowHeight : 30, // 행 높이
			showRowNumColumn : true, // 번호 행 출력 여부
			showStateColumn : true, // 상태표시 행 출력 여부
			rowNumHeaderText : "번호", // 번호 행 텍스트 설정
			selectionMode : "multipleCells",
			showRowCheckColumn : true,
			editable : true
		};
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		readyHandler();
	}

	function readyHandler() {
		// 화면에서 받아온 데이터 그리드로 추가
		for (let i = 0; i < data.length; i++) {
			data[i].item = {
				primary : "",
				note : ""
			}
			AUIGrid.addRow(myGridID, data[i].item, "last");
		}
	}

	function attach(data) {
		const template = "<img src='" + data.icon + "' style='position: relative; top: 2px;'>";
		AUIGrid.updateRowsById(myGridID, {
			_$uid : recentGridItem._$uid,
			primary : template,
			primaryPath : data.fullPath
		});
	}

	// 그리드 행 삭제
	function remove() {
		const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
		for (let i = checkedItems.length - 1; i >= 0; i--) {
			let rowIndex = checkedItems[i].rowIndex;
			AUIGrid.removeRow(myGridID, rowIndex);
		}
	}

	// 개정
	function revise() {

		const addRows = AUIGrid.getAddedRowItems(myGridID);
		for (let i = 0; i < addRows.length; i++) {
			const item = addRows[i];
			const version = item.version;
			const next = item.next;

			if (isNull(item.primary)) {
				AUIGrid.showToastMessage(myGridID, i, 9, "첨부 파일을 선택하세요.");
				return false;
			}

			if (version >= next) {
				AUIGrid.showToastMessage(myGridID, i, 6, "개정후 부품의 버전이 개정전 부품의 버전과 같거나 혹은 더 낮습니다.");
				return false;
			}

			if (isNull(item.note)) {
				AUIGrid.showToastMessage(myGridID, i, 8, "개정사유를 입력하세요.");
				return false;
			}

			if (isNull(item.primary)) {
				AUIGrid.showToastMessage(myGridID, i, 8, "첨부파일 선택하세요.");
				return false;
			}
		}

		if (!confirm("개정 하시겠습니까?")) {
			return false;
		}

		const params = new Object();
		const url = getCallUrl("/kePart/revise");
		params.addRows = addRows;
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			}
		}); // POST 메소드 생략한다
	}

	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid(columns);
		AUIGrid.resize(myGridID);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
	});
</script>