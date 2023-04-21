<%@page import="java.math.RoundingMode"%>
<%@page import="java.math.BigDecimal"%>
<%@page import="e3ps.project.dto.ProjectDTO"%>
<%@page import="e3ps.project.template.dto.TemplateDTO"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ProjectDTO dto = (ProjectDTO) request.getAttribute("dto");
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
WTUser sessionUser = (WTUser) request.getAttribute("sessionUser");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/extcore/include/css.jsp"%>
<%@include file="/extcore/include/script.jsp"%>
<%@include file="/extcore/include/auigrid.jsp"%>
<%@include file="/extcore/include/highchart.jsp"%>
<script type="text/javascript" src="/Windchill/extcore/js/auigrid.js?v=1010"></script>
</head>
<body style="margin: 0px 0px 0px 5px;">
	<form>
		<input type="hidden" name="isAdmin" id="isAdmin" value="<%=isAdmin%>">
		<input type="hidden" name="sessionName" id="sessionName" value="<%=sessionUser.getFullName()%>">
		<input type="hidden" name="sessionId" id="sessionId" value="<%=sessionUser.getName()%>">
		<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
		<div id="tabs">
			<ul>
				<li>
					<a href="#tabs-1">기본정보</a>
				</li>
				<li>
					<a href="#tabs-2">참조작번</a>
				</li>
				<li>
					<a href="#tabs-3">특이사항</a>
				</li>
				<li>
					<a href="#tabs-4">기계 수배표</a>
				</li>
				<li>
					<a href="#tabs-5">전기 수배표</a>
				</li>
				<li>
					<a href="#tabs-6">T-BOM</a>
				</li>
				<li>
					<a href="#tabs-7">통합 수배표</a>
				</li>
				<li>
					<a href="#tabs-8">CIP</a>
				</li>
				<li>
					<a href="#tabs-9">도면 일람표</a>
				</li>
			</ul>
			<div id="tabs-1">
				<table class="view-table">
					<%
					if (!dto.isEstimate()) {
					%>
					<colgroup>
						<col width="140">
						<col width="140">
						<col width="140">
						<col width="140">
						<col width="140">
						<col width="140">
						<col width="30">
						<col width="140">
						<col width="140">
					</colgroup>
					<tr>
						<th class="lb rb">KEK 작번</th>
						<th class="rb">거래처</th>
						<th class="rb">설치장소</th>
						<th class="rb">모델</th>
						<th class="rb">발행일</th>
						<th class="rb">요구 납기일</th>
						<td rowspan="4" class="tb-none bb-none" style="width: 30px;">&nbsp;</td>
						<th rowspan="2">진행률</th>
						<td rowspan="2" class="center"><%=dto.getKekProgress()%>%
						</td>
					</tr>
					<tr>
						<td class="center"><%=dto.getKekNumber()%></td>
						<td class="center"><%=dto.getCustomer_name()%></td>
						<td class="center"><%=dto.getInstall_name()%></td>
						<td class="center"><%=dto.getModel()%></td>
						<td class="center"><%=dto.getPdate_txt()%></td>
						<td class="center"><%=dto.getCustomDate_txt()%></td>
					</tr>
					<tr>
						<th class="lb rb">KE 작번</th>
						<th class="rb">USER ID</th>
						<th class="rb">작번 유형</th>
						<th class="rb">막종 / 막종상세</th>
						<th class="rb" colspan="2">작업 내용</th>
						<th>기계</th>
						<td class="center"><%=dto.getMachineProgress()%>%
						</td>
					</tr>
					<tr>
						<td class="center"><%=dto.getKeNumber()%></td>
						<td class="center"><%=dto.getUserId()%></td>
						<td class="center"><%=dto.getProjectType_name()%></td>
						<td class="center"><%=dto.getMak_name()%>
							/
							<%=dto.getDetail_name()%></td>
						<td class="indent5" colspan="2"><%=dto.getDescription()%></td>
						<th>전기</th>
						<td class="center"><%=dto.getElecProgress()%>%
						</td>
					</tr>
					<%
					} else {
					%>
	<colgroup>
						<col width="140">
						<col width="140">
						<col width="140">
						<col width="140">
						<col width="140">
						<col width="140">
						<col width="30">
						<col width="140">
						<col width="140">
					</colgroup>
					<tr>
						<th class="lb rb">KEK 작번</th>
						<th class="rb">거래처</th>
						<th class="rb">설치장소</th>
						<th class="rb">모델</th>
						<th class="rb">발행일</th>
						<th class="rb">요구 납기일</th>
						<td rowspan="4" class="tb-none bb-none" style="width: 30px;">&nbsp;</td>
						<th rowspan="4">진행률</th>
						<td rowspan="4" class="center"><%=dto.getKekProgress()%>%
						</td>
					</tr>
					<tr>
						<td class="center"><%=dto.getKekNumber()%></td>
						<td class="center"><%=dto.getCustomer_name()%></td>
						<td class="center"><%=dto.getInstall_name()%></td>
						<td class="center"><%=dto.getModel()%></td>
						<td class="center"><%=dto.getPdate_txt()%></td>
						<td class="center"><%=dto.getCustomDate_txt()%></td>
					</tr>
					<tr>
						<th class="lb rb">KE 작번</th>
						<th class="rb">USER ID</th>
						<th class="rb">작번 유형</th>
						<th class="rb">막종 / 막종상세</th>
						<th class="rb" colspan="2">작업 내용</th>
					</tr>
					<tr>
						<td class="center"><%=dto.getKeNumber()%></td>
						<td class="center"><%=dto.getUserId()%></td>
						<td class="center"><%=dto.getProjectType_name()%></td>
						<td class="center"><%=dto.getMak_name()%>
							/
							<%=dto.getDetail_name()%></td>
						<td class="indent5" colspan="2"><%=dto.getDescription()%></td>
					</tr>
					<%
					}
					%>

				</table>


				<div class="info-header">
					<img src="/Windchill/extcore/images/header.png">
					작번 상세 정보
				</div>

				<table class="view-table">
					<tr>
						<th class="lb rb">총기간[공수](일)</th>
						<th class="rb">계획 시작일</th>
						<th class="rb">계획 종료일</th>
						<th class="rb">실제 시작일</th>
						<th class="rb">실제 종료일</th>
					</tr>
					<tr>
						<td class="center"><%=dto.getDuration()%>[
							<font color="red"><%=dto.getHoliday()%></font>
							]일
						</td>
						<td class="center"><%=dto.getPlanStartDate_txt()%></td>
						<td class="center"><%=dto.getPlanEndDate_txt()%></td>
						<td class="center"><%=dto.getStartDate_txt()%></td>
						<td class="center"><%=dto.getEndDate_txt()%></td>
					</tr>
					<tr>
						<th class="lb rb">
							총괄 책임자&nbsp;
							<img src="/Windchill/extcore/images/edit.gif" class="edit" onclick="edit();">
						</th>
						<th class="rb">
							세부일정 책임자&nbsp;
							<img src="/Windchill/extcore/images/edit.gif" class="edit" onclick="edit();">
						</th>
						<th class="rb">
							기계&nbsp;
							<img src="/Windchill/extcore/images/edit.gif" class="edit" onclick="edit();">
						</th>
						<th class="rb">
							전기&nbsp;
							<img src="/Windchill/extcore/images/edit.gif" class="edit" onclick="edit();">
						</th>
						<th class="rb">
							SOFT&nbsp;
							<img src="/Windchill/extcore/images/edit.gif" class="edit" onclick="edit();">
						</th>
					</tr>
					<tr>
						<td class="center"><%=dto.getPm()%></td>
						<td class="center"><%=dto.getSubPm()%></td>
						<td class="center"><%=dto.getMachine_name() != null ? dto.getMachine_name() : "지정안됨"%></td>
						<td class="center"><%=dto.getElec_name() != null ? dto.getElec_name() : "지정안됨"%></td>
						<td class="center"><%=dto.getSoft_name() != null ? dto.getSoft_name() : "지정안됨"%></td>
					</tr>
					<%
					String outputTotal = String.format("%,.0f", dto.getOutputTotalPrice());
					String inputTotal = String.format("%,.0f", dto.getTotalPrice());
					BigDecimal outputTotalCounting = new BigDecimal(dto.getOutputTotalPrice());
					BigDecimal inputTotalPriceCounting = new BigDecimal(dto.getTotalPrice());

					int tPgoress = 0;
					if (inputTotalPriceCounting.intValue() != 0) {
						BigDecimal result = outputTotalCounting.divide(inputTotalPriceCounting, 2, RoundingMode.FLOOR);
						tPgoress = (int) (result.doubleValue() * 100);
					}

					String outputMachine = String.format("%,.0f", dto.getOutputMachinePrice());
					String inputOutputMachine = String.format("%,.0f", dto.getMachinePrice());
					BigDecimal outputMachineCounting = new BigDecimal(dto.getOutputMachinePrice());
					BigDecimal inputOutputMachineCounting = new BigDecimal(dto.getMachinePrice());

					int mProgress = 0;
					if (inputOutputMachineCounting.intValue() != 0) {
						BigDecimal result = outputMachineCounting.divide(inputOutputMachineCounting, 2, RoundingMode.FLOOR);
						mProgress = (int) (result.doubleValue() * 100);
					}

					String outputElec = String.format("%,.0f", dto.getOutputElecPrice());
					String inputOutputElec = String.format("%,.0f", dto.getElecPrice());
					BigDecimal outputElecCounting = new BigDecimal(dto.getOutputElecPrice());
					BigDecimal inputOutputElecCounting = new BigDecimal(dto.getElecPrice());

					int eProgress = 0;
					if (inputOutputElecCounting.intValue() != 0) {
						BigDecimal result = outputElecCounting.divide(inputOutputElecCounting, 2, RoundingMode.FLOOR);
						eProgress = (int) (result.doubleValue() * 100);
					}
					%>
					<tr>
						<th class="lb rb">작번상태</th>
						<th class="rb">진행상태</th>
						<%
						if (!dto.isEstimate()) {
						%>
						<th class="rb">작번 견적 금액</th>
						<th class="rb">
							기계 견적 금액&nbsp;
							<img src="/Windchill/extcore/images/edit.gif" class="edit" onclick="money('<%=inputOutputMachine%>', 'm');">
						</th>
						<th class="rb">
							전기 견적 금액&nbsp;
							<img src="/Windchill/extcore/images/edit.gif" class="edit" onclick="money('<%=inputOutputElec%>', 'e');">
						</th>
						<%
						} else {
						%>
						<th class="rb" colspan="3">&nbsp;</th>
						<%
						}
						%>
					</tr>
					<tr>
						<td class="center"><%=dto.getKekState()%></td>
						<td class="center"><%=dto.getState()%></td>
						<%
						if (!dto.isEstimate()) {
						%>
						<td class="rb center">
							<font color="blue">
								<b><%=outputTotal%>원
								</b>
							</font>
							/
							<font color="red">
								<b><%=inputTotal%>원
								</b>
							</font>
							/
							<%=tPgoress%>%
						</td>
						<td class="rb center">
							<font color="blue">
								<b><%=outputMachine%>원
								</b>
							</font>
							/
							<font color="red">
								<b><%=inputOutputMachine%>원
								</b>
							</font>
							/
							<%=mProgress%>%
						</td>
						<td class="rb center">
							<font color="blue">
								<b><%=outputElec%>원
								</b>
							</font>
							/
							<font color="red">
								<b><%=inputOutputElec%>원
								</b>
							</font>
							/
							<%=eProgress%>%
						</td>
						<%
						} else {
						%>
						<td colspan="3">&nbsp;</td>
						<%
						}
						%>
					</tr>
				</table>
				<br>
				<div id="_chart" style="height: 410px;"></div>
				<script type="text/javascript">
					Highcharts.chart('_chart', {
						chart : {
							type : 'column'
						},
						title : {
							text : '작번 견적 금액 차트(수배표/입력)',
						},
						subtitle : {
							text : "<%=dto.getKekNumber()%> / <%=dto.getKeNumber()%>",
						},
						tooltip : {
							headerFormat : '<span style="font-size:10px">{point.key}</span><table>',
							pointFormat : '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' + '<td style="padding:0"><b>{point.y:.1f} mm</b></td></tr>',
							footerFormat : '</table>',
							shared : true,
							useHTML : true
						},
						xAxis : {
							categories : [ '작번 견적 금액(수배표)', '작번 견적 금액(입력)' ],
							crosshair : true
						},
						yAxis : {
							min : 0,
							title : {
								text : '원'
							}
						},
						plotOptions : {
							column : {
								pointPadding : 0.2,
								borderWidth : 0
							}
						},
						tooltip : {
							headerFormat : '<span style="font-size:10px">{point.key}</span><table>',
							pointFormat : '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' + '<td style="padding:0"><b>{point.y:%,.0f} 원</b></td></tr>',
							footerFormat : '</table>',
							shared : true,
							useHTML : true
						},
						series : [ {
							name : '작번 견적 금액',
							data : [
				<%=dto.getOutputTotalPrice()%>
					,
				<%=dto.getTotalPrice()%>
					]
						}, {
							name : '기계 견적 금액',
							data : [
				<%=dto.getOutputMachinePrice()%>
					,
				<%=dto.getMachinePrice()%>
					]
						}, {
							name : '전기 견적 금액',
							data : [
				<%=dto.getOutputElecPrice()%>
					,
				<%=dto.getElecPrice()%>
					]
						} ]
					});
				</script>
			</div>
			<div id="tabs-2"></div>
			<div id="tabs-3">
				<iframe style="height: 800px;" src="/Windchill/plm/project/issueTab?oid=<%=dto.getOid()%>"></iframe>
			</div>
			<div id="tabs-4">
				<iframe style="height: 800px;" src="/Windchill/plm/project/partlistTab?oid=<%=dto.getOid()%>&invoke=m"></iframe>
			</div>
			<div id="tabs-5">
				<iframe style="height: 800px;" src="/Windchill/plm/project/partlistTab?oid=<%=dto.getOid()%>&invoke=e"></iframe>
			</div>
			<div id="tabs-6">
				<iframe style="height: 800px;" src="/Windchill/plm/project/tbomTab?oid=<%=dto.getOid()%>" onload="hide();"></iframe>
			</div>
			<div id="tabs-7">
				<iframe style="height: 800px;" src="/Windchill/plm/project/partlistTab?oid=<%=dto.getOid()%>&invoke=a"></iframe>
			</div>
			<div id="tabs-8">
				<iframe style="height: 800px;" src="/Windchill/plm/project/cipTab?oid=<%=dto.getOid()%>&invoke=a"></iframe>
			</div>
			<div id="tabs-9">
				<iframe style="height: 800px;" src="/Windchill/plm/project/workOrderTab?oid=<%=dto.getOid()%>&invoke=a"></iframe>
			</div>
		</div>
		<script type="text/javascript">
			function hide() {
				// 				parent.parent.closeLayer();
			}

			function money(money, type) {
				const oid = document.getElementById("oid").value;
				const url = getCallUrl("/project/money?oid=" + oid + "&money=" + money + "&type=" + type);
				popup(url, 500, 300);
			}

			function edit() {
				const oid = document.getElementById("oid").value;
				const url = getCallUrl("/project/editUser?oid=" + oid);
				popup(url, 500, 300);
			}

			document.addEventListener("DOMContentLoaded", function() {
				$("#tabs").tabs({
				// 					activate : function(event, ui) {
				// 						var tabId = ui.newPanel.prop("id");
				// 						switch (tabId) {
				// 						case "tabs-2":
				// 							parent.parent.openLayer();
				// 							break;
				// 						case "tabs-3":
				// 							parent.parent.openLayer();
				// 							break;
				// 						case "tabs-4":
				// 							parent.parent.openLayer();
				// 							break;
				// 						case "tabs-5":
				// 							parent.parent.openLayer();
				// 							break;
				// 						case "tabs-6":
				// 							parent.parent.openLayer();
				// 							break;
				// 						}
				// 					}
				})
				parent.parent.closeLayer();
			})
		</script>
	</form>
</body>
</html>