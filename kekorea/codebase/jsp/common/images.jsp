<%@page import="wt.content.ContentHolder"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="java.util.Vector"%>
<%@page import="e3ps.common.util.ContentUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String oid = request.getParameter("oid");
	ReferenceFactory rf = new ReferenceFactory();
	ContentHolder holder = (ContentHolder) rf.getReference(oid).getObject();
%>
<tr>
	<th>이미지 파일</th>
	<td colspan="3">
		<table class="file_table">
			<colgroup>
				<col width="*">
				<col width="150">
				<col width="150">
				<col width="150">
			</colgroup>
			<tr>
				<th>파일명</th>
				<th>파일타입</th>
				<th>파일크기</th>
			</tr>
			<%
				Vector<String[]> secondarys = ContentUtils.getImages(holder);
				for (String[] secondary : secondarys) {
			%>
			<tr>
				<td class="left indent5"><a href="<%=secondary[5]%>"> <img src="<%=secondary[4]%>" class="pos2">&nbsp;<%=secondary[2]%></a></td>
				<td class="center"><%=secondary[7]%></td>
				<td class="center"><%=secondary[3]%></td>
			</tr>
			<%
				}
				if (secondarys.size() == 0) {
			%>
			<tr>
				<td colspan="3" class="nodata">등록된 이미지 파일이 없습니다.</td>
			</tr>
			<%
				}
			%>
		</table>
	</td>
</tr>