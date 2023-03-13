<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- AUIGrid -->
<%@include file="/extcore/include/auigrid.jsp"%>
<%
String location = (String) request.getAttribute("location");
String container = (String) request.getAttribute("container");
String method = (String) request.getAttribute("method");
boolean multi = (boolean) request.getAttribute("multi");
%>
<!-- 폴더 그리드 리스트 -->
<table class="button-table">
	<tr>
		<td class="right">
			<input type="button" value="선택" title="선택" class="blue" onclick="<%=method %>();">
			<input type="button" value="닫기" title="닫기" class="red" onclick="self.close();">
		</td>
	</tr>
</table>
<div id="_grid_wrap" style="height: 500px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
	let _myGridID;
	const _columns = [ {
		dataField : "name",
		headerText : "폴더명",
		dataType : "string",
		filter : {
			showIcon : true,
			inline : true
		},		
	} ]

	function _createAUIGrid(columnLayout) {
		const props = {
			headerHeight : 30,
			rowHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			fillColumnSizeMode : true,
			selectionMode: "singleRow",
			enableFilter : true, // 필터 사용 여부
			showInlineFilter : true,		
			displayTreeOpen : true,
			showRowCheckColumn : true,
			<%
				if(!multi) {
			%>
			rowCheckToRadio : true
			<%
				}
			%>
		}
		_myGridID = AUIGrid.create("#_grid_wrap", columnLayout, props);
		loadFolderTree();
		AUIGrid.bind(_myGridID, "cellClick", auiCellClickHandler);
		AUIGrid.bind(_myGridID, "cellDoubleClick", auiCellDoubleClick);
	}

	function auiCellClickHandler(event) {
		const item = event.item;
		const rowIdField = AUIGrid.getProp(event.pid, "rowIdField"); // rowIdField 얻기
		const rowId = item[rowIdField];
		if(AUIGrid.isCheckedRowById(event.pid, rowId)) {
			AUIGrid.addUncheckedRowsByIds(event.pid, rowId);
		} else {
			AUIGrid.addCheckedRowsByIds(event.pid, rowId);
		}
	}
	
	function auiCellDoubleClick(event) {
		const item = event.item;
		opener.<%=method%>(item);
		self.close();
	}

	
	function loadFolderTree() {
		const location = decodeURIComponent("<%=location%>");
		const url = getCallUrl("/loadFolderTree");
		const params = new Object();
		params.location = location;
		params.container = "<%=container%>";
		call(url, params, function(data) {
			AUIGrid.setGridData(_myGridID, data.list);
		});
	}
	
	
	
	function <%=method%>() {
		const checkedItems = AUIGrid.getCheckedRowItems(_myGridID);
		if(checkedItems.length == 0) {
			alert("폴더를 선택하세요.");
			return false;
		}
		<%
			if(!multi) {
		%>
		const item = checkedItems[0].item;
		opener.<%=method%>(item);
		self.close();
		<%
			}
		%>
	}
	
	// jquery 삭제를 해가는 쪽으로 한다..
	document.addEventListener("DOMContentLoaded", function() {
		// DOM이 로드된 후 실행할 코드 작성
		_createAUIGrid(_columns);
		AUIGrid.resize(_myGridID);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(_myGridID);
	});
</script>
