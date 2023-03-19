<%@page import="e3ps.part.kePart.service.KePartHelper"%>
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
if ("kePart".equals(obj)) {
	data = KePartHelper.manager.jsonArrayAui(oid);
}
%>
<div class="include">
	<div id="_grid_wrap" style="height: <%=height%>px; border-top: 1px solid #3180c3;"></div>
	<script type="text/javascript">
		let _myGridID;
		const _columns = [ {
			dataField : "name",
			headerText : "T-BOM 제목",
			dataType : "string",
			width : 300,
			style : "underline",
			filter : {
				showIcon : true,
				inline : true
			},
			cellMerge : true
		// 구분1 칼럼 셀 세로 병합 실행
		}, {
			dataField : "info",
			headerText : "",
			width : 40,
			renderer : {
				type : "IconRenderer",
				iconWidth : 16, // icon 사이즈, 지정하지 않으면 rowHeight에 맞게 기본값 적용됨
				iconHeight : 16,
				iconTableRef : { // icon 값 참조할 테이블 레퍼런스
					"default" : "/Windchill/extcore/images/details.gif" // default
				},
				onClick : function(event) {
					const oid = event.item.loid;
					const url = getCallUrl("/partlist/info?oid=" + oid);
					popup(url);
				}
			},
			filter : {
				showIcon : false,
				inline : false
			},
			cellMerge : true,
			mergeRef : "name",
			mergePolicy : "restrict"
		}, {
			dataField : "projectType_name",
			headerText : "설계구분",
			dataType : "string",
			width : 80,
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
			width : 100,
			renderer : {
				type : "LinkRenderer",
				baseUrl : "javascript",
				jsCallback : function(rowIndex, columnIndex, value, item) {
					alert("( " + rowIndex + ", " + columnIndex + " ) " + item.color + "  Link 클릭\r\n자바스크립트 함수 호출하고자 하는 경우로 사용하세요!");
				}
			},
			filter : {
				showIcon : true,
				inline : true
			},
		}, {
			dataField : "keNumber",
			headerText : "KE 작번",
			dataType : "string",
			style : "underline",
			width : 100,
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
			headerText : "작업내용",
			dataType : "string",
			width : 300,
			style : "left",
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
			headerText : "설치 장소",
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
				inline : true,
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
				inline : true,
			},
			cellMerge : true,
			mergeRef : "name",
			mergePolicy : "restrict"
		}, {
			dataField : "modifiedDate_txt",
			headerText : "수정일",
			dataType : "string",
			width : 100,
			filter : {
				showIcon : true,
				inline : true,
			},
			cellMerge : true,
			mergeRef : "name",
			mergePolicy : "restrict"
		}, {
			dataField : "state",
			headerText : "상태",
			dataType : "string",
			width : 100,
			filter : {
				showIcon : true,
				inline : true
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
				noDataMessage : "관련된 T-BOM이 없습니다.",
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
	</script>
</div>