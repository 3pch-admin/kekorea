<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.common.util.ContentUtils"%>
<%@page import="e3ps.workspace.notice.dto.NoticeDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
NoticeDTO dto = (NoticeDTO) request.getAttribute("dto");
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
				공지사항 정보
			</div>
		</td>
		<td class="right">
			<input type="button" value="삭제" title="삭제" class="red" >
			<input type="button" value="수정" title="수정" class="green">
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
		<table class="view-table">
			<colgroup>
				<col width="10%">
				<col width="300">
				<col width="130">
				<col width="300">
			</colgroup>
			<tr>
				<th class="lb">공지사항 제목</th>
				<td colspan="3" class="indent5"></td>
			</tr>
			<tr>
				<th class="lb">작성자</th>
				<td class="indent5"></td>
				<th class="lb">작성일</th>
				<td class="indent5"></td> 
			</tr>
			<tr>
				<th class="lb">설명</th>
				<td colspan="3" class="indent5" >
				<textarea name="descriptionNotice" id="descriptionNotice" rows="4" cols="" readonly="readonly"></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">주 첨부파일</th>
				<td colspan="3" class="indent5"></td>
			</tr>
			<tr>
				<th class="lb">첨부파일</th>
				<td colspan="3" class="indent5"></td>
			</tr>
			<tr>
				<th class="lb">공지사항</th>
				<td class="indent5" colspan="4">
					<div id="_grid_wrap" style="height: 350px; border-top: 1px solid #3180c3; margin: 5px 5px 5px 5px;"></div>
					<script type="text/javascript">
						let _myGridID;
						const _columns = [ {
							dataField : "name",
							headerText : "공지사항 제목",
							dataType : "string",
							width : 350,
							style : "aui-left",
							renderer : {
								type : "LinkRenderer",
								baseUrl : "javascript",
								jsCallback : function(rowIndex, columnIndex, value, item) {
									const oid = item.oid;
									const url = getCallUrl("/notice/view?oid=" + oid);
									popup(url, 1400, 700);
								}
							},
							filter : {
								showIcon : true,
								inline : true
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
									const url = getCallUrl("/notice/view?oid=" + oid);
									popup(url, 1400, 700);
								}
							},
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
						}, {
							dataField : "createdDate",
							headerText : "작성일",
							dataType : "date",
							formatString : "yyyy-mm-dd",
							width : 100,
							filter : {
								showIcon : true,
								inline : true,
								displayFormatValues : true
							},
						}, {
							dataField : "primary",
							headerText : "첨부파일",
							dataType : "string",
							width : 80,
							renderer : {
								type : "TemplateRenderer"
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
								// 그리드 공통속성 시작
								headerHeight : 30,
								rowHeight : 30,
								showRowNumColumn : true,
								showStateColumn : true,
								rowNumHeaderText : "번호",
								noDataMessage : "관련된 공지사항이 없습니다.",
								enableFilter : true,
								selectionMode : "multipleCells",
								showInlineFilter : true,
								filterLayerWidth : 320,
								filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
								// 그리드 공통속성 끝
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
				}
			},
			activate : function(event, ui) {
				var tabId = ui.newPanel.prop("id");
				switch (tabId) {
				case "tabs-1":
					const _isCreated = AUIGrid.isCreated(_myGridID_);
					if (_isCreated_ && _isCreated) {
						AUIGrid.resize(_myGridID);
					} else {
						_createAUIGrid(_columns);
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