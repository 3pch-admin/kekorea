<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<td valign="top">
	<!-- script area -->
	<script type="text/javascript">
	$(document).ready(function() {
		// init AXUpload5
		$("input").checks();
		
// 		name nameofparts dwg_no rev
// 		파일명 품명 규격 REV 나머지 그대로

// 		파일명 품명 규격 나머지 그대로
		
		
		$(".documents_add_table").tableHeadFixer();
	})
	</script>
	
	<!-- create header title -->
	
	<table class="btn_table">
		<tr>
			<td>
				<div class="header_title">
					<i class="axi axi-subtitles"></i><span>코드 생성</span>
					<!-- req msg -->
					<font class="reqMsg">(빨간색 속성 값은 필수 입력 값입니다.)</font>
				</div>
			</td>
			<td class="right">
				<input type="button" value="UNIT BOM" id="createUnitBtn" title="UNIT BOM" data-self="true" class="blueBtn">
				<input type="button" value="등록" id="createCodeBtn" title="등록" data-self="true">
<!-- 				<input type="button" value="자가결재" id="" title="자가결재" class="blueBtn" data-self="true"> -->
<!-- 				<input type="button" value="뒤로" id="backBtn" title="뒤로" class="blueBtn"> -->
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
		<!-- <tr>
			<th>결재 부품</th>
			<td colspan="3">
				<table class="in_btn_table">
					<tr>
						<td class="add">
							<input type="button" title="부품 추가" value="부품 추가" id="addParts" data-context="product" data-dbl="true" data-state="INWORK">
							<input type="button" title="부품 삭제" value="부품 삭제" id="delParts" class="blueBtn">
						</td>
					</tr>
				</table>	
		        <table id="tblBackground">
		            <tr>
		                <td>				
							<div>
								<table class="create_table_in documents_add_table fix_table">
									<colgroup>
										<col width="40">
										<col width="200">
										<col width="300">
										<col width="300">
										<col width="80">
										<col width="80">
										<col width="100">
										<col width="100">
									</colgroup>
									<thead>
										<tr>
											<th>
												<input type="checkbox" name="allParts" id="allParts">
											</th>
											<th>DWG_NO</th>
											<th>NAME</th>
											<th>NAME_OF_PARTS</th>
											<th>버전</th>
											<th>상태</th>
											<th>작성자</th>
											<th>수정자</th>
										</tr>						
									</thead>
									<tbody id="addPartsBody">
										<tr id="nodataParts">
											<td class="nodata" colspan="8">결재 부품이 없습니다.</td>
										</tr>
									</tbody>						
								</table>
							</div>					
						</td>
					</tr>
				</table>	
			</td>
		</tr> -->
		<tr>
			<th>결재 도면</th>
			<td colspan="3">
				<table class="in_btn_table">
					<tr>
						<td class="add">
<!-- 							<input type="button" title="도면 추가" value="도면 추가" id="addEpms" data-fun="codeEpm" data-context="product" data-dbl="true" data-state="INWORK" data-cadtype=""> -->
							<input type="button" value="도면 추가" title="도면 추가" id="addParts" data-context="product" data-changeable="false" data-dbl="true" data-fun="codeParts" data-state="INWORK">
<!-- 							<input type="button" title="도면 삭제" value="도면 삭제" id="delCodeEpms" class="blueBtn"> -->
							<input type="button" value="도면 삭제" title="도면 삭제" id="delParts" class="blueBtn">
						</td>
					</tr>
				</table>	
				 <table id="tblBackground">
		            <tr>
		                <td>
							<div id="parts_container">
								<table class="create_table_in documents_add_table fix_table">
									<colgroup>
										<col width="40">
										<col width="350">
										<col width="350">
										<col width="200">
<!-- 										<col width="300"> -->
										<col width="80">
										<col width="80">
										<col width="100">
										<col width="100">
									</colgroup>
									<thead>
										<tr>
											<th>
												<input type="checkbox" name="allParts" id="allParts">
											</th>
											<!-- <th>DWG_NO</th>
											<th>NAME</th>
											<th>NAME_OF_PARTS</th> -->
											<th>파일명</th>
											<th>품명</th>
											<th>규격</th>
<!-- 											<th>REV</th> -->
											<th>버전</th>
											<th>상태</th>
											<th>작성자</th>
											<th>수정자</th>
										</tr>						
									</thead>
									<tbody id="addPartsBody">
										<tr id="nodataParts">
											<td class="nodata" colspan="8">관련 도면이 없습니다.</td>
										</tr>
									</tbody>						
								</table>
							</div>
						</td>
					</tr>
				</table>
		       <!--  <table id="tblBackground">
		            <tr>
		                <td>				
							<div id="epms_container">
								<table class="create_table_in documents_add_table fix_table">
									<colgroup>
										<col width="40">
										<col width="300">
										<col width="300">
										<col width="300">
										<col width="300">
										<col width="80">
										<col width="80">
										<col width="100">
										<col width="100">
									</colgroup>
									<thead>
										<tr>
											<th>
												<input type="checkbox" name="allEpms" id="allEpms">
											</th>
											<th>DWG_NO</th>
											<th>REV</th>
											<th>NAME</th>
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
				</table>	 -->
			</td>
		</tr>		
		<tr>
			<th>결재 라이브러리</th>
			<td colspan="3">
				<table class="in_btn_table">
					<tr>
						<td class="add">
								<input type="button" value="라이브러리 추가" title="라이브러리 추가" id="addLibraryParts" data-context="library" data-changeable="false" data-fun="codeLibrary" data-dbl="true" data-state="APPROVED">
							<input type="button" title="라이브러리 삭제" value="라이브러리 삭제" id="delLibrarys" class="blueBtn">
						</td>
					</tr>
				</table>	
		        <table id="tblBackground">
		            <tr>
		                <td>				
							<div id="librarys_container">
								<table class="create_table_in documents_add_table fix_table">
									<colgroup>
										<col width="40">
										<col width="300">
										<col width="300">
										<col width="300">
										<col width="80">
										<col width="80">
										<col width="100">
										<col width="100">
									</colgroup>
									<thead>
										<tr>
											<th>
												<input type="checkbox" name="allLibrarys" id="allLibrarys">
											</th>
											<th>파일명</th>
											<th>품명</th>
											<th>규격</th>
											<th>버전</th>
											<th>상태</th>
											<th>작성자</th>
											<th>수정자</th>
										</tr>						
									</thead>
									<tbody id="addLibrarysBody">
										<tr id="nodataLibrarys">
											<td class="nodata" colspan="8">결재 라이브러리가 없습니다.</td>
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
			<th>결재 UNITBOM</th>
			<td colspan="3">
				<table class="in_btn_table">
					<tr>
						<td class="add">
							<input type="button" title="UNITBOM 추가" value="UNITBOM 추가" id="addUnits" data-context="product" data-dbl="true" data-state="INWORK">
							<input type="button" title="UNITBOM 삭제" value="UNITBOM 삭제" id="delUnits" class="blueBtn">
						</td>
					</tr>
				</table>	
		        <table id="tblBackground">
		            <tr>
		                <td>				
							<div id="units_container">
								<table class="create_table_in documents_add_table fix_table">
									<colgroup>
										<col width="40">
										<col width="300">
										<col width="300">
										<col width="300">
										<col width="200">
										<col width="80">
										<col width="80">
										<col width="100">
										<col width="130">
									</colgroup>
									<thead>
										<tr>
											<th>
												<input type="checkbox" name="allUnits" id="allUnits">
											</th>
											<th>품번</th>
											<th>품명</th>
											<th>규격</th>
											<th>기준단위</th>
											<th>메이커</th>
											<th>기본구매처</th>
											<th>통화</th>
											<th>단가</th>
										</tr>						
									</thead>
									<tbody id="addUnitsBody">
										<tr id="nodataUnits">
											<td class="nodata" colspan="9">결재 UNITBOM이 없습니다.</td>
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
<%-- 		<jsp:include page="/jsp/common/appLine.jsp"> --%>
<%-- 			<jsp:param value="true" name="required" /> --%>
<%-- 		</jsp:include>	 --%>
	</table>
	
	
</td>