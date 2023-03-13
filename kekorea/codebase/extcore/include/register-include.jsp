<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.common.util.StringUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String height = StringUtils.getParameter(request.getParameter("height"), "200");
%>
<div class="include">
	<input type="button" value="결재선 추가" title="결재선 추가" class="blue" onclick="_register();">
	<input type="button" value="결재선 삭제" title="결재선 삭제" class="red" onclick="_deleteRow_();">
	<div id="_grid_wrap_" style="height: <%=height%>px; border-top: 1px solid #3180c3; margin: 3px 5px 3px 5px;"></div>
	<script type="text/javascript">
		let _myGridID_;
		const _columns_ = [ {
			dataField : "sort",
			headerText : "순서",
			dataType : "numeric",
			width : 80
		}, {
			dataField : "type",
			headerText : "결재타입",
			dataType : "string",
			width : 130,
		}, {
			dataField : "name",
			headerText : "이름",
			dataType : "string",
			width : 130
		}, {
			dataField : "id",
			headerText : "아이디",
			dataType : "string",
			width : 130,
		}, {
			dataField : "duty",
			headerText : "직급",
			dataType : "string",
			width : 130
		}, {
			dataField : "department_name",
			headerText : "부서",
			dataType : "string",
			width : 130
		}, {
			dataField : "email",
			headerText : "이메일",
			dataType : "string",
		} ]

		function _createAUIGrid_(columnLayout) {
			const props = {
				headerHeight : 30,
				rowHeight : 30,
				showRowNumColumn : true,
				rowNumHeaderText : "번호",
				fillColumnSizeMode : true,
				showStateColumn : true, // 상태표시 행 출력 여부
				softRemoveRowMode : false,
				showRowCheckColumn : true, // 체크 박스 출력
			}
			_myGridID_ = AUIGrid.create("#_grid_wrap_", columnLayout, props);
		}

		// 결재선 지정
		function _register() {
			const url = getCallUrl("/workspace/popup");
			popup(url, 1200, 700);
		}

		// 행 삭제
		function _deleteRow_() {
			const checked = AUIGrid.getCheckedRowItems(_myGridID_);
			for (let i = checked.length - 1; i >= 0; i--) {
				const rowIndex = checked[i].rowIndex;
				AUIGrid.removeRow(_myGridID_, rowIndex);
			}
		}

		function setLine(rows1, rows2, rows3) {
			AUIGrid.clearGridData(_myGridID_);
			//수신
			for (let i = 0; i < rows3.length; i++) {
				const item = rows3[i];
				item.type = "수신";
				AUIGrid.addRow(_myGridID_, item, "first");
			}

			//결재
			let sort = rows2.length;
			for (let i = 0; i < rows2.length; i++) {
				const item = rows2[i];
				item.type = "결재";
				item.sort = sort;
				AUIGrid.addRow(_myGridID_, item, "first");
				sort--;
			}
			//검토
			for (let i = 0; i < rows1.length; i++) {
				const item = rows1[i];
				item.type = "검토";
				AUIGrid.addRow(_myGridID_, item, "first");
			}
		}
	</script>
</div>