<%@page import="net.sf.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
JSONArray nList = (JSONArray) request.getAttribute("nList");
JSONArray aList = (JSONArray) request.getAttribute("aList");
JSONArray pList = (JSONArray) request.getAttribute("pList");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>
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
									popup(url, 1400, 500);
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
								selectionMode : "multipleCells",
								enableSorting : false
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
					<div id="_grid_wrap_" style="height: 786px; border-top: 1px solid #3180c3;"></div>
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
									const url = getCallUrl("/workspace/lineView?oid=" + oid + "&columnType=COLUMN_APPROVAL&poid=" + item.poid);
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
								selectionMode : "multipleCells",
								enableSorting : false
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
					<div id="_grid_wrap" style="height: 412px; border-top: 1px solid #3180c3;"></div>
					<script type="text/javascript">
						let _myGridID;
						const _columns = [ {
							dataField : "kekNumber",
							headerText : "KEK 작번",
							dataType : "string",
							width : 100,
							renderer : {
								type : "LinkRenderer",
								baseUrl : "javascript",
								jsCallback : function(rowIndex, columnIndex, value, item) {
									const oid = item.oid;
									const url = getCallUrl("/project/view?oid=" + oid);
									popup(url);
								}
							},
						}, {
							dataField : "keNumber",
							headerText : "KE 작번",
							dataType : "string",
							width : 100,
						}, {
							dataField : "mak",
							headerText : "막종",
							dataType : "string",
							width : 100,
						}, {
							dataField : "detail",
							headerText : "막종상세",
							dataType : "string",
							width : 100,
						}, {
							dataField : "customer",
							headerText : "고객사",
							dataType : "string",
							width : 100,
						}, {
							dataField : "install",
							headerText : "설치장소",
							dataType : "string",
							width : 100,
						}, {
							dataField : "description",
							headerText : "작업내용",
							dataType : "string",
							style : "aui-left"
						} ]

						function _createAUIGrid(columnLayout) {
							const props = {
								headerHeight : 30,
								showRowNumColumn : true,
								rowNumHeaderText : "번호",
								showAutoNoDataMessage : false,
								selectionMode : "multipleCells",
								enableSorting : false
							};
							_myGridID = AUIGrid.create("#_grid_wrap", columnLayout, props);
							AUIGrid.setGridData(_myGridID,
					<%=pList%>
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

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID);
				AUIGrid.resize(_myGridID);
				AUIGrid.resize(_myGridID_);
			});
		</script>
	</form>
</body>
</html>