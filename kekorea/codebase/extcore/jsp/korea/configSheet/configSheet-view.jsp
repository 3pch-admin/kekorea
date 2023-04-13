<%@page import="e3ps.korea.configSheet.beans.ConfigSheetDTO"%>
<%@page import="net.sf.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
JSONArray data = (JSONArray) request.getAttribute("data");
ConfigSheetDTO dto = (ConfigSheetDTO) request.getAttribute("dto");
String oid = (String) request.getAttribute("oid");
JSONArray history = (JSONArray) request.getAttribute("history");
JSONArray list = (JSONArray) request.getAttribute("list");
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
%>
<%@include file="/extcore/include/auigrid.jsp"%>
<style type="text/css">
.row1 {
	background-color: #99CCFF;
}

.row2 {
	background-color: #FFCCFF;
}

.row3 {
	background-color: #CCFFCC;
}

.row4 {
	background-color: #FFFFCC;
}

.row5 {
	background-color: #FFCC99;
}

.row6 {
	background-color: #CCCCFF;
}

.row7 {
	background-color: #99FF66;
}

.row8 {
	background-color: #CC99FF;
}

.row9 {
	background-color: #66CCFF;
}

.row10 {
	background-color: #CCFFCC;
}

.row11 {
	background-color: #FFCCFF;
}

.row12 {
	background-color: #FFFFCC;
}
</style>
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				CONFIG SHEET 정보
			</div>
		</td>
		<td class="right">
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
			<a href="#tabs-2">CONFIG SHEET</a>
		</li>
		<li>
			<a href="#tabs-3">결재이력</a>
		</li>
	</ul>
	<div id="tabs-1">
		<table class="view-table">
			<colgroup>
				<col width="150">
				<col width="*">
			</colgroup>
			<tr>
				<th class="lb">CONFIG SHEET 제목</th>
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
				<td colspan="3">
					<div class="include">
						<div id="_grid_wrap" style="height: 200px; border-top: 1px solid #3180c3; margin: 5px;"></div>
						<script type="text/javascript">
							let _myGridID;
							const list =
						<%=list%>
							const _columns = [ {
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
								width : 120,
								filter : {
									showIcon : true,
									inline : true
								},
							}, {
								dataField : "mak_name",
								headerText : "막종",
								dataType : "string",
								width : 120,
								filter : {
									showIcon : true,
									inline : true
								},
							}, {
								dataField : "detail_name",
								headerText : "막종상세",
								dataType : "string",
								width : 120,
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
										const oid = item.oid;
										alert(oid);
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
								width : 100,
								renderer : {
									type : "LinkRenderer",
									baseUrl : "javascript",
									jsCallback : function(rowIndex, columnIndex, value, item) {
										const oid = item.oid;
										alert(oid);
									}
								},
								filter : {
									showIcon : true,
									inline : true
								},
							}, {
								dataField : "description",
								headerText : "작업 내용",
								dataType : "string",
								style : "aui-left",
								filter : {
									showIcon : true,
									inline : true
								},
							} ]
							function _createAUIGrid(columnLayout) {
								const props = {
									headerHeight : 30,
									showRowNumColumn : true,
									rowNumHeaderText : "번호",
									selectionMode : "singleRow",
								}
								_myGridID = AUIGrid.create("#_grid_wrap", columnLayout, props);
								AUIGrid.setGridData(_myGridID, list);
							}
						</script>
					</div>
				</td>
			</tr>
			<tr>
				<th class="lb">설명</th>
				<td class="indent5" colspan="3">
					<textarea name="description" id="description" rows="6" readonly="readonly"><%=dto.getContent()%></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">첨부파일</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/include/attachment-view.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
						<jsp:param value="secondary" name="mode" />
					</jsp:include>
				</td>
			</tr>
		</table>
	</div>
	<div id="tabs-2">
		<div id="grid_wrap" style="height: 800px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID;
			const data =
		<%=data%>
			const columns = [ {
				dataField : "category_name",
				headerText : "CATEGORY",
				dataType : "string",
				width : 250,
				cellMerge : true,
			}, {
				dataField : "item_name",
				headerText : "ITEM",
				dataType : "string",
				width : 200,
				cellMerge : true,
				mergeRef : "category_code",
				mergePolicy : "restrict",
			}, {
				dataField : "spec",
				headerText : "사양",
				dataType : "string",
				width : 250,
			}, {
				dataField : "note",
				headerText : "NOTE",
				dataType : "string",
			}, {
				dataField : "apply",
				headerText : "APPLY",
				dataType : "string",
				width : 350
			} ]

			function createAUIGrid(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : true,
					showStateColumn : true,
					rowNumHeaderText : "번호",
					selectionMode : "multipleCells",
					enableCellMerge : true,
					rowStyleFunction : function(rowIndex, item) {
						const value = item.category_code;
						if (value === "CATEGORY_2") {
							return "row1";
						} else if (value === "CATEGORY_3") {
							return "row2";
						} else if (value === "CATEGORY_4") {
							return "row3";
						} else if (value === "CATEGORY_5") {
							return "row4";
						} else if (value === "CATEGORY_6") {
							return "row5";
						} else if (value === "CATEGORY_7") {
							return "row6";
						} else if (value === "CATEGORY_8" || value === "CATEGORY_9") {
							return "row7";
						} else if (value === "CATEGORY_10") {
							return "row8";
						} else if (value === "CATEGORY_11") {
							return "row9";
						} else if (value === "CATEGORY_12") {
							return "row4";
						} else if (value === "CATEGORY_13") {
							return "row10";
						} else if (value === "CATEGORY_14") {
							return "row11";
						} else if (value === "CATEGORY_15") {
							return "row12";
						}
						return "";
					}
				};
				myGridID = AUIGrid.create("#grid_wrap", columns, props);
				AUIGrid.setGridData(myGridID, data);
			}
		</script>
	</div>
	<div id="tabs-3">
		<div id="_grid_wrap_" style="height: 550px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let _myGridID_;
			const history =
		<%=history%>
			const _columns_ = [ {
				dataField : "type",
				headerText : "타입",
				dataType : "string",
				width : 80
			}, {
				dataField : "role",
				headerText : "역할",
				dataType : "string",
				width : 80
			}, {
				dataField : "name",
				headerText : "제목",
				dataType : "string",
				style : "aui-left"
			}, {
				dataField : "state",
				headerText : "상태",
				dataType : "string",
				width : 100
			}, {
				dataField : "owner",
				headerText : "담당자",
				dataType : "string",
				width : 100
			}, {
				dataField : "receiveDate_txt",
				headerText : "수신일",
				dataType : "string",
				width : 130
			}, {
				dataField : "completeDate_txt",
				headerText : "완료일",
				dataType : "string",
				width : 130
			}, {
				dataField : "description",
				headerText : "결재의견",
				dataType : "string",
				style : "aui-left",
				width : 450,
			}, ]

			function _createAUIGrid_(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : true,
					rowNumHeaderText : "번호",
					selectionMode : "singleRow",
					noDataMessage : "결재이력이 없습니다.",
					enableSorting : false
				}
				_myGridID_ = AUIGrid.create("#_grid_wrap_", columnLayout, props);
				AUIGrid.setGridData(_myGridID_, history);
			}
		</script>
	</div>
</div>
<script type="text/javascript">
	function _delete() {

		if (!confirm("삭제 하시겠습니까?")) {
			return false;
		}

		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/configSheet/delete?oid=" + oid);
		openLayer();
		call(url, null, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
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
					const _isCreated = AUIGrid.isCreated(_myGridID);
					if (_isCreated) {
						AUIGrid.resize(_myGridID);
					} else {
						_createAUIGrid(_columns);
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
					const _isCreated_ = AUIGrid.isCreated(_myGridID_);
					if (_isCreated_) {
						AUIGrid.resize(_myGridID_);
					} else {
						_createAUIGrid_(_columns_);
					}
					break;
				}
			}
		});

		_createAUIGrid(_columns);
		_createAUIGrid_(_columns_);
		createAUIGrid(columns);
		AUIGrid.resize(_myGridID);
		AUIGrid.resize(_myGridID_);
		AUIGrid.resize(myGridID);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
		AUIGrid.resize(_myGridID);
		AUIGrid.resize(_myGridID_);
	});
</script>