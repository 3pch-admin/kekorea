<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.korea.cip.dto.CipDTO"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
JSONArray list = (JSONArray) request.getAttribute("list");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/include/css.jsp"%>
<%@include file="/extcore/include/script.jsp"%>
<%@include file="/extcore/include/auigrid.jsp"%>
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js?v=1010"></script>
</head>
<body>
	<form>
		<table class="button-table">
			<tr>
				<td class="left">
					<input type="button" value="행 추가" title="행 추가" class="blue" onclick="addRow3();">
					<input type="button" value="행 삭제" title="행 삭제" class="red" onclick="deleteRow3();">
					<input type="button" value="저장" title="저장" onclick="save3();">
				</td>
			</tr>
		</table>
		<div id="grid_wrap3" style="height: 740px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID3;
			const columns3 = [ {
				dataField : "name",
				headerText : "특이사항 제목",
				dataType : "string",
				style : "aui-left",
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "description",
				headerText : "설명",
				dataType : "string",
				width : 450,
				style : "aui-left",
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "creator",
				headerText : "작성자",
				dataType : "string",
				width : 100,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "createdDate_txt",
				headerText : "작성일",
				dataType : "string",
				width : 100,
				filter : {
					showIcon : true,
					inline : true,
				},
			}, {
				dataField : "primary",
				headerText : "첨부파일",
				dataType : "string",
				width : 100,
				filter : {
					showIcon : true,
					inline : true,
				},
			}, {
				dataField : "button",
				headerText : "",
				width : 80,
				editable : false,
				renderer : {
					type : "ButtonRenderer",
					labelText : "파일선택",
					onclick : function(rowIndex, columnIndex, value, item) {
						recentGridItem = item;
						const _$uid = item._$uid;
						const url = getCallUrl("/aui/primary?oid=" + _$uid + "&method=attach");
						popup(url, 1000, 300);
					}
				},
				filter : {
					showIcon : false,
					inline : false
				},
			} ]

			function createAUIGrid3(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : true,
					showRowCheckColumn : true,
					showStateColumn : true,
					rowNumHeaderText : "번호",
					showAutoNoDataMessage : false,
					enableFilter : true,
					selectionMode : "singleRow",
					enableMovingColumn : true,
					showInlineFilter : true,
					useContextMenu : true,
					enableRightDownFocus : true,
					filterLayerWidth : 320,
					filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
				};
				myGridID3 = AUIGrid.create("#grid_wrap3", columnLayout, props);
				AUIGrid.setGridData(myGridID3, <%=list%>);
				AUIGrid.bind(myGridID3, "addRowFinish", auiAddRowFinishHandler3);
			}

			function auiAddRowFinishHandler3(event) {
				const selected = AUIGrid.getSelectedIndex(myGridID3);
				if (selected.length <= 0) {
					return false;
				}

				const rowIndex = selected[0];
				const colIndex = AUIGrid.getColumnIndexByDataField(myGridID3, "name");
				AUIGrid.setSelectionByIndex(myGridID3, rowIndex, colIndex);
				AUIGrid.openInputer(myGridID3);
			}

			function addRow3() {
				const item = {};
				AUIGrid.addRow(myGridID3, item, "first");
			}

			function deleteRow3() {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID3);
				for (let i = checkedItems.length - 1; i >= 0; i--) {
					const item = checkedItems[i].item;
					const rowIndex = checkedItems[i].rowIndex;
					AUIGrid.removeRow(myGridID3, rowIndex);
				}
			}

			document.addEventListener("DOMContentLoaded", function() {
				createAUIGrid3(columns3);
				AUIGrid.resize(myGridID3);
			})

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID3);
			});
		</script>
	</form>
</body>
</html>