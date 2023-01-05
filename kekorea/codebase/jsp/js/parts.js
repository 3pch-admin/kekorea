/**
 * 부품 전용 javascript
 */

var parts = {

	root : "/Default",

	product_context : "product",
	library_context : "library",

	createUnitBomActionUrl : "/Windchill/plm/part/createUnitBomAction",

	createBundlePartActionUrl : "/Windchill/plm/part/createBundlePartAction",

	createProductSpecActionUrl : "/Windchill/plm/part/createProductSpecAction",

	createLibraryPartActionUrl : "/Windchill/plm/part/createLibraryPartAction",

	modifyLibraryPartActionUrl : "/Windchill/plm/part/modifyLibraryPartAction",

	createProductPartActionUrl : "/Windchill/plm/part/createProductPartAction",

	approvalLibraryPartActionUrl : "/Windchill/plm/part/approvalLibraryPartAction",

	createBomActionUrl : "/Windchill/plm/part/createBomAction",

	createAllPartsActionUrl : "/Windchill/plm/part/createAllPartsAction",

	createSaveAsActionUrl : "/Windchill/plm/common/saveAsObjectAction",

	listLibraryUrl : "/Windchill/plm/part/listLibraryPart",

	listProductUrl : "/Windchill/plm/part/listProductPart",

	createCodeActionUrl : "/Windchill/plm/part/createCodeAction",

	createUnitCodeActionUrl : "/Windchill/plm/part/createUnitCodeAction",

	approvalEpmActionUrl : "/Windchill/plm/part/approvalEplanAction",

	modifyPartUrl : "/Windchill/plm/part/modifyPart",

	modifyPartActionUrl : "/Windchill/plm/part/modifyPartAction",

	addCodePartsAction : function(state) {
		var dialogs = $(document).setOpen();
		$len = $("input[name=oid]").length;
		if ($len == 0) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "추가할 부품을 먼저 검색하세요."
			})
			return false;
		}

		var isSelect = $(document).isSelect();
		if (isSelect == false) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "추가할 부품을 선택하세요"
			})
			return false;
		}

		var url = "/Windchill/plm/part/addPartAction";
		var params = $(document).getListParams("list", "input[name=oid]");

		mask.open();
		$("#loading_layer").show();

		$(document).ajaxCallServer(url, params, function(data) {
			parts.addCodeParts(data.list, state);
		}, false);
	},

	addCodeParts : function(list, state) {
		$len = list.length;
		var html = "";
		var body = $(opener.document).find("#addPartsBody");
		$plen = $(opener.document).find("input[name=partOid]");
		$container = $(opener.document).find("#parts_container");

		for (var i = 0; i < $len; i++) {
			$bool = true;
			$.each($plen, function(idx) {
				if ($plen.eq(idx).val() == list[i][0]) {
					$bool = false;
				}
			})

			// 상태
			if (state != "" && list[i][3].split("$")[1] != state) {
				continue;
			}

			if (!$bool) {
				continue;
			}

			// 0 oid, 1 number, 2 name, 3 state, 4 version, 5 creator, 6
			// createdate 8 spec 9 maker 10 master_type
			// 11 context ...

			var context = list[i][11];
			$(opener.document).find("#nodataParts").remove();
			html += "<tr>";
			html += "<td><input type=\"checkbox\" name=\"partOid\" value=\"" + list[i][0] + "\"></td>";
			html += "<td class=\"infoParts left\" data-oid=\"" + list[i][0] + "\"><img class=\"pos3\" src=\"" + list[i][15] + "\">&nbsp;" + list[i][2] + "</td>";
			html += "<td class=\"infoParts left\" data-oid=\"" + list[i][0] + "\"><img class=\"pos3\" src=\"" + list[i][15] + "\">&nbsp;" + list[i][14] + "</td>";
			html += "<td class=\"infoParts left\" data-oid=\"" + list[i][0] + "\">" + list[i][17] + "</td>";
			// html += "<td><input type=\"text\" name=\"rev\" style=\"width: 250px;\" maxlength=\"3\"></td>";
			html += "<td>" + list[i][4] + "</td>"; // version
			html += "<td>" + list[i][16] + "</td>"; // 
			html += "<td>" + list[i][13] + "</td>"; //
			html += "<td>" + list[i][5] + "</td>"; //
			// if ("true" == context) {

			/*
			 * html += "<td>" + list[i][3].split("$")[0] + "</td>"; html += "<td>" + list[i][5] + "</td>"; html += "<td>" + list[i][6] + "</td>";
			 */
			// }
			html += "</tr>";
		}
		body.append(html);

		opener.setBoxs("input[name=partOid]");
		parts.setBoxs();

		$opener = $(opener.document).find("input[name=partOid]");
		if (!$container.hasClass("parts_container") && $opener.length >= 6) {
			$container.addClass("parts_container");
		}

		mask.close();
		$("#loading_layer").hide();
	},

	setBoxs3 : function() {
		$boxs = $(opener.document).find("input[name=partOid]");
		$.each($boxs, function(idx) {
			if (!$boxs.eq(idx).hasClass("isBox")) {
				$boxs.eq(idx).addClass("isBox");
			}
		})
	},

	codeParts : function(obj, value, state) {

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

		var url = "/Windchill/plm/part/addPartAction";
		var params = $(document).getDblFromData(value);
		mask.open();
		$("#loading_layer").show();

		$(document).ajaxCallServer(url, params, function(data) {
			parts.addCodeParts(data.list, state);
		}, false);
	},

	codeLibrary : function(obj, value, state) {
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

		var url = "/Windchill/plm/part/addPartAction";
		var params = $(document).getDblFromData(value);
		mask.open();
		$("#loading_layer").show();

		$(document).ajaxCallServer(url, params, function(data) {
			parts.addCodeLibrarys(data.list, state);
		}, false);
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

		var url = "/Windchill/plm/part/addPartAction";
		var params = $(document).getListParams("list", "input[name=oid]");

		mask.open();
		$("#loading_layer").show();
		$(document).ajaxCallServer(url, params, function(data) {
			parts.addCodeLibrarys(data.list, state);
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

			if (state != "" && list[i][3].split("$")[1] != state) {
				continue;
			}

			if (!$bool) {
				continue;
			}

			/*
			 * <th>DWG_NO</th> <th>NAME</th> <th>NAME_OF_PARTS</th> <th>버전</th> <th>상태</th> <th>작성자</th> <th>수정자</th>
			 */
			// 0 oid, 1 number, 2 name, 3 state, 4 version, 5 creator, 6
			// createdate
			$(opener.document).find("#nodataLibrarys").remove();
			html += "<tr>";
			html += "<td><input type=\"checkbox\" name=\"libraryOid\" value=\"" + list[i][0] + "\"></td>";
			html += "<td>" + list[i][2] + "</td>";
			// html += "<td><input type=\"text\" name=\"rev\" style=\"width: 250px;\" maxlength=\"3\"></td>";
			html += "<td>" + list[i][14] + "</td>";
			html += "<td>" + list[i][17] + "</td>";
			html += "<td>" + list[i][4] + "</td>";
			html += "<td>" + list[i][3].split("$")[0] + "</td>";
			html += "<td>" + list[i][13] + "</td>";
			html += "<td>" + list[i][5] + "</td>";
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

	addUnitBomsAction : function() {
		var dialogs = $(document).setOpen();
		$len = $("input[name=oid]").length;
		if ($len == 0) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "추가할 UNIT BOM 먼저 검색하세요."
			})
			return false;
		}

		var isSelect = $(document).isSelect();
		if (isSelect == false) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "추가할 UNIT BOM을 선택하세요"
			})
			return false;
		}

		var url = "/Windchill/plm/part/addUnitBomAction";
		var params = $(document).getListParams("list", "input[name=oid]");

		mask.open();
		$("#loading_layer").show();

		$(document).ajaxCallServer(url, params, function(data) {
			parts.addUnitBoms(data.list);
		}, false);
	},

	addUnitBoms : function(list, state) {
		$len = list.length;
		var html = "";
		var body = $(opener.document).find("#addUnitsBody");
		$plen = $(opener.document).find("input[name=unitBomOid]");
		$container = $(opener.document).find("#parts_container");

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
			// createdate 8 spec 9 maker 10 master_type
			// 11 context ...

			var context = list[i][11];
			$(opener.document).find("#nodataUnits").remove();
			html += "<tr>";
			html += "<td><input type=\"checkbox\" name=\"unitBomOid\" value=\"" + list[i][0] + "\"></td>";
			html += "<td data-oid=\"" + list[i][0] + "\">" + list[i][1] + "</td>";
			html += "<td data-oid=\"" + list[i][0] + "\">" + list[i][2] + "</td>";
			html += "<td>" + list[i][3] + "</td>"; //
			html += "<td>" + list[i][4] + "</td>"; //
			html += "<td>" + list[i][5] + "</td>"; //
			html += "<td>" + list[i][6] + "</td>"; //
			html += "<td>" + list[i][7] + "</td>"; //
			html += "<td>" + list[i][8] + "</td>"; //
			html += "</tr>";
		}
		body.append(html);

		opener.setBoxs("input[name=unitBomOid]");
		parts.setBoxs();

		$opener = $(opener.document).find("input[name=unitBomOid]");
		if (!$container.hasClass("parts_container") && $opener.length >= 6) {
			$container.addClass("parts_container");
		}

		mask.close();
		$("#loading_layer").hide();
	},

	addPartsAction : function(state) {
		var dialogs = $(document).setOpen();
		$len = $("input[name=oid]").length;
		if ($len == 0) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "추가할 부품을 먼저 검색하세요."
			})
			return false;
		}

		var isSelect = $(document).isSelect();
		if (isSelect == false) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "추가할 부품을 선택하세요"
			})
			return false;
		}

		var url = "/Windchill/plm/part/addPartAction";
		var params = $(document).getListParams("list", "input[name=oid]");

		mask.open();
		$("#loading_layer").show();

		$(document).ajaxCallServer(url, params, function(data) {
			parts.addParts(data.list, state);
		}, false);
	},

	addDblUnitBoms : function(obj, value) {

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

		var url = "/Windchill/plm/part/addUnitBomAction";
		var params = $(document).getDblFromData(value);
		mask.open();
		$("#loading_layer").show();

		$(document).ajaxCallServer(url, params, function(data) {
			parts.addUnitBoms(data.list);
		}, false);
	},

	addDblParts : function(obj, value, state) {

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

		var url = "/Windchill/plm/part/addPartAction";
		var params = $(document).getDblFromData(value);
		mask.open();
		$("#loading_layer").show();

		$(document).ajaxCallServer(url, params, function(data) {
			parts.addParts(data.list, state);
		}, false);
	},

	addParts : function(list, state) {
		$len = list.length;
		var html = "";
		var body = $(opener.document).find("#addPartsBody");
		$plen = $(opener.document).find("input[name=partOid]");
		$container = $(opener.document).find("#parts_container");

		for (var i = 0; i < $len; i++) {
			$bool = true;
			$.each($plen, function(idx) {
				if ($plen.eq(idx).val() == list[i][0]) {
					$bool = false;
				}
			})

			// 상태
			if (state != "" && list[i][3].split("$")[1] != state) {
				continue;
			}

			if (!$bool) {
				continue;
			}

			// 0 oid, 1 number, 2 name, 3 state, 4 version, 5 creator, 6
			// createdate 8 spec 9 maker 10 master_type
			// 11 context ...

			var context = list[i][11];
			$(opener.document).find("#nodataParts").remove();
			html += "<tr>";
			html += "<td><input type=\"checkbox\" name=\"partOid\" value=\"" + list[i][0] + "\"></td>";
			html += "<td class=\"infoParts left\" data-oid=\"" + list[i][0] + "\"><img class=\"pos3\" src=\"" + list[i][15] + "\">&nbsp;" + list[i][1] + "</td>";
			html += "<td class=\"infoParts\" data-oid=\"" + list[i][0] + "\">" + list[i][2] + "</td>";
			html += "<td class=\"infoParts left\" data-oid=\"" + list[i][0] + "\"><img class=\"pos3\" src=\"" + list[i][15] + "\">&nbsp;" + list[i][14] + "</td>";
			html += "<td>" + list[i][4] + "</td>"; // version
			html += "<td>" + list[i][16] + "</td>"; // 
			html += "<td>" + list[i][13] + "</td>"; //
			html += "<td>" + list[i][5] + "</td>"; //
			// if ("true" == context) {

			/*
			 * html += "<td>" + list[i][3].split("$")[0] + "</td>"; html += "<td>" + list[i][5] + "</td>"; html += "<td>" + list[i][6] + "</td>";
			 */
			// }
			html += "</tr>";
		}
		body.append(html);

		opener.setBoxs("input[name=partOid]");
		parts.setBoxs();

		$opener = $(opener.document).find("input[name=partOid]");
		if (!$container.hasClass("parts_container") && $opener.length >= 6) {
			$container.addClass("parts_container");
		}

		mask.close();
		$("#loading_layer").hide();
	},

	setBoxs : function() {
		$boxs = $(opener.document).find("input[name=partOid]");
		$.each($boxs, function(idx) {
			if (!$boxs.eq(idx).hasClass("isBox")) {
				$boxs.eq(idx).addClass("isBox");
			}
		})

		$boxs = $(opener.document).find("input[name=unitBomOid]");
		$.each($boxs, function(idx) {
			if (!$boxs.eq(idx).hasClass("isBox")) {
				$boxs.eq(idx).addClass("isBox");
			}
		})
	},

	delUnits : function() {
		var isSelect = $(document).isSelectParams("unitBomOid");
		var dialogs = $(document).setOpen();
		$oid = $("input[name=unitBomOid]");
		if ($oid.length == 0) {
			return false;
		}

		if (isSelect == false) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "삭제할 UNIT BOM을 선택하세요."
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

		if ($len < 6 && $("#parts_container").hasClass("parts_container")) {
			$("#parts_container").removeClass("parts_container");
		}

		if ($len == 0) {
			if ($("#parts_container").hasClass("parts_container")) {
				$("#parts_container").removeClass("parts_container");
			}

			var body = $("#addUnitsBody");
			var html = "";
			html += "<tr id=\"nodataUnits\">";
			html += "<td class=\"nodata\" colspan=\"9\">UNIT BOM을 추가하세요.</td>";
			html += "</tr>";
			body.append(html);
			$("#allUnits").prop("checked", false);
			$("#allUnits").next().removeClass("sed");

		}
	},

	delParts : function() {
		var isSelect = $(document).isSelectParams("partOid");
		var dialogs = $(document).setOpen();
		$oid = $("input[name=partOid]");
		if ($oid.length == 0) {
			return false;
		}

		if (isSelect == false) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "삭제할 부품을 선택하세요."
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

		if ($len < 6 && $("#parts_container").hasClass("parts_container")) {
			$("#parts_container").removeClass("parts_container");
		}

		if ($len == 0) {
			if ($("#parts_container").hasClass("parts_container")) {
				$("#parts_container").removeClass("parts_container");
			}

			var body = $("#addPartsBody");
			var html = "";
			html += "<tr id=\"nodataParts\">";
			html += "<td class=\"nodata\" colspan=\"8\">부품을 추가하세요.</td>";
			html += "</tr>";
			body.append(html);
			$("#allParts").prop("checked", false);
			$("#allParts").next().removeClass("sed");

		}
	},

	openAddUnitBomPopup : function(obj) {
		var dbl = $(obj).data("dbl");
		if (dbl == undefined) {
			dbl = "true";
		}

		var fun = $(obj).data("fun");
		if (fun == undefined) {
			fun = "addDblUnitBoms";
		}

		var url = "/Windchill/plm/part/addUnitBom?dbl=" + dbl + "&fun=" + fun;
		$(document).openURLViewOpt(url, 1400, 700, "");
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
			fun = "addDblParts";
		}

		var context = $(obj).data("context");
		if (context == undefined) {
			context = "product";
		}

		var changeable = $(obj).data("changeable");
		if (changeable == undefined) {
			changeable = true;
		}

		var url = "/Windchill/plm/part/addPart?context=" + context + "&dbl=" + dbl + "&fun=" + fun + "&state=" + state + "&changeable=" + changeable;
		$(document).openURLViewOpt(url, 1400, 700, "");
	},

	bindBox : function() {
		$bool = false;
		$oid = $("input[name=partOid");
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
			$("#allParts").prop("checked", true);
			$("#allParts").next().addClass("sed");
		}

		if ($bool) {
			$("#allParts").prop("checked", false);
			$("#allParts").next().removeClass("sed");
		}

		$bool = false;
		$oid = $("input[name=unitBomOid");
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
			$("#allUnits").prop("checked", true);
			$("#allUnits").next().addClass("sed");
		}

		if ($bool) {
			$("#allUnits").prop("checked", false);
			$("#allUnits").next().removeClass("sed");
		}
	},

	allParts : function(obj, e) {
		$oid = $("input[name=partOid]");

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

	allUnits : function(obj, e) {
		$oid = $("input[name=unitBomOid]");

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

	listViewPart : function() {
		if (!$("#list_view_part").hasClass("active_view")) {
			$("#list_view_part").addClass("active_view");
		}

		if ($("#img_view_part").hasClass("active_view")) {
			$("#img_view_part").removeClass("active_view");
		}

		$(".img_container").hide();
		$(".list_container").show();
	},

	imgViewPart : function() {
		if (!$("#img_view_part").hasClass("active_view")) {
			$("#img_view_part").addClass("active_view");
		}

		if ($("#list_view_part").hasClass("active_view")) {
			$("#list_view_part").removeClass("active_view");
		}

		$(".img_container").show();
		$(".list_container").hide();
	},

	createLibraryPartAction : function() {
		var dialogs = $(document).setOpen();
		var url = this.createLibraryPartActionUrl;
		$root = $("#locationStr").text();
		if ($root == this.root) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "구매품 저장위치를 선택하세요."
			}, function() {
				var url = "/Windchill/plm/common/openFolder?popup=true&root=" + parts.root + "&context=LIBRARY";
				if (this.key == "ok") {
					$(document).openURLViewOpt(url, 400, 400, "");
				}
				if (this.state == "close") {
					$(document).openURLViewOpt(url, 400, 400, "");
				}
			})
			return false;
		}

		$ItemClassName = $("#ItemClassName");
		if ($ItemClassName.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "자재 소분류를 선택하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					// $name.focus();
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
				// msg : "구매품 부품명을 입력하세요."
				msg : "PRODUCT_NAME을 입력하세요."
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
				// msg : "가공품 부품번호를 입력하세요."
				msg : "파일이름을 입력하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$number.focus();
				}
			})
			return false;
		}

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "구매품을 등록 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 등록 진행
				// 일반 문서 값
				var params = $(document).getFormParams();
				// 관련 부품
				params = $(document).getReferenceParams(params, "input[name=docOid]", "docOids");
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
							document.location.href = data.url;
						}
					})
				}, true);
			}

			if (this.key == "cancel" || this.state == "close") {
				mask.close();
			}
		})
	},

	openCreoView : function(obj) {
		$url = $(obj).data("url");
		if ($url == "") {
			return false;
		}
		createCDialogWindow($url, "ProductViewLite", "1200", "600", "0", "0");
	},

	createLibraryPartAppAction : function() {
		$obj = $("#addParts");
		var dialogs = $(document).setOpen();
		var url = this.approvalLibraryPartActionUrl;
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

		$len = $("input[name=partOid]").length;
		if ($len == 0) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "결재 등록할 구매품을 선택하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					parts.openAddPopup($obj);
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
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					var url = "/Windchill/plm/org/addLine?lineType=" + lineType;
					$(document).openURLViewOpt(url, 1200, 630, "");
				}
			})
			return false;
		}

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "구매품 결재를 등록 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 일반 폼 값
				var params = $(document).getFormParams();
				// 결재선
				params = $(document).getAppLines(params);
				// 관련
				params = $(document).getReferenceParams(params, "input[name=partOid]", "partOids");
				$(document).ajaxCallServer(url, params, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						msg : data.msg
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
	},

	createProductPartAction : function() {
		var dialogs = $(document).setOpen();
		var url = this.createProductPartActionUrl;
		$root = $("#locationStr").text();
		if ($root == this.root) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "가공품 저장위치를 선택하세요."
			}, function() {
				var url = "/Windchill/plm/common/openFolder?popup=true&root=" + parts.root + "&context=PRODUCT";
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
				msg : "가공품 부품명을 입력하세요."
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
				msg : "가공품 부품번호를 입력하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$number.focus();
				}
			})
			return false;
		}

		$primaryContent = primary.getUploadedList("object")[0];
		if (!$primaryContent) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "가공품 도면파일을 선택하세요."
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
			msg : "가공품을 등록 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 등록 진행
				// 일반 문서 값
				var params = $(document).getFormParams();
				// 관련 부품
				params = $(document).getReferenceParams(params, "input[name=docOid]", "docOids");
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
							document.location.href = data.url;
						}
					})
				}, true);
			}

			if (this.key == "cancel" || this.state == "close") {
				mask.close();
			}
		})
	},

	createAllPartsAction : function() {
		var url = this.createAllPartsActionUrl;
		var dialogs = $(document).setOpen();
		$primaryContent = primary.getUploadedList("object")[0];

		if (!$primaryContent) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "엑셀파일을 선택하세요."
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

		var ext = $primaryContent.ext;
		if (ext.toLowerCase() != "xlsx" && ext.toLowerCase() != "xls") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "엑셀파일을 선택하세요."
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
			msg : "부품 일괄 등록 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				var params = $(document).getFormParams();
				$(document).ajaxCallServer(url, params, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						msg : data.msg
					}, function() {
						// 버튼 클릭 ok, esc
						if (this.key == "ok" || this.state == "close") {
							document.location.href = data.url;
						}
					})
				}, true);
			}

			if (this.key == "cancel" || this.state == "close") {
				mask.close();
			}
		})
	},

	createBomAction : function() {
		var url = this.createBomActionUrl;
		var dialogs = $(document).setOpen();
		$primaryContent = primary.getUploadedList("object")[0];

		if (!$primaryContent) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "엑셀파일을 선택하세요."
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

		var ext = $primaryContent.ext;
		if (ext.toLowerCase() != "xlsx" && ext.toLowerCase() != "xls") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "엑셀파일을 선택하세요."
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
			msg : "BOM을 등록 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				var params = $(document).getFormParams();
				$(document).ajaxCallServer(url, params, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						msg : data.msg
					}, function() {
						// 버튼 클릭 ok, esc
						if (this.key == "ok" || this.state == "close") {
							document.location.href = data.url;
						}
					})
				}, true);
			}

			if (this.key == "cancel" || this.state == "close") {
				mask.close();
			}
		})
	},

	detailView : function() {
		$(document).setHTML();
		$(".detailPart").toggle();
		$("#partType").bindSelect();
	},

	toggles : function() {
		alert("C");
	},

	createSaveAsAction : function() {
		var url = this.createSaveAsActionUrl;
		var dialogs = $(document).setOpen();
		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "파생품을 생성 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 등록 진행
				// 일반 문서 값
				var params = $(document).getNonListParams();

				var nameArray = new Array();
				$names = $("input[name*=name]");
				$.each($names, function(idx) {
					var value = $names.eq(idx).val();
					nameArray.push(value);
				})

				params.nameArray = nameArray;

				var numberArray = new Array();
				$numbers = $("input[name*=name]");
				$.each($numbers, function(idx) {
					var value = $numbers.eq(idx).val();
					numberArray.push(value);
				})

				params.numberArray = numberArray;
				// 관련 부품
				$(document).ajaxCallServer(url, params, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						msg : data.msg
					}, function() {
						// 버튼 클릭 ok, esc
						if (this.key == "ok" || this.state == "close") {
							// opener.document.location.reload();
							opener.parent.reloadPage();
							self.close();
						}
					})
				}, true);
			}

			if (this.key == "cancel" || this.state == "close") {
				mask.close();
			}
		})
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

	infoEndPart : function(obj) {
		$oid = $(obj).data("oid");
		$context = $(obj).data("context");
		var url = "/Windchill/plm/part/infoEndPart?oid=" + $oid + "&context=" + $context + "&popup=true";
		$(document).openURLViewOpt(url, 1200, 200, "");
	},

	infoDownPart : function(obj) {
		$oid = $(obj).data("oid");
		$context = $(obj).data("context");
		var url = "/Windchill/plm/part/infoDownPart?oid=" + $oid + "&context=" + $context + "&popup=true";
		$(document).openURLViewOpt(url, 1200, 500, "");
	},
	infoUpPart : function(obj) {
		$oid = $(obj).data("oid");
		$context = $(obj).data("context");
		var url = "/Windchill/plm/part/infoUpPart?oid=" + $oid + "&context=" + $context + "&popup=true";
		$(document).openURLViewOpt(url, 1200, 500, "");
	},

	/*
	 * $(document).onLayer(); $oid = $("input[name=oid]").val(); document.location.href = "/Windchill/plm/part/modifyLibraryPart?oid=" + $oid;
	 */
	modifyPart : function() {
		mask.open();
		$("#loading_layer").show();
		$oid = $("input[name=oid]").val();
		$popup = $("input[name=popup]").val();
		document.location.href = this.modifyPartUrl + "?oid=" + $oid + "&popup=" + $popup;
	},

	erp_duplicate : function() {
		var url = "/Windchill/plm/erp/checkErpPartNumberAction";
		var dialogs = $(document).setOpen();

		var number = $("input[name=number]").val();

		if (number == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "품목번호를 입력하세요"
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					mask.close();
				}
			})
			return false;
		}

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "EPR 중복 체크를 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				var params = new Object();
				params.number = number;
				$(document).ajaxCallServer(url, params, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						msg : data.msg,
						width : 400
					}, function() {
						// 버튼 클릭 ok, esc
						if (this.key == "ok" || this.state == "close") {
							// document.location.href = data.url;
						}
					})
				}, true);
			}

			if (this.key == "cancel" || this.state == "close") {
				mask.close();
			}
		})
	},

	modifyLibraryPart : function() {
		$(document).onLayer();
		$oid = $("input[name=oid]").val();
		document.location.href = "/Windchill/plm/part/modifyLibraryPart?oid=" + $oid;
	},

	back : function() {
		var preUrl = document.referrer;
		if (preUrl) {
			$(document).onLayer();
			document.location.href = preUrl;
		}
	},

	modifyLibraryPartAction : function() {
		var dialogs = $(document).setOpen();
		var url = this.modifyLibraryPartActionUrl;
		$root = $("#locationStr").text();
		if ($root == this.root) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "구매품 저장위치를 선택하세요."
			}, function() {
				var url = "/Windchill/plm/common/openFolder?popup=true&root=" + parts.root + "&context=LIBRARY";
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
				// msg : "구매품 부품명을 입력하세요."
				msg : "PRODUCT_NAME을 입력하세요."
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
				// msg : "가공품 부품번호를 입력하세요."
				msg : "파일이름을 입력하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$number.focus();
				}
			})
			return false;
		}

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "구매품을 수정 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 등록 진행
				// 일반 문서 값
				var params = $(document).getFormParams();
				// 관련 부품
				params = $(document).getReferenceParams(params, "input[name=docOid]", "docOids");
				// 결재선
				params = $(document).getAppLines(params);

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
			}

			if (this.key == "cancel" || this.state == "close") {
				mask.close();
			}
		})
	},

	setColumns : function(jexcels) {
		$tr = $(".jexcel_row");
		$.each($tr, function(idx) {
			// console.log(idx);

			console.log($tr.eq(idx).prev("td").length);

			// $tr.eq(idx).data("x").css("background-color", "red");
		})
		// jexcels.setStyle("A0", "background-color", "red");
	},

	getData : function(instance, cell, x, y, value) {
		if (x == 4) {
			var params = new Object();
			var number = jexcels.getValueFromCoords(4, y);
			var check = jexcels.getValueFromCoords(0, y);
			var check1 = jexcels.getValueFromCoords(1, y);
			if (number != "" && number != undefined) {
				params.index = Number(y);
				params.number = number;
				var url = "/Windchill/plm/part/plmPartDataCheck";
				$(document).ajaxCallServer(url, params, function(data) {
					if (data.check == "true") {
						var index = data.index;
						jexcels.setValueFromCoords(0, index, "NG(DWG_NO)", true);
					} else if (data.check == "false") {
						var index = data.index;
						console.log('hid');
						jexcels.setValueFromCoords(0, index, "OK", true);
						if (check1 != "OK") {
							jexcels.setValueFromCoords(1, index, "", true);
						}
					}
				})
			}
		}

		if (x == 2) {
			var params = new Object();
			var number = jexcels.getValueFromCoords(2, y);
			var check = jexcels.getValueFromCoords(0, y);
			var check1 = jexcels.getValueFromCoords(1, y);
			if (number != "" && number != undefined) {
				params.index = Number(y);
				params.number = number;
				var url = "/Windchill/plm/part/plmPartCheckYcode";
				$(document).ajaxCallServer(url, params, function(data) {
					if (data.check == "true") {
						var index = data.index;
						jexcels.setValueFromCoords(1, index, "NG(YCODE)", true);
					} else if (data.check == "false") {
						var index = data.index;
						if (check != "OK") {
							jexcels.setValueFromCoords(0, index, "", true);
						}
						jexcels.setValueFromCoords(1, index, "OK", true);
					}
				})
			}
		}

		if (x == 4) {
			var params = new Object();
			var spec = jexcels.getValueFromCoords(4, y);
			var check = jexcels.getValueFromCoords(1, y);
			if (spec != "" && spec != undefined && check != "OK") {
				params.spec = spec;
				params.index = Number(y);
				var url = "/Windchill/plm/erp/getKEK_VDAItemBySpec";
				$(document).callServer(url, params, function(data) {

					if (data.YCODE == "true") {
						alert("리턴..");
					}

					console.log(data);

					var ItemName = data.ItemName;
					if (ItemName == undefined) {
						ItemName = "";
					}
					var ItemNo = data.ItemNo;
					if (ItemNo == undefined) {
						ItemNo = "";
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

					var index = data.index;
					jexcels.setValueFromCoords(3, index, ItemName, true);
					jexcels.setValueFromCoords(2, index, ItemNo, true);
					jexcels.setValueFromCoords(6, index, CustName, true);
					jexcels.setValueFromCoords(7, index, UnitName, true);
					jexcels.setValueFromCoords(5, index, MakerName, true);
					jexcels.setValueFromCoords(8, index, Price, true);
					jexcels.setValueFromCoords(9, index, CurrName, true);
				}, false);
			}
		}

		if (x == 2) {
			var params = new Object();
			var yCode = jexcels.getValueFromCoords(2, y);
			var check = jexcels.getValueFromCoords(0, y);
			if (yCode != "" && yCode != undefined && check != "OK") {
				params.yCode = yCode;
				params.index = Number(y);
				var url = "/Windchill/plm/erp/getKEK_VDAItem";
				$(document).callServer(url, params, function(data) {
					var ItemName = data.ItemName;
					if (ItemName == undefined) {
						ItemName = "";
					}
					var Spec = data.Spec;
					// sepc...
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

					var index = data.index;
					jexcels.setValueFromCoords(3, index, ItemName, true);
					jexcels.setValueFromCoords(4, index, Spec, true);
					jexcels.setValueFromCoords(6, index, CustName, true);
					jexcels.setValueFromCoords(7, index, UnitName, true);
					jexcels.setValueFromCoords(5, index, MakerName, true);
					jexcels.setValueFromCoords(8, index, Price, true);
					jexcels.setValueFromCoords(9, index, CurrName, true);
				}, false);
			}
		}
	},

	// checkCreatePartValidate : function() {
	// var params = new Object();
	// $tr = $(".jexcel_row");
	// $url = "/Windchill/plm/part/checkPartNumber";
	// for (var i = 0; i < $tr.length; i++) {
	// var value = jexcels.getValueFromCoords(2, i);
	// params.partNumber = value;
	// params.index = i;
	// $(document).ajaxCallServer($url, params, function(data) {
	// var nResult = data.nResult;
	// var index = data.index;
	// jexcels.setValueFromCoords(0, index, nResult, true);
	// $(".readonly").css({
	// "background-color" : "#cbdced",
	// "color" : "red",
	// "font-weight" : "bold"
	// });
	// })
	// }
	// },

	createBundlePart : function() {
		var dialogs = $(document).setOpen();
		var url = this.createBundlePartActionUrl;

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

		var value = jexcels.getValueFromCoords(0, 0);
		if (value == null) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "부품 내용을 입력하세요."
			}, function() {
				if (this.state == "close" || this.key == "ok") {
					mask.close();
				}
			})
			return false;
		}

		if (bool) {
			var number = jexcels.getValueFromCoords(3, index);
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : index + "행의 부품 " + number + "가 중복됩니다."
			}, function() {
				if (this.state == "close" || this.key == "ok") {
					mask.close();
				}
			})
			return false;
		}

		var len = $("input[name*=allContent]");

		$number = $("input[name=number]");
		if (len.length > jexcels.getData(false).length) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "첨부 파일의 개수가 더 많습니다.",
			}, function() {
				if (this.key == "ok" || this.state == "close") {
				}
			})
			return false;
		}

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "부품일괄등록 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 등록 진행
				// 일반 문서 값
				var params = $(document).getFormParams();
				// 결재선
				params = $(document).getAppLines(params);

				params.jexcels = jexcels.getData(false);

				$(document).ajaxCallServer(url, params, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						msg : data.msg
					}, function() {
						// 버튼 클릭 ok, esc
						if (this.key == "ok" || this.state == "close") {
							// if (data.reload) {
							// document.location.href = data.url;
							// }
							var list = data.list;
							for (var i = 0; i < list.length; i++) {
								jexcels.setValueFromCoords(2, i, list[i], false);
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

	createProductSpec : function() {
		var dialogs = $(document).setOpen();
		var url = this.createProductSpecActionUrl;

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
			var number = jexcels.getValueFromCoords(3, index);
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : index + "행의 부품 " + number + "가 중복됩니다."
			}, function() {
				if (this.state == "close" || this.key == "ok") {
					mask.close();
				}
			})
			return false;
		}

		var len = $("input[name=docOid]");

		if (len.length != jexcels.getData(false).length) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				// msg : "가공품 부품번호를 입력하세요."
				msg : "부품과 문서의 개수가 일치하지 않습니다."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
				}
			})
			return false;
		}

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "제품사양서를 등록 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 등록 진행
				// 일반 문서 값
				var params = $(document).getFormParams();
				params = $(document).getReferenceParams(params, "input[name=docOid]", "docOids");
				params.jexcels = jexcels.getData(false);

				$(document).ajaxCallServer(url, params, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						msg : data.msg
					}, function() {
						// 버튼 클릭 ok, esc
						if (this.key == "ok" || this.state == "close") {
							var list = data.list;
							for (var i = 0; i < list.length; i++) {
								jexcels.setValueFromCoords(2, i, list[i], false);
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

	createUnitBom : function() {
		var dialogs = $(document).setOpen();
		var url = this.createUnitBomActionUrl;

		var box = $(document).setNonOpen();
		$tr = $(".jexcel_row");
		var fl = false;
		for (var i = 0; i < $tr.length; i++) {
			var check = jexcels.getValueFromCoords(0, i);
			if(check=="NG")fl = true;
		}
		
		if(fl){
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "체크를 확인해주세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
//					$spec.focus();
				}
			})
			return false;
		}
		
		$check = $("input[name=check]");
		if ($check.val() == "false") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "중복확인이 필요합니다."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
//					$spec.focus();
				}
			})
			return false;
		}
		
		$partName = $("input[name=partName]");
		if ($partName.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "품명을 입력하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$partName.focus();
				}
			})
			return false;
		}
		
		$spec = $("input[name=spec]");
		if ($spec.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "규격을 입력하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$spec.focus();
				}
			})
			return false;
		}
		
		$unit = $("input[name=unit]");
		if ($unit.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "기준단위를 입력하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$unit.focus();
				}
			})
			return false;
		}
		
		$currency = $("input[name=currency]");
		if ($currency.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "통화를 입력하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$currency.focus();
				}
			})
			return false;
		}
		
//		$price = $("input[name=price]");
//		if ($price.val() == "") {
//			dialogs.alert({
//				theme : "alert",
//				title : "경고",
//				msg : "단가를 입력하세요."
//			}, function() {
//				if (this.key == "ok" || this.state == "close") {
//					$price.focus();
//				}
//			})
//			return false;
//		}
		
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "UNIT BOM 등록 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 등록 진행
				// 일반 문서 값
				var params = $(document).getFormParams();
				// 결재선
//				params.jexcels = jexcels2.getData(false);
				params.jexcels2 = jexcels.getData(false);

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
	
	checkUnitBom : function() {
		var dialogs = $(document).setOpen();
		var url = "/Windchill/plm/part/checkUnitBomAction";

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "UNIT BOM 등록 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 등록 진행
				// 일반 문서 값
				var params = $(document).getFormParams();
				// 결재선
//				params.jexcels = jexcels2.getData(false);
				params.jexcels2 = jexcels.getData(false);

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

	createCodeAction : function(obj) {
		var dialogs = $(document).setOpen();
		var url = this.createCodeActionUrl;

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

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "코드 생성 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 등록 진행
				// 일반 문서 값
				var params = $(document).getFormParams();

				// params.self = $(obj).data("self");

				// 관련 부품
				params = $(document).getReferenceParams(params, "input[name=partOid]", "partOids");

				params = $(document).getReferenceParams(params, "input[name=rev]", "rev");

				// 관련 도면
				params = $(document).getReferenceParams(params, "input[name=epmOid]", "epmOids");

				// 관련 라이브러리
				params = $(document).getReferenceParams(params, "input[name=libraryOid]", "libraryOids");

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

	createUnitCodeAction : function(obj) {
		var dialogs = $(document).setOpen();
		var url = this.createUnitCodeActionUrl;

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

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "코드 생성 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 등록 진행
				// 일반 문서 값
				var params = $(document).getFormParams();

				// params.self = $(obj).data("self");

				// 관련 부품
				params = $(document).getReferenceParams(params, "input[name=unitBomOid]", "unitBomOids");

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

	createEplanAppAction : function() {
		$obj = $("#addLibraryParts");
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

		$len = $("input[name=libraryOid]").length;
		if ($len == 0) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "결재 등록할 EPLAN을 선택하세요."
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
					$(document).openURLViewOpt(url, 1100, 630, "no");
				}
			})
			return false;
		}

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "EPLAN 결재를 등록 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 일반 폼 값
				var params = $(document).getFormParams();
				// 결재선
				params = $(document).getAppLines(params);
				// 관련
				params = $(document).getReferenceParams(params, "input[name=libraryOid]", "libraryOids");
				
				params = $(document).getReferenceParams(params, "input[name=docOid]", "docOids");

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

	modifyPartAction : function() {
		var dialogs = $(document).setOpen();
		// $root = $("#locationStr").text();
		var url = this.modifyPartActionUrl;
		/*
		 * if ($root == this.root) { dialogs.alert({ theme : "alert", title : "경고", msg : "문서 문류를 선택하세요." }, function() { var url = "/Windchill/plm/common/openFolder?popup=true&root=" + documents.root + "&context=PRODUCT"; if (this.key == "ok") { $(document).openURLViewOpt(url, 400, 400, ""); } if
		 * (this.state == "close") { $(document).openURLViewOpt(url, 400, 400, ""); } }) return false; }
		 */

		// 문서 제목
		/*
		 * $name = $("input[name=name]"); if ($name.val() == "") { dialogs.alert({ theme : "alert", title : "경고", msg : "문서 제목을 입력하세요." }, function() { if (this.key == "ok") { $name.focus(); } }) return false; }
		 */

		/*
		 * $primaryContent = primary.getUploadedList("object")[0]; if (!$primaryContent) { dialogs.alert({ theme : "alert", title : "경고", msg : "주 첨부파일을 선택하세요." }, function() { if (this.key == "ok") { $("#primary_layer_AX_selector").click(); } if (this.state == "close") {
		 * $("#primary_layer_AX_selector").click(); } }) return false; }
		 */

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "부품을 수정 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 등록 진행
				// 일반 문서 값
				var params = $(document).getFormParams();
				// 관련 부품
				// params = $(document).getReferenceParams(params, "input[name=partOid]", "partOids");
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
	
	erpCheck : function() {
		var dialogs = $(document).setOpen();
		var url = "/Windchill/plm/part/checkUnitBom";
		$check = $("input[name=check]");
		$spec = $("input[name=spec]");
		if ($spec.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "규격을 입력하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$spec.focus();
				}
			})
			return false;
		}
		
		var params = $(document).getFormParams();
		params = $(document).getAppLines(params);

		$(document).ajaxCallServer(url, params, function(data) {
			dialogs.alert({
				theme : "alert",
				title : "결과",
				msg : data.msg
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$check.val(data.check);
					mask.close();
				}
			})
		}, true);
	},
}

$(document).ready(function() {
	
	$("#erpCheck").click(function() {
		parts.erpCheck();
	});
	
	$("#checkUnitBomBtn").click(function() {
		parts.createUnitBom();
	})
	
	$("#createUnitBomBtn").click(function() {
		parts.createUnitBom();
	})

	$("#createBundlePartBtn").click(function() {
		parts.createBundlePart();
	})

	$("#createProductSpecBtn").click(function() {
		parts.createProductSpec();
	})

	$("#modifyLibraryPartBtnAction").click(function() {
		parts.modifyLibraryPartAction();
	})

	// jquery bind
	$("#popupPartBtn").click(function() {
		self.close();
	})

	// 부품 추가 페이지
	$("#addParts").click(function() {
		parts.openAddPopup(this);
	})

	$("#addLibraryParts").click(function() {
		parts.openAddPopup(this);
	})

	$(document).bind("click", "input[name=partOid]", function(e) {
		parts.bindBox();
	})

	$(document).bind("click", "input[name=unitBomOid]", function(e) {
		parts.bindBox();
	})

	$("#allParts").click(function(e) {
		parts.allParts(this, e);
	})

	$("#allUnits").click(function(e) {
		parts.allUnits(this, e);
	})

	$("#delParts").click(function() {
		parts.delParts();
	})

	$("#delUnits").click(function() {
		parts.delUnits();
	})

	$("#list_view_part").click(function() {
		parts.listViewPart();
	})

	$("#img_view_part").click(function() {
		parts.imgViewPart();
	})

	$("#createLibraryPartBtn").click(function() {
		parts.createLibraryPartAction();
	})

	$("#createLibraryPartAppBtn").click(function() {
		parts.createLibraryPartAppAction();
	})

	$("#createProductPartBtn").click(function() {
		parts.createProductPartAction();
	})

	// 부품 추가 헤더 픽스
	$(".documents_add_table").tableHeadFixer();

	$(".fix_table").tableHeadFixer();

	// $("#refEbom_table").tableHeadFixer();
	//
	// $("#refDoc_table").tableHeadFixer();

	$(".creoView").click(function() {
		// parts.openCreoView(this);
	})

	$(document).on("click", ".creoView", function(e) {
		parts.openCreoView(this);
	})

	$("#createBomBtn").click(function() {
		parts.createBomAction();
	})

	$("#detailPartBtn").click(function() {
		parts.detailView();
	})

	$("#createAllPartsBtn").click(function() {
		parts.createAllPartsAction();
	})

	// bom
	$("span.icon_span").click(function() {
		parts.toggles();
	})

	$("#createSaveAsBtn").click(function() {
		parts.createSaveAsAction();
	})

	$("#partType").bindSelect();

	$("#listLibraryPartBtn").click(function() {
		parts.listLibrary();
	})

	$("#listProductPartBtn").click(function() {
		parts.listProduct();
	})

	$("#infoUpPart").click(function() {
		parts.infoUpPart(this);
	})

	$("#infoDownPart").click(function() {
		parts.infoDownPart(this);
	})

	$("#infoEndPart").click(function() {
		parts.infoEndPart(this);
	})

	$("#erp_duplicate").click(function() {
		parts.erp_duplicate();
	})

	$("#createCodeBtn").click(function() {
		parts.createCodeAction();
	})

	$("#createUnitBtn").click(function() {
		parts.createUnitCodeAction();
	})

	$("#reviseLibraryPartBtn").click(function() {
		$(document).revise();
	})

	$("#modifyLibraryPartBtn").click(function() {
		parts.modifyLibraryPart();
	})

	$("#backPartBtn").click(function() {
		parts.back();
	})

	$("#modifyPartBtn").click(function() {
		parts.modifyPart();
	})

	$("#modifyPartActionBtn").click(function() {
		parts.modifyPartAction();
	})

	$("#createEplanAppBtn").click(function() {
		parts.createEplanAppAction(this);
	})

	$("#addUnits").click(function() {
		parts.openAddUnitBomPopup(this);
	})
})
