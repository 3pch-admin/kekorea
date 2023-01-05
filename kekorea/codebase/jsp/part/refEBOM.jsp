<%@page import="e3ps.echange.column.EBOMColumnData"%>
<%@page import="e3ps.echange.EBOM"%>
<%@page import="e3ps.document.column.DocumentColumnData"%>
<%@page import="e3ps.part.service.PartHelper"%>
<%@page import="e3ps.document.service.DocumentHelper"%>
<%@page import="e3ps.part.column.PartProductColumnData"%>
<%@page import="wt.part.WTPart"%>
<%@page import="java.util.ArrayList"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="e3ps.common.util.ContentUtils"%>
<%@page import="wt.content.ContentHolder"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	String oid = request.getParameter("oid");
	ReferenceFactory rf = new ReferenceFactory();
	WTPart part = (WTPart) rf.getReference(oid).getObject();
	ArrayList<EBOM> list = PartHelper.manager.getEBOM(part);
	int size = list.size();
%>
<table id="tblBackground">
	<tr>
		<td>
			<div <%if(size > 4) { %> class="refEbom_container" <%} %> id="refEbom_container">
				<table class="in_list_table left-border fix_table">
					<colgroup>
						<col width="300">
						<col width="350">
						<col width="200">
						<col width="100">
						<col width="100">
						<col width="100">
						<col width="300">
						<col width="130">
						<col width="100">
						<col width="150">
					</colgroup>
					<thead>
						<tr>
							<th>EBOM LSIT 번호</th>
							<th>SUBJECT</th>
							<th>모델</th>
							<th>총 비용</th>
							<th>긴급여부</th>
							<th>긴급사유</th>
							<th>특이사항</th>
							<th>상태</th>
							<th>작성자</th>
							<th>작성일</th>
						</tr>
					</thead>
					<%
						for (EBOM ebom : list) {
							EBOMColumnData pd = new EBOMColumnData(ebom);
					%>
					<tr>
						<td class="left infoPer" data-oid="<%=pd.oid %>"><img src="<%=pd.iconPath %>" class="pos3">&nbsp;<%=pd.number %></td>
						<td class="left infoPer" data-oid="<%=pd.oid %>"><img src="<%=pd.iconPath %>" class="pos3">&nbsp;<%=pd.subject %></td>
						<td><%=pd.model %></td>
						<td><%=pd.cost %></td>
						<td><%=pd.emergency %></td>
						<td><%=pd.emergencyReason %></td>
						<td><%=pd.description %></td>
						<td><%=pd.state %></td>
						<td><%=pd.creator %></td>
						<td><%=pd.createDate %></td>
					</tr>
					<%
						}
						if(list.size() == 0) {
					%>
					<tr>
						<td class="nodata" colspan="10">관련 EBOM LIST가 없습니다.</td>
					</tr>
					<%
						}
					%>
				</table>
			</div>
		</td>
	</tr>
</table>