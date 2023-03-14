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
			</colgroup>
			<tr>
				<th>템플릿 제목</th>
				<td>
					<input type="text" name="templateName" id="templateName" class="AXInput">
				</td>
				<th>작성자</th>
				<td>
					<input type="text" name="duration" id="duration" class="AXInput">
				</td>
				<th>작성일</th>
				<td>
					<input type="text" name="partName" class="AXInput width-100">
					~
					<input type="text" name="partName" class="AXInput width-100">
				</td>
			</tr>
			<tr>
				<th>기간</th>
				<td>
					<input type="text" name="partName" class="AXInput">
				</td>
				<th>수정자</th>
				<td>
					<input type="text" name="" class="AXInput">
				</td>
				<th>수정일</th>
				<td>
					<input type="text" name="partNamea" class="AXInput width-100">
					~
					<input type="text" name="partNamea" class="AXInput width-100">
				</td>
			</tr>
		</table>

		<!-- 버튼 테이블 -->
		<table class="button-table">
			<tr>
				<td class="left">
					<input type="button" value="테이블 저장" title="테이블 저장" class="orange" onclick="saveColumnLayout('template-list');">
					<input type="button" value="등록" title="등록" class="blue" onclick="create();">
				</td>
				<td class="right">
					<input type="button" value="조회" title="조회" onclick="loadGridData();">
				</td>
			</tr>
		</table>

		<!-- 그리드 리스트 -->
		<div id="grid_wrap" style="height: 715px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID;
			function _layout() {
				return [ {
					dataField : "name",
					headerText : "템플릿 제목",
					dataType : "string",
					width : 350,
					style : "left indent10",
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const oid = item.oid;
							const url = getCallUrl("/template/info?oid=" + oid);
							popup(url);
						}
					},
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "description",
					headerText : "내용",
					dataType : "string",
					style : "left indent10",
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "duration",
					headerText : "기간",
					dataType : "numeric", // 날짜 및 사람명 컬럼 사이즈 100
					width : 100,
					postfix : "일",
					filter : {
						showIcon : true,
						inline : true
					},
				}, {
					dataField : "enable",
					headerText : "사용여부",
					dataType : "boolean", // 날짜 및 사람명 컬럼 사이즈 100
					width : 100,
					filter : {
						showIcon : false,
						inline : false
					},
					renderer : {
						type : "CheckBoxEditRenderer"
					}
				}, {
					dataField : "creator",
					headerText : "작성자",
					dataType : "string", // 날짜 및 사람명 컬럼 사이즈 100
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
				};

				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				//화면 첫 진입시 리스트 호출 함수
				loadGridData();
				// Lazy Loading 이벤트 바인딩
				AUIGrid.bind(myGridID, "vScrollChange", vScrollChangeHandler);
			}

			function loadGridData() {
				const params = new Object();
				const url = getCallUrl("/template/list");
				const templateName = document.getElementById("templateName").value;
				const duration = document.getElementById("duration").value;
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

			function create() {
				const url = getCallUrl("/template/create");
				popup(url, 1200, 350);
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
				const params = new Object();
				const curPage = document.getElementById("curPage").value;
				const sessionid = document.getElementById("sessionid").value;
				params.sessionid = sessionid;
				params.start = (curPage * 100);
				params.end = (curPage * 100) + 100;
				const url = getCallUrl("/aui/appendData");
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

			// jquery 삭제를 해가는 쪽으로 한다..
			document.addEventListener("DOMContentLoaded", function() {
				// DOM이 로드된 후 실행할 코드 작성
				const columns = loadColumnLayout("template-list");
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

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID);
			});
		</script>
	</form>
</body>
</html>