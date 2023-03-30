<%@page import="java.util.Map"%>
<%@page import="java.util.Enumeration"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.korea.service.KoreaHelper"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
CommonCode makCode = (CommonCode) request.getAttribute("makCode");
ArrayList<String> data = (ArrayList<String>) request.getAttribute("data");
Map<String, ArrayList<String>> drillDown = (Map<String, ArrayList<String>>) request.getAttribute("drillDown");
System.out.println(drillDown);
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/include/highchart.jsp"%>
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js?v=1"></script>
</head>
<body>
	<div id="container" style="height: 380px;"></div>

	<script type="text/javascript">
Highcharts.chart('container', {
    chart: {
        type: 'column'
    },
    title: {
        align: 'left',
        text: '<%=makCode.getName()%> 막종 작번 개수'
    },
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
            	enabled :true,
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
            	<%
            	for (String dataValue : data) {
            		String name = dataValue.split("&")[0];
            		String key = dataValue.split("&")[1];
            		String value = dataValue.split("&")[2];
            	%>
                {
                    name: '<%=name%>',
                    y: <%=value%>,
                    drilldown: '<%=key%>'
                },
                <%}%>
            ],
            dataLabels: {
                enabled: function() {
                  return this.y !== 0;
                }
              },
            showInLegend: false
        }
    ],
    drilldown: {
        breadcrumbs: {
            position: {
                align: 'right'
            }
        },
        series: [
        	<%
        	for (String dataValue : data) {
        		String name = dataValue.split("&")[0];
        		String key = dataValue.split("&")[1];
        		ArrayList<String> drill = drillDown.get(key);
        	%>
            {
                name: '<%=name%>',
                id: '<%=key%>',
                data: [
                <%
                	for (String drillValue : drill) {
                		String drillName = drillValue.split("&")[0];
                		String value = drillValue.split("&")[2];
                %>
                	['<%=drillName%>', <%=value%>],
                <%
               	 }
                %>
                ]
            },
            <%}%>
        ]
    }
});
</script>
</body>
</html>