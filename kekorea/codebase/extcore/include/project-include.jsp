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
JSONArray data = null;
// 객체 마다 다르게 
if ("meeting".equals(obj)) {
	// 회의로
	data = MeetingHelper.manager.jsonArrayAui(oid);
} else if("partlist".equals(obj)) {
	// 수배표
	data = PartlistHelper.manager.jsonArrayAui(oid);
}
%>
<div class="include">
	<%
	// 등록 및 수정
	if ("create".equals(mode) || "update".equals(mode)) {
	%>
	<input type="button" value="작번 추가" title="작번 추가" class="blue" onclick="_insert();">
	<input type="button" value="행 삭제" title="행 삭제" class="red" onclick="_deleteRow();">
	<%
	}
	%>
	<div id="_grid_wrap" style="height: <%=height %>px; border-top: 1px solid #3180c3; margin: 3px 5px 3px 5px;"></div>
	<script type="text/javascript">
		let _myGridID;
		const _columns = [ {
			dataField : "projectType_name",
			headerText : "작번유형",
			dataType : "string",
			width : 80
		}, {
			dataField : "customer_name",
			headerText : "거래처",
			dataType : "string",
			width : 120
		}, {
			dataField : "mak_name",
			headerText : "막종",
			dataType : "string",
			width : 120
		}, {
			dataField : "detail_name",
			headerText : "막종상세",
			dataType : "string",
			width : 120
		}, {
			dataField : "kekNumber",
			headerText : "KEK 작번",
			dataType : "string",
			width : 130,
			<%if ("view".equals(mode)) {%>
			renderer : {
				type : "LinkRenderer",
				baseUrl : "javascript", // 자바스크립 함수 호출로 사용하고자 하는 경우에 baseUrl 에 "javascript" 로 설정
				// baseUrl 에 javascript 로 설정한 경우, 링크 클릭 시 callback 호출됨.
				jsCallback : function(rowIndex, columnIndex, value, item) {
					const oid = item.oid;
					alert(oid);
				}
			},			
			<%}%>
		}, {
			dataField : "keNumber",
			headerText : "KE 작번",
			dataType : "string",
			width : 130,
			<%if ("view".equals(mode)) {%>
			renderer : {
				type : "LinkRenderer",
				baseUrl : "javascript", // 자바스크립 함수 호출로 사용하고자 하는 경우에 baseUrl 에 "javascript" 로 설정
				// baseUrl 에 javascript 로 설정한 경우, 링크 클릭 시 callback 호출됨.
				jsCallback : function(rowIndex, columnIndex, value, item) {
					const oid = item.oid;
					alert(oid);
				}
			},			
			<%}%>			
		}, {
			dataField : "description",
			headerText : "작업 내용",
			dataType : "string",
			style : "left indent10",
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
			AUIGrid.setGridData(_myGridID, <%=data%>);
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