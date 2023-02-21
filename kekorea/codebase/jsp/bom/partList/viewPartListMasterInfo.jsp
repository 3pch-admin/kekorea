<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="e3ps.partlist.beans.PartListDataViewData"%>
<%@page import="e3ps.partlist.PartListData"%>
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
// 	QueryResult qr2 = PersistenceHelper.manager.navigate(data.master, "project", PartListMasterProjectLink.class);
// 	out.println(qr2.size());
	// isAdmin
	boolean isAdmin = CommonUtils.isAdmin();
	
// 	out.println(data.jsonList);
// 	out.println("=========="+data.master.getLifeCycleName());
// 	out.println(data.master.getNumber());
	String install = (String)request.getAttribute("install");
%> 
<td valign="top">
	<script type="text/javascript">
	$(document).ready(function() {
		
		$("input").checks();

		$("#engType").bindSelect();
		$("#engType").bindSelectSetValue("<%=data.engType.substring(0, 2) %>");
	})
	
	</script>
	
	<script src="/cdn/jquery.slim.min.js"></script>
	<script src="plm_kekorea/jsp/js/tableSortable.min.js"></script>
	<script src="https://unpkg.com/jquery-tablesortable"></script>
	<script type="text/javascript">
		$(function() {
			$("#myTable").tableSortable();
		});
	</script>
	<input type="hidden" name="oid" id="oid" value="<%=data.oid %>">
	<input type="hidden" name="popup" id="popup" value="<%=isPopup %>">

<table class="btn_table">
		<tr>
			<td>
	<div class="header_title">
		<i class="axi axi-subtitles"></i><span>수배표 상세 정보</span>
		<!-- req msg -->
	</div>
	</td>
	<%-- <%
		if(isPopup) {
	%> --%>
	<td>
	<div class="right">
				<input type="button" value="수정" id="modifyPartListBtn" title="수정" data-oid="<%=data.oid %>">
				<%if(data.isCreator || isAdmin){ %>
					<input type="button" value="삭제" id="deletePartListBtn" title="삭제" class="redBtn">
				<%} %>
				<input type="button" value="닫기" id="closeDocBtn" title="닫기" class="redBtn">		
	</div>
	</td>
	</tr>
	</table>
	<%-- <%
		}
	%> --%>
	
	<table class="view_table">
		<tr>
			<th style="min-width: 70px; width: 70px; max-width: 70px;">수배표 제목</th>
			<td><%=data.name %></td>
		</tr>
		<tr>
			<th>설계구분</th>
				<td><%=data.engType %></td>
		</tr>
		<tr>
			<th>설명</th>
			<td >
				<%=data.description %>
			</td>			
		</tr>
		<tr>
			<th>연관된<br>프로젝트</th>
			<td colspan="3">	
			<table class="output_table">
				<tr>
					<td class="nonBorder">
						<div id="outputs_container">
							<table class="create_project_table_in" id="myTable">
								<thead>
									<tr>
										<th>NO</th>
										<th>작번유형</th>
										<th>KEK 작번</th>
										<th>KE 작번</th>
										<th>고객사</th>
										<th>막종</th>
										<th>작업내용</th>
										<th>발행일</th>
									</tr>
								</thead>
								<tbody id="addOutputsBody">
								<%
									int cnt = 1;
									for(PartListMasterProjectLink projectLink : projectList) {
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
									<td class="center"><%=project.getPDate().toString().substring(0, 10) %></td>
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
		<tr>
			<th>첨부파일</th>
			<td>
				<jsp:include page="/jsp/common/secondary.jsp">
					<jsp:param value="<%=data.oid %>" name="oid"/>
				</jsp:include>
			</td>
		</tr>			
		<tr>
			<th>수배표</th>
			<td>
	        <table id="tblBackground">
	            <tr>
	                <td>			
						<div>
							<table class="list_table fix_table partlist_table">
								<!-- 17개.. -->
								<colgroup>
									<col width="40">
									<col width="50">
									<col width="100">
									<col width="80">
									<col width="240">
									<col width="300">
									<col width="100">
									<col width="100">
									<col width="40">
									<col width="40">
									<col width="100">
									<col width="50">
									<col width="100">
									<col width="80">
									<col width="50">
									<col width="100">
									<col width="60">
									<col width="150">
								</colgroup>
								<thead>
									<tr>
										<th>NO</th>
										<th>LOT NO</th>
										<th>UNIT NAME</th>
										<th>부품번호</th>
										<th>부품명</th>
										<th>규격</th>
										<th>MAKER</th>
										<th>거래처</th>
										<th>수량</th>
										<th>단위</th>
										<th>단가</th>
										<th>화폐</th>
										<th>원화금액</th>
										<th>수배일자</th>
										<th>환율</th>
										<th>참고도면</th>
										<th>조달구분</th>
										<th>비고</th>
									</tr>						
								</thead>
								<%
									int count = 1;
									ArrayList<PartListData> list = data.list;
									for(PartListData datas : list) {
										PartListDataViewData vdata = new PartListDataViewData(datas);
								%>
								<tr>
									<td title="<%=count%>"><%=count++%></td>
									<td title="<%=vdata.lotNo%>"><%=vdata.lotNo%></td>
									<td title="<%=vdata.unitName%>"><%=vdata.unitName%></td>
									<td class="y_code" data-number="<%=vdata.partNo%>" title="<%=vdata.partNo%>"><%=vdata.partNo%></td>
									<td title="<%=vdata.partName%>"><%=vdata.partName%></td>
									<td class="left indent10" title="<%=vdata.standard%>"><%=vdata.standard%></td>
									<td title="<%=vdata.maker%>"><%=vdata.maker%></td>
									<td title="<%=vdata.customer %>"><%=vdata.customer%></td>
									<td  title="<%=vdata.quantity%>"><%=vdata.quantity%></td>
									<td  title="<%=vdata.unit%>"><%=vdata.unit%></td>
									<td class="right"  title="<%=vdata.price%>"><%=vdata.price%>&nbsp;</td>
									<td  title="<%=vdata.currency%>"><%=vdata.currency%></td>
									<td class="right" title="<%=String.format("%,f", vdata.won).substring(0, String.format("%,f", vdata.won).lastIndexOf("."))%>"><%=String.format("%,f", vdata.won).substring(0, String.format("%,f", vdata.won).lastIndexOf("."))%>&nbsp;</td>
									<td title="<%=vdata.partListDate%>"><%=vdata.partListDate%></td>
									<td title="<%=vdata.exchangeRate%>"><%=vdata.exchangeRate%></td>
									<td title="<%=vdata.referDrawing%>"><%=vdata.referDrawing%></td>
									<td title="<%=vdata.classification%>"><%=vdata.classification%></td>
									<td title="<%=vdata.note%>"><%=vdata.note%></td>
								</tr>
								<%
									}
								%>
								<tr>
									<td colspan="12" class="right" style="background-color: #C4FFBE; text-align: left;" title="<% String s = String.format("%,f", data.master.getTotalPrice());%> <%=s.substring(0, s.lastIndexOf(".")) %>">합계 &nbsp; </td>
									<td colspan="6" style="text-align: left;" title="<%=s.substring(0, s.lastIndexOf(".")) %>
									"><strong>&nbsp;&nbsp;
									
									<%=s.substring(0, s.lastIndexOf(".")) %>
									</strong></td>
								</tr>
								</table>
							</div>
						</td>
					</tr>
				</table>
	</td>
</tr>
</table>