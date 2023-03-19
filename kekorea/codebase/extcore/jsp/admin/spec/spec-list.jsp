<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
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
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-200">
				</td>
				<th>설명</th>
				<td class="indent5">
					<input type="text" name="description" class="width-200">
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

		<!-- 버튼 테이블 -->
		<table class="button-table">
			<tr>
				<td class="left">
					<input type="button" value="저장" title="저장" onclick="save();">
					<input type="button" value="자식 추가" title="자식 추가" class="orange" onclick="addTreeRow();">
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
		<div id="grid_wrap" style="height: 705px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID;
			const list = [ {
				key : "SPEC",
				value : "사양"
			}, {
				key : "OPTION",
				value : "옵션"
			} ]
			const columns = [ {
				dataField : "name",
				headerText : "코드 명",
				dataType : "string",
				width : 500,
				style : "left",
				editRenderer : {
					type : "InputEditRenderer",
					validator : function(oldValue, newValue, item, dataField) {
						let isValid = true;
						if (newValue === "") {
							isValid = false;
						}
						return {
							"validate" : isValid,
							"message" : "코드명은 공백을 입력 할 수 없습니다."
						};
					}
				},
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "code",
				headerText : "코드",
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
							"message" : "코드는 공백을 입력 할 수 없습니다."
						};
					}
				},
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "codeType",
				headerText : "코드타입",
				dataType : "string", // 날짜 및 사람명 컬럼 사이즈 100
				width : 120,
				labelFunction : function(rowIndex, columnIndex, value, headerText, item) { // key-value 에서 엑셀 내보내기 할 때 value 로 내보내기 위한 정의
					let retStr = ""; // key 값에 맞는 value 를 찾아 반환함.
					for (let i = 0, len = list.length; i < len; i++) {
						if (list[i]["key"] == value) {
							retStr = list[i]["value"];
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
				dataField : "sort",
				headerText : "정렬",
				dataType : "numeric",
				width : 80,
				formatString : "###0",
				editRenderer : {
					type : "InputEditRenderer",
					onlyNumeric : true, // 0~9만 입력가능
				},
				filter : {
					showIcon : false,
					inline : false
				},
			}, {
				dataField : "enable",
				headerText : "사용여부",
				dataType : "boolean",
				width : 120,
				renderer : {
					type : "CheckBoxEditRenderer",
					editable : true,
					disabledFunction : function(rowIndex, columnIndex, value, isChecked, item, dataField) {
						if (rowIndex != 0) {
							return false;
						}
						return true;
					},
				},
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "description",
				headerText : "설명",
				dataType : "string",
				style : "left indent10",
				filter : {
					showIcon : true,
					inline : true
				},
			} ]

			// AUIGrid 생성 함수
			function createAUIGrid(columnLayout) {
				// 그리드 속성
				const props = {
					rowIdField : "oid",
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
					showInlineFilter : true,
					filterLayerWidth : 320,
					filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
					// 그리드 공통속성 끝
					displayTreeOpen : true,
					editable : true
				};

				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				//화면 첫 진입시 리스트 호출 함수
				loadGridData();
				AUIGrid.bind(myGridID, "addRowFinish", auiAddRowFinish);
				AUIGrid.bind(myGridID, "cellEditBegin", auiCellEditBegin);

				// 스크롤 체인지 핸들러.
				AUIGrid.bind(myGridID, "vScrollChange", function(event) {
					vScrollChangeHandler(event); // lazy loading
				});

				AUIGrid.bind(myGridID, "cellEditEndBefore", auiCellEditEndBefore);
				AUIGrid.bind(myGridID, "cellClick", auiCellClickHandler);
			}

			function auiCellClickHandler(event) {
				const item = event.item;
				rowIdField = AUIGrid.getProp(event.pid, "rowIdField"); // rowIdField 얻기
				rowId = item[rowIdField];

				// 이미 체크 선택되었는지 검사
				if (AUIGrid.isCheckedRowById(event.pid, rowId)) {
					// 엑스트라 체크박스 체크해제 추가
					AUIGrid.addUncheckedRowsByIds(event.pid, rowId);
				} else {
					// 엑스트라 체크박스 체크 추가
					AUIGrid.addCheckedRowsByIds(event.pid, rowId);
				}
			}

			function auiCellEditEndBefore(event) {
				const dataField = event.dataField;
				const value = event.value;
				if (dataField === "code") {
					const isUnique = AUIGrid.isUniqueValue(myGridID, "code", value);
					if (!isUnique) {
						alert("입력하신 코드는 이미 존재합니다.");
						return "";
					}
				}
				return value;
			}

			// 저장
			function save() {
				const url = getCallUrl("/spec/save");
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
					if (isNull(item.name)) {
						AUIGrid.showToastMessage(myGridID, i + 1, 0, "코드 명 값은 공백을 입력 할 수 없습니다.");
						return false;
					}
					if (isNull(item.code)) {
						AUIGrid.showToastMessage(myGridID, i + 1, 1, "코드 값은 공백을 입력 할 수 없습니다.");
						return false;
					}
				}

				for (let i = 0; i < editRows.length; i++) {
					const item = editRows[i];
					if (isNull(item.name)) {
						AUIGrid.showToastMessage(myGridID, i + 1, 0, "코드 명 값은 공백을 입력 할 수 없습니다.");
						return false;
					}
					if (isNull(item.code)) {
						AUIGrid.showToastMessage(myGridID, i + 1, 1, "코드 값은 공백을 입력 할 수 없습니다.");
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
					if (data.result) {
						loadGridData();
					} else {
						// 실패...
					}
				})
			}

			function auiCellEditBegin(event) {
				const dataField = event.dataField;
				const rowIndex = event.rowIndex;
				if (rowIndex == 0) {
					return false;
				}

				if (dataField === "codeType") {
					return false;
				}
				return true;
			}

			function auiAddRowFinish(event) {
				const item = event.items[0];
				const depth = item._$depth;
				if (depth == 2) {
					const item = {
						"codeType" : "SPEC"
					};
					AUIGrid.updateRow(myGridID, item, "selectedIndex");
				}

				if (depth == 3) {
					const item = {
						"codeType" : "OPTION"
					};
					AUIGrid.updateRow(myGridID, item, "selectedIndex");
				}

				if (depth > 3 || depth === undefined) {
					AUIGrid.removeRow(myGridID, "selectedIndex");
				}

				const selected = AUIGrid.getSelectedIndex(myGridID);
				if (selected.length <= 0) {
					return false;
				}

				const rowIndex = selected[0];
				const colIndex = AUIGrid.getColumnIndexByDataField(myGridID, "name");
				AUIGrid.setSelectionByIndex(myGridID, rowIndex, colIndex);
				AUIGrid.openInputer(myGridID);
			}

			function loadGridData() {
				const params = new Object();
				const url = getCallUrl("/spec/list");
				const psize = document.getElementById("psize").value;
				params.psize = psize;
				AUIGrid.showAjaxLoader(myGridID);
				parent.openLayer();
				call(url, params, function(data) {
					AUIGrid.removeAjaxLoader(myGridID);
					AUIGrid.setGridData(myGridID, data.list);
					parent.closeLayer();
				});
			}

			// 행 추가
			function addRow() {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				if (checkedItems.length <= 0) {
					alert("행을 추가할 행을 선택하세요.");
					return false;
				}

				if (checkedItems.length > 1) {
					alert("하나의 행을 선택하세요.");
					return false;
				}

				let selItem;
				let parentItem;
				let parentRowId;

				selItem = checkedItems[0].item;
				// 선택 행의 동급 레벨로 추가하기 위해 선택행의 부모 가져오기
				parentItem = AUIGrid.getParentItemByRowId(myGridID, selItem.oid);
				parentRowId = parentItem.oid;

				const newItem = new Object();
				newItem.parentRowId = parentRowId; // 부모의 rowId 값을 보관해 놓음...나중에 개발자가 유용하게 쓰기 위함...실제 그리드는 사용하지 않음.
				newItem.enable = true;
				newItem.sort = 0;
				AUIGrid.addTreeRow(myGridID, newItem, parentRowId, "last");
			}

			// 행 추가
			function addTreeRow() {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				if (checkedItems.length <= 0) {
					alert("자식행을 추가할 행을 선택하세요.");
					return false;
				}

				if (checkedItems.length > 1) {
					alert("하나의 행을 선택하세요.");
					return false;
				}

				const selItem = checkedItems[0].item;
				const parentRowId = selItem.oid; // 선택행의 자식으로 행 추가

				const newItem = new Object();
				newItem.parentRowId = parentRowId; // 부모의 rowId 값을 보관해 놓음...나중에 개발자가 유용하게 쓰기 위함...실제 그리드는 사용하지 않음.
				newItem.enable = true;
				newItem.sort = 0;
				AUIGrid.addTreeRow(myGridID, newItem, parentRowId, "selectionDown");
			}

			// 행 삭제
			function deleteRow() {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				for (let i = checkedItems.length - 1; i >= 0; i--) {
					const rowIndex = checkedItems[i].rowIndex;
					AUIGrid.removeRow(myGridID, rowIndex);
				}
			}

			// jquery 삭제를 해가는 쪽으로 한다..
			document.addEventListener("DOMContentLoaded", function() {
				// DOM이 로드된 후 실행할 코드 작성
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);

				// 사용자 검색 바인딩 see base.js finderUser function 
				finderUser("creator");

				// 날짜 검색용 바인딩 see base.js twindate funtion
				twindate("created");
				selectbox("psize");
			});

			document.addEventListener("keydown", function(event) {
				// 키보드 이벤트 객체에서 눌린 키의 코드 가져오기
				const keyCode = event.keyCode || event.which;
				if (keyCode === 13) {
					loadGridData();
				}
			})

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID);
			});
		</script>
	</form>
</body>
</html>