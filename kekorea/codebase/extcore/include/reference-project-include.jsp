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
	<input type="button" value="작번 추가" title="작번 추가" onclick="_insert();">
	<input type="button" value="행 삭제" title="행 삭제" class="red" onclick="_deleteRow();">
	<input type="button" value="저장" title="저장" class="blue">
	<%
	}
	%>
	<div id="_grid_wrap_" style="height: <%=height%>px; border-top: 1px solid #3180c3; margin: 3px 5px 3px 5px;"></div>
	<script type="text/javascript">
		let _myGridID_;
		const _columns_ = [ {
			dataField : "kekNumber",
			headerText : "KEK 작번",
			dataType : "string",
			width : 130
		}, {
			dataField : "projectType_name",
			headerText : "작번유형",
			dataType : "string",
			width: 100
		}, {
			dataField : "description",
			headerText : "작업내용",
			dataType : "string",
			style : "left indent5"
		}, {
			dataField : "mak_name",
			headerText : "막종",
			dataType : "string",width : 130
		}, {
			dataField : "detail_name",
			headerText : "막종상세",
			dataType : "string",width : 130
		}, {
			dataField : "pdate",
			headerText : "발행일",
			dataType : "date",
			formatString : "yyyy-mm-dd",
			width : 100
		} , {
			dataField : "pdate",
			headerText : "요청납기일",
			dataType : "date",
			formatString : "yyyy-mm-dd",
			width : 100
		} ]

		function _createAUIGrid_(columnLayout) {
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
				<%}%>
				<%if (!Boolean.parseBoolean(multi)) {%>
				rowCheckToRadio : true
				<%}%>
			}
			_myGridID_ = AUIGrid.create("#_grid_wrap_", columnLayout, props);
			<%if ("view".equals(mode)) {%>
			AUIGrid.setGridData(_myGridID_, <%//=data%>);
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