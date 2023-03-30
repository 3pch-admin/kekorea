<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="wt.org.WTUser"%>
<%@page import="e3ps.doc.dto.DocumentDTO"%>
<%@page import="e3ps.project.dto.ProjectDTO"%>
<%@page import="e3ps.common.util.ContentUtils"%>
<%@include file="/extcore/include/auigrid.jsp"%>
<%@page import="net.sf.json.JSONArray"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
DocumentDTO dto = (DocumentDTO) request.getAttribute("dto");
ProjectDTO pdto = (ProjectDTO) request.getAttribute("pdto");
JSONArray list = (JSONArray) request.getAttribute("list");
String oid = request.getParameter("oid");
%>
<input type="hidden" name="isAdmin" id="isAdmin" value="<%=isAdmin%>">
<input type="hidden" name="oid" id="oid">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				문서 정보
			</div>
		</td>
		<td class="right">
			<input type="button" value="개정" id="reviseBtn" title="개정" class="blue">
			<input type="button" value="수정" id="modifyDocBtn" title="수정">
			<%
			if (isAdmin) {
			%>
			<input type="button" value="삭제" id="deleteDocBtn" title="삭제" class="red">
			<%
			}
			%>
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
		<li>
			<a href="#tabs-3">버전정보</a>
		</li>
	</ul>
	<div id="tabs-1">
		<table class="view-table">
			<colgroup>
				<col width="150">
				<col width="700">
				<col width="100">
				<col width="300">
			</colgroup>
			<tr>
				<th class="lb">문서제목</th>
				<td class="indent5"><%=dto.getName()%></td>
				<th class="lb">문서번호</th>
				<td class="indent5"><%=dto.getNumber()%></td>
			</tr>
			<tr>
				<th class="lb">버전</th>
				<td class="indent5"><%=dto.getVersion()%></td>
				<th class="lb">상태</th>
				<td class="indent5"><%=dto.getState()%></td>
			</tr>
			<tr>
				<th class="lb">작성자</th>
				<td class="indent5"><%=dto.getCreator()%></td>
				<th class="lb">작성일</th>
<%-- 				<td class="indent5"><%=dto.getCreatedDate().toString().substring(0, 10)%></td> --%>
								<td class="indent5"><%=dto.getCreatedDate_txt()%></td>
			</tr>
			<tr>
				<th class="lb">수정자</th>
				<td class="indent5"><%=dto.getModifier()%></td>
				<th class="lb">수정일</th>
<%-- 				<td class="indent5"><%=dto.getModifiedDate().toString().substring(0, 10)%></td> --%>
				<td class="indent5"><%=dto.getModifiedDate_txt()%></td>
			</tr>
			<tr>
				<th class="lb">저장위치</th>
				<td class="indent5" colspan="3"><%=dto.getLocation()%></td>
			</tr>
			<tr>
				<th class="lb">설명</th>
				<td colspan="3" class="indent5">
					<textarea name="descriptionNotice" id="descriptionNotice" rows="12" cols="" readonly="readonly"><%=dto.getDescription()%></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">관련부품</th>
				<td class="indent5" colspan="3">
					<%-- 			<jsp:include page="/extcore/include/part-include.jsp"> --%>
					<%-- 				<jsp:param value="<%=dto.getOid()%>" name="oid" /> --%>
					<%-- 				<jsp:param value="view" name="mode" /> --%>
					<%-- 				<jsp:param value="true" name="multi" /> --%>
					<%-- 				<jsp:param value="part" name="obj" /> --%>
					<%-- 				<jsp:param value="150" name="height" /> --%>
					<%-- 			</jsp:include> --%>
					<%-- <%=dto.getOid() %> --%>
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
			<tr>
				<th class="lb">연관된 프로젝트</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/include/project-include.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
						<jsp:param value="view" name="mode" />
						<jsp:param value="true" name="multi" />
						<jsp:param value="document" name="obj" />
						<jsp:param value="180" name="height" />
					</jsp:include>
				</td>
			</tr>
		</table>
	</div>
	<div id="tabs-2">
		<table class="view-table">
		</table>
	</div>
	<div id="tabs-3">
		<%-- 					<td class="indent5"><%=dto.getVersion()%></td> --%>
		<div id="grid_wrap" style="height: 550px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID;
			const columns = [ {
				dataField : "name",
				headerText : "이름",
				dataType : "string",
				width : 600,
				style : "aui-left",
				filter : {
					showIcon : true,
					inline : true
				},
			},{
				dataField : "version",
				headerText : "버전",
				dataType : "string",
				width : 100,
				filter : {
					showIcon : true,
					inline : true
				},
			},{
				dataField : "creator",
				headerText : "작성자",
				dataType : "string",
				width : 130,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "createdDate_txt",
				headerText : "작성일",
				dataType : "date",
				width : 130,
				formatString : "yyyy-mm-dd",
				filter : {
					showIcon : true,
					inline : true,
					displayFormatValues : true
				},
			}, {
				dataField : "modifier",
				headerText : "수정자",
				dataType : "string",
				width : 130,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "modifiedDate_txt",
				headerText : "수정일",
				dataType : "date",
				width : 130,
				formatString : "yyyy-mm-dd",
				filter : {
					showIcon : true,
					inline : true,
					displayFormatValues : true
				}
			} ]
			
			function createAUIGrid(columnLayout) {
				const props = {
						headerHeight : 30,
						rowHeight : 30,
						showRowNumColumn : true,
						showStateColumn : true,
						rowNumHeaderText : "번호",
						noDataMessage : "검색 결과가 없습니다.",
						enableFilter : true,
						selectionMode : "multipleCells",
						enableMovingColumn : true,
						showInlineFilter : true,
						enableRightDownFocus : true,
						filterLayerWidth : 320,
						filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
				}
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				AUIGrid.setGridData(myGridID,<%=list%>);
			}
		</script>
	</div>
</div>
<script type="text/javascript">
	document.addEventListener("DOMContentLoaded", function() {
		$("#tabs").tabs({
			active : 0,
			create : function(event, ui) {
				const tabId = ui.panel.prop("id");
				switch (tabId) {
				case "tabs-3":
					createAUIGrid(columns);
					AUIGrid.resize(myGridID);
					break;
				}
			},
			activate : function(event, ui) {
				var tabId = ui.newPanel.prop("id");
				switch (tabId) {
				case "tabs-3":
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
	
	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
	});
</script>