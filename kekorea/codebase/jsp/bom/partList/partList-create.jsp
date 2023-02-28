<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="e3ps.org.Department"%>
<%@page import="e3ps.doc.service.DocumentHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
Department dept = (Department) request.getAttribute("dept");

String deptName = "";
if (dept != null) {
	deptName = dept.getName();
}

String poid = (String) request.getParameter("poid");
String progress = (String) request.getParameter("progress");
String tname = (String) request.getParameter("tname");

boolean isOutput = true;
if (!StringUtils.isNull(poid)) {
	isOutput = true;
}
%>
<!-- script area -->
<script type="text/javascript">
	$(document).ready(function() {

		$('#name').keydown(function(e) {
			if (e.keyCode == 13) {
				e.preventDefault();
			}
		});

		$("#engType").bindSelect();
<%if (!StringUtils.isNull(tname)) {%>
$("#engType").bindSelectSetValue("<%=tname%>");
$("#engType").bindSelectDisabled(true);
<%}%>
	})
</script>

<%
if (!StringUtils.isNull(poid)) {
%>
<input type="hidden" name="projectOid" value="<%=poid%>">
<%
}
%>
<%@include file="/jsp/include/auigrid.jsp"%>
<table class="btn_table">
	<tr>
		<td>
			<div class="header_title">
				<i class="axi axi-subtitles"></i>
				<span>수배표 등록</span>
				<!-- req msg -->
				<font class="reqMsg">(빨간색 속성 값은 필수 입력 값입니다.)</font>
			</div>
		</td>
		<td class="right">
			<input type="button" value="저장" id="createPartListBtn" title="저장" data-output="<%=isOutput%>" data-progress="<%=progress%>">
			<input type="button" value="닫기" id="closeBtn" title="닫기" class="redBtn">
		</td>
	</tr>
</table>

<!-- create table -->
<table class="create_table">
	<colgroup>
		<col width="130">
		<col width="800">
		<col width="130">
		<col width="800">
	</colgroup>
	<tr>
		<th class="min-wid100">
			<font class="req">수배표 제목</font>
		</th>
		<td>
			<input type="text" name="name" id="name" class="AXInput wid500">
		</td>
		<th>
			<font class="req">설계구분</font>
		</th>
		<td colspan="3">
			<select name="engType" id="engType" class="AXSelect wid100">
				<option value="" <%if (deptName.equals("")) {%> selected <%}%>>선택</option>
				<option value="전기" <%if (deptName.equals("전기설계")) {%> selected <%}%>>전기</option>
				<option value="기계" <%if (deptName.equals("기계설계")) {%> selected <%}%>>기계</option>
				<%-- 					<option value="SOFT" <%if(deptName.equals("SW설계")) {  %> selected <%} %>>SOFT</option> --%>
			</select>
		</td>
	</tr>
	<tr>
		<th>설명</th>
		<td colspan="3">
			<textarea name="description" id="description" rows="7" cols=""></textarea>
		</td>
	</tr>
	<tr>
		<th>
			<font class="req">KEK 작번</font>
		</th>
		<td colspan="3">
			<jsp:include page="/jsp/include/include-project.jsp"></jsp:include>
		</td>
	</tr>
	<!-- 결재 -->
	<%-- 	<jsp:include page="/jsp/common/appLine.jsp"> --%>
	<%-- 		<jsp:param value="true" name="required" /> --%>
	<%-- 	</jsp:include> --%>
	<tr>
		<th>첨부파일</th>
		<td colspan="3">
			<jsp:include page="/jsp/include/include-secondary.jsp" />
		</td>
	</tr>
	<tr>
		<th>
			<font class="req">수배표 등록</font>
		</th>
		<td colspan="3">
			<div id="partlist_grid_wrap" style="height: 550px; border-top: 1px solid #3180c3; margin-top: 5px;"></div>
			<script type="text/javascript">
				let partlistGridID;
				let list = [ "KRW", "JPY" ];
				const partlist_columns = [ {
					dataField : "ok",
					headerText : "검증",
					width : 80,
					renderer : {
						type : "CheckBoxEditRenderer",
					},
					editable : false
				}, {
					dataField : "lotNo",
					headerText : "LOT",
					dataType : "string",
					width : 100,
					editable : false
				}, {
					dataField : "unitName",
					headerText : "UNIT_NAME",
					dataType : "string",
					width : 130,
					editable : false
				}, {
					dataField : "partNo",
					headerText : "부품번호",
					dataType : "string",
					width : 150,
				}, {
					dataField : "partName",
					headerText : "부품명",
					dataType : "string",
					width : 270,
					editable : false
				}, {
					dataField : "standard",
					headerText : "규격",
					dataType : "string",
					width : 150,
					editable : false
				}, {
					dataField : "maker",
					headerText : "MAKER",
					dataType : "string",
					width : 150,
					editable : false
				}, {
					dataField : "customer",
					headerText : "거래처",
					dataType : "string",
					width : 150,
					editable : false
				}, {
					dataField : "quantity",
					headerText : "수량",
					dataType : "numeric",
					width : 80,
					formatString : "###0",
					editRenderer : {
						type : "InputEditRenderer",
						onlyNumeric : true, // 0~9만 입력가능
					},
				}, {
					dataField : "unit",
					headerText : "단위",
					dataType : "string",
					width : 80,
					editable : false
				}, {
					dataField : "price",
					headerText : "단가",
					dataType : "numeric",
					editable : false,
					width : 130,
					formatString : "#,###",
				}, {
					dataField : "currency",
					headerText : "화폐",
					dataType : "string",
					width : 80,
					editable : false,
				}, {
					dataField : "won",
					headerText : "원화금액",
					dataType : "numeric",
					editable : false,
					width : 130,
					formatString : "#,###",
				}, {
					dataField : "partListDate",
					headerText : "수배일자",
					dataType : "date",
					formatString : "yyyy-mm-dd",
					editable : false
				}, {
					dataField : "exchangeRate",
					headerText : "환율",
					dataType : "numeric",
					formatString : "#,###",
					editable : false
				}, {
					dataField : "referDrawing",
					headerText : "참고도면",
					dataType : "string",
					width : 200
				}, {
					dataField : "classification",
					headerText : "조달구분",
					dataType : "string",
					width : 200
				}, {
					dataField : "note",
					headerText : "비고",
					dataType : "string",
					width : 200
				}, {
					dataField : "oid",
					headerText : "oid",
					dataType : "string",
					visible : false
				} ]

				const partlist_props = {
					headerHeight : 30,
					rowHeight : 30,
					showRowNumColumn : true,
					rowNumHeaderText : "번호",
					softRemoveRowMode : false,
					showRowCheckColumn : true, // 체크 박스 출력,
					showStateColumn : true,
					editable : true,
					selectionMode : "multipleCells",
					$compaEventOnPaste : true
				};

				$(function() {
					partlistGridID = AUIGrid.create("#partlist_grid_wrap", partlist_columns, partlist_props);
					AUIGrid.addRow(partlistGridID, new Object(), "first");
					AUIGrid.bind(partlistGridID, "cellEditEnd", auiCellEditEndHandler);

					$("#closeBtn").click(function() {
						self.close();
					})
				})

				function auiCellEditEndHandler(event) {
					let dataField = event.dataField;
					let item = event.item;
					console.log(item);
					if (dataField === "partNo" || dataField === "quantity") {
						let url = getCallUrl("/erp/partListItemValue?partNo=" + item.partNo + "&quantity=" + item.quantity);
						call(url, null, function(data) {
							console.log(data);
						}, "GET");
					}
				}

				$(window).resize(function() {
					AUIGrid.resize(partlistGridID);
				})
			</script>
		</td>
	</tr>
</table>