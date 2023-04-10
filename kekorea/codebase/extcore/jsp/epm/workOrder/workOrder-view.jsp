<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.epm.workOrder.dto.WorkOrderDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
WorkOrderDTO dto = (WorkOrderDTO) request.getAttribute("dto");
JSONArray list = (JSONArray) request.getAttribute("list");
JSONArray history = (JSONArray) request.getAttribute("history");
%>
<%@include file="/extcore/include/auigrid.jsp"%>
<style type="text/css">
.compare {
	background-color: yellow;
	color: red;
	font-weight: bold;
}
</style>
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
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
		<li>
			<a href="#tabs-3">결재이력</a>
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
					<textarea rows="7" cols="" readonly="readonly"><%=dto.getDescription() != null ? dto.getDescription() : ""%></textarea>
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
				renderer : {
					type : "LinkRenderer",
					baseUrl : "javascript",
					jsCallback : function(rowIndex, columnIndex, value, item) {
						const oid = item.oid;
						const doid = item.doid;
						const number = item.number;
						const rev = item.rev;
						let url;
						if (doid.indexOf("KeDrawing") > -1) {
							url = getCallUrl("/keDrawing/viewByNumberAndRev?number=" + number + "&rev=" + rev);
						} else {
							url = getCallUrl("/project/info?oid=" + oid);
						}
						popup(url);
					}
				},
			}, {
				dataField : "current",
				headerText : "CURRENT VER",
				dataType : "string",
				width : 130,
				styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
					const rev = item.rev;
					if (value !== rev) {
						return "compare";
					}
					return "";
				},
				renderer : {
					type : "LinkRenderer",
					baseUrl : "javascript",
					jsCallback : function(rowIndex, columnIndex, value, item) {
						const oid = item.oid;
						const doid = item.doid;
						const number = item.number;
						const current = item.current;
						let url;
						if (doid.indexOf("KeDrawing") > -1) {
							url = getCallUrl("/keDrawing/viewByNumberAndRev?number=" + number + "&rev=" + current);
						} else {
							url = getCallUrl("/project/info?oid=" + oid);
						}
						popup(url);
					}
				},
			}, {
				dataField : "rev",
				headerText : "REV",
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
				AUIGrid.setGridData(myGridID,
		<%=list%>
			);
			}
		</script>
	</div>
	<div id="tabs-3">
		<div id="_grid_wrap_" style="height: 550px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let _myGridID_;
			const history =
		<%=history%>
			const _columns_ = [ {
				dataField : "type",
				headerText : "타입",
				dataType : "string",
				width : 80
			}, {
				dataField : "role",
				headerText : "역할",
				dataType : "string",
				width : 80
			}, {
				dataField : "name",
				headerText : "제목",
				dataType : "string",
				style : "aui-left"
			}, {
				dataField : "state",
				headerText : "상태",
				dataType : "string",
				width : 100
			}, {
				dataField : "owner",
				headerText : "담당자",
				dataType : "string",
				width : 100
			}, {
				dataField : "receiveDate_txt",
				headerText : "수신일",
				dataType : "string",
				width : 130
			}, {
				dataField : "completeDate_txt",
				headerText : "완료일",
				dataType : "string",
				width : 130
			}, {
				dataField : "description",
				headerText : "결재의견",
				dataType : "string",
				style : "aui-left",
				width : 450,
			}, ]
			function _createAUIGrid_(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : true,
					rowNumHeaderText : "번호",
					selectionMode : "singleRow",
					noDataMessage : "결재이력이 없습니다.",
					enableSorting : false
				}
				_myGridID_ = AUIGrid.create("#_grid_wrap_", columnLayout, props);
				AUIGrid.setGridData(_myGridID_, history);
			}
		</script>
	</div>
</div>
<script type="text/javascript">
	function zip() {
		const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
		if (checkedItems.length === 0) {
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
			activate : function(event, ui) {
				var tabId = ui.newPanel.prop("id");
				switch (tabId) {
				case "tabs-1":
					const _isCreated = AUIGrid.isCreated(_myGridID);
					if (_isCreated) {
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
				case "tabs-3":
					const _isCreated_ = AUIGrid.isCreated(_myGridID_);
					if (_isCreated_) {
						AUIGrid.resize(_myGridID_);
					} else {
						_createAUIGrid_(_columns_);
					}
					break;
				}
			}
		});
		createAUIGrid(columns);
		_createAUIGrid(_columns);
		_createAUIGrid_(_columns_);
		AUIGrid.resize(myGridID);
		AUIGrid.resize(_myGridID);
		AUIGrid.resize(_myGridID_);
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
		AUIGrid.resize(_myGridID);
		AUIGrid.resize(_myGridID_);
	});
</script>