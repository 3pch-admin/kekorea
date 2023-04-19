<%@page import="java.util.Map"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.common.util.ContentUtils"%>
<%@page import="e3ps.epm.keDrawing.dto.KeDrawingDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<CommonCode> maks = (ArrayList<CommonCode>) request.getAttribute("maks");
ArrayList<Map<String, String>> projectTypes = (ArrayList<Map<String, String>>) request.getAttribute("projectTypes");
String before = (String) request.getAttribute("before");
String end = (String) request.getAttribute("end");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/include/css.jsp"%>
<%@include file="/extcore/include/script.jsp"%>
<%@include file="/extcore/include/auigrid.jsp"%>
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js?v=1"></script>
</head>
<body>
	<form>
		<input type="hidden" name="sessionid" id="sessionid">
		<input type="hidden" name="curPage" id="curPage">

		<table class="search-table">
			<colgroup>
				<col width="100">
				<col width="500">
				<col width="100">
				<col width="500">
				<col width="100">
				<col width="500">
			</colgroup>
			<tr>
				<th>KEK 작번</th>
				<td class="indent5">
					<input type="text" name="kekNumber" id="kekNumber" class="width-300">
				</td>
				<th>발행일</th>
				<td class="indent5">
					<input type="text" name="pdateFrom" id="pdateFrom" class="width-100" value="<%=before%>">
					~
					<input type="text" name="pdateTo" id="pdateTo" class="width-100" value="<%=end%>">
				</td>
				<th>작번 유형</th>
				<td class="indent5">
					<select name="projectType" id="projectType" class="width-200">
						<option value="">선택</option>
						<%
						for (Map projectType : projectTypes) {
						%>
						<option value="<%=projectType.get("key")%>"><%=projectType.get("value")%></option>
						<%
						}
						%>
					</select>
				</td>
			</tr>
			<tr>
				<th>막종</th>
				<td colspan="5">
					&nbsp;
					<%
					for (CommonCode mak : maks) {
					%>
					<div class="pretty p-switch">
						<input type="checkbox" name="mak" value="<%=mak.getCode()%>" checked="checked">
						<div class="state p-success">
							<label>
								<b><%=mak.getName()%></b>
							</label>
						</div>
					</div>
					<%
					}
					%>
				</td>
			</tr>
		</table>

		<iframe src="" style="height: 360px;" id="chart"></iframe>

		<table class="button-table">
			<tr>
				<td class="left">
					<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('korea-list');">
					<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('korea-list');">
					<input type="button" value="통합수배표비교" title="통합수배표비교" onclick="partlistCompare('a');">
					<input type="button" value="P-BOM비교" title="P-BOM비교" class="red" onclick="tbomCompare();">
					<input type="button" value="T-BOM비교" title="T-BOM비교" class="blue" onclick="tbomCompare();">
					<input type="button" value="기계수배표비교" title="기계수배표비교" class="orange" onclick="partlistCompare('m');">
					<input type="button" value="전기수배표비교" title="전기수배표비교" onclick="partlistCompare('e');">
					<input type="button" value="도면일람표비교" title="도면일람표비교" class="blue" onclick="workOrderCompare();">
					<input type="button" value="CONFIG SHEET비교" title="CONFIG SHEET비교" class="red" onclick="loadGridData();">
					<input type="button" value="이력비교" title="이력비교" class="orange" onclick="historyCompare();">
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

		<div id="grid_wrap" style="height: 340px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID;
			const columns = [ {
				dataField : "projectType_name",
				headerText : "작번유형",
				dataType : "string",
				width : 80,
			}, {
				dataField : "customer_name",
				headerText : "거래처",
				dataType : "string",
				width : 100,
			}, {
				dataField : "install_name",
				headerText : "설치장소",
				dataType : "string",
				width : 100,
			}, {
				dataField : "mak_name",
				headerText : "막종",
				dataType : "string",
				width : 100,
			}, {
				dataField : "detail_name",
				headerText : "막종상세",
				dataType : "string",
				width : 100,
			}, {
				dataField : "kekNumber",
				headerText : "KEK 작번",
				dataType : "string",
				width : 100,
				renderer : {
					type : "LinkRenderer",
					baseUrl : "javascript",
					jsCallback : function(rowIndex, columnIndex, value, item) {
						const oid = item.oid;
						const url = getCallUrl("/project/info?oid=" + oid);
						popup(url);
					}
				},
			}, {
				dataField : "keNumber",
				headerText : "KE 작번",
				dataType : "string",
				width : 100,
				renderer : {
					type : "LinkRenderer",
					baseUrl : "javascript",
					jsCallback : function(rowIndex, columnIndex, value, item) {
						const oid = item.oid;
						const url = getCallUrl("/project/info?oid=" + oid);
						popup(url);
					}
				},
			}, {
				dataField : "userId",
				headerText : "USER ID",
				dataType : "string",
				width : 100,
			}, {
				dataField : "description",
				headerText : "작업 내용",
				dataType : "string",
				width : 450,
				style : "aui-left",
			}, {
				dataField : "pdate",
				headerText : "발행일",
				dataType : "date",
				formatString : "yyyy-mm-dd",
				width : 100,
			}, {
				dataField : "completeDate",
				headerText : "설계 완료일",
				dataType : "date",
				formatString : "yyyy-mm-dd",
				width : 100,
			}, {
				dataField : "customDate",
				headerText : "요구 납기일",
				dataType : "date",
				formatString : "yyyy-mm-dd",
				width : 100,
			}, {
				dataField : "model",
				headerText : "모델",
				dataType : "string",
				width : 130,
			}, {
				dataField : "machine",
				headerText : "기계 담당자",
				dataType : "string",
				width : 100,
			}, {
				dataField : "elec",
				headerText : "전기 담당자",
				dataType : "string",
				width : 100,
			}, {
				dataField : "soft",
				headerText : "SW 담당자",
				dataType : "string",
				width : 100,
			}, {
				dataField : "kekProgress",
				headerText : "진행율",
				postfix : "%",
				width : 80,
				renderer : {
					type : "BarRenderer",
					min : 0,
					max : 100
				},
			}, {
				dataField : "kekState",
				headerText : "작번상태",
				dataType : "string",
				width : 100,
			} ]

			function createAUIGrid(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : true,
					showRowCheckColumn : true,
					rowNumHeaderText : "번호",
					showAutoNoDataMessage : false,
					selectionMode : "multipleCells",
					enableRightDownFocus : true,
					filterLayerWidth : 320,
					filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
					useContextMenu : true,
					enableRowCheckShiftKey : true,
					contextMenuItems : [ {
						label : "통합 수배표 비교",
						callback : contextItemHandler
					}, {
						label : "기계 수배표 비교",
						callback : contextItemHandler
					}, {
						label : "전기 수배표 비교",
						callback : contextItemHandler
					}, {
						label : "T-BOM 비교",
						callback : contextItemHandler
					}, {
						label : "도면 일람표 비교",
						callback : contextItemHandler
					}, {
						label : "CONFIG SHEET 비교",
						callback : contextItemHandler
					}, {
						label : "이력 비교",
						callback : contextItemHandler
					} ],
					autoGridHeight : true
				}
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				loadGridData();
				AUIGrid.bind(myGridID, "cellClick", auiCellClickHandler);
			}

			function auiCellClickHandler(event) {
				const item = event.item;
				rowIdField = AUIGrid.getProp(event.pid, "rowIdField");
				rowId = item[rowIdField];
				rowIdField = AUIGrid.getProp(event.pid, "rowIdField");
				rowId = item[rowIdField];
				if (AUIGrid.isCheckedRowById(event.pid, rowId)) {
					AUIGrid.addUncheckedRowsByIds(event.pid, rowId);
				} else {
					AUIGrid.addCheckedRowsByIds(event.pid, rowId);
				}
			}

			function contextItemHandler(event) {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				const arr = [];
				if (checkedItems.length <= 1) {
					alert("데이터 비교를 위해선 최소 2개 이상의 데이터를 선택해야 합니다.");
					return false;
				}
				for (let i = 1; i < checkedItems.length; i++) {
					arr.push(checkedItems[i].item.oid);
				}
				switch (event.contextIndex) {
				case 0:
					const url0 = getCallUrl("/partlist/compare?oid=" + checkedItems[0].item.oid + "&compareArr=" + arr.join(",") + "&invoke=a");
					popup(url0);
					break;
				case 1:
					const url1 = getCallUrl("/partlist/compare?oid=" + checkedItems[0].item.oid + "&compareArr=" + arr.join(",") + "&invoke=m");
					popup(url1);
					break;
				case 2:
					const url2 = getCallUrl("/partlist/compare?oid=" + checkedItems[0].item.oid + "&compareArr=" + arr.join(",") + "&invoke=e");
					popup(url2);
					break;
				case 3:
					const url3 = getCallUrl("/tbom/compare?oid=" + checkedItems[0].item.oid + "&compareArr=" + arr.join(","));
					popup(url3, 1500, 800);
					break;
				case 4:
					const url4 = getCallUrl("/workOrder/compare?oid=" + checkedItems[0].item.oid + "&compareArr=" + arr.join(","));
					popup(url4);
					break;
				case 5:
					const url5 = getCallUrl("/configSheet/compare?oid=" + checkedItems[0].item.oid + "&compareArr=" + arr.join(","));
					popup(url5);
					break;
				case 6:
					const url6 = getCallUrl("/history/compare?oid=" + checkedItems[0].item.oid + "&compareArr=" + arr.join(","));
					popup(url6);
					break;
				}
			}

			function partlistCompare(invoke) {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				const arr = [];
				if (checkedItems.length <= 1) {
					alert("데이터 비교를 위해선 최소 2개 이상의 데이터를 선택해야 합니다.");
					return false;
				}
				for (let i = 1; i < checkedItems.length; i++) {
					arr.push(checkedItems[i].item.oid);
				}
				const url = getCallUrl("/partlist/compare?oid=" + checkedItems[0].item.oid + "&compareArr=" + arr.join(",") + "&invoke=" + invoke);
				popup(url);
			}

			function historyCompare() {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				const arr = [];
				if (checkedItems.length <= 1) {
					alert("데이터 비교를 위해선 최소 2개 이상의 데이터를 선택해야 합니다.");
					return false;
				}
				for (let i = 1; i < checkedItems.length; i++) {
					arr.push(checkedItems[i].item.oid);
				}
				const url = getCallUrl("/history/compare?oid=" + checkedItems[0].item.oid + "&compareArr=" + arr.join(","));
				popup(url);
			}

			function tbomCompare() {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				const arr = [];
				if (checkedItems.length <= 1) {
					alert("데이터 비교를 위해선 최소 2개 이상의 데이터를 선택해야 합니다.");
					return false;
				}
				for (let i = 1; i < checkedItems.length; i++) {
					arr.push(checkedItems[i].item.oid);
				}
				const url = getCallUrl("/tbom/compare?oid=" + checkedItems[0].item.oid + "&compareArr=" + arr.join(","));
				popup(url);
			}

			function workOrderCompare() {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
				const arr = [];
				if (checkedItems.length <= 1) {
					alert("데이터 비교를 위해선 최소 2개 이상의 데이터를 선택해야 합니다.");
					return false;
				}
				for (let i = 1; i < checkedItems.length; i++) {
					arr.push(checkedItems[i].item.oid);
				}
				const url = getCallUrl("/workOrder/compare?oid=" + checkedItems[0].item.oid + "&compareArr=" + arr.join(","));
				popup(url);
			}

			function loadGridData() {
				const params = new Object();
				const url = getCallUrl("/korea/list");
				const psize = document.getElementById("psize").value;
				const pdateFrom = document.getElementById("pdateFrom").value;
				const pdateTo = document.getElementById("pdateTo").value;
				const projectType = document.getElementById("projectType").value;
				const kekNumbers = [];
				const kekNumber = document.getElementById("kekNumber").value;
				const values = kekNumber.split(",");
				for (let i = 0; i < values.length; i++) {
					if (values[i] !== "") {
						kekNumbers.push(values[i]);
					}
				}

				const mak = document.querySelectorAll("input[name=mak]:checked");
				const maks = [];
				for (let i = 0; i < mak.length; i++) {
					maks.push(mak[i].value);
				}

				params.kekNumbers = kekNumbers;
				params.maks = maks;
				params.psize = psize;
				params.pdateFrom = pdateFrom;
				params.pdateTo = pdateTo;
				params.projectType = projectType;
				AUIGrid.showAjaxLoader(myGridID);
				parent.openLayer();
				call(url, params, function(data) {
					AUIGrid.removeAjaxLoader(myGridID);
					document.getElementById("sessionid").value = data.sessionid;
					document.getElementById("curPage").value = data.curPage;
					AUIGrid.setGridData(myGridID, data.list);
					parent.closeLayer();
					AUIGrid.resize(myGridID);
					selectbox("psize");
				});

				callChart(kekNumbers, pdateFrom, pdateTo, projectType, maks);
			}

			function callChart(kekNumbers, pdateFrom, pdateTo, projectType, maks) {
				let url = getCallUrl("/korea/chart");
				url += "?kekNumbers=" + kekNumbers.join(",") + "&pdateFrom=" + pdateFrom + "&pdateTo=" + pdateTo + "&projectType=" + projectType + "&maks=" + maks.join(",");
				const chart = document.getElementById("chart");
				chart.src = url;
			}

			document.getElementById("kekNumber").addEventListener("input", function() {
				this.value = this.value.toUpperCase().replace(/[^a-zA-Z0-9]/g, "");
			});

			document.addEventListener("DOMContentLoaded", function() {
				document.getElementById("kekNumber").focus();
				twindate("pdate");
				selectbox("projectType");
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);
				selectbox("psize");
			})

			document.addEventListener("keydown", function(event) {
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