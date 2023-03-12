<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- 결재선 CSS -->
<link rel="stylesheet" href="/Windchill/extcore/css/approval.css?v=1">
<!-- AUIGrid -->
<%@include file="/extcore/include/auigrid.jsp"%>
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
		<!-- 결재선 선택할 부분들 -->
		<td valign="top">
			<table>
				<tr>
					<td class="center tab active" id="org">조직도</td>
					<td class="center tab" id="user">사용자검색</td>
					<td class="center tab" id="line">개인결재선</td>
				</tr>
				<tr>
					<td colspan="3" class="pt5">
						<!-- 폴더 그리드 리스트 -->
						<div id="_grid_wrap" style="height: 280px; border-top: 1px solid #3180c3;"></div>
						<script type="text/javascript">
							let _myGridID;
							const _columns = [ {
								dataField : "name",
								headerText : "부서명",
								dataType : "string",
							} ]

							function _createAUIGrid(columnLayout) {
								const props = {
									headerHeight : 30,
									rowHeight : 30,
									showRowNumColumn : true,
									rowNumHeaderText : "번호",
									fillColumnSizeMode : true,
									selectionMode : "singleRow",
									displayTreeOpen : true
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
									// 선택 대표 셀 정보 
									const primeCell = event.primeCell;
									// 대표 셀에 대한 전체 행 아이템
									const rowItem = primeCell.item;
									const oid = rowItem.oid; // oid로 할지 location 으로 할지...
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
								if(oid === undefined) {
									oid = "";
								}
								const url = getCallUrl("/org/loadDepartmentUser?oid=" + oid);
								AUIGrid.showAjaxLoader(_$myGridID);
								call(url, null, function(data) {
									AUIGrid.removeAjaxLoader(_$myGridID);
									AUIGrid.setGridData(_$myGridID, data.list);
								}, "GET");
							}

							document.addEventListener("DOMContentLoaded", function() {
								_createAUIGrid(_columns); // 트리
								AUIGrid.resize(_myGridID); // 트리
							});

							window.addEventListener("resize", function() {
								AUIGrid.resize(_myGridID); // 트리
							});
						</script>
					</td>
				</tr>
				<tr>
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
									rowHeight : 30,
									showRowNumColumn : true,
									rowNumHeaderText : "번호",
									fillColumnSizeMode : true,
									selectionMode : "singleRow",
									noDataMessage : "검색된 사용자가 없습니다",
									showRowCheckColumn : true,
									enableRowCheckShiftKey : true
								}
								_$myGridID = AUIGrid.create("#list_wrap", columnLayout, props);
								AUIGrid.bind(_$myGridID, "cellClick", cellClickHandler);
								loadDepartmentUser();
							}

							function cellClickHandler(event) {
								const item = event.item;
								const rowIdField = AUIGrid.getProp(event.pid, "rowIdField");
								const rowId = item[rowIdField];
								// 이미 체크 선택되었는지 검사
								if (AUIGrid.isCheckedRowById(event.pid, rowId)) {
									// 엑스트라 체크박스 체크해제 추가
									AUIGrid.addUncheckedRowsByIds(event.pid, rowId);
								} else {
									// 엑스트라 체크박스 체크 추가
									AUIGrid.addCheckedRowsByIds(event.pid, rowId);
								}
							};

							document.addEventListener("DOMContentLoaded", function() {
								_$createAUIGrid(_$columns);
								AUIGrid.resize(_$myGridID); // 트리
							});

							window.addEventListener("resize", function() {
								AUIGrid.resize(_$myGridID); // 트리
							});
						</script>
					</td>
				</tr>
			</table>
		</td>
		<td>&nbsp;</td>
		<!-- 결재선 버튼들 -->
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
			</table>
		</td>
		<td>&nbsp;</td>
		<!-- 결재선 지정 되는부분 -->
		<td valign="top">
			<table>
				<tr>
					<td>
						<div class="line-title">
							<i class="axi axi-subtitles" style="font-size: 14px;"></i>
							<span>검토 라인</span>
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
									rowHeight : 30,
									showRowNumColumn : true,
									rowNumHeaderText : "번호",
									fillColumnSizeMode : true,
									selectionMode : "singleRow",
									showRowCheckColumn : true,
									enableRowCheckShiftKey : true
								}
								_myGridID1 = AUIGrid.create("#agree_wrap", columnLayout, props);
							}

							document.addEventListener("DOMContentLoaded", function() {
								_createAUIGrid1(_columns1);
								AUIGrid.resize(_myGridID1); // 트리
							});

							window.addEventListener("resize", function() {
								AUIGrid.resize(_myGridID1); // 트리
							});
						</script>
					</td>
				</tr>

				<tr>
					<td>
						<div class="line-title">
							<i class="axi axi-subtitles" style="font-size: 14px;"></i>
							<span>결재 라인</span>
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
									rowHeight : 30,
									showRowNumColumn : true,
									rowNumHeaderText : "순서",
									fillColumnSizeMode : true,
									selectionMode : "singleRow",
									showRowCheckColumn : true,
									enableRowCheckShiftKey : true
								}
								_myGridID2 = AUIGrid.create("#approval_wrap", columnLayout, props);
							}

							document.addEventListener("DOMContentLoaded", function() {
								_createAUIGrid2(_columns2);
								AUIGrid.resize(_myGridID2); // 트리
							});

							window.addEventListener("resize", function() {
								AUIGrid.resize(_myGridID2); // 트리
							});
						</script>
					</td>
				</tr>

				<tr>
					<td>
						<div class="line-title">
							<i class="axi axi-subtitles" style="font-size: 14px;"></i>
							<span>수신 라인</span>
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
									rowHeight : 30,
									showRowNumColumn : true,
									rowNumHeaderText : "번호",
									fillColumnSizeMode : true,
									selectionMode : "singleRow",
									showRowCheckColumn : true,
									enableRowCheckShiftKey : true
								}
								_myGridID3 = AUIGrid.create("#receive_wrap", columnLayout, props);
							}

							document.addEventListener("DOMContentLoaded", function() {
								_createAUIGrid3(_columns3);
								AUIGrid.resize(_myGridID3); // 트리
							});

							window.addEventListener("resize", function() {
								AUIGrid.resize(_myGridID3); // 트리
							});
						</script>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

<!-- 결재선 지정 하는 스크립트들 -->
<script type="text/javascript">
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
				alert("검토라인 이미 등록된 사용자(" + name + ")입니다.");
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
				alert("검토라인 이미 등록된 사용자(" + name + ")입니다.");
				return false;
			}
			
			const isUnique2 = AUIGrid.isUniqueValue(_myGridID2, "oid", oid);
			if(!isUnique2) {
				alert("결재라인에 이미 등록된 사용자(" + name + ")입니다.");
				return false;
			}
		}
		AUIGrid.addRow(_myGridID3, rows, "last");
	}
	AUIGrid.setAllCheckedRows(_$myGridID);
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
	const rows1 = AUIGrid.getGridData(_myGridID1);
	const rows2 = AUIGrid.getGridData(_myGridID2);
	const rows3 = AUIGrid.getGridData(_myGridID3);
	opener.setLine(rows1, rows2, rows3);
	self.close();
}
</script>