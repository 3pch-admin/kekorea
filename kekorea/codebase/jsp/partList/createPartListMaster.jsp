<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="e3ps.org.Department"%>
<%@page import="e3ps.doc.service.DocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	Department dept = (Department) request.getAttribute("dept");
	
	String deptName = "";
	if(dept != null) {
		deptName = dept.getName();
	}

	boolean isPopup = Boolean.parseBoolean((String) request.getParameter("popup"));

	String poid = (String) request.getParameter("poid");
	String progress = (String) request.getParameter("progress");
	String tname = (String) request.getParameter("tname");
	
	boolean isOutput = true;
	if(!StringUtils.isNull(poid)) {
		isOutput = true;
	}
%>    
<td valign="top">
	<!-- script area -->
	<script type="text/javascript">
	 $(document).ready(function() {
		 
		 $('#name').keydown(function(e){
			 if(e.keyCode == 13){
					e.preventDefault();
				}
		 });
// 		upload.pageStart(null, null, "primary");
		upload.pageStart(null, null, "secondary");
		$("input").checks();

		$("#engType").bindSelect();
		
		<%
			if(!StringUtils.isNull(tname)) {
		%>
		$("#engType").bindSelectSetValue("<%=tname %>");
		$("#engType").bindSelectDisabled(true);
		<%
			}
		%>
	}) 
	</script>
	
	<%
		if(!StringUtils.isNull(poid)) {
	%>
	<input type="hidden" name="projectOid" value="<%=poid %>">
	<%
		}
	%>
	<input type="hidden" name="popup" value="<%=isPopup %>">
	<!-- create header title -->
	<table class="btn_table">
		<tr>
			<td>
				<div class="header_title">
					<i class="axi axi-subtitles"></i><span>수배표 등록</span>
					<!-- req msg -->
					<font class="reqMsg">(빨간색 속성 값은 필수 입력 값입니다.)</font>
				</div>
			</td>
			<td class="right">
				<input type="button" value="저장" id="createPartListBtn" title="저장"  data-output="<%=isOutput %>" data-progress="<%=progress %>">
				<%
					if(isPopup) {
				%>
				<input type="button" value="닫기" id="closePartList" title="닫기" class="redBtn">
				<%
					} else {
				%>
				<input type="button" value="취소" id="backBtn" title="취소" class="redBtn">
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
		</colgroup>
		<tr>
			<th class="min-wid100"><font class="req">수배표 제목</font></th>
			<td colspan="3">
				<input type="text" name="name" id="name" class="AXInput wid300">
			</td>
<!-- 			<th>수배표 번호</th> -->
<!-- 			<td> -->
<!-- 				<input type="text" name="number" id="number" class="AXInput wid300"> -->
<!-- 				<input type="text" name="MODEL_NAME" id="MODEL_NAME" class="AXInput wid200"> -->
<!-- 			</td>			 -->
		</tr>
		<tr>
			<th><font class="req">설계구분</font></th>
			<td colspan="3">
				<select name="engType" id="engType" class="AXSelect wid100">
					<option value="" <%if(deptName.equals("")) {  %> selected <%} %>>선택</option>
					<option value="전기" <%if(deptName.equals("전기설계")) {  %> selected <%} %>>전기</option>
					<option value="기계" <%if(deptName.equals("기계설계")) {  %> selected <%} %>>기계</option>
<%-- 					<option value="SOFT" <%if(deptName.equals("SW설계")) {  %> selected <%} %>>SOFT</option> --%>
				</select>
			</td>
		</tr>		
		<tr>
			<th>설명<br><span id="descDocCnt">0</span>/1000</th>
			<td colspan="3">
				<!-- rows 세로, cols 가로 -->
				<textarea class="AXTextarea" name="descriptionDoc" id="descriptionDoc" rows="" cols=""></textarea>
			</td>			
		</tr>	
		<tr>
			<th><font class="req">KEK 작번</font><br>( <span id="descProjectCnt">0</span>&nbsp;개 )</th>
			<td colspan="3">
				<table class="in_btn_table">
					<tr>
						<td class="add">
							<input type="button" value="작번 추가" title="작번 추가" id="addProjects" data-context="product" data-dbl="true">
							<input type="button" value="작번 삭제" title="작번 삭제" id="delProjects" class="blueBtn">
						</td>
					</tr>
				</table>
		        <table id="tblBackground">
		            <tr>
		                <td>
							<div id="projects_container">
								<table class="create_table_in fix_table">
									<colgroup>
										<col width="40">
										<col width="80">
										<col width="180">
										<col width="180">
										<col width="100">
										<col width="120">
										<col width="*">
									</colgroup>
									<thead>
										<tr>
											<th>
												<input type="checkbox" name="allProjects" id="allProjects">
											</th>
											<th>작번 유형</th>
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
			</td>
		</tr>		
		<!-- 결재 -->
		<jsp:include page="/jsp/common/appLine.jsp">
			<jsp:param value="true" name="required" />
		</jsp:include>				
		<tr>
			<th>첨부파일<span id="fileCount"></span></th>
			<td colspan="3">
				<!-- upload.js see -->
				<div class="AXUpload5" id="secondary_layer"></div>
				<div class="AXUpload5QueueBox_list" id="uploadQueueBox"></div>
			</td>
		</tr>			
		<tr>
			<th><font class="req">수배표 등록</font></th>
			<td colspan="3">
		        <table id="tblBackground">
	        	    <tr>
		                <td>
						<div id="spreadsheet"></div>
						<script>
						
						var jexcels = jexcel(document.getElementById('spreadsheet'), {
// 							data : [{}],
							rowResize:false,
						    columnDrag:false,
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
						        { type: 'text', title:'단가', width:90, readOnly:true},
						        { type: 'dropdown', title:'화폐', width:50, readOnly:true, source : ["KRW", "JPY"] },
						        { type: 'numeric', title:'원화금액', width:100, readOnly:true},
						        { type: 'text', title:'수배일자', width:80, readOnly:true },
						        { type: 'text', title:'환율', width:60, readOnly:true },
						        { type: 'text', title:'참고도면', width:100 },
						        { type: 'text', title:'조달구분', width:100 },
						        { type: 'text', title:'비고', width:150 },
						     ],
// 						     style: {
// 						         A0:'background-color: #e0f0fd;',
// 						     },
						});
						</script>			
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</td>