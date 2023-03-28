<%@page import="org.json.JSONArray"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
JSONArray dataList = (JSONArray) request.getAttribute("dataList");
JSONArray _dataList = (JSONArray) request.getAttribute("_dataList");
%>
<%@include file="/extcore/include/auigrid.jsp"%>
<style type="text/css">
.compare {
	background-color: yellow;
	color: red;
	font-weight: bold;
}
</style>
<table class="button-table">
	<tr>
		<td class="left"></td>
		<td class="right">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>

<table>
	<colgroup>
		<col width="49%">
		<col width="15px;">
		<col width="49%">
	</colgroup>
	<tr>
		<td>
			<div id="grid_wrap" style="height: 705px; border-top: 1px solid #3180c3;"></div>
			<script type="text/javascript">
				let myGridID;
				const columns = [ {
					dataField : "preView",
					headerText : "미리보기",
					width : 80,
					editable : false,
					renderer : {
						type : "ImageRenderer",
						altField : null,
						imgHeight : 34,
					},
				}, {
					dataField : "name",
					headerText : "DRAWING TITLE",
					dataType : "string",
					style : "aui-left",
					width : 250,
				}, {
					dataField : "number",
					headerText : "DWG. NO",
					dataType : "string",
					width : 100,
					styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
						const other = AUIGrid.getItemByRowIndex(_myGridID, rowIndex);
						if (!other) {
							return;
						}
						if (value === other.number) {
							return "compare";
						}
						return "";
					}								
				}, {
					dataField : "current",
					headerText : "CURRENT VER",
					dataType : "string",
					width : 80,
				}, {
					dataField : "rev",
					headerText : "REV",
					dataType : "string",
					width : 80,
				}, {
					dataField : "latest",
					headerText : "REV (최신)",
					dataType : "string",
					width : 80,
				}, {
					dataField : "lotNo",
					headerText : "LOT",
					dataType : "numeric",
					width : 80,
					formatString : "###0",
				}, {
					dataField : "createdData_txt",
					headerText : "등록일",
					dataType : "string",
					width : 100,
				}, {
					dataField : "note",
					headerText : "NOTE",
					dataType : "string",
					width : 250,
				} ]

				function createAUIGrid(columnLayout) {
					const props = {
						headerHeight : 30,
						rowHeight : 30,
						showStateColumn : true,
						selectionMode : "multipleCells",
						rowNumHeaderText : "번호",
						showRowCheckColumn : true,
					};
					myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
					AUIGrid.setGridData(myGridID,
			<%=dataList%>
				);
					AUIGrid.bind(myGridID, "hScrollChange", function(event) {
						AUIGrid.setHScrollPositionByPx(_myGridID, event.position);
					});

					AUIGrid.bind(myGridID, "vScrollChange", function(event) {
						AUIGrid.setRowPosition(_myGridID, event.position);
					});
				}
			</script>
		</td>
		<td>&nbsp;</td>
		<td>
			<div id="_grid_wrap" style="height: 705px; border-top: 1px solid #3180c3;"></div>
			<script type="text/javascript">
				let _myGridID;
				const _columns = [ {
					dataField : "preView",
					headerText : "미리보기",
					width : 80,
					editable : false,
					renderer : {
						type : "ImageRenderer",
						altField : null,
						imgHeight : 34,
					},
				}, {
					dataField : "name",
					headerText : "DRAWING TITLE",
					dataType : "string",
					style : "aui-left",
					width : 250
				}, {
					dataField : "number",
					headerText : "DWG. NO",
					dataType : "string",
					width : 100,
					styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
						const other = AUIGrid.getItemByRowIndex(myGridID, rowIndex);
						if (!other) {
							return;
						}
						if (value !== other.number) {
							return "compare";
						}

						return "";
					}					
				}, {
					dataField : "current",
					headerText : "CURRENT VER",
					dataType : "string",
					width : 80,
				}, {
					dataField : "rev",
					headerText : "REV",
					dataType : "string",
					width : 80,
				}, {
					dataField : "latest",
					headerText : "REV (최신)",
					dataType : "string",
					width : 80,
				}, {
					dataField : "lotNo",
					headerText : "LOT",
					dataType : "numeric",
					width : 80,
					formatString : "###0",
				}, {
					dataField : "createdData_txt",
					headerText : "등록일",
					dataType : "string",
					width : 100,
				}, {
					dataField : "note",
					headerText : "NOTE",
					dataType : "string",
					width : 250,
				} ]

				function _createAUIGrid(columnLayout) {
					const props = {
						headerHeight : 30,
						rowHeight : 30,
						showStateColumn : true,
						selectionMode : "multipleCells",
						rowNumHeaderText : "번호",
						showRowCheckColumn : true,
					};
					_myGridID = AUIGrid.create("#_grid_wrap", columnLayout, props);
					AUIGrid.setGridData(_myGridID, <%=_dataList%>);
					
					AUIGrid.bind(_myGridID, "hScrollChange", function(event) {
						AUIGrid.setHScrollPositionByPx(myGridID, event.position);
					});

					AUIGrid.bind(_myGridID, "vScrollChange", function(event) {
						AUIGrid.setRowPosition(myGridID, event.position);
					});
				}
			</script>
		</td>
	</tr>
</table>
<script type="text/javascript">
	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid(columns);
		AUIGrid.resize(myGridID);		
		_createAUIGrid(_columns);
		AUIGrid.resize(_myGridID);
		AUIGrid.refresh(myGridID);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
		AUIGrid.resize(_myGridID);
	});
</script>