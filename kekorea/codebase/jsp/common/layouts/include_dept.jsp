<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<td valign="top" class="tree_td">
	<script type="text/javascript">
	$(document).ready(function() {
		$("#deptView").fancytree({
			dblclick : function(e, data) {
				$(document).getColumn();
			},
			init : function(e, data) {
// 				$(document).setHTML();
			},
			click : function(e, data) {
				var targetType = data.targetType ;
				if(targetType != "expander") {
					var oid = data.node.data.id;
					var name = data.node.title;
					$("#deptName").text(name);
					$("input[name=deptOid]").val(oid);
					$(document).getColumn();
				}
			},
			icon : function(e, data) {
				if(data.node.isFolder()) {
					return "my-group-icon-class";
				}
			},
			source : $.ajax({
				url : "/Windchill/plm/org/getDeptTree",
				type : "POST"
			})
		});
		var h = $("#container_td").height();
		$("ul.fancytree-container").css("height", h - 9);
	})
	</script>
	<div id="deptView"></div>
</td>