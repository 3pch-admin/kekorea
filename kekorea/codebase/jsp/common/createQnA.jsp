<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<td valign="top">
	<!-- script area -->
	<script type="text/javascript">
	$(document).ready(function() {
		// init AXUpload5
		upload.pageStart(null, null, "secondary");
		
		$("input").checks();
		
	})
	</script>
	
	<!-- create header title -->
	<div class="header_title">
		<i class="axi axi-subtitles"></i><span>QNA 등록</span>
		<!-- req msg -->
		<font class="reqMsg">(빨간색 속성 값은 필수 입력 값입니다.)</font>
	</div>


	<!-- create table -->
	<table class="create_table">
		<!-- colgroup -->
		<colgroup>
			<col width="200">
			<col>
		</colgroup>
		<tr>
			<th><font class="req">QNA 제목</font></th>
			<td>
				<input type="text" name="name" id="name" class="AXInput wid300">
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
			<th>첨부파일<span id="fileCount"></span></th>
			<td colspan="3">
				<!-- upload.js see -->
				<div class="AXUpload5" id="secondary_layer"></div>
				<div class="AXUpload5QueueBox_list" id="uploadQueueBox"></div>
			</td>
		</tr>
	</table>
	
	<table class="btn_table">
		<tr>
			<td class="center">
				<input type="button" value="등록" id="createQnAActionBtn" title="등록"> 
				<input type="button" value="뒤로" id="backBtn" title="뒤로" class="blueBtn">
			</td>
		</tr>
	</table>
</td>