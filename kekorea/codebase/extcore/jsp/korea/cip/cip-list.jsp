<%@page import="java.util.HashMap"%>
<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
JSONArray maks = (JSONArray) request.getAttribute("maks");
JSONArray installs = (JSONArray) request.getAttribute("installs");
JSONArray customers = (JSONArray) request.getAttribute("customers");
String userId = (String) request.getAttribute("userId");
String name = (String) request.getAttribute("name");
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
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js"></script>
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
				<th>공지사항 제목</th>
				<td>
					<input type="text" name="fileName" class="AXInput">
				</td>
				<th>설명</th>
				<td>
					<input type="text" name="partCode" class="AXInput">
				</td>
				<th>작성자</th>
				<td>
					<input type="text" name="partName" class="AXInput">
				</td>
				<th>작성일</th>
				<td>
					<input type="text" name="number" class="AXInput">
				</td>
			</tr>
		</table>

		<!-- 버튼 테이블 -->
		<table class="button-table">
			<tr>
				<td class="left">
					<input type="button" value="테이블 저장" title="테이블 저장" class="orange" onclick="saveColumnLayout('cip-list');">
					<input type="button" value="테이블 초기화" title="테이블 초기화" onclick="resetColumnLayout('cip-list');">
					<input type="button" value="저장" title="저장" onclick="save();">
					<input type="button" value="행 추가" title="행 추가" class="blue" onclick="addRow();">
					<input type="button" value="행 삭제" title="행 삭제" class="red" onclick="deleteRow();">
				</td>
				<td class="right">
					<input type="button" value="조회" title="조회" onclick="loadGridData();">
				</td>
			</tr>
		</table>

		<!-- 그리드 리스트 -->
		<div id="grid_wrap" style="height: 750px; border-top: 1px solid #3180c3;"></div>
		<!-- 컨텍스트 메뉴 사용시 반드시 넣을 부분 -->
		<%@include file="/extcore/jsp/common/aui/aui-context.jsp"%>
		<script type="text/javascript">
			let myGridID;
			const maks = <%=maks%>
			const installs = <%=installs%>
			const customers = <%=customers%>
			let recentGridItem = null;
			let subListMap = {};
			let success = true;
			const list = [ "적용완료", "일부적용", "미적용", "검토중" ];
			function _layout() {
				return [ {
					dataField : "item",
					headerText : "항목",
					dataType : "string",
					width : 120,
					editRenderer: {
						type: "InputEditRenderer",

						// ID는 고유값만 가능하도록 에디팅 유효성 검사
						validator: function (oldValue, newValue, item, dataField) {
							let isValid = true;
							if(newValue === "") {
								isValid = false;
								success = false;
							}
							// 리턴값은 Object 이며 validate 의 값이 true 라면 패스, false 라면 message 를 띄움
							return { "validate": isValid, "message": "항목 값은 공백을 입력 할 수 없습니다." };
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
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "improvement",
					headerText : "개선책",
					dataType : "string",
					width : 300,
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
						type : "DropDownListRenderer",
						showEditorBtn : false,
						showEditorBtnOver : false, // 마우스 오버 시 에디터버턴 보이기
						list : list,
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
						type : "DropDownListRenderer",
						showEditorBtn : false,
						showEditorBtnOver : false, // 마우스 오버 시 에디터버턴 보이기						
						list : maks, //key-value Object 로 구성된 리스트
						keyField : "key", // key 에 해당되는 필드명
						valueField : "value", // value 에 해당되는 필드명,
						descendants : [ "detail_code" ], // 자손 필드들
						descendantDefaultValues : [ "-" ], // 변경 시 자손들에게 기본값 지정
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
						type : "DropDownListRenderer",
						showEditorBtn : false,
						showEditorBtnOver : false, // 마우스 오버 시 에디터버턴 보이기						
						keyField : "key", // key 에 해당되는 필드명
						valueField : "value", // value 에 해당되는 필드명,
						listFunction : function(rowIndex, columnIndex, item, dataField) {
							var param = item.mak_code;
							var dd = subListMap[param]; // param으로 보관된 리스트가 있는지 여부
							if (dd === undefined) {
								return [];
							}
							return dd;
						},
					},
					labelFunction : function(rowIndex, columnIndex, value, headerText, item) { // key-value 에서 엑셀 내보내기 할 때 value 로 내보내기 위한 정의
						let retStr = ""; // key 값에 맞는 value 를 찾아 반환함.
						const param = item.mak_code;
						const dd = subListMap[param]; // param으로 보관된 리스트가 있는지 여부
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
						type : "DropDownListRenderer",
						showEditorBtn : false,
						showEditorBtnOver : false, // 마우스 오버 시 에디터버턴 보이기						
						list : customers, //key-value Object 로 구성된 리스트
						keyField : "key", // key 에 해당되는 필드명
						valueField : "value",
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
						type : "DropDownListRenderer",
						showEditorBtn : false,
						showEditorBtnOver : false, // 마우스 오버 시 에디터버턴 보이기						
						list : installs, //key-value Object 로 구성된 리스트
						keyField : "key", // key 에 해당되는 필드명
						valueField : "value",
					},
					labelFunction : function(rowIndex, columnIndex, value, headerText, item) { // key-value 에서 엑셀 내보내기 할 때 value 로 내보내기 위한 정의
						let retStr = ""; // key 값에 맞는 value 를 찾아 반환함.
						for (let i = 0, len = installs.length; i < len; i++) {
							if (installs[i]["key"] == value) {
								retStr = installs[i]["value"];
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
					dataField : "",
					headerText : "",
					width : 100,
					editable : false,
					renderer : {
						type : "ButtonRenderer",
						labelText : "파일선택",
						onclick : function(rowIndex, columnIndex, value, item) {
							recentGridItem = item;
							const oid = item.oid;
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
					dataField : "",
					headerText : "",
					width : 100,
					editable : false,
					renderer : {
						type : "ButtonRenderer",
						labelText : "파일선택",
						onclick : function(rowIndex, columnIndex, value, item) {
							recentGridItem = item;
							const oid = item.oid;
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
					filter : {
						showIcon : true,
						inline : true
					},
				} ]
			}

			function createAUIGrid(columns) {
				const props = {
					rowIdField : "oid",
					// 그리드 공통속성 시작
					headerHeight : 30, // 헤더높이
					rowHeight : 30, // 행 높이
					showRowNumColumn : true, // 번호 행 출력 여부
					showStateColumn : true, // 상태표시 행 출력 여부
					rowNumHeaderText : "번호", // 번호 행 텍스트 설정
					noDataMessage : "검색 결과가 없습니다.", // 데이터 없을시 출력할 내용
					enableFilter : true, // 필터 사용 여부
					selectionMode : "multipleCells",
					enableMovingColumn : true,
					showInlineFilter : true,
					useContextMenu : true,
					enableRightDownFocus : true,
					filterLayerWidth : 320,
					filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
					// 그리드 공통속성 끝
					editable : true
				};
				myGridID = AUIGrid.create("#grid_wrap", columns, props);
				loadGridData();
				AUIGrid.bind(myGridID, "vScrollChange", vScrollChangeHandler);
				AUIGrid.bind(myGridID, "addRowFinish", auiAddRowHandler);
				AUIGrid.bind(myGridID, "cellClick", auiCellClickHandler);
				AUIGrid.bind(myGridID, "cellEditEnd", auiCellEditEndHandler);
				
				// 컨텍스트 메뉴 이벤트 바인딩
				AUIGrid.bind(myGridID, "contextMenu", auiContextMenuHandler);

				// 스크롤 체인지 핸들러.
				AUIGrid.bind(myGridID, "vScrollChange", function(event) {
					hideContextMenu(); // 컨텍스트 메뉴 감추기
				});

				AUIGrid.bind(myGridID, "hScrollChange", function(event) {
					hideContextMenu(); // 컨텍스트 메뉴 감추기
				});
			}

			function auiCellEditEndHandler(event) {
				const dataField = event.dataField;
				const item = event.item;
				const rowIndex = event.rowIndex;
				if (dataField === "mak_code") {
					const mak = item.mak_code;
					const url = getCallUrl("/commonCode/getChildrens?parentCode=" + mak + "&codeType=MAK");
					call(url, null, function(data) {
						subListMap[mak] = data.list;
					}, "GET");
				}
			}

			function auiCellClickHandler(event) {
				const oid = event.item.oid;
				const dataField = event.dataField;
				if (dataField == "preView" && oid.indexOf("Cip") > -1) {
					const url = getCallUrl("/aui/thumbnail?oid=" + oid);
					popup(url);
				}
			}

			function loadGridData() {
				const params = new Object();
				const url = getCallUrl("/cip/list");
				AUIGrid.showAjaxLoader(myGridID);
				call(url, params, function(data) {
					AUIGrid.removeAjaxLoader(myGridID);
					document.getElementById("sessionid").value = data.sessionid;
					document.getElementById("curPage").value = data.curPage;
					AUIGrid.setGridData(myGridID, data.list);
				});
			}

			let last = false;
			function vScrollChangeHandler(event) {
				if (event.position == event.maxPosition) {
					if (!last) {
						requestAdditionalData();
					}
				}
			}
			function requestAdditionalData() {
				const url = getCallUrl("/aui/appendData");
				const params = new Object();
				const curPage = document.getElementById("curPage").value;
				const sessionid = document.getElementById("sessionid").value
				params.sessionid = sessionid;
				params.start = (curPage * 30);
				params.end = (curPage * 30) + 30;
				AUIGrid.showAjaxLoader(myGridID);
				call(url, params, function(data) {
					if (data.list.length == 0) {
						last = true;
						AUIGrid.removeAjaxLoader(myGridID);
					} else {
						AUIGrid.appendData(myGridID, data.list);
						AUIGrid.removeAjaxLoader(myGridID);
						document.getElementById("curPage").value = parseInt(curPage) + 1;
					}
				})
			}

			function auiAddRowHandler(event) {
				const selected = AUIGrid.getSelectedIndex(myGridID);
				if (selected.length <= 0) {
					return;
				}

				const rowIndex = selected[0];
				const colIndex = AUIGrid.getColumnIndexByDataField(myGridID, "item");
				AUIGrid.setSelectionByIndex(myGridID, rowIndex, colIndex);
				AUIGrid.openInputer(myGridID);
			}

			function preView(data) {
				const preView = data.base64;
				const preViewPath = data.fullPath;
				AUIGrid.updateRowsById(myGridID, {
					oid : recentGridItem.oid,
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
					oid : recentGridItem.oid,
					secondaryPaths : arr,
					icons : template
				});
			}

			// 행 추가
			function addRow() {
				const item = new Object();
				item.createdDate = new Date();
				item.creator = "<%=name%>";
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
				
				 // 저장전에 검증ㄷ되어야..
				if (!confirm("저장 하시겠습니까?")) {
					return false;
				}
				
				const url = getCallUrl("/cip/save");
				const params = new Object();
				const addRows = AUIGrid.getAddedRowItems(myGridID);
				const removeRows = AUIGrid.getRemovedItems(myGridID);
				const editRows = AUIGrid.getEditedRowItems(myGridID);
				
				for (let i = 0; i < addRows.length; i++) {
					const item = addRows[i];
					
					if(isNull(item.item)) {
						AUIGrid.showToastMessage(myGridID, i, 0, "항목 값은 공백을 입력 할 수 없습니다.");
						return false;
					}
					if(isNull(item.improvements)) {
						AUIGrid.showToastMessage(myGridID, i, 1, "개선내용 값은 공백을 입력 할 수 없습니다.");
						return false;
					}
					if(isNull(item.improvement)) {
						AUIGrid.showToastMessage(myGridID, i, 2, "개선책 값은 공백을 입력 할 수 없습니다.");
						return false;
					}
					if(isNull(item.apply)) {
						AUIGrid.showToastMessage(myGridID, i, 3, "적용/미적용 값을 선택하세요.");
						return false;
					}
					if(isNull(item.mak_code)) {
						AUIGrid.showToastMessage(myGridID, i, 4, "막종 값을 선택하세요.");
						return false;
					}
					if(isNull(item.detail_code)) {
						AUIGrid.showToastMessage(myGridID, i, 5, "막종상세 값을 선택하세요.");
						return false;
					}
					if(isNull(item.customer_code)) {
						AUIGrid.showToastMessage(myGridID, i, 6, "거래처 값을 선택하세요.");
						return false;
					}
					if(isNull(item.install_code)) {
						AUIGrid.showToastMessage(myGridID, i, 7, "설치장소 값을 선택하세요.");
						return false;
					}
					if(isNull(item.preView)) {
						AUIGrid.showToastMessage(myGridID, i, 9, "미리보기를 선택하세요.");
						return false;
					}
					if(isNull(item.icons)) {
						AUIGrid.showToastMessage(myGridID, i, 11, "첨부파일을 선택하세요.");
						return false;
					}
				}
				params.addRows = addRows;
				params.removeRows = removeRows;
				params.editRows = editRows;
				console.log(params);
				call(url, params, function(data) {
					alert(data.msg);
					if (data.result) {
						loadGridData();
					} else {
						// 실패 햇을 경우 처리..
					}
				});
			}

			// jquery 삭제를 해가는 쪽으로 한다..
			document.addEventListener("DOMContentLoaded", function() {
				// DOM이 로드된 후 실행할 코드 작성
				const columns = loadColumnLayout("cip-list");
				// 컨텍스트 메뉴 시작
				let contenxtHeader = genColumnHtml(columns); // see auigrid.js
				$("#h_item_ul").append(contenxtHeader);
				$("#headerMenu").menu({
					select : headerMenuSelectHandler
				});
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);
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