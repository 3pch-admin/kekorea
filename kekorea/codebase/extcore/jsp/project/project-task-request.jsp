<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.project.task.dto.TaskDTO"%>
<%@page import="wt.org.WTUser"%>
<%@page import="e3ps.project.dto.ProjectDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ProjectDTO data = (ProjectDTO) request.getAttribute("data");
TaskDTO dto = (TaskDTO) request.getAttribute("dto");
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
JSONArray list = (JSONArray) request.getAttribute("list");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/include/css.jsp"%>
<%@include file="/extcore/include/script.jsp"%>
<%@include file="/extcore/include/auigrid.jsp"%>
<%@include file="/extcore/include/highchart.jsp"%>
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js?v=1010"></script>
</head>
<body style="margin: 0px 0px 0px 5px;">
	<form>
		<input type="hidden" name="isAdmin" id="isAdmin" value="<%=isAdmin%>">
		<input type="hidden" name="sessionName" id="sessionName" value="<%=sessionUser.getFullName()%>">
		<input type="hidden" name="sessionId" id="sessionId" value="<%=sessionUser.getName()%>">
		<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
		<div id="tabs">
			<ul>
				<li>
					<a href="#tabs-1">기본정보</a>
				</li>
				<li>
					<a href="#tabs-2">참조작번</a>
				</li>
				<li>
					<a href="#tabs-3">특이사항</a>
				</li>
				<li>
					<a href="#tabs-4">기계 수배표</a>
				</li>
				<li>
					<a href="#tabs-5">전기 수배표</a>
				</li>
				<li>
					<a href="#tabs-6">T-BOM</a>
				</li>
				<li>
					<a href="#tabs-7">수배표 통합</a>
				</li>
				<li>
					<a href="#tabs-8">CIP</a>
				</li>
				<li>
					<a href="#tabs-9">도면 일람표</a>
				</li>
			</ul>
			<div id="tabs-1">
				<table class="view-table">
					<%
					if (!data.isEstimate()) {
					%>
					<colgroup>
						<col width="140">
						<col width="140">
						<col width="140">
						<col width="140">
						<col width="140">
						<col width="140">
						<col width="30">
						<col width="140">
						<col width="140">
					</colgroup>
					<tr>
						<th class="lb rb">KEK 작번</th>
						<th class="rb">거래처</th>
						<th class="rb">설치장소</th>
						<th class="rb">모델</th>
						<th class="rb">발행일</th>
						<th class="rb">요구 납기일</th>
						<td rowspan="4" class="tb-none bb-none" style="width: 30px;">&nbsp;</td>
						<th rowspan="2">진행률</th>
						<td rowspan="2" class="center"><%=data.getKekProgress()%>%
						</td>
					</tr>
					<tr>
						<td class="center"><%=data.getKekNumber()%></td>
						<td class="center"><%=data.getCustomer_name()%></td>
						<td class="center"><%=data.getInstall_name()%></td>
						<td class="center"><%=data.getModel()%></td>
						<td class="center"><%=data.getPdate_txt()%></td>
						<td class="center"><%=data.getCustomDate_txt()%></td>
					</tr>
					<tr>
						<th class="lb rb">KE 작번</th>
						<th class="rb">USER ID</th>
						<th class="rb">작번 유형</th>
						<th class="rb">막종 / 막종상세</th>
						<th class="rb" colspan="2">작업 내용</th>
						<th>기계</th>
						<td class="center">%</td>
					</tr>
					<tr>
						<td class="center"><%=data.getKeNumber()%></td>
						<td class="center"><%=data.getUserId()%></td>
						<td class="center"><%=data.getProjectType_name()%></td>
						<td class="center"><%=data.getMak_name()%>
							/
							<%=data.getDetail_name()%></td>
						<td class="indent5" colspan="2"><%=dto.getDescription()%></td>
						<th>전기</th>
						<td class="center">%</td>
					</tr>
					<%
					} else {
					%>

					<%
					}
					%>

				</table>


				<div class="info-header">
					<img src="/Windchill/extcore/images/header.png">
					태스크 상세 정보
				</div>

				<table class="view-table">
					<tr>
						<th class="lb rb">태스크 명</th>
						<th class="rb">태스크 타입</th>
						<th class="rb">할당율</th>
						<th class="rb">총기간[공수](일)</th>
						<th class="rb">태스크 상태</th>
					</tr>
					<tr>
						<td class="center"><%=dto.getName()%></td>
						<td class="center"><%=dto.getTaskType()%></td>
						<td class="center"><%=dto.getAllocate()%>%
						</td>
						<td class="center">
							<%
							//=dto.getName()
							%>
						</td>
						<td class="center"><%=dto.getState()%></td>
					</tr>
					<tr>
						<th class="lb rb">계획 시작일</th>
						<th class="rb">계획 종요일</th>
						<th class="rb">실제 시작일</th>
						<th class="rb">실제 종료일</th>
						<th class="rb">진행율</th>
					</tr>
					<tr>
						<td class="center"><%=dto.getPlanEndDate_txt()%></td>
						<td class="center"><%=dto.getPlanStartDate_txt()%></td>
						<td class="center"><%=dto.getStartDate_txt()%></td>
						<td class="center"><%=dto.getEndDate_txt()%></td>
						<td class="center"><%=dto.getProgress()%>%
						</td>
					</tr>
				</table>

				<div class="info-header">
					<img src="/Windchill/extcore/images/header.png">
					의뢰서 산출물 정보
				</div>

				<table class="button-table">
					<tr>
						<td class="left">
							<input type="button" value="의뢰서 등록" title="의뢰서 등록" class="blue" onclick="addBeforeRow();">
							<input type="button" value="링크 등록" title="링크 등록" class="orange" onclick="addAfterRow();">
							<input type="button" value="삭제" title="삭제" class="red" onclick="deleteRow();">
						</td>
					</tr>
				</table>
				<div id="grid_wrap" style="height: 440px; border-top: 1px solid #3180c3;"></div>
				<script type="text/javascript">
					let myGridID;
					const columns = [ {
						dataField : "name",
						headerText : "의뢰서 제목",
						dataType : "string",
						style : "aui-left",
					}, {
						dataField : "version",
						headerText : "버전",
						dataType : "string",
						width : 100,
					}, {
						dataField : "state",
						headerText : "상태",
						dataType : "string",
						width : 100,
					}, {
						dataField : "creator",
						headerText : "작성자",
						dataType : "string",
						width : 100,
					}, {
						dataField : "createdDate_txt",
						headerText : "작성일",
						dataType : "string",
						width : 100,
					}, {
						dataField : "primary",
						headerText : "첨부파일",
						dataType : "string",
						width : 100,
						renderer : {
							type : "TemplateRenderer"
						}
					} ]

					function createAUIGrid(columnLayout) {
						const props = {
							headerHeight : 30,
							showRowCheckColumn : true,
							showRowNumColumn : true,
							showStateColumn : true,
							rowNumHeaderText : "번호",
							showAutoNoDataMessage : false,
							selectionMode : "singleRow",
						};
						myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
						AUIGrid.setGridData(myGridID,
				<%=list%>
					);
					}
				</script>
			</div>
		</div>

		<script type="text/javascript">
			document.addEventListener("DOMContentLoaded", function() {
				$("#tabs").tabs({
					heightStyle : "content",
					active : 0,
					create : function(event, ui) {
						const tabId = ui.panel.prop("id");
						switch (tabId) {
						case "tabs-1":
							createAUIGrid(columns);
							AUIGrid.resize(myGridID);
							break;
						}
					},
					activate : function(event, ui) {
						var tabId = ui.newPanel.prop("id");
						switch (tabId) {
						case "tabs-1":
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
			});
		</script>
	</form>
</body>
</html>