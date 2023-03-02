<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<!-- AUIGrid 사용을 위한 필수 부분 -->
<%@include file="/extcore/include/auigrid.jsp"%>
</head>
<body>
	<table>
		<tr>
			<td>
				<input type="button" value="저장" title="저장" class="outline" id="saveBtn">
			</td>
		</tr>
	</table>
	<!-- AUIGrid -->
	<div id="grid_wrap" style="height: 500px; border-top: 1px solid #3180c3; margin-bottom: 10px;"></div>
	<!-- Attach File Area -->
	<div class="AXUpload5" id="secondary_layer"></div>
	<div class="AXUpload5QueueBox_list" id="uploadQueueBox" style="height: 200px;"></div>
	<script type="text/javascript">
		let myGridID;
		const columns = [ {
			dataField : "dwg_check",
			headerText : "체크(DWG_NO)",
			dataType : "string",
			width : 120,
			editable : false,
			renderer : {
				type : "CheckBoxEditRenderer",
			},
		}, {
			dataField : "ycode_check",
			headerText : "체크(YCODE)",
			dataType : "string",
			width : 120,
			editable : false,
			renderer : {
				type : "CheckBoxEditRenderer",
			},
		}, {
			dataField : "number",
			headerText : "품번",
			dataType : "string",
			width : 150,
		}, {
			dataField : "name",
			headerText : "품명",
			dataType : "string",
			width : 250,
		}, {
			dataField : "spec",
			headerText : "규격",
			dataType : "string",
		}, {
			dataField : "maker",
			headerText : "메이커",
			dataType : "string",
			width : 150,
		}, {
			dataField : "customer",
			headerText : "기본구매처",
			dataType : "string",
			width : 150,
		}, {
			dataField : "unit",
			headerText : "기준단위",
			dataType : "string",
			width : 100,
		}, {
			dataField : "price",
			headerText : "단가",
			dataType : "numeric",
			width : 150,
			formatString : "#,###",
			editRenderer : {
				type : "InputEditRenderer",
				onlyNumeric : true, // 0~9만 입력가능
			},
		}, {
			dataField : "currency",
			headerText : "통화",
			dataType : "string",
			width : 100,
		}, ]

		let secondary = new AXUpload5();
		function load() {
			secondary.setConfig({
				isSingleUpload : false,
				targetID : "secondary_layer",
				uploadFileName : "secondary",
				buttonTxt : "파일 선택",
				uploadMaxFileSize : (1024 * 1024 * 1024),
				uploadUrl : "/Windchill/plm/content/aui/auiUpload",
				dropBoxID : "uploadQueueBox",
				queueBoxID : "uploadQueueBox",
				uploadPars : {
					roleType : "secondary"
				},
				uploadMaxFileCount : 100,
				deleteUrl : "/Windchill/plm/content/delete",
				fileKeys : {},
				onComplete : function() {
				},
			})
		}
		load();

		function createAUIGrid(columnLayout) {
			const props = {
				rowIdField : "rowId",
				// 공통
				headerHeight : 30, // 헤더 행 높이
				rowHeight : 30, // 행 높이
				showRowNumColumn : true, // 번호 컬럼 표시
				rowNumHeaderText : "번호", // 번호 컬럼 이름 변경
				showStateColumn : true, // 컬럼 상태 표기 행
				showRowCheckColumn : true, // 체크 박스 표시 여부 행
				selectionMode : "multipleCells", // 그리드 선택 모드
				// 공통 끝
				editable : true, // 수정 가능 여부
				fillColumnSizeMode : true, // 화면 꽉채우기
			}

			myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
			// 그리드 생성후 작업
			readyHandler();
			AUIGrid.bind(myGridID, "cellEditEnd", cellEditEndHandler);
		}

		function cellEditEndHandler(event) {
			let dataField = event.dataField;
			let item = event.item;
			let rowIndex = event.rowIndex;

			if (dataField === "spec") {
				let url = getCallUrl("/part/bundleValidatorSpec?spec=" + item.spec);
				call(url, null, function(data) {
					// 서버 없으면 OK
					let obj = {
						dwg_check : data.dwg_check
					}
					AUIGrid.updateRow(myGridID, obj, rowIndex);
				}, "GET");
			}

			if (dataField === "number") {
				let url = getCallUrl("/part/bundleValidatorNumber?number=" + item.number);
				call(url, null, function(data) {
					// 서버 없으면 OK
					let obj = {
						ycode_check : data.ycode_check
					}
					AUIGrid.updateRow(myGridID, obj, rowIndex);
				}, "GET");

				let ycode_check = AUIGrid.getCellValue(myGridID, event.rowIndex, "ycode_check");
				alert(ycode_check);
			}
		}

		function readyHandler() {
			AUIGrid.addRow(myGridID, new Object(), "first");
		}

		$(function() {
			createAUIGrid(columns);
			parent.closeLayer();
		})

		$(window).resize(function() {
			AUIGrid.resize(myGridID);
		})
	</script>
</body>
</html>