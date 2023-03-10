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
				<span>KEK 도번 개정</span>
			</div>
		</td>
		<td class="right">
			<input type="button" value="삭제" class="orangeBtn" id="deleteRowBtn" title="삭제">
			<input type="button" value="개정" id="createBtn" title="개정">
			<input type="button" value="닫기" id="closeBtn" title="닫기" class="blueBtn">
		</td>
	</tr>
</table>

<div id="grid_wrap" style="height: 490px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
	const data = window.list;
	let myGridID;
	const columns = [ {
		dataField : "number",
		headerText : "도면번호",
		dataType : "string",
		editable : false,
		width : 120
	}, {
		dataField : "name",
		headerText : "도면명",
		dataType : "string",
		width : 200,
		editable : false
	}, {
		dataField : "businessSector",
		headerText : "사업부문",
		dataType : "string",
		width : 200,
		editable : false
	}, {
		dataField : "drawingCompany",
		headerText : "도면생성회사",
		dataType : "string",
		width : 150,
		editable : false
	}, {
		dataField : "department",
		headerText : "작성부서구분",
		dataType : "string",
		width : 150,
		editable : false
	}, {
		dataField : "document",
		headerText : "작성문서구분",
		dataType : "string",
		width : 150,
		editable : false
	}, {
		dataField : "version",
		headerText : "버전",
		dataType : "numeric",
		width : 100,
		editRenderer : {
			type : "InputEditRenderer",
			onlyNumeric : true, // 0~9만 입력가능
		},
	}, {
		dataField : "note",
		headerText : "개정사유",
		dataType : "string",
	}, {
		dataField : "oid",
		headerText : "oid",
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
		for (let i = 0; i < data.length; i++) {
			AUIGrid.addRow(myGridID, data[i].item, "last");
		}
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

		$("#createBtn").click(function() {
			let addRows = AUIGrid.getAddedRowItems(myGridID);
			let params = new Object();
			let url = getCallUrl("/numberRule/revise");
			params.addRows = addRows;
			openLayer();
			console.log(params);
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