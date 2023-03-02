<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<input type="button" value="부품 추가" title="부품 추가" id="addParts" data-context="product" data-dbl="true">
<input type="button" value="부품 삭제" title="부품 삭제" id="delParts" class="blueBtn">
<div id="part_grid_wrap" style="height: 100px; border-top: 1px solid #3180c3; margin-top: 5px;"></div>
<script type="text/javascript">
	let partGridID;
	const part_columns = [ {
		dataField : "",
		headerText : "DWG_NO",
		width : 200

	}, {

		dataField : "name",
		headerText : "파일이름",
		width : 250

	}, {

		dataField : "name_of_parts",
		headerText : "품명",
		width : 200
	}, {

		dataField : "version",
		headerText : "버전",
		width : 80

	}, {

		dataField : "state",
		headerText : "상태",
		width : 100

	}, {

		dataField : "creator",
		headerText : "작성자",
		width : 100

	}, {

		dataField : "modifier",
		headerText : "수정자",
		width : 100

	}, {

		dataField : "oid",
		headerText : "oid",
		dataType : "string",
		visible : false
	} ];

	const part_props = {
		headerHeight : 30,
		rowHeight : 30,
		showRowNumColumn : false,
		showRowCheckColumn : true, // 체크 박스 출력
		fillColumnSizeMode : true
	}

	$(function() {
		partGridID = AUIGrid.create("#part_grid_wrap", part_columns, part_props);
		
		$("#addParts").click(function() {
			let url = getCallUrl("/document/popup");
			popup(url);
		});
		
		$("#delParts").click(function() {
			
		})
		
	});
	
	$(window).resize(function() {
		AUIGrid.resize(partGridID);
	})
</script>