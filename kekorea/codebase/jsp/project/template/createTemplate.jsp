
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<td valign="top">
	<!-- script area -->
	<script type="text/javascript">
	$(document).ready(function() {
		$("input").checks();
		
		$(".documents_add_table").tableHeadFixer();
	})
	</script>
	
	<!-- create header title -->
	
	<table class="btn_table">
			
		<tr>
			<td>
				<div class="header_title">
					<i class="axi axi-subtitles"></i><span>템플릿 등록</span>
					<!-- req msg -->
					<font class="reqMsg">(빨간색 속성 값은 필수 입력 값입니다.)</font>
				</div>
			</td>
			<td class="right">
<!-- 				<input type="button" value="수정" id="modifyTemplateBtn" title="수정">  -->
				<input type="button" value="등록" id="createTemplateBtn" title="등록" class="blueBtn"> 
				<input type="button" value="취소" id="backBtn" title="뒤로" class="redBtn">
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
			<th><font class="req">템플릿 이름</font></th>
			<td colspan="3">
				<input type="text" name="name" id="name" class="AXInput wid300">
			</td>
		</tr>	
<!-- 		<tr> -->
<!-- 			<th>총기간</th> -->
<!-- 			<td colspan="3"> -->
<!-- 				<input type="text" name="duration" id="duration" class="AXInput wid100"> 일 -->
<!-- 			</td> -->
<!-- 		</tr> -->
		<tr>
			<th>참조 템플릿</th>
			<td colspan="3">
				<table class="in_btn_table">
					<tr>
						<td class="add">
							<input type="button" value="템플릿 추가" title="템플릿 추가" id="addTemplate" data-dbl="true">
							<input type="button" value="템플릿 삭제" title="템플릿 삭제" id="delTemplate" class="blueBtn">
						</td>
					</tr>
				</table>
		        <table id="tblBackground">
		            <tr>
		                <td>
							<div id="template_container">
								<table class="create_table_in documents_add_table fix_table">
									<colgroup>
<!-- 										<col width="40"> -->
										<col width="*">
										<col width="150">
										<col width="150">
										<col width="150">
									</colgroup>
									<thead>
										<tr>
<!-- 											<th> -->
<!-- 												<input type="checkbox" name="allTemplate" id="allTemplate"> -->
<!-- 											</th> -->
											<th>템플릿 이름</th>
											<th>총기간</th>
											<th>등록일</th>
											<th>수정일</th>
										</tr>						
									</thead>
									<tbody id="addTemplateBody">
										<tr id="nodataTemplate">
											<td class="nodata" colspan="4">관련 템플릿이 없습니다.</td>
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
			<th>설명<br><span id="descTemplateCnt">0</span>/1000</th>
			<td colspan="3">
				<!-- rows 세로, cols 가로 -->
				<textarea class="AXTextarea" name="descriptionTemp" id="descriptionTemp" rows="3" cols=""></textarea>
			</td>			
		</tr>
<!-- 			
		<tr>
			<th>사용여부</th>
			<td>
				<input type="checkbox" name="enable" id="enable" value="true" checked="checked">
			</td>
		</tr> -->		
					
			
		
		<!-- 결재 -->
<%-- 		<jsp:include page="/jsp/common/appLine.jsp"> --%>
<%-- 			<jsp:param value="false" name="required" /> --%>
<%-- 		</jsp:include> --%>
	</table>
	
	
</td>