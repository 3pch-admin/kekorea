<%@page import="java.util.Map"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.part.kePart.beans.KePartDTO"%>
<%@page import="e3ps.common.util.ContentUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
KePartDTO dto = (KePartDTO) request.getAttribute("dto");
Map<String, Object> primarys = (Map) request.getAttribute("primarys");
JSONArray list = (JSONArray) request.getAttribute("list");
JSONArray data = (JSONArray) request.getAttribute("data");
%>
<!-- AUIGrid -->
<%@include file="/extcore/include/auigrid.jsp"%>
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				KE 부품 정보
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
			<a href="#tabs-2">버전정보</a>
		</li>
	</ul>
	<div id="tabs-1">
		<table class="view-table">
			<colgroup>
				<col width="10%">
				<col width="400">
				<col width="130">
				<col width="400">
				<col width="130">
				<col width="400">
			</colgroup>
			<tr>
				<th class="lb">부품명</th>
				<td class="indent5"><%=dto.getName()%></td>
				<th>부품번호</th>
				<td class="indent5"><%=dto.getKeNumber()%></td>
				<th>LOT NO</th>
				<td class="indent5"><%=dto.getLotNo()%></td>
			</tr>
			<tr>
				<th class="lb">버전</th>
				<td class="indent5"><%=dto.getVersion()%></td>
				<th>중간코드</th>
				<td class="indent5"><%=dto.getCode()%></td>
				<th>KokusaiModel</th>
				<td class="indent5"><%=dto.getModel()%></td>
			</tr>
			<tr>
				<th class="lb">작성자</th>
				<td class="indent5"><%=dto.getCreator()%></td>
				<th>작성일</th>
				<td class="indent5"><%=dto.getCreatedDate_txt()%></td>
				<th>도면파일</th>
				<td class="indent5"><%=primarys.get("link") %>
<%-- 					<a href="<%=primarys[5]%>"> --%>
<%-- 						<span style="position: relative; bottom: 2px;"><%=primarys[2]%></span> --%>
<%-- 						<img src="<%=primarys[4]%>" style="position: relative; top: 1px;"> --%>
<!-- 					</a> -->
				</td>
			</tr>
			<tr>
				<th class="lb">수정자</th>
				<td class="indent5"><%=dto.getModifier()%></td>
				<th>수정일</th>
				<td class="indent5"><%=dto.getModifiedDate_txt()%></td>
				<th>상태</th>
				<td class="indent5"><%=dto.getState()%></td>
			</tr>
			<tr>
				<th class="lb">관련 T-BOM</th>
				<td class="indent5" colspan="5">
					<div id="_grid_wrap" style="height: 350px; border-top: 1px solid #3180c3; margin: 5px 5px 5px 5px;"></div>
					<script type="text/javascript">
						let _myGridID;
						const _columns = [ {
							dataField : "name",
							headerText : "T-BOM 제목",
							dataType : "string",
							width : 300,
							style : "underline",
							filter : {
								showIcon : true,
								inline : true
							},
							cellMerge : true
						}, {
							dataField : "info",
							headerText : "",
							width : 40,
							renderer : {
								type : "IconRenderer",
								iconWidth : 16, 
								iconHeight : 16,
								iconTableRef : { 
									"default" : "/Windchill/extcore/images/details.gif" 
								},
								onClick : function(event) {
									const oid = event.item.loid;
									const url = getCallUrl("/partlist/info?oid=" + oid);
									popup(url);
								}
							},
							filter : {
								showIcon : false,
								inline : false
							},
							cellMerge : true,
							mergeRef : "name",
							mergePolicy : "restrict"
						}, {
							dataField : "projectType_name",
							headerText : "설계구분",
							dataType : "string",
							width : 80,
							filter : {
								showIcon : true,
								inline : true
							},
						}, {
							dataField : "mak_name",
							headerText : "막종",
							dataType : "string",
							width : 100,
							filter : {
								showIcon : true,
								inline : true
							},
						}, {
							dataField : "detail_name",
							headerText : "막종상세",
							dataType : "string",
							width : 100,
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
									alert("( " + rowIndex + ", " + columnIndex + " ) " + item.color + "  Link 클릭\r\n자바스크립트 함수 호출하고자 하는 경우로 사용하세요!");
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
							style : "underline",
							width : 100,
							filter : {
								showIcon : true,
								inline : true
							},
						}, {
							dataField : "userId",
							headerText : "USER ID",
							dataType : "string",
							width : 100,
							filter : {
								showIcon : true,
								inline : true
							},
						}, {
							dataField : "description",
							headerText : "작업내용",
							dataType : "string",
							width : 300,
							style : "left",
							filter : {
								showIcon : true,
								inline : true
							},
						}, {
							dataField : "customer_name",
							headerText : "거래처",
							dataType : "string",
							width : 100,
							filter : {
								showIcon : true,
								inline : true
							},
						}, {
							dataField : "install_name",
							headerText : "설치 장소",
							dataType : "string",
							width : 100,
							filter : {
								showIcon : true,
								inline : true
							},
						}, {
							dataField : "pdate_txt",
							headerText : "발행일",
							dataType : "string",
							width : 100,
							filter : {
								showIcon : true,
								inline : true,
							},
						}, {
							dataField : "model",
							headerText : "모델",
							dataType : "string",
							width : 100,
							filter : {
								showIcon : true,
								inline : true
							},
						}, {
							dataField : "creator",
							headerText : "작성자",
							dataType : "string",
							width : 100,
							filter : {
								showIcon : true,
								inline : true
							},
							cellMerge : true,
							mergeRef : "name",
							mergePolicy : "restrict"
						}, {
							dataField : "createdDate_txt",
							headerText : "작성일",
							dataType : "string",
							width : 100,
							filter : {
								showIcon : true,
								inline : true,
							},
							cellMerge : true,
							mergeRef : "name",
							mergePolicy : "restrict"
						}, {
							dataField : "modifiedDate_txt",
							headerText : "수정일",
							dataType : "string",
							width : 100,
							filter : {
								showIcon : true,
								inline : true,
							},
							cellMerge : true,
							mergeRef : "name",
							mergePolicy : "restrict"
						}, {
							dataField : "state",
							headerText : "상태",
							dataType : "string",
							width : 100,
							filter : {
								showIcon : true,
								inline : true
							},
							cellMerge : true,
							mergeRef : "name",
							mergePolicy : "restrict"
						} ]

						function _createAUIGrid(columnLayout) {
							const props = {
								headerHeight : 30,
								rowHeight : 30,
								showRowNumColumn : true,
								showStateColumn : true,
								rowNumHeaderText : "번호",
								noDataMessage : "관련된 T-BOM이 없습니다.",
								enableFilter : true,
								selectionMode : "multipleCells",
								showInlineFilter : true,
								filterLayerWidth : 320,
								filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
								fixedColumnCount : 1,
								cellMergePolicy : "withNull",
								enableCellMerge : true,
							}
							_myGridID = AUIGrid.create("#_grid_wrap", columnLayout, props);
							AUIGrid.setGridData(_myGridID,
					<%=data%>
						);
						}
					</script>
				</td>
			</tr>
		</table>
	</div>
	<div id="tabs-2">
		<div id="grid_wrap" style="height: 550px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID;
			const columns = [ {
				dataField : "lotNo",
				headerText : "LOT",
				dataType : "numeric",
				width : 100,
				formatString : "###0",
				editRenderer : {
					type : "InputEditRenderer",
					onlyNumeric : true,
				},
				filter : {
					showIcon : true,
					inline : true,
					displayFormatValues : true
				},
			}, {
				dataField : "name",
				headerText : "DRAWING TITLE",
				dataType : "string",
				style : "left indent10",
				renderer : {
					type : "LinkRenderer",
					baseUrl : "javascript",
					jsCallback : function(rowIndex, columnIndex, value, item) {
						const oid = item.oid;
						const url = getCallUrl("/kePart/view?oid=" + oid);
						popup(url, 1100, 600);
					}
				},
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "keNumber",
				headerText : "DWG NO",
				dataType : "string",
				width : 200,
				renderer : {
					type : "LinkRenderer",
					baseUrl : "javascript",
					jsCallback : function(rowIndex, columnIndex, value, item) {
						const oid = item.oid;
						const url = getCallUrl("/kePart/view?oid=" + oid);
						popup(url, 1100, 600);
					}
				},
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "version",
				headerText : "버전",
				dataType : "numeric",
				width : 80,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "latest",
				headerText : "최신버전",
				dataType : "boolean",
				width : 100,
				renderer : {
					type : "CheckBoxEditRenderer"
				},
				filter : {
					showIcon : false,
					inline : false
				},
			}, {
				dataField : "creator",
				headerText : "작성자",
				dataType : "string",
				width : 100,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "createdDate_txt",
				headerText : "작성일",
				dataType : "string",
				width : 100,
				filter : {
					showIcon : true,
					inline : true,
				},
			}, {
				dataField : "modifier",
				headerText : "수정자",
				dataType : "string",
				width : 100,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "modifiedDate_txt",
				headerText : "수정일",
				dataType : "string",
				width : 100,
				filter : {
					showIcon : true,
					inline : true,
				},
			}, {
				dataField : "note",
				headerText : "개정사유",
				dateType : "string",
				width : 250,
				filter : {
					showIcon : true,
					inline : true
				},
			} ]

			function createAUIGrid(columnLayout) {
				const props = {
					headerHeight : 30,
					rowHeight : 30,
					showRowNumColumn : true,
					showStateColumn : true,
					rowNumHeaderText : "번호",
					enableFilter : true,
					selectionMode : "multipleCells",
					showInlineFilter : true,
					filterLayerWidth : 320,
					filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
				}
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				AUIGrid.setGridData(myGridID,
		<%=list%>
			);
			}
		</script>
	</div>
</div>

<script type="text/javascript">
	document.addEventListener("DOMContentLoaded", function() {
		$("#tabs").tabs({
			active : 0,
			create : function(event, ui) {
				const tabId = ui.panel.prop("id");
				switch (tabId) {
				case "tabs-1":
					_createAUIGrid(_columns);
					AUIGrid.resize(_myGridID);
					break;
				case "tabs-2":
					createAUIGrid(columns);
					AUIGrid.resize(myGridID);
					break;
				}
			},
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
				}
			}
		});
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
		AUIGrid.resize(_myGridID);
	});
</script>