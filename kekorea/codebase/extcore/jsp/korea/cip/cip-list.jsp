<%@page import="net.sf.json.JSONArray"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.ArrayList"%>
<%@page import="wt.org.WTUser"%>
<%@page import="java.util.HashMap"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
JSONArray maks = (JSONArray) request.getAttribute("maks");
JSONArray installs = (JSONArray) request.getAttribute("installs");
JSONArray customers = (JSONArray) request.getAttribute("customers");
WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
ArrayList<Map<String, String>> customer_list = (ArrayList<Map<String, String>>) request.getAttribute("customer_list");
ArrayList<Map<String, String>> mak_list = (ArrayList<Map<String, String>>) request.getAttribute("mak_list");
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
				<th>항목</th>
				<td class="indent5">
					<input type="text" name="item" id="item" class="width-200">
				</td>
				<th>개선내용</th>
				<td class="indent5">
					<input type="text" name="improvements" id="improvements" class="width-200">
				</td>
				<th>개선책</th>
				<td class="indent5">
					<input type="text" name="improvement" id="improvement" class="width-200">
				</td>
				<th>적용/미적용</th>
				<td class="indent5">
					<select name="apply" id="apply" class="width-200">
						<option value="">선택</option>
						<option value="">적용</option>
						<option value="">미적용</option>
					</select>
				</td>
			</tr>
			<tr>
				<th>막종</th>
				<td class="indent5">
					<select name="mak" id="mak" class="width-200">
						<option value="">선택</option>
						<%
						for (Map<String, String> map : mak_list) {
							String oid = map.get("key");
							String name = map.get("value");
						%>
						<option value="<%=oid%>"><%=name%></option>
						<%
						}
						%>
					</select>
				</td>
				<th>막종상세</th>
				<td class="indent5">
					<select name="detail" id="detail" class="width-200">
						<option value="">선택</option>
					</select>
				</td>
				<th>거래처</th>
				<td class="indent5">
					<select name="customer" id="customer" class="width-200">
						<option value="">선택</option>
						<%
						for (Map<String, String> map : customer_list) {
							String oid = map.get("key");
							String name = map.get("value");
						%>
						<option value="<%=oid%>"><%=name%></option>
						<%
						}
						%>
					</select>
				</td>
				<th>설치장소</th>
				<td class="indent5">
					<select name="install" id="install" class="width-200">
						<option value="">선택</option>
					</select>
				</td>
			</tr>
		</table>

		<table class="button-table">
			<tr>
				<td class="left">
					<img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="엑셀 다운로드" onclick="exportExcel();">
					<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('cip-list');">
					<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('cip-list');">
					<input type="button" value="저장" title="저장" onclick="save();">
					<input type="button" value="행 추가" title="행 추가" class="blue" onclick="addRow();">
					<input type="button" value="행 삭제" title="행 삭제" class="red" onclick="deleteRow();">
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
			const maks =
		<%=maks%>
			const installs =
		<%=installs%>
			const customers =
		<%=customers%>
			let recentGridItem = null;
			let detailMap = {};
			let installMap = {};
			const list = [ "적용완료", "일부적용", "미적용", "검토중" ];
			function _layout() {
				return [ {
					dataField : "item",
					headerText : "항목",
					dataType : "string",
					width : 120,
					editRenderer : {
						type : "InputEditRenderer",
						validator : function(oldValue, newValue, item, dataField) {
							let isValid = true;
							if (newValue === "") {
								isValid = false;
							}
							return {
								"validate" : isValid,
								"message" : "항목은 공백을 입력 할 수 없습니다."
							};
						}
					},
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "improvements",
					headerText : "개선내용",
					dataType : "string",
					width : 300,
					editRenderer : {
						type : "InputEditRenderer",
						validator : function(oldValue, newValue, item, dataField) {
							let isValid = true;
							if (newValue === "") {
								isValid = false;
							}
							return {
								"validate" : isValid,
								"message" : "개선내용은 공백을 입력 할 수 없습니다."
							};
						}
					},
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "improvement",
					headerText : "개선책",
					dataType : "string",
					width : 300,
					editRenderer : {
						type : "InputEditRenderer",
						validator : function(oldValue, newValue, item, dataField) {
							let isValid = true;
							if (newValue === "") {
								isValid = false;
							}
							return {
								"validate" : isValid,
								"message" : "개선책 공백을 입력 할 수 없습니다."
							};
						}
					},
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "apply",
					headerText : "적용/미적용",
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
					dataField : "mak_code",
					headerText : "막종",
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
						list : maks,
						keyField : "key",
						valueField : "value",
						descendants : [ "detail_code" ],
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							let isValid = false;
							for (let i = 0, len = maks.length; i < len; i++) {
								if (maks[i]["value"] == newValue) {
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
						for (let i = 0, len = maks.length; i < len; i++) {
							if (maks[i]["key"] == value) {
								retStr = maks[i]["value"];
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
					dataField : "detail_code",
					headerText : "막종상세",
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
						keyField : "key",
						valueField : "value",
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							const param = item.mak_code;
							const dd = detailMap[param];
							if (dd === undefined)
								return;
							let isValid = false;
							for (let i = 0, len = dd.length; i < len; i++) {
								if (dd[i]["value"] == newValue) {
									isValid = true;
									break;
								}
							}
							return {
								"validate" : isValid,
								"message" : "리스트에 있는 값만 선택(입력) 가능합니다."
							};
						},
						listFunction : function(rowIndex, columnIndex, item, dataField) {
							const param = item.mak_code;
							const dd = detailMap[param];
							if (dd === undefined) {
								return [];
							}
							return dd;
						},
					},
					labelFunction : function(rowIndex, columnIndex, value, headerText, item) {
						let retStr = "";
						const param = item.mak_code;
						const dd = detailMap[param];
						if (dd === undefined)
							return value;
						for (let i = 0, len = dd.length; i < len; i++) {
							if (dd[i]["key"] == value) {
								retStr = dd[i]["value"];
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
					dataField : "customer_code",
					headerText : "거래처",
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
						list : customers,
						keyField : "key",
						valueField : "value",
						descendants : [ "install_code" ],
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							let isValid = false;
							for (let i = 0, len = customers.length; i < len; i++) {
								if (customers[i]["value"] == newValue) {
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
						for (let i = 0, len = customers.length; i < len; i++) {
							if (customers[i]["key"] == value) {
								retStr = customers[i]["value"];
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
					dataField : "install_code",
					headerText : "설치장소",
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
						keyField : "key",
						valueField : "value",
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							const param = item.customer_code;
							const dd = installMap[param];
							if (dd === undefined)
								return;
							let isValid = false;
							for (let i = 0, len = dd.length; i < len; i++) {
								if (dd[i]["value"] == newValue) {
									isValid = true;
									break;
								}
							}
							return {
								"validate" : isValid,
								"message" : "리스트에 있는 값만 선택(입력) 가능합니다."
							};
						},
						listFunction : function(rowIndex, columnIndex, item, dataField) {
							const param = item.customer_code;
							const dd = installMap[param];
							if (dd === undefined) {
								return [];
							}
							return dd;
						},
					},
					labelFunction : function(rowIndex, columnIndex, value, headerText, item) {
						let retStr = "";
						const param = item.customer_code;
						const dd = installMap[param];
						if (dd === undefined)
							return value;
						for (let i = 0, len = dd.length; i < len; i++) {
							if (dd[i]["key"] == value) {
								retStr = dd[i]["value"];
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
					dataField : "note",
					headerText : "비고",
					dataType : "string",
					width : 150,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "preView",
					headerText : "미리보기",
					width : 100,
					editable : false,
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
					dataField : "preViewCacheId",
					headerText : "",
					width : 100,
					editable : false,
					renderer : {
						type : "ButtonRenderer",
						labelText : "파일선택",
						onclick : function(rowIndex, columnIndex, value, item) {
							recentGridItem = item;
							const oid = item._$uid;
							const url = getCallUrl("/aui/primary?oid=" + oid + "&method=preView");
							popup(url, 1000, 300);
						}
					},
					filter : {
						showIcon : false,
						inline : false
					},
				}, {
					dataField : "icons",
					headerText : "첨부파일",
					width : 100,
					editable : false,
					renderer : {
						type : "TemplateRenderer",
					},
					filter : {
						showIcon : false,
						inline : false
					},
				}, {
					dataField : "",
					headerText : "",
					width : 100,
					editable : false,
					renderer : {
						type : "ButtonRenderer",
						labelText : "파일선택",
						onclick : function(rowIndex, columnIndex, value, item) {
							recentGridItem = item;
							const oid = item._$uid;
							const url = getCallUrl("/aui/secondary?oid=" + oid + "&method=attach");
							popup(url, 1000, 400);
						}
					},
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
					dataField : "createdDate",
					headerText : "작성일",
					dataType : "date",
					formatString : "yyyy-mm-dd",
					width : 100,
					editable : false,
					filter : {
						showIcon : true,
						inline : true,
						displayFormatValues : true
					},
				} ]
			}

			function createAUIGrid(columns) {
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
					filterLayerWidth : 320,
					filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
					editable : true,
					enterKeyColumnBase : true
				};
				myGridID = AUIGrid.create("#grid_wrap", columns, props);
				loadGridData();
				AUIGrid.bind(myGridID, "cellEditEnd", auiCellEditEndHandler);
				AUIGrid.bind(myGridID, "vScrollChange", function(event) {
					hideContextMenu();
					vScrollChangeHandler(event);
				});
				AUIGrid.bind(myGridID, "hScrollChange", function(event) {
					hideContextMenu();
				});
				AUIGrid.bind(myGridID, "cellClick", auiCellClickHandler);
				AUIGrid.bind(myGridID, "cellEditBegin", auiCellEditBegin);
				AUIGrid.bind(myGridID, "ready", readyHandler);
			}

			function readyHandler() {
				const item = AUIGrid.getGridData(myGridID);
				for (let i = 0; i < item.length; i++) {
					if (detailMap.length === undefined) {
						const mak = item[i].mak_code;
						const url = getCallUrl("/commonCode/getChildrens?parentCode=" + mak + "&codeType=MAK");
						call(url, null, function(data) {
							detailMap[mak] = data.list;
						}, "GET");
					}

					if (installMap.length === undefined) {
						const customer = item[i].customer_code;
						const url = getCallUrl("/commonCode/getChildrens?parentCode=" + customer + "&codeType=CUSTOMER");
						call(url, null, function(data) {
							installMap[customer] = data.list;
						}, "GET");
					}
				}
			}

			function auiCellEditBegin(event) {
				const item = event.item;
				const sessionId = document.getElementById("sessionId").value;
				if (!checker(sessionId, item.creatorId)) {
					alert("데이터 작성자가 아닙니다.");
					return false;
				}
			}

			function auiCellClickHandler(event) {
				const dataField = event.dataField;
				const oid = event.item.oid;
				const preView = event.item.preView;
				if (dataField === "preView") {
					if (!isNull(preView) && !isNull(oid)) {
						const url = getCallUrl("/aui/thumbnail?oid=" + oid);
						popup(url);
					}
				}
			}

			function auiCellEditEndHandler(event) {
				const dataField = event.dataField;
				const item = event.item;
				const rowIndex = event.rowIndex;
				if (dataField === "mak_code") {
					const mak = item.mak_code;
					const url = getCallUrl("/commonCode/getChildrens?parentCode=" + mak + "&codeType=MAK");
					call(url, null, function(data) {
						detailMap[mak] = data.list;
					}, "GET");
				}

				if (dataField === "customer_code") {
					const customer = item.customer_code;
					const url = getCallUrl("/commonCode/getChildrens?parentCode=" + customer + "&codeType=CUSTOMER");
					call(url, null, function(data) {
						installMap[customer] = data.list;
					}, "GET");
				}
			}

			function loadGridData() {
				const params = new Object();
				const url = getCallUrl("/cip/list");
				const item = document.getElementById("item").value;
				const improvements = document.getElementById("improvements").value;
				const improvement = document.getElementById("improvement").value;
				const apply = document.getElementById("apply").value;
				const mak = document.getElementById("mak").value;
				const detail = document.getElementById("detail").value;
				const customer = document.getElementById("customer").value;
				const install = document.getElementById("install").value;
				const psize = document.getElementById("psize").value;
				params.item = item;
				params.improvements = improvements;
				params.improvement = improvement;
				params.apply = apply;
				params.mak = mak;
				params.detail = detail;
				params.customer = customer;
				params.install = install;
				params.psize = psize;
				AUIGrid.showAjaxLoader(myGridID);
				parent.openLayer();
				console.log(params);
				call(url, params, function(data) {
					console.log(data);
					AUIGrid.removeAjaxLoader(myGridID);
					document.getElementById("sessionid").value = data.sessionid;
					document.getElementById("curPage").value = data.curPage;
					AUIGrid.setGridData(myGridID, data.list);
					parent.closeLayer();
				});
			}

			function preView(data) {
				const preView = data.base64;
				AUIGrid.updateRowsById(myGridID, {
					_$uid : recentGridItem._$uid,
					preView : preView,
					preViewCacheId : data.cacheId
				});
			}

			function attach(data) {
				let template = "";
				const arr = new Array();
				for (let i = 0; i < data.length; i++) {
					template += "<img style='position: relative; top: 2px' src='" + data[i].icon + "'>&nbsp;";
					arr.push(data[i].cacheId);
				}

				AUIGrid.updateRowsById(myGridID, {
					_$uid : recentGridItem._$uid,
					secondarys : arr,
					icons : template
				});
			}

			function addRow() {
				const item = new Object();
				item.latest = true;
				item.creator = document.getElementById("sessionName").value;
				item.creatorId = document.getElementById("sessionId").value;
				AUIGrid.addRow(myGridID, item, "first");
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
				const url = getCallUrl("/cip/save");
				const params = new Object();
				const addRows = AUIGrid.getAddedRowItems(myGridID);
				const removeRows = AUIGrid.getRemovedItems(myGridID);
				const editRows = AUIGrid.getEditedRowItems(myGridID);

				if (addRows.length == 0 && removeRows.length == 0 && editRows.length == 0) {
					alert("변경된 내용이 없습니다.");
					return false;
				}

				for (let i = 0; i < addRows.length; i++) {
					const item = addRows[i];
					const rowIndex = AUIGrid.rowIdToIndex(myGridID, item._$uid);
					if (isNull(item.item)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 0, "항목 값은 공백을 입력 할 수 없습니다.");
						return false;
					}
					if (isNull(item.improvements)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 1, "개선내용 값은 공백을 입력 할 수 없습니다.");
						return false;
					}
					if (isNull(item.improvement)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 2, "개선책 값은 공백을 입력 할 수 없습니다.");
						return false;
					}
					if (isNull(item.apply)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 3, "적용/미적용 값을 선택하세요.");
						return false;
					}
					if (isNull(item.mak_code)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 4, "막종을 선택하세요.");
						return false;
					}
					if (isNull(item.detail_code)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 5, "막종상세를 선택하세요.");
						return false;
					}
					if (isNull(item.customer_code)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 6, "거래처를 선택하세요.");
						return false;
					}
					if (isNull(item.install_code)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 7, "설치장소를 선택하세요.");
						return false;
					}
					if (isNull(item.preView)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 9, "미리보기를 선택하세요.");
						return false;
					}
					if (isNull(item.icons)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 11, "첨부파일을 선택하세요.");
						return false;
					}
				}

				for (let i = 0; i < editRows.length; i++) {
					const item = editRows[i];
					const rowIndex = AUIGrid.rowIdToIndex(myGridID, item._$uid);
					if (isNull(item.item)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 0, "항목 값은 공백을 입력 할 수 없습니다.");
						return false;
					}
					if (isNull(item.improvements)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 1, "개선내용 값은 공백을 입력 할 수 없습니다.");
						return false;
					}
					if (isNull(item.improvement)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 2, "개선책 값은 공백을 입력 할 수 없습니다.");
						return false;
					}
					if (isNull(item.apply)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 3, "적용/미적용 값을 선택하세요.");
						return false;
					}
					if (isNull(item.mak_code)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 4, "막종을 선택하세요.");
						return false;
					}
					if (isNull(item.detail_code)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 5, "막종상세를 선택하세요.");
						return false;
					}
					if (isNull(item.customer_code)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 6, "거래처를 선택하세요.");
						return false;
					}
					if (isNull(item.install_code)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 7, "설치장소를 선택하세요.");
						return false;
					}
					if (isNull(item.preView)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 9, "미리보기를 선택하세요.");
						return false;
					}
					if (isNull(item.icons)) {
						AUIGrid.showToastMessage(myGridID, rowIndex, 11, "첨부파일을 선택하세요.");
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
				console.log(params);
				call(url, params, function(data) {
					alert(data.msg);
					if (data.result) {
						loadGridData();
					} else {
						parent.closeLayer();
					}
				});
			}

			function exportExcel() {
				const exceptColumnFields = [ "preView", "preViewBtn", "icons", "iconsBtn" ];
				const sessionName = document.getElementById("name").value;
				exportToExcel("CIP 리스트", "CIP", "CIP 리스트", exceptColumnFields, sessionName);
			}

			document.addEventListener("DOMContentLoaded", function() {
				const columns = loadColumnLayout("cip-list");
				const contenxtHeader = genColumnHtml(columns);
				$("#h_item_ul").append(contenxtHeader);
				$("#headerMenu").menu({
					select : headerMenuSelectHandler
				});
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);
				selectbox("apply");
				$("#mak").bindSelect({
					onchange : function() {
						const oid = this.optionValue;
						$("#detail").bindSelect({
							ajaxUrl : getCallUrl("/commonCode/getChildrens?parentOid=" + oid),
							reserveKeys : {
								options : "list",
								optionValue : "value",
								optionText : "name"
							},
							setValue : this.optionValue,
							alwaysOnChange : true,
						})
					}
				})
				selectbox("detail");
				$("#customer").bindSelect({
					onchange : function() {
						const oid = this.optionValue;
						$("#install").bindSelect({
							ajaxUrl : getCallUrl("/commonCode/getChildrens?parentOid=" + oid),
							reserveKeys : {
								options : "list",
								optionValue : "value",
								optionText : "name"
							},
							setValue : this.optionValue,
							alwaysOnChange : true,
						})
					}
				})
				selectbox("install");
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