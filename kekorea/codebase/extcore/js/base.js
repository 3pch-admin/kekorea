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
	if (value === undefined) {
		return true;
	}
	return false;
}


/**
 * 범위 달력
 */
function fromToCalendar(fromId, iconClass) {
	$("#" + fromId).add("." + iconClass).daterangepicker({
		locale: {
			"format": "YYYY-MM-DD",
			"applyLabel": "적용",
			"cancelLabel": "취소",
			"daysOfWeek": ["월", "화", "수", "목", "금", "토", "일"],
			"monthNames": ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"],
			"customRangeLabel": "날짜 선택"
		},
		ranges: {
			"오늘": [moment(), moment()],
			"어제": [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
			'7일 전': [moment().subtract(6, 'days'), moment()],
			'30일 전': [moment().subtract(29, 'days'), moment()],
			'이번달(시작~끝)': [moment().startOf('month'), moment().endOf('month')],
			'지난달(시작~끝)': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')],
		},
		autoUpdateInput: false
	});

	$("#" + fromId).add("." + iconClass).on("apply.daterangepicker", function(ev, picker) {
		document.getElementById(fromId).value = picker.startDate.format("YYYY-MM-DD") + " ~ " + picker.endDate.format("YYYY-MM-DD");
		document.getElementById(fromId + "From").value = picker.startDate.format("YYYY-MM-DD");
		document.getElementById(fromId + "To").value = picker.endDate.format("YYYY-MM-DD");
	})
}

/**
 * 범위 달력 값 삭제
 */
function fromToDelete(iconClass) {
	$("." + iconClass).click(function() {
		const fromId = $("." + iconClass).data("target");
		document.getElementById(fromId).value = "";
		document.getElementById(fromId + "From").value = "";
		document.getElementById(fromId + "To").value = "";
	})
}