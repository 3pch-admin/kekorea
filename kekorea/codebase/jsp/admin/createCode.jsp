<%@page import="java.util.ArrayList"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	ArrayList<String> customer = (ArrayList<String>) request.getAttribute("customer");
%>    
<td valign="top">
	<!-- script area -->
	<script type="text/javascript">
	$(document).ready(function() {
		$("#customer").bindSelect();	
	})
	</script>
	
	<!-- create header title -->
	<div class="header_title">
		<i class="axi axi-subtitles"></i><span>설치장소 생성</span>
		<!-- req msg -->
	</div>


	<!-- create table -->
	<table class="create_table">
		<!-- colgroup -->
		<colgroup>
			<col width="230">
			<col>
		</colgroup>
		<tr>
			<th><font class="req">고객사</font></th>
			<td>
				<select name="customer" id="customer" class="AXSelect">
					<option value="">선택</option>
					<%
						for(String value : customer) {
					%>
					<option value="<%=value %>"><%=value %></option>
					<%
						}
					%>
				</select>
				<font class="req">(고객사 추가는 관리자에게 문의하세요.)</font>
			</td>
		</tr>
		<tr>
			<th><font class="req">설치장소</font></th>
			<td>
				<input type="text" name="install" id="install" class="AXInput wid300">
			</td>	
		</tr>	
	</table>
	
	<table class="btn_table">
		<tr>
			<td class="center">
				<input type="button" value="등록" id="createCode" title="등록"> 
				<input type="button" value="뒤로" id="backBtn" title="뒤로" class="blueBtn">
			</td>
		</tr>
	</table>
</td>