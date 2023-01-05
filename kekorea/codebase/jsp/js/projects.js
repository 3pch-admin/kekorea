/**
 * 작번 전용 javascript
 */

var config = {
	min : 0, // {Number} [min=Number.MIN_VALUE] - 최소값 (optional)
	max : 10000000000
// {Number} [max=Number.MAX_VALUE] - 최대값 (optional)
};

var projects = {
	request_root : "/Default/프로젝트/의뢰서",

	listUrl : "/Windchill/plm/project/listProject",

	createActionUrl : "/Windchill/plm/project/createProjectAction",

	modifyActionUrl : "/Windchill/plm/project/modifyProjectPriceAction",

	addOutputActionUrl : "/Windchill/plm/project/addOutputAction",

	addRequestDocumentActionUrl : "/Windchill/plm/project/addRequestDocumentAction",

	delOutputActionUrl : "/Windchill/plm/project/delOutputAction",

	completeTaskUrl : "/Windchill/plm/project/completeTaskAction",

	addIssueUrl : "/Windchill/plm/project/addIssue",

	delIssueUrl : "/Windchill/plm/project/delIssueAction",

	createIssueActionUrl : "/Windchill/plm/project/createIssueAction",

	modifyIssueActionUrl : "/Windchill/plm/project/modifyIssueAction",

	setUserActionUrl : "/Windchill/plm/project/setUserAction",

	setProgressActionUrl : "/Windchill/plm/project/setProgressAction",

	modifyProjectActionUrl : "/Windchill/plm/project/modifyProjectAction",

	deleteActionUrl : "/Windchill/plm/project/deleteProjectAction",

	createCodeActionUrl : "/Windchill/plm/project/createCodeAction",

	addProjectsAction : function(state) {
		var dialogs = $(document).setOpen();
		$len = $("input[name=oid]").length;
		if ($len == 0) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "추가할 작번을 먼저 검색하세요."
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

		var url = "/Windchill/plm/project/addProjectAction";
		var params = $(document).getListParams("list", "input[name=oid]");

		mask.open();
		$("#loading_layer").show();
		$(document).ajaxCallServer(url, params, function(data) {
			projects.addProjects(data.list, state);
		}, false);
	},

	viewProject : function(obj) {
		$oid = $(obj).data("oid");
		var $url = "/Windchill/plm/project/viewProject?popup=true&oid=" + $oid;
		// var $url =
		// "/Windchill/plm/epm/viewEpm?popup=true&oid=wt.epm.EPMDocument:9284261";
		$(document).openURLViewOpt($url, 1200, 700, "");
	},

	addProjects : function(list, state) {
		$len = list.length;
		var html = "";
		var body = $(opener.document).find("#addProjectsBody");
		$plen = $(opener.document).find("input[name=projectOid]");
		$container = $(opener.document).find("#projects_container");

		$poid = $plen.val();
		// alert($poid);
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

			$(opener.document).find("#nodataProjects").remove();
			html += "<tr>";
			html += "<td><input type=\"checkbox\" name=\"projectOid\" value=\"" + list[i][0] + "\"></td>";
			html += "<td>" + list[i][9] + "</td>";
			html += "<td class=\"infoProjects\" data-oid=\"" + list[i][0] + "\">" + list[i][1] + "</td>";
			html += "<td class=\"infoProjects\" data-oid=\"" + list[i][0] + "\">" + list[i][2] + "</td>";
			html += "<td>" + list[i][6] + "</td>";
			html += "<td>" + list[i][7] + "</td>";
			html += "<td class=\"left indent10\">" + list[i][8] + "</td>";
			html += "</tr>";
		}
		body.append(html);

		opener.setBoxs("input[name=projectOid]");
		projects.setBoxs();

		$opener = $(opener.document).find("input[name=projectOid]");
		if (!$container.hasClass("projects_container") && $opener.length >= 6) {
			$container.addClass("projects_container");
		}

		if (!$container_p.hasClass("prints_container") && $opener.length >= 12) {
			$container_p.addClass("prints_container");
		}

		var oid = $(opener.document).find("input[name=projectOid]");
		$(opener.document).find("#descProjectCnt").text(oid.length);

		mask.close();
		$("#loading_layer").hide();
	},

	addIssueProjectsAction : function(state) {
		var dialogs = $(document).setOpen();
		$len = $("input[name=oid]").length;
		if ($len == 0) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "추가할 작번을 먼저 검색하세요."
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

		var url = "/Windchill/plm/project/addIssueProjectsAction";
		var params = $(document).getListParams("list", "input[name=oid]");

		mask.open();
		$("#loading_layer").show();
		$(document).ajaxCallServer(url, params, function(data) {
			projects.addProjects(data.list, state);
		}, false);
	},

	addIssueProjects : function(list, state) {
		$len = list.length;
		var html = "";
		var body = $(opener.document).find("#addProjectsBody");
		$plen = $(opener.document).find("input[name=projectOid]");
		$container = $(opener.document).find("#projects_container");

		$poid = $plen.val();
		// alert($poid);
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

			$(opener.document).find("#nodataProjects").remove();
			html += "<tr>";
			html += "<td><input type=\"checkbox\" name=\"projectOid\" value=\"" + list[i][0] + "\"></td>";
			html += "<td>" + list[i][9] + "</td>";
			html += "<td class=\"infoProjects\" data-oid=\"" + list[i][0] + "\">" + list[i][1] + "</td>";
			html += "<td class=\"infoProjects\" data-oid=\"" + list[i][0] + "\">" + list[i][2] + "</td>";
			html += "<td>" + list[i][6] + "</td>";
			html += "<td>" + list[i][7] + "</td>";
			html += "<td class=\"left indent10\">" + list[i][8] + "</td>";
			html += "</tr>";
		}
		body.append(html);

		opener.setBoxs("input[name=projectOid]");
		projects.setBoxs();

		$opener = $(opener.document).find("input[name=projectOid]");
		if (!$container.hasClass("projects_container") && $opener.length >= 6) {
			$container.addClass("projects_container");
		}

		if (!$container_p.hasClass("prints_container") && $opener.length >= 12) {
			$container_p.addClass("prints_container");
		}

		var oid = $(opener.document).find("input[name=projectOid]");
		$(opener.document).find("#descProjectCnt").text(oid.length);

		mask.close();
		$("#loading_layer").hide();
	},

	setBoxs : function() {
		$boxs = $(opener.document).find("input[name=projectOid]");
		$.each($boxs, function(idx) {
			if (!$boxs.eq(idx).hasClass("isBox")) {
				$boxs.eq(idx).addClass("isBox");
			}
		})
	},

	openAddPopup : function(obj) {
		var dbl = $(obj).data("dbl");
		if (dbl == undefined) {
			dbl = "true";
		}

		var state = $(obj).data("state");
		if (state == undefined) {
			state = "";
		}

		var fun = $(obj).data("fun");
		if (fun == undefined) {
			fun = "addDblProjects";
		}

		var context = $(obj).data("context");
		if (context == undefined) {
			context = "product";
		}

		var changeable = $(obj).data("changeable");
		if (changeable == undefined) {
			changeable = true;
		}

		var url = "/Windchill/plm/project/addProject?context=" + context + "&dbl=" + dbl + "&fun=" + fun + "&state=" + state + "&changeable=" + changeable;
		$(document).openURLViewOpt(url, 1400, 700, "no");
	},

	addDblProjects : function(obj, value, state) {

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

		var url = "/Windchill/plm/project/addProjectAction";
		var params = $(document).getDblFromData(value);
		mask.open();
		$("#loading_layer").show();

		$(document).ajaxCallServer(url, params, function(data) {
			projects.addProjects(data.list, state);
		}, false);
	},

	delProjects : function() {
		var isSelect = $(document).isSelectParams("projectOid");
		var dialogs = $(document).setOpen();
		$oid = $("input[name=projectOid]");
		if ($oid.length == 1) {
			return false;
		}

		if (isSelect == false) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "삭제할 작번을 선택하세요."
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

		if ($len < 6 && $("#projects_container").hasClass("projects_container")) {
			$("#projects_container").removeClass("projects_container");
		}

		if ($len == 0) {
			if ($("#projects_container").hasClass("projects_container")) {
				$("#projects_container").removeClass("projects_container");
			}

			var body = $("#addProjectsBody");
			var html = "";
			html += "<tr id=\"nodataProjects\">";
			html += "<td class=\"nodata\" colspan=\"7\">작번을 추가하세요.</td>";
			html += "</tr>";
			body.append(html);
			$("#allProjects").prop("checked", false);
			$("#allProjects").next().removeClass("sed");
		}

		var oid = $("input[name=projectOid]");
		$("#descProjectCnt").text(oid.length);
	},

	modifyProjectAction : function() {
		var dialogs = $(document).setOpen();
		var url = this.modifyProjectActionUrl;
		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "작번을 등록 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 등록 진행
				// 일반 문서 값
				var params = $(document).getFormParams();

				params.oid = $("input[name=oid]").val();
				// 관련 부품
				// params = $(document).getReferenceParams(params,
				// "input[name=templateOid]", "templateOids");
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

	createProject : function() {
		var dialogs = $(document).setOpen();
		var url = this.createActionUrl;
		// 프로젝트 제목
		$kekNumber = $("input[name=kekNumber]");
		if ($kekNumber.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "KEK 작번을 입력하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$kekNumber.focus();
				}
			})
			return false;
		}

		$postdate = $("input[name=postdate]");
		if ($postdate.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "작번 발행일을 입력하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$postdate.focus();
				}
			})
			return false;
		}

		$keNumber = $("input[name=keNumber]");
		if ($keNumber.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "KE 작번을 입력하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$keNumber.focus();
				}
			})
			return false;
		}

		$postdate_m = $("input[name=postdate_m]");
		if ($postdate_m.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "요구납기일을 입력하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$postdate_m.focus();
				}
			})
			return false;
		}

		$userId = $("input[name=userId]");
		if ($userId.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "USER ID를 입력하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$userId.focus();
				}
			})
			return false;
		}

		/*
		 * $systemInfo = $("input[name=systemInfo]"); if ($systemInfo.val() == "") { dialogs.alert({ theme : "alert", title : "경고", msg : "SYSTEM INFO를 입력하세요." }, function() { if (this.key == "ok" || this.state == "close") { $systemInfo.focus(); } }) return false; }
		 */

		$mak = $("input[name=mak]");
		if ($mak.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "막종을 입력하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$mak.focus();
				}
			})
			return false;
		}

		$model = $("input[name=model]");
		if ($model.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "모델을 입력하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$model.focus();
				}
			})
			return false;
		}

		$customer = $("select[name=customer]");
		if ($customer.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "거래처를 선택하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$customer.focus();
				}
			})
			return false;
		}

		$ins_location = $("select[name=ins_location]");
		if ($ins_location.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "설치 장소를 선택하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$ins_location.focus();
				}
			})
			return false;
		}

		$pType = $("select[name=pType]");
		if ($pType.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "작번 유형을 선택하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$pType.focus();
				}
			})
			return false;
		}

		$description = $("textarea[name=description]");
		if ($description.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "작업 내용을 입력하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$description.focus();
				}
			})
			return false;
		}

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "작번을 등록 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 등록 진행
				// 일반 문서 값
				var params = $(document).getFormParams();
				// 관련 부품
				// params = $(document).getReferenceParams(params,
				// "input[name=templateOid]", "templateOids");
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

	openAddOutputPopup : function(obj) {
		var dbl = $(obj).data("dbl");
		if (dbl == undefined) {
			dbl = "true";
		}

		var fun = $(obj).data("fun");
		if (fun == undefined) {
			fun = "addDblOutputs";
		}

		var context = $(obj).data("context");
		if (context == undefined) {
			context = "PRODUCT";
		}

		var type = $(obj).data("type");
		if (type == undefined || type == "") {
			type = "all";
		}

		var toid = $(obj).data("oid");

		var url = "/Windchill/plm/project/addOutput?context=" + context + "&dbl=" + dbl + "&fun=" + fun + "&toid=" + toid + "&type=" + type;
		$(document).openURLViewOpt(url, 1400, 700, "");
	},

	addDblOutputs : function(obj, value, toid) {
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

		var url = this.addOutputActionUrl;
		var params = $(document).getDblFromData(value);
		params.toid = toid;
		mask.open();
		$("#loading_layer").show();

		$(document).ajaxCallServer(url, params, function(data) {
			projects.addOutputs(data.list);
		}, false);
	},

	addOutputsAction : function(toid) {
		var dialogs = $(document).setOpen();
		$len = $("input[name=oid]").length;
		if ($len == 0) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "추가할 산출물을 먼저 검색하세요."
			})
			return false;
		}

		var isSelect = $(document).isSelect();
		if (isSelect == false) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "추가할 산출물을 선택하세요"
			})
			return false;
		}

		var url = this.addOutputActionUrl;
		var params = $(document).getListParams("list", "input[name=oid]");
		params.toid = toid;
		mask.open();
		$("#loading_layer").show();

		$(document).ajaxCallServer(url, params, function(data) {
			projects.addOutputs(data.list);
		}, false);
	},

	addOutputs : function(list) {
		$len = list.length;
		var html = "";
		// var body = $("#addPartsBody");
		var body = $(opener.document).find("#addOutputsBody");
		$plen = $(opener.document).find("input[name=outputOid]");
		$container = $(opener.document).find("#outputs_container");

		for (var i = 0; i < $len; i++) {
			$bool = true;
			$.each($plen, function(idx) {
				// if ($plen.eq(idx).val() == list[i][0]) {
				if ($plen.eq(idx).data("doid") == list[i][0]) {
					$bool = false;
				}
			})

			if (!$bool) {
				continue;
			}

			// 0 oid, 1 number, 2 name, 3 state, 4 version, 5 creator, 6
			// createdate
			$(opener.document).find("#nodataOutputs").remove();
			html += "<tr>";
			html += "<td><input type=\"checkbox\" name=\"outputOid\" data-doid=\"" + list[i][0] + "\" value=\"" + list[i][0] + "\"></td>";
			html += "<td class=\"infoDocs left indent10\" data-oid=\"" + list[i][0] + "\">" + list[i][2] + "</td>";
			html += "<td class=\"infoDocs left indent10\" data-oid=\"" + list[i][0] + "\">" + list[i][9] + "</td>";
			html += "<td>" + list[i][4] + "</td>";
			html += "<td>" + list[i][3].split("$")[0] + "</td>";
			html += "<td>" + list[i][11] + "</td>";
			html += "<td>" + list[i][10] + "</td>";
			if (list[i][13] != null) {
				html += "<td><a href=\"" + list[i][12] + "\"><img src=\"" + list[i][13] + "\" class=\"pos2\"></a></td>";
			} else {
				html += "<td>&nbsp;</td>";
			}
			html += "</tr>";
		}
		body.append(html);

		opener.setBoxs("input[name=outputOid]");
		projects.setBindBox();

		$opener = $(opener.document).find("input[name=outputOid]");
		if (!$container.hasClass("outputs_container") && $opener.length >= 6) {
			$container.addClass("outputs_container");
		}

		mask.close();
		$("#loading_layer").hide();
	},

	setBindBox : function() {
		$boxs = $(opener.document).find("input[name=outputOid]");
		$.each($boxs, function(idx) {
			if (!$boxs.eq(idx).hasClass("isBox")) {
				$boxs.eq(idx).addClass("isBox");
			}
		})
	},

	delOutputsAction : function(toid) {
		var dialogs = $(document).setOpen();
		var isSelect = $(document).isOutputSelect();
		if (isSelect == false) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "삭제할 산출물을 선택하세요"
			})
			return false;
		}

		var url = this.delOutputActionUrl;
		var params = $(document).getListParams("list", "input[name=outputOid]");

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "산출물을 삭제 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 결재선
				$(document).ajaxCallServer(url, params, function(data) {
					projects.delOutputs();
				}, true);
			}

			if (this.key == "cancel" || this.state == "close") {
				mask.close();
			}
		})
	},

	allOutputs : function(obj, e) {
		$oid = $("input[name=outputOid]");

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
				// $tr2 = $oid.eq(idx).parent().parent().next();
				$tr.css("background-color", "#fbfed1");
				// $tr2.css("background-color", "#fbfed1");
				$oid.eq(idx).prop("checked", true);
			})
		} else {
			$.each($oid, function(idx) {
				$oid.eq(idx).next().removeClass("sed");
				$tr = $oid.eq(idx).parent().parent();
				// $tr2 = $oid.eq(idx).parent().parent().next();
				$tr.css("background-color", "white");
				// $tr2.css("background-color", "white");
				$oid.eq(idx).prop("checked", false);
			})
		}
	},

	bindBox : function(e) {
		$bool = false;
		$oid = $("input[name=outputOid");
		$cnt = 0;
		$.each($oid, function(idx) {
			if ($oid.eq(idx).prop("checked") == false) {
				$bool = true;
				return false;
			} else if ($oid.eq(idx).prop("checked") == true) {
				$cnt++;
			}
		})

		$.each($oid, function(idx) {
			if ($oid.eq(idx).prop("checked") == false) {
				$oid.eq(idx).parent().parent().css("background-color", "white");
			} else if ($oid.eq(idx).prop("checked") == true) {
				$oid.eq(idx).parent().parent().css("background-color", "#fbfed1");
			}
		})

		if ($cnt == $oid.length && $cnt != 0) {
			$("#allOutputs").prop("checked", true);
			$("#allOutputs").next().addClass("sed");
		}

		if ($bool) {
			$("#allOutputs").prop("checked", false);
			$("#allOutputs").next().removeClass("sed");
		}
	},

	bindBox2 : function(e) {
		$bool = false;
		$oid = $("input[name=projectOid");
		$cnt = 0;
		$.each($oid, function(idx) {
			if ($oid.eq(idx).prop("checked") == false) {
				$bool = true;
				return false;
			} else if ($oid.eq(idx).prop("checked") == true) {
				$cnt++;
			}
		})

		$.each($oid, function(idx) {
			if ($oid.eq(idx).prop("checked") == false) {
				$oid.eq(idx).parent().parent().css("background-color", "white");
			} else if ($oid.eq(idx).prop("checked") == true) {
				$oid.eq(idx).parent().parent().css("background-color", "#fbfed1");
			}
		})

		if ($cnt == $oid.length && $cnt != 0) {
			$("#allProjects").prop("checked", true);
			$("#allProjects").next().addClass("sed");
		}

		if ($bool) {
			$("#allProjects").prop("checked", false);
			$("#allProjects").next().removeClass("sed");
		}
	},

	bindBox3 : function(e) {
		$bool = false;
		$oid = $("input[name=issueOid");
		$cnt = 0;
		$.each($oid, function(idx) {
			if ($oid.eq(idx).prop("checked") == false) {
				$bool = true;
				return false;
			} else if ($oid.eq(idx).prop("checked") == true) {
				$cnt++;
			}
		})

		$.each($oid, function(idx) {
			if ($oid.eq(idx).prop("checked") == false) {
				$oid.eq(idx).parent().parent().css("background-color", "white");
			} else if ($oid.eq(idx).prop("checked") == true) {
				$oid.eq(idx).parent().parent().css("background-color", "#fbfed1");
			}
		})

		if ($cnt == $oid.length && $cnt != 0) {
			$("#allIssues").prop("checked", true);
			$("#allIssues").next().addClass("sed");
		}

		if ($bool) {
			$("#allIssues").prop("checked", false);
			$("#allIssues").next().removeClass("sed");
		}
	},

	allProjects : function(obj, e) {
		$oid = $("input[name=projectOid]");

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
				$tr.css("background-color", "#fbfed1");
				$oid.eq(idx).prop("checked", true);
			})
		} else {
			$.each($oid, function(idx) {
				$oid.eq(idx).next().removeClass("sed");
				$tr = $oid.eq(idx).parent().parent();
				$tr.css("background-color", "white");
				$oid.eq(idx).prop("checked", false);
			})
		}
	},

	startProject : function(obj) {
		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "프로젝트를 시작 하시겠습니까?"
		}, function() {
			var url = "/Windchill/plm/project/startProjectAction";
			var params = new Object();
			params.oid = $(obj).data("oid");
			if (this.key == "ok") {
				// 결재선
				$(document).ajaxCallServer(url, params, function(data) {
					document.location.reload();
				}, true);
			}

			if (this.key == "cancel" || this.state == "close") {
				mask.close();
			}
		})
	},

	restartProject : function(obj) {
		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "프로젝트를 재시작 하시겠습니까?"
		}, function() {
			var url = "/Windchill/plm/project/restartProjectAction";
			var params = new Object();
			params.oid = $(obj).data("oid");
			if (this.key == "ok") {
				// 결재선
				$(document).ajaxCallServer(url, params, function(data) {
					document.location.reload();
				}, true);
			}

			if (this.key == "cancel" || this.state == "close") {
				mask.close();
			}
		})
	},

	stopProject : function(obj) {
		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "프로젝트를 중단 하시겠습니까?"
		}, function() {
			var url = "/Windchill/plm/project/stopProjectAction";
			var params = new Object();
			params.oid = $(obj).data("oid");
			if (this.key == "ok") {
				// 결재선
				$(document).ajaxCallServer(url, params, function(data) {
					document.location.reload();
				}, true);
			}

			if (this.key == "cancel" || this.state == "close") {
				mask.close();
			}
		})
	},

	completeProject : function(obj) {
		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "프로젝트를 완료 하시겠습니까?"
		}, function() {
			var url = "/Windchill/plm/project/completeProjectAction";
			var params = new Object();
			params.oid = $(obj).data("oid");
			if (this.key == "ok") {
				// 결재선
				$(document).ajaxCallServer(url, params, function(data) {
					document.location.reload();
				}, true);
			}

			if (this.key == "cancel" || this.state == "close") {
				mask.close();
			}
		})
	},

	modifyProjectPopup : function(obj) {
		$poid = $("input[name=oid]").val();
		var url = "/Windchill/plm/project/modifyProject?popup=true&oid=" + $poid;
		$(document).openURLViewOpt(url, 1100, 400, "no");
	},

	modifyProject : function() {
		var dialogs = $(document).setOpen();
		var url = this.modifyActionUrl;

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "작번을 수정 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				var params = new Object();

				params.machinePrice = $("input[name=machinePrice]").val();
				params.elecPrice = $("input[name=elecPrice]").val();
				params.oid = $("input[name=oid]").val();
				params.kekState = $("select[name=kekState]").val();

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

	completeTask : function() {
		var dialogs = $(document).setOpen();
		var url = this.completeTaskUrl;

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "태스크를 완료 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				var params = new Object();

				params.oid = $("input[name=oid]").val();
				console.log(params);
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

	addIssue : function(obj) {
		$poid = $(obj).data("poid");
		$popup = $("input[name=popup]").val();
		var url = "/Windchill/plm/project/addIssue?popup=" + $popup + "&poid=" + $poid;
		$(document).openURLViewOpt(url, 1200, 600, "no");
	},

	delIssueAction : function(obj) {
		var dialogs = $(document).setOpen();
		var isSelect = $(document).isIssueSelect();
		if (isSelect == false) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "삭제할 특이사항을 선택하세요"
			})
			return false;
		}

		var url = this.delIssueUrl;
		var params = $(document).getListParams("list", "input[name=issueOid]");
		console.log(params);
		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "특이사항을 삭제 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 결재선
				$(document).ajaxCallServer(url, params, function(data) {
					projects.delIssues();
				}, true);
			}

			if (this.key == "cancel" || this.state == "close") {
				mask.close();
			}
		})
	},

	createIssueAction : function(obj) {
		var dialogs = $(document).setOpen();
		var url = this.createIssueActionUrl;

		// 문서 제목
		$name = $("input[name=name]");
		if ($name.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "특이사항 제목을 입력하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$name.focus();
				}
			})
			return false;
		}

		$description = $("input[name=description]");
		if ($description.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "특이사항 설명을 입력하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$description.focus();
				}
			})
			return false;
		}

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "특이사항을 등록 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 등록 진행
				// 일반 문서 값
				var params = $(document).getFormParams();
				// 관련 부품
				params.poid = $(obj).data("poid");
				params = $(document).getReferenceParams(params, "input[name=projectOid]", "projectOids");
				console.log(params);
				$(document).ajaxCallServer(url, params, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						msg : data.msg
					}, function() {
						// 버튼 클릭 ok, esc
						if (this.key == "ok" || this.state == "close") {
							if (data.reload) {
								projects.addIssues(data.list);
								opener.location.reload();
								window.close();
								// self.opener = self;
								// window.close();
								// document.location.href = data.url;
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

	modifyIssueAction : function(obj) {
		var dialogs = $(document).setOpen();
		var url = this.modifyIssueActionUrl;

		// 문서 제목
		$name = $("input[name=name]");
		if ($name.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "특이사항 제목을 입력하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$name.focus();
				}
			})
			return false;
		}

		$description = $("input[name=description]");
		if ($description.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "특이사항 설명을 입력하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$description.focus();
				}
			})
			return false;
		}

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "특이사항을 수정 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 등록 진행
				// 일반 문서 값
				var params = $(document).getFormParams();
				// params = $(document).getReferenceParams(params,"input[name=projectOid]", "projectOids");

				var oid = $("input[name=oid]").val();
				var poid = $("input[name=poid]").val();
				var name = $("input[name=istitle]").val();
				var description = $("textarea[name=description]").val();

				params.oid = oid;
				params.poid = poid;
				params.name = name;
				params.description = description;
				params = $(document).getReferenceParams(params, "input[name=projectOid]", "projectOids");
				console.log(params);
				$(document).ajaxCallServer(url, params, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						msg : data.msg
					}, function() {
						// 버튼 클릭 ok, esc
						if (this.key == "ok" || this.state == "close") {
							if (data.reload) {
								// projects.addIssues(data.list);
								document.location.href = data.url;
								window.close();
								opener.location.reload();
								// self.opener = self;
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

	addIssues : function(list) {
		$len = list.length;
		var html = "";
		// var body = $("#addPartsBody");
		var body = $(opener.document).find("#addIssuesBody");
		$plen = $(opener.document).find("input[name=issueOid]");
		$container = $(opener.document).find("#issues_container");

		for (var i = 0; i < $len; i++) {
			$bool = true;
			$.each($plen, function(idx) {
				// if ($plen.eq(idx).val() == list[i][0]) {
				if ($plen.eq(idx).data("ioid") == list[i][0]) {
					$bool = false;
				}
			})

			if (!$bool) {
				continue;
			}

			// 0 oid, 1 number, 2 name, 3 state, 4 version, 5 creator, 6
			// createdate
			$(opener.document).find("#nodataIssues").remove();
			html += "<tr id=\"nodataIssues\">";
			html += "<td><input class\"isBox\" type=\"checkbox\" name=\"issueOid\" data-oid=\"" + list[i][0] + "\" value=\"" + list[i][0] + "\"></td>";
			// html += "<td class=\"infoDocs left indent10\" data-oid=\"" +
			// list[i][0] + "\">" + list[i][2] + "</td>";
			// html += "<td class=\"infoDocs left indent10\" data-oid=\"" +
			// list[i][0] + "\">" + list[i][9] + "</td>";
			html += "<td>" + list[i][1] + "</td>";
			html += "<td>" + list[i][2] + "</td>";
			// html += "<td>" + list[i][3].split("$")[0] + "</td>";
			html += "<td>" + list[i][3] + "</td>";
			html += "<td>" + list[i][4] + "</td>";
			// if (list[i][13] != null) {
			// html += "<td><a href=\"" + list[i][12] + "\"><img src=\"" +
			// list[i][13] + "\" class=\"pos2\"></a></td>";
			// } else {
			// html += "<td>&nbsp;</td>";
			// }
			html += "</tr>";
		}
		body.append(html);

		opener.setBoxs("input[name=issueOid]");
		projects.setBindBox3();

		$opener = $(opener.document).find("input[name=issueOid]");
		if (!$container.hasClass("issues_container") && $opener.length >= 6) {
			$container.addClass("issues_container");
		}

		mask.close();
		$("#loading_layer").hide();
	},

	setBindBox3 : function() {
		$boxs = $(opener.document).find("input[name=issueOid]");
		$.each($boxs, function(idx) {
			if (!$boxs.eq(idx).hasClass("isBox")) {
				$boxs.eq(idx).addClass("isBox");
			}
		})
	},

	delIssues : function() {
		var dialogs = $(document).setOpen();
		$oid = $("input[name=issueOid]");
		if ($oid.length == 0) {
			return false;
		}

		var isSelect = $(document).isSelectParams("issueOid");
		if (isSelect == false) {
			return false;
		}

		$len = $oid.length;
		$.each($oid, function(idx) {
			if ($oid.eq(idx).prop("checked") == true) {
				$oid.eq(idx).parent().parent().remove();
				$len--;
			}
		})

		if ($len < 6 && $("#issues_container").hasClass("issues_container")) {
			$("#issues_container").removeClass("issues_container");
		}

		if ($len == 0) {
			if ($("#issues_container").hasClass("issues_container")) {
				$("#issues_container").removeClass("issues_container");
			}
			var body = $("#addIssuesBody");
			var html = "";
			html += "<tr id=\"nodataIssues\">";
			html += "<td class=\"nodata\" colspan=\"5\">특이사항을 추가하세요.</td>";
			html += "</tr>";
			body.append(html);
			$("#allIssues").prop("checked", false);
			$("#allIssues").next().removeClass("sed");

			mask.close();
			$("#loading_layer").hide();
		} else {
			mask.close();
			$("#loading_layer").hide();
		}
	},

	delOutputs : function() {
		var dialogs = $(document).setOpen();
		$oid = $("input[name=outputOid]");
		if ($oid.length == 0) {
			return false;
		}

		var isSelect = $(document).isSelectParams("outputOid");
		if (isSelect == false) {
			return false;
		}

		$len = $oid.length;
		$.each($oid, function(idx) {
			if ($oid.eq(idx).prop("checked") == true) {
				$oid.eq(idx).parent().parent().remove();
				$len--;
			}
		})

		if ($len < 6 && $("#outputs_container").hasClass("outputs_container")) {
			$("#outputs_container").removeClass("outputs_container");
		}

		if ($len == 0) {
			if ($("#outputs_container").hasClass("outputs_container")) {
				$("#outputs_container").removeClass("outputs_container");
			}
			var body = $("#addOutputsBody");
			var html = "";
			html += "<tr id=\"nodataOutputs\">";
			html += "<td class=\"nodata\" colspan=\"7\">산출물을 추가하세요.</td>";
			html += "</tr>";
			body.append(html);
			$("#allOutputs").prop("checked", false);
			$("#allOutputs").next().removeClass("sed");

			mask.close();
			$("#loading_layer").hide();
		} else {
			mask.close();
			$("#loading_layer").hide();
		}
	},

	allIssues : function(obj, e) {
		$oid = $("input[name=issueOid]");

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
				$tr.css("background-color", "#fbfed1");
				$oid.eq(idx).prop("checked", true);
			})
		} else {
			$.each($oid, function(idx) {
				$oid.eq(idx).next().removeClass("sed");
				$tr = $oid.eq(idx).parent().parent();
				$tr.css("background-color", "white");
				$oid.eq(idx).prop("checked", false);
			})
		}
	},

	elecPrice : function() {
		var prompt = $(document).prompt();
		prompt.prompt({
			theme : "info",
			title : "전기견적 금액 입력"
		}, function() {
			if (this.key == "ok" || this.key == "enter") {
				projects.modifyElecPrice(this.input.value);
			} else if (this.key == "cancel") {
				mask.close();
			}
		});
	},

	machinePrice : function() {
		var prompt = $(document).prompt();
		prompt.prompt({
			input : {
				machinePrice : {
					required : true
				},
			},
			theme : "info",
			title : "기계견적 금액 입력"
		}, function() {
			if (this.key == "ok" || this.key == "enter") {
				// $(document).gotoList(this.input.value);
				projects.modifyMachinePrice(this.input.machinePrice);
			} else if (this.key == "cancel") {
				mask.close();
			}
		});
	},

	modifyMachinePrice : function(value) {
		var url = this.modifyActionUrl;
		var params = new Object();
		var dialogs = $(document).setOpen();
		params.machinePrice = value;
		params.oid = $("input[name=oid]").val();

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
	},

	modifyElecPrice : function(value) {
		var url = this.modifyActionUrl;
		var params = new Object();
		var dialogs = $(document).setOpen();
		params.elecPrice = value;
		params.oid = $("input[name=oid]").val();

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
	},

	setKekState : function(obj) {
		var url = this.modifyActionUrl;
		var params = new Object();
		var dialogs = $(document).setOpen();
		params.kekState = obj.value;
		params.oid = $("input[name=oid]").val();

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
						// document.forms[0].submit();
					}
				}
			})
		}, true);
	},

	createPartListMaster : function(obj) {
		$poid = $(obj).data("poid");
		$progress = $(obj).data("progress");
		$tname = $(obj).data("loc").substring(0, 2);
		var url = "/Windchill/plm/partList/createPartListMaster?popup=true&poid=" + $poid + "&tname=" + $tname + "&progress=" + $progress;
		$(document).openURLViewOpt(url, 1600, 700, "no");
	},

	setUser : function(obj) {
		var poid = $("input[name=oid]").val();
		var url = "/Windchill/plm/project/setUser?popup=true&oid=" + poid;
		$(document).openURLViewOpt(url, 470, 370, "no");
	},

	setProgress : function(obj) {
		var dialogs = $(document).setOpen();
		var prompt = $(document).prompt();
		var progress = $(obj).data("progress");
		var url = this.setProgressActionUrl;
		var oid = $("input[name=oid]").val();
		prompt.prompt({
			input : {
				pro : {
					label : "현재 태스크 진행률 : " + progress + "%",
					required : true
				},
			},
			theme : "info",
			title : "태스크 진행률 입력"
		}, function() {

			if (this.input.pro > 100) {
				mask.close();
				return false;
			}

			if (this.key == "ok" || this.key == "enter") {

				var taskProgress = this.input.pro;
				var params = new Object();
				params.progress = taskProgress;
				params.oid = oid;
				$(document).ajaxCallServer(url, params, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						msg : data.msg
					}, function() {
						if (this.key == "ok" || this.state == "close") {
							document.location.reload();
						}
					})
				})
			}

			if (this.key == "cancel") {
				mask.close();
			}
		})
	},

	setUserAction : function() {
		var url = this.setUserActionUrl;
		var params = new Object();
		var dialogs = $(document).setOpen();
		var popup = $("input[name=popup]").val();
		params.oid = $("input[name=oid]").val();
		params.pmOid = $("input[name=pmOid]").val();
		params.subpmOid = $("input[name=sub_pmOid]").val();
		params.machineOid = $("input[name=machineOid]").val();
		params.elecOid = $("input[name=elecOid]").val();
		params.softOid = $("input[name=softOid]").val();
		params.kekState = $("select[name=kekState]").val();
		$(document).ajaxCallServer(url, params, function(data) {
			dialogs.alert({
				theme : "alert",
				title : "결과",
				msg : data.msg
			}, function() {
				// 버튼 클릭 ok, esc
				if (this.key == "ok" || this.state == "close") {
					if (data.reload && popup == "false") {
						document.location.href = data.url;
					}

					if (popup == "true") {
						self.close();
						opener.document.location.reload();
					}
				}
			})
		}, true);
	},

	openAddRequestDocumentPopup : function(obj) {
		var dbl = $(obj).data("dbl");
		if (dbl == undefined) {
			dbl = "true";
		}

		var fun = $(obj).data("fun");
		if (fun == undefined) {
			fun = "addDblOutputs";
		}

		var context = $(obj).data("context");
		if (context == undefined) {
			context = "PRODUCT";
		}

		var toid = $(obj).data("oid");

		var url = "/Windchill/plm/project/addRequestDocument?context=" + context + "&dbl=" + dbl + "&fun=" + fun + "&toid=" + toid;
		$(document).openURLViewOpt(url, 1400, 700, "");
	},

	createCodeAction : function() {
		var dialogs = $(document).setOpen();
		var url = this.createCodeActionUrl;
		var params = $(document).getFormParams();
		console.log(params);

		$customer = $("select[name=customer]");
		if ($customer.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "고객사를 선택하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$customer.focus();
				}
			})
			return false;
		}

		$install = $("input[name=install]");
		if ($install.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "설치장소를 입력하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$install.focus();
				}
			})
			return false;
		}

		$(document).ajaxCallServer(url, params, function(data) {
			dialogs.alert({
				theme : "alert",
				title : "결과",
				msg : data.msg
			}, function() {
				// 버튼 클릭 ok, esc
				if (this.key == "ok" || this.state == "close") {
					if (data.reload) {
						document.location.reload();
						// yhkim
						// document.location.href = data.url;
					}
				}
			})
		}, true);
	},

	createRequestDocument : function(obj) {
		$outputLoc = $(obj).data("loc");
		$progress = $(obj).data("progress");
		$poid = $(obj).data("poid");
		$toid = $(obj).data("oid");
		var url = "/Windchill/plm/document/createRequestDocument?popup=true&poid=" + $poid + "&toid=" + $toid;
		$(document).openURLViewOpt(url, 1600, 700, "no");
	},

	viewSchedule : function(obj) {
		// $poid = $("input[name=oid]").val();
		$poid = $(obj).data("oid");
		var url = "/Windchill/plm/project/openProjectTaskCalendar?popup=true&oid=" + $poid;
		$(document).openURLViewOpt(url, 1600, 700, "");
	},

	viewOutput : function(obj) {
		$oid = $(obj).data("oid");
		$task = $(obj).data("task");
		var url = "/Windchill/plm/document/viewOutput?popup=true&oid=" + $oid + "&task=" + $task;
		$(document).openURLViewOpt(url, 1600, 700, "");
	},

	deleteProjectAction : function() {
		var isSelect = $(document).isSelect();
		var dialogs = $(document).setOpen();
		var url = this.deleteActionUrl;
		if (isSelect == false) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "삭제할 작번을 선택하세요."
			})
			return false;
		}

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "선택한 작번을 삭제 하시겠습니까?\n삭제한 데이터는 복구가 불가능합니다."
		}, function() {
			// 확인
			if (this.key == "ok") {
				var arr = new Array();
				var params = $(document).getListParams();

				$oid = $("input[name=oid]");

				if ($oid.length == 0) {
					return false;
				}

				$.each($oid, function(idx) {
					if ($oid.eq(idx).prop("checked") == true) {
						arr.push($oid.eq(idx).val());
					}
				})

				params.list = arr;

				$(document).ajaxCallServer(url, params, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						msg : data.msg
					}, function() {
						if (this.key == "ok" || this.state == "close") {
							// document.location.reload();
							reloadPage();
						}
					})
				}, true);
				// 취소 or esc
			} else if (this.key == "cancel" || this.state == "close") {
				mask.close();
			}
		})
	},

	completeStepAction : function() {
		var step = $("#stateStep").val();
		var dialogs = $(document).setOpen();
		var url = "/Windchill/plm/project/completeStepAction";
		if (step == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "완료할 단계를 선택하세요."
			})
			return false;
		}

		// mask.open();
		// $("#loading_layer").show();

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : step + "단계를 완료 처리 합니다."
		}, function() {

			if (this.key == "ok") {
				var params = new Object();
				params = $(document).getListParams("list", "input[name=oid]");
				params.step = step;
				$(document).ajaxCallServer(url, params, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						msg : data.msg
					}, function() {
						// 버튼 클릭 ok, esc
						if (this.key == "ok" || this.state == "close") {
							if (data.reload) {
								// document.location.href = data.url;
								document.location.reload();
							}
						}
					})
				}, true);
			}

			if (this.key == "cancel" || this.state == "close") {
				mask.close();
			}
		})
	}
}

$(document).ready(function() {

	$(document).bind("click", "input[name=outputOid]", function(e) {
		projects.bindBox(e);
	})

	$(document).bind("click", "input[name=projectOid]", function(e) {
		projects.bindBox2(e);
	})

	$(document).bind("click", "input[name=issueOid]", function(e) {
		projects.bindBox3(e);
	})

	$("#allProjects").click(function(e) {
		projects.allProjects(this, e);
	})

	$("#delOutputs").click(function() {
		projects.delOutputsAction();
	})

	$("#setUser").click(function() {
		projects.setUserAction();
	})

	$("#addOutputs").click(function() {
		projects.openAddOutputPopup(this);
	})

	$("#addProjects").click(function() {
		projects.openAddPopup(this);
	})

	$("#addIssueProjects").click(function() {
		projects.openAddPopup(this);
	})

	$("#createProjectBtn").click(function() {
		projects.createProject();
	})

	$("#delProjects").click(function() {
		projects.delProjects();
	})

	$("#allOutputs").click(function(e) {
		projects.allOutputs(this, e);
	})

	$("#allIssues").click(function(e) {
		projects.allIssues(this, e);
	})

	$("#startProject").click(function() {
		projects.startProject(this);
	})

	$("#restartProject").click(function() {
		projects.restartProject(this);
	})

	$("#stopProject").click(function() {
		projects.stopProject(this);
	})

	$("#completeProject").click(function() {
		projects.completeProject(this);
	})

	$("#addIssue").click(function() {
		projects.addIssue(this);
	})

	$("#delIssue").click(function() {
		projects.delIssueAction(this);
	})

	$("#createIssueAction").click(function() {
		projects.createIssueAction(this);
	})

	$("#modifyIssueActionBtn").click(function() {
		projects.modifyIssueAction(this);
	})

	$("#modifyProjectBtn").click(function() {
		projects.modifyProjectPopup(this);
	})

	$("#modifyProjectActionBtn").click(function() {
		projects.modifyProjectAction(this);
	})

	$("#closeProjectBtn").click(function() {
		self.close();
	})

	$("#completeTaskBtn").click(function() {
		projects.completeTask();
	})

	$(".elecPrice").click(function() {
		projects.elecPrice();
	})

	$(".machinePrice").click(function() {
		projects.machinePrice();
	})

	$("#createPartListMaster").click(function() {
		projects.createPartListMaster(this);
	})

	$(".progress").click(function() {
		projects.setProgress(this);
	}).mouseover(function() {
		$(this).css("cursor", "pointer");
	})

	$(".pm").add(".subpm").add(".machine").add(".elec").add(".soft").click(function() {
		projects.setUser(this);
	})

	$("#viewSchedule").click(function() {
		projects.viewSchedule(this);
	})

	$("#addRequestDocuments").click(function() {
		projects.openAddRequestDocumentPopup(this);
	})

	$("#createRequestDocument").click(function() {
		projects.createRequestDocument(this);
	})

	$(".viewProject").click(function() {
		projects.viewProject(this);
	})

	$(".viewOutput").click(function() {
		projects.viewOutput(this);
	})

	$("#deleteProjectBtn").click(function() {
		projects.deleteProjectAction();
	})

	$("#createCode").click(function() {
		projects.createCodeAction();
	})

	$("#completeStep").click(function() {
		projects.completeStepAction();
	})

	// $("input[name=name]").attr("maxlength", 3);
	$("input[data-dialog-prompt=pro]").attr("maxlength", 3);

	$("#pType").bindSelect();
})