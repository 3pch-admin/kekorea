<%@page import="e3ps.org.service.OrgHelper"%>
<%@page import="wt.epm.EPMDocumentType"%>
<%@page import="e3ps.epm.service.EpmHelper"%>
<%@page import="e3ps.part.service.PartHelper"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="e3ps.common.util.ColumnUtils"%>
<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@page import="wt.fc.PagingQueryResult"%>
<%@page import="e3ps.common.util.PageQueryUtils"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
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
		<input type="hidden" name="isAdmin" id="isAdmin" value="<%=isAdmin%>">
		<input type="hidden" name="sessionName" id="sessionName" value="<%=sessionUser.getFullName()%>">
		<input type="hidden" name="sessionId" id="sessionId" value="<%=sessionUser.getName()%>">
		<input type="hidden" name="sessionid" id="sessionid">
		<input type="hidden" name="curPage" id="curPage">
		<table class="search-table">
			<colgroup>
				<col width="130">
				<col width="*">
				<col width="130">
				<col width="*">
				<col width="130">
				<col width="*">
				<col width="130">
				<col width="*">
			</colgroup>
			<tr>
				<th>ë¶í ë¶ë¥</th>
				<td colspan="7" class="indent5">
					<input type="hidden" name="location" value="">
					<span id="location">defaultttttttt</span>
				</td>
			</tr>
			<tr>
				<th>íì¼ ì´ë¦</th>
				<td class="indent5">
					<input type="text" name="partCode">
				</td>
				<th>íë²</th>
				<td class="indent5">
					<input type="text" name="partName">
				</td>
				<th>íëª</th>
				<td class="indent5">
					<input type="text" name="number">
				</td>
				<th>ê·ê²©</th>
				<td class="indent5">
					<input type="text" name="number">
				</td>
			</tr>
			<tr>
				<th>MATERIAL</th>
				<td class="indent5">
					<input type="text" name="number">
				</td>
				<th>REMARK</th>
				<td class="indent5">
					<input type="text" name="number">
				</td>
				<th>REFERENCE ëë©´</th>
				<td colspan="3" class="indent5">
					<input type="text" name="number">
				</td>
			</tr>
			<tr>
				<th>ìì±ì</th>
				<td class="indent5">
					<input type="text" name="creator" id="creator">
				</td>
				<th>ìì±ì¼</th>
				<td class="indent5">
					<input type="text" name="createdFrom" id="createdFrom" class="width-100">
					~
					<input type="text" name="createdTo" id="createdTo" class="width-100">
				</td>
				<th>ìì ì</th>
				<td class="indent5">
					<input type="text" name="modifier" id="modifier">
				</td>
				<th>ìì ì¼</th>
				<td class="indent5">
					<input type="text" name="modifiedFrom" id="modifiedFrom" class="width-100">
					~
					<input type="text" name="modifiedTo" id="modifiedTo" class="width-100">
				</td>
			</tr>
			<tr>
				<th>ìí</th>
				<td class="indent5">
					<select name="template" id="template" class="width-200">
						<option value="">ì í</option>
					</select>
				</td>
				<th>ë²ì </th>
				<td colspan="5">
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="latest" value="true" checked="checked">
						<div class="state p-success">
							<label>
								<b>최신버전</b>
							</label>
						</div>
					</div>
					&nbsp;
					<div class="pretty p-switch">
						<input type="radio" name="latest" value="">
						<div class="state p-success">
							<label>
								<b>모든버전</b>
							</label>
						</div>
					</div>
				</td>
			</tr>
		</table>

		<table class="button-table">
			<tr>
				<td class="left">
					<img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="ìì ë¤ì´ë¡ë" onclick="exportExcel();">
					<img src="/Windchill/extcore/images/save.gif" title="íì´ë¸ ì ì¥" onclick="saveColumnLayout('epm-list');">
					<img src="/Windchill/extcore/images/redo.gif" title="íì´ë¸ ì´ê¸°í" onclick="resetColumnLayout('epm-list');">
				</td>
				<td class="right">
					<select name="psize" id="psize">
						<option value="30">30</option>
						<option value="50">50</option>
						<option value="100">100</option>
						<option value="200">200</option>
						<option value="300">300</option>
					</select>
					<input type="button" value="ì¡°í" title="ì¡°í" onclick="loadGridData();">
				</td>
			</tr>
		</table>

		<div id="grid_wrap" style="height: 600px; border-top: 1px solid #3180c3;"></div>
		<%@include file="/extcore/jsp/common/aui/aui-context.jsp"%>
		<script type="text/javascript">
			let myGridID;
			function _layout() {
				return [ {
					dataField : "thumnail",
					headerText : "",
					dataType : "string",
					width : 60,
					renderer : {
						type : "ImageRenderer",
						altField : null,
						onClick : function(event) {
						}
					},
					filter : {
						showIcon : false,
						inline : false
					},
				}, {
					dataField : "name",
					headerText : "íì¼ì´ë¦",
					dataType : "string",
					width : 350,
					style : "aui-left",
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const oid = item.oid;
							const url = getCallUrl("/epm/view?oid=" + oid);
							popup(url, 1400, 600);
						}
					},
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "part_code",
					headerText : "íë²",
					dataType : "string",
					width : 130,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "name_of_parts",
					headerText : "íëª",
					dataType : "string",
					width : 350,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "dwg_no",
					headerText : "ê·ê²©",
					dataType : "string",
					width : 130,
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const oid = item.oid;
							const url = getCallUrl("/epm/view?oid=" + oid);
							popup(url, 1400, 600);
						}
					},
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "material",
					headerText : "MATERIAL",
					dataType : "string",
					width : 130,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "remark",
					headerText : "REMARK",
					dataType : "string",
					width : 150,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "reference",
					headerText : "REFERENCE ëë©´",
					dataType : "string",
					width : 150,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "version",
					headerText : "ë²ì ",
					dataType : "string",
					width : 80,
					filter : {
						showIcon : false,
						inline : false
					},
				}, {
					dataField : "modifier",
					headerText : "ìì ì",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "modifiedDate",
					headerText : "ìì ì¼",
					dataType : "date",
					formatString : "yyyy-mm-dd",
					width : 100,
					filter : {
						showIcon : true,
						inline : true,
						displayFormatValues : true
					},
				}, {
					dataField : "creator",
					headerText : "ìì±ì",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "createdDate",
					headerText : "ìì±ì¼",
					dataType : "date",
					formatString : "yyyy-mm-dd",
					width : 100,
					filter : {
						showIcon : true,
						inline : true,
						displayFormatValues : true
					},
				}, {
					dataField : "state",
					headerText : "ìí",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "location",
					headerText : "FOLDER",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : false,
						inline : false
					},
				} ]
			}

			function createAUIGrid(columns) {
				const props = {
					headerHeight : 30,
					rowHeight : 30,
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
					filterItemMoreMessage : "íí°ë§ ê²ìì´ ëë¬´ ë§ìµëë¤. ê²ìì ì´ì©í´ì£¼ì¸ì.",
				};
				myGridID = AUIGrid.create("#grid_wrap", columns, props);
				loadGridData();
				AUIGrid.bind(myGridID, "contextMenu", auiContextMenuHandler);
				AUIGrid.bind(myGridID, "vScrollChange", function(event) {
					hideContextMenu();
					vScrollChangeHandler(event);
				});
				AUIGrid.bind(myGridID, "hScrollChange", function(event) {
					hideContextMenu();
				});
			}

			function loadGridData() {
				const params = new Object();
				const url = getCallUrl("/epm/list");
				const psize = document.getElementById("psize").value;
				params.psize = psize;
				AUIGrid.showAjaxLoader(myGridID);
				parent.openLayer();
				call(url, params, function(data) {
					AUIGrid.removeAjaxLoader(myGridID);
					document.getElementById("sessionid").value = data.sessionid;
					document.getElementById("curPage").value = data.curPage;
					AUIGrid.setGridData(myGridID, data.list);
					parent.closeLayer();
				});
			}

			function exportExcel() {
				const exceptColumnFields = [ "primary" ];
				const sessionName = document.getElementById("sessionName").value;
				exportToExcel("ê³µì§ì¬í­ ë¦¬ì¤í¸", "ê³µì§ì¬í­", "ê³µì§ì¬í­ ë¦¬ì¤í¸", exceptColumnFields, sessionName);
			}

			document.addEventListener("DOMContentLoaded", function() {
				const columns = loadColumnLayout("epm-list");
				const contenxtHeader = genColumnHtml(columns);
				$("#h_item_ul").append(contenxtHeader);
				$("#headerMenu").menu({
					select : headerMenuSelectHandler
				});
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);
				selectbox("psize");
				selectbox("state");
				finderUser("creator");
				finderUser("modifier");
				twindate("created");
				twindate("modified");
				selectbox("psize");
			});

			document.addEventListener("keydown", function(event) {
				const keyCode = event.keyCode || event.which;
				if (keyCode === 13) {
					loadGridData();
				}
			})

			document.addEventListener("click", function(event) {
				hideContextMenu();
			})

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID);
			});
		</script>
	</form>
</body>
</html>