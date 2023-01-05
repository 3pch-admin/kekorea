<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="e3ps.project.Issue"%>
<%@page import="e3ps.project.beans.ProjectViewData"%>
<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="e3ps.doc.service.DocumentHelper"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="e3ps.project.Project"%>
<%@page import="e3ps.project.IssueProjectLink"%>
<%@page import="e3ps.project.ProjectOutputLink"%>
<%@page import="java.util.ArrayList"%>
<% 

String root = DocumentHelper.OUTPUT_ROOT;

String popup = (String) request.getParameter("popup");
boolean isPopup = Boolean.parseBoolean(popup);
Issue issue = (Issue) request.getAttribute("issue");
String poid = (String)request.getParameter("poid");
ProjectViewData data = (ProjectViewData) request.getAttribute("data");
String ioid = issue.getPersistInfo().getObjectIdentifier().getStringValue();
ArrayList<IssueProjectLink> projectList = (ArrayList<IssueProjectLink>) request.getAttribute("projectList");

Project project = null;
project = (Project) request.getAttribute("project");
String outputLoc = (String) request.getParameter("outputLoc");
boolean isOutput = false;
ReferenceFactory rf = new ReferenceFactory();
if(!StringUtils.isNull(outputLoc)) {
	root = outputLoc;
	isOutput = true;
	project = (Project)rf.getReference(poid).getObject();
}

%>

<td valign="top">

<script type="text/javascript">
	$(document).ready(function() {
		upload.pageStart("<%=ioid%>", "", "primary");
		upload.pageStart("<%=ioid%>", "", "secondary");
		$("input").checks();
		$(".documents_add_table").tableHeadFixer();
	})

</script>

	<input type="hidden" name="popup" id="popup" value="<%=isPopup%>">
	<input type="hidden" name="poid" id="poid" value="<%=poid%>">
	<input type="hidden" name="oid" id="oid" value="<%=ioid%>">
	<table class="btn_table">
	<!-- create header title -->
		<tr>
			<td>
				<div class="header_title">
					<i class="axi axi-subtitles"></i><span>특이사항 수정</span>
					<!-- req msg -->
					<font class="reqMsg">(빨간색 속성 값은 필수 입력 값입니다.)</font>
				
				</div>
			</td>
			<td class="right">
				<input type="button" value="수정" id="modifyIssueActionBtn" title="수정" >
				<input type="button" value="닫기" id="IscloseBtn" title="닫기" class="redBtn"  onclick="javascript:window.close()">
			</td>
		</tr>
	</table>

	<table class = "view_table">
		<colgroup>
			<col width="200">
			<col width="600">
			<col width="200">
			<col width="600">
		</colgroup>
		<tr>
			<th>특이사항 제목</th>
			<td  colspan="3">
				<input type="text" name="istitle" id="istitle" class="AXInput wid500"  value="<%= issue.getName()%>"/>
			</td>
		</tr>
		<tr>
			<th>내용</th>
			<td  colspan="3">
				<textarea style="height: 100px !Important;" class="AXTextarea" name="description" id="description" rows="3" cols=""><%=issue.getDescription() %></textarea>
			</td>
		</tr>
		<tr>
			<th>주 첨부파일</th>
			<td colspan="3">
				<!-- upload.js see -->
				<div class="AXUpload5" id="primary_layer"></div>
			</td>
		</tr>
		<tr>
		<tr>
			<th>첨부파일<span id="fileCount"></span></th>
			<td colspan="3">
				<div class="AXUpload5" id="secondary_layer"></div>
				<div class="AXUpload5QueueBox_list" id="uploadQueueBox"></div>
			</td>
		</tr>
		<tr>
			<th>관련 프로젝트</th>
			<td colspan="3">
			
				<table class="in_btn_table">
					<tr>
						<td class="add">
							<input type="button" value="작번 추가" title="작번 추가" id="addIssueProjects" data-context="product" data-dbl="true" >
							<input type="button" value="작번 삭제" title="작번 삭제" id="delProjects" class="blueBtn">
						</td>
					</tr>
				</table>
<!-- 				<div style="width:100%; height:300px; overflow:auto;"> -->
		        <table id="tblBackground">
		            <tr>
		                <td>
							<div id="projects_container">
								<table class="create_project_table_in documents_add_table">
									<colgroup>
										<col width="40">
										<col width="80">
										<col width="100">
										<col width="100">
										<col width="100">
										<col width="100">
										<col width="400">
									</colgroup>
									<thead>
										<tr>
											<th>
												<input type="checkbox" name="allProjects" id="allProjects">
											</th>
											<th>작번유형</th>
											<th>KEK 작번</th>
											<th>KE 작번</th>
											<th>고객사</th>
											<th>막종</th>									
											<th>작업내용</th>
										</tr>						
									</thead>
									
									<tbody id="addProjectsBody">
									<%
									for(IssueProjectLink projectLink : projectList) {
										project = projectLink.getProject();
										String ooid = project.getPersistInfo().getObjectIdentifier().getStringValue();
									%>
										<tr>
											<td>
												<input type="checkbox" value="<%=ooid%>" name="projectOid" class="isBox">
											</td>
											<td class="center" data-oid="<%=ooid %>"><%=project.getPType() %></td>
											<td class="infoParts" data-oid="<%=ooid %>"><%=project.getKekNumber() %></td>
											<td class="infoParts" data-oid="<%=ooid %>">
												<%=project.getKeNumber() %>
											</td>
											<td><%=project.getCustomer()%></td>
											<td><%=project.getMak()%></td>
											<td class="left indent10"><%=project.getDescription() %></td>
										</tr>
									<%
										}
										if(projectList.size() == 0) {
									%>
										<tr id="nodataProjects">
											<td class="nodata" colspan="7">관련 작번이 없습니다.</td>
										</tr>
									<%
										}
									%>
									
									</tbody>						
								</table>
							</div>
						</td>
					</tr>
				</table>
<!-- 				</div> -->
			</td>
		</tr>
	</table>
</td>