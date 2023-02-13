<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@page import="e3ps.project.task.Task"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.project.template.beans.TemplateViewData"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
TemplateViewData data = (TemplateViewData) request.getAttribute("data");
ArrayList<Task> list = (ArrayList<Task>) request.getAttribute("list");
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<%@include file="/jsp/common/layouts/include_css.jsp"%>
<%@include file="/jsp/common/layouts/include_script.jsp"%>
</head>
<body style="margin: 0 auto;">
	<table class="btn_table">
		<tr>
			<td>
				<div class="header_title">
					<i class="axi axi-subtitles"></i>
					<span>템플릿 정보</span>
				</div>
			</td>
		</tr>
	</table>
	<table class="view_table">
		<colgroup>
			<col width="130">
			<col width="600">
			<col width="130">
			<col width="600">
		</colgroup>
		<tr>
			<th>템플릿 이름</th>
			<td><%=data.getName()%></td>
			<th>총 기간</th>
			<td><%=data.getDuration()%>일
			</td>
		</tr>
		<tr>
			<th>작성자</th>
			<td><%=data.getCreator()%></td>
			<th>작성일</th>
			<td><%=data.getCreatedDate()%></td>
		</tr>
		<tr>
			<th>설명</th>
			<td colspan="3">
				<textarea style="height: 200px !important;" class="view" readonly="readonly"><%=data.getDescription()%></textarea>
			</td>
		</tr>
	</table>

	<div class="header_title margin_top10">
		<i class="axi axi-subtitles"></i>
		<span>태스크 정보</span>
	</div>

	<table class="view_table">
		<tr>
			<th>태스크 명</th>
			<th>태스크 타입</th>
			<th>기간</th>
			<th>할당율</th>
			<th>계획시작일</th>
			<th>계획종료일</th>
		</tr>
		<%
		for (Task task : list) {
		%>
		<tr>
			<td class="left indent10 bl"><%=task.getName()%></td>
			<td class="center"><%=task.getTaskType().getName()%></td>
			<td class="center"><%=task.getDuration()%>일
			</td>
			<td class="center"><%=task.getAllocate() != null ? task.getAllocate() : ""%></td>
			<td class="center"><%=CommonUtils.getPersistableTime(task.getPlanStartDate())%></td>
			<td class="center"><%=CommonUtils.getPersistableTime(task.getPlanEndDate())%></td>
		</tr>
		<%
		}
		%>
	</table>

	<div class="header_title margin_top10">
		<i class="axi axi-subtitles"></i>
		<span>구성원 정보</span>
	</div>

	<table class="view_table">
		<colgroup>
			<col width="50%">
			<col width="50%">
		</colgroup>
		<tr>
			<th>총괄 책임자</th>
			<th>세부일정 책임자</th>
		<tr>
			<td class="center bl">
				<%
				if (!isAdmin) {
				%>
				<%=data.getPm() != null ? data.getPm() : ""%>
				<%
				} else {
				String value = data.getPm() != null ? data.getPm().getFullName() : "";
				String poid = data.getPm() != null ? data.getPm().getPersistInfo().getObjectIdentifier().getStringValue() : "";
				%>
				<input type="text" name="pm" id="pm" class="AXInput wid200" data-dbl="true" value="<%=value%>">
				<input type="hidden" name="pmOid" value="<%=poid%>" id="pmOid">
				<i title="삭제" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="pm"></i>
				<img src="/Windchill/jsp/images/save.gif" style="position: relative; left: 6px; top: 4px; cursor: pointer;" class="saveBtn">
				<%
				}
				%>
			</td>
			<td class="center">
				<%
				if (!isAdmin) {
				%>
				<%=data.getSubPm() != null ? data.getSubPm() : ""%>
				<%
				} else {
				String value = data.getSubPm() != null ? data.getSubPm().getFullName() : "";
				String poid = data.getSubPm() != null ? data.getSubPm().getPersistInfo().getObjectIdentifier().getStringValue() : "";
				%>
				<input type="text" name="sub_pm" id="sub_pm" class="AXInput wid200" data-dbl="true" value="<%=value%>">
				<input type="hidden" name="sub_pmOid" value="<%=poid%>" id="sub_pmOid">
				<i title="삭제" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="sub_pm"></i>
				<img src="/Windchill/jsp/images/save.gif" style="position: relative; left: 6px; top: 4px; cursor: pointer;" class="saveBtn">
				<%
				}
				%>
			</td>
		</tr>
	</table>
	<script type="text/javascript">
		$(function() {
			$(".saveBtn").click(function() {
				if (!confirm("저장 하시겠습니까?")) {
					return false;
				}
				let url = getCallUrl("/template/saveUserLink");
				let params = new Object();
				params.pmOid = $("#pmOid").val();
				params.sub_pmOid = $("#sub_pmOid").val();
				params.oid = "<%=data.getOid()%>";
				call(url, params, function(data) {
					alert(data.msg);
					if (data.result) {
						document.location.reload();
					} else {
						parent.closeLayer();
					}
				}, "POST");
			})

			$("input[name=pm]").add("input[name=sub_pm]").bindSelector({
				reserveKeys : {
					options : "list",
					optionValue : "value",
					optionText : "name"
				},
				onsearch : function(id, objVal, cbm) {
					let value = $("#" + id).val();
					let params = new Object();
					params.value = value;
					console.log(params);
					let url = getCallUrl("/org/getUserBind");
					call(url, params, function(data) {
						cbm({
							options : data.list
						})
					}, "POST")
				},
				onchange : function() {
					let id = this.targetID;
					let target = id + "Oid";
					$("#" + target).remove();
					$("#" + id).before("<input type=\"hidden\" name=\"" + target + "\" id=\"" + target + "\"> ");
					$("#" + target).val(this.selectedOption.value);
				},
				finder : function() {

				}
			})
		})
	</script>
</body>
</html>