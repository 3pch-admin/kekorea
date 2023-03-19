<%@page import="e3ps.common.util.ContentUtils"%>
<%@page import="e3ps.epm.keDrawing.dto.KeDrawingDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
KeDrawingDTO dto = (KeDrawingDTO) request.getAttribute("dto");
String[] primarys = (String[]) request.getAttribute("primarys");
%>
<!-- AUIGrid -->
<%@include file="/extcore/include/auigrid.jsp"%>
<!-- hidden -->
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid()%>">
<input type="hidden" name="moid" id="moid" value="<%=dto.getMoid()%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				KE 도면 정보
			</div>
		</td>
		<td class="right">
			<input type="button" value="버전이력" title="버전이력" onclick="history();">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>

<table class="view-table">
	<colgroup>
		<col width="130">
		<col width="600">
		<col width="130">
		<col width="600">
		<col width="300">
	</colgroup>
	<tr>
		<th class="lb">DRAWING TITLE</th>
		<td class="indent5"><%=dto.getName()%></td>
		<th>DWG NO</th>
		<td class="indent5"><%=dto.getKeNumber()%></td>
		<td class="center" rowspan="5">
			<img src="<%=dto.getPreView()%>" style="height: 140px; cursor: pointer;" onclick="preView();" title="클릭시 원본크기로 볼 수 있습니다.">
		</td>
	</tr>
	<tr>
		<th class="lb">LOT NO</th>
		<td class="indent5"><%=dto.getLotNo()%></td>
		<th>버전</th>
		<td class="indent5"><%=dto.getVersion()%></td>
	</tr>
	<tr>
		<th class="lb">작성자</th>
		<td class="indent5"><%=dto.getCreator()%></td>
		<th>작성일</th>
		<td class="indent5"><%=dto.getCreatedDate_txt()%></td>
	</tr>
	<tr>
		<th class="lb">수정자</th>
		<td class="indent5"><%=dto.getModifier()%></td>
		<th>수정일</th>
		<td class="indent5"><%=dto.getModifiedDate_txt()%></td>
	</tr>
	<tr>
		<th class="lb">도면파일</th>
		<td class="indent5" colspan="3">
			<a href="<%=primarys[5]%>">
				<span style="position: relative; bottom: 2px;"><%=primarys[2]%></span>
				<img src="<%=primarys[4]%>" style="position: relative; top: 1px;">
			</a>
		</td>
	</tr>
</table>


<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				관련 도면 일람표
			</div>
		</td>
	</tr>
</table>

<jsp:include page="/extcore/include/workOrder-include.jsp">
	<jsp:param value="<%=dto.getOid()%>" name="oid" />
	<jsp:param value="keDrawing" name="obj" />
	<jsp:param value="330" name="height" />
</jsp:include>


<script type="text/javascript">
	function history() {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/keDrawing/history?oid=" + oid);
		popup(url, 1100, 500);
	}

	function preView() {
		const oid = document.getElementById("oid").value;
		const url = getCallUrl("/aui/thumbnail?oid=" + oid);
		popup(url);
	}

	document.addEventListener("DOMContentLoaded", function() {
		// DOM이 로드된 후 실행할 코드 작성
		_createAUIGrid(_columns);
		AUIGrid.resize(_myGridID);
	});

	window.addEventListener("resize", function() {
		AUIGrid.resize(_myGridID);
	});
</script>