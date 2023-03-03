<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div style="margin-top: 3px;">
	<input type="button" value="작번 추가" title="작번 추가" class="blue" onclick="appendData('project', 'append');">
	<input type="button" value="행 삭제" title="행 삭제" class="red" onclick="deleteRow('#_grid_wrap');">
	<div id="_grid_wrap" style="height: 150px; border-top: 1px solid #3180c3; margin: 3px 5px 3px 5px;"></div>
	<script type="text/javascript">
		let _myGridID;
		const _columns = [ {
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
			width : 130
		}, {
			dataField : "keNumber",
			headerText : "KE 작번",
			dataType : "string",
			width : 130
		}, {
			dataField : "description",
			headerText : "작업 내용",
			dataType : "string",
			style : "left indent10",
		} ]

		function _createAUIGrid(columnLayout) {
			const props = {
				headerHeight : 30,
				rowHeight : 30,
				showRowNumColumn : true,
				rowNumHeaderText : "번호",
				softRemoveRowMode : false,
				showRowCheckColumn : true, // 체크 박스 출력
				fillColumnSizeMode : true
			}

			_myGridID = AUIGrid.create("#_grid_wrap", columnLayout, props);
		}

		function append(data) {
			for (let i = 0; i < data.length; i++) {
				let item = data[i].item;
				let isUnique = AUIGrid.isUniqueValue(_myGridID, "oid", item.oid);
				if (isUnique) {
					AUIGrid.addRow(_myGridID, item, "first");
				}
			}
		}
	</script>

</div>