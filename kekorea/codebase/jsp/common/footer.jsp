<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<script>
	$(function() {
		$(".sortable-table").tablesort().data('tablesort');
	});
</script>
<div id="loading_layer">
	<img src="/Windchill/jsp/images/loading.gif">
</div>
<a id="downloadFileContent"></a>
<div>
	<script type="text/javascript">
		$(window).resize(function() {
			var s = $("#header_table").width();
			var tree = $(".tree_td").width();
			if(tree != null) {
				$("#container_td").css({
					width : "100%"
				})
			} else {
				$("#content_table").width(s);
			}
			$(document).setHTML();
		})

		$(document).ready(function() {
			$(document).setHTML();
			var s = $("#header_table").width();
			var w = $("td.list_container_td").width();
			var tree = $(".tree_td").width();
			if(tree != null) {
// 				s = s - tree;
			}
// 			$("#content_table").width(s);

			$(".chatLayer").draggable();

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
					onChange : function() {
						//toast.push(Object.toJSON(this));
					}
				});
			});

			var config = {
				options : [ {
					optionValue : "part",
					optionText : "부품"
				}, {
					optionValue : "epm",
					optionText : "도면"
				}, {
					optionValue : "document",
					optionText : "문서"
				}, ]
			}

			var configApp = {
				options : [ {
					optionValue : "part",
					optionText : "구매품"
				}, {
					optionValue : "e-bom",
					optionText : "E-BOM"
				}, {
					optionValue : "ecn",
					optionText : "ECN"
				}, {
					optionValue : "stn",
					optionText : "STN"
				}, {
					optionValue : "document",
					optionText : "문서"
				}, {
					optionValue : "partApp",
					optionText : "일괄결재(부품)"
				}, {
					optionValue : "documentApp",
					optionText : "일괄결재(문서)"
				}, ]
			}

			var fileType = {
				options : [ {
					optionValue : "xls",
					optionText : "엑셀"
				}, {
					optionValue : "ppt",
					optionText : "파워포인트"
				}, ]
			}

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
						$(document).openURLViewOpt(url, 1200, 600, "");
					}
				}
			});

			$(".datePicker").each(function() {
// 				$(this).css("width", "35%");

				$(this).bindDate({
					align : "left",
					valign : "top",
					buttonText : "확인",
					customPos : {
						top : 25,
						left : 25
					}
				});
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
		})
	</script>
</div>