<%@page import="e3ps.doc.service.DocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<td valign="top">
	<!-- script area -->
	<script type="text/javascript">
	$(document).ready(function() {
		// init AXUpload5
// 		upload.pageStart(null, null, "all");
		upload.pageStart(null, null, "primary");
		
		$("input").checks();
	})
	</script>
	
	<!-- create header title -->
	
	<table class="btn_table">
		<tr>
			<td>
				<div class="header_title">
				<i class="axi axi-subtitles"></i><span>뷰어 등록</span>
				<!-- req msg -->
				<font class="reqMsg">(빨간색 속성 값은 필수 입력 값입니다.)</font>
			</div>
			</td>
			<td class="right">
				<input type="button" value="등록" id="createViewerBtn" title="등록" data-self="false"> 
				<input type="button" value="뒤로" id="backBtn" title="뒤로" class="blueBtn">
			</td>
		</tr>
	</table>
	

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
			<th><font class="req">품명</font></th>
			<td>
				<input type="text" name="name" id="name" class="AXInput wid300">
			</td>
			<th><font class="req">규격</font></th>
			<td>
				<input type="text" name="number" id="number" class="AXInput wid200">
			</td>			
		</tr>
		<tr>
			<th><font class="req">파일이름</font></th>
			<td colspan="3">
				<input type="text" name="fileName" id="fileName" class="AXInput wid300">
			</td>			
		</tr>		
		<tr>
			<th><font class="req">첨부파일</font><span id="fileCount"></span></th>
			<td colspan="3">
				<!-- upload.js see -->
					<div class="AXUpload5" id="primary_layer"></div>
<!-- 				<div class="AXUpload5" id="allUpload_layer"></div> -->
<!-- 				<div class="AXUpload5QueueBox_list" id="uploadQueueBox"></div> -->
			</td>
		</tr>
	</table>
</td>