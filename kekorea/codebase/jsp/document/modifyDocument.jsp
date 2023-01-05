<%@page import="e3ps.doc.service.DocumentHelper"%>
<%@page import="e3ps.doc.beans.DocumentViewData"%>
<%@page import="e3ps.part.column.PartProductColumnData"%>
<%@page import="wt.part.WTPart"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	DocumentViewData data = (DocumentViewData) request.getAttribute("data");
	String popup = (String) request.getParameter("popup");
	boolean isPopup = Boolean.parseBoolean(popup);
	String root = DocumentHelper.ROOT;
%>
<td valign="top">
	<!-- script -->
	<script type="text/javascript">
	$(document).ready(function() {
		upload.pageStart("<%=data.oid%>", "", "primary");
		upload.pageStart("<%=data.oid%>", "", "secondary");
		
		$("input").checks();
		
		$(".documents_add_table").tableHeadFixer();
		
		var len = "<%=data.description.length()%>";
		$("#descDocCnt").text(len);
	})
	</script>
	<input type="hidden" name="oid" value="<%=data.oid%>">
	<input type="hidden" name="popup" id="popup" value="<%=isPopup%>">
	<!-- create header title -->
	<div class="header_title">
		<i class="axi axi-subtitles"></i><span>문서 수정</span>
		<!-- req msg -->
		<font class="reqMsg">(빨간색 속성 값은 필수 입력 값입니다.)</font>
	</div>
	<%
		if(isPopup) {
	%>
	<div class="right">
		<input type="button" value="수정" id="modifyDocBtnAction" title="수정"> 
		<input type="button" value="닫기" id="closeDocBtn" title="닫기" class="redBtn">
	</div>	
	<%
			}
	%>
	
	<table class="create_table">
		<!-- colgroup -->
		<colgroup>
			<col width="400">
			<col width="400">
			<col width="400">
			<col width="400">
		</colgroup>
		<tr>
			<th><font class="req">저장위치</font></th>
			<td colspan="3">
				<input type="hidden" name="location" id="location" value="<%=data.location%>">
				<span class="location" id="locationStr"><span class="locText"><%=data.location%></span></span>&nbsp;&nbsp;
				<input type="button" data-popup="true" data-context="PRODUCT" data-root="/Default/문서" title="폴더선택" class="openLoc" value="폴더선택">
			</td>
		</tr>	
		<tr>
			<th><font class="req">문서제목</font></th>
			<td>
				<input value="<%=data.name%>" type="text" name="name" id="name" class="AXInput wid500">
			</td>
			<th>문서번호</th>
			<td><%=data.number%></td>
		</tr>		
		<tr>
			<th>버전</th>
			<td><%=data.fullVersion%></td>
			<th>상태</th>
			<td><%=data.state%></td>			
		</tr>		
		<tr>
			<th>작성자</th>
			<td><%=data.creator%></td>
			<th>작성일</th>
			<td><%=data.createDate%></td>			
		</tr>			
		<tr>
			<th>수정자</th>
			<td><%=data.modifier%></td>
			<th>수정일</th>
			<td><%=data.modifyDate%></td>			
		</tr>		
		<tr>
			<th>저장위치</th>
			<td colspan="3"><%=data.location%></td>
<!-- 			<th>MODEL_NAME</th> -->
<!-- 			<td> -->
<%-- 				<input type="text" name="MODEL_NAME" id="MODEL_NAME" class="AXInput wid200" value="<%=data.model_name%>"> --%>
<!-- 			</td>			 -->
		</tr>
		<tr>
			<th>설명<br><span id="descDocCnt">0</span>/1000</th>
			<td colspan="3">
				<!-- rows 세로, cols 가로 -->
				<textarea class="AXTextarea" name="descriptionDoc" id="descriptionDoc" rows="3" cols=""><%=data.description%></textarea>
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
							<div id="parts_container" <%if(data.refPart.size() > 0) {%> class="parts_container2" <%}%>>
								<table class="create_table_in parts_add_table">
									<colgroup>
										<col width="40">
										<col width="200">
										<col width="280">
										<col width="280">
										<col width="150">
										<col width="150">
										<col width="150">
										<col width="150">
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
											<th>수정자</th>
										</tr>						
									</thead>
									<tbody id="addPartsBody">
									<%
										for(WTPart refPart : data.refPart) {
											PartProductColumnData pdata = new PartProductColumnData(refPart);
									%>
										<tr>
											<td>
												<input type="checkbox" value="<%=pdata.oid %>" name="partOid" class="isBox">
											</td>
											<td class="infoParts" data-oid="<%=pdata.oid %>"><img src="<%=pdata.iconPath %>" class="pos3">&nbsp;<%=pdata.number %></td>
											<td class="infoParts left" data-oid="<%=pdata.oid %>">
												<%=pdata.name %>
											</td>
											<td><img src="<%=pdata.iconPath %>" class="pos3">&nbsp;<%=pdata.name_of_parts %></td>
											<td><%=pdata.version %></td>
											<td><%=pdata.state %></td>
											<td><%=pdata.creator %></td>
											<td><%=pdata.modifier %></td>
										</tr>
									<%
										}
										if(data.refPart.size() == 0) {
									%>
										<tr id="nodataParts">
											<td class="nodata" colspan="7">관련 부품이 없습니다.</td>
										</tr>
									<%
										}
									%>
									</tbody>	
								</table>
							</div>
						</td>
					</tr>
				</table>
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
		<!-- 결재 -->
		<jsp:include page="/jsp/common/appLine.jsp">
			<jsp:param value="false" name="required" />
		</jsp:include>		
	</table>
	
	<%
		if(!isPopup) {
	%>
	<table class="btn_table">
		<tr>
			<td class="center">
				<input type="button" value="수정" id="modifyDocBtnAction" title="수정"> 
				<input type="button" value="뒤로" id="backDocBtn" title="뒤로" class="blueBtn" data-oid="<%=data.oid %>">
			</td>
		</tr>
	</table>	
	<%
		}
	%>
</td>