<%@page import="e3ps.epm.dto.EpmViewData"%>
<%@page import="wt.epm.EPMDocument"%>
<%@page import="wt.fc.Persistable"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="e3ps.approval.beans.ApprovalLineViewData"%>
<%@page import="e3ps.workspace.ApprovalLine"%>
<%@page import="e3ps.workspace.ApprovalContract"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	String oid = (String) request.getAttribute("oid");
	String moid = (String) request.getAttribute("moid");
	ReferenceFactory rf = new ReferenceFactory();
	ApprovalContract cc = (ApprovalContract) rf.getReference(oid).getObject();
	ApprovalLine line = (ApprovalLine) rf.getReference(moid).getObject();
	ApprovalLineViewData data = new ApprovalLineViewData(line);
%>
<td valign="top">
	<!-- script area --> <input type="hidden" name="oid" value="<%=oid%>">
	<input type="hidden" name="moid" value="<%=moid%>"> <script
		type="text/javascript">
		$(document).ready(function() {
			// init AXUpload5
			$("input").checks();

			$(".documents_add_table").tableHeadFixer();
		})
	</script> <!-- create header title -->


	<table class="btn_table">
		<tr>
			<td>
				<div class="header_title">
					<i class="axi axi-subtitles"></i><span>도면 결재</span>
					<!-- req msg -->
					<font class="reqMsg">(빨간색 속성 값은 필수 입력 값입니다.)</font>
				</div>
			</td>
			<td class="right"><input type="button" value="수정"
				id="modifyEpmAppBtn" title="수정"> <input type="button"
				value="뒤로" id="backBtn" title="뒤로" class="blueBtn"></td>
		</tr>
	</table> <!-- 	<table class="create_table"> -->
	<table class="approval_table">
		<!-- colgroup -->
		<colgroup>
			<col width="150">
			<col>
		</colgroup>
		<tr>
			<th><font class="req">결재 제목</font></th>
			<td><input type="text" name="name" id="name"
				class="AXInput wid400" value="<%=cc.getName()%>"></td>
		</tr>
		<tr>
			<th><font class="req">결재 도면</font></th>
			<td colspan="3">
				<table class="in_btn_table">
					<tr>
						<td class="add"><input type="button" title="도면 추가"
							value="도면 추가" id="addEpms" data-context="product" data-dbl="true"
							data-state="INWORK"> <input type="button" title="도면 삭제"
							value="도면 삭제" id="delEpms" class="blueBtn"></td>
					</tr>
				</table>
				<table id="tblBackground">
					<tr>
						<td>
							<div id="epms_container">
								<table class="create_table_in documents_add_table fix_table">
									<colgroup>
										<col width="40">
										<col width="300">
										<col width="300">
										<col width="300">
										<col width="80">
										<col width="80">
										<col width="100">
										<col width="130">
									</colgroup>
									<thead>
										<tr>
											<th><input type="checkbox" name="allEpms" id="allEpms">
											</th>
											<th>NAME</th>
											<th>DWG_NO</th>
											<th>NAME_OF_PARTS</th>
											<th>버전</th>
											<th>상태</th>
											<th>작성자</th>
											<th>수정자</th>
										</tr>
									</thead>
									<tbody id="addEpmsBody">
										<%
											QueryResult result = data.result;
											while (result.hasMoreElements()) {
												Persistable per = (Persistable) result.nextElement();
												EPMDocument epm = (EPMDocument) per;
												EpmViewData dd = new EpmViewData(epm);
										%>
										<tr>
											<td rowspan="2"><input type="checkbox" name="epmOid"
												value="<%=dd.oid%>" class="isBox" style="display: none;">
											</td>
											<td class="infoEpms left" data-oid="<%=dd.oid%>"><img
												class="pos3" src="<%=dd.iconPath%>">&nbsp;<%=dd.name%></td>
											<td class="infoEpms" data-oid="<%=dd.oid%>"><%=dd.number%></td>
											<td><%=dd.name_of_parts%></td>
											<td><%=dd.version%></td>
											<td><%=dd.state%></td>
											<td><%=dd.creator%></td>
											<td><%=dd.createDate%></td>
										</tr>
										<tr class="<%=dd.oid%>">
											<td colspan="7" class="inputTd indent10 left"><input
												value="<%=dd.description%>" type="text" name="description"
												class="AXInput widMax"></td>
										</tr>
										<%
											}
										%>
										<tr id="nodataEpms">
											<td class="nodata" colspan="8" <%if (result.size() > 0) {%>
												style="display: none;" <%}%>>결재 도면이 없습니다.</td>
										</tr>
									</tbody>
								</table>
							</div>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<th>결재의견</th>
			<td colspan="3">
				<!-- rows 세로, cols 가로 --> <textarea class="AXTextarea"
					name="comment" id="comment" rows="3" cols=""><%=cc.getDescription()%></textarea>
			</td>
		</tr>
	</table>
</td>