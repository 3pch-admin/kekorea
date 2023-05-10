<%@page import="java.sql.Timestamp"%>
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
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>    
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js?v=1010"></script>
<style type="text/css">
.preView {
	background-color: #caf4fd;
	cursor: pointer;
}
</style>
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
				<th>DRAWING TITLE</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-200">
				</td>
				<th>LOT NO</th>
				<td class="indent5">
					<input type="number" name="lotNo" id="lotNo" class="width-200">
				</td>
				<th>작성자</th>
				<td class="indent5">
					<input type="text" name="creator" id="creator" class="width-200">
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
			</tr>
			<tr>
				<th>DWG NO</th>
				<td class="indent5">
					<input type="text" name="keNumber" id="keNumber" class="width-200">
				</td>
				<th>버전</th>
				<td>
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
				<th>수정자</th>
				<td class="indent5">
					<input type="text" name="modifier" id="modifier" class="width-200">
					<input type="hidden" name="modifierOid" id="modifierOid">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('modifier')">
				</td>
				<th>수정일</th>
				<td class="indent5">
					<input type="text" name="modifiedFrom" id="modifiedFrom" class="width-100">
					~
					<input type="text" name="modifiedTo" id="modifiedTo" class="width-100">
					<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearFromTo('modifiedFrom', 'modifiedTo')">
				</td>
			</tr>
		</table>

		<table class="button-table">
			<tr>
				<td class="left">
					<img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="엑셀 다운로드" onclick="exportExcel();">
					<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('keDrawing-list');">
					<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('keDrawing-list');">
					<input type="button" value="저장" title="저장" onclick="create();">
					<input type="button" value="개정" title="개정" class="red" onclick="revise();">
					<input type="button" value="행 추가" title="행 추가" class="blue" onclick="addRow();">
					<%
					if (isAdmin) {
					%>
					<input type="button" value="행 삭제" title="행 삭제" class="red" onclick="deleteRow();">
					<%
					}
					%>
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
			let recentGridItem = null;
			const list = [ "사용", "폐기" ];
			function _layout() {
				return [ {
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
					dataField : "name",
					headerText : "DRAWING TITLE",
					dataType : "string",
					style : "aui-left",
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const oid = item.oid;
							if (oid === undefined) {
								return false;
							}
							const moid = item.moid;
							const url = getCallUrl("/keDrawing/view?oid=" + oid);
							popup(url, 1400, 620);
						}
					},
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "keNumber",
					headerText : "DWG NO",
					dataType : "string",
					width : 100,
					editable : false,
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const oid = item.oid;
							if (oid === undefined) {
								return false;
							}
							const moid = item.moid;
							const url = getCallUrl("/keDrawing/view?oid=" + oid);
							popup(url, 1400, 620);
						}
					},
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "version",
					headerText : "버전",
					dataType : "numeric",
					width : 80,
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
					dataField : "latest",
					headerText : "최신버전",
					dataType : "boolean",
					width : 80,
					renderer : {
						type : "CheckBoxEditRenderer"
					},
					editable : false,
					filter : {
						showIcon : false,
						inline : false
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
						inline : true,
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
						inline : true,
					},
				}, {
					dataField : "preView",
					headerText : "미리보기",
					width : 80,
					editable : false,
					style : "preView",
					renderer : {
						type : "ImageRenderer",
						altField : null,
						imgHeight : 34,
					},
					filter : {
						showIcon : false,
						inline : false
					},
				}, {
					dataField : "primary",
					headerText : "도면파일",
					dataType : "string",
					width : 80,
					editable : false,
					renderer : {
						type : "TemplateRenderer",
					},
					filter : {
						showIcon : false,
						inline : false
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
				}, {
					dataField : "note",
					headerText : "개정사유",
					dateType : "string",
					width : 250,
					style : "aui-left",
					editable : false,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "isNew",
					dataType : "boolean",
					visible : false
				} ]
			}

			function createAUIGrid(columnLayout) {
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
					editable : true,
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
				AUIGrid.bind(myGridID, "beforeRemoveRow", auiBeforeRemoveRowHandler);
				AUIGrid.bind(myGridID, "cellClick", auiCellClickHandler);
				AUIGrid.bind(myGridID, "pasteEnd", auiPasteEnd);
			}

			function auiPasteEnd(event) {
				const clipboardData = event.clipboardData;
				for (let i = 0; i < clipboardData.length; i++) {
					AUIGrid.setCellValue(myGridID, i, "latest", true);
					AUIGrid.setCellValue(myGridID, i, "state", "사용");
					AUIGrid.setCellValue(myGridID, i, "version", 1);
					AUIGrid.setCellValue(myGridID, i, "isNew", true);
				}
			}

			function auiCellClickHandler(event) {
				const dataField = event.dataField;
				const oid = event.item.oid;
				const preView = event.item.preView;
				if (dataField === "preView") {
					if (preView === null) {
						alert("미리보기 파일이 생성되어있지 않습니다.");
						return false;
					}
					const url = getCallUrl("/aui/thumbnail?oid=" + oid);
					popup(url);
				}
			}

			function auiBeforeRemoveRowHandler(event) {
				const items = event.items;
				for (let i = 0; i < items.length; i++) {
					const latest = items[i].latest;
					const isNew = items[i].isNew;
					if (!latest && !isNull(isNew)) {
						alert("최신버전의 도면이 아닌 데이터가 있습니다.\n" + i + "행 데이터");
						return false;
					}
				}
				return true;
			}

			function loadGridData() {
				const params = new Object();
				const url = getCallUrl("/keDrawing/list");
				const name = document.getElementById("name").value;
				const lotNo = Number(document.getElementById("lotNo").value);
				const creatorOid = document.getElementById("creatorOid").value;
				const createdFrom = document.getElementById("createdFrom").value;
				const createdTo = document.getElementById("createdTo").value;
				const keNumber = document.getElementById("keNumber").value;
				const latest = !!document.querySelector("input[name=latest]:checked").value;
				const modifierOid = document.getElementById("modifierOid").value;
				const modifiedFrom = document.getElementById("modifiedFrom").value;
				const modifiedTo = document.getElementById("modifiedTo").value;
				const psize = document.getElementById("psize").value;
				params.name = name;
				params.lotNo = lotNo;
				params.creatorOid = creatorOid;
				params.createdFrom = createdFrom;
				params.createdTo = createdTo;
				params.keNumber = keNumber;
				params.latest = latest;
				params.modifierOid = modifierOid;
				params.modifiedFrom = modifiedFrom;
				params.modifiedTo = modifiedTo;
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

			function addRow() {
				const item = {
					latest : true,
					preView : null,
					state : "사용"
				};
				AUIGrid.addRow(myGridID, item, "first");
			}

			function deleteRow() {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				const sessionId = document.getElementById("sessionId").value;
				for (let i = checkedItems.length - 1; i >= 0; i--) {
					const item = checkedItems[i].item;
					const rowIndex = checkedItems[i].rowIndex;
					if ((!isNull(item.creatorId) && !checker(sessionId, item.creatorId)) || (!isNull(item.modifierId) && !checker(sessionId, item.modifierId))) {
						alert(rowIndex + "행 데이터의 작성자 혹은 수정자가 아닙니다.");
						return false;
					}
					if (!item.latest) {
						alert("최신버전의 도면이 아닙니다.");
						return false;
					}
					AUIGrid.removeRow(myGridID, rowIndex);
				}
			}

			function attach(data) {
				const name = data.name;
				if (name.length !== 18) {
					alert("도면파일 이름명을 체크하세요. \nDWG NO : 10자리, 버전 3자리의 양식을 맞춰주세요.");
					return false;
				}
				const start = name.indexOf("-");
				if (start <= -1) {
					alert("도면파일 이름의 양식이 맞지 않습니다.\nDWG NO-버전 형태의 파일명만 허용됩니다.");
					return false;
				}
				const end = name.lastIndexOf(".");
				if (end <= -1) {
					alert("도면파일 확장자를 체크해주세요.");
					return false;
				}
				const ext = name.substring(end + 1);
				if (ext.toLowerCase() !== "pdf") {
					alert("PDF 파일 형식의 도면파일만 허용됩니다.");
					return false;
				}
				const number = name.substring(0, start);
				if (number.length !== 10) {
					alert("도면파일의 DWG NO의 자리수를 확인해주세요. 등록가능한 도번의 자리수는 10자리여야 합니다.");
					return false;
				}
				const version = name.substring(start + 1, end);
				if (version.length !== 3) {
					alert("도면파일의 버전 자리수를 확인해주세요. 등록가능한 버전의 자리수는 3자리여야 합니다.");
					return false;
				}

				const template = "<img src='" + data.icon + "' style='position: relative; top: 2px;'>";
				AUIGrid.updateRowsById(myGridID, {
					_$uid : recentGridItem._$uid,
					keNumber : number,
					version : Number(version),
					file : name,
					primary : template,
					cacheId : data.cacheId
				});
			}

			function create() {
				const url = getCallUrl("/keDrawing/save");
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
					if (isNull(item.lotNo) || item.lotNo === 0) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 0, "LOT NO의 값은 0혹은 공백을 입력 할 수 없습니다.");
						return false;
					}

					if (isNull(item.name)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 1, "DRAWING TITLE의 값은 공백을 입력 할 수 없습니다.");
						return false;
					}

					if (isNull(item.primary)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 9, "도면파일을 선택하세요.");
						return false;
					}
				}

				for (let i = 0; i < editRows.length; i++) {
					const item = editRows[i];
					const rowIndex = AUIGrid.rowIdToIndex(myGridID, item._$uid);
					if (isNull(item.lotNo) || item.lotNo === 0) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 0, "LOT NO의 값은 0을 입력 할 수 없습니다.");
						return false;
					}

					if (isNull(item.name)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 1, "DRAWING TITLE의 값은 공백을 입력 할 수 없습니다.");
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

			function revise() {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);

				if (checkedItems.length == 0) {
					alert("개정할 도면을 선택하세요.");
					return false;
				}

				for (let i = 0; i < checkedItems.length; i++) {
					const oid = checkedItems[i].item.oid;
					const latest = checkedItems[i].item.latest;
					const rowIndex = checkedItems[i].rowIndex;
					checkedItems[i].item.note = "";
					checkedItems[i].item.primary = "";
					if (!latest) {
						alert("최신버전이 아닌 도면이 포함되어있습니다.\n" + (rowIndex + 1) + "행 데이터");
						return false;
					}

					if (oid === undefined) {
						alert("신규로 작성한 데이터가 존재합니다.\n" + (rowIndex + 1) + "행 데이터");
						return false;
					}
				}
				const url = getCallUrl("/keDrawing/revise");
				const p = popup(url, 1600, 550);
				p.list = checkedItems;
			}

			function exportExcel() {
				const exceptColumnFields = [ "preView", "button", "primary","latest" ];
				const sessionName = document.getElementById("sessionName").value;
				exportToExcel("KE 도면 리스트", "KE 도면", "KE 도면 리스트", exceptColumnFields, sessionName);
			}

			document.addEventListener("DOMContentLoaded", function() {
				document.getElementById("name").focus();
				const columns = loadColumnLayout("keDrawing-list");
				const contenxtHeader = genColumnHtml(columns);
				$("#h_item_ul").append(contenxtHeader);
				$("#headerMenu").menu({
					select : headerMenuSelectHandler
				});
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);
				finderUser("creator");
				twindate("created");
				finderUser("modifier");
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