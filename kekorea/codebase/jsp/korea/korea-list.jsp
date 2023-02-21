<%@page import="e3ps.korea.service.KoreaHelper"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.admin.commonCode.CommonCodeType"%>
<%@page import="org.json.JSONArray"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<CommonCode> installs = (ArrayList<CommonCode>) request.getAttribute("installs");
ArrayList<CommonCode> maks = (ArrayList<CommonCode>) request.getAttribute("maks");
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
	<div id="container" style="height: 490px;"></div>
	<div id="grid_wrap" style="height: 330px; border-top: 1px solid #3180c3;"></div>
</body>
<script type="text/javascript">
//Create the chart
Highcharts.chart('container', {
    chart: {
        type: 'column'
    },
    title: {
        align: 'left',
        text: '막종별 작번 개수'
    },
//     subtitle: {
//         align: 'left',
//         text: 'Click the columns to view versions. Source: <a href="http://statcounter.com" target="_blank">statcounter.com</a>'
//     },
    accessibility: {
        announceNewData: {
            enabled: true
        }
    },
    xAxis: {
        type: 'category'
    },
    yAxis: {
        title: {
            text: '작번 개수'
        }
    },
    plotOptions: {
        series: {
            borderWidth: 0,
            dataLabels: {
                enabled: true,
                format: '{point.y}개'
            },
            pointWidth: 20,
        }
    },
	legend : {
		enabled : false
	},
    series: [
        {
        	name : '막종별 프로젝트',
            colorByPoint: true,
            events : {
            	cursor: 'pointer',
            	drillup : function(e) {
            		console.log(this);
            	}
            },
            data: [
            	<%for (CommonCode mak : maks) {%>
                {
                    name: '<%=mak.getName()%>',
                    y: <%=KoreaHelper.manager.yAxisValueForMak(mak)%>,
                    drilldown: '<%=mak.getCode()%>'
                },
                <%}%>
            ]
        }
    ],
    drilldown: {
        breadcrumbs: {
            position: {
                align: 'right'
            }
        },
        series: [
        	<%for (CommonCode mak : maks) {
						ArrayList<CommonCode> details = KoreaHelper.manager.drillDownList(mak);%>
            {
                name: '<%=mak.getName()%>',
                id: '<%=mak.getCode()%>',
                data: [
                <%for (CommonCode detail : details) {%>
                	['<%=detail.getName()%>', <%=KoreaHelper.manager.yAxisValueForDetail(detail)%>],
                <%}%>
                ]
            },
            <%}%>
        ]
    }
});
</script>
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
		dataField : "projectType_name",
		headerText : "작번유형",
		dataType : "string",
		width : 100
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
		width : 450,
		style :  "left indent10"
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
			fixedColumnCount : 8,
			// 컨텍스트 메뉴 사용
			useContextMenu : true,

			// 컨텍스트 메뉴 아이템들
			contextMenuItems : [ {
				label : "BOM 비교",
				callback : contextHandler
			}, {
				label : "E-BOM 비교",
				callback : contextHandler
			}, {
				label : "T-BOM 비교",
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
	
	function clicked(name) {
		let params = new Object();
		let url = getCallUrl("/project/list");
		params.mak = name;
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
	
	function loadGridData() {
		let params = new Object();
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
				parent.close();
			} else {
				AUIGrid.appendData(myGridID, data.list);
				AUIGrid.removeAjaxLoader(myGridID);
				$("input[name=curPage]").val(parseInt(curPage) + 1);
				parent.closeLayer();
			}
		})
	}

	$(function() {
		createAUIGrid(columns);

		$("#searchBtn").click(function() {
			loadGridData();
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