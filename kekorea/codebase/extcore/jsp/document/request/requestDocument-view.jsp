<%@page import="e3ps.doc.request.dto.RequestDocumentDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="wt.org.WTUser"%>
<%@page import="e3ps.doc.dto.DocumentDTO"%>
<%-- <%@page import="e3ps.project.dto.ProjectDTO"%> --%>
<%@page import="e3ps.common.util.ContentUtils"%>
<%@include file="/extcore/include/auigrid.jsp"%>
<%@page import="net.sf.json.JSONArray"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
RequestDocumentDTO dto = (RequestDocumentDTO) request.getAttribute("dto");
JSONArray history = (JSONArray) request.getAttribute("history");
JSONArray data = (JSONArray) request.getAttribute("data");
%>
<input type="hidden" name="isAdmin" id="isAdmin" value="<%=isAdmin%>">
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				의뢰서 정보
			</div>
		</td>
		<td class="right">
			<input type="button" value="닫기" id="close" title="닫기" class="red" onclick="self.close();">
		</td>
	</tr>
</table>
<div id="tabs">
	<ul>
		<li>
			<a href="#tabs-1">기본정보</a>
		</li>
		<li>
			<a href="#tabs-2">결재이력</a>
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
				<th class="lb">의뢰서 제목</th>
				<td class="indent5" colspan="3"><%=dto.getName()%></td>
			</tr>
			<tr>
				<th class="lb">작성자</th>
				<td class="indent5"><%=dto.getCreator()%></td>
				<th class="lb">작성일</th>
				<td class="indent5"><%=dto.getCreatedDate_txt()%></td>
			</tr>
			<tr>
				<th class="lb">설명</th>
				<td class="indent5" colspan="3">
					<textarea id="descriptionNotice" rows="5" readonly="readonly"><%=dto.getDescription() != null ? dto.getDescription() : ""%></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">KEK 작번</th>
				<td colspan="3">
					<div class="include">
						<div id="_grid_wrap" style="height: 200px; border-top: 1px solid #3180c3; margin: 5px;"></div>
						<script type="text/javascript">
							let _myGridID;
							const data =
						<%=data%>
							const _columns = [ {
								dataField : "projectType_name",
								headerText : "작번유형",
								dataType : "string",
								width : 80,
							}, {
								dataField : "customer_name",
								headerText : "거래처",
								dataType : "string",
								width : 120,
							}, {
								dataField : "mak_name",
								headerText : "막종",
								dataType : "string",
								width : 120,
							}, {
								dataField : "detail_name",
								headerText : "막종상세",
								dataType : "string",
								width : 120,
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
										alert(oid);
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
										alert(oid);
									}
								},
							}, {
								dataField : "userId",
								headerText : "USER ID",
								dataType : "string",
								width : 100,
							}, {
								dataField : "customDate_txt",
								headerText : "요구납기일",
								dataType : "string",
								width : 100,
							}, {
								dataField : "description",
								headerText : "작업 내용",
								dataType : "string",
								style : "aui-left",
							}, {
								dataField : "model",
								headerText : "모델",
								dataType : "string",
								width : 120,
							}, {
								dataField : "pdate_txt",
								headerText : "발행일",
								dataType : "string",
								width : 100,
							}, ]
							function _createAUIGrid(columnLayout) {
								const props = {
									headerHeight : 30,
									showRowNumColumn : true,
									rowNumHeaderText : "번호",
									selectionMode : "singleRow",
									showAutoNoDataMessage : false,
								}
								_myGridID = AUIGrid.create("#_grid_wrap", columnLayout, props);
								AUIGrid.setGridData(_myGridID, data);
							}
						</script>
					</div>
				</td>
			</tr>
			<tr>
				<th class="lb">주 첨부파일</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/include/attachment-view.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
						<jsp:param value="primary" name="mode" />
					</jsp:include>
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
					const _isCreated_ = AUIGrid.isCreated(_myGridID_);
					if (_isCreated_) {
						AUIGrid.resize(_myGridID_);
					} else {
						_createAUIGrid_(_columns_);
					}
					break;
				}
			},
		});
		_createAUIGrid(_columns);
		_createAUIGrid_(_columns_);
		AUIGrid.resize(_myGridID);
		AUIGrid.resize(_myGridID_);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(_myGridID);
		AUIGrid.resize(_myGridID_);
	});
</script>