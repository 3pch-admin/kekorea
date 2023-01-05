<%@page import="e3ps.common.util.CommonUtils"%>
<%@page import="e3ps.project.Issue"%>
<%@page import="e3ps.project.Project"%>
<%@page import="e3ps.doc.beans.DocumentViewData"%>
<%@page import="e3ps.project.beans.ProjectViewData"%>
<%@page import="e3ps.project.IssueProjectLink"%>
<%@page import="e3ps.project.ProjectOutputLink"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="e3ps.doc.service.DocumentHelper"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<% 

String root = DocumentHelper.OUTPUT_ROOT;

//popup check
String popup = (String) request.getParameter("popup");
String poid = (String) request.getParameter("poid");
boolean isPopup = Boolean.parseBoolean(popup);
Issue issue = (Issue) request.getAttribute("issue");
//data
// ProjectViewData data = (ProjectViewData) request.getAttribute("data");
ArrayList<IssueProjectLink> projectList = (ArrayList<IssueProjectLink>) request.getAttribute("projectList");
String oid = issue.getPersistInfo().getObjectIdentifier().getStringValue();
// String poid = data.oid;
%>

<td valign="top">
	<script type="text/javascript">
	$(document).ready(function() {
		
		var len = "<%=issue.getDescription().length() %>";
		$("#descDocCnt").text(len);
		$(".documents_add_table").tableHeadFixer();
	})
	
 	function openPopup(oid) {
		var url = "/Windchill/plm/project/modifyIssue?popup=true&oid=" + oid + "&poid=<%=poid %>";
		var name = "modifyIssue";
// 		var option = "scrollbars=yes, resizable=yes, fullscreen=yes";
		window.open(url, name, 'height=' + screen.height + ',width=' + screen.width + 'fullscreen=yes');
		
	}
	
	</script>
<input type="hidden" name="oid" id="oid" value="<%=issue.getPersistInfo().getObjectIdentifier().getStringValue() %>">
<input type="hidden" name="popup" id="popup" value="<%=isPopup %>">
	
	<table class = "btn_table">
		<tr>
			<td>
				<div class="header_title">
					<i class="axi axi-subtitles"></i><span>특이사항 정보</span>
				</div>
			</td>
			<td>
				<div class = "right">
					<a href = "javascript:openPopup('<%=oid%>');" >
						<input type="button" value="수정" id="IsmodifyBtn" title="수정"  >
					</a>
					<input type="button" value="닫기" id="IscloseBtn" title="닫기" class="redBtn"  onclick="window.close();">
				</div>
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
			<td colspan="3"><%=issue.getName()%></td>
		</tr>
		<tr>
			<th>설명<br><span id="descDocCnt">0</span>/4000</th>
			<td colspan="3">
				<textarea style="height: 100px !important;" rows="3" cols="" class="AXTextarea bgk" readonly="readonly"><%=issue.getDescription() %></textarea>
			</td>	
		</tr>
		<tr>
			<th>주 첨부파일</th>
			<td colspan="3">
				<jsp:include page="/jsp/common/primary.jsp">
					<jsp:param value="<%=oid%>" name="oid"/>
				</jsp:include>
			</td>
		</tr>	
		<tr>
			<th>첨부파일</th>
			<td colspan="3">
				<jsp:include page="/jsp/common/secondary.jsp">
					<jsp:param value="<%=oid %>" name="oid"/>
				</jsp:include>
			</td>
		</tr>
		<tr>
			<th>작성자</th>	
			<%-- <td><%=issue.getCreator() %></td> --%>
			<td><%=issue.getOwnership().getOwner().getFullName() %></td>
			<th>작성일</th>
			<td><%=issue.getCreateTimestamp().toString().substring(0, 16) %></td>
		</tr>
		
<!-- 		<tr> -->
<!-- 			<th>수정자</th>	 -->
<%-- 			<td><%=issue.%></td> --%>
<!-- 			<th>수정일</th> -->
<%-- 			<td><%=issue.getModifyTimestamp().toString().substring(0, 16) %></td> --%>
<!-- 		</tr> -->
		
		<tr>
			<th>연관된 프로젝트</th>
			<td colspan="3">	
			<table class="output_table">
				<tr>
					<td class="nonBorder">
						<div id="projects_container">
							<table class="create_project_table_in">
								<thead>
									<tr>
										<th>NO</th>
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
 									int cnt = 1;
									for(IssueProjectLink projectLink : projectList) {
										Project project = projectLink.getProject();
										String ooid = project.getPersistInfo().getObjectIdentifier().getStringValue();
								%>
								<tr>
									<td class="center"><%=cnt++ %></td>
									<td class="center"><%=project.getPType() %></td>
									<td class="center viewProject" data-oid="<%=ooid%>"><input type="hidden" name="projectOid" value="<%=ooid %>"><%=project.getKekNumber() %></td>
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
								<tbody id="addProjectsBody">
									<tr id="nodataIssues">
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
	
