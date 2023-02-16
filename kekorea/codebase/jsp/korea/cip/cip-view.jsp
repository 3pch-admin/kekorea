<%@page import="e3ps.korea.cip.beans.CipColumnData"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<CipColumnData> list = (ArrayList<CipColumnData>) request.getAttribute("list");
%>
<%@include file="/jsp/include/auigrid.jsp"%>
<table class="btn_table">
	<tr>
		<td class="right">
			<input type="button" value="닫기" id="closeBtn" title="닫기" class="blueBtn">
		</td>
	</tr>
</table>


<script type="text/javascript">
	$(function() {
		$("#closeBtn").click(function() {
			self.close();
		})
	})
</script>