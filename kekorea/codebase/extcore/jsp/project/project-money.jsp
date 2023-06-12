<%@page import="java.text.DecimalFormat"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = (String) request.getAttribute("oid");
double money = (double) request.getAttribute("money");
String name = (String) request.getAttribute("name");
String type = (String) request.getAttribute("type");
DecimalFormat df = new DecimalFormat("#,##0");
%>
<input type="hidden" name="oid" id="oid" value="<%=oid%>">
<input type="hidden" name="type" id="type" value="<%=type%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				<%=name%>
				견적 금액 수정
			</div>
		</td>
		<td class="right">
			<input type="button" value="저장" title="저장" onclick="save();">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>

<table class="create-table">
	<colgroup>
		<col width="150">
		<col width="*">
	</colgroup>
	<tr>
		<th class="lb"><%=name%>
			견적 금액
		</th>
		<td class="indent5">
			<input type="text" name="money" id="money" value="<%=df.format(money)%>" oninput="commas(this)" onkeypress="isNumber(event)">
		</td>
	</tr>
</table>
<script type="text/javascript">
	function save() {
		const money = document.getElementById("money");
		if(isNull(money)) {
			alert("금액을 입력하세요.");
			money.focus();
			return false;
		}
		
		if (!confirm("저장 하시겠습니까?")) {
			return false;
		}
		const oid = document.getElementById("oid").value;
		const type = document.getElementById("type").value;
		const url = getCallUrl("/project/money");
		const params = new Object();
		params.oid = oid;
		params.type = type;
		params.money = parseInt(money.value.replaceAll(",", ""));
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

	function commas(input) {
		if(isNull(input.value)) {
			return false;
		}
		const num = parseFloat(input.value.replace(/,/g, ''));
		input.value = num.toLocaleString("en-US");
	}
	
	function isNumber(e) {
		const keyCode = (e.which) ? e.which : e.keyCode;
		if(keyCode < 48 || keyCode > 57) {
			e.preventDefault();
			return false;
		}
		return true;
	}
	
	document.addEventListener("DOMContentLoaded", function() {
		document.getElementById("money").focus();
	})
</script>