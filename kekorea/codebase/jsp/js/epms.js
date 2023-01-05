/**
 * 도면 전용 javascript
 */

var epms = {

	downPDFUrl : "/Windchill/plm/epm/downPdf",

	downDRWUrl : "/Windchill/plm/epm/downDrw",

	downDWGUrl : "/Windchill/plm/epm/downDwg",

	printEPMUrl : "/Windchill/plm/epm/printEpm",

	listLibraryUrl : "/Windchill/plm/epm/listLibraryEpm",

	listProductUrl : "/Windchill/plm/epm/listProductEpm",

	approvalEpmActionUrl : "/Windchill/plm/epm/approvalEpmAction",

	createPartCodeActionUrl : "/Windchill/plm/epm/createPartCodeAction",

	sendDWGActionUrl : "/Windchill/plm/epm/sendDWGAction",

	listViewEpm : function() {
		if (!$("#list_view_epm").hasClass("active_view")) {
			$("#list_view_epm").addClass("active_view");
		}

		if ($("#img_view_epm").hasClass("active_view")) {
			$("#img_view_epm").removeClass("active_view");
		}

		$(".img_container").hide();
		$(".list_container").show();
	},

	imgViewEpm : function() {
		if (!$("#img_view_epm").hasClass("active_view")) {
			$("#img_view_epm").addClass("active_view");
		}

		if ($("#list_view_epm").hasClass("active_view")) {
			$("#list_view_epm").removeClass("active_view");
		}

		$(".img_container").show();
		$(".list_container").hide();
	},

	openAddPopup : function(obj) {
		var dbl = $(obj).data("dbl");
		if (dbl == undefined) {
			dbl = "true";
		}

		var state = $(obj).data("state");
		if (state == undefined) {
			state = "INWORK";
		}

		var cadtype = $(obj).data("cadtype");
		if (cadtype == undefined) {
			cadtype = "";
		}

		var fun = $(obj).data("fun");

		if (fun == "codeEpm") {
			fun = "addDblCodeEpms";
		} else if (fun == "codeLibrary") {
			fun = "addDblCodeLibrarys"
		}
		if (fun == undefined) {
			fun = "addDblEpms";
		}

		var context = $(obj).data("context");
		if (context == undefined) {
			context = "product";
		}

		var changeable = $(obj).data("changeable");
		if (changeable == undefined) {
			changeable = true;
		}

		var url = "/Windchill/plm/epm/addEpm?context=" + context + "&dbl="
				+ dbl + "&fun=" + fun + "&state=" + state + "&cadtype="
				+ cadtype + "&changeable=" + changeable;
		$(document).openURLViewOpt(url, 1400, 700, "");
	},

	bindBox : function() {
		$bool = false;
		$oid = $("input[name=epmOid");
		$cnt = 0;
		$.each($oid, function(idx) {
			if ($oid.eq(idx).prop("checked") == false) {
				$bool = true;
				return false;
			} else if ($oid.eq(idx).prop("checked") == true) {
				$cnt++;
			}
		})

		$.each($oid,
				function(idx) {
					if ($oid.eq(idx).prop("checked") == false) {
						$oid.eq(idx).parent().parent().css("background-color",
								"white");
						// $oid.eq(idx).parent().parent().next().css("background-color",
						// "white");
					} else if ($oid.eq(idx).prop("checked") == true) {
						$oid.eq(idx).parent().parent().next().css(
								"background-color", "#fbfed1");
					}
				})

		if ($cnt == $oid.length && $cnt != 0) {
			$("#allEpms").prop("checked", true);
			$("#allEpms").next().addClass("sed");
		}

		if ($bool) {
			$("#allEpms").prop("checked", false);
			$("#allEpms").next().removeClass("sed");
		}
	},

	allEpms : function(obj, e) {
		$oid = $("input[name=epmOid]");

		if ($oid.length == 0) {
			e.stopPropagation();
			e.preventDefault();
			$(obj).next().removeClass("sed");
			return false;
		}

		if ($(obj).prop("checked") == true) {
			$.each($oid, function(idx) {
				$oid.eq(idx).next().addClass("sed");
				$tr = $oid.eq(idx).parent().parent();
				$tr2 = $oid.eq(idx).parent().parent().next();
				$tr.css("background-color", "#fbfed1");
				$tr2.css("background-color", "#fbfed1");
				$oid.eq(idx).prop("checked", true);
			})
		} else {
			$.each($oid, function(idx) {
				$oid.eq(idx).next().removeClass("sed");
				$tr = $oid.eq(idx).parent().parent();
				$tr2 = $oid.eq(idx).parent().parent().next();
				$tr.css("background-color", "white");
				$tr2.css("background-color", "white");
				$oid.eq(idx).prop("checked", false);
			})
		}
	},

	addEpmsAction : function(state) {
		var dialogs = $(document).setOpen();
		$len = $("input[name=oid]").length;
		if ($len == 0) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "추가할 도면을 먼저 검색하세요."
			})
			return false;
		}

		var isSelect = $(document).isSelect();
		if (isSelect == false) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
			})
			return false;
		}

		var url = "/Windchill/plm/epm/addEpmAction";
		var params = $(document).getListParams("list", "input[name=oid]");

		mask.open();
		$("#loading_layer").show();
		$(document).ajaxCallServer(url, params, function(data) {
			epms.addEpms(data.list, state);
		}, false);
	},

	addEpms : function(list, state) {
		$len = list.length;
		var html = "";
		var body = $(opener.document).find("#addEpmsBody");
		$plen = $(opener.document).find("input[name=epmOid]");
		$container = $(opener.document).find("#epms_container");

		$container_p = $(opener.document).find("#prints_container");

		for (var i = 0; i < $len; i++) {
			$bool = true;
			$.each($plen, function(idx) {
				if ($plen.eq(idx).val() == list[i][0]) {
					$bool = false;
				}
			})

			// 상태
			// if (list[i][3].split("$")[1] != state) {
			// continue;
			// }

			if (!$bool) {
				continue;
			}

			// 0 oid, 1 number, 2 name, 3 state, 4 version, 5 creator, 6
			// createdate
			$(opener.document).find("#nodataEpms").remove();
			html += "<tr>";
			html += "<td rowspan=\"2\"><input type=\"checkbox\" name=\"epmOid\" value=\""
					+ list[i][0] + "\"></td>";
			// html += "<td><a href=\"" + list[i][9] + "\"><img class=\"pos3\"
			// src=\"" + list[i][7] + "\"></a></td>";
			html += "<td class=\"infoEpms left\" data-oid=\"" + list[i][0]
					+ "\"><img class=\"pos3\" src=\"" + list[i][7]
					+ "\">&nbsp;" + list[i][2] + "</td>";
			html += "<td class=\"infoEpms\" data-oid=\"" + list[i][0] + "\">"
					+ list[i][1] + "</td>";
			html += "<td>" + list[i][17] + "</td>";
			html += "<td>" + list[i][4] + "</td>";
			html += "<td>" + list[i][3].split("$")[0] + "</td>";
			/*
			 * if (list[i][11] == null) { html += "<td>&nbsp;</td>"; } else {
			 * html += "<td><a href=\"" + list[i][11] + "\"><img
			 * class=\"pos3\" src=\"" + list[i][10] + "\"></a></td>"; }
			 * 
			 * if (list[i][13] == null) { html += "<td>&nbsp;</td>"; } else {
			 * html += "<td><a href=\"" + list[i][13] + "\"><img
			 * class=\"pos3\" src=\"" + list[i][12] + "\"></a></td>"; }
			 */
			html += "<td>" + list[i][18] + "</td>";
			html += "<td>" + list[i][16] + "</td>";
			// html += "<td>" + list[i][6] + "</td>";
			html += "</tr>";
			html += "<tr class=\"" + list[i][0] + "\">";
			html += "<td colspan=\"7\" class=\"inputTd indent10 left\"><input value=\""
					+ list[i][19]
					+ "\" type=\"text\" name=\"description\" class=\"AXInput widMax\"></td>";
			html += "</tr>";
		}
		body.append(html);

		opener.setBoxs("input[name=epmOid]");
		epms.setBoxs();

		$opener = $(opener.document).find("input[name=epmOid]");
		if (!$container.hasClass("epms_container") && $opener.length >= 6) {
			$container.addClass("epms_container");
		}

		if (!$container_p.hasClass("prints_container") && $opener.length >= 12) {
			$container_p.addClass("prints_container");
		}

		mask.close();
		$("#loading_layer").hide();
	},

	setBoxs : function() {
		$boxs = $(opener.document).find("input[name=epmOid]");
		$.each($boxs, function(idx) {
			if (!$boxs.eq(idx).hasClass("isBox")) {
				$boxs.eq(idx).addClass("isBox");
			}
		})
	},

	addDblEpms : function(obj, value, state) {
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

		var url = "/Windchill/plm/epm/addEpmAction";
		var params = $(document).getDblFromData(value);
		mask.open();
		$("#loading_layer").show();

		$(document).ajaxCallServer(url, params, function(data) {
			epms.addEpms(data.list, state);
		}, false);
	},

	addDblCodeEpms : function(obj, value, state) {
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

		var url = "/Windchill/plm/epm/addEpmAction";
		var params = $(document).getDblFromData(value);
		mask.open();
		$("#loading_layer").show();

		$(document).ajaxCallServer(url, params, function(data) {
			epms.addCodeEpms(data.list, state);
		}, false);
	},

	addDblCodeLibrarys : function(obj, value, state) {
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

		var url = "/Windchill/plm/epm/addEpmAction";
		var params = $(document).getDblFromData(value);
		mask.open();
		$("#loading_layer").show();

		$(document).ajaxCallServer(url, params, function(data) {
			epms.addCodeLibrarys(data.list, state);
		}, false);
	},

	delCodeEpms : function() {
		var isSelect = $(document).isSelectParams("epmOid");
		var dialogs = $(document).setOpen();
		$oid = $("input[name=epmOid]");
		if ($oid.length == 0) {
			return false;
		}

		if (isSelect == false) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "삭제할 도면을 선택하세요."
			})
			return false;
		}

		$len = $oid.length;
		$.each($oid, function(idx) {
			if ($oid.eq(idx).prop("checked") == true) {
				$oid.eq(idx).parent().parent().remove();
				$len--;
			}
		})

		if ($len < 6 && $("#epms_container").hasClass("epms_container")) {
			$("#epms_container").removeClass("epms_container");
		}

		if ($len == 0) {
			if ($("#epms_container").hasClass("epms_container")) {
				$("#epms_container").removeClass("epms_container");
			}

			var body = $("#addEpmsBody");
			var html = "";
			html += "<tr id=\"nodataEpms\">";
			html += "<td class=\"nodata\" colspan=\"9\">도면을 추가하세요.</td>";
			html += "</tr>";
			body.append(html);
			$("#allEpms").prop("checked", false);
			$("#allEpms").next().removeClass("sed");

		}
	},

	downDWG : function() {
		var url = this.downDWGUrl;
		$oid = $("input[name=epmOid]");
		if ($oid.length == 0) {
			return false;
		}
		var dialogs = $(document).setOpen();
		var isSelect = $(document).isSelectParams("epmOid");
		if (isSelect == false) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "DWG 다운로드 할 도면을 선택하세요"
			})
			return false;
		}

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "DWG 파일을 다운로드 하시겠습니까",
			width : 380
		}, function() {
			if (this.key == "ok") {
				var params = $(document).getCheckBoxValue(params,
						"input[name=epmOid]", "list");
				$(document).ajaxCallServer(url, params, function(data) {
					$("#downloadFileContent").attr("href", data.url);
					document.getElementById("downloadFileContent").click();
					mask.close();
				}, false);
			}

			if (this.key == "cancel" || this.state == "close") {
				mask.close();
			}
		})
	},

	downDRW : function() {
		var url = this.downDRWUrl;
		$oid = $("input[name=epmOid]");
		if ($oid.length == 0) {
			return false;
		}

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "도면(DRW) 파일을 다운로드 하시겠습니까",
			width : 380
		}, function() {
			if (this.key == "ok") {
				var params = $(document).getCheckBoxValue(params,
						"input[name=epmOid]", "list");
				$(document).ajaxCallServer(url, params, function(data) {
					$("#downloadFileContent").attr("href", data.url);
					document.getElementById("downloadFileContent").click();
					mask.close();
				}, false);
			}

			if (this.key == "cancel" || this.state == "close") {
				mask.close();
			}
		})
	},

	downPDF : function() {
		var url = this.downPDFUrl;
		$oid = $("input[name=epmOid]");
		if ($oid.length == 0) {
			return false;
		}

		var dialogs = $(document).setOpen();
		var isSelect = $(document).isSelectParams("epmOid");
		if (isSelect == false) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "PDF 다운로드 할 도면을 선택하세요"
			})
			return false;
		}

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "PDF 파일을 다운로드 하시겠습니까",
			width : 380
		}, function() {
			if (this.key == "ok") {
				var params = $(document).getCheckBoxValue(params,
						"input[name=epmOid]", "list");
				$(document).ajaxCallServer(url, params, function(data) {
					$("#downloadFileContent").attr("href", data.url);
					document.getElementById("downloadFileContent").click();
					mask.close();
				}, false);
			}

			if (this.key == "cancel" || this.state == "close") {
				mask.close();
			}
		})
	},

	printEPM : function() {
		$oid = $("input[name=epmOid]");
		if ($oid.length == 0) {
			return false;
		}

		var box = $(document).setNonOpen();
		box
				.confirm(
						{
							theme : "info",
							title : "확인",
							msg : "도면 출력을 하시겠습니까",
							width : 380
						},
						function() {
							if (this.key == "ok") {
								var items = "";
								$checkbox = $("input[name=epmOid]");
								$.each($checkbox, function(idx) {
									var value = $checkbox.eq(idx).val();
									items += value + ",";
								})
								items = items.substring(0, items.length - 1);

								$("#items").val(items);
								var url = "/Windchill/jsp/epm/printClipboard.jsp";
								var title = "batchPrint";
								var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
								leftpos = (screen.width - 1000) / 2;
								toppos = (screen.height - 600) / 2;
								rest = "width=1000,height=600,left=" + leftpos
										+ ',top=' + toppos;
								var newwin = window
										.open("", title, opts + rest);
								$("form").attr("target", title); // form.target
								// 이 부분이 빠지면
								// form값 전송이 되지 않습니다.
								$("form").attr("action", url); // form.action 이
								// 부분이 빠지면
								// action값을 찾지 못해서 제대로 된 팝업이 뜨질
								// 않습니다.
								$("form").attr("method", "post");
								$("form").submit();
								newwin.focus();
								mask.close();
							} else if (this.key == "cancel"
									|| this.state == "close") {
								mask.close();
							}
						})
	},

	detailView : function() {
		$(document).setHTML();
		$(".detailEpm").toggle();
		$("#epmType").bindSelect();
	},

	listProduct : function() {
		mask.open();
		$("#loading_layer").show();
		document.location.href = this.listProductUrl;
	},

	listLibrary : function() {
		mask.open();
		$("#loading_layer").show();
		document.location.href = this.listLibraryUrl;
	},

	delEpms : function() {
		var isSelect = $(document).isSelectParams("epmOid");
		var dialogs = $(document).setOpen();
		$oid = $("input[name=epmOid]");

		if ($oid.length == 0) {
			return false;
		}
		if (isSelect == false) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "삭제 할 도면을 선택하세요"
			})
			return false;
		}

		$len = $oid.length;
		$.each($oid, function(idx) {
			if ($oid.eq(idx).prop("checked") == true) {
				$oid.eq(idx).parent().parent().next().remove();
				$oid.eq(idx).parent().parent().remove();
				$len--;
			}
		})

		if ($len < 6 && $("#epms_container").hasClass("epms_container")) {
			$("#epms_container").removeClass("epms_container");
		}

		if ($len == 0) {
			if ($("#epms_container").hasClass("epms_container")) {
				$("#epms_container").removeClass("epms_container");
			}
			var body = $("#addEpmsBody");
			var html = "";
			html += "<tr id=\"nodataEpms\">";
			html += "<td class=\"nodata\" colspan=\"8\">도면을 추가하세요.</td>";
			html += "</tr>";
			body.append(html);
			$("#allEpms").prop("checked", false);
			$("#allEpms").next().removeClass("sed");
		}
	},

	delLibrarys : function() {
		var isSelect = $(document).isSelectParams("libraryOid");
		var dialogs = $(document).setOpen();
		$oid = $("input[name=libraryOid]");

		if ($oid.length == 0) {
			return false;
		}
		if (isSelect == false) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "삭제 할 라이브러리를 선택하세요"
			})
			return false;
		}

		$len = $oid.length;
		$.each($oid, function(idx) {
			if ($oid.eq(idx).prop("checked") == true) {
				$oid.eq(idx).parent().parent().remove();
				$len--;
			}
		})

		if ($len < 6 && $("#librarys_container").hasClass("librarys_container")) {
			$("#librarys_container").removeClass("librarys_container");
		}

		if ($len == 0) {
			if ($("#librarys_container").hasClass("librarys_container")) {
				$("#librarys_container").removeClass("librarys_container");
			}
			var body = $("#addLibrarysBody");
			var html = "";
			html += "<tr id=\"nodataEpms\">";
			html += "<td class=\"nodata\" colspan=\"8\">라이브러리를 추가하세요.</td>";
			html += "</tr>";
			body.append(html);
			$("#allLibrarys").prop("checked", false);
			$("#allLibrarys").next().removeClass("sed");
		}
	},

	createEpmAppAction : function() {
		$obj = $("#addEpms");
		var dialogs = $(document).setOpen();
		var url = this.approvalEpmActionUrl;
		// 문서 제목
		$name = $("input[name=name]");
		if ($name.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "결재 제목을 입력하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$name.focus();
				}
			})
			return false;
		}

		$len = $("input[name=epmOid]").length;
		if ($len == 0) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "결재 등록할 도면을 선택하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					parts.openAddPopup($obj);
				}
			})
			return false;
		}

		var msg = "결재라인을 지정하세요.";

		$lineLen = $("input[name=appUserOid]").length; // 결재자
		if ($lineLen == 0) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : msg
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					// var url = "/Windchill/plm/org/addLine;";
					var url = "/Windchill/plm/org/addLine?lineType=parallel";
					// $(document).openURLViewOpt(url, 1100, 630, "");
					$(document).openURLViewOpt(url, 1100, 630, "no");
				}
			})
			return false;
		}

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "도면 결재를 등록 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 일반 폼 값
				var params = $(document).getFormParams();
				// 결재선
				params = $(document).getAppLines(params);
				// 관련
				params = $(document).getReferenceParams(params,
						"input[name=epmOid]", "epmOids");
				params = $(document).getReferenceParams(params,
						"input[name=description]", "description");
				params.comment = $("#comment").val();
				console.log(params);
				$(document).ajaxCallServer(url, params, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						msg : data.msg
					}, function() {
						if (this.key == "ok" || this.state == "close") {
							if (data.reload) {
								document.location.href = data.url;
							}
						}
					})
				}, true);

			} else if (this.key == "cancel" || this.state == "close") {
				mask.close();
			}
		})
	},

	createPartCodeBtnAction : function(obj) {
		$obj = $("#addEpms");
		var dialogs = $(document).setOpen();
		var url = this.createPartCodeActionUrl;
		// 문서 제목
		$name = $("input[name=name]");
		if ($name.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "결재 제목을 입력하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$name.focus();
				}
			})
			return false;
		}

		$len = $("input[name=epmOid]").length;
		if ($len == 0) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "결재 등록할 도면을 선택하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					parts.openAddPopup($obj);
				}
			})
			return false;
		}

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "부품코드를 생성 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 일반 폼 값
				var params = $(document).getFormParams();
				// 결재선
				params = $(document).getAppLines(params);
				// 관련
				params = $(document).getReferenceParams(params,
						"input[name=epmOid]", "epmOids");
				params = $(document).getReferenceParams(params,
						"input[name=description]", "description");
				params.self = $(obj).data("self");

				$(document).ajaxCallServer(url, params, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						msg : data.msg
					}, function() {
						if (this.key == "ok" || this.state == "close") {
							if (data.reload) {
								document.location.href = data.url;
							}
						}
					})
				}, true);

			} else if (this.key == "cancel" || this.state == "close") {
				mask.close();
			}
		})
	},

	addPrintEpmsAction : function(state) {
		var dialogs = $(document).setOpen();
		$len = $("input[name=oid]").length;
		if ($len == 0) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "추가할 도면을 먼저 검색하세요."
			})
			return false;
		}

		var isSelect = $(document).isSelect();
		if (isSelect == false) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
			})
			return false;
		}

		var url = "/Windchill/plm/epm/addEpmAction";
		var params = $(document).getListParams("list", "input[name=oid]");

		mask.open();
		$("#loading_layer").show();
		$(document).ajaxCallServer(url, params, function(data) {
			epms.addPrintEpms(data.list, state);
		}, false);
	},

	addPrintEpm : function(obj, value, state) {
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

		var url = "/Windchill/plm/epm/addEpmAction";
		var params = $(document).getDblFromData(value);
		mask.open();
		$("#loading_layer").show();

		$(document).ajaxCallServer(url, params, function(data) {
			epms.addPrintEpms(data.list, state);
		}, false);
	},

	addPrintEpms : function(list, state) {
		$len = list.length;
		var html = "";
		var body = $(opener.document).find("#addEpmsBody");
		$plen = $(opener.document).find("input[name=epmOid]");
		$container = $(opener.document).find("#epms_container");

		$container_p = $(opener.document).find("#prints_container");

		for (var i = 0; i < $len; i++) {
			$bool = true;
			$.each($plen, function(idx) {
				if ($plen.eq(idx).val() == list[i][0]) {
					$bool = false;
				}
			})

			// 상태
			// if (list[i][3].split("$")[1] != state) {
			// continue;
			// }

			if (!$bool) {
				continue;
			}

			// 0 oid, 1 number, 2 name, 3 state, 4 version, 5 creator, 6
			// createdate
			$(opener.document).find("#nodataEpms").remove();
			html += "<tr>";
			html += "<td><input type=\"checkbox\" name=\"epmOid\" value=\""
					+ list[i][0] + "\"></td>";

			if (list[i][11] == null) {
				html += "<td>&nbsp;</td>";
			} else {
				html += "<td><a href=\"" + list[i][11]
						+ "\"><img class=\"pos3\" src=\"" + list[i][10]
						+ "\"></a></td>";
			}

			if (list[i][12] == null) {
				html += "<td>&nbsp;</td>";
			} else {
				html += "<td><a href=\"" + list[i][13]
						+ "\"><img class=\"pos3\" src=\"" + list[i][12]
						+ "\"></a></td>";
			}
			html += "<td>" + list[i][2] + "</td>";
			html += "<td>" + list[i][1] + "</td>";
			html += "<td>" + list[i][20] + "</td>";
			html += "<td>" + list[i][3].split("$")[0] + "</td>";
			html += "<td>" + list[i][4] + "</td>";
			html += "</tr>";
		}
		body.append(html);

		opener.setBoxs("input[name=epmOid]");
		epms.setBoxs();

		$opener = $(opener.document).find("input[name=epmOid]");
		if (!$container.hasClass("epms_container") && $opener.length >= 6) {
			$container.addClass("epms_container");
		}

		if (!$container_p.hasClass("prints_container") && $opener.length >= 12) {
			$container_p.addClass("prints_container");
		}

		mask.close();
		$("#loading_layer").hide();
	},

	sendERPAction : function() {
		var url = this.sendDWGActionUrl;
		var params = $(document).getListParams("list", "input[name=epmOid]");
		var dialogs = $(document).setOpen();
		if (params.list.length == 0) {
			dialogs.alert({
				theme : "alert",
				title : "확인",
				msg : "DWG를 전송할 도면을 선택하세요."
			})
			return false;
		}
		$(document).ajaxCallServer(url, params, function(data) {
			dialogs.alert({
				theme : "alert",
				width : 350,
				title : "도면 전송 완료",
				msg : data.msg
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					// $(document).getColumn();
				}
			})
		}, true);
	},

	codeLibraryAction : function(state) {
		var dialogs = $(document).setOpen();
		$len = $("input[name=oid]").length;
		if ($len == 0) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "추가할 라이브러리를 먼저 검색하세요."
			})
			return false;
		}

		var isSelect = $(document).isSelect();
		if (isSelect == false) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
			})
			return false;
		}

		var url = "/Windchill/plm/epm/addEpmAction";
		var params = $(document).getListParams("list", "input[name=oid]");

		mask.open();
		$("#loading_layer").show();
		$(document).ajaxCallServer(url, params, function(data) {
			epms.addCodeLibrarys(data.list, state);
		}, false);
	},

	addCodeLibrarys : function(list, state) {
		$len = list.length;
		var html = "";
		var body = $(opener.document).find("#addLibrarysBody");
		$plen = $(opener.document).find("input[name=libraryOid]");
		$container = $(opener.document).find("#librarys_container");

		$container_p = $(opener.document).find("#prints_container");

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

			// 0 oid, 1 number, 2 name, 3 state, 4 version, 5 creator, 6
			// createdate
			$(opener.document).find("#nodataLibrarys").remove();
			html += "<tr>";
			html += "<td><input type=\"checkbox\" name=\"libraryOid\" value=\""
					+ list[i][0] + "\"></td>";
			html += "<td>" + list[i][1] + "</td>";
			// html += "<td><input type=\"text\" name=\"rev\" style=\"width:
			// 250px;\" maxlength=\"3\"></td>";
			html += "<td>" + list[i][2] + "</td>";
			html += "<td>" + list[i][2] + "</td>";
			html += "<td>" + list[i][4] + "</td>";
			html += "<td>" + list[i][3].split("$")[0] + "</td>";
			html += "<td>" + list[i][5] + "</td>";
			html += "<td>" + list[i][15] + "</td>";
			html += "</tr>";
		}
		body.append(html);

		opener.setBoxs("input[name=libraryOid]");
		epms.setBoxs2();

		$opener = $(opener.document).find("input[name=libraryOid]");
		if (!$container.hasClass("librarys_container") && $opener.length >= 6) {
			$container.addClass("librarys_container");
		}

		if (!$container_p.hasClass("prints_container") && $opener.length >= 12) {
			$container_p.addClass("prints_container");
		}

		mask.close();
		$("#loading_layer").hide();
	},

	setBoxs2 : function() {
		$boxs = $(opener.document).find("input[name=libraryOid]");
		$.each($boxs, function(idx) {
			if (!$boxs.eq(idx).hasClass("isBox")) {
				$boxs.eq(idx).addClass("isBox");
			}
		})
	},

	codeEpmAction : function(state) {
		var dialogs = $(document).setOpen();
		$len = $("input[name=oid]").length;
		if ($len == 0) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "추가할 도면을 먼저 검색하세요."
			})
			return false;
		}

		var isSelect = $(document).isSelect();
		if (isSelect == false) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
			})
			return false;
		}

		var url = "/Windchill/plm/epm/addEpmAction";
		var params = $(document).getListParams("list", "input[name=oid]");

		mask.open();
		$("#loading_layer").show();
		$(document).ajaxCallServer(url, params, function(data) {
			epms.addCodeEpms(data.list, state);
		}, false);
	},

	addCodeEpms : function(list, state) {
		$len = list.length;
		var html = "";
		var body = $(opener.document).find("#addEpmsBody");
		$plen = $(opener.document).find("input[name=epmOid]");
		$container = $(opener.document).find("#epms_container");

		$container_p = $(opener.document).find("#prints_container");

		for (var i = 0; i < $len; i++) {
			$bool = true;
			$.each($plen, function(idx) {
				if ($plen.eq(idx).val() == list[i][0]) {
					$bool = false;
				}
			})

			// 상태
			// if (list[i][3].split("$")[1] != state) {
			// continue;
			// }

			if (!$bool) {
				continue;
			}

			// 0 oid, 1 number, 2 name, 3 state, 4 version, 5 creator, 6
			// createdate
			$(opener.document).find("#nodataEpms").remove();
			html += "<tr>";
			html += "<td><input type=\"checkbox\" name=\"epmOid\" value=\""
					+ list[i][0] + "\"></td>";
			html += "<td>" + list[i][1] + "</td>";
			html += "<td><input type=\"text\" name=\"rev\" style=\"width: 250px;\" maxlength=\"3\"></td>";
			html += "<td>" + list[i][2] + "</td>";
			html += "<td>" + list[i][2] + "</td>";
			html += "<td>" + list[i][4] + "</td>";
			html += "<td>" + list[i][3].split("$")[0] + "</td>";
			html += "<td>" + list[i][5] + "</td>";
			html += "<td>" + list[i][15] + "</td>";
			html += "</tr>";
		}
		body.append(html);

		opener.setBoxs("input[name=epmOid]");
		epms.setBoxs();

		$opener = $(opener.document).find("input[name=epmOid]");
		if (!$container.hasClass("epms_container") && $opener.length >= 6) {
			$container.addClass("epms_container");
		}

		if (!$container_p.hasClass("prints_container") && $opener.length >= 12) {
			$container_p.addClass("prints_container");
		}

		mask.close();
		$("#loading_layer").hide();
	},

	createViewerAction : function() {
		var dialogs = $(document).setOpen();
		var url = "/Windchill/plm/epm/createViewerAction";

		// 문서 제목
		$name = $("input[name=name]");
		if ($name.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "품명을 입력하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$name.focus();
				}
			})
			return false;
		}

		$number = $("input[name=number]");
		if ($number.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "규격을 입력하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$number.focus();
				}
			})
			return false;
		}

		$fileName = $("input[name=fileName]");
		if ($fileName.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "파일이름을 입력하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$fileName.focus();
				}
			})
			return false;
		}

		$primaryContent = primary.getUploadedList("object")[0];
		if (!$primaryContent) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "첨부 파일을 첨부하세요."
			}, function() {
				if (this.key == "ok") {
					$("#primary_layer_AX_selector").click();
				}
				if (this.state == "close") {
					$("#primary_layer_AX_selector").click();
				}
			})
			return false;
		}

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "뷰어를 등록 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 등록 진행
				// 일반 문서 값
				var params = $(document).getFormParams();

				$(document).ajaxCallServer(url, params, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						msg : data.msg
					}, function() {
						// 버튼 클릭 ok, esc
						if (this.key == "ok" || this.state == "close") {
							if (data.reload) {
								// yhkim
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

	modifyApprovalEpm : function(obj) {
		$oid = $(obj).data("oid");
		$moid = $(obj).data("moid");
		document.location.href = "/Windchill/plm/epm/modifyApprovalEpm?oid="
				+ $oid + "&moid=" + $moid;
	},

	modifyEpmAppAction : function() {
		$obj = $("#addEpms");
		var dialogs = $(document).setOpen();
		var url = "/Windchill/plm/epm/approvalModifyEpmAction";
		// 문서 제목
		$name = $("input[name=name]");
		if ($name.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "결재 제목을 입력하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$name.focus();
				}
			})
			return false;
		}

		$len = $("input[name=epmOid]").length;
		if ($len == 0) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "결재 등록할 도면을 선택하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					parts.openAddPopup($obj);
				}
			})
			return false;
		}

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "도면 결재를 수정 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 일반 폼 값
				var params = $(document).getFormParams();
				// 결재선
				params = $(document).getAppLines(params);
				// 관련
				params = $(document).getReferenceParams(params,
						"input[name=epmOid]", "epmOids");
				params = $(document).getReferenceParams(params,
						"input[name=description]", "description");
				params.comment = $("#comment").val();
				params.oid = $("input[name=oid]").val();
				params.moid = $("input[name=moid]").val();
				$(document).ajaxCallServer(url, params, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						msg : data.msg
					}, function() {
						if (this.key == "ok" || this.state == "close") {
							if (data.reload) {
								self.close();
								opener.document.location.reload();
							}
						}
					})
				}, true);

			} else if (this.key == "cancel" || this.state == "close") {
				mask.close();
			}
		})
	},

}

$(document).ready(function() {

	$(document).on("click", ".infoEpms", function(e) {
		var oid = $(this).data("oid");
		if (oid == "") {
			return;
		}
		var url = "/Windchill/plm/epm/viewEpm?oid=" + oid + "&popup=true";
		$(document).openURLViewOpt(url, 1200, 600, "");
	}).on("mouseover", ".infoEpms", function(e) {
		$(this).css("cursor", "pointer").attr("title", "도면정보보기");
	})

	$("#list_view_epm").click(function() {
		epms.listViewEpm();
	})

	$("#img_view_epm").click(function() {
		epms.imgViewEpm();
	})

	$("#addEpms").click(function() {
		epms.openAddPopup(this);
	})

	$("#addLibrarys").click(function() {
		epms.openAddPopup(this);
	})

	$(document).bind("click", "input[name=epmOid]", function(e) {
		epms.bindBox();
	})

	$("#allEpms").click(function(e) {
		epms.allEpms(this, e);
	})

	$("#delEpms").click(function() {
		epms.delEpms();
	})

	$("#delLibrarys").click(function() {
		epms.delLibrarys();
	})

	$("#delCodeEpms").click(function() {
		epms.delCodeEpms();
	})

	$("#downDRW").click(function() {
		epms.downDRW();
	})

	$("#downDWG").click(function() {
		epms.downDWG();
	})

	$("#downPDF").click(function() {
		epms.downPDF();
	})

	$("#printEPM").click(function() {
		epms.printEPM();
	})

	$("#detailEpmBtn").click(function() {
		epms.detailView();
	})

	$("#listLibraryEpmBtn").click(function() {
		epms.listLibrary();
	})

	$("#listProductEpmBtn").click(function() {
		epms.listProduct();
	})

	$("#createEpmAppBtn").click(function() {
		epms.createEpmAppAction(this);
	})

	$("#modifyApprovalEpm").click(function() {
		epms.modifyApprovalEpm(this);
	})

	$("#createPartCodeBtn").click(function() {
		epms.createPartCodeBtnAction(this);
	})

	$("#sendERP").click(function() {
		epms.sendERPAction();
	})

	$("#createViewerBtn").click(function() {
		epms.createViewerAction();
	})

	$("#modifyEpmAppBtn").click(function() {
		epms.modifyEpmAppAction();
	})

	$("#epmType").bindSelect();

	$(".epms_add_table").tableHeadFixer();
})
