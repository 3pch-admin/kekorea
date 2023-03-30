<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.common.util.ContentUtils"%>
<%@page import="e3ps.epm.keDrawing.dto.KeDrawingDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
KeDrawingDTO dto = (KeDrawingDTO) request.getAttribute("dto");
String[] primarys = (String[]) request.getAttribute("primarys");
JSONArray list = (JSONArray) request.getAttribute("list");
JSONArray data = (JSONArray) request.getAttribute("data");
%>
<%@include file="/extcore/include/auigrid.jsp"%>
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				KE 도면 정보
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
				<col width="300">
				<col width="130">
				<col width="300">
				<col width="200">
			</colgroup>
			<tr>
				<th class="lb">DRAWING TITLE</th>
				<td class="indent5"><%=dto.getName()%></td>
				<th>DWG NO</th>
				<td class="indent5"><%=dto.getKeNumber()%></td>
				<td class="center" rowspan="5">
					<img src="<%=dto.getPreView()%>" style="height: 140px; cursor: pointer;" onclick="preView();" title="클릭시 원본크기로 볼 수 있습니다.">
				</td>
			</tr>
			<tr>
				<th class="lb">LOT NO</th>
				<td class="indent5"><%=dto.getLotNo()%></td>
				<th>버전</th>
				<td class="indent5"><%=dto.getVersion()%></td>
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
				<th class="lb">도면파일</th>
				<td class="indent5" colspan="3">
					<a href="<%=primarys[5]%>">
						<span style="position: relative; bottom: 2px;"><%=primarys[2]%></span>
						<img src="<%=primarys[4]%>" style="position: relative; top: 1px;">
					</a>
				</td>
			</tr>
			<tr>
				<th class="lb">도면일람표</th>
				<td class="indent5" colspan="4">
					<div id="_grid_wrap" style="height: 350px; border-top: 1px solid #3180c3; margin: 5px 5px 5px 5px;"></div>
					<script type="text/javascript">
						let _myGridID;
						const _columns = [ {
							dataField : "name",
							headerText : "도면일람표 제목",
							dataType : "string",
							width : 350,
							style : "left indent10 underline",
							renderer : {
								type : "LinkRenderer",
								baseUrl : "javascript",
								jsCallback : function(rowIndex, columnIndex, value, item) {
									const oid = item.oid;
									const url = getCallUrl("/workOrder/view?oid=" + oid);
									popup(url);
								}
							},
							filter : {
								showIcon : true,
								inline : true
							},
							cellMerge : true
						}, {
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
							width : 100,
							filter : {
								showIcon : true,
								inline : true
							},
						}, {
							dataField : "install_name",
							headerText : "설치장소",
							dataType : "string",
							width : 100,
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
							width : 130,
							style : "underline",
							filter : {
								showIcon : true,
								inline : true
							},
						}, {
							dataField : "keNumber",
							headerText : "KE 작번",
							dataType : "string",
							width : 130,
							style : "underline",
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
							headerText : "작업 내용",
							dataType : "string",
							width : 450,
							style : "left indent10",
							filter : {
								showIcon : true,
								inline : true
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
							dataField : "pdate_txt",
							headerText : "발행일",
							dataType : "string",
							width : 100,
							filter : {
								showIcon : true,
								inline : true
							},
						}, {
							dataField : "state",
							headerText : "상태",
							dataType : "string",
							width : 80,
							filter : {
								showIcon : true,
								inline : true
							},
							cellMerge : true,
							mergeRef : "name",
							mergePolicy : "restrict"
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
								inline : true
							},
							cellMerge : true,
							mergeRef : "name",
							mergePolicy : "restrict"
						}, {
							dataField : "cover",
							headerText : "표지",
							dataType : "string",
							width : 80,
							editable : false,
							renderer : {
								type : "TemplateRenderer",
							},
							filter : {
								showIcon : false,
								inline : false
							},
							cellMerge : true,
							mergeRef : "name",
							mergePolicy : "restrict"
						}, {
							dataField : "secondary",
							headerText : "첨부파일",
							dataType : "string",
							width : 80,
							editable : false,
							renderer : {
								type : "TemplateRenderer",
							},
							filter : {
								showIcon : false,
								inline : false
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
								noDataMessage : "관련된 도면일람표가 없습니다.",
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
						const url = getCallUrl("/keDrawing/view?oid=" + oid);
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
						const url = getCallUrl("/keDrawing/view?oid=" + oid);
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
	function preView() {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/aui/thumbnail?oid=" + oid);
		popup(url);
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