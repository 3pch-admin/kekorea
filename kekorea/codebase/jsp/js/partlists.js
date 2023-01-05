/**
 * 
 */

var partlists = {

	createPartListActionUrl : "/Windchill/plm/partList/createPartListAction",

	createRequestDocumentActionUrl : "/Windchill/plm/document/createRequestDocumentAction",

	modifyRequestDocumentActionUrl : "/Windchill/plm/document/modifyRequestDocumentAction",

	installExcelActionUrl : "/Windchill/plm/partList/installExcelAction",

	modifyPartListUrl : "/Windchill/plm/partList/modifyPartListMaster",

	modifyPartListActionUrl : "/Windchill/plm/partList/modifyPartListAction",

	deletePartListActionUrl : "/Windchill/plm/partList/deletePartListAction",

	addPartsAction : function(state) {
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
		var params = $(document).getListParams();

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
			html += "<td rowspan=\"2\"><input type=\"checkbox\" name=\"projectOid\" value=\"" + list[i][0] + "\"></td>";
			// html += "<td><a href=\"" + list[i][9] + "\"><img class=\"pos3\"
			// src=\"" + list[i][7] + "\"></a></td>";
			html += "<td class=\"infoEpms\" data-oid=\"" + list[i][0] + "\">" + list[i][1] + "</td>";
			html += "<td class=\"infoEpms left\" data-oid=\"" + list[i][0] + "\"><img class=\"pos3\" src=\"" + list[i][7] + "\">&nbsp;" + list[i][2] + "</td>";
			html += "<td>" + list[i][17] + "</td>";
			html += "<td>" + list[i][4] + "</td>";
			html += "<td>" + list[i][3].split("$")[0] + "</td>";
			/*
			 * if (list[i][11] == null) { html += "<td>&nbsp;</td>"; } else { html += "<td><a href=\"" + list[i][11] + "\"><img class=\"pos3\" src=\"" + list[i][10] + "\"></a></td>"; }
			 * 
			 * if (list[i][13] == null) { html += "<td>&nbsp;</td>"; } else { html += "<td><a href=\"" + list[i][13] + "\"><img class=\"pos3\" src=\"" + list[i][12] + "\"></a></td>"; }
			 */
			html += "<td>" + list[i][18] + "</td>";
			html += "<td>" + list[i][16] + "</td>";
			// html += "<td>" + list[i][6] + "</td>";
			html += "</tr>";
			html += "<tr class=\"" + list[i][0] + "\">";
			html += "<td colspan=\"7\" class=\"inputTd indent10 left\"><input type=\"text\" name=\"description\" class=\"AXInput widMax\"></td>";
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
	createRequestDocument : function() {
		var dialogs = $(document).setOpen();
		var url = this.createRequestDocumentActionUrl;

		var box = $(document).setNonOpen();

		$name = $("input[name=name]");
		if ($name.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "의뢰서 제목을 입력하세요."
			}, function() {
				if (this.state == "close" || this.key == "ok") {
					mask.close();
					$name.focus();
				}
			})
			return false;
		}

		if (popup != "true") {
			$pTemplate = $("select[name=pTemplate]");
			if ($pTemplate.val() == "") {
				dialogs.alert({
					theme : "alert",
					title : "경고",
					msg : "작번 템플릿을 선택하세요."
				}, function() {
					if (this.state == "close" || this.key == "ok") {
						mask.close();
						$pTemplate.focus();
					}
				})
				return false;
			}
		}

//		$primaryContent = primary.getUploadedList("object")[0];
//		if (!$primaryContent) {
//			dialogs.alert({
//				theme : "alert",
//				title : "경고",
//				msg : "첨부파일을 선택하세요."
//			}, function() {
//				if (this.key == "ok") {
//					$("#primary_layer_AX_selector").click();
//				}
//				if (this.state == "close") {
//					$("#primary_layer_AX_selector").click();
//				}
//			})
//			return false;
//		}

		$lineLen = $("input[name=appUserOid]").length; // 결재자
		var lineType = "series";
		if ($lineLen == 0) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "결재라인을 지정하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					var url = "/Windchill/plm/org/addLine?lineType=" + lineType;
					$(document).openURLViewOpt(url, 1100, 630, "no");
				}
			})
			return false;
		}

		if (popup != "true") {
			var value = jexcels.getValueFromCoords(0, 0);
			if (value == null) {
				dialogs.alert({
					theme : "alert",
					title : "경고",
					msg : "작번 내용을 붙여넣으세요."
				}, function() {
					if (this.state == "close" || this.key == "ok") {
						mask.close();
					}
				})
				return false;
			}

			$td = $(".readonly");
			var index;
			var bool = false;
			$.each($td, function(idx) {
				if ($td.eq(idx).text() == "NG") {
					index = idx;
					bool = true;
					return false;
				}
			})

			if (bool) {
				var kekNumber = jexcels.getValueFromCoords(5, index);
				dialogs.alert({
					theme : "alert",
					title : "경고",
					msg : index + "행의 작번 " + kekNumber + "가 중복됩니다."
				}, function() {
					if (this.state == "close" || this.key == "ok") {
						mask.close();
					}
				})
				return false;
			}
		}

		box.confirm({
			theme : "info",
			title : "확인",
			msg : "의뢰서를 등록 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 등록 진행
				// 일반 문서 값
				var params = $(document).getFormParams();
				// 결재선
				params = $(document).getAppLines(params);

				if (popup != "true") {
					params.jexcels = jexcels.getData(false);
				}

				params.poid = $("input[name=poid]").val();
				params.toid = $("input[name=toid]").val();

				$(document).ajaxCallServer(url, params, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						msg : data.msg
					}, function() {
						// 버튼 클릭 ok, esc
						if (this.key == "ok" || this.state == "close") {

							if (popup == "true") {
								self.close();
								opener.document.location.reload();
							}

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

	createPartList : function(obj) {
		var dialogs = $(document).setOpen();
		var url = this.createPartListActionUrl;
		var popup = $("input[name=popup]").val();
		var box = $(document).setNonOpen();
		var progress = $(obj).data("progress");
		var output = $(obj).data("output");

		$app = $("input[name=appUserInfo]");
		$final = $app.length;

		if ($final == 0) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				width : 450,
				msg : "수배표는 반드시 결재선을 지정해야 합니다."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					mask.close();
				}
			})
			return false;
		}

		if ($final > 0) {
			$var = $app.eq($final - 1).val().split("&")[2];
			// if ($var != "19940009") {
			if ($var != "19940009" && $var != "20050112" && $var != "skyun") {
				dialogs.alert({
					theme : "alert",
					title : "경고",
					width : 450,
					msg : "수배표의 최종결재자는 반드시 팀장님으로 지정 되어야 합니다."
				}, function() {
					if (this.key == "ok" || this.state == "close") {
						mask.close();
					}
				})
				return false;
			}
		}

		$name = $("input[name=name]");
		if ($name.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "수배표 제목을 입력하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$name.focus();
				}
			})
			return false;
		}

		$engType = $("select[name=engType]");
		if ($engType.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "설계구분을 선택하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$engType.focus();
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

		var prompt = $(document).prompt();
		if (progress == null) {
			progress = 0;
		}
		prompt.prompt({
			input : {
				pro : {
					label : "현재 태스크 진행률 : " + progress + "%",
					required : true
				// placeholder : "Input your name"
				},
			},
			theme : "info",
			title : "태스크 진행률 입력"
		}, function() {
			if (this.key == "ok" || this.key == "enter") {
				var taskProgress = this.input.pro;

				var params = $(document).getFormParams();
				// 결재선
				params = $(document).getAppLines(params);
				params.taskProgress = taskProgress;
				params = $(document).getReferenceParams(params, "input[name=projectOid]", "projectOids");

				params.jexcels = jexcels.getData(false);

				$(document).ajaxCallServer(url, params, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						msg : data.msg
					}, function() {
						// 버튼 클릭 ok, esc
						if (this.key == "ok" || this.state == "close") {
							console.log(data.reload);
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
			} else if (this.key == "cancel") {
				mask.close();
			}
		})
	},

	checkPartNumber : function() {
		var params = new Object();
		params.jexcels = jexcels.getData(false);
		// $(".readonly").css({
		// "background-color" : "#cbdced",
		// "color" : "red",
		// "font-weight" : "bold"
		// }).text("N G");
	},

	checkERP : function(instance, cell, x, y, value) {
		var params = new Object();
		$tr = $(".jexcel_row");
		var url = "/Windchill/plm/erp/getKEK_VDAItem";
		var url2 = "/Windchill/plm/erp/getKEK_LotNo";
		$(document).onLayer();

		for (var i = 0; i < $tr.length; i++) {

			var lotNo = jexcels.getValueFromCoords(1, i);
			if (lotNo == null) {
				$(document).offLayer();
				continue;
			} else {
				params.lotNo = lotNo;
				params.index = i;
				$(document).callServer(url2, params, function(data) {
					var LotUnitName = data.LotUnitName;
					if (LotUnitName == undefined) {
						LotUnitName = "";
					}
					var index = data.index;
					jexcels.setValueFromCoords(2, index, LotUnitName, true);
				}, true)
			}

			var yCode = jexcels.getValueFromCoords(3, i);
			if (yCode == null) {
				$(document).offLayer();
				continue;
			} else {

				var qty = jexcels.getValueFromCoords(8, i);
				params.yCode = yCode;
				params.index = i;
				params.qty = qty;
				$(document).callServer(url, params, function(data) {
					var ItemName = data.ItemName;
					if (ItemName == undefined) {
						ItemName = "";
					}
					var Spec = data.Spec;
					if (Spec == undefined) {
						Spec = "";
					}
					var MakerName = data.MakerName;
					if (MakerName == undefined) {
						MakerName = "";
					}
					var CustName = data.CustName;
					if (CustName == undefined) {
						CustName = "";
					}
					var UnitName = data.UnitName;
					if (UnitName == undefined) {
						UnitName = "";
					}
					var Price = data.Price;
					if (Price == undefined) {
						Price = "";
					}
					var CurrName = data.CurrName;
					if (CurrName == undefined) {
						CurrName = "";
					}
					var won = data.won;
					if (won == undefined) {
						won = "";
					}

					var ExRate = data.ExRate;
					if (ExRate == undefined) {
						ExRate = "";
					}

					var index = data.index;
					jexcels.setValueFromCoords(4, index, ItemName, true);
					jexcels.setValueFromCoords(5, index, Spec, true);
					jexcels.setValueFromCoords(6, index, MakerName, true);
					jexcels.setValueFromCoords(7, index, CustName, true);
					jexcels.setValueFromCoords(9, index, UnitName, true);
					jexcels.setValueFromCoords(10, index, Price, true);
					jexcels.setValueFromCoords(11, index, CurrName, true);
					jexcels.setValueFromCoords(12, index, won, true);
					jexcels.setValueFromCoords(14, index, ExRate, true);
				}, true)
			}
		}
	},

	changedCheckERP : function(instance, cell, x, y, value) {
		var params = new Object();
		var dialogs = $(document).setOpen();
//		$tr = $(".jexcel_row");
//		var fl = false;
//		for (var i = 0; i < $tr.length; i++) {
//			if(y==i)continue;
//			var yCode = jexcels.getValueFromCoords(3, y);
//			var yCodeList = jexcels.getValueFromCoords(3, i);
//			if(yCode == yCodeList)fl = true;
//		}
		
		if (x == 1) {
			var lotNo = jexcels.getValueFromCoords(1, y);
			params.lotNo = lotNo;
			params.index = Number(y);
			var url = "/Windchill/plm/erp/getKEK_LotNo";
			$(document).callServer(url, params, function(data) {
				var LotUnitName = data.LotUnitName;
				if (LotUnitName == undefined) {
					LotUnitName = "";
				}
				var index = data.index;
				jexcels.setValueFromCoords(2, index, LotUnitName, true);
			}, true)
		}

		if (x == 3) {
			var qty = jexcels.getValueFromCoords(8, y);
			var yCode = jexcels.getValueFromCoords(3, y);
			params.number = yCode;
			params.index = Number(y);
			params.qty = qty;
			var url = "/Windchill/plm/erp/checkYCode";
			$(document).callServer(url, params, function(data) {
				var index = data.index;
				
//				if (data.check == "false" || fl) {
//					jexcels.setValueFromCoords(0, index, "NG", true);
//				} else if (data.check == "true") {
//					jexcels.setValueFromCoords(0, index, "OK", true);
//				}
				
				if (data.check == "true") {
					jexcels.setValueFromCoords(0, index, "OK", true);
					console.log('잘됨');
				} else if (data.check == "false") {
					jexcels.setValueFromCoords(0, index, "NG", true);
				}
			}, true)
		}

		if (x == 3 || x == 8) {
			// $(document).onLayer();
			var yCode = jexcels.getValueFromCoords(3, y);
			var qty = jexcels.getValueFromCoords(8, y);
			params.yCode = yCode;
			params.index = Number(y);
			params.qty = qty;
			console.log(params);
			var url = "/Windchill/plm/erp/getKEK_VDAItem";
			$(document).callServer(url, params, function(data) {
				var ItemName = data.ItemName;
				if (ItemName == undefined) {
					ItemName = "";
				}
				var Spec = data.Spec;
				if (Spec == undefined) {
					Spec = "";
				}
				var MakerName = data.MakerName;
				if (MakerName == undefined) {
					MakerName = "";
				}
				var CustName = data.CustName;
				if (CustName == undefined) {
					CustName = "";
				}
				var UnitName = data.UnitName;
				if (UnitName == undefined) {
					UnitName = "";
				}
				var Price = data.Price;
				if (Price == undefined) {
					Price = "";
				}
				var CurrName = data.CurrName;
				if (CurrName == undefined) {
					CurrName = "";
				}
				var won = data.won;
				if (won == undefined) {
					won = "";
				}

				var ExRate = data.ExRate;
				if (ExRate == undefined) {
					ExRate = "";
				}

				if(ItemName == "") {
//					alert("C");
				}
				
				var index = data.index;
				jexcels.setValueFromCoords(4, index, ItemName, true);
				jexcels.setValueFromCoords(5, index, Spec, true);
				jexcels.setValueFromCoords(6, index, MakerName, true);
				jexcels.setValueFromCoords(7, index, CustName, true);
				jexcels.setValueFromCoords(9, index, UnitName, true);
				jexcels.setValueFromCoords(10, index, Price, true);
				jexcels.setValueFromCoords(11, index, CurrName, true);
				jexcels.setValueFromCoords(12, index, won, true);
				jexcels.setValueFromCoords(14, index, ExRate, true);
			}, false);
		}

		// if (x == 8) {
		// var price = jexcels.getValueFromCoords(10, y);
		// if (price != null) {
		// alert(parseInt(qty));
		// var minus = qty.substring(0, 1);
		// var nPrice = Number(price.replace(",", ""));
		// var sum = qty * nPrice;
		// jexcels.setValueFromCoords(12, y, sum, true);
		// }
		// }

		if (x == 10) {
			// var price = jexcels.getValueFromCoords(10, y);
			// if (qty != null && price != null) {
			// var nPrice = price.replace(",", "");
			// var sum = BigInt(qty * nPrice);
			// // nPrice = nPrice.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
			// // jexcels.setValueFromCoords(10, y, nPrice, true);
			// jexcels.setValueFromCoords(12, y, sum, true);
			// }
		}
	},

	checkColumns : function() {
		// alert("C");
	},

	viewY_code : function(obj) {
		$yCode = $(obj).data("number");
		var $url = "/Windchill/plm/partList/viewPartByYCode?popup=true&yCode=" + $yCode;
		// var $url = "/Windchill/plm/epm/viewEpm?popup=true&oid=wt.epm.EPMDocument:9284261";
		$(document).openURLViewOpt($url, 1200, 700, "");
	},

	checkKekNumber : function() {
		var params = new Object();
		$tr = $(".jexcel_row");
		$url = "/Windchill/plm/project/checkKekNumberAction";
		for (var i = 0; i < $tr.length; i++) {
			var value = jexcels.getValueFromCoords(5, i);
			var pType = jexcels.getValueFromCoords(1, i);
			params.kekNumber = value;
			params.index = i;
			params.pType = pType;
			$(document).ajaxCallServer($url, params, function(data) {
				var nResult = data.nResult;
				var index = data.index;
				jexcels.setValueFromCoords(0, index, nResult, true);
				$(".readonly").css({
					"background-color" : "#cbdced",
					"color" : "red",
					"font-weight" : "bold"
				});
			})
		}
	},

	changed : function(instance, cell, x, y, value) {
		var params = new Object();

		if (x == 5) {
			var pType = jexcels.getValueFromCoords(1, y);
			$url = "/Windchill/plm/project/checkKekNumberAction";
			params.kekNumber = value;
			params.index = Number(y);
			params.pType = pType;
			$(document).ajaxCallServer($url, params, function(data) {
				var nResult = data.nResult;
				var index = data.index;
				jexcels.setValueFromCoords(0, index, nResult, true);
				$(".readonly").css({
					"background-color" : "#cbdced",
					"color" : "red",
					"font-weight" : "bold"
				});
			})
		}
	},

	modifyReq : function(obj) {
		$oid = $(obj).data("oid");
		document.location.href = "/Windchill/plm/document/modifyRequestDocument?oid=" + $oid + "&popup=true";
	},

	modifyPartList : function() {
		mask.open();
		$("#loading_layer").show();
		$oid = $("input[name=oid]").val();
		$popup = $("input[name=popup]").val();
		document.location.href = this.modifyPartListUrl + "?oid=" + $oid + "&popup=" + $popup;
	},

	modifyRequestDocument : function(obj) {
		var dialogs = $(document).setOpen();
		var url = this.modifyRequestDocumentActionUrl;
		var box = $(document).setNonOpen();

		$name = $("input[name=name]");
		if ($name.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "의뢰서 제목을 입력하세요."
			}, function() {
				if (this.state == "close" || this.key == "ok") {
					mask.close();
					$name.focus();
				}
			})
			return false;
		}

		$pTemplate = $("select[name=pTemplate]");
		if ($pTemplate.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "작번 템플릿을 선택하세요."
			}, function() {
				if (this.state == "close" || this.key == "ok") {
					mask.close();
					$pTemplate.focus();
				}
			})
			return false;
		}

		$primaryContent = primary.getUploadedList("object")[0];
		if (!$primaryContent) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "첨부파일을 선택하세요."
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

		$lineLen = $("input[name=appUserOid]").length; // 결재자
		var lineType = "series";
		if ($lineLen == 0) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "결재라인을 지정하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					var url = "/Windchill/plm/org/addLine?lineType=" + lineType;
					$(document).openURLViewOpt(url, 1100, 630, "no");
				}
			})
			return false;
		}

		var value = jexcels.getValueFromCoords(0, 0);
		if (value == null) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "작번 내용을 붙여넣으세요."
			}, function() {
				if (this.state == "close" || this.key == "ok") {
					mask.close();
				}
			})
			return false;
		}

		box.confirm({
			theme : "info",
			title : "확인",
			msg : "의뢰서를 수정 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 등록 진행
				// 일반 문서 값
				var params = $(document).getFormParams();
				// 결재선
				params = $(document).getAppLines(params);

				params.self = $(obj).data("self");

				params.oid = $("input[name=oid]").val();

				params.jexcels = jexcels.getData(false);

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
								self.close();
								opener.document.location.href = data.url;
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

	installExcel : function(obj) {
		var dialogs = $(document).setOpen();
		var url = this.installExcelActionUrl;
		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "엑셀출력 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 등록 진행
				// 일반 문서 값
				var params = $(document).getFormParams();

				params.oid = $(obj).data("oid");

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
	},

	modifyPartListAction : function() {
		var dialogs = $(document).setOpen();
		var url = this.modifyPartListActionUrl;
		var box = $(document).setNonOpen();

		$name = $("input[name=name]");
		if ($name.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "수배표 제목을 입력하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$name.focus();
				}
			})
			return false;
		}

		$app = $("input[name=appUserInfo]");
		$final = $app.length;

		if ($final == 0) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				width : 450,
				msg : "수배표는 반드시 결재선을 지정해야 합니다."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					mask.close();
				}
			})
			return false;
		}

		if ($final > 0) {
			$var = $app.eq($final - 1).val().split("&")[2];
			// if ($var != "19940009") {
			if ($var != "19940009" && $var != "20050112" && $var != "skyun") {
//			if ($var != "19940009" && $var != "20050112") {
				dialogs.alert({
					theme : "alert",
					title : "경고",
					width : 450,
					msg : "수배표의 최종결재자는 반드시 팀장님으로 지정 되어야 합니다."
				}, function() {
					if (this.key == "ok" || this.state == "close") {
						mask.close();
					}
				})
				return false;
			}
		}

		$engType = $("select[name=engType]");
		if ($engType.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "설계구분을 선택하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$engType.focus();
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
		var params = $(document).getFormParams();

		params = $(document).getReferenceParams(params, "input[name=projectOid]", "projectOids");

		params = $(document).getAppLines(params);

		params.oid = $("input[name=oid]").val();

		console.log(jexcels.length);
		params.jexcels = jexcels.getData(false);

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
						opener.document.location.href = data.url;
					}
				}
			})
		}, true);
	},

	deletePartList : function() {
		var dialogs = $(document).setOpen();
		var url = this.deletePartListActionUrl;
		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "수배표를 삭제 하시겠습니까?\n삭제한 데이터는 복구가 불가능합니다."
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

	totalPartList : function(obj) {
		$pname = $(obj).data("pname");
		$oid = $(obj).data("oid");
		$engType = $(obj).data("eng");
		$url = "/Windchill/plm/partList/viewTotalPartList?oid=" + $oid + "&popup=true&engType=" + $engType + "&pname=" + $pname;
		$(document).openURLViewOpt($url, 1200, 700, "");
	},

	viewPartList : function(obj) {
		$oid = $(obj).data("oid");
		$url = "/Windchill/plm/partList/viewPartListMaster?oid=" + $oid + "&popup=true";
		$(document).openURLViewOpt($url, 1200, 700, "");
	},

	setColumnStyles : function() {
		$x2 = $("td[data-x=2]");
		$tr = $(".jexcel_row");
		for (var i = 0; i < $tr.length; i++) {
			$x2.eq(i + 1).css("background-color", "#e0f0fd");
		}
	},

	info : function(obj) {
		$oid = $(obj).data("oid");
		$url = "/Windchill/plm/partList/viewPartListMasterInfo?oid=" + $oid + "&popup=true";
		$(document).openURLViewOpt($url, 1200, 700, "");
	},
	
}

$(document).ready(function() {

	$(".y_code").click(function() {
		partlists.viewY_code(this);
	})

	$("#modifyRequestDocumentBtn").click(function() {
		partlists.modifyRequestDocument();
	})

	$("#createPartListBtn").click(function() {
		partlists.createPartList(this);
	})

	$("#createRequestDocumentBtn").click(function() {
		partlists.createRequestDocument(this);
	})

	$("#createSelfRequestDocumentBtn").click(function() {
		partlists.createRequestDocument(this);
	})

	$("#modifyReqBtn").click(function() {
		partlists.modifyReq(this);
	})

	$("#totalPartListBtn").click(function() {
		partlists.totalPartList(this);
	})

	$(".viewPartList").click(function() {
		partlists.viewPartList(this);
	})

	$("#totalElecPartListBtn").click(function() {
		partlists.totalPartList(this);
	})

	$("#totalMachincePartListBtn").click(function() {
		partlists.totalPartList(this);
	})

	$("#closePartList").click(function() {
		self.close();
	})

	$("#installExcelBtn").click(function() {
		partlists.installExcel(this);
	})

	$("#modifyPartListBtn").click(function() {
		partlists.modifyPartList();
	})

	$("#modifyPartListActionBtn").click(function() {
		partlists.modifyPartListAction();
	})

	$("#deletePartListBtn").click(function() {
		partlists.deletePartList();
	})

	$(".partListInfo").click(function() {
		partlists.info(this);
	})
})
