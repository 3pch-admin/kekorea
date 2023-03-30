<%@page import="e3ps.korea.service.KoreaHelper"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.admin.commonCode.CommonCodeType"%>
<%@page import="org.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String code = (String) request.getAttribute("code");
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
		<table class="button-table">
			<tr>
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
		<div id="grid_wrap" style="height: 390px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID;
			const columns = [ {
				dataField : "state",
				headerText : "진행상태",
				dataType : "string",
				width : 80,
				renderer : {
					type : "TemplateRenderer",
				},
			}, {
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
				width : 130,
			}, {
				dataField : "keNumber",
				headerText : "KE 작번",
				dataType : "string",
				width : 130,
				style : "underline",
			}, {
				dataField : "userId",
				headerText : "USER ID",
				dataType : "string",
				width : 100,
				style : "underline",
			}, {
				dataField : "description",
				headerText : "작업 내용",
				dataType : "string",
				width : 450,
				style : "left indent10",
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
					rowHeight : 30,
					showRowNumColumn : true,
					showRowCheckColumn : true,
					showStateColumn : true,
					rowNumHeaderText : "번호",
					noDataMessage : "검색 결과가 없습니다.",
					selectionMode : "multipleCells",
					enableRightDownFocus : true,
					filterLayerWidth : 320,
					filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
					useContextMenu : true,
					contextMenuItems : [ {
						label : "수배표 비교",
						callback : contextItemHandler
					}, {
						label : "도면 일람표 비교",
						callback : contextItemHandler
					}, {
						label : "CONFIG SHEET 비교",
						callback : contextItemHandler
					} ]
				}
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				loadGridData();
			}

			function contextItemHandler(event) {
				switch (event.contextIndex) {
				case 0:
					break;
				case 1:
					const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
					if (checkedItems.length <= 0) {
						alert("도면 일람표 비교할 작번을 선택하세요.");
						return;
					}
					if (checkedItems.length !== 2) {
						alert("도면 일람표 비교할 작번을 2개 선택하세요.");
						return;
					}
					const oid = checkedItems[0].item.oid;
					const _oid = checkedItems[1].item.oid;
					const url = getCallUrl("/workOrder/compare?oid=" + oid + "&_oid=" + _oid);
					popup(url);
					break;
				case 2:
					break;
				}
			}

			function loadGridData() {
				const params = new Object();
				const url = getCallUrl("/korea/list");
				const psize = document.getElementById("psize").value;
				params.code = "<%=code%>";
				params.psize = psize;
				AUIGrid.showAjaxLoader(myGridID);
				parent.parent.openLayer();
				call(url, params, function(data) {
					AUIGrid.removeAjaxLoader(myGridID);
					document.getElementById("sessionid").value = data.sessionid;
					document.getElementById("curPage").value = data.curPage;
					AUIGrid.setGridData(myGridID, data.list);
					parent.parent.closeLayer();
				});
			}

			document.addEventListener("DOMContentLoaded", function() {
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);
				selectbox("psize");
			});

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