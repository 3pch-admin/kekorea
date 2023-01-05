<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<td valign="top"><script type="text/javascript">
	$(document).ready(function() {
		upload.pageStart(null, null, "primary");

		$("#uploadBtn").click(function() {
			$(document).uploadExcel();
		})

		$.fn.uploadExcel = function() {
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
			
			var primaryContent = primary.getUploadedList("object")[0];
			if (!primaryContent) {
				dialogs.alert({
					theme : "alert",
					title : "주 첨부파일 미선택",
					msg : "주 첨부파일은 선택하세요."
				})
				return false;
			}

			var box = $(document).setNonOpen();
			box.confirm({
				theme : "info",
				title : "속성 업로드",
				msg : "속성 업로드를 진행하시겠습니까?"
			}, function() {
				if (this.key == "ok") {
					var url = "/Windchill/plm/epm/uploadEpmAttrAction";
					var params = new Object();
					// 결재??
					params = $(document).getFormData(params);
					// 추가 속성
					$(document).ajaxCallServer(url, params, function(data) {
						dialogs.alert({
							theme : "alert",
							title : "결과",
							msg : data.msg
						}, function() {
							if (this.key == "ok") {
								document.location.href = data.url;
							}
						})
					}, true);
				} else if (this.key == "cancel" || this.state == "close") {
					mask.close();
				}
			})
		}

	}).keypress(function(e) {
		var keyCode = e.keyCode;

		if (keyCode == 85) {
			$(document).uploadExcel();
		}
	})
</script> <!-- search table... -->
	<div class="header_title">
		<i class="axi axi-subtitles"></i><span>속성입력</span>
	</div>
	<table class="search_table">
		<colgroup>
			<col width="200">
			<col width="*">
		</colgroup>
		<tr>
			<th><font class="req">주 첨부파일</font></th>
			<td colspan="3">
				<div class="AXUpload5" id="primary_layer"></div>
			</td>
		</tr>
		<tr>
			<th>엑셀양식 가공품(Part)</th>
			<td><a href="/Windchill/jsp/epm/inputAttr.xls"> <img src="/Windchill/jsp/images/fileicon/file_excel.gif" class="pos2"></a></td>
		</tr>
		<tr>
			<th>엑셀양식 가공품(Assy)</th>
			<td><a href="/Windchill/jsp/epm/inputAttr.xls"> <img src="/Windchill/jsp/images/fileicon/file_excel.gif" class="pos2"></a></td>
		</tr>
		<tr>
			<th>엑셀양식 가공품</th>
			<td><a href="/Windchill/jsp/epm/inputAttr.xls"> <img src="/Windchill/jsp/images/fileicon/file_excel.gif" class="pos2"></a></td>
		</tr>
	</table>
	<table class="btn_table">
		<tr>

			<td class="center"><input type="button" value="업로드 (U)" class="blueBtn" id="uploadBtn" title="업로드 (U)"> <input type="button" value="뒤로 (B)" class="" id="backBtn" title="뒤로 (B)">
		</tr>
	</table></td>