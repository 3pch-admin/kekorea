<%@page import="net.sf.json.JSONArray"%>
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
%>
<div class="include">
	<%
	// 등록 및 수정
	if ("create".equals(mode) || "update".equals(mode)) {
	%>
	<input type="button" value="문서 추가" title="문서 추가" class="blue" onclick="_insert();">
	<input type="button" value="문서 삭제" title="문서 삭제" class="red" onclick="_deleteRow();">
	<%
	}
	%>
	<div id="_grid_wrap" style="height: <%=height%>px; border-top: 1px solid #3180c3; margin: 3px 5px 3px 5px;"></div>
	<script type="text/javascript">
		let _myGridID;
		const _columns = [ {
			dataField : "number",
			headerText : "문서번호",
			dataType : "string",
			width : 100
		}, {
			dataField : "name",
			headerText : "문서제목",
			dataType : "string",
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
			dataField : "version",
			headerText : "버전",
			dataType : "string",
			width : 80
		}, {
			dataField : "state",
			headerText : "상태",
			dataType : "string",
			width : 100,
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
		},{
			dataField : "modifier",
			headerText : "수정자",
			dataType : "string",
			width : 100
		}, {
			dataField : "modifiedDate",
			headerText : "수정일",
			dataType : "date",
			formatString : "yyyy-mm-dd",
			width : 100
		}, {
			dataField : "oid",
			visible : false,
			dataType : "string"
		} ]

		function _createAUIGrid(columnLayout) {
			const props = {
				rowIdField : "oid",
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
			}
			_myGridID = AUIGrid.create("#_grid_wrap", columnLayout, props);
			<%if ("view".equals(mode)) {%>
			AUIGrid.setGridData(_myGridID, <%=data%>);
			<%}%>
		}
		// 등록 및 수정
	<%if ("create".equals(mode) || "update".equals(mode)) {%>
		function _insert() {
			const url = getCallUrl("/document/popup?method=append&multi=<%=multi%>");
			popup(url);
		}
	
		function append(data) {
// 			rowIdField : "oid",
			for (let i = 0; i < data.length; i++) {
				let item = data[i].item;
				let isUnique = AUIGrid.isUniqueValue(_myGridID, "oid", item.oid);
// 				alert(isUnique);
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