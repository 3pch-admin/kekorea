/**
 * 사용자 전용 javascript
 */
var orgs = {

	resignListUserActionUrl : "/Windchill/plm/org/setResignAction",

	changePasswordActionUrl : "/Windchill/plm/org/changePasswordAction",

	addUserActionUrl : "/Windchill/plm/org/addUserAction",

	listUserUrl : "/Windchill/plm/org/viewOrg",

	modifyUserUrl : "/Windchill/plm/org/modifyUser",

	addDblUsers : function(obj, value, target) {

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

		var url = this.addUserActionUrl;
		var params = $(document).getDblFormOneData(value);
		mask.open();
		$("#loading_layer").show();

		$(document).ajaxCallServer(url, params, function(data) {
			orgs.addUsers(data.list, target);
		}, false);
	},

	addUserAction : function(target) {
		var dialogs = $(document).setOpen();
		$len = $("input[name=oid]").length;
		if ($len == 0) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "추가할 사용자 먼저 검색하세요."
			})
			return false;
		}

		var isSelect = $(document).isSelect();
		if (isSelect == false) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "추가할 사용자를 선택하세요"
			})
			return false;
		}

		var url = this.addUserActionUrl;
		// var params = $(document).getDblFormOneData(value);

		var params = new Object();
		$oid = $("input[name=oid]");
		$.each($oid, function(idx) {
			if ($oid.eq(idx).prop("checked") == true) {
				params.oid = $oid.eq(idx).val();
				return false;
			}
		})

		mask.open();
		$("#loading_layer").show();

		$(document).ajaxCallServer(url, params, function(data) {
			orgs.addUsers(data.list, target);
		}, false);
	},

	addUsers : function(list, target) {
		var value = list[0][0];
		$(opener.document).find("#" + target).val(
				list[0][1] + " " + "[" + list[0][2] + "]");
		$(opener.document).find("#" + target + "Oid").val(value);
		mask.close()
		$("#loading_layer").hide();
	},

	resignListUserAction : function() {
		var isSelect = $(document).isSelect();
		var dialogs = $(document).setOpen();
		var url = this.resignListUserActionUrl;
		if (isSelect == false) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "퇴사처리할 사용자를 선택하세요."
			})
			return false;
		}

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			width : 400,
			msg : "선택한 사용자를 퇴사처리 하시겠습니까?"
		}, function() {
			// 확인
			if (this.key == "ok") {
				var params = $(document).getListParams();
				//console.log(params);
				//console.log($("input[name=oid]").checks().val());;
				var arr = new Array();
				$("input[name=oid]:checked").each(function(){
					var val = $(this).val();
					console.log(val);
					arr.push(val);

				})
				params.list = arr;
				
				$(document).ajaxCallServer(url, params, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						width : 400,
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

	changePasswordAction : function(obj) {
		var lengths = $(obj).data("lengths");
		var dialogs = $(document).setOpen();
		var url = this.changePasswordActionUrl;
		$password = $("input[name=password]");
		$repassword = $("input[name=repassword]");

		var regExp = /^(?=(.*\d){2})/;
		var regExp2 = /^(?=(.*[a-zA-Z]){2})/;
		var regExp3 = /^(?=(.*[!%&'()._:;]){2})/;
		// var regExp = /^[0-9]{2,}$/;

		if (!regExp.exec($("input[name=password]").val())) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "숫자 2개이상 입력하세요"
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$password.focus();
				}
			})
			return false;
		}

		if (!regExp2.exec($("input[name=password]").val())) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "문자 2개이상 입력하세요"
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$password.focus();
				}
			})
			return false;
		}

		if (!regExp3.exec($("input[name=password]").val())) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "특수문자[!%&'()._:;] 2개이상 입력하세요"
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$password.focus();
				}
			})
			return false;
		}

		if ($password.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "변경할 비밀번호를 입력하세요"
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$password.focus();
				}
			})
			return false;
		}

		if ($repassword.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "비밀번호 확인을 입력하세요"
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$repassword.focus();
				}
			})
			return false;
		}

		if ($password.val() != $repassword.val()) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "변경 비밀번호 값이 일치 하지 않습니다."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$password.val("");
					$repassword.val("");
					$password.focus();
				}
			})
			return false;
		}

		if ($password.val().length < 8 || $repassword.val().length < 8) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "비밀번호는 최소 8자리 이상이어야 합니다.",
				width : 350
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$password.focus();
				}
			})
			return false;
		}

		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "비밀번호를 변경 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 등록 진행
				// 일반 문서 값
				var params = $(document).getFormParams();
				// 관련 부품
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

	closeUser : function() {
		self.close();
	},

	modifyUser : function(obj) {
		$oid = $(obj).data("oid");
		$(document).onLayer();
		document.location.href = this.modifyUserUrl + "?oid=" + $oid
				+ "&popup=true";
	},

	listUser : function() {
		$(document).onLayer();
		document.location.href = this.listUserUrl;
	}
}

$(document).ready(function() {

	$("#resignListUserBtn").click(function() {
		orgs.resignListUserAction();
	})

//	$("#changePasswordBtn").click(function() {
//		orgs.changePasswordAction();
//	})

	$("#closeUserBtn").click(function() {
		orgs.closeUser();
	})

	$("#modifyUserBtn").click(function() {
		orgs.modifyUser(this);
	})

	$("#listUserBtn").click(function() {
		orgs.listUser();
	})
})