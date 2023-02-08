<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%@include file="/jsp/common/layouts/include_css.jsp"%>
<%@include file="/jsp/common/layouts/include_script.jsp"%>
</head>
<body>
<td valign="top">
	<!-- script area --> <script type="text/javascript">
		$(document).ready(function() {
			// init AXUpload5
			$("input").checks();

			$(".documents_add_table").tableHeadFixer();
		})
	</script> <!-- create header title -->


	<table class="btn_table">
		<tr>
			<td>
				<div class="header_title">
					<i class="axi axi-subtitles"></i><span>EPLAN 결재</span>
					<!-- req msg -->
					<font class="reqMsg">(빨간색 속성 값은 필수 입력 값입니다.)</font>
				</div>
			</td>
			<td class="right"><input type="button" value="등록"
				id="createEplanAppBtn" title="등록"> <input type="button"
				value="뒤로" id="backBtn" title="뒤로" class="blueBtn"></td>
		</tr>
	</table> <!-- 	<table class="create_table"> -->
	<table class="approval_table">
		<!-- colgroup -->
		<colgroup>
			<col width="150">
			<col>
		</colgroup>
		<tr>
			<th><font class="req">결재 제목</font></th>
			<td><input type="text" name="name" id="name"
				class="AXInput wid400"></td>
		</tr>
		<!-- 결재 -->
<%-- 		<jsp:include page="/jsp/common/appLine.jsp"> --%>
<%-- 			<jsp:param value="true" name="required" /> --%>
<%-- 		</jsp:include> --%>
		<tr>
			<th><font class="req">결재</font></th>
			<td colspan="3">
				<table class="in_btn_table">
					<tr>
						<td class="add">
							<input type="button" value="결재선 지정"  class="greenBtn" >
							<input type="button" value="결재선 삭제"  class="blueBtn">
						</td>
					</tr>
				</table>
				<table id="tblBackground">
					<tr>
                		<td>
							<div id="app_container">
								<table id="create_series_table" class="create_table_in create_series_table fix_table">
									<colgroup>
										<col width="40">
										<col width="100">
										<col width="250">
										<col width="250">
										<col width="250">
										<col width="250">
										<col width="250">
									</colgroup>
									<thead>
										<tr>
											<th>
												<input type="checkbox" name="allDocuments" id="allDocuments">
											</th>
											<th>순서</th>
											<th>결제타입</th>
											<th>이름</th>
											<th>아이디</th>
											<th>직급</th>
											<th>부서</th>
										</tr>						
									</thead>
									<tbody id="addLineBody_series">
										<tr id="nodataSeriesLine">
											<td class="nodata" colspan="7">지정된 결재라인이 없습니다.</td>
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
			<th><font class="req">결재 EPALN</font></th>
			<td colspan="3">
				<table class="in_btn_table">
					<tr>
						<td class="add"><input type="button" value="EPLAN 추가"
							title="EPLAN추가" id="addLibraryParts" data-context="eplan"
							data-changeable="false" data-fun="codeLibrary" data-dbl="true"
							data-state="INWORK"> <input type="button"
							title="EPLAN 삭제" value="EPLAN 삭제" id="delLibrarys"
							class="blueBtn"></td>
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
											<th><input type="checkbox" name="allLibrarys"
												id="allLibrarys"></th>
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
											<td class="nodata" colspan="8">결재 EPLAN이 없습니다.</td>
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
			<th><font class="req">결재 EPLAN<br>문서
			</font></th>
			<td colspan="3">
				<table class="in_btn_table">
					<tr>
						<td class="add"><input type="button" value="EPLAN 추가"
							title="EPLAN추가" id="addEplanDoc" data-context="eplan"
							data-changeable="false" data-dbl="true" data-state="INWORK">
							<input type="button" title="EPLAN 삭제" value="EPLAN 삭제"
							id="delDocuments" class="blueBtn"></td>
					</tr>
				</table>
				<table id="tblBackground">
					<tr>
						<td>
							<div id="documents_container">
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
											<th><input type="checkbox" name="allDocuments"
												id="allDocuments"></th>
											<th>파일명</th>
											<th>품명</th>
											<th>규격</th>
											<th>버전</th>
											<th>상태</th>
											<th>작성자</th>
											<th>수정자</th>
										</tr>
									</thead>
									<tbody id="addDocumentsBody">
										<tr id="nodataDocuments">
											<td class="nodata" colspan="8">문서를 추가하세요.</td>
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
			<th>결재의견</th>
			<td colspan="3">
				<!-- rows 세로, cols 가로 --> <textarea class="AXTextarea"
					name="comment" id="comment" rows="3" cols=""></textarea>
			</td>
		</tr>
	</table>
</td>
</body>
</html>