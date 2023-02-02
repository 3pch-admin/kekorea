<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<%@include file="/jsp/common/layouts/include_css.jsp"%>
<%@include file="/jsp/common/layouts/include_script.jsp"%>
<!-- auigrid -->
<%@include file="/jsp/include/auigrid.jsp"%>
</head>
<body onload="loadGridData();">
	<input type="hidden" name="sessionid" id="sessionid">
	<input type="hidden" name="curPage" id="curPage">
	<table class="search_table">
	<colgroup>
			<col width="130">
			<col width="*">
			<col width="130">
			<col width="*">
			<col width="130">
			<col width="*">
			<col width="130">
			<col width="*">
	</colgroup>
	<tr>
		<th>산출물 분류</th>
		<td colspan="8">
			<input type="text" name="name" class="AXInput wid200">
		</td>	
	</tr>
	<tr>
		<th>항목</th>
		<td>
			<input type="text" name="name" class="AXInput wid200">
		</td>	
		<th>산출물 번호</th>
		<td>
			<input type="text" name="name" class="AXInput wid200">
		</td>	
		<th>개선내용</th>
		<td>
			<input type="text" name="name" class="AXInput wid200">
		</td>	
		<th>막종</th>
		<td>
			<input type="text" name="name" class="AXInput wid200">
		</td>	
	</tr>
	<tr>
		<th>작성자</th>
		<td>
			<input type="text" name="name" class="AXInput wid200">
		</td>	
		<th>작성일</th>
		<td>
			<input type="text" name="name" class="AXInput wid200">
		</td>	
		<th>적용여부</th>
		<td>
			<input type="text" name="name" class="AXInput wid200">
		</td>	
		<th>상태</th>
		<td>
			<input type="text" name="name" class="AXInput wid200">
		</td>	
	</tr>	
	</table>
	<!-- button table -->
	<table class="btn_table">
		<tr>
			<td class="right">
				<input type="button" value="등록" class="redBtn" id="addBtn" title="등록">
			</td>
		</tr>
	</table>
	<div id="grid_wrap" style="height: 750px; border-top: 1px solid #3180c3;"></div>
</body>
<script type="text/javascript">
	let myGridID;
	const columns = [{
		dataField : "item",
		headerText : "항목",
		dataType : "string",
		width : "10%"
	}, {
		dataField : "improvements",
		headerText : "개선내용",
		dataType : "string",
		width : "25%"
	}, {
		dataField : "improvement",
		headerText : "개선책",
		dataType : "string",
		width : "25%"
	}, {
		dataField : "",
		headerText : "버전",
		dataType : "string",
		width : "5%"
	}, {
		dataField : "",
		headerText : "작성자",
		dataType : "string",
		width : "5%"
	}, {
		dataField : "",
		headerText : "적용/미적용",
		dataType : "string",
		width : "10%",
// 		renderer : {
// 			type : "DropDownListRenderer",
// 			list : ["적용완료", "일부적용", "미적용", "검토중"]
// 		}
	}, {
		dataField : "",
		headerText : "막종",
		dataType : "string",
		width : "10%",
// 		renderer : {
// 			type : "DropDownListRenderer",
// 			list : ["PYRO", "MT-ZrO2", "MT-HfO2", "HDPL", "LTM-TIN", "공통"]
// 		}
	}, {
		dataField : "",
		headerText : "미리보기",
		dataType : "string",
		width : "5%",
// 		renderer : {
			
// 		}
	}, {
		dataField : "",
		headerText : "파일",
		dataType : "",
		width : "5%",
// 		renderer : {
// 			type : "IconRenderer",
// 			iconFunction : function(rowIndex, columnIndex, value, iten) {
				
// 			}
// 		}
	}, {
		dataField : "oid",
		headerText : "oid",
		dataType : "string",
		visible : false
	}]
	
		const props = {
				rowIdField : "oid",
				headerHeight : 30,
				rowHeight : 30,
				showRowNumColumn : true,
				rowNumHeaderText : "번호",
				multiepleMode : true	//다중선택
			};
// 	function createAUIGrid(columns) {
		
	
// 	};
	myGridID = AUIGrid.create("#grid_wrap", columns, props);
	
	function loadGridData() {
		let params = new Object();
		let url = getCallUrl("/cip/list");
		AUIGrid.showAjaxLoader(myGridID);
		call(url, params, function(data) {
// 			createAUIGrid(columns);
			AUIGrid.removeAjaxLoader(myGridID);
			$("input[name=sessionid]").val(data.sessionid);
			$("input[name=curPage]").val(data.curPage);
			AUIGrid.setGridData(myGridID, data.list);
			parent.close();	//이거해야 로딩바 사라짐;
			console.log(data);
		});
	}
	
	$("#addBtn").click(function() {
		var url = getCallUrl("/cip/create");
		popup(url, 1200, 600);
	})
</script>
</html>