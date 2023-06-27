<%@page import="net.sf.json.JSONObject"%>
<%@page import="java.util.ListIterator"%>
<%@page import="java.util.Iterator"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.admin.numberRuleCode.NumberRuleCode"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="wt.org.WTUser"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.admin.commonCode.CommonCodeType"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
boolean isSupervisor = (boolean) request.getAttribute("isSupervisor");
WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
Timestamp time = (Timestamp) request.getAttribute("time");
JSONArray sizes = (JSONArray) request.getAttribute("sizes");
JSONArray drawingCompanys = (JSONArray) request.getAttribute("drawingCompanys");
JSONArray writtenDocuments = (JSONArray) request.getAttribute("writtenDocuments");
JSONArray businessSectors = (JSONArray) request.getAttribute("businessSectors");
JSONArray classificationWritingDepartments = (JSONArray) request.getAttribute("classificationWritingDepartments");
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
			<tr>
				<th>도면번호</th>
				<td class="indent5">
					<input type="text" name="number" id="number">
				</td>
				<th>사이즈</th>
				<td class="indent5">
					<select name="size" id="size" class="width-200">
						<option value="">선택</option>
						<%
						ListIterator lit = sizes.listIterator();
						while (lit.hasNext()) {
							JSONObject node = (JSONObject) lit.next();
							String key = node.getString("key");
							String value = node.getString("value");
						%>
						<option value="<%=key%>"><%=value%></option>
						<%
						}
						%>
					</select>
				</td>
				<th>LOT</th>
				<td class="indent5">
					<input type="text" name="lotNo" id="lotNo" maxlength="4">
				</td>
				<th>UNIT NAME</th>
				<td class="indent5">
					<input type="text" name="unitName" id="unitName">
				</td>
			</tr>
			<tr>
				<th>도면명</th>
				<td class="indent5">
					<input type="text" name="name" id="name">
				</td>
				<th>작성부서구분</th>
				<td class="indent5">
					<select name="classificationWritingDepartments_code" id="classificationWritingDepartments_code" class="width-200">
						<option value="">선택</option>
						<%
						ListIterator lit2 = classificationWritingDepartments.listIterator();
						while (lit2.hasNext()) {
							JSONObject node = (JSONObject) lit2.next();
							String key = node.getString("key");
							String value = node.getString("value");
						%>
						<option value="<%=key%>"><%=value%></option>
						<%
						}
						%>
					</select>
				</td>
				<th>작성문서구분</th>
				<td class="indent5">
					<select name="writtenDocuments_code" id="writtenDocuments_code" class="width-200">
						<option value="">선택</option>
						<%
						ListIterator lit3 = writtenDocuments.listIterator();
						while (lit3.hasNext()) {
							JSONObject node = (JSONObject) lit3.next();
							String key = node.getString("key");
							String value = node.getString("value");
						%>
						<option value="<%=key%>"><%=value%></option>
						<%
						}
						%>
					</select>
				</td>
				<th>상태</th>
				<td class="indent5">
					<select name="state" id="state" class="width-200">
						<option value="">선택</option>
						<option value="작업 중">작업 중</option>
						<option value="승인중">승인중</option>
						<option value="승인됨">승인됨</option>
						<option value="반려됨">반려됨</option>
					</select>
				</td>
			</tr>
			<tr>
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
				<th>버전</th>
				<td colspan="3">
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
								<b>모든버전</b>
							</label>
						</div>
					</div>
				</td>
			</tr>
		</table>

		<table class="button-table">
			<tr>
				<td class="left">
					<img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="엑셀 다운로드" onclick="exportExcel();">
					<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('numberRule-list');">
					<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('numberRule-list');">
					<img src="/Windchill/extcore/images/help.gif" title="메뉴얼 재생" onclick="play('test.mp4');">
<!-- 					<input type="button" value="등록" title="등록" class="blue" onclick="create();"> -->
					<input type="button" value="저장" title="저장" onclick="save();">
					<input type="button" value="개정" title="개정" class="red" onclick="revise();">
					<%
// 					if (isSupervisor) {
					%>
					<input type="button" value="행 추가" title="행 추가" class="blue" onclick="addRow();">
					<input type="button" value="행 삭제" title="행 삭제" class="red" onclick="deleteRow();">
					<%
// 					}
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

		<div id="grid_wrap" style="height: 670px; border-top: 1px solid #3180c3;"></div>
		<%@include file="/extcore/jsp/common/aui/aui-context.jsp"%>
		<script type="text/javascript">
			let myGridID;
			const businessSector =
		<%=businessSectors%>
			const drawingCompany =
		<%=drawingCompanys%>
			const size =
		<%=sizes%>
			const writtenDocuments =
		<%=writtenDocuments%>
			const classificationWritingDepartments =
		<%=classificationWritingDepartments%>
			function _layout() {
				return [ {
					dataField : "number",
					headerText : "도면번호",
					dataType : "string",
					width : 100,
					editable : false,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "size_code",
					headerText : "사이즈",
					dataType : "string",
					width : 80,
					renderer : {
						type : "IconRenderer",
						iconWidth : 16,
						iconHeight : 16,
						iconPosition : "aisleRight",
						iconTableRef : {
							"default" : "/Windchill/extcore/component/AUIGrid/images/list-icon.png"
						},
						onClick : function(event) {
							AUIGrid.openInputer(event.pid);
						}
					},
					editRenderer : {
						type : "ComboBoxRenderer",
						autoCompleteMode : true,
						autoEasyMode : true,
						matchFromFirst : false,
						showEditorBtnOver : false,
						list : size,
						keyField : "key",
						valueField : "value",
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							let isValid = false;
							for (let i = 0, len = size.length; i < len; i++) {
								if (size[i]["value"] == newValue) {
									isValid = true;
									break;
								}
							}
							return {
								"validate" : isValid,
								"message" : "리스트에 있는 값만 선택(입력) 가능합니다."
							};
						}
					},
					labelFunction : function(rowIndex, columnIndex, value, headerText, item) {
						let retStr = "";
						for (let i = 0, len = size.length; i < len; i++) {
							if (size[i]["key"] == value) {
								retStr = size[i]["value"];
								break;
							}
						}
						return retStr == "" ? value : retStr;
					},
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "lotNo",
					headerText : "LOT",
					dataType : "numeric",
					width : 80,
					formatString : "###0",
					editRenderer : {
						type : "InputEditRenderer",
						onlyNumeric : true,
						maxlength : 4,
					},
					filter : {
						showIcon : true,
						inline : true,
						displayFormatValues : true
					},
				}, {
					dataField : "unitName",
					headerText : "UNIT NAME",
					dataType : "string",
					width : 200,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "name",
					headerText : "도번명",
					dataType : "string",
					width : 250,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "businessSector_code",
					headerText : "사업부문",
					dataType : "string",
					width : 200,
					editable : false,
					labelFunction : function(rowIndex, columnIndex, value, headerText, item) {
						let retStr = "";
						for (let i = 0, len = businessSector.length; i < len; i++) {
							if (businessSector[i]["key"] == value) {
								retStr = businessSector[i]["value"];
								break;
							}
						}
						return retStr == "" ? value : retStr;
					},
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "drawingCompany_code",
					headerText : "도면생성회사",
					dataType : "string",
					width : 150,
					editable : false,
					labelFunction : function(rowIndex, columnIndex, value, headerText, item) {
						let retStr = "";
						for (let i = 0, len = drawingCompany.length; i < len; i++) {
							if (drawingCompany[i]["key"] == value) {
								retStr = drawingCompany[i]["value"];
								break;
							}
						}
						return retStr == "" ? value : retStr;
					},
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "classificationWritingDepartments_code",
					headerText : "작성부서구분",
					dataType : "string",
					width : 150,
					renderer : {
						type : "IconRenderer",
						iconWidth : 16,
						iconHeight : 16,
						iconPosition : "aisleRight",
						iconTableRef : {
							"default" : "/Windchill/extcore/component/AUIGrid/images/list-icon.png"
						},
						onClick : function(event) {
							AUIGrid.openInputer(event.pid);
						}
					},
					editRenderer : {
						type : "ComboBoxRenderer",
						autoCompleteMode : true,
						autoEasyMode : true,
						matchFromFirst : false,
						showEditorBtnOver : false,
						list : classificationWritingDepartments,
						keyField : "key",
						valueField : "value",
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							let isValid = false;
							for (let i = 0, len = classificationWritingDepartments.length; i < len; i++) {
								if (classificationWritingDepartments[i]["value"] == newValue) {
									isValid = true;
									break;
								}
							}
							return {
								"validate" : isValid,
								"message" : "리스트에 있는 값만 선택(입력) 가능합니다."
							};
						}
					},
					labelFunction : function(rowIndex, columnIndex, value, headerText, item) {
						let retStr = "";
						for (let i = 0, len = classificationWritingDepartments.length; i < len; i++) {
							if (classificationWritingDepartments[i]["key"] == value) {
								retStr = classificationWritingDepartments[i]["value"];
								break;
							}
						}
						return retStr == "" ? value : retStr;
					},
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "writtenDocuments_code",
					headerText : "작성문서구분",
					dataType : "string",
					width : 150,
					renderer : {
						type : "IconRenderer",
						iconWidth : 16,
						iconHeight : 16,
						iconPosition : "aisleRight",
						iconTableRef : {
							"default" : "/Windchill/extcore/component/AUIGrid/images/list-icon.png"
						},
						onClick : function(event) {
							AUIGrid.openInputer(event.pid);
						}
					},
					editRenderer : {
						type : "ComboBoxRenderer",
						autoCompleteMode : true,
						autoEasyMode : true,
						matchFromFirst : false,
						showEditorBtnOver : false,
						list : writtenDocuments,
						keyField : "key",
						valueField : "value",
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							let isValid = false;
							for (let i = 0, len = writtenDocuments.length; i < len; i++) {
								if (writtenDocuments[i]["value"] == newValue) {
									isValid = true;
									break;
								}
							}
							return {
								"validate" : isValid,
								"message" : "리스트에 있는 값만 선택(입력) 가능합니다."
							};
						}
					},
					labelFunction : function(rowIndex, columnIndex, value, headerText, item) {
						let retStr = "";
						for (let i = 0, len = writtenDocuments.length; i < len; i++) {
							if (writtenDocuments[i]["key"] == value) {
								retStr = writtenDocuments[i]["value"];
								break;
							}
						}
						return retStr == "" ? value : retStr;
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
					editable : false,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "state",
					headerText : "상태",
					dataType : "string",
					width : 80,
					editable : false,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "creator",
					headerText : "작성자",
					dataType : "string",
					width : 100,
					editable : false,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "createdDate_txt",
					headerText : "작성일",
					dataType : "string",
					width : 100,
					editable : false,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "modifier",
					headerText : "수정자",
					dataType : "string",
					width : 100,
					editable : false,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "modifiedDate_txt",
					headerText : "수정일",
					dataType : "string",
					width : 100,
					editable : false,
					filter : {
						showIcon : true,
						inline : true
					},
				} ]
			}

			function createAUIGrid(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : true,
					showRowCheckColumn : true,
					showStateColumn : true,
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
					editable : true,
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
				AUIGrid.bind(myGridID, "cellEditBegin", auiCellEditBeginHandler);
				AUIGrid.bind(myGridID, "cellEditEnd", auiCellEditEndHandler);
				AUIGrid.bind(myGridID, "keyDown", auiKeyDownHandler);
			}

			// enter 키 행 추가
			function auiKeyDownHandler(event) {
				if (event.keyCode == 13) { // 엔터 키
					var selectedItems = AUIGrid.getSelectedItems(event.pid);
					var rowIndex = selectedItems[0].rowIndex;
					if (rowIndex === AUIGrid.getRowCount(event.pid) - 1) { // 마지막 행인지 여부
						const item = {
							version : 1,
							drawingCompany_code : "K",
							businessSector_code : "K",
							number : "K"
						}
						AUIGrid.addRow(event.pid, item); // 행 추가
						return false; // 엔터 키의 기본 행위 안함.
					}
				}
				return true; // 기본 행위 유지
			}

			function create() {
				const url = getCallUrl("/numberRule/create");
				popup(url, 1600, 650);
			}

			// 			function register() {
			// 				const url = getCallUrl("/numberRule/register");
			// 				popup(url);
			// 			}

			function auiCellEditEndHandler(event) {
				const item = event.item;
				const rowIndex = event.rowIndex;
				const dataField = event.dataField;
				const value = event.value;
				if (dataField === "classificationWritingDepartments_code") {
					const newNumber = "K" + value;
					AUIGrid.setCellValue(myGridID, rowIndex, "number", newNumber);
				}

				if (dataField === "writtenDocuments_code") {
					const value1 = AUIGrid.getCellValue(myGridID, rowIndex, "classificationWritingDepartments_code");
					const newNumber = "K" + value1 + value;
					const url = getCallUrl("/numberRule/last?number=" + newNumber);
					call(url, null, function(data) {
						const next = data.next;
						AUIGrid.setCellValue(myGridID, rowIndex, "number", newNumber + next);
					}, "GET");
				}

				const lotNo = item.lotNo;
				if (dataField === "lotNo") {
					const url = getCallUrl("/erp/getUnitName?lotNo=" + lotNo + "&callLoc=KEK 도번");
					parent.openLayer();
					call(url, null, function(data) {
						if (data.result) {
							const newItem = {
								unitName : data.unitName,
							};
							AUIGrid.updateRow(myGridID, newItem, rowIndex);
						} else {
							alert(data.msg);
						}
						parent.closeLayer();
					}, "GET");
				}
			}

			function auiCellEditBeginHandler(event) {
				const dataField = event.dataField;
				const rowIndex = event.rowIndex;
				const state = event.item.state;
				if (state === "승인됨") {
					return false;
				}

				if (dataField === "writtenDocuments_code") {
					const value = AUIGrid.getCellValue(myGridID, rowIndex, "classificationWritingDepartments_code");
					if (isNull(value)) {
						alert("작성부서구분을 먼저 선택하세요.");
						return false;
					}
				}
				return true;
			}

			function revise() {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);

				if (checkedItems.length == 0) {
					alert("개정할 도번을 선택하세요.");
					return false;
				}

				for (let i = 0; i < checkedItems.length; i++) {
					const oid = checkedItems[i].item.oid;
					const latest = checkedItems[i].item.latest;
					const rowIndex = checkedItems[i].rowIndex;
					if (!latest) {
						alert("최신버전이 아닌 도면이 포함되어있습니다.\n" + (rowIndex + 1) + "행 데이터");
						return false;
					}

					if (oid === undefined) {
						alert("신규로 작성한 데이터가 존재합니다.\n" + (rowIndex + 1) + "행 데이터");
						return false;
					}
				}
				const url = getCallUrl("/numberRule/revise");
				const p = popup(url, 1600, 550);
				p.list = checkedItems;
			}

			function save() {
				const url = getCallUrl("/numberRule/save");
				const params = new Object();
				const addRows = AUIGrid.getAddedRowItems(myGridID);
				const removeRows = AUIGrid.getRemovedItems(myGridID);
				const editRows = AUIGrid.getEditedRowItems(myGridID);

				if (addRows.length === 0 && removeRows.length === 0 && editRows.length === 0) {
					alert("변경된 내용이 없습니다.");
					return false;
				}

				for (let i = 0; i < addRows.length; i++) {
					const item = addRows[i];
					const rowIndex = AUIGrid.rowIdToIndex(myGridID, item._$uid);

					if (isNull(item.size_code)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 1, "사이즈를 선택하세요.");
						return false;
					}

					if (isNull(item.lotNo) || item.lotNo === 0) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 2, "LOT NO의 값은 0혹은 공백을 입력 할 수 없습니다.");
						return false;
					}

					if (isNull(item.unitName)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 3, "UNIT NAME을 입력하세요.");
						return false;
					}

					if (isNull(item.name)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 4, "도면명을 입력하세요.");
						return false;
					}

					if (isNull(item.classificationWritingDepartments_code)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 7, "작성부서를 선택하세요.");
						return false;
					}

					if (isNull(item.writtenDocuments_code)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 8, "작성문서구분을 선택하세요.");
						return false;
					}
				}

				for (let i = 0; i < editRows.length; i++) {
					const item = editRows[i];
					const rowIndex = AUIGrid.rowIdToIndex(myGridID, item._$uid);

					if (isNull(item.size_code)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 1, "사이즈를 선택하세요.");
						return false;
					}

					if (isNull(item.lotNo) || item.lotNo === 0) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 2, "LOT NO의 값은 0혹은 공백을 입력 할 수 없습니다.");
						return false;
					}

					if (isNull(item.unitName)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 3, "UNIT NAME을 입력하세요.");
						return false;
					}

					if (isNull(item.name)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 4, "도면명을 입력하세요.");
						return false;
					}

					if (isNull(item.classificationWritingDepartments_code)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 7, "작성부서를 선택하세요.");
						return false;
					}

					if (isNull(item.writtenDocuments_code)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 8, "작성문서구분을 선택하세요.");
						return false;
					}
				}

				if (!confirm("저장 하시겠습니까?")) {
					return false;
				}

				params.addRows = addRows;
				params.removeRows = removeRows;
				params.editRows = editRows;
				parent.openLayer();
				call(url, params, function(data) {
					alert(data.msg);
					parent.closeLayer();
					if (data.result) {
						loadGridData();
					} else {
						parent.closeLayer();
					}
				});
			}

			function loadGridData() {
				let params = new Object();
				const url = getCallUrl("/numberRule/list");
				const field = [ "number", "name", "lotNo", "unitName", "size", "state", "writtenDocuments_code", "creatorOid", "createdFrom", "createdTo", "classificationWritingDepartments_code", "_psize" ];
				const latest = !!document.querySelector("input[name=latest]:checked").value;
				params = toField(params, field);
				params.latest = latest;
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

			function deleteRow() {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				const sessionId = document.getElementById("sessionId").value;
				for (let i = checkedItems.length - 1; i >= 0; i--) {
					const item = checkedItems[i].item;
					if (!isNull(item.creatorId) && !checker(sessionId, item.creatorId)) {
						alert("데이터 작성자가 아닙니다.");
						return false;
					}
					const rowIndex = checkedItems[i].rowIndex;
					AUIGrid.removeRow(myGridID, rowIndex);
				}
			}

			function addRow() {
				const item = new Object();
				item.version = 1;
				item.drawingCompany_code = "K";
				item.businessSector_code = "K";
				item.number = "K";
				AUIGrid.addRow(myGridID, item, "first");
			}

			function exportExcel() {
				const exceptColumnFields = [];
				const sessionName = document.getElementById("sessionName").value;
				exportToExcel("KEK 도번 리스트", "KEK 도번", "KEK 도번 리스트", exceptColumnFields, sessionName);
			}

			document.addEventListener("DOMContentLoaded", function() {
				toFocus("number");
				const columns = loadColumnLayout("numberRule-list");
				const contenxtHeader = genColumnHtml(columns);
				$("#h_item_ul").append(contenxtHeader);
				$("#headerMenu").menu({
					select : headerMenuSelectHandler
				});
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);
				selectbox("_psize");
				selectbox("state");
				selectbox("size");
				selectbox("writtenDocuments_code");
				selectbox("classificationWritingDepartments_code");
				finderUser("creator");
				twindate("created");
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