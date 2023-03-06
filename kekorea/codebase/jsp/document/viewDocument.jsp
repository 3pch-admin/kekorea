<%@page import="e3ps.project.Project"%>
<%@page import="e3ps.project.ProjectOutputLink"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.doc.dto.DocumentViewData"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	// popup check
	String popup = (String) request.getParameter("popup");
	boolean isPopup = Boolean.parseBoolean(popup);

	// data
	DocumentViewData data = (DocumentViewData) request.getAttribute("data");
	ArrayList<ProjectOutputLink> projectList = (ArrayList<ProjectOutputLink>) request.getAttribute("projectList");
	// isAdmin
	boolean isAdmin = CommonUtils.isAdmin();
	
%>

<td valign="top">
	<script type="text/javascript">
	$(document).ready(function() {
		var len = "<%=data.description.length() %>";
		$("#descDocCnt").text(len);
	})
	</script>
	<input type="hidden" name="oid" id="oid" value="<%=data.oid %>">
	<input type="hidden" name="popup" id="popup" value="<%=isPopup %>">

	<table class="btn_table">
		<tr>
			<td>
				<div class="header_title">
					<i class="axi axi-subtitles"></i><span>문서 정보</span>
				</div>
			</td>
			<td>
				<div class="right">
					<%
// 						if(data.isRevise) {
					%>
					<input type="button" value="개정" id="reviseBtn" title="개정" class="blueBtn">
					<%
// 						}
// 						if(data.isModify) {
					%>
					<input type="button" value="수정" id="modifyDocBtn" title="수정">
					<%
// 						}
						if(isAdmin) {
					%>
					<input type="button" value="삭제" id="deleteDocBtn" title="삭제" class="redBtn">
					<%
						}
					%>		
					<input type="button" value="결재이력" data-oid="<%=data.oid%>" class="infoApprovalHistory" id="infoApprovalHistory" title="결재이력">
					<input type="button" value="버전정보" id="infoVersionBtn" title="버전정보">
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
			<th class="wid200">문서제목</th>
			<td><%=data.name %></td>
			<th class="wid200">문서번호</th>
			<td><%=data.number %></td>			
		</tr>
		<tr>
			<th>버전</th>
			<td><%=data.fullVersion %></td>
			<th>상태</th>
			<td><%=data.state %></td>			
		</tr>		
		
		<tr>
			<th>작성자</th>
			<td><%=data.creator %></td>
			<th>작성일</th>
			<td><%=data.createDate %></td>			
		</tr>			
		<tr>
			<th>수정자</th>
			<td><%=data.modifier %></td>
			<th>수정일</th>
			<td><%=data.modifyDate %></td>			
		</tr>	
		<tr>
			<th>저장위치</th>
			<td colspan="3"><%=data.location %></td>
		</tr>			
		<tr>
			<th>설명<br><span id="descDocCnt">0</span>/4000</th>
			<td colspan="3">
				<textarea rows="3" cols="" class="AXTextarea bgk" readonly="readonly"><%=data.description %></textarea>
			</td>			
		</tr>		
		<tr>
			<th>관련부품</th>
			<td colspan="3">
				<jsp:include page="/jsp/document/refPart.jsp">
					<jsp:param value="<%=data.oid %>" name="oid"/>
				</jsp:include>
			</td>
		</tr>						
		<tr>
			<th>주 첨부파일</th>
			<td colspan="3">
				<jsp:include page="/jsp/common/primary.jsp">
					<jsp:param value="<%=data.oid %>" name="oid"/>
				</jsp:include>
			</td>
		</tr>		
		<tr>
			<th>첨부파일</th>
			<td colspan="3">
				<jsp:include page="/jsp/common/secondary.jsp">
					<jsp:param value="<%=data.oid %>" name="oid"/>
				</jsp:include>
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
 									for(ProjectOutputLink projectLink : projectList) { 
 										Project project = projectLink.getProject(); 
 										String ooid = project.getPersistInfo().getObjectIdentifier().getStringValue(); 
								%> 
								<tr>
									<td class="center"><%=project.getPType() %></td>
									<td class="center viewProject" data-oid="<%=ooid%>"><%=project.getKekNumber() %></td>
									<td class="center viewProject" data-oid="<%=ooid%>"><%=project.getKeNumber() %></td>
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