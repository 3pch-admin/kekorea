<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%@include file="/jsp/common/layouts/include_css.jsp"%>
<%@include file="/jsp/common/layouts/include_script.jsp"%>
</head>
<body>
<td valign="top">
	<!-- script area -->
	<script type="text/javascript">
	$(document).ready(function() {
		// init AXUpload5
		$("input").checks();
		
		$(".documents_add_table").tableHeadFixer();
	})
	</script>
	
	<!-- create header title -->
	
	
	<table class="btn_table">
		<tr>
			<td>
				<div class="header_title">
					<i class="axi axi-subtitles"></i><span>도면 결재</span>
					<!-- req msg -->
					<font class="reqMsg">(빨간색 속성 값은 필수 입력 값입니다.)</font>
				</div>
			</td>
			<td class="right">
				<input type="button" value="등록" id="createEpmAppBtn" title="등록"> 
				<input type="button" value="뒤로" id="backBtn" title="뒤로" class="blueBtn">
			</td>
		</tr>
	</table>
	
<!-- 	<table class="create_table"> -->
	<table class="approval_table"> 
		<!-- colgroup -->
		<colgroup>
			<col width="150">
			<col>
		</colgroup>
		<tr>
			<th><font class="req">결재 제목</font></th>
			<td>
				<input type="text" name="name" id="name" class="AXInput wid400">
			</td>
		</tr>
		<!-- 결재 -->
<%-- 		<jsp:include page="/jsp/common/appLine.jsp"> --%>
<%-- 			<jsp:param value="true" name="required" /> --%>
<%-- 		</jsp:include>		 --%>
		<tr>
			<th><font class="req">결재</font></th>
			<td colspan="3">
				<table class="in_btn_table">
					<tr>
						<td class="add">
							<input type="button" value="결재선 지정"  class="greenBtn" >
							<input type="button" value="결재선 삭제"  class="blueBtn">
						</td>
					</tr>
				</table>
				<table id="tblBackground">
					<tr>
                		<td>
							<div id="app_container">
								<table id="create_series_table" class="create_table_in create_series_table fix_table">
									<colgroup>
										<col width="40">
										<col width="100">
										<col width="250">
										<col width="250">
										<col width="250">
										<col width="250">
										<col width="250">
									</colgroup>
									<thead>
										<tr>
											<th>
												<input type="checkbox" name="allDocuments" id="allDocuments">
											</th>
											<th>순서</th>
											<th>결제타입</th>
											<th>이름</th>
											<th>아이디</th>
											<th>직급</th>
											<th>부서</th>
										</tr>						
									</thead>
									<tbody id="addLineBody_series">
										<tr id="nodataSeriesLine">
											<td class="nodata" colspan="7">지정된 결재라인이 없습니다.</td>
										</tr>
									</tbody>	
								</table>
							</div>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<th><font class="req">결재 도면</font></th>
			<td colspan="3">
				<table class="in_btn_table">
					<tr>
						<td class="add">
							<input type="button" title="도면 추가" value="도면 추가" id="addEpms" data-context="product" data-dbl="true" data-state="INWORK">
							<input type="button" title="도면 삭제" value="도면 삭제" id="delEpms" class="blueBtn">
						</td>
					</tr>
				</table>	
		        <table id="tblBackground">
		            <tr>
		                <td>				
							<div id="epms_container">
								<table class="create_table_in documents_add_table fix_table">
									<colgroup>
										<col width="40">
										<col width="300">
										<col width="300">
										<col width="300">
										<col width="80">
										<col width="80">
										<col width="100">
										<col width="130">
									</colgroup>
									<thead>
										<tr>
											<th>
												<input type="checkbox" name="allEpms" id="allEpms">
											</th>
											<th>NAME</th>
											<th>DWG_NO</th>
											<th>NAME_OF_PARTS</th>
											<th>버전</th>
											<th>상태</th>
											<th>작성자</th>
											<th>수정자</th>
										</tr>						
									</thead>
									<tbody id="addEpmsBody">
										<tr id="nodataEpms">
											<td class="nodata" colspan="8">결재 도면이 없습니다.</td>
										</tr>
									</tbody>						
								</table>
							</div>					
						</td>
					</tr>
				</table>	
			</td>
		</tr>
		<tr>
			<th>결재의견</th>
			<td colspan="3">
				<!-- rows 세로, cols 가로 -->
				<textarea class="AXTextarea" name="comment" id="comment" rows="3" cols=""></textarea>
			</td>			
		</tr>				
	</table>
</td>
</body>
</html>