<%@page import="e3ps.korea.configSheet.beans.ConfigSheetDTO"%>
<%@page import="net.sf.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
JSONArray data = (JSONArray) request.getAttribute("data");
ConfigSheetDTO dto = (ConfigSheetDTO) request.getAttribute("dto");
String oid = (String) request.getAttribute("oid");
%>
<%@include file="/extcore/include/auigrid.jsp"%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				CONFIG SHEET 정보
			</div>
		</td>
		<td class="right">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>
<div id="tabs">
	<ul>
		<li>
			<a href="#tabs-1">기본정보</a>
		</li>
		<li>
			<a href="#tabs-2">CONFIG SHEET</a>
		</li>
		<li>
			<a href="#tabs-3">결재이력</a>
		</li>
	</ul>
	<div id="tabs-1">
		<table class="view-table">
			<colgroup>
				<col width="150">
				<col width="*">
			</colgroup>
			<tr>
				<th class="lb">CONFIG SHEET 제목</th>
				<td class="indent5"><%=dto.getName()%></td>
			</tr>
			<tr>
				<th class="lb">KEK 작번</th>
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
				<th class="lb">설명</th>
				<td class="indent5">
					<textarea name="description" id="description" rows="6" readonly="readonly"><%=dto.getContent()%></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">첨부파일</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/include/attachment-view.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
						<jsp:param value="secondary" name="mode" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="req lb">결재</th>
				<td>
					<jsp:include page="/extcore/include/register-include.jsp">
						<jsp:param value="200" name="height" />
					</jsp:include>
				</td>
			</tr>
		</table>
	</div>
	<div id="tabs-2">
		<div id="grid_wrap" style="height: 800px; border-top: 1px solid #3180c3;"></div>
	</div>
	<div id="tabs-3">
	
	</div>
</div>
<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "category_name",
		headerText : "CATEGORY",
		dataType : "string",
		width : 250,
		cellMerge : true,
	}, {
		dataField : "item_name",
		headerText : "ITEM",
		dataType : "string",
		width : 200,
		cellMerge : true,
		mergeRef : "category_code",
		mergePolicy : "restrict",
	}, {
		dataField : "spec_name",
		headerText : "사양",
		dataType : "string",
		width : 250,
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
			headerHeight : 30,
			rowHeight : 30,
			showRowNumColumn : true,
			showStateColumn : true,
			rowNumHeaderText : "번호",
			selectionMode : "multipleCells",
			enableCellMerge : true,
		};
		myGridID = AUIGrid.create("#grid_wrap", columns, props);
		readyHandler();
	}

	function readyHandler() {
		const data =
<%=data%>
	AUIGrid.setGridData(myGridID, data);
	}

	document.addEventListener("DOMContentLoaded", function() {
		$("#tabs").tabs({
			active : 0,
			create : function(event, ui) {
				const tabId = ui.panel.prop("id");
				switch (tabId) {
				case "tabs-1":
					_createAUIGrid(_columns);
					_createAUIGrid_(_columns_);
					break;
				case "tabs-2":
					AUIGrid.resize(myGridID);
					break;
				}
			},
			activate : function(event, ui) {
				var tabId = ui.newPanel.prop("id");
				switch (tabId) {
				case "tabs-1":
					const _isCreated_ = AUIGrid.isCreated(_myGridID);
					const _isCreated = AUIGrid.isCreated(_myGridID_);
					if (_isCreated_ && _isCreated) {
						AUIGrid.resize(_myGridID);
						AUIGrid.resize(_myGridID_);
					} else {
						_createAUIGrid(_columns);
						_createAUIGrid_(_columns_);
					}
					break;
				case "tabs-2":
					const isCreated = AUIGrid.isCreated(myGridID);
					if (isCreated) {
						AUIGrid.resize(myGridID);
					} else {
						createAUIGrid(columns);
					}
					break;
				}
			}
		});
	});
</script>