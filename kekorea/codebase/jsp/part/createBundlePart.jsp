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
					<i class="axi axi-subtitles"></i><span>부품 일괄 등록</span>
					<!-- req msg -->
					<font class="reqMsg">(빨간색 속성 값은 필수 입력 값입니다.)</font>
				</div>
			</td>
			<td class="right"><input type="button" value="저장" id="createBundlePartBtn" title="부품 일괄 등록"> <!-- <input type="button" value="자가결재" id="createBundlePartBtn" title="자가결재" class="blueBtn">  --> <input type="button" value="취소" id="backBtn" title="취소" class="blueBtn"></td>
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
								// 품번, 품명, 규격, 메이커, 기본구매처, 단위, 단가, 통화
							</script>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<th>첨부파일<span id="fileCount"></span></th>
			<td colspan="1">
				<!-- upload.js see -->
				<div class="AXUpload5" id="allUpload_layer"></div>
				<div class="AXUpload5QueueBox_list" id="uploadQueueBox" style="height: 300px;"></div>
			</td>
		</tr>
	</table> <!-- 	<table class="create_table table_padding border-top-line"> --> <!-- 	<colgroup> --> <!-- 			<col width="200"> --> <!-- 			</colgroup> --> <!-- 	</table>	 -->
</td>