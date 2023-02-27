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
%>
<%@include file="/jsp/include/auigrid.jsp"%>
<table class="btn_table">
	<tr>
		<td>
			<div class="header_title">
				<i class="axi axi-subtitles"></i>
				<span>T-BOM 등록</span>
				<font class="reqMsg">(빨간색 속성 값은 필수 입력 값입니다.)</font>
			</div>
		</td>
		<td class="right">
			<input type="button" value="저장" id="saveBtn" title="저장">
			<input type="button" value="닫기" id="closeBtn" title="닫기" class="redBtn">
		</td>
	</tr>
</table>

<!-- create table -->
<table class="create_table">
	<colgroup>
		<col width="130">
		<col width="600">
		<col width="130">
		<col width="600">
	</colgroup>
	<tr>
		<th>
			<font class="req">T-BOM 제목</font>
		</th>
		<td>
			<input type="text" name="name" id="name" class="AXInput wid500">
		</td>
		<th>
			<font class="req">설계구분</font>
		</th>
		<td>
			<select name="engType" id="engType" class="AXSelect wid100">
				<option value="" <%if (deptName.equals("")) {%> selected <%}%>>선택</option>
				<option value="전기" <%if (deptName.equals("전기설계")) {%> selected <%}%>>전기</option>
				<option value="기계" <%if (deptName.equals("기계설계")) {%> selected <%}%>>기계</option>
			</select>
		</td>
	</tr>
	<tr>
		<th>설명</th>
		<td colspan="3">
			<textarea class="AXTextarea" name="description" id="description" rows="" cols=""></textarea>
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
	<tr>
		<th>첨부파일</th>
		<td colspan="3">
			<jsp:include page="/jsp/include/include-secondary.jsp" />
		</td>
	</tr>
	<tr>
		<th>
			<font class="req">T-BOM</font>
		</th>
		<td colspan="3">
			<input type="button" value="추가" id="tbomAddBtn" title="추가">
			<input type="button" value="삭제" id="tbomDeleteBtn" title="삭제" class="redBtn">
			<div id="tbom_grid_wrap" style="height: 550px; border-top: 1px solid #3180c3; margin-top: 5px;"></div>
			<script type="text/javascript">
				let tbomGridID;
				const tbom_columns = [ {
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
					dataField : "code",
					headerText : "중간코드",
					dataType : "string",
					width : 130,
					editable : false
				}, {
					dataField : "kePartNumber",
					headerText : "부품번호",
					dataType : "string",
					width : 150,
				}, {
					dataField : "kePartName",
					headerText : "부품명",
					dataType : "string",
					width : 270,
					editable : false
				}, {
					dataField : "model",
					headerText : "KokusaiModel",
					dataType : "string",
					editable : false
				}, {
					dataField : "qty",
					headerText : "Qty",
					dataType : "numeric",
					width : 100,
					formatString : "###0",
					editRenderer : {
						type : "InputEditRenderer",
						onlyNumeric : true, // 0~9만 입력가능
					},
				}, {
					dataField : "unit",
					headerText : "Unit",
					dataType : "string",
					width : 130
				}, {
					dataField : "provide",
					headerText : "Provide",
					dataType : "string",
					width : 130
				}, {
					dataField : "discontinue",
					headerText : "Discontinue",
					dataType : "string",
					width : 200
				}, {
					dataField : "oid",
					headerText : "oid",
					dataType : "string",
					visible : false
				} ]

				const tbom_props = {
					headerHeight : 30,
					rowHeight : 30,
					showRowNumColumn : true,
					rowNumHeaderText : "번호",
					softRemoveRowMode : false,
					showRowCheckColumn : true, // 체크 박스 출력,
					showStateColumn : true,
					editable : true,
					fillColumnSizeMode : true,
					selectionMode : "multipleCells",
					$compaEventOnPaste : true
				};

				function auiAddRowHandler(event) {
					let selected = AUIGrid.getSelectedIndex(tbomGridID);
					if (selected.length <= 0) {
						return;
					}
					let rowIndex = selected[0];
					let colIndex = AUIGrid.getColumnIndexByDataField(tbomGridID, "kePartNumber");
					AUIGrid.setSelectionByIndex(tbomGridID, rowIndex, colIndex);
					AUIGrid.openInputer(tbomGridID);
				}

				function auiCellEditEndHandler(event) {
					let dataField = event.dataField;
					if (dataField === "kePartNumber") {
						let url = getCallUrl("/kepart/get?kePartNumber=" + event.item.kePartNumber);
						call(url, null, function(data) {
							if (data.ok) {
								let item = {
									kePartNumber : data.kePartNumber,
									kePartName : data.kePartName,
									model : data.model,
									code : data.code,
									lotNo : data.lotNo,
									ok : data.ok,
									oid : data.oid
								}
								AUIGrid.updateRow(tbomGridID, item, event.rowIndex);
							} else {
								let item = {
									kePartNumber : data.kePartNumber,
									ok : data.ok
								}
								AUIGrid.updateRow(tbomGridID, item, event.rowIndex);
							}
						}, "GET");
					}
				}

				$(function() {
					tbomGridID = AUIGrid.create("#tbom_grid_wrap", tbom_columns, tbom_props);
					AUIGrid.addRow(tbomGridID, new Object(), "first");
					AUIGrid.bind(tbomGridID, "addRowFinish", auiAddRowHandler);
					AUIGrid.bind(tbomGridID, "cellEditEnd", auiCellEditEndHandler);

					$("#tbomDeleteBtn").click(function() {
						let checkedItems = AUIGrid.getCheckedRowItems(tbomGridID);
						for (let i = checkedItems.length - 1; i >= 0; i--) {
							let rowIndex = checkedItems[i].rowIndex;
							AUIGrid.removeRow(tbomGridID, rowIndex);
						}
					})

					$("#tbomAddBtn").click(function() {
						let item = new Object();
						AUIGrid.addRow(tbomGridID, item, "first");
					})

					$("#saveBtn").click(function() {
						let url = getCallUrl("/tbom/create");
						let _addRows = AUIGrid.getAddedRowItems(projectGridID);
						let addRows = AUIGrid.getAddedRowItems(tbomGridID);
						let params = new Object();
						params = form(params, "create_table");
						params.addRows = addRows;
						params._addRows = _addRows;
						openLayer();
						call(url, params, function(data) {
							alert(data.msg);
							if (data.result) {
								opener.loadGridData();
								self.close();
							}
						}, "POST");
					})

					selectBox("engType");

					$("#closeBtn").click(function() {
						self.close();
					})
				})

				$(window).resize(function() {
					AUIGrid.resize(tbomGridID);
				})
			</script>
		</td>
	</tr>
</table>