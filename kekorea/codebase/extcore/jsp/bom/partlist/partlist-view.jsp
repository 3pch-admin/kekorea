<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.bom.partlist.dto.PartListDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
PartListDTO dto = (PartListDTO) request.getAttribute("dto");
JSONArray list = (JSONArray) request.getAttribute("list");
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
%>
<%@include file="/extcore/include/auigrid.jsp"%>
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<input type="hidden" name="loid" id="loid" value="<%=dto.getLoid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				수배표 정보
			</div>
		</td>
		<td class="right">
			<input type="button" value="수정" title="수정" onclick="modify();">
			<%
			if (isAdmin) {
			%>
			<input type="button" value="삭제" title="삭제" class="red" onclick="_delete();">
			<%
			}
			%>
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>
<div id="tabs">
	<ul>
		<li>
			<a href="#tabs-1">기본정보</a>
		</li>
		<li>
			<a href="#tabs-2">수배표</a>
		</li>
		<li>
			<a href="#tabs-3">결재이력</a>
		</li>
	</ul>
	<div id="tabs-1">
		<table class="view-table">
			<colgroup>
				<col style="width: 10%;">
				<col style="width: 40%;">
				<col style="width: 10%;">
				<col style="width: 40%;">
			</colgroup>
			<tr>
				<th class="lb">수배표 제목</th>
				<td class="indent5"><%=dto.getName()%></td>
				<th>설계구분</th>
				<td class="indent5"><%=dto.getEngType()%></td>
			</tr>
			<tr>
				<th class="lb">상태</th>
				<td class="indent5"><%=dto.getState()%></td>
				<th>전체금액</th>
				<td class="indent5"><%=dto.getTotalPrice_txt()%>원
				</td>
			</tr>
			<tr>
				<th class="lb">작성자</th>
				<td class="indent5"><%=dto.getCreator()%></td>
				<th>작성일</th>
				<td class="indent5"><%=dto.getCreatedDate_txt()%></td>
			</tr>
			<tr>
				<th class="lb">KEK 작번</th>
				<td colspan="3">
					<jsp:include page="/extcore/jsp/common/project-include.jsp">
						<jsp:param value="<%=dto.getOid() %>" name="oid" />
						<jsp:param value="view" name="mode" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">내용</th>
				<td class="indent5" colspan="3">
					<textarea rows="7" cols="" readonly="readonly"><%=dto.getContent() != null ? dto.getContent() : ""%></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">첨부파일</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/jsp/common/secondary-view.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
					</jsp:include>
				</td>
			</tr>
		</table>
	</div>
	<div id="tabs-2">
		<div id="grid_wrap" style="height: 665px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID;
			const list =
		<%=list%>
			const columns = [ {
				dataField : "lotNo",
				headerText : "LOT_NO",
				dataType : "numeric",
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
				width : 100,
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
				dataField : "partListDate_txt",
				headerText : "수배일자",
				dataType : "string",
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
				colSpan : 7,
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
				colSpan : 3,
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
					headerHeight : 30,
					showRowNumColumn : true,
					rowNumHeaderText : "번호",
					selectionMode : "multipleCells",
					showFooter : true,
					footerPosition : "top",
				};
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				AUIGrid.setFooter(myGridID, footerLayout);
				AUIGrid.setGridData(myGridID, list);
			}
		</script>
	</div>
	<div id="tabs-3">
		<!-- 결재이력 -->
		<jsp:include page="/extcore/jsp/common/approval-history.jsp">
			<jsp:param value="<%=dto.getOid()%>" name="oid" />
		</jsp:include>
	</div>
</div>
<script type="text/javascript">
	function modify() {
		openLayer();
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/partlist/modify?oid=" + oid);
		document.location.href = url;
	}

	function _delete() {
		if (!confirm("삭제 하시겠습니까?")) {
			return false;
		}

		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/partlist/delete?oid=" + oid);
		openLayer();
		call(url, null, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			} else {
				closeLayer();
			}
		}, "GET");
	}

	document.addEventListener("DOMContentLoaded", function() {
		$("#tabs").tabs({
			active : 0,
			activate : function(event, ui) {
				var tabId = ui.newPanel.prop("id");
				switch (tabId) {
				case "tabs-1":
					const isCreated9 = AUIGrid.isCreated(myGridID9);
					if (isCreated9) {
						AUIGrid.resize(myGridID9);
					} else {
						createAUIGrid9(columns9);
					}
					break;
				case "tabs-2":
					const isCreated = AUIGrid.isCreated(myGridID);
					if (isCreated) {
						AUIGrid.resize(myGridID);
					} else {
						createAUIGrid(columns);
					}
					break;
				case "tabs-3":
					const isCreated100 = AUIGrid.isCreated(myGridID100);
					if (isCreated100) {
						AUIGrid.resize(myGridID100);
					} else {
						createAUIGrid100(columns100);
					}
					break;
				}
			},
		});
		createAUIGrid9(columns9);
		createAUIGrid(columns);
		createAUIGrid100(columns100);
		AUIGrid.resize(myGridID9);
		AUIGrid.resize(myGridID);
		AUIGrid.resize(myGridID100);
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID9);
		AUIGrid.resize(myGridID);
		AUIGrid.resize(myGridID100);
	});
</script>
