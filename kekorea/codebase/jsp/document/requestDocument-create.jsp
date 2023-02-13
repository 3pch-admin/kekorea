<%@page import="e3ps.project.template.Template"%>
<%@page import="e3ps.common.code.service.CommonCodeHelper"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.doc.service.DocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String customer = (String) request.getAttribute("customer");
String install = (String) request.getAttribute("install");
ArrayList<Template> tmp = (ArrayList<Template>) request.getAttribute("tmp");
boolean isPopup = (boolean) request.getAttribute("isPopup");
String poid = (String) request.getParameter("poid");
String toid = (String) request.getParameter("toid");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>의뢰서 등록</title>
<%@include file="/jsp/common/layouts/include_css.jsp"%>
<%@include file="/jsp/common/layouts/include_script.jsp"%>
<style>
.jexcel_container {
	height: 300px;
}
</style>
</head>
<body>
	<input type="hidden" name="poid" value="<%=poid%>">
	<input type="hidden" name="toid" value="<%=toid%>">
	<!-- script area -->
	<script type="text/javascript">
	
	var popup = "<%=isPopup%>";
	
	$(document).ready(function() {
		
		 $('#name').keydown(function(e){
			 if(e.keyCode == 13){
					e.preventDefault();
				}
		 });
		
		
// 		upload.pageStart(null, null, "primary");
		$("#pTemplate").bindSelect();
		$("input").checks();
		
		upload.pageStart(null, null, "all");
	})
	
	dropdownFilter = function(instance, cell, c, r, source) {
		var value = instance.jexcel.getValueFromCoords(2, r);
	    if (value == "GROBAL FOUNDRIES") {
	    	return <%=CommonCodeHelper.manager.getInstallCommonCodeByName("GROBAL FOUNDRIES")%>
	    } else if(value == "KOKUSAI ELECTRIC") {
	    	return <%=CommonCodeHelper.manager.getInstallCommonCodeByName("KOKUSAI ELECTRIC")%>
	    } else if(value == "HYNIX") {
	    	return <%=CommonCodeHelper.manager.getInstallCommonCodeByName("HYNIX")%>
	    } else if(value == "KEK") {
	    	return <%=CommonCodeHelper.manager.getInstallCommonCodeByName("KEK")%>
	    } else if(value == "SAMSUNG") {
	    	return <%=CommonCodeHelper.manager.getInstallCommonCodeByName("SAMSUNG")%>
		} else if (value == "국가핵융합연구소") {
				return
	<%=CommonCodeHelper.manager.getInstallCommonCodeByName("국가핵융합연구소")%>
		} else {
				return source;
			}
		}
	</script>

	<table class="btn_table">
		<!-- create header title -->

		<tr>
			<td>
				<div class="header_title">
					<i class="axi axi-subtitles"></i>
					<span>의뢰서 등록</span>
					<!-- req msg -->
					<font class="reqMsg">(빨간색 속성 값은 필수 입력 값입니다.)</font>

				</div>
			</td>
			<td class="right">
				<input type="button" value="저장" id="createRequestDocumentBtn" title="저장" data-self="false">
				<!-- 				<input type="button" value="자가결재" id="createSelfRequestDocumentBtn" title="자가결재" class="blueBtn" data-self="true">  -->
				<input type="button" value="닫기" id="closeDocBtn" title="닫기" class="redBtn" onclick="self.close()">
			</td>
		</tr>
	</table>

	<!-- create button -->


	<!-- create table -->
	<table class="create_table">
		<!-- colgroup -->
		<colgroup>
			<col width="90">
			<col>
			<col width="90">
			<col>
		</colgroup>
		<tr>
			<th>
				<font class="req">의뢰서 제목</font>
			</th>
			<td <%if (isPopup) {%> colspan="3" <%}%>>
				<input type="text" name="name" id="name" class="AXInput wid300">
			</td>
			<%
			if (!isPopup) {
			%>
			<th>
				<font class="req">작번 템플릿</font>
			</th>
			<td colspan="3">
				<select name="pTemplate" id="pTemplate" class="AXSelect wid300">
					<option value="">선택</option>
					<%
// 					for (Template pTemplate : tmp) {
					%>
<%-- 					<option value="<%=pTemplate.getPersistInfo().getObjectIdentifier().getStringValue()%>"><%=pTemplate.getName()%></option> --%>
					<%
					}
					%>
				</select>
			</td>
			<%
// 			}
			%>
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
				<textarea class="AXTextarea theight50" name="descriptionDoc" id="descriptionDoc" rows="3" cols=""></textarea>
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
			<jsp:param value="true" name="required" />
		</jsp:include>
		<%
		if (!isPopup) {
		%>
		<tr>
			<th>
				<font class="req">작번 등록</font>
			</th>
			<td colspan="3">
				<table id="tblBackground">
					<tr>
						<td>
							<div id="spreadsheet"></div>
							<script>
								var jexcels = jexcel(document.getElementById('spreadsheet'), {
									rowResize : false,
									columnDrag : false,
									tableOverflow : false,
									onpaste : partlists.checkKekNumber,
									onchange : partlists.changed,
									columns : [ {
										type : 'text',
										title : '체크',
										width : 30,
										readOnly : true
									}, {
										type : 'text',
										title : '작번유형',
										width : 40
									}, {
										type : 'dropdown',
										title : '거래처',
										width : 150,
										source : [
							<%=customer%>
								]
									}, {
										type : 'dropdown',
										title : '설치장소',
										width : 100,
										source : [
							<%=install%>
								],
										filter : dropdownFilter
									}, {
										type : 'text',
										title : '막종',
										width : 70
									}, {
										type : 'text',
										title : 'KEK작번',
										width : 70
									}, {
										type : 'text',
										title : 'KE작번',
										width : 70
									}, {
										type : 'text',
										title : 'USER ID',
										width : 70
									}, {
										type : 'text',
										title : '요구 납기일',
										width : 70
									}, {
										type : 'text',
										title : '작업내용',
										width : 200
									}, {
										type : 'text',
										title : '모델',
										width : 70
									}, {
										type : 'text',
										title : '발행일',
										width : 70
									}, {
										type : 'text',
										title : '전기 담당자',
										width : 70
									}, {
										type : 'text',
										title : '기계 담당자',
										width : 70
									}, {
										type : 'text',
										title : 'SW 담당자',
										width : 70
									}, ],
								});
							</script>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<%
		}
		%>
	</table>
</body>
</html>