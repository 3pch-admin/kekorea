<%@page import="wt.log4j.SystemOutFacade"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.common.util.ContentUtils"%>
<%@page import="e3ps.epm.dto.EpmDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
EpmDTO dto = (EpmDTO) request.getAttribute("dto");
JSONArray list = (JSONArray) request.getAttribute("list");
JSONArray data = (JSONArray) request.getAttribute("data");
JSONArray history = (JSONArray) request.getAttribute("history");
%>
<%@include file="/extcore/include/auigrid.jsp"%>
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				KEK 도면 정보
			</div>
		</td>
		<td class="right">
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
		<li>
			<a href="#tabs-2">도면속성</a>
		</li>
		<li>
			<a href="#tabs-3">관련작번</a>
		</li>
		<li>
			<a href="#tabs-4">버전이력</a>
		</li>
		<li>
			<a href="#tabs-5">결재이력</a>
		</li>
	</ul>
	<div id="tabs-1">
		<table class="view-table">
			<colgroup>
				<col width="150">
				<col width="250">
				<col width="150">
				<col width="250">
				<col width="400">
			</colgroup>
			<tr>
				<th class="lb">도면 번호</th>
				<td class="indent5" colspan="3"></td>
				<td class="center" rowspan="10">
					<img src="<%=dto.getPreView()%>" style="height: 140px; cursor: pointer;" onclick="preView();" title="클릭시 원본크기로 볼 수 있습니다.">
				</td>
			</tr>
			<tr>
				<th class="lb">도면 이름</th>
				<td class="indent5" colspan="3"><%=dto.getName()%></td>
			</tr>
			<tr>
				<th class="lb">버전</th>
				<td class="indent5" colspan="3"><%=dto.getVersion()%></td>
			</tr>
			<tr>
				<th class="lb">상태</th>
				<td class="indent5"><%=dto.getState()%></td>
				<th class="lb">저장위치</th>
				<td class="indent5"><%=dto.getLocation()%></td>
			</tr>
			<tr>
				<th class="lb">종류</th>
				<td class="indent5"></td>
				<th class="lb">응용프로그램</th>
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
				<td class="indent5"></td>
				<th class="lb">변환 파일</th>
				<td class="indent5"></td>
			</tr>
			<tr>
				<th class="lb">부품</th>
				<td class="indent5" colspan="3"></td>
			</tr>
			<tr>
				<th class="lb">설명</th>
				<td colspan="3" class="indent5">
					<textarea name="descriptionNotice" id="descriptionNotice" rows="7" cols="" readonly="readonly"><%=dto.getDescription()%></textarea>
				</td>
			</tr>
		</table>
	</div>
	<div id="tabs-2">
		<table class="view-table">
			<colgroup>
				<col width="150">
				<col width="*">
				<col width="150">
				<col width="*">
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
				<td class="indent5"><%=dto.getRemark()%></td>
			</tr>
			<tr>
				<th class="lb">PART_CODE</th>
				<td class="indent5"><%=dto.getPart_code()%></td>
				<th class="lb">STD_UNIT</th>
				<td class="indent5"></td>
			</tr>
			<tr>
				<th class="lb">MAKER</th>
				<td class="indent5"></td>
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
				<td class="indent5" colspan="3"><%=dto.getReference()%></td>
			</tr>
		</table>
	</div>
	<div id="tabs-3">
		<div id="grid_wrap" style="height: 300px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let myGridID;
			const data =
		<%=data%>
			const columns = [ {
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
				renderer : {
					type : "LinkRenderer",
					baseUrl : "javascript",
					jsCallback : function(rowIndex, columnIndex, value, item) {
						const oid = item.oid;
						alert(oid);
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
				width : 130,
				renderer : {
					type : "LinkRenderer",
					baseUrl : "javascript",
					jsCallback : function(rowIndex, columnIndex, value, item) {
						const oid = item.oid;
						alert(oid);
					}
				},
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "description",
				headerText : "작업 내용",
				dataType : "string",
				style : "aui-left",
				filter : {
					showIcon : true,
					inline : true
				}
			} ]

			function createAUIGrid(columnLayout) {
				const props = {
					headerHeight : 30,
					rowHeight : 30,
					showRowNumColumn : true,
					showStateColumn : true,
					rowNumHeaderText : "번호",
					selectionMode : "multipleCells",
				}
				myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
				AUIGrid.setGridData(myGridID, data);
			}
		</script>
	</div>
	<div id="tabs-4">
		<div id="_grid_wrap" style="height: 460px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let _myGridID;
			const _columns = [ {
				dataField : "name",
				headerText : "파일이름",
				dataType : "string",
				width : 500,
				style : "aui-left",
				renderer : {
					type : "LinkRenderer",
					baseUrl : "javascript",
				},
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "version",
				headerText : "버전",
				dataType : "string",
				width : 80,
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
				dataType : "date",
				formatString : "yyyy-mm-dd",
				width : 130,
				filter : {
					showIcon : true,
					inline : true,
					displayFormatValues : true
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
				dataType : "date",
				formatString : "yyyy-mm-dd",
				width : 130,
				filter : {
					showIcon : true,
					inline : true,
					displayFormatValues : true
				}
			} ]
			function _createAUIGrid(columnLayout) {
				const props = {
					headerHeight : 30,
					rowHeight : 30,
					showRowNumColumn : true,
					showStateColumn : true,
					rowNumHeaderText : "번호",
					noDataMessage : "검색 결과가 없습니다.",
					enableFilter : true,
					selectionMode : "multipleCells",
					enableMovingColumn : true,
					showInlineFilter : true,
					enableRightDownFocus : true,
					filterLayerWidth : 320,
					filterItemMoreMessage : "필터링 검색이 너무 많습니다. 검색을 이용해주세요.",
				}
				_myGridID = AUIGrid.create("#_grid_wrap", columnLayout, props);
				AUIGrid.setGridData(_myGridID,
		<%=list%>
			);
			}
		</script>
	</div>
	<div id="tabs-5">
		<div id="_grid_wrap_" style="height: 460px; border-top: 1px solid #3180c3;"></div>
		<script type="text/javascript">
			let _myGridID_;
			const history =
		<%=history%>
			const _columns_ = [ {
				dataField : "type",
				headerText : "구분",
				dataType : "string",
				width : 80,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "role",
				headerText : "역할",
				dataType : "string",
				width : 80,
				filter : {
					showIcon : true,
					inline : true
				},
			}, {
				dataField : "name",
				headerText : "결재제목",
				dataType : "string",
				style : "aui-left",
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
			}, {
				dataField : "owner",
				headerText : "담당자",
				dataType : "string",
				width : 80
			}, {
				dataField : "receiveTime",
				headerText : "수신일",
				dataType : "date",
				formatString : "yyyy-mm-dd HH:MM:ss",
				width : 130,
				filter : {
					showIcon : true,
					inline : true,
					displayFormatValues : true
				},
			}, {
				dataField : "completeDate_txt",
				headerText : "완료일",
				dataType : "date",
				formatString : "yyyy-mm-dd HH:MM:ss",
				width : 130,
				filter : {
					showIcon : true,
					inline : true,
					displayFormatValues : true
				},
			}, {
				dataField : "",
				headerText : "결재의견",
				dataType : "string",
				width : 130,
				filter : {
					showIcon : true,
					inline : true
				},
			} ]

			function _createAUIGrid_(columnLayout) {
				const props = {
					headerHeight : 30,
					rowHeight : 30,
					showRowNumColumn : true,
					showStateColumn : true,
					rowNumHeaderText : "번호",
					selectionMode : "multipleCells",
					noDataMessage : "결재이력이 없습니다."
				};
				_myGridID_ = AUIGrid.create("#_grid_wrap_", columnLayout, props);
				AUIGrid.setGridData(_myGridID_, history);
			}
		</script>
	</div>
</div>
<script type="text/javascript">
	function preView() {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/aui/thumbnail?oid=" + oid);
		popup(url, 1400, 600);
	}
	document.addEventListener("DOMContentLoaded", function() {
		$("#tabs").tabs({
			active : 0,
			create : function(event, ui) {
				const tabId = ui.panel.prop("id");
				switch (tabId) {
				case "tabs-3":
					createAUIGrid(columns);
					AUIGrid.resize(myGridID);
					break;
				case "tabs-4":
					_createAUIGrid(_columns);
					AUIGrid.resize(_myGridID);
					break;
				case "tabs-5":
					_createAUIGrid_(_columns_);
					AUIGrid.resize(_myGridID_);
					break;
				}
			},
			activate : function(event, ui) {
				var tabId = ui.newPanel.prop("id");
				switch (tabId) {
				case "tabs-3":
					const isCreated = AUIGrid.isCreated(myGridID);
					if (isCreated) {
						AUIGrid.resize(myGridID);
					} else {
						createAUIGrid(columns);
					}
					break;
				case "tabs-4":
					const _isCreated = AUIGrid.isCreated(_myGridID);
					if (_isCreated) {
						AUIGrid.resize(_myGridID);
					} else {
						_createAUIGrid(_columns);
					}
					break;
				case "tabs-5":
					const _isCreated_ = AUIGrid.isCreated(_myGridID_);
					if (_isCreated_) {
						AUIGrid.resize(_myGridID_);
					} else {
						_createAUIGrid_(_columns_);
					}
					break;
				}
			}
		});
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
		AUIGrid.resize(_myGridID);
		AUIGrid.resize(_myGridID_);
	});
</script>