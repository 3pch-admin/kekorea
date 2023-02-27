<%@page import="org.json.JSONArray"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- auigrid -->
<%@include file="/jsp/include/auigrid.jsp"%>
<table class="btn_table">
	<tr>
		<td>
			<div class="header_title">
				<i class="axi axi-subtitles"></i>
				<span>KE 도면 개정</span>
			</div>
		</td>
		<td class="right">
			<input type="button" value="삭제" class="orangeBtn" id="deleteRowBtn" title="삭제">
			<input type="button" value="개정" id="reviseBtn" title="개정">
			<input type="button" value="닫기" id="closeBtn" title="닫기" class="blueBtn">
		</td>
	</tr>
</table>

<div id="grid_wrap" style="height: 490px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
	const data = window.list;
	console.log(data);
	let myGridID;
	const columns = [ {
		dataField : "lotNo",
		headerText : "LOT",
		dataType : "numeric",
		width : 100,
		formatString : "###0",
	}, {
		dataField : "name",
		headerText : "DRAWING TITLE",
		dataType : "string",
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
		editable : false,
		formatString : "###0",
	}, {
		dataField : "next",
		headerText : "개정버전",
		dataType : "numeric",
		width : 80,
		editable : false,
		formatString : "###0",
	}, {
		dataField : "note",
		headerText : "개정사유",
		dataType : "string",
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
				let oid = item.rowId;
				let url = getCallUrl("/aui/primary?oid=" + oid + "&method=attach");
				popup(url, 1000, 200);
			}
		}
	}, {
		dataField : "oid",
		headerText : "oid",
		dataType : "string",
		visible : false
	}, {
		dataField : "primaryPath",
		headerText : "",
		dataType : "string",
		visible : false
	}, ]

	function createAUIGrid(columnLayout) {
		const props = {
			rowIdField : "rowId",
			// 공통 시작
			headerHeight : 30, // 헤더높이
			rowHeight : 30, // 행 높이
			showRowNumColumn : true, // 번호 행 출력 여부
			showStateColumn : true, // 상태표시 행 출력 여부
			rowNumHeaderText : "번호", // 번호 행 텍스트 설정
			noDataMessage : "검색 결과가 없습니다.", // 데이터 없을시 출력할 내용
			enableFilter : true, // 필터 사용 여부
			showRowCheckColumn : true, // 엑스트라 체크 박스 사용 여부
			selectionMode : "multiCells",
			// 공통 끝
			fillColumnSizeMode : true,
			editable : true,
		};
		myGridID = AUIGrid.create("#grid_wrap", columns, props);
		for (let i = 0; i < data.length; i++) {
			AUIGrid.addRow(myGridID, data[i].item, "last");
		}
	}

	let recentGridItem = null;
	function attach(data) {
		console.log(data);
		let name = data.name;
		let start = name.indexOf("-");
		let end = name.lastIndexOf(".");
		let number = name.substring(0, start);
		let next = name.substring(start + 1, end);
		let template = "<img src='" + data.icon + "'>";
		AUIGrid.updateRowsById(myGridID, {
			rowId : recentGridItem.rowId,
			number : number,
			next : Number(next),
			file : name,
			primary : template,
			primaryPath : data.fullPath
		});
	}

	$(function() {
		createAUIGrid(columns);

		$("#closeBtn").click(function() {
			self.close();
		})

		$("#deleteRowBtn").click(function() {
			let checkedItems = AUIGrid.getCheckedRowItems(myGridID);
			for (let i = checkedItems.length - 1; i >= 0; i--) {
				let rowIndex = checkedItems[i].rowIndex;
				AUIGrid.removeRow(myGridID, rowIndex);
			}
		})

		$("#reviseBtn").click(function() {
			let addRows = AUIGrid.getAddedRowItems(myGridID);
			let params = new Object();
			let url = getCallUrl("/keDrawing/revise");
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
			}, "POST");
		})
	})
</script>