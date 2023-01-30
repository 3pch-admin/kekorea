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
	<!-- button table -->
	<table class="btn_table">
		<tr>
			<td class="right">
				<input type="button" value="등록" class="redBtn" id="addRowBtn" title="등록">
<!-- 				<input type="button" value="삭제" class="orangeBtn" id="deleteRowBtn" title="삭제"> -->
<!-- 				<input type="button" value="저장" class="" id="saveBtn" title="저장"> -->
<!-- 				<input type="button" value="조회" class="blueBtn" id="searchBtn" title="조회"> -->
			</td>
		</tr>
	</table>
	<div id="grid_wrap" style="height: 750px; border-top: 1px solid #3180c3;"></div>
</body>
<script type="text/javascript">
	let myGridID;
	const columns = [{
		dataField : "",
		headerText : "항목",
		dataType : "string",
		width : 100
	}, {
		dataField : "",
		headerText : "개선내용",
		dataType : "string",
		width : 400
	}, {
		dataField : "",
		headerText : "개선책",
		dataType : "string",
		width : 400
	}, {
		dataField : "",
		headerText : "적용/미적용",
		dataType : "string",
		width : 100,
		renderer : {
			type : "DropDownListRenderer",
			list : ["적용완료", "일부적용", "미적용", "검토중"]
		}
	}, {
		dataField : "",
		headerText : "대상막종",
		dataType : "string",
		width : 100,
		renderer : {
			type : "DropDownListRenderer",
			list : ["PYRO", "MT-ZrO2", "MT-HfO2", "HDPL", "LTM-TIN", "공통"]
		}
	}, {
		dataField : "",
		headerText : "비고",
		dataType : "string",
		width : 300
	}, {
		dataField : "",
		headerText : "미리보기",
		dataType : "string",
		width : 100,
		renderer : {
			
		}
	}, {
		dataField : "",
		headerText : "파일첨부",
		dataType : "",
		width : 100,
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
		let url = getCallUrl("/cip/create");
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
</script>
</html>