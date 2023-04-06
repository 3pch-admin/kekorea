<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/include/css.jsp"%>
<%@include file="/extcore/include/script.jsp"%>
<%@include file="/extcore/include/auigrid.jsp"%>
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js?v=1010"></script>
</head>
<body>
	<form>
		<table class="button-table">
			<tr>
				<td class="left">
					<div class="header">
						<img src="/Windchill/extcore/images/header.png">
						문서결재 등록
					</div>
				</td>
				<td class="right">
					<input type="button" value="결재등록" title="결재등록" onclick="save();">
				</td>
			</tr>
		</table>

		<table class="create-table">
			<colgroup>
				<col width="130">
				<col width="*">
			</colgroup>
			<tr>
				<th class="req lb">결재 제목</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-700">
				</td>
			</tr>
			<tr>
				<th class="req lb">결재 의견</th>
				<td class="indent5">
					<textarea name="description" id="description" rows="6"></textarea>
				</td>
			</tr>
			<tr>
				<th class="req lb">결재 문서</th>
				<td>
					<div class="include">
						<input type="button" value="문서 추가" title="문서 추가" class="blue" onclick="_insert();">
						<input type="button" value="문서 삭제" title="문서 삭제" class="red" onclick="_deleteRow();">
						<div id="_grid_wrap" style="height: 250px; border-top: 1px solid #3180c3; margin: 5px;"></div>
						<script type="text/javascript">
							let _myGridID;
							const _columns = [ {
								dataField : "number",
								headerText : "문서번호",
								dataType : "string",
								width : 140
							}, {
								dataField : "name",
								headerText : "문서제목",
								dataType : "string",
							}, {
								dataField : "version",
								headerText : "버전",
								dataType : "string",
								width : 80
							}, {
								dataField : "state",
								headerText : "상태",
								dataType : "string",
								width : 100,
							}, {
								dataField : "creator",
								headerText : "작성자",
								dataType : "string",
								width : 100
							}, {
								dataField : "createdDate",
								headerText : "작성일",
								dataType : "date",
								formatString : "yyyy-mm-dd",
								width : 100
							}, {
								dataField : "modifier",
								headerText : "수정자",
								dataType : "string",
								width : 100
							}, {
								dataField : "modifiedDate",
								headerText : "수정일",
								dataType : "date",
								formatString : "yyyy-mm-dd",
								width : 100
							}, {
								dataField : "oid",
								visible : false,
								dataType : "string"
							} ]

							function _insert() {
								const url = getCallUrl("/document/popup?method=append&multi=true");
								popup(url, 1400, 600);
							}

							function append(data) {
								for (let i = 0; i < data.length; i++) {
									let item = data[i].item;
									let isUnique = AUIGrid.isUniqueValue(_myGridID, "oid", item.oid);
									if (isUnique) {
										AUIGrid.addRow(_myGridID, item, "first");
									}
								}
							}

							function _createAUIGrid(columnLayout) {
								const props = {
									headerHeight : 30,
									rowHeight : 30,
									showRowNumColumn : true,
									rowNumHeaderText : "번호",
									showStateColumn : true,
									softRemoveRowMode : false,
									showRowCheckColumn : true,
								}
								_myGridID = AUIGrid.create("#_grid_wrap", columnLayout, props);
							}

							function _deleteRow() {
								const checked = AUIGrid.getCheckedRowItems(_myGridID);
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
				<th class="req lb">결재</th>
				<td>
					<jsp:include page="/extcore/include/register-include.jsp"></jsp:include>
				</td>
			</tr>
		</table>
		<script type="text/javascript">
			function save() {
				const url = getCallUrl("/document/register");
				const params = new Object();
				const _addRows = AUIGrid.getAddedRowItems(_myGridID);
				const _addRows_ = AUIGrid.getAddedRowItems(_myGridID_);
				const name = document.getElementById("name").value;
				const description = document.getElementById("description").value;
				params.name = name;
				params.description = description;
				params._addRows = _addRows; //문서
				toRegister(params, _addRows_);

				if (!confirm("문서결재를 등록하시겠습니까?")) {
					return false;
				}

				parent.openLayer();
				call(url, params, function(data) {
					alert(data.msg);
					if (data.result) {
						document.location.href = getCallUrl("/workspace/approval");
					}
				})
			}

			document.addEventListener("DOMContentLoaded", function() {
				document.getElementById("name").focus();
				_createAUIGrid(_columns);
				_createAUIGrid_(_columns_);
			});

			window.addEventListener("resize", function() {
				AUIGrid.bind(_myGridID);
				AUIGrid.bind(_myGridID_);
			});
		</script>
	</form>
</body>
</html>