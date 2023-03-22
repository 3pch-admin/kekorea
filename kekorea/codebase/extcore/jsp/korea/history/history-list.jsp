<%@page import="wt.org.WTUser"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="java.util.Map"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.admin.commonCode.CommonCodeType"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<Map<String, String>> headers = (ArrayList<Map<String, String>>) request.getAttribute("headers");
Map<String, ArrayList<Map<String, String>>> list = (Map<String, ArrayList<Map<String, String>>>) request
		.getAttribute("list");
WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/include/css.jsp"%>
<%@include file="/extcore/include/script.jsp"%>
<%@include file="/extcore/include/auigrid.jsp"%>
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js?v=1"></script>
</head>
<body>
	<form>
		<input type="hidden" name="sessionid" id="sessionid">
		<input type="hidden" name="curPage" id="curPage">
		<table class="search-table">
			<colgroup>
				<col width="130">
				<col width="*">
				<col width="130">
				<col width="*">
				<col width="130">
				<col width="*">
				<col width="130">
				<col width="*">
			</colgroup>
			<tr>
				<th>공지사항 제목</th>
				<td class="indent5">
					<input type="text" name="fileName" class="width-200">
				</td>
				<th>설명</th>
				<td class="indent5">
					<input type="text" name="description" class="width-200">
				</td>
				<th>작성자</th>
				<td class="indent5">
					<input type="text" name="creator" id="creator" class="width-200">
				</td>
				<th>작성일</th>
				<td class="indent5">
					<input type="text" name="createdFrom" id="createdFrom" class="width-100">
					~
					<input type="text" name="createdTo" id="createdTo" class="width-100">
				</td>
			</tr>
		</table>

		<table class="button-table">
			<tr>
				<td class="left">
					<img src="/Windchill/extcore/images/fileicon/file_excel.gif" title="엑셀 다운로드" onclick="exportExcel();">
					<img src="/Windchill/extcore/images/save.gif" title="테이블 저장" onclick="saveColumnLayout('history-list');">
					<img src="/Windchill/extcore/images/redo.gif" title="테이블 초기화" onclick="resetColumnLayout('history-list');">
					<input type="button" value="저장" title="저장" onclick="save();">
				</td>
				<td class="right">
					<select name="psize" id="psize">
						<option value="30">30</option>
						<option value="50">50</option>
						<option value="100">100</option>
						<option value="200">200</option>
						<option value="300">300</option>
					</select>
					<input type="button" value="조회" title="조회" onclick="loadGridData();">
				</td>
			</tr>
		</table>

		<div id="grid_wrap" style="height: 705px; border-top: 1px solid #3180c3;"></div>
		<%@include file="/extcore/jsp/common/aui/aui-context.jsp"%>
		<script type="text/javascript">
			let myGridID;
			const list = ["유", "무"];
			function _layout() {
				return [ {
				dataField : "pdate",
				headerText : "발행일",
				dataType : "date",
				formatString : "yyyy-mm-dd",
				width : 120,
				editable : false,
				filter : {
					showIcon : true,
					inline : true,
					displayFormatValues : true
				},				
			}, {
				dataField : "install",
				headerText : "설치장소",
				dataType : "string",
				width : 100,
				editable : false,
				filter : {
					showIcon : true,
					inline : true
				},				
			}, {
				dataField : "kekNumber",
				headerText : "KEK작번",
				dataType : "string",
				width : 140,
				renderer : {
					type : "LinkRenderer",
					baseUrl : "javascript",
					jsCallback : function(rowIndex, columnIndex, value, item) {
						const oid = item.oid;
						alert("링크 준비중");
					}
				},				
				filter : {
					showIcon : true,
					inline : true
				},				
			}, {
				dataField : "keNumber",
				headerText : "KE작번",
				dataType : "string",
				width : 140,
				editable : false,
				renderer : {
					type : "LinkRenderer",
					baseUrl : "javascript",
					jsCallback : function(rowIndex, columnIndex, value, item) {
						const oid = item.oid;
						alert("링크 준비중");
					}
				},				
				filter : {
					showIcon : true,
					inline : true
				},				
			}, {
				dataField : "tuv",
				headerText : "TUV유무",
				dataType : "string",
				width : 130,
				renderer : {
					type : "IconRenderer",
					iconWidth : 16, 
					iconHeight : 16,
					iconPosition : "aisleRight",
					iconTableRef : { 
						"default" : "/Windchill/extcore/component/AUIGrid/images/list-icon.png" // default
					},
					onClick : function(event) {
						AUIGrid.openInputer(event.pid);
					}
				},
				editRenderer : {
					type : "ComboBoxRenderer",
					autoCompleteMode : true,
					autoEasyMode : true,
					matchFromFirst : false, 
					showEditorBtnOver : false,
					list : list,
					validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
						let isValid = false;
						for (let i = 0, len = list.length; i < len; i++) { // keyValueList 있는 값만..
							if (list[i] == newValue) {
								isValid = true;
								break;
							}
						}
						return {
							"validate" : isValid,
							"message" : "리스트에 있는 값만 선택(입력) 가능합니다."
						};
					}					
				},				
				filter : {
					showIcon : true,
					inline : true
				},			
			},
			<%for (Map<String, String> header : headers) {
	String key = header.get("key"); // spec code ....key
	String value = header.get("value");
	JSONArray array = JSONArray.fromObject(list.get(key));%>
			{
				dataField : "<%=key%>",
				headerText : "<%=value%>",
				dataType : "string",
				width : 130,
				renderer : {
					type : "IconRenderer",
					iconWidth : 16, 
					iconHeight : 16,
					iconPosition : "aisleRight",
					iconTableRef : { 
						"default" : "/Windchill/extcore/component/AUIGrid/images/list-icon.png" // default
					},
					onClick : function(event) {
						AUIGrid.openInputer(event.pid);
					}
				},
				editRenderer : {
					type : "ComboBoxRenderer",
					autoCompleteMode : true,
					autoEasyMode : true,
					matchFromFirst : false, 
					showEditorBtnOver : false,
					keyField : "key", 
					valueField : "value",
					list : <%=array%>,
					validator : function(oldValue, newValue, item, dataField, fromClipboard, which) {
						let isValid = false;
						for (let i = 0, len = <%=array%>.length; i < len; i++) { // keyValueList 있는 값만..
							if (<%=array%>[i]["value"] == newValue) {
								isValid = true;
								break;
							}
						}
						return {
							"validate" : isValid,
							"message" : "리스트에 있는 값만 선택(입력) 가능합니다."
						};
					}						
				},			
				labelFunction : function(rowIndex, columnIndex, value, headerText, item) { // key-value 에서 엑셀 내보내기 할 때 value 로 내보내기 위한 정의
					let retStr = ""; // key 값에 맞는 value 를 찾아 반환함.
					for (let i = 0, len = <%=array%>.length; i < len; i++) {
						if (<%=array%>[i]["key"] == value) {
							retStr = <%=array%>[i]["value"];
							break;
						}
					}
					return retStr == "" ? value : retStr;
				},				
				filter : {
					showIcon : true,
					inline : true
				},
			},
			<%}%>
			]}

			function createAUIGrid(columnLayout) {
				const props = {
					headerHeight : 30,
					rowHeight : 30,
					showRowNumColumn : true,
					showRowCheckColumn : true,
					showStateColumn : true,
					rowNumHeaderText : "번호",
					noDataMessage : "검색 결과가 없습니다.",
					enableFilter : true,
					selectionMode : "multipleCells",
					enableMovingColumn : true,
					showInlineFilter : true,
					useContextMenu : true,
					enableRightDownFocus : true,
					filterLayerWidth : 320,
					filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
					editable : true
				};
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				loadGridData();
				AUIGrid.bind(myGridID, "contextMenu", auiContextMenuHandler);
				AUIGrid.bind(myGridID, "vScrollChange", function(event) {
					hideContextMenu(); 
				});
				AUIGrid.bind(myGridID, "hScrollChange", function(event) {
					hideContextMenu(); 
				});
			}


			function save() {
				const url = getCallUrl("/history/save");
				const editRows = AUIGrid.getEditedRowItems(myGridID);
				const removeRows = AUIGrid.getRemovedItems(myGridID);
				const params = new Object();
				
				if(editRows.length ===0 && removeRows.length ===0){
					alert("변경된 내용이 없습니다.");
					return false;
				}
				
				
				if(!confirm("저장 하시겠습니까?")) {
					return false;
				}
				
				params.editRows = editRows;
				params.removeRows = removeRows;
				console.log(params);
				call(url, params, function(data) {
					alert(data.msg);
					if(data.result) {
						loadGridData();
					}
				})
			}
			
			function loadGridData() {
				const params = new Object();
				const url = getCallUrl("/history/list");
				const psize = document.getElementById("psize").value;
				params.psize = psize;
				AUIGrid.showAjaxLoader(myGridID);
				parent.openLayer();
				call(url, params, function(data) {
					AUIGrid.removeAjaxLoader(myGridID);
					document.getElementById("sessionid").value = data.sessionid;
					document.getElementById("curPage").value = data.curPage;
					AUIGrid.setGridData(myGridID, data.list);
					parent.closeLayer();
				});
			}

			document.addEventListener("DOMContentLoaded", function() {
				const columns = loadColumnLayout("history-list");
				const contenxtHeader = genColumnHtml(columns); // see auigrid.js
				$("#h_item_ul").append(contenxtHeader);
				$("#headerMenu").menu({
					select : headerMenuSelectHandler
				});
				createAUIGrid(columns);
				AUIGrid.resize(myGridID);
				finderUser("creator");
				twindate("created");
				selectbox("psize");
			});

			function exportExcel() {
				const exceptColumnFields = [];
				exportToExcel("이력관리 리스트", "이력관리", "이력관리 리스트", exceptColumnFields, "<%=sessionUser.getFullName()%>");
			}
			
			document.addEventListener("keydown", function(event) {
				const keyCode = event.keyCode || event.which;
				if (keyCode === 13) {
					loadGridData();
				}
			})

			document.addEventListener("click", function(event) {
				hideContextMenu();
			})

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID);
			});
		</script>
	</form>
</body>
</html>