S<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String base64 = (String) request.getAttribute("base64");
%>
<table class="button-table">
	<tr>
		<td class="right">
			<input type="button" value="닫기" title="닫기" onclick="self.close();">
		</td>
	</tr>
</table>
<img src="<%=base64%>" id="thumbnail">

<script type="text/javascript">
	function imageResize() {
		let width = document.getElementById("thumbnail").width;
		let height = document.getElementById("thumbnail").height;
		window.resizeTo(width + 50, height + 140);

		let left = (screen.width - width) / 2;
		let top = (screen.height - height) / 3;
		window.moveTo(left, top);
	}

	document.addEventListener("DOMContentLoaded", function() {
		imageResize();
	});
</script>