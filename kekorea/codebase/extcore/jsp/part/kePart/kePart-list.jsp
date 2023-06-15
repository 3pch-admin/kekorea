<%@page import="wt.org.WTUser"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.admin.commonCode.CommonCodeType"%>
<%@page import="org.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
boolean isSupervisor = (boolean) request.getAttribute("isSupervisor");
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
				<th>LOT</th>
				<td class="indent5">
					<input type="text" name="lotNo" id="lotNo" class="width-200" maxlength="4">
				</td>
				<th>중간코드</th>
				<td class="indent5">
					<input type="text" name="code" id="code" class="width-200">
				</td>
				<th>부품번호</th>
				<td class="indent5">
					<input type="text" name="keNumber" id="keNumber" class="width-200">
				</td>
				<th>부품명</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-200">
				</td>
			</tr>
			<tr>
				<th>상태</th>
				<td class="indent5">
					<select name="state" id="state" class="width-100">
						<option value="">선택</option>
						<option value="사용">사용</option>
						<option value="폐기">폐기</option>
					</select>
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
				<th>KokusaiModel</th>
				<td class="indent5" colspan="3">
					<input type="text" name="model" id="model" class="width-400">
				</td>
			</tr>
			<tr>
				<th>작성자</th>
				<td class="indent5">
					<input type="text" name="creator" id="creator" data-multi="false" class="width-200">
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
				<th>수정자</th>
				<td class="indent5">
					<input type="text" name="modifier" id="modifier" data-multi="false" class="width-200">
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
					<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('kePart-list');">
					<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('kePart-list');">
					<input type="button" value="등록" title="등록" class="blue" onclick="create();">
					<input type="button" value="저장" title="저장" onclick="save();">
					<input type="button" value="개정" title="개정" class="red" onclick="revise();">
					<input type="button" value="행 추가" title="행 추가" class="blue" onclick="addRow();">
					<input type="button" value="행 삭제" title="행 삭제" class="red" onclick="deleteRow();">
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

		<div id="grid_wrap" style="height: 670px; border-top: 1px solid #3180c3;"></div>
		<%@include file="/extcore/jsp/common/aui/aui-context.jsp"%>
		<script type="text/javascript">
			let myGridID;
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
					dataField : "code",
					headerText : "중간코드",
					dataType : "string",
					width : 100,
					editRenderer : {
						type : "InputEditRenderer",
						regExp : "^[a-zA-Z0-9]+$",
						autoUpperCase : true,
						maxlength : 10,
					},
					filter : {
						showIcon : true,
						inline : true,
					},
				}, {
					dataField : "keNumber",
					headerText : "부품번호",
					dataType : "string",
					width : 100,
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const oid = item.oid;
							if (oid !== undefined) {
								const url = getCallUrl("/kePart/view?oid=" + oid);
								popup(url, 1400, 600);
							} else {
								alert("서버에 저장되지 않은 데이터 입니다.");
							}
						}
					},
					editRenderer : {
						type : "InputEditRenderer",
						regExp : "^[a-zA-Z0-9]+$",
						autoUpperCase : true,
						maxlength : 10,
					},
					filter : {
						showIcon : true,
						inline : true,
					},
				}, {
					dataField : "name",
					headerText : "부품명",
					dataType : "string",
					width : 200,
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const oid = item.oid;
							if (oid !== undefined) {
								const url = getCallUrl("/kePart/view?oid=" + oid);
								popup(url, 1400, 600);
							} else {
								alert("서버에 저장 되지 않은 데이터 입니다.");
							}
						}
					},
					editRenderer : {
						type : "InputEditRenderer",
						autoUpperCase : true
					},
					filter : {
						showIcon : true,
						inline : true,
					},
				}, {
					dataField : "model",
					headerText : "KokusaiModel",
					dataType : "string",
					width : 300,
					editRenderer : {
						type : "InputEditRenderer",
						autoUpperCase : true
					},
					filter : {
						showIcon : true,
						inline : true,
					},
				}, {
					dataField : "version",
					headerText : "버전",
					dataType : "numeric",
					formatString : "###0",
					width : 80,
					editable : false,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "latest",
					headerText : "최신버전",
					dataType : "string",
					width : 80,
					renderer : {
						type : "CheckBoxEditRenderer",
						edtiable : false,
					},
					filter : {
						showIcon : false,
						inline : false
					},
				}, {
					dataField : "state",
					headerText : "상태",
					dataType : "string",
					width : 100,
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
					headerText : "등록자",
					dataType : "string",
					width : 100,
					editable : false,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "createdDate",
					headerText : "등록일",
					dataType : "date",
					formatString : "yyyy-mm-dd",
					width : 100,
					editable : false,
					filter : {
						showIcon : true,
						inline : true,
						displayFormatValues : true
					},
				}, {
					dataField : "modifier",
					headerText : "수정자",
					dataType : "string",
					width : 100,
					editable : false,
					filter : {
						showIcon : true
					}
				}, {
					dataField : "modifiedDate",
					headerText : "수정일",
					dataType : "date",
					formatString : "yyyy-mm-dd",
					width : 100,
					editable : false,
					filter : {
						showIcon : true,
						inline : true,
						displayFormatValues : true
					},
				}, {
					dataField : "primary",
					headerText : "첨부파일",
					dataType : "string",
					width : 100,
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
					width : 100,
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
				AUIGrid.bind(myGridID, "beforeRemoveRow", auiBeforeRemoveRowHandler);
				AUIGrid.bind(myGridID, "pasteEnd", auiPasteEnd);
				AUIGrid.bind(myGridID, "keyDown", auiKeyDownHandler);
			}

			// enter 키 행 추가
			function auiKeyDownHandler(event) {
				if (event.keyCode == 13) { // 엔터 키
					var selectedItems = AUIGrid.getSelectedItems(event.pid);
					var rowIndex = selectedItems[0].rowIndex;
					if (rowIndex === AUIGrid.getRowCount(event.pid) - 1) { // 마지막 행인지 여부 
						const item = {
							latest : true,
							state : "사용",
							version : 1
						};
						AUIGrid.addRow(event.pid, item); // 행 추가
						return false; // 엔터 키의 기본 행위 안함.
					}
				}
				return true; // 기본 행위 유지
			}

			function auiBeforeRemoveRowHandler(event) {
				const items = event.items;
				for (let i = 0; i < items.length; i++) {
					const latest = items[i].latest;
					const isNew = items[i].isNew;
					if (!latest && !isNull(isNew)) {
						alert("최신버전의 부품이 아닌 데이터가 있습니다.\n" + i + "행 데이터");
						return false;
					}
				}
				return true;
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

			function loadGridData() {
				let params = new Object();
				const url = getCallUrl("/kePart/list");
				const field = [ "lotNo", "keNumber", "name", "code", "model", "state", "creatorOid", "createdFrom", "createdTo", "modifierOid", "modifiedFrom", "modifiedTo", "_psize" ];
				const latest = !!document.querySelector("input[name=latest]:checked").value;
				params = toField(params, field);
				params.latest = latest;
				AUIGrid.showAjaxLoader(myGridID);
				parent.openLayer();
				call(url, params, function(data) {
					AUIGrid.removeAjaxLoader(myGridID);
					document.getElementById("sessionid").value = data.sessionid;
					document.getElementById("curPage").value = data.curPage;
					document.getElementById("lastNum").value = data.list.length;
					AUIGrid.setGridData(myGridID, data.list);
					parent.closeLayer();
				});
			}

			function save() {
				const url = getCallUrl("/kePart/save");
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

					if (isNull(item.code)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 1, "중간코드의 값은 공백을 입력 할 수 없습니다.");
						return false;
					}

					if (isNull(item.keNumber)) {
						AUIGrid.showToastMessage(myGridID, i, 2, "부품번호의 값은 공백을 입력 할 수 없습니다.");
						return false;
					}

					if (isNull(item.name)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 3, "부품명 값은 공백을 입력 할 수 없습니다.");
						return false;
					}

					if (isNull(item.model)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 4, "KokusaiModel 값은 공백을 입력 할 수 없습니다.");
						return false;
					}
				}

				for (let i = 0; i < editRows.length; i++) {
					const item = editRows[i];
					const rowIndex = AUIGrid.rowIdToIndex(myGridID, item._$uid);
					if (isNull(item.lotNo) || item.lotNo === 0) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 0, "LOT NO의 값은 0혹은 공백을 입력 할 수 없습니다.");
						return false;
					}

					if (isNull(item.code)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 1, "중간코드의 값은 공백을 입력 할 수 없습니다.");
						return false;
					}

					if (isNull(item.keNumber)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 2, "부품번호의 값은 공백을 입력 할 수 없습니다.");
						return false;
					}

					if (isNull(item.name)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 3, "부품명 값은 공백을 입력 할 수 없습니다.");
						return false;
					}

					if (isNull(item.model)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 4, "KokusaiModel 값은 공백을 입력 할 수 없습니다.");
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

			function addRow() {
				const item = {
					latest : true,
					state : "사용",
					version : 1
				};
				AUIGrid.addRow(myGridID, item, "first");
			}

			function deleteRow() {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				const sessionId = document.getElementById("sessionId").value;
				for (let i = checkedItems.length - 1; i >= 0; i--) {
					const item = checkedItems[i].item;
					const rowIndex = checkedItems[i].rowIndex;
					if ((!isNull(item.creatorId) && !checker(sessionId, item.creatorId))) {
						// 					if ((!isNull(item.creatorId) && !checker(sessionId, item.creatorId)) || (!isNull(item.modifierId) && !checker(sessionId, item.modifierId))) {
						alert(rowIndex + "행 데이터의 작성자 혹은 수정자가 아닙니다.");
						return false;
					}
					AUIGrid.removeRow(myGridID, rowIndex);
				}
			}

			function attach(data) {
				const template = "<img src='" + data.icon + "' style='position: relative; top: 2px;'>";
				AUIGrid.updateRowsById(myGridID, {
					_$uid : recentGridItem._$uid,
					primary : template,
					cacheId : data.cacheId
				});
			}

			function revise() {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				if (checkedItems.length == 0) {
					alert("개정할 부품을 선택하세요.");
					return false;
				}

				for (let i = 0; i < checkedItems.length; i++) {
					const oid = checkedItems[i].item.oid;
					const latest = checkedItems[i].item.latest;
					const rowIndex = checkedItems[i].rowIndex;
					const state = checkedItems[i].state;
					checkedItems[i].item.note = ""; // 개정사유는 초기화한다.
					if (state !== "승인됨") {
						// 						alert("승인되지 않은 부품이 포함되어있습니다.\n" + rowIndex + "행 데이터");
						// 						return false;
					}

					if (!latest) {
						alert("최신버전이 아닌 부품이 포함되어있습니다.\n" + rowIndex + "행 데이터");
						return false;
					}

					if (oid === undefined) {
						alert("신규로 작성한 데이터가 존재합니다.\n" + rowIndex + "행 데이터");
						return false;
					}
				}
				const panel = popup("/Windchill/plm/kePart/revise", 1600, 550);
				panel.list = checkedItems;
			}

			function create() {
				const url = getCallUrl("/kePart/create");
				popup(url, 1600, 650);
			}

			function exportExcel() {
				const exceptColumnFields = [ "button", "primary", "latest" ];
				const sessionName = document.getElementById("sessionName").value;
				exportToExcel("KE 부품 리스트", "KE 부품", "KE 부품 리스트", exceptColumnFields, sessionName);
			}

			document.addEventListener("DOMContentLoaded", function() {
				toFocus("lotNo");
				const columns = loadColumnLayout("kePart-list");
				const contenxtHeader = genColumnHtml(columns);
				$("#h_item_ul").append(contenxtHeader);
				$("#headerMenu").menu({
					select : headerMenuSelectHandler
				});
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);
				selectbox("state");
				finderUser("creator");
				twindate("created");
				finderUser("modifier");
				twindate("modified");
				selectbox("_psize");
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