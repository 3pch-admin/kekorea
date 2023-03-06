<%@page import="e3ps.org.People"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="e3ps.org.Department"%>
<%@page import="java.util.Vector"%>
<%@page import="wt.content.ContentRoleType"%>
<%@page import="wt.content.ContentHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="e3ps.org.dto.UserViewData"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	// popup check
	String popup = (String) request.getParameter("popup");
	boolean isPopup = Boolean.parseBoolean(popup);

	// data
	UserViewData data = (UserViewData) request.getAttribute("data");
	ReferenceFactory rf = new ReferenceFactory();
	People user = (People)rf.getReference(data.oid).getObject();
// 	out.println(user.getDepartment().getPersistInfo().getObjectIdentifier().getStringValue());
	
	// isAdmin
	boolean isAdmin = CommonUtils.isAdmin();
%>

<td valign="top">
	<script type="text/javascript">
	$(document).ready(function() {
		
	})
	</script>
	<input type="hidden" name="oid" id="oid" value="<%=data.oid %>">
	<input type="hidden" name="popup" id="popup" value="<%=isPopup %>">

	<div class="header_title">
		<i class="axi axi-subtitles"></i><span>사용자 정보</span>
	</div>
	<%
		if(isPopup) {
	%>
	<div class="right">
		<input type="button" value="수정" id="modifyUserBtn" title="수정" data-oid="<%=data.oid %>">
		<input type="button" value="닫기" id="closeUserBtn" title="닫기" class="redBtn">
	</div>
	<%
		}
	%>
	
	<table class="view_table">
		<colgroup>
			<col width="250">
			<col width="*">
			<col width="250">
			<col width="*">
		</colgroup>
		<tr>
			<th>아이디</th>
			<td colspan="3"><%=data.id %></td>
			<td class="center userImg" rowspan="8">
			<%
				Vector<String[]> ss = data.photo;
				if(ss.size() > 0) {
				String[] photo = (String[])ss.get(0);
					if(photo[0] == null) {
			%>
			<img src="/Windchill/jsp/images/user_photo_default.gif">
			<%
					} else {
			%>
			<img src="<%=photo[5] %>">
			<%
					}
				}
			%>
			</td>
		</tr>
		<tr>
			<th>이름</th>
			<td colspan="3"><%=data.name %></td>
		</tr>		
		<tr>
			<th>부서</th>
			<td colspan="3"><%=data.departmentName %></td>
		</tr>
		<tr>
			<th>직급</th>
			<td colspan="3"><%=data.duty %></td>
		</tr>	
		<tr>
			<th>직위</th>
			<td colspan="3"><%=data.rank %></td>
		</tr>
		<tr>
			<th>이메일</th>
			<td colspan="3"><%=data.email %></td>
		</tr>
		<tr>
			<th>퇴사여부</th>
			<td colspan="3"><%=data.resign %></td>
		</tr>
		<tr>
			<th>전화번호</th>
			<td colspan="3"><%=data.mobile %></td>
		</tr>							
	</table>
	
	<%
		if(!isPopup) {
	%>
	<table class="btn_table">
		<tr>
			<td class="center">
				<input type="button" value="수정" id="modifyUserBtn" title="수정" data-oid="<%=data.oid %>">
				<input type="button" value="목록" id="listUserBtn" title="목록" class="blueBtn">			
			</td>
		</tr>
	</table>
	<%
		}
	%>
</td>