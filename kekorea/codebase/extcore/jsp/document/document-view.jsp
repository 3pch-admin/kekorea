<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="wt.org.WTUser"%>
<%@page import="e3ps.doc.dto.DocumentDTO"%>
<%-- <%@page import="e3ps.project.dto.ProjectDTO"%> --%>
<%@page import="e3ps.common.util.ContentUtils"%>
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>
<%@page import="net.sf.json.JSONArray"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
DocumentDTO dto = (DocumentDTO) request.getAttribute("dto");
JSONArray versionHistory = (JSONArray) request.getAttribute("versionHistory");
String oid = request.getParameter("oid");
%>
<input type="hidden" name="isAdmin" id="isAdmin" value="<%=isAdmin%>">
<input type="hidden" name="oid" id="oid">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				문서 정보
			</div>
		</td>
		<td class="right">
			<input type="button" value="개정" title="개정" class="blue" onclick="update('update');">
			<input type="button" value="수정" title="수정" onclick="update('modify');">
			<%
			if (isAdmin) {
			%>
			<input type="button" value="삭제" title="삭제" class="red" onclick="_delete();">
			<%
			}
			%>
			<input type="button" value="닫기" title="닫기" class="red" onclick="self.close();">
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
				<col width="150">
				<col width="600">
				<col width="150">
				<col width="600">
			</colgroup>
			<tr>
				<th class="lb">문서제목</th>
				<td class="indent5"><%=dto.getName()%></td>
				<th>문서번호</th>
				<td class="indent5"><%=dto.getNumber()%></td>
			</tr>
			<tr>
				<th class="lb">저장위치</th>
				<td class="indent5"><%=dto.getLocation()%></td>
				<th>도번</th>
				<td class="indent5"><%=dto.getNumberRule()%>
					[
					<font color="red">
						<b><%=dto.getNumberRuleVersion()%></b>
					</font>
					]
				</td>
			</tr>
			<tr>
				<th class="lb">버전</th>
				<td class="indent5"><%=dto.getVersion()%></td>
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
				<th class="lb">수정자</th>
				<td class="indent5"><%=dto.getModifier()%></td>
				<th>수정일</th>
				<td class="indent5"><%=dto.getModifiedDate_txt()%></td>
			</tr>
			<tr>
				<th class="lb">설명</th>
				<td colspan="3" class="indent5">
					<textarea rows="5" readonly="readonly"><%=dto.getDescription() != null ? dto.getDescription() : ""%></textarea>
				</td>
			</tr>
			<tr>
				<th class="req lb">관련부품</th>
				<td colspan="3">
					<jsp:include page="/extcore/jsp/common/part-include.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
						<jsp:param value="view" name="mode" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">주 첨부파일</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/jsp/common/primary-view.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
					</jsp:include></td>
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
		<!-- 버전이력 쭉 쌓이게 autoGrid 설정 true -->
		<div id="grid_wrap" style="height: 350px; border-top: 1px solid #3180c3; margin: 5px;"></div>
		<script type="text/javascript">
			let myGridID;
			const columns = [ {
				dataField : "name",
				headerText : "문서제목",
				dataType : "string",
			}, {
				dataField : "version",
				headerText : "버전",
				dataType : "string",
				width : 100,
			}, {
				dataField : "creator",
				headerText : "작성자",
				dataType : "string",
				width : 100,
			}, {
				dataField : "createdDate_txt",
				headerText : "거래처",
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
				dataField : "priamry",
				headerText : "주 첨부파일",
				dataType : "string",
				width : 100,
				renderer : {
					type : "TemplateRenderer"
				}
			}, {
				dataField : "secondary",
				headerText : "첨부파일",
				dataType : "string",
				width : 150,
				renderer : {
					type : "TemplateRenderer"
				}
			}, ]

			function createAUIGrid(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : true,
					rowNumHeaderText : "번호",
					showAutoNoDataMessage : false,
					softRemoveRowMode : false,
					autoGridHeight : true,
				}
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				AUIGrid.setGridData(myGridID,
		<%=versionHistory%>
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
	function update(mode) {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/doc/update?oid=" + oid + "&mode=" + mode);
		document.location.href = url;
	}
	
	function _delete() {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/doc/delete?oid=" + oid);
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
		createAUIGrid7(columns7);
		createAUIGrid(columns);
		createAUIGrid100(columns100);
		AUIGrid.resize(myGridID7);
		AUIGrid.resize(myGridID);
		AUIGrid.resize(myGridID100);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID7);
		AUIGrid.resize(myGridID);
		AUIGrid.resize(myGridID100);
	});
</script>