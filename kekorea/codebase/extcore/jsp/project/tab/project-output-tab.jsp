<%@page import="wt.org.WTUser"%>
<%@page import="e3ps.bom.tbom.service.TBOMHelper"%>
<%@page import="java.util.Map"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.korea.cip.dto.CipDTO"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
JSONArray data = (JSONArray) request.getAttribute("data");
WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>
</head>
<body>
	<form>
		<input type="hidden" name="isAdmin" id="isAdmin" value="<%=isAdmin%>">
		<input type="hidden" name="sessionName" id="sessionName" value="<%=sessionUser.getFullName()%>">
		<input type="hidden" name="sessionId" id="sessionId" value="<%=sessionUser.getName()%>">
		<input type="hidden" name="oid" id="oid" value="<%=oid%>">
		<div id="_grid_wrap" style="height: 775px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let _myGridID;
			const data =
		<%=data%>
			const _columns = [ {
				dataField : "type",
				headerText : "산출물 타입",
				dataType : "string",
				width : 100,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "taskName",
				headerText : "태스크 명",
				dataType : "string",
				width : 130,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "name",
				headerText : "산출물 제목",
				dataType : "string",
				style : "aui-left",
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "version",
				headerText : "버전",
				dataType : "string",
				width : 80,
				filter : {
					showIcon : false,
					inline : false
				},
			}, {
				dataField : "state",
				headerText : "상태",
				dataType : "string",
				width : 80,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "creator",
				headerText : "작성자",
				dataType : "date",
				width : 100,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "createdDate_txt",
				headerText : "작성일",
				dataType : "string",
				width : 100
			}, {
				dataField : "primary",
				headerText : "주 첨부파일",
				dataType : "date",
				width : 130,
				filter : {
					showIcon : false,
					inline : false
				},
				renderer : {
					type : "TemplateRenderer"
				}
			}, {
				dataField : "secondary",
				headerText : "첨부파일",
				dataType : "string",
				width : 200,
				filter : {
					showIcon : false,
					inline : false
				},
				renderer : {
					type : "TemplateRenderer"
				}
			} ]
			function _createAUIGrid(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : true,
					rowNumHeaderText : "번호",
					selectionMode : "multipleCells",
					showAutoNoDataMessage : false,
					showRowCheckColumn : true,
					showStateColumn : true,
					enableFilter : true,
					showInlineFilter : true,
					filterLayerWidth : 320,
					filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
				};
				_myGridID = AUIGrid.create("#_grid_wrap", columnLayout, props);
				AUIGrid.setGridData(_myGridID, data);
			}

			document.addEventListener("DOMContentLoaded", function() {
				// 화면 활성화시 불러오게 설정한다 속도 생각 
				_createAUIGrid(_columns);
				AUIGrid.resize(_myGridID3);
			})

			window.addEventListener("resize", function() {
				AUIGrid.resize(_myGridID3);
			});
		</script>
	</form>
</body>
</html>