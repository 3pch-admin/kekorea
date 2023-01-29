<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.admin.commonCode.CommonCodeType"%>
<%@page import="org.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<CommonCode> installs = (ArrayList<CommonCode>) request.getAttribute("installs");
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
<!-- highchart -->
<%@include file="/jsp/include/highchart.jsp"%>
</head>
<body>
	<input type="hidden" name="sessionid" id="sessionid">
	<input type="hidden" name="curPage" id="curPage">
	<div id="container"></div>
	<div id="grid_wrap" style="height: 383px; border-top: 1px solid #3180c3;"></div>
</body>
<script type="text/javascript">
	Highcharts.chart('container', {
		chart : {
			type : 'column'
		},
		title : {
			text : '고객별 작번 현황'
		},
		xAxis : {
			categories : [ 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec' ],
			crosshair : true
		},
		yAxis : {
			min : 0,
			title : {
				text : '한국생산(台)'
			}
		},
		tooltip : {
			headerFormat : '<span style="font-size:10px">{point.key}</span><table>',
			pointFormat : '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' + '<td style="padding:0"><b>{point.y:.1f} mm</b></td></tr>',
			footerFormat : '</table>',
			shared : true,
			useHTML : true
		},
		plotOptions : {
			column : {
				pointPadding : 0.2,
				borderWidth : 0
			}
		},
		series : [ {
			name : 'Tokyo',
			data : [ 49.9, 71.5, 106.4, 129.2, 144.0, 176.0, 135.6, 148.5, 216.4, 194.1, 95.6, 54.4 ]

		}, {
			name : 'New York',
			data : [ 83.6, 78.8, 98.5, 93.4, 106.0, 84.5, 105.0, 104.3, 91.2, 83.5, 106.6, 92.3 ]

		}, {
			name : 'London',
			data : [ 48.9, 38.8, 39.3, 41.4, 47.0, 48.3, 59.0, 59.6, 52.4, 65.2, 59.3, 51.2 ]

		}, {
			name : 'Berlin',
			data : [ 42.4, 33.2, 34.5, 39.7, 52.6, 75.5, 57.4, 60.4, 47.6, 39.1, 46.8, 51.1 ]

		} ]
	});
</script>
<script type="text/javascript">
	let myGridID;

	const columns = [ {
		dataField : "state",
		headerText : "진행상태",
		dataType : "string",
		width : 100
	}, {
		dataField : "pType",
		headerText : "작번유형",
		dataType : "string",
		width : 100
	}, {
		dataField : "customer",
		headerText : "거래처",
		dataType : "string",
		width : 100
	}, {
		dataField : "ins_location",
		headerText : "설치장소",
		dataType : "string",
		width : 130
	}, {
		dataField : "mak",
		headerText : "막종",
		dataType : "string",
		width : 130
	}, {
		dataField : "kek_number",
		headerText : "KEK 작번",
		dataType : "string",
		width : 130
	}, {
		dataField : "ke_number",
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
		dataField : "pDate",
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
		width : 80
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
			rowIdField : "oid",
			headerHeight : 30,
			rowHeight : 30,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showRowCheckColumn : true, // 체크 박스 출력
			fixedColumnCount : 7,

			// 컨텍스트 메뉴 사용
			useContextMenu : true,

			// 컨텍스트 메뉴 아이템들
			contextMenuItems : [ {
				label : "BOM 비교",
				callback : contextHandler
			}, {
				label : "CONFIG 비교",
				callback : contextHandler
			}, {
				label : "도면일람표 비교",
				callback : contextHandler
			} ],
		};

		myGridID = AUIGrid.create("#grid_wrap", columns, props);
		loadGridData();
		// LazyLoading 바인딩
		AUIGrid.bind(myGridID, "vScrollChange", vScrollChangeHandler);
	}

	function contextHandler(event) {

		let checkedItems = AUIGrid.getCheckedRowItems(myGridID);
		if (checkedItems.length < 2) {
			alert("2개 이상의 작번을 선택하세요.");
			return false;
		}

		switch (event.contextIndex) {
		case 0:
			alert(event.value + ", rowIndex : " + event.rowIndex + ", columnIndex : " + event.columnIndex);
			break;

		case 2:
			// 내보내기 실행
			AUIGrid.exportToXlsx(event.pid);
			break;

		case 3:
			window.open("https://www.google.com", "_blank");
			break;
		}
	};

	function loadGridData() {
		let params = new Object();
		let url = getCallUrl("/commonCode/list");
		AUIGrid.showAjaxLoader(myGridID);
		call(url, params, function(data) {
			AUIGrid.removeAjaxLoader(myGridID);
			$("input[name=sessionid]").val(data.sessionid);
			$("input[name=curPage]").val(data.curPage);
			AUIGrid.setGridData(myGridID, data.list);
			parent.close();
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

		// 그리드 행 추가
		$("#addRowBtn").click(function() {
			let item = new Object();
			item.createDate = new Date();
			AUIGrid.addRow(myGridID, item, "last");
		})

		$("#saveBtn").click(function() {
			let addRows = AUIGrid.getAddedRowItems(myGridID);
			let removeRows = AUIGrid.getRemovedItems(myGridID);
			let editRows = AUIGrid.getEditedRowItems(myGridID);
			let params = new Object();
			let url = getCallUrl("/commonCode/create");
			params.addRows = addRows;
			params.removeRows = removeRows;
			params.editRows = editRows;
			console.log(params);
			call(url, params, function(data) {

			}, "POST");
		})

		// 그리드 행 삭제
		$("#deleteRowBtn").click(function() {
			let checkedItems = AUIGrid.getCheckedRowItems(myGridID);
			for (let i = checkedItems.length - 1; i >= 0; i--) {
				let rowIndex = checkedItems[i].rowIndex;
				AUIGrid.removeRow(myGridID, rowIndex);
			}
		})

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