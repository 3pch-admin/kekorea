<%@page import="java.math.BigDecimal"%>
<%@page import="e3ps.project.Project"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.project.beans.ProjectViewData"%>
<%@page import="e3ps.project.beans.TemplateViewData"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	// popup check
	String popup = (String) request.getParameter("popup");
	boolean isPopup = Boolean.parseBoolean(popup);

	// data
	ProjectViewData data = (ProjectViewData) request.getAttribute("data");

	// isAdmin
	boolean isAdmin = CommonUtils.isAdmin();
	String oid = data.oid;
	
	ArrayList<Project> refProjectList = (ArrayList<Project>) request.getAttribute("refProjectList");
	
%>

<td valign="top">
<script type="text/javascript">
	$(document).ready(function() {
		var len = "<%=data.description.length()%>";
		$("#descTempCnt").text(len);
		$("input").checks();
		
		$("#kekState").bindSelect();
		
		$("#re").click(function() {
// 			alert($("#machinePrice").val().money());
		})
	})
</script> 
	<input type="hidden" name="oid" id="oid" value="<%=data.oid%>">
	<input type="hidden" name="popup" id="popup" value="<%=isPopup%>">
	
	<table class="btn_table">
		<tr>
			<td>
				<div class="header_title">
					<i class="axi axi-subtitles"></i><span>작번 정보</span>
				</div>
			</td>
			<td>	
			<div class="right">
				<input type="button" value="일정보기" id="viewSchedule" title="일정보기" class="blueBtn">
				<input type="button" value="수정" id="modifyProjectBtn" title="수정" class="blueBtn">
				<%
					if(data.isStand) {
				%>
				<input type="button" value="시작" id="startProject" title="시작" data-oid="<%=data.oid %>">
				<%
					} else if(data.isStart) {
						if(data.kekProgress != 100) {
				%>
				<input type="button" value="중단" id="stopProject" title="중단" data-oid="<%=data.oid %>">
				<%
						} else if(data.kekProgress == 100) {
				%>
				<input type="button" value="완료" id="completeProject" title="완료" data-oid="<%=data.oid %>">
				<%
						}
					} else if(data.isStop) {
				%>
				<input type="button" value="재시작" id="restartProject" title="재시작" data-oid="<%=data.oid %>">
				<%
					}
				%>
				<input type="button" value="닫기" id="closeDocBtn" title="닫기" class="redBtn">
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
					<tr>
						<th class="min-wid150">KEK 작번</th>
						<td class="center" style="border-top: 2px solid #2b85c1;"><%=data.kek_number %></td>
							<th class="min-wid150">거래선</th>
						<td class="center" style="border-top: 2px solid #2b85c1;"><%=data.customer %></td>
						
						<th class="border-none bgnone min-wid20">&nbsp;</th>
						<th rowspan="2" class="border-left min-wid150">진행률/적정</th>
						<td rowspan="2"  class="center min-wid150 border-top-blue "><%=data.kekProgress %>%/<%=data.comp%>%</td>
					</tr>
					
					<tr>
						<th class="min-wid150">고객사 라인</th>
						<td class="center"><%=data.ins_location %></td>
						<th class="min-wid150">막종</th>
						<td class="center"><%=data.mak %></td>
						
							<th class="border-none bgnone min-wid20">&nbsp;</th>
							
					</tr>
					
					<tr>
						<th class="min-wid150">발행일</th>
							<td class="center"><%=data.pDate %></td>
						<th class="min-wid150">요청 납기일</th>
							<td class="center"><%=data.customDate %></td>
							
								<th class="border-none bgnone min-wid20">&nbsp;</th>
									<th class="border-left">기계</th>
							<td class="center"><%=data.machineProgress %>%</td>
					</tr>
					
					<tr>
						<th>KE 작번</th>
						<td class="center"><%=data.ke_number %></td>
						<th>USER ID</th>
						<td class="center"><%=data.userID %>
						
						<th class="border-none bgnone min-wid20">&nbsp;</th>
								<th class="border-left">전기</th>
							<td class="center"><%=data.elecProgress %>%</td>
					</tr>
					<tr>
					<th >작업 내용</th>
						<td class="indent10" colspan="3"><%=data.description %></td>
					</tr>
				</table>
				
				<div class="header_title margin_top10">
					<i class="axi axi-subtitles"></i><span>작번 상세 정보</span>
				</div>
				
				<table class="project_table">
					<tr>
						<th>총기간[공수](일)</th>
						<td class="center" style="border-top: 2px solid #2b85c1;"><%=data.duration %>[<%=data.holiday %>]일</td>
						<th>계획 시작일</th>
						<td class="center" style="border-top: 2px solid #2b85c1;"><%=data.planStartDate %></td>
					</tr>				
					
					<tr>
							<th>계획 종료일</th>
								<td class="center"><%=data.planEndDate %></td>
						<th>실제 시작일</th>
						<td class="center"><%=data.startDate %></td>
					
					</tr>
					<tr>
							<th>실제 종료일</th>
						<td class="center"><%=data.endDate %></td>
							<th>총괄 책임자</th>
							<td class="center"><%=data.pm != null ?  data.pm.getFullName() : "지정안됨" %></td>
					</tr>					
					<tr>
						<th>세부일정 책임자</th>
						<td class="center"><input type="text" class="AXInput wid150"></td>
						<th>기계</th>
						<td class="center"><%=data.machine != null ?  data.machine.getFullName() : "지정안됨" %></td>
						
					</tr>
					<tr>
						<th>전기</th>
						<td class="center"><%=data.elec != null ?  data.elec.getFullName() : "지정안됨" %></td>
						<th>SOFT</th>
						<td class="center"><%=data.soft != null ?  data.soft.getFullName() : "지정안됨" %></td>
					</tr>
		<%-- 		<%
						if(!data.isEditer) {
					%>
					<tr>
						<td class="center"><input type="text" class="AXInput wid150"></td>
						<td class="center"><input type="text" class="AXInput wid150"></td>
						<td class="center"><input type="text" class="AXInput wid150"></td>
						<td class="center"><input type="text" class="AXInput wid150"></td>
						<td class="center"><input type="text" class="AXInput wid150"></td>
					</tr>
					<%
						} else {
					%>
					<tr>
						<td class="center"><%=data.pm != null ?  data.pm.getFullName() : "지정안됨" %></td>
						<td class="center"><input type="text" class="AXInput wid150"></td>
						<td class="center"><%=data.machine != null ?  data.machine.getFullName() : "지정안됨" %></td>
						<td class="center"><%=data.elec != null ?  data.elec.getFullName() : "지정안됨" %></td>
						<td class="center"><%=data.soft != null ?  data.soft.getFullName() : "지정안됨" %></td>
					</tr>					
					<%
						}
					%> --%>
					<%
						// 금액 계산식
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
						<th>작업상태</th>
						<td class="center"><%=data.kekState %></td>
						<th>진행상태</th>
						<td class="center"><%=data.state %></td>
					</tr>
					<tr>
						<th>작번 견적 금액</th>
						<td class="center">
						<%=outputTotal %>원<font color="red">(수배표)&nbsp;</font>/&nbsp;<%=inputTotal %>원<font color="blue">(입력)</font>&nbsp;/&nbsp;<%=tPgoress %>%</td>
						<th>기계 견적 금액</th>
							<td class="center" id="totalMachincePartListBtn" data-oid="<%=data.oid %>" data-eng="기계">
						<%=outputMachine %>원<font color="red">(수배표)&nbsp;</font>/&nbsp;<%=inputOutputMachine %>원<font color="blue">(입력)</font>&nbsp;/&nbsp;<%=mProgress %>%</td>
					</tr>
					<tr>
						<th>전기 견적 금액</th>
						<td colspan="3" class="center"  id="totalElecPartListBtn" data-oid="<%=data.oid %>" data-eng="전기">
						<%=outputElec %>원<font color="red">(수배표)&nbsp;</font>/&nbsp;<%=inputOutputElec%>원<font color="blue">(입력)</font>&nbsp;/&nbsp;<%=eProgress %>%</td>
					</tr>
							<%-- <%
						if(data.isEditer) {
					%>
					
					<tr>
						<td class="center">
							<select name="kekState" id="kekState" class="AXSelect wid150">
								<option value="설계 중">설계 중</option>
								<option value="설계완료">설계 완료</option>
								<option value="작업 완료">작업 완료</option>
								<option value="홀딩">홀딩</option>
								<option value="취소">취소</option>
							</select>
						</td>
						<td class="center"><%=data.state %></td>
						<td class="center"><%=outputTotal %>원<font color="red">(수배표)&nbsp;</font>/&nbsp;<%=inputTotal %>원<font color="blue">(입력)</font>&nbsp;/&nbsp;<%=tPgoress %>%</td>
						<td class="center"><%=outputMachine %>원<font color="red">(수배표)&nbsp;</font>/&nbsp;<input value="<%=inputOutputMachine %>" type="text" name="machinePrice" id="machinePrice" class="AXInput wid100">&nbsp;/&nbsp;<%=mProgress %>%</td>
						<td class="center"><%=outputElec %>원<font color="red">(수배표)&nbsp;</font>/&nbsp;<input value="<%=inputOutputElec %>" type="text" name="elecPrice" id="elecPrice" class="AXInput wid100">&nbsp;/&nbsp;<%=eProgress %>%</td>
					</tr>
					<%
						// 수정 권한이 없는
						} else {
					%>
					<tr>
						<td class="center"><%=data.kekState %></td>
						<td class="center"><%=data.state %></td>
						<td class="center">
						<%=outputTotal %>원<font color="red">(수배표)&nbsp;</font>/&nbsp;<%=inputTotal %>원<font color="blue">(입력)</font>&nbsp;/&nbsp;<%=tPgoress %>%</td>
						<td class="center" id="totalMachincePartListBtn" data-oid="<%=data.oid %>" data-eng="기계">
						<%=outputMachine %>원<font color="red">(수배표)&nbsp;</font>/&nbsp;<%=inputOutputMachine %>원<font color="blue">(입력)</font>&nbsp;/&nbsp;<%=mProgress %>%</td>
						<td class="center"  id="totalElecPartListBtn" data-oid="<%=data.oid %>" data-eng="전기">
						<%=outputElec %>원<font color="red">(수배표)&nbsp;</font>/&nbsp;<%=inputOutputElec%>원<font color="blue">(입력)</font>&nbsp;/&nbsp;<%=eProgress %>%</td>						
					</tr>					
					<%
						}
					%> --%>
				</table>
				
				<div class="header_title margin_top10">
					<i class="axi axi-subtitles"></i><span>참조 작번 정보</span>
				</div>		
			
				<table class="project_table">
					<thead>
						<tr>
							<th class="min-wid150">작번 유형</th>
							<th class="min-wid300">작업 내용</th>
							<th class="min-wid150">발생일</th>
							<th class="min-wid150">요청납기일</th>
						</tr>
					</thead>
					<%
						for(Project refProject : refProjectList) {
							ProjectViewData refData = new ProjectViewData(refProject);
					%>
					<tr>
						<td class="center"><%=refData.pType %></td>
						<td class="indent10"><%=refData.description %></td>
						<td class="center"><%=refData.createDate %></td>
						<td class="center"><%=refData.customDate %></td>
					</tr>
					<%
						}
						if(refProjectList.size() == 0) {
					%>
					<tr>
						<td class="nodata" colspan="4">관련 작번이 없습니다.</td>
					</tr>
					<%
						}
					%>
				</table>
					
				<div class="header_title margin_top10">
					<i class="axi axi-subtitles"></i><span>특이 사항 정보</span>
				</div>	
									
				<table class="project_table">
					<tr>
						<td colspan="5" class="border-top-blue">
						<table class="project_in_btn_table">
							<tr>
								<td class="add"><input type="button" value="특이사항 등록"
									title="특이사항 추가" id="addIssue" data-dbl="true">
									<input type="button" value="링크등록"
									title="특이사항 추가" id="linkIssue" data-dbl="true">
									 <input
									type="button" value="특이사항 삭제" title="특이사항 삭제" id="delIssue"
									class="blueBtn"></td>
							</tr>
						</table>
							<table id="tblBackground">
								<tr>
									<td class="nonBorder">
										<div id="users_container">
											<table class="create_project_table_in fix_table">
												<colgroup>
													<col width="40">
													<col width="100">
													<col width="*">
													<col width="100">
													<col width="100">
													<col width="100">
												</colgroup>
												<thead>
													<tr>
														<th><input type="checkbox" name="allParts"
															id="allParts"></th>
														<th>번호</th>
														<th>내용</th>
														<th>작성자</th>
														<th>작성일</th>
														<th>첨부파일</th>
													</tr>
												</thead>
												<tbody id="addUsersBody">
													<tr id="nodataUsers">
														<td class="nodata" colspan="6">등록된 특이사항이 없습니다.</td>
													</tr>
												</tbody>
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