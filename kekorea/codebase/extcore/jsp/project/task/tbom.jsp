<%@page import="e3ps.project.service.ProjectHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	boolean isAdmin = Boolean.parseBoolean(request.getParameter("isAdmin"));
	String poid = (String) request.getParameter("poid");
	String toid = (String) request.getParameter("toid");
%>
<div class="info-header">
	<img src="/Windchill/extcore/images/header.png">
	태스크 T-BOM 정보
</div>

<table class="button-table">
	<tr>
		<td class="left">
			<input type="button" value="T-BOM 등록" title="T-BOM 등록" class="blue" onclick="create();">
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
		headerText : "T-BOM 제목",
		dataType : "string",
		style : "aui-left",
		renderer : {
			type : "LinkRenderer",
			baseUrl : "javascript",
			jsCallback : function(rowIndex, columnIndex, value, item) {
				const oid = item.oid;
				const url = getCallUrl("/tbom/view?oid=" + oid);
				popup(url);
			}
		},
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
		dataField : "secondary",
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
	
	// 등록 후 호출
	function _reload() {
		document.location.reload();
		parent.readyHandler();
	}

	function create() {
		const toid = document.getElementById("oid").value;
		const poid = document.getElementById("poid").value;
		const url = getCallUrl("/meeting/create?toid=" + toid + "&poid=" + poid);
		popup(url);
	}

	function connect() {
		const toid = document.getElementById("oid").value;
		const poid = document.getElementById("poid").value;
		const url = getCallUrl("/tbom/connect?toid=" + toid + "&poid=" + poid);
		popup(url, 1600, 700);
	}

	function _connect(data, toid, poid, callBack) {
		const arr = new Array();
		for (let i = 0; i < data.length; i++) {
			const item = data[i].item;
			arr.push(item.oid);
		}
		const url = getCallUrl("/tbom/connect");
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
			alert("삭제할 T-BOM을 선택하세요.");
			return false;
		}

		for (let i = 0; i < checkedItems.length; i++) {
			const item = checkedItems[i].item;
			const oid = item.ooid;
			arr.push(oid);
		}
		const url = getCallUrl("/output/disconnect");
		const params = new Object();
		params.arr = arr;
		if (!confirm("삭제 하시겠습니까?\nT-BOM 태스크의 연결관계만 삭제 되어집니다.")) {
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