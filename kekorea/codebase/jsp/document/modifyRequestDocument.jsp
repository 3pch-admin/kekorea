<%@page import="e3ps.doc.beans.RequestDocumentViewData"%>
<%@page import="e3ps.doc.RequestDocument"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	// popup check
	String popup = (String) request.getParameter("popup");
	boolean isPopup = Boolean.parseBoolean(popup);

	// data
	RequestDocumentViewData data = (RequestDocumentViewData) request.getAttribute("data");
	
	// isAdmin
	boolean isAdmin = CommonUtils.isAdmin();

	String install = (String)request.getAttribute("install");
%> 
<td valign="top">
	<script type="text/javascript">
	$(document).ready(function() {
		upload.pageStart("<%=data.oid%>", "", "primary");
		upload.pageStart("<%=data.oid%>", "", "secondary");
		
		$("input").checks();
	})
	</script>
	<input type="hidden" name="oid" id="oid" value="<%=data.oid %>">
	<input type="hidden" name="popup" id="popup" value="<%=isPopup %>">

<table class="btn_table">
		<tr>
			<td>
	<div class="header_title">
		<i class="axi axi-subtitles"></i><span>의뢰서 수정</span>
		<!-- req msg -->
		<font class="reqMsg">(빨간색 속성 값은 필수 입력 값입니다.)</font>
	</div>
	</td>
	<%-- <%
		if(isPopup) {
	%> --%>
	<td>
	<div class="right">
				<input type="button" value="수정" id="modifyRequestDocumentBtn" title="수정" data-oid="<%=data.oid %>">
				<input type="button" value="뒤로" id="backBtn" title="뒤로" class="blueBtn">				
		<!-- <input type="button" value="수정" id="infoVersionBtn" title="수정"> -->
		<!-- <input type="button" value="닫기" id="closeDocBtn" title="닫기" class="redBtn"> -->
	</div>
	</td>
	</tr>
	</table>
	<%-- <%
		}
	%> --%>
	
	<table class="create_table">
		<tr>
			<th class="min-wid200"><font class="req">의뢰서 제목</font></th>
			<td><input type="text" name="name" id="name" class="AXInput wid300" value="<%=data.name %>"></td>
		</tr>
		<tr>
			<th>설명<br><span id="descDocCnt">0</span>/4000</th>
			<td colspan="3">
				<textarea rows="3" cols="" class="AXTextarea bgk" name="reqDescription"><%=data.description %></textarea>
			</td>			
		</tr>		
		<tr>
			<th><font class="req">주 첨부파일</font>
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
		<jsp:include page="/jsp/common/appLine.jsp">
			<jsp:param value="true" name="required" />
		</jsp:include>			
		<tr>
			<th>관련 작번</th>
			<td colspan="3">
		        <table id="tblBackground">
	        	    <tr>
		                <td>
							<div id="spreadsheet"></div>
							<script>
							
							var jexcels = jexcel(document.getElementById('spreadsheet'), {
								data : <%=data.jsonList %>,
								rowResize:false,
							    columnDrag:false,
							    tableOverflow:true,
							    columns: [
							    	{ type: 'hidden', title:'oid', readOnly: true },
							        { type: 'text', title:'작번유형', width:150, readOnly: true },
							        { type: 'text', title:'거래처', width:200, readOnly: true  },
							        { type: 'dropdown', title:'설치장소', width:150, source: [<%=install %>] },
							        { type: 'text', title:'막종', width:150},
							        { type: 'text', title:'KEK작번', width:150, readOnly: true },
							        { type: 'text', title:'KE작번', width:150},
							        { type: 'text', title:'USER ID', width:200},
							        { type: 'text', title:'요구 납기일', width:200},
							        { type: 'text', title:'작업내용', width:230},
							        { type: 'text', title:'모델', width:130},
							        { type: 'text', title:'발행일', width:300},
							        { type: 'text', title:'전기 담당자', width:130},
							        { type: 'text', title:'기계 담당자', width:130},
							        { type: 'text', title:'SW 담당자', width:130},
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