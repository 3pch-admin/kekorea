<%@page import="wt.log4j.SystemOutFacade"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.common.util.ContentUtils"%>
<%@page import="e3ps.epm.dto.EpmDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
EpmDTO dto = (EpmDTO) request.getAttribute("dto");
JSONArray versionHistory = (JSONArray) request.getAttribute("versionHistory");
JSONArray data = (JSONArray) request.getAttribute("data");
JSONArray history = (JSONArray) request.getAttribute("history");
boolean isAutoCad = (boolean) request.getAttribute("isAutoCad");
boolean isCreo = (boolean) request.getAttribute("isCreo");
%>
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				KEK 도면 정보
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
			<a href="#tabs-2">도면속성</a>
		</li>
		<li>
			<a href="#tabs-3">관련작번</a>
		</li>
		<li>
			<a href="#tabs-4">버전이력</a>
		</li>
		<li>
			<a href="#tabs-5">결재이력</a>
		</li>
	</ul>
	<div id="tabs-1">
		<table class="view-table">
			<colgroup>
				<col width="150">
				<col width="250">
				<col width="150">
				<col width="250">
				<col width="400">
			</colgroup>
			<tr>
				<th class="lb">도면 번호</th>
				<td class="indent5" colspan="3"><%=dto.getDwg_no()%></td>
				<td class="center" rowspan="7">
					<img src="<%=dto.getThumnail()%>" style="height: 140px; cursor: pointer;" onclick="preView();" title="클릭시 원본크기로 볼 수 있습니다.">
				</td>
			</tr>
			<tr>
				<th class="lb">도면 이름</th>
				<td class="indent5" colspan="3"><%=dto.getName()%></td>
			</tr>
			<tr>
				<th class="lb">버전</th>
				<td class="indent5"><%=dto.getVersion()%></td>
				<th class="lb">부품</th>
				<td class="indent5"></td>
			</tr>
			<tr>
				<th class="lb">상태</th>
				<td class="indent5"><%=dto.getState()%></td>
				<th class="lb">저장위치</th>
				<td class="indent5"><%=dto.getLocation()%></td>
			</tr>
			<tr>
				<th class="lb">도면타입</th>
				<td class="indent5"><%=dto.getCadType()%></td>
				<th class="lb">응용프로그램</th>
				<td class="indent5"><%=dto.getApplicationType()%></td>
			</tr>
			<tr>
				<th class="lb">작성자</th>
				<td class="indent5"><%=dto.getCreator()%></td>
				<th class="lb">작성일</th>
				<td class="indent5"><%=dto.getCreatedDate_txt()%></td>
			</tr>
			<tr>
				<th class="lb">수정자</th>
				<td class="indent5"><%=dto.getModifier()%></td>
				<th class="lb">수정일</th>
				<td class="indent5"><%=dto.getModifiedDate_txt()%></td>
			</tr>
			<tr>
				<th class="lb">설명</th>
				<td colspan="4" class="indent5">
					<textarea class="description" rows="7" cols="" readonly="readonly"><%=dto.getDescription() != null ? dto.getDescription() : ""%></textarea>
				</td>
			</tr>
		</table>
	</div>
	<div id="tabs-2">
		<table class="view-table">
			<colgroup>
				<col width="150">
				<col width="*">
				<col width="150">
				<col width="*">
			</colgroup>
			<tr>
				<th class="lb">NAME_OF_PARTS</th>
				<td class="indent5"><%=dto.getName_of_parts()%></td>
				<th class="lb">DWG_NO</th>
				<td class="indent5">
					<%
					//=dto.getDwg_no()
					%>
				</td>
			</tr>
			<tr>
				<th class="lb">MATERIAL</th>
				<td class="indent5"><%=dto.getMaterial()%></td>
				<th class="lb">REMARKS</th>
				<td class="indent5"><%=dto.getRemarks()%></td>
			</tr>
			<tr>
				<th class="lb">PART_CODE</th>
				<td class="indent5"><%=dto.getPart_code()%></td>
				<th class="lb">STD_UNIT</th>
				<td class="indent5"></td>
			</tr>
			<tr>
				<th class="lb">MAKER</th>
				<td class="indent5"></td>
				<%
				if (isCreo) {
				%>
				<th class="lb">CUSTNAME</th>
				<td class="indent5"></td>
				<%
				} else {
				%>
				<th class="lb">CUSNAME</th>
				<td class="indent5"></td>
				<%
				}
				%>
			</tr>
			<tr>
				<th class="lb">PRICE</th>
				<td class="indent5"></td>
				<th class="lb">CURRNAME</th>
				<td class="indent5"></td>
			</tr>
			<tr>
				<th class="lb">REF_NO</th>
				<td class="indent5" colspan="3"><%=dto.getReference()%></td>
			</tr>
		</table>
	</div>
	<div id="tabs-3">
		<jsp:include page="/extcore/jsp/common/project-include.jsp">
			<jsp:param value="<%=dto.getOid()%>" name="oid" />
			<jsp:param value="view" name="mode" />
			<jsp:param value="350" name="height" />
		</jsp:include>
	</div>
	<div id="tabs-4">
		<div id="_grid_wrap" style="height: 460px; border-top: 1px solid #3180c3; margin: 5px;"></div>
		<script type="text/javascript">
			let myGridID;
			const columns = [ {
				dataField : "name",
				headerText : "이름",
				dataType : "string",
				style : "aui-left",
				renderer : {
					type : "LinkRenderer",
					baseUrl : "javascript",
					jsCallback : function(rowIndex, columnIndex, value, item) {
						const oid = item.oid;
						const url = getCallUrl("/epm/view?oid=" + oid);
						popup(url, 1400, 500);
					}
				},
			}, {
				dataField : "version",
				headerText : "버전",
				dataType : "string",
				width : 80,
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
			} ]
			function createAUIGrid(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : true,
					rowNumHeaderText : "번호",
					selectionMode : "multipleCells",
					showAutoNoDataMessage : false,
					enableSorting : false,
					autoGridHeight : true,
				}
				myGridID = AUIGrid.create("#_grid_wrap", columnLayout, props);
				AUIGrid.setGridData(myGridID, <%=versionHistory%>);
			}
		</script>
	</div>
	<div id="tabs-5">
		<jsp:include page="/extcore/jsp/common/approval-history.jsp">
			<jsp:param value="<%=dto.getOid()%>" name="oid" />
		</jsp:include>
	</div>
</div>
<script type="text/javascript">
	function preView() {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/aui/thumbnail?oid=" + oid);
		popup(url, 1400, 600);
	}
	document.addEventListener("DOMContentLoaded", function() {
		$("#tabs").tabs({
			active : 0,
			activate : function(event, ui) {
				var tabId = ui.newPanel.prop("id");
				switch (tabId) {
				case "tabs-3":
					const isCreated9 = AUIGrid.isCreated(myGridID9);
					if (isCreated9) {
						AUIGrid.resize(myGridID9);
					} else {
						createAUIGrid9(columns9);
					}
					break;
				case "tabs-4":
					const isCreated = AUIGrid.isCreated(myGridID);
					if (isCreated) {
						AUIGrid.resize(myGridID);
					} else {
						createAUIGrid(columns);
					}
					break;
				case "tabs-5":
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
		createAUIGrid9(columns9);
		createAUIGrid100(columns100);
		AUIGrid.resize(myGridID);
		AUIGrid.resize(myGridID9);
		AUIGrid.resize(myGridID100);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
		AUIGrid.resize(myGridID9);
		AUIGrid.resize(myGridID100);
	});
</script>