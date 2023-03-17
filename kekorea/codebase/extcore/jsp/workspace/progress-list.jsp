<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
			</colgroup>
			<tr>
				<th>결재 제목</th>
				<td class="indent5">
					<input type="text" name="approvalTitle" id="approvalTitle" class="width-300">
				</td>
			</tr>
		</table>

		<!-- 버튼 테이블 -->
		<table class="button-table">
			<tr>
				<td class="left">
					<input type="button" value="테이블 저장" title="테이블 저장" class="orange" onclick="saveColumnLayout('progress-list');">
					<input type="button" value="테이블 초기화" title="테이블 초기화" onclick="resetColumnLayout('progress-list');">
				</td>
				<td class="right">
					<input type="button" value="조회" title="조회" onclick="loadGridData();">
				</td>
			</tr>
		</table>

		<!-- 그리드 리스트 -->
		<div id="grid_wrap" style="height: 705px; border-top: 1px solid #3180c3;"></div>
		<!-- 컨텍스트 메뉴 사용시 반드시 넣을 부분 -->
		<%@include file="/extcore/jsp/common/aui/aui-context.jsp"%>
		<script type="text/javascript">
			let myGridID;
			function _layout() {
				return [ {
					dataField : "name",
					headerText : "결재 제목",
					dataType : "string",
					style : "left indent10",
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
					dataField : "material",
					headerText : "진행단계",
					dataType : "string",
					width : 150,
					filter : {
						showIcon : false,
						inline : false
					},
				}, {
					dataField : "createdDate",
					headerText : "기안일",
					dataType : "date",
					formatString : "yyyy-mm-dd HH:MM:ss",
					width : 130,
					// 시간 표현시 130
					filter : {
						showIcon : true,
						inline : true,
						displayFormatValues : true
					},
				} ]
			}

			// AUIGrid 생성 함수
			function createAUIGrid(columnLayout) {
				// 그리드 속성
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
				};

				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				//화면 첫 진입시 리스트 호출 함수
				loadGridData();
				
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
				const url = getCallUrl("/workspace/progress");
				const approvalTitle = document.getElementById("approvalTitle").value;
				params.approvalTitle = approvalTitle;
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

			// jquery 삭제를 해가는 쪽으로 한다..
			document.addEventListener("DOMContentLoaded", function() {
				// DOM이 로드된 후 실행할 코드 작성
				const columns = loadColumnLayout("progress-list");
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