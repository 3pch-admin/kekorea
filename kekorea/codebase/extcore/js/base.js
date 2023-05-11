/**
 * 메소드 호출 URL 생성 
 * CONTEXT 값을 제외한 URL 주소
 */
function getCallUrl(url) {
	return "/Windchill/plm" + url;
}

/**
 * AJAX 호출 메소드
 */
function call(url, params, callBack, methodType) {

	if (methodType == null) {
		methodType = "POST";
	}

	if (params == null) {
		params = new Object();
	}

	params = JSON.stringify(params);
	$.ajax({
		type: methodType,
		url: url,
		dataType: "JSON",
		crossDomain: true,
		data: params,
		async: true,
		contentType: "application/json; charset=UTF-8",
		beforeSend: function() {
		},
		success: function(res) {
			callBack(res);
		},
		complete: function(res) {

		},
		error: function(res) {
			const status = res.status;
			if (status == 405) {
				alert("에러코드 : " + status + ", 컨트롤러 해당 메소드를 지원 하는지 확인 !! (EX : POST, GET, PUT, DELETE 방식)")
			} else if (status == 404) {
				alert("에러코드 : " + status + ", 호출 URL : " + url + ", 존재하지 않는 호출 주소 !!");
			}

			if (opener !== undefined) {
				closeLayer();
			}
		}
	})
}

/**
 * 팝업창
 */
function popup(url, width, height) {
	if (width === undefined) {
		width = screen.availWidth;
	}

	if (height === undefined) {
		height = screen.availHeight;
	}

	let popW = width;
	let popH = height;
	let left = (screen.width - popW) / 2;
	let top = (screen.height - popH) / 2;
	let panel = window.open(url, "", "top=" + top + ", left=" + left + ", height=" + popH + ", width=" + popW);
	return panel;
}


/**
 * 등록 페이지 FORM PARAMETER 가져오기
 */
function form(params, table) {
	if (params === null) {
		params = new Object();
	}

	let input = $("." + table + " input[type=text]");
	$.each(input, function(idx) {
		let key = input.eq(idx).attr("name");
		if (key === undefined) {
			return true;
		}
		let value = input.eq(idx).val();
		params[key] = value;
	})

	let select = $("." + table + " select");
	$.each(select, function(idx) {
		let key = select.eq(idx).attr("name");
		if (key === undefined) {
			return true;
		}
		let value = select.eq(idx).val();
		params[key] = value;
	})

	let radio = $("." + table + " input[type=radio]");
	$.each(radio, function(idx) {
		if (radio.eq(idx).prop("checked")) {
			let key = radio.eq(idx).attr("name");
			if (key === undefined) {
				return true;
			}
			let value = radio.eq(idx).val();
			params[key] = value;
		}
	})

	let textarea = $("." + table + " textarea");
	$.each(textarea, function(idx) {
		let key = textarea.eq(idx).attr("name");
		if (key === undefined) {
			return true;
		}
		let value = textarea.eq(idx).val();
		params[key] = value;
	})

	let checkbox = $("." + table + " input[type=checkbox");
	$.each(checkbox, function(idx) {
		if (checkbox.eq(idx).prop("checked")) {
			var key = checkbox.eq(idx).attr("name");
			var value = Boolean(checkbox.eq(idx).val());
			params[key] = value;
		}
	})
	return params;
}

/**
 * BIND DATE 달력
 */
function date(name) {
	$("input[name=" + name + "]").bindDate();
}

/**
 * BIND PRE TO AFTER DATE
 */
function rangeDate(name, startName) {
	let config = {
		align: "left",
		valign: "top",
		buttonText: "확인",
		startTargetID: startName,
		customPos: {
			top: 28,
			left: 25
		},
	}
	$("input[name=" + name + "]").bindTwinDate(config);
}


// 태그 NAME이 여러개인 것을 배열 객체로 리턴 해주는 함수
function toArray(name) {
	let array = new Array();
	let list = document.getElementsByName(name);
	for (let i = 0; i < list.length; i++) {
		array.push(list[i].value);
	}
	return array;
}

// auigrid 값 체크..
function isNull(value) {
	if (value === undefined || value === null || value === "") {
		return true;
	}
	return false;
}


// axisj user 검색 바인딩 공용 
function finderUser(id) {
	axdom("#" + id).bindSelector({
		reserveKeys: {
			options: "list",
			optionValue: "oid",
			optionText: "name"
		},
		optionPrintLength: "all",
		onsearch: function(id, obj, callBack) {
			const value = document.getElementById(id).value;
			const params = new Object();
			const url = getCallUrl("/org/keyValue");
			params.value = value;
			params.obj = obj;
			call(url, params, function(data) {
				callBack({
					options: data.list
				})
			})
		},
		onchange: function() {
			const id = this.targetID;
			const value = this.selectedOption.oid
			document.getElementById(id + "Oid").value = value;
		},
		finder: {
			onclick: function() { // {Function} - 파인더 버튼 클릭 이벤트 콜백함수 (optional)
				const multi = document.getElementById(id).dataset.multi;
				const url = getCallUrl("/org/popup?multi=" + multi + "&openerId=" + id);
				popup(url, 1300, 600);
			}
		},
	})
}

// axisj 날짜 범위 검색 바인딩 공용
function twindate(endIdPrefix) {
	axdom("#" + endIdPrefix + "To").bindTwinDate({
		startTargetID: endIdPrefix + "From",
		align: "left",
		valign: "top",
		buttonText: "확인",
		customPos: {
			top: 28,
			left: 25
		},
	})
}

// axisj select 박스 바인딩
function selectbox(id) {
	axdom("#" + id).bindSelect();
}

// 선택한 사용자 세팅
function inputUser(openerId, data) {
	const input = opener.document.getElementById(openerId);
	const el = opener.document.getElementById(openerId + "Oid");
	const item = data.item;
	input.value = item.name;
	el.value = item.woid;
}

// 결재 라인 세팅
function toRegister(params, rows) {
	const agrees = new Array();
	const approvals = new Array();
	const receives = new Array();

	for (let i = 0; i < rows.length; i++) {
		const type = rows[i].type;
		if (type === "검토") {
			agrees.push(rows[i]);
		} else if (type === "결재") {
			approvals.push(rows[i]);
		} else if (type === "수신") {
			receives.push(rows[i]);
		}
	}
	params.agreeRows = agrees;
	params.approvalRows = approvals;
	params.receiveRows = receives;
}

// 사용자 정보 삭제
function clearUser(target) {
	document.getElementById(target).value = "";
	document.getElementById(target + "Oid").value = "";
}

// 범위 기간 달력 삭제
function clearFromTo(from, to) {
	document.getElementById(from).value = "";
	document.getElementById(to).value = "";
}

