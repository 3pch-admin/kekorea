<%@page import="e3ps.bom.partlist.dto.PartListDTO"%>
<%@page import="org.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
JSONArray data = (JSONArray) request.getAttribute("data");
PartListDTO dto = (PartListDTO) request.getAttribute("dto");
%>
<!-- AUIGrid -->
<%@include file="/extcore/include/auigrid.jsp"%>
<!-- hidden -->
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<input type="hidden" name="compare" id="compare">
<!-- 검색 테이블 -->
<table class="search-table">
	<colgroup>
		<col width="130">
		<col width="800">
		<col width="130">
		<col width="800">
	</colgroup>
	<tr>
		<th>기준수배표</th>
		<td>
			<input type="text" class="AXInput width-500" readonly="readonly" value="<%=dto.getName()%>">
		</td>
		<th>비교수배표</th>
		<td>
			<input type="text" name="comp" id="comp" class="AXInput width-500" readonly="readonly" onclick="opener();">
		</td>
	</tr>
</table>

<table class="button-table">
	<tr>
		<td class="left">
			<input type="button" value="LOT 비교" title="LOT 비교" class="orange" onclick="_compare('lotNo');">
			<input type="button" value="품번 비교" title="품번 비교" class="blue" onclick="_compare('partNo');">
			<input type="button" value="전체 비교" title="전체 비교" class="red" onclick="_compare('');">
		</td>
		<td class="right">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>

<!-- 그리드 리스트 -->
<table class="tb-none">
	<colgroup>
		<col width="50%">
		<col width="50%">
	</colgroup>
	<tr>
		<td>
			<div id="grid_wrap" style="height: 665px; border-top: 1px solid #3180c3;"></div>
		</td>
		<td>
			<div id="_grid_wrap" style="height: 665px; border-top: 1px solid #3180c3;"></div>
		</td>
</table>
<script type="text/javascript">
	let myGridID;
	let _myGridID;
	const data =
<%=data%>
	const columns = [ {
		dataField : "lotNo",
		headerText : "LOT_NO",
		dataType : "string",
		width : 80,
	}, {
		dataField : "unitName",
		headerText : "UNIT NAME",
		dataType : "string",
		width : 120
	}, {
		dataField : "partNo",
		headerText : "부품번호",
		dataType : "string",
		style : "underline",
		width : 130,
	}, {
		dataField : "partName",
		headerText : "부품명",
		dataType : "string",
		width : 200,
	}, {
		dataField : "standard",
		headerText : "규격",
		dataType : "string",
		width : 250,
	}, {
		dataField : "maker",
		headerText : "MAKER",
		dataType : "string",
		width : 130,
	}, {
		dataField : "customer",
		headerText : "거래처",
		dataType : "string",
		width : 130,
	}, {
		dataField : "quantity",
		headerText : "수량",
		dataType : "numeric",
		width : 60,
	}, {
		dataField : "unit",
		headerText : "단위",
		dataType : "string",
		width : 80,
	}, {
		dataField : "price",
		headerText : "단가",
		dataType : "numeric",
		width : 120,
	}, {
		dataField : "currency",
		headerText : "화폐",
		dataType : "string",
		width : 60,
	}, {
		dataField : "won",
		headerText : "원화금액",
		dataType : "numeric",
		width : 120,
	}, {
		dataField : "partListDate",
		headerText : "수배일자",
		dataType : "date",
		formatString : "yyyy-mm-dd",
		width : 100,
	}, {
		dataField : "exchangeRate",
		headerText : "환율",
		dataType : "numeric",
		width : 80,
		formatString : "#,##0.0000"
	}, {
		dataField : "referDrawing",
		headerText : "참고도면",
		dataType : "string",
		width : 120,
	}, {
		dataField : "classification",
		headerText : "조달구분",
		dataType : "string",
		width : 120,
	}, {
		dataField : "note",
		headerText : "비고",
		dataType : "string",
		width : 250,
	} ];

	function createAUIGrid(columnLayout) {
		const props = {
			// 그리드 공통속성 시작
			headerHeight : 30, // 헤더높이
			rowHeight : 30, // 행 높이
			showRowNumColumn : true, // 번호 행 출력 여부
			rowNumHeaderText : "번호", // 번호 행 텍스트 설정
			// 그리드 공통속성 끝
			rowStyleFunction : function(rowIndex, item) {
				console.log(rowIndex);
				console.log(item);
			}
		};
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		_myGridID = AUIGrid.create("#_grid_wrap", columnLayout, props);

		// H스크롤 체인지 핸들러.
		AUIGrid.bind(myGridID, "hScrollChange", function(event) {
			AUIGrid.setHScrollPositionByPx(_myGridID, event.position); // 수평 스크롤 이동 시킴..
		});

		// V스크롤 체인지 핸들러.
		AUIGrid.bind(myGridID, "vScrollChange", function(event) {
			AUIGrid.setRowPosition(_myGridID, event.position); // 수평 스크롤 이동 시킴..
		});

		// H스크롤 체인지 핸들러.
		AUIGrid.bind(_myGridID, "hScrollChange", function(event) {
			AUIGrid.setHScrollPositionByPx(myGridID, event.position); // 수평 스크롤 이동 시킴...
		});

		// V스크롤 체인지 핸들러.
		AUIGrid.bind(_myGridID, "vScrollChange", function(event) {
			AUIGrid.setRowPosition(myGridID, event.position); // 수평 스크롤 이동 시킴..
		});

	}
	// jquery 삭제를 해가는 쪽으로 한다..
	document.addEventListener("DOMContentLoaded", function() {
		// DOM이 로드된 후 실행할 코드 작성
		createAUIGrid(columns);
		AUIGrid.setGridData(myGridID, data);
		AUIGrid.resize(myGridID);
		AUIGrid.resize(_myGridID);
		AUIGrid.setGridData(myGridID,
<%=data%>
	);
	});

	function opener() {
		let url = getCallUrl("/partlist/popup?method=attach&multi=false");
		popup(url);
	}

	function attach(data) {
		const item = data[0].item;
		const oid = item.oid;
		document.getElementById("compare").value = oid;
		document.getElementById("comp").value = item.name;
	}

	// 비교
	function _compare(compareType) {
		const oid = document.getElementById("oid").value;
		const _oid = document.getElementById("compare").value;
		const url = getCallUrl("/partlist/compare");
		const params = new Object();
		params.oid = oid;
		params._oid = _oid;
		params.compareType = compareType;
		console.log(params);
		AUIGrid.clearGridData(myGridID);
		AUIGrid.clearGridData(_myGridID);
		call(url, params, function(data) {
			AUIGrid.setGridData(myGridID, data.dataList);
			AUIGrid.setGridData(_myGridID, data._dataList);
		})
	}

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID);
		AUIGrid.resize(_myGridID);
	});
</script>
