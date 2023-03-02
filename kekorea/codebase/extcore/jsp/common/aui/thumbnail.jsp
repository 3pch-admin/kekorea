<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String base64 = (String) request.getAttribute("base64");
%>
<table class="btn_table">
	<tr>
		<td class="right">
			<input type="button" value="닫기" class="blueBtn" id="closeBtn" title="닫기">
		</td>
	</tr>
</table>
<img src="<%=base64%>" id="thumbnail">

<script type="text/javascript">
	$(function() {

		$("#closeBtn").click(function() {
			self.close();
		})

		let width = $("#thumbnail").width();
		let height = $("#thumbnail").height();
		window.resizeTo(width + 50, height + 140);

		let left = (screen.width - width) / 2;
		let top = (screen.height - height) / 3;
		window.moveTo(left, top);
	})
</script>