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
					<i class="axi axi-subtitles"></i><span>UNIT BOM ���</span>
					<!-- req msg -->
					<font class="reqMsg">(������ �Ӽ� ���� �ʼ� �Է� ���Դϴ�.)</font>
				</div>
			</td>
			<td class="right">
				<input type="button" value="����" id="createUnitBomBtn" title="UNIT BOM ���" >
			<!-- 	<input type="button" value="�ڰ�����" id="createPartListBtn" title="�ڰ�����" class="blueBtn">  -->
				<input type="button" value="���" id="backBtn" title="���" class="blueBtn">
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
<!-- 			<th><font class="req">ǰ��</font></th> -->
<!-- 			<td> -->
<!-- 				<input type="text" name="partNo" id="partNo" class="AXInput wid300"> -->
<!-- 			</td> -->
			<th><font class="req">ǰ��</font></th>
			<td>
				<input type="text" name="partName" id="partName" class="AXInput wid300">
			</td>			
			<th><font class="req">���ش���</font></th>
			<td>
				<input type="text" name="unit" id="unit" class="AXInput wid300">
			</td>	
		</tr>
		<tr>
			<th><font class="req">�԰�</font></th>
			<td>
				<input type="text" name="spec" id="spec" class="AXInput wid300">
				<input type="button" value="�ߺ�Ȯ��" id="erpCheck" title="�ߺ�Ȯ��" >
			</td>
			<th>����Ŀ</th>
			<td>
				<input type="text" name="maker" id="maker" class="AXInput wid300">
			</td>
		</tr>
		<tr>
			<th><font class="req">��ȭ</font></th>
			<td>
				<input type="text" name="currency" id="currency" class="AXInput wid300">
			</td>
			<th>�⺻����ó</th>
			<td>
				<input type="text" name="customer" id="customer" class="AXInput wid300">
			</td>			
		</tr>
<!-- 		<tr> -->
<!-- 			<th><font class="req">�ܰ�</font></th> -->
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
						    	{ type: 'text', title:'üũ', width:40, readOnly:true },
						        { type: 'text', title:'LOT_NO', width:60 },
						        { type: 'text', title:'UNIT_NAME', width:130 },
						        { type: 'text', title:'��ǰ��ȣ', width:100 },
						        { type: 'text', title:'��ǰ��', width:240, readOnly:true },
						        { type: 'text', title:'�԰�', width:300, readOnly:true },
						        { type: 'text', title:'MAKER', width:100 },
						        { type: 'text', title:'�ŷ�ó', width:100 },
						        { type: 'text', title:'����', width:40 },
						        { type: 'text', title:'����', width:40, readOnly:true },
						        { type: 'text', title:'�ܰ�', width:90, readOnly:true },
						        { type: 'text', title:'ȭ��', width:50, readOnly:true },
						        { type: 'text', title:'��ȭ�ݾ�', width:100, readOnly:true },
						        { type: 'text', title:'��������', width:80, readOnly:true },
						        { type: 'text', title:'ȯ��', width:60, readOnly:true },
						        { type: 'text', title:'������', width:100 },
						        { type: 'text', title:'���ޱ���', width:100 },
						        { type: 'text', title:'���', width:150 },
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