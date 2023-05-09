<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="e3ps.project.service.ProjectHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
String mode = request.getParameter("mode");
String height = StringUtils.replaceToValue(request.getParameter("height"), "150");
boolean multi = StringUtils.parseBoolean(request.getParameter("multi"), true);
boolean isView = "view".equals(mode);
boolean isCreate = "create".equals(mode);
boolean isUpdate = "update".equals(mode);
%>

<div class="include">
	<%
	if (isCreate || isUpdate) {
	%>
	<input type="button" value="작번 추가" title="작번 추가" class="blue" onclick="insert9();">
	<input type="button" value="작번 삭제" title="작번 삭제" class="red" onclick="deleteRow9();">
	<%
	}
	%>
	<div id="grid_wrap9" style="height: <%=height%>px; border-top: 1px solid #3180c3; margin: 5px;"></div>
	<script type="text/javascript">
		let myGridID9;
		const columns9 = [ {
			dataField : "kekNumber",
			headerText : "KEK 작번",
			dataType : "string",
			width : 100,
			<%
				if(isView) {
			%>
			renderer : {
				type : "LinkRenderer",
				baseUrl : "javascript",
				jsCallback : function(rowIndex, columnIndex, value, item) {
					const oid = item.oid;
					const url = getCallUrl("/project/info?oid=" + oid);
					popup(url);
				}
			},
			<%
				}
			%>
		}, {
			dataField : "keNumber",
			headerText : "KE 작번",
			dataType : "string",
			width : 100,
			<%
				if(isView) {
			%>
			renderer : {
				type : "LinkRenderer",
				baseUrl : "javascript",
				jsCallback : function(rowIndex, columnIndex, value, item) {
					const oid = item.oid;
					const url = getCallUrl("/project/info?oid=" + oid);
					popup(url);
				}
			},
			<%
				}
			%>
		}, {
			dataField : "projectType_name",
			headerText : "작번유형",
			dataType : "string",
			width : 80,
		}, {
			dataField : "customer_name",
			headerText : "거래처",
			dataType : "string",
			width : 120,
		}, {
			dataField : "install_name",
			headerText : "설치장소",
			dataType : "string",
			width : 120,
		}, {
			dataField : "mak_name",
			headerText : "막종",
			dataType : "string",
			width : 120,
		}, {
			dataField : "detail_name",
			headerText : "막종상세",
			dataType : "string",
			width : 120,
		}, {
			dataField : "description",
			headerText : "작업 내용",
			dataType : "string",
			style : "aui-left",
		}, {
			dataField : "oid",
			visible : false
		} ]

		function createAUIGrid9(columnLayout) {
			const props = {
				headerHeight : 30,
				showRowNumColumn : true,
				rowNumHeaderText : "번호",
				showAutoNoDataMessage : false,
				enableSorting : false,
				softRemoveRowMode : false,
				<%if (isCreate || isUpdate) {%>
				showRowCheckColumn : true,
				showStateColumn : true,
				<%}%>
				<%
					if(!multi) {
				%>
				rowCheckToRadio : true
				<%
					}
				%>
			}
			myGridID9 = AUIGrid.create("#grid_wrap9", columnLayout, props);
			<%if (isView || isUpdate) {%>
			AUIGrid.setGridData(myGridID9, <%=ProjectHelper.manager.jsonAuiProject(oid)%>);
			<%}%>
		}

		function insert9() {
			const url = getCallUrl("/project/popup?method=append&multi=<%=multi%>");
			popup(url, 1500, 700);
		}

		function append(data, callBack) {
			for (let i = 0; i < data.length; i++) {
				const item = data[i].item;
				const isUnique = AUIGrid.isUniqueValue(myGridID9, "oid", item.oid);
				if (isUnique) {
					<%
						if(!multi) {
					%>
					// 멀티 아닐경우 그리드 데이터 클리어
					AUIGrid.clearGridData(myGridID9);
					<%
						}
					%>
					AUIGrid.addRow(myGridID9, item, "first");
				}
			}
			callBack(true);
		}

		function deleteRow9() {
			const checked = AUIGrid.getCheckedRowItems(myGridID9);
			if (checked.length === 0) {
				alert("삭제할 행을 선택하세요.");
				return false;
			}

			for (let i = checked.length - 1; i >= 0; i--) {
				const rowIndex = checked[i].rowIndex;
				AUIGrid.removeRow(myGridID9, rowIndex);
			}
		}
	</script>
</div>