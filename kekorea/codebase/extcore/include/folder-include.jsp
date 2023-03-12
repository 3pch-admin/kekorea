<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String location = request.getParameter("location");
String container = request.getParameter("container");
String mode = request.getParameter("mode");
String height = request.getParameter("height");
%>
<!-- 폴더 그리드 리스트 -->
<div id="_grid_wrap" style="height: <%=height%>px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
	let _myGridID;
	const _columns = [ {
		dataField : "name",
		headerText : "폴더명",
		dataType : "string",
	} ]

	function _createAUIGrid(columnLayout) {
		const props = {
			rowIdField : "oid",
			headerHeight : 30,
			rowHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			fillColumnSizeMode : true,
			selectionMode: "singleRow",
		}
		_myGridID = AUIGrid.create("#_grid_wrap", columnLayout, props);
		loadFolderTree();
		AUIGrid.bind(_myGridID, "selectionChange", auiGridSelectionChangeHandler);
	}
	
	let timerId = null;
	function auiGridSelectionChangeHandler(event) {
		<%
			if("list".equals(mode)) {
		%>
		// 500ms 보다 빠르게 그리드 선택자가 변경된다면 데이터 요청 안함
		if (timerId) {
			clearTimeout(timerId);
		}

		timerId = setTimeout(function () {
			// 선택 대표 셀 정보 
			const primeCell = event.primeCell;
			// 대표 셀에 대한 전체 행 아이템
			const rowItem = primeCell.item;
			const oid = rowItem.oid; // oid로 할지 location 으로 할지...
			// 히든 값 세팅
			document.getElementById("oid").value = oid;
// 			requestMyData("./data/getJsonDetails.php?id=" + custId, myGridID2);
			loadGridData();
		}, 500);  
		<%
			}
		%>
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
</script>
