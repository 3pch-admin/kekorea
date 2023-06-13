<%@page import="e3ps.doc.service.DocumentHelper"%>
<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="e3ps.project.service.ProjectHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
String mode = request.getParameter("mode");
String height = StringUtils.replaceToValue(request.getParameter("height"), "150");
boolean isView = "view".equals(mode);
boolean isCreate = "create".equals(mode);
boolean isUpdate = "update".equals(mode);
%>

<div class="include">
	<%
	if (isCreate || isUpdate) {
	%>
	<input type="button" value="부품 추가" title="부품 추가" class="blue" onclick="insert7();">
	<input type="button" value="부품 삭제" title="부품 삭제" class="red" onclick="deleteRow7();">
	<%
	}
	%>
	<div id="grid_wrap7" style="height: <%=height%>px; border-top: 1px solid #3180c3; margin: 5px;"></div>
	<script type="text/javascript">
		let myGridID7;
		const columns7 = [ {
			dataField : "dwgNo",
			headerText : "DWG NO",
			dataType : "string",
			width : 250,
			renderer : {
				type : "LinkRenderer",
				baseUrl : "javascript",
				jsCallback : function(rowIndex, columnIndex, value, item) {
					const oid = item.oid;
					const url = getCallUrl("/part/view?oid=" + oid);
					popup(url, 1500, 500);
				}
			},
		}, {
			dataField : "name",
			headerText : "NAME",
			dataType : "string",
			width : 250,
			renderer : {
				type : "LinkRenderer",
				baseUrl : "javascript",
				jsCallback : function(rowIndex, columnIndex, value, item) {
					const oid = item.oid;
					const url = getCallUrl("/part/view?oid=" + oid);
					popup(url, 1500, 500);
				}
			},
		}, {
			dataField : "nameOfParts",
			headerText : "NAME_OF_PARTS",
			dataType : "string",
		}, {
			dataField : "version",
			headerText : "버전",
			dataType : "string",
			width : 80,
		}, {
			dataField : "state",
			headerText : "상태",
			dataType : "string",
			width : 80,
		}, {
			dataField : "creator",
			headerText : "작성자",
			dataType : "string",
			width : 100
		}, {
			dataField : "createdDate_txt",
			headerText : "작성일",
			dataType : "string",
			width : 100
		}, {
			dataField : "oid",
			visible : false
		} ]

		function createAUIGrid7(columnLayout) {
			const props = {
				headerHeight : 30,
				showRowNumColumn : true,
				rowNumHeaderText : "번호",
				showAutoNoDataMessage : false,
				enableSorting : false,
				softRemoveRowMode : false,
// 				autoGridHeight : true,
				<%if (isCreate || isUpdate) {%>
				showRowCheckColumn : true,
				showStateColumn : true,
				<%}%>
			}
			myGridID7 = AUIGrid.create("#grid_wrap7", columnLayout, props);
			<%if (isView || isUpdate) {%>
			AUIGrid.setGridData(myGridID7, <%=DocumentHelper.manager.jsonAuiPart(oid)%>);
			<%}%>
		}

		function insert7() {
			const url = getCallUrl("/part/popup?method=append&multi=true");
			popup(url, 1500, 800);
		}

		function append(data, callBack) {
			for (let i = 0; i < data.length; i++) {
				const item = data[i].item;
				const isUnique = AUIGrid.isUniqueValue(myGridID7, "oid", item.oid);
				if (isUnique) {
					AUIGrid.addRow(myGridID7, item, "first");
				}
			}
			callBack(true);
		}

		function deleteRow7() {
			const checked = AUIGrid.getCheckedRowItems(myGridID7);
			if (checked.length === 0) {
				alert("삭제할 행을 선택하세요.");
				return false;
			}

			for (let i = checked.length - 1; i >= 0; i--) {
				const rowIndex = checked[i].rowIndex;
				AUIGrid.removeRow(myGridID7, rowIndex);
			}
		}
	</script>
</div>