/**
 * 결재 전용 javascript
 */

var approvals = {
	listNoticeUrl : "/Windchill/plm/approval/listNotice",

	baseSeriesUrl : "/Windchill/plm/org/addLine?lineType=series",

	baseParallelUrl : "/Windchill/plm/org/addLine?lineType=parallel",

	createNoticeUrl : "/Windchill/plm/approval/createNotice",

	createNoticeActionUrl : "/Windchill/plm/approval/createNoticeAction",

	modifyNoticeUrl : "/Windchill/plm/approval/modifyNotice",

	viewNoticeUrl : "/Windchill/plm/approval/viewNotice",

	modifyNoticeActionUrl : "/Windchill/plm/approval/modifyNoticeAction",

	deleteNoticeActionUrl : "/Windchill/plm/approval/deleteNoticeAction",

	approvalActionUrl : "/Windchill/plm/approval/approvalAction",

	returnActionUrl : "/Windchill/plm/approval/returnAction",

	agreeActionUrl : "/Windchill/plm/approval/agreeAction",

	unagreeActionUrl : "/Windchill/plm/approval/unagreeAction",

	receiveActionUrl : "/Windchill/plm/approval/receiveAction",

	skipActionUrl : "/Windchill/plm/approval/skipApprovalAction",

	initAppActionUrl : "/Windchill/plm/approval/initApprovalAction",

	initAppLineActionUrl : "/Windchill/plm/approval/initApprovalLineAction",

	reassignActionUrl : "/Windchill/plm/approval/reassignApprovalAction",
	
	deleteReturnActionUrl : "/Windchill/plm/approval/deleteReturnAction",

	closeNotice : function() {
		self.close();
	},

	modifyNoticeAction : function() {

		var dialogs = $(document).setOpen();
		var url = this.modifyNoticeActionUrl;

		// 문서 제목
		$name = $("input[name=name]");
		if ($name.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "공지사항 제목을 입력하세요."
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
			msg : "공지사항을 수정 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 등록 진행
				// 일반 문서 값
				var params = $(document).getFormParams();

				params.oid = $("input[name=oid]").val();

				$(document).ajaxCallServer(url, params, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						msg : data.msg
					}, function() {
						$popup = $("input[name=popup]").val();
						if (data.reload) {
							if ($popup == "true") {
								self.close();
								opener.document.location.href = data.url;
							} else if ($popup == "false") {
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
	
	
	deleteReturn : function() {
		var dialogs = $(document).setOpen();
		var url = this.deleteReturnActionUrl;
		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "반려된 결재 정보를 삭제 하시겠습니까?\n삭제한 데이터는 복구가 불가능합니다."
		}, function() {
			// 확인
			if (this.key == "ok") {
				var oid = $("input[name=oid]").val();
				var arr = new Array();
				var params = new Object();
				arr.push(oid);
				params.list = arr;
				// params.list = value List<String> 구조
				$(document).ajaxCallServer(url, params, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						msg : data.msg
					}, function() {
						if (this.key == "ok" || this.state == "close") {
							$popup = $("input[name=popup]").val();
							if ($popup == "true") {
								self.close();
								opener.document.location.href = data.url;
							} else if ($popup == "false") {
								document.location.href = data.url;
							}
						}
					})
				}, true);
				// 취소 or esc
			} else if (this.key == "cancel" || this.state == "close") {
				mask.close();
			}
		})
	},

	listNotice : function() {
		mask.open();
		$("#loading_layer").show();
		document.location.href = this.listNoticeUrl;
	},

	initAppTable : function() {
		// 병렬 버튼
		$("#parallel_add").hide();
		// 병렬 테이블
		$("#create_parallel_table").hide();
	},

	reversToSeries : function() {
		var dialogs = $(document).setOpen();
		dialogs.confirm({
			theme : "info",
			title : "확인",
			msg : "지정된 병렬 결재라인이 모두 삭제 되어집니다.\n계속 하시겠습니까?",
			width : 380
		}, function() {
			$("#series_add").show();
			// 병렬 테이블
			$("#create_series_table").show();

			$("#parallel_add").hide();
			// 병렬 테이블
			$("#create_parallel_table").hide();

			$(".lineMsg").text("직렬");

			var body = $("#addLineBody_parallel");
			body.empty();
			var html = "";
			html += "<tr id=\"nodataParallelLine\">";
			html += "<td class=\"nodata\" colspan=\"7\">지정된 병렬 결재라인이 없습니다.</td>";
			html += "</tr>";

			body.append(html);

			$container = $(document).find("#app_container");
			if ($container.hasClass("app_container")) {
				$container.removeClass("app_container");
			}

			$("input[name=allLines").prop("checked", false);
			$("input[name=allLines").next().removeClass("sed");

			$lineType = $("input[name=lineType]");
			$.each($lineType, function(idx) {
				if (!$lineType.eq(idx).hasClass("hidden")) {
					$lineType.eq(idx).addClass("hidden");
				} else {
					$lineType.eq(idx).removeClass("hidden");
				}
			})
		})
	},

	reversToParallel : function() {
		// 병렬로 전환
		var dialogs = $(document).setOpen();
		dialogs.confirm({
			theme : "info",
			title : "확인",
			msg : "지정된 직렬 결재라인이 모두 삭제 되어집니다.\n계속 하시겠습니까?",
			width : 380
		}, function() {
			if (this.key == "ok") {
				$("#series_add").hide();
				// 병렬 테이블
				$("#create_series_table").hide();

				$("#parallel_add").show();
				// 병렬 테이블
				$("#create_parallel_table").show();

				$(".lineMsg").text("병렬");

				var body = $("#addLineBody_series");
				body.empty();
				var html = "";
				html += "<tr id=\"nodataSeriesLine\">";
				html += "<td class=\"nodata\" colspan=\"7\">지정된 직렬 결재라인이 없습니다.</td>";
				html += "</tr>";

				body.append(html);

				$container = $(document).find("#app_container");
				if ($container.hasClass("app_container")) {
					$container.removeClass("app_container");
				}
				$("input[name=allLines").prop("checked", false);
				$("input[name=allLines").next().removeClass("sed");

				$lineType = $("input[name=lineType]");
				$.each($lineType, function(idx) {
					if (!$lineType.eq(idx).hasClass("hidden")) {
						$lineType.eq(idx).addClass("hidden");
					} else {
						$lineType.eq(idx).removeClass("hidden");
					}
				})
			}
		})
	},

	applyLine : function() {
		var dialogs = $(document).setOpen();
		$appOid = $("input[name=appOid]");

		$allLines = $("input[name=allLines]", opener.document);
		$allLines.prop("checked", false);
		$allLines.next().removeClass("sed");

//		$lineType = $("input[name=lineType]", opener.document);
//		$bool = false;
//		$.each($lineType, function(idx) {
//			if (!$lineType.eq(idx).hasClass("hidden")) {
//				$bool = $lineType.eq(idx).val();
//			}
//		})

		if ($appOid.length == 0) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "최소 한명 이상의 결재자를 지정하세요."
			})
			return false;
		}

		var appType = $("input[name=type]").val();
//		if (appType != $bool) {
//			dialogs.alert({
//				theme : "alert",
//				title : "경고",
//				msg : "지정하려는 결재타입이 상이합니다."
//			})
//			return false;
//		}
		// 직렬
		var lineBody;
		$lineType.val($bool);
		$container = $(opener.document).find("#app_container");
		if (!$container.hasClass("app_container") && $appOid.length >= 8) {
			$container.addClass("app_container");
		}

		mask.open();
		$("#loading_layer").show();
		if (appType == "series") {
			lineBody = $("#addLineBody_series", opener.document);
			for (var i = 0; i < lineBody.length; i++) {
				lineBody.eq(i).children().remove();
			}

			$cnt = 0;
			$appValue = $("input[name=appValue]");

			// 검토
			$agreeValue = $("input[name=agreeValue]");
			for (var i = 0; i < $agreeValue.length; i++) {
				$value = $agreeValue.eq(i).val();
				// 0 oid, 1 id, 2 name, 3 duty, 4 depart
				$oid = $value.split("&")[0];
				$id = $value.split("&")[1];
				$name = $value.split("&")[2];
				$duty = $value.split("&")[3];
				$depart = $value.split("&")[4];

				$html = "<tr>";
				$html += "<td>";
				$html += "<input type=\"checkbox\" name=\"lines\">";
				$html += "<input type=\"hidden\" name=\"agreeUserOid\" value=\"" + $oid + "\">";
				$html += "<input type=\"hidden\" name=\"agreeUserInfo\" value=\"" + $value + "\">";
				// hidden value
				$html += "</td>";
				$html += "<td>&nbsp;</td>";
				$html += "<td><font color=\"green\">검토</font></td>";
				$html += "<td>" + $id + "</td>";
				$html += "<td>" + $name + "</td>";
				$html += "<td>" + $duty + "</td>";
				$html += "<td>" + $depart + "</td>";
				$html += "</tr>";
				lineBody.append($html);
			}
			for (var i = 0; i < $appValue.length; i++) {
				$value = $appValue.eq(i).val();
				// 0 oid, 1 id, 2 name, 3 duty, 4 depart
				$oid = $value.split("&")[0];
				$id = $value.split("&")[1];
				$name = $value.split("&")[2];
				$duty = $value.split("&")[3];
				$depart = $value.split("&")[4];

				$html = "<tr>";
				$html += "<td>";
				$html += "<input type=\"checkbox\" name=\"lines\">";
				$html += "<input type=\"hidden\" name=\"appUserOid\" value=\"" + $oid + "\">";
				$html += "<input type=\"hidden\" name=\"appUserInfo\" value=\"" + $value + "\">";
				// hidden value
				$html += "</td>";
				$html += "<td>" + ++$cnt + "</td>";
				$html += "<td><font color=\"blue\">결재</font></td>";
				$html += "<td>" + $id + "</td>";
				$html += "<td>" + $name + "</td>";
				$html += "<td>" + $duty + "</td>";
				$html += "<td>" + $depart + "</td>";
				$html += "</tr>";
				lineBody.append($html);
			}
			// 병렬
		} else if (appType == "parallel") {
			lineBody = $("#addLineBody_parallel", opener.document);
			for (var i = 0; i < lineBody.length; i++) {
				lineBody.eq(i).children().remove();
			}

			$appValue = $("input[name=appValue]");
			for (var i = 0; i < $appValue.length; i++) {
				$value = $appValue.eq(i).val();
				// 0 oid, 1 id, 2 name, 3 duty, 4 depart
				$oid = $value.split("&")[0];
				$id = $value.split("&")[1];
				$name = $value.split("&")[2];
				$duty = $value.split("&")[3];
				$depart = $value.split("&")[4];

				$html = "<tr>";
				$html += "<td>";
				$html += "<input type=\"checkbox\" name=\"lines\">";
				$html += "<input type=\"hidden\" name=\"appUserOid\" value=\"" + $oid + "\">";
				$html += "<input type=\"hidden\" name=\"appUserInfo\" value=\"" + $value + "\">";
				// hidden value
				$html += "</td>";
				$html += "<td><font color=\"blue\">결재</font></td>";
				$html += "<td>" + $id + "</td>";
				$html += "<td>" + $name + "</td>";
				$html += "<td>" + $duty + "</td>";
				$html += "<td>" + $depart + "</td>";
				$html += "</tr>";
				lineBody.append($html);
			}
		}

		// 수신
		$receiveValue = $("input[name=receiveValue]");
		for (var i = 0; i < $receiveValue.length; i++) {
			$value = $receiveValue.eq(i).val();
			// 0 oid, 1 id, 2 name, 3 duty, 4 depart
			$oid = $value.split("&")[0];
			$id = $value.split("&")[1];
			$name = $value.split("&")[2];
			$duty = $value.split("&")[3];
			$depart = $value.split("&")[4];

			$html = "<tr>";
			$html += "<td>";
			$html += "<input type=\"checkbox\" name=\"lines\">";
			$html += "<input type=\"hidden\" name=\"receiveUserOid\" value=\"" + $oid + "\">";
			$html += "<input type=\"hidden\" name=\"receiveUserInfo\" value=\"" + $value + "\">";
			// hidden value
			$html += "</td>";
			$html += "<td>&nbsp;</td>";
			$html += "<td><font color=\"red\">수신</font></td>";
			$html += "<td>" + $id + "</td>";
			$html += "<td>" + $name + "</td>";
			$html += "<td>" + $duty + "</td>";
			$html += "<td>" + $depart + "</td>";
			$html += "</tr>";
			lineBody.append($html);
		}

		opener.setBoxs("input[name=lines]");
		approvals.setBoxs();

		mask.close();
		$("#loading_layer").hide();
		self.close();
	},

	setBoxs : function() {
		$boxs = $(opener.document).find("input[name=lines]");
		$.each($boxs, function(idx) {
			if (!$boxs.eq(idx).hasClass("isBox")) {
				$boxs.eq(idx).addClass("isBox");
			}
		})
	},

	delLine : function() {
		var isSelect = $(document).isSelectParams("lines");
		var dialogs = $(document).setOpen();
		$oid = $("input[name=lines]");
		if ($oid.length == 0) {
			return false;
		}

		if (isSelect == false) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "삭제할 결재라인을 선택하세요."
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

		$appType = $("input[name=lineType").val();

		if ($len < 8 && $("#app_container").hasClass("app_container")) {
			$("#app_container").removeClass("app_container");
		}

		if ($appType == "series") {
			if ($len == 0) {
				if ($("#app_container").hasClass("app_container")) {
					$("#app_container").removeClass("app_container");
				}
				var body = $("#addLineBody_series");
				var html = "";
				html += "<tr id=\"nodataSeriesLine\">";
				html += "<td class=\"nodata\" colspan=\"7\">지정된 직렬 결재라인이 없습니다.</td>";
				html += "</tr>";
				body.append(html);

				$("input[name=allLines]").prop("checked", false);
				$("input[name=allLines]").next().removeClass("sed");
			}
		} else if ($appType == "parallel") {
			if ($len == 0) {
				if ($("#app_container").hasClass("app_container")) {
					$("#app_container").removeClass("app_container");
				}

				var body = $("#addLineBody_parallel");
				var html = "";
				html += "<tr id=\"nodataParallelLine\">";
				html += "<td class=\"nodata\" colspan=\"6\">지정된 병렬 결재라인이 없습니다.</td>";
				html += "</tr>";
				body.append(html);

				$("input[name=allLines]").prop("checked", false);
				$("input[name=allLines]").next().removeClass("sed");
			}
		}
	},

	allLines : function(obj, e) {
		$oid = $("input[name=lines]");

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

	bindBox : function() {
		$bool = false;
		$oid = $("input[name=lines");
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
			$("input[name=allLines]").prop("checked", true);
			$("input[name=allLines]").next().addClass("sed");
		}

		if ($bool) {
			$("input[name=allLines]").prop("checked", false);
			$("input[name=allLines]").next().removeClass("sed");
		}
	},

	createNotice : function() {
		mask.open();
		$("#loading_layer").show();
		document.location.href = this.createNoticeUrl;
	},

	createNoticeAction : function() {
		var dialogs = $(document).setOpen();
		var url = this.createNoticeActionUrl;

		// 문서 제목
		$name = $("input[name=name]");
		if ($name.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "공지사항 제목을 입력하세요."
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
			msg : "공지사항을 등록 하시겠습니까?"
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

	initApprovalLineAction : function() {
		var isSelect = $(document).isSelect();
		var dialogs = $(document).setOpen();
		var url = this.initAppLineActionUrl;

		if (isSelect == false) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "초기화할 결재를 선택하세요."
			})
			return false;
		}

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "선택한 결재를 초기화 하시겠습니까?\n초기화한 결재는 복구가 불가능합니다."
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
						// $len--;
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
							document.location.reload();
						}
					})
				}, true);
				// 취소 or esc
			} else if (this.key == "cancel" || this.state == "close") {
				mask.close();
			}
		})
	},

	initApprovalAction : function() {
		var isSelect = $(document).isSelect();
		var dialogs = $(document).setOpen();
		var url = this.initAppActionUrl;

		if (isSelect == false) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "초기화할 결재를 선택하세요."
			})
			return false;
		}

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "선택한 결재를 초기화 하시겠습니까?\n초기화한 결재는 복구가 불가능합니다."
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
						// $len--;
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
							document.location.reload();
						}
					})
				}, true);
				// 취소 or esc
			} else if (this.key == "cancel" || this.state == "close") {
				mask.close();
			}
		})
	},

	deleteListNotice : function() {
		var isSelect = $(document).isSelect();
		var dialogs = $(document).setOpen();
		var url = this.deleteNoticeActionUrl;
		if (isSelect == false) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "삭제할 공지사항을 선택하세요."
			})
			return false;
		}

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "선택한 공지사항을 삭제 하시겠습니까?\n삭제한 데이터는 복구가 불가능합니다."
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
						// $len--;
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
							document.location.reload();
						}
					})
				}, true);
				// 취소 or esc
			} else if (this.key == "cancel" || this.state == "close") {
				mask.close();
			}
		})
	},

	deleteNoticeAction : function() {
		var dialogs = $(document).setOpen();
		var url = this.deleteNoticeActionUrl;
		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "공지사항을 삭제 하시겠습니까?\n삭제한 데이터는 복구가 불가능합니다."
		}, function() {
			// 확인
			if (this.key == "ok") {
				var oid = $("input[name=oid]").val();
				var arr = new Array();
				var params = new Object();
				arr.push(oid);
				params.list = arr;
				// params.list = value List<String> 구조
				$(document).ajaxCallServer(url, params, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						msg : data.msg
					}, function() {
						if (this.key == "ok" || this.state == "close") {
							$popup = $("input[name=popup]").val();
							if ($popup == "true") {
								self.close();
								opener.document.location.href = data.url;
							} else if ($popup == "false") {
								document.location.href = data.url;
							}
						}
					})
				}, true);
				// 취소 or esc
			} else if (this.key == "cancel" || this.state == "close") {
				mask.close();
			}
		})
	},

	modifyNotice : function() {
		mask.open();
		$("#loading_layer").show();
		$oid = $("input[name=oid]").val();
		$popup = $("input[name=popup]").val();
		document.location.href = this.modifyNoticeUrl + "?oid=" + $oid + "&popup=" + $popup;
	},

	approvalAction : function() {
		var dialogs = $(document).setOpen();
		var url = this.approvalActionUrl;

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "결재를 승인 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 등록 진행
				// 일반 문서 값
				var params = new Object();
				params.oid = $("input[name=oid]").val();
				params.description = $("#description").val();
				$(document).ajaxCallServer(url, params, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						msg : data.msg
					}, function() {
						// 버튼 클릭 ok, esc
						$popup = $("input[name=popup]").val();
						if ($popup == "true") {
							if (data.reload) {
								self.close();
//								opener.document.location.href = data.url;
//								$(document).getColumn();
								opener.parent.search();
							}
						} else if ($popup == "false") {
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

	returnAction : function() {
		var dialogs = $(document).setOpen();
		var url = this.returnActionUrl;

		var description = $("#description").val();
		if (description == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "반려 의견을 입력하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$("#description").focus();
				}
			})
			return false;
		}

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "결재를 반려 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 등록 진행
				// 일반 문서 값
				var params = new Object();
				params.oid = $("input[name=oid]").val();
				params.description = $("#description").val();
				$(document).ajaxCallServer(url, params, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						msg : data.msg
					}, function() {
						// 버튼 클릭 ok, esc
						$popup = $("input[name=popup]").val();
						if ($popup == "true") {
							if (data.reload) {
								self.close();
//								opener.document.location.href = data.url;
//								$(document).getColumn();
								opener.parent.search();
							}
						} else if ($popup == "false") {
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

	agreeAction : function() {
		var dialogs = $(document).setOpen();
		var url = this.agreeActionUrl;

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "결재를 검토완료 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 등록 진행
				// 일반 문서 값
				var params = new Object();
				params.oid = $("input[name=oid]").val();
				params.description = $("#description").val();
				$(document).ajaxCallServer(url, params, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						msg : data.msg
					}, function() {
						// 버튼 클릭 ok, esc
						$popup = $("input[name=popup]").val();
						if ($popup == "true") {
							if (data.reload) {
								self.close();
//								opener.document.location.href = data.url;
//								$(document).getColumn();
								opener.parent.search();
							}
						} else if ($popup == "false") {
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

	unagreeAction : function() {
		var dialogs = $(document).setOpen();
		var url = this.unagreeActionUrl;

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "결재를 불합의 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 등록 진행
				// 일반 문서 값
				var params = new Object();
				params.oid = $("input[name=oid]").val();
				params.description = $("#description").val();
				$(document).ajaxCallServer(url, params, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						msg : data.msg
					}, function() {
						// 버튼 클릭 ok, esc
						$popup = $("input[name=popup]").val();
						if ($popup == "true") {
							if (data.reload) {
								self.close();
//								opener.document.location.href = data.url;
//								$(document).getColumn();
								opener.parent.search();
							}
						} else if ($popup == "false") {
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

	receiveAction : function() {
		var dialogs = $(document).setOpen();
		var url = this.receiveActionUrl;

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "결재를 수신확인 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 등록 진행
				// 일반 문서 값
				var params = new Object();
				params.oid = $("input[name=oid]").val();
				params.description = $("#description").val();
				$(document).ajaxCallServer(url, params, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						msg : data.msg
					}, function() {
						// 버튼 클릭 ok, esc
						$popup = $("input[name=popup]").val();
						if ($popup == "true") {
							if (data.reload) {
								self.close();
//								opener.document.location.href = data.url;
//								$(document).getColumn();
								opener.parent.search();
							}
						} else if ($popup == "false") {
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

	listApp : function() {
		$("#loading_layer").show();
		mask.open();
		document.location.href = "/Windchill/plm/approval/listApproval";
	},

	listAgree : function() {
		$("#loading_layer").show();
		mask.open();
		document.location.href = "/Windchill/plm/approval/listAgree";
	},

	listReceive : function() {
		$("#loading_layer").show();
		mask.open();
		document.location.href = "/Windchill/plm/approval/listReceive";
	},

	listIng : function() {
		$("#loading_layer").show();
		mask.open();
		document.location.href = "/Windchill/plm/approval/listIng";
	},

	listComplete : function() {
		$("#loading_layer").show();
		mask.open();
		document.location.href = "/Windchill/plm/approval/listComplete";
	},

	listReturn : function() {
		$("#loading_layer").show();
		mask.open();
		document.location.href = "/Windchill/plm/approval/listReturn";
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
					$data = $("#descriptionNotice").val().substring(0, 1000);
					$("#descriptionNotice").val($data);
					$("#descriptionNotice").focus();
					$("#descNoticeCnt").text($data.length);
				}
			})
		}
		$("#descNoticeCnt").text(len);
	},

	listNotice : function() {
		$(document).onLayer();
		document.location.href = this.listNoticeUrl;
	},

	backNotice : function(obj) {
		var url = this.viewNoticeUrl;
		var box = $(document).setOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "작성 중이던 내용은 모두 삭제 됩니다.\n진행 하시겠습니까?"
		}, function() {
			if (this.key == "ok") {
				$oid = $(obj).data("oid");
				$(document).onLayer();
				document.location.href = url + "?oid=" + $oid;
			}
		})
	},

	skipApprovalAction : function(obj) {
		var dialogs = $(document).setOpen();
		var url = this.skipActionUrl;

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "결재를 스킵??? 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 등록 진행
				// 일반 문서 값
				var params = new Object();
				params.oid = $(obj).data("oid");
				$(document).ajaxCallServer(url, params, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						msg : data.msg
					}, function() {
						document.location.href = data.url;
					})
				}, true);
			}

			if (this.key == "cancel" || this.state == "close") {
				mask.close();
			}
		})
	},

	reassignApprovalAction : function(obj) {
		var dialogs = $(document).setOpen();
		var url = this.reassignActionUrl;

		var reassignUser = $("input[name=reassignUser]").val();
		if (reassignUser == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "위임할 사용자를 선택하세요."
			})
			return false;
		}

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "결재를 위임 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 등록 진행
				// 일반 문서 값
				var params = new Object();
				params.oid = $(obj).data("oid");
				params.reassignUser = $("input[name=reassignUserOid]").val();
				$(document).ajaxCallServer(url, params, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						msg : data.msg,
						width : 380
					}, function() {
						self.close();
						console.log(opener);
						//console.log(opener.parent);
						if(opener!=null){
							opener.parent.search();
						}
						//console.log(opener.parent.search());
						//opener.parent.search();
//						var preUrl = document.referrer;
//						if (preUrl) {
//							document.location.href = preUrl;
//						} else {
//							document.location.href = data.url;
//						}
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

	approvals.initAppTable();

	$("#addLine_series").click(function() {
		var url = approvals.baseSeriesUrl;
		$(document).openURLViewOpt(url, 1100, 630, "no");
	})

	$("#addLine_parallel").click(function() {
		var url = approvals.baseParallelUrl;
		$(document).openURLViewOpt(url, 1100, 630, "");
	})

	// 직렬 전환
	$("#series_table_btn").click(function() {
		approvals.reversToSeries();
	})

	// 병렬 전환
	$("#parallel_table_btn").click(function() {
		approvals.reversToParallel();
	})

	// 결재선 적용
	$("#applyLineBtn").click(function() {
		approvals.applyLine();
	})

	// 결재선 삭제
	$("#delLine").click(function() {
		approvals.delLine();
	})
	
	$("#deleteReturn").click(function(){
		approvals.deleteReturn();
	})

	$("#createNoticeBtn").click(function() {
		approvals.createNotice();
	})

	$("#createNoticeBtnAction").click(function() {
		approvals.createNoticeAction();
	})

	$("#deleteListNoticeBtn").click(function() {
		approvals.deleteListNotice();
	})

	$("#listNoticeBtn").click(function() {
		approvals.listNotice();
	})

	$("#modifyNoticeBtn").click(function() {
		approvals.modifyNotice();
	})

	$("#modifyNoticeBtnAction").click(function() {
		approvals.modifyNoticeAction();
	})

	$("#closeNoticeBtn").click(function() {
		approvals.closeNotice();
	})

	$("#deleteNoticeBtn").click(function() {
		approvals.deleteNoticeAction();
	})

	$("#create_series_table").add("#create_parallel_table").tableHeadFixer();

	$(document).bind("click", "input[name=lines]", function(e) {
		approvals.bindBox();
	})

	$("input[name=allLines]").click(function(e) {
		approvals.allLines(this, e);
	})

	$("#listAppBtn").click(function() {

	})

	$("#approvalBtn").click(function() {
		approvals.approvalAction();
	})

	$("#approvalEBOMBtn").click(function() {
		var dialogs = $(document).setOpen();
		$review = $(this).data("review");
		$overlap = $(this).data("overlap");
		if (!$review) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "E-BOM 검토가 완료 되지 않았습니다.",
				width : 380
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					mask.close();
				}
			})
			return false;
		}

		if (!$overlap) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "E-BOM 중복체크가 완료 되지 않았습니다.",
				width : 380
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					mask.close();
				}
			})
			return false;
		}

		approvals.approvalAction();
	})

	$("#returnBtn").click(function() {
		approvals.returnAction();
	})

	$("#agreeBtn").click(function() {
		approvals.agreeAction();
	})

	$("#unagreeBtn").click(function() {
		approvals.unagreeAction();
	})

	$("#receiveBtn").click(function() {
		approvals.receiveAction();
	})

	$("#listAppBtn").click(function() {
		approvals.listApp();
	})

	$("#listAgreeBtn").click(function() {
		approvals.listAgree();
	})

	$("#listReceiveBtn").click(function() {
		approvals.listReceive();
	})

	$("#listReturn").click(function() {
		approvals.listReturn();
	})

	$("#listIng").click(function() {
		approvals.listIng();
	})

	$("#listComplete").click(function() {
		approvals.listComplete();
	})

	$("#refAppObject_table").tableHeadFixer();

	$("#refAppLine_table").tableHeadFixer();

	$("#descriptionNotice").keyup(function() {
		approvals.count(this);
	})

	$("#listNoticeBtn").click(function() {
		approvals.listNotice();
	})

	$("#backNoticeBtn").click(function() {
		approvals.backNotice(this);
	})

	$("#skipApproval").click(function() {
		approvals.skipApprovalAction(this);
	})

	$("#reassignApprovalBtn").click(function() {
		approvals.reassignApprovalAction(this);
	})

	$("#initAppBtn").click(function() {
		approvals.initApprovalAction(this);
	})

	$("#initAppLineBtn").click(function() {
		approvals.initApprovalLineAction(this);
	})
})