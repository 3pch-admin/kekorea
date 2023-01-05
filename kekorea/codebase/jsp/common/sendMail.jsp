<%@page import="e3ps.approval.service.ApprovalHelper"%>
<%@page import="e3ps.approval.beans.ApprovalMasterViewData"%>
<%@page import="e3ps.approval.beans.ApprovalLineViewData"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	WTUser user = (WTUser) request.getAttribute("user");
	ApprovalMasterViewData data = (ApprovalMasterViewData) request.getAttribute("data");
%>
<td valign="top"><script type="text/javascript">
	$(document).ready(function() {

		$("input[name=name]").focus();

		$("#sendBtn").click(function() {
			var dialogs = $(document).setOpen();
			$name = $("input[name=name]");
			if ($name.val() == "") {
				dialogs.alert({
					theme : "alert",
					title : "메일 제목 미입력",
					msg : "메일 제목을 입력하세요."
				}, function() {
					if (this.key == "ok") {
						$name.focus();
					}
				})
				return false;
			}

			$description = $("#description")
			if ($description.val() == "") {
				dialogs.alert({
					theme : "alert",
					title : "메일 내용 미입력",
					msg : "메일 내용을 입력하세요."
				}, function() {
					if (this.key == "ok") {
						$description.focus();
					}
				})
				return false;
			}

			dialogs.confirm({
				theme : "info",
				title : "메일 전송",
				msg : "메일을 전송하시겠습니까?"
			}, function() {
				if (this.key == "ok") {
					var url = "/Windchill/plm/common/sendMailAction";
					var params = new Object();
					params = $(document).getFormData(params);
					$(document).ajaxCallServer(url, params, function(data) {
						dialogs.alert({
							theme : "alert",
							title : "결과",
							msg : data.msg
						}, function() {
							if (this.key == "ok") {
								self.close();
							}
						})
					}, true);
				}
			})
		})
	})
</script>
	<table class="btn_table top_margin0">
		<tr>
			<td class="left">
				<div class="header_title pos5">
					<i class="axi axi-subtitles"></i><span>메일 전송</span>
				</div>
			</td>
			<td class="right"><input type="button" value="전송" title="전송" id="sendBtn" class=""> <input type="button" value="닫기 (C)" title="닫기 (C)" id="closeBtn" class="redBtn"></td>
		</tr>
	</table>

	<table class="create_table">
		<colgroup>
			<col width="200">
			<col width="400">
			<col width="200">
			<col width="400">
		</colgroup>
		<tr>
			<th>보내는 사람</th>
			<td><%=user.getFullName()%>&nbsp;(<%=user.getEMail() != null ? user.getEMail() : ""%>) <input type="hidden" name="from" value="<%=user.getEMail() != null ? user.getEMail() : ""%>"></td>
			<th>받는 사람</th>
			<td><%=data.ingUser.getFullName()%>&nbsp;(<%=data.ingUser.getEMail() != null ? data.ingUser.getEMail() : ""%>) <input type="hidden" name="toMail"
				value="<%=data.ingUser.getEMail() != null ? data.ingUser.getEMail() : ""%>"></td>
		</tr>
		<tr>
			<th>메일 제목<font class="req">*</font></th>
			<td colspan="3"><input type="text" class="AXInput wid300" name="name" id="name"></td>
		</tr>
		<tr>
			<th>내용<font class="req">*</font><br> <span class="cnt">0</span>/4000
			</th>
			<td colspan="3"><textarea class="AXTextarea" id="description" name="description" rows="3" cols=""></textarea></td>
		</tr>
	</table></td>