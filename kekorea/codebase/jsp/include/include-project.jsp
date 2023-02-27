<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<input type="button" value="작번 추가" id="projectAddBtn" title="작번 추가">
<input type="button" value="작번 삭제" id="projectDeleteBtn" title="박전 삭제" class="redBtn">
<div id="project_grid_wrap" style="height: 150px; border-top: 1px solid #3180c3; margin-top: 5px;"></div>
<script type="text/javascript">
	let projectGridID;
	const project_columns = [ {
		dataField : "projectType_name",
		headerText : "작번유형",
		dataType : "string",
		width : 80
	}, {
		dataField : "customer_name",
		headerText : "거래처",
		dataType : "string",
		width : 120
	}, {
		dataField : "mak_name",
		headerText : "막종",
		dataType : "string",
		width : 120
	}, {
		dataField : "detail_name",
		headerText : "막종상세",
		dataType : "string",
		width : 120
	}, {
		dataField : "kekNumber",
		headerText : "KEK 작번",
		dataType : "string",
		width : 140
	}, {
		dataField : "keNumber",
		headerText : "KE 작번",
		dataType : "string",
		width : 140
	}, {
		dataField : "description",
		headerText : "작업 내용",
		dataType : "string",
		style : "left indent10"
	}, {
		dataField : "oid",
		headerText : "oid",
		dataType : "string",
		visible : false
	} ]

	const project_props = {
		headerHeight : 30,
		rowHeight : 30,
		showRowNumColumn : true,
		rowNumHeaderText : "번호",
		softRemoveRowMode : false,
		showRowCheckColumn : true, // 체크 박스 출력
		fillColumnSizeMode : true
	};
	
	
	function attach(data) {
		for(let i=0; i<data.length; i++) {
			let item = data[i].item;
			let isUnique = AUIGrid.isUniqueValue(projectGridID, "oid", item.oid);
			if(isUnique) {
				AUIGrid.addRow(projectGridID, item, "first");
			}
		}
	}

	$(function() {
		projectGridID = AUIGrid.create("#project_grid_wrap", project_columns, project_props);

		$("#projectAddBtn").click(function() {
			let url = getCallUrl("/project/popup?method=attach");
			popup(url);
		})

		$("#projectDeleteBtn").click(function() {
			let checkedItems = AUIGrid.getCheckedRowItems(projectGridID);
			for (let i = checkedItems.length - 1; i >= 0; i--) {
				let rowIndex = checkedItems[i].rowIndex;
				AUIGrid.removeRow(projectGridID, rowIndex);
			}
		})
	})
	
	$(window).resize(function() {
		AUIGrid.resize(projectGridID);
	})
</script>