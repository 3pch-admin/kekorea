<%@page import="e3ps.org.Department"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="java.util.HashMap"%>
<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="e3ps.org.User"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	ArrayList<User> noReglist = (ArrayList<User>) request.getAttribute("noReglist");
	ArrayList<User> reglist = (ArrayList<User>) request.getAttribute("reglist");
	String oid = (String) request.getParameter("oid");
	ReferenceFactory rf = new ReferenceFactory();
	Department dd = (Department) rf.getReference(oid).getObject();
%>
<td valign="top"><script type="text/javascript">
	$(document).ready(function() {

		var d = "<%=oid %>";

		var f = document.forms[0];
		
		$(".left").click(function() {
			var dialogs = $(document).setOpen();
			$options = $(".userSelect option");
			var arr = new Array();
			$.each($options, function(idx) {
				if ($options.eq(idx).prop("selected") == true) {
					value = $options.eq(idx).val();
					arr.push(value);
				}
			})
			
			if (arr == "") {
				dialogs.alert({
					theme : "alert",
					title : "사용자 미선택",
					msg : "<%=dd.getName()%> 부서에서 제외할 사용자를 선택하세요.",
					width : 400
				})
				return false;
			}

			
			var idx = f.noRegList.length;
			for (var i = 0; i < f.regList.length; i++) {
				if (f.regList.options[i].selected == true) {
					f.noRegList.length += 1;
					f.noRegList.options[idx].text = f.regList.options[i].text;
					f.noRegList.options[idx].value = f.regList.options[i].value;
					f.regList.options[i] = null;
					i -= 1;
					idx += 1;
				}
			}
			
			var url = "/Windchill/plm/admin/setDeptAction";
			var params = new Object();
			params.list = arr;
			params.doid = null;
			$(document).ajaxCallServer(url, params, function(data) {
			})
		}).mouseover(function() {
			$(this).attr("title", "오른쪽으로 이동").css("cursor", "pointer");
		})

		$(".right").click(function() {
			var dialogs = $(document).setOpen();
			$options = $(".dutySelect option");
			var arr = new Array();
			$.each($options, function(idx) {
				if ($options.eq(idx).prop("selected") == true) {
					value = $options.eq(idx).val();
					arr.push(value);
				}
			})

			if (arr == "") {
				dialogs.alert({
					theme : "alert",
					title : "사용자 미선택",
					msg : "<%=dd.getName()%> 부서으로 설정할 사용자를 선택하세요.",
					width : 400
				})
				return false;
			}

			var idx = f.regList.length;

			for (var i = 0; i < f.noRegList.length; i++) {
				if (f.noRegList.options[i].selected == true) {
					f.regList.length += 1;
					f.regList.options[idx].text = f.noRegList.options[i].text;
					f.regList.options[idx].value = f.noRegList.options[i].value;
					f.noRegList.options[i] = null;
					i -= 1;
					idx += 1;
				}
			}

			var url = "/Windchill/plm/admin/setDeptAction";
			var params = new Object();
			params.list = arr;
			params.doid = d;
			$(document).ajaxCallServer(url, params, function(data) {
			})
		}).mouseover(function() {
			$(this).attr("title", "왼쪽으로 이동").css("cursor", "pointer");
		})

		$("#openerCloseBtn").click(function() {
			opener.document.location.reload();
			self.close();
		})
	})
</script>
	<div class="header_title">
		<i class="axi axi-subtitles"></i><span class="">부서설정</span>
	</div>

	<div class="clear"></div>

	<table id="duty_table">
		<colgroup>
			<col width="46%">
			<col width="50px">
			<col width="46%">
		</colgroup>
		<tr>
			<th><%=dd.getName()%>&nbsp;(미등록)</th>
			<td>&nbsp;</td>
			<th><%=dd.getName()%>&nbsp;(등록)</th>
		</tr>
		<tr>
			<td><select name="noRegList" multiple="multiple" class="AXSelect dutySelect">
					<%
						for (int i = 0; i < noReglist.size(); i++) {
							User user = (User) noReglist.get(i);
							String oids = user.getPersistInfo().getObjectIdentifier().getStringValue();
							String d = user.getDepartment() != null ? user.getDepartment().getName() : "지정안됨";
					%>
					<option value="<%=oids%>"><%=user.getName()%>&nbsp;(<%=d%>)
					</option>
					<%
						}
					%>
			</select></td>
			<td><img src="/Windchill/jsp/images/bt_next.gif" class="right"><br> <br> <img class="left" src="/Windchill/jsp/images/bt_pre.gif"></td>
			<td><select name="regList" multiple="multiple" class="AXSelect userSelect">
					<%
						for (int i = 0; i < reglist.size(); i++) {
							User user = (User) reglist.get(i);
							String oids = user.getPersistInfo().getObjectIdentifier().getStringValue();
							String d = user.getDepartment() != null ? user.getDepartment().getName() : "지정안됨";
					%>
					<option value="<%=oids%>"><%=user.getName()%>&nbsp;(<%=d%>)
					</option>
					<%
						}
					%>
			</select></td>
		</tr>
	</table>
	<table class="btn_table">
		<tr>
			<td class="center"><input type="button" value="닫기 (C)" title="닫기 (C)" id="openerCloseBtn"></td>
		</tr>
	</table></td>