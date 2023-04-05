<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.bom.tbom.dto.TBOMDTO"%>
<%@page import="e3ps.common.util.ContentUtils"%>
<%@page import="e3ps.epm.keDrawing.dto.KeDrawingDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
TBOMDTO dto = (TBOMDTO) request.getAttribute("dto");
JSONArray list = (JSONArray) request.getAttribute("list");
JSONArray history = (JSONArray) request.getAttribute("history");
%>
<%@include file="/extcore/include/auigrid.jsp"%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				T-BOM 정보
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
			<a href="#tabs-2">T-BOM</a>
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
				<th class="lb">수배표 제목</th>
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
				<td colspan="3">
					<div class="include">
						<div id="_grid_wrap" style="height: 200px; border-top: 1px solid #3180c3; margin: 5px;"></div>
						<script type="text/javascript">
							let _myGridID;
							const data =
						<%=list%>
							const _columns = [ {
								dataField : "projectType_name",
								headerText : "작번유형",
								dataType : "string",
								width : 80,
								filter : {
									showIcon : true,
									inline : true
								},
							}, {
								dataField : "customer_name",
								headerText : "거래처",
								dataType : "string",
								width : 120,
								filter : {
									showIcon : true,
									inline : true
								},
							}, {
								dataField : "mak_name",
								headerText : "막종",
								dataType : "string",
								width : 120,
								filter : {
									showIcon : true,
									inline : true
								},
							}, {
								dataField : "detail_name",
								headerText : "막종상세",
								dataType : "string",
								width : 120,
								filter : {
									showIcon : true,
									inline : true
								},
							}, {
								dataField : "kekNumber",
								headerText : "KEK 작번",
								dataType : "string",
								width : 130,
								renderer : {
									type : "LinkRenderer",
									baseUrl : "javascript",
									jsCallback : function(rowIndex, columnIndex, value, item) {
										const oid = item.oid;
										alert(oid);
									}
								},
								filter : {
									showIcon : true,
									inline : true
								},
							}, {
								dataField : "keNumber",
								headerText : "KE 작번",
								dataType : "string",
								width : 130,
								renderer : {
									type : "LinkRenderer",
									baseUrl : "javascript",
									jsCallback : function(rowIndex, columnIndex, value, item) {
										const oid = item.oid;
										alert(oid);
									}
								},
								filter : {
									showIcon : true,
									inline : true
								},
							}, {
								dataField : "description",
								headerText : "작업 내용",
								dataType : "string",
								style : "aui-left",
								filter : {
									showIcon : true,
									inline : true
								},
							} ]
							function _createAUIGrid(columnLayout) {
								const props = {
									headerHeight : 30,
									rowHeight : 30,
									showRowNumColumn : true,
									rowNumHeaderText : "번호",
									selectionMode : "singleRow",
								}
								_myGridID = AUIGrid.create("#_grid_wrap", columnLayout, props);
								AUIGrid.setGridData(_myGridID, data);
							}
						</script>
					</div>
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
	<div id="tabs-2"></div>
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
	$("#tabs").tabs({
		active : 0,
		create : function(event, ui) {
			const tabId = ui.panel.prop("id");
			switch (tabId) {
			case "tabs-1":
				_createAUIGrid(_columns);
				AUIGrid.resize(_myGridID);
				break;
			case "tabs-3":
				_createAUIGrid_(_columns_);
				AUIGrid.resize(_myGridID_);
				break;
			}
		},
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
			case "tabs-3":
				const _isCreated_ = AUIGrid.isCreated(_myGridID_);
				if (_isCreated_) {
					AUIGrid.resize(_myGridID_);
				} else {
					_createAUIGrid_(_columns_);
				}
				break;
			}
		},
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(_myGridID);
		AUIGrid.resize(_myGridID_);
	});
</script>