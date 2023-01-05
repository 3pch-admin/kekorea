<%@page import="e3ps.doc.service.DocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<td valign="top">
	<!-- script area -->
	<script type="text/javascript">
	$(document).ready(function() {
		
		$("input").checks();
		
	})
	</script>
	
	<input type="hidden" name="items" id="items">
	
	<!-- create header title -->
	<div class="header_title">
		<i class="axi axi-subtitles"></i><span>도면 출력</span>
		<!-- req msg -->
	</div>


	<!-- create table -->
	<table class="create_table">
		<!-- colgroup -->
		<colgroup>
			<col width="150">
			<col>
		</colgroup>
		<tr>
<!-- 			<th>출력 도면</th> -->
			<td class="border-left">		
				<table class="in_btn_table">
					<colgroup>
						<col width="50%">
						<col width="50%">
					</colgroup>
					<tr>
						<td class="add left">
							 <input type="button" value="도면 추가" class="" title="도면 추가" id="addEpms" data-fun="addPrintEpm" data-context="product" data-dbl="true" data-cadtype="CADDRAWING">
							 <input type="button" value="도면 삭제" class="blueBtn" title="도면 삭제" id="delEpms">
						</td>
						<td class="add right">
							<input type="button" value="DWG ERP I/F" class="redBtn" title="DWG ERP I/F" id="sendERP">
							<input type="button" value="DWG" class="" title="DWG" id="downDWG">
							<input type="button" value="PDF" class="" title="PDF" id="downPDF">
							<input type="button" value="프린트" class="blueBtn" title="프린트" id="printEPM">
						</td>
					</tr>
				</table>
		        <table id="tblBackground">
		            <tr>
		                <td>					
							<div id="prints_container">
								<table class="create_table_in epms_add_table">
									<colgroup>
										<col width="40">
										<col width="100">
										<col width="100">
										<col width="250">
										<col width="*">
										<col width="200">
										<col width="100">
										<col width="100">
									</colgroup>
									<thead>
										<tr>
											<th>
												<input type="checkbox" name="allEpms" id="allEpms">
											</th>
											<th>DWG</th>
											<th>PDF</th>
											<th>파일이름</th>
											<th>품명</th>
											<th>FOLDER</th>
											<th>상태</th>
											<th>버전</th>
										</tr>						
									</thead>
									<tbody id="addEpmsBody">
										<tr id="nodataEpms">
											<td class="nodata" colspan="8">출력할 도면이 없습니다.</td>
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