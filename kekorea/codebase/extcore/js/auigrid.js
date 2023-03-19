/**
 * AUIGrid 에서 사용되는 공통 함수들
 */

/**
* AUIGrid 칼럼 저장 
*/
function saveColumnLayout(storageID) {
	const columns = AUIGrid.getColumnLayout(myGridID);
	const columnJson = JSONfn.stringify(columns);
	const psize = document.getElementById("psize").value;
	localStorage.setItem(storageID, columnJson);
	localStorage.setItem(storageID + "_psize", psize);
	alert("현재 그리드의 상태가 보관되었습니다.\r\n브라우저를 종료하거나 F5 로 갱신했을 때 현재 상태로 그리드가 출력됩니다.");
}

/**
 * AUIGrid 컬럼 가져오기
 */
function loadColumnLayout(storageID) {
	let columnLayout = null;
	const column = getLocalStorageValue(storageID);
	if (column && typeof column != "undefined") {
		columnLayout = JSONfn.parse(column);
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
		document.getElementById("psize").value = localStorage.getItem(storageID + "_psize");
		return localStorage.getItem(storageID);
	} else {
		alert("localStorage 를 지원하지 않는 브라우저입니다.");
	}
};

/**
 * 그리드 컬럼 정보 삭제
 */
function resetColumnLayout(storageID) {
	if (typeof (Storage) != "undefined") { // Check browser support
		localStorage.removeItem(storageID);
		localStorage.removeItem("psize");
		alert("저장된 그리드의 상태를 초기화했습니다.\r\n브라우저를 종료하거나 F5 로 갱신했을 때 원래 상태로 출력됩니다.");
	} else {
		alert("localStorage 를 지원하지 않는 브라우저입니다.");
		return;
	}
};

/**
 * AUIGrid 스크롤시 헤더 컨텍스트 감추는 함수
 */
let nowHeaderMenuVisible = false;
let currentDataField;
function hideContextMenu() {
	if (nowHeaderMenuVisible) { // 메뉴 감추기
		$("#headerMenu").menu("destroy");
		$("#headerMenu").hide();
		nowHeaderMenuVisible = false;
	}
};


/**
 * 헤더 컨텍스트 이벤트 핸들러
 */
function auiContextMenuHandler(event) {
	if (event.target == "header") { // 헤더 컨텍스트

		if (nowHeaderMenuVisible) {
			hideContextMenu();
		}

		nowHeaderMenuVisible = true;

		// 컨텍스트 메뉴 생성된 dataField 보관.
		currentDataField = event.dataField;

		if (event.dataField == "id") { // ID 칼럼은 숨기기 못하게 설정
			$("#h_item_4").addClass("ui-state-disabled");
		} else {
			$("#h_item_4").removeClass("ui-state-disabled");
		}

		// 헤더 에서 사용할 메뉴 위젯 구성
		$("#headerMenu").menu({
			select: headerMenuSelectHandler
		});

		$("#headerMenu").css({
			left: event.pageX,
			top: event.pageY
		}).show();
	}
}

/**
 * 헤더 컨텍스트 시작
 */
function headerMenuSelectHandler(event, ui) {
	const selectedId = ui.item.prop("id");

	switch (selectedId) {
		case "h_item_1": // 오름 차순 정렬
			// currentDataField 로 오름차순 정렬 실행
			AUIGrid.setSorting(myGridID, [{ "dataField": currentDataField, "sortType": 1 }]);
			break;
		case "h_item_2": // 내림 차순 정렬
			// currentDataField 로 내림차순 정렬 실행
			AUIGrid.setSorting(myGridID, [{ "dataField": currentDataField, "sortType": -1 }]);
			break;
		case "h_item_3": // 정렬 초기화
			AUIGrid.clearSortingAll(myGridID);
			break;
		case "h_item_4": // 현재 칼럼 숨기기
			AUIGrid.hideColumnByDataField(myGridID, currentDataField);
			$("#h_item_ul span.ui-icon[data=" + currentDataField + "]").removeClass("ui-icon-check")
				.addClass("ui-icon-blank");
			break;
		case "h_item_6": // 모든 칼럼 보이기
			AUIGrid.showAllColumns(myGridID);
			$("#h_item_ul span.ui-icon[data]").addClass("ui-icon-check")
				.removeClass("ui-icon-blank");
			break;
		default: // 헤더 보이기 / 숨기기
			const kids = ui.item.children();
			const dataField = kids.attr("data"); // data 속성에서 dataField 얻기
			if (typeof dataField != "undefined") {
				const checked = kids.hasClass("ui-icon-check");
				if (checked) {
					AUIGrid.hideColumnByDataField(myGridID, dataField);
					kids.removeClass("ui-icon-check")
						.addClass("ui-icon-blank");
				} else {
					AUIGrid.showColumnByDataField(myGridID, dataField);
					kids.addClass("ui-icon-check");
					kids.removeClass("ui-icon-blank");
				}
			}
			break;
	}
}

/** AUIGrid 컬럼 레이아웃을 반영하여 HTML 작성 */
function genColumnHtml(columns) {
	const arr = [];
	for (let i = 0, len = columns.length; i < len; i++) {
		recursiveParse(columns[i]);
	}
	return arr.join('');

	// 재귀함수
	function recursiveParse(column) {
		if (typeof column.children != "undefined") {
			arr.push('<li>' + column.headerText + '<ul>');
			for (let i = 0, l = column.children.length; i < l; i++) {
				recursiveParse(column.children[i]);
			}
			arr.push('</ul></li>');
		} else {
			if (column.dataField == "id") { // ID 칼럼은 숨기기 못하게 설정
				arr.push('<li class="ui-state-disabled"><span class="ui-icon ui-icon-check"/>' + column.headerText + '</li>');
			} else {
				if (typeof column.visible != "undefined" && !column.visible) {
					arr.push('<li><span class="ui-icon ui-icon-blank" data="' + column.dataField + '"/>' + column.headerText + '</li>');
				} else {
					arr.push('<li><span class="ui-icon ui-icon-check" data="' + column.dataField + '"/>' + column.headerText + '</li>');
				}
			}
		}
	};
};

/**
 * LAZY LOAD 공통
 * 리스트 페이지에서 모두 밖으로 제외
 */
let last = false;
function vScrollChangeHandler(event) {
	if (event.position == event.maxPosition) {
		if (!last) {
			requestAdditionalData();
		}
	}
}

function requestAdditionalData() {
	const url = getCallUrl("/aui/appendData");
	const params = new Object();
	const curPage = document.getElementById("curPage").value
	const sessionid = document.getElementById("sessionid").value;
	params.sessionid = sessionid;
	params.start = (curPage * 100);
	params.end = (curPage * 100) + 100;
	AUIGrid.showAjaxLoader(myGridID);
	parent.openLayer();
	call(url, params, function(data) {
		if (data.list.length == 0) {
			last = true;
		} else {
			AUIGrid.appendData(myGridID, data.list);
			document.getElementById("curPage").value = parseInt(curPage) + 1;
		}
		AUIGrid.removeAjaxLoader(myGridID);
		parent.closeLayer();
	})
}

/**
 * 엑셀 익스포트
 */
function exportToExcel(fileName, headerName, sheetName, exceptColumnFields, creator) {

	const date = new Date();
	const year = date.getFullYear();
	const month = (date.getMonth() + 1).toString().padStart(2, '0');
	const day = date.getDate().toString().padStart(2, '0');
	const today = year + "/" + month + "/" + day;

	AUIGrid.exportToXlsx(myGridID, {
		// 저장하기 파일명
		fileName: fileName,
		progressBar: true,
		sheetName: sheetName,
		exceptColumnFields: exceptColumnFields,
		// 헤더 내용
		headers: [{
			text: "", height: 20 // 첫행 빈줄
		}, {
			text: headerName, height: 36, style:
				{ fontSize: 20, textAlign: "center", fontWeight: "bold", background: "#DAD9FF" }
		}, {
			text: "작성자 : " + creator, style: { textAlign: "right", fontWeight: "bold" }
		}, {
			text: "작성일 : " + today, style: { textAlign: "right", fontWeight: "bold" }
		}, {
			text: "", height: 5, style: { background: "#555555" } // 빈줄 색깔 경계 만듬
		}],
		// 푸터 내용
		footers: [{
			text: "", height: 5, style: { background: "#555555" } // 빈줄 색깔 경계 만듬
		}, {
			text: "COPYRIGHT 2023 KEKOREA", height: 24, style:
				{ textAlign: "right", fontWeight: "bold", color: "#ffffff", background: "#222222" }
		}]
	});
}