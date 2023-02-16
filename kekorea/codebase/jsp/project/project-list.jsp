<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.admin.commonCode.CommonCodeType"%>
<%@page import="org.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
String before = (String) request.getAttribute("before");
String end = (String) request.getAttribute("end");
ArrayList<CommonCode> customers = (ArrayList<CommonCode>) request.getAttribute("customers");
ArrayList<CommonCode> projectTypes = (ArrayList<CommonCode>) request.getAttribute("projectTypes");
ArrayList<CommonCode> maks = (ArrayList<CommonCode>) request.getAttribute("maks");
ArrayList<CommonCode> details = (ArrayList<CommonCode>) request.getAttribute("details");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/jsp/common/layouts/include_css.jsp"%>
<%@include file="/jsp/common/layouts/include_script.jsp"%>
<!-- auigrid -->
<%@include file="/jsp/include/auigrid.jsp"%>
</head>
<body>
	<input type="hidden" name="sessionid" id="sessionid">
	<input type="hidden" name="curPage" id="curPage">
	<table class="search_table">
		<tr>
			<th>KEK 작번</th>
			<td>
				<input type="text" name="kekNumber" class="AXInput wid200">
			</td>
			<th>발행일</th>
			<td>
				<input type="text" name="predate" id="predate" class="AXInput" value="<%=before%>">
				~
				<input type="text" name="postdate" id="postdate" class="AXInput twinDatePicker" data-start="predate" value="<%=end%>">
				<i title="삭제" class="axi axi-ion-close-circled delete-calendar" data-start="predate" data-end="postdate"></i>
			</td>
			<th>KE 작번</th>
			<td>
				<input type="text" name="keNumber" class="AXInput wid200">
			</td>
			<th>USER ID</th>
			<td>
				<input type="text" name="userId" id="userId" class="AXInput wid200">
			</td>
		</tr>
		<tr>
			<th>작번상태</th>
			<td>
				<select name="kekState" id="kekState" class="AXSelect wid200">
					<option value="">선택</option>
					<option value="준비">준비</option>
					<option value="설계중">설계중</option>
					<option value="설계완료">설계완료</option>
					<option value="작업완료">작업완료</option>
					<option value="중단됨">중단됨</option>
					<option value="취소">취소</option>
				</select>
			</td>
			<th>모델</th>
			<td>
				<input type="text" name="model" class="AXInput wid200">
			</td>
			<th>거래처</th>
			<td>
				<select name="customer" id="customer" class="AXSelect wid200">
					<option value="">선택</option>
					<%
					for (CommonCode customer : customers) {
					%>
					<option value="<%=customer.getPersistInfo().getObjectIdentifier().getStringValue()%>"><%=customer.getName()%></option>
					<%
					}
					%>
				</select>
			</td>
			<th>설치장소</th>
			<td>
				<select name="install" id="install" class="AXSelect wid209">
					<option value="">선택</option>
				</select>
			</td>
		</tr>
		<tr>
			<th>작번유형</th>
			<td>
				<select name="projectType" id="projectType" class="AXSelect wid200">
					<option value="">선택</option>
					<%
					for (CommonCode projectType : projectTypes) {
					%>
					<option value="<%=projectType.getPersistInfo().getObjectIdentifier().getStringValue()%>"><%=projectType.getName()%></option>
					<%
					}
					%>
				</select>
			</td>
			<th>기계 담당자</th>
			<td>
				<input type="text" name="machine" id="machine" class="AXInput wid200" data-dbl="true" data-dept="기계설계" data-resign="resign">
				<input type="hidden" name="machineOid" id="machineOid">
				<i title="삭제" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="machine"></i>
			</td>
			<th>전기 담당자</th>
			<td>
				<input type="text" name="elec" id="elec" class="AXInput wid192" data-dbl="true" data-dept="전기설계" data-resign="resign">
				<input type="hidden" name="elecOid" id="elecOid">
				<i title="삭제" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="elec"></i>
			</td>
			<th>SW 담당자</th>
			<td>
				<input type="text" name="soft" id="soft" class="AXInput wid200" data-dbl="true" data-dept="SW설계" data-resign="resign">
				<input type="hidden" name="softOid" id="softOid">
				<i title="삭제" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="soft"></i>
			</td>
		</tr>
		<tr>
			<th>막종</th>
			<td>
				<select name="mak" id="mak" class="AXSelect wid200">
					<option value="">선택</option>
					<%
					for (CommonCode mak : maks) {
					%>
					<option value="<%=mak.getPersistInfo().getObjectIdentifier().getStringValue()%>"><%=mak.getName()%></option>
					<%
					}
					%>
				</select>
			</td>
			<th>막종상세</th>
			<td>
				<select name="detail" id="detail" class="AXSelect wid200">
					<option value="">선택</option>
				</select>
			</td>
			<th>작업내용</th>
			<td colspan="3">
				<input type="text" name="description" class="AXInput wid500">
			</td>
		</tr>
	</table>
	<!-- button table -->
	<table class="btn_table">
		<tr>
			<td class="left">
				<input type="button" value="등록" class="blueBtn" id="createBtn" title="등록">
			</td>
			<td class="right">
				<%
				if (isAdmin) {
				%>
				<input type="button" value="완료" class="redBtn" id="completeStep" title="완료">
				<input type="button" value="삭제" class="redBtn" id="deleteProjectBtn" title="삭제">
				<%
				}
				%>
				<input type="button" value="조회" class="blueBtn" id="searchBtn" title="조회">
				<input type="button" value="초기화" class="" id="initGrid" title="초기화">
			</td>
		</tr>
	</table>
	<div id="grid_wrap" style="height: 625px; border-top: 1px solid #3180c3;"></div>
</body>
<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "state",
		headerText : "진행상태",
		dataType : "string",
		width : 80,
		renderer : {
			type : "TemplateRenderer",
		},
	}, {
		dataField : "cip",
		headerText : "CIP",
		dataType : "string",
		width : 60,
		renderer : {
			type : "IconRenderer",
			iconWidth : 16, // icon 사이즈, 지정하지 않으면 rowHeight에 맞게 기본값 적용됨
			iconHeight : 16,
			iconTableRef : { // icon 값 참조할 테이블 레퍼런스
				"default" : "/Windchill/jsp/images/search.gif" // default
			},
			onClick : function(event) {
				let item = event.item;
				console.log(item);
				let detail_oid = item.detail_oid;
				let customer_oid = item.customer_oid;
				let install_oid = item.install_oid;
				let url = getCallUrl("/cip/view?detail_oid=" + detail_oid + "&customer_oid=" + customer_oid + "&install_oid=" + install_oid);
				popup(url);
			}
		}
	}, {
		dataField : "projectType_name",
		headerText : "작번유형",
		dataType : "string",
		width : 80
	}, {
		dataField : "customer_name",
		headerText : "거래처",
		dataType : "string",
		width : 100
	}, {
		dataField : "install_name",
		headerText : "설치장소",
		dataType : "string",
		width : 100
	}, {
		dataField : "mak_name",
		headerText : "막종",
		dataType : "string",
		width : 100
	}, {
		dataField : "detail_name",
		headerText : "막종상세",
		dataType : "string",
		width : 100
	}, {
		dataField : "kekNumber",
		headerText : "KEK 작번",
		dataType : "string",
		width : 130
	}, {
		dataField : "keNumber",
		headerText : "KE 작번",
		dataType : "string",
		width : 130
	}, {
		dataField : "userId",
		headerText : "USER ID",
		dataType : "string",
		width : 100
	}, {
		dataField : "description",
		headerText : "작업 내용",
		dataType : "string",
		width : 450
	}, {
		dataField : "pdate",
		headerText : "발행일",
		dataType : "date",
		formatString : "yyyy-mm-dd",
		width : 100
	}, {
		dataField : "completeDate",
		headerText : "설계 완료일",
		dataType : "date",
		formatString : "yyyy-mm-dd",
		width : 100
	}, {
		dataField : "endDate",
		headerText : "요구 납기일",
		dataType : "date",
		formatString : "yyyy-mm-dd",
		width : 100
	}, {
		dataField : "model",
		headerText : "모델",
		dataType : "string",
		width : 130
	}, {
		dataField : "machine",
		headerText : "기계 담당자",
		dataType : "string",
		width : 100
	}, {
		dataField : "elec",
		headerText : "전기 담당자",
		dataType : "string",
		width : 100
	}, {
		dataField : "soft",
		headerText : "SW 담당자",
		dataType : "string",
		width : 100
	}, {
		dataField : "kekProgress",
		headerText : "진행율",
		dataType : "string",
		postfix : "%",
		width : 80,
		renderer : {
			type : "BarRenderer",
			min : 0,
			max : 100
		},
	}, {
		dataField : "kekState",
		headerText : "작번상태",
		dataType : "string",
		width : 80
	}, {
		dataField : "oid",
		headerText : "oid",
		dataType : "string",
		visible : false
	} ]

	function createAUIGrid(columnLayout) {
		const props = {
			rowIdField : "rowId",
			headerHeight : 30,
			rowHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showRowCheckColumn : true, // 체크 박스 출력
			fixedColumnCount : 9,
		};

		myGridID = AUIGrid.create("#grid_wrap", columns, props);
		loadGridData();
		// LazyLoading 바인딩
		AUIGrid.bind(myGridID, "vScrollChange", vScrollChangeHandler);
		AUIGrid.bind(myGridID, "cellClick", auiCellClickHandler);
	}

	function auiCellClickHandler(event) {
		let dataField = event.dataField;
		let oid = event.item.oid;
		if (dataField === "kekNumber" || dataField === "keNumber") {
			let url = getCallUrl("/project/view?oid=" + oid);
			popup(url);
		}
	}

	function loadGridData() {
		let params = new Object();
		params = form(params, "search_table");
		let url = getCallUrl("/project/list");
		AUIGrid.showAjaxLoader(myGridID);
		parent.openLayer();
		call(url, params, function(data) {
			AUIGrid.removeAjaxLoader(myGridID);
			$("input[name=sessionid]").val(data.sessionid);
			$("input[name=curPage]").val(data.curPage);
			AUIGrid.setGridData(myGridID, data.list);
			parent.closeLayer();
		})
	}

	let last = false;
	function vScrollChangeHandler(event) {
		if (event.position == event.maxPosition) {
			if (!last) {
				requestAdditionalData();
			}
		}
	}

	function requestAdditionalData() {
		let params = new Object();
		let curPage = $("input[name=curPage]").val();
		params.sessionid = $("input[name=sessionid]").val();
		params.start = (curPage * 30);
		params.end = (curPage * 30) + 30;
		let url = getCallUrl("/appendData");
		AUIGrid.showAjaxLoader(myGridID);
		parent.openLayer();
		call(url, params, function(data) {
			if (data.list.length == 0) {
				last = true;
				alert("마지막 데이터 입니다.");
				AUIGrid.removeAjaxLoader(myGridID);
			} else {
				AUIGrid.appendData(myGridID, data.list);
				AUIGrid.removeAjaxLoader(myGridID);
				$("input[name=curPage]").val(parseInt(curPage) + 1);
			}
			parent.closeLayer();
		})
	}

	$(function() {

		createAUIGrid(columns);

		$("#mak").bindSelect({
			onchange : function() {
				let oid = this.optionValue;
				$("#detail").bindSelect({
					ajaxUrl : getCallUrl("/commonCode/getChildrensByOid?parentOid=" + oid),
					reserveKeys : {
						options : "list",
						optionValue : "value",
						optionText : "name"
					},
					setValue : this.optionValue,
					alwaysOnChange : true,
				})
			}
		})

		$("#customer").bindSelect({
			onchange : function() {
				let oid = this.optionValue;
				$("#install").bindSelect({
					ajaxUrl : getCallUrl("/commonCode/getChildrensByOid?parentOid=" + oid),
					reserveKeys : {
						options : "list",
						optionValue : "value",
						optionText : "name"
					},
					setValue : this.optionValue,
					alwaysOnChange : true,
				})
			}
		})

		// select box bind
		selectBox("detail"); // 기본 바인딩만..
		selectBox("install");
		selectBox("projectType");
		selectBox("kekState");

		$("#searchBtn").click(function() {
			loadGridData();
		})

		// 등록페이지
		$("#createBtn").click(function() {
			let url = getCallUrl("/project/create");
			popup(url, 1200, 540);
		})

		rangeDate("postdate", "predate");

	}).keypress(function(e) {
		let keyCode = e.keyCode;
		if (keyCode == 13) {
			loadGridData();
		}
	})

	$(window).resize(function() {
		AUIGrid.resize(myGridID);
	})
</script>
</html>