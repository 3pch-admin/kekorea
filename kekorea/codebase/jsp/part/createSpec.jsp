<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@page import="e3ps.doc.service.DocumentHelper"%>
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
		
		
		<!-- create button -->
	
		<tr>
			<td>
				<div class="header_title">
					<i class="axi axi-subtitles"></i><span>제작사양서 등록</span>
					<!-- req msg -->
					<font class="reqMsg">(빨간색 속성 값은 필수 입력 값입니다.)</font>
				</div>
			</td>
			<td class="right">
				<input type="button" value="저장" id="createProductSpecBtn" title="제품 사양서 등록" >
				<!-- <input type="button" value="자가결재" id="createBundlePartBtn" title="자가결재" class="blueBtn">  -->
				<input type="button" value="취소" id="backBtn" title="취소" class="blueBtn">
			</td>
		</tr>
	</table>
	
	<!-- create table -->
	<table class="create_table">
		<colgroup>
			<col width="100">
			<col>
			<col width="100">
			<col>
		</colgroup>
		<tr>
			<td colspan="4" class="border-left-line">
		        <table id="tblBackground">
	        	    <tr>
		                <td>
						<div id="spreadsheet"></div>
						<script>
						
						var jexcels = jexcel(document.getElementById('spreadsheet'), {
							rowResize:false,
						    columnDrag:false,
						    tableOverflow:true,
							onchange : parts.getData,
						    columns: [
								{ type: 'text', title:'체크(DWG_NO)', width:80, readOnly:true },
								{ type: 'text', title:'체크(YCODE)', width:80, readOnly:true },
						    	{ type: 'text', title:'품번', width:60 },
						    	{ type: 'text', title:'품명', width:100 },
						    	{ type: 'text', title:'규격', width:300 },
						    	{ type: 'text', title:'메이커', width:80 },
						    	{ type: 'text', title:'기본구매처', width:80 },
						    	{ type: 'text', title:'기준단위', width:50 },
						    	{ type: 'text', title:'단가', width:80, mask : "#,##" },
						    	{ type: 'text', title:'통화', width:50 },
						     ]
						});
						</script>			
						</td>
					</tr>
				</table>
			</td>
		</tr>	
		<tr>
			<th>제작 사양서</th>
			<td colspan="3">
				<table class="in_btn_table">
					<tr>
						<td class="add"><input type="button" title="제작사양서 추가(NEW)" value="제작사양서 추가(NEW)" id="addDocuments" data-context="product" data-dbl="true" data-state="APPROVED" data-location="SPEC">
						<input type="button" title="제작사양서 추가(OLD)" value="제작사양서 추가(OLD)" id="addOldDocuments" data-context="product" data-dbl="true" data-state="APPROVED" data-location="OLDSPEC"> 
						<input type="button" title="제작사양서 삭제" value="제작사양서 삭제" id="delDocuments" class="blueBtn"></td>
					</tr>
				</table>
		        <table id="tblBackground">
		            <tr>
		                <td>				
							<div id="documents_container">
								<table class="create_table_in documents_add_table fix_table">
									<colgroup>
										<col width="40">
										<col width="200">
										<col width="600">
										<col width="100">
										<col width="100">
										<col width="100">
										<col width="100">
									</colgroup>
									<thead>
										<tr>
											<th><input type="checkbox" name="allDocuments" id="allDocuments"></th>
											<th>문서번호</th>
											<th>문서제목</th>
											<th>버전</th>
											<th>상태</th>
											<th>수정자</th>
											<th>수정일</th>
										</tr>
									</thead>
									<tbody id="addDocumentsBody">
										<tr id="nodataDocuments">
											<td class="nodata" colspan="7">관련 제작사양서가 없습니다.</td>
										</tr>
									</tbody>
								</table>
							</div>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</td>