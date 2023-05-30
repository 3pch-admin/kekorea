<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = (String) request.getAttribute("oid");
%>
<link rel="stylesheet" href="/Windchill/extcore/css/approval.css?v=1">
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>
<input type="hidden" name="oid" id="oid" value="<%=oid%>">
<table class="button-table">
	<tr>
		<td class="right">
			<input type="button" value="등록" title="등록" onclick="register();">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>

<table>
	<colgroup>
		<col width="350">
		<col width="5">
		<col width="150">
		<col width="5">
		<col width="700">
	</colgroup>
	<tr>
		<td valign="top">
			<table>
				<tr>
					<td class="center tab active" id="org" data-type="org">조직도</td>
					<td class="center tab" id="user" data-type="user">사용자검색</td>
					<td class="center tab" id="line" data-type="line">개인결재선</td>
				</tr>
				<tr class="org_tr">
					<td colspan="3" class="pt5">
						<div id="_grid_wrap" style="height: 280px; border-top: 1px solid #3180c3;"></div>
						<script type="text/javascript">
							let _myGridID;
							const approvals = window.approvals;
							const agrees = window.agrees;
							const receives = window.receives;
							const _columns = [ {
								dataField : "name",
								headerText : "부서명",
								dataType : "string",
							} ]

							function _createAUIGrid(columnLayout) {
								const props = {
									headerHeight : 30,
									showRowNumColumn : true,
									showAutoNoDataMessage : false,
									rowNumHeaderText : "번호",
									fillColumnSizeMode : true,
									selectionMode : "multipleCells",
									displayTreeOpen : true,
									enableDrop : false,
								}
								_myGridID = AUIGrid.create("#_grid_wrap", columnLayout, props);
								loadDepartmentTree();
								AUIGrid.bind(_myGridID, "selectionChange", auiGridSelectionChangeHandler);
							}

							let timerId = null;
							function auiGridSelectionChangeHandler(event) {
								if (timerId) {
									clearTimeout(timerId);
								}

								timerId = setTimeout(function() {
									const primeCell = event.primeCell;
									const rowItem = primeCell.item;
									const oid = rowItem.oid; 
									loadDepartmentUser(oid);
								}, 500);
							}

							function loadDepartmentTree() {
								const url = getCallUrl("/org/loadDepartmentTree");
								const params = new Object();
								AUIGrid.showAjaxLoader(_myGridID);
								call(url, params, function(data) {
									AUIGrid.removeAjaxLoader(_myGridID);
									AUIGrid.setGridData(_myGridID, data.list);
								});
							}

							function loadDepartmentUser(oid) {
								const url = getCallUrl("/org/loadDepartmentUser?oid=" + oid);
								AUIGrid.showAjaxLoader(_$myGridID);
								call(url, null, function(data) {
									AUIGrid.removeAjaxLoader(_$myGridID);
									AUIGrid.setGridData(_$myGridID, data.list);
								}, "GET");
							}

						</script>
					</td>
				</tr>
				<tr class="org_tr">
					<td colspan="3" class="pt5">
						<div id="list_wrap" style="height: 300px; border-top: 1px solid #3180c3;"></div>
						<script type="text/javascript">
							let _$myGridID;
							const _$columns = [ {
								dataField : "name",
								headerText : "이름",
								dataType : "string",
								width : 100,
							}, {
								dataField : "department",
								headerText : "부서",
								dataType : "string",
							} ]

							function _$createAUIGrid(columnLayout) {
								const props = {
									headerHeight : 30,
									showRowNumColumn : true,
									rowNumHeaderText : "번호",
									selectionMode : "multipleCells",
									showAutoNoDataMessage : false,
									showRowCheckColumn : true,
									enableRowCheckShiftKey : true,
									showDragKnobColumn : true,
									enableDrag : true,
									enableMultipleDrag : true,
									enableDrop : false,
									dropToOthers: true,
								}
								_$myGridID = AUIGrid.create("#list_wrap", columnLayout, props);
								AUIGrid.bind(_$myGridID, "cellClick", cellClickHandler);
								const oid = document.getElementById("oid").value;
								loadDepartmentUser(oid);
								AUIGrid.bind(_$myGridID, "dropEndBefore", function (event) {
									event.isMoveMode = false;

									const pids = ["#agree_wrap", "#approval_wrap", "#receive_wrap"];
									const items = event.items;
									let copy = true;
									for(let i=0; i<items.length; i++) {
										const item = event.items[0]; 
										for(let k=0; k<pids.length; k++) {
											const notHave = AUIGrid.isUniqueValue(pids[k], "oid", item.oid); 
											if (!notHave) {
												copy = false;
												break;
											}
										}
									}
									return copy;
								});
							}

							function cellClickHandler(event) {
								const item = event.item;
								const rowIdField = AUIGrid.getProp(event.pid, "rowIdField");
								const rowId = item[rowIdField];
								if (AUIGrid.isCheckedRowById(event.pid, rowId)) {
									AUIGrid.addUncheckedRowsByIds(event.pid, rowId);
								} else {
									AUIGrid.addCheckedRowsByIds(event.pid, rowId);
								}
							};

							document.addEventListener("DOMContentLoaded", function() {
								_createAUIGrid1(_columns1);
								_createAUIGrid2(_columns2);
								_createAUIGrid3(_columns3);
								_createAUIGrid(_columns); 
								_$createAUIGrid(_$columns);
								_$createAUIGrid2(_$columns2);
								_$createAUIGrid3(_$columns3);
								for(let i=0; i< 2; i++) {
									AUIGrid.resize(_myGridID1); 
									AUIGrid.resize(_myGridID2); 
									AUIGrid.resize(_myGridID3); 
									AUIGrid.resize(_myGridID); 
									AUIGrid.resize(_$myGridID);
									AUIGrid.resize(_$myGridID2); 
									AUIGrid.resize(_$myGridID3);
								}
								loadFavorite();
							});

							window.addEventListener("resize", function() {
								AUIGrid.resize(_myGridID1); 
								AUIGrid.resize(_myGridID); 
								AUIGrid.resize(_$myGridID); 
								AUIGrid.resize(_$myGridID2); 
								AUIGrid.resize(_$myGridID3); 
								AUIGrid.resize(_myGridID2); 
								AUIGrid.resize(_myGridID3); 
							});
						</script>
					</td>
				</tr>

				<!-- 사용자 검색 -->
				<tr class="user_tr" style="display: none;">
					<td colspan="3" class="pt5">
						<input type="text" name="key" id="key" class="AXInput">
						<input type="button" value="조회" title="조회" onclick="loadGridData();" style="position: relative; top: 1.5px;">
					</td>
				</tr>
				<tr class="user_tr" style="display: none;">
					<td colspan="3" class="pt5">
						<div id="list_wrap2" style="height: 550px; border-top: 1px solid #3180c3;"></div>
						<script type="text/javascript">
							let _$myGridID2;
							const _$columns2 = [ {
								dataField : "name",
								headerText : "이름",
								dataType : "string",
								width : 100,
							}, {
								dataField : "department_name",
								headerText : "부서",
								dataType : "string",
							} ]

							function _$createAUIGrid2(columnLayout) {
								const props = {
									headerHeight : 30,
									showRowNumColumn : false,
									selectionMode : "multipleCells",
									showAutoNoDataMessage : false,
									showRowCheckColumn : true,
									enableRowCheckShiftKey : true,
									showDragKnobColumn : true,
									enableDrag : true,
									enableMultipleDrag : true,
									enableDrop : false,
									dropToOthers: true,
								}
								_$myGridID2 = AUIGrid.create("#list_wrap2", columnLayout, props);
								AUIGrid.bind(_$myGridID2, "cellClick", cellClickHandler);
								const oid = document.getElementById("oid").value;
								AUIGrid.bind(_$myGridID2, "dropEndBefore", function (event) {
									event.isMoveMode = false;

									const pids = ["#agree_wrap", "#approval_wrap", "#receive_wrap"];
									const items = event.items;
									let copy = true;
									for(let i=0; i<items.length; i++) {
										const item = event.items[0]; 
										for(let k=0; k<pids.length; k++) {
											const notHave = AUIGrid.isUniqueValue(pids[k], "oid", item.oid); 
											if (!notHave) {
												copy = false;
												break;
											}
										}
									}
									return copy;
								});
							}
						</script>
					</td>
				</tr>

				<!-- 개인결재선 -->
				<tr class="line_tr" style="display: none;">
					<td colspan="3" class="pt5">
						<input type="text" name="name" id="name" class="AXInput">
						<input type="button" value="조회" title="조회" onclick="loadGridLine();" style="position: relative; top: 1.5px;" class="blue">
						<input type="button" value="저장" title="저장" onclick="_save();" style="position: relative; top: 1.5px;">
						<input type="button" value="삭제" title="삭제" onclick="_delete();" style="position: relative; top: 1.5px;" class="red">
					</td>
				</tr>
				<tr class="line_tr" style="display: none;">
					<td colspan="3" class="pt5">
						<div id="list_wrap3" style="height: 550px; border-top: 1px solid #3180c3;"></div>
						<script type="text/javascript">
							let _$myGridID3
							const _$columns3 = [ {
								dataField : "name",
								headerText : "개인 결재선 이름",
								dataType : "string",
								editable : false
							}, {
								dataField : "favorite",
								headerText  : "즐겨찾기",
								dataType : "boolean",
								width : 120,
// 								renderer : {
// 									type : "CheckBoxEditRenderer",
// 									editable : true
// 								},
								renderer : {
									type : "TemplateRenderer"
								},
								labelFunction: function (rowIndex, columnIndex, value, headerText, item) {
									const oid = item.oid;
									let checked = "";
									if(value) {
										checked = ' checked="checked"';
									} 
									let html = "<input type='checkbox' name='name'" + checked + " onclick=\"favorite(this, '" + oid + "');\">";
									return html;
								}
							} ]
							
							function favorite(obj, oid) {
								const url = getCallUrl("/workspace/favorite")
								const params = new Object();
								params.oid = oid;
								params.checked = !!obj.checked;
								AUIGrid.showAjaxLoader(_$myGridID3);
								openLayer();
								call(url, params, function(data) {
									alert(data.msg);
									if(data.result) {
										loadGridLine();
										loadFavorite();
									} else {
										AUIGrid.removeAjaxLoader(_$myGridID3);
									}
									closeLayer();
								})
							}

							function _$createAUIGrid3(columnLayout) {
								const props = {
									headerHeight : 30,
									showRowNumColumn : true,
									showRowCheckColumn : true,
									rowNumHeaderText : "번호",
									selectionMode : "multipleCells",
									showAutoNoDataMessage : false,
									showRowCheckColumn : true,
									rowCheckToRadio : true,
									editable : true
								}
								_$myGridID3 = AUIGrid.create("#list_wrap3", columnLayout, props);
								AUIGrid.bind(_$myGridID3, "cellDoubleClick", function(event) {
									const oid = event.item.oid;
									const url = getCallUrl("/workspace/loadFavorite?oid="+oid);
									AUIGrid.showAjaxLoader(_myGridID1);
									AUIGrid.showAjaxLoader(_myGridID2);
									AUIGrid.showAjaxLoader(_myGridID3);
									openLayer();
									call(url, null, function(data) {
										AUIGrid.removeAjaxLoader(_myGridID1);
										AUIGrid.removeAjaxLoader(_myGridID2);
										AUIGrid.removeAjaxLoader(_myGridID3);
										if(data.result) {
											const approval = data.approval;
											const agree = data.agree;
											const receive = data.receive;
											AUIGrid.setGridData(_myGridID1, agree);
											AUIGrid.setGridData(_myGridID2, approval);
											AUIGrid.setGridData(_myGridID3, receive);
										} else {
											alert(data.msg);
										}
										closeLayer();
									}, "GET");
								})
							}
						</script>
					</td>
				</tr>
			</table>
		</td>
		<td>&nbsp;</td>
		<td>
			<table class="select-table">
				<tr>
					<td class="center">결재타입</td>
				</tr>
				<tr>
					<td class="center">
						<div class="pretty p-switch">
							<input type="radio" name="lineType" value="agree">
							<div class="state p-success">
								<label>
									<b>검토</b>
								</label>
							</div>
						</div>
					</td>
				</tr>
				<tr>
					<td class="center">
						<div class="pretty p-switch">
							<input type="radio" name="lineType" checked="checked" value="approval">
							<div class="state p-success">
								<label>
									<b>결재</b>
								</label>
							</div>
						</div>
					</td>
				</tr>
				<tr>
					<td class="center">
						<div class="pretty p-switch">
							<input type="radio" name="lineType" value="receive">
							<div class="state p-success">
								<label>
									<b>수신</b>
								</label>
							</div>
						</div>
					</td>
				</tr>
			</table>
			<table class="button-table">
				<tr>
					<td class="center">
						<input type="button" value="추가" title="추가" onclick="moveRow();">
						<input type="button" value="삭제" title="삭제" class="red" onclick="deleteRow();">
					</td>
				</tr>
				<tr>
					<td class="center">
						<input type="button" value="전체 삭제" title="전체 삭제" class="blue" onclick="_clear();">
					</td>
				</tr>
			</table>
		</td>
		<td>&nbsp;</td>
		<td valign="top">
			<table>
				<tr>
					<td>
						<div class="line-title">
							<div class="header">
								<img src="/Windchill/extcore/images/header.png">
								검토 라인
							</div>
						</div>
					</td>
				</tr>
				<tr>
					<td>
						<div id="agree_wrap" style="height: 180px; border-top: 1px solid #3180c3;"></div>
						<script type="text/javascript">
							let _myGridID1;
							const _columns1 = [ {
								dataField : "name",
								headerText : "이름",
								dataType : "string",
								width : 130,
							}, {
								dataField : "id",
								headerText : "아이디",
								dataType : "string",
								width : 130,
							}, {
								dataField : "duty",
								headerText : "직급",
								dataType : "string",
								width : 130,
							}, {
								dataField : "department_name",
								headerText : "부서",
								dataType : "string",
							}, {
								dataField : "oid",
								visible : false
							} ]
							function _createAUIGrid1(columnLayout) {
								const props = {
									headerHeight : 30,
									showRowNumColumn : false,
									selectionMode : "multipleCells",
									showAutoNoDataMessage : false,
									showRowCheckColumn : true,
									enableRowCheckShiftKey : true,
									showDragKnobColumn : true,
									enableDrag : true,
									enableMultipleDrag : true,
									enableDrop : true,
									dropToOthers: true,
									useContextMenu : true,
									contextMenuItems : [ {
										label : "선택된 행 삭제",
										callback : contextItemHandler
									} ],
								}
								_myGridID1 = AUIGrid.create("#agree_wrap", columnLayout, props);
								AUIGrid.bind(_myGridID1, "dropEndBefore", function (event) {
									const pids = ["#approval_wrap", "#receive_wrap"];
									const items = event.items;
									let copy = true;
									for(let i=0; i<items.length; i++) {
										const item = event.items[0]; 
										for(let k=0; k<pids.length; k++) {
											const notHave = AUIGrid.isUniqueValue(pids[k], "oid", item.oid); 
											if (!notHave) {
												copy = false;
												break;
											}
										}
									}
									return copy;
								});
								AUIGrid.setGridData(_myGridID1, agrees);
							}


						</script>
					</td>
				</tr>

				<tr>
					<td>
						<div class="line-title">
							<div class="header">
								<img src="/Windchill/extcore/images/header.png">
								결재 라인
							</div>
						</div>
					</td>
				</tr>
				<tr>
					<td>
						<div id="approval_wrap" style="height: 180px; border-top: 1px solid #3180c3;"></div>
						<script type="text/javascript">
							let _myGridID2;
							const _columns2 = [ {
								dataField : "name",
								headerText : "이름",
								dataType : "string",
								width : 130,
							}, {
								dataField : "id",
								headerText : "아이디",
								dataType : "string",
								width : 130,
							}, {
								dataField : "duty",
								headerText : "직급",
								dataType : "string",
								width : 130,
							}, {
								dataField : "department_name",
								headerText : "부서",
								dataType : "string",
							}, {
								dataField :"oid",
								visible : false
							} ]
							function _createAUIGrid2(columnLayout) {
								const props = {
									headerHeight : 30,
									showRowNumColumn : true,
									rowNumHeaderText : "순서",
									selectionMode : "multipleCells",
									showAutoNoDataMessage : false,
									showRowCheckColumn : true,
									enableRowCheckShiftKey : true,
									showDragKnobColumn : true,
									enableDrag : true,
									enableMultipleDrag : true,
									enableDrop : true,
									dropToOthers: true,
									useContextMenu : true,
									contextMenuItems : [ {
										label : "선택된 행 삭제",
										callback : contextItemHandler
									} ],
								}
								_myGridID2 = AUIGrid.create("#approval_wrap", columnLayout, props);
								AUIGrid.bind(_myGridID2, "dropEndBefore", function (event) {
									const pids = ["#agree_wrap", "#receive_wrap"];
									const items = event.items;
									let copy = true;
									for(let i=0; i<items.length; i++) {
										const item = event.items[0]; 
										for(let k=0; k<pids.length; k++) {
											const notHave = AUIGrid.isUniqueValue(pids[k], "oid", item.oid); 
											if (!notHave) {
												copy = false;
												break;
											}
										}
									}
									return copy;
								});
								AUIGrid.setGridData(_myGridID2, approvals);
							}


						</script>
					</td>
				</tr>

				<tr>
					<td>
						<div class="line-title">
							<div class="header">
								<img src="/Windchill/extcore/images/header.png">
								수신 라인
							</div>
						</div>
					</td>
				</tr>

				<tr>
					<td>
						<div id="receive_wrap" style="height: 180px; border-top: 1px solid #3180c3;"></div>
						<script type="text/javascript">
							let _myGridID3;
							const _columns3 = [ {
								dataField : "name",
								headerText : "이름",
								dataType : "string",
								width : 130,
							}, {
								dataField : "id",
								headerText : "아이디",
								dataType : "string",
								width : 130,
							}, {
								dataField : "duty",
								headerText : "직급",
								dataType : "string",
								width : 130,
							}, {
								dataField : "department_name",
								headerText : "부서",
								dataType : "string",
							}, {
								dataField : "oid",
								visible : false
							} ]
							function _createAUIGrid3(columnLayout) {
								const props = {
									headerHeight : 30,
									showRowNumColumn : false,
									selectionMode : "multipleCells",
									showAutoNoDataMessage : false,
									showRowCheckColumn : true,
									enableRowCheckShiftKey : true,		
									showDragKnobColumn : true,
									enableDrag : true,
									enableMultipleDrag : true,
									enableDrop : true,
									dropToOthers: true,
									useContextMenu : true,
									contextMenuItems : [ {
										label : "선택된 행 삭제",
										callback : contextItemHandler
									} ],
								}
								_myGridID3 = AUIGrid.create("#receive_wrap", columnLayout, props);
								AUIGrid.bind(_myGridID3, "dropEndBefore", function (event) {
									const pids = ["#agree_wrap", "#approval_wrap"];
									const items = event.items;
									let copy = true;
									for(let i=0; i<items.length; i++) {
										const item = event.items[0]; 
										for(let k=0; k<pids.length; k++) {
											const notHave = AUIGrid.isUniqueValue(pids[k], "oid", item.oid); 
											if (!notHave) {
												copy = false;
												break;
											}
										}
									}
									return copy;
								});
								AUIGrid.setGridData(_myGridID3, receives);
							}
						</script>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

<script type="text/javascript">

const org = document.getElementById("org");
const user = document.getElementById("user");
const line = document.getElementById("line");

org.addEventListener("click", changeHandler);
user.addEventListener("click", changeHandler);
line.addEventListener("click", changeHandler);

const org_tr = document.querySelectorAll('.org_tr');
const user_tr = document.querySelectorAll('.user_tr');
const line_tr = document.querySelectorAll('.line_tr');

function loadFavorite() {
	const url = getCallUrl("/workspace/loadFavorite");
	const params = new Object();
	call(url, params, function(data) {
		const approval = data.approval;
		const agree = data.agree;
		const receive = data.receive;
		AUIGrid.setGridData(_myGridID1, agree);
		AUIGrid.setGridData(_myGridID2, approval);
		AUIGrid.setGridData(_myGridID3, receive);
	})
}

function changeHandler(event) {
	const target = event.target;
	const type = target.dataset.type;
	
	if(type === "org") {
		org.classList.add("active");
		user.classList.remove("active");
		line.classList.remove("active");
		
		
		for(let i=0; i<org_tr.length; i++) {
			org_tr[i].style.display = "";
		}
		
		for(let i=0; i<user_tr.length; i++) {
			user_tr[i].style.display = "none";
		}
		
		for(let i=0; i<line_tr.length; i++) {
			line_tr[i].style.display = "none";
		}
		
		AUIGrid.resize(_myGridID); 
	} else if(type === "user") {
		org.classList.remove("active");
		user.classList.add("active");
		line.classList.remove("active");
		
		for(let i=0; i<org_tr.length; i++) {
			org_tr[i].style.display = "none";
		}
		
		for(let i=0; i<user_tr.length; i++) {
			user_tr[i].style.display = "";
		}
		
		for(let i=0; i<line_tr.length; i++) {
			line_tr[i].style.display = "none";
		}

		toFocus("key");
		loadGridData();
		for(let i=0; i<6; i++) {
			AUIGrid.resize(_$myGridID2);
		}
	} else if(type === "line") {
		org.classList.remove("active");
		user.classList.remove("active");
		line.classList.add("active");
		
		
		for(let i=0; i<org_tr.length; i++) {
			org_tr[i].style.display = "none";
		}
		
		for(let i=0; i<user_tr.length; i++) {
			user_tr[i].style.display = "none";
		}
		
		for(let i=0; i<line_tr.length; i++) {
			line_tr[i].style.display = "";
		}
		toFocus("name");
		loadGridLine();
		// 이상해...
		for(let i=0; i<6; i++) {
			AUIGrid.resize(_$myGridID3);
		}
	}
}


function loadGridLine() {
	const params = new Object();
	const url = getCallUrl("/workspace/loadLine")
	const name = document.getElementById("name").value;
	params.name = name;
	AUIGrid.showAjaxLoader(_$myGridID3);
	openLayer();
	call(url, params, function(data) {
		AUIGrid.removeAjaxLoader(_$myGridID3);
		if (data.result) {
			AUIGrid.setGridData(_$myGridID3, data.list);
		} else {
			alert(data.msg);
		}
		closeLayer();
	})
}

function loadGridData() {
	const params = new Object();
	const url = getCallUrl("/org/loadUser")
	const key = document.getElementById("key").value;
	params.key = key;
	AUIGrid.showAjaxLoader(_$myGridID2);
	openLayer();
	call(url, params, function(data) {
		AUIGrid.removeAjaxLoader(_$myGridID2);
		if (data.result) {
			AUIGrid.setGridData(_$myGridID2, data.list);
		} else {
			alert(data.msg);
		}
		closeLayer();
	})
}


function _delete() {
	const checked = AUIGrid.getCheckedRowItems(_$myGridID3);
	if(checked.length === 0) {
		alert("삭제할 개인결재선을 선택하세요.");
		return false;
	}
	
	const oid = checked[0].item.oid;
	const url = getCallUrl("/workspace/delete?oid="+oid);
	AUIGrid.showAjaxLoader(_$myGridID3);
	openLayer();
	call(url, null, function(data) {
		alert(data.msg);
		if(data.result) {
			loadGridLine();
		} else {
			AUIGrid.removeAjaxLoader(_$myGridID3);
		}
		closeLayer();
	}, "GET");
}

function _save() {
	const name = document.getElementById("name").value;
	if(name === "") {
		alert("개인결재선 이름을 입력하세요.");
		toFocus("name");
		return false;
	}
	
	const agree = AUIGrid.getGridData(_myGridID1);
	const approval = AUIGrid.getGridData(_myGridID2);
	const receive = AUIGrid.getGridData(_myGridID3);
	
	if(approval.length === 0) {
		alert("최소 하나이상의 결재선이 지정되어야합니다.");
		return false;
	}
	
	const params = new Object();
	const url = getCallUrl("/workspace/save");
	params.name = name;
	params.approvalList = approval;
	params.agreeList = agree;
	params.receiveList = receive;
	AUIGrid.showAjaxLoader(_$myGridID3);
	openLayer();
	call(url, params, function(data) {
		alert(data.msg);
		if(data.result) {
			name.value = "";
			loadGridLine();
		} else {
			AUIGrid.removeAjaxLoader(_$myGridID3);
		}
		closeLayer();
	})
}


function contextItemHandler(event) {
	const item = new Object();
	switch (event.contextIndex) {
	case 0:
		const selectedItems = AUIGrid.getSelectedItems(event.pid);
		for (let i = selectedItems.length - 1; i >= 0; i--) {
			const rowIndex = selectedItems[i].rowIndex;
			AUIGrid.removeRow(event.pid, rowIndex);
		}
		break;
	}
}

function moveRow() {
	const radioGroup = document.getElementsByName("lineType");
	let selectedValue;
	for (const radioButton of radioGroup) {
	  if (radioButton.checked) {
	    selectedValue = radioButton.value;
	    break;
	  }
	}
	
	const rows = AUIGrid.getCheckedRowItemsAll(_$myGridID);
	if (rows.length <= 0) {
		alert("선택된 사용자가 없습니다.");
		return false;
	}
	
	if("agree" === selectedValue) {
		
		for(let i=0; i<rows.length; i++) {
			const oid = rows[i].oid;
			const name = rows[i].name;
			const isUnique1 = AUIGrid.isUniqueValue(_myGridID1, "oid", oid);
			if(!isUnique1) {
				alert("검토라인에 이미 등록된 사용자(" + name + ")입니다.");
				return false;
			}
			
			const isUnique2 = AUIGrid.isUniqueValue(_myGridID2, "oid", oid);
			if(!isUnique2) {
				alert("결재라인에 이미 등록된 사용자(" + name + ")입니다.");
				return false;
			}
			
			const isUnique3 = AUIGrid.isUniqueValue(_myGridID3, "oid", oid);
			if(!isUnique3) {
				alert("수신라인에 이미 등록된 사용자(" + name + ")입니다.");
				return false;
			}
		}
		
		AUIGrid.addRow(_myGridID1, rows, "last");
	} else if("approval" === selectedValue) {
		
		for(let i=0; i<rows.length; i++) {
			const oid = rows[i].oid;
			const name = rows[i].name;
			const isUnique1 = AUIGrid.isUniqueValue(_myGridID1, "oid", oid);
			if(!isUnique1) {
				alert("검토라인에 이미 등록된 사용자(" + name + ")입니다.");
				return false;
			}
			
			const isUnique2 = AUIGrid.isUniqueValue(_myGridID2, "oid", oid);
			if(!isUnique2) {
				alert("결재라인에 이미 등록된 사용자(" + name + ")입니다.");
				return false;
			}
			
			const isUnique3 = AUIGrid.isUniqueValue(_myGridID3, "oid", oid);
			if(!isUnique3) {
				alert("수신라인에 이미 등록된 사용자(" + name + ")입니다.");
				return false;
			}
		}
		
		AUIGrid.addRow(_myGridID2, rows, "last");
	} else if("receive" === selectedValue) {
		
		for(let i=0; i<rows.length; i++) {
			const oid = rows[i].oid;
			const name = rows[i].name;
			const isUnique1 = AUIGrid.isUniqueValue(_myGridID1, "oid", oid);
			if(!isUnique1) {
				alert("검토라인에 이미 등록된 사용자(" + name + ")입니다.");
				return false;
			}
			
			const isUnique2 = AUIGrid.isUniqueValue(_myGridID2, "oid", oid);
			if(!isUnique2) {
				alert("결재라인에 이미 등록된 사용자(" + name + ")입니다.");
				return false;
			}
			
			const isUnique3 = AUIGrid.isUniqueValue(_myGridID3, "oid", oid);
			if(!isUnique3) {
				alert("수신라인에 이미 등록된 사용자(" + name + ")입니다.");
				return false;
			}
		}
		AUIGrid.addRow(_myGridID3, rows, "last");
	}
	AUIGrid.setAllCheckedRows(_$myGridID);
}

function _clear() {
	AUIGrid.clearGridData(_myGridID1);
	AUIGrid.clearGridData(_myGridID2);
	AUIGrid.clearGridData(_myGridID3);
}


function deleteRow() {
	const checkedAgree = AUIGrid.getCheckedRowItems(_myGridID1);
	for (let i = checkedAgree.length - 1; i >= 0; i--) {
		const rowIndex = checkedAgree[i].rowIndex;
		AUIGrid.removeRow(_myGridID1, rowIndex);
	}

	const checkedApproval = AUIGrid.getCheckedRowItems(_myGridID2);
	for (let i = checkedApproval.length - 1; i >= 0; i--) {
		let rowIndex = checkedApproval[i].rowIndex;
		AUIGrid.removeRow(_myGridID2, rowIndex);
	}

	const checkedReceive = AUIGrid.getCheckedRowItems(_myGridID3);
	for (let i = checkedReceive.length - 1; i >= 0; i--) {
		const rowIndex = checkedItems[i].rowIndex;
		AUIGrid.removeRow(_myGridID3, rowIndex);
	}
}

function register() {
	const agree = AUIGrid.getGridData(_myGridID1);
	const approval = AUIGrid.getGridData(_myGridID2);
	const receive = AUIGrid.getGridData(_myGridID3);
	opener.setLine(agree, approval, receive);
	self.close();
}
</script>