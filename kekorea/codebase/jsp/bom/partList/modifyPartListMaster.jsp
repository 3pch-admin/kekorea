<%@page import="e3ps.project.Project"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.partlist.PartListMasterProjectLink"%>
<%@page import="e3ps.partlist.beans.PartListMasterViewData"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	// popup check
	String popup = (String) request.getParameter("popup");
	boolean isPopup = Boolean.parseBoolean(popup);

	// data
	PartListMasterViewData data = (PartListMasterViewData) request.getAttribute("data");
	ArrayList<PartListMasterProjectLink> projectList = (ArrayList<PartListMasterProjectLink>) request.getAttribute("projectList");
	// isAdmin
	boolean isAdmin = CommonUtils.isAdmin();

	String install = (String)request.getAttribute("install");
	
	
%> 
<td valign="top">
	<script type="text/javascript">
	$(document).ready(function() {
		upload.pageStart("<%=data.oid%>", "", "secondary");
		
		$("input").checks();

		$("#engType").bindSelect();
		$("#engType").bindSelectSetValue("<%=data.engType.substring(0, 2) %>");
	})
	</script>
	<input type="hidden" name="oid" id="oid" value="<%=data.oid %>">
	<input type="hidden" name="popup" id="popup" value="<%=isPopup %>">

<table class="btn_table">
		<tr>
			<td>
	<div class="header_title">
		<i class="axi axi-subtitles"></i><span>수배표 수정</span>
		<!-- req msg -->
		<font class="reqMsg">(빨간색 속성 값은 필수 입력 값입니다.)</font>
	</div>
	</td>
	<%-- <%
		if(isPopup) {
	%> --%>
	<td>
	<div class="right">
				<input type="button" value="수정" id="modifyPartListActionBtn" title="수정" data-oid="<%=data.oid %>">
				<input type="button" value="뒤로" id="backBtn" title="뒤로" class="blueBtn">				
	</div>
	</td>
	</tr>
	</table>
	<%-- <%
		}
	%> --%>
	
	<table class="create_table">
		<tr>
			<th class="min-wid200"><font class="req">수배표 제목</font></th>
			<td><input type="text" name="name" id="name" class="AXInput" style="width: 80%;" value="<%=data.name %>"></td>
		</tr>
		<tr>
			<th><font class="req">설계구분</font></th>
			<td colspan="3">
				<select name="engType" id="engType" class="AXSelect wid100">
					<option value="" <%if(data.engType.equals("")) {  %> selected <%} %>>선택</option>
					<option value="전기" <%if(data.engType.equals("전기")) {  %> selected <%} %>>전기</option>
					<option value="기계" <%if(data.engType.equals("기계")) {  %> selected <%} %>>기계</option>
<%-- 					<option value="SOFT" <%if(deptName.equals("SW설계")) {  %> selected <%} %>>SOFT</option> --%>
				</select>
			</td>
		</tr>
		<tr>
			<th>설명<br><span id="descDocCnt">0</span>/4000</th>
			<td colspan="3">
				<textarea rows="3" cols="" class="AXTextarea bgk" name="description"><%=data.description %></textarea>
			</td>			
		</tr>
		<tr>
			<th><font class="req">관련 KEK 작번</font></th>
			<td colspan="3">
				<table class="in_btn_table">
					<tr>
						<td class="add">
							<input type="button" value="작번 추가" title="작번 추가" id="addProjects" data-context="product" data-dbl="true">
							<input type="button" value="작번 삭제" title="작번 삭제" id="delProjects" class="blueBtn">
						</td>
					</tr>
				</table>
		        <table id="tblBackground">
		            <tr>
		                <td>
							<div id="projects_container">
								<table class="create_table_in fix_table">
									<colgroup>
										<col width="40">
										<col width="80">
										<col width="100">
										<col width="100">
										<col width="100">
										<col width="100">
										<col width="600">
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
									for(PartListMasterProjectLink projectLink : projectList) {
										Project project = projectLink.getProject();
										String ooid = project.getPersistInfo().getObjectIdentifier().getStringValue();
											//PartProductColumnData pdata = new PartProductColumnData(refPart);
									%>
										<tr>
											<td>
												<input type="checkbox" value="<%=ooid%>" name="projectOid" class="isBox">
											</td>
											<td class="center"><%=project.getPType() %></td>
											<td class="infoParts center" data-oid="<%=ooid %>"><%=project.getKekNumber() %></td>
											<td class="infoParts center" data-oid="<%=ooid %>">
												<%=project.getKeNumber() %>
											</td>
											<td><%=project.getCustomer()%></td>
											<td><%=project.getMak()%></td>
											<td class="left indent10" title="<%=project.getDescription() %>"><%=project.getDescription() %></td>
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
			</td>
		</tr>
		<!-- 결재 -->
		<jsp:include page="/jsp/common/appLine.jsp">
			<jsp:param value="true" name="required" />
		</jsp:include>					
		<tr>
			<th>첨부파일<span id="fileCount"></span></th>
			<td colspan="3">
				<!-- upload.js see -->
				<div class="AXUpload5" id="secondary_layer"></div>
				<div class="AXUpload5QueueBox_list" id="uploadQueueBox"></div>
			</td>
		</tr>				
		<tr>
			<th>수배표 수정</th>
			<td colspan="3">
		        <table id="tblBackground">
	        	    <tr>
		                <td>
							<div id="spreadsheet"></div>
							<script>
							
							var jexcels = jexcel(document.getElementById('spreadsheet'), {
								data : <%=data.jsonList %>,								
								rowResize:false,
							    columnDrag:false,
							    tableOverflow:true,
							    onchange : partlists.changedCheckERP,
							    columns: [
							    	{ type: 'hidden', title:'oid', readOnly: true },
							        { type: 'text', title:'LOT_NO', width:60 },
							        { type: 'text', title:'UNIT_NAME', width:130 },
							        { type: 'text', title:'부품번호', width:100 },
							        { type: 'text', title:'부품명', width:240, readOnly:true },
							        { type: 'text', title:'규격', width:300, readOnly:true },
							        { type: 'text', title:'MAKER', width:100 },
							        { type: 'text', title:'거래처', width:100 },
							        { type: 'text', title:'수량', width:40 },
							        { type: 'text', title:'단위', width:40, readOnly:true },
							        { type: 'text', title:'단가', width:100, readOnly:false },
							        { type: 'text', title:'화폐', width:50, readOnly:true },
							        { type: 'text', title:'원화금액', width:100, readOnly:false },
							        { type: 'text', title:'수배일자', width:80, readOnly:true },
							        { type: 'text', title:'환율', width:50, readOnly:true },
							        { type: 'text', title:'참고도면', width:100 },
							        { type: 'text', title:'조달구분', width:100 },
							        { type: 'text', title:'비고', width:150 },
							     ]
							});
							</script>			
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</td>