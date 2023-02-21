<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.korea.cip.beans.CipColumnData"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<CipColumnData> list = (ArrayList<CipColumnData>) request.getAttribute("list");
JSONArray data = JSONArray.fromObject(list);
%>
<%@include file="/jsp/include/auigrid.jsp"%>
<table class="btn_table">
	<tr>
		<td class="right">
			<input type="button" value="닫기" id="closeBtn" title="닫기" class="blueBtn">
		</td>
	</tr>
</table>
<div id="grid_wrap" style="height: 900px; border-top: 1px solid #3180c3;"></div>

<script type="text/javascript">
	let myGridID;
	let data = <%=data%>
	const columns = [ {
		dataField : "item",
		headerText : "항목",
		dataType : "string",
		width : 120,
		filter : {
			showIcon : true
		}
	}, {
		dataField : "improvements",
		headerText : "개선내용",
		dataType : "string",
		width : 300,
		filter : {
			showIcon : true
		}
	}, {
		dataField : "improvement",
		headerText : "개선책",
		dataType : "string",
		width : 300,
		filter : {
			showIcon : true
		}
	}, {
		dataField : "apply",
		headerText : "적용/미적용",
		width : 100,
		dataType : "string",
		filter : {
			showIcon : true
		}
	}, {
		dataField : "mak_code",
		headerText : "막종",
		width : 150,
		dataType : "string",
		filter : {
			showIcon : true
		}
	}, {
		dataField : "detail_code",
		headerText : "막종상세",
		width : 150,
		dataType : "string",
		filter : {
			showIcon : true
		}
	}, {
		dataField : "customer_code",
		headerText : "거래처",
		width : 150,
		dataType : "string",
		filter : {
			showIcon : true
		}
	}, {
		dataField : "install_code",
		headerText : "설치장소",
		width : 150,
		dataType : "string",
		filter : {
			showIcon : true
		}
	}, {
		dataField : "note",
		headerText : "비고",
		dataType : "string",
		width : 150,
		filter : {
			showIcon : true
		}
	}, {
		dataField : "preView",
		headerText : "미리보기",
		width : 100,
		editable : false,
		renderer : {
			type : "ImageRenderer",
			altField : null,
			imgHeight : 34,
		}
	}, {
		dataField : "icons",
		headerText : "첨부파일",
		width : 100,
		editable : false,
		renderer : {
			type : "TemplateRenderer",
		},
	}, {
		dataField : "creator",
		headerText : "작성자",
		dataType : "string",
		width : 100,
		filter : {
			showIcon : true
		}
	}, {
		dataField : "createdDate_txt",
		headerText : "작성일",
		dataType : "string",
		width : 100,
		filter : {
			showIcon : true
		}
	}, {
		dataField : "oid",
		headerText : "oid",
		dataType : "string",
		visible : false
	} ]

	function createAUIGrid(columns) {
		const props = {
			rowIdField : "oid",
			headerHeight : 30,
			rowHeight : 36,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			editable : false,
			showRowCheckColumn : true,
			noDataMessage : "검색 결과가 없습니다.",
			enableFilter : true,
			fixedColumnCount : 3,
			editableOnFixedCell : true
		};
		myGridID = AUIGrid.create("#grid_wrap", columns, props);
		AUIGrid.setGridData(myGridID, data);
	}

	$(function() {
		createAUIGrid(columns);
		
		$("#closeBtn").click(function() {
			self.close();
		})
	})
	
	$(window).resize(function() {
		AUIGrid.resize(myGridID);
	})	
</script>