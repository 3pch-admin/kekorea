<%@page import="e3ps.common.util.ContentUtils"%>
<%@page import="e3ps.epm.keDrawing.dto.KeDrawingDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				도면일람표 도면 정보
			</div>
		</td>
		<td class="right">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>

<div id="tabs">
	<ul>
		<li>
			<a href="/Windchill/plm/workOrder/view?oid=${oid}">기본정보</a>
		</li>
		<li>
			<a href="/Windchill/plm/workOrder/data?oid=${oid }">도면일람표</a>
		</li>
	</ul>
</div>
<script type="text/javascript">
	document.addEventListener("DOMContentLoaded", function() {
		$("#tabs").tabs();
	})

</script>