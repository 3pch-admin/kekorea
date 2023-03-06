<%@page import="e3ps.project.IssueProjectLink"%>
<%@page import="e3ps.project.Issue"%>
<%@page import="e3ps.project.Project"%>
<%@page import="e3ps.project.ProjectOutputLink"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.doc.dto.OutputViewData"%>
<%@page import="e3ps.doc.dto.DocumentViewData"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	// popup check
	String popup = (String) request.getParameter("popup");
	boolean isPopup = Boolean.parseBoolean(popup);

	// data
// 	OutputViewData data = (OutputViewData) request.getAttribute("data");
	Issue issue = (Issue) request.getAttribute("isseu");
	ArrayList<IssueProjectLink> projectList = (ArrayList<IssueProjectLink>) request.getAttribute("projectList");
	
	// isAdmin
	boolean isAdmin = CommonUtils.isAdmin();
%>

<td valign="top">
	<script type="text/javascript">
	$(document).ready(function() {
		
		var len = "<%=issue.getDescription().length() %>";
		$("#descDocCnt").text(len);
	})
	</script>
	<input type="hidden" name="oid" id="oid" value="<%=issue.getPersistInfo().getObjectIdentifier().getStringValue()%>">
	<input type="hidden" name="popup" id="popup" value="<%=isPopup %>">

	<table class="btn_table">
		<tr>
			<td>
				<div class="header_title">
					<i class="axi axi-subtitles"></i><span>산출물 정보</span>
				</div>
			</td>
			<td>
				<div class="right">
<!-- 					<input type="button" value="수정" id="modifyIssueBtn" title="수정"> -->
					<% if(isPopup){ %>
					<input type="button" value="닫기" id="closeDocBtn" title="닫기" class="redBtn">
					<%}else{ %>
					<input type="button" value="뒤로" id="b" title="뒤로" class="blueBtn">
					<%} %>
				</div>
			</td>
		</tr>
	</table>
	
	<table class="view_table">
		<tr>
			<th>특이사항 제목</th>
			<td colspan="3"><%=issue.getName() %></td>
		</tr>
		<tr>
			<th>작성자</th>
			<td><%=issue.getOwnership().getOwner().getFullName() %></td>
			<th>작성일</th>
			<td><%=issue.getCreateTimestamp().toString().substring(0, 16) %></td>			
		</tr>	
		<tr>
			<th>설명<br><span id="descDocCnt">0</span>/4000</th>
			<td colspan="3">
				<textarea rows="3" cols="" class="AXTextarea bgk" readonly="readonly"><%=issue.getDescription() %></textarea>
			</td>			
		</tr>	
		<tr>
			<th>연관된 프로젝트</th>
			<td colspan="3">	
			<table class="output_table">
								<tr>
									<td class="nonBorder">
										<div id="outputs_container">
											<table class="create_project_table_in">
												<thead>
													<tr>
														<th>작번유형</th>
														<th>KEK 작번</th>
														<th>KE 작번</th>
														<th>고객사</th>
														<th>막종</th>
														<th>작업내용</th>
													</tr>
												</thead>
												<tbody id="addOutputsBody">
												<%
													for(IssueProjectLink projectLink : projectList) {
														Project project = projectLink.getProject();
														String ooid = project.getPersistInfo().getObjectIdentifier().getStringValue();
												%>
												<tr>
													<td class="center"><%=project.getPType() %></td>
													<td class="center"><input type="hidden" name="projectOid" value="<%=ooid %>"><%=project.getKekNumber() %></td>
													<td class="center"><%=project.getKeNumber() %></td>
													<td class="center"><%=project.getCustomer() %></td>
													<td class="center"><%=project.getMak() %></td>
													<td class="left indent10"><%=project.getDescription() %></td>
												</tr>
												<%
													}
												%>
												</tbody>
												<%
													if(projectList.size() == 0) {
												%>
												<tbody id="addOutputsBody">
													<tr id="nodataOutputs">
														<td class="nodata" colspan="8">지정된 프로젝트가 없습니다.</td>
													</tr>
												</tbody>
												<%
													}
												%>
											</table>
										</div>
									</td>
								</tr>
							</table>
			</td>
		</tr>	
	</table>
</td>