<%@page import="e3ps.admin.service.AdminHelper"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="e3ps.admin.PasswordSetting"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	WTUser user = (WTUser) request.getAttribute("user");

	PasswordSetting ps = AdminHelper.manager.getPasswordSetting();

	

%>    
<td valign="top">
	<!-- script area -->
	<script type="text/javascript">
	$(document).ready(function() {
		$("input").checks();
		

		$("#length").click(function() {
			if($(this).prop("checked")) {
				$("#range").show();
			} else {
				$("#range").hide();
			}
		})
	})
	</script>
	
	<!-- create header title -->
	<div class="header_title">
		<i class="axi axi-subtitles"></i><span>비밀번호 세팅</span>
		<!-- req msg -->
		<font class="reqMsg">(빨간색 속성 값은 필수 입력 값입니다.)</font>
	</div>


	<!-- create table -->
	<table class="create_table">
		<!-- colgroup -->
		<colgroup>
			<col width="230">
			<col>
		</colgroup>
		<tr>
			<th><font class="req">특수문자 세팅</font></th>
			<td>
				<input type="checkbox" name="complex" id="complex" value="complex" <%if(ps.isComplex()) {%> checked="checked" <%} %>>
			</td>
		</tr>
		<tr>
			<th><font class="req">길이세팅</font></th>
			<td>
				<input type="checkbox" name="length" id="length" value="length" <%if(ps.isLength()) { %> checked="checked" <%} %>>
				<input type="text" name="range" id="range" value="<%=ps.getPrange() != null ? ps.getPrange() : 6 %>" class='AXInput'  <%if(!ps.isLength()) {%>style="display: none;"<%} %>>
			</td>	
		</tr>	
		<tr>
			<th><font class="req">재설정기간</font></th>
			<td>
				<input type="radio" name="reset" value="3" <%if(ps.getReset() == 3) {%>checked="checked"  <%} %>>3개월
				<input type="radio" name="reset" value="6" <%if(ps.getReset() == 6) {%>checked="checked"  <%} %>>6개월
			</td>
		</tr>		
	</table>
	
	<table class="btn_table">
		<tr>
			<td class="center">
				<input type="button" value="변경" id="changeSetBtn" title="변경"> 
				<input type="button" value="뒤로" id="backBtn" title="뒤로" class="blueBtn">
			</td>
		</tr>
	</table>
</td>