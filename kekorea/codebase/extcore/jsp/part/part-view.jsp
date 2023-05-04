<%@page import="e3ps.part.service.PartHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="e3ps.part.dto.PartDTO"%>
<%@page import="e3ps.common.util.ContentUtils"%>
<%@page import="net.sf.json.JSONArray"%>
<%
PartDTO dto = (PartDTO) request.getAttribute("dto");
JSONArray versionHistory = (JSONArray) request.getAttribute("versionHistory");
%>
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				부품 정보
			</div>
		</td>
		<td class="right">
			<input type="button" value="수정" title="수정" onclick="modify();">
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
			<a href="#tabs-2">PROE 속성</a>
		</li>
		<li>
			<a href="#tabs-3">버전정보</a>
		</li>
		<li>
			<a href="#tabs-4">결재이력</a>
		</li>
	</ul>
	<div id="tabs-1">
		<table class="view-table">
			<colgroup>
				<col style="width: 10%;">
				<col style="width: 30%;">
				<col style="width: 10%;">
				<col style="width: 20%;">
				<col style="width: 30%;">
			</colgroup>
			<tr>
				<th class="lb">파일이름</th>
				<td class="indent5" colspan="3"><%=dto.getName()%></td>
				<td class="center" rowspan="6">
					<img src="<%=dto.getPreView()%>" style="height: 140px; cursor: pointer;" onclick="preView();" title="클릭시 원본크기로 볼 수 있습니다.">
				</td>
			</tr>
			<tr>
				<th class="lb">버전</th>
				<td class="indent5"><%=dto.getVersion()%></td>
				<th class="lb">상태</th>
				<td class="indent5"><%=dto.getState()%></td>
			</tr>
			<tr>
				<th class="lb">저장위치</th>
				<td class="indent5"><%=dto.getLocation()%></td>
				<th class="lb">도면</th>
				<td class="indent5"></td>
			</tr>
			<tr>
				<th class="lb">작성자</th>
				<td class="indent5"><%=dto.getCreator()%></td>
				<th class="lb">작성일</th>
				<td class="indent5"><%=dto.getCreatedDate_txt()%></td>
			</tr>
			<tr>
				<th class="lb">수정자</th>
				<td class="indent5"><%=dto.getModifier()%></td>
				<th class="lb">수정일</th>
				<td class="indent5"><%=dto.getModifiedDate_txt()%></td>
			</tr>
			<tr>
				<th class="lb">도면파일</th>
				<td class="indent5" colspan="3"></td>
			</tr>
			<tr>
				<th class="lb">주 첨부파일</th>
				<td class="indent5" colspan="4">
					<jsp:include page="/extcore/jsp/common/primary-view.jsp">
						<jsp:param value="<%=dto.getOid()%>" name="oid" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="lb">관련문서</th>
				<td colspan="4">
					<div id="_grid_wrap" style="height: 120px; border-top: 1px solid #3180c3; margin: 5px;"></div>
					<script type="text/javascript">
						let _myGridID;
						const _columns = [ {
							dataField : "number",
							headerText : "문서번호",
							dataType : "string",
							width : 150,
							renderer : {
								type : "LinkRenderer",
								baseUrl : "javascript",
								jsCallback : function(rowIndex, columnIndex, value, item) {
									const oid = item.oid;
									const url = getCallUrl("/doc/view?oid=" + oid);
									popup(url, 1600, 800);
								}
							},
						}, {
							dataField : "name",
							headerText : "문서 제목",
							dataType : "string",
							style : "aui-left",
							renderer : {
								type : "LinkRenderer",
								baseUrl : "javascript",
								jsCallback : function(rowIndex, columnIndex, value, item) {
									const oid = item.oid;
									const url = getCallUrl("/doc/view?oid=" + oid);
									popup(url, 1600, 800);
								}
							},
						}, {
							dataField : "version",
							headerText : "버전",
							dataType : "string",
							width : 100,
						}, {
							dataField : "state",
							headerText : "상태",
							dataType : "string",
							width : 100,
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
						}, {
							dataField : "primary",
							headerText : "주 첨부파일",
							dataType : "string",
							width : 100,
							renderer : {
								type : "TemplateRenderer"
							}
						}, {
							dataField : "secondary",
							headerText : "첨부파일",
							dataType : "string",
							width : 150,
							renderer : {
								type : "TemplateRenderer"
							}
						} ]
						function _createAUIGrid(columnLayout) {
							const props = {
								headerHeight : 30,
								showRowNumColumn : true,
								rowNumHeaderText : "번호",
								showAutoNoDataMessage : false,
								enableSorting : false,
							}
							_myGridID = AUIGrid.create("#_grid_wrap", columnLayout, props);
							AUIGrid.setGridData(_myGridID,
					<%=PartHelper.manager.jsonAuiDocument(dto.getOid())%>
						);
						}
					</script>
				</td>
			</tr>
		</table>
	</div>
	<div id="tabs-2">
		<table class="view-table">
			<colgroup>
				<col style="width: 10%;">
				<col style="width: 40%;">
				<col style="width: 10%;">
				<col style="width: 40%;">
			</colgroup>
			<tr>
				<th class="lb">NAME_OF_PARTS</th>
				<td class="indent5"><%=dto.getName_of_parts()%></td>
				<th class="lb">DWG_NO</th>
				<td class="indent5"><%=dto.getDwg_no()%></td>
			</tr>
			<tr>
				<th class="lb">MATERIAL</th>
				<td class="indent5"><%=dto.getMaterial()%></td>
				<th class="lb">REMARKS</th>
				<td class="indent5"><%=dto.getRemarks()%></td>
			</tr>
			<tr>
				<th class="lb">PART_CODE</th>
				<td class="indent5"><%=dto.getPart_code()%></td>
				<th class="lb">STD_UNIT</th>
				<td class="indent5"></td>
			</tr>
			<tr>
				<th class="lb">MAKER</th>
				<td class="indent5"><%=dto.getMaker()%></td>
				<th class="lb">CUSNAME</th>
				<td class="indent5"></td>
			</tr>
			<tr>
				<th class="lb">PRICE</th>
				<td class="indent5"></td>
				<th class="lb">CURRNAME</th>
				<td class="indent5"></td>
			</tr>
			<tr>
				<th class="lb">REF_NO</th>
				<td class="indent5" colspan="3"></td>
			</tr>
		</table>
	</div>
	<div id="tabs-3">
		<div id="tabs-2">
			<!-- 버전이력 쭉 쌓이게 autoGrid 설정 true -->
			<div id="grid_wrap" style="height: 350px; border-top: 1px solid #3180c3; margin: 5px;"></div>
			<script type="text/javascript">
				let myGridID;
				const columns = [ {
					dataField : "number",
					headerText : "부품번호",
					dataType : "string",
					width : 120,
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const oid = item.oid;
							const url = getCallUrl("/part/view?oid=" + oid);
							popup(url, 1500, 800);
						}
					},
				}, {
					dataField : "name",
					headerText : "부품명",
					dataType : "string",
					style : "aui-left",
					renderer : {
						type : "LinkRenderer",
						baseUrl : "javascript",
						jsCallback : function(rowIndex, columnIndex, value, item) {
							const oid = item.oid;
							const url = getCallUrl("/part/view?oid=" + oid);
							popup(url, 1500, 800);
						}
					},
				}, {
					dataField : "version",
					headerText : "버전",
					dataType : "string",
					width : 100,
				}, {
					dataField : "creator",
					headerText : "작성자",
					dataType : "string",
					width : 100,
				}, {
					dataField : "createdDate_txt",
					headerText : "거래처",
					dataType : "string",
					width : 100,
				}, {
					dataField : "modifier",
					headerText : "수정자",
					dataType : "string",
					width : 100,
				}, {
					dataField : "modifiedDate_txt",
					headerText : "수정일",
					dataType : "string",
					width : 100,
				}, {
					dataField : "primary",
					headerText : "주 첨부파일",
					dataType : "string",
					width : 100,
					renderer : {
						type : "TemplateRenderer"
					}
				}, ]

				function createAUIGrid(columnLayout) {
					const props = {
						headerHeight : 30,
						showRowNumColumn : true,
						rowNumHeaderText : "번호",
						showAutoNoDataMessage : false,
						softRemoveRowMode : false,
						autoGridHeight : true,
					}
					myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
					AUIGrid.setGridData(myGridID,
			<%=versionHistory%>
				);
				}
			</script>
		</div>
	</div>
	<div id="tabs-4">
		<!-- 결재이력 -->
		<jsp:include page="/extcore/jsp/common/approval-history.jsp">
			<jsp:param value="<%=dto.getOid()%>" name="oid" />
		</jsp:include>
	</div>
</div>
<script type="text/javascript">
	function preView() {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/aui/thumbnail?oid=" + oid);
		popup(url, 1400, 600);
	}

	function modify() {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/part/modify?oid=" + oid);
		openLayer();
		document.location.href = url;
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
				case "tabs-3":
					const isCreated = AUIGrid.isCreated(myGridID);
					if (isCreated) {
						AUIGrid.resize(myGridID);
					} else {
						createAUIGrid(columns);
					}
					break;
				case "tabs-4":
					const isCreated100 = AUIGrid.isCreated(myGridID100);
					if (isCreated100) {
						AUIGrid.resize(myGridID100);
					} else {
						createAUIGrid100(columns100);
					}
					break;
				}
			},
		});
		_createAUIGrid(_columns);
		createAUIGrid(columns);
		createAUIGrid100(columns100);
		AUIGrid.resize(_myGridID);
		AUIGrid.resize(myGridID);
		AUIGrid.resize(myGridID100);
	});
	window.addEventListener("resize", function() {
		AUIGrid.resize(_myGridID);
		AUIGrid.resize(myGridID);
		AUIGrid.resize(myGridID100);
	});
</script>