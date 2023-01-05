<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<script type="text/javascript">
	$(document).ready(function() {
		$("#addBtn").windowpopup({
			href : "/Windchill/plm/epm/addEpm",
			width : 1200,
			height : 600,
			scrollbars : "yes"
		})
		$("input").checks();

		$("#printDrw").click(function() {

			var dialogs = $(document).setConfrimConfig();
			dialogs.confirm({
				title : "도면출력",
				msg : "선택한 도면들을 출력하시겠습니까?"
			}, function() {
				if (this.key == "ok") {
					var url = "/Windchill/plm/epm/printDrw";
					// checkbox 미체크 일 경우에도 가져옴
					var params = $(document).getListData();
					$(document).ajaxCallServer(url, params, function(data) {
						var okDialog = $(document).setOkConfig();
						okDialog.alert({
							msg : data.msg
						}, function() {
							if (this.key == "ok") {
								document.location.href = data.url;
							}
						})
					}, false);
				}
			})
		})

		$("#deleteBtn").click(function() {
			var check = $(document).isCheck();
			var dialogs = $(document).setOpen();
			if (check == false) {
				dialogs.alert({
					title : "삭제도면 미선택",
					msg : "삭제할 도면을 선택하세요."
				})
				return false;
			}

			$list = $("input[name=oid]");
			$.each($list, function(idx) {
				if ($list.eq(idx).prop("checked") == true) {
					$list.eq(idx).parent().parent().remove();
				}
			})
			var list = $("input[name=oid]");
			if (list.length == 0) {
				var body = $("#addDrwBody");
				var html;
				html += "<tr id=\"nodataTr\">";
				html += "<td class=\"nodata\" colspan=\"7\">출력할 도면을 선택하세요.</td>";
				html += "</tr>";
				body.append(html);
				$("input[name=all]").prop("checked", false);
				$(".ico-checkbox").removeClass("sed");
			}
		})

		$(document).on("click", ".addTag", function() {
			$(this).find(".ico-checkbox").toggleClass("sed");
			$(this).find("input[name=oid]").prop("checked", true);
		})
	})

	function addDrw(list) {
		var len = list.length;
		$("#nodataTr").remove();
		var html;
		var body = $("#addDrwBody");
		for (var i = 0; i < len; i++) {
			html += "<tr>";
			html += "<td class=\"addTag\"><input style=\"display: none;\" type=\"checkbox\" id=\"oid\" name=\"oid\" value=\"" + list[i][0] + "\"><div class=\"ico-checkbox helper-checks helper-checks-checkbox-oid\"></div></td>";
			html += "<td>" + list[i][1] + "</td>";
			html += "<td class=\"left indent10\">" + list[i][2] + "</td>";
			html += "<td>" + list[i][3] + "</td>";
			html += "<td>" + list[i][4] + "</td>";
			html += "<td>" + list[i][5] + "</td>";
			html += "<td>" + list[i][6] + "</td>";
			html += "</tr>";
		}
		body.append(html);
	}
</script>
<td valign="top">
	<!-- search table... -->
	<table class="btn_table top_margin0">
		<tr>
			<td class="left">
				<div class="header_title">
					<i class="axi axi-subtitles"></i><span>도면출력</span>
				</div>
			</td>
			<td class="right"><input type="button" value="추가" id="addBtn" title="추가"> <input type="button" value="삭제 (D)" id="deleteBtn" title="삭제 (D)"> <input type="button" value="DRW"
				id="printDrw" title="DRW"> <input type="button" value="DWG" id="printDwg" title="DWG"> <input type="button" value="PDF" id="printPdf" title="PDF"></td>
		</tr>
	</table>
	<div style="height: 500px; overflow: scroll;">
		<table class="list_table indexed sortable-table">
			<colgroup>
				<col width="60">
				<col width="150">
				<col width="*">
				<col width="100">
				<col width="100">
				<col width="100">
				<col width="100">
			</colgroup>
			<thead>
				<tr>
					<th><input name="all" id="all" type="checkbox"></th>
					<th>도면번호</th>
					<th>도면명</th>
					<th>버전</th>
					<th>상태</th>
					<th>등록자</th>
					<th>등록일</th>
				</tr>
			</thead>
			<tbody id="addDrwBody">
				<tr id="nodataTr">
					<td class="nodata" colspan="7">출력할 도면을 선택하세요.</td>
				</tr>
			</tbody>
		</table>
	</div>
</td>