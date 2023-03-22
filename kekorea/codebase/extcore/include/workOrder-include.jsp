<%@page import="e3ps.epm.keDrawing.service.KeDrawingHelper"%>
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
String obj = StringUtils.getParameter(request.getParameter("obj"));
String height = StringUtils.getParameter(request.getParameter("height"), "150");
JSONArray data = null;
if ("keDrawing".equals(obj)) {
	data = KeDrawingHelper.manager.jsonArrayAui(oid);
}
%>
<div class="include">
	<div id="_grid_wrap" style="height: <%=height%>px; border-top: 1px solid #3180c3;"></div>
	<script type="text/javascript">
		let _myGridID;
		const _columns = [ {
			dataField : "name",
			headerText : "도면일람표 제목",
			dataType : "string",
			width : 350,
			style : "left indent10 underline",
			renderer : {
				type : "LinkRenderer",
				baseUrl : "javascript",
				jsCallback : function(rowIndex, columnIndex, value, item) {
					const oid = item.oid;
					const url = getCallUrl("/workOrder/view?oid=" + oid);
					popup(url);
				}
			},
			filter : {
				showIcon : true,
				inline : true
			},
			cellMerge : true
		}, {
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
			width : 100,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "install_name",
			headerText : "설치장소",
			dataType : "string",
			width : 100,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "mak_name",
			headerText : "막종",
			dataType : "string",
			width : 100,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "detail_name",
			headerText : "막종상세",
			dataType : "string",
			width : 100,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "kekNumber",
			headerText : "KEK 작번",
			dataType : "string",
			width : 130,
			style : "underline",
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "keNumber",
			headerText : "KE 작번",
			dataType : "string",
			width : 130,
			style : "underline",
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "userId",
			headerText : "USER ID",
			dataType : "string",
			width : 100,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "description",
			headerText : "작업 내용",
			dataType : "string",
			width : 450,
			style : "left indent10",
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "model",
			headerText : "모델",
			dataType : "string",
			width : 100,
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "pdate_txt",
			headerText : "발행일",
			dataType : "string",
			width : 100,
			filter : {
				showIcon : true,
				inline : true
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
			cellMerge : true,
			mergeRef : "name",
			mergePolicy : "restrict"
		}, {
			dataField : "creator",
			headerText : "작성자",
			dataType : "string",
			width : 100,
			filter : {
				showIcon : true,
				inline : true
			},
			cellMerge : true,
			mergeRef : "name",
			mergePolicy : "restrict"
		}, {
			dataField : "createdDate_txt",
			headerText : "작성일",
			dataType : "string",
			width : 100,
			filter : {
				showIcon : true,
				inline : true
			},
			cellMerge : true,
			mergeRef : "name",
			mergePolicy : "restrict"
		}, {
			dataField : "cover",
			headerText : "표지",
			dataType : "string",
			width : 80,
			editable : false,
			renderer : {
				type : "TemplateRenderer",
			},
			filter : {
				showIcon : false,
				inline : false
			},
			cellMerge : true,
			mergeRef : "name",
			mergePolicy : "restrict"
		}, {
			dataField : "secondary",
			headerText : "첨부파일",
			dataType : "string",
			width : 80,
			editable : false,
			renderer : {
				type : "TemplateRenderer",
			},
			filter : {
				showIcon : false,
				inline : false
			},
			cellMerge : true,
			mergeRef : "name",
			mergePolicy : "restrict"
		} ]

		function _createAUIGrid(columnLayout) {
			const props = {
				// 그리드 공통속성 시작
				headerHeight : 30,
				rowHeight : 30,
				showRowNumColumn : true,
				showStateColumn : true,
				rowNumHeaderText : "번호",
				noDataMessage : "관련된 도면일람표가 없습니다.",
				enableFilter : true,
				selectionMode : "multipleCells",
				showInlineFilter : true,
				filterLayerWidth : 320,
				filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
				// 그리드 공통속성 끝
				fixedColumnCount : 1,
				cellMergePolicy : "withNull",
				enableCellMerge : true,
			}
			_myGridID = AUIGrid.create("#_grid_wrap", columnLayout, props);
			AUIGrid.setGridData(_myGridID, <%=data%>);
		}

		_createAUIGrid(_columns);
		AUIGrid.resize(_myGridID);
		window.addEventListener("resize", function() {
			AUIGrid.resize(_myGridID);
		});
	</script>
</div>