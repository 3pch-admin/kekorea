<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<script type="text/javascript">
	$(document).ready(function() {

		$("input").checks();

		$(".create_table_in").tableHeadFixer();

		$("#createBtn").click(function() {
			var dialogs = $(document).setOpen();
			$name = $("input[name=name]");
			if ($name.val() == "") {
				dialogs.alert({
					theme : "alert",
					title : "결재 제목 미입력",
					msg : "결재 제목을 입력하세요."
				}, function() {
					if (this.key == "ok") {
						$name.focus();
					}
				})
				return false;
			}

			$len = $("input[name=partOid]").length;
			if ($len == 0) {
				dialogs.alert({
					theme : "alert",
					title : "결재 전장품 부품 미선택",
					msg : "결재 등록할 전장품 부품를 선택하세요."
				}, function() {
					if (this.key == "ok") {
						var url = "/Windchill/plm/part/addPart";
						var opt = "scrollbars=yes, resizable=yes";
						$(document).openURLViewOpt(url, 1200, 600, opt);
					}
				})
				return false;
			}

			$appLen = $("input[name=appUserOid]").length;
			if ($appLen == 0) {
				dialogs.alert({
					theme : "alert",
					title : "결재라인 미지정",
					msg : "결재 라인을 지정하세요."
				}, function() {
					if (this.key == "ok") {
						var url = "/Windchill/plm/org/addLine";
						var opt = "scrollbars=yes, resizable=yes";
						$(document).openURLViewOpt(url, 1200, 600, opt);
					}
				})
				return false;
			}

			dialogs.confirm({
				theme : "info",
				title : "결재 등록",
				msg : "결재를 등록하시겠습니까?"
			}, function() {
				if (this.key == "ok") {
					var url = "/Windchill/plm/part/approvalLibraryPartAction";
					var params = new Object();
					// 결재??
					params = $(document).setLines(params);

					$checkbox = $("input[name=partOid]");
					var arr = new Array();
					$.each($checkbox, function(idx) {
						var value = $checkbox.eq(idx).val();
						arr.push(value);
					})
					params.partOids = arr;
					params.lineType = $("input[name=lineType]").val();
					params = $(document).getFormData(params);
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

		$("#delPart").click(function() {
			var check = $(document).isCheckStr("partOid");
			var dialogs = $(document).setOpen();
			if (check == false) {
				dialogs.alert({
					theme : "alert",
					title : "삭제전장품 부품 미선택",
					msg : "삭제할 전장품 부품를 선택하세요."
				})
				return false;
			}

			$list = $("input[name=partOid]");
			$.each($list, function(idx) {
				if ($list.eq(idx).prop("checked") == true) {
					$list.eq(idx).parent().parent().remove();
				}
			})
			var list = $("input[name=partOid]");
			if (list.length == 0) {
				var body = $("#addPartBody");
				var html;
				html += "<tr id=\"nodataTr\">";
				html += "<td class=\"nodata h140\" colspan=\"7\"><font class=\"noInfo\"><a class=\"axi axi-info-outline\"></a> <span>결재할 전장품 부품을 추가하세요.</span></font></td>";
				html += "</tr>";
				body.append(html);
				$("input[name=allPart]").prop("checked", false);
				$("#allPart").next().removeClass("sed");
			}
		})

		$(document).on("click", ".addTag", function() {
			$(this).find(".ico-checkbox").toggleClass("sed");
			if ($(this).find("input[name=partOid]").prop("checked") == true) {
				$(this).find("input[name=partOid]").prop("checked", false);
			} else {
				$(this).find("input[name=partOid]").prop("checked", true);
			}
		})
	})

	function addPart(list) {
		var len = list.length;
		var html;
		var body = $("#addPartBody");
		$plen = $("input[name=partOid]");
		for (var i = 0; i < len; i++) {

			var bool = true;
			$.each($plen, function(idx) {
				if ($plen.eq(idx).val() == list[i][0]) {
					bool = false;
				}
			})

			if (list[i][3] != "작업 중") {
				// 작업중인 전장품 부품만 결재 가능..
				continue;
			}

			if (!bool) {
				continue;
			}

			$("#nodataTr").remove();
			html += "<tr>";
			html += "<td class=\"addTag\"><input style=\"display: none;\" type=\"checkbox\" name=\"partOid\" value=\"" + list[i][0] + "\"><div class=\"ico-checkbox helper-checks helper-checks-checkbox-allPart\"></div></td>";
			html += "<td>" + list[i][1] + "</td>";
			html += "<td class=\"left\"><img src=\"" + list[i][7] + "\" class=\"pos3\">&nbsp;" + list[i][2] + "</td>";
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
	<div class="header_title">
		<i class="axi axi-subtitles"></i><span>전장품 부품 결재</span>
	</div>
	<table class="create_table">
		<colgroup>
			<col width="200">
			<col width="*">
			<col width="200">
			<col width="*">
		</colgroup>
		<tr>
			<th>결재제목<font class="req">*</font></th>
			<td colspan="3"><input type="text" class="AXInput wid400" name="name" id="name"></td>
		</tr>
		<tr>
			<th>결재 전장품 부품<font class="req">*</font></th>
			<td colspan="3">
				<table class="in_btn_table">
					<tr>
						<td class="add"><span class="gray button small" id="addPart" data-context="product"><span title="전장품 부품 추가">추가</span></span> <span class="gray button small"><span title="전장품 부품 삭제"
								id="delPart">삭제</span></span> &nbsp;<span class="star">*</span>&nbsp;<font class="msg">상태가 작업 중인 전장품 부품만 추가가 가능합니다.</font></td>
					</tr>
				</table>
				<div class="div_scroll_200">
					<table class="create_table_in">
						<colgroup>
							<col width="40">
							<col width="200">
							<col width="*">
							<col width="100">
							<col width="100">
							<col width="100">
							<col width="100">
						</colgroup>
						<thead>
							<tr>
								<th><input type="checkbox" name="allPart" id="allPart"></th>
								<th>전장품 부품번호</th>
								<th>전장품 부품명</th>
								<th>상태</th>
								<th>버전</th>
								<th>등록자</th>
								<th>등록일</th>
							</tr>
						</thead>
						<tbody id="addPartBody">
							<tr id="nodataTr">
								<td class="nodata h140" colspan="7"><font class="noInfo"><a class="axi axi-info-outline"></a> <span>결재할 전장품 부품을 추가하세요.</span></font></td>
							</tr>
						</tbody>
					</table>
				</div>
			</td>
		</tr>
		<tr>
			<jsp:include page="/jsp/common/appLine.jsp">
				<jsp:param value="true" name="required" />
			</jsp:include>
		</tr>
	</table>
	<table class="btn_table">
		<tr>
			<td class="center"><input class="" type="button" value="등록" id="createBtn" title="등록"> <input type="button" value="뒤로 (B)" id="backBtn" class="blueBtn" title="뒤로 (B)"></td>
		</tr>
	</table>
</td>