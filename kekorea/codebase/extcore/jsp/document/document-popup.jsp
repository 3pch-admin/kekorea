<%@page import="e3ps.doc.service.DocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String method = (String) request.getAttribute("method");
boolean multi = (boolean) request.getAttribute("multi");
%>
<%@include file="/extcore/include/auigrid.jsp"%>
<input type="hidden" name="sessionid" id="sessionid">
<input type="hidden" name="curPage" id="curPage">
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js?v=1"></script>

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
			<input type="text" name="fileName" class="width-150">
		</td>
		<th>설명</th>
		<td class="indent5">
			<input type="text" name="description" class="width-150">
		</td>
		<th>작성자</th>
		<td class="indent5">
			<input type="text" name="creator" id="creator" class="width-150">
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
			<input type="button" value="테이블 저장" title="테이블 저장" class="orange" onclick="saveColumnLayout('document-popup');">
			<input type="button" value="테이블 초기화" title="테이블 초기화" onclick="resetColumnLayout('document-popup');">
			<input type="button" value="추가" title="추가" class="blue" onclick="<%=method%>();">
			<input type="button" value="닫기" title="닫기" class="red" onclick="self.close();">
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
				<jsp:param value="705" name="height" />
			</jsp:include>
		</td>
		<td>&nbsp;</td>
		<td>
			<!-- 그리드 리스트 -->
			<div id="grid_wrap" style="height: 705px; border-top: 1px solid #3180c3;"></div>
		</td>
	</tr>
</table>
<!-- 컨텍스트 메뉴 사용시 반드시 넣을 부분 -->
<%@include file="/extcore/jsp/common/aui/aui-context.jsp"%>
<script type="text/javascript">
			let myGridID;
			function _layout() {
				return [ {
					dataField : "name",
					headerText : "문서제목",
					dataType : "string",
					width : 350,
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "number",
					headerText : "문서번호",
					dataType : "string",
					width : 120,
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
						inline : true,
						displayFormatValues : true
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
					// 그리드 공통속성 끝
					showRowCheckColumn : true,
					<%if (!multi) {%>
					rowCheckToRadio : true
					<%}%>
				};

				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				//화면 첫 진입시 리스트 호출 함수
				loadGridData();
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
			}

			function loadGridData() {
				const url = getCallUrl("/document/list");
				const params = new Object();
				const psize = document.getElementById("psize").value;
				params.latest = true;
				params.psize = psize;
				AUIGrid.showAjaxLoader(myGridID);
				call(url, params, function(data) {
					AUIGrid.removeAjaxLoader(myGridID);
					AUIGrid.setGridData(myGridID, data.list);
					document.getElementById("sessionid").value = data.sessionid;
					document.getElementById("curPage").value = data.curPage;
				});
			}

			function <%=method%>() {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				if (checkedItems.length == 0) {
					alert("추가할 문서를 선택하세요.");
					return false;
				}
				opener.<%=method%>(checkedItems);
				self.close();
			}
			
			function auiCellClickHandler(event) {
				const item = event.item;
				rowIdField = AUIGrid.getProp(event.pid, "rowIdField"); // rowIdField 얻기
				rowId = item[rowIdField];
				
				// 이미 체크 선택되었는지 검사
				if(AUIGrid.isCheckedRowById(event.pid, rowId)) {
					// 엑스트라 체크박스 체크해제 추가
					AUIGrid.addUncheckedRowsByIds(event.pid, rowId);
				} else {
					// 엑스트라 체크박스 체크 추가
					AUIGrid.addCheckedRowsByIds(event.pid, rowId);
				}
			}
			
			// jquery 삭제를 해가는 쪽으로 한다..
			document.addEventListener("DOMContentLoaded", function() {
				// DOM이 로드된 후 실행할 코드 작성
				const columns = loadColumnLayout("document-popup");
				let contenxtHeader = genColumnHtml(columns); // see auigrid.js
				$("#h_item_ul").append(contenxtHeader);
				$("#headerMenu").menu({
					select : headerMenuSelectHandler
				});
				createAUIGrid(columns);
				_createAUIGrid(_columns); // 트리
				
				finderUser("creator");
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
				AUIGrid.resize(_myGridID);
				AUIGrid.resize(myGridID);
			});
</script>