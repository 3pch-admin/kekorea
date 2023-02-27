<%@page import="e3ps.part.beans.PartViewData"%>
<%@page import="e3ps.approval.beans.ApprovalLineViewData"%>
<%@page import="e3ps.workspace.ApprovalLine"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.approval.beans.ApprovalMasterViewData"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	ArrayList<PartViewData> list = (ArrayList<PartViewData>) request.getAttribute("list");
	String context = (String) request.getAttribute("context");
	
	boolean isLibrary = false;
	boolean isProduct = false;
	String title = "구매품";
	if("product".equals(context)) {
		title = "가공품";
		isProduct = true;
	} else if("library".equals(context)) {
		title = "구매품";
		isLibrary = true;
	}
	// popup check
	String popup = (String) request.getParameter("popup");
	boolean isPopup = Boolean.parseBoolean(popup);
%>
<td valign="top">

<!-- script -->
	<script type="text/javascript">
	$(document).ready(function() {
		$("#closeBtn").click(function() {
			self.close();
		})
	})
	</script>
	<table class="btn_table top_margin0">
		<tr>
			<td class="left">
				<div class="header_title pos5">
					<i class="axi axi-subtitles"></i><span><%=title %>제품 정보</span>
				</div>
			</td>
			<td class="right">
				<input type="button" id="closeBtn" title="닫기" value="닫기">
			</td>
		</tr>
	</table>
	
<!-- 	// 완전히 분리 -->
	<%
		// 가공품
		if(isProduct) {
	%>
	<table class="in_list_table left-border">
		<colgroup>
			<col width="250">
			<col width="*">
			<col width="90">
			<col width="90">
			<col width="130">
			<col width="130">
		</colgroup>
		<tr>
			<th>파일이름</th>
			<th>PRODUCT_NAME</th>
			<th>버전</th>
			<th>상태</th>
			<th>수정자</th>
			<th>수정일</th>
		</tr>
		<%
			for (PartViewData data : list) {
		%>
		<tr>
			<td class="left"><img src="<%=data.iconPath %>" class="pos2">&nbsp;<%=data.number %></td>
			<td class="left"><%=data.name %></td>
			<td><%=data.version + "." + data.iteration %></td>
			<td><%=data.state %></td>
			<td><%=data.modifier %></td>
			<td><%=data.modifyDate %></td>
		</tr>
		<%
			}
			if(list.isNull()) {
		%>
		<tr>
			<td class="nodata" colspan="6"><%=title %>제품이 없습니다.</td>		
		</tr>
		<%
			}
		%>
	</table>
	<%
		}
		if(isLibrary) {
	%>
	<table class="in_list_table left-border">
		<colgroup>
			<col width="250">
			<col width="*">
			<col width="150">
			<col width="150">
			<col width="130">
			<col width="130">
		</colgroup>
		<tr>
			<th>파일이름</th>
			<th>SPEC</th>
			<th>PRODUCT_NAME</th>
			<th>MAKER</th>
			<th>MASTER_TYPE</th>
			<th>상태</th>
		</tr>
		<%
			for (PartViewData data : list) {
		%>
		<tr>
			<td class="left"><img src="<%=data.iconPath %>" class="pos2">&nbsp;<%=data.number %></td>
			<td class="left"><%=data.spec %></td>
			<td><%=data.name %></td>
			<td><%=data.maker %></td>
			<td><%=data.master_type %></td>
			<td><%=data.state %></td>
		</tr>
		<%
			}
			if(list.isNull()) {
		%>
		<tr>
			<td class="nodata" colspan="6"><%=title %>제품이 없습니다.</td>			
		</tr>
		<%
			}
		%>
	</table>	
	<%
		}
	%>
</td>