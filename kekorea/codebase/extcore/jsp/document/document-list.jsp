<%@page import="e3ps.doc.service.DocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
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
</head>
<body>
	<form>
		<!-- 리스트 검색시 반드시 필요한 히든 값 -->
		<input type="hidden" name="sessionid" id="sessionid">
		<input type="hidden" name="curPage" id="curPage">
		<!-- 폴더 OID 히든값 -->
		<input type="hidden" name="oid" id="oid">
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
				<th>문서 분류</th>
				<td colspan="7">
					<input type="hidden" name="location" value="<%=DocumentHelper.ROOT%>"> <span id="location"><%=DocumentHelper.ROOT%></span>
				</td>
				</tr>
				<tr>
				<th>문서 제목</th>
				<td>
					<input type="text" name="partCode" class="AXInput">
				</td>
				<th>문서 번호</th>
				<td>
					<input type="text" name="partName" class="AXInput">
				</td>
				<th>설명</th>
				<td>
					<input type="text" name="number" class="AXInput">
				</td>
				<th>상태</th>
				<td>
					<select name="size" id="size" class="AXSelect w200">
						<option value="">선택</option>
					</select>
				</td>
				</tr>
				<tr>
				<th>작성자</th>
				<td>
					<input type="text" name="number" class="AXInput">
				</td>
				<th>작성일</th>
				<td colspan="3">
					<input type="text" name="partNamea" class="AXInput width-100"> ~
					<input type="text" name="partNamea" class="AXInput width-100">
				</td>
				<th>버전</th>
				<td>
					<label title="최신 버전">
						<input type="radio" name="latestVersion" value="true" checked="checked">
						<span class="latestVersion">최신 버전</span>
					</label>
					<label title="모든 버전">
						<input type="radio" name="allVersion" value="false" >
						<span class="allVersion">모든 버전</span>
					</label>
				</td>
			</tr>
		</table>

		<!-- 버튼 테이블 -->
		<table class="button-table">
			<tr>
				<td class="left">
					<input type="button" value="테이블 저장" title="테이블 저장" class="orange" onclick="saveColumnLayout('document-list');">
					<input type="button" value="등록" title="등록" class="blue" onclick="create();">
					<%
					if (isAdmin) {
					%>
					<input type="button" value="저장" title="저장" onclick="save();">
					<input type="button" value="행 삭제" title="행 삭제" class="red" onclick="deleteRow();">
					<%
					}
					%>
				</td>
				<td class="right">
					<input type="button" value="조회" title="조회" onclick="loadGridData();">
				</td>
			</tr>
		</table>
		<!-- 리스트 테이블 -->
		<table>
			<colgroup>
				<col width="230">
				<col width="10">
				<col width="*">
			</colgroup>
			<tr>
				<td valign="top">
					<jsp:include page="/extcore/include/folder-include.jsp">
						<jsp:param value="<%=DocumentHelper.ROOT%>" name="location" />
						<jsp:param value="product" name="container" />
						<jsp:param value="list" name="mode" />
						<jsp:param value="680" name="height" />
					</jsp:include>
				</td>
				<td>&nbsp;</td>
				<td>
					<!-- 그리드 리스트 -->
					<div id="grid_wrap" style="height: 680px; border-top: 1px solid #3180c3;"></div>
				</td>
			</tr>
		</table>
		<script type="text/javascript">
			let myGridID;
			function _layout() {
				return [ {
					dataField : "name",
					headerText : "문서제목",
					dataType : "string",
					width : 350,
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
					dataField : "number",
					headerText : "문서번호",
					dataType : "string",
					width : 120,
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
					dataField : "description",
					headerText : "설명",
					dataType : "string",
					style : "left indent10",
					width : 350,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "location",
					headerText : "문서분류",
					dataType : "string",
					width : 250,
					style : "left indent10",
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "docType",
					headerText : "문서타입",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "state",
					headerText : "상태",
					dataType : "string",
					width : 100,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "version",
					headerText : "버전",
					dataType : "string",
					width : 80,
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
					dataField : "createdDate",
					headerText : "작성일",
					dataType : "date",
					width : 100,
					formatString : "yyyy-mm-dd",
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
					dataField : "modifiedDate",
					headerText : "수정일",
					dataType : "date",
					width : 100,
					formatString : "yyyy-mm-dd",
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "primary",
					headerText : "첨부파일",
					width : 100,
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
					// 그리드 공통속성 끝
					showRowCheckColumn : true,
					useContextMenu : true
				};

				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				//화면 첫 진입시 리스트 호출 함수
				loadGridData();
				// Lazy Loading 이벤트 바인딩
				AUIGrid.bind(myGridID, "vScrollChange", vScrollChangeHandler);
			}

			function loadGridData() {
				const url = getCallUrl("/document/list");
				const params = new Object();
				params.latest = true;
				AUIGrid.showAjaxLoader(myGridID);
				parent.openLayer();
				call(url, params, function(data) {
					AUIGrid.removeAjaxLoader(myGridID);
					AUIGrid.setGridData(myGridID, data.list);
					document.getElementById("sessionid").value = data.sessionid;
					document.getElementById("curPage").value = data.curPage;
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
				params.sessionid = document.getElementById("sessionid").value;
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

			// 등록
			function create() {
				const url = getCallUrl("/document/create");
				popup(url);
			}

			function save() {

				if (!confirm("저장 하시겠습니까?")) {
					return false;
				}

				const url = getCallUrl("/meeting/delete");
				const params = new Object();
				const removeRows = AUIGrid.getRemovedItems(myGridID);
				params.removeRows = removeRows;
				parent.openLayer();
				call(url, params, function(data) {
					alert(data.msg);
					parent.closeLayer();
					if (data.result) {
						loadGridData();
					}
				});
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
				const columns = loadColumnLayout("document-list");
				createAUIGrid(columns);
				_createAUIGrid(_columns);
			});

			document.addEventListener("keydown", function(event) {
				// 키보드 이벤트 객체에서 눌린 키의 코드 가져오기
				const keyCode = event.keyCode || event.which;
				if (keyCode === 13) {
					loadGridData();
				}
			})

			window.addEventListener("resize", function() {
				AUIGrid.resize(_myGridID);
				AUIGrid.resize(myGridID);
			});
		</script>
	</form>
</body>
</html>