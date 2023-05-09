<%@page import="e3ps.project.issue.beans.IssueDTO"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="java.util.Map"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
IssueDTO dto = (IssueDTO) request.getAttribute("dto");
%>
<%@include file="/extcore/jsp/common/aui/auigrid.jsp"%>
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				특이사항 수정
			</div>
		</td>
		<td class="right">
			<input type="button" value="수정" title="수정" onclick="modify();">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>

<table class="create-table">
	<colgroup>
		<col width="130">
		<col width="*">
	</colgroup>
	<tr>
		<th class="req lb">특이사항 제목</th>
		<td class="indent5">
			<input type="text" name="name" id="name" class="width-500" value="<%=dto.getName()%>">
		</td>
	</tr>
	<tr>
		<th class="req lb">KEK 작번</th>
		<td>
			<jsp:include page="/extcore/jsp/common/project-include.jsp">
				<jsp:param value="<%=dto.getOid() %>" name="oid" />
				<jsp:param value="update" name="mode" />
			</jsp:include>
		</td>
	</tr>
	<tr>
		<th class="req lb">내용</th>
		<td class="indent5">
			<textarea name="description" id="description" rows="10"><%=dto.getContent() %></textarea>
		</td>
	</tr>
	<tr>
		<th class="lb">첨부파일</th>
		<td class="indent5">
			<jsp:include page="/extcore/jsp/common/attach-secondary.jsp">
				<jsp:param value="<%=dto.getOid() %>" name="oid" />
			</jsp:include>
		</td>
	</tr>
</table>
<script type="text/javascript">
	function modify() {
		const params = new Object();
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/issue/modify");
		const name = document.getElementById("name");
		const description = document.getElementById("description");
		const addRows9 = AUIGrid.getGridData(myGridID9);

		params.name = name.value;
		params.description = description.value;
		params.oid = oid;
		params.addRows9 = addRows9;
		params.secondarys = toArray("secondarys");
		if (isNull(params.name)) {
			alert("특이사항 제목 값은 공백을 입력 할 수 없습니다.");
			name.focus();
			return false;
		}
		if (isNull(params.description)) {
			alert("내용 값은 공백을 입력 할 수 없습니다.");
			description.focus();
			return false;
		}
		if (!confirm("수정 하시겠습니까?")) {
			return false;
		}
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
		createAUIGrid9(columns9);
		AUIGrid.resize(myGridID9);
		document.getElementById("name").focus();
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(myGridID9);
	});
</script>