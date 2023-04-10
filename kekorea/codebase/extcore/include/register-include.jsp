<%@page import="e3ps.workspace.service.WorkspaceHelper"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@page import="wt.fc.Persistable"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.common.util.StringUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
JSONArray lines = new JSONArray();
if (!StringUtils.isNull(oid)) {
	Persistable per = CommonUtils.getObject(oid);
	lines = WorkspaceHelper.manager.loadAllLines(per);
}
%>
<div class="include">
	<input type="button" value="결재선 추가" title="결재선 추가" class="blue" onclick="_register();">
	<input type="button" value="결재선 삭제" title="결재선 삭제" class="red" onclick="_deleteRow_();">
	<div id="_grid_wrap_" style="height: 200px; border-top: 1px solid #3180c3; margin: 5px;"></div>
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
				showRowNumColumn : true,
				rowNumHeaderText : "번호",
				selectionMode : "multipleCells",
				showAutoNoDataMessage : false,
				showStateColumn : true,
				showRowCheckColumn : true,
				showAutoNoDataMessage : false,
				enableRowCheckShiftKey : true,
				showDragKnobColumn : true,
				enableDrag : true,
				enableMultipleDrag : true,
				enableDrop : true,
				useContextMenu : true,
				enableSorting : false,
				enableRightDownFocus : true,
				contextMenuItems : [ {
					label : "선택된 행 삭제",
					callback : contextItemHandler
				} ],
			}
			_myGridID_ = AUIGrid.create("#_grid_wrap_", columnLayout, props);
			AUIGrid.setGridData(_myGridID_,
	<%=lines%>
		);
		}

		function _register() {
			const list = AUIGrid.getGridData(_myGridID_);
			const approvals = [];
			const agrees = [];
			const receives = [];

			for (let i = 0; i < list.length; i++) {
				const type = list[i].type;
				if ("검토" === type) {
					agrees.push(list[i]);
				} else if ("결재" === type) {
					approvals.push(list[i]);
				} else if ("수신" === type) {
					receives.push(list[i]);
				}
			}
			
			const url = getCallUrl("/workspace/popup");
			const p = popup(url, 1200, 700);
			p.approvals = approvals;
			p.agrees = agrees;
			p.receives = receives;
		}

		function contextItemHandler(event) {
			const item = new Object();
			switch (event.contextIndex) {
			case 0:
				const selectedItems = AUIGrid.getSelectedItems(event.pid);
				for (let i = selectedItems.length - 1; i >= 0; i--) {
					const rowIndex = selectedItems[i].rowIndex;
					AUIGrid.removeRow(event.pid, rowIndex);
				}
				break;
			}
		}

		function _deleteRow_() {
			const checked = AUIGrid.getCheckedRowItems(_myGridID_);
			for (let i = checked.length - 1; i >= 0; i--) {
				const rowIndex = checked[i].rowIndex;
				AUIGrid.removeRow(_myGridID_, rowIndex);
			}
		}

		function setLine(agree, approval, receive) {
			AUIGrid.clearGridData(_myGridID_);

			for (let i = receive.length - 1; i >= 0; i--) {
				const item = receive[i];
				item.type = "수신";
				AUIGrid.addRow(_myGridID_, item, "first");
			}

			let sort = approval.length;
			for (let i = approval.length - 1; i >= 0; i--) {
				const item = approval[i];
				item.type = "결재";
				item.sort = sort;
				AUIGrid.addRow(_myGridID_, item, "first");
				sort--;
			}

			for (let i = agree.length - 1; i >= 0; i--) {
				const item = agree[i];
				item.type = "검토";
				AUIGrid.addRow(_myGridID_, item, "first");
			}
		}
	</script>
</div>