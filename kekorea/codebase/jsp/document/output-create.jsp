<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="e3ps.project.Project"%>
<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="e3ps.doc.service.DocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
// module root
String root = DocumentHelper.OUTPUT_ROOT;

boolean isPopup = Boolean.parseBoolean((String) request.getParameter("popup"));

String number = (String) request.getAttribute("number");
String toid = (String) request.getParameter("toid");
String ptype = (String) request.getParameter("ptype");
String outputLoc = (String) request.getParameter("outputLoc");
String poid = (String) request.getParameter("poid");
String progress = (String) request.getParameter("progress");

Project project = null;
boolean isOutput = false;
ReferenceFactory rf = new ReferenceFactory();
if (!StringUtils.isNull(outputLoc)) {
	root = outputLoc;
	isOutput = true;
	project = (Project) rf.getReference(poid).getObject();
}
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>산출물 등록</title>
<%@include file="/jsp/common/layouts/include_css.jsp"%>
<%@include file="/jsp/common/layouts/include_script.jsp"%>
</head>
<body>
	<!-- script area -->
	<script type="text/javascript">
		$(document).ready(function() {
			// init AXUpload5
			// 		upload.pageStart(null, null, "primary");
			upload.pageStart(null, null, "all");
			// 		upload.pageStart(null, null, "secondary");

			$("input").checks();

			$(".documents_add_table").tableHeadFixer();
			// 		$(".fix_table").tableHeadFixer();
		})
	</script>
	<%
	if (!StringUtils.isNull(poid)) {
	%>
	<input type="hidden" name="projectOid" value="<%=poid%>">
	<%
	}
	%>
	<input type="hidden" name="popup" value="<%=isPopup%>">
	<table class="btn_table">
		<!-- create header title -->
		<tr>
			<td>
				<div class="header_title">
					<i class="axi axi-subtitles"></i>
					<span>산출물 등록</span>
					<!-- req msg -->
					<font class="reqMsg">(빨간색 속성 값은 필수 입력 값입니다.)</font>

				</div>
			</td>
			<td class="right">
				<input type="button" value="저장" id="createOutputAction" title="저장" data-self="false" data-output="<%=isOutput%>" data-toid="<%=toid%>" data-progress="<%=progress%>" data-ptype="<%=ptype%>">
				<%-- 				<input type="button" value="자가결재" id="createSelfOutputBtn" title="자가결재" class="blueBtn" data-self="true" data-output="<%=isOutput %>" data-progress="<%=progress %>"> --%>
				<%
				if (!isPopup) {
				%>
				<input type="button" value="뒤로" id="backBtn" title="뒤로" class="blueBtn">
				<!-- 				<input type="button" value="취소" id="backBtn" title="취소" class="redBtn"> -->
				<%
				} else {
				%>
				<input type="button" value="닫기" onclick="self.close();" title="닫기" class="redBtn">
				<%
				}
				%>
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
			<th>
				<font class="req">저장위치</font>
			</th>
			<td <%if (!isOutput) {%> colspan="3" <%}%>>
				<input type="hidden" name="location" id="location" <%if (!StringUtils.isNull(outputLoc)) {%> value="<%=outputLoc%>" <%}%>>
				<span class="location" id="locationStr">
					<span class="locText"><%=root%></span>
				</span>
				&nbsp;&nbsp;
				<input <%if (isOutput) {%> style="visibility: hidden;" <%}%> type="button" data-popup="true" data-context="PRODUCT" data-root="/Default/프로젝트" title="폴더선택" class="openLoc" value="폴더선택">
			</td>
			<%
			if (isOutput) {
			%>
			<th>KEK작번</th>
			<td><%=project.getKekNumber()%>-<%=project.getPType()%></td>
			<%
			}
			%>
		</tr>
		<%
		if (!isOutput) {
		%>
		<tr>
			<th>
				<font class="req">산출물 제목</font>
			</th>
			<td>
				<input type="text" name="name" id="name" class="AXInput wid300">
			</td>
			<th>산출물 번호</th>
			<td>
				<input type="text" name="snumber" id="snumber" value="<%=number%>" class="AXInput wid200" readonly="readonly">
			</td>
		</tr>
		<%
		} else {
		%>
		<tr>
			<th>
				<font class="req">산출물 제목</font>
			</th>
			<td>
				<input type="text" name="name" id="name" class="AXInput wid300">
			</td>
			<th>산출물 번호</th>
			<td>
				<input type="text" name="number" id="number" class="AXInput wid200" readonly="readonly" value="<%=number%>">
			</td>
		</tr>
		<%
		}
		%>
		<tr>
			<th>
				<font class="req">
					KEK 작번
					<br>
					(
					<span id="descProjectCnt">0</span>
					&nbsp;개 )
				</font>
			</th>
			<td colspan="3">

				<table class="in_btn_table">
					<tr>
						<td class="add">
							<input type="button" value="작번 추가" title="작번 추가" id="addProjects" data-context="product" data-dbl="true">
							<input type="button" value="작번 삭제" title="작번 삭제" id="delProjects" class="blueBtn">
						</td>
					</tr>
				</table>
				<!-- 				<div style="width:100%; height:300px; overflow:auto;"> -->
				<table id="tblBackground">
					<tr>
						<td>
							<div id="projects_container">
								<table class="create_table_in documents_add_table fix_table">
									<colgroup>
										<col width="40">
										<col width="80">
										<col width="100">
										<col width="100">
										<col width="100">
										<col width="100">
										<col width="400">
									</colgroup>
									<thead>
										<tr>
											<th>
												<input type="checkbox" name="allProjects" id="allProjects">
											</th>
											<th>작번유형</th>
											<th>KEK 작번</th>
											<th>KE 작번</th>
											<th>고객사</th>
											<th>막종</th>
											<th>작업내용</th>
										</tr>
									</thead>
									<tbody id="addProjectsBody">
										<tr id="nodataProjects">
											<td class="nodata" colspan="7">관련 작번이 없습니다.</td>
										</tr>
									</tbody>
								</table>
							</div>
						</td>
					</tr>
				</table>
				<!-- 				</div> -->
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
			<th>
				<font class="req">산출물 파일</font>
			<td colspan="3">
				<!-- upload.js see -->
				<!-- 				<div class="AXUpload5" id="primary_layer"></div> -->
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