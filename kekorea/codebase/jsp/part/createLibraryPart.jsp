<%@page import="e3ps.part.service.PartHelper"%>
<%@page import="e3ps.document.service.DocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	// module root
	String root = PartHelper.LIBRARY_ROOT;
%>
<td valign="top">
	<!-- script area --> <script type="text/javascript">
		$(document).ready(function() {
			// init AXUpload5
			upload.pageStart(null, null, "primary");
			upload.pageStart(null, null, "secondary");

			$("input").checks();

		})
	</script> <!-- create header title -->
	<div class="header_title">
		<i class="axi axi-subtitles"></i><span>구매품 등록</span>
		<!-- req msg -->
		<font class="reqMsg">(빨간색 속성 값은 필수 입력 값입니다.)</font>
	</div> <!-- create table -->
	<table class="create_table">
		<!-- colgroup -->
		<colgroup>
			<col width="200">
			<col>
			<col width="200">
			<col>
		</colgroup>
		<tr>
			<th><font class="req">저장위치</font></th>
			<td colspan="3"><input type="hidden" name="location" id="location"> <span class="location" id="locationStr"><span class="locText"><%=root%></span></span>&nbsp;&nbsp; <input
				type="button" title="폴더선택" value="폴더선택" data-popup="true" data-context="LIBRARY" data-root="/Default" class="openLoc"></td>
		</tr>
		<tr>
			<th><font class="req">PRODUCT_NAME</font></th>
			<td><input type="text" name="name" id="name" class="AXInput wid300"></td>
			<th><font class="req">SPEC</font></th>
			<td><input type="text" name="SPEC" id="SPEC" class="AXInput wid300"> <input type="button" value="중복체크" title="중복체크" id="erp_duplicate" class="pos2"></td>
		</tr>
		<tr>
			<th><font class="req">자재 소분류</font></th>
			<td colspan="3"><input type="text" name="ItemClassName" id="ItemClassName" class="AXInput wid250"> <input type="hidden" name="ItemClassSeq" id="ItemClassSeq"></td>
		</tr>
		<tr>
			<th>속성</th>
			<td colspan="3">
				<table class="create_table">
					<tr>
						<th>파일이름</th>
						<td><input type="text" name="number" id="number" class="AXInput wid200"></td>
						<th>WEIGHT</th>
						<td><input type="text" name="WEIGHT" id="WEIGHT" class="AXInput wid200"></td>
					</tr>
					<tr>
						<th>MAKER</th>
						<td><input type="text" name="MAKER" id="MAKER" class="AXInput wid200"></td>
						<th>BOM</th>
						<td><input type="text" name="BOM" id="BOM" class="AXInput wid200"></td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<th>도면파일</th>
			<td colspan="3">
				<!-- upload.js see -->
				<div class="AXUpload5" id="primary_layer"></div>
			</td>
		</tr>
		<tr>
			<th>첨부파일<span id="fileCount"></span></th>
			<td colspan="3">
				<!-- upload.js see -->
				<div class="AXUpload5" id="secondary_layer"></div>
				<div class="AXUpload5QueueBox_list" id="uploadQueueBox"></div>
			</td>
		</tr>

		<tr>
			<th>관련 문서</th>
			<td colspan="3">
				<table class="in_btn_table">
					<tr>
						<td class="add"><input type="button" title="문서 추가" value="문서 추가" id="addDocuments" data-context="product" data-dbl="true" data-state="INWORK"> <input type="button" title="문서 삭제"
							value="문서 삭제" id="delDocuments" class="blueBtn"></td>
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
										<col width="250">
										<col width="200">
										<col width="100">
										<col width="100">
										<col width="130">
										<col width="130">
									</colgroup>
									<thead>
										<tr>
											<th><input type="checkbox" name="allDocuments" id="allDocuments"></th>
											<th>문서번호</th>
											<th>문서제목</th>
											<th>MODEL_NAME</th>
											<th>버전</th>
											<th>상태</th>
											<th>수정자</th>
											<th>수정일</th>
										</tr>
									</thead>
									<tbody id="addDocumentsBody">
										<tr id="nodataDocuments">
											<td class="nodata" colspan="8">관련 문서가 없습니다.</td>
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
			<jsp:param value="false" name="required" />
		</jsp:include>
	</table>

	<table class="btn_table">
		<tr>
			<td class="center">
				<input type="button" value="등록" id="createLibraryPartBtn" title="등록"> 
				<input type="button" value="뒤로" id="backBtn" title="뒤로" class="blueBtn">
			</td>
		</tr>
	</table>
</td>