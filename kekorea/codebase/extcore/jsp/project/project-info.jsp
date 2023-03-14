<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = (String) request.getAttribute("oid");
%>
<table>
	<colgroup>
		<col width="230">
		<col width="*">
	</colgroup>
	<tr>
		<td valign="top">
			<iframe src="/Windchill/plm/project/tree?oid=<%=oid%>" style="height: calc(100vh - 100px); width: 220px;"></iframe>
		</td>
		<td valign="top">
			<iframe src="/Windchill/plm/project/view?oid=<%=oid%>" style="height: calc(100vh - 200px); width: calc(100vw - 250px);"></iframe>
		</td>
	</tr>
</table>