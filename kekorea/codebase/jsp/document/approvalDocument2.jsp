<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<%@include file="/jsp/common/layouts/include_css.jsp"%>
<%@include file="/jsp/common/layouts/include_script.jsp"%>
<!-- auigrid -->
<%@include file="/jsp/include/auigrid.jsp"%>
</head>
<body>
	<input type="hidden" name="sessionid" id="sessionid">
	<input type="hidden" name="curPage" id="curPage">
	<table class="btn_table">
		<tr>
			<td class="right">
				<input type="button" value="등록"  class="blueBtn" id="createBtn" title="등록">
			</td>
		</tr>
	</table>
	<table class="approval_table">
		<colgroup>
			<col width="150">
			<col>
		</colgroup>
		<tr>
			<th><font class="req">결재 제목</font></th>
			<td>
				<input type="text" name="name" id="name" class="AXInput wid400">
			</td>
		</tr>
		<tr>
			<th><font class="req">결재 문서</font></th>
			<td colspan="3">
				<table class="in_btn_table">
					<tr>
						<td class="add">
							<input type="button" title="문서 추가" value="문서 추가" id="addDocuments" data-context="product" data-dbl="true" data-state="INWORK">
							<input type="button" title="문서 삭제" value="문서 삭제" id="delDocuments" class="blueBtn">
						</td>
					</tr>
				</table>
				<table>
				<div id="grid_wrap1" style="height: 200px; border-top: 1px solid #3180c3;"></div>
				<script type="text/javascript">
					let myGridID1;
					const columns1 = [ {
						dataField : "number",
		                headerText : "문서번호",
		                width : "30%"
					}, {
						dataField : "name",
		                headerText : "문서제목",
		                width : "30%"
					}, {
						dataField : "",
		                headerText : "MODEL_NAME",
		                width : "30%"
					}, {
						dataField : "version",
		                headerText : "버전",
		                width : "30%"
					}, {
						dataField : "state",
		                headerText : "상태",
		                width : "30%"
					}, {
						dataField : "modifier",
		                headerText : "수정자",
		                width : "30%"
					}, {
						dataField : "modifyDate",
		                headerText : "수정일",
		                width : "30%"
					}, {
						dataField : "oid",
						headerText : "oid",
						dataType : "string",
						visible : false
					} ]
					
					function createAUIGrid1(columns1) {
						const props = {
								rowIdField : "oid",
								headerHeight : 30,
								rowHeight : 30,
// 								showRowNumColumn : true
								showRowCheckColumn : true
							};
						myGridID1 = AUIGrid.create("#grid_wrap1", columns1, props);
					}
				</script>
				</table>
				<table>
				
				</table>
			</td>
		</tr>
	</table>
</body>
</html>