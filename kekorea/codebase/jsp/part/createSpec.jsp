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
					<i class="axi axi-subtitles"></i><span>���ۻ�缭 ���</span>
					<!-- req msg -->
					<font class="reqMsg">(������ �Ӽ� ���� �ʼ� �Է� ���Դϴ�.)</font>
				</div>
			</td>
			<td class="right">
				<input type="button" value="����" id="createProductSpecBtn" title="��ǰ ��缭 ���" >
				<!-- <input type="button" value="�ڰ�����" id="createBundlePartBtn" title="�ڰ�����" class="blueBtn">  -->
				<input type="button" value="���" id="backBtn" title="���" class="blueBtn">
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
								{ type: 'text', title:'üũ(DWG_NO)', width:80, readOnly:true },
								{ type: 'text', title:'üũ(YCODE)', width:80, readOnly:true },
						    	{ type: 'text', title:'ǰ��', width:60 },
						    	{ type: 'text', title:'ǰ��', width:100 },
						    	{ type: 'text', title:'�԰�', width:300 },
						    	{ type: 'text', title:'����Ŀ', width:80 },
						    	{ type: 'text', title:'�⺻����ó', width:80 },
						    	{ type: 'text', title:'���ش���', width:50 },
						    	{ type: 'text', title:'�ܰ�', width:80, mask : "#,##" },
						    	{ type: 'text', title:'��ȭ', width:50 },
						     ]
						});
						</script>			
						</td>
					</tr>
				</table>
			</td>
		</tr>	
		<tr>
			<th>���� ��缭</th>
			<td colspan="3">
				<table class="in_btn_table">
					<tr>
						<td class="add"><input type="button" title="���ۻ�缭 �߰�(NEW)" value="���ۻ�缭 �߰�(NEW)" id="addDocuments" data-context="product" data-dbl="true" data-state="APPROVED" data-location="SPEC">
						<input type="button" title="���ۻ�缭 �߰�(OLD)" value="���ۻ�缭 �߰�(OLD)" id="addOldDocuments" data-context="product" data-dbl="true" data-state="APPROVED" data-location="OLDSPEC"> 
						<input type="button" title="���ۻ�缭 ����" value="���ۻ�缭 ����" id="delDocuments" class="blueBtn"></td>
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
											<th>������ȣ</th>
											<th>��������</th>
											<th>����</th>
											<th>����</th>
											<th>������</th>
											<th>������</th>
										</tr>
									</thead>
									<tbody id="addDocumentsBody">
										<tr id="nodataDocuments">
											<td class="nodata" colspan="7">���� ���ۻ�缭�� �����ϴ�.</td>
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