<%@page import="org.json.JSONArray"%>
<%@page import="java.util.Map"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
JSONArray categorys = (JSONArray) request.getAttribute("categorys");
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
		<!-- 버튼 테이블 -->
		<table class="button-table">
			<tr>
				<td class="right">
					<input type="button" value="등록" title="등록" onclick="create();">
				</td>
			</tr>
		</table>

		<table class="create-table">
			<colgroup>
				<col width="130">
				<col width="*">
			</colgroup>
			<tr>
				<th class="req">CONFIG SHEET 제목</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="AXInput width-700">
				</td>
			</tr>
			<tr>
				<th class="req">KEK 작번</th>
				<td>
					<jsp:include page="/extcore/include/project-include.jsp">
						<jsp:param value="" name="oid" />
						<jsp:param value="create" name="mode" />
						<jsp:param value="true" name="multi" />
						<jsp:param value="" name="obj" />
						<jsp:param value="150" name="height" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th>설명</th>
				<td class="indent5">
					<textarea name="description" id="description" rows="6" cols=""></textarea>
				</td>
			</tr>
			<tr>
				<th>첨부파일</th>
				<td class="indent5">
					<jsp:include page="/extcore/include/secondary-include.jsp">
						<jsp:param value="" name="oid" />
						<jsp:param value="create" name="mode" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="req">결재</th>
				<td>
					<jsp:include page="/extcore/include/register-include.jsp">
						<jsp:param value="200" name="height" />
					</jsp:include>
				</td>
			</tr>
		</table>
		<table class="button-table">
			<tr>
				<td class="left">
					<input type="button" value="행 추가" title="행 추가" class="blue" onclick="addRow();">
					<input type="button" value="행 삭제" title="행 삭제" class="red" onclick="deleteRow();">
				</td>
			</tr>
		</table>
		<div id="grid_wrap" style="height: 400px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID;
			const categorys = <%=categorys%>
			let itemListMap = {};
			let specListMap = {};
			const columns = [ {
				dataField : "category_code",
				headerText : "CATEGORY",
				dataType : "string",
				width : 250,
				cellMerge : true,
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
					list : categorys, //key-value Object 로 구성된 리스트
					keyField : "key", // key 에 해당되는 필드명
					valueField : "value", // value 에 해당되는 필드명,
					descendants : [ "item_code" ], // 자손 필드들
					descendantDefaultValues : [ "-" ], // 변경 시 자손들에게 기본값 지정
				},
				labelFunction : function(rowIndex, columnIndex, value, headerText, item) { // key-value 에서 엑셀 내보내기 할 때 value 로 내보내기 위한 정의
					let retStr = ""; // key 값에 맞는 value 를 찾아 반환함.
					for (let i = 0, len = categorys.length; i < len; i++) {
						if (categorys[i]["key"] == value) {
							retStr = categorys[i]["value"];
							break;
						}
					}
					return retStr == "" ? value : retStr;
				},
			}, {
				dataField : "item_code",
				headerText : "ITEM",
				dataType : "string",
				width : 200,
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
					descendants : [ "spec_code" ], // 자손 필드들
					descendantDefaultValues : [ "-" ], // 변경 시 자손들에게 기본값 지정
					listFunction : function(rowIndex, columnIndex, item, dataField) {
						const param = item.category_code;
						const dd = itemListMap[param];
						if (dd === undefined) {
							return [];
						}
						return dd;
					},
				},
				labelFunction : function(rowIndex, columnIndex, value, headerText, item) { // key-value 에서 엑셀 내보내기 할 때 value 로 내보내기 위한 정의
					let retStr = ""; // key 값에 맞는 value 를 찾아 반환함.
					const param = item.category_code;
					const dd = itemListMap[param]; // param으로 보관된 리스트가 있는지 여부
					if (dd === undefined) {
						return value;
					}
					for (let i = 0, len = dd.length; i < len; i++) {
						if (dd[i]["key"] == value) {
							retStr = dd[i]["value"];
							break;
						}
					}
					return retStr == "" ? value : retStr;
				},
			}, {
				dataField : "spec_code",
				headerText : "사양",
				dataType : "string",
				width : 250,
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
						const param = item.item_code;
						const dd = specListMap[param];
						if (dd === undefined) {
							return [];
						}
						return dd;
					},
				},
				labelFunction : function(rowIndex, columnIndex, value, headerText, item) { // key-value 에서 엑셀 내보내기 할 때 value 로 내보내기 위한 정의
					let retStr = ""; // key 값에 맞는 value 를 찾아 반환함.
					const param = item.item_code;
					const dd = specListMap[param]; // param으로 보관된 리스트가 있는지 여부
					if (dd === undefined) {
						return value;
					}
					for (let i = 0, len = dd.length; i < len; i++) {
						if (dd[i]["key"] == value) {
							retStr = dd[i]["value"];
							break;
						}
					}
					return retStr == "" ? value : retStr;
				},				
			}, {
				dataField : "note",
				headerText : "NOTE",
				dataType : "string",
			}, {
				dataField : "apply",
				headerText : "APPLY",
				dataType : "string",
				width : 350
			} ]

			function createAUIGrid(columnLayout) {
				const props = {
					rowIdField : "loid",
					// 그리드 공통속성 시작
					headerHeight : 30, // 헤더높이
					rowHeight : 30, // 행 높이
					showRowNumColumn : true, // 번호 행 출력 여부
					showStateColumn : true, // 상태표시 행 출력 여부
					rowNumHeaderText : "번호", // 번호 행 텍스트 설정
					selectionMode : "multipleCells",
					// 그리드 공통속성 끝
					showRowCheckColumn : true,
					editable : true,
					enableCellMerge : true
				};
				myGridID = AUIGrid.create("#grid_wrap", columns, props);
				AUIGrid.bind(myGridID, "cellEditEnd", auiCellEditEndHandler);
			}

			function auiCellEditEndHandler(event) {
				const dataField = event.dataField;
				const item = event.item;
				const rowIndex = event.rowIndex;
				if (dataField === "category_code") {
					const categoryCode = item.category_code;
					const url = getCallUrl("/commonCode/getChildrens?parentCode=" + categoryCode + "&codeType=CATEGORY");
					call(url, null, function(data) {
						itemListMap[categoryCode] = data.list;
					}, "GET");
				}
				
				if(dataField === "item_code") {
					const itemCode = item.item_code;
					const url = getCallUrl("/commonCode/getChildrens?parentCode=" + itemCode + "&codeType=CATEGORY_ITEM");
					call(url, null, function(data) {
						specListMap[itemCode] = data.list;
					}, "GET");
				}
			}

			// 행 추가
			function addRow() {
				const item = new Object();
				AUIGrid.addRow(myGridID, item, "last");
			}

			// 행 삭제
			function deleteRow() {
				const checked = AUIGrid.getCheckedRowItems(myGridID);
				for (let i = checked.length - 1; i >= 0; i--) {
					const rowIndex = checked[i].rowIndex;
					AUIGrid.removeRow(myGridID, rowIndex);
				}
			};

			document.addEventListener("DOMContentLoaded", function() {
				createAUIGrid(columns);
				_createAUIGrid(_columns);
				_createAUIGrid_(_columns_); // 결재
			});

			window.addEventListener("resize", function() {
				AUIGrid.bind(_myGridID);
				AUIGrid.bind(_myGridID_);
			});
		</script>
	</form>
</body>
</html>