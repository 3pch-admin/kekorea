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
		<input type="hidden" name="curPage" id="curPage">
		<table class="search-table">
			<tr>
				<th>사업부문</th>
				<td class="indent5">
					<select name="businessSector" id="businessSector" class="width-200">
						<option value="">선택</option>
					</select>
				</td>
				<th>작성기간</th>
				<td class="indent5">&nbsp;</td>
				<th>도면번호</th>
				<td class="indent5">
					<input type="text" name="kekNumber" class="width-200">
				</td>
				<th>도면생성회사</th>
				<td class="indent5">
					<select name="size" id="size" class="width-200">
						<option value="">선택</option>
					</select>
				</td>
			</tr>
			<tr>
				<th>사이즈</th>
				<td class="indent5">
					<select name="size" id="size" class="width-200">
						<option value="">선택</option>
					</select>
				</td>
				<th>도면구분</th>
				<td class="indent5">&nbsp;</td>
				<th>년도</th>
				<td class="indent5">&nbsp;</td>
				<th>관리번호</th>
				<td class="indent5">
					<input type="text" name="kekNumber" class="width-200">
				</td>
			</tr>
			<tr>
				<th>부품도구분</th>
				<td class="indent5">&nbsp;</td>
				<th>진행상태</th>
				<td class="indent5">
					<select name="state" id="state" class="width-200">
						<option value="">선택</option>
						<option value="진행중">진행중</option>
						<option value="완료">완료</option>
						<option value="폐기">폐기</option>
					</select>
				</td>
				<th>작성부서</th>
				<td class="indent5">&nbsp;</td>
				<th>작성자</th>
				<td class="indent5">
					<input type="text" name="kekNumber" class="width-200">
				</td>
			</tr>
		</table>

		<table class="button-table">
			<tr>
				<td class="left">
					<img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="엑셀 다운로드" onclick="exportExcel();">
					<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('numberRule-list');">
					<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('numberRule-list');">
					<input type="button" value="저장" title="저장" onclick="save();">
					<input type="button" value="개정" title="개정" class="red" onclick="revise();">
					<input type="button" value="결재" title="결재" class="red" onclick="register();">
					<input type="button" value="행 삭제" title="행 삭제" class="red" onclick="deleteRow();">
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
					<input type="button" value="조회" title="조회" onclick="loadGridData();">
				</td>
			</tr>
		</table>

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
			const list = [ "사용", "폐기" ];
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
					dataField : "size",
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
				// 					editable : false
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
					dataField : "businessSector",
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
					dataField : "drawingCompany",
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
					dataField : "classificationWritingDepartments",
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
					dataField : "writtenDocuments",
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
					editable : true,
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
						list : list,
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							let isValid = false;
							for (let i = 0, len = list.length; i < len; i++) {
								if (list[i] == newValue) {
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
					dataField : "modifiedDate_txt",
					headerText : "수정일",
					dataType : "string",
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
					showRowNumColumn : true,
					showRowCheckColumn : true,
					showStateColumn : true,
					rowNumHeaderText : "번호",
					showAutoNoDataMessage : false,
					enableFilter : true,
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
				AUIGrid.bind(myGridID, "cellEditBegin", auiCellEditBeginHandler);
				AUIGrid.bind(myGridID, "cellEditEnd", auiCellEditEndHandler);
			}

			function register() {
				const url = getCallUrl("/numberRule/register");
				popup(url);
			}

			function auiCellEditEndHandler(event) {
				const item = event.item;
				const rowIndex = event.rowIndex;
				const dataField = event.dataField;
				const value = event.value;
				if (dataField === "classificationWritingDepartments") {
					const newNumber = "K" + value;
					AUIGrid.setCellValue(myGridID, rowIndex, "number", newNumber);
				}

				if (dataField === "writtenDocuments") {
					const value1 = AUIGrid.getCellValue(myGridID, rowIndex, "classificationWritingDepartments");
					const newNumber = "K" + value1 + value;
					const url = getCallUrl("/numberRule/last?number=" + newNumber);
					call(url, null, function(data) {
						console.log(data);
						const next = data.next;
						AUIGrid.setCellValue(myGridID, rowIndex, "number", newNumber + next);
					}, "GET");
				}

				const lotNo = item.lotNo;
				if (dataField === "lotNo") {
					const url = getCallUrl("/erp/getUnitName?lotNo=" + lotNo);
					call(url, null, function(data) {
						if (data.result) {
							const newItem = {
								unitName : data.unitName,
							};
							AUIGrid.updateRow(myGridID, newItem, rowIndex);
						}
					}, "GET");
				}
			}

			function auiCellEditBeginHandler(event) {
				const dataField = event.dataField;
				const rowIndex = event.rowIndex;
				if (dataField === "writtenDocuments") {
					const value = AUIGrid.getCellValue(myGridID, rowIndex, "classificationWritingDepartments");
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
					// 					const latest = checkedItems[i].item.latest;
					const rowIndex = checkedItems[i].rowIndex;
					// 					if (!latest) {
					// 						alert("최신버전이 아닌 도면이 포함되어있습니다.\n" + (rowIndex + 1) + "행 데이터");
					// 						return false;
					// 					}

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
				const params = new Object();
				const url = getCallUrl("/numberRule/list");
				const psize = document.getElementById("psize").value;
				params.psize = psize;
				AUIGrid.showAjaxLoader(myGridID);
				parent.openLayer();
				call(url, params, function(data) {
					console.log(data);
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
				item.drawingCompany = "K";
				// 				item.businessSector = "국제엘렉트릭코리아(주)";
				item.businessSector = "K";
				item.number = "K";
				item.state = "사용";
				AUIGrid.addRow(myGridID, item, "first");
			}

			function exportExcel() {
				const exceptColumnFields = [];
				const sessionName = document.getElementById("sessionName").value;
				exportToExcel("KEK 도번 리스트", "KEK 도번", "KEK 도번 리스트", exceptColumnFields, sessionName);
			}

			document.addEventListener("DOMContentLoaded", function() {
				const columns = loadColumnLayout("numberRule-list");
				const contenxtHeader = genColumnHtml(columns);
				$("#h_item_ul").append(contenxtHeader);
				$("#headerMenu").menu({
					select : headerMenuSelectHandler
				});
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);
				selectbox("psize");
				selectbox("businessSector");
				selectbox("state");
				selectbox("size");
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