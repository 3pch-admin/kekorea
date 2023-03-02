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
			</colgroup>
			<tr>
				<th>결재제목</th>
				<td>
					<input type="text" name="fileName" class="AXInput">
				</td>
			</tr>
		</table>

		<!-- 버튼 테이블 -->
		<table class="button-table">
			<tr>
				<td class="left">
					<input type="button" value="테이블 저장" title="테이블 저장" class="orange" onclick="saveColumnLayout('approval-list');">
					<input type="button" value="조회" title="조회" onclick="loadGridData();">
				</td>
				<td class="right"></td>
			</tr>
		</table>

		<!-- 그리드 리스트 -->
		<div id="grid_wrap" style="height: 665px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID;
			function _layout() {
				return [ {
					dataField : "name",
					headerText : "결재 제목",
					dataType : "string",
					style : "left indent10 underline",
					filter : {
						showIcon : true,
						useExMenu : true
					},
				}, {
					dataField : "returnPoint",
					headerText : "반려단계",
					dataType : "string",
					width : 150,
					filter : {
						showIcon : true,
						useExMenu : true
					},
				}, {
					dataField : "createDate",
					headerText : "기안일",
					dataType : "date",
					formatString : "yyyy-mm-dd HH:MM:ss",
					width : 150,	
					// 시간 표현시 130
					filter : {
						showIcon : true,
						useExMenu : true
					},
				}, {
					dataField : "completeTime",
					headerText : "반려일",
					dataType : "date",
					formatString : "yyyy-mm-dd HH:MM:ss",
					width : 150,	
					// 시간 표현시 130
					filter : {
						showIcon : true,
						useExMenu : true
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
					selectionMode : "multiCells",
					enableMovingColumn : true,
					showInlineFilter : true,
					// 그리드 공통속성 끝
					fillColumnSizeMode : true
				};

				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				//화면 첫 진입시 리스트 호출 함수
				loadGridData();
				// Lazy Loading 이벤트 바인딩
				AUIGrid.bind(myGridID, "vScrollChange", vScrollChangeHandler);
			}

			function loadGridData() {
				let params = new Object();
				let url = getCallUrl("/workspace/approval");
				AUIGrid.showAjaxLoader(myGridID);
				call(url, params, function(data) {
					AUIGrid.removeAjaxLoader(myGridID);
					$("input[name=sessionid]").val(data.sessionid);
					$("input[name=curPage]").val(data.curPage);
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
				let params = new Object();
				let curPage = $("input[name=curPage]").val();
				params.sessionid = $("input[name=sessionid]").val();
				params.start = (curPage * 100);
				params.end = (curPage * 100) + 100;
				let url = getCallUrl("/appendData");
				AUIGrid.showAjaxLoader(myGridID);
				call(url, params, function(data) {
					if (data.list.length == 0) {
						last = true;
					} else {
						AUIGrid.appendData(myGridID, data.list);
						$("input[name=curPage]").val(parseInt(curPage) + 1);
					}
					AUIGrid.removeAjaxLoader(myGridID);
				})
			}

			// jquery 모든 DOM구조 로딩 후 
			$(function() {
				// 로컬 스토리지에 저장된 컬럼 값 불러오기 see - base.js
				let columns = loadColumnLayout("approval-list");
				createAUIGrid(columns);
			}).keypress(function(e) {
				let keyCode = e.keyCode;
				if (keyCode === 13) {
				}
			})
		</script>
	</form>
</body>
</html>