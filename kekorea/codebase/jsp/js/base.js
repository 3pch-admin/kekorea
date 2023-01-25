let context = "/Windchill/plm";

/**
 * 메소드 호출 URL 생성 
 * CONTEXT 값을 제외한 URL 주소
 */
function getCallUrl(url) {
	return context + url;
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
		success: function(res) {
			callBack(res);
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
function check(name) {
	$("input:checkbox[name=" + name + "]").checks();
}

/**
 * 팝업창
 */
function popup(url, width, height) {
	var popW = width;
	var popH = height;
	var left = (screen.width - popW) / 2;
	window.open(url, "", "top=" + top + ", left=" + left + ", height=" + popH + ", width=" + popW);
}
