<%@page import="java.sql.Timestamp"%>
<%@page import="wt.org.WTUser"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.admin.commonCode.CommonCodeType"%>
<%@page import="org.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
Timestamp time = (Timestamp) request.getAttribute("time");
ArrayList<CommonCode> sizes = (ArrayList<CommonCode>) request.getAttribute("sizes");
ArrayList<CommonCode> drawingCompanys = (ArrayList<CommonCode>) request.getAttribute("drawingCompanys");
ArrayList<CommonCode> writtenDocuments = (ArrayList<CommonCode>) request.getAttribute("writtenDocuments");
ArrayList<CommonCode> businessSectors = (ArrayList<CommonCode>) request.getAttribute("businessSectors");
ArrayList<CommonCode> classificationWritingDepartment = (ArrayList<CommonCode>) request
		.getAttribute("classificationWritingDepartment");
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
		<input type="hidden" name="time" id="time" value="<%=time%>">
		<input type="hidden" name="sessionid" id="sessionid">
		<input type="hidden" name="curPage" id="curPage">
		<table class="search_table">
			<tr>
				<th>사업부문</th>
				<td>
					<select name="size" id="size" class="AXSelect w200">
						<option value="">선택</option>
						<%
						for (CommonCode commonCode : sizes) {
							String value = commonCode.getPersistInfo().getObjectIdentifier().getStringValue();
							String display = commonCode.getName();
						%>
						<option value="<%=value%>"><%=display%></option>
						<%
						}
						%>
					</select>
				</td>
				<th>작성기간</th>
				<td>&nbsp;</td>
				<th>도면번호</th>
				<td>
					<input type="text" name="kekNumber" class="AXInput wid200">
				</td>
				<th>도면생성회사</th>
				<td>
					<select name="size" id="size" class="AXSelect w200">
						<option value="">선택</option>
						<%
						for (CommonCode commonCode : sizes) {
							String value = commonCode.getPersistInfo().getObjectIdentifier().getStringValue();
							String display = commonCode.getName();
						%>
						<option value="<%=value%>"><%=display%></option>
						<%
						}
						%>
					</select>
				</td>
			</tr>
			<tr>
				<th>사이즈</th>
				<td>
					<select name="size" id="size" class="AXSelect w200">
						<option value="">선택</option>
						<%
						for (CommonCode commonCode : sizes) {
							String value = commonCode.getPersistInfo().getObjectIdentifier().getStringValue();
							String display = commonCode.getName();
						%>
						<option value="<%=value%>"><%=display%></option>
						<%
						}
						%>
					</select>
				</td>
				<th>도면구분</th>
				<td>&nbsp;</td>
				<th>년도</th>
				<td>&nbsp;</td>
				<th>관리번호</th>
				<td>
					<input type="text" name="kekNumber" class="AXInput wid200">
				</td>
			</tr>
			<tr>
				<th>부품도구분</th>
				<td>&nbsp;</td>
				<th>진행상태</th>
				<td>
					<select name="state" id="state" class="AXSelect w200">
						<option value="">선택</option>
						<option value="진행중">진행중</option>
						<option value="완료">완료</option>
						<option value="폐기">폐기</option>
					</select>
				</td>
				<th>작성부서</th>
				<td>&nbsp;</td>
				<th>작성자</th>
				<td>
					<input type="text" name="kekNumber" class="AXInput wid200">
				</td>
			</tr>
		</table>
		<!-- button table -->
		<table class="btn_table">
			<tr>
				<td class="left">
					<img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="엑셀 다운로드" onclick="exportExcel();">
					<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('keDrawing-list');">
					<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('keDrawing-list');">
					<input type="button" value="저장" title="저장" onclick="create();">
					<input type="button" value="개정" title="개정" class="red" onclick="revise();">
					<input type="button" value="행 추가" title="행 추가" class="blue" onclick="addRow();">
				</td>
				<td class="right">
					<select name="psize" id="psize">
						<option value="30">30</option>
						<option value="50">50</option>
						<option value="100">100</option>
						<option value="200">200</option>
						<option value="300">300</option>
					</select>
					<input type="button" value="조회" class="blueBtn" id="searchBtn" title="조회">
				</td>
			</tr>
		</table>
		<div id="grid_wrap" style="height: 450px; border-top: 1px solid #3180c3;"></div>
		<%@include file="/extcore/jsp/common/aui/aui-context.jsp"%>
		<script type="text/javascript">
			let myGridID;
			function _layout() {
				return [ {
					dataField : "number",
					headerText : "도면번호",
					dataType : "string",
					width : 150,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "name",
					headerText : "도면명",
					dataType : "string",
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "businessSector",
					headerText : "사업부문",
					dataType : "string",
					width : 200,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "drawingCompany",
					headerText : "도면생성회사",
					dataType : "string",
					width : 150,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "department",
					headerText : "작성부서구분",
					dataType : "string",
					width : 150,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "document",
					headerText : "작성문서구분",
					dataType : "string",
					width : 150,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "latest",
					headerText : "최신버전여부",
					dataType : "boolean",
					width : 100,
					renderer : {
						type : "CheckBoxEditRenderer",
					},
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "version",
					headerText : "버전",
					dataType : "string",
					width : 100,
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
						inline : true
					},
				}, {
					dataField : "modifier",
					headerText : "수정자",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "modifiedDate",
					headerText : "수정일",
					dataType : "date",
					formatString : "yyyy-mm-dd",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
				} ]
			}

			function createAUIGrid(columnLayout) {
				const props = {
					headerHeight : 30,
					rowHeight : 30,
					showRowNumColumn : true,
					showRowCheckColumn : true,
					showStateColumn : true,
					rowNumHeaderText : "번호",
					noDataMessage : "검색 결과가 없습니다.",
					enableFilter : true,
					selectionMode : "multipleCells",
					enableMovingColumn : true,
					showInlineFilter : true,
					useContextMenu : true,
					enableRightDownFocus : true,
					filterLayerWidth : 320,
					filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
					editable : true
				};
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
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
				const url = getCallUrl("/numberRule/list");
				const psize = document.getElementById("psize").value;
				params.psize = psize;
				AUIGrid.showAjaxLoader(myGridID);
				parent.openLayer();
				call(url, params, function(data) {
					AUIGrid.removeAjaxLoader(myGridID);
					$("input[name=sessionid]").val(data.sessionid);
					$("input[name=curPage]").val(data.curPage);
					AUIGrid.setGridData(myGridID, data.list);
					parent.closeLayer();
				})
			}

			function addRow() {
				const sessionName = document.getElementById("sessionName").value;
				const time = document.getElementById("time").value;
				const item = {
					creator : sessionName,
					modifier : sessionName,
					createdDate_txt : time,
					modifiedDate_txt : time,
					latest : true,
				};
				AUIGrid.addRow(myGridID, item, "first");
			}

			document.addEventListener("DOMContentLoaded", function() {
				const columns = loadColumnLayout("keDrawing-list");
				const contenxtHeader = genColumnHtml(columns);
				$("#h_item_ul").append(contenxtHeader);
				$("#headerMenu").menu({
					select : headerMenuSelectHandler
				});
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);
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