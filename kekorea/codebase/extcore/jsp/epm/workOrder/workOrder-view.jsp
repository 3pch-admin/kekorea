<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.epm.workOrder.dto.WorkOrderDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
WorkOrderDTO dto = (WorkOrderDTO) request.getAttribute("dto");
JSONArray list = (JSONArray) request.getAttribute("list");
%>
<%@include file="/extcore/include/auigrid.jsp"%>
<style type="text/css">
.compare {
	background-color: yellow;
	color: red;
	font-weight: bold;
}
</style>
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				도면일람표 정보
			</div>
		</td>
		<td class="right">
			<input type="button" value="수정" title="수정" onclick="modify();">
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
			<a href="#tabs-2">도면 일람표</a>
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
				<th class="lb">도면 일람표 제목</th>
				<td class="indent5"><%=dto.getName()%></td>
				<th>상태</th>
				<td class="indent5"><%=dto.getState()%></td>
			</tr>
			<tr>
				<th class="lb">작성자</th>
				<td class="indent5"><%=dto.getCreator()%></td>
				<th>작성일</th>
				<td class="indent5"><%=dto.getCreatedDate_txt()%></td>
			</tr>
			<tr>
				<th class="lb">KEK 작번</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/jsp/common/project-include.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
						<jsp:param value="view" name="mode" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">내용</th>
				<td class="indent5" colspan="3">
					<textarea rows="5" readonly="readonly"><%=dto.getDescription() != null ? dto.getDescription() : ""%></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">표지파일</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/jsp/common/primary-view.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
					</jsp:include>
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
		<table class="button-table">
			<tr>
				<td class="left">
					<input type="button" value="표지 다운로드" title="표지 다운로드" onclick="cover();">
					<input type="button" value="PDF 압축파일 다운로드" title="PDF 압축파일 다운로드" class="blue" onclick="zip();">
					<input type="button" value="병합 PDF 다운로드" title="병합 PDF 다운로드" class="orange" onclick="merge();">
				</td>
			</tr>
		</table>
		<div id="grid_wrap" style="height: 800px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID;
			const columns = [ {
				dataField : "preView",
				headerText : "미리보기",
				width : 80,
				renderer : {
					type : "ImageRenderer",
					altField : null,
					imgHeight : 34,
				},
			}, {
				dataField : "name",
				headerText : "DRAWING TITLE",
				dataType : "string",
				style : "aui-left",
			}, {
				dataField : "number",
				headerText : "DWG. NO",
				dataType : "string",
				width : 200,
				renderer : {
					type : "LinkRenderer",
					baseUrl : "javascript",
					jsCallback : function(rowIndex, columnIndex, value, item) {
						const oid = item.oid;
						const doid = item.doid;
						const number = item.number;
						const rev = item.rev;
						let url;
						if (doid.indexOf("KeDrawing") > -1) {
							url = getCallUrl("/keDrawing/viewByNumberAndRev?number=" + number + "&rev=" + rev);
							popup(url, 1400, 700);
						} else {
							url = getCallUrl("/project/info?oid=" + oid);
						}
					}
				},
			}, {
				dataField : "current",
				headerText : "CURRENT VER",
				dataType : "string",
				width : 130,
				styleFunction : function(rowIndex, columnIndex, value, headerText, item, dataField) {
					const rev = item.rev;
					if (Number(value) !== Number(rev)) {
						console.log(rev);
						console.log(value);
						return "compare";
					}
					return "";
				},
				renderer : {
					type : "LinkRenderer",
					baseUrl : "javascript",
					jsCallback : function(rowIndex, columnIndex, value, item) {
						const oid = item.oid;
						const doid = item.doid;
						const number = item.number;
						const current = item.current;
						let url;
						if (doid.indexOf("KeDrawing") > -1) {
							url = getCallUrl("/keDrawing/viewByNumberAndRev?number=" + number + "&rev=" + current);
							popup(url, 1400, 700);
						} else {
							url = getCallUrl("/project/info?oid=" + oid);
						}
					}
				},
			}, {
				dataField : "rev",
				headerText : "REV",
				dataType : "string",
				width : 130,

			}, {
				dataField : "lotNo",
				headerText : "LOT",
				dataType : "numeric",
				width : 100,
				formatString : "###0",
			}, {
				dataField : "createdData_txt",
				headerText : "등록일",
				dataType : "string",
				width : 100,
			}, {
				dataField : "note",
				headerText : "NOTE",
				dataType : "string",
				width : 350,
			}, {
				dataField : "primary",
				headerText : "도면파일",
				dataType : "string",
				width : 80,
				renderer : {
					type : "TemplateRenderer",
				},
			} ]

			function createAUIGrid(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : true,
					showStateColumn : true,
					selectionMode : "multipleCells",
					rowNumHeaderText : "번호",
					showRowCheckColumn : true,
				};
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				AUIGrid.setGridData(myGridID,
		<%=list%>
			);
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
	function zip() {
		const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
		if (checkedItems.length === 0) {
			alert("ZIP파일로 다운로드 받을 도면을 하나 이상 선택하세요.");
			return false;
		}
	}

	function cover() {

	}

	function merge() {

	}

	function modify() {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/workOrder/modify?oid=" + oid);
		document.location.href = url;
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
			}
		});
		createAUIGrid9(columns9);
		createAUIGrid(columns);
		createAUIGrid100(columns100);
		AUIGrid.resize(myGridID9);
		AUIGrid.resize(myGridID100);
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID9);
		AUIGrid.resize(myGridID);
		AUIGrid.resize(myGridID100);
	});
</script>