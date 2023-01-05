<%@page import="e3ps.partlist.PartListMasterProjectLink"%>
<%@page import="java.util.Vector"%>
<%@page import="wt.enterprise.RevisionControlled"%>
<%@page import="e3ps.project.DocumentMasterOutputLink"%>
<%@page import="wt.content.ApplicationData"%>
<%@page import="wt.content.ContentHelper"%>
<%@page import="wt.content.HolderToContent"%>
<%@page import="wt.fc.Persistable"%>
<%@page import="java.math.BigDecimal"%>
<%@page import="wt.util.WTAttributeNameIfc"%>
<%@page import="wt.query.ClassAttribute"%>
<%@page import="wt.vc.VersionControlHelper"%>
<%@page import="wt.fc.PersistenceServerHelper"%>
<%@page import="wt.fc.ObjectReference"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.Statement"%>
<%@page import="java.sql.Connection"%>
<%@page import="e3ps.common.db.DBCPManager"%>
<%@page import="e3ps.doc.PRJDocument"%>
<%@page import="e3ps.doc.E3PSDocument"%>
<%@page import="wt.doc.WTDocumentMaster"%>
<%@page import="e3ps.doc.E3PSDocumentMaster"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.query.QuerySpec"%>
<%@page import="wt.query.SearchCondition"%>
<%@page import="e3ps.project.Task"%>
<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="wt.vc.Versioned"%>
<%@page import="wt.content.ContentHolder"%>
<%@page import="e3ps.partlist.PartListMaster"%>
<%@page import="wt.lifecycle.LifeCycleManaged"%>
<%@page import="e3ps.project.DocumentOutputLink"%>
<%@page import="e3ps.common.util.ContentUtils"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="e3ps.project.Output"%>
<%@page import="e3ps.project.service.ProjectHelper"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.project.beans.ProjectViewData"%>
<%@page import="e3ps.project.beans.TaskViewData"%>
<%@page import="e3ps.project.beans.TemplateViewData"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	// popup check
	String popup = (String) request.getParameter("popup");
	boolean isPopup = Boolean.parseBoolean(popup);

	// data
	TaskViewData data = (TaskViewData) request.getAttribute("data");
	ProjectViewData pdata = (ProjectViewData) request.getAttribute("pdata");
// 	QueryResult qr2 = PersistenceHelper.manager.navigate(pdata.project, "partListMaster", PartListMasterProjectLink.class);
// 	out.println(qr2.size());
	// isAdmin
	boolean isAdmin = CommonUtils.isAdmin();
	String poid = pdata.oid;
	String oid = data.oid;
	ArrayList<DocumentOutputLink> outputList = (ArrayList<DocumentOutputLink>) request.getAttribute("outputList");
	ArrayList<DocumentMasterOutputLink> outputMasterLink = (ArrayList<DocumentMasterOutputLink>) request.getAttribute("outputMasterLink");
	boolean isNormalTask = data.isNormalTask;
	String text = "산출물";
	String ids = "createOutput";
	String add = "addOutputs";
	String del = "delOutputs";
	String partListType = "";
	if(data.isElecPartList && !data.isReq) {
		text = "수배표";
		ids = "createPartListMaster";
		partListType = "elec";
	} else if(data.isMachinePartList){
		text = "수배표";
		ids = "createPartListMaster";
		partListType = "machine";
	}else if(data.isReq) {
		text = "의뢰서";
		ids = "createRequestDocument";
		add = "addRequestDocuments";
		del = "delRequestDocuments";
	}
	
%>


<td valign="top"><script type="text/javascript">
	$(document).ready(function() {
		var len = "<%=pdata.description.length()%>";
		$("#descTempCnt").text(len);
		
		var len2 = "<%=data.description.length()%>";

		$("#descTaskCnt").text(len2);

		$("input").checks();

		$(".documents_add_table").tableHeadFixer();

		$("#left_menu_td").hide();
		$("img.right_switch").show();
		$("#colGroups").remove();
		$(document).setHTML();
	})
</script> <input type="hidden" name="oid" id="oid" value="<%=data.oid%>">
	<input type="hidden" name="popup" id="popup" value="<%=isPopup%>">

	<table class="btn_table">
		<tr>
			<td>
				<div class="header_title">
					<i class="axi axi-subtitles"></i><span>작번 정보</span>
				</div>
			</td>
			<td>
				<%
					if (isPopup) {
				%>
				<div class="right">
					<input type="button" data-oid="<%=pdata.oid %>" value="일정보기"
						id="viewSchedule" title="일정보기" class="blueBtn"> <input
						type="button" value="닫기" id="closeDocBtn" title="닫기"
						class="redBtn">
				</div> <%
 	}
 %>
			</td>
		</tr>
	</table>

	<table class="container_table">
		<tr>
			<!-- only folder tree.. -->
			<jsp:include page="/jsp/project/include_projectTask.jsp">
				<jsp:param value="<%=poid%>" name="oid" />
			</jsp:include>
			<td id="container_td" valign="top">
				<table class="project_table">
					<%
						if(!pdata.isQuotation) {
					%>
					<tr>
						<th class="min-wid150">KEK 작번</th>
						<th class="min-wid150">거래선</th>
						<th class="min-wid150">설치장소</th>
						<th class="min-wid150">막종</th>
						<th class="min-wid150">발행일</th>
						<th class="min-wid150">요청 납기일</th>
						<th rowspan="4" class="border-none bgnone min-wid20">&nbsp;</th>
						<th rowspan="2" class="border-left min-wid100">진행률</th>
						<td rowspan="2" class="center min-wid100 border-top-blue "><%=pdata.kekProgress %>%<%//=StringUtils.numberFormat(data.comp, "###") %></td>
					</tr>

					<tr>
						<td class="center"><%=pdata.kek_number %></td>
						<td class="center"><%=pdata.customer %></td>
						<td class="center"><%=pdata.ins_location %></td>
						<td class="center"><%=pdata.mak %></td>
						<td class="center"><%=pdata.pDate %></td>
						<td class="center"><%=pdata.customDate %></td>
					</tr>
					<tr>
						<th>KE 작번</th>
						<th>USER ID</th>
						<th>작번 유형</th>
						<th colspan="3">작업 내용</th>
						<th class="border-left">기계</th>
						<td class="center"><%=pdata.machineProgress %>%</td>
					</tr>
					<tr>
						<td class="center"><%=pdata.ke_number %></td>
						<td class="center"><%=pdata.userID %>
						<td class="center"><%=pdata.pType %>
						<td class="indent10" colspan="3"><%=pdata.description %></td>
						<th class="border-left">전기</th>
						<td class="center"><%=pdata.elecProgress %>%</td>
					</tr>
					<%
						} else {
					%>
					<tr>
						<th class="min-wid150">KEK 작번</th>
						<th class="min-wid150">거래선</th>
						<th class="min-wid150">설치장소</th>
						<th class="min-wid150">막종</th>
						<th class="min-wid150">발행일</th>
						<th class="min-wid150">요청 납기일</th>
						<th rowspan="4" class="border-none bgnone min-wid20">&nbsp;</th>
						<th rowspan="5" class="border-left min-wid100">진행률</th>
						<td rowspan="5" class="center min-wid100 border-top-blue "><%=pdata.kekProgress %>%<%//=StringUtils.numberFormat(data.comp, "###") %></td>
					</tr>

					<tr>
						<td class="center"><%=pdata.kek_number %></td>
						<td class="center"><%=pdata.customer %></td>
						<td class="center"><%=pdata.ins_location %></td>
						<td class="center"><%=pdata.mak %></td>
						<td class="center"><%=pdata.pDate %></td>
						<td class="center"><%=pdata.customDate %></td>
					</tr>
					<tr>
						<th>KE 작번</th>
						<th>USER ID</th>
						<th>작번 유형</th>
						<th colspan="3">작업 내용</th>
					</tr>
					<tr>
						<td class="center"><%=pdata.ke_number %></td>
						<td class="center"><%=pdata.userID %>
						<td class="center"><%=pdata.pType %>
						<td class="indent10" colspan="3"><%=pdata.description %></td>
					</tr>
					<%
						}
					%>
				</table>
				<div class="header_title margin_top10">
					<i class="axi axi-subtitles"></i><span>테스크 상세 정보</span>
				</div>

				<table class="project_table">
					<tr>
						<th class="min-wid200">태스크 명</th>
						<th class="min-wid200">태스크 타입</th>
						<th class="min-wid200">할당률</th>
						<th class="min-wid200">총기간[공수](일)</th>
						<th class="min-wid200">태스크 상태</th>
					</tr>
					<tr>
						<td class="center"><%=data.name %></td>
						<td class="center"><%=data.taskType %></td>
						<td class="center"><%=data.allocate %>%</td>
						<td class="center"><%=data.duration %>[<font color="red"><%=data.holiday %></font>](일)</td>
						<td class="center"><%=data.state %></td>
					</tr>
					<tr>
						<th class="min-wid200">계획 시작일</th>
						<th class="min-wid200">계획 종료일</th>
						<th class="min-wid200">실제 시작일</th>
						<th class="min-wid200">실제 종료일</th>
						<%
							if(!data.isReq) {
						%>
						<th class="min-wid200">진행률<%if(data.isEditer) { %>&nbsp;<img
							src="/Windchill/jsp/images/edit.gif" class="pos3 progress"
							data-progress="<%=data.progress %>">
							<%} %></th>
						<%
							} else {
						%>
						<th class="min-wid200">&nbsp;</th>
						<%
							}
						%>
					</tr>
					<tr>
						<td class="center"><%=data.planStartDate %></td>
						<td class="center"><%=data.planEndDate %></td>
						<td class="center"><%=data.startDate %></td>
						<td class="center"><%=data.endDate %></td>
						<%
							if(!data.isReq) {
						%>
						<td class="center"><%=data.progress %>%</td>
						<%
							} else {
						%>
						<td class="center">&nbsp;</td>
						<%
							}
						%>
					</tr>
				</table>
				<div class="header_title margin_top10">
					<i class="axi axi-subtitles"></i><span>선행 태스크</span>
				</div> <jsp:include page="/jsp/project/refDependencyTask.jsp">
					<jsp:param value="<%=data.oid %>" name="oid" />
				</jsp:include>


				<div class="header_title margin_top10">
					<i class="axi axi-subtitles"></i><span>테스크 <%=text %> 정보
					</span>
				</div>

				<table class="project_table">
					<tr>
						<td colspan="5" class="border-top-blue">
							<%
								if(data.isPartList) {
									Task parent = data.task.getParentTask();
									String pname = "";
									if(parent != null) {
										if(parent.getName().lastIndexOf("_") > -1) {
											pname = parent.getName().substring(0, parent.getName().lastIndexOf("_"));
										}
									}
									
									if(data.name.lastIndexOf("_") > -1) {
										String engType = data.name.substring(0, data.name.lastIndexOf("_"));
										if(parent == null) {
							%>
							<table class="project_in_btn_table">
								<tr>
									<td class="add"><input type="button" value="<%=text %> 등록"
										title="<%=text %> 등록" id="<%=ids %>" data-oid="<%=data.oid %>"
										data-loc="<%=data.name %>" data-poid="<%=pdata.oid %>"
										data-progress="<%=data.progress %>"> <input
										type="button" value="링크등록" title="링크등록" id="<%=add%>"
										data-dbl="true" class="blueBtn" data-oid="<%=data.oid%>"
										data-type="<%=partListType%>"> <input type="button"
										value="삭제" title="삭제" id="delOutputs" class="redBtn">
									</td>

									<td class="rightBtn"><input type="button"
										value="수배표 통합 보기" title="수배표 통합 보기" id="totalPartListBtn"
										data-pname="<%=pname %>" data-oid="<%=pdata.oid %>"
										data-eng="<%=engType %>"></td>
								</tr>
							</table> <%
										}
									}
								} else {
							%>
							<table class="project_in_btn_table">
								<tr>
									<td class="add"><input type="button" value="<%=text %> 등록"
										title="<%=text %> 등록" data-ptype="<%=data.taskType %>"
										id="<%=ids %>" data-loc="<%=data.name %>"
										data-poid="<%=pdata.oid %>" data-oid="<%=data.oid %>"
										data-progress="<%=data.progress %>"> <input
										type="button" value="링크등록" title="링크등록" id="<%=add%>"
										data-dbl="true" class="blueBtn" data-oid="<%=data.oid%>">
										<input type="button" value="삭제" title="삭제" id="delOutputs"
										class="redBtn"></td>
								</tr>
							</table> <%
								}
							%>
							<table class="output_table">
								<tr>
									<td class="nonBorder">
										<div id="outputs_container">
											<table class="create_project_table_in documents_add_table">
												<thead>
													<tr>
														<th class="min-wid40"><input type="checkbox"
															name="allOutputs" id="allOutputs"></th>
														<th class="min-wid450"><%=text %>제목</th>
														<th class="min-wid100"><%=text %> 저장위치</th>
														<th class="min-wid40">버전</th>
														<th class="min-wid40">상태</th>
														<th class="min-wid80">작성자</th>
														<th class="min-wid100">작성일</th>
														<th class="min-wid40">첨부파일 <%=outputList.size() %></th>
													</tr>
												</thead>
												<tbody id="addOutputsBody">
													<%
													if(outputList.size() != 0) {
													for(DocumentOutputLink outputLink : outputList) {
														Output output = outputLink.getOutput();
// 														out.println("======="+outputLink);
// 														out.println("======="+outputLink.getDocument());
														
// 														if(outputLink.getDocument() == null) {
// 															continue;
// 														}
														
														LifeCycleManaged lcm = outputLink.getDocument();
														
														boolean isNormalOutput = true;
														String name = "";
														if((lcm instanceof Versioned) && !data.name.equals("의뢰서")) {
															if(!CommonUtils.isLatestVersion(lcm)) {
																continue;
															}
														}
														
														if(lcm instanceof PartListMaster) {
															PartListMaster dd = (PartListMaster)lcm;
															name = dd.getName();
															isNormalOutput = false;
														} else if(lcm instanceof WTDocument) {
															WTDocument doc = (WTDocument)lcm;
															name = doc.getName();
															isNormalOutput = true;
														}
														String[] primary = ContentUtils.getPrimary((ContentHolder)lcm);
// 														String[] primary = new String[]{};
														String ooid = output.getPersistInfo().getObjectIdentifier().getStringValue();
														String doid = lcm.getPersistInfo().getObjectIdentifier().getStringValue();
														
														String clz = "left indent10 viewOutput";
														if(data.isPartList) {
															clz = "left indent10 partListInfo";
														} else if(data.isReq) {
															clz = "left indent10 viewReqDoc";
														}
												%>
												
													<tr>
														<td><input class="isBox" type="checkbox"
															name="outputOid" value="<%=ooid %>"
															data-doid="<%=doid %>"></td>
														<td class="<%=clz %>" title="<%=name %>"
															data-oid="<%=doid %>" data-task="true"><%=name %>
															<%//=output.getName() %></td>
														<td class="center"><%=output.getLocation() %></td>
														<%
														if(isNormalOutput && !data.isPartList) {
															Versioned versioned = (Versioned)lcm;
													%>
														<td><%=versioned.getVersionIdentifier().getSeries().getValue() %>.<%=versioned.getIterationIdentifier().getSeries().getValue() %></td>
														<%
														} else {
													%>
													<td>&nbsp;</td>
													<%
														}
													%>
														<td><%=lcm.getLifeCycleState().getDisplay(SessionHelper.manager.getLocale()) %></td>
														<td><%=output.getOwnership().getOwner().getFullName() %></td>
														<td><%=output.getCreateTimestamp().toString().substring(0, 16) %></td>
														<%
															if(isNormalOutput) {
														%>
														<td>
															<%
															if(primary[4] != null) {
														%> <a href="<%=primary[5] %>"><img
																src="<%=primary[4] %>" class="pos2"></a> <%
															} else {
																out.println("&nbsp;");
															}
														%>														
														</td>
														<%
															} else {
														%>
														<td>
														<%
															Vector<String[]> ss = ContentUtils.getSecondary((ContentHolder)lcm);
															for(String[] s : ss) {
														%>
														<a href="<%=s[5] %>"><img src="<%=s[4] %>" class="pos2"></a>														
														<%
															}
														%>
														</td>														
														<%
															}
														%>
													</tr>
													<%
													}
													}
													
													if(outputMasterLink.size() != 0) {
												%>
<%
													for(DocumentMasterOutputLink outputLink : outputMasterLink) {
														Output output = outputLink.getOutput();
														String ooid = output.getPersistInfo().getObjectIdentifier().getStringValue();
														E3PSDocumentMaster mm = outputLink.getMaster();

														RevisionControlled rc = CommonUtils.getLatestObject(mm);
														
														String roid = rc.getPersistInfo().getObjectIdentifier().getStringValue();
														
														String[] primary = ContentUtils.getPrimary((ContentHolder)rc);
														
// 														String title = "산출물 정보보기";
														String clz = "left indent10 viewOutput";
												%>
													<tr>
														<td><input class="isBox" type="checkbox"
															name="outputOid" value="<%=ooid %>"
															data-doid="<%=roid %>"></td>
														<td class="<%=clz %>" title="<%=rc.getName() %>"
															data-oid="<%=roid %>" data-task="true"><%=rc.getName() %>
															<%//=output.getName() %></td>
														<td class="left indent10"><%=rc.getLocation() %></td>
														<td><%=rc.getVersionIdentifier().getSeries().getValue() %>.<%=rc.getIterationIdentifier().getSeries().getValue() %></td>													
														<td><%=rc.getLifeCycleState().getDisplay(SessionHelper.manager.getLocale()) %></td>
														<td><%=rc.getCreatorFullName() %></td>
														<td><%=rc.getCreateTimestamp().toString().substring(0, 16) %></td>
														<td>
															<%
															if(primary[4] != null) {
														%> <a href="<%=primary[5] %>"><img
																src="<%=primary[4] %>" class="pos2"></a> <%
															} else {
																out.println("&nbsp;");
															}
														%>
														</td>
													</tr>
													<%
													}
												%>												
												
												<%
													}
												%>
												</tbody>
												<%
													if(outputList.size() == 0 && outputMasterLink.size() == 0) {
												%>
												<tbody id="addOutputsBody">
													<tr id="nodataOutputs">
														<td class="nodata" colspan="8">등록된 산출물이 없습니다.</td>
													</tr>
												</tbody>
												<%
													}
												%>
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
	</table></td>
