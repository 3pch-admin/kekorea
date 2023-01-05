<%@page import="e3ps.document.service.DocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<td valign="top">
	<!-- script area -->
	<script type="text/javascript">
	$(document).ready(function() {
		// init AXUpload5
		upload.pageStart(null, null, "primary");
		
	})
	</script>
	
	<!-- create header title -->
	<div class="header_title">
		<i class="axi axi-subtitles"></i><span>부품 일괄 등록</span>
		<!-- req msg -->
	</div>


	<!-- create table -->
	<table class="create_table">
		<!-- colgroup -->
		<colgroup>
			<col width="200">
			<col>
			<col width="200">
			<col>
		</colgroup>
		<tr>
			<th><font class="req">엑셀파일</font>
			<td colspan="3">
				<!-- upload.js see -->
				<div class="AXUpload5" id="primary_layer"></div>
			</td>
		</tr>
	</table>
	
	<table class="btn_table">
		<tr>
			<td class="center">
				<input type="button" value="부품 일괄 등록" id="createAllPartsBtn" title="부품 일괄 등록"> 
				<input type="button" value="뒤로" id="backBtn" title="뒤로" class="blueBtn">
			</td>
		</tr>
	</table>
</td>