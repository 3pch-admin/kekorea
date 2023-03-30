<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.epm.workOrder.dto.WorkOrderDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
WorkOrderDTO dto = (WorkOrderDTO) request.getAttribute("dto");
JSONArray list = (JSONArray) request.getAttribute("list");
%>
<%@include file="/extcore/include/auigrid.jsp"%>
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid() %>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				도면일람표 정보
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
			<a href="#tabs-2">도면 일람표</a>
		</li>
	</ul>
	<div id="tabs-1">
		<table class="view-table">
			<colgroup>
				<col style="width: 10%;">
				<col style="width: 40%;">
				<col style="width: 10%;">
				<col style="width: 40%;">
			</colgroup>
			<tr>
				<th class="lb">도면 일람표 제목</th>
				<td class="indent5"><%=dto.getName()%></td>
				<th>상태</th>
				<td class="indent5"><%=dto.getState()%></td>
			</tr>
			<tr>
				<th class="lb">작성자</th>
				<td class="indent5"><%=dto.getCreator()%></td>
				<th>작성일</th>
				<td class="indent5"><%=dto.getCreatedDate_txt()%></td>
			</tr>
			<tr>
				<th class="lb">KEK 작번</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/include/project-include.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
						<jsp:param value="view" name="mode" />
						<jsp:param value="false" name="multi" />
						<jsp:param value="project" name="obj" />
						<jsp:param value="400" name="height" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">내용</th>
				<td class="indent5" colspan="3">
					<textarea rows="7" cols="" readonly="readonly"><%=dto.getContent() != null ? dto.getContent() : ""%></textarea>
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
		</table>
	</div>
	<div id="tabs-2">
		<table class="button-table">
			<tr>
				<td class="left">
					<input type="button" value="표지 다운로드" title="표지 다운로드" onclick="cover();">
					<input type="button" value="PDF 압축파일 다운로드" title="PDF 압축파일 다운로드" class="blue" onclick="zip();">
					<input type="button" value="병합 PDF 다운로드" title="병합 PDF 다운로드" class="orange" onclick="merge();">
				</td>
			</tr>
		</table>
		<div id="grid_wrap" style="height: 800px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID;
			const columns = [ {
				dataField : "preView",
				headerText : "미리보기",
				width : 80,
				renderer : {
					type : "ImageRenderer",
					altField : null,
					imgHeight : 34,
				},
			}, {
				dataField : "name",
				headerText : "DRAWING TITLE",
				dataType : "string",
				style : "aui-left",
			}, {
				dataField : "number",
				headerText : "DWG. NO",
				dataType : "string",
				width : 200,
			}, {
				dataField : "current",
				headerText : "CURRENT VER",
				dataType : "string",
				width : 130,
			}, {
				dataField : "rev",
				headerText : "REV",
				dataType : "string",
				width : 130,
			}, {
				dataField : "latest",
				headerText : "REV (최신)",
				dataType : "string",
				width : 130,
			}, {
				dataField : "lotNo",
				headerText : "LOT",
				dataType : "numeric",
				width : 100,
				formatString : "###0",
			}, {
				dataField : "createdData_txt",
				headerText : "등록일",
				dataType : "string",
				width : 100,
			}, {
				dataField : "note",
				headerText : "NOTE",
				dataType : "string",
				width : 350,
			}, {
				dataField : "primary",
				headerText : "도면파일",
				dataType : "string",
				width : 80,
				renderer : {
					type : "TemplateRenderer",
				},
			} ]

			function createAUIGrid(columnLayout) {
				const props = {
					headerHeight : 30,
					rowHeight : 30,
					showRowNumColumn : true,
					showStateColumn : true,
					selectionMode : "multipleCells",
					rowNumHeaderText : "번호",
					showRowCheckColumn : true,
				};
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				AUIGrid.setGridData(myGridID, <%=list%>);
			}
		</script>
	</div>
</div>
<script type="text/javascript">

	function zip() {
		const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
		if(checkedItems.length === 0) {
			alert("ZIP파일로 다운로드 받을 도면을 하나 이상 선택하세요.");
			return false;
		}
	}
	
	function cover() {
		
	}
	
	function merge() {
		
	}

	document.addEventListener("DOMContentLoaded", function() {
		$("#tabs").tabs({
			active : 0,
			create : function(event, ui) {
				const tabId = ui.panel.prop("id");
				switch (tabId) {
				case "tabs-1":
					_createAUIGrid(_columns);
					AUIGrid.resize(_myGridID);
					break;
				case "tabs-2":
					createAUIGrid(columns);
					AUIGrid.resize(myGridID);
					break;
				}
			},
			activate : function(event, ui) {
				var tabId = ui.newPanel.prop("id");
				switch (tabId) {
				case "tabs-1":
					const _isCreated = AUIGrid.isCreated(_myGridID_);
					if (_isCreated_ && _isCreated) {
						AUIGrid.resize(_myGridID);
					} else {
						_createAUIGrid(_columns);
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
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
		AUIGrid.resize(_myGridID);
	});
</script>