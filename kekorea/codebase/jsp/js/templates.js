/**
 * 템플릿 전용 javascript
 */

var templates = {

	listUrl : "/Windchill/plm/template/listTemplate",

	createActionUrl : "/Windchill/plm/template/createTemplateAction",

	modifyActionUrl : "/Windchill/plm/template/modifyTemplateAction",

	deleteActionUrl : "/Windchill/plm/template/deleteTemplateAction",

	addTemplateActionUrl : "/Windchill/plm/template/addTemplateAction",

	modifyTemplate : function() {
		var dialogs = $(document).setOpen();
		var url = this.modifyActionUrl;

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "템플릿을 수정 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 등록 진행
				// 일반 문서 값
				var params = new Object();
				params.oid = $("input[name=oid]").val();
				params.name = $("input[name=name]").val();
				params.pmOid = $("input[name=pmOid]").val();
				params.sub_pmOid = $("input[name=sub_pmOid]").val();

				var popup = $("input[name=popup]").val();
				console.log(params);
				$(document).ajaxCallServer(url, params, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						msg : data.msg
					}, function() {
						// 버튼 클릭 ok, esc
						if (this.key == "ok" || this.state == "close") {
							if (data.reload && popup != "false") {
								document.location.href = data.url;
							}

							if (popup == "true") {
								self.close();
								opener.document.location.reload();
							}
						}
					})
				}, true);
			}

			if (this.key == "cancel" || this.state == "close") {
				mask.close();
			}
		})
	},

	createTemplate : function() {
		var dialogs = $(document).setOpen();
		var url = this.createActionUrl;
		// 문서 제목
		$name = $("input[name=name]");
		if ($name.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "템플릿 이름을 입력하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$name.focus();
				}
			})
			return false;
		}

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "템플릿을 등록 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 등록 진행
				// 일반 문서 값
				var params = $(document).getFormParams();
				// 관련 부품
				params.templateOid = $("input[name=templateOid]").val();
				console.log(params);
				// 결재선
				$(document).ajaxCallServer(url, params, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						msg : data.msg
					}, function() {
						// 버튼 클릭 ok, esc
						if (this.key == "ok" || this.state == "close") {
							if (data.reload) {
								document.location.href = data.url;
							}
						}
					})
				}, true);
			}

			if (this.key == "cancel" || this.state == "close") {
				mask.close();
			}
		})
	},

	deleteTemplate : function() {
		var dialogs = $(document).setOpen();
		var url = this.deleteActionUrl;

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "템플릿을 삭제 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 등록 진행
				// 일반 문서 값
				var params = $(document).getFormParams();
				// 관련 부품
				params.oid = $("input[name=oid]").val();
				// 결재선
				$(document).ajaxCallServer(url, params, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						msg : data.msg
					}, function() {
						// 버튼 클릭 ok, esc
						if (this.key == "ok" || this.state == "close") {
							if (data.reload) {
								document.location.href = data.url;
							}
						}
					})
				}, true);
			}

			if (this.key == "cancel" || this.state == "close") {
				mask.close();
			}
		})
	},

	list : function() {
		mask.open();
		$("#loading_layer").show();
		document.location.href = this.listUrl;
	},

	count : function(obj) {
		var len = $(obj).val().length;
		var dialogs = $(document).setOpen();
		if (len > 1000) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "설명은 1000자 이상 입력 불가능합니다."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$data = $("#descriptionTemp").val().substring(0, 1000);
					$("#descriptionTemp").val($data);
					$("#descriptionTemp").focus();
					$("#descTemplateCnt").text($data.length);
				}
			})
		}
		$("#descTemplateCnt").text(len);
	},

	openAddPopup : function(obj) {
		var dbl = $(obj).data("dbl");
		if (dbl == undefined) {
			dbl = "true";
		}

		var fun = $(obj).data("fun");
		if (fun == undefined) {
			fun = "addDblUsers";
		}

		var multi = $(obj).data("multi");
		if (multi == undefined) {
			multi = "false";
		}

		var url = "/Windchill/plm/org/addUser?dbl=" + dbl + "&fun=" + fun
				+ "&multi=" + multi;
		$(document).openURLViewOpt(url, 1200, 600, "");
	},

	addTemplatesAction : function() {
		var dialogs = $(document).setOpen();
		$len = $("input[name=oid]").length;
		if ($len == 0) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "추가할 템플릿을 먼저 검색하세요."
			})
			return false;
		}

		var isSelect = $(document).isSelect();
		if (isSelect == false) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "추가할 템플릿을 선택하세요"
			})
			return false;
		}

		var url = this.addTemplateActionUrl;
		var params = $(document).getListParams();

		mask.open();
		$("#loading_layer").show();

		$(document).ajaxCallServer(url, params, function(data) {
			templates.addTemplates(data.list);
		}, false);
	},

	addTemplates : function(list) {
		$len = list.length;
		var html = "";
		var body = $(opener.document).find("#addTemplateBody");
		$plen = $(opener.document).find("input[name=templateOid]");
		$container = $(opener.document).find("#template_container");

		for (var i = 0; i < $len; i++) {
			$bool = true;
			$.each($plen, function(idx) {
				if ($plen.eq(idx).val() == list[i][0]) {
					$bool = false;
				}
			})

			if (!$bool) {
				continue;
			}

			body.empty();

			$(opener.document).find("#nodataTemplate").remove();
			html += "<tr>";
			html += "<td class=\"infoTemplates left\" data-oid=\"" + list[i][0]
					+ "\"><input type=\"hidden\" name=\"templateOid\" value=\""
					+ list[i][0] + "\"><img class=\"pos3\" src=\"" + list[i][8]
					+ "\">&nbsp;" + list[i][2] + "</td>";
			html += "<td>" + list[i][1] + "</td>";
			html += "<td>" + list[i][5] + "</td>";
			html += "<td>" + list[i][7] + "</td>";
			html += "</tr>";
		}
		body.append(html);

		opener.setBoxs("input[name=templateOid]");
		templates.setBoxs();

		$opener = $(opener.document).find("input[name=templateOid]");
		if (!$container.hasClass("template_container") && $opener.length >= 6) {
			$container.addClass("template_container");
		}

		mask.close();
		$("#loading_layer").hide();
	},

	setBoxs : function() {
		$boxs = $(opener.document).find("input[name=templateOid]");
		$.each($boxs, function(idx) {
			if (!$boxs.eq(idx).hasClass("isBox")) {
				$boxs.eq(idx).addClass("isBox");
			}
		})
	},

	addDblTemplates : function(obj, value) {

		$all = $("input[name=all]");
		if ($all.prop("checked") == true) {
			$all.prop("checked", false);
			$all.next().removeClass("sed");
		}

		$oid = $("input[name=oid]");
		$.each($oid, function(idx) {
			$oid.eq(idx).parent().parent().css("background-color", "white");
			$oid.eq(idx).prop("checked", false);
			$oid.eq(idx).next().removeClass("sed");
		})

		if ($(obj).find("input[name=oid]").prop("checked") == false) {
			$(obj).css("background-color", "#fbfed1");
			$(obj).find("input[name=oid]").prop("checked", true);
			$(obj).find("div").addClass("sed");
		}

		var url = this.addTemplateActionUrl;
		var params = $(document).getDblFromData(value);
		mask.open();
		$("#loading_layer").show();

		$(document).ajaxCallServer(url, params, function(data) {
			templates.addTemplates(data.list);
		}, false);
	},

	openAddTemplatePopup : function() {
		var dbl = $(this).data("dbl");
		if (dbl == undefined) {
			dbl = "true";
		}

		var fun = $(this).data("fun");
		if (fun == undefined) {
			fun = "addDblTemplates";
		}

		var url = "/Windchill/plm/template/addTemplate?dbl=" + dbl + "&fun="
				+ fun;
		$(document).openURLViewOpt(url, 1400, 700, "");
	},

	delTemplate : function() {
		var dialogs = $(document).setOpen();
		$oid = $("input[name=templateOid]");
		if ($oid.length == 0) {
			return false;
		}

		$len = $oid.length;
		$.each($oid, function(idx) {
			$oid.eq(idx).parent().parent().remove();
			$len--;
		})

		if ($len < 6 && $("#template_container").hasClass("template_container")) {
			$("#template_container").removeClass("template_container");
		}

		if ($len == 0) {
			if ($("#template_container").hasClass("template_container")) {
				$("#template_container").removeClass("template_container");
			}
			var body = $("#addTemplateBody");
			var html = "";
			html += "<tr id=\"nodataTemplate\">";
			html += "<td class=\"nodata\" colspan=\"4\">템플릿을 추가하세요.</td>";
			html += "</tr>";
			body.append(html);
			$("#allTemplate").prop("checked", false);
			$("#allTemplate").next().removeClass("sed");
		}
	},
}

$(document).ready(function() {

	$("#createTemplateBtn").click(function() {
		templates.createTemplate();
	})

	$("#listTempBtn").click(function() {
		templates.list();
	})

	$("#descriptionTemp").keyup(function() {
		templates.count(this);
	})

	// 부품 추가 페이지
	$("#addUsers").click(function() {
		templates.openAddPopup(this);
	})

	$("#closeTemplate").click(function() {
		self.close();
	})

	$("#modifyTemplateBtn").click(function() {
		templates.modifyTemplate();
	})

	$("#addTemplate").click(function() {
		templates.openAddTemplatePopup(this);
	})

	$("#delTemplate").click(function() {
		templates.delTemplate();
	})

	$("#deleteTemplateBtn").click(function() {
		templates.deleteTemplate();
	})

})