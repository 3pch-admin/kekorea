$.support.cors = true;
var browserChecker = {
	chk : navigator.userAgent.toLowerCase()
}
browserChecker = {
	ie : browserChecker.chk.indexOf('msie') != -1,
	ie6 : browserChecker.chk.indexOf('msie 6') != -1,
	ie7 : browserChecker.chk.indexOf('msie 7') != -1,
	ie8 : browserChecker.chk.indexOf('msie 8') != -1,
	ie9 : browserChecker.chk.indexOf('msie 9') != -1,
	ie10 : browserChecker.chk.indexOf('msie 10') != -1,
	ie11a : browserChecker.chk.indexOf('trident') != -1,
	opera : !!window.opera,
	safari : browserChecker.chk.indexOf('safari') != -1,
	safari3 : browserChecker.chk.indexOf('applewebkir/5') != -1,
	mac : browserChecker.chk.indexOf('mac') != -1,
	chrome : browserChecker.chk.indexOf('chrome') != -1,
	firefox : browserChecker.chk.indexOf('firefox') != -1
}

$(document).ready(function() {
	$("#locale").bindSelect();
	$("#locale").change(function() {
		$(document).onLayer();
		document.forms[0].submit();
	})
	
	$("img.download").click(function() {
		var url = $(this).data("url");
		//alert(url);
	})

	$("img.markupCreo").click(function() {
		$url = $(this).data("url");
		// alert($url);
		if ($url == "") {
			return false;
		}
		createCDialogWindow($url, "ProductViewLite", "1200", "600", "0", "0");
	}).mouseover(function() {
		$(this).css("cursor", "pointer").attr("title", "주석 세트 확인");
	})

	$("img.markup").click(function() {
		$oid = $(this).data("oid");
		var popup = $(this).data("popup");
		if (popup == undefined) {
			popup = "true";
		}
		var url = "/Windchill/plm/common/viewMarkup?oid=" + $oid + "&popup=" + popup;
		$(document).openURLViewOpt(url, 1400, 700, "");
	})

	$("img.left_switch").click(function() {
		$("#left_menu_td").hide();
		$("img.right_switch").show();
		$("#colGroups").remove();
		$(document).setHTML();
	})

	$("img.right_switch").click(function() {

		$content_table = $("#content_table");
		var col = "<colgroup id=\"colGroups\">";
		col += "<col width=\"230\">";
		col += "<col width=\"*\">";
		col += "</colgroup>";

		$("#left_menu_td").show();
		$("img.right_switch").hide();
		$content_table.append(col);
		$(document).setHTML();
	})

	$(document).on("click", "img.deletePublisher", function() {
		$url = $(this).data("url");
		var dialogs = $(document).setOpen();
		var params = new Object();
		$(document).ajaxCallServer($url, params, function(data) {
			dialogs.alert({
				theme : "alert",
				title : "결과",
				msg : data.msg,
				width : 380
			}, function() {
				// 버튼 클릭 ok, esc
				if (this.key == "ok" || this.state == "close") {
					// document.location.href = data.url;
				}
			})
		}, true);
	})

	$(document).on("click", "img.doPublisher", function() {
		$url = $(this).data("url");
		$creo = $(this).data("creo");
		var bool = $url != undefined && ($creo == "" || $creo == undefined);
		if (!bool) {
			return;
		}

		var dialogs = $(document).setOpen();
		var params = new Object();
		$(document).ajaxCallServer($url, params, function(data) {
			dialogs.alert({
				theme : "alert",
				title : "결과",
				msg : data.msg,
				width : 380
			}, function() {
				// 버튼 클릭 ok, esc
				if (this.key == "ok" || this.state == "close") {
					// document.location.href = data.url;
				}
			})
		}, true);
	})

	$("#table_search_icon").click(function() {
		var k = $("#table_search").val();
		$(".list_table > tbody > tr").hide();
		// var temp = $(".list_table > tbody > tr
		// >td:nth-child(5n+2):contains('" + k + "')");
		$temp = $(".list_table > tbody > tr > td");

		for (var i = 0; i < $temp.length; i++) {
			$text = $(".list_table > tbody > tr > td:eq('" + i + "'):contains('" + k + "')");
			$(".list_table > tbody > tr > td:eq('" + i + "')").css({
				"border-top" : "0px solid #c8c8c8"
			})
			
			$(".list_table > tbody > tr > td:eq('" + i + "')").css({
				"border-bottom" : "1px solid #c8c8c8"
			})

			$("#header_tr th").css({
				"border-bottom" : "1px solid #c8c8c8"
			})

			// $text.parent().show().css("background-color", "black");
			$text.parent().show();
		}
	})

	// 등록 form data
	$.fn.getFormParams = function(params) {
		if (params == null) {
			params = new Object();
		}

		// class create_table
		$input = $(".approval_table input[type=text]");
		$.each($input, function(idx) {
			var key = $input.eq(idx).attr("name");
			var value = $input.eq(idx).val();
			params[key] = value;
		})

		$password = $(".create_table input[type=password]");
		$.each($password, function(idx) {
			var key = $password.eq(idx).attr("name");
			var value = $password.eq(idx).val();
			params[key] = value;
		})

		// class create_table
		$input = $(".create_table input[type=text]");
		$.each($input, function(idx) {
			var key = $input.eq(idx).attr("name");
			var value = $input.eq(idx).val();
			params[key] = value;
		})

		$hidden = $(".create_table input[type=hidden]");
		$.each($hidden, function(idx) {
			var key = $hidden.eq(idx).attr("name");
			var value = $hidden.eq(idx).val();
			params[key] = value;
		})

		$radio = $(".create_table input[type=radio]");
		$.each($radio, function(idx) {
			if ($radio.eq(idx).prop("checked") == true) {
				var key = $radio.eq(idx).attr("name");
				var value = $radio.eq(idx).val();
				params[key] = value;
			}
		})

		$checkbox = $(".create_table input[type=checkbox]");
		$.each($checkbox, function(idx) {
			if ($checkbox.eq(idx).prop("checked") == true) {
				var key = $checkbox.eq(idx).attr("name");
				var value = $checkbox.eq(idx).val();
				params[key] = value;
			}
		})

		$select = $(".create_table select");
		$.each($select, function(idx) {
			$key = $select.eq(idx).attr("name");
			if ($key == undefined) {
				return true;
			}
			$value = $select.eq(idx).val();
			params[$key] = $value;
		})

		$textarea = $(".create_table textarea");
		$.each($textarea, function(idx) {
			$key = $textarea.eq(idx).attr("name");
			if ($key == undefined) {
				return true;
			}
			$value = $textarea.eq(idx).val();
			params[$key] = $value;
		})

		$primary = $("form:eq(0)").find("input[name*=primary]");
		$.each($primary, function(idx) {
			$key = $primary.eq(idx).attr("name");
			if ($key == undefined) {
				return true;
			}
			$value = $primary.eq(idx).val();
			params[$key] = $value;
		})

		$secondary = $("form:eq(0)").find("input[name*=secondary_]");
		$.each($secondary, function(idx) {
			$key = $secondary.eq(idx).attr("name");
			if ($key == undefined) {
				return true;
			}
			$value = $secondary.eq(idx).val();
			params[$key] = $value;
		})

		$allContents = $("form:eq(0)").find("input[name*=allContent_]");
		$.each($allContents, function(idx) {
			$key = $allContents.eq(idx).attr("name");
			if ($key == undefined) {
				return true;
			}
			$value = $allContents.eq(idx).val();
			params[$key] = $value;
		})

		return params;
	}

	// 결재선 가져오기
	$.fn.getAppLines = function(params) {
		if (params == null) {
			params = new Object();
		}

		$appOids = $("input[name=appUserOid]");
		$agreeOids = $("input[name=agreeUserOid]");
		$receiveOids = $("input[name=receiveUserOid]");

		var appList = new Array();
		var agreeList = new Array();
		var receiveList = new Array();

		$.each($appOids, function(idx) {
			$value = $appOids.eq(idx).val();
			appList.push($value);
		})

		$.each($agreeOids, function(idx) {
			$value = $agreeOids.eq(idx).val();
			agreeList.push($value);
		})

		$.each($receiveOids, function(idx) {
			$value = $receiveOids.eq(idx).val();
			receiveList.push($value);
		})

		params.appList = appList;
		params.agreeList = agreeList;
		params.receiveList = receiveList;
		return params;
	}

	$.fn.getReferenceParams = function(params, target, key) {
		if (params == null) {
			params = new Object();
		}

		$reference = $(target);
		var arr = new Array();
		$.each($reference, function(idx) {
			$value = $reference.eq(idx).val();
			arr.push($value);
		})
		params[key] = arr;
		return params;
	}

	$.fn.getCheckBoxValue = function(params, target, key) {
		if (params == null) {
			params = new Object();
		}

		$reference = $(target);
		var arr = new Array();
		$.each($reference, function(idx) {
			if ($reference.eq(idx).prop("checked") == true) {
				$value = $reference.eq(idx).val();
				arr.push($value);
			}
		})
		params[key] = arr;
		return params;
	}

	// 구매품 or 가공품 전환
	$("#product_view").add("#library_view").add("#eplan_view").add("#newOutput_view").add("#oldOutput_view").click(function(e) {
		$url = $(this).data("url");
		$(document).onLayer();
		document.location.href = $url;
	})

	// 리스트에서 선택 유무 체크
	$.fn.isSelect = function() {
		$bool = false;
		$oid = $("input[name=oid]");
		$.each($oid, function(idx) {
			if ($oid.eq(idx).prop("checked") == true) {
				$bool = true;
				return false;
			}
		})
		return $bool;
	}

	$.fn.isOutputSelect = function() {
		$bool = false;
		$oid = $("input[name=outputOid]");
		$.each($oid, function(idx) {
			if ($oid.eq(idx).prop("checked") == true) {
				$bool = true;
				return false;
			}
		})
		return $bool;
	}

	$.fn.isIssueSelect = function() {
		$bool = false;
		$oid = $("input[name=issueOid]");
		$.each($oid, function(idx) {
			if ($oid.eq(idx).prop("checked") == true) {
				$bool = true;
				return false;
			}
		})
		return $bool;
	}

	$.fn.isSelectParams = function(str) {
		var bool = false;
		$oid = $("input[name=" + str + "]");
		$.each($oid, function(idx) {
			if ($oid.eq(idx).prop("checked") == true) {
				bool = true;
				return false;
			}
		})
		return bool;
	}

	$.fn.getOneParams = function(key) {
		var params = new Object();
		if (key == undefined) {
			key = "list";
		}

		$oid = $("input[name=oid]");
		$value = "";
		$.each($oid, function(idx) {
			if ($oid.eq(idx).prop("checked") == true) {
				$value = $oid.eq(idx).val();
			}
		})
		params[key] = $value;
		return params;
	}

	// 객체 추가 페이지 함수
	$.fn.getListParams = function(key, value) {
		var params = new Object();
		var arr = new Array();

		if (key == undefined) {
			key = "list";
		}

		$oid = $(value);
		$.each($oid, function(idx) {
			if ($oid.eq(idx).prop("checked") == true) {
				$value = $oid.eq(idx).val();
				arr.push($value);
			}
		})
		params[key] = arr;
		return params;
	}

	$.fn.getNonListParams = function() {
		var params = new Object();
		var arr = new Array();
		$oid = $("input[name=oid]");
		$.each($oid, function(idx) {
			$value = $oid.eq(idx).val();
			arr.push($value);
		})
		params.list = arr;
		return params;
	}

	$.fn.getDblFromData = function(value) {
		var params = new Object();
		var array = new Array();
		array.push(value);
		params.list = array;
		return params;
	}

	$.fn.getDblFormOneData = function(value) {
		var params = new Object();
		params.oid = value;
		return params;
	}

	$.fn.revise = function() {
		$oid = $("input[name=oid]").val();
		$popup = $("input[name=popup]").val();
		var preUrl = document.referrer;
		var url = "/Windchill/plm/common/reviseObject?oid=" + $oid + "&preUrl=" + preUrl;
		var dialogs = $(document).setOpen();
		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "개정 하시겠습니까?"
		}, function() {
			if (this.key == "ok") {

				$(document).ajaxCallServer(url, null, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						msg : data.msg
					}, function() {
						if (this.key == "ok" || this.state == "close") {
							if ($popup == "true") {
								self.close();
								opener.document.location.href = data.url;
							} else if ($popup == "false") {
								document.location.href = data.url;
							}
						}
					})
				}, true)
			}

			if (this.key == "cancel" || this.state == "close") {
				mask.close();
			}
		})
	}

	// var popup;
	$.fn.openURLViewOpt = function(url, width, height, opt) {
		var popW = width;
		var popH = height;
		var left = (screen.width - popW) / 2;
		var top = (screen.height - popH) / 2;

		if (opt == "no") {
			opt = "scrollbars=yes resizable=yes";
		}

		if (opt == "" || opt == undefined || opt == "full") {
			popW = screen.width;
			popH = screen.height;
			opt = "scrollbars=yes, resizable=yes, fullscreen=yes";
		}

		// if (popup == undefined) {
		// popup = window.open(url, "", opt + ", top=" + top + ", left=" + left
		// + ", height=" + popH + ", width=" + popW);
		window.open(url, "", opt + ", top=" + top + ", left=" + left + ", height=" + popH + ", width=" + popW);
		// }
	}

	$.fn.getObjectName = function(oid) {
		var objName;
		if (oid.indexOf("Notice") > -1) {
			objName = "공지사항";
		} else if (oid.indexOf("WTPart") > -1) {
			objName = "부품";
		} else if (oid.indexOf("WTDocument") > -1) {
			objName = "문서";
		} else if (oid.indexOf("EPMDocument") > -1) {
			objName = "도면";
		} else if (oid.indexOf("ApprovalLine") > -1) {
			objName = "결재";
		} else if (oid.indexOf("ApprovalMaster") > -1) {
			objName = "결재";
		} else if (oid.indexOf("User") > -1) {
			objName = "유저";
		} else if (oid.indexOf("EBOM") > -1) {
			objName = "E-BOM";
		} else if (oid.indexOf("ECN") > -1) {
			objName = "ECN";
		} else if (oid.indexOf("Template") > -1) {
			objName = "템플릿";
		} else if (oid.indexOf("Project") > -1) {
			objName = "프로젝트";
		} else if (oid.indexOf("PartListMaster") > -1) {
			objName = "수배리스트";
		} else if (oid.indexOf("RequestDocument") > -1) {
			objName = "의뢰서";
		} else if (oid.indexOf("Issue") > -1) {
			objName = "특이사항";
		} else if(oid.indexOf("PRJDocument") > -1) {
			objName = "산출물";
		}
		return objName;
	}

	$(".infoProject").on("click", function(e) {
		var oid = $(this).data("oid");
		var popup = $(this).data("popup");
		if (popup == undefined) {
			popup = "true";
		}
		var url = "/Windchill/plm/project/viewProject?oid=" + oid + "&popup=" + popup;
		$(document).openURLViewOpt(url, 1400, 700, "");
	}).on("mouseover", function(e) {
		var oid = $(this).data("oid");
		var obj = $(document).getObjectName(oid);
		var text = $(this).text();
		$(this).css("cursor", "pointer").attr("title",  text + " " + obj + "정보보기");
	})

	$(".infoPer").on("click", function(e) {
		var oid = $(this).data("oid");
		var popup = $(this).data("popup");
		if (popup == undefined) {
			popup = "true";
		}
		var url = getURL(oid, popup);
		$(document).openURLViewOpt(url, 1400, 700, "");
	}).on("mouseover", function(e) {
		var oid = $(this).data("oid");
		var obj = $(document).getObjectName(oid);
		var text = $(this).text();
		$(this).css("cursor", "pointer").attr("title",  text + " " + obj + "정보보기");
	})

	$("#backBtn").click(function() {
		var preUrl = document.referrer;
		if (preUrl) {
			$(document).onLayer();
			document.location.href = preUrl;
		}
	})

	$.fn.list = function() {
		$(document).onLayer();
		document.forms[0].submit();
	}
	// end..

	$.fn.getFormData = function(params) {

		if (params == null) {
			params = new Object();
		}
		$hidden = $("input[type=hidden]");
		$.each($hidden, function(idx) {
			var key = $hidden.eq(idx).attr("name");
			var value = $hidden.eq(idx).val();
			params[key] = value;
		})

		$input = $("input[type=text]");
		$.each($input, function(idx) {
			var key = $input.eq(idx).attr("name");
			var value = $input.eq(idx).val();
			params[key] = value;
		})

		$select = $("select");
		$.each($select, function(idx, value) {
			var key = $select.eq(idx).attr("name");
			var value = $select.eq(idx).val();
			params[key] = value;
		})

		$textarea = $("textarea");
		$.each($textarea, function(idx, value) {
			var key = $textarea.eq(idx).attr("name");
			var value = $textarea.eq(idx).val();
			params[key] = value;
		})
		return params;
	}

	$.fn.gotoList = function(page) {
		var check = $("input[name=tpage]").val();
		if (check == undefined) {
			return false;
		}
		$("input[name=tpage]").val(page);
		$(document).onLayer();
		$("form").submit();
	}

	$("#paging_count").bindSelect();

	$.fn.list = function(e) {
		var check = $("input[name=tpage]").val();
		if (check == undefined) {
			return false;
		}
		$("input[name=tpage]").val(1);
		$("input[name=sessionid]").val(0);
		$(document).onLayer();
		$("form").submit();
	}

	$(".openLoc").click(function() {
		$root = $(this).data("root");
		$context = $(this).data("context");
		$popup = $(this).data("popup");
		$url = "/Windchill/plm/common/openFolder?popup=" + $popup + "&root=" + $root + "&context=" + $context;
		$(document).openURLViewOpt($url, 400, 400, "no");
	})

	$(".approval").add(".epm").add(".part").add(".document").add(".admin").add(".echange").add(".project").click(function(e) {
		$url = $(this).data("url");
		var target;
		if (e.toElement) {
			target = e.toElement.localName;
		} else {
			target = e.target.tagName;
		}
		// alert(target);
		if (target == "li" || target == "LI") {
			$(document).onLayer();
			document.location.href = $url;
		}
	})

	$list_tr = $(".list_tr");
	// $("#count_text").html("(" + $list_tr.length + "/<font
	// color=\"red\">0</font>)개 선택됨");

	$(".noPDF").click(function() {
		$oid = $(this).data("oid");
		var url = "/Windchill/plm/epm/doPublishAction";
		var params = new Object();
		params.oid = $oid;

		$(document).ajaxCallServer(url, params, function(data) {

		}, true);
	}).mouseover(function() {
		$(this).attr("title", "PDF 생성").css("cursor", "pointer");
	})

	$(".manual").click(function() {
		$(document).onLayer();
		$url = "/Windchill/plm/common/manual";
		document.location.href = $url;
	})

	$(".setup").click(function() {
		$(document).onLayer();
		$url = "/Windchill/plm/common/setupFiles";
		document.location.href = $url;
	})

	$(".infoEpm").on("click", function(e) {
		var oid = $(this).data("oid");
		var url = "/Windchill/plm/epm/viewEpm?oid=" + oid + "&popup=true";
		$(document).openURLViewOpt(url, 1400, 700, "");
	}).on("mouseover", function(e) {
		$(this).attr("title", "도면정보보기");
	})

	$(".infoEBOM").on("click", function(e) {
		var oid = $(this).data("oid");
		if (oid == "") {
			return;
		}
		var url = "/Windchill/plm/echange/viewEBOM?oid=" + oid + "&popup=true";
		$(document).openURLViewOpt(url, 1400, 700, "");
	}).on("mouseover", function(e) {
		$(this).attr("title", "EBOM정보고기").css("cursor", "pointer");
	})

	$(document).on("click", ".infoDoc", function(e) {
		var oid = $(this).data("oid");
		var url = "/Windchill/plm/document/viewDocument?oid=" + oid + "&popup=true";
		$(document).openURLViewOpt(url, 1400, 700, "");
	}).on("mouseover", ".infoDoc", function(e) {
		$(this).attr("title", "문서정보보기");
	})

	$("#global_search").click(function() {
		if ($("input[name=global_obj]").val() == "") {
			var dialogs = $(document).setAlertConfig();
			dialogs.alert({
				title : "검색 대상 미선택",
				msg : "검색할 대상을 선택하세요."
			}, function() {
				if (this.key == "ok") {
					$("input[name=global_obj]").focus();
				}
			})
			return false;
		}

		if ($("input[name=global_value]").val() == "") {
			var dialogs = $(document).setAlertConfig();
			dialogs.alert({
				title : "검색 단어 미입력",
				msg : "검색할 단어를 입력하세요."
			}, function() {
				if (this.key == "ok") {
					$("input[name=global_value]").focus();
				}
			})
			return false;
		}

		$obj = $("input[name=global_obj]").val();
		var url;
		if ($obj == "문서") {
			url = "/Windchill/plm/document/listDocument";
		} else if ($obj == "도면") {
			url = "/Windchill/plm/epm/listEpm";
		} else if ($obj == "부품") {
			url = "/Windchill/plm/part/listPart";
		}

		$("input[name=tpage]").val(1);
		$("input[name=sessionid]").val(0);
		$(document).onLayer();
		$("form").attr("action", url).submit();
	})

	$("#addDoc").click(function() {
		var dbl = $(this).data("dbl");
		if (dbl == undefined) {
			dbl = "false";
		}

		var multi = $(this).data("multi");
		if (multi == undefined) {
			multi = "false";
		}

		var box = $(this).data("box");
		if (box == undefined) {
			box = "false";
		}

		var fun = $(this).data("fun");
		if (fun == undefined) {
			fun = "addDoc";
		}

		var url = "/Windchill/plm/document/addDocument?dbl=" + dbl + "&fun=" + fun + "&multi=" + multi + "&box=" + box;
		var opt = "scrollbars=yes, resizable=yes";
		$(document).openURLViewOpt(url, 1400, 700, "");
	})

	$(".nodataEBOM").mouseover(function() {
		$(this).css("background-color", "white");
	})

	$(".add_ebom").add(".add_ecn").click(function() {
		var context = $(this).data("context");
		if (context == undefined) {
			context = "product";
		}

		var dbl = $(this).data("dbl");
		if (dbl == undefined) {
			dbl = "false";
		}

		var part_type = $(this).data("type");
		var fun = $(this).attr("class");
		var url = "/Windchill/plm/part/addPart?context=" + context + "&fun=" + fun + "&part_type=" + part_type + "&dbl=" + dbl;
		var opt = "scrollbars=yes, resizable=yes";
		var width = screen.width;
		var height = screen.height;
		$(document).openURLViewOpt(url, width, height, opt);
	})

	$.fn.infoHistory = function(oid) {
		$url = "/Windchill/plm/common/infoVersion?oid=" + oid;
		$opt = "resizable=no, scrollbars=yes";
		$(document).openURLViewOpt($url, 1000, 400, $opt);
	}

	$(".goLatest").click(function() {

		$(document).onLayer();
		var oid = $(this).data("oid");
		var popup = $(this).data("popup");
		if (popup == undefined) {
			popup = false;
		}
		var url = getURL(oid, popup);
		document.location.href = url;
	})

	function getURL(oid, popup) {
		var url;
		if (oid.indexOf("WTDocument") > -1) {
//			url = "/Windchill/plm/document/viewDocument?oid=" + oid + "&popup=" + popup;
			url = "/Windchill/plm/document/viewOutput?oid=" + oid + "&popup=" + popup;
		} else if (oid.indexOf("WTPart") > -1) {
			url = "/Windchill/plm/part/viewPart?oid=" + oid + "&popup=" + popup;
		} else if (oid.indexOf("EPMDocument") > -1) {
			url = "/Windchill/plm/epm/viewEpm?oid=" + oid + "&popup=" + popup;
		} else if (oid.indexOf("EBOM") > -1) {
			url = "/Windchill/plm/echange/viewEBOM?oid=" + oid + "&popup=" + popup;
		} else if (oid.indexOf("ECN") > -1) {
			url = "/Windchill/plm/echange/viewECN?oid=" + oid + "&popup=" + popup;
		} else if (oid.indexOf("PartListMaster") > -1) {
			url = "/Windchill/plm/partList/viewPartListMaster?oid=" + oid + "&popup=" + popup;
		} else if (oid.indexOf("RequestDocument") > -1) {
			url = "/Windchill/plm/document/viewRequestDocument?oid=" + oid + "&popup=" + popup;
		} else if(oid.indexOf("PRJDocument") > -1) {
			url = "/Windchill/plm/document/viewOutput?oid=" + oid + "&popup=" + popup;
//			url = "/Windchill/plm/document/viewDocument?oid=" + oid + "&popup=" + popup;
		}
		return url;
	}

	$.fn.setNonOpen = function() {
		var box = new ax5.ui.dialog();
		box.setConfig({
			theme : "info",
			lang : {
				"ok" : "확인",
				"cancel" : "취소"
			},
			onStateChanged : function() {
				if (this.state == "open") {
					mask.open();
				} else if (this.state == "close") {
					// mask.close();
				}
			}
		})
		return box;
	}

	$.fn.gotoPaging = function(page, e) {
		if (page == 0) {
			return false;
		}
		var prompt = $(document).prompt();
		prompt.prompt({
			theme : "info",
			title : "페이징 1 ~ " + page
		}, function() {
			if (this.key == "ok" || this.key == "enter") {
				$(document).gotoList(this.input.value);
			} else if (this.key == "cancel") {
				mask.close();
			}
		});
	}

	$.fn.prompt = function() {
		var prompt = new ax5.ui.dialog();
		prompt.setConfig({
			theme : "info",
			lang : {
				"ok" : "확인",
				"cancel" : "취소"
			},
			onStateChanged : function() {
				if (this.state == "open") {
					mask.open();
				} else if (this.state == "close") {
					// mask.close();
				}
			}
		})
		return prompt;
	}

	$.fn.setOpen = function() {
		var dialog = new ax5.ui.dialog();
		dialog.setConfig({
			theme : "info",
			lang : {
				"ok" : "확인",
				"cancel" : "취소"
			},
			onStateChanged : function() {
				if (this.state == "open") {
					mask.open();
				} else if (this.state == "close") {
					mask.close();
				}
			}
		})
		return dialog;
	}

	$.fn.getListData = function() {
		var params = new Object();
		var array = new Array();
		$checkbox = $("input[name=oid]");
		$.each($checkbox, function(idx) {
			var value = $checkbox.eq(idx).val();
			array.push(value);
		})
		params.list = array;
		return params;
	}

	$.fn.getDataHeaders = function(params) {
		var array = new Array();
		$checkbox = $("input[name=hideBox]");
		$.each($checkbox, function(idx) {
			var value = $checkbox.eq(idx).data("headers");
			array.push(value);
		})
		params.headers = array;
		return params;
	}

	$.fn.getDataIndex = function(params) {
		// var cols = $(".list_table").find("thead tr th");
		var cols = $(this).find("thead tr th");
		var array = new Array();
		var texts = new Array();
		[].forEach.call(cols, function(col) {
			var index = $(col).index();
			var header = $(col).data("header");
			var text = $(col).text();
			// ↑↓
			text = text.replace("↑", "");
			text = text.replace("↓", "");

			if (header == "checkbox") {
				return false;
			}
			texts.push(text);
			array.push(header);
		});
		params.texts = texts;
		params.indexs = array;
		return params;
	}

	$.fn.getDataKeys = function(params) {
		var array = new Array();
		$checkbox = $("input[name=hideBox]");
		$.each($checkbox, function(idx) {
			var value = $checkbox.eq(idx).data("column")
			array.push(value);
		})
		params.keys = array;
		return params;
	}

	$.fn.getDataStyles = function(params) {
		var array = new Array();
		$checkbox = $("input[name=hideBox]");
		$.each($checkbox, function(idx) {
			if ($checkbox.eq(idx).prop("checked") == true) {
				var value = "style=\"display: '';\"";
				array.push(value);
			}

			if ($checkbox.eq(idx).prop("checked") == false) {
				var value = "style=\"display: none;\"";
				array.push(value);
			}
		})
		params.styles = array;
		return params;
	}

	$.fn.getDataCols = function(params) {
		var array = new Array();
		$checkbox = $("input[name=hideBox]");
		$.each($checkbox, function(idx) {
			if ($checkbox.eq(idx).prop("checked") == true) {
				var value = "style=\"display: '';\"";
				array.push(value);
			}

			if ($checkbox.eq(idx).prop("checked") == false) {
				var value = "style=\"display: none;\"";
				array.push(value);
			}
		})
		params.cols = array;
		return params;
	}

	$(document).bind("click", "input[name=epmOid]", function() {
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

		$.each($oid, function(idx) {
			if ($oid.eq(idx).prop("checked") == false) {
				$oid.eq(idx).parent().parent().css("background-color", "white");
			} else if ($oid.eq(idx).prop("checked") == true) {
				$oid.eq(idx).parent().parent().css("background-color", "#fbfed1");
			}
		})

		if ($cnt == $oid.length && $cnt != 0) {
			$("#allEpm").prop("checked", true);
			$("#allEpm").next().addClass("sed");
		}

		if ($bool) {
			$("#allEpm").prop("checked", false);
			$("#allEpm").next().removeClass("sed");
		}
	})

	$(document).bind("click", "input[name=docOid]", function() {
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

		$.each($oid, function(idx) {
			if ($oid.eq(idx).prop("checked") == false) {
				$oid.eq(idx).parent().parent().css("background-color", "white");
			} else if ($oid.eq(idx).prop("checked") == true) {
				$oid.eq(idx).parent().parent().css("background-color", "#fbfed1");
			}
		})

		if ($cnt == $oid.length && $cnt != 0) {
			$("#allDoc").prop("checked", true);
			$("#allDoc").next().addClass("sed");
		}

		if ($bool) {
			$("#allDoc").prop("checked", false);
			$("#allDoc").next().removeClass("sed");
		}
	})

	$(document).on("click", ".addDocTag", function() {
		$(this).find(".ico-checkbox").toggleClass("sed");
		if ($(this).find("input[name=docOid]").prop("checked") == true) {
			$(this).find("input[name=docOid]").prop("checked", false);
		} else {
			$(this).find("input[name=docOid]").prop("checked", true);
		}
	})

	$(document).on("click", ".addEpmTag", function() {
		$(this).find(".ico-checkbox").toggleClass("sed");
		if ($(this).find("input[name=epmOid]").prop("checked") == true) {
			$(this).find("input[name=epmOid]").prop("checked", false);
		} else {
			$(this).find("input[name=epmOid]").prop("checked", true);
		}
	})

	$(document).on("click", ".addLineTag", function() {
		$(this).find(".ico-checkbox").toggleClass("sed");
		if ($(this).find("input[name=lines]").prop("checked") == true) {
			$(this).find("input[name=lines]").prop("checked", false);
		} else {
			$(this).find("input[name=lines]").prop("checked", true);
		}
	})

	$("#delDoc").click(function() {
		$msg = $(this).data("msg");
		var check = $(document).isCheckStr("docOid");
		var dialogs = $(document).setOpen();
		if (check == false) {
			dialogs.alert({
				theme : "alert",
				title : "삭제문서 미선택",
				msg : "삭제할 문서를 선택하세요."
			})
			return false;
		}

		$list = $("input[name=docOid]");
		$.each($list, function(idx) {
			if ($list.eq(idx).prop("checked") == true) {
				$list.eq(idx).parent().parent().remove();
			}
		})
		var list = $("input[name=docOid]");
		if (list.length == 0) {
			var body = $("#addDocBody");
			var html;
			html += "<tr id=\"nodataTr\">";
			html += "<td class=\"nodata rh250\" colspan=\"7\"><font class=\"noInfo\"><a class=\"axi axi-info-outline\"></a> <span>" + $msg + " 문서를 추가하세요.</span></font></td>";
			html += "</tr>";
			body.append(html);
			$("input[name=allDoc]").prop("checked", false);
			$("#allDoc").next().removeClass("sed");
		}
	})

	$("#allEpm").click(function(e) {
		$oid = $("input[name=epmOid]");
		if ($oid.length == 0) {
			e.stopPropagation();
			e.preventDefault();
			$(this).next().removeClass("sed");
			return false;
		}

		if ($(this).prop("checked") == true) {
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
	})

	$("#allEBOM").click(function(e) {
		$oid = $("input[name=ebomOid]");

		if ($oid.length == 0) {
			e.stopPropagation();
			e.preventDefault();
			$(this).next().removeClass("sed");
			return false;
		}

		if ($(this).prop("checked") == true) {
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
	})

	$(document).bind("click", "input[name=ebomOid]", function(e) {
		$bool = false;
		$oid = $("input[name=ebomOid");
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
			$("#allEBOM").prop("checked", true);
			$("#allEBOM").next().addClass("sed");
		}

		if ($bool) {
			$("#allEBOM").prop("checked", false);
			$("#allEBOM").next().removeClass("sed");
		}
	})

	$.fn.ajaxCallServer = function(url, params, cbMethod, progress) {

		if (params == null) {
			params = new Object();
		}

		params = JSON.stringify(params);
		$isSync = false;
		if (cbMethod != null) {
			$isSync = true;
		}

		$.ajax({
			type : "POST",
			url : url,
			dataType : "JSON",
			crossDomain : true,
			data : params,
			async : $isSync,
			// accepts : "application/json",
			contentType : "application/json; charset=UTF-8",

			beforeSend : function(data) {
				if (progress) {
					$(document).onLayer();
				}
			},
			complete : function(data) {
				if (progress) {
					// $(document).offLayer();
					// mask.close();
					$("#loading_layer").hide();
				}
			},
			success : function(data) {
				if (data.result == "SUCCESS") {
					cbMethod(data);
				}

				if (data.result == "FAIL") {
					cbMethod(data);
				}
			},
			error : function(data) {

			}
		})
	}

	$.fn.callServer = function(url, params, cbMethod, progress) {

		if (params == null) {
			params = new Object();
		}

		params = JSON.stringify(params);
		$isSync = false;
		if (cbMethod != null) {
			$isSync = true;
		}

		$.ajax({
			type : "POST",
			url : url,
			dataType : "JSON",
			crossDomain : true,
			data : params,
			async : $isSync,
			// accepts : "application/json",
			contentType : "application/json; charset=UTF-8",

			beforeSend : function(data) {
				if (progress) {
					$(document).onLayer();
				}
			},
			complete : function(data) {
				if (progress) {
					// $(document).offLayer();
					mask.close();
					$("#loading_layer").hide();
				}
			},
			success : function(data) {
				if (data.result == "SUCCESS") {
					cbMethod(data);
				}

				if (data.result == "FAIL") {
					cbMethod(data);
				}
			},
			error : function(data) {

			}
		})
	}

	$("#logo").click(function() {
		$(document).onLayer();
		document.location.href = "/Windchill/plm/common/main";
	})

	$("i.axi-ion-log-out").click(function() {
		// if (!confirm("로그아웃 하시겠습니까?")) {
		// return false;
		// }
		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "로그아웃",
			msg : "로그아웃 하시겠습니까?"
		}, function() {
			if (this.key == "ok") {
				document.execCommand("ClearAuthenticationCache");
				document.location.href = "/Windchill/login/logout.jsp";
			} else if (this.key == "cancel" || this.state == "close") {
				mask.close();
			}
		})
	}).mouseover(function() {
		$(this).attr("title", "로그아웃");
	})

	$(".userName").mouseover(function() {
		$("#infoBox").show();
	})

	$.fn.initCreateBtn = function() {
		$text = $("input[type=text]");
		$.each($text, function(idx) {
			$text.eq(idx).val("");
		})

		$textarea = $("textarea");
		$.each($textarea, function(idx) {
			$textarea.eq(idx).val("");
		})

		$hidden = $("input[type=hidden]");
		$.each($hidden, function(idx) {
			$hidden.eq(idx).val("");
		})

		$("span.cnt").text("0");
	}

	$("#initCreateBtn").click(function() {
		$(document).initCreateBtn();
	})

	$("#userInfo").mouseout(function(e) {
		var target;

		if (e.toElement) {
			target = e.toElement.localName;
		}

		if (target != "div" || target != "li" && target != "ul") {
			// $("#infoBox").hide();
		}
	})

	// $("#paging_count").change(function(e) {
	// $psize = $(this).val();
	// $("input[name=psize]").val($psize);
	// $("input[name=sessionid]").val(0);
	// $("input[name=tpage]").val(1);
	//
	// var url = "/Windchill/plm/common/saveUserPaging";
	// var params = new Object();
	// params.module = $("#module").val();
	// params.list = $psize;
	// $(document).onLayer();
	// $(document).ajaxCallServer(url, params, function(data) {
	// document.forms[0].submit();
	// }, true);
	// })

	// $(".page_table a").click(function() {
	// $page = $(this).data("page");
	// $("input[name=tpage]").val($page);
	// $(document).onLayer();
	// document.forms[0].submit();
	// })

	$.fn.onLayer = function() {
		// document.location.href = "#";
		mask.open();
		$("#loading_layer").show();
		$(document).setHTML();
	}

	$.fn.offLayer = function() {
		mask.close();
		$("#loading_layer").hide();
		$(document).setHTML();
	}

	$.fn.cellOpenClick = function(key, url) {
		$(".list_table td").click(function(e) {
			$column = $(this).data("column");
			if (key + "_column" == $column) {
				$oid = $(this).parent("tr").data("oid");
				$(document).openURLViewOpt(url + "?popup=true&oid=" + $oid, 1400, 700, "");
			}
		}).mouseover(function() {
			$column = $(this).data("column");
			if (key + "_column" == $column) {
				$oid = $(this).parent("tr").data("oid");
				var obj = $(document).getObjectName($oid);
				var text = $(this).text();
				$(this).css("cursor", "pointer").attr("title",  text + " " + obj + "정보보기");
			}
		})
	}

	$.fn.cellClick = function(key, url) {
		$(document).on("mouseover", ".list_table td", function() {
			$column = $(this).data("column");
			$(this).css("cursor", "pointer");
		})

		$(".list_table td").click(function(e) {
			$column = $(this).data("column");
			if (key + "_column" == $column) {
				$(document).onLayer();
				$oid = $(this).parent("tr").data("oid");
				document.location.href = url + "?oid=" + $oid;
			}
		}).mouseover(function() {
			$column = $(this).data("column");
			if (key + "_column" == $column) {
				$oid = $(this).parent("tr").data("oid");
				var obj = $(document).getObjectName($oid);
				var text = $(this).text();
				$(this).css("cursor", "pointer").attr("title",  text + " " + obj + "정보보기");
			}
		})
	}

	$(document).on("mouseover", ".list_tr", function() {
		$bool = $(this).children().find("input[name=oid]").prop("checked");
		if (!$bool) {
			$bg = $(this).css("background-color");
			if ($bg != "rgb(183, 240, 177)" && $bg != "rgb(173, 197, 245)") {
				$(this).css("background-color", "#e0f0fd");
			}

			// $(this).css("background-color", "#e0f0fd");
		}
	}).on("mouseout", ".list_tr", function() {
		$bool = $(this).children().find("input[name=oid]").prop("checked");
		if (!$bool) {
			$bg = $(this).css("background-color");
			if ($bg != "rgb(183, 240, 177)" && $bg != "rgb(173, 197, 245)") {
				$(this).css("background-color", "white");
			}
			// $(this).css("background-color", "white");
		}
	})

	$("#contextmenu").bind("contextmenu", function(e) {
		e.stopPropagation();
		e.preventDefault();
	})

	$(".list_table th").bind("contextmenu", function(e) {
		e.stopPropagation();
		e.preventDefault();
		$("div.rightmenu").hide();

		$("div.rightmenu_multi").hide();

		$all = $("input[name=all");
		$all.prop("checked", false);
		$all.next().removeClass("sed");
		$all.parent().parent().css("background-color", "white");

		$oid = $("input[name=oid]");
		$.each($oid, function(dd) {
			$oid.eq(dd).prop("checked", false);
			$oid.eq(dd).next().removeClass("sed");
			$oid.eq(dd).parent().parent().css("background-color", "white");
		})

		$("#contextmenu").toggle().css({
			"top" : e.pageY + "px",
			"left" : e.pageX + "px",
		})
	})

	$(".list_table th").click(function(e) {
		if (e.target.tagName == "I") {
			return false;
		}

		$sortKey = $(this).data("sortkey");
		$sort = $("input[name=sort]").val();
		if ($sortKey == "number" || $sortKey == "name" || $sortKey == "createDate") {
			var sortKeyView;
			if ($sortKey == "number") {
				sortKeyView = "master>number";
			} else if ($sortKey == "name") {
				sortKeyView = "master>name";
			} else if ($sortKey == "createDate") {
				sortKeyView = "thePersistInfo.createStamp";
			}

			var sortView;
			if ($sort == "false") {
				sortView = true;
			} else if ($sort == "true") {
				sortView = false;
			}

			$("input[name=sessionid]").val(0);
			$("input[name=sort]").val(sortView);
			$("input[name=sortKey]").val(sortKeyView);
			$(document).onLayer();
			$("form").submit();
		}
	}).mouseover(function() {
		$(this).css("cursor", "pointer");
	})

	$tr = $(".list_table tr");

	$("div.rightmenu").bind("contextmenu", function(e) {
		e.stopPropagation();
		e.preventDefault();
	})

	$("div.rightmenu_multi").bind("contextmenu", function(e) {
		e.stopPropagation();
		e.preventDefault();
	})

	$defaultLen = 0;
	$.each($tr, function(idx) {
		$defaultLen = $(".excelExport").data("len");
		$tr.eq(idx).bind("contextmenu", function(e) {
			e.preventDefault();
			$ids = $(this).attr("id");
			if ($ids == "header_tr") {
				return false;
			}

			if ($("#contextmenu").css("display") == "block") {
				$("#contextmenu").hide();
			}

			$(".excelExport").data("len", $defaultLen);

			$excelBox = $("input[name=excelBox]");
			$.each($excelBox, function(idx) {
				$excelBox.eq(idx).prop("checked", true);
				$excelBox.eq(idx).next().addClass("sed");
			})

			$oid = $("input[name=oid]");
			$checkMulti = false;
			$cnt = 0;
			$.each($oid, function(ii) {
				if ($oid.eq(ii).prop("checked") == true) {
					$cnt++;
				}
				if ($cnt >= 2) {
					$checkMulti = true;
					return true;
				}
			})

			if ($checkMulti) {
				$("." + $(this).data("key") + "_multi").show().css({
					"top" : e.pageY + "px",
					"left" : e.pageX + "px",
				})
			} else {
				$.each($oid, function(dd) {
					$oid.eq(dd).prop("checked", false);
					$oid.eq(dd).next().removeClass("sed");
					$oid.eq(dd).parent().parent().css("background-color", "white");
				})

				$("input[name=oid").eq(idx - 1).prop("checked", true);
				$("input[name=oid").eq(idx - 1).next().addClass("sed");
				$("input[name=oid").eq(idx - 1).parent().parent().css("background-color", "#fbfed1");
				$("#count_text").html("(" + $list_tr.length + "/<font color=\"red\">1</font>)개 선택됨");

				$(document).setRightMenuData($(this).data("oid"));

				$("." + $(this).data("key")).show().css({
					"top" : e.pageY + "px",
					"left" : e.pageX + "px",
				})
			}
		})
	})

	$left_url = $("span.left_title");
	$.each($left_url, function(idx) {
		$left_url.eq(idx).click(function() {
			$url = $left_url.eq(idx).data("url");

			if ($url == undefined) {
				return false;
			}
			$(document).onLayer();
			document.location.href = "/Windchill/plm" + $url;
		})
	})

	$(".hideColumn").click(function() {
		$bool = 0;
		$hideBox = $("input[name=hideBox]");

		$columnKey = $(this).data("column");
		$data_column = $(".list_table td");
		$.each($hideBox, function(idx) {
			$check = $hideBox.eq(idx).prop("checked");

			if ($check == false) {
				$bool++;
			}
		})

		if ($bool != $hideBox.length) {
			$.each($data_column, function(idx) {
				if ($columnKey + "_column" == $data_column.eq(idx).data("column")) {
					$data_column.eq(idx).toggle();
				}
			})

			$("#" + $columnKey).toggle();
			// th 개수 만큼 넓이 재조정..
			// $width = $(".list_table").width();
			$th = $(".list_table th");
			$.each($th, function(idx) {
				if (idx == 0) {
					return true;
				}
				$s = 100 / ($th.length - 1);
				$th.eq(idx).css("width", $s + "px;");
			})
		}

		if ($bool == $hideBox.length) {
			var dialogs = $(document).setOpen();
			dialogs.alert({
				theme : "alert",
				title : "컬럼 숨기기",
				msg : "모든 컬럼을 숨김 처리 할 수 없습니다."
			}, function() {
				if (this.key == "ok") {
					$(this).prop("checked", true);
				}
			})
			return false;
		}

		var url = "/Windchill/plm/common/saveUserTableSet";
		var params = new Object();
		params = $(document).getDataKeys(params);
		params = $(document).getDataHeaders(params);
		params = $(document).getDataStyles(params);
		params = $(document).getDataIndex(params);
		params.module = $("#module").val();
		$(document).ajaxCallServer(url, params, function(data) {

		}, false);
	})

	$("[data-magnify=gallery]").magnify({
		draggable : true,
		resizable : true,
		movable : true,
		keyboard : true,
		title : true,
		modalWidth : 350,
		modalHeight : 350,
		fixedContent : false,
		initMaximized : false,
		// gapThreshold : 1,
		// ratioThreshold : 1,
		minRatio : 0.5,
		maxRatio : 5,
		headToolbar : [ 'maximize', 'close' ],
		footToolbar : [ 'zoomIn', 'zoomOut', 'prev', 'fullscreen', 'next', 'actualSize', 'rotateLeft', 'rotateRight' ],
		i18n : {
			minimize : '최대',
			maximize : '최대화면',
			close : '닫기',
			zoomIn : '확대',
			zoomOut : '축소',
			prev : '이전',
			next : '다음',
			fullscreen : '풀 스크린',
			actualSize : '원본사이즈',
			rotateLeft : '왼쪽회전',
			rotateRight : '오른쪽회전',
		},
		initEvent : 'click',
		multiInstances : true,
		// ??
		fixedModalPos : false,
		zIndex : 2000,
		initAnimation : true
	})

	$(".delete-text").click(function() {
		$target = $(this).data("target");
		$prefix = $(this).data("prefix");

		$("#" + $target).val("");
		$("#" + $target + $prefix).val("");

	})

	$(".delete-calendar").click(function() {
		$start = $(this).data("start");
		$end = $(this).data("end");

		$("#" + $start).val("");
		$("#" + $end).val("");
	})

	$.fn.init_table = function() {
		$(".search_table input[type=text]").val("");
		$(".search_table input[type=hidden]").val("");
		$(".search_table input[type=checkbox]").prop("checked", false);
		$(".search_table input[type=checkbox]").next().removeClass("sed");
		$(".search_table input[type=radio]:eq(0)").prop("checked", true);
		$s = $(".search_table .ico-radio");
		$select = $(".search_table select");
		$.each($select, function(idx) {
			$select.eq(idx).find("option:eq(0)").prop("selected", true);
			$("div.selectedText").text("선택");
		})

		$.each($s, function(idx) {
			if (idx == 0) {
				$s.eq(idx).prop("checked", true);
				$s.eq(idx).addClass("sed");
			} else {
				$s.eq(idx).prop("checked", false);
				$s.eq(idx).removeClass("sed");
			}
		})
	}

	$("#init_table").click(function() {
		$(document).init_table();
	})

	$.fn.setUserLines = function(params) {
		if (params == null) {
			params = new Object();
		}

		$appOid = $("input[name=appValue]");
		$agreeOid = $("input[name=agreeValue]");
		$receiveOid = $("input[name=receiveValue]");

		var appArr = new Array();
		$.each($appOid, function(idx) {
			var value = $appOid.eq(idx).val();
			appArr.push(value);
		})

		var agreeArr = new Array();
		$.each($agreeOid, function(idx) {
			var value = $agreeOid.eq(idx).val();
			agreeArr.push(value);
		})

		var receiveArr = new Array();
		$.each($receiveOid, function(idx) {
			var value = $receiveOid.eq(idx).val();
			receiveArr.push(value);
		})

		params.appList = appArr;
		params.agreeList = agreeArr;
		params.receiveList = receiveArr;
		return params;
	}

	$("span.user_span").click(function() {
		$("#my_info").toggle(100);
	})

	$(".sub_url").click(function() {
		$url = $(this).data("url");
		$(document).onLayer();
		document.location.href = $url;
	})

	$("i.siteMap").click(function() {
		$("#siteMap").toggle(500);
	})

	$(".approval").mouseover(function() {
		$(".approval>ul").show();
	}).mouseout(function() {
		$(".approval>ul").hide();
	})

	$(".project").mouseover(function() {
		$(".project>ul").show();
	}).mouseout(function() {
		$(".project>ul").hide();
	})

	$(".echange").mouseover(function() {
		$(".echange>ul").show();
	}).mouseout(function() {
		$(".echange>ul").hide();
	})

	$(".document").mouseover(function() {
		$(".document>ul").show();
	}).mouseout(function() {
		$(".document>ul").hide();
	})

	$(".epm").mouseover(function() {
		$(".epm>ul").show();
	}).mouseout(function() {
		$(".epm>ul").hide();
	})

	$(".part").mouseover(function() {
		$(".part>ul").show();
	}).mouseout(function() {
		$(".part>ul").hide();
	})

	$(".admin").mouseover(function() {
		$(".admin>ul").show();
	}).mouseout(function() {
		$(".admin>ul").hide();
	})

	var pathname = document.location.pathname;
	var last = pathname.lastIndexOf("/");
	var start = pathname.substring(0, last);
	var end = start.lastIndexOf("/");
	var module = start.substring(end + 1, last);

	if ("approval" == module) {
		$(".approval").addClass("headerBG");
	} else if ("epm" == module) {
		$(".epm").addClass("headerBG");
	} else if ("document" == module) {
		$(".document").addClass("headerBG");
	} else if ("part" == module) {
		$(".part").addClass("headerBG");
	} else if ("echange" == module) {
		$(".echange").addClass("headerBG");
	} else if ("project" == module) {
		$(".project").addClass("headerBG");
	} else if ("admin" == module) {
		$(".admin").addClass("headerBG");
	}


	$.fn.setHTML = function() {

		$("#statesPart").bindSelect();
		$("#statesEpm").bindSelect();
		$("#statesDoc").bindSelect();
		
		
//		$("select").bindSelect();
		
		$("input[name=pm]").add("input[name=sub_pm]").add("input[name=machine]").add("input[name=elec]").add("input[name=soft]").bindSelector({
			reserveKeys : {
				options : "list",
				optionValue : "value",
				optionText : "name"
			},
			optionPrintLength : "all",
			onsearch : function(objID, objVal, callBack) {

				var key = $("#" + objID).val();
				var dept = $("#"+ objID).data("dept");
				var resign = $("#"+ objID).data("resign");
				var params = new Object();
				if (key.indexOf("[") > -1) {
					var idx = key.indexOf("[");
					key = key.substring(0, idx - 1);
				}
				params.key = key;
				params.dept = dept;
				params.resign = resign;
				
				
				var url = "/Windchill/plm/bind/getUserBind";
				$(document).ajaxCallServer(url, params, function(data) {
					console.log(data);
					callBack({
						options : data.list
					})
				}, false);
			},
			onchange : function() {
				var value;
				var targetID = this.targetID;
				var target = targetID + "Oid";
				if (this.selectedOption != null) {
					value = this.selectedOption.value;
				}
				$("#" + target).remove();
				$("#" + targetID).before("<input type=\"hidden\" name=\"" + target + "\" id=\"" + target + "\"> ");
				$("#" + target).val(value);
			},
			finder : {
				onclick : function() {
					var dbl = $("#" + this.targetID).data("dbl");
					if (dbl == undefined) {
						dbl = "true";
					}

					var fun = $("#" + this.targetID).data("fun");
					if (fun == undefined) {
						fun = "addDblUsers";
					}

					var multi = $("#" + this.targetID).data("multi");
					if (multi == undefined) {
						multi = "false";
					}
					var target = this.targetID;
					var url = "/Windchill/plm/org/addUser?dbl=" + dbl + "&fun=" + fun + "&multi=" + multi + "&target=" + target;
					$(document).openURLViewOpt(url, 1200, 600, "");
				}
			}
		});

		
		
		
		$("input[name=reassignUser]").add("input[name=modifier]").add("input[name=creators]").bindSelector({
			reserveKeys : {
				options : "list",
				optionValue : "value",
				optionText : "name"
			},
			optionPrintLength : "all",
			onsearch : function(objID, objVal, callBack) {

				var key = $("#" + objID).val();
				var params = new Object();
				if (key.indexOf("[") > -1) {
					var idx = key.indexOf("[");
					key = key.substring(0, idx - 1);
				}
				params.key = key;

				var url = "/Windchill/plm/bind/getUserBind";
				$(document).ajaxCallServer(url, params, function(data) {
					callBack({
						options : data.list
					})
				}, false);
			},
			onchange : function() {
				var value;
				var targetID = this.targetID;
				var target = targetID + "Oid";
				if (this.selectedOption != null) {
					value = this.selectedOption.value;
				}
				$("#" + target).remove();
				$("#" + targetID).before("<input type=\"hidden\" name=\"" + target + "\" id=\"" + target + "\"> ");
				$("#" + target).val(value);
			},
			finder : {
				onclick : function() {
					var dbl = $("#" + this.targetID).data("dbl");
					if (dbl == undefined) {
						dbl = "true";
					}

					var fun = $("#" + this.targetID).data("fun");
					if (fun == undefined) {
						fun = "addDblUsers";
					}

					var multi = $("#" + this.targetID).data("multi");
					if (multi == undefined) {
						multi = "false";
					}
					var target = this.targetID;
					var url = "/Windchill/plm/org/addUser?dbl=" + dbl + "&fun=" + fun + "&multi=" + multi + "&target=" + target;
					$(document).openURLViewOpt(url, 1400, 700, "");
				}
			}
		});

		$(".twinDatePicker_m").each(function() {
			$(this).css("width", "35%");

			var sDateId = $(this).data("start");

			$("#" + sDateId).css("width", "35%");

			$(this).bindTwinDate({
				align : "left",
				valign : "top",
				buttonText : "확인",
				customPos : {
					top : 25,
					left : 25
				},
				startTargetID : sDateId,
				onchange : function() {
					// toast.push(Object.toJSON(this));
				}
			});
		});

		$(".twinDatePicker").each(function() {
			$(this).css("width", "35%");

			var sDateId = $(this).data("start");

			$("#" + sDateId).css("width", "35%");

			$(this).bindTwinDate({
				align : "left",
				valign : "top",
				buttonText : "확인",
				customPos : {
					top : 25,
					left : 25
				},
				startTargetID : sDateId,
				onchange : function() {
					// toast.push(Object.toJSON(this));
				}
			});
		});

		$(".twinDatePicker2").each(function() {
			$(this).css("width", "35%");

			var sDateId = $(this).data("start");

			$("#" + sDateId).css("width", "35%");

			$(this).bindTwinDate({
				align : "left",
				valign : "top",
				buttonText : "확인",
				customPos : {
					top : 25,
					left : 25
				},
				startTargetID : sDateId,
				onchange : function() {
					// toast.push(Object.toJSON(this));
				}
			});
		});
		$("#pType").bindSelect();
		$("#engType").bindSelect();
//		$("#customer").bindSelect();
//		$("#ins_location").bindSelect();
//		$("#kekState").bindSelectSetValue("");
//		$("#pType").bindSelectSetValue("");
//		$("#customer").bindSelectSetValue("");
//		$("#ins_location").bindSelectSetValue("");
	}

	$(".que").mouseover(function(e) {
		$(".que_panel").show().css({
			"top" : e.pageY + "px",
			"left" : e.pageX + "px",
		})
		$(document).setHTML();
	})

	$.fn.selectOne = function(box, tag) {
		for (var i = 0; i < box.length; i++) {
			if (box[i] != tag) {
				box.eq(i).prop("checked", false);
				box.eq(i).next().removeClass("sed");
				box.eq(i).parent().parent().css("background-color", "white");
			}
		}
	}

	$(".que_panel").mouseout(function(e) {
		$(this).hide();
	})

	$(".siteLink").click(function() {
		$url = $(this).data("url");
		$(document).onLayer();
		document.location.href = $url;
	}).mouseover(function() {
		$(this).css("cursor", "pointer");
	})

	$(".more_notice").add(".more_app").add(".more_checkout").click(function() {
		$url = $(this).data("url");
		$(document).onLayer();
		document.location.href = $url;
	}).mouseover(function() {
		$(this).css("cursor", "pointer");
	})

	$("input[name=MIN_TEMP_START]").on("keyup", function() {
		$(this).val($(this).val().replace(/[^0-9]/g, ""));
	})

	$("input[name=MIN_TEMP_END]").on("keyup", function() {
		$(this).val($(this).val().replace(/[^0-9]/g, ""));
	})

	$("input[name=MAX_TEMP_START]").on("keyup", function() {
		$(this).val($(this).val().replace(/[^0-9]/g, ""));
	})

	$("input[name=MAX_TEMP_END]").on("keyup", function() {
		$(this).val($(this).val().replace(/[^0-9]/g, ""));
	})

	// 컬럼 이동
	$(document).on("keyup", ".form-control", function(e) {
		// if ($(this).val() == 0) {
		// $(this).val("");
		// }
		// if (e.keyCode < 48 || e.keyCode > 57) {
		// $(this).val("");
		// }
	})

}).bind("click", function(e) {
	$("#contextmenu").hide();
	$tr = $(".list_table tr");

	$("div.rightmenu").hide();
	$("div.rightmenu_multi").hide();
	$(".que_panel").hide();
}).scroll(function() {
	// $(document).setHTML();
	// $("#contextmenu").hide();
	var pathname = document.location.pathname;
	var idx = pathname.lastIndexOf("/");
	var s = pathname.substring(idx + 1);
	if (s == "addEpm") {
		var ss = $("#statesEpm").val();
		if (ss != "") {
			$("#statesEpm").bindSelectDisabled(true);
		}
		$("#epmTypes").bindSelectDisabled(true);
	}

	if (s == "addPart" || s == "addEBOM" || s == "addECN") {
		var ss = $("#statesPart").val();
		if (ss != "") {
			$("#statesPart").bindSelectDisabled(true);
		}
	}
}).keyup(function(e) {
	if(e.keyCode == 27) {
		mask.close();
		$("#loading_layer").hide();
	}
})

// 기본 스크립트

function setBoxs(box) {
	$box = $(box);
	$.each($box, function(idx) {
		if (!$box.eq(idx).hasClass("isBox")) {
			$(this).checks();
		}
	})
}