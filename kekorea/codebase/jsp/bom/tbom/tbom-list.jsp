<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
			<th>수배표 제목</th>
			<td>
				<input type="text" name="name" class="AXInput wid200">
			</td>
			<th>상태</th>
			<td>
				<select name="statesDoc" id="statesDoc" class="AXSelect wid200">
					<option value="">선택</option>
					<%-- 					<% --%>
					<!--  						for(StateKeys state : states) { -->
					<%-- 					%> --%>
					<%-- 					<option value="<%=state.name() %>"><%=state.getDisplay() %></option> --%>
					<%-- 					<% --%>
					<!--  						} -->
					<%-- 					%> --%>
				</select>
			</td>
			<th>KEK 작번</th>
			<td>
				<input type="text" name="kekNumber" class="AXInput wid200">
			</td>
			<th>KE 작번</th>
			<td>
				<input type="text" name="keNumber" class="AXInput wid200">
			</td>
		</tr>
		<tr>
			<th>설명</th>
			<td>
				<input type="text" name="description" class="AXInput wid200">
			</td>
			<th>설계 구분</th>
			<td>
				<select name="engType" id="engType" class="AXSelect wid100">
					<option value="">선택</option>
					<option value="개조">개조</option>
					<option value="견적">견적</option>
					<option value="양산">양산</option>
					<option value="연구개발">연구개발</option>
					<option value="이설">이설</option>
					<option value="판매">판매</option>
					<option value="평가용">평가용</option>
				</select>
			</td>
			<th>막종</th>
			<td>
				<input type="text" name="mak" class="AXInput wid200">
			</td>
			<th>작업 내용</th>
			<td>
				<input type="text" name="pDescription" class="AXInput wid200">
			</td>
		</tr>
		<tr class="detailEpm">
			<th>작성자</th>
			<td>
				<input type="text" name="creators" id="creators" class="AXInput wid200" data-dbl="true">
				<input type="hidden" name="creatorsOid" id="creatorsOid" class="AXInput wid200" data-dbl="true">
				<i title="삭제" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="creators"></i>
			</td>
			<th>작성일</th>
			<td>
				<input type="text" name="predate" id="predate" class="AXInput">
				~
				<input type="text" name="postdate" id="postdate" class="AXInput twinDatePicker" data-start="predate">
				<i title="삭제" class="axi axi-ion-close-circled delete-calendar" data-start="predate" data-end="postdate"></i>
			</td>
			<th>수정자</th>
			<td>
				<input type="text" name="modifier" id="modifier" class="AXInput wid200" data-dbl="true">
				<i title="삭제" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="creators"></i>
			</td>
			<th>수정일</th>
			<td>
				<input type="text" name="predate_m" id="predate_m" class="AXInput">
				~
				<input type="text" name="pstdate_m" id="postdate_m" class="AXInput twinDatePicker_m" data-start="predate_m">
				<i title="삭제" class="axi axi-ion-close-circled delete-calendar" data-start="predate_m" data-end="postdate_m"></i>
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
				<input type="button" value="삭제" class="redBtn" id="deletePartListBtn" title="삭제">
				<input type="button" value="상세조회" class="orangeBtn" id="detailEpmBtn" title="상세조회">
				<input type="button" value="조회" class="blueBtn" id="searchBtn" title="조회">
				<%-- 				<input type="button" value="초기화" class="" id="initGrid" title="초기화" data-location="<%=root %>"> --%>
			</td>
		</tr>
	</table>
	<div id="grid_wrap" style="height: 650px; border-top: 1px solid #3180c3;"></div>
</body>
<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "projectType_name",
		headerText : "작번유형",
		dataType : "string",
		width : 80
	}, {
		dataField : "info",
		headerText : "",
		dataType : "string",
		width : 40,
		renderer : {
			type : "IconRenderer",
			iconWidth : 16, // icon 사이즈, 지정하지 않으면 rowHeight에 맞게 기본값 적용됨
			iconHeight : 16,
			iconTableRef : { // icon 값 참조할 테이블 레퍼런스
				"default" : "/Windchill/jsp/images/details.gif" // default
			},
			onClick : function(event) {
				let oid = event.item.oid;
				let url = getCallUrl("/tbom/view?oid=" + oid);
				popup(url);
			}
		}
	}, {
		dataField : "name",
		headerText : "T-BOM 제목",
		dataType : "string",
		style : "left",
		width : 300
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
		width : 100
	}, {
		dataField : "keNumber",
		headerText : "KE 작번",
		dataType : "string",
		width : 100
	}, {
		dataField : "userId",
		headerText : "USER ID",
		dataType : "string",
		width : 100
	}, {
		dataField : "description",
		headerText : "작업내용",
		dataType : "string",
		width : 300,
		style : "left indent10"
	}, {
		dataField : "customer_name",
		headerText : "거래처",
		dataType : "string",
		width : 100
	}, {
		dataField : "install_name",
		headerText : "설치 장소",
		dataType : "string",
		width : 100
	}, {
		dataField : "pdate",
		headerText : "발행일",
		dataType : "string",
		width : 100
	}, {
		dataField : "model",
		headerText : "모델",
		dataType : "string",
		width : 100
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
		dataField : "state",
		headerText : "상태",
		dataType : "string",
		width : 100
	} ]

	function createAUIGrid(columnLayout) {
		const props = {
			rowIdField : "oid",
			headerHeight : 30,
			rowHeight : 30,
			showRowCheckColumn : true,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			// 			fillColumnSizeMode : true, // 화면 꽉채우기
			noDataMessage : "검색 결과가 없습니다.",
		};
		myGridID = AUIGrid.create("#grid_wrap", columns, props);
		loadGridData();
		// LazyLoading 바인딩
		AUIGrid.bind(myGridID, "vScrollChange", vScrollChangeHandler);

	}

	function loadGridData() {
		let params = new Object();
		let url = getCallUrl("/tbom/list");
		AUIGrid.showAjaxLoader(myGridID);
		parent.openLayer();
		call(url, params, function(data) {
			AUIGrid.removeAjaxLoader(myGridID);
			$("input[name=sessionid]").val(data.sessionid);
			$("input[name=curPage]").val(data.curPage);
			AUIGrid.setGridData(myGridID, data.list);
			parent.closeLayer();
		});
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

		$("#searchBtn").click(function() {
			loadGridData();
		})

		$("#createBtn").click(function() {
			let url = getCallUrl("/tbom/create");
			popup(url);
		});

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