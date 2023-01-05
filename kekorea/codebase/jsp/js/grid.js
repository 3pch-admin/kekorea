var grid = {

	// icon path
	gridStart : function(headers, isBox, init, isMulti) {
		grid.initGrid.init(headers, isBox, init, isMulti);
	},

	getData : function(data, headers, isBox, isReload, url) {
		grid.getGridData.init(data, headers, isBox, isReload, url);
	},

	cellClickPopupValue : function(key, url) {
		$(document).on("mouseover", ".list_table td", function() {
			$column = $(this).data("column");
			if (key + "_column" == $column) {
				$(this).css("text-decoration", "underline");
				$(this).css("font-weight", "bold");
				$(this).css("color", "blue");
//				$(this).css("font-size", "16px");
				$oid = $(this).parent("tr").data("oid");
				var obj = $(document).getObjectName($oid);
				var text = $(this).text();
				$(this).css("cursor", "pointer").attr("title", text + " " + obj + "정보보기");
			}
		}).on("click", ".list_table td", function() {
			$column = $(this).data("column");
			if (key + "_column" == $column) {
				$value = $(this).data("value");
				var popupUrl = url + "?" + key + "=" + $value + "&popup=true";
				$(document).openURLViewOpt(popupUrl, 1400, 700, "");
			}
		}).on("mouseout", ".list_table td", function() {
			$(this).css("text-decoration", "none");
			$(this).css("font-size", "12px");
			$(this).css("color", "black");
		})
	},

	cellClickPopup : function(key, url) {
		$(document).on("mouseover", ".list_table td", function() {
			$column = $(this).data("column");
			if (key + "_column" == $column) {
				$(this).css("text-decoration", "underline");
//				$(this).css("font-size", "16px");
				$(this).css("color", "blue");
				$(this).css("font-weight", "bold");
				$oid = $(this).parent("tr").data("oid");
				var obj = $(document).getObjectName($oid);
				var text = $(this).text();
				$(this).css("cursor", "pointer").attr("title", text + " " + obj + "정보보기");
			}
		}).on("click", ".list_table td", function() {
			$column = $(this).data("column");
			if (key + "_column" == $column) {
				$oid = $(this).parent("tr").data("oid");
				var popupUrl = url + "?oid=" + $oid + "&popup=true";
				$(document).openURLViewOpt(popupUrl, 1200, 700, "");
			}
		}).on("mouseout", ".list_table td", function() {
			$(this).css("text-decoration", "none");
			$(this).css("font-size", "12px");
			$(this).css("color", "black");
		})
	},

	cellClickStandardPopup : function(key, url) {
		$(document).on("mouseover", ".list_table td", function() {
			$column = $(this).data("column");
			if (key + "_column" == $column) {
				$(this).css("text-decoration", "underline");
//				$(this).css("font-size", "16px");
				$(this).css("color", "blue");
				$(this).css("font-weight", "bold");
				$oid = $(this).parent("tr").data("oid");
				var obj = $(document).getObjectName($oid);
				var text = $(this).text();
				$(this).css("cursor", "pointer").attr("title", text + " " + obj + "정보보기");
			}
		}).on("click", ".list_table td", function() {
			$column = $(this).data("column");
			if (key + "_column" == $column) {
				$oid = $(this).parent("tr").data("oid");
				var popupUrl = url + "?oid=" + $oid + "&popup=true";
				$(document).openURLViewOpt(popupUrl, 1500, 800, "no");
			}
		}).on("mouseout", ".list_table td", function() {
			$(this).css("text-decoration", "none");
			$(this).css("font-size", "12px");
			$(this).css("color", "black");
		})
	},

	cellClickSmallPopup : function(key, url) {
		$(document).on("mouseover", ".list_table td", function() {
			$column = $(this).data("column");
			if (key + "_column" == $column) {
				$(this).css("text-decoration", "underline");
//				$(this).css("font-size", "16px");
				$(this).css("color", "blue");
				$(this).css("font-weight", "bold");
				$oid = $(this).parent("tr").data("oid");
				var obj = $(document).getObjectName($oid);
				var text = $(this).text();
				$(this).css("cursor", "pointer").attr("title", text + " " + obj + "정보보기");
			}
		}).on("click", ".list_table td", function() {
			$column = $(this).data("column");
			if (key + "_column" == $column) {
				$oid = $(this).parent("tr").data("oid");
				var popupUrl = url + "?oid=" + $oid + "&popup=true";
				$(document).openURLViewOpt(popupUrl, 2000, 700, "no");
			}
		}).on("mouseout", ".list_table td", function() {
			$(this).css("text-decoration", "none");
			$(this).css("font-size", "12px");
			$(this).css("color", "black");
		})
	},
	
	cellClickUnitBomPopup : function(key, url) {
		$(document).on("mouseover", ".list_table td", function() {
			$column = $(this).data("column");
			if (key + "_column" == $column) {
				$(this).css("text-decoration", "underline");
//				$(this).css("font-size", "16px");
				$(this).css("color", "blue");
				$(this).css("font-weight", "bold");
				$oid = $(this).parent("tr").data("oid");
				var obj = $(document).getObjectName($oid);
				var text = $(this).text();
				$(this).css("cursor", "pointer").attr("title", text + " " + obj + "정보보기");
			}
		}).on("click", ".list_table td", function() {
			$column = $(this).data("column");
			if (key + "_column" == $column) {
				$oid = $(this).parent("tr").data("oid");
				var popupUrl = url + "?oid=" + $oid + "&popup=true";
				$(document).openURLViewOpt(popupUrl, 1600, 700, "no");
			}
		}).on("mouseout", ".list_table td", function() {
			$(this).css("text-decoration", "none");
			$(this).css("font-size", "12px");
			$(this).css("color", "black");
		})
	},

	// 셀 클릭 이벤트
	cellClick : function(key, url) {
		$(document).on("mouseover", ".list_table td", function() {
			$column = $(this).data("column");
			if (key + "_column" == $column) {
				$(this).css("text-decoration", "underline");
//				$(this).css("font-size", "16px");
				$(this).css("color", "blue");
				$(this).css("font-weight", "bold");
				$oid = $(this).parent("tr").data("oid");
				var obj = $(document).getObjectName($oid);
				var text = $(this).text();
				$(this).css("cursor", "pointer").attr("title", text + " " + obj + "정보보기");
			}
		}).on("click", ".list_table td", function() {
			$column = $(this).data("column");
			if (key + "_column" == $column) {
				$oid = $(this).parent("tr").data("oid");
				var popupUrl = url + "?oid=" + $oid + "&popup=true";
				$(document).openURLViewOpt(popupUrl, 1200, 700, "no");
			}
		}).on("mouseout", ".list_table td", function() {
			$(this).css("text-decoration", "none");
			$(this).css("font-size", "12px");
			$(this).css("color", "black");
		})
	},

	// 리스트 호출 전
	preLoading : function() {
		$("#table_search").val("");
		mask.open();
		$("#loading_layer").show();
	},

	// 리스트 완료 후
	completeLoading : function(sessionid, curPage) {
		$list_tr = $(".list_tr");
		$("#count_text").html("(" + $list_tr.length + "/<font color=\"red\">0</font>)개 선택됨");

		$("#all").prop("checked", false);
		$("#all").next().removeClass("sed");

		$("input[name=sessionid]").val(sessionid);
		$("input[name=tpage]").val(curPage);
		mask.close();
		$("#loading_layer").hide();
	},

	listCheckbox : function(multi) {
		$(document).on("click", "div.helper-checks-checkbox-oid", function() {
			$tr = $(this).parent().parent();

			$bg = $tr.css("background-color");
			if ($bg != "rgb(183, 240, 177)" && $bg != "rgb(173, 197, 245)") {
				$tr.css("background-color", "#fbfed1");
			}

			if (multi == undefined) {
				multi = true;
			}

			if (multi == true) {

				$bool = false;
				$oid = $("input[name=oid");
				$cnt = 0;
				$click = 0;
				$.each($oid, function(idx) {
					if ($oid.eq(idx).prop("checked") == false) {
						$bool = true;
						return false;
					} else if ($oid.eq(idx).prop("checked") == true) {
						$cnt++;
					}
				})

				$.each($oid, function(idx) {
					if ($oid.eq(idx).prop("checked") == true) {
						$click++;
					}
				})

				$("#count_text").html("(" + $list_tr.length + "/<font color=\"red\">" + $click + "</font>)개 선택됨");

				if ($cnt == $oid.length) {
					$("#all").prop("checked", true);
					$("#all").next().addClass("sed");
				}

				if ($bool) {
					$("#all").prop("checked", false);
					$("#all").next().removeClass("sed");
				}
			} else {
				$("#count_text").html("(" + $list_tr.length + "/<font color=\"red\">1</font>)개 선택됨");
				$oid = $("input[name=oid]");
				$.each($oid, function(idx) {
					$oid.eq(idx).parent().parent().css("background-color", "white");
					$oid.eq(idx).prop("checked", false);
					$oid.eq(idx).next().removeClass("sed");
				})
				if (!$(this).hasClass("sed")) {
					$tr.css("background-color", "#fbfed1");
					$(this).prev().prop("checked", true);
					$(this).addClass("sed");
				}
			}
		})
	},

	allCheckbox : function() {

		$("#all").click(function(e) {

			$("div.rightmenu").hide();

			$("div.rightmenu_multi").hide();

			$oid = $("input[name=oid]");
			if ($oid.length == 0) {
				$(this).next().removeClass("sed");
				e.stopPropagation();
				e.preventDefault();
				return false;
			}

			if ($(this).prop("checked") == true) {
				$.each($oid, function(idx) {
					$oid.eq(idx).next().addClass("sed");
					$tr = $oid.eq(idx).parent().parent();

					$bg = $tr.css("background-color");
					if ($bg != "rgb(183, 240, 177)" && $bg != "rgb(173, 197, 245)") {
						$tr.css("background-color", "#fbfed1");
					}
					$oid.eq(idx).prop("checked", true);
					$("#count_text").html("(" + $list_tr.length + "/<font color=\"red\">" + $list_tr.length + "</font>)개 선택됨");
				})
			} else {
				$.each($oid, function(idx) {
					$oid.eq(idx).next().removeClass("sed");
					$tr = $oid.eq(idx).parent().parent();
					$bg = $tr.css("background-color");
					if ($bg == "rgb(251, 254, 209)") {
						$tr.css("background-color", "white");
					}
					$oid.eq(idx).prop("checked", false);
					$("#count_text").html("(" + $list_tr.length + "/<font color=\"red\">0</font>)개 선택됨");
				})
			}
		})
	},

	sortView : function() {

		$th = $("#header_tr>th");

		$.each($th, function(idx) {

			if ($th.eq(idx).hasClass("ascending") || $th.eq(idx).hasClass("is-date") || $th.eq(idx).hasClass("is-number")) {
				$th.eq(idx).find("i.up-sort-icon").show();
			}

			$th.eq(idx).mouseover(function() {

				if ($(this).hasClass("ascending")) {
					$(this).find("i.up-sort-icon").show();
				}

				if ($(this).hasClass("descending")) {
					$(this).find("i.down-sort-icon").show();
				}
			}).click(function() {
				if ($(this).hasClass("ascending")) {
					$(this).find("i.down-sort-icon").show();
					$(this).find("i.up-sort-icon").hide();
				}

				if ($(this).hasClass("descending")) {
					$(this).find("i.down-sort-icon").hide();
					$(this).find("i.up-sort-icon").show();
				}
			})
		})
	},

	// grid 초기화
	initGrid : {
		init : function(headers, isBox, init, isMulti) {

			if (isMulti == undefined) {
				isMulti = true;
			}

			if (init == undefined) {
				init = true;
			}

			$list_tr = $(".list_tr");
			$("#count_text").html("(" + $list_tr.length + "/<font color=\"red\">0</font>)개 선택됨");

			$header = $("#grid_header");
			$header.empty();
			// 순서 class, id, data attr
			var html = "<tr class=\"dnd-moved sorter-header\" id=\"header_tr\">\n";

			if (isBox == "true" && isMulti) {
				// type, name, id, class
				html += "<th class=\"header_check  no-sort\" id=\"checkbox\" data-header=\"checkbox\">" + "<input type=\"checkbox\" name=\"all\" id=\"all\"></th>\n";
			} else {
				// html += "<th class=\"header_check no-sort\">&nbsp;</th>\n";
			}

			// class, id, style, data attr
			// sorting...
			for (var i = 0; i < headers.length; i++) {
//				var sort = grid.getSort(headers[i].key);
				sort = "";
				var styleKey = grid.getStyleKey(headers[i].style);
				html += "<th class=\"header_th " + sort + "\" id=\"" + headers[i].key + "\" style=\"" + styleKey + "\" data-header=\"" + headers[i].key + "\">" + headers[i].display
						+ "<i class=\"axi axi-ion-arrow-up-c up-sort-icon\"></i><i class=\"axi axi-ion-arrow-down-c down-sort-icon\"></i></th>\n";
			}
			html += "</tr>\n";

			var colspan = headers.length
			if (isBox) {
				colspan = colspan + 1;
			}

			if (init) {
				$list = $("#grid_list");
				var msg = "";
				msg += "<tr>";
				msg += "<td class=\"nodata_icon\" colspan=\"" + colspan + "\">";
				msg += "<a class=\"axi axi-info-outline\"></a>";
				msg += "<span>";
				msg += "&nbsp;조회 버튼을 눌러서 검색을 하세요.";
				msg += "</span>";
				msg += "</td>";
				msg += "</tr>";
				$list.append(msg);
			}
			$header.append(html);

//			grid.sortView();
		}
	},

	// 그리드 데이터 들고 오기
	getGridData : {

		init : function(data, headers, isBox, isReload, url) {

			// 초기 세팅들..
			// grid.dragHeader(".list_table");

			// contextmenu
			grid.bindListContextmenu();

			grid.paginate(data.total, data.curPage);
			grid.bindPaging(headers, data.sessionid, url, isBox, isReload);

			$list = $("#grid_list");
			$list.empty();
			var html = "";

			var lists = data.list;

			var cnt = data.topListCount;
			for (var i = 0; i < lists.length; i++) {
				
				var bg = "";
				if (lists[i]["kekState"] == "중단됨" || lists[i]["kekState"] == "취소") {
					bg = "style=\"color: red; background-color: #B7F0B1;\"";
				} else if (lists[i]["kekState"] == "작업 완료") {
					bg = "style=\"background-color: #adc5f5;\"";
				}

				html += "<tr " + bg + " class=\"list_tr dnd-moved indexed\" data-oid=\"" + lists[i].oid + "\" data-key=\"rightmenu\">\n";
				if (isBox == "true") {
					html += "<td><input type=\"checkbox\" name=\"oid\" value=\"" + lists[i].oid + "\"></td>\n";
				} else {
					// html += "<td>&nbsp;</td>";
				}

				for (var k = 0; k < headers.length; k++) {
					var key = headers[k].key;
					var style = headers[k].style;
					console.log(key);
					// style += "width: " + style + "px;";
					// style = "";
					if (key == "kekState") {
						// alert(lists[i][key]);
					}

					// 순번
					if (key == "no") {
						html += "<td style=\"" + style + "\" data-column=\"" + key + "_column\">" + cnt-- + "</td>";
					} else if (key == "returnPoint") {
						var returnPoint = grid.returnPointRender(lists[i][key]);
						html += "<td style=\"text-align: right; padding-right: 10px;\" data-column=\"" + key + "_column\">" + returnPoint + "</td>";
					} else if (key == "ingPoint" || key == "returnPoint") {
						var ingPoint = grid.ingPointRender(lists[i][key]);
						html += "<td style=\"text-align: right; padding-right: 10px;\" data-column=\"" + key + "_column\">" + ingPoint + "</td>";
					} else if (key == "name" || key == "number" || key == "pdescription") {
						// var text = grid.subStrings(lists[i][key]);
						var text = lists[i][key];
						html += "<td class=\"left indent5\" style=\"" + style + "\" data-column=\"" + key + "_column\">";
						html += "<img src=\"" + lists[i]["iconPath"] + "\" class=\"pos3\">&nbsp;" + text;
						html += "</td>";
						// 썸네일
					} else if (key == "thumnail") {
						var thumnail = grid.thumnailRender(lists[i][key], lists[i]["creoView"]);
						html += "<td style=\"" + style + "\" data-column=\"" + key + "_column\">" + thumnail + "</td>";
					} else if (key == "filename") {
						var filename = grid.filenameRender(lists[i]["iconPath"], lists[i]["primary"], lists[i][key]);
						html += "<td class=\"left indent5\" style=\"" + style + "\" data-column=\"" + key + "_column\">" + filename + "</td>";
						// 첨부 파일
					} else if (key == "primary") {
						var primary = grid.primaryRender(lists[i][key]);
						html += "<td style=\"" + style + "\" data-column=\"" + key + "_column\">" + primary + "</td>";
					} else if (key == "state") {
						var text = lists[i][key];
						html += "<td class=\"" + clz + "\" style=\"" + style + "\" data-value=\"" + text + "\"  data-column=\"" + key + "_column\">" + text + "</td>";
					} else if (key == "kekNumber") {
						var text = lists[i][key];
						html += "<td title=\"" + lists[i][key] + "\" class=\"" + clz + "\" style=\"" + style + "\" data-value=\"" + text + "/" + lists[i]["pjtType"] + "\"  data-column=\"" + key + "_column\">" + text + "</td>";
					} else if (key == "info") {
						// var info = grid.infoRender(lists[i][key]);
						html += "<td class=\"center\" style=\"" + style + "\" data-column=\"" + key + "_column\">";
						html += "<img src=\"" + lists[i]["info"] + "\" class=\"pos3 partListInfo\">";
						html += "</td>";
					} else {
						if (lists[i][key] != undefined && lists[i][key] != "null" && lists[i][key] != "kekState") {
							var clz = "";

							if (lists[i][key].length > 25) {
								// clz = "left indent5 wid1000";
							}

							if (key == "description") {
								clz = "left indent5";
							}
							var text = grid.subStrings(lists[i][key]);
							// var text = lists[i][key];
							html += "<td title=\"" + lists[i][key] + "\" class=\"" + clz + "\" style=\"" + style + "\" data-value=\"" + text + "\"  data-column=\"" + key + "_column\">" + text + "</td>";
						} else {
							html += "<td>&nbsp;</td>";
						}
					}
				}
				html += "</tr>\n";
			}

			if (lists.length == 0) {
				var colspan = headers.length;
				html += "<tr>";
				html += "<td class=\"nodata_icon\" colspan=\"" + colspan + "\">";
				html += "<a class=\"axi axi-info-outline\"></a>";
				html += "<span>";
				html += "&nbsp;조회 결과가 없습니다.";
				html += "</span>";
				html += "</td>";
				html += "</tr>";

				var paginate = $("#grid_paginate");
				paginate.empty();
			}
			$list.append(html);
		}
	},

	setBoxs : function() {
		$("input[name=sub_data]").checks();
		$("input[name=all]").checks();
		$("input[name=sub_folder]").checks();
		$("input[name=hideBox]").checks();
		$("input[type=radio]").checks();
		$("input[name=excelBox]").checks();
		$("input[name=emergency]").checks();
		$("input[name=partTypes]").checks();
	},

	// contextmenu bind
	bindListContextmenu : function(e) {
		$list_tr = $(".list_tr");

		$(document).on("contextmenu", ".list_tr", function(e) {
			e.preventDefault();

			$("div#contextmenu").hide();

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
					$bg = $oid.eq(dd).parent().parent().css("background-color");
					if ($bg != "rgb(183, 240, 177)" && $bg != "rgb(173, 197, 245)") {
						$oid.eq(dd).parent().parent().css("background-color", "white");
					}
					// $oid.eq(dd).parent().parent().css("background-color", "white");
				})

				$(this).find("input[name=oid]").prop("checked", true);
				$bg = $(this).find("input[name=oid]").parent().parent().css("background-color");
				if ($bg != "rgb(183, 240, 177)" && $bg != "rgb(173, 197, 245)") {
					$(this).find("input[name=oid]").parent().parent().css("background-color", "#fbfed1");
				}
				$(this).find("input[name=oid]").next().addClass("sed");
				$("#count_text").html("(" + $list_tr.length + "/<font color=\"red\">1</font>)개 선택됨");

				$(document).setRightMenuData($(this).data("oid"));

				$("." + $(this).data("key")).show().css({
					"top" : e.pageY + "px",
					"left" : e.pageX + "px",
				})
			}
		})
	},

	getStyleKey : function(styles) {
		var keys = "";
		if (styles) {
			keys = "min-width: " + styles + "px !important;";
			keys += " width: " + styles + "px !important;";
			keys += " max-width: " + styles + "px !important;";
		}
		return keys;
	},

	// header contextmenu
	bindHeaderContextmenu : function() {
		$(document).on("contextmenu", "#header_tr", function(e) {
			e.preventDefault();

			$("div.rightmenu").hide();

			$("#contextmenu").show().css({
				"top" : e.pageY + "px",
				"left" : e.pageX + "px",
			})
		})
	},

	getPagingParams : function(params) {
		if (params == null) {
			params = new Object();
		}

		$tpage = $("input[name=tpage").val();
		params.tpage = $tpage;

		$sessionid = $("input[name=sessionid").val();
		params.sessionid = $sessionid;
		return params;
	},

	getParams : function(params) {
		if (params == null) {
			params = new Object();
		}

		$paging = $("#paging_count option");
		$.each($paging, function(idx) {
			if ($paging.eq(idx).prop("selected") == true) {
				var value = $paging.eq(idx).val();
				params["psize"] = value;
			}
		})

		$sub_folder = $("#sub_folder");
		if ($sub_folder.prop("checked") == true) {
			var key = $sub_folder.attr("name");
			var value = $sub_folder.val();
			params[key] = value;
		}

		$sub_data = $("#sub_data");
		if ($sub_data.prop("checked") == true) {
			var key = $sub_data.attr("name");
			var value = $sub_data.val();
			params[key] = value;
		}

		$input = $(".search_table input[type=text]");
		$.each($input, function(idx) {
			var key = $input.eq(idx).attr("name");
			var value = $input.eq(idx).val();
			params[key] = value;
		})

		$hidden = $(".search_table input[type=hidden]");
		$.each($hidden, function(idx) {
			var key = $hidden.eq(idx).attr("name");
			var value = $hidden.eq(idx).val();
			params[key] = value;
		})

		// $hidden = $(". input[type=hidden]");
		// $.each($hidden, function(idx) {
		// var key = $hidden.eq(idx).attr("name");
		// var value = $hidden.eq(idx).val();
		// params[key] = value;
		// })

		$radio = $(".search_table input[type=radio]");
		$.each($radio, function(idx) {
			if ($radio.eq(idx).prop("checked") == true) {
				var key = $radio.eq(idx).attr("name");
				var value = $radio.eq(idx).val();
				params[key] = value;
			}
		})

		$checkbox = $(".search_table input[type=checkbox]");
		$.each($checkbox, function(idx) {
			if ($checkbox.eq(idx).prop("checked") == true) {
				var key = $checkbox.eq(idx).attr("name");
				var value = $checkbox.eq(idx).val();
				params[key] = value;
			}
		})

		$select = $(".search_table select");
		$.each($select, function(idx) {
			$option = $select.eq(idx).find("option");

			$.each($option, function(dd) {
				if ($option.eq(dd).prop("selected") == true) {
					var key = $select.eq(idx).attr("name");
					var value = $option.eq(dd).val();
					params[key] = value;
				}
			})
		})
		return params;
	},

	dragHeader : function(target) {
		$(target).dragableColumns();
	},

	paginate : function(total, curPage) {

		var psize = $("#paging_count").val();
		var pageCount = 10;

		var paginate = $("#grid_paginate");
		paginate.empty();

		var ksize = Math.ceil(total / psize);
		var temp = Math.ceil(curPage / pageCount);

		var start = (temp - 1) * pageCount + 1;
		var end = start + pageCount - 1;
		if (end > ksize) {
			end = ksize;
		}

		var html = "";
		html += "<colgroup>";
		html += "<col width=\"130\">";
		html += "<col width=\"*\">";
		html += "<col width=\"130\">";
		html += "</colgroup>";

		html += "<tr>";
		html += "<td>&nbsp;</td>";
		// html += "<td>[전체페이지 <span class=\"paginate\">:</span> " + ksize +
		// "][전체개수 <span class=\"paginate\">:</span> " + total + "]</td>"

		html += "<td>";

		if (curPage > 1) {
			html += "<a data-page=\"1\" title=\"처음 페이지로\" class=\"paging_left\">&lt;&lt;</a>\n";
			html += "<a data-page=\"" + (curPage - 1) + "\" title=\"이전 페이지로\" class=\"paging_left\">&lt;</a>\n";
		}

		for (var k = start; k <= end; k++) {
			if (k == curPage) {
				html += "<a title=\"" + k + "페이지로\" class=\"active_page\" data-page=\"" + k + "\">" + k + "</a>&nbsp;";
			} else {
				html += "<a title=\"" + k + "페이지로\" data-page=\"" + k + "\">" + k + "</a>&nbsp;";
			}
		}

		if (curPage < ksize) {
			html += "<a data-page=\"" + (curPage + 1) + "\" title=\"다음 페이지로\" class=\"paging_right\">&gt;</a>\n";
			html += "<a data-page=\"" + ksize + "\" title=\"마지막 페이지로\" class=\"paging_right\">&gt;&gt;</a>\n"
		}

		html += "</td>";
		html += "<td>[전체페이지 <span class=\"paginate\">:</span> " + ksize + "][전체개수 <span class=\"paginate\">:</span> " + total + "]</td>"
		// html += "<td>&nbsp;</td>";

		html += "</tr>";
		paginate.html(html);
	},

	bindPaging : function(headers, sessionid, url, isBox, isReload) {

		$pager = $("#grid_paginate a");
		$pager.click(function() {
			$page = $(this).data("page");
			$("input[name=tpage").val($page);
			if (sessionid != 0) {
				$("input[name=sessionid").val(sessionid);
				grid.reloadGrid(headers, url, isBox);
			}
		})

		// $(document).on("click", ".page_table a", function(e) {
		// $page = $(this).data("page");
		// $("input[name=tpage").val($page);
		// if (sessionid != 0) {
		// $("input[name=sessionid").val(sessionid);
		// grid.reloadGrid(headers, url, isBox);
		// }
		// })
	},

	reloadGrid : function(headers, url, isBox) {
		var params = grid.getParams();
		params = grid.getPagingParams(params);
		grid.preLoading();
		$(document).ajaxCallServer(url, params, function(data) {
			grid.getData(data, headers, isBox, true, url);
			grid.completeLoading(data.sessionid, data.curPage);
			$("input[name=oid]").checks();
		}, false);
	},

	contentRender : function(value) {
		var is2D = value["is2D"];
		var content = "";
		if (is2D) {
			var dwg = value["dwg"];
			if (dwg[0] != "" && dwg[0] != undefined) {
				content += "<a href=\"" + dwg[5] + "\"><img class=\"pos_b2\" src=\"/Windchill/jsp/images/fileicon/file_dwg.gif\"></a>&nbsp;";
			}

			var pdf = value["pdf"];
			if (pdf[0] != "" && pdf[0] != undefined) {
				content += "<a href=\"" + pdf[5] + "\"><img class=\"pos_b2\" src=\"/Windchill/jsp/images/fileicon/file_pdf.gif\"></a>";
			}
		}
		return content;
	},

	creoViewRender : function(value) {
		var creoView = "";
		if (value != "" && value != undefined) {
			creoView = "<img data-url=\"" + value + "\" title=\"CreoView 열기\" class=\"creoView pos_b2\" src=\"/Windchill/jsp/images/creo_view.png\">";
		}
		return creoView;
	},

	imgRender : function(value, creoView) {
		var img = "";
		if (value[0] != undefined) {
			img = "<a data-magnify=\"gallery\" data-caption=\"\" href=\"" + value[0] + "\"><img src=\"" + value[0] + "\"></a>";
		} else {
			img = "<img data-creo=\"" + creoView + "\" data-url=\"/Windchill/plm/common/doPublisher?oid=" + value[3] + "\" title=\"뷰어 파일 생성\" class=\"doPublisher\" src=\"/Windchill/wt/clients/images/wvs/productview_publish_288.png\">";
		}
		return img;
	},

	ingPointRender : function(value) {
		var ingPoint = "&nbsp;";
		if (value != "") {
			var s = value.substring(0, value.length - 1);
			var t = s.split(",");

			for (var i = 0; i < t.length; i++) {
				var text = t[i].split("&")[0];
				var bool = t[i].split("&")[1];

				if (bool == "true") {
					ingPoint += "<img style=\"height: 23px; width: 11px; position: relative; left: 0px;\" class=\"ingLeft\" src=\"/Windchill/jsp/images/process-sleft.gif\">";
				} else {
					ingPoint += "<img style=\"height: 23px; width: 11px; position: relative; left: 0px;\" class=\"ingLeft\" src=\"/Windchill/jsp/images/process-nleft.gif\">";
				}

				if (bool == "true") {
					ingPoint += "<span class=\"ingTextIng\">" + text + "</span>";
				} else {
					ingPoint += "<span class=\"ingText\">" + text + "</span>";
				}

				if (bool == "true") {
					ingPoint += "<img style=\"height: 23px; width: 11px; position: relative; left: 0px;\" class=\"ingRight\" src=\"/Windchill/jsp/images/process-sright.gif\">";
				} else {
					ingPoint += "<img style=\"height: 23px; width: 11px; position: relative; left: 0px;\" class=\"ingRight\" src=\"/Windchill/jsp/images/process-nright.gif\">";
				}

				if (i != t.length - 1) {
					ingPoint += "<img style=\"height: 23px; width: 11px; position: relative; left: 0px;\" class=\"ingRight\" src=\"/Windchill/jsp/images/process-line.gif\">";
				}
			}
		}
		return ingPoint;
	},

	returnPointRender : function(value) {
		var returnPoint = "";
		if (value != "") {
			var s = value.substring(0, value.length - 1);
			var t = s.split(",");
			for (var i = 0; i < t.length; i++) {
				var text = t[i].split("&")[0];
				var bool = t[i].split("&")[1];
				if (bool == "true") {
					returnPoint += "<img style=\"height: 23px; width: 11px; position: relative; left: 0px;\" class=\"ingLeft\" src=\"/Windchill/jsp/images/process-sleft.gif\">";
				} else {
					returnPoint += "<img style=\"height: 23px; width: 11px; position: relative; left: 0px;\" class=\"ingLeft\" src=\"/Windchill/jsp/images/process-nleft.gif\">";
				}
				if (bool == "true") {
					returnPoint += "<span class=\"ingTextIng\">" + text + "</span>";
				} else {
					returnPoint += "<span class=\"ingText\">" + text + "</span>";
				}

				if (bool == "true") {
					returnPoint += "<img style=\"height: 23px; width: 11px; position: relative; left: 0px;\" class=\"ingRight\" src=\"/Windchill/jsp/images/process-sright.gif\">";
				} else {
					returnPoint += "<img style=\"height: 23px; width: 11px; position: relative; left: 0px;\" class=\"ingRight\" src=\"/Windchill/jsp/images/process-nright.gif\">";
				}

				if (i != t.length - 1) {
					returnPoint += "<img style=\"height: 23px; width: 11px; position: relative; left: 0px;\" class=\"ingRight\" src=\"/Windchill/jsp/images/process-line.gif\">";
				}
				// if (bool == "true") {
				// returnPoint += "<img class=\"ingLeft\" src=\"/Windchill/jsp/images/signstep_on.gif\">";
				// } else {
				// // returnPoint += "<img class=\"ingLeft\" src=\"/Windchill/jsp/images/signstep_left.gif\">";
				// returnPoint += "<img class=\"ingLeft\" src=\"/Windchill/jsp/images/process-sleft.gif\">";
				// }
				// returnPoint += "<span class=\"ingText\">" + text + "</span>";
				// // returnPoint += "<img class=\"ingRight\" src=\"/Windchill/jsp/images/signstep_right.gif\">";
				// returnPoint += "<img class=\"ingRight\" src=\"/Windchill/jsp/images/process-sright.gif\">";
			}
		}
		return returnPoint;
	},

	filenameRender : function(icon, value, text) {
		var filename = "";
		if (value[0] != undefined) {
			filename = "<a href=\"" + value[5] + "\"><img src=\"" + icon + "\" class=\"pos2\">&nbsp;" + text + "</a>";
		}
		return filename;
	},

	thumnailRender : function(value, creoView) {
		var thumnail = "";
		if (value[0] != undefined) {
			// thumnail = "<a data-magnify=\"gallery\" data-caption=\"\" href=\"" + value[0] + "\"><img class=\"pos2\" src=\"" + value[1] + "\"></a>";
			thumnail = "<img data-creo=\"" + creoView + "\" data-url=\"" + creoView + "\" title=\"CreoView 열기\" class=\"creoView\" src=\"" + value[1] + "\">";
		} else {
			thumnail = "<img data-creo=\"" + creoView + "\" data-url=\"/Windchill/plm/common/doPublisher?oid=" + value[3] + "\" title=\"뷰어 파일 생성\" class=\"doPublisher\" src=\"/Windchill/jsp/images/productview_publish_24.png\">";
		}
		return thumnail;
	},

	// 첨부 파일 랜더링
	primaryRender : function(value) {
		var primary = "";
		if (value != undefined && value[0] != null) {
			primary = "<a href=\"" + value[5] + "\"><img class=\"pos2\" src=\"" + value[4] + "\"></a>";
		}
		return primary;
	},

	infoRender : function(value) {
		var info = "";
		if (value != undefined && value[0] != null) {
			info = "<img style=\"\" src=\"" + value[4] + "\">";
		}
		return info;
	},

	enableBoxs : function() {
		// $("div.rightmenu").hide();
		// $("div.rightmenu_multi").hide();
		// $("div#contextmenu").hide();

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

		if (!$checkMulti) {
			// $.each($oid, function(idx) {
			// $oid.eq(idx).next().removeClass("sed");
			// $tr = $oid.eq(idx).parent().parent();
			// $tr.css("background-color", "white");
			// $oid.eq(idx).prop("checked", false);
			//
			// $("#all").prop("checked", false);
			// $("#all").next().removeClass("sed");
			// })
		}
	},

	initGridPage : function(obj) {

		$text = $(".search_table input[type=text]");
		$.each($text, function(idx) {
			$text.eq(idx).val("");
		})

		$select = $(".search_table select");
		$.each($select, function(idx) {
			$select.eq(idx).val("");
		})

		$hidden = $(".search_table input[type=hidden]");
		$.each($hidden, function(idx) {
			$hidden.eq(idx).val("");
		})

		$radio = $(".search_table input[type=radio]");
		$radio.eq(0).prop("checked", true);
		$.each($radio, function(idx) {
			if (idx == 0) {
				$radio.eq(idx).next().addClass("sed");
			} else {
				$radio.eq(idx).next().removeClass("sed");
			}
		})

		$list_tr = $(".list_tr");
		$("#count_text").html("(" + $list_tr.length + "/<font color=\"red\">0</font>)개 선택됨");

		$("input[name=sub_folder]").prop("checked", false).next().removeClass("sed");

		$("#statesEBOM").bindSelectSetValue("");
		$("#statesEpm").bindSelectSetValue("");
		$("#statesPart").bindSelectSetValue("");
		$("#statesDoc").bindSelectSetValue("");
		$("#epmTypes").bindSelectSetValue("");
		$("#partTypes").bindSelectSetValue("");
		$("#objType").bindSelectSetValue("");
		
		$("#kekState").bindSelectSetValue("");
		$("#pType").bindSelectSetValue("");
		$("#customer").bindSelectSetValue("");
		$("#ins_location").bindSelectSetValue("");

		$location = $(obj).data("location");
		if ($location != undefined) {
			$("input[name=location]").val($location);
			$("#location").text($location);
		}

		// $th = $("#grid_header>tr>th");
		// $list = $("#grid_list");
		// $list.empty();
		// var msg = "";
		// msg += "<tr>";
		// msg += "<td class=\"nodata_icon\" colspan=\"" + $th.length + "\">";
		// msg += "<a class=\"axi axi-info-outline\"></a>";
		// msg += "<span>";
		// msg += "&nbsp;조회 버튼을 눌러서 검색을 하세요.";
		// msg += "</span>";
		// msg += "</td>";
		// msg += "</tr>";
		// $list.append(msg);
		//
		// $list = $("#grid_img");
		// $list.empty();
		// var msg = "";
		// msg += "<tr>";
		// msg += "<td class=\"nodata_icon\">";
		// msg += "<a class=\"axi axi-info-outline\"></a>";
		// msg += "<span>";
		// msg += "&nbsp;조회 버튼을 눌러서 검색을 하세요.";
		// msg += "</span>";
		// msg += "</td>";
		// msg += "</tr>";
		// $list.append(msg);
		//
		// var paginate = $("#grid_paginate");
		// paginate.empty();
	},

	getSort : function(key) {
		var sort = "no-sort";

		if (key == "kek_number" || key == "name" || key == "mak" || key == "ke_number" || key == "modifier" || key == "submiter" || key == "id" || key == "modelName") {
			sort = "ascending";
		} else if (key == "createDate" || key == "pDate") {
			sort = "is-date"
		} else if (key == "number") {
			sort = "is-number";
		}
		return sort;
	},

	subStrings : function(data) {
		var rtn = data;
		if (data != undefined && data.length > 50) {
			rtn = data.substring(0, 40) + "....";
		}
		return rtn;
	},
}

$(document).ready(function() {

	$("#initGrid").click(function() {
		grid.initGridPage(this);
	})

	$(".list_container").scroll(function(e) {
		grid.enableBoxs();
	}).bind("contextmenu", function(e) {
		e.preventDefault();
	}).mousewheel(function(e, delta) {

	})

}).scroll(function(e) {
	grid.enableBoxs();
})

function reloadPage() {
	$(document).getColumn();
}