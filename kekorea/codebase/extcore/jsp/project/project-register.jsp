<%@page import="e3ps.project.Project"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="java.util.Map"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
JSONArray data = (JSONArray) request.getAttribute("data");
String oid = (String) request.getAttribute("oid");
Project project = (Project) request.getAttribute("project");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js?v=1010"></script>
</head>
<body>
	<form>
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						작번(<%=project.getKekNumber()%>) 산출물 결재 등록
					</div>
				</td>
				<td class="right">
					<input type="button" value="결재등록" title="결재등록" onclick="registerLine();">
				</td>
			</tr>
		</table>
		<table class="create-table">
			<colgroup>
				<col width="130">
				<col width="*">
			</colgroup>
			<tr>
				<th class="req lb">결재 제목</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-700">
				</td>
			</tr>
			<tr>
				<th class="req lb">결재 의견</th>
				<td class="indent5">
					<textarea id="description" name="description" rows="5"></textarea>
				</td>
			</tr>
			<tr>
				<th class="req lb">결재 산출물</th>
				<td>
					<div class="include">
						<input type="button" value="삭제" title="삭제" class="red" onclick="deleteRow();">
						<div id="grid_wrap" style="height: 300px; border-top: 1px solid #3180c3; margin: 3px 5px 3px 5px;"></div>
						<script type="text/javascript">
							let myGridID;
							const columns = [ {
								dataField : "type",
								headerText : "산출물 타입",
								dataType : "string",
								width : 130
							}, {
								dataField : "taskName",
								headerText : "태스크 명",
								dataType : "string",
								width : 150
							}, {
								dataField : "name",
								headerText : "산출물 제목",
								dataType : "string",
								style : "aui-left",
							}, {
								dataField : "version",
								headerText : "버전",
								dataType : "string",
								width : 100,
							}, {
								dataField : "state",
								headerText : "상태",
								dataType : "string",
								width : 100
							}, {
								dataField : "creator",
								headerText : "작성자",
								dataType : "date",
								width : 100
							}, {
								dataField : "createdDate_txt",
								headerText : "작성일",
								dataType : "string",
								width : 100
							}, {
								dataField : "primary",
								headerText : "주 첨부파일",
								dataType : "date",
								width : 130,
								renderer : {
									type : "TemplateRenderer"
								}
							}, {
								dataField : "secondary",
								headerText : "첨부파일",
								dataType : "string",
								width : 200,
								renderer : {
									type : "TemplateRenderer"
								}
							}, {
								dataField : "oid",
								visible : false,
								dataType : "string"
							} ]

							function createAUIGrid(columnLayout) {
								const props = {
									headerHeight : 30,
									showRowNumColumn : true,
									rowNumHeaderText : "번호",
									showStateColumn : true,
									showRowCheckColumn : true,
									enableSorting : false,
									selectionMode : "multipleCells",
									softRemoveRowMode : false,
									showAutoNoDataMessage : false,
								}
								myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
								AUIGrid.setGridData(myGridID,
						<%=data%>
							);
							}

							function deleteRow() {
								const checked = AUIGrid.getCheckedRowItems(myGridID);
								for (let i = checked.length - 1; i >= 0; i--) {
									const rowIndex = checked[i].rowIndex;
									AUIGrid.removeRow(myGridID, rowIndex);
								}
							}
						</script>
					</div>
				</td>
			</tr>
			<tr>
				<th class="req lb">결재</th>
				<td>
					<jsp:include page="/extcore/jsp/common/approval-register.jsp">
						<jsp:param value="" name="oid" />
						<jsp:param value="create" name="mode" />
					</jsp:include>
				</td>
			</tr>
		</table>
		<script type="text/javascript">
			function registerLine() {
				const url = getCallUrl("/project/register");
				const params = new Object();
				const addRows = AUIGrid.getGridData(myGridID); // 문서
				const addRows8 = AUIGrid.getAddedRowItems(myGridID8); // 결재
				const name = document.getElementById("name");
				const description = document.getElementById("description").value;

				if (isNull(name.value)) {
					alert("결재 제목을 입력하세요.");
					name.focus();
					return false;
				}

				if (addRows8.length === 0) {
					alert("결재선을 지정하세요.");
					_register();
					return false;
				}

				if (!confirm("작번 산출물 결재를 등록하시겠습니까?")) {
					return false;
				}
				params.name = name.value;
				params.description = description;
				params.addRows = addRows;
				toRegister(params, addRows8);
				parent.openLayer();
				call(url, params, function(data) {
					alert(data.msg);
					if (data.result) {
						const subLoc = parent.document.getElementById("subLoc");
						subLoc.innerHTML = "나의 업무 > 결재함";
						document.location.href = getCallUrl("/workspace/approval");
					} else {
						parent.closeLayer();
					}
				})
			}

			document.addEventListener("DOMContentLoaded", function() {
				parent.closeLayer();
				toFocus("name");
				createAUIGrid(columns);
				createAUIGrid8(columns8);
				AUIGrid.resize(myGridID);
				AUIGrid.resize(myGridID8);
			});

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID);
				AUIGrid.resize(myGridID8);
			});
		</script>
	</form>
</body>
</html>