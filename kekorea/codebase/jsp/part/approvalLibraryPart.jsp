<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<td valign="top">
	<!-- script area -->
	<script type="text/javascript">
	$(document).ready(function() {
		// init AXUpload5
		$("input").checks();
		
		$(".parts_add_table").tableHeadFixer();
	})
	</script>
	
	<!-- create header title -->
	<div class="header_title">
		<i class="axi axi-subtitles"></i><span>구매품 결재</span>
		<!-- req msg -->
		<font class="reqMsg">(빨간색 속성 값은 필수 입력 값입니다.)</font>
	</div>
	
	<table class="create_table">
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
			<th><font class="req">결재 구매품</font></th>
			<td colspan="3">
				<table class="in_btn_table">
					<tr>
						<td class="add">
							<input type="button" value="구매품 추가" title="구매품 추가" id="addParts" data-changeable="false" data-context="library" data-dbl="true" data-state="INWORK">
							<input type="button" value="구매품 삭제" title="구매품 삭제" id="delParts" class="blueBtn">
						</td>
					</tr>
				</table>	
		        <table id="tblBackground">
		            <tr>
		                <td>						
							<div id="parts_container">
								<table class="create_table_in parts_add_table fix_table">
									<colgroup>
										<col width="40">
										<col width="200">
										<col width="200">
										<col width="200">
										<col width="150">
										<col width="150">							
										<col width="80">
										<col width="80">
										<col width="100">
										<col width="130">
									</colgroup>
									<thead>
										<tr>
											<th>
												<input type="checkbox" name="allParts" id="allParts">
											</th>
											<th>파일이름</th>
											<th>SPEC</th>
											<th>PRODUCT_NAME</th>
											<th>MAKER</th>
											<th>MASTER_TYPE</th>
											<th>버전</th>
											<th>상태</th>
											<th>수정자</th>
											<th>수정일</th>
										</tr>						
									</thead>
									<tbody id="addPartsBody">
										<tr id="nodataParts">
											<td class="nodata" colspan="10">결재 할 구매품이 없습니다.</td>
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
	
	<table class="btn_table">
		<tr>
			<td class="center">
				<input type="button" value="등록" id="createLibraryPartAppBtn" title="등록"> 
				<input type="button" value="뒤로" id="backBtn" title="뒤로" class="blueBtn">
			</td>
		</tr>
	</table>
</td>