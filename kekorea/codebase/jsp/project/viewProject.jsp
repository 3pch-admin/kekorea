<%@page import="wt.content.ApplicationData"%>
<%@page import="wt.content.ContentItem"%>
<%@page import="wt.content.ContentRoleType"%>
<%@page import="wt.content.ContentHelper"%>
<%@page import="e3ps.common.util.ContentUtils"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="wt.content.ContentHolder"%>
<%@page import="wt.query.SearchCondition"%>
<%@page import="wt.query.QuerySpec"%>
<%@page import="e3ps.project.Output"%>
<%@page import="e3ps.project.ProjectOutputLink"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="e3ps.project.service.ProjectHelper"%>
<%@page import="e3ps.project.Issue"%>
<%@page import="e3ps.project.IssueProjectLink"%>
<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="java.math.BigDecimal"%>
<%@page import="e3ps.project.Project"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.project.dto.ProjectViewData"%>
<%@page import="e3ps.project.dto.TemplateViewData"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	// popup check
	String popup = (String) request.getParameter("popup");
	boolean isPopup = Boolean.parseBoolean(popup);

	// data
	ProjectViewData data = (ProjectViewData) request.getAttribute("data");
// 	ProjectHelper.manager.gate1StateIcon(data.project);

// ProjectHelper.manager.setProgress(data.project);

	// isAdmin
	boolean isAdmin = CommonUtils.isAdmin();
	String oid = data.oid;
	
	ArrayList<Project> refProjectList = (ArrayList<Project>) request.getAttribute("refProjectList");
	ArrayList<IssueProjectLink> issueProjectList = (ArrayList<IssueProjectLink>) request.getAttribute("issueLink");
	
%>

<td valign="top">
<script type="text/javascript">
	$(document).ready(function() {
		var len = "<%=data.description.length()%>";
		$("#descTempCnt").text(len);
		$("input").checks();
		
		$("#kekState").bindSelect();
		$("#kekState").bindSelectSetValue("<%=data.kekState %>");
		
		$(".documents_add_table").tableHeadFixer();
		
		$("#kekState").change(function() {
			if(this.value != "<%=data.kekState %>") {
				projects.setKekState(this);				
			}
		})
	})
	
	function Open(oid) {
		var url = "/Windchill/plm/project/viewIssueDocument?popup=true&oid=" + oid + "&poid=<%=data.oid %>";
		var name = "viewIssueDocument";
// 		var option = "scrollbars=yes, resizable=yes, fullscreen=yes";
		window.open(url, name, 'height=' + screen.height + ',width=' + screen.width + 'fullscreen=yes');		
	}
	
</script> 
	<input type="hidden" name="oid" id="oid" value="<%=data.oid%>">
	<input type="hidden" name="popup" id="popup" value="<%=isPopup%>">
	
	<table class="btn_table">
		<tr>
			<td>
				<div class="header_title">
					<i class="axi axi-subtitles"></i><span>?????? ??????</span>
				</div>
			</td>
			<td>	
			<div class="right">
				<input type="button" value="????????????" data-oid="<%=data.oid %>" id="viewSchedule" title="????????????" class="blueBtn">
				<input type="button" value="??????" id="modifyProjectBtn" title="??????" class="blueBtn">
				<%
					if(data.isStand) {
				%>
<%-- 				<input type="button" value="??????" id="startProject" title="??????" data-oid="<%=data.oid %>"> --%>
				<%
					} else if(data.isStart) {
						if(data.kekProgress != 100) {
				%>
				<input type="button" value="??????" id="stopProject" title="??????" data-oid="<%=data.oid %>">
				<%
						} else if(data.kekProgress == 100) {
				%>
				<input type="button" value="??????" id="completeProject" title="??????" data-oid="<%=data.oid %>">
				<%
						}
					} else if(data.isStop) {
				%>
				<input type="button" value="?????????" id="restartProject" title="?????????" data-oid="<%=data.oid %>">
				<%
					}
				%>
				<input type="button" value="??????" id="closeProjectBtn" title="??????" class="redBtn">
			</div>
			</td>
		</tr>
	</table>

	<table class="container_table">
		<tr>
			<jsp:include page="/jsp/project/include_projectTask.jsp">
				<jsp:param value="<%=oid%>" name="oid" />
			</jsp:include>
			<!-- only folder tree.. -->
			<td id="container_td">
				<table class="project_table">
					<%
						if(!data.isQuotation) {
					%>
					<tr>
						<th class="min-wid150">KEK ??????</th>
						<th class="min-wid150">?????????</th>
						<th class="min-wid150">????????????</th>
						<th class="min-wid150">??????</th>
						<th class="min-wid150">?????????</th>
						<th class="min-wid150">?????? ?????????</th>
						<th rowspan="4" class="border-none bgnone min-wid20">&nbsp;</th>
						<th rowspan="2" class="border-left min-wid100">?????????</th>
						<td rowspan="2" class="center min-wid100 border-top-blue "><%=data.kekProgress %>%<%//=StringUtils.numberFormat(data.comp, "###") %></td>
					</tr>

					<tr>
						<td class="center"><%=data.kek_number %></td>
						<td class="center"><%=data.customer %></td>
						<td class="center"><%=data.ins_location %></td>
						<td class="center"><%=data.mak %></td>
						<td class="center"><%=data.pDate %></td>
						<td class="center"><%=data.customDate %></td>
					</tr>
					<tr>
						<th>KE ??????</th>
						<th>USER ID</th>
						<th>?????? ??????</th>
						<th>??????</th>
						<th colspan="2">?????? ??????</th>
						<th class="border-left">??????</th>
						<td class="center"><%=data.machineProgress %>%</td>
					</tr>
					<tr>
						<td class="center"><%=data.ke_number %></td>
						<td class="center"><%=data.userID %>
						<td class="center"><%=data.pType %>
						<td class="center"><%=data.model %>
						<td class="indent10" colspan="2"><%=data.description %></td>
						<th class="border-left">??????</th>
						<td class="center"><%=data.elecProgress %>%</td>
					</tr>
					<%
						} else {
					%>
					<tr>
						<th class="min-wid150">KEK ??????</th>
						<th class="min-wid150">?????????</th>
						<th class="min-wid150">????????????</th>
						<th class="min-wid150">??????</th>
						<th class="min-wid150">?????????</th>
						<th class="min-wid150">?????? ?????????</th>
						<th rowspan="4" class="border-none bgnone min-wid20">&nbsp;</th>
						<th rowspan="5" class="border-left min-wid100">?????????</th>
						<td rowspan="5" class="center min-wid100 border-top-blue "><%=data.kekProgress %>%<%//=StringUtils.numberFormat(data.comp, "###") %></td>
					</tr>

					<tr>
						<td class="center"><%=data.kek_number %></td>
						<td class="center"><%=data.customer %></td>
						<td class="center"><%=data.ins_location %></td>
						<td class="center"><%=data.mak %></td>
						<td class="center"><%=data.pDate %></td>
						<td class="center"><%=data.customDate %></td>
					</tr>
					<tr>
						<th>KE ??????</th>
						<th>USER ID</th>
						<th>?????? ??????</th>
						<th colspan="3">?????? ??????</th>
					</tr>
					<tr>
						<td class="center"><%=data.ke_number %></td>
						<td class="center"><%=data.userID %>
						<td class="center"><%=data.pType %>
						<td class="indent10" colspan="3"><%=data.description %></td>
					</tr>					
					<%
						}
					%>
				</table>
				
				<div class="header_title margin_top10">
					<i class="axi axi-subtitles"></i><span>?????? ?????? ??????</span>
				</div>
				
				<table class="project_table">
					<tr>
						<th>?????????[??????](???)</th>
						<th>?????? ?????????</th>
						<th>?????? ?????????</th>
						<th>?????? ?????????</th>
						<th>?????? ?????????</th>
					</tr>				
					<tr>
						<td class="center"><%=data.duration %>[<font color="red"><%=data.holiday %></font>]???</td>
						<td class="center"><%=data.planStartDate %></td>
						<td class="center"><%=data.planEndDate %></td>
						<td class="center"><%=data.startDate %></td>
						<td class="center"><%=data.endDate %></td>
					</tr>					
					<tr>
						<th>?????? ?????????<%if(data.isEditer) { %>&nbsp;<img src="/Windchill/jsp/images/edit.gif" class="pos3 pm" data-user="pm"><%} %></th>
						<th>???????????? ?????????<%if(data.isEditer) { %>&nbsp;<img src="/Windchill/jsp/images/edit.gif" class="pos3 subpm" data-user="subpm"><%} %></th>
						<th>??????<%if(data.isEditer) { %>&nbsp;<img src="/Windchill/jsp/images/edit.gif" class="pos3 machine" data-user="machine"><%} %></th>
						<th>??????<%if(data.isEditer) { %>&nbsp;<img src="/Windchill/jsp/images/edit.gif" class="pos3 elec" data-user="elec"><%} %></th>
						<th>SOFT<%if(data.isEditer) { %>&nbsp;<img src="/Windchill/jsp/images/edit.gif" class="pos3 soft" data-user="soft"><%} %></th>
					</tr>
					<tr>
						<td class="center"><%=data.pm != null ?  data.pm.getFullName() : "????????????" %></td>
						<td class="center"><%=data.subPm != null ? data.subPm.getFullName() : "????????????" %></td>
						<td class="center"><%=data.machine != null ?  data.machine.getFullName() : "????????????" %></td>
						<td class="center"><%=data.elec != null ?  data.elec.getFullName() : "????????????" %></td>
						<td class="center"><%=data.soft != null ?  data.soft.getFullName() : "????????????" %></td>
					</tr>					
					<tr>
						<th>????????????<%if(data.isEditer) { %>&nbsp;<img src="/Windchill/jsp/images/edit.gif" class="pos3 pm" data-user="pm"><%} %></th>
						<th>????????????</th>
						<%
							if(!data.isQuotation) {
						%>
						<th>?????? ?????? ??????</th>
						<th>?????? ?????? ??????<%if(data.isEditer) { %>&nbsp;<img src="/Windchill/jsp/images/edit.gif" class="pos3 machinePrice"><%} %></th>
						<th>?????? ?????? ??????<%if(data.isEditer) { %>&nbsp;<img src="/Windchill/jsp/images/edit.gif" class="pos3 elecPrice"><%} %></th>
						<%
							} else {
						%>
						<th colspan="3">&nbsp;</th>
						<%
							}
						%>
					</tr>
					<%
						// ?????? ?????????
							String outputTotal = String.format("%,f",data.outputTotalPrice);
							outputTotal = outputTotal.substring(0, outputTotal.lastIndexOf("."));
							
							String inputTotal = String.format("%,f",data.totalPrice);
							inputTotal = inputTotal.substring(0, inputTotal.lastIndexOf("."));
							
							BigDecimal outputTotalCounting = new BigDecimal(data.outputTotalPrice);
							BigDecimal inputTotalPriceCounting = new BigDecimal(data.totalPrice);

							int tPgoress = 0;
							if(inputTotalPriceCounting.intValue() != 0) {
								BigDecimal result = outputTotalCounting.divide(inputTotalPriceCounting, 2, BigDecimal.ROUND_FLOOR);
								tPgoress = (int) (result.doubleValue() * 100);
							}
					
							String outputMachine = String.format("%,f",data.outputMachinePrice);
							outputMachine = outputMachine.substring(0, outputMachine.lastIndexOf("."));
							
							String inputOutputMachine = String.format("%,f",data.machinePrice);
							inputOutputMachine = inputOutputMachine.substring(0, inputOutputMachine.lastIndexOf("."));
							
							BigDecimal outputMachineCounting = new BigDecimal(data.outputMachinePrice);
							BigDecimal inputOutputMachineCounting = new BigDecimal(data.machinePrice); //???

							int mProgress = 0;
							if(inputOutputMachineCounting.intValue() != 0) {
								BigDecimal result = outputMachineCounting.divide(inputOutputMachineCounting, 2, BigDecimal.ROUND_FLOOR);
								mProgress = (int) (result.doubleValue() * 100);
							}
					
							String outputElec = String.format("%,f",data.outputElecPrice);
							outputElec = outputElec.substring(0, outputElec.lastIndexOf("."));
							
							String inputOutputElec = String.format("%,f",data.elecPrice);
							inputOutputElec = inputOutputElec.substring(0, inputOutputElec.lastIndexOf("."));
							
							BigDecimal outputElecCounting = new BigDecimal(data.outputElecPrice);
							BigDecimal inputOutputElecCounting = new BigDecimal(data.elecPrice); //???

							int eProgress = 0;
							if(inputOutputElecCounting.intValue() != 0) {
								BigDecimal result = outputElecCounting.divide(inputOutputElecCounting, 2, BigDecimal.ROUND_FLOOR);
								eProgress = (int) (result.doubleValue() * 100);
							}
					%>
					<tr>
						<td class="center"><%=data.kekState %></td>
						<td class="center"><%=data.state %></td>
						<%
							if(!data.isQuotation) {
						%>
						<td class="center"><font color="blue"><b><%=outputTotal %>???</b></font>/<font color="red"><b><%=inputTotal %>???</b></font>/<%=tPgoress %>%</td>
						<td class="center"><font color="blue"><b><%=outputMachine %>???</b></font>/<font color="red"><b><%=inputOutputMachine %>???</b></font>/<%=mProgress %>%</td>
						<td class="center"><font color="blue"><b><%=outputElec %>???</b></font>/<font color="red"><b><%=inputOutputElec %>???</b></font>/<%=eProgress %>%</td>
						<%
							} else {
						%>
						<td colspan="3">&nbsp;</td>
						<%
							}
						%>
					</tr>
				</table>
				
				<div class="header_title margin_top10">
					<i class="axi axi-subtitles"></i><span>?????? ?????? ??????</span>
				</div>		
			
				<table class="project_table">
					<thead>
						<tr>
							<th class="min-wid100">KEK??????</th>
							<th class="min-wid100">?????? ??????</th>
							<th class="min-wid300">?????? ??????</th>
							<th class="min-wid100">??????</th>
							<th class="min-wid100">????????????</th>
							<th class="min-wid100">?????????</th>
							<th class="min-wid100">???????????????</th>
							
						</tr>
					</thead>
					<%
						for(Project refProject : refProjectList) {
							ProjectViewData refData = new ProjectViewData(refProject);
					%>
					<tr>
						<td class="center viewProject" data-oid="<%=refData.oid%>"><%=refData.kek_number %></td>
						<td class="center"><%=refData.pType %></td>
						<td class="indent10"><%=refData.description %></td>
						<td class="center"><%=refData.mak %></td>
						<td class="center"><%=refData.ins_location %></td>
						<td class="center"><%=refData.createDate %></td>
						<td class="center"><%=refData.customDate %></td>
					</tr>
					<%
						}
						if(refProjectList.size() == 0) {
					%>
					<tr>
						<td class="nodata" colspan="7">?????? ????????? ????????????.</td>
					</tr>
					<%
						}
					%>
				</table>
					
				<div class="header_title margin_top10">
					<i class="axi axi-subtitles"></i><span>?????? ?????? ??????</span>
				</div>	
									
				<table class="project_table">
					<tr>
						<td colspan="5" class="border-top-blue">
						<table class="project_in_btn_table">
							<tr>
								<td class="add">
									<input type="button" value="???????????? ??????" title="???????????? ??????" id="addIssue" data-dbl="true" data-poid="<%=data.oid %>">
<!-- 								<input type="button" value="????????????" title="???????????? ??????" id="linkIssue" data-dbl="true"> -->
									<input type="button" value="???????????? ??????" title="???????????? ??????" id="delIssue" class="blueBtn"></td>
							</tr>
						</table>
							<table id="tblBackground">
								<tr>
									<td class="nonBorder">
										<div id="issues_container">
											<table class="create_project_table_in documents_add_table fix_table">
												<colgroup>
													<col width="40">
													<col width="300">
													<col width="*">
													<col width="150">
													<col width="150">
													<col width="100"> 
												</colgroup>
												<thead>
													<tr>
														<th><input type="checkbox" name="allIssues"
															id="allIssues"></th>
														<th>??????</th>
														<th>??????</th>
														<th>?????????</th>
														<th>?????????</th>
														<th>????????????</th>
													</tr>
												</thead>
												<tbody id="addIssuesBody">
												<%
													
												
												
													for(IssueProjectLink issueProjectLink : issueProjectList) {
														Issue issue = issueProjectLink.getIssue();
														String ioid = issue.getPersistInfo().getObjectIdentifier().getStringValue();
														String des = issue.getDescription();
														String primarysURL= "";
														if(!StringUtils.isNull(des)) {
														des = des.replaceAll("\n", "<br>");
														} else {
															des = "";
														}
														ReferenceFactory rf = new ReferenceFactory();
														ContentHolder holder = (ContentHolder)rf.getReference(ioid).getObject();
														String[] primarys = ContentUtils.getPrimary(holder);
														QueryResult qr = ContentHelper.service.getContentsByRole(holder, ContentRoleType.PRIMARY);
														if (qr.hasMoreElements()) {
															ContentItem item = (ContentItem) qr.nextElement();

															if (item instanceof ApplicationData) {
																ApplicationData ap = (ApplicationData) item;
															
																primarysURL= ContentHelper.getDownloadURL(holder, ap,false,primarys[2]).toString();
															}	
														}
														System.out.println(primarys.length);
														System.out.println("1 : " + primarys[0]);
														System.out.println("2 : " + primarys[1]);
														System.out.println("3 : " + primarys[2]);
														System.out.println("4 : " + primarys[3]);
														System.out.println("5 : " + primarys[4]);
														System.out.println("6 : " + primarys[5]);
														System.out.println("7 : " + primarys[6]);
														System.out.println("999 : " + primarysURL);
														%>
														<tr id="nodataIssues">
															<td>
																<input class="isBox" type="checkbox" name="issueOid" value="<%=ioid %>">
															</td>
															<td title="<%=issue.getName() %>" style="text-overflow: ellipsis; overflow:hidden;"data-oid="<%=ioid %>">
																<a href= "javascript:Open('<%=ioid%>');" ><%=issue.getName() %></a>
															</td>
															<td class="left">
															<a href= "javascript:Open('<%=ioid%>');" ><%=des%></a></td>
															<td><%=issue.getOwnership().getOwner().getFullName() %></td>
															<td><%=issue.getCreateTimestamp().toString().substring(0, 16) %></td>
															<%
																if(primarys[0] != null){
															%>
																	<td><a href="<%=primarysURL %>"><img src="<%=primarys[4] %>" class="pos2"></a></td>
															<% } else { %>
																	<td >-</td>
															<% }			%>
														<%
													}
												%>
												</tbody>
												<%
													if(issueProjectList.size() == 0) {
												%>
												<tbody id="addIssuesBody">
													<tr id="nodataIssues">
														<td class="nodata" colspan="6">????????? ??????????????? ????????????.</td>
													</tr>
												</tbody>
												<% } %>
											</table>
										</div>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</td>