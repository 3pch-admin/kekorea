<%@page import="e3ps.document.service.DocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	// module root
	String root = DocumentHelper.ROOT;
%>    
<td valign="top">
	<!-- script area -->
	<script type="text/javascript">
	$(document).ready(function() {

		// init AXUpload5
		upload.pageStart(null, null, "primary");
		upload.pageStart(null, null, "secondary");
	})
	</script>
	
	
	<!-- create header title -->
	<div class="header_title">
		<i class="axi axi-subtitles"></i><span>문서 등록</span>
		<!-- req msg -->
		<font class="reqMsg">(빨간색 속성 값은 필수 입력 값입니다.)</font>
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
			<th><font class="req">문서분류</font></th>
			<td colspan="3">
				<span class="location" id="location"><%=root %></span>&nbsp;&nbsp;
				<span class="gray button small">
					<span data-popup="true" data-context="PRODUCT" data-root="/Default/문서" title="분류선택" class="openLoc">
					분류선택
					</span>
				</span>
			</td>
		</tr>
		<tr>
			<th><font class="req">문서 제목</font></th>
			<td>
				<input type="text" name="name" id="name" class="AXInput wid300">
			</td>
			<th><font class="req">모델 이름</font></th>
			<td>
				<input type="text" name="model_name" id="model_name" class="AXInput wid300">
			</td>			
		</tr>
		<tr>
			<th>설명<br>0/4000</th>
			<td colspan="3">
				<!-- rows 세로, cols 가로 -->
				<textarea class="AXTextarea" name="description" id="description" rows="3" cols=""></textarea>
			</td>			
		</tr>	
		<tr>
			<th><font class="req">주 첨부파일</font>
			<td colspan="3">
				<!-- upload.js see -->
				<div class="AXUpload5" id="primary_layer"></div>
			</td>
		</tr>
		<tr>
			<th>첨부파일<span id="fileCount"></span></th>
			<th colspan="3">
				<!-- upload.js see -->
				<div class="AXUpload5" id="secondary_layer"></div>
				<div class="AXUpload5QueueBox_list" id="uploadQueueBox"></div>
			</th>
		</tr>
	</table>
</td>