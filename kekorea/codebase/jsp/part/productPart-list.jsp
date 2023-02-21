<%@page import="e3ps.part.service.PartHelper"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
// folder root
String root = PartHelper.PRODUCT_ROOT;
// admin
boolean isAdmin = CommonUtils.isAdmin();

boolean isBox = true;
isBox = true;

String context = (String) request.getParameter("context");

if (context == null) {
	context = "product";
}
boolean isProduct = false;
boolean isLibrary = false;
boolean isEplan = false;
String purl = "/Windchill/plm/part/listPart?context=product";
String lurl = "/Windchill/plm/part/listProductPart2?context=library";
String eurl = "/Windchill/plm/part/listProductPart2?context=eplan";

String title = "";
if ("product".equals(context)) {
	isProduct = true;
	// 		module = ModuleKeys.list_product_part.name();
	title = "부품";
} else if ("library".equals(context)) {
	isLibrary = true;
	// 		module = ModuleKeys.list_library_part.name();
	title = "라이브러리";
} else if ("eplan".equals(context)) {
	isEplan = true;
	// 		module = ModuleKeys.list_eplan_part.name();
	title = "EPLAN";
}
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/jsp/common/layouts/include_css.jsp"%>
<%@include file="/jsp/common/layouts/include_script.jsp"%>
<!-- auigrid -->
<%@include file="/jsp/include/auigrid.jsp"%>
</head>
<body>
	<input type="hidden" name="sessionid" id="sessionid">
	<input type="hidden" name="curPage" id="curPage">
	<table class="container_table">
		<tr>
			<!-- only folder tree.. -->
			<jsp:include page="/jsp/common/layouts/include_tree.jsp">
				<jsp:param value="<%=root%>" name="root" />
				<jsp:param value="<%=context.toUpperCase()%>" name="context" />
			</jsp:include>
			<td id="container_td">
				<table class="search_table">
					<tr>
						<th>부품분류</th>
						<td colspan="7">
							<input type="hidden" name="location" value="<%=root%>">
							<span id="location"><%=root%></span>
						</td>
					</tr>
					<%
					if (isProduct) {
					%>
					<tr>
						<th>파일이름</th>
						<td>
							<input type="text" name="fileName" class="AXInput wid200">
						</td>
						<th>품번</th>
						<td>
							<input type="text" name="partCode" class="AXInput wid200">
						</td>
						<th>품명</th>
						<td>
							<input type="text" name="partName" class="AXInput wid200">
						</td>
						<th>규격</th>
						<td>
							<input type="text" name="number" class="AXInput wid200">
						</td>
					</tr>
					<tr>
						<th>MATERIAL</th>
						<td>
							<input type="text" name="material" class="AXInput wid200">
						</td>
						<th>REMARK</th>
						<td>
							<input type="text" name="remark" class="AXInput wid200">
						</td>
						<th>MAKER</th>
						<td colspan="3">
							<input type="text" name="maker" class="AXInput wid200">
						</td>
					</tr>
					<tr class="detailPart">
						<th>작성자</th>
						<td>
							<input type="text" name="creators" id="creators" class="AXInput wid200" data-dbl="true">
							<i title="삭제" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="creators"></i>
						</td>
						<th>작성일</th>
						<td>
							<input type="text" name="predate" id="predate" class="AXInput">
							~
							<input type="text" name="postdate" id="postdate" class="AXInput twinDatePicker" data-start="predate">
							<i title="삭제" class="axi axi-ion-close-circled delete-calendar" data-start="predate" data-end="postdate"></i>
						</td>
						<th>수정자</th>
						<td>
							<input type="text" name="modifier" id="modifier" class="AXInput wid200" data-dbl="true">
							<i title="삭제" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="modifier"></i>
						</td>
						<th>수정일</th>
						<td>
							<input type="text" name="predate_m" id="predate_m" class="AXInput">
							~
							<input type="text" name="postdate_m" id="postdate_m" class="AXInput twinDatePicker_m" data-start="predate_m">
							<i title="삭제" class="axi axi-ion-close-circled delete-calendar" data-start="predate_m" data-end="postdate_m"></i>
						</td>
					</tr>
					<tr class="detailPart">
						<th>상태</th>
						<td>
							<select name="statesPart" id="statesPart" class="AXSelect wid200">
								<option value="">선택</option>
								<%
								String[] displays = PartHelper.PART_STATE_DISPLAY;
								String[] values = PartHelper.PART_STATE_VALUE;
								for (int i = 0; i < displays.length; i++) {
								%>
								<option value="<%=values[i]%>"><%=displays[i]%></option>
								<%
								}
								%>
							</select>
						</td>
						<th>버전</th>
						<td colspan="5">
							<label title="최신버전">
								<input type="radio" name="latest" value="true" checked="checked">
								<span class="latest">최신버전</span>
							</label>
							<label title="모든버전">
								<input type="radio" name="latest" value="false">
								<span class="latest">모든버전</span>
							</label>
						</td>
					</tr>
					<%
					} else {
					%>
					<tr>
						<th>파일이름</th>
						<td>
							<input type="text" name="fileName" class="AXInput wid200">
						</td>
						<th>품번</th>
						<td>
							<input type="text" name="partCode" class="AXInput wid200">
						</td>
					</tr>
					<tr>
						<th>품명</th>
						<td>
							<input type="text" name="partName" class="AXInput wid200">
						</td>
						<th>규격</th>
						<td>
							<input type="text" name="number" class="AXInput wid200">
						</td>

					</tr>
					<tr>
						<th>MATERIAL</th>
						<td>
							<input type="text" name="material" class="AXInput wid200">
						</td>
						<th>REMARK</th>
						<td>
							<input type="text" name="remark" class="AXInput wid200">
						</td>
					</tr>
					<tr>
						<th>MAKER</th>
						<td colspan="3">
							<input type="text" name="maker" class="AXInput wid200">
						</td>
					</tr>
					<tr class="detailPart">
						<th>작성자</th>
						<td>
							<input type="text" name="creators" id="creators" class="AXInput wid200" data-dbl="true">
							<i title="삭제" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="creators"></i>
						</td>
						<th>작성일</th>
						<td>
							<input type="text" name="predate" id="predate" class="AXInput">
							~
							<input type="text" name="postdate" id="postdate" class="AXInput twinDatePicker" data-start="predate">
							<i title="삭제" class="axi axi-ion-close-circled delete-calendar" data-start="predate" data-end="postdate"></i>
						</td>
					</tr>
					<tr class="detailPart">
						<th>수정자</th>
						<td>
							<input type="text" name="modifier" id="modifier" class="AXInput wid200" data-dbl="true">
							<i title="삭제" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="modifier"></i>
						</td>
						<th>수정일</th>
						<td>
							<input type="text" name="predate_m" id="predate_m" class="AXInput">
							~
							<input type="text" name="postdate_m" id="postdate_m" class="AXInput twinDatePicker_m" data-start="predate_m">
							<i title="삭제" class="axi axi-ion-close-circled delete-calendar" data-start="predate_m" data-end="postdate_m"></i>
						</td>
					</tr>
					<tr class="detailPart">
						<th>상태</th>
						<td>
							<select name="statesPart" id="statesPart" class="AXSelect wid200">
								<option value="">선택</option>
								<%
								String[] displays = PartHelper.PART_STATE_DISPLAY;
								String[] values = PartHelper.PART_STATE_VALUE;
								for (int i = 0; i < displays.length; i++) {
								%>
								<option value="<%=values[i]%>"><%=displays[i]%></option>
								<%
								}
								%>
							</select>
						</td>
						<th>버전</th>
						<td colspan="3">
							<label title="최신버전">
								<input type="radio" name="latest" value="true" checked="checked">
								<span class="latest">최신버전</span>
							</label>
							<label title="모든버전">
								<input type="radio" name="latest" value="false">
								<span class="latest">모든버전</span>
							</label>
						</td>
					</tr>
					<%
					}
					%>
				</table>
				<!-- button table -->
				<table class="btn_table">
					<tr>
						<td class="left">
							<!-- list jsp.. -->
							<div class="view_layer">
								<!--					<div class="non_paging_layer"> -->
								<ul>
									<li data-url="<%=purl%>" <%if (isProduct) {%> class="active_view" <%}%> id="product_view" title="부품">부품</li>
									<li data-url="<%=lurl%>" <%if (isLibrary) {%> class="active_view" <%}%> id="library_view" title="라이브러리">라이브러리</li>
									<li data-url="<%=eurl%>" <%if (isEplan) {%> class="active_view" <%}%> id="eplan_view" title="EPLAN">EPLAN</li>
									<li class="hidden">
										<span id="thumbnail" title="썸네일 리스트로 확인하세요.">썸네일 리스트로 확인하세요 </span>
									</li>
								</ul>
							</div>
						</td>
						<td class="right">
							<input type="button" value="상세조회" class="orangeBtn" id="detailPartBtn" title="상세조회">
							<input type="button" value="조회" class="blueBtn" id="searchBtn" title="조회">
							<input type="button" value="초기화" class="" id="initGrid" title="초기화">
						</td>
					</tr>
				</table>
				<div id="grid_wrap" style="height: 650px; border-top: 1px solid #3180c3;"></div>
			</td>
		</tr>
	</table>
</body>
<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "thumnail",
		headerText : "",
		width : 50

	}, {

		dataField : "name",
		headerText : "파일이름",
		width : 250

	}, {

		dataField : "part_code",
		headerText : "품번",
		width : 130

	}, {

		dataField : "name_of_parts",
		headerText : "품명",
		width : 200
	}, {

		dataField : "number",
		headerText : "규격",
		width : 150

	}, {

		dataField : "material",
		headerText : "MATERIAL",
		width : 150

	}, {

		dataField : "remark",
		headerText : "REMARK",
		width : 120

	}, {

		dataField : "maker",
		headerText : "MAKER",
		width : 120
	}, {

		dataField : "version",
		headerText : "버전",
		width : 80

	}, {

		dataField : "creator",
		headerText : "작성자",
		width : 100

	}, {

		dataField : "createDate",
		headerText : "작성일",
		width : 110

	}, {

		dataField : "modifier",
		headerText : "수정자",
		width : 100

	}, {

		dataField : "modifyDate",
		headerText : "수정일",
		width : 110
	}, {

		dataField : "state",
		headerText : "상태",
		width : 100

	}, {

		dataField : "location",
		headerText : "FOLDER",
		width : 60

	}, {

		dataField : "oid",
		headerText : "oid",
		dataType : "string",
		visible : false
	} ];

	function createAUIGrid(columnLayout) {
		const props = {
			showRowNumColumn : true,
			showRowCheckColumn : true,
			rowIdField : "oid",
			headerHeight : 30,
			rowHeight : 30,
			rowNumHeaderText : "번호",
			fillColumnSizeMode : true, // 화면 꽉채우기
			enableCellMerge : true,
			cellMergePolicy : "withNull"
		};
		myGridID = AUIGrid.create("#grid_wrap", columns, props);
		loadGridData();
		// LazyLoading 바인딩
		AUIGrid.bind(myGridID, "vScrollChange", vScrollChangeHandler);

		// 		AUIGrid.bind(myGridID, "cellDoubleClick", function(event) {
		// 				if(event.dataField == "name" || event.dataField == "number") {
		// 					var rowItem = event.item;
		// 					var url = "/Windchill/plm/part/viewPart";
		// 					var popupUrl = url + "?oid=" + rowItem.oid + "&popup=true";
		// 					$(document).openURLViewOpt(popupUrl, 1500, 750, "no");
		// 				}
		// 			});
	};

	function loadGridData() {
		let params = new Object();
		let url = getCallUrl("/part/listPart");
<%if (isProduct) {%>
	url = "/Windchill/plm/part/listPart";
<%} else if (isLibrary) {%>
	url = "/Windchill/plm/part/listLibrary";
<%} else if (isEplan) {%>
	url = "/Windchill/plm/part/listEplan";
<%}%>
	AUIGrid.showAjaxLoader(myGridID);
	parent.openLayer();
		call(url, params, function(data) {
			AUIGrid.removeAjaxLoader(myGridID);
			$("input[name=sessionid]").val(data.sessionid);
			$("input[name=curPage]").val(data.curPage);
			AUIGrid.setGridData(myGridID, data.list);
			parent.closeLayer();
			console.log();
		});
	};

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
		params.start = (curPage * 30);
		params.end = (curPage * 30) + 30;
		let url = getCallUrl("/appendData");
		AUIGrid.showAjaxLoader(myGridID);
		call(url, params, function(data) {
			if (data.list.length == 0) {
				last = true;
				alert("마지막 데이터 입니다.");
				AUIGrid.removeAjaxLoader(myGridID);
			} else {
				AUIGrid.appendData(myGridID, data.list);
				AUIGrid.removeAjaxLoader(myGridID);
				$("input[name=curPage]").val(parseInt(curPage) + 1);
			}
		})
	}

	$(function() {
		createAUIGrid(columns);

		$("#searchBtn").click(function() {
			loadGridData();
		})

	}).keypress(function(e) {
		let keyCode = e.keyCode;
		if (keyCode == 13) {
			loadGridData();
		}
	})

	$(window).resize(function() {
		AUIGrid.resize(myGridID);
	})
</script>
</html>