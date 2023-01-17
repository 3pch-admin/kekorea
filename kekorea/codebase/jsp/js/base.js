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
function call(url, params, callBack) {
	if (params == null) {
		params = new Object();
	}

	params = JSON.stringify(params);
	$.ajax({
		type: "POST",
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