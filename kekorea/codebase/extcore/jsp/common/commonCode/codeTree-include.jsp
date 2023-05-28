<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String height = request.getParameter("height");
%>
<!-- 폴더 그리드 리스트 -->
<div id="_grid_wrap" style="height: <%=height%>px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
	let _myGridID;
	const _columns = [ {
		dataField : "name",
		headerText : "코드타입",
		dataType : "string",
		filter : {
			showIcon : true,
			inline : true
		},				
	} ]

	function _createAUIGrid(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			selectionMode: "multipleCells",
			enableFilter : true, 
			showInlineFilter : true,		
			displayTreeOpen : true,
			forceTreeView : true
		}
		_myGridID = AUIGrid.create("#_grid_wrap", columnLayout, props);
		loadTree();
		AUIGrid.bind(_myGridID, "cellClick", auiCellClick);
		AUIGrid.bind(_myGridID, "ready", auiReadyHandler);
	}

	function auiReadyHandler() {
		AUIGrid.showItemsOnDepth(_myGridID, 2);
	}
	
	let timerId = null;
	function auiCellClick(event) {
		if (timerId) {
			clearTimeout(timerId);
		}

		timerId = setTimeout(function () {
			const primeCell = event.item;
			const codeType = primeCell.codeType;
			
			if(codeType === "DETAIL" || codeType === "INSTALL") {
				AUIGrid.hideColumnByDataField(myGridID, "parent_name");	
			} else {
				AUIGrid.showColumnByDataField(myGridID, "parent_name");	
			}
			
			$("#codeType").bindSelectSetValue(codeType);
			loadGridData();
		}, 500);  
	}
	
	
	function loadTree() {
		const url = getCallUrl("/commonCode/loadTree");
		const params = new Object();
		call(url, params, function(data) {
			console.log(data);
			AUIGrid.setGridData(_myGridID, data.list);
		});
	}
</script>
