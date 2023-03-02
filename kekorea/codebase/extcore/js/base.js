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
		async: false,
		contentType: "application/json; charset=UTF-8",
		success: function(res) {
			callBack(res);
		},
		complete: function() {
		},
		error: function(res) {
			let status = res.status;
			if (status == 405) {
				alert("에러코드 : " + status + ", 컨트롤러 해당 메소드를 지원 하는지 확인 !! (EX : POST, GET, PUT, DELETE 방식)")
			} else if (status == 404) {
				alert("에러코드 : " + status + ", 호출 URL : " + url + ", 존재하지 않는 호출 주소 !!");
			}
		}
	})
}

/**
 * SELECT 박스 바인딩
 * 변수 SELECT 박스 NAME 값
 */
function select(name) {
	$("select[name=" + name + "]").bindSelect();
}

/**
 * RADIO 박스 바인딩
 * 변수 RADIO 박스 NAME 값
 */
function radio(name) {
	$("input:radio[name=" + name + "]").checks();
}

/**
 * CHECKBOX 박스 바인딩
 * 변수 CHECKBOX 박스 NAME 값
 */
function checkBox(name) {
	$("input:checkbox[name=" + name + "]").checks();
}

/**
 * SELECTBOX 박스 바인딩
 * 변수 SELECTBOX 박스 NAME 값
 */
function selectBox(name) {
	$("select[name=" + name + "]").bindSelect();
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
	const config = {
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

/**
 * AUIGrid 칼럼 저장 
 */
function saveColumnLayout(storageID) {
	let columns = AUIGrid.getColumnLayout(myGridID);
	let columnJson = JSON.stringify(columns);
	localStorage.setItem(storageID, columnJson);
	alert("현재 그리드의 상태가 보관되었습니다.\r\n브라우저를 종료하거나 F5 로 갱신했을 때 현재 상태로 그리드가 출력됩니다.");
}

/**
 * AUIGrid 컬럼 가져오기
 */
function loadColumnLayout(storageID) {
	let columnLayout = null;
	let column = getLocalStorageValue(storageID);
	if (column && typeof column != "undefined") {
		columnLayout = JSON.parse(column);
		//감춰진 칼럼에 따라 데모 상에 보이는 체크박스 동기화 시킴.
		//		syncCheckbox(columnLayout);
	}

	if (!columnLayout) {
		columnLayout = _layout();
	}
	return columnLayout;
};

/**
 * 로컬 스토리지 가져오기
 */
function getLocalStorageValue(storageID) {
	if (typeof (Storage) != "undefined") {
		return localStorage.getItem(storageID);
	} else {
		alert("localStorage 를 지원하지 않는 브라우저입니다.");
	}
};