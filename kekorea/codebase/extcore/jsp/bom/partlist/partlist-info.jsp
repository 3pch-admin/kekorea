<%@page import="org.json.JSONArray"%>
<%@page import="e3ps.bom.partlist.dto.PartListDTO"%>
<%@page import="e3ps.doc.meeting.dto.MeetingDTO"%>
<%@page import="com.lowagie.text.Meta"%>
<%@page import="e3ps.doc.meeting.dto.MeetingTemplateDTO"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
PartListDTO dto = (PartListDTO) request.getAttribute("dto");
JSONArray data = (JSONArray) request.getAttribute("data");
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
%>
<!-- AUIGrid -->
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>    
<table class="button-table">
	<tr>
		<td class="right">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
			<%
			if (isAdmin) {
			%>
			<input type="button" value="삭제" title="삭제" class="red" onclick="_delete();">
			<%
			}
			%>
		</td>
	</tr>
</table>

<table class="create-table">
	<colgroup>
		<col width="130">
		<col width="800">
		<col width="130">
		<col width="800">
	</colgroup>
	<tr>
		<th>회의록 제목</th>
		<td class="indent5"><%=dto.getName()%></td>
		<th>회의록 템플릿</th>
		<td class="indent5"><%=dto.getName()%></td>
	</tr>
	<tr>
		<th>KEK 작번</th>
		<td colspan="3">
			<jsp:include page="/extcore/include/project-include.jsp">
				<jsp:param value="<%=dto.getOid()%>" name="oid" />
				<jsp:param value="view" name="mode" />
				<jsp:param value="" name="multi" />
				<jsp:param value="partlist" name="obj" />
				<jsp:param value="250" name="height" />
			</jsp:include>
		</td>
	</tr>
	<tr>
		<th>내용</th>
		<td class="indent5" colspan="3">
			<textarea name="description" id="description" rows="8" readonly="readonly"><%=dto.getContent()%></textarea>
		</td>
	</tr>
	<tr>
		<th>첨부파일</th>
		<td class="indent5" colspan="3">
			<jsp:include page="/extcore/include/secondary-include.jsp">
				<jsp:param value="<%=dto.getOid()%>" name="oid" />
				<jsp:param value="view" name="mode" />
			</jsp:include>
		</td>
	</tr>
</table>
<!-- 그리드 리스트 -->
<div id="grid_wrap" style="height: 665px; border-top: 1px solid #3180c3; margin-top: 5px;"></div>
<script type="text/javascript">
	let myGridID;
	const data = <%=data%>
	const columns = [ {
		dataField : "lotNo",
		headerText : "LOT_NO",
		dataType : "string",
		width : 80,
	}, {
		dataField : "unitName",
		headerText : "UNIT NAME",
		dataType : "string",
		width : 120
	}, {
		dataField : "partNo",
		headerText : "부품번호",
		dataType : "string",
		style : "underline",
		width : 130,
	}, {
		dataField : "partName",
		headerText : "부품명",
		dataType : "string",
		width : 200,
	}, {
		dataField : "standard",
		headerText : "규격",
		dataType : "string",
		width : 250,
	}, {
		dataField : "maker",
		headerText : "MAKER",
		dataType : "string",
		width : 130,
	}, {
		dataField : "customer",
		headerText : "거래처",
		dataType : "string",
		width : 130,
	}, {
		dataField : "quantity",
		headerText : "수량",
		dataType : "numeric",
		width : 60,
	}, {
		dataField : "unit",
		headerText : "단위",
		dataType : "string",
		width : 80,
	}, {
		dataField : "price",
		headerText : "단가",
		dataType : "numeric",
		width : 120,
	}, {
		dataField : "currency",
		headerText : "화폐",
		dataType : "string",
		width : 60,
	}, {
		dataField : "won",
		headerText : "원화금액",
		dataType : "numeric",
		width : 120,
	}, {
		dataField : "partListDate",
		headerText : "수배일자",
		dataType : "date",
		formatString : "yyyy-mm-dd",
		width : 100,
	}, {
		dataField : "exchangeRate",
		headerText : "환율",
		dataType : "numeric",
		width : 80,
		formatString : "#,##0.0000"
	}, {
		dataField : "referDrawing",
		headerText : "참고도면",
		dataType : "string",
		width : 120,
	}, {
		dataField : "classification",
		headerText : "조달구분",
		dataType : "string",
		width : 120,
	}, {
		dataField : "note",
		headerText : "비고",
		dataType : "string",
		width : 250,
	} ];

	const footerLayout = [ {
		labelText : "∑",
		positionField : "#base",
	}, {
		dataField : "lotNo",
		positionField : "lotNo",
		style : "right",
		colSpan : 7, // 자신을 포함하여 3개의 푸터를 가로 병합함.
		labelFunction : function(value, columnValues, footerValues) {
			return "수배표 수량 합계 금액";
		}
	}, {
		dataField : "quantity",
		positionField : "quantity",
		operation : "SUM",
		dataType : "numeric",
	}, {
		dataField : "unit",
		positionField : "unit",
		style : "right",
		colSpan : 3, // 자신을 포함하여 3개의 푸터를 가로 병합함.
		labelFunction : function(value, columnValues, footerValues) {
			return "수배표 수량 합계 금액";
		}
	}, {
		dataField : "won",
		positionField : "won",
		operation : "SUM",
		dataType : "numeric",
		formatString : "#,##0",
	}, {
		dataField : "partListDate",
		positionField : "partListDate",
		colSpan : "5",
	}, ];

	function createAUIGrid(columnLayout) {
		const props = {
			// 그리드 공통속성 시작
			headerHeight : 30, // 헤더높이
			rowHeight : 30, // 행 높이
			showRowNumColumn : true, // 번호 행 출력 여부
			rowNumHeaderText : "번호", // 번호 행 텍스트 설정
			selectionMode : "multipleCells",
			showFooter : true,
			footerPosition : "top",
		// 그리드 공통속성 끝
		};
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		AUIGrid.setFooter(myGridID, footerLayout);
	}

	function _delete() {
		if (!confirm("삭제 하시겠습니까?")) {
			return false;
		}
		const url = getCallUrl("/partlist/delete?oid=<%=dto.getOid()%>");
		call(url, null, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			} else {
				// 실패시..
			}
		}, "GET");
	}

	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid(columns);
		AUIGrid.setGridData(myGridID, data);
		AUIGrid.resize(myGridID);

		_createAUIGrid(_columns);
		AUIGrid.resize(_myGridID);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(_myGridID);
		AUIGrid.resize(myGridID);
	});
</script>