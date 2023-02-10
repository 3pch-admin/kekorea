<%@page import="e3ps.project.template.beans.TemplateViewData"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
TemplateViewData data = (TemplateViewData) request.getAttribute("data");
%>
<!-- auigrid -->
<%@include file="/jsp/include/auigrid.jsp"%>
<table class="btn_table">
	<tr>
		<td>
			<div class="header_title">
				<i class="axi axi-subtitles"></i>
				<span>템플릿 정보</span>
			</div>
		</td>
		<td class="right">
			<input type="button" value="닫기" id="closeBtn" title="닫기" class="blueBtn">
		</td>
	</tr>
</table>

<table>
	<colgroup>
		<col width="50%">
		<col width="30">
		<col width="49%">
	</colgroup>
	<tr>
		<td>
			<div id="grid_wrap" style="height: 920px; border-top: 1px solid #3180c3;"></div>
		</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
</table>
<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "name",
		headerText : "템플릿 명",
		dataType : "string",
		width : 300,
	}, {
		dataField : "description",
		headerText : "설명",
// 		style : "left indent10",
		dataType : "string",
	}, {
		dataField : "oid",
		headerText : "oid",
		dataType : "string",
		visible : false
	} ]

	function createAUIGrid(columnLayout) {
		const props = {
			rowIdField : "oid",
			headerHeight : 30,
			rowHeight : 30,
			showRowNumColumn : false,
			displayTreeOpen : true,
			treeColumnIndex : 0,
			enableDrag : true,
			enableDragByCellDrag : true,
			enableDrop : true,
			enableUndoRedo : true,
			editable : true,
			softRemoveRowMode : false,
			selectionMode : "multipleCells",
			treeLevelIndent : 25,
			enableSorting : false,
			useContextMenu : true,

			// 컨텍스트 메뉴 아이템들
			contextMenuItems : [ {
				label : "BOM 비교",
				callback : contextHandler
			}, {
				label : "CONFIG 비교",
				callback : contextHandler
			}, {
				label : "도면일람표 비교",
				callback : contextHandler
			} ],
		};

		myGridID = AUIGrid.create("#grid_wrap", columns, props);
		loadGridData();
	}

	function contextHandler(event) {
		
	}
	
	function loadGridData() {
		let params = new Object();
		let url = getCallUrl("/template/load?oid=<%=data.getOid()%>");
		AUIGrid.showAjaxLoader(myGridID);
		call(url, params, function(data) {
			AUIGrid.removeAjaxLoader(myGridID);
			AUIGrid.setGridData(myGridID, data.list);
		}, "GET");
	}

	$(function() {
		createAUIGrid(columns);
	})
</script>