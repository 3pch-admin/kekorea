/**
 * 관리자 페이지
 */

var admins = {
	createQnAUrl : "/Windchill/plm/common/createQnA",

	createQnAActionUrl : "/Windchill/plm/common/createQnAAction",

	initPasswordUrl : "/Windchill/plm/org/initPasswordAction",
	
	createCodesActionUrl : "/Windchill/plm/admin/createCodeAction",

	createQnA : function() {
		$(document).onLayer();
		document.location.href = this.createQnAUrl;
	},

	createQnAAction : function() {
		var dialogs = $(document).setOpen();
		var url = this.createQnAActionUrl;

		// 문서 제목
		$name = $("input[name=name]");
		if ($name.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "QNA 제목을 입력하세요."
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
			msg : "QNA를 등록 하시겠습니까?"
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

	initPasswordAction : function() {
		$oid = $("#creatorsOid").val();
		var dialogs = $(document).setOpen();
		if ($oid == undefined) {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "비밀번호 초기화 할 사용자를 선택하세요.",
				width : 350
			})
			return false;
		}

		var url = this.initPasswordUrl;
		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "해당 사용자의 비밀번호를 초기화 하시겠습니까?",
			width : 380
		}, function() {

			if (this.key == "ok") {
				var params = new Object();
				params.oid = $("#creatorsOid").val();
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

	
	changeSet : function() {
		var dialogs = $(document).setOpen();
		var url = "/Windchill/plm/admin/setPasswordAction";
		
		var box = $(document).setNonOpen();
		box.confirm({
			theme : "info",
			title : "확인",
			msg : "비밀번호 세팅을 변경 하시겠습니까?",
			width : 380
		}, function() {

			if (this.key == "ok") {
				var params = $(document).getFormParams();
				console.log(params);
				$(document).ajaxCallServer(url, params, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						msg : "변경 되었습니다."
					}, function() {
						// 버튼 클릭 ok, esc
						if (this.key == "ok" || this.state == "close") {
//							document.location.href = data.url;
							document.location.reload();
						}
					})
				}, true);
			}

			if (this.key == "cancel" || this.state == "close") {
				mask.close();
			}
		})

	},
	
	createCodesAction : function() {
		var dialogs = $(document).setOpen();
		var url = this.createCodesActionUrl;;
		
		/*String poid = (String) param.get("poid");
		String name = (String) param.get("name");
		String codeValue = (String) param.get("code");
		String text = (String) param.get("text");
		String codeType = (String) param.get("codeType");
		int depths = (int) param.get("depths");*/
		// 문서 제목
		$name = $("input[name=name]");
		if ($name.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "코드 이름을 입력하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$name.focus();
				}
			})
			return false;
		}
		
		$code= $("input[name=code]");
		if ($code.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "코드명을 입력하세요."
			}, function() {
				if (this.key == "ok" || this.state == "close") {
					$name.focus();
				}
			})
			return false;
		}
		
		$description = $("input[name=description]");
		if ($description.val() == "") {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "코드 설명을 입력하세요."
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
			msg : "코드를 등록 하시겠습니까?"
		}, function() {

			if (this.key == "ok") {
				// 등록 진행
				// 일반 문서 값
				var params = $(document).getFormParams();
				params.depths =0;
				params.text = $('#description').val();
				params.name = $('#name').val();
				params.code = $('#code').val();
				params.codeType = $('#codeType').val();
				$(document).ajaxCallServer(url, params, function(data) {
					dialogs.alert({
						theme : "alert",
						title : "결과",
						msg : "코드 생성을 완료하였습니다."
					}, function() {
						// 버튼 클릭 ok, esc
						if (this.key == "ok" || this.state == "close") {
							document.location.href = "/Windchill/plm/admin/manageCode";
						}
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
	$("#createQnABtn").click(function() {
		admins.createQnA();
	})

	$("#createQnAActionBtn").click(function() {
		admins.createQnAAction();
	})

	$("#initPasswordBtn").click(function() {
		admins.initPasswordAction();
	})

	$("#changeSetBtn").click(function(e) {
		admins.changeSet();
	})
	
	$("#createCodesBtn").click(function() {
		admins.createCodesAction();
	});

	// $("#createCodeBtn").click(function() {
	// admins.createCodeAction(this);
	// })
})