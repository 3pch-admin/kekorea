<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%
String toid = (String) request.getAttribute("toid");
String poid = (String) request.getAttribute("poid");
String location = (String) request.getAttribute("location");
JSONArray list = (JSONArray) request.getAttribute("list");
%>
<%@include file="/extcore/include/auigrid.jsp"%>
<input type="hidden" name="toid" id="toid" value="<%=toid%>">
<input type="hidden" name="poid" id="poid" value="<%=poid%>">
<input type="hidden" name="location" id="location" value="<%=location%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				의뢰서 등록
			</div>
		</td>
		<td class="right">
			<input type="button" value="등록" title="등록" onclick="create();">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>

<div id="tabs">
	<ul>
		<li>
			<a href="#tabs-1">기본정보</a>
		</li>
	</ul>
	<div id="tabs-1">
		<table class="create-table">
			<colgroup>
				<col width="150">
				<col width="500">
				<col width="150">
				<col width="500">
			</colgroup>
			<tr>
				<th class="req lb">산출물 제목</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-300">
				</td>
				<th class="lb">진행율</th>
				<td class="indent5">
					<input type="number" name="progress" id="progress" class="width-300" value="0" maxlength="3">
				</td>
			</tr>
			<tr>
				<th class="req lb">KEK 작번</th>
				<td colspan="3">
					<div class="include">
						<input type="button" value="작번 추가" title="작번 추가" class="blue" onclick="_insert();">
						<input type="button" value="작번 삭제" title="작번 삭제" class="red" onclick="_deleteRow();">
						<div id="_grid_wrap" style="height: 150px; border-top: 1px solid #3180c3; margin: 5px;"></div>
						<script type="text/javascript">
							let _myGridID;
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
								width : 130,
								filter : {
									showIcon : true,
									inline : true
								},
							}, {
								dataField : "keNumber",
								headerText : "KE 작번",
								dataType : "string",
								width : 130,
								filter : {
									showIcon : true,
									inline : true
								},
							}, {
								dataField : "description",
								headerText : "작업 내용",
								dataType : "string",
								style : "left indent10",
								filter : {
									showIcon : true,
									inline : true
								},
							}, {
								dataField : "oid",
								headerText : "",
								visible : false
							} ]
							function _createAUIGrid(columnLayout) {
								const props = {
									headerHeight : 30,
									showRowNumColumn : true,
									showRowCheckColumn : true,
									showStateColumn : true,
									rowNumHeaderText : "번호",
									showAutoNoDataMessage : false,
									selectionMode : "singleRow",
									enableSorting : false
								}
								_myGridID = AUIGrid.create("#_grid_wrap", columnLayout, props);
								AUIGrid.setGridData(_myGridID, <%=list%>);
							}

							function _insert() {
								const url = getCallUrl("/project/popup?method=append&multi=true");
								popup(url, 1500, 700);
							}

							function append(data, callBack) {
								for (let i = 0; i < data.length; i++) {
									const item = data[i].item;
									const isUnique = AUIGrid.isUniqueValue(_myGridID, "oid", item.oid);
									if (isUnique) {
										AUIGrid.addRow(_myGridID, item, "first");
									}
								}
								callBack(true);
							}

							function _deleteRow() {
								const checked = AUIGrid.getCheckedRowItems(_myGridID);
								if (checked.length === 0) {
									alert("삭제할 행을 선택하세요.");
									return false;
								}

								for (let i = checked.length - 1; i >= 0; i--) {
									const rowIndex = checked[i].rowIndex;
									AUIGrid.removeRow(_myGridID, rowIndex);
								}
							}
						</script>
					</div>
				</td>
			</tr>
			<tr>
				<th class="lb">내용</th>
				<td class="indent5" colspan="3">
					<textarea name="description" id="description" rows="6"></textarea>
				</td>
			</tr>			
			<tr>
				<th class="lb">첨부파일</th>
				<td class="indent5" colspan="3">
					<jsp:include page="/extcore/include/primary-include.jsp">
						<jsp:param value="" name="oid" />
						<jsp:param value="create" name="mode" />
						<jsp:param value="150" name="height" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="req lb">결재</th>
				<td colspan="3">
					<jsp:include page="/extcore/include/register-include.jsp">
						<jsp:param value="250" name="height" />
					</jsp:include>
				</td>
			</tr>
		</table>
	</div>
</div>
<script type="text/javascript">
	function create() {
		const params = new Object();
		const url = getCallUrl("/output/create");
		const name = document.getElementById("name");
		const progress = document.getElementById("progress");
		const description = document.getElementById("description").value;
		const _addRows = AUIGrid.getGridData(_myGridID);
		const _addRows_ = AUIGrid.getAddedRowItems(_myGridID_);
		const toid = document.getElementById("toid").value;
		const poid = document.getElementById("poid").value;
		const location = document.getElementById("location").value;
		if (isNull(name.value)) {
			alert("산출물 제목을 입력하세요.");
			name.focus();
			return false;
		}

		if(_addRows.length === 0) {
			alert("하나 이상의 작번이 추가되어야 합니다.");
			return false;
		}
		
		
		if (_addRows_.length === 0) {
			alert("결재선을 지정하세요.");
			_register();
			return false;
		}

		if (!confirm("등록 하시겠습니까?")) {
			return false;
		}

		params.name = name.value;
		params.description = description;
		params.progress = Number(progress);
		params._addRows = _addRows;
		params._addRows_ = _addRows_;
		params.primarys = toArray("primarys");
		params.location = location;
		params.toid = toid;
		params.poid = poid;
		console.log(params);
		toRegister(params, _addRows_);
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.document.location.reload();
				self.close();
			}
		})
	}

	document.addEventListener("DOMContentLoaded", function() {
		$("#tabs").tabs({
			active : 0,
			create : function(event, ui) {
				const tabId = ui.panel.prop("id");
				switch (tabId) {
				case "tabs-1":
					_createAUIGrid(_columns);
					AUIGrid.resize(_myGridID);
					_createAUIGrid_(_columns_);
					AUIGrid.resize(_myGridID_);
					break;
				}
			},
			activate : function(event, ui) {
				return false;
				var tabId = ui.newPanel.prop("id");
				switch (tabId) {
				case "tabs-1":
					const _isCreated_ = AUIGrid.isCreated(_myGridID_);
					if (_isCreated_) {
						AUIGrid.resize(_myGridID_);
					} else {
						_createAUIGrid_(_columns_);
					}
					const _isCreated = AUIGrid.isCreated(_myGridID);
					if (_isCreated) {
						AUIGrid.resize(_myGridID);
					} else {
						_createAUIGrid_(_columns);
					}
					break;
				}
			}
		});
		document.getElementById("name").focus();
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(_myGridID);
		AUIGrid.resize(_myGridID_);
	});
</script>