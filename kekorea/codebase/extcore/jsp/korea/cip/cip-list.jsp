<%@page import="wt.org.WTUser"%>
<%@page import="java.util.HashMap"%>
<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
JSONArray maks = (JSONArray) request.getAttribute("maks");
JSONArray installs = (JSONArray) request.getAttribute("installs");
JSONArray customers = (JSONArray) request.getAttribute("customers");
WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<!-- CSS 공통 모듈 -->
<%@include file="/extcore/include/css.jsp"%>
<!-- 스크립트 공통 모듈 -->
<%@include file="/extcore/include/script.jsp"%>
<!-- AUIGrid -->
<%@include file="/extcore/include/auigrid.jsp"%>
<!-- AUIGrid 리스트페이지에서만 사용할 js파일 -->
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js?v=1"></script>
</head>
<body>
	<form>
		<!-- 리스트 검색시 반드시 필요한 히든 값 -->
		<input type="hidden" name="sessionid" id="sessionid">
		<input type="hidden" name="curPage" id="curPage">
		<!-- 검색 테이블 -->
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
					<input type="text" name="fileName" class="width-200">
				</td>
				<th>개선내용</th>
				<td class="indent5">
					<input type="text" name="description" class="width-200">
				</td>
				<th>개선책</th>
				<td class="indent5">
					<input type="text" name="description" class="width-200">
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
					<select name="apply" id="apply" class="width-200">
						<option value="">선택</option>
						<option value="">적용</option>
						<option value="">미적용</option>
					</select>
				</td>
				<th>막종상세</th>
				<td class="indent5">
					<select name="apply" id="apply" class="width-200">
						<option value="">선택</option>
						<option value="">적용</option>
						<option value="">미적용</option>
					</select>
				</td>
				<th>거래처</th>
				<td class="indent5">
					<select name="apply" id="apply" class="width-200">
						<option value="">선택</option>
						<option value="">적용</option>
						<option value="">미적용</option>
					</select>
				</td>
				<th>설치장소</th>
				<td class="indent5">
					<select name="apply" id="apply" class="width-200">
						<option value="">선택</option>
						<option value="">적용</option>
						<option value="">미적용</option>
					</select>
				</td>
			</tr>
		</table>

		<!-- 버튼 테이블 -->
		<table class="button-table">
			<tr>
				<td class="left">
					<!-- exportExcel 함수참고 -->
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

		<!-- 그리드 리스트 -->
		<div id="grid_wrap" style="height: 670px; border-top: 1px solid #3180c3;"></div>
		<!-- 컨텍스트 메뉴 사용시 반드시 넣을 부분 -->
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
						iconWidth : 16, // icon 사이즈, 지정하지 않으면 rowHeight에 맞게 기본값 적용됨
						iconHeight : 16,
						iconPosition : "aisleRight",
						iconTableRef : { // icon 값 참조할 테이블 레퍼런스
							"default" : "/Windchill/extcore/component/AUIGrid/images/list-icon.png" // default
						},
						onClick : function(event) {
							// 아이콘을 클릭하면 수정으로 진입함.
							AUIGrid.openInputer(event.pid);
						}
					},
					editRenderer : {
						type : "ComboBoxRenderer",
						autoCompleteMode : true, // 자동완성 모드 설정
						autoEasyMode : true,
						matchFromFirst : false, // 처음부터 매치가 아닌 단순 포함되는 자동완성
						showEditorBtnOver : false, // 마우스 오버 시 에디터버턴 보이기
						list : list,
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							let isValid = false;
							for (let i = 0, len = list.length; i < len; i++) { // keyValueList 있는 값만..
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
						iconWidth : 16, // icon 사이즈, 지정하지 않으면 rowHeight에 맞게 기본값 적용됨
						iconHeight : 16,
						iconPosition : "aisleRight",
						iconTableRef : { // icon 값 참조할 테이블 레퍼런스
							"default" : "/Windchill/extcore/component/AUIGrid/images/list-icon.png" // default
						},
						onClick : function(event) {
							// 아이콘을 클릭하면 수정으로 진입함.
							AUIGrid.openInputer(event.pid);
						}
					},
					editRenderer : {
						type : "ComboBoxRenderer",
						autoCompleteMode : true, // 자동완성 모드 설정
						autoEasyMode : true,
						matchFromFirst : false, // 처음부터 매치가 아닌 단순 포함되는 자동완성
						showEditorBtnOver : false, // 마우스 오버 시 에디터버턴 보이기						
						list : maks, //key-value Object 로 구성된 리스트
						keyField : "key", // key 에 해당되는 필드명
						valueField : "value", // value 에 해당되는 필드명,
						descendants : [ "detail_code" ], // 자손 필드들
						descendantDefaultValues : [ "" ], // 변경 시 자손들에게 기본값 지정
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							let isValid = false;
							for (let i = 0, len = maks.length; i < len; i++) { // keyValueList 있는 값만..
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
					labelFunction : function(rowIndex, columnIndex, value, headerText, item) { // key-value 에서 엑셀 내보내기 할 때 value 로 내보내기 위한 정의
						let retStr = ""; // key 값에 맞는 value 를 찾아 반환함.
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
						iconWidth : 16, // icon 사이즈, 지정하지 않으면 rowHeight에 맞게 기본값 적용됨
						iconHeight : 16,
						iconPosition : "aisleRight",
						iconTableRef : { // icon 값 참조할 테이블 레퍼런스
							"default" : "/Windchill/extcore/component/AUIGrid/images/list-icon.png" // default
						},
						onClick : function(event) {
							// 아이콘을 클릭하면 수정으로 진입함.
							AUIGrid.openInputer(event.pid);
						}
					},
					editRenderer : {
						type : "ComboBoxRenderer",
						autoCompleteMode : true, // 자동완성 모드 설정
						autoEasyMode : true,
						matchFromFirst : false, // 처음부터 매치가 아닌 단순 포함되는 자동완성
						showEditorBtnOver : false, // 마우스 오버 시 에디터버턴 보이기						
						keyField : "key", // key 에 해당되는 필드명
						valueField : "value", // value 에 해당되는 필드명,
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							const param = item.mak_code;
							const dd = detailMap[param]; // param으로 보관된 리스트가 있는지 여부
							let isValid = false;
							for (let i = 0, len = dd.length; i < len; i++) { // keyValueList 있는 값만..
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
							const dd = detailMap[param]; // param으로 보관된 리스트가 있는지 여부
							if (dd === undefined) {
								return [];
							}
							return dd;
						},
					},
					labelFunction : function(rowIndex, columnIndex, value, headerText, item) { // key-value 에서 엑셀 내보내기 할 때 value 로 내보내기 위한 정의
						let retStr = ""; // key 값에 맞는 value 를 찾아 반환함.
						const param = item.mak_code;
						const dd = detailMap[param]; // param으로 보관된 리스트가 있는지 여부
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
						iconWidth : 16, // icon 사이즈, 지정하지 않으면 rowHeight에 맞게 기본값 적용됨
						iconHeight : 16,
						iconPosition : "aisleRight",
						iconTableRef : { // icon 값 참조할 테이블 레퍼런스
							"default" : "/Windchill/extcore/component/AUIGrid/images/list-icon.png" // default
						},
						onClick : function(event) {
							// 아이콘을 클릭하면 수정으로 진입함.
							AUIGrid.openInputer(event.pid);
						}
					},
					editRenderer : {
						type : "ComboBoxRenderer",
						autoCompleteMode : true, // 자동완성 모드 설정
						autoEasyMode : true,
						matchFromFirst : false, // 처음부터 매치가 아닌 단순 포함되는 자동완성
						showEditorBtnOver : false, // 마우스 오버 시 에디터버턴 보이기						
						list : customers, //key-value Object 로 구성된 리스트
						keyField : "key", // key 에 해당되는 필드명
						valueField : "value", // value 에 해당되는 필드명,
						descendants : [ "install_code" ], // 자손 필드들
						descendantDefaultValues : [ "" ], // 변경 시 자손들에게 기본값 지정						
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							let isValid = false;
							for (let i = 0, len = customers.length; i < len; i++) { // keyValueList 있는 값만..
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
					labelFunction : function(rowIndex, columnIndex, value, headerText, item) { // key-value 에서 엑셀 내보내기 할 때 value 로 내보내기 위한 정의
						let retStr = ""; // key 값에 맞는 value 를 찾아 반환함.
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
						iconWidth : 16, // icon 사이즈, 지정하지 않으면 rowHeight에 맞게 기본값 적용됨
						iconHeight : 16,
						iconPosition : "aisleRight",
						iconTableRef : { // icon 값 참조할 테이블 레퍼런스
							"default" : "/Windchill/extcore/component/AUIGrid/images/list-icon.png" // default
						},
						onClick : function(event) {
							// 아이콘을 클릭하면 수정으로 진입함.
							AUIGrid.openInputer(event.pid);
						}
					},
					editRenderer : {
						type : "ComboBoxRenderer",
						autoCompleteMode : true, // 자동완성 모드 설정
						autoEasyMode : true,
						matchFromFirst : false, // 처음부터 매치가 아닌 단순 포함되는 자동완성
						showEditorBtnOver : false, // 마우스 오버 시 에디터버턴 보이기						
						keyField : "key", // key 에 해당되는 필드명
						valueField : "value", // value 에 해당되는 필드명,
						validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
							const param = item.customer_code;
							const dd = installMap[param]; // param으로 보관된 리스트가 있는지 여부
							let isValid = false;
							for (let i = 0, len = dd.length; i < len; i++) { // keyValueList 있는 값만..
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
							const dd = installMap[param]; // param으로 보관된 리스트가 있는지 여부
							if (dd === undefined) {
								return [];
							}
							return dd;
						},
					},
					labelFunction : function(rowIndex, columnIndex, value, headerText, item) { // key-value 에서 엑셀 내보내기 할 때 value 로 내보내기 위한 정의
						let retStr = ""; // key 값에 맞는 value 를 찾아 반환함.
						const param = item.customer_code;
						const dd = installMap[param]; // param으로 보관된 리스트가 있는지 여부
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
					dataField : "preViewBtn",
					headerText : "",
					width : 100,
					editable : false,
					renderer : {
						type : "ButtonRenderer",
						labelText : "파일선택",
						onclick : function(rowIndex, columnIndex, value, item) {
							recentGridItem = item;
							const oid = item._$uid;
							const url = getCallUrl("/aui/preview?oid=" + oid + "&method=preView");
							popup(url, 1000, 200);
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
					dataField : "iconsBtn",
					headerText : "",
					width : 100,
					editable : false,
					renderer : {
						type : "ButtonRenderer",
						labelText : "파일선택",
						onclick : function(rowIndex, columnIndex, value, item) {
							recentGridItem = item;
							const oid = item._$uid;
							const url = getCallUrl("/aui/secondary?oid=" + oid + "&method=setSecondary");
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
					// 포맷팅 형태로 필터링 처리
					},
				} ]
			}

			function createAUIGrid(columns) {
				const props = {
					// 그리드 공통속성 시작
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
					// 그리드 공통속성 끝
					editable : true,
					enterKeyColumnBase : true
				};
				myGridID = AUIGrid.create("#grid_wrap", columns, props);
				loadGridData();

				AUIGrid.bind(myGridID, "cellEditEnd", auiCellEditEndHandler);

				// 컨텍스트 메뉴 이벤트 바인딩
				AUIGrid.bind(myGridID, "contextMenu", auiContextMenuHandler);

				// 스크롤 체인지 핸들러.
				AUIGrid.bind(myGridID, "vScrollChange", function(event) {
					hideContextMenu(); // 컨텍스트 메뉴 감추기
					vScrollChangeHandler(event); // lazy loading
				});

				AUIGrid.bind(myGridID, "hScrollChange", function(event) {
					hideContextMenu(); // 컨텍스트 메뉴 감추기
				});

				AUIGrid.bind(myGridID, "cellClick", auiCellClickHandler);
			}

			function auiCellClickHandler(event) {
				const dataField = event.dataField;
				const oid = event.item.oid;
				if (dataField === "preView") {
					const url = getCallUrl("/aui/thumbnail?oid=" + oid);
					popup(url);
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

			function preView(data) {
				const preView = data.base64;
				const preViewPath = data.fullPath;
				AUIGrid.updateRowsById(myGridID, {
					_$uid : recentGridItem._$uid,
					preView : preView,
					preViewPath : preViewPath
				});
			}

			function setSecondary(data) {
				let template = "";
				const arr = new Array();
				for (let i = 0; i < data.length; i++) {
					template += "<img style='position: relative; top: 2px' src='" + data[i].icon + "'>&nbsp;";
					arr.push(data[i].fullPath);
				}

				AUIGrid.updateRowsById(myGridID, {
					_$uid : recentGridItem._$uid,
					secondaryPaths : arr,
					icons : template
				});
			}

			// 행 추가
			function addRow() {
				const item = new Object();
				item.latest = true;
				AUIGrid.addRow(myGridID, item, "first");
			}

			// 행 삭제
			function deleteRow() {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				for (let i = checkedItems.length - 1; i >= 0; i--) {
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

				// 변경 점이 없는거 체크
				if (addRows.length == 0 && removeRows.length == 0 && editRows.length == 0) {
					alert("변경된 내용이 없습니다.");
					return false;
				}

				for (let i = 0; i < addRows.length; i++) {
					const item = addRows[i];

					if (isNull(item.item)) {
						AUIGrid.showToastMessage(myGridID, i, 0, "항목 값은 공백을 입력 할 수 없습니다.");
						return false;
					}
					if (isNull(item.improvements)) {
						AUIGrid.showToastMessage(myGridID, i, 1, "개선내용 값은 공백을 입력 할 수 없습니다.");
						return false;
					}
					if (isNull(item.improvement)) {
						AUIGrid.showToastMessage(myGridID, i, 2, "개선책 값은 공백을 입력 할 수 없습니다.");
						return false;
					}
					if (isNull(item.apply)) {
						AUIGrid.showToastMessage(myGridID, i, 3, "적용/미적용 값을 선택하세요.");
						return false;
					}
					if (isNull(item.mak_code)) {
						AUIGrid.showToastMessage(myGridID, i, 4, "막종을 선택하세요.");
						return false;
					}
					if (isNull(item.detail_code)) {
						AUIGrid.showToastMessage(myGridID, i, 5, "막종상세를 선택하세요.");
						return false;
					}
					if (isNull(item.customer_code)) {
						AUIGrid.showToastMessage(myGridID, i, 6, "거래처를 선택하세요.");
						return false;
					}
					if (isNull(item.install_code)) {
						AUIGrid.showToastMessage(myGridID, i, 7, "설치장소를 선택하세요.");
						return false;
					}
					if (isNull(item.preView)) {
						AUIGrid.showToastMessage(myGridID, i, 9, "미리보기를 선택하세요.");
						return false;
					}
					if (isNull(item.icons)) {
						AUIGrid.showToastMessage(myGridID, i, 11, "첨부파일을 선택하세요.");
						return false;
					}
				}

				for (let i = 0; i < editRows.length; i++) {
					const item = editRows[i];

					if (isNull(item.item)) {
						AUIGrid.showToastMessage(myGridID, i, 0, "항목 값은 공백을 입력 할 수 없습니다.");
						return false;
					}
					if (isNull(item.improvements)) {
						AUIGrid.showToastMessage(myGridID, i, 1, "개선내용 값은 공백을 입력 할 수 없습니다.");
						return false;
					}
					if (isNull(item.improvement)) {
						AUIGrid.showToastMessage(myGridID, i, 2, "개선책 값은 공백을 입력 할 수 없습니다.");
						return false;
					}
					if (isNull(item.apply)) {
						AUIGrid.showToastMessage(myGridID, i, 3, "적용/미적용 값을 선택하세요.");
						return false;
					}
					if (isNull(item.mak_code)) {
						AUIGrid.showToastMessage(myGridID, i, 4, "막종을 선택하세요.");
						return false;
					}
					if (isNull(item.detail_code)) {
						AUIGrid.showToastMessage(myGridID, i, 5, "막종상세를 선택하세요.");
						return false;
					}
					if (isNull(item.customer_code)) {
						AUIGrid.showToastMessage(myGridID, i, 6, "거래처를 선택하세요.");
						return false;
					}
					if (isNull(item.install_code)) {
						AUIGrid.showToastMessage(myGridID, i, 7, "설치장소를 선택하세요.");
						return false;
					}
					if (isNull(item.preView)) {
						AUIGrid.showToastMessage(myGridID, i, 9, "미리보기를 선택하세요.");
						return false;
					}
					if (isNull(item.icons)) {
						AUIGrid.showToastMessage(myGridID, i, 11, "첨부파일을 선택하세요.");
						return false;
					}
				}

				if (!confirm("저장 하시겠습니까?")) {
					return false;
				}

				params.addRows = addRows;
				params.removeRows = removeRows;
				params.editRows = editRows;
				call(url, params, function(data) {
					alert(data.msg);
					if (data.result) {
						loadGridData();
					}
				});
			}

			function exportExcel() {
				const exceptColumnFields = [ "preView", "preViewBtn", "icons", "iconsBtn" ];
				exportToExcel("CIP 리스트", "CIP", "CIP 리스트", exceptColumnFields, "<%=sessionUser.getFullName()%>");
			}

			// jquery 삭제를 해가는 쪽으로 한다..
			document.addEventListener("DOMContentLoaded", function() {
				// DOM이 로드된 후 실행할 코드 작성
				const columns = loadColumnLayout("cip-list");
				// 컨텍스트 메뉴 시작
				const contenxtHeader = genColumnHtml(columns); // see auigrid.js
				$("#h_item_ul").append(contenxtHeader);
				$("#headerMenu").menu({
					select : headerMenuSelectHandler
				});
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);

				selectbox("psize");
			});

			document.addEventListener("keydown", function(event) {
				// 키보드 이벤트 객체에서 눌린 키의 코드 가져오기
				const keyCode = event.keyCode || event.which;
				if (keyCode === 13) {
					loadGridData();
				}
			})

			// 컨텍스트 메뉴 숨기기
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