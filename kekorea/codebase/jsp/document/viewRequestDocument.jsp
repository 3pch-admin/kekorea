<%@page import="e3ps.project.Project"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.doc.ReqDocumentProjectLink"%>
<%@page import="e3ps.doc.dto.RequestDocumentViewData"%>
<%@page import="e3ps.doc.RequestDocument"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
// popup check
	String popup = (String) request.getParameter("popup");
	boolean isPopup = Boolean.parseBoolean(popup);

	// data
	RequestDocumentViewData data = (RequestDocumentViewData) request.getAttribute("data");
	ArrayList<RequestDocumentProjectLink> projectList = (ArrayList<RequestDocumentProjectLink>) request.getAttribute("projectList");
	// isAdmin
	boolean isAdmin = CommonUtils.isAdmin();
%>

<td valign="top">
	<script type="text/javascript">
	$(document).ready(function() {
	})
	</script>
	<input type="hidden" name="oid" id="oid" value="<%=data.oid%>">
	<input type="hidden" name="popup" id="popup" value="<%=isPopup%>">
	<table class="btn_table">
		<tr>
			<td>
				<div class="header_title">
					<i class="axi axi-subtitles"></i><span>의뢰서 정보</span>
				</div>
			</td>
	<%-- <%
		if(isPopup) {
	%> --%>
			<td>
				<div class="right">
				<%
				if(CommonUtils.isLatestVersion(data.reqDoc)) {
				%>
				<input type="button" value="수정" id="modifyReqBtn" title="수정" data-oid="<%=data.oid%>">
				<%
				}
						if(isPopup) {
				%>
				<input type="button" value="결재이력" data-oid="<%=data.oid%>" class="infoApprovalHistory" id="infoApprovalHistory" title="결재이력">
					<!-- <div class="right"> -->
						<input type="button" value="닫기" id="closeDocBtn" title="닫기" class="redBtn">
					<!-- </div> -->
				<%
				}else{
				%>
					<input type="button" value="뒤로" id="b" title="뒤로" class="blueBtn">		
				<%
						}
						%>
<!-- 		<input type="button" value="닫기" id="closeDocBtn" title="닫기" class="redBtn"> -->
				</div>
			</td>
		</tr>
	</table>
	<%-- <%
		}
	%> --%>
	
	<table class="view_table">
		<tr>
			<th class="min-wid200">의뢰서 제목</th>
			<td><%=data.name%></td>
		</tr>
		<tr>
			<th>설명<br><span id="descDocCnt">0</span>/4000</th>
			<td colspan="3">
				<textarea rows="3" cols="" class="AXTextarea bgk" readonly="readonly"><%=data.description%></textarea>
			</td>			
		</tr>
		<tr>
			<th>주 첨부파일</th>
			<td>
				<jsp:include page="/jsp/common/primary.jsp">
					<jsp:param value="<%=data.oid%>" name="oid"/>
				</jsp:include>
			</td>
		</tr>						
		<tr>
			<th>첨부파일</th>
			<td>
				<jsp:include page="/jsp/common/secondary.jsp">
					<jsp:param value="<%=data.oid%>" name="oid"/>
				</jsp:include>
			</td>
		</tr>				
		<tr>
			<th>관련 작번</th>
			<td colspan="3">
		        <table id="tblBackground">
	        	    <tr>
		                <td>
						<div id="spreadsheet"></div>
						<script>
						var jexcels = jexcel(document.getElementById('spreadsheet'), {
							data : <%=data.jsonList%>,
							rowResize:false,
						    columnDrag:false,
						    tableOverflow:true,
						    columns: [
						    	{ type: 'hidden', title:'oid', readOnly: true },
						        { type: 'text', title:'작번유형', width:60, readOnly: true },
						        { type: 'text', title:'거래처', width:100, readOnly: true },
						        { type: 'text', title:'설치장소', width:100, readOnly: true },
						        { type: 'text', title:'막종', width:100, readOnly: true },
						        { type: 'text', title:'KEK작번', width:100, readOnly: true },
						        { type: 'text', title:'KE작번', width:140, readOnly: true },
						        { type: 'text', title:'USER ID', width:90, readOnly: true },
						        { type: 'text', title:'요구 납기일', width:90, readOnly: true },
						        { type: 'text', title:'작업내용', width:300, readOnly: true },
						        { type: 'text', title:'모델', width:100, readOnly: true },
						        { type: 'text', title:'발행일', width:90, readOnly: true },
						     ]
						});
						</script>
						</td>
					</tr>
				</table>			
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
								for(RequestDocumentProjectLink projectLink : projectList) { 
								 										Project project = projectLink.getProject(); 
								 										String ooid = project.getPersistInfo().getObjectIdentifier().getStringValue();
								%> 
								<tr>
									<td class="center"><%=project.getPType() %></td>
									<td class="center"><%=project.getKekNumber() %></td>
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