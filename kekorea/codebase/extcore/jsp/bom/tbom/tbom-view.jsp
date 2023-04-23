<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.bom.tbom.dto.TBOMDTO"%>
<%@page import="e3ps.common.util.ContentUtils"%>
<%@page import="e3ps.epm.keDrawing.dto.KeDrawingDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
TBOMDTO dto = (TBOMDTO) request.getAttribute("dto");
JSONArray list = (JSONArray) request.getAttribute("list");
JSONArray data = (JSONArray) request.getAttribute("data");
JSONArray history = (JSONArray) request.getAttribute("history");
int latestVersion = (int) request.getAttribute("latestVersion");
String loid = (String) request.getAttribute("loid");
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
%>
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>    
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				T-BOM 정보
			</div>
		</td>
		<td class="right">
			<%
			if (isAdmin && dto.isLatest()) {
			%>
			<input type="button" value="삭제" title="삭제" class="red" onclick="_delete();">
			<%
			}
			%>
			<%
			if (isAdmin || dto.isEdit()) {
			%>
			<input type="button" value="수정" title="수정" class="green" onclick="modify();">
			<%
			}
			%>
			<%
			if (isAdmin || dto.isRevise()) {
			%>
			<input type="button" value="개정" title="개정" class="green" onclick="revise();">
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
			<a href="#tabs-2">버전정보</a>
		</li>
		<li>
			<a href="#tabs-3">T-BOM</a>
		</li>
		<li>
			<a href="#tabs-4">결재이력</a>
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
				<th class="lb">T-BOM 제목</th>
				<td class="indent5"><%=dto.getName()%></td>
				<th>T-BOM 번호</th>
				<td class="indent5"><%=dto.getNumber()%></td>
			</tr>
			<tr>
				<th class="lb">버전</th>
				<td class="indent5"><%=dto.getVersion()%>
					(
					<a href="javascript:view();">
						<font color="red">
							<b><%=latestVersion%></b>
						</font>
					</a>
					)
				</td>
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
					<textarea rows="5" readonly="readonly"><%=dto.getContent() != null ? dto.getContent() : ""%></textarea>
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
		<div id="grid_wrap_" style="height: 500px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID_;
			const columns_ = [ {
				dataField : "number",
				headerText : "T-BOM 번호",
				dataType : "string",
				width : 150,
			}, {
				dataField : "name",
				headerText : "T-BOM 제목",
				dataType : "string",
				style : "aui-left",
				renderer : {
					type : "LinkRenderer",
					baseUrl : "javascript",
					jsCallback : function(rowIndex, columnIndex, value, item) {
						const oid = item.oid;
						const url = getCallUrl("/tbom/view?oid=" + oid);
						popup(url, 1600, 800);
					}
				},
			}, {
				dataField : "description",
				headerText : "내용",
				dataType : "string",
				style : "aui-left"
			}, {
				dataField : "version",
				headerText : "버전",
				dataType : "numeric",
				width : 80,
			}, {
				dataField : "state",
				headerText : "상태",
				dataType : "string",
				width : 100,
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
				dataField : "secondarys",
				headerText : "첨부파일",
				dataType : "string",
				width : 130,
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

			function createAUIGrid_(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : true,
					rowNumHeaderText : "번호",
					selectionMode : "multipleCells",
					showAutoNoDataMessage : false,
					enableSorting : false,
				}
				myGridID_ = AUIGrid.create("#grid_wrap_", columnLayout, props);
				AUIGrid.setGridData(myGridID_,
		<%=history%>
			);
			}
		</script>
	</div>
	<div id="tabs-3">
		<div id="grid_wrap" style="height: 550px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID;
			const data =
		<%=data%>
			const columns = [ {
				dataField : "lotNo",
				headerText : "LOT",
				dataType : "numeric",
				width : 100,
				formatString : "###0",
			}, {
				dataField : "code",
				headerText : "중간코드",
				dataType : "string",
				width : 130,
			}, {
				dataField : "keNumber",
				headerText : "부품번호",
				dataType : "string",
				width : 150,
				renderer : {
					type : "LinkRenderer",
					baseUrl : "javascript",
					jsCallback : function(rowIndex, columnIndex, value, item) {
						const oid = item.oid;
						const url = getCallUrl("/kePart/view?oid=" + oid);
						popup(url, 1400, 700);
					}
				},
			}, {
				dataField : "name",
				headerText : "부품명",
				dataType : "string",
				renderer : {
					type : "LinkRenderer",
					baseUrl : "javascript",
					jsCallback : function(rowIndex, columnIndex, value, item) {
						const oid = item.oid;
						const url = getCallUrl("/kePart/view?oid=" + oid);
						popup(url, 1400, 700);
					}
				},
			}, {
				dataField : "model",
				headerText : "KokusaiModel",
				dataType : "string",
				width : 200,
			}, {
				dataField : "qty",
				headerText : "QTY",
				dataType : "numeric",
				width : 100,
				formatString : "###0",
			}, {
				dataField : "unit",
				headerText : "UNIT",
				dataType : "string",
				width : 130
			}, {
				dataField : "provide",
				headerText : "PROVIDE",
				dataType : "string",
				width : 130
			}, {
				dataField : "discontinue",
				headerText : "DISCONTINUE",
				dataType : "string",
				width : 200
			} ]

			function createAUIGrid(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : true,
					rowNumHeaderText : "번호",
					selectionMode : "multipleCells",
				};
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				AUIGrid.setGridData(myGridID, data);
			}
		</script>
	</div>
	<div id="tabs-4">
		<!-- 결재이력 -->
		<jsp:include page="/extcore/jsp/common/approval-history.jsp">
			<jsp:param value="<%=dto.getOid()%>" name="oid" />
		</jsp:include>
	</div>
</div>
<script type="text/javascript">
	function view() {
		const url = getCallUrl("/tbom/view?oid=" + item.oid);
		popup(url, 1500, 700);
	}

	function modify() {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/tbom/modify?oid=" + oid);
		document.location.href = url;
	}

	function revise() {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/tbom/revise?oid=" + oid);
		document.location.href = url;
	}

	function _delete() {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/tbom/delete?oid=" + oid);

		if (!confirm("삭제 하시겠습니까?")) {
			return false;
		}

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
					const isCreated_ = AUIGrid.isCreated(myGridID_);
					if (isCreated_) {
						AUIGrid.resize(myGridID_);
					} else {
						createAUIGrid_(columns_);
					}
					break;
				case "tabs-3":
					const isCreated = AUIGrid.isCreated(myGridID);
					if (isCreated) {
						AUIGrid.resize(myGridID);
					} else {
						createAUIGrid(columns);
					}
					break;
				case "tabs-4":
					const isCreated100 = AUIGrid.isCreated(myGridID100);
					if (isCreated100) {
						AUIGrid.resize(myGridID100);
					} else {
						createAUIGrid100(columns100);
					}
					break;
				}
			},
		})
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