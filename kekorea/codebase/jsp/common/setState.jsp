<%@page import="wt.epm.EPMDocument"%>
<%@page import="wt.part.WTPart"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="wt.fc.Identified"%>
<%@page import="wt.doc.WTDocumentMaster"%>
<%@page import="wt.vc.Iterated"%>
<%@page import="wt.enterprise.Master"%>
<%@page import="wt.enterprise.RevisionControlled"%>
<%@page import="java.util.ArrayList"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	ArrayList<RevisionControlled> list = (ArrayList<RevisionControlled>) request.getAttribute("list");
%>
<td valign="top"><script type="text/javascript">
	$(document).ready(function() {
		$("#closeBtn").click(function() {
			self.close();
		})

		$(".list_table").tableHeadFixer();

		$.fn.saveAs = function() {
			var dialogs = $(document).setOpen();
			var bool = $(document).isCheck();
			if (!bool) {
				dialogs.alert({
					theme : "alert",
					title : "객체 미선택",
					msg : "문서 제목을 입력하세요."
				}, function() {
					if (this.key == "ok") {
						$name.focus();
					}
				})
				return false;
			}

			var url = "/Windchill/plm/common/saveAsObjectAction";
			var params = new Object();
			// 결재??
			$checkbox = $("input[name=oid]");
			$name = $("input[name*='name']");
			$number = $("input[name*='number']");

			var oidArray = new Array();
			var nameArray = new Array();
			var numberArray = new Array();
			$.each($checkbox, function(idx) {
				var value = $checkbox.eq(idx).val();
				oidArray.push(value);
			})

			$.each($name, function(idx) {
				var value = $name.eq(idx).val();
				nameArray.push(value);
			})

			$.each($number, function(idx) {
				var value = $number.eq(idx).val();
				numberArray.push(value);
			})

			params.oidArray = oidArray;
			params.numberArray = numberArray;
			params.nameArray = nameArray;
			$(document).ajaxCallServer(url, params, function(data) {
				dialogs.alert({
					theme : "alert",
					title : "결과",
					msg : data.msg
				}, function() {
					if (this.key == "ok" || this.state == "close") {
						self.close();
						opener.document.location.reload();
					}
				})
			}, true);
		}

		$("#changeBtn").click(function() {
			$(document).saveAs();
		})

		$("input").checks();

	}).keypress(function(e) {
		var keyCode = e.keyCode;
		if (keyCode == 79) {
			$(document).saveAs();
		}
	})
</script>
	<table class="btn_table top_margin0">
		<tr>
			<td class="left">
				<div class="header_title pos5">
					<i class="axi axi-subtitles"></i><span>새 이름으로 저장</span>
				</div>
			</td>
			<td class="right">
			<td class="right"><input type="button" id="changeBtn" class="blueBtn" title="변경 (O)" value="변경 (O)"> <input type="button" id="closeBtn" title="닫기 (C)" value="닫기 (C)"></td>
		</tr>
	</table>
	<div class="div_scroll">
		<table class="list_table indexed sortable-table">
			<colgroup>
				<!-- 1500 -->
				<col width="40">
				<col width="200">
				<col width="200">
				<col width="100">
				<col width="60">
				<col width="160">
				<col width="90">
				<col width="90">
			</colgroup>
			<tr>
				<th><input type="checkbox"></th>
				<th><%//=prefix%>번호</th>
				<th><%//=prefix%>이름</th>
				<th>상태</th>
				<th>버전</th>
				<th>위치</th>
				<th>등록일</th>
				<th>등록자</th>
			</tr>
			<%
				for (int i = 0; i < list.size(); i++) {
					RevisionControlled rc = (RevisionControlled) list.get(i);
					String number = "";
					String reName = "";
					String reNumber = "";
					String oid = rc.getPersistInfo().getObjectIdentifier().getStringValue();
					if (rc instanceof WTPart) {
						WTPart part = (WTPart) rc;
						number = part.getNumber();
					} else if (rc instanceof EPMDocument) {
						EPMDocument epm = (EPMDocument) rc;
						number = epm.getNumber();
					} else if (rc instanceof WTDocument) {
						WTDocument document = (WTDocument) rc;
						number = document.getNumber();
					}
			%>
			<tr>
				<td><input type="checkbox" name="oid" value="<%=oid%>"></td>
				<td class="left indent5"><%=number%></td>
				<td class="left indent5"><%=rc.getName()%></td>
				<td><%=rc.getLifeCycleState().getDisplay()%></td>
				<td><%=rc.getVersionIdentifier().getSeries().getValue()%>.<%=rc.getIterationIdentifier().getSeries().getValue()%></td>
				<td><%=rc.getLocation()%></td>
				<td><%=rc.getCreateTimestamp().toString().substring(0, 10)%></td>
				<td><%=rc.getCreatorFullName()%></td>
			</tr>
			<%
				}
			%>
		</table>
	</div></td>