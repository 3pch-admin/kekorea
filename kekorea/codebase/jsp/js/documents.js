/**
 * 문서 전용 javascript
 */

var documents = {

	root : "/Default/문서",

	output_root : "/Default/프로젝트",

	listUrl : "/Windchill/plm/document/listDocument",

	createUrl : "/Windchill/plm/document/createDocument",

	modifyUrl : "/Windchill/plm/document/modifyDocument",

	modifyOutputUrl : "/Windchill/plm/document/modifyOutput",

	modifyOutputActionUrl : "/Windchill/plm/document/modifyOutputAction",

	versionUrl : "/Windchill/plm/common/infoVersion",

	viewDocumentUrl : "/Windchill/plm/document/viewDocument",

	addDocumentActionUrl : "/Windchill/plm/document/addDocumentAction",

	createActionUrl : "/Windchill/plm/document/createDocumentAction",

	deleteActionUrl : "/Windchill/plm/document/deleteDocumentAction",

	modifyActionUrl : "/Windchill/plm/document/modifyDocumentAction",

	approvalActionUrl : "/Windchill/plm/document/approvalDocumentAction",

	createOutputActionUrl : "/Windchill/plm/document/createOutputAction",

	addDocumentsAction : function(state) {
		var dialogs = $(document).setOpen();
		$len = $("input[name=oid]").length;
		if ($len == 0) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "추가할 문서를 먼저 검색하세요."
			})
			return false;
		}

		var isSelect = $(document).isSelect();
		if (isSelect == false) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "추가할 문서를 선택하세요"
			})
			return false;
		}

		var url = this.addDocumentActionUrl;
		var params = $(document).getListParams("list", "input[name=oid]");

		mask.open();
		$("#loading_layer").show();

		$(document).ajaxCallServer(url, params, function(data) {
			documents.addDocuments(data.list, state);
		}, false);
	},

	addDocuments : function(list, state) {
		$len = list.length;
		var html = "";
		// var body = $("#addPartsBody");
		var body = $(opener.document).find("#addDocumentsBody");
		$plen = $(opener.document).find("input[name=docOid]");
		$container = $(opener.document).find("#documents_container");

		for (var i = 0; i < $len; i++) {
			$bool = true;
			$.each($plen, function(idx) {
				if ($plen.eq(idx).val() == list[i][0]) {
					$bool = false;
				}
			})
			// 상태
			if (list[i][3].split("$")[1] != state) {
				continue;
			}
			//
			// if (!$bool) {
			// continue;
			// }

			// 0 oid, 1 number, 2 name, 3 state, 4 version, 5 creator, 6
			// createdate
			$(opener.document).find("#nodataDocuments").remove();
			html += "<tr>";
			html += "<td><input type=\"checkbox\" name=\"docOid\" value=\""
					+ list[i][0] + "\"></td>";
			html += "<td class=\"infoDocs left\" data-oid=\"" + list[i][0]
					+ "\"><img class=\"pos3\" src=\"" + list[i][7]
					+ "\">&nbsp;" + list[i][1] + "</td>";
			html += "<td class=\"infoDocs left\" data-oid=\"" + list[i][0]
					+ "\"><img class=\"pos3\" src=\"" + list[i][7]
					+ "\">&nbsp;" + list[i][2] + "</td>";
			html += "<td>" + list[i][4] + "</td>";
			html += "<td>" + list[i][3].split("$")[0] + "</td>";
			html += "<td>" + list[i][5] + "</td>";
			html += "<td>" + list[i][6] + "</td>";
			html += "</tr>";
		}
		body.append(html);

		opener.setBoxs("input[name=docOid]");
		documents.setBoxs();

		$opener = $(opener.document).find("input[name=docOid]");
		if (!$container.hasClass("documents_container") && $opener.length >= 6) {
			$container.addClass("documents_container");
		}

		mask.close();
		$("#loading_layer").hide();
	},

	addDblDocuments : function(obj, value, state) {

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

		var url = this.addDocumentActionUrl;
		var params = $(document).getDblFromData(value);
		mask.open();
		$("#loading_layer").show();

		$(document).ajaxCallServer(url, params, function(data) {
			documents.addDocuments(data.list, state);
		}, false);
	},

	setBoxs : function() {
		$boxs = $(opener.document).find("input[name=docOid]");
		$.each($boxs, function(idx) {
			if (!$boxs.eq(idx).hasClass("isBox")) {
				$boxs.eq(idx).addClass("isBox");
			}
		})
	},

	infoVersion : function() {
		$oid = $("input[name=oid]").val();
		$url = this.versionUrl + "?oid=" + $oid;
		$(document).openURLViewOpt($url, 800, 400, "no");
	},

	modify : function() {
		mask.open();
		$("#loading_layer").show();
		$oid = $("input[name=oid]").val();
		$popup = $("input[name=popup]").val();
		document.location.href = this.modifyUrl + "?oid=" + $oid + "&popup="
				+ $popup;
	},

	modifyOutput : function(obj) {
		mask.open();
		$("#loading_layer").show();
		$oid = $("input[name=oid]").val();
		$popup = $("input[name=popup]").val();
		$task = $(obj).data("task");
		document.location.href = this.modifyOutputUrl + "?oid=" + $oid
				+ "&popup=" + $popup + "&task=" + $task;
	},

	createOutput : function(obj) {
		$outputLoc = $(obj).data("loc");
		$progress = $(obj).data("progress");
		$poid = $(obj).data("poid");
		$ptype = $(obj).data("ptype");
		var url = "/Windchill/plm/document/createOutput?popup=true&outputLoc="
				+ this.output_root + "/" + $outputLoc + "&poid=" + $poid
				+ "&progress= " + $progress + "&ptype=" + $ptype;
		$(document).openURLViewOpt(url, 1600, 700, "no");
	},

	viewOutput : function(obj) {
		$outputLoc = $(obj).data("loc");
		var url = "/Windchill/plm/document/viewOutput?popup=true";
		$(document).openURLViewOpt(url, 1600, 700, "no");
	},

	list : function() {
		mask.open();
		$("#loading_layer").show();
		document.location.href = this.listUrl;
	},

	createDocument : function(obj) {
		var dialogs = $(document).setOpen();
		var url = this.createActionUrl;
		$root = $("#locationStr").text();
		if ($root == this.root) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "저장위치를 선택하세요."
			}, function() {
				var url = "/Windchill/plm/common/openFolder?popup=true&root="
						+ documents.root + "&context=PRODUCT";
				if (this.key == "ok") {
					$(document).openURLViewOpt(url, 400, 400, "no");
				}
				if (this.state == "close") {
					$(document).openURLViewOpt(url, 400, 400, "no");
				}
			})
			return false;
		}

		// 문서 제목
		$name = $("input[name=name]");
		if ($name.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "문서 제목을 입력하세요."
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
			msg : "문서를 등록 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 등록 진행
				// 일반 문서 값
				var params = $(document).getFormParams();

				params.self = $(obj).data("self");

				// 관련 부품
				params = $(document).getReferenceParams(params,
						"input[name=partOid]", "partOids");

				// 결재선
				params = $(document).getAppLines(params);

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

	deleteDocument : function() {
		var dialogs = $(document).setOpen();
		var url = this.deleteActionUrl;
		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "문서를 삭제 하시겠습니까?\n삭제한 데이터는 복구가 불가능합니다."
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

	/*
	 * deleteListNotice : function() { var isSelect = $(document).isSelect();
	 * var dialogs = $(document).setOpen(); var url = this.deleteActionUrl; if
	 * (isSelect == false) { dialogs.alert({ theme : "alert", title : "경고", msg :
	 * "삭제할 문서를 선택하세요." }) return false; }
	 * 
	 * var box = $(document).setNonOpen(); box.confirm({ theme : "info", title :
	 * "확인", msg : "선택한 문서를 삭제 하시겠습니까?\n삭제한 데이터는 복구가 불가능합니다." }, function() { //
	 * 확인 if (this.key == "ok") { var arr = new Array(); var params =
	 * $(document).getListParams(); $oid = $("input[name=oid]");
	 * 
	 * if ($oid.length == 0) { return false; }
	 * 
	 * $.each($oid, function(idx) { if ($oid.eq(idx).prop("checked") == true) {
	 * arr.push($oid.eq(idx).val()); //$len--; } })
	 * 
	 * params.list = arr;
	 * 
	 * $(document).ajaxCallServer(url, params, function(data) { dialogs.alert({
	 * theme : "alert", title : "결과", msg : data.msg }, function() { if
	 * (this.key == "ok" || this.state == "close") { document.location.reload(); } }) },
	 * true); // 취소 or esc } else if (this.key == "cancel" || this.state ==
	 * "close") { mask.close(); } }) },
	 */

	deleteDocuments : function() {
		var isSelect = $(document).isSelect();
		var dialogs = $(document).setOpen();
		var url = this.deleteActionUrl;
		if (isSelect == false) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "삭제할 문서를 선택하세요."
			})
			return false;
		}

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "선택한 문서를 삭제 하시겠습니까?\n삭제한 데이터는 복구가 불가능합니다."
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

	backDocBtn : function(obj) {
		var url = this.viewDocumentUrl;
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

	modifyAction : function() {
		var dialogs = $(document).setOpen();
		$root = $("#locationStr").text();
		var url = this.modifyActionUrl;
		if ($root == this.root) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "문서 문류를 선택하세요."
			}, function() {
				var url = "/Windchill/plm/common/openFolder?popup=true&root="
						+ documents.root + "&context=PRODUCT";
				if (this.key == "ok") {
					$(document).openURLViewOpt(url, 400, 400, "");
				}
				if (this.state == "close") {
					$(document).openURLViewOpt(url, 400, 400, "");
				}
			})
			return false;
		}

		// 문서 제목
		$name = $("input[name=name]");
		if ($name.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "문서 제목을 입력하세요."
			}, function() {
				if (this.key == "ok") {
					$name.focus();
				}
			})
			return false;
		}

		$primaryContent = primary.getUploadedList("object")[0];
		if (!$primaryContent) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "주 첨부파일을 선택하세요."
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
			msg : "문서를 수정 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 등록 진행
				// 일반 문서 값
				var params = $(document).getFormParams();
				// 관련 부품
				params = $(document).getReferenceParams(params,
						"input[name=partOid]", "partOids");
				// 결재선
				params = $(document).getAppLines(params);
				// 객체 oid
				params.oid = $("input[name=oid]").val();

				$(document).ajaxCallServer(url, params, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						msg : data.msg
					}, function() {
						// 버튼 클릭 ok, esc
						if (this.key == "ok" || this.state == "close") {
							$popup = $("input[name=popup]").val();
							if (data.reload) {
								if ($popup == "true") {
									self.close();
									opener.document.location.href = data.url;
								} else if ($popup == "false") {
									document.location.href = data.url;
								}
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

	modifyOutputAction : function(obj) {
		var dialogs = $(document).setOpen();
		$root = $("#locationStr").text();
		$task = $(obj).data("task");
		var url = this.modifyOutputActionUrl;
		if ($root == this.root) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "산출물 분류를 선택하세요."
			}, function() {
				var url = "/Windchill/plm/common/openFolder?popup=true&root="
						+ documents.output_root + "&context=PRODUCT";
				if (this.key == "ok") {
					$(document).openURLViewOpt(url, 400, 400, "no");
				}
				if (this.state == "close") {
					$(document).openURLViewOpt(url, 400, 400, "");
				}
			})
			return false;
		}

		// 문서 제목
		$name = $("input[name=name]");
		if ($name.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "산출물 제목을 입력하세요."
			}, function() {
				if (this.key == "ok") {
					$name.focus();
				}
			})
			return false;
		}

		$primaryContent = primary.getUploadedList("object")[0];
		if (!$primaryContent) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "주 첨부파일을 선택하세요."
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
		// 등록 진행
		// 일반 문서 값
		var params = $(document).getFormParams();
		// 관련 프로젝트
		params = $(document).getReferenceParams(params,
				"input[name=projectOid]", "projectOids");
		// 결재선
		params = $(document).getAppLines(params);
		// 객체 oid
		params.oid = $("input[name=oid]").val();

		$(document).ajaxCallServer(url, params, function(data) {
			dialogs.alert({
				theme : "alert",
				title : "결과",
				msg : data.msg
			}, function() {
				// 버튼 클릭 ok, esc
				if (this.key == "ok" || this.state == "close") {
					$popup = $("input[name=popup]").val();
					if (data.reload) {
						if ($task) {
							self.close();
							opener.location.reload();
						} else {
							if ($popup == "true") {
								self.close();
								opener.document.location.href = data.url;
							} else if ($popup == "false") {
								document.location.href = data.url;
							}
						}
					}
				}
			})
		}, true);
	},

	createAppDocument : function() {
		var dialogs = $(document).setOpen();
		var url = this.approvalActionUrl;
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

		$len = $("input[name=docOid]").length;
		if ($len == 0) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "결재 등록할 문서를 선택하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					documents.openAddPopup();
				}
			})
			return false;
		}

		var display = $(".create_series_table").css("display");
		var lineType = "series";
		var msg = "직렬 결재라인을 지정하세요.";
		if (display == "none") {
			lineType = "parallel";
			msg = "병렬 결재라인을 지정하세요";
		}

		$lineLen = $("input[name=appUserOid]").length; // 결재자
		if ($lineLen == 0) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : msg
			},
					function() {
						if (this.key == "ok" || this.state == "close") {
							var url = "/Windchill/plm/org/addLine?lineType="
									+ lineType;
							$(document).openURLViewOpt(url, 1200, 630, "");
						}
					})
			return false;
		}

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "문서 일괄 결재를 등록 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 일반 폼 값
				var params = $(document).getFormParams();
				// 결재선
				params = $(document).getAppLines(params);
				// 관련
				params = $(document).getReferenceParams(params,
						"input[name=docOid]", "docOids");
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

	delDocuments : function() {
		var isSelect = $(document).isSelectParams("docOid");
		var dialogs = $(document).setOpen();
		$oid = $("input[name=docOid]");
		if ($oid.length == 0) {
			return false;
		}

		if (isSelect == false) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "삭제할 문서를 선택하세요."
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

		if ($len < 6
				&& $("#documents_container").hasClass("documents_container")) {
			$("#documents_container").removeClass("documents_container");
		}

		if ($len == 0) {
			if ($("#documents_container").hasClass("documents_container")) {
				$("#documents_container").removeClass("documents_container");
			}
			var body = $("#addDocumentsBody");
			var html = "";
			html += "<tr id=\"nodataDocuments\">";
			html += "<td class=\"nodata\" colspan=\"8\">문서를 추가하세요.</td>";
			html += "</tr>";
			body.append(html);
			$("#allDocuments").prop("checked", false);
			$("#allDocuments").next().removeClass("sed");
		}
	},

	allDocuments : function(obj, e) {
		$oid = $("input[name=docOid]");
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

	bindBox : function(e) {
		$bool = false;
		$oid = $("input[name=docOid");
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
					} else if ($oid.eq(idx).prop("checked") == true) {
						$oid.eq(idx).parent().parent().css("background-color",
								"#fbfed1");
					}
				})

		if ($cnt == $oid.length && $cnt != 0) {
			$("#allDocuments").prop("checked", true);
			$("#allDocuments").next().addClass("sed");
		}

		if ($bool) {
			$("#allDocuments").prop("checked", false);
			$("#allDocuments").next().removeClass("sed");
		}
	},

	openAddPopup : function(obj) {
		var dbl = $(this).data("dbl");
		if (dbl == undefined) {
			dbl = "true";
		}

		var state = $(this).data("state");
		state = $(obj).data("state");
		if (state == undefined) {
			state = "INWORK";
		}

		var fun = $(this).data("fun");
		if (fun == undefined) {
			fun = "addDblDocuments";
		}

		var location = $(obj).data("location");

		var context = $(this).data("context");
		if (context == undefined) {
			context = "product";
		}
		var url = "/Windchill/plm/document/addDocument?context=" + context
				+ "&dbl=" + dbl + "&fun=" + fun + "&state=" + state
				+ "&location=" + location;
		$(document).openURLViewOpt(url, 1400, 700, "no");
	},
	
	openAddEplanPopup : function(obj) {
		var dbl = $(this).data("dbl");
		if (dbl == undefined) {
			dbl = "true";
		}

		var state = $(this).data("state");
		state = $(obj).data("state");
		if (state == undefined) {
			state = "INWORK";
		}

		var fun = $(this).data("fun");
		if (fun == undefined) {
			fun = "addDblDocuments";
		}

		var location = $(obj).data("location");

		var context = $(this).data("context");
		if (context == undefined) {
			context = "product";
		}
		var url = "/Windchill/plm/part/addEplanDoc?context=" + context
				+ "&dbl=" + dbl + "&fun=" + fun + "&state=" + state
				+ "&location=" + location;
		$(document).openURLViewOpt(url, 1400, 700, "");
	},

	downDocContents : function() {
		var bool = $(document).isSelect();
		var dialogs = $(document).setOpen();

		var oid = $("input[name=oid]");
		if (oid.length == 0) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "다운로드할 파일을 먼저 검색하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					mask.close();
				}
			})
			return false;
		}

		if (!bool && oid.length != 0) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "다운로드할 파일을 선택하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					mask.close();
				}
			})
			return false;
		}

		var url;
		var params = $(document).getListParams();

		if (params.list.length == 1) {
			url = "/Windchill/plm/content/contentsDown";
		} else {
			url = "/Windchill/plm/content/contentsMultiDown";
		}

		$(document).onLayer();
		$(document).ajaxCallServer(url, params, function(data) {
			if (browserChecker.ie) {
				window.open(data.url, "_blank", "width=100,height=100");
			} else {
				$("#downloadFileContent").attr("href", data.url);
				document.getElementById("downloadFileContent").click();
			}

			$(document).offLayer();
		}, false);
		return false;
	},

	count : function(obj) {
		var len = $(obj).val().length;
		var dialogs = $(document).setOpen();
		if (len > 199) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "설명은 200자 이상 입력 불가능합니다."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$data = $("#descriptionDoc").val().substring(0, 199);
					$("#descriptionDoc").val($data);
					$("#descriptionDoc").focus();
					$("#descDocCnt").text($data.length);
				}
			})
		}
		$("#descDocCnt").text(len);
	},

	createOutputAction : function(obj) {
		var toid = $(obj).data("toid");
		var ptype = $(obj).data("ptype");
		var progress = $(obj).data("progress");
		var output = $(obj).data("output");
		var dialogs = $(document).setOpen();
		var url = this.createOutputActionUrl;
		$root = $("#locationStr").text();
		if ($root == this.output_root) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "산출물 저장위치를 선택하세요."
			}, function() {
				var url = "/Windchill/plm/common/openFolder?popup=true&root="
						+ documents.output_root + "&context=PRODUCT";
				if (this.key == "ok") {
					$(document).openURLViewOpt(url, 400, 400, "no");
				}
				if (this.state == "close") {
					$(document).openURLViewOpt(url, 400, 400, "no");
				}
			})
			return false;
		}

		// 문서 제목
		$name = $("input[name=name]");
		if ($name.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "산출물 제목을 입력하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$name.focus();
				}
			})
			return false;
		}

		var oid = $("input[name=projectOid]");
		if (oid.length == 0) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "작번을 추가하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					projects.openAddPopup(this);
				}
			})
			return false;
		}

		var len = $("input[name*=allContent]");
		if (len.length == 0) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "산출물 파일을 첨부하세요.",
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					if (this.key == "ok") {
						$("#allUpload_layer_AX_selector").click();
					}
					if (this.state == "close") {
						$("#allUpload_layer_AX_selector").click();
					}
				}
			})
			return false;
		}

		var box = $(document).setNonOpen();
		var popup = $("input[name=popup]").val();

		// alert(ptype);
		// if (output == true && (ptype != "일반" && ptype != "공통" && ptype !=
		// null)) {
		if (output == true) {
			var prompt = $(document).prompt();
			prompt
					.prompt(
							{
								input : {
									pro : {
										label : "현재 태스크 진행률 : " + progress
												+ "%",
										required : true
									// placeholder : "Input your name"
									},
								},
								theme : "info",
								title : "태스크 진행률 입력"
							},
							function() {

								if (this.key == "ok" || this.key == "enter") {
									var taskProgress = this.input.pro;
									box
											.confirm(
													{
														theme : "info",
														title : "확인",
														msg : "산출물을 등록 하시겠습니까?"
													},
													function() {

														if (this.key == "ok") {
															// 등록 진행
															// 일반 문서 값
															var params = $(
																	document)
																	.getFormParams();
															// 관련 부품
															params = $(document)
																	.getReferenceParams(
																			params,
																			"input[name=projectOid]",
																			"projectOids");
															// 결재선

															params.toid = toid;
															params.taskProgress = taskProgress;
															params.self = $(obj)
																	.data(
																			"self");
															params = $(document)
																	.getAppLines(
																			params);
															$(document)
																	.ajaxCallServer(
																			url,
																			params,
																			function(
																					data) {
																				dialogs
																						.alert(
																								{
																									theme : "alert",
																									title : "결과",
																									msg : data.msg
																								},
																								function() {
																									// 버튼
																									// 클릭
																									// ok,
																									// esc
																									if (this.key == "ok"
																											|| this.state == "close") {
																										if (data.reload) {
																											if (popup == "true") {
																												self
																														.close();
																												opener.document.location
																														.reload();
																											} else {
																												document.location.href = data.url;
																											}
																										}
																									}
																								})
																			},
																			true);
														}
														if (this.key == "cancel"
																|| this.state == "close") {
															mask.close();
														}
													})
								} else if (this.key == "cancel") {
									mask.close();
								}
							});
		} else {

			// 등록 진행
			// 일반 문서 값
			var params = $(document).getFormParams();
			// 관련 부품
			params = $(document).getReferenceParams(params,
					"input[name=projectOid]", "projectOids");
			// 결재선
			params.toid = toid;
			params.self = $(obj).data("self");
			params = $(document).getAppLines(params);
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
							if (popup == "true") {
								self.close();
								opener.document.location.reload();
							} else {
								document.location.href = data.url;
							}
						}
					}
				})
			}, true);
		}
	},

	reviseOutput : function(obj) {
		$oid = $("input[name=oid]").val();
		$popup = $("input[name=popup]").val();
		$location = $("input[name=location]").val();
		$task = $(obj).data("task");
		var preUrl = document.referrer;
		var url = "/Windchill/plm/common/reviseOutput?oid=" + $oid + "&preUrl="
				+ preUrl + "&location=" + $location;
		var dialogs = $(document).setOpen();
		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "개정 하시겠습니까?"
		}, function() {
			if (this.key == "ok") {

				var params = $(document).getFormParams();
				// 관련 부품
				params = $(document).getReferenceParams(params,
						"input[name=projectOid]", "projectOids");

				params.task = $(obj).data("task");
				// params = $(document).getReferenceParams(params,
				// "input[name=location]", "location");

				console.log(params);
				$(document).ajaxCallServer(url, params, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						msg : data.msg
					}, function() {
						if (this.key == "ok" || this.state == "close") {
							if ($task) {
								self.close();
								opener.location.reload();
							} else {
								if ($popup == "true") {
									self.close();
									opener.document.location.href = data.url;
								} else if ($popup == "false") {
									document.location.href = data.url;
								}
							}
						}
					})
				}, true)
			}

			if (this.key == "cancel" || this.state == "close") {
				mask.close();
			}
		})
	},

	viewReqDoc : function(obj) {
		$oid = $(obj).data("oid");
		$url = "/Windchill/plm/document/viewRequestDocument?oid=" + $oid
				+ "&popup=true";
		$(document).openURLViewOpt($url, 1200, 700, "");
	}
}

$(document).ready(function() {

	$(".viewReqDoc").click(function() {
		documents.viewReqDoc(this);
	})

	$("#createOutputAction").click(function() {
		documents.createOutputAction(this);
	})

	$("#createOutput").click(function() {
		documents.createOutput(this);
	})

	$("#createSelfOutputBtn").click(function() {
		documents.createOutputAction(this);
	})

	$("#modifyDocBtnAction").click(function() {
		documents.modifyAction();
	})

	$("#modifyOutputBtnAction").click(function() {
		documents.modifyOutputAction(this);
	})

	$("#listDocBtn").click(function() {
		documents.list();
	})

	$("#modifyDocBtn").click(function() {
		documents.modify();
	})

	$("#modifyOutputBtn").click(function() {
		documents.modifyOutput(this);
	})

	$("#deleteDocBtn").click(function() {
		documents.deleteDocument();
	})

	$("#deleteListDocBtn").click(function() {
		documents.deleteDocuments();
	})

	$("#reviseOutputBtn").click(function() {
		documents.reviseOutput(this);
	})

	$("#reviseBtn").click(function() {
		$(document).revise();
	})

	$("#infoVersionBtn").click(function() {
		documents.infoVersion();
	})

	$("#backDocBtn").click(function() {
		documents.backDocBtn(this);
	})

	$("#createDocBtn").click(function() {
		documents.createDocument(this);
	})

	$("#createSelfDocBtn").click(function() {
		documents.createDocument(this);
	})

	$("#createDocAppBtn").click(function() {
		documents.createAppDocument();
	})

	// 문서 추가 페이지
	$("#addDocuments").add("#addOldDocuments").click(function() {
		documents.openAddPopup(this);
	})

	$("#addEplanDoc").click(function() {
		documents.openAddEplanPopup(this);
	})

	$(document).bind("click", "input[name=docOid]", function(e) {
		documents.bindBox(e);
	})

	$("#allDocuments").click(function(e) {
		documents.allDocuments(this, e);
	})

	$("#delDocuments").click(function() {
		documents.delDocuments();
	})

	$("#closeDocBtn").click(function() {
		// self.close();
		self.opener = self;
		window.close();
	})

	$("#downDocContents").click(function() {
		documents.downDocContents();
	})

	// 부품 추가 헤더 픽스
	$(".parts_add_table").tableHeadFixer();

	$("#descriptionDoc").keyup(function() {
		documents.count(this);
	})
})

function setNumber(loc) {

	var url = "/Windchill/plm/document/setNumber";

	var params = new Object();
	params.loc = loc + "/";

	$(document).ajaxCallServer(url, params, function(data) {
		var number = data.number;
		$("input[name=number]").val(number);
	}, false);

}