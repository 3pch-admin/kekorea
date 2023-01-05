<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="e3ps.doc.service.DocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	boolean isPopup = Boolean.parseBoolean((String) request.getParameter("popup"));

	String poid = (String) request.getParameter("poid");
%>
<td valign="top">
	<!-- script area --> <script type="text/javascript">
		$(document).ready(function() {
			// init AXUpload5
			// 		upload.pageStart(null, null, "primary");
			// 		upload.pageStart(null, null, "secondary");
			upload.pageStart(null, null, "all");
			$("input").checks();
		})
	</script> <%
 	if (!StringUtils.isNull(poid)) {
 %> <input type="hidden" name="projectOid" value="<%=poid%>"> <%
 	}
 %>
	<table class="btn_table">
		<!-- create header title -->
		<tr>
			<td>
				<div class="header_title">
					<i class="axi axi-subtitles"></i><span>특이 사항 등록</span>
					<!-- req msg -->
					<font class="reqMsg">(빨간색 속성 값은 필수 입력 값입니다.)</font>

				</div>
			</td>
			<td class="right"><input type="button" value="저장" id="createIssueAction" title="저장" data-self="false" data-poid="<%=poid%>"> <%
 	if (!isPopup) {
 %> <input type="button" value="취소" id="backBtn" title="취소" class="redBtn"> <%
 	} else {
 %> <input type="button" value="닫기" onclick="self.close();" title="닫기" class="redBtn"> <%
 	}
 %></td>
		</tr>
	</table> <!-- create table -->
	<table class="create_table">
		<tr>
			<th class="min-wid150"><font class="req">특이 사항 제목</font></th>
			<td colspan="3"><input type="text" name="name" id="name" class="AXInput wid300"></td>
		</tr>

		<tr>
			<th><font class="req">설명</font><br>
			<span id="descDocCnt">0</span>/1000</th>
			<td colspan="3">
				<!-- rows 세로, cols 가로 --> <textarea style="height: 100px !important;" class="AXTextarea" name="description" id="description" rows="5" cols=""></textarea>
			</td>
		</tr>
		<tr>
			<th>첨부파일<span id="fileCount"></span></th>
			<td colspan="3">
				<!-- upload.js see -->
				<div class="AXUpload5" id="allUpload_layer"></div>
				<div class="AXUpload5QueueBox_list" id="uploadQueueBox"></div>
			</td>
		</tr>
		<tr>
			<th>KEK 작번<br>( <span id="descProjectCnt">0</span>&nbsp;개 )</th>
			<td colspan="3">

				<table class="in_btn_table">
					<tr>
						<td class="add"><input type="button" value="작번 추가" title="작번 추가" id="addProjects" data-context="product" data-dbl="true"> <input type="button" value="작번 삭제" title="작번 삭제" id="delProjects" class="blueBtn"></td>
					</tr>
				</table> <!-- 				<div style="width:100%; height:300px; overflow:auto;"> -->
				<table id="tblBackground">
					<tr>
						<td>
							<div id="projects_container">
								<table class="create_project_table_in documents_add_table">
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
											<th><input type="checkbox" name="allProjects" id="allProjects"></th>
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
				</table> <!-- 				</div> -->
			</td>
		</tr>
	</table>
</td>