<%@page import="java.util.Map"%>
<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.common.util.ContentUtils"%>
<%@page import="e3ps.epm.keDrawing.dto.KeDrawingDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
KeDrawingDTO dto = (KeDrawingDTO) request.getAttribute("dto");
JSONArray history = (JSONArray) request.getAttribute("history");
int latestVersion = (int) request.getAttribute("latestVersion");
String loid = (String) request.getAttribute("loid");
%>
<%@include file="/extcore/include/auigrid.jsp"%>
<input type="hidden" name="loid" id="loid" value="<%=loid %>">
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<style type="text/css">
.preView {
	background-color: #caf4fd;
	cursor: pointer;
}
</style>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				KE 도면 정보
			</div>
		</td>
		<td class="right">
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
			<a href="#tabs-2">버전정보</a>
		</li>
		<li>
			<a href="#tabs-3">결재이력</a>
		</li>
	</ul>
	<div id="tabs-1">
		<table class="view-table">
			<colgroup>
				<col width="10%">
				<col width="300">
				<col width="130">
				<col width="300">
				<col width="200">
			</colgroup>
			<tr>
				<th class="lb">DRAWING TITLE</th>
				<td class="indent5"><%=dto.getName()%></td>
				<th>DWG NO</th>
				<td class="indent5"><%=dto.getKeNumber()%></td>
				<td class="center" rowspan="5">
					<%
					if (!StringUtils.isNull(dto.getPreView())) {
					%>
					<img src="<%=dto.getPreView()%>" style="height: 140px; cursor: pointer;" onclick="preView();" title="클릭시 원본크기로 볼 수 있습니다.">
					<%
					}
					%>
				</td>
			</tr>
			<tr>
				<th class="lb">LOT NO</th>
				<td class="indent5"><%=dto.getLotNo()%></td>
				<th>버전</th>
				<td class="indent5"><%=dto.getVersion()%> (<a href="javascript:view();"><font color="red"><b><%=latestVersion %></b></font></a>)</td>
			</tr>
			<tr>
				<th class="lb">작성자</th>
				<td class="indent5"><%=dto.getCreator()%></td>
				<th>작성일</th>
				<td class="indent5"><%=dto.getCreatedDate_txt()%></td>
			</tr>
			<tr>
				<th class="lb">수정자</th>
				<td class="indent5"><%=dto.getModifier()%></td>
				<th>수정일</th>
				<td class="indent5"><%=dto.getModifiedDate_txt()%></td>
			</tr>
			<tr>
				<th class="lb">도면파일</th>
				<td class="indent5">
					<jsp:include page="/extcore/jsp/common/primary-view.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
					</jsp:include>
				</td>
				<th class="lb">개정사유</th>
				<td class="indent5">
					<%=dto.getNote() != null ? dto.getNote() : ""%>
				</td>
			</tr>
			<tr>
				<th class="lb">도면일람표</th>
				<td class="indent5" colspan="4">
					<jsp:include page="/extcore/jsp/common/project-reference.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
						<jsp:param value="도면일람표 제목" name="header" />
					</jsp:include>
				</td>
			</tr>
		</table>
	</div>
	<div id="tabs-2">
		<div id="grid_wrap" style="height: 350px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID;
			const columns = [ {
				dataField : "lotNo",
				headerText : "LOT",
				dataType : "numeric",
				width : 80,
			}, {
				dataField : "name",
				headerText : "DRAWING TITLE",
				dataType : "string",
				style : "aui-left",
				width : 250,
				renderer : {
					type : "LinkRenderer",
					baseUrl : "javascript",
					jsCallback : function(rowIndex, columnIndex, value, item) {
						const oid = item.oid;
						if (oid === undefined) {
							return false;
						}
						const moid = item.moid;
						const url = getCallUrl("/keDrawing/view?oid=" + oid);
						popup(url, 1400, 700);
					}
				},
			}, {
				dataField : "keNumber",
				headerText : "DWG NO",
				dataType : "string",
				width : 100,
				renderer : {
					type : "LinkRenderer",
					baseUrl : "javascript",
					jsCallback : function(rowIndex, columnIndex, value, item) {
						const oid = item.oid;
						if (oid === undefined) {
							return false;
						}
						const moid = item.moid;
						const url = getCallUrl("/keDrawing/view?oid=" + oid);
						popup(url, 1400, 700);
					}
				},
			}, {
				dataField : "version",
				headerText : "버전",
				dataType : "numeric",
				width : 80,
			}, {
				dataField : "state",
				headerText : "상태",
				dataType : "string",
				width : 80,
			}, {
				dataField : "latest",
				headerText : "최신버전",
				dataType : "boolean",
				width : 80,
				renderer : {
					type : "CheckBoxEditRenderer"
				},
			}, {
				dataField : "creator",
				headerText : "작성자",
				dataType : "string",
				width : 100,
			}, {
				dataField : "createdDate_txt",
				headerText : "작성일",
				dataType : "string",
				width : 100,
			}, {
				dataField : "modifier",
				headerText : "수정자",
				dataType : "string",
				width : 100,
			}, {
				dataField : "modifiedDate_txt",
				headerText : "수정일",
				dataType : "string",
				width : 100,
			}, {
				dataField : "preView",
				headerText : "미리보기",
				width : 80,
				style : "preView",
				renderer : {
					type : "ImageRenderer",
					altField : null,
					imgHeight : 34,
				},
			}, {
				dataField : "primary",
				headerText : "도면파일",
				dataType : "string",
				width : 80,
				renderer : {
					type : "TemplateRenderer",
				},
			}, {
				dataField : "note",
				headerText : "개정사유",
				dateType : "string",
				width : 250,
				style : "aui-left",
			} ]

			function createAUIGrid(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : true,
					rowNumHeaderText : "번호",
					selectionMode : "multipleCells",
					noDataMessage : "결재이력이 없습니다.",
					enableSorting : false,
				}
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				AUIGrid.bind(myGridID, "cellClick", auiCellClickHandler);
				AUIGrid.setGridData(myGridID, <%=history%>);
			}

			function auiCellClickHandler(event) {
				const dataField = event.dataField;
				const oid = event.item.oid;
				const preView = event.item.preView;
				if (dataField === "preView") {
					if (preView === null) {
						alert("미리보기 파일이 생성되어있지 않습니다.");
						return false;
					}
					const url = getCallUrl("/aui/thumbnail?oid=" + oid);
					popup(url);
				}
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
	function preView() {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/aui/thumbnail?oid=" + oid);
		popup(url);
	}
	
	function view() {
		const loid = document.getElementById("loid").value;
		const url = getCallUrl("/keDrawing/view?oid=" + loid);
		popup(url, 1400, 700);
	}

	document.addEventListener("DOMContentLoaded", function() {
		$("#tabs").tabs({
			active : 0,
			activate : function(event, ui) {
				var tabId = ui.newPanel.prop("id");
				switch (tabId) {
				case "tabs-1":
					const isCreated50 = AUIGrid.isCreated(myGridID50);
					if (isCreated50) {
						AUIGrid.resize(myGridID50);
					} else {
						createAUIGrid50(columns50);
					}
					break;
				case "tabs-2":
					const isCreated = AUIGrid.isCreated(myGridID);
					if (isCreated) {
						AUIGrid.resize(myGridID);
					} else {
						createAUIGrid50(columns);
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

		createAUIGrid(columns);
		createAUIGrid50(columns50);
		createAUIGrid100(columns100);
		AUIGrid.resize(myGridID);
		AUIGrid.resize(myGridID50);
		AUIGrid.resize(myGridID100);
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
		AUIGrid.resize(myGridID50);
		AUIGrid.resize(myGridID100);
	});
</script>