<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@page import="e3ps.doc.service.DocumentHelper"%>
<td valign="top">
	<!-- script area -->
	<script type="text/javascript">
	 $(document).ready(function() {
		 var spec = document.getElementById('spec');
		 spec.onkeypress = function() {
			$("input[name=check]").val("false");
		};
	 }) 
	</script>
	
	<!-- create header title -->
	<table class="btn_table">
		
		
		<!-- create button -->
	
		<tr>
			<td>
				<div class="header_title">
					<i class="axi axi-subtitles"></i><span>UNIT BOM 등록</span>
					<!-- req msg -->
					<font class="reqMsg">(빨간색 속성 값은 필수 입력 값입니다.)</font>
				</div>
			</td>
			<td class="right">
				<input type="button" value="저장" id="createUnitBomBtn" title="UNIT BOM 등록" >
			<!-- 	<input type="button" value="자가결재" id="createPartListBtn" title="자가결재" class="blueBtn">  -->
				<input type="button" value="취소" id="backBtn" title="취소" class="blueBtn">
			</td>
		</tr>
	</table>
	
	<!-- create table -->
	<input type="hidden" name="check" id="check" value="false"/>
	<table class="create_table">
		<!-- colgroup -->
		<colgroup>
			<col width="200">
			<col>
			<col width="200">
			<col>
		</colgroup>
		<tr>
<!-- 			<th><font class="req">품번</font></th> -->
<!-- 			<td> -->
<!-- 				<input type="text" name="partNo" id="partNo" class="AXInput wid300"> -->
<!-- 			</td> -->
			<th><font class="req">품명</font></th>
			<td>
				<input type="text" name="partName" id="partName" class="AXInput wid300">
			</td>			
			<th><font class="req">기준단위</font></th>
			<td>
				<input type="text" name="unit" id="unit" class="AXInput wid300">
			</td>	
		</tr>
		<tr>
			<th><font class="req">규격</font></th>
			<td>
				<input type="text" name="spec" id="spec" class="AXInput wid300">
				<input type="button" value="중복확인" id="erpCheck" title="중복확인" >
			</td>
			<th>메이커</th>
			<td>
				<input type="text" name="maker" id="maker" class="AXInput wid300">
			</td>
		</tr>
		<tr>
			<th><font class="req">통화</font></th>
			<td>
				<input type="text" name="currency" id="currency" class="AXInput wid300">
			</td>
			<th>기본구매처</th>
			<td>
				<input type="text" name="customer" id="customer" class="AXInput wid300">
			</td>			
		</tr>
<!-- 		<tr> -->
<!-- 			<th><font class="req">단가</font></th> -->
<!-- 			<td><input type="text" name="price" id="price" class="AXInput wid300"></td>	 -->
<!-- 			<th></th> -->
<!-- 			<td></td>	 -->
<!-- 		</tr> -->
	</table>
	
	<table class="create_table no-border">
		<tr>
			<td class="no-border">
		        <table id="tblBackground">
	        	    <tr>
		                <td>
						<div id="spreadsheet2"></div>
						<script>
						
						var jexcels = jexcel(document.getElementById('spreadsheet2'), {
							rowResize:false,
						    columnDrag:false,
// 						    onpaste : partlists.checkPartNumber,
						    onpaste : partlists.checkERP,
						    onchange : partlists.changedCheckERP,
						    columns: [
						    	{ type: 'text', title:'체크', width:40, readOnly:true },
						        { type: 'text', title:'LOT_NO', width:60 },
						        { type: 'text', title:'UNIT_NAME', width:130 },
						        { type: 'text', title:'부품번호', width:100 },
						        { type: 'text', title:'부품명', width:240, readOnly:true },
						        { type: 'text', title:'규격', width:300, readOnly:true },
						        { type: 'text', title:'MAKER', width:100 },
						        { type: 'text', title:'거래처', width:100 },
						        { type: 'text', title:'수량', width:40 },
						        { type: 'text', title:'단위', width:40, readOnly:true },
						        { type: 'text', title:'단가', width:90, readOnly:true },
						        { type: 'text', title:'화폐', width:50, readOnly:true },
						        { type: 'text', title:'원화금액', width:100, readOnly:true },
						        { type: 'text', title:'수배일자', width:80, readOnly:true },
						        { type: 'text', title:'환율', width:60, readOnly:true },
						        { type: 'text', title:'참고도면', width:100 },
						        { type: 'text', title:'조달구분', width:100 },
						        { type: 'text', title:'비고', width:150 },
						     ]
						});
						</script>		
						</td>
					</tr>
				</table>
			</td>
		</tr>		
	</table>		
</td>