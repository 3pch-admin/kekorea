<%@page import="e3ps.project.output.dto.OutputDTO"%>
<%@page import="e3ps.project.output.service.OutputHelper"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%
OutputDTO dto = (OutputDTO) request.getAttribute("dto");
String mode = (String) request.getAttribute("mode");
String title = "";
if ("modify".equals(mode)) {
	title = "수정";
} else if ("revise".equals(mode)) {
	title = "개정";
}
%>
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid() %>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				산출물
				<%=title%>
			</div>
		</td>
		<td class="right">
			<input type="button" value="<%=title%>" title="<%=title%>" onclick="<%=mode%>();">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>

<table class="create-table">
	<colgroup>
		<col width="150">
		<col width="600">
		<col width="150">
		<col width="600">
	</colgroup>
	<tr>
		<th class="req lb">저장위치</th>
		<td class="indent5">
			<span id="loc"><%=dto.getLocation()%></span>
			<input type="button" value="폴더선택" title="폴더선택" class="blue" onclick="folder();">
		</td>
		<th>진행율</th>
		<td class="indent5">
			<input type="number" name="progress" id="progress" class="width-300" value="0" maxlength="3">
		</td>
	</tr>
	<tr>
		<th class="req lb">산출물 제목</th>
		<td class="indent5">
			<input type="text" name="name" id="name" class="width-300" value="<%=dto.getName() %>">
		</td>
		<th class="req">산출물 번호</th>
		<td class="indent5">
			<input type="text" name="number" id="number" class="width-300" value="<%=dto.getNumber() %>" readonly="readonly">
		</td>
	</tr>
	<tr>
		<th class="req lb">도번</th>
		<td colspan="3">
			<jsp:include page="/extcore/jsp/common/numberRule-include.jsp">
				<jsp:param value="<%=dto.getOid() %>" name="oid" />
				<jsp:param value="update" name="mode" />
			</jsp:include>
		</td>
	</tr>
	<tr>
		<th class="req lb">KEK 작번</th>
		<td colspan="5">
			<jsp:include page="/extcore/jsp/common/project-include.jsp">
				<jsp:param value="<%=dto.getOid() %>" name="oid" />
				<jsp:param value="update" name="mode" />
			</jsp:include>
		</td>
	</tr>
	<tr>
		<th class="lb">내용</th>
		<td class="indent5" colspan="3">
			<textarea name="description" id="description" rows="6" readonly="readonly"><%=dto.getDescription() %></textarea>
		</td>
	</tr>
	<tr>
		<th class="lb req">첨부파일</th>
		<td class="indent5" colspan="3">
			<jsp:include page="/extcore/jsp/common/attach-primary.jsp">
				<jsp:param value="<%=dto.getOid() %>" name="oid" />
			</jsp:include>
		</td>
	</tr>
	<tr>
		<th class="req lb">결재</th>
		<td colspan="3">
			<jsp:include page="/extcore/jsp/common/approval-register.jsp">
				<jsp:param value="<%=dto.getOid() %>" name="oid" />
				<jsp:param value="update" name="mode" />
			</jsp:include>
		</td>
	</tr>
</table>
<script type="text/javascript">
	function folder() {
		const location = decodeURIComponent("/Default/프로젝트");
		const url = getCallUrl("/folder?location=" + location + "&container=product&method=setNumber&multi=false");
		popup(url, 500, 600);
	}
	
	function setNumber(item) {
		const url = getCallUrl("/doc/setNumber");
		const params = new Object();
		params.loc = item.location;
		call(url, params, function(data) {
			document.getElementById("loc").innerHTML = item.location;
			document.getElementById("location").value = item.location;
			document.getElementById("number").value = data.number;
		})
	}

	function <%=mode%>s() {
		const params = new Object();
		const url = getCallUrl("/output/<%=mode%>");
		const name = document.getElementById("name");
		const number = document.getElementById("number").value;
		const progress = document.getElementById("progress").value;
		const description = document.getElementById("description").value;
		const addRows9 = AUIGrid.getGridData(myGridID9);
		const addRows11 = AUIGrid.getGridData(myGridID11);
		const addRows8 = AUIGrid.getGridData(myGridID8);
		const location = document.getElementById("location").value;
		const oid = document.getElementById("oid").value;
		const primarys = toArray("primarys");


		if (location === "/Default/프로젝트") {
			alert("산출물 저장 위치를 선택하세요.");
			folder();
			return false;
		}

		if (isNull(name.value)) {
			alert("산출물 제목을 입력하세요.");
			name.focus();
			return false;
		}
		
// 		if(addRows11.length === 0) {
// 			alert("도번을 추가하세요.");
// 			insert11();
// 			return false;
// 		}

		if (addRows9.length === 0) {
			alert("하나 이상의 작번이 추가되어야 합니다.");
			insert9();
			return false;
		}

		if (primarys.length === 0) {
			alert("첨부파일을 선택하세요.");
			return false;
		}

		if (addRows8.length === 0) {
			alert("결재선을 지정하세요.");
			_register();
			return false;
		}

		if (!confirm("<%=title%> 하시겠습니까?")) {
			return false;
		}

		params.name = name.value;
		params.oid = oid;
		params.number = number;
		params.description = description;
		params.progress = Number(progress);
		params.addRows9 = addRows9
		params.addRows11 = addRows11;
		params.primarys = primarys;
		params.location = location;
		params.self = JSON.parse(isSelf);
		toRegister(params, addRows8);
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.document.location.reload();
				self.close();
			} else {
				closeLayer();
			}
		})
	}

	document.addEventListener("DOMContentLoaded", function() {
		createAUIGrid11(columns11);
		createAUIGrid9(columns9);
		createAUIGrid8(columns8);
		AUIGrid.resize(myGridID11);
		AUIGrid.resize(myGridID9);
		AUIGrid.resize(myGridID8);
		document.getElementById("name").focus();
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID11);
		AUIGrid.resize(myGridID9);
		AUIGrid.resize(myGridID8);
	});
</script>