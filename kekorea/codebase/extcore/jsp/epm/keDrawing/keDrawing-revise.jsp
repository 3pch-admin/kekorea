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
	const data = window.datas;
	console.log(data);
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
		style : "left indent10",
		editable : false
	}, {
		dataField : "keNumber",
		headerText : "DWG NO (개정전)",
		dataType : "string",
		width : 140,
		editable : false
	}, {
		dataField : "keNumberNext",
		headerText : "DWG NO (개정후)",
		dataType : "string",
		width : 140,
		editable : false
	}, {
		dataField : "version",
		headerText : "버전(개정전)",
		dataType : "numeric",
		width : 80,
		formatString : "###0",
		editable : false,
	}, {
		dataField : "next",
		headerText : "버전(개정후)",
		dataType : "numeric",
		width : 80,
		formatString : "###0",
		editable : false,
	}, {
		dataField : "note",
		headerText : "개정사유",
		dataType : "string",
	}, {
		dataField : "primary",
		headerText : "개정도면",
		dataType : "string",
		width : 100,
		renderer : {
			type : "TemplateRenderer",
		},
	}, {
		width : 100,
		editable : false,
		renderer : {
			type : "ButtonRenderer",
			labelText : "파일선택",
			onclick : function(rowIndex, columnIndex, value, item) {
				recentGridItem = item
				const _$uid = item._$uid; // ... oid 가 있는데 이상하게 안받아와짐.
				const url = getCallUrl("/aui/primary?oid=" + _$uid + "&method=attach");
				popup(url, 1000, 200);
			}
		}
	}, ]

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
		const name = data.name;
		if (name.length !== 17) {
			alert("도면파일 이름명을 체크하세요. \nDWG NO : 9자리, 버전 3자리의 양식을 맞춰주세요.");
			return false;
		}

		const start = name.indexOf("-");
		if (start <= -1) {
			alert("도면파일 이름의 양식이 맞지 않습니다.\nDWG NO-버전 형태의 파일명만 허용됩니다.");
			return false;
		}

		const end = name.lastIndexOf(".");
		if (end <= -1) {
			alert("도면파일 확장자를 체크해주세요.");
			return false;
		}

		const ext = name.substring(end + 1);
		if (ext.toLowerCase() !== "pdf") {
			alert("PDF 파일 형식의 도면파일만 허용됩니다.");
			return false;
		}

		const number = name.substring(0, start);
		if (number.length !== 9) {
			alert("도면파일의 DWG NO의 자리수를 확인해주세요. 등록가능한 도번의 자리수는 9자리여야 합니다.");
			return false;
		}

		const next = name.substring(start + 1, end);
		if (next.length !== 3) {
			alert("도면파일의 버전 자리수를 확인해주세요. 등록가능한 버전의 자리수는 3자리여야 합니다.");
			return false;
		}

		const template = "<img src='" + data.icon + "' style='position: relative; top: 2px;'>";
		AUIGrid.updateRowsById(myGridID, {
			_$uid : recentGridItem._$uid,
			keNumberNext : number,
			next : Number(next),
			file : name,
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
			const keNumberNext = item.keNumberNext;
			const keNumber = item.keNumber;

			if (isNull(item.primary)) {
				AUIGrid.showToastMessage(myGridID, i, 7, "개정도면을 선택하세요.");
				return false;
			}

			if (keNumberNext !== keNumber) {
				AUIGrid.showToastMessage(myGridID, i, 3, "개정전/후의 도번이 일치 하지 않습니다.");
				return false;
			}

			if (version >= next) {
				AUIGrid.showToastMessage(myGridID, i, 5, "개정후 도면의 버전이 개정전 도면의 버전과 같거나 혹은 더 낮습니다.");
				return false;
			}

			if (isNull(item.note)) {
				AUIGrid.showToastMessage(myGridID, i, 6, "개정사유를 입력하세요.");
				return false;
			}

		}

		if (!confirm("개정 하시겠습니까?")) {
			return false;
		}

		const params = new Object();
		const url = getCallUrl("/keDrawing/revise");
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