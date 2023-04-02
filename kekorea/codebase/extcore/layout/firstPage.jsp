<%@page import="net.sf.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
JSONArray nList = (JSONArray) request.getAttribute("nList");
JSONArray aList = (JSONArray) request.getAttribute("aList");
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
		<table>
			<colgroup>
				<col width="49%">
				<col width="10">
				<col width="49%">
			</colgroup>
			<tr>
				<td>
					<div class="info-header">
						<img src="/Windchill/extcore/images/header.png">
						공지사항
					</div>
				</td>
				<td>&nbsp;</td>
				<td>
					<div class="info-header">
						<img src="/Windchill/extcore/images/header.png">
						결재 및 검토 리스트
					</div>
				</td>
			</tr>
		</table>

		<table>
			<colgroup>
				<col width="49%">
				<col width="10">
				<col width="49%">
			</colgroup>
			<tr>
				<td valign="top">
					<div id="grid_wrap" style="height: 340px; border-top: 1px solid #3180c3;"></div>
					<script type="text/javascript">
						let myGridID;
						const columns = [ {
							dataField : "name",
							headerText : "공지사항 제목",
							dataType : "string",
							style : "aui-left",
							renderer : {
								type : "LinkRenderer",
								baseUrl : "javascript",
								jsCallback : function(rowIndex, columnIndex, value, item) {
									const oid = item.oid;
									const url = getCallUrl("/notice/view?oid=" + oid);
									popup(url, 1400, 600);
								}
							},
						}, {
							dataField : "createdDate_txt",
							headerText : "작성일",
							dataType : "string",
							width : 150,
						} ]

						function createAUIGrid(columnLayout) {
							const props = {
								headerHeight : 30,
								showRowNumColumn : true,
								rowNumHeaderText : "번호",
								showAutoNoDataMessage : false,
								selectionMode : "singleRow",
							};
							myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
							AUIGrid.setGridData(myGridID,
					<%=nList%>
						);
						}
					</script>
				</td>
				<td>&nbsp;</td>
				<td valign="top" rowspan="3">
					<div id="_grid_wrap_" style="height: 756px; border-top: 1px solid #3180c3;"></div>
					<script type="text/javascript">
						let _myGridID_;
						const _columns_ = [ {
							dataField : "name",
							headerText : "결재 제목",
							dataType : "string",
							style : "aui-left",
							renderer : {
								type : "LinkRenderer",
								baseUrl : "javascript",
								jsCallback : function(rowIndex, columnIndex, value, item) {
									const oid = item.oid;
									const url = getCallUrl("/notice/view?oid=" + oid);
									popup(url, 1400, 600);
								}
							},
						}, {
							dataField : "createdDate_txt",
							headerText : "수신일",
							dataType : "string",
							width : 150,
						} ]

						function _createAUIGrid_(columnLayout) {
							const props = {
								headerHeight : 30,
								showRowNumColumn : true,
								rowNumHeaderText : "번호",
								showAutoNoDataMessage : false,
								selectionMode : "singleRow",
							};
							_myGridID_ = AUIGrid.create("#_grid_wrap_", columnLayout, props);
							AUIGrid.setGridData(_myGridID_,
					<%=aList%>
						);
						}
					</script>
				</td>
			</tr>
			<tr>
				<td colspan="3" valign="top">
					<div class="info-header">
						<img src="/Windchill/extcore/images/header.png">
						나의 작번 리스트
					</div>
				</td>
			</tr>
			<tr>
				<td valign="top">
					<div id="_grid_wrap" style="height: 380px; border-top: 1px solid #3180c3;"></div>
					<script type="text/javascript">
						let _myGridID;
						const _columns = [ {
							dataField : "name",
							headerText : "공지사항 제목",
							dataType : "string",
							style : "aui-left",
							renderer : {
								type : "LinkRenderer",
								baseUrl : "javascript",
								jsCallback : function(rowIndex, columnIndex, value, item) {
									const oid = item.oid;
									const url = getCallUrl("/notice/view?oid=" + oid);
									popup(url, 1400, 600);
								}
							},
						}, {
							dataField : "createdDate_txt",
							headerText : "작성일",
							dataType : "string",
							width : 150,
						} ]

						function _createAUIGrid(columnLayout) {
							const props = {
								headerHeight : 30,
								showRowNumColumn : true,
								rowNumHeaderText : "번호",
								showAutoNoDataMessage : false,
								selectionMode : "singleRow",
							};
							_myGridID = AUIGrid.create("#_grid_wrap", columnLayout, props);
							AUIGrid.setGridData(_myGridID,
					<%=nList%>
						);
						}
					</script>
				</td>
			</tr>
		</table>
		<script type="text/javascript">
			document.addEventListener("DOMContentLoaded", function() {
				createAUIGrid(columns);
				_createAUIGrid(_columns);
				_createAUIGrid_(_columns_);
			});
		</script>
	</form>
</body>
</html>