<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String root = (String) request.getParameter("root");
	String context = (String) request.getParameter("context");
%>

<td valign="top">
<script type="text/javascript">
$(document).ready(function() {
	$("#closeTreeBtn").click(function() {
		self.close();
	})
	
	var root = encodeURIComponent("<%=root %>");
	
	$("#tree").fancytree({
		select : function(e, data) {
			
		},
		
		dblclick : function(e, data) {
			var loc = data.node.data.location;
			$(opener.document).find("#locationStr").text(loc);
			$(opener.document).find("#location").val(loc);
			opener.parent.setNumber(loc);
			self.close();
		},
		
		click : function(e, data) {
			
		},
		
		source : $.ajax({
			url : "/Windchill/plm/common/getFolder?root=" + root + "&context=<%=context %>",
			type : "POST"
			
		})
	})
	
	$.fn.selectFolder = function() {
		var node = $("#tree").fancytree("getActiveNode");
		var dialogs = $(document).setOpen();
		
		if(node) {
			var loc = node.data.location;
			$(opener.document).find("#locationStr").text(loc);
			$(opener.document).find("#location").val(loc);
			opener.parent.setNumber(loc);
			self.close();
		} else {
			dialogs.alert({
				theme : "alert",
				title : "경고",
				msg : "폴더를 선택하세요."
			})
			return false;
		}
	}
	
	$("#selectBtn").click(function() {
		$(document).selectFolder();
	})
})
</script>
	<div class="header_title">
		<i class="axi axi-subtitles"></i><span>폴더 선택</span>
	</div>
	<div class="right">
		<input type="button" value="선택" class="redBtn" id="selectBtn" title="선택">
		<input type="button" value="닫기" id="closeTreeBtn" title="닫기">
	</div>
	<div class="clear5"></div>
	<div id="tree" oncontextmenu="return false;"></div>
</td>