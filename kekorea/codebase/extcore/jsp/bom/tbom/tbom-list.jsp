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
				<th>공지사항 제목</th>
				<td class="indent5">
					<input type="text" name="fileName" class="width-200">
				</td>
				<th>설명</th>
				<td class="indent5">
					<input type="text" name="partCode" class="width-200">
				</td>
				<th>작성자</th>
				<td class="indent5">
					<input type="text" name="creator" id="creator" class="width-200">
				</td>
				<th>작성일</th>
				<td class="indent5">
					<input type="text" name="createdFrom" id="createdFrom" class="width-100">
					~
					<input type="text" name="createdTo" id="createdTo" class="width-100">
				</td>
			</tr>
		</table>

		<table class="button-table">
			<tr>
				<td class="left">
					<img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="엑셀 다운로드" onclick="exportExcel();">
					<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('tbom-list');">
					<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('tbom-list');">
					<input type="button" value="등록" title="등록" class="blue" onclick="create();">
				</td>
				<td class="right">
					<select name="psize" id="psize">
						<option value="30">30</option>
						<option value="50">50</option>
						<option value="100">100</option>
						<option value="200">200</option>
						<option value="300">300</option>
					</select>
					<input type="button" value="조회" title="조회" onclick="loadGridData();">
				</td>
			</tr>
		</table>

		<div id="grid_wrap" style="height: 705px; border-top: 1px solid #3180c3;"></div>
		<%@include file="/extcore/jsp/common/aui/aui-context.jsp"%>
		<script type="text/javascript">
			let myGridID;
			function _layout() {
				return [ {
					dataField : "name",
					headerText : "T-BOM 제목",
					dataType : "string",
					width : 300,
					style : "aui-left",
					filter : {
						showIcon : true,
						inline : true
					},
					cellMerge : true
				}, {
					dataField : "info",
					headerText : "",
					width : 40,
					renderer : {
						type : "IconRenderer",
						iconWidth : 16,
						iconHeight : 16,
						iconTableRef : {
							"default" : "/Windchill/extcore/images/details.gif"
						},
						onClick : function(event) {
							const oid = event.item.loid;
							const url = getCallUrl("/partlist/info?oid=" + oid);
							popup(url);
						}
					},
					filter : {
						showIcon : false,
						inline : false
					},
					cellMerge : true,
					mergeRef : "name",
					mergePolicy : "restrict"
				}, {
					dataField : "projectType_name",
					headerText : "설계구분",
					dataType : "string",
					width : 80,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "mak_name",
					headerText : "막종",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "detail_name",
					headerText : "막종상세",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "kekNumber",
					headerText : "KEK 작번",
					dataType : "string",
					width : 100,
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							alert("( " + rowIndex + ", " + columnIndex + " ) " + item.color + "  Link 클릭\r\n자바스크립트 함수 호출하고자 하는 경우로 사용하세요!");
						}
					},
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "keNumber",
					headerText : "KE 작번",
					dataType : "string",
					style : "underline",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "userId",
					headerText : "USER ID",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "description",
					headerText : "작업내용",
					dataType : "string",
					width : 300,
					style : "left",
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "customer_name",
					headerText : "거래처",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "install_name",
					headerText : "설치 장소",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "pdate_txt",
					headerText : "발행일",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true,
					},
				}, {
					dataField : "model",
					headerText : "모델",
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
					cellMerge : true,
					mergeRef : "name",
					mergePolicy : "restrict"
				}, {
					dataField : "createdDate_txt",
					headerText : "작성일",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true,
					},
					cellMerge : true,
					mergeRef : "name",
					mergePolicy : "restrict"
				}, {
					dataField : "modifiedDate_txt",
					headerText : "수정일",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true,
					},
					cellMerge : true,
					mergeRef : "name",
					mergePolicy : "restrict"
				}, {
					dataField : "state",
					headerText : "상태",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
					cellMerge : true,
					mergeRef : "name",
					mergePolicy : "restrict"
				} ]
			};

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
					enableCellMerge : true,
					fixedColumnCount : 1,
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
				AUIGrid.bind(myGridID, "cellDoubleClick", auiCellDoubleClickHandler);
			}

			function auiCellDoubleClickHandler(event) {

			}

			function loadGridData() {
				const params = new Object();
				const url = getCallUrl("/tbom/list");
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

			function create() {
				const url = getCallUrl("/tbom/create");
				popup(url);
			}

			document.addEventListener("DOMContentLoaded", function() {
				const columns = loadColumnLayout("tbom-list");
				const contenxtHeader = genColumnHtml(columns);
				$("#h_item_ul").append(contenxtHeader);
				$("#headerMenu").menu({
					select : headerMenuSelectHandler
				});
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);
				finderUser("creator");
				twindate("created");
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