<%@page import="e3ps.project.issue.service.IssueHelper"%>
<%@page import="e3ps.epm.workOrder.service.WorkOrderHelper"%>
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
String mode = StringUtils.getParameter(request.getParameter("mode"));
String multi = StringUtils.getParameter(request.getParameter("multi"));
String obj = StringUtils.getParameter(request.getParameter("obj"));
String height = StringUtils.getParameter(request.getParameter("height"), "150");
JSONArray data = null;
// 객체 마다 다르게 
if ("meeting".equals(obj)) {
	// 회의록
	data = MeetingHelper.manager.jsonArrayAui(oid);
} else if ("partlist".equals(obj)) {
	// 수배표
	data = PartlistHelper.manager.jsonArrayAui(oid);
} else if ("project".equals(obj)) {
	// 도면일람표
	data = WorkOrderHelper.manager.jsonArrayAui(oid);
} else if ("issue".equals(obj)) {
	data = IssueHelper.manager.jsonArrayAui(oid);
}
%>
<input type="hidden" id="param" name="param" value="<%=oid %>">
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
	<div id="_grid_wrap" style="height: <%=height%>px; border-top: 1px solid #3180c3; margin:  5px;"></div>
	<script type="text/javascript">
	console.log(document.getElementById("param").value);//e3ps.doc.meeting.Meeting:558611-v / e3ps.doc.meeting.Meeting:558611-m
	const data = <%=data%>;console.log(data);
		let _myGridID;
		const _columns = [ {
			dataField : "projectType_name",
			headerText : "작번유형",
			dataType : "string",
			width : 80,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "customer_name",
			headerText : "거래처",
			dataType : "string",
			width : 120,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "mak_name",
			headerText : "막종",
			dataType : "string",
			width : 120,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "detail_name",
			headerText : "막종상세",
			dataType : "string",
			width : 120,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "kekNumber",
			headerText : "KEK 작번",
			dataType : "string",
			width : 100,
			<%if ("view".equals(mode)) { %>
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
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "keNumber",
			headerText : "KE 작번",
			dataType : "string",
			width : 100,
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
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "description",
			headerText : "작업 내용",
			dataType : "string",
			style : "aui-left",
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "oid",
			headerText : "",
			visible : false
		} ]

		function _createAUIGrid(columnLayout) {
			const props = {
				headerHeight : 30,
				showRowNumColumn : true,
				rowNumHeaderText : "번호",
				<%if ("create".equals(mode) || "update".equals(mode)) {%>
				showRowCheckColumn : true,
				showStateColumn : true,
				showAutoNoDataMessage : false,
				selectionMode : "singleRow",
				enableSorting : false,
				<%} else {%>
				rowHeight : 30,
				selectionMode : "multipleCells",
				filterLayerWidth : 320,
				noDataMessage : "관련 작번이 없습니다.",
				filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
				<%}%>
				<%if ("view".equals(mode)) {%>
				enableFilter : true,
				showInlineFilter : true,
				<%}%>
				// 그리드 공통속성 끝
				<%if (!Boolean.parseBoolean(multi)) {%>
				rowCheckToRadio : true,
				<%}%>
			}
			_myGridID = AUIGrid.create("#_grid_wrap", columnLayout, props);
			<%if ("view".equals(mode) || "update".equals(mode)) {%>
			AUIGrid.setGridData(_myGridID, <%=data%>);
			<%}%>
		}
		// 등록 및 수정
	<%if ("create".equals(mode) || "update".equals(mode)) {%>
		function _insert() {
			const url = getCallUrl("/project/popup?method=append&multi=<%=multi%>");
			popup(url,1500,700);
		}
	
		function append(data, callBack) {
			for (let i = 0; i < data.length; i++) {
				const item = data[i].item;
				item.sort = data.length - i;
				const isUnique = AUIGrid.isUniqueValue(_myGridID, "oid", item.oid);
				if (isUnique) {
					AUIGrid.addRow(_myGridID, item, "first");
				}
			}
			callBack(true);
		}

		// 행 삭제
		function _deleteRow() {
			const checked = AUIGrid.getCheckedRowItems(_myGridID);
			if (checked.length === 0) {
				alert("삭제할 행을 선택하세요.");
				return false;
			}
			for (let i = checked.length - 1; i >= 0; i--) {
				const rowIndex = checked[i].rowIndex;
				AUIGrid.removeRow(_myGridID, rowIndex);
			}
		}
		
	<%}%>
	
	_createAUIGrid(_columns);
	AUIGrid.resize(_myGridID);
	window.addEventListener("resize", function() {
		AUIGrid.resize(_myGridID);
	});
	</script>
</div>