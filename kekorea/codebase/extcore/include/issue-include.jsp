<%@page import="e3ps.bom.partlist.service.PartlistHelper"%>
<%@page import="e3ps.doc.meeting.service.MeetingHelper"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.common.util.StringUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = StringUtils.getParameter(request.getParameter("oid"));
String mode = StringUtils.getParameter(request.getParameter("mode"), "create");
String multi = StringUtils.getParameter(request.getParameter("multi"), "false");
String obj = StringUtils.getParameter(request.getParameter("obj"));
String height = StringUtils.getParameter(request.getParameter("height"), "150");
JSONArray data = new JSONArray();
%>
<div class="include">
	<%
	// 등록 및 수정
	if ("create".equals(mode) || "update".equals(mode)) {
	%>
	<input type="button" value="특이사항 등록" title="특이사항 등록" onclick="_insert();">
	<input type="button" value="행 삭제" title="행 삭제" class="red" onclick="_deleteRow();">
	<input type="button" value="저장" title="저장" class="blue">
	<%
	}
	%>
	<div id="_grid_wrap" style="height: <%=height %>px; border-top: 1px solid #3180c3; margin: 3px 5px 3px 5px;"></div>
	<script type="text/javascript">
		let _myGridID;
		const _columns = [ {
			dataField : "projectType_name",
			headerText : "제목",
			dataType : "string",
			width : 200
		}, {
			dataField : "customer_name",
			headerText : "내용",
			dataType : "string",
		}, {
			dataField : "creator",
			headerText : "작성자",
			dataType : "string",
			width : 100
		}, {
			dataField : "createdDate",
			headerText : "작성일",
			dataType : "date",
			formatString : "yyyy-mm-dd",
			width : 100
		}, {
			dataField : "secondary",
			headerText : "첨부파일",
			width : 150
		} ]

		function _createAUIGrid(columnLayout) {
			const props = {
				headerHeight : 30,
				rowHeight : 30,
				showRowNumColumn : true,
				rowNumHeaderText : "번호",
				fillColumnSizeMode : true,
				<%if ("create".equals(mode) || "update".equals(mode)) {%>
				showStateColumn : true, // 상태표시 행 출력 여부
				softRemoveRowMode : false,
				showRowCheckColumn : true, // 체크 박스 출력
				<%
				}
				%>
				<%
					if(!Boolean.parseBoolean(multi)) {
				%>
				rowCheckToRadio : true
				<%
					}
				%>
			}
			_myGridID = AUIGrid.create("#_grid_wrap", columnLayout, props);
			<%if ("view".equals(mode)) {%>
			AUIGrid.setGridData(_myGridID, <%//=data%>);
			<%}%>
		}
		// 등록 및 수정
	<%if ("create".equals(mode) || "update".equals(mode)) {%>
		function _insert() {
			const url = getCallUrl("/project/popup?method=append&multi=<%=multi%>");
			popup(url);
		}
	
		function append(data) {
			for (let i = 0; i < data.length; i++) {
				let item = data[i].item;
				let isUnique = AUIGrid.isUniqueValue(_myGridID, "oid", item.oid);
				if (isUnique) {
					AUIGrid.addRow(_myGridID, item, "first");
				}
			}
		}

		// 행 삭제
		function _deleteRow() {
			let checked = AUIGrid.getCheckedRowItems(_myGridID);
			for (let i = checked.length - 1; i >= 0; i--) {
				let rowIndex = checked[i].rowIndex;
				AUIGrid.removeRow(_myGridID, rowIndex);
			}
		}
	<%}%>
	</script>
</div>