<%@ page language="java" contentType="text/html; charset=EUC-KR" pageEncoding="EUC-KR"%>
<%@page import="e3ps.doc.service.DocumentHelper"%>
<td valign="top">
	<!-- script area --> <script type="text/javascript">
		$(document).ready(function() {
			// init AXUpload5
			upload.pageStart(null, null, "all");
			$("input").checks();
		})
	</script> <!-- create header title -->
	<table class="btn_table">


		<!-- create button -->

		<tr>
			<td>
				<div class="header_title">
					<i class="axi axi-subtitles"></i><span>��ǰ �ϰ� ���</span>
					<!-- req msg -->
					<font class="reqMsg">(������ �Ӽ� ���� �ʼ� �Է� ���Դϴ�.)</font>
				</div>
			</td>
			<td class="right"><input type="button" value="����" id="createBundlePartBtn" title="��ǰ �ϰ� ���"> <!-- <input type="button" value="�ڰ�����" id="createBundlePartBtn" title="�ڰ�����" class="blueBtn">  --> <input type="button" value="���" id="backBtn" title="���" class="blueBtn"></td>
		</tr>
	</table> <!-- create table -->
	<table class="create_table">
		<colgroup>
			<col width="200">
			<col>
			<col width="200">
			<col>
		</colgroup>
		<tr>
			<td class="border-left-line" colspan="2">
				<table id="tblBackground">

					<tr>
						<td>
							<div id="spreadsheet"></div> <script>
								var jexcels = jexcel(document.getElementById('spreadsheet'), {
									rowResize : false,
									columnDrag : false,
									tableOverflow : true,
									onchange : parts.getData,
// 									oninsertrow : parts.setColumns,
// 									onload : parts.setColumns,
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
								// ǰ��, ǰ��, �԰�, ����Ŀ, �⺻����ó, ����, �ܰ�, ��ȭ
							</script>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<th>÷������<span id="fileCount"></span></th>
			<td colspan="1">
				<!-- upload.js see -->
				<div class="AXUpload5" id="allUpload_layer"></div>
				<div class="AXUpload5QueueBox_list" id="uploadQueueBox" style="height: 300px;"></div>
			</td>
		</tr>
	</table> <!-- 	<table class="create_table table_padding border-top-line"> --> <!-- 	<colgroup> --> <!-- 			<col width="200"> --> <!-- 			</colgroup> --> <!-- 	</table>	 -->
</td>