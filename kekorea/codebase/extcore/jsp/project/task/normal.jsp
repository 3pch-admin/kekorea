<%@page import="e3ps.project.service.ProjectHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	boolean isAdmin = Boolean.parseBoolean(request.getParameter("isAdmin"));
	String poid = (String) request.getParameter("poid");
	String toid = (String) request.getParameter("toid");
%>
<div class="info-header">
	<img src="/Windchill/extcore/images/header.png">
	태스크 산출물 정보
</div>

<table class="button-table">
	<tr>
		<td class="left">
			<input type="button" value="산출물 등록" title="산출물 등록" class="blue" onclick="create();">
			<input type="button" value="링크 등록" title="링크 등록" class="orange" onclick="connect();">
			<%
			if (isAdmin) {
			%>
			<input type="button" value="삭제" title="삭제" class="red" onclick="_delete();">
			<%
			}
			%>
		</td>
	</tr>
</table>
<div id="grid_wrap" style="height: 450px; border-top: 1px solid #3180c3;"></div>
<script type="text/javascript">
	let myGridID;
	const columns = [ {
		dataField : "name",
		headerText : "산출물 제목",
		dataType : "string",
		style : "aui-left",
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.ooid;
				const url = getCallUrl("/output/view?oid=" + oid);
				popup(url);
			}
		},
	}, {
		dataField : "version",
		headerText : "버전",
		dataType : "string",
		width : 100,
	}, {
		dataField : "state",
		headerText : "상태",
		dataType : "string",
		width : 100,
	}, {
		dataField : "creator",
		headerText : "작성자",
		dataType : "string",
		width : 100,
	}, {
		dataField : "createdDate_txt",
		headerText : "작성일",
		dataType : "string",
		width : 100,
	}, {
		dataField : "primary",
		headerText : "첨부파일",
		dataType : "string",
		width : 100,
		renderer : {
			type : "TemplateRenderer"
		}
	} ]

	function createAUIGrid(columnLayout) {
		const props = {
			headerHeight : 30,
			showRowCheckColumn : true,
			showRowNumColumn : true,
			rowNumHeaderText : "번호",
			showAutoNoDataMessage : false,
			selectionMode : "multipleCells",
		};
		myGridID = AUIGrid.create("#grid_wrap", columnLayout, props);
		AUIGrid.setGridData(myGridID, <%=ProjectHelper.manager.jsonAuiOutput(poid, toid)%>);
	}

	function create() {
		const toid = document.getElementById("oid").value;
		const poid = document.getElementById("poid").value;
		const url = getCallUrl("/output/create?toid=" + toid + "&poid=" + poid);
		popup(url, 1600, 800);
	}

	function connect() {
		const toid = document.getElementById("oid").value;
		const poid = document.getElementById("poid").value;
		const url = getCallUrl("/output/connect?toid=" + toid + "&poid=" + poid);
		popup(url, 1600, 700);
	}

	function _connect(data, toid, poid, callBack) {
		const arr = new Array();
		for (let i = 0; i < data.length; i++) {
			const item = data[i].item;
			arr.push(item.oid);
		}
		const url = getCallUrl("/output/connect");
		const params = new Object();
		params.arr = arr;
		params.toid = toid;
		params.poid = poid;
		call(url, params, function(res) {
			callBack(res);
		})
	}

	function _delete() {
		const checkedItems = AUIGrid.getCheckedRowItems(myGridID);
		const arr = new Array();
		if (checkedItems.length === 0) {
			alert("삭제할 산출물을 선택하세요.");
			return false;
		}

		for (let i = 0; i < checkedItems.length; i++) {
			const item = checkedItems[i].item;
			const oid = item.oid;
			arr.push(oid);
		}
		const url = getCallUrl("/output/disconnect");
		const params = new Object();
		if (!confirm("삭제 하시겠습니까?\n산출물과 태스크의 연결관계만 삭제 되어집니다.")) {
			return false;
		}
		parent.parent.openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				document.location.reload();
			} else {
				parent.parent.closeLayer();
			}
		});
	}
</script>