<%@page import="e3ps.part.service.PartHelper"%>
<%@page import="e3ps.epm.beans.PRODUCTAttr"%>
<%@page import="e3ps.common.util.IBAUtils"%>
<%@page import="e3ps.epm.beans.CADAttr"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String root = PartHelper.ELEC_ROOT;
%>
<script type="text/javascript">
	$(document).ready(function() {

		upload.pageStart(null, null, "primary");

		$("#createBtn").click(function() {

			var dialogs = $(document).setOpen();

			var primaryContent = primary.getUploadedList("object")[0];
			if (!primaryContent) {
				dialogs.alert({
					theme : "alert",
					title : "엑셀파일 미선택",
					msg : "엑셀파일을 선택하세요."
				})
				return false;
			}

			dialogs.confirm({
				theme : "info",
				title : "전장품 부품 등록",
				msg : "전장품 부품을 등록하시겠습니까?"
			}, function() {
				if (this.key == "ok") {

					// 					
					var url = "/Windchill/plm/part/createElecPartAction";
					var params = new Object();

					params = $(document).getFormData(params);
					// 추가 속성
					params.location = $("#location").text();
					$(document).ajaxCallServer(url, params, function(data) {
						dialogs.alert({
							theme : "alert",
							title : "결과",
							msg : data.msg,
							width : 350
						}, function() {
							if (this.key == "ok") {
								document.location.href = data.url;
							}
						})
					}, true);
				}
			})
		})
	})
</script>
<td valign="top">
	<div class="header_title">
		<i class="axi axi-subtitles"></i><span>전장품 부품 등록</span>
	</div>
	<table class="create_table">
		<colgroup>
			<col width="200">
			<col width="*">
		</colgroup>
		<tr>
			<th>전장품 부품분류</th>
			<td colspan="3"><span class="location" id="location"><%=root%></span>&nbsp;&nbsp; <span class="gray button small"> <span data-popup="true" data-context="PRODUCT" data-root="<%=root%>"
					title="분튜선택" class="openLoc">분튜선택</span></span> <span></span></td>
		</tr>
		<tr>
			<th>엑셀파일<font class="req">*</font></th>
			<td colspan="3">
				<div class="AXUpload5" id="primary_layer"></div>
			</td>
		</tr>
		<tr>
			<th>전장품 부품 엑셀 양식</th>
			<td colspan="3"><a title="전장품 부품 엑셀 양식 다운로드" href="/Windchill/jsp/loadFiles/part/elecForm.xls"><img class="pos3" src="/Windchill/jsp/images/fileicon/file_excel.gif"> <font color="blue">전장품 부품
						엑셀 양식을 다운로드 받아서 작성 후 업로드 하세요.</font> </a></td>
		</tr>
	</table>
	<table class="btn_table">
		<tr>
			<td class="center"><input class="" type="button" value="등록" id="createBtn" title="등록"> <input type="button" value="뒤로 (B)" id="backBtn" class="blueBtn" title="뒤로 (B)"></td>
		</tr>
	</table>
</td>