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
				<col width="130">
				<col width="*">
			</colgroup>
			<tr>
				<th>파일이름</th>
				<td>
					<input type="text" name="fileName" class="AXInput">
				</td>
				<th>품번</th>
				<td>
					<input type="text" name="partCode" class="AXInput">
				</td>
				<th>품명</th>
				<td>
					<input type="text" name="partName" class="AXInput">
				</td>
				<th>규격</th>
				<td>
					<input type="text" name="number" class="AXInput">
				</td>
			</tr>
			<tr>
				<th>MATERIAL</th>
				<td>
					<input type="text" name="material" class="AXInput">
				</td>
				<th>REMARK</th>
				<td>
					<input type="text" name="remark" class="AXInput">
				</td>
				<th>MAKER</th>
				<td colspan="3">
					<input type="text" name="maker" class="AXInput">
				</td>
			</tr>
		</table>

		<!-- 버튼 테이블 -->
		<table class="button-table">
			<tr>
				<td class="left">
					<input type="button" value="테이블 저장" title="테이블 저장" class="orange" onclick="saveColumnLayout('part-list');">
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
					dataField : "thumnail",
					headerText : "",
					dataType : "string",
					width : 60,
					renderer : {
						type : "ImageRenderer",
						altField : null,
						onClick : function(event) {
						}
					},
					filter : {
						showIcon : false,
						inline : false
					},
				}, {
					dataField : "name",
					headerText : "파일이름",
					dataType : "string",
					width : 350,
					style : "left indent10 underline",
					filter : {
						showIcon : true,
						useExMenu : true
					},
				}, {
					dataField : "part_code",
					headerText : "품번",
					dataType : "string",
					width : 120
				}, {
					dataField : "name_of_parts",
					headerText : "품명",
					dataType : "string",
					width : 300,
					style : "left indent10"
				}, {
					dataField : "number",
					headerText : "규격",
					dataType : "string",
					width : 150
				}, {
					dataField : "material",
					headerText : "MATERIAL",
					dataType : "string",
					width : 150
				}, {
					dataField : "remark",
					headerText : "REMARK",
					dataType : "string",
					width : 150
				}, {
					dataField : "maker",
					headerText : "MAKER",
					dataType : "string",
					width : 150
				}, {
					dataField : "version",
					headerText : "버전",
					dataType : "string", // 버전 사이즈 80
					width : 80
				}, {
					dataField : "creator",
					headerText : "작성자",
					dataType : "string", // 날짜 및 사람명 컬럼 사이즈 100
					width : 100
				}, {
					dataField : "createdDate",
					headerText : "작성일",
					dataType : "date",
					formatString : "yyyy-mm-dd",
					width : 100
				}, {
					dataField : "modifier",
					headerText : "수정자",
					dataType : "string",
					width : 100
				}, {
					dataField : "modifiedDate",
					headerText : "수정일",
					dataType : "date",
					formatString : "yyyy-mm-dd",
					width : 100
				}, {
					dataField : "state",
					headerText : "상태",
					dataType : "string", //상태 사이즈 100
					width : 100
				}, {
					dataField : "location",
					headerText : "FOLDER",
					dataType : "string",
					width : 150
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
					showRowCheckColumn : true, // 엑스트라 체크 박스 사용 여부
				};

				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				//화면 첫 진입시 리스트 호출 함수
				loadGridData();
				// Lazy Loading 이벤트 바인딩
				AUIGrid.bind(myGridID, "vScrollChange", vScrollChangeHandler);
			}

			function loadGridData() {
				parent.openLayer();
				let params = new Object();
				let url = getCallUrl("/part/list");
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
				let columns = loadColumnLayout("part-list");
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