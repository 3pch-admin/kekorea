<%@page import="java.util.Map"%>
<%@page import="wt.org.WTUser"%>
<%@page import="e3ps.org.service.OrgHelper"%>
<%@page import="wt.epm.EPMDocumentType"%>
<%@page import="e3ps.epm.service.EpmHelper"%>
<%@page import="e3ps.part.service.PartHelper"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@page import="wt.fc.PagingQueryResult"%>
<%@page import="e3ps.common.util.PageQueryUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
boolean isSupervisor = (boolean) request.getAttribute("isSupervisor");
WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
ArrayList<Map<String, String>> maks = (ArrayList<Map<String, String>>) request.getAttribute("maks");
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
		<input type="hidden" name="isAdmin" id="isAdmin" value="<%=isAdmin%>">
		<input type="hidden" name="sessionName" id="sessionName" value="<%=sessionUser.getFullName()%>">
		<input type="hidden" name="sessionId" id="sessionId" value="<%=sessionUser.getName()%>">
		<input type="hidden" name="sessionid" id="sessionid">
		<input type="hidden" name="lastNum" id="lastNum">
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
				<th>특이사항 제목</th>
				<td class="indent5">
					<input type="text" name="name" id="name">
				</td>
				<th>설명</th>
				<td class="indent5">
					<input type="text" name="content" id="content">
				</td>
				<th>KEK 작번</th>
				<td class="indent5">
					<input type="text" name="kekNumber" id="kekNumber">
				</td>
				<th>KE 작번</th>
				<td class="indent5">
					<input type="text" name="keNumber" id="keNumber">
				</td>
			</tr>
			<tr>
				<th>막종</th>
				<td class="indent5">
					<select name="mak_name" id="mak_name" class="width-200">
						<option value="">선택</option>
						<%
						for (Map<String, String> map : maks) {
							String oid = map.get("key");
							String name = map.get("value");
						%>
						<option value="<%=oid%>"><%=name%></option>
						<%
						}
						%>
					</select>
				</td>
				<th>작성자</th>
				<td class="indent5">
					<input type="text" name="creator" id="creator" data-multi="false">
					<input type="hidden" name="creatorOid" id="creatorOid">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('creator')">
				</td>
				<th>작성일</th>
				<td class="indent5">
					<input type="text" name="createdFrom" id="createdFrom" class="width-100">
					~
					<input type="text" name="createdTo" id="createdTo" class="width-100">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearFromTo('createdFrom', 'createdTo')">
				</td>
				<th>작업 내용</th>
				<td class="indent5">
					<input type="text" name="description" id="description">
				</td>
			</tr>
		</table>

		<table class="button-table">
			<tr>
				<td class="left">
					<img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="엑셀 다운로드" onclick="exportExcel();">
					<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('issue-list');">
					<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('issue-list');">
					<img src="/Windchill/extcore/images/help.gif" title="메뉴얼 재생" onclick="play('test.mp4');">
					<input type="button" value="확장" title="확장" class="red" onclick="expand();">
					<%
					if (isSupervisor) {
					%>
					<input type="button" value="저장" title="저장" onclick="save();">
					<input type="button" value="행 삭제" title="행 삭제" class="red" onclick="deleteRow();">
					<%
					}
					%>
				</td>
				<td class="right">
					<select name="_psize" id="_psize">
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
		
				<!-- 메뉴얼 비디오 구간 -->
		<%@include file="/extcore/jsp/common/video-layer.jsp"%>

		<div id="grid_wrap" style="height: 705px; border-top: 1px solid #3180c3;"></div>
		<%@include file="/extcore/jsp/common/aui/aui-context.jsp"%>
		<script type="text/javascript">
			let myGridID;
			function _layout() {
				return [ {
					dataField : "name",
					headerText : "수배표 제목",
					dataType : "string",
					width : 300,
					style : "underline",
					filter : {
						showIcon : true,
						inline : true
					},
					cellMerge : true
				}, {
					dataField : "content",
					headerText : "특이사항 내용",
					dataType : "string",
					width : 350,
					style : "aui-left",
					filter : {
						showIcon : true,
						inline : true
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
							const oid = item.poid;
							const url = getCallUrl("/project/info?oid=" + oid);
							popup(url);
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
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const oid = item.poid;
							const url = getCallUrl("/project/info?oid=" + oid);
							popup(url);
						}
					},
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
					style : "auit-left",
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
				} ]
			};

			function createAUIGrid(columns) {
				const props = {
					headerHeight : 30,
					showStateColumn : true,
					showRowCheckColumn : true,
					showRowNumColumn : true,
					rowNumHeaderText : "번호",
					showAutoNoDataMessage : false,
					enableFilter : true,
					selectionMode : "multipleCells",
					enableMovingColumn : true,
					showInlineFilter : true,
					useContextMenu : true,
					enableRightDownFocus : true,
					filterLayerWidth : 320,
					filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
					enableCellMerge : true,
					forceTreeView : true
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
				AUIGrid.bind(myGridID, "cellDoubleClick", auiCellDoubleClick);
			}

			function auiCellDoubleClick(event) {
				const dataField = event.dataField;
				const item = event.item;
				if (dataField == "name") {
					const url = getCallUrl("/issue/view?oid=" + item.loid);
					popup(url, 1400, 550);
				}
			}

			function loadGridData() {
				let params = new Object();
				const url = getCallUrl("/issue/list");
				const field = [ "name", "content", "description", "kekNumber", "keNumber", "mak_name", "creatorOid", "createdFrom", "createdTo", "_psize" ];
				params = toField(params, field);
				AUIGrid.showAjaxLoader(myGridID);
				parent.openLayer();
				call(url, params, function(data) {
					AUIGrid.removeAjaxLoader(myGridID);
					if (data.result) {
						document.getElementById("sessionid").value = data.sessionid;
						document.getElementById("curPage").value = data.curPage;
						document.getElementById("lastNum").value = data.list.length;
						AUIGrid.setGridData(myGridID, data.list);
					} else {
						alert(data.msg);
					}
					parent.closeLayer();
				});
			}

			function deleteRow() {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				const sessionId = document.getElementById("sessionId").value;
				for (let i = checkedItems.length - 1; i >= 0; i--) {
					const item = checkedItems[i].item;
					if (!checker(sessionId, item.creatorId)) {
						alert("데이터 작성자가 아닙니다.");
						return false;
					}
					const rowIndex = checkedItems[i].rowIndex;
					AUIGrid.removeRow(myGridID, rowIndex);
				}
			}

			function save() {
				const url = getCallUrl("/issue/delete");
				const params = new Object();
				const removeRows = AUIGrid.getRemovedItems(myGridID);
				if (removeRows.length === 0) {
					alert("변경된 내용이 없습니다.");
					return false;
				}

				params.removeRows = removeRows;

				if (!confirm("저장 하시겠습니까?")) {
					return false;
				}

				parent.openLayer();
				call(url, params, function(data) {
					alert(data.msg);
					parent.closeLayer();
					if (data.result) {
						loadGridData();
					}
				});
			}

			function exportExcel() {
				const exceptColumnFields = [];
				const sessionName = document.getElementById("sessionName").value;
				exportToExcel("특이사항 리스트", "특이사항", "특이사항 리스트", exceptColumnFields, sessionName);
			}

			document.addEventListener("DOMContentLoaded", function() {
				document.getElementById("name").focus();
				const columns = loadColumnLayout("issue-list");
				const contenxtHeader = genColumnHtml(columns);
				$("#h_item_ul").append(contenxtHeader);
				$("#headerMenu").menu({
					select : headerMenuSelectHandler
				});
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);
				finderUser("creator");
				twindate("created");
				selectbox("_psize");
				selectbox("mak_name");
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