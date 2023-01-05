<%@page import="e3ps.doc.service.DocumentHelper"%>
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
	WTDocument document = (WTDocument) rf.getReference(oid).getObject();
	ArrayList<WTPart> list = DocumentHelper.manager.getWTPart(document);
	int size = list.size();
%>
<table id="tblBackground">
	<tr>
		<td>
			<div <%if(size > 6) { %> class="parts_container" <%} %> id="parts_container">
				<table class="in_list_table left-border2 fix_table">
					<colgroup>
						<col width="200">
						<col width="300">
						<col width="300">
						<col width="100">
						<col width="150">
						<col width="150">
						<col width="150">
<!-- 						<col width="20%"> -->
<!-- 						<col width="20%"> -->
<!-- 						<col width="20%"> -->
<!-- 						<col width="10%"> -->
<!-- 						<col width="10%"> -->
<!-- 						<col width="10%"> -->
<!-- 						<col width="10%"> -->
					</colgroup>
					<thead>
						<tr>
							<th>규격</th>
							<th>파일이름</th>
							<th>품명</th>
							<th>버전</th>
							<th>상태</th>
							<th>작성자</th>
							<th>수정자</th>
						</tr>		
					</thead>
					<%
						for (WTPart part : list) {
							PartProductColumnData pd = new PartProductColumnData(part);
					%>
					<tr>
						<td class="left infoPer" data-oid="<%=pd.oid %>"><img src="<%=pd.iconPath %>" class="pos3">&nbsp;<%=pd.number %></td>
						<td><%=pd.name %></td>
						<td class="left infoPer" data-oid="<%=pd.oid %>"><img src="<%=pd.iconPath %>" class="pos3">&nbsp;<%=pd.name_of_parts %></td>
						<td><%=pd.version %></td>
						<td><%=pd.state %></td>
						<td><%=pd.creator %></td>
						<td><%=pd.modifier %></td>
					</tr>
					<%
						}
						if(list.size() == 0) {
					%>
					<tr>
						<td class="nodata" colspan="7">관련 부품이 없습니다.</td>
					</tr>
					<%
						}
					%>
				</table>
			</div>
		</td>
	</tr>
</table>