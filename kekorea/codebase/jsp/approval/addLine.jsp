<%@page import="e3ps.org.service.OrgHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String lineType = (String) request.getParameter("lineType");
	boolean isSeries = lineType.equals("series");
%>
<td valign="top"><script type="text/javascript">
	$(document).ready(function() {
		$("input").checks();
<%if (isSeries) {%>
	$(".parallel_table").hide();
<%} else {%>
	$(".series_table").hide();
<%}%>
	$("#receive_table").tableHeadFixer();

		$("#agree_table").tableHeadFixer();

		$("#approval_table").tableHeadFixer();

		$key = $("#key").focus();

		$("#searchBtn").click(function() {
			$(document).searchLine();
		})

		$("#dept").fancytree({
			icon : function(event, data) {
				if (data.node.isFolder()) {
					return "my-group-icon-class";
				}
			},
			click : function(e, data) {
				$(".deptNameView").text(data.node.title);
				var oid = data.node.data.id;
				var url = "/Windchill/plm/org/getUserForDept"
				var params = new Object();
				params.oid = oid;

				$(document).ajaxCallServer(url, params, function(data) {
					$(document).setUserForDept(data);
				}, false);
			},
			source : $.ajax({
				url : "/Windchill/plm/org/getDeptTree",
				type : "POST"
			})
		});

		var h = $("#userList").height();
		$("ul.fancytree-container").css("height", h - 200);
	}).keypress(function(e) {
		var keyCode = e.keyCode;
// 		alert(keyCode);
		
		if(keyCode == 68) {
			// 개인결재선 삭제
		} else if(keyCode == 65) {
			// 개인결재선 적용
		}
		
	})
</script>
	<table class="btn_table top_margin0">
		<tr>
			<td class="left"><input type="hidden" name="type" value="<%=lineType%>">
				<div class="header_title pos5">
					<i class="axi axi-subtitles"></i><span>결재선 지정</span>
				</div></td>
			<td class="right"><input type="button" value="적용" title="적용" id="applyLineBtn" class=""> <input type="button" value="닫기" title="닫기 (C)" id="closeBtn" onclick="self.close();" class="redBtn"></td>
		</tr>
	</table>

	<div class="line_layer">
		<!-- 전체 테이블 -->
		<table id="line_main_table">
			<colgroup>
				<col width="330">
				<col width="120">
				<col width="580">
			</colgroup>
			<tr>
				<td valign="top" class="line_top_td">
					<table id="org_left_table">
						<tr>
							<td class="strong app_org">조직도</td>
							<td class="app_search">사용자검색</td>
							<td class="app_user_line">개인결재선</td>
						</tr>
						<tr>
							<td colspan="3">
								<div id="dept"></div>
							</td>
						</tr>
						<tr>
							<td colspan="3" class="deptTd"><i class="axi axi-subtitles deptIcon"></i><span class="deptNameView"><%=OrgHelper.manager.getRoot().getName()%></span></td>
						</tr>
						<tr>
							<td colspan="3"><select class="AXSelect" name="userDeptList" id="userDeptList" multiple="multiple">
							</select></td>
						</tr>
					</table>

					<table id="line_left_table">
						<tr>
							<td class="app_org">조직도</td>
							<td class="strong app_search">사용자검색</td>
							<td class="app_user_line">개인결재선</td>
						</tr>
						<tr>
							<td colspan="3"><span class="input">아이디 또는 이름 <span class="pos1">:</span></span> <input type="text" class="AXInput wid100" name="key" id="key"> <input id="searchBtn" type="button"
								value="조회" title="조회" class="pos1"></td>
						</tr>
						<tr>
							<td colspan="3"><select class="AXSelect" name="userList" id="userList" multiple="multiple">
							</select></td>
						</tr>
					</table>

					<table id="user_left_table">
						<tr>
							<td class="app_org">조직도</td>
							<td class="app_search">사용자검색</td>
							<td class="strong app_user_line">개인결재선</td>
						</tr>
						<tr>
							<td colspan="3"><span class="input">결재선 이름 <span class="pos1">:</span></span> <input type="text" class="AXInput wid100" name="lineName" id="lineName"> <input id="lineSearch"
								type="button" value="조회" title="조회" class="pos1"></td>
						</tr>
						<tr>
							<td colspan="3"><select class="AXSelect" name="userLineList" id="userLineList" multiple="multiple">
							</select></td>
						</tr>
					</table>
				</td>
				<td>
					<table class="type_table">
						<tr>
							<td>결재타입</td>
						</tr>
						<tr>
							<td><label title="결재"><input value="app" name="appType" type="radio" checked="checked">결재</label></td>
						</tr>
						<tr>
							<td><label title="검토"><input value="agree" name="appType" type="radio">검토</label></td>
						</tr>
						<tr>
							<td><label title="수신"><input value="receive" name="appType" type="radio">수신</label></td>
						</tr>
						<tr>
							<td class="border_none pad-5 org_btn"><input type="button" title="추가" value="추가" id="addOrgLineBtn"> <input type="button" title="삭제" value="삭제" id="deleteLine"
								class="redBtn"></td>
							<td class="border_none pad-5 line_btn none"><input type="button" title="추가" value="추가" id="addLineBtn"> <input type="button" title="삭제" value="삭제" id="deleteLines"
								class="redBtn"></td>
						</tr>
						<tr>
							<td class="border_none pad-5"><input type="button" id="resetAll" title="전체삭제" value="전체삭제" class="blueBtn"></td>
						</tr>
					</table> <br>
					<table class="selectTable">
						<tr>
							<td>결재방법</td>
						</tr>
						<tr>
							<td><label title="직렬"><input value="series" name="lineType" type="radio" <%if (isSeries) {%> checked="checked" <%} else {%> disabled="disabled" <%}%>>직렬</label></td>
						</tr>
						<tr>
							<td><label title="병렬"><input value="parallel" name="lineType" type="radio" <%if (!isSeries) {%> checked="checked" <%} else {%> disabled="disabled" <%}%>>병렬</label></td>
						</tr>
					</table>
					<table class="line_btn_table">
						<tr>
							<td class="border_none pad-5"><input type="button" title="개인결재선 저장" value="개인결재선 저장" id="saveBtn"></td>
						</tr>
						<tr>
							<td class="border_none pad-5"><input type="button" title="삭제" value="삭제" id="deleteLineBtn" class="redBtn"></td>
						</tr>
						<tr>
							<td class="border_none pad-5"><input type="button" id="applyUserLineBtn" title="결재선 적용" value="결재선 적용" class="blueBtn"></td>
						</tr>
					</table>
				</td>
				<td valign="top">
					<table class="app_layer_table">
						<tr>
							<td>
								<div class="header_title">
									<i class="axi axi-subtitles"></i><span>결재라인</span>
								</div>
							</td>
						</tr>
						<tr>
							<td>
								<div class="line_layer_view">
									<table class="line_table_in series_table" id="approval_table">
										<colgroup>
											<col width="40">
											<col width="60">
											<col width="*">
											<col width="120">
											<col width="130">
											<col width="130">
										</colgroup>
										<thead>
											<tr>
												<th><input type="checkbox" name="allApp"></th>
												<th>순서</th>
												<th>이름</th>
												<th>아이디</th>
												<th>직급</th>
												<th>부서</th>
											</tr>
										</thead>
										<tbody id="appBody_series">
											<tr id="nodataTr_app_series">
												<td class="app_nodata_icon" colspan="6"><a class="axi axi-info-outline"></a> <span>지정된 결재라인이 없습니다.</span></td>
											</tr>
										</tbody>
									</table>

									<table class="line_table_in parallel_table" id="approval_table">
										<colgroup>
											<col width="40">
											<col width="*">
											<col width="120">
											<col width="130">
											<col width="130">
										</colgroup>
										<thead>
											<tr>
												<th><input type="checkbox" name="allApp"></th>
												<th>이름</th>
												<th>아이디</th>
												<th>직급</th>
												<th>부서</th>
											</tr>
										</thead>
										<tbody id="appBody_parallel">
											<tr id="nodataTr_app_parallel">
												<td class="app_nodata_icon" colspan="5"><a class="axi axi-info-outline"></a> <span>지정된 결재라인이 없습니다.</span></td>
											</tr>
										</tbody>
									</table>
								</div>
							</td>
						</tr>
					</table>

					<table class="app_layer_table">
						<tr>
							<td>
								<div class="header_title">
									<i class="axi axi-subtitles"></i><span>검토라인</span>
								</div>
							</td>
						</tr>
						<tr>
							<td>
								<div class="line_layer_view">
									<table class="line_table_in" id="agree_table">
										<colgroup>
											<col width="40">
											<col width="*">
											<col width="120">
											<col width="130">
											<col width="130">
										</colgroup>
										<thead>
											<tr>
												<th><input type="checkbox" id="allAgree"></th>
												<th>이름</th>
												<th>아이디</th>
												<th>직급</th>
												<th>부서</th>
											</tr>
										</thead>
										<tbody id="agreeBody">
											<tr id="nodataTr_agree">
												<td class="app_nodata_icon" colspan="5"><a class="axi axi-info-outline"></a> <span>지정된 검토라인이 없습니다.</span></td>
											</tr>
										</tbody>
									</table>
								</div>
							</td>
						</tr>
					</table>

					<table class="app_layer_table">
						<tr>
							<td>
								<div class="header_title">
									<i class="axi axi-subtitles"></i><span>수신라인</span>
								</div>
							</td>
						</tr>
						<tr>
							<td>
								<div class="line_layer_view">
									<table class="line_table_in" id="receive_table">
										<colgroup>
											<col width="40">
											<col width="*">
											<col width="120">
											<col width="130">
											<col width="130">
										</colgroup>
										<thead>
											<tr>
												<th><input type="checkbox" id="allReceive"></th>
												<th>이름</th>
												<th>아이디</th>
												<th>직급</th>
												<th>부서</th>
											</tr>
										</thead>
										<tbody id="receiveBody">
											<tr id="nodataTr_receive">
												<td class="app_nodata_icon" colspan="5"><a class="axi axi-info-outline"></a> <span>지정된 수신라인이 없습니다.</span></td>
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
	</div></td>