<%@page import="e3ps.admin.commonCode.service.CommonCodeHelper"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Enumeration"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.korea.service.KoreaHelper"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<CommonCode> customers = (ArrayList<CommonCode>) request.getAttribute("customers");
Map<String, ArrayList<Integer>> data = (Map<String, ArrayList<Integer>>) request.getAttribute("data");
ArrayList<String> list = (ArrayList<String>) request.getAttribute("list");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/include/highchart.jsp"%>
</head>
<body>
	<div id="container" style="height: 340px;"></div>
	<script type="text/javascript">
		Highcharts.chart('container', {
			chart : {
				type : 'column'
			},
			title : {
				text : '막종별 차트'
			},
		   credits: {
			   enabled: false
		   },			
			xAxis : {
				categories : [ 
					<%for (CommonCode customer : customers) {%>
					'<%=customer.getName()%>',
					<%}%>
				],
				crosshair : true,
			},
			yAxis : {
				min : 0,
				title : {
					text : '개'
				},
			},
			tooltip : {
				headerFormat : '<span style="font-size:10px">{point.key}</span><table>',
				pointFormat : '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' + '<td style="padding:0"><b>{point.y} 개</b></td></tr>',
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
			series : [ 
			<%for (String mak : list) {
	CommonCode makCode = CommonCodeHelper.manager.getCommonCode(mak, "MAK");
	ArrayList<Integer> yAxis = data.get(mak);%>
			{
				name : '<%=makCode.getName()%>',
				data : 
					[ 
					<%for (Integer y : yAxis) {%>
					<%=y%>,
					<%}%>
					],
				drilldown: '<%=makCode.getCode()%>',
			},
			<%}%>
			 ],
		    drilldown: {
		        breadcrumbs: {
		            position: {
		                align: 'right'
		            }
		        },
		        series: [
		        <%for (String mak : list) {
		        	CommonCode makCode = CommonCodeHelper.manager.getCommonCode(mak, "MAK");
		        %>
		        	{
		                name: '<%=makCode.getName()%>',
		                id: '<%=makCode.getCode()%>',
		                data: [
		                    [
		                        'v65.0',
		                        0.1
		                    ]
	                    ]
		        	},
		        <%}%>
		        ]
		    }
		});
	</script>
</body>
</html>