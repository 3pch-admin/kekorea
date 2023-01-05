<%@page import="wt.org.WTUser"%>
<%@page import="e3ps.document.service.DocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<td valign="top">
	<!-- script area -->
	<script type="text/javascript">
	$(document).ready(function() {
		
	})
	</script>
	
	<!-- create header title -->
	<div class="header_title">
		<i class="axi axi-subtitles"></i><span>비밀번호 초기화</span>
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
			<th><font class="req">초기화 유저 아이디</font></th>
			<td>
				<input type="text" name="creators" id="creators" class="AXInput wid250" data-dbl="true"> 
				<i title="삭제" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="creators"></i>
			</td>
		</tr>
	</table>
	
	<table class="btn_table">
		<tr>
			<td class="center">
				<input type="button" value="초기화" id="initPasswordBtn" title="초기화"> 
				<input type="button" value="뒤로" id="backBtn" title="뒤로" class="blueBtn">
			</td>
		</tr>
	</table>
</td>