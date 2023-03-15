/**
 * AUIGrid 에서 사용되는 공통 함수들
 */

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

/**
 * 그리드 컬럼 정보 삭제
 */
function resetColumnLayout(storageID) {
	if (typeof (Storage) != "undefined") { // Check browser support
		localStorage.removeItem(storageID);
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
	let selectedId = ui.item.prop("id");

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
			let kids = ui.item.children();
			let dataField = kids.attr("data"); // data 속성에서 dataField 얻기
			if (typeof dataField != "undefined") {
				let checked = kids.hasClass("ui-icon-check");
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
	let arr = [];
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