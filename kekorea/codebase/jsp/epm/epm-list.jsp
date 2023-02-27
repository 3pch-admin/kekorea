<%@page import="e3ps.org.service.OrgHelper"%>
<%@page import="wt.epm.EPMDocumentType"%>
<%@page import="e3ps.epm.service.EpmHelper"%>
<%@page import="e3ps.part.service.PartHelper"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="e3ps.common.util.ColumnUtils"%>
<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@page import="wt.fc.PagingQueryResult"%>
<%@page import="e3ps.common.util.PageQueryUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
	<table class="search_table">
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
			<th>REFERENCE 도면</th>
			<td colspan="3">
				<input type="text" name="referenceDrwing" class="AXInput wid200">
			</td>
		</tr>
		<tr class="detailEpm">
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
		<tr class="detailEpm">
			<th>상태</th>
			<td>
				<select name="state" id="state" class="AXSelect wid200">
					<option value="">선택</option>
					<%
					String[] displays = EpmHelper.EPM_STATE_DISPLAY;
					String[] values = EpmHelper.EPM_STATE_VALUE;
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
	</table>

	<!-- button table -->
	<table class="btn_table">
		<tr>
			<td class="right">
				<input type="button" value="상세조회" class="orangeBtn" id="detailEpmBtn" title="상세조회">
				<input type="button" value="조회" class="blueBtn" id="searchBtn" title="조회">
				<input type="button" value="초기화" class="" id="initGrid" title="초기화">
			</td>
		</tr>
	</table>
	<div id="grid_wrap" style="height: 585px; border-top: 1px solid #3180c3;"></div>
</body>
<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "thumnail",
		headerText : "",
		dataType : "string",
		width : 60,
		renderer : {
			type : "ImageRenderer",
			altField : null,
			onClick : function(event) {
			}
		}
	}, {
		dataField : "name",
		headerText : "파일이름",
		dataType : "string",
		width : 350,
		style : "left indent10",
	}, {
		dataField : "part_code",
		headerText : "품번",
		dataType : "string",
		width : 130
	}, {
		dataField : "name_of_parts",
		headerText : "품명",
		dataType : "string",
		width : 350,
		style : "left indent10",
	}, {
		dataField : "dwg_no",
		headerText : "규격",
		dataType : "string",
		width : 130
	}, {
		dataField : "material",
		headerText : "MATERIAL",
		dataType : "string",
		width : 130
	}, {
		dataField : "remark",
		headerText : "REMARK",
		dataType : "string",
		width : 150
	}, {
		dataField : "reference",
		headerText : "REFERENCE 도면",
		dataType : "string",
		width : 150
	}, {
		dataField : "version",
		headerText : "버전",
		dataType : "string",
		width : 80
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
		dataField : "creator",
		headerText : "작성자",
		dataType : "string",
		width : 100
	}, {
		dataField : "createdDate",
		headerText : "작성일",
		dataType : "date",
		formatString : "yyyy-mm-dd",
		width : 100
	}, {
		dataField : "state",
		headerText : "상태",
		dataType : "string",
		width : 100
	}, {
		dataField : "location",
		headerText : "폴더",
		dataType : "string",
		width : 100
	}, {
		dataField : "oid",
		headerText : "oid",
		dataType : "string",
		visible : false
	} ]

	function createAUIGrid(columns) {
		const props = {
			rowIdField : "oid",
			headerHeight : 30,
			rowHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showStateColumn : true,
			noDataMessage : "검색 결과가 없습니다.",
			enableFilter : true,
		// 			fixedColumnCount : 8
		};
		myGridID = AUIGrid.create("#grid_wrap", columns, props);
		loadGridData();
		AUIGrid.bind(myGridID, "vScrollChange", vScrollChangeHandler);
		AUIGrid.bind(myGridID, "cellClick", auiCellClickHandler);
	}

	function auiCellClickHandler(event) {
		let oid = event.item.oid;
		let dataField = event.dataField;
	}

	function loadGridData() {
		let params = new Object();
		let url = getCallUrl("/epm/list");
		AUIGrid.showAjaxLoader(myGridID);
		parent.openLayer();
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
		params.start = (curPage * 30);
		params.end = (curPage * 30) + 30;
		let url = getCallUrl("/appendData");
		AUIGrid.showAjaxLoader(myGridID);
		call(url, params, function(data) {
			if (data.list.length == 0) {
				last = true;
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
		
		radio("latest");
		rangeDate("postdate");
		rangeDate("postdate_m");
	})
</script>
</html>