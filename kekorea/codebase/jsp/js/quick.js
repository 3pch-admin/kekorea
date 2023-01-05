$(document).ready(function() {

	$pagingBox = $(".pagingBox");
	$pagingBox.click(function(e) {

		var url = "/Windchill/plm/common/saveUserPaging";
		var value;
		$.each($pagingBox, function(idx) {
			if ($pagingBox.eq(idx).prop("checked") == true) {
				value = $pagingBox.eq(idx).val();
			}
		})
		var params = new Object();
		params.module = $("#module").val();
		params.list = value;
		$(document).ajaxCallServer(url, params, function(data) {
			$("#contextmenu").hide();
			$("#paging_count").bindSelectSetValue(data.list);
			$(document).getColumn();
		}, true);
	})

	$(".excelBox").click(function() {
		$excelBox = $("input[name=excelBox]");
		$len = $(".excelExport").data("len");
		$click = $(this).prop("checked");

		if ($click == false) {
			$(".excelExport").data("len", ($len - 1));
		} else if ($click == true) {
			$(".excelExport").data("len", ($len + 1));
		}

		$checkLen = $(".excelExport").data("len");
		if ($checkLen == 0) {
			var dialogs = $(document).setOpen();
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "출력할 엑셀 컬럼을 모두 해제 할 수 없습니다.",
				width : 380
			}, function() {
				if (this.key == "ok") {
					$(this).prop("checked", true);
					for (var i = 0; i < $tr.length; i++) {
						$("div.rightmenu").hide();
					}
				}
			})
			return false;
		}
	})

	$(".main_paging").mouseover(function(e) {
		var pos = $(this).position();
		$(".sub_paging").show();
		$(".sub_paging").css("top", pos.top);
	}).mouseout(function() {
		$(".sub_paging").hide();
	})

	$(".main_part_function").mouseover(function(e) {
		var pos = $(this).position();
		$(".sub_part_function").show();
		$(".sub_part_function").css("top", pos.top);
	}).mouseout(function() {
		$(".sub_part_function").hide();
	})

	$(".main_epm_function").mouseover(function(e) {
		var pos = $(this).position();
		$(".sub_epm_function").show();
		$(".sub_epm_function").css("top", pos.top);
	}).mouseout(function() {
		$(".sub_epm_function").hide();
	})

	$(".main_download").mouseover(function() {
		var pos = $(this).position();
		$(".sub_download").show();
		$(".sub_download").css("top", pos.top);
	}).mouseout(function() {
		$(".sub_download").hide();
	})

	$(".main_info").mouseover(function() {
		var pos = $(this).position();
		$(".sub_info").show();
		$(".sub_info").css("top", pos.top - 1);
	}).mouseout(function() {
		$(".sub_info").hide();
	})

	$(".main_drw").mouseover(function() {
		var pos = $(this).position();
		$(".sub_drw").show();
		$(".sub_drw").css("top", pos.top);
	}).mouseout(function() {
		$(".sub_drw").hide();
	})

	$(".main_epm_erp").mouseover(function() {
		var pos = $(this).position();
		$(".sub_epm_erp").show();
		$(".sub_epm_erp").css("top", pos.top);
	}).mouseout(function() {
		$(".sub_epm_erp").hide();
	})

	$(".setState").mouseover(function() {
		var pos = $(this).position();
		$(".sub_state_info").show();
		$(".sub_state_info").css("top", pos.top);
	}).mouseout(function() {
		$(".sub_state_info").hide();
	})

	$(".main_part_erp").mouseover(function() {
		var pos = $(this).position();
		$(".sub_part_erp").show();
		$(".sub_part_erp").css("top", pos.top);
	}).mouseout(function() {
		$(".sub_part_erp").hide();
	})

	$(".main_part_info").mouseover(function() {
		var pos = $(this).position();
		$(".sub_part_info").show();
		$(".sub_part_info").css("top", pos.top);
	}).mouseout(function() {
		$(".sub_part_info").hide();
	})

	$(".main_excel").mouseover(function(e) {
		var pos = $(this).position();
		$(".sub_excel").show();
		$(".sub_excel").css("top", pos.top);
	}).mouseout(function() {
		$(".sub_excel").hide();
	})

	var w = screen.width;
	var h = screen.height;

	$.fn.setRightMenuData = function($oid) {
		// 객체 정보
		$(".infoObj").data("oid", $oid);
		// 버전 정보
		$(".infoVersion").data("oid", $oid);
		// 결재 이력
		$(".infoApprovalHistory").data("oid", $oid);
		// 엑셀 출력
		$("li.exportExcel").data("oid", $oid);
		// bom
		$("li.infoBom").data("oid", $oid);
		// bom 에디터
		$("li.bomEditor").data("oid", $oid);
		// 제품
		$("li.infoEndPart").data("oid", $oid);
		// 상위품목
		$("li.infoUpPart").data("oid", $oid);
		// 하위품목
		$("li.infoDownPart").data("oid", $oid);
		// erp part 전송
		$("li.sendERPPART").data("oid", $oid);
		// erp bom 전송
		$("li.sendERPBOM").data("oid", $oid);

		$("li.exportBom").data("oid", $oid);

		$("li.creoViewOpen").data("oid", $oid);
		// 파생품
		// $(".saveAsObject").data("oid", $oid);
		$("li.exportBom").data("oid", $oid);

		$("li.viewERPHistory").data("oid", $oid);
	}

	$("li.viewERPHistory").click(function() {
		$oid = $(this).data("oid");
		var url = "/Windchill/plm/erp/viewERPHistory?oid=" + $oid + "&popup=true";
		$(document).openURLViewOpt(url, 1200, 500, "");
	})

	$("li.publishThum").click(function() {
		var url = "/Windchill/plm/common/doPublisherMulti";
		var dialogs = $(document).setOpen();
		var params = $(document).getListParams();
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
	})

	$("li.publishThumMulti").click(function() {
		var url = "/Windchill/plm/common/doPublisherMulti";
		var dialogs = $(document).setOpen();
		var params = $(document).getListParams();
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
	})

	$("li.creoViewOpen").click(function() {
		$oid = $(this).data("oid");
		var url = "/Windchill/plm/common/getCreoViewURL";
		var params = new Object();
		params.oid = $oid;
		var box = $(document).setOpen();
		$(document).ajaxCallServer(url, params, function(data) {
			if (data.creoView == undefined) {
				box.alert({
					theme : "alert",
					title : "경고",
					msg : data.msg,
					width : 350
				}, function() {
					if (this.key == "ok" || this.state == "close") {
						mask.close();
					}
				})
			} else {
				$url = data.creoView;
				createCDialogWindow($url, "CreoView", "1200", "600", "0", "0");
			}
		}, false);
	})

	$("li.exportBom").click(function() {
		var dialogs = $(document).setNonOpen();
		var oid = $(this).data("oid");
		dialogs.confirm({
			theme : "info",
			title : "확인",
			msg : "BOM 엑셀 출력을 하시겠습니까?"
		}, function() {
			if (this.key == "ok") {
				var url = "/Windchill/plm/part/exportBomExcel";
				var params = new Object();
				params.url = document.location.href;
				params.oid = oid;
				$(document).onLayer();
				$(document).ajaxCallServer(url, params, function(data) {
					if (browserChecker.ie) {
						window.open(data.url, "_blank", "width=100, height=100");
					} else {
						$("#downloadFileContent").attr("href", data.url);
						document.getElementById("downloadFileContent").click();
					}
					$(document).offLayer();

				}, false);
				return false;
			} else if (this.key == "cancel" || this.state == "close") {
				mask.close();
			}
		})
	})

	$(".setStateObj").click(function() {
		var dialogs = $(document).setNonOpen();
		var box = $(document).setOpen();
		$sbox = $("input[name=stateBox]");
		$bool = false;
		$state = "";
		$.each($sbox, function(idx) {
			if ($sbox.eq(idx).prop("checked") == true) {
				$bool = true;
				$state = $sbox.eq(idx).val();
				return false;
			}
		})

		// alert($state);
		if ($bool == false) {
			$("#quickmenu_multi").hide();
			$("#quickmenu").hide();
			box.alert({
				theme : "alert",
				title : "경고",
				msg : "변경할 상태값을 선택하세요."
			}, function() {
				if (this.key == "ok") {
					// $name.focus();
				}
			})
			return false;
		}

		dialogs.confirm({
			theme : "info",
			title : "확인",
			width : 380,
			msg : "선택한 객체의 상태값을 변경 하시겠습니까?"
		}, function() {
			if (this.key == "ok") {
				$checkbox = $("input[name=oid]");
				var items = "";
				$.each($checkbox, function(idx) {
					if ($checkbox.eq(idx).prop("checked") == true) {
						var value = $checkbox.eq(idx).val();
						items += value + ",";
					}
				})
				items = items.substring(0, items.length - 1);
				var params = new Object();
				params.items = items;
				params.state = $state;
				var url = "/Windchill/plm/common/setStateObjAction";
				$(document).ajaxCallServer(url, params, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						msg : data.msg,
						width : 400
					}, function() {
						if (this.key == "ok" || this.state == "close") {
							document.location.reload();
						}
					})
				}, true);
			} else if (this.key == "cancel" || this.state == "close") {
				mask.close();
			}
		})
	})

	$(".resign").click(function() {
		var dialogs = $(document).setNonOpen();
		dialogs.confirm({
			theme : "info",
			title : "확인",
			width : 380,
			msg : "선택된 사용자를 퇴사처리 하시겠습니까?"
		}, function() {
			if (this.key == "ok") {
				var params = new Object();
				params = $(document).getCheckBoxValue(params, "input[name=oid]", "list");
				var url = "/Windchill/plm/org/setResignAction";
				$(document).ajaxCallServer(url, params, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						msg : data.msg,
						width : 400
					}, function() {
						if (this.key == "ok" || this.state == "close") {
							document.location.reload();
						}
					})
				}, true);
			} else if (this.key == "cancel" || this.state == "close") {
				mask.close();
			}
		})
	})

	$(".deleteObject").click(function() {
		var dialogs = $(document).setNonOpen();
		dialogs.confirm({
			theme : "info",
			title : "확인",
			msg : "선택된 데이터를 삭제 하시겠습니까?\n삭제한 데이터는 복구가 불가능합니다."
		}, function() {
			if (this.key == "ok") {
				$checkbox = $("input[name=oid]");
				var items = "";
				$.each($checkbox, function(idx) {
					if ($checkbox.eq(idx).prop("checked") == true) {
						var value = $checkbox.eq(idx).val();
						items += value + ",";
					}
				})
				items = items.substring(0, items.length - 1);
				var params = new Object();
				params.items = items;
				params.url = document.location.href;
				var url = "/Windchill/plm/common/deleteObject";
				$(document).ajaxCallServer(url, params, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						msg : data.msg,
						width : 400
					}, function() {
						if (this.key == "ok" || this.state == "close") {
							document.location.href = data.url;
						}
					})
				}, true);
			} else if (this.key == "cancel" || this.state == "close") {
				mask.close();
			}
		})
	})

	$("li.downAll").click(function() {
		var url = "/Windchill/plm/epm/downAll";
		var params = $(document).getListParams("list", "input[name=oid]");
		$(document).onLayer();
		console.log(params);
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
	})

	$("li.contentsDocDown").click(function() {
		var url = "/Windchill/plm/content/contentsDocDown";
		var params = $(document).getListParams();

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
	})

	// 모두
	$("li.downContentAll").click(function() {
		var url = "/Windchill/plm/content/downContentAll";
		var params = $(document).getListParams();

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
	})

	// 주 첨부파일
	$("li.downPrimary").click(function() {
		var url = "/Windchill/plm/content/downPrimary";
		var params = $(document).getListParams();

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
	})

	// 첨부파일
	$("li.downSecondary").click(function() {
		var url = "/Windchill/plm/content/downSecondary";
		var params = $(document).getListParams();

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
	})

	$("li.contentsDown").click(function() {
		var url = "/Windchill/plm/content/contentsDown";
		var params = $(document).getListParams();

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
	})

	$("li.contentsMultiDown").click(function() {
		var url = "/Windchill/plm/content/contentsMultiDown";
		var params = $(document).getListParams();

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
	})

	$("li.downPdf").click(function() {
		var url = "/Windchill/plm/epm/downPdf";
		var params = $(document).getListParams("list", "input[name=oid]");
		var dialogs = $(document).setOpen();
		$(document).onLayer();
		$(document).ajaxCallServer(url, params, function(data) {

			if (data.reload == true) {
				dialogs.alert({
					theme : "alert",
					title : "경고",
					msg : data.msg
				}, function() {
					if (this.key == "ok" || this.state == "close") {
						$(document).offLayer();
					}
				})
				$("#loading_layer").hide();
				$("#quickmenu").hide();
				return false;
			}

			if (browserChecker.ie) {
				window.open(data.url, "_blank", "width=100,height=100");
			} else {
				$("#downloadFileContent").attr("href", data.url);
				document.getElementById("downloadFileContent").click();
			}

			$(document).offLayer();
		}, false);
		return false;
	})

	$("li.downDwg").click(function() {
		var url = "/Windchill/plm/epm/downDwg";
		var params = $(document).getListParams("list", "input[name=oid]");
		$(document).onLayer();
		$(document).ajaxCallServer(url, params, function(data) {
			if (data.reload == true) {
				dialogs.alert({
					theme : "alert",
					title : "경고",
					msg : data.msg
				}, function() {
					if (this.key == "ok" || this.state == "close") {
						$(document).offLayer();
					}
				})
				$("#loading_layer").hide();
				$("#quickmenu").hide();
				return false;
			}
			if (browserChecker.ie) {
				window.open(data.url, "_blank", "width=100,height=100");
			} else {
				$("#downloadFileContent").attr("href", data.url);
				document.getElementById("downloadFileContent").click();
			}

			$(document).offLayer();
		}, false);
		return false;
	})

	$("li.downDrw").click(function() {
		var url = "/Windchill/plm/epm/downDrw";
		var params = $(document).getListParams();
		$(document).onLayer();
		$(document).ajaxCallServer(url, params, function(data) {
			if (data.reload == true) {
				dialogs.alert({
					theme : "alert",
					title : "경고",
					msg : data.msg
				}, function() {
					if (this.key == "ok" || this.state == "close") {
						$(document).offLayer();
					}
				})
				$("#loading_layer").hide();
				$("#quickmenu").hide();
				return false;
			}
			if (browserChecker.ie) {
				window.open(data.url, "_blank", "width=100,height=100");
			} else {
				$("#downloadFileContent").attr("href", data.url);
				document.getElementById("downloadFileContent").click();
			}
			$(document).offLayer();

		}, false);
		return false;
	})

	$("li.downPrt").click(function() {

	})

	$("li.sendERPDRW").click(function() {
		var url = "/Windchill/plm/erp/sendERPDWGAction";
		var params = $(document).getListParams("list", "input[name=oid]");
		var dialogs = $(document).setOpen();
		if (params.list.length == 0) {
			dialogs.alert({
				theme : "alert",
				title : "DWG 전송 도면 미선택",
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
	})

	$("li.sendERPPDF").click(function() {
		var url = "/Windchill/plm/erp/sendERPPDFAction";
		var params = $(document).getListParams("list", "input[name=oid]");
		var dialogs = $(document).setOpen();
		if (params.list.length == 0) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "PDF를 전송할 도면을 선택하세요."
			})
			return false;
		}
		$(document).ajaxCallServer(url, params, function(data) {
			dialogs.alert({
				theme : "alert",
				width : 350,
				title : "PDF 전송 완료",
				msg : data.msg
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					// $(document).getColumn();
				}
			})
		}, false);
	})

	$(".printDrw").click(function() {
		var params = $(document).getListParams();
		var dialogs = $(document).setOpen();

		var url = "/Windchill/plm/epm/checkDrawing";

		$(document).ajaxCallServer(url, params, function(data) {

			if (!data.is2D) {
				dialogs.alert({
					theme : "alert",
					width : 350,
					title : "결과",
					msg : data.msg
				}, function() {
					if (this.key == "ok" || this.state == "close") {
						return false;
					}
				})
			} else {
				dialogs.confirm({
					theme : "info",
					title : "확인",
					msg : "선택한 도면들을 출력 하시겠습니까?",
					width : 380
				}, function() {
					if (this.key == "ok") {
						$checkbox = $("input[name=oid]");
						var items = "";
						$.each($checkbox, function(idx) {
							var value = $checkbox.eq(idx).val();
							if ($checkbox.eq(idx).prop("checked") == true) {
								items += value + ",";
							}
						})
						items = items.substring(0, items.length - 1);
						$("#items").val(items);
						var url = "/Windchill/jsp/epm/printClipboard.jsp";
						var title = "batchPrint";
						var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
						leftpos = (screen.width - 1000) / 2;
						toppos = (screen.height - 600) / 2;
						rest = "width=1000,height=600,left=" + leftpos + ',top=' + toppos;
						var newwin = window.open("", title, opts + rest);
						$("form").attr("target", title); // form.target 이
						// 부분이 빠지면 form값
						// 전송이 되지 않습니다.
						$("form").attr("action", url); // form.action 이 부분이
						// 빠지면 action값을c 찾지
						// 못해서 제대로 된 팝업이 뜨질
						// 않습니다.
						$("form").attr("method", "post");
						$("form").submit();
						newwin.focus();
						$("form").attr("action", "");
					}
				})
			}
		}, true);
	})

	$(".saveAsObject").click(function() {
		$checkbox = $("input[name=oid]");
		var items = "";
		$.each($checkbox, function(idx) {
			if ($checkbox.eq(idx).prop("checked") == true) {
				var value = $checkbox.eq(idx).val();
				items += value + ",";
			}
		})
		items = items.substring(0, items.length - 1);

		$("#items").val(items);

		var url = "/Windchill/plm/common/saveAsObject";
		var title = "saveAsObject";
		var opts = "toolbar=0,location=0,directory=0,status=1,menubar=0,scrollbars=1,resizable=1,";
		leftpos = (screen.width - 1500) / 2;
		toppos = (screen.height - 600) / 2;
		rest = "width=1500, height=600,left=" + leftpos + ',top=' + toppos;
		var newwin = window.open("", title, opts + rest);
		$("form").attr("target", title); // form.target 이
		// 부분이 빠지면 form값
		// 전송이 되지 않습니다.
		$("form").attr("action", url); // form.action 이 부분이
		// 빠지면 action값을 찾지
		// 못해서 제대로 된 팝업이 뜨질
		// 않습니다.
		$("form").attr("method", "post");
		$("form").submit();
		newwin.focus();
		$("form").attr("action", "");
		$("form").attr("target", "_self");
	})

	$("li.sendERPBOM").click(function() {
		var url = "/Windchill/plm/erp/sendERPBOMAction";
		var params = $(document).getListParams();
		var dialogs = $(document).setOpen();
		if (params.list.length == 0) {
			var dialogs = $(document).setOpen();
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "ERP 서버로 전송할 BOM을 선택하세요."
			})
			return false;
		}

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "선택한 부품의 BOM을 ERP 서버로 전송 하시겠습니까",
			width : 400
		}, function() {
			if (this.key == "ok") {
				$(document).ajaxCallServer(url, params, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						msg : data.msg,
						width : 400
					}, function() {
						if (this.key == "ok" || this.state == "close") {
							// document.location.reload();
						}
					})
				}, true);
			} else if (this.key == "cancel" || this.state == "close") {
				mask.close();
			}
		})
	})

	$("li.sendERPPART").click(function() {
		var url = "/Windchill/plm/erp/sendERPPARTAction";
		var params = $(document).getListParams();
		var dialogs = $(document).setOpen();
		if (params.list.length == 0) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "ERP 서버로 전송할 부품을	 선택하세요."
			})
			return false;
		}

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "선택한 부품들을 ERP 서버로 전송 하시겠습니까",
			width : 400
		}, function() {
			if (this.key == "ok") {
				$(document).ajaxCallServer(url, params, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						msg : data.msg,
						width : 400
					}, function() {
						if (this.key == "ok" || this.state == "close") {
							// document.location.reload();
						}
					})
				}, true);
			} else if (this.key == "cancel" || this.state == "close") {
				mask.close();
			}
		})
	})

	$("li.infoDownPart").click(function() {
		var $oid = $(this).data("oid");
		var url = "/Windchill/plm/part/infoDownPart?oid=" + $oid + "&popup=true";
		$(document).openURLViewOpt(url, 1000, 500, "");
	})

	$("li.infoUpPart").click(function() {
		var $oid = $(this).data("oid");
		var url = "/Windchill/plm/part/infoUpPart?oid=" + $oid + "&popup=true";
		$(document).openURLViewOpt(url, 1000, 500, "");
	})

	$("li.infoEndPart").click(function() {
		var $oid = $(this).data("oid");
		var url = "/Windchill/plm/part/infoEndPart?oid=" + $oid + "&popup=true";
		$(document).openURLViewOpt(url, 1200, 200, "");
	})

	$("li.bomEditor").click(function() {
		var $oid = $(this).data("oid");
		var url = "/Windchill/plm/part/bomEditor?oid=" + $oid;
		$(document).openURLViewOpt(url, 1400, 600, "");
	})

	$("li.infoBom").click(function() {
		var $oid = $(this).data("oid");
		var url = "/Windchill/plm/part/infoBom?oid=" + $oid + "&popup=true";
		$(document).openURLViewOpt(url, screen.width, screen.height, "");
	})

	$("li.excelExport").click(function() {
		$li = $(this).parent().find("li");
		$opt = $(this).data("opt");
		var dialogs = $(document).setNonOpen();
		dialogs.confirm({
			theme : "info",
			title : "확인",
			msg : "엑셀 출력을 하시겠습니까?"
		}, function() {
			if (this.key == "ok") {
				var url = "/Windchill/plm/common/exportExcel";
				// var params = new Object();
				var array = new Array();
				var array_name = new Array();
				$.each($li, function(idx) {
					if ($li.eq(idx).find("input[name=excelBox]").prop("checked") == true) {
						var value = $li.eq(idx).find("input[name=excelBox]").val();
						var columns_name = $li.eq(idx).find("input[name=excelBox]").data("display");
						array.push(value);
						array_name.push(columns_name);
					}
				})
				var params = $(document).getListParams();
				params = grid.getParams(params);
				params.url = document.location.href;
				params.opt = $opt;
				params.columns = array;
				params.columns_name = array_name;
				params.module = $("#module").val();

				$(document).onLayer();
				$(document).ajaxCallServer(url, params, function(data) {
					if (browserChecker.ie) {
						window.open(data.url, "_blank", "width=100, height=100");
					} else {
						$("#downloadFileContent").attr("href", data.url);
						document.getElementById("downloadFileContent").click();
					}
					$(document).offLayer();

				}, false);
				return false;
			} else if (this.key == "cancel" || this.state == "close") {
				mask.close();
			}
		})
	})

	$(".infoApprovalHistory").click(function() {
		var $oid = $(this).data("oid");
		var url = "/Windchill/plm/approval/infoApprovalHistory?oid=" + $oid;
		$(document).openURLViewOpt(url, 1200, 400, "no");
	})

	$(".infoVersion").click(function() {
		var $oid = $(this).data("oid");
		var url = "/Windchill/plm/common/infoVersion?oid=" + $oid;
		$(document).openURLViewOpt(url, 800, 400, "no");
	})

	$(".infoObj").click(function() {
		var $oid = $(this).data("oid");
		var url = "/Windchill/plm/common/viewObject?oid=" + $oid;
		$(document).openURLViewOpt(url, 1200, 700, "no");
	})

	// $quick.click(function() {
	// $("div.rightmenu").hide();
	// })
})