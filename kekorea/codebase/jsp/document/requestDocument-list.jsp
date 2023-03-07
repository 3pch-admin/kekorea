<%@page import="e3ps.doc.service.DocumentHelper"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
boolean isAdmin = CommonUtils.isAdmin();
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
	<!-- list header title -->
	<div class="header_title">
		<i class="axi axi-subtitles"></i>
		<span>의뢰서 조회</span>
		<!-- info search -->
		<jsp:include page="/jsp/common/search_info.jsp"></jsp:include>
	</div>
	<!-- search table -->
	<table class="search_table">
		<tr>
			<th>KEK 작번</th>
			<td>
				<input type="text" name="kekNumber" class="AXInput wid200">
			</td>
			<th>KE 작번</th>
			<td>
				<input type="text" name="keNumber" class="AXInput wid200">
			</td>
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
		</tr>
		<tr>
			<th>거래처</th>
			<td>
				<input type="text" name="customer" class="AXInput wid200">
			</td>
			<th>USER ID</th>
			<td>
				<input type="text" name="userId" class="AXInput wid200">
			</td>
			<th>막종</th>
			<td>
				<input type="text" name="mak" class="AXInput wid200">
			</td>
			<th>작업 내용</th>
			<td>
				<input type="text" name="pdescription" class="AXInput wid200">
			</td>
		</tr>
		<tr>
			<th>설치장소</th>
			<td colspan="3">
				<input type="text" name="ins_location" class="AXInput wid200">
			</td>
			<th>작성자</th>
			<td>
				<input type="text" name="creators" id="creators" class="AXInput wid200" data-dbl="true">
				<i title="삭제" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="creators"></i>
			</td>
			<th>작성일</th>
			<td>
				<input type="text" name="predate" id="predate" class="AXInput">
				~
				<input type="text" name="postdate" id="postdate" class="AXInput twinDatePicker" data-start="predate">
				<i title="삭제" class="axi axi-ion-close-circled delete-calendar" data-start="predate" data-end="postdate"></i>
			</td>
		</tr>
		<tr class="detailEpm">
			<th>수정자</th>
			<td>
				<input type="text" name="modifier" id="modifier" class="AXInput wid200" data-dbl="true">
				<i title="삭제" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="modifier"></i>
			</td>
			<th>수정일</th>
			<td>
				<input type="text" name="predate_m" id="predate_m" class="AXInput">
				~
				<input type="text" name="pstdate_m" id="postdate_m" class="AXInput twinDatePicker_m" data-start="predate_m">
				<i title="삭제" class="axi axi-ion-close-circled delete-calendar" data-start="predate_m" data-end="postdate_m"></i>
			</td>
			<th>버전</th>
			<td colspan="3">
				<label title="최신버전">
					<input type="radio" name="latest" value="true" checked="checked">
					<span class="latest">최신버전</span>
				</label>
				<label title="모든버전">
					<input type="radio" name="latest" value="false">
					<span class="latest">모든버전</span>
				</label>
			</td>
		</tr>
		<%-- <tr>
						<th>상태</th>
						<td>
							<select name="statesDoc" id="statesDoc" class="AXSelect wid200">
								<option value="">선택</option>
								<%
									for(StateKeys state : states) {
								%>
								<option value="<%=state.name() %>"><%=state.getDisplay() %></option>
								<%
									}
								%>
							</select>
						</td>
						
						<th>버전</th>
						<td>
							<label title="최신버전"> 
								<input type="radio" name="latest" value="true" checked="checked"> 
								<span class="latest">최신버전</span>
							</label> 
							<label title="모든버전"> 
								<input type="radio" name="latest" value="false"> 
								<span class="latest">모든버전</span>
							</label>
						</td>
					</tr> --%>
	</table>

	<!-- button table -->
	<table class="btn_table">
		<tr>
			<!-- start sub table -->
			<td class="left">
				<input type="button" value="등록" class="blueBtn" id="createBtn" title="등록">
			</td>
			<td class="right">
				<%
				if (isAdmin) {
				%>
				<input type="button" value="삭제" class="redBtn" id="deleteListDocBtn" title="삭제">
				<%
				}
				%>
				<input type="button" value="상세조회" class="orangeBtn" id="detailEpmBtn" title="상세조회">
				<input type="button" value="조회" class="blueBtn" id="searchBtn" title="조회">
				<input type="button" value="초기화" class="" id="initGrid" title="초기화" data-location="<%=root%>">
			</td>
		</tr>
	</table>
	<!-- end button table -->
	<div id="grid_wrap" style="height: 740px; border-top: 1px solid #3180c3;"></div>
</body>
<script type="text/javascript">
	let myGridID;
	const columns = [ {

		dataField : "pjtType",
		headerText : "작번 유형",
		width : 100

	}, {

		dataField : "name",
		headerText : "의뢰서 제목",
		width : 370

	}, {

		dataField : "customer",
		headerText : "거래처",
		width : 80

	}, {

		dataField : "ins_location",
		headerText : "설치장소",
		width : 100

	}, {

		dataField : "mak",
		headerText : "막종",
		width : 100

	}, {

		dataField : "kekNumber",
		headerText : "KEK 작번",
		width : 100

	}, {

		dataField : "keNumber",
		headerText : "KE 작번",
		width : 100

	}, {

		dataField : "user_id",
		headerText : "USER ID",
		width : 100

	}, {

		dataField : "pdescription",
		headerText : "작업내용",
		width : 470

	}, {

		dataField : "ingPoint",
		headerText : "검토자",
		width : 100

	}, {

		dataField : "version",
		headerText : "버전",
		width : 80

	}, {

		dataField : "state",
		headerText : "상태",
		width : 80

	}, {

		dataField : "model",
		headerText : "모델",
		width : 110

	}, {

		dataField : "pDate",
		headerText : "발행일",
		width : 110

	}, {

		dataField : "creator",
		headerText : "작성자",
		width : 80

	}, {

		dataField : "createDate",
		headerText : "작성일",
		width : 110

	}, {

		dataField : "modifier",
		headerText : "수정자",
		width : 80

	}, {

		dataField : "modifyDate",
		headerText : "수정일",
		width : 110

	}, {

		dataField : "oid",
		headerText : "oid",
		visible : false

	} ];

	function createAUIGrid(columnLayout) {
		const props = {
			rowIdField : "oid",
			headerHeight : 30,
			rowHeight : 30,
			showRowCheckColumn : true,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			fillColumnSizeMode : true, // 화면 꽉채우기
		};
		myGridID = AUIGrid.create("#grid_wrap", columns, props);
		loadGridData();
		// LazyLoading 바인딩
		AUIGrid.bind(myGridID, "vScrollChange", vScrollChangeHandler);

	}

	function loadGridData() {
		let params = new Object();
		let url = getCallUrl("/document/listRequestDocument");
		AUIGrid.showAjaxLoader(myGridID);
		call(url, params, function(data) {
			AUIGrid.removeAjaxLoader(myGridID);
			$("input[name=sessionid]").val(data.sessionid);
			$("input[name=curPage]").val(data.curPage);
			AUIGrid.setGridData(myGridID, data.list);
			parent.close();
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
		let url = getCallUrl("/aui/appendData");
		AUIGrid.showAjaxLoader(myGridID);
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
		})
	}

	$(function() {
		createAUIGrid(columns);

		$("#searchBtn").click(function() {
			loadGridData();
		})

		$("#createBtn").click(function() {
			let url = getCallUrl("/document/createRequestDocument");
			popup(url, 1400, 570);	
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