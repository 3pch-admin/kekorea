<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- auigrid -->
<%@include file="/jsp/include/auigrid.jsp"%>
<table class="btn_table">
	<tr>
		<td>
			<div class="header_title">
				<i class="axi axi-subtitles"></i>
				<span>도면 일람표 등록</span>
			</div>
		</td>
		<td class="right">
			<input type="button" value="등록" id="createBtn" title="등록">
			<input type="button" value="닫기" id="closeBtn" title="닫기" class="blueBtn">
		</td>
	</tr>
</table>

<table class="create_table">
	<colgroup>
		<col width="130">
		<col width="*">
	</colgroup>
	<tr>
		<th>
			<font class="req">작업 일람표 명</font>
		</th>
		<td>
			<input type="text" name="name" id="name" class="AXInput wid300">
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
		<th>
			<font class="req">작업 내용</font>
		</th>
		<td>
			<textarea class="description" name="description" id="description"></textarea>
		</td>
	</tr>
</table>
<table class="btn_table">
	<tr>
		<td class="left">
			<input type="button" value="KEK 도면 추가" id="addKekBtn" title="KEK 도면 추가">
			<input type="button" value="KE 도면 추가" id="addKeBtn" title="KE 도면 추가" class="blueBtn">
			<input type="button" value="삭제" class="orangeBtn" id="deleteRowBtn" title="삭제">
		</td>
	</tr>
</table>
<div id="grid_wrap" style="height: 400px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "name",
		headerText : "DRAWING TITLE",
		dataType : "string",
	}, {
		dataField : "number",
		headerText : "DWG. NO",
		dataType : "string",
		width : 200
	}, {
		dataField : "latest",
		headerText : "CURRENT VER",
		dataType : "string",
		width : 100
	}, {
		dataField : "creator",
		headerText : "REV",
		dataType : "string",
		width : 100
	}, {
		dataField : "lot",
		headerText : "등록일",
		dataType : "string",
		width : 100
	}, {
		dataField : "note",
		headerText : "NOTE",
		dataType : "string",
		width : 200
	}, {
		dataField : "oid",
		headerText : "oid",
		dataType : "string",
		visible : false
	} ]

	function createAUIGrid(columnLayout) {
		const props = {
			rowIdField : "rowId",
			headerHeight : 30,
			rowHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			fillColumnSizeMode : true
		};
		myGridID = AUIGrid.create("#grid_wrap", columns, props);
	}

	$(function() {

		createAUIGrid(columns);

		$("#closeBtn").click(function() {
			self.close();
		})

		$("#deleteRowBtn").click(function() {
			let checkedItems = AUIGrid.getCheckedRowItems(myGridID);
			for (let i = checkedItems.length - 1; i >= 0; i--) {
				let rowIndex = checkedItems[i].rowIndex;
				AUIGrid.removeRow(myGridID, rowIndex);
			}
		})

		$("#createBtn").click(function() {
			let url = getCallUrl("/project/create");
			let params = new Object();
			params = form(params, "create_table");
			open();
			call(url, params, function(data) {
				alert(data.msg);
				if (data.result) {
					opener.loadGridData();
					self.close();
				} else {
					close();
				}
			}, "POST");
		})
	})
</script>