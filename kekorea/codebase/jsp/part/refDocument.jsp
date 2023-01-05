
<%@page import="e3ps.doc.column.DocumentColumnData"%>
<%@page import="e3ps.part.service.PartHelper"%>

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
	ArrayList<WTDocument> list = PartHelper.manager.getWTDocument(part);
	int size = list.size();
%>
<table id="tblBackground">
	<tr>
		<td>
			<div <%if(size > 4) { %> class="refDoc_container" <%} %> id="refDoc_container">
				<table class="in_list_table left-border3 fix_table">
					<colgroup>
						<col width="300">
						<col width="300">
						<col width="100">
						<col width="80">
						<col width="80">
						<col width="100">
					</colgroup>
					<thead>
						<tr>
							<th>문서번호</th>
							<th>문서명</th>
							<th>버전</th>
							<th>상태</th>
							<th>수정자</th>
							<th>수정일</th>
						</tr>
					</thead>
					<%
						for (WTDocument document : list) {
							DocumentColumnData pd = new DocumentColumnData(document);
					%>
					<tr>
						<%-- <td class="left infoPer" data-oid="<%=pd.oid %>"><img src="<%=pd.iconPath %>" class="pos3">&nbsp;<%=pd.number %></td> --%>
						<td class="left infoPer" data-oid="<%=pd.oid %>"><img src="<%=pd.iconPath %>" class="pos3">&nbsp;<%=pd.number %></td>
						<td class="left infoPer" data-oid="<%=pd.oid %>"><img src="<%=pd.iconPath %>" class="pos3">&nbsp;<%=pd.name %></td>
						<td><%=pd.version %></td>
						<td><%=pd.state %></td>
						<td><%=pd.modifier %></td>
						<td><%=pd.modifyDate %></td>
					</tr>
					<%
						}
						if(list.size() == 0) {
					%>
					<tr>
						<td class="nodata" colspan="6">관련 문서가 없습니다.</td>
					</tr>
					<%
						}
					%>
				</table>
			</div>
		</td>
	</tr>
</table>