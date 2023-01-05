<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String oid = (String) request.getParameter("oid");
%>
<td valign="top" class="tree_td"><script type="text/javascript">
	$(document).ready(function() {
		var dialogs = $(document).setOpen();
		$("#tree").fancytree({
			collapse : function() {
				$(document).setHTML();
			},
			
			expand : function() {
// 				$(document).setHTML();
			},

			dblclick : function(e, data) {

			},
			
			init : function(e, data) {
				$(document).setHTML();
			},
			click : function(e, data) {
				if(data.targetType != "expander") {
					$(document).onLayer();
					var oid = data.node.data.id;
					var url;
					if(oid.indexOf("Task") > -1) {
						url = "/Windchill/plm/template/viewTemplateTask?oid=" + oid + "&popup=true";
					} else if(oid.indexOf("Template") > -1) {
						url = "/Windchill/plm/template/viewTemplate?oid=" + oid + "&popup=true";
					}
					document.location.href = url;
				}
			},
			persist : {
				store : "local"
			},
 			focusOnSelect : false,
			source : $.ajax({
				url : "/Windchill/plm/template/getTemplateTaskTree?oid=<%=oid %>",
				type : "POST"
			})
		})

		var h = $("#container_td").height();
		$("ul.fancytree-container").css("height", h - 9);

		$.contextMenu({
			selector : "#tree span.fancytree-title",
			items : {
				"editTask" : {
					name : "태스크 편집",
					icon : "edit"
				}
			},
			callback : function(itemKey, opt) {
				var node = $.ui.fancytree.getNode(opt.$trigger);
				if(itemKey == "editTask") {
					var url = "/Windchill/plm/template/openTemplateTaskEditor?oid=<%=oid %>&popup=true";
					var opt = "scrollbars=yes, resizable=yes, fullscreen=yes";
					$(document).openURLViewOpt(url, screen.width, screen.height, opt);
				}
			}
		});
	})
</script>
	<div id="tree"></div>
</td>