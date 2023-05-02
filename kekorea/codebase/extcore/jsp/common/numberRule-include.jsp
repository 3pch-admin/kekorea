<%@page import="e3ps.doc.service.DocumentHelper"%>
<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="e3ps.project.service.ProjectHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
String mode = request.getParameter("mode");
boolean isView = "view".equals(mode);
boolean isCreate = "create".equals(mode);
boolean isUpdate = "update".equals(mode);
%>

<div class="include">
	<%
	if (isCreate || isUpdate) {
	%>
	<input type="button" value="도번 추가" title="도번 추가" class="blue" onclick="insert11();">
	<input type="button" value="도번 삭제" title="도번 삭제" class="red" onclick="deleteRow11();">
	<%
	}
	%>
	<div id="grid_wrap11" style="height: 80px; border-top: 1px solid #3180c3; margin: 5px;"></div>
	<script type="text/javascript">
		let myGridID11;
		const columns11 = [ {
			dataField : "number",
			headerText : "도면번호",
			dataType : "string",
			width : 100,
			editable : false,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "size_txt",
			headerText : "사이즈",
			dataType : "string",
			width : 80,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "lotNo",
			headerText : "LOT",
			dataType : "numeric",
			width : 80,
			formatString : "###0",
			filter : {
				showIcon : true,
				inline : true,
				displayFormatValues : true
			},
		}, {
			dataField : "unitName",
			headerText : "UNIT NAME",
			dataType : "string",
			width : 200,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "name",
			headerText : "도번명",
			dataType : "string",
			width : 250,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "businessSector_txt",
			headerText : "사업부문",
			dataType : "string",
			width : 200,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "classificationWritingDepartments_txt",
			headerText : "작성부서구분",
			dataType : "string",
			width : 150,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "writtenDocuments_txt",
			headerText : "작성문서구분",
			dataType : "string",
			width : 150,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "version",
			headerText : "버전",
			dataType : "string",
			width : 100,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "state",
			headerText : "상태",
			dataType : "string",
			width : 80,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "creator",
			headerText : "작성자",
			dataType : "string",
			width : 100,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "createdDate_txt",
			headerText : "작성일",
			dataType : "string",
			width : 100,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "modifier",
			headerText : "수정자",
			dataType : "string",
			width : 100,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "modifiedDate_txt",
			headerText : "수정일",
			dataType : "string",
			width : 100,
			filter : {
				showIcon : true,
				inline : true
			},
		} ]

		function createAUIGrid11(columnLayout) {
			const props = {
				headerHeight : 30,
				showRowNumColumn : true,
				rowNumHeaderText : "번호",
				showAutoNoDataMessage : false,
				enableSorting : false,
				softRemoveRowMode : false,
				rowCheckToRadio : true,
				<%if (isCreate || isUpdate) {%>
				showRowCheckColumn : true,
				showStateColumn : true,
				<%}%>
			}
			myGridID11 = AUIGrid.create("#grid_wrap11", columnLayout, props);
			<%if (isView || isUpdate) {%>
			AUIGrid.setGridData(myGridID11, <%=DocumentHelper.manager.jsonAuiNumberRule(oid)%>);
			<%}%>
		}

		function insert11() {
			const url = getCallUrl("/numberRule/popup?method=append11&multi=true");
			popup(url, 1500, 600);
		}

		function append11(data, callBack) {
			AUIGrid.clearGridData(myGridID11);
			AUIGrid.addRow(myGridID11, data.item, "first");
			callBack(true);
		}

		function deleteRow11() {
			const checked = AUIGrid.getCheckedRowItems(myGridID11);
			if (checked.length === 0) {
				alert("삭제할 행을 선택하세요.");
				return false;
			}

			for (let i = checked.length - 1; i >= 0; i--) {
				const rowIndex = checked[i].rowIndex;
				AUIGrid.removeRow(myGridID11, rowIndex);
			}
		}
	</script>
</div>