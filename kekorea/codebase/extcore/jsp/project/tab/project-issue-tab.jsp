<%@page import="wt.org.WTUser"%>
<%@page import="e3ps.bom.tbom.service.TBOMHelper"%>
<%@page import="java.util.Map"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.korea.cip.dto.CipDTO"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = request.getParameter("oid");
JSONArray data = (JSONArray) request.getAttribute("data");
WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/jsp/common/css.jsp"%>
<%@include file="/extcore/jsp/common/script.jsp"%>
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>    
<style type="text/css">
#textAreaWrap {
	font-size: 12px;
	position: absolute;
	height: 100px;
	min-width: 100px;
	background: #fff;
	border: 1px solid #555;
	display: none;
	padding: 4px;
	text-align: right;
	z-index: 9999;
}

#textAreaWrap textarea {
	font-size: 12px;
	width: calc(100% - 6px);
}

.editor_btn {
	background: #ccc;
	border: 1px solid #555;
	cursor: pointer;
	margin: 2px;
	padding: 2px;
}

.nav_u {
	display: inline-block;
}

ul, ol {
	list-style: none;
	padding: 0;
	margin: 0;
}

.nav_u li {
	display: inline;
	white-space: nowrap;
	text-align: right;
}
</style>
</head>
<body>
	<form>
		<input type="hidden" name="isAdmin" id="isAdmin" value="<%=isAdmin%>">
		<input type="hidden" name="sessionName" id="sessionName" value="<%=sessionUser.getFullName()%>">
		<input type="hidden" name="sessionId" id="sessionId" value="<%=sessionUser.getName()%>">
		<input type="hidden" name="oid" id="oid" value="<%=oid%>">
		<table class="button-table">
			<tr>
				<td class="left">
					<input type="button" value="저장" title="저장" onclick="save();">
					<input type="button" value="등록" title="등록" class="blue" onclick="create();">
					<input type="button" value="행 삭제" title="행 삭제" class="red" onclick="deleteRow();">
				</td>
			</tr>
		</table>
		<div id="grid_wrap3" style="height: 740px; border-top: 1px solid #3180c3;"></div>
		<div id="textAreaWrap">
			<textarea id="myTextArea" class="aui-grid-custom-renderer-ext" style="height: 90px;"></textarea>
			<ul class="nav_u">
				<li>
					<button class="editor_btn" id="editEnd">확인</button>
				</li>
				<li>
					<button class="editor_btn" id="cancel">취소</button>
				</li>
			</ul>
		</div>
		<script type="text/javascript">
			let myGridID3;
			let recentGridItem = null;
			const data = <%=data%>
			const columns3 = [ {
				dataField : "name",
				headerText : "특이사항 제목",
				dataType : "string",
				width : 300,
				style : "aui-left",
				renderer : {
					type : "LinkRenderer",
					baseUrl : "javascript",
					jsCallback : function(rowIndex, columnIndex, value, item) {
						const oid = item.oid;
						const url = getCallUrl("/issue/view?oid=" + oid);
						popup(url, 1400, 700);
					}
				},
			}, {
				dataField : "description",
				headerText : "내용",
				dataType : "string",
				style : "aui-left",
				renderer : {
					type : "LinkRenderer",
					baseUrl : "javascript",
					jsCallback : function(rowIndex, columnIndex, value, item) {
						const oid = item.oid;
						const url = getCallUrl("/issue/view?oid=" + oid);
						popup(url, 1400, 700);
					}
				},
			}, {
				dataField : "icons",
				headerText : "첨부파일",
				width : 100,
				renderer : {
					type : "TemplateRenderer"
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
			} ]

			function createAUIGrid3(columnLayout) {
				const props = {
					headerHeight : 30,
					showRowNumColumn : true,
					rowNumHeaderText : "번호",
					selectionMode : "multipleCells",
					showAutoNoDataMessage : false,
					showRowCheckColumn : true,
					showStateColumn : true,
				};
				myGridID3 = AUIGrid.create("#grid_wrap3", columnLayout, props);
				AUIGrid.setGridData(myGridID3, data);
				AUIGrid.bind(myGridID3, "cellEditBegin", auiCellEditBeginHandler);
				AUIGrid.bind(myGridID3, "pasteBegin", auiPasteBeginHandler);
			}

			function auiPasteBeginHandler(event) {
				const data = event.clipboardData;
				let arr;
				let i, j, len, len2, str;
				// 엑셀 개행 문자가 없는 경우
				if (data.indexOf("\n") === -1) {
					return data;
				}

				arr = CSVToArray(data, "\t"); // tab 문자 구성 String 을 배열로 반환
				if (arr && arr.length) {
					if (String(arr[arr.length - 1]).trim() == "") { // 마지막 빈 값이 삽입되는 경우가 존재함.
						arr.pop();
					}
					for (i = 0, len = arr.length; i < len; i++) {
						arr2 = arr[i];
						if (arr2 && arr2.length) {
							for (j = 0, len2 = arr2.length; j < len2; j++) {
								str = arr2[j];
								arr[i][j] = str.replace(/\n/g, "<br/>"); // 엑셀 개행 문자를 br 태그로 변환
							}
						}
					}
				}
				return arr;
			}

			function CSVToArray(strData, strDelimiter) {
				strDelimiter = (strDelimiter || ",");
				const objPattern = new RegExp(("(\\" + strDelimiter + "|\\r?\\n|\\r|^)" + "(?:\"([^\"]*(?:\"\"[^\"]*)*)\"|" + "([^\"\\" + strDelimiter + "\\r\\n]*))"), "gi");
				let arrData = [ [] ];
				let arrMatches = null;
				while (arrMatches = objPattern.exec(strData)) {
					const strMatchedDelimiter = arrMatches[1];
					if (strMatchedDelimiter.length && strMatchedDelimiter !== strDelimiter) {
						arrData.push([]);
					}
					let strMatchedValue;
					if (arrMatches[2]) {
						strMatchedValue = arrMatches[2].replace(new RegExp("\"\"", "g"), "\"");
					} else {
						strMatchedValue = arrMatches[3];
					}
					arrData[arrData.length - 1].push(strMatchedValue);
				}
				return (arrData);
			};

			function auiCellEditBeginHandler(event) {
				if (event.isClipboard) {
					return true;
				}
				if (event.dataField === "description") {
					openTextarea(event);
				} else {
					return true;
				}
			}

			function openTextarea(event) {
				const dataField = event.dataField;
				const obj = document.getElementById("textAreaWrap");
				const textArea = document.getElementById("myTextArea");
				obj.style.left = event.position.x + "px";
				obj.style.top = event.position.y + "px";
				obj.style.width = (event.size.width - 8) + "px";
				obj.style.height = "125px";
				obj.style.display = "block";
				textArea.value = String(event.value).replace(/[<]br[/][>]/gi, "\r\n");
				obj.setAttribute("data-field", dataField);
				// 행인덱스 보관
				obj.setAttribute("data-row-index", event.rowIndex);

				// 포커싱
				setTimeout(function() {
					textArea.focus();
					textArea.select();
				}, 16);
			}

			function forceEditngTextArea(value, event) {
				const dataField = document.getElementById("textAreaWrap").getAttribute("data-field"); // 보관한 dataField 얻기
				const rowIndex = Number(document.getElementById("textAreaWrap").getAttribute("data-row-index")); // 보관한 rowIndex 얻기
				value = value.replace(/\r|\n|\r\n/g, "<br/>");

				const item = {};
				item[dataField] = value;

				AUIGrid.updateRow(myGridID3, item, rowIndex);
				document.getElementById("textAreaWrap").style.display = "none";
				event.preventDefault();
			};

			document.getElementById("myTextArea").addEventListener("blur", function(event) {
				const relatedTarget = event.relatedTarget || document.activeElement;

				// 확인 버튼 클릭한 경우
				if (relatedTarget.getAttribute("id") === "editEnd") {
					return;
				} else if (relatedTarget.getAttribute("id") === "cancel") { // 취소 버튼
					return;
				}
				forceEditngTextArea(this.value, event);
			});

			function deleteRow() {
				const checkedItems = AUIGrid.getCheckedRowItems(myGridID3);
				// 				const sessionId = document.getElementById("sessionId").value;
				for (let i = checkedItems.length - 1; i >= 0; i--) {
					const item = checkedItems[i].item;
					// 					if (!checker(sessionId, item.creatorId)) {
					// 						alert("데이터 작성자가 아닙니다.");
					// 						return false;
					// 					}
					const rowIndex = checkedItems[i].rowIndex;
					AUIGrid.removeRow(myGridID3, rowIndex);
				}
			}

			function create() {
				const oid = document.getElementById("oid").value;
				const url = getCallUrl("/issue/create?oid=" + oid);
				popup(url, 1400, 700);
			}

			function attach(data) {
				let template = "";
				const arr = new Array();
				for (let i = 0; i < data.length; i++) {
					template += "<img style='position: relative; top: 2px' src='" + data[i].icon + "'>&nbsp;";
					arr.push(data[i].cacheId);
				}

				AUIGrid.updateRowsById(myGridID3, {
					_$uid : recentGridItem._$uid,
					secondarys : arr,
					icons : template
				});
			}

			document.getElementById("cancel").addEventListener("click", function(event) {
				document.getElementById("textAreaWrap").style.display = "none";
				event.preventDefault();
			});

			document.getElementById("editEnd").addEventListener("click", function(event) {
				const value = document.getElementById("myTextArea").value;
				forceEditngTextArea(value, event);
			});

			document.addEventListener("DOMContentLoaded", function() {
				// 화면 활성화시 불러오게 설정한다 속도 생각 
				createAUIGrid3(columns3);
				AUIGrid.resize(myGridID3);
			})

			window.addEventListener("resize", function() {
				AUIGrid.resize(myGridID3);
			});
		</script>
	</form>
</body>
</html>