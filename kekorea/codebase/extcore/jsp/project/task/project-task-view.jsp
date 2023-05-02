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
int taskType = (int) request.getAttribute("taskType");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>
<%@include file="/extcore/jsp/common/highchart.jsp"%>
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js?v=1010"></script>
</head>
<body style="margin: 0px 0px 0px 5px;">
	<form>
		<input type="hidden" name="isAdmin" id="isAdmin" value="<%=isAdmin%>">
		<input type="hidden" name="sessionName" id="sessionName" value="<%=sessionUser.getFullName()%>">
		<input type="hidden" name="sessionId" id="sessionId" value="<%=sessionUser.getName()%>">
		<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
		<input type="hidden" name="poid" id="poid" value="<%=data.getOid()%>">
		<div id="tabs">
			<ul>
				<li>
					<a href="#tabs-1">기본정보</a>
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
					<a href="#tabs-7">통합 수배표</a>
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
						<td class="center"><%=data.getMachineProgress()%>%
						</td>
					</tr>
					<tr>
						<td class="center"><%=data.getKeNumber()%></td>
						<td class="center"><%=data.getUserId()%></td>
						<td class="center"><%=data.getProjectType_name()%></td>
						<td class="center"><%=data.getMak_name()%>
							/
							<%=data.getDetail_name()%></td>
						<td class="center" colspan="2"><%=data.getDescription()%></td>
						<th>전기</th>
						<td class="center"><%=data.getElecProgress()%>%
						</td>
					</tr>
					<%
					} else {
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
						<th rowspan="4">진행률</th>
						<td rowspan="4" class="center"><%=data.getKekProgress()%>%
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
					</tr>
					<tr>
						<td class="center"><%=data.getKeNumber()%></td>
						<td class="center"><%=data.getUserId()%></td>
						<td class="center"><%=data.getProjectType_name()%></td>
						<td class="center"><%=data.getMak_name()%>
							/
							<%=data.getDetail_name()%></td>
						<td class="indent5" colspan="2"><%=data.getDescription()%></td>
					</tr>
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

				<%
				if (taskType == 0) {
				%>
				<jsp:include page="/extcore/jsp/project/task/normal.jsp">
					<jsp:param value="<%=data.getOid() %>" name="poid"/>
					<jsp:param value="<%=dto.getOid() %>" name="toid"/>
					<jsp:param value="<%=isAdmin %>" name="isAdmin"/>
				</jsp:include>
				<%
				} else if (taskType == 1) {
				%>
				<jsp:include page="/extcore/jsp/project/task/request.jsp">
					<jsp:param value="<%=data.getOid() %>" name="poid"/>
					<jsp:param value="<%=dto.getOid() %>" name="toid"/>
					<jsp:param value="<%=isAdmin %>" name="isAdmin"/>
				</jsp:include>
				<%
				} else if (taskType == 2) {
				%>


				<%
				} else if (taskType == 3) {
				%>

				<%
				} else if (taskType == 4) {
				%>
				<jsp:include page="/extcore/jsp/project/task/tbom.jsp">
					<jsp:param value="<%=data.getOid() %>" name="poid"/>
					<jsp:param value="<%=dto.getOid() %>" name="toid"/>
					<jsp:param value="<%=isAdmin %>" name="isAdmin"/>
				</jsp:include>
				<%
				} else if (taskType == 5) {
				%>
				<jsp:include page="/extcore/jsp/project/task/meeting.jsp">
					<jsp:param value="<%=data.getOid() %>" name="poid"/>
					<jsp:param value="<%=dto.getOid() %>" name="toid"/>
					<jsp:param value="<%=isAdmin %>" name="isAdmin"/>
				</jsp:include>
				<%
				}
				%>
			</div>

			<div id="tabs-3">
				<iframe style="height: 800px;" src="/Windchill/plm/project/issueTab?oid=<%=data.getOid()%>"></iframe>
			</div>
			<div id="tabs-4">
				<iframe style="height: 800px;" src="/Windchill/plm/project/partlistTab?oid=<%=data.getOid()%>&invoke=m"></iframe>
			</div>
			<div id="tabs-5">
				<iframe style="height: 800px;" src="/Windchill/plm/project/partlistTab?oid=<%=data.getOid()%>&invoke=e"></iframe>
			</div>
			<div id="tabs-6">
				<iframe style="height: 800px;" src="/Windchill/plm/project/tbomTab?oid=<%=data.getOid()%>"></iframe>
			</div>
			<div id="tabs-7">
				<iframe style="height: 800px;" src="/Windchill/plm/project/partlistTab?oid=<%=data.getOid()%>&invoke=a"></iframe>
			</div>
			<div id="tabs-8">
				<iframe style="height: 800px;" src="/Windchill/plm/project/cipTab?oid=<%=data.getOid()%>&invoke=a"></iframe>
			</div>
			<div id="tabs-9">
				<iframe style="height: 800px;" src="/Windchill/plm/project/workOrderTab?oid=<%=data.getOid()%>&invoke=a"></iframe>
			</div>
		</div>

		<script type="text/javascript">
			document.addEventListener("DOMContentLoaded", function() {
				$("#tabs").tabs();
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);
				parent.parent.closeLayer();
			})

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID);
			});
		</script>
	</form>
</body>
</html>