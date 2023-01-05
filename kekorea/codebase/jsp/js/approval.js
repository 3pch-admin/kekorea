$(document).ready(
		function() {
			// $("#addLine_series").windowpopup({
			// href : "/Windchill/plm/org/addLine?lineType=series",
			// width : 1100,
			// height : 630,
			// scrollbars : "yes",
			// resizable : "yes"
			// })
			//
			// $("#addLine_parallel").windowpopup({
			// href : "/Windchill/plm/org/addLine?lineType=parallel",
			// width : 1100,
			// height : 630,
			// scrollbars : "yes",
			// resizable : "yes"
			// })

			$("#addOrgLineBtn").click(function() {
				$appType = $("input[name=appType]");
				var type;
				$.each($appType, function(idx) {
					if ($appType.eq(idx).prop("checked") == true) {
						type = $appType.eq(idx).val();
						return false;
					}
				})

				$options = $("#userDeptList option");
				var items = "";
				$.each($options, function(idx) {
					if ($options.eq(idx).prop("selected") == true) {
						var value = $options.eq(idx).val();
						items += value + ",";
						// return false;
					}
				})

				var dialogs = $(document).setOpen();
				if (items == undefined || items == "") {
					dialogs.alert({
						theme : "alert",
						title : "결재자 미선택",
						msg : "최소 한명 이상의 결재자를 선택하세요."
					}, function() {
						if (this.key == "ok") {
							// $name.focus();
						}
					})
					return false;
				}

				items = items.substring(0, items.length - 1);
				if (type == "app") {
					$(document).setAppLine(items);
				} else if (type == "agree") {
					$(document).setAgreeLine(items);
				} else if (type == "receive") {
					$(document).setReceiveLine(items);
				}
			})

			$("#addLineBtn").click(function() {
				$appType = $("input[name=appType]");
				var type;
				$.each($appType, function(idx) {
					if ($appType.eq(idx).prop("checked") == true) {
						type = $appType.eq(idx).val();
						return false;
					}
				})

				$options = $("#userList option");
				var items = "";
				$.each($options, function(idx) {
					if ($options.eq(idx).prop("selected") == true) {
						var value = $options.eq(idx).val();
						items += value + ",";
						// return false;
					}
				})

				var dialogs = $(document).setOpen();
				if (items == undefined || items == "") {
					dialogs.alert({
						theme : "alert",
						title : "결재자 미선택",
						msg : "최소 한명 이상의 결재자를 선택하세요."
					}, function() {
						if (this.key == "ok") {
							// $name.focus();
						}
					})
					return false;
				}
				items = items.substring(0, items.length - 1);
				if (type == "app") {
					$(document).setAppLine(items);
				} else if (type == "agree") {
					$(document).setAgreeLine(items);
				} else if (type == "receive") {
					$(document).setReceiveLine(items);
				}
			})

			$(".app_user_line").click(function() {
				$(".app_org").removeClass("strong");
				$(".app_search").removeClass("strong");
				$(".app_user_line").addClass("strong");

				$(".type_table").hide();
				$(".line_btn_table").show();

				$("#line_left_table").hide();
				$("#org_left_table").hide();
				$("#user_left_table").show();

				$(document).getUserLine();
			})

			$(".app_org").click(function() {
				$(".app_search").removeClass("strong");
				$(".app_user_line").removeClass("strong");
				$(".app_org").addClass("strong");

				$(".type_table").show();
				$(".line_btn_table").hide();
				$(".line_btn").hide();
				$(".org_btn").show();

				$("#line_left_table").hide();
				$("#org_left_table").show();
				$("#user_left_table").hide();
			})

			$(".app_search").click(function() {
				$(".app_org").removeClass("strong");
				$(".app_user_line").removeClass("strong");
				$(".app_search").addClass("strong");

				$(".type_table").show();
				$(".line_btn_table").hide();
				$(".org_btn").hide();
				$(".line_btn").show();

				$("#line_left_table").show();
				$("#org_left_table").hide();
				$("#user_left_table").hide();
			})

			$("#deleteLineBtn").click(function() {
				$options = $("#userLineList option");
				var value;
				var oid;
				$.each($options, function(idx) {
					if ($options.eq(idx).prop("selected") == true) {
						value = $options.eq(idx).val();
						return false;
					}
				})

				var arr = new Array();
				$.each($options, function(idx) {
					if ($options.eq(idx).prop("selected") == true) {
						oid = $options.eq(idx).data("oid");
						arr.push(oid);
					}
				})

				var dialogs = $(document).setOpen();
				if (value == undefined) {
					dialogs.alert({
						theme : "alert",
						title : "개인결재선 미선택",
						msg : "삭제할 개인결재선을 선택하세요."
					}, function() {
						if (this.key == "ok" || this.state == "close") {
							$("input[name=lineName]").val("").focus();
						}
					})
					return false;
				}
				$lineType = $("input[name=lineType]");
				var type;
				$.each($lineType, function(idx) {
					if ($lineType.eq(idx).prop("checked") == true) {
						type = $lineType.eq(idx).val();
						return false;
					}
				})

				dialogs.confirm({
					theme : "info",
					title : "개인결재선 삭제",
					msg : "개인결재선을 삭제 하시겠습니까?",
					width : 300
				}, function() {
					if (this.key == "ok") {
						var url = "/Windchill/plm/org/deleteUserLineAction";
						var params = new Object();
						params.lineType = type;
						params.list = arr;
						$(document).ajaxCallServer(url, params, function(data) {
							dialogs.alert({
								theme : "alert",
								title : "결과",
								msg : data.msg,
								width : 380
							}, function() {
								if (this.key == "ok") {
									document.location.href = data.url;
								}
							})
						}, true)
					}
				})
			})

			$("#saveBtn").click(function() {
				$lineName = $("input[name=lineName]").val();
				var dialogs = $(document).setOpen();
				if ($lineName == "") {
					dialogs.alert({
						theme : "alert",
						title : "결재선 이름 미입력",
						msg : "개인 결재선 이름을 입력하세요."
					}, function() {
						if (this.key == "ok") {
							$("input[name=lineName]").focus();
						}
					})
					return false;
				}

				$appOid = $("input[name=appOid]");
				if ($appOid.length == 0) {
					dialogs.alert({
						theme : "alert",
						title : "결재자 미선택",
						msg : "최소 한명 이상의 결재자 지정 되어야 합니다.",
						width : 350
					}, function() {
						if (this.key == "ok") {
							$("#line_left_table").show();
							$("#user_left_table").hide();
							$("#key").val("").focus();
							$(".line_btn_table").hide();
							$(".type_table").show();
							$(".app_user_line").removeClass("strong");
							$(".app_search").addClass("strong");
						}
					})
					return false;
				}

				$lineType = $("input[name=lineType]");
				var type;
				$.each($lineType, function(idx) {
					if ($lineType.eq(idx).prop("checked") == true) {
						type = $lineType.eq(idx).val();
						return false;
					}
				})

				var box = $(document).setNonOpen();
				box.confirm({
					theme : "info",
					title : "개인결재선 저장",
					msg : "개인결재선을 저장 하시겠습니까?",
					width : 300
				}, function() {
					if (this.key == "ok") {
						var url = "/Windchill/plm/org/saveUserLineAction";
						var params = new Object();
						params.lineName = $lineName;
						params.lineType = type;
						params = $(document).setUserLines(params);
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
						}, true)
					} else if (this.key == "cancel" || this.state == "close") {
						mask.close();
					}
				})
			})

			$.fn.setAppLine = function(items) {
				$lineType = $("input[name=lineType]");
				var type;
				$.each($lineType, function(idx) {
					if ($lineType.eq(idx).prop("checked") == true) {
						type = $lineType.eq(idx).val();
						return false;
					}
				})

				var values = items.split(",");
				for (var i = 0; i < values.length; i++) {
					value = values[i];
					var check = $(document).checkUser(value);
					if (check) {
						return false;
					}

					var len = $("input[name=appOid]").length;
					// oid, name, id, duty, depart
					var oid = value.split("&")[0];
					var name = value.split("&")[1];
					var id = value.split("&")[2];
					var duty = value.split("&")[3];
					var depart = value.split("&")[4];
					// $("#nodataTr_app").remove();
					if (type == "series") {
						$("#nodataTr_app_series").css("display", "none");
						var html;
						var body = $("#appBody_series");
						html += "<tr id=\"appTr\">";
						html += "<td class=\"addAppTag right-border\"><input type=\"hidden\" name=\"appValue\" value=\"" + value + "\">" + "<input style=\"display: none;\" type=\"checkbox\" name=\"appOid\" value=\"" + oid
								+ "\"><div class=\"ico-checkbox helper-check helper-checks-checkbox-allApp\"></div></td>";
						html += "<td class=\"right-border\">" + (len + 1 + i) + "</td>";
						html += "<td class=\"right-border\">" + name + "</td>";
						html += "<td class=\"right-border\">" + id + "</td>";
						html += "<td class=\"right-border\">" + duty + "</td>";
						html += "<td>" + depart + "</td>";
						html += "</tr>";
						// body.append(html);
					} else {
						$("#nodataTr_app_parallel").css("display", "none");
						var html;
						var body = $("#appBody_parallel");
						html += "<tr id=\"appTr\">";
						html += "<td class=\"addAppTag right-border\"><input type=\"hidden\" name=\"appValue\" value=\"" + value + "\">" + "<input style=\"display: none;\" type=\"checkbox\" name=\"appOid\" value=\"" + oid
								+ "\"><div class=\"ico-checkbox helper-check helper-checks-checkbox-allApp\"></div></td>";
						html += "<td class=\"right-border\">" + name + "</td>";
						html += "<td class=\"right-border\">" + id + "</td>";
						html += "<td class=\"right-border\">" + duty + "</td>";
						html += "<td>" + depart + "</td>";
						html += "</tr>";
						// body.append(html);
					}
				}
				body.append(html);
			}

			$.fn.setAgreeLine = function(items) {

				var values = items.split(",");
				for (var i = 0; i < values.length; i++) {
					value = values[i];

					var check = $(document).checkUser(value);
					if (check) {
						return false;
					}

					// oid, name, id, duty, depart
					var oid = value.split("&")[0];
					var name = value.split("&")[1];
					var id = value.split("&")[2];
					var duty = value.split("&")[3];
					var depart = value.split("&")[4];
					// $("#nodataTr_agree").remove();
					$("#nodataTr_agree").css("display", "none");
					var html;
					var body = $("#agreeBody");
					html += "<tr id=\"appTr\">";
					html += "<td class=\"addAgreeTag right-border\"><input type=\"hidden\" name=\"agreeValue\" value=\"" + value + "\">" + "<input style=\"display: none;\" type=\"checkbox\" name=\"agreeOid\" value=\"" + oid
							+ "\"><div class=\"ico-checkbox helper-check helper-checks-checkbox-allAgree\"></div></td>";
					html += "<td class=\"right-border\">" + name + "</td>";
					html += "<td class=\"right-border\">" + id + "</td>";
					html += "<td class=\"right-border\">" + duty + "</td>";
					html += "<td>" + depart + "</td>";
					html += "</tr>";
				}
				body.append(html);
			}

			$.fn.setReceiveLine = function(items) {
				var values = items.split(",");
				for (var i = 0; i < values.length; i++) {
					value = values[i];
					var check = $(document).checkUser(value);
					if (check) {
						return false;
					}
					// oid, name, id, duty, depart
					var oid = value.split("&")[0];
					var name = value.split("&")[1];
					var id = value.split("&")[2];
					var duty = value.split("&")[3];
					var depart = value.split("&")[4];
					// $("#nodataTr_receive").remove();
					$("#nodataTr_receive").css("display", "none");
					var html;
					var body = $("#receiveBody");
					html += "<tr id=\"appTr\">";
					html += "<td class=\"addReceiveTag right-border\"><input type=\"hidden\" name=\"receiveValue\" value=\"" + value + "\">" + "<input style=\"display: none;\" type=\"checkbox\" name=\"receiveOid\" value=\"" + oid
							+ "\"><div class=\"ico-checkbox helper-check helper-checks-checkbox-allReceive\"></div></td>";
					html += "<td class=\"right-border\">" + name + "</td>";
					html += "<td class=\"right-border\">" + id + "</td>";
					html += "<td class=\"right-border\">" + duty + "</td>";
					html += "<td>" + depart + "</td>";
					html += "</tr>";
				}
				body.append(html);
			}

			$.fn.setUserLine = function(data) {
				$("#userLineList option").remove();
				var len = data.list.length;
				for (var i = 0; i < len; i++) {
					var type = data.list[i].lineType;
					var txt = "직렬결재";

					var appList = data.list[i].appList;
					var agreeList = data.list[i].agreeList;
					var receiveList = data.list[i].receiveList;

					if ("parallel" == type) {
						txt = "병렬결재";
					}
					$("#userLineList").append(
							"<option data-type=\"" + data.list[i].lineType + "\" data-app=\"" + appList + "\" data-agree=\"" + agreeList + "\" data-receive=\"" + receiveList + "\" value=\"" + data.list[i].value + "\" data-oid=\"" + data.list[i].oid + "\">[" + txt + "]&nbsp;" + data.list[i].name
									+ "</option>");
				}
			}

			$("#userLineList option").on("dblclick", function(e) {
				callLine();
			})

			$.fn.setUserForDept = function(data) {
				$("#userDeptList option").remove();
				var len = data.list.length;
				for (var i = 0; i < len; i++) {
					$("#userDeptList").append("<option value=\"" + data.list[i].value + "\">" + data.list[i].name + " [" + data.list[i].deptName + "]</option>");
				}
			}

			$.fn.setUserList = function(data) {
				$("#userList option").remove();
				var len = data.list.length;
				for (var i = 0; i < len; i++) {
					$("#userList").append("<option value=\"" + data.list[i].value + "\">" + data.list[i].name + " [" + data.list[i].deptName + "]</option>");
				}
				$(document).offLayer();
			}

			$(document).on("click", ".addAppTag", function() {
				$(this).find(".ico-checkbox").toggleClass("sed");
				if ($(this).find("input[name=appOid]").prop("checked") == true) {
					$(this).find("input[name=appOid]").prop("checked", false);
				} else {
					$(this).find("input[name=appOid]").prop("checked", true);
				}
			})

			$(document).on("click", ".addAgreeTag", function() {
				$(this).find(".ico-checkbox").toggleClass("sed");
				if ($(this).find("input[name=agreeOid]").prop("checked") == true) {
					$(this).find("input[name=agreeOid]").prop("checked", false);
				} else {
					$(this).find("input[name=agreeOid]").prop("checked", true);
				}
			})

			$(document).on("click", ".addReceiveTag", function() {
				$(this).find(".ico-checkbox").toggleClass("sed");
				if ($(this).find("input[name=receiveOid]").prop("checked") == true) {
					$(this).find("input[name=receiveOid]").prop("checked", false);
				} else {
					$(this).find("input[name=receiveOid]").prop("checked", true);
				}
			})

			$("input[name=allApp]").click(function(e) {

				$oid = $("input[name=appOid]");
				if ($oid.length == 0) {
					e.stopPropagation();
					e.preventDefault();
					$(this).next().removeClass("sed");
					return false;
				}

				if ($(this).prop("checked") == true) {
					$.each($oid, function(idx) {
						$oid.eq(idx).next().addClass("sed");
						$oid.eq(idx).prop("checked", true);
					})
				} else {
					$.each($oid, function(idx) {
						$oid.eq(idx).next().removeClass("sed");
						$oid.eq(idx).prop("checked", false);
					})
				}
			})

			$("#allAgree").click(function() {
				$oid = $("input[name=agreeOid]");
				if ($(this).prop("checked") == true) {
					$.each($oid, function(idx) {
						$oid.eq(idx).next().addClass("sed");
						$oid.eq(idx).prop("checked", true);
					})
				} else {
					$.each($oid, function(idx) {
						$oid.eq(idx).next().removeClass("sed");
						$oid.eq(idx).prop("checked", false);
					})
				}
			})

			$("#allReceive").click(function() {
				$oid = $("input[name=receiveOid]");
				if ($(this).prop("checked") == true) {
					$.each($oid, function(idx) {
						$oid.eq(idx).next().addClass("sed");
						$oid.eq(idx).prop("checked", true);
					})
				} else {
					$.each($oid, function(idx) {
						$oid.eq(idx).next().removeClass("sed");
						$oid.eq(idx).prop("checked", false);
					})
				}
			})

			$(document).bind("click", "input[name=appOid]", function() {
				$bool = false;
				$oid = $("input[name=appOid");
				$cnt = 0;
				$.each($oid, function(idx) {
					if ($oid.eq(idx).prop("checked") == false) {
						$bool = true;
						return false;
					} else if ($oid.eq(idx).prop("checked") == true) {
						$cnt++;
					}
				})

				if ($cnt == $oid.length && $cnt != 0) {
					$("input[name=allApp]").prop("checked", true);
					$("input[name=allApp]").next().addClass("sed");
				}

				if ($bool) {
					$("input[name=allApp]").prop("checked", false);
					$("input[name=allApp]").next().removeClass("sed");
				}
			})

			$(document).bind("click", "input[name=agreeOid]", function() {
				$bool = false;
				$oid = $("input[name=agreeOid");
				$cnt = 0;
				$.each($oid, function(idx) {
					if ($oid.eq(idx).prop("checked") == false) {
						$bool = true;
						return false;
					} else if ($oid.eq(idx).prop("checked") == true) {
						$cnt++;
					}
				})

				if ($cnt == $oid.length && $cnt != 0) {
					$("#allAgree").prop("checked", true);
					$("#allAgree").next().addClass("sed");
				}

				if ($bool) {
					$("#allAgree").prop("checked", false);
					$("#allAgree").next().removeClass("sed");
				}
			})

			$(document).bind("click", "input[name=receiveOid]", function() {
				$bool = false;
				$oid = $("input[name=receiveOid");
				$cnt = 0;
				$.each($oid, function(idx) {
					if ($oid.eq(idx).prop("checked") == false) {
						$bool = true;
						return false;
					} else if ($oid.eq(idx).prop("checked") == true) {
						$cnt++;
					}
				})

				if ($cnt == $oid.length && $cnt != 0) {
					$("#allReceive").prop("checked", true);
					$("#allReceive").next().addClass("sed");
				}

				if ($bool) {
					$("#allReceive").prop("checked", false);
					$("#allReceive").next().removeClass("sed");
				}
			})

			$.fn.checkUser = function(value) {
				var dialogs = $(document).setOpen();
				var bool = false;
				var type;
				// oid check
				$oid = value.split("&")[0];
				$name = value.split("&")[1];
				$appOid = $("input[name=appOid]");
				$.each($appOid, function(idx) {
					if ($oid == $appOid.eq(idx).val()) {
						bool = true;
						type = "결재라인";
						return false;
					}
				})

				$agreeOid = $("input[name=agreeOid]");
				$.each($agreeOid, function(idx) {
					if ($oid == $agreeOid.eq(idx).val()) {
						bool = true;
						type = "합의라인";
						return false;
					}
				})

				$receiveOid = $("input[name=receiveOid]");
				$.each($receiveOid, function(idx) {
					if ($oid == $receiveOid.eq(idx).val()) {
						bool = true;
						type = "수신라인";
						return false;
					}
				})

				if (bool) {
					dialogs.alert({
						theme : "alert",
						title : "중복 결재자",
						msg : $name + " 사용자는 이미 " + type + "에 등록되어 있습니다.",
						width : 420
					}, function() {
						if (this.key == "ok") {
							// return true;
						}
					})
				}
				return bool;
			}

			$("#allApp").click(function(e) {
				$oid = $("input[name=appOid]");
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

			$(document).bind("click", "input[name=appOid]", function(e) {
				$bool = false;
				$oid = $("input[name=appOid]");
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
					$("input[name=allApp]").prop("checked", true);
					$("input[name=allApp]").next().addClass("sed");
				}

				if ($bool) {
					$("input[name=allApp]").prop("checked", false);
					$("input[name=allApp]").next().removeClass("sed");
				}
			})

			$("#allReceive").click(function(e) {
				$oid = $("input[name=receiveOid]");

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

			$("#applyUserLineBtn").click(function() {
				$options = $("#userLineList option");
				var value;
				$.each($options, function(idx) {
					if ($options.eq(idx).prop("selected") == true) {
						value = $options.eq(idx).val();
						return false;
					}
				})

				var dialogs = $(document).setOpen();
				if (value == undefined) {
					dialogs.alert({
						theme : "alert",
						title : "개인결재선 미선택",
						msg : "개인결재선을 선택하세요."
					})
					return false;
				}
				callLine();
			})

			$(document).bind("click", "input[name=receiveOid]", function(e) {
				$bool = false;
				$oid = $("input[name=receiveOid]");
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
					$("#allReceive").prop("checked", true);
					$("#allReceive").next().addClass("sed");
				}

				if ($bool) {
					$("#allReceive").prop("checked", false);
					$("#allReceive").next().removeClass("sed");
				}
			})

			$("#allAgree").click(function(e) {
				$oid = $("input[name=agreeOid]");

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

			$(document).bind("click", "input[name=agreeOid]", function(e) {
				$bool = false;
				$oid = $("input[name=agreeOid]");
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
					$("#allAgree").prop("checked", true);
					$("#allAgree").next().addClass("sed");
				}

				if ($bool) {
					$("#allAgree").prop("checked", false);
					$("#allAgree").next().removeClass("sed");
				}
			})

			$.fn.resetAll = function() {

				$lineType = $("input[name=lineType]");
				var type;
				$.each($lineType, function(idx) {
					if ($lineType.eq(idx).prop("checked") == true) {
						type = $lineType.eq(idx).val();
						return false;
					}
				})

				$appOid = $("input[name=appOid]");
				$agreeOid = $("input[name=agreeOid]");
				$receiveOid = $("input[name=receiveOid]");

				$approvalLen = $appOid.length;
				$agreeLen = $agreeOid.length;
				$receiveLen = $receiveOid.length;

				for (var i = 0; i < $appOid.length; i++) {
					$appOid.eq(i).parent().parent().remove();
					--$approvalLen;
				}

				for (var i = 0; i < $agreeOid.length; i++) {
					$agreeOid.eq(i).parent().parent().remove();
					--$agreeLen;
				}

				for (var i = 0; i < $receiveOid.length; i++) {
					$receiveOid.eq(i).parent().parent().remove();
					--$receiveLen;
				}

				if ($approvalLen == 0) {
					if (type == "series") {
						$("#nodataTr_app_series").css("display", "");
					} else {
						$("#nodataTr_app_parallel").css("display", "");
					}
				}

				if ($agreeLen == 0) {
					$("#nodataTr_agree").css("display", "");
				}

				if ($receiveLen == 0) {
					$("#nodataTr_receive").css("display", "");
				}

				$("input[name=allApp]").prop("checked", false);
				$("input[name=allApp]").next().removeClass("sed");
				$("#allAgree").prop("checked", false);
				$("#allAgree").next().removeClass("sed");
				$("#allReceive").prop("checked", false);
				$("#allReceive").next().removeClass("sed");
			}

			$("#resetAll").click(function() {
				var dialogs = $(document).setOpen();
				dialogs.confirm({
					theme : "info",
					title : "결재라인 초기화",
					msg : "모든 결재, 합의, 수신 라인이 삭제 되어집니다.\n계속 하시겠습니까?",
					width : 380
				}, function() {
					if (this.key == "ok") {
						$(document).resetAll();
					}
				})
			})

			$("#deleteLine").add("#deleteLines").click(function() {

				$lineType = $("input[name=lineType]");
				var type;

				$.each($lineType, function(idx) {
					if ($lineType.eq(idx).prop("checked") == true) {
						type = $lineType.eq(idx).val();
						return false;
					}
				})
				$appOid = $("input[name=appOid]");
				$agreeOid = $("input[name=agreeOid]");
				$receiveOid = $("input[name=receiveOid]");

				$approvalLen = $appOid.length;
				$agreeLen = $agreeOid.length;
				$receiveLen = $receiveOid.length;

				for (var i = $appOid.length; i >= 0; i--) {
					if ($appOid.eq(i).prop("checked") == true) {
						$appOid.parent().parent().eq(i).remove();
						--$approvalLen;
					}
				}

				for (var i = $agreeOid.length; i >= 0; i--) {
					if ($agreeOid.eq(i).prop("checked") == true) {
						$agreeOid.parent().parent().eq(i).remove();
						--$agreeLen;
					}
				}

				for (var i = $receiveOid.length; i >= 0; i--) {
					if ($receiveOid.eq(i).prop("checked") == true) {
						$receiveOid.parent().parent().eq(i).remove();
						--$receiveLen;
					}
				}

				if ($approvalLen == 0) {
					$("input[name=allApp]").prop("checked", false);
					$("input[name=allApp]").next().removeClass("sed");

					if (type == "series") {
						$("#nodataTr_app_series").css("display", "");
					} else {
						$("#nodataTr_app_parallel").css("display", "");
					}
				}

				if ($agreeLen == 0) {
					$("#allAgree").prop("checked", false);
					$("#allAgree").next().removeClass("sed");
					$("#nodataTr_agree").css("display", "");
				}

				if ($receiveLen == 0) {
					$("#allReceive").prop("checked", false);
					$("#allReceive").next().removeClass("sed");
					$("#nodataTr_receive").css("display", "");
				}
			})

			$.fn.getUserLine = function() {
				var url = "/Windchill/plm/org/getUserLine";
				var params = new Object();
				params.lineName = $("input[name=lineName]").val();

				$(document).ajaxCallServer(url, params, function(data) {
					$(document).setUserLine(data);
					mask.close();
				}, true);
			}

			$("#lineSearch").click(function() {
				$(document).getUserLine();
			})

			$.fn.searchLine = function() {
				$(document).onLayer();
				$key = $("#key");
				var url = "/Windchill/plm/org/getUserList";
				var params = new Object();
				params.key = $key.val();

				$(document).ajaxCallServer(url, params, function(data) {
					$(document).setUserList(data);
				}, false);
			}

			$.fn.loadUserLine = function(lineType, appList, agreeList, receiveList) {

				$appValue = appList.split(",");
				$agreeValue = agreeList.split(",");
				$receiveValue = receiveList.split(",");

				$appLen = appList.split(",").length;
				$agreeLen = agreeList.split(",").length;
				$receiveLen = receiveList.split(",").length;

				if (lineType == "parallel") {
					$(".series_table").hide();
					$(".parallel_table").show();
				} else {
					$(".series_table").show();
					$(".parallel_table").hide();
				}
				// oid, name, id, duty, depart
				var appBody;
				var appHtml;
				for (var i = 0; i < $appLen; i++) {
					var oid = $appValue[i].split("&")[0];
					var name = $appValue[i].split("&")[1];
					var id = $appValue[i].split("&")[2];
					var duty = $appValue[i].split("&")[3];
					var depart = $appValue[i].split("&")[4];
					if (lineType == "series") {
						$("#nodataTr_app_series").css("display", "none");
						appBody = $("#appBody_series");
						appHtml += "<tr id=\"appTr\">";
						appHtml += "<td class=\"addAppTag right-border\"><input type=\"hidden\" name=\"appValue\" value=\"" + $appValue[i] + "\">" + "<input style=\"display: none;\" type=\"checkbox\" name=\"appOid\" value=\"" + oid
								+ "\"><div class=\"ico-checkbox helper-check helper-checks-checkbox-allApp\"></div></td>";
						appHtml += "<td class=\"right-border\">" + (i + 1) + "</td>";
						appHtml += "<td class=\"right-border\">" + name + "</td>";
						appHtml += "<td class=\"right-border\">" + id + "</td>";
						appHtml += "<td class=\"right-border\">" + duty + "</td>";
						appHtml += "<td>" + depart + "</td>";
						appHtml += "</tr>";
					} else {
						$("#nodataTr_app_parallel").css("display", "none");
						appBody = $("#appBody_parallel");
						appHtml += "<tr id=\"appTr\">";
						appHtml += "<td class=\"addAppTag right-border\"><input type=\"hidden\" name=\"appValue\" value=\"" + $appValue[i] + "\">" + "<input style=\"display: none;\" type=\"checkbox\" name=\"appOid\" value=\"" + oid
								+ "\"><div class=\"ico-checkbox helper-check helper-checks-checkbox-allApp\"></div></td>";
						appHtml += "<td class=\"right-border\">" + name + "</td>";
						appHtml += "<td class=\"right-border\">" + id + "</td>";
						appHtml += "<td class=\"right-border\">" + duty + "</td>";
						appHtml += "<td>" + depart + "</td>";
						appHtml += "</tr>";
					}
				}
				appBody.append(appHtml);

				var agreeBody = $("#agreeBody");
				var agreeHtml = "";
				var bool = true;
				for (var i = 0; i < $agreeLen; i++) {
					var oid = $agreeValue[i].split("&")[0];
					var name = $agreeValue[i].split("&")[1];
					var id = $agreeValue[i].split("&")[2];
					var duty = $agreeValue[i].split("&")[3];
					var depart = $agreeValue[i].split("&")[4];
					$("#nodataTr_agree").css("display", "none");
					agreeHtml += "<tr id=\"appTr\">";
					agreeHtml += "<td class=\"addAgreeTag right-border\"><input type=\"hidden\" name=\"agreeValue\" value=\"" + $agreeValue[i] + "\">" + "<input style=\"display: none;\" type=\"checkbox\" name=\"agreeOid\" value=\"" + oid
							+ "\"><div class=\"ico-checkbox helper-check helper-checks-checkbox-allAgree\"></div></td>";
					agreeHtml += "<td class=\"right-border\">" + name + "</td>";
					agreeHtml += "<td class=\"right-border\">" + id + "</td>";
					agreeHtml += "<td class=\"right-border\">" + duty + "</td>";
					agreeHtml += "<td>" + depart + "</td>";
					agreeHtml += "</tr>";

					if (oid == undefined || oid == "") {
						bool = false;
					}
				}

				if (bool) {
					agreeBody.append(agreeHtml);
				}

				var receiveBody = $("#receiveBody");
				var receiveHtml = "";
				var bool2 = true;
				for (var i = 0; i < $receiveLen; i++) {
					// oid, name, id, duty, depart
					var oid = $receiveValue[i].split("&")[0];
					var name = $receiveValue[i].split("&")[1];
					var id = $receiveValue[i].split("&")[2];
					var duty = $receiveValue[i].split("&")[3];
					var depart = $receiveValue[i].split("&")[4];
					// $("#nodataTr_receive").remove();
					$("#nodataTr_receive").css("display", "none");
					receiveHtml += "<tr id=\"appTr\">";
					receiveHtml += "<td class=\"addReceiveTag right-border\"><input type=\"hidden\" name=\"receiveValue\" value=\"" + $receiveValue[i] + "\">" + "<input style=\"display: none;\" type=\"checkbox\" name=\"receiveOid\" value=\"" + oid
							+ "\"><div class=\"ico-checkbox helper-check helper-checks-checkbox-allReceive\"></div></td>";
					receiveHtml += "<td class=\"right-border\">" + name + "</td>";
					receiveHtml += "<td class=\"right-border\">" + id + "</td>";
					receiveHtml += "<td class=\"right-border\">" + duty + "</td>";
					receiveHtml += "<td>" + depart + "</td>";
					receiveHtml += "</tr>";
					if (oid == undefined || oid == "") {
						bool2 = false;
					}
				}

				if (bool2) {
					receiveBody.append(receiveHtml);
				}

				$agreeOid = $("input[name=agreeOid]");
				$receiveOid = $("input[name=receiveOid]");

				if ($agreeOid.length == 0) {
					$("#nodataTr_agree").css("display", "");
				}

				if ($receiveOid.length == 0) {
					$("#nodataTr_receive").css("display", "");
				}

				$("#receive_table").tableHeadFixer();

				$("#agree_table").tableHeadFixer();

				$("#approval_table").tableHeadFixer();
			}

			$("#applyLineBtns").click(function() {
				var dialogs = $(document).setOpen();
				$appOid = $("input[name=appOid]");
				if ($appOid.length == 0) {
					dialogs.alert({
						theme : "alert",
						title : "결재자 미선택",
						msg : "최소 한명 이상의 결재자를 선택하세요."
					})
					return false;
				}

				var type = $("input[name=type]").val();

				if (type == "series") {
					$addLineBody = $("#addLineBody_series", opener.document);
					for (var i = $addLineBody.length; i >= 0; i--) {
						$addLineBody.eq(i).children().remove();
					}

					$html = "<tr class=\"noDisplay\">";
					$html += "<td colspan=\"7\"><input type=\"hidden\" name=\"lineType\" value=\"" + type + "\"></td>";
					$html += "</tr>";
					$addLineBody.append($html);

					$cnt = 0;
					$appValue = $("input[name=appValue]");
					for (var i = 0; i < $appValue.length; i++) {
						$value = $appValue.eq(i).val();
						$oid = $value.split("&")[0];
						$id = $value.split("&")[1];
						$name = $value.split("&")[2];
						$duty = $value.split("&")[3];
						$depart = $value.split("&")[4];
						$html = "<tr>";
						$html += "<td class=\"addLineTag right-border\"><input style=\"display: none;\" type=\"checkbox\" name=\"lines\" value=\"\"><div class=\"ico-checkbox helper-checks helper-checks-checkbox-allLine\"></div></td>";
						$html += "<td class=\"right-border\">" + ++$cnt;
						$html += "<input value=\"" + $oid + "\" name=\"appUserOid\" type=\"hidden\">";
						$html += "<input value=\"" + $value + "\" name=\"appUserInfo\" type=\"hidden\">";
						$html += "</td>";
						$html += "<td class=\"right-border\"><font color=\"blue\">결재</font></td>";
						$html += "<td class=\"right-border\">" + $id + "</td>";
						$html += "<td class=\"right-border\">" + $name + "</td>";
						$html += "<td class=\"right-border\">" + $duty + "</td>";
						$html += "<td>" + $depart + "</td>";
						$html += "</tr>";
						$addLineBody.append($html);
					}
				} else {
					$addLineBody = $("#addLineBody_parallel", opener.document);
					for (var i = $addLineBody.length; i >= 0; i--) {
						$addLineBody.eq(i).children().remove();
					}

					$html = "<tr class=\"noDisplay\">";
					$html += "<td colspan=\"6\"><input type=\"hidden\" name=\"lineType\" value=\"" + type + "\"></td>";
					$html += "</tr>";
					$addLineBody.append($html);

					$appValue = $("input[name=appValue]");
					for (var i = 0; i < $appValue.length; i++) {
						$value = $appValue.eq(i).val();
						$oid = $value.split("&")[0];
						$id = $value.split("&")[1];
						$name = $value.split("&")[2];
						$duty = $value.split("&")[3];
						$depart = $value.split("&")[4];
						$html = "<tr>";
						$html += "<td class=\"addLineTag right-border\"><input style=\"display: none;\" type=\"checkbox\" name=\"lines\" value=\"\"><div class=\"ico-checkbox helper-checks helper-checks-checkbox-allLine\"></div></td>";
						$html += "<td class=\"right-border\"><input value=\"" + $value + "\" name=\"appUserInfo\" type=\"hidden\"><input value=\"" + $oid + "\" name=\"appUserOid\" type=\"hidden\"><font color=\"blue\">결재</font></td>";
						$html += "<td class=\"right-border\">" + $id + "</td>";
						$html += "<td class=\"right-border\">" + $name + "</td>";
						$html += "<td class=\"right-border\">" + $duty + "</td>";
						$html += "<td>" + $depart + "</td>";
						$html += "</tr>";
						$addLineBody.append($html);
					}
				}

				$agreeValue = $("input[name=agreeValue]");
				for (var i = 0; i < $agreeValue.length; i++) {
					$value = $agreeValue.eq(i).val();
					$oid = $value.split("&")[0];
					$id = $value.split("&")[1];
					$name = $value.split("&")[2];
					$duty = $value.split("&")[3];
					$depart = $value.split("&")[4];
					$html = "<tr>";
					$html += "<td class=\"addLineTag right-border\"><input style=\"display: none;\" type=\"checkbox\" name=\"lines\"><div class=\"ico-checkbox helper-checks helper-checks-checkbox-allLine\"></div></td>";
					if (type == "series") {
						$html += "<td class=\"right-border\">&nbsp;";
						$html += "<input value=\"" + $oid + "\" name=\"agreeUserOid\" type=\"hidden\">";
						$html += "<input value=\"" + $value + "\" name=\"agreeUserInfo\" type=\"hidden\">";
						$html += "</td>";
						$html += "<td class=\"right-border\"><font color=\"green\">합의</font></td>";
					} else {
						$html += "<td class=\"right-border\"><input value=\"" + $oid + "\" name=\"agreeUserOid\" type=\"hidden\"><input value=\"" + $value + "\" name=\"agreeUserInfo\" type=\"hidden\"><font color=\"green\">합의</font></td>";
					}

					$html += "<td class=\"right-border\">" + $id + "</td>";
					$html += "<td class=\"right-border\">" + $name + "</td>";
					$html += "<td class=\"right-border\">" + $duty + "</td>";
					$html += "<td>" + $depart + "</td>";
					$html += "</tr>";
					$addLineBody.append($html);
				}

				$receiveValue = $("input[name=receiveValue]");
				for (var i = 0; i < $receiveValue.length; i++) {
					$value = $receiveValue.eq(i).val();
					$oid = $value.split("&")[0];
					$id = $value.split("&")[1];
					$name = $value.split("&")[2];
					$duty = $value.split("&")[3];
					$depart = $value.split("&")[4];
					$html = "<tr>";
					$html += "<td class=\"addLineTag right-border\"><input style=\"display: none;\" type=\"checkbox\" name=\"lines\"><div class=\"ico-checkbox helper-checks helper-checks-checkbox-allLine\"></div></td>";
					if (type == "series") {
						$html += "<td class=\"right-border\">&nbsp;";
						$html += "<input value=\"" + $oid + "\" name=\"receiveUserOid\" type=\"hidden\">";
						$html += "<input value=\"" + $value + "\" name=\"receiveUserInfo\" type=\"hidden\">";
						$html += "</td>";
						$html += "<td class=\"right-border\"><font color=\"red\">수신</font></td>";
					} else {
						$html += "<td class=\"right-border\"><input value=\"" + $oid + "\" name=\"receiveUserOid\" type=\"hidden\"><input value=\"" + $value + "\" name=\"receiveUserInfo\" type=\"hidden\"><font color=\"red\">수신</font></td>";
					}
					$html += "<td class=\"right-border\">" + $id + "</td>";
					$html += "<td class=\"right-border\">" + $name + "</td>";
					$html += "<td class=\"right-border\">" + $duty + "</td>";
					$html += "<td>" + $depart + "</td>";
					$html += "</tr>";
					$addLineBody.append($html);
				}
				self.close();
			})

		})
function callLine() {
	$options = $("#userLineList option");
	var cnt = 0;
	var appList;
	var agreeLsit;
	var receiveList;
	var lineType;
	$.each($options, function(idx) {
		if ($options.eq(idx).prop("selected") == true) {
			cnt++;
			lineType = $options.eq(idx).data("type");
			appList = $options.eq(idx).data("app");
			agreeList = $options.eq(idx).data("agree");
			receiveList = $options.eq(idx).data("receive");
		}
	})
	var dialogs = $(document).setOpen();
	if (cnt > 1) {
		dialogs.alert({
			theme : "alert",
			title : "개인결재선 다중선택",
			msg : "하나의 개인결재선을 선택하세요."
		}, function() {
			if (this.key == "ok") {
				$.each($options, function(idx) {
					$options.eq(idx).prop("selected", false);
				})
			}
		})
		return false;
	}

	// dialogs.confirm({
	// theme : "info",
	// title : "개인결재선 적용",
	// msg : "개인결재선을 적용 하시겠습니까?\n기존 결재선이 초기화 되어집니다.",
	// width : 300
	// }, function() {
	// if (this.key == "ok") {
	$("input[name=type]").val(lineType);

	if (lineType == "parallel") {
		$list = $("input[name=lines]", opener.document);
		$.each($list, function(idx) {
			$list.eq(idx).parent().parent().remove();
		})

		var list = $("input[name=lines]", opener.document);
		$("#nodataTrLine_series", opener.document).remove();

		if (list.length == 0) {
			var body = $("#addLineBody_series", opener.document);
			var html;
			html += "<tr id=\"nodataTrLine_series\">";
			html += "<td class=\"nodata h140\" colspan=\"7\"><font class=\"noInfo\"><a class=\"axi axi-info-outline\"></a> <span>지정된 직렬 결재라인이 없습니다.</span></font></td>";
			html += "</tr>";
			body.append(html);
			$("input[name=allLine]", opener.document).prop("checked", false);
			$("input[name=allLine]", opener.document).next().removeClass("sed");
		}
	} else if ("series") {
		$list = $("input[name=lines]", opener.document);
		$.each($list, function(idx) {
			$list.eq(idx).parent().parent().remove();
		})

		var list = $("input[name=lines]", opener.document);

		$("#nodataTrLine_parallel", opener.document).remove();

		if (list.length == 0) {
			var body = $("#addLineBody_parallel", opener.document);
			var html;
			html += "<tr id=\"nodataTrLine_parallel\">";
			html += "<td class=\"nodata h140\" colspan=\"6\"><font class=\"noInfo\"><a class=\"axi axi-info-outline\"></a> <span>지정된 병렬 결재라인이 없습니다.</span></font></td>";
			html += "</tr>";
			body.append(html);
			$("input[name=allLine]", opener.document).prop("checked", false);
			$("input[name=allLine]", opener.document).next().removeClass("sed");
		}
	}

	// $(opener.location).attr("href", "Javascript:reverseTable('" + lineType + "');");
	$(document).resetAll();
	$(document).loadUserLine(lineType, appList, agreeList, receiveList);
	// }
	// })
}
