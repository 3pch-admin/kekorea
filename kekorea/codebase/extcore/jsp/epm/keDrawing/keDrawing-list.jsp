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
				<th>부품 분류</th>
				<td colspan="7" class="indent5">
					<input type="hidden" name="location" value=""> <span id="location">defaultttttttt</span>
				</td>
				</tr>
				<tr>
				<th>파일 이름</th>
				<td class="indent5">
					<input type="text" name="partCode">
				</td>
				<th>품번</th>
				<td class="indent5">
					<input type="text" name="partName">
				</td>
				<th>품명</th>
				<td class="indent5">
					<input type="text" name="number">
				</td>
				<th>규격</th>
				<td class="indent5">
					<input type="text" name="number">
				</td>
				</tr>
				<tr>
				<th>MATERIAL</th>
				<td class="indent5">
					<input type="text" name="number">
				</td>
				<th>REMARK</th>
				<td class="indent5">
					<input type="text" name="number">
				</td>
				<th>REFERENCE 도면</th>
				<td colspan="3" class="indent5">
					<input type="text" name="number">
				</td>
				</tr>
				<tr>
				<th>작성자</th>
				<td class="indent5">
					<input type="text" name="number">
				</td>
				<th>작성일</th>
				<td colspan="3" class="indent5">
					<input type="text" name="partName" class="width-100"> ~
					<input type="text" name="partName" class="width-100">
				</td>
				<th>상태</th>
				<td class="indent5">
					<input type="text" name="number">
				</td>
				</tr>
				<tr>
				<th>수정자</th>
				<td class="indent5">
					<input type="text" name="number">
				</td>
				<th>수정일</th>
				<td colspan="3" class="indent5">
					<input type="text" name="partName" class="width-100"> ~
					<input type="text" name="partName" class="width-100">
				</td>
				<th>버전</th>
				<td class="indent5">
					<input type="text" name="number">
				</td>
			</tr>
		</table>

		<!-- 버튼 테이블 -->
		<table class="button-table">
			<tr>
				<td class="left">
					<input type="button" value="테이블 저장" title="테이블 저장" class="orange" onclick="saveColumnLayout('keDrawing-list');">
					<input type="button" value="테이블 초기화" title="테이블 초기화" onclick="resetColumnLayout('keDrawing-list');">
					<input type="button" value="저장" title="저장" onclick="create();">
					<input type="button" value="개정" title="개정" class="red" onclick="revise();">
					<input type="button" value="행 추가" title="행 추가" class="blue" onclick="addRow();">
					<input type="button" value="행 삭제" title="행 삭제" class="red" onclick="deleteRow();">
				</td>
				<td class="right">
					<input type="button" value="조회" title="조회" onclick="loadGridData();">
				</td>
			</tr>
		</table>

		<!-- 그리드 리스트 -->
		<div id="grid_wrap" style="height: 610px; border-top: 1px solid #3180c3;"></div>
		<!-- 컨텍스트 메뉴 사용시 반드시 넣을 부분 -->
		<%@include file="/extcore/jsp/common/aui/aui-context.jsp"%>
		<script type="text/javascript">
			let myGridID;
			let recentGridItem = null;
			function _layout() {
				return [ {
					dataField : "lotNo",
					headerText : "LOT",
					dataType : "numeric",
					width : 100,
					formatString : "###0",
					editRenderer : {
						type : "InputEditRenderer",
						onlyNumeric : true, // 0~9만 입력가능
					},
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "name",
					headerText : "DRAWING TITLE",
					dataType : "string",
					width : 300,
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
					headerText : "DWG. NO",
					dataType : "string",
					width : 200,
					editable : false,
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
					dataField : "latest",
					headerText : "최신버전",
					dataType : "boolean",
					width : 100,
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
					dataField : "createdDate",
					headerText : "작성일",
					dataType : "date",
					formatString : "yyyy-mm-dd",
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
					dataField : "modifiedDate",
					headerText : "수정일",
					dataType : "date",
					formatString : "yyyy-mm-dd",
					width : 100,
					editable : false,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "primary",
					headerText : "도면파일",
					dataType : "string",
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
							const url = getCallUrl("/aui/primary?oid=" + oid + "&method=attach");
							popup(url, 1000, 200);
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
					editable : false,
					filter : {
						showIcon : true,
						inline : true
					},
				} ]
			}

			// AUIGrid 생성 함수
			function createAUIGrid(columnLayout) {
				// 그리드 속성
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
					editable : true,
					showRowCheckColumn : true,
				};

				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				// 화면 첫 진입시 리스트 호출 함수
				// 등록이 있는곳은 제외 한다.
				loadGridData();
				// Lazy Loading 이벤트 바인딩
				AUIGrid.bind(myGridID, "vScrollChange", vScrollChangeHandler);
				
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

			function loadGridData() {
				const params = new Object();
				const url = getCallUrl("/keDrawing/list");
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
				const sessionid = document.getElementById("sessionid").value;
				params.sessionid = sessionid;
				params.start = (curPage * 100);
				params.end = (curPage * 100) + 100;
				AUIGrid.showAjaxLoader(myGridID);
				parent.openLayer();
				call(url, params, function(data) {
					if (data.list.length == 0) {
						last = true;
					} else {
						AUIGrid.appendData(myGridID, data.list);
						document.getElementById("curPage").value = parseInt(curPage) + 1;
					}
					AUIGrid.removeAjaxLoader(myGridID);
					parent.closeLayer();
				})
			}

			// 행 추가
			function addRow() {
				const item = new Object();
				item.createdDate = new Date();
				item.modifiedDate = new Date();
				item.creator = "<%=sessionUser.getFullName()%>";
				item.modifier = "<%=sessionUser.getFullName()%>";
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

			function attach(data) {
				const name = data.name;
				const start = name.indexOf("-");
				const end = name.lastIndexOf(".");
				const number = name.substring(0, start);
				const version = name.substring(start + 1, end);
				const template = "<img src='" + data.icon + "' style='position: relative; top: 2px;'>";
				AUIGrid.updateRowsById(myGridID, {
					oid : recentGridItem.oid,
					keNumber : number,
					version : Number(version),
					file : name,
					primary : template,
					primaryPath : data.fullPath
				});
			}

			// 저장
			function create() {

				if (!confirm("저장 하시겠습니까?")) {
					return false;
				}

				const url = getCallUrl("/keDrawing/create");
				const params = new Object();
				const addRows = AUIGrid.getAddedRowItems(myGridID);
				const removeRows = AUIGrid.getRemovedItems(myGridID);
				const editRows = AUIGrid.getEditedRowItems(myGridID);

				for (let i = 0; i < addRows.length; i++) {
					const item = addRows[i];
					
					if(isNull(item.name)) {
						AUIGrid.showToastMessage(myGridID, i, 1, "DRAWING TITLE의 값은 공백을 입력 할 수 없습니다.");
						return false;
					}
					if(isNull(item.primary)) {
						AUIGrid.showToastMessage(myGridID, i, 9, "첨부파일을 선택하세요.");
						return false;
					}
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
					}
				});
			}

			function revise() {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				if (checkedItems.length == 0) {
					alert("개정할 도면을 선택하세요.");
					return false;
				}
				const url = getCallUrl("/keDrawing/revise");
				const panel = popup(url, 1600, 550);
				panel.list = checkedItems;
			}

			document.addEventListener("DOMContentLoaded", function() {
				// DOM이 로드된 후 실행할 코드 작성
				const columns = loadColumnLayout("keDrawing-list");
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