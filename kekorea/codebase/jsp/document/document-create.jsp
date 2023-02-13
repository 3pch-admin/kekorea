<%@page import="e3ps.doc.service.DocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
// module root
String root = DocumentHelper.ROOT;
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>문서 등록</title>
<%@include file="/jsp/common/layouts/include_css.jsp"%>
<%@include file="/jsp/common/layouts/include_script.jsp"%>
</head>
<body>
	<!-- script area -->
	<script type="text/javascript">
		$(document).ready(function() {
			// init AXUpload5
			upload.pageStart(null, null, "all");

			$("input").checks();

			$(".documents_add_table").tableHeadFixer();
		})
	</script>

	<!-- create header title -->

	<table class="btn_table">
		<tr>
			<td>
				<div class="header_title">
					<i class="axi axi-subtitles"></i>
					<span>문서 등록</span>
					<!-- req msg -->
					<font class="reqMsg">(빨간색 속성 값은 필수 입력 값입니다.)</font>
				</div>
			</td>
			<td class="right">
				<input type="button" value="등록" id="createDocBtn" title="등록" data-self="false">
				<input type="button" value="자가결재" id="createSelfDocBtn" title="자가결재" class="blueBtn" data-self="true">
				<input type="button" value="닫기" id="closeBtn" title="닫기" class="blueBtn" onclick="self.close();">
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
			<th>
				<font class="req">저장위치</font>
			</th>
			<td colspan="3">
				<input type="hidden" name="location" id="location">
				<span class="location" id="locationStr">
					<span class="locText"><%=root%></span>
				</span>
				&nbsp;&nbsp;
				<input type="button" data-popup="true" data-context="PRODUCT" data-root="/Default/문서" title="폴더선택" class="openLoc" value="폴더선택">
			</td>
		</tr>
		<tr>
			<th>
				<font class="req">문서제목</font>
			</th>
			<td>
				<input type="text" name="name" id="name" class="AXInput wid300">
			</td>
			<th>문서번호</th>
			<td>
				<input type="text" name="number" id="number" class="AXInput wid200" readonly="readonly">
			</td>
		</tr>
		<tr>
			<th>
				설명
				<br>
				<span id="descDocCnt">0</span>
				/1000
			</th>
			<td colspan="3">
				<!-- rows 세로, cols 가로 -->
				<textarea class="AXTextarea" name="descriptionDoc" id="descriptionDoc" rows="3" cols=""></textarea>
			</td>
		</tr>
		<tr>
			<th>관련부품</th>
			<td colspan="3">
				<table class="in_btn_table">
					<tr>
						<td class="add">
							<input type="button" value="부품 추가" title="부품 추가" id="addParts" data-context="product" data-dbl="true">
							<input type="button" value="부품 삭제" title="부품 삭제" id="delParts" class="blueBtn">
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
										<col width="200">
										<col width="350">
										<col width="400">
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
											<th>수정지</th>
										</tr>
									</thead>
									<tbody id="addPartsBody">
										<tr id="nodataParts">
											<td class="nodata" colspan="8">관련 부품이 없습니다.</td>
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
			<th>
				첨부파일
				<span id="fileCount"></span>
			</th>
			<td colspan="3">
				<!-- upload.js see -->
				<div class="AXUpload5" id="allUpload_layer"></div>
				<div class="AXUpload5QueueBox_list" id="uploadQueueBox"></div>
			</td>
		</tr>

		<!-- 결재 -->
		<jsp:include page="/jsp/common/appLine.jsp">
			<jsp:param value="false" name="required" />
		</jsp:include>
	</table>
</body>
</html>