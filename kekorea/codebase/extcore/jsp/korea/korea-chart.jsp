<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.korea.service.KoreaHelper"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	ArrayList<CommonCode> maks = (ArrayList<CommonCode>) request.getAttribute("maks");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<!-- AUIGrid -->
<%@include file="/extcore/include/highchart.jsp"%>
</head>
<body>
<div id="container" style="height: 430px;"></div>
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
</body>
</html>