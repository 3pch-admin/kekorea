<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<td valign="top">
	<!-- script area -->
	<script type="text/javascript">
	$(document).ready(function() {
		// init AXUpload5
		$("input").checks();
		
		$(".documents_add_table").tableHeadFixer();
	})
	</script>
	
	<table class="btn_table">
		<tr>
			<td>
				<div class="header_title">
					<i class="axi axi-subtitles"></i><span>문서 결재</span>
					<!-- req msg -->
					<font class="reqMsg">(빨간색 속성 값은 필수 입력 값입니다.)</font>
				</div>
			</td>
			<td class="right">
				<input type="button" value="등록" id="createDocAppBtn" title="등록"> 
				<input type="button" value="뒤로" id="backBtn" title="뒤로" class="blueBtn">
			</td>
		</tr>
	</table>
	
	<!-- create header title -->
	
	
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
		<tr>
			<th><font class="req">결재 문서</font></th>
			<td colspan="3">
				<table class="in_btn_table">
					<tr>
						<td class="add">
							<input type="button" title="문서 추가" value="문서 추가" id="addDocuments" data-context="product" data-dbl="true" data-state="INWORK">
							<input type="button" title="문서 삭제" value="문서 삭제" id="delDocuments" class="blueBtn">
						</td>
					</tr>
				</table>	
		        <table id="tblBackground">
		            <tr>
		                <td>				
							<div id="documents_container">
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
												<input type="checkbox" name="allDocuments" id="allDocuments">
											</th>
											<th>문서번호</th>
											<th>문서제목</th>
											<th>MODEL_NAME</th>
											<th>버전</th>
											<th>상태</th>
											<th>수정자</th>
											<th>수정일</th>
										</tr>						
									</thead>
									<tbody id="addDocumentsBody">
										<tr id="nodataDocuments">
											<td class="nodata" colspan="8">결재 문서가 없습니다.</td>
										</tr>
									</tbody>						
								</table>
							</div>					
						</td>
					</tr>
				</table>	
			</td>
		</tr>		
		<!-- 결재 -->
		<jsp:include page="/jsp/common/appLine.jsp">
			<jsp:param value="true" name="required" />
		</jsp:include>
	</table>
</td>