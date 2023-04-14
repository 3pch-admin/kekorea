<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String oid = (String) request.getAttribute("oid");
WTUser pmUser = (WTUser) request.getAttribute("pmUser");
WTUser subPmUser = (WTUser) request.getAttribute("subPmUser");
WTUser machineUser = (WTUser) request.getAttribute("machineUser");
WTUser elecUser = (WTUser) request.getAttribute("elecUser");
WTUser softUser = (WTUser) request.getAttribute("softUser");
%>
<input type="hidden" name="oid" id="oid" value="<%=oid%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				도면일람표 정보
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
		<th class="lb">총괄 책임자</th>
		<td class="indent5">
			<input type="text" name="pm" id="pm" value="<%=pmUser != null ? pmUser.getFullName() : ""%>">
			<input type="hidden" name="pmOid" id="pmOid" value="<%=pmUser != null ? pmUser.getPersistInfo().getObjectIdentifier().getStringValue() : ""%>">
		</td>
	</tr>
	<tr>
		<th class="lb">세부일정 책임자</th>
		<td class="indent5">
			<input type="text" name="subPm" id="subPm" value="<%=subPmUser != null ? subPmUser.getFullName() : ""%>">
			<input type="hidden" name="subPmOid" id="subPmOid" value="<%=subPmUser != null ? subPmUser.getPersistInfo().getObjectIdentifier().getStringValue() : ""%>">
		</td>
	</tr>
	<tr>
		<th class="lb">기계</th>
		<td class="indent5">
			<input type="text" name="machine" id="machine" value="<%=machineUser != null ? machineUser.getFullName() : ""%>">
			<input type="hidden" name="machineOid" id="machineOid" value="<%=machineUser != null ? machineUser.getPersistInfo().getObjectIdentifier().getStringValue() : ""%>">
		</td>
	</tr>
	<tr>
		<th class="lb">전기</th>
		<td class="indent5">
			<input type="text" name="elec" id="elec" value="<%=elecUser != null ? elecUser.getFullName() : ""%>">
			<input type="hidden" name="elecOid" id="elecOid" value="<%=elecUser != null ? elecUser.getPersistInfo().getObjectIdentifier().getStringValue() : ""%>">
		</td>
	</tr>
	<tr>
		<th class="lb">SW</th>
		<td class="indent5">
			<input type="text" name="soft" id="soft" value="<%=softUser != null ? softUser.getFullName() : ""%>">
			<input type="hidden" name="softOid" id="softOid" value="<%=softUser != null ? softUser.getPersistInfo().getObjectIdentifier().getStringValue() : ""%>">
		</td>
	</tr>
</table>
<script type="text/javascript">
	function save() {
		if (!confirm("저장 하시겠습니까?")) {
			return false;
		}
		const oid = document.getElementById("oid").value;
		const pmOid = document.getElementById("pmOid").value;
		const subPmOid = document.getElementById("subPmOid").value;
		const machineOid = document.getElementById("machineOid").value;
		const elecOid = document.getElementById("elecOid").value;
		const softOid = document.getElementById("softOid").value;
		const url = getCallUrl("/project/editUser");
		const params = new Object();
		params.oid = oid;
		params.pmOid = pmOid;
		params.subPmOid = subPmOid;
		params.machineOid = machineOid;
		params.elecOid = elecOid;
		params.softOid = softOid;
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.document.location.reload();
				self.close();
			}
		})
	}

	document.addEventListener("DOMContentLoaded", function() {
		finderUser("pm");
		finderUser("subPm");
		finderUser("machine");
		finderUser("elec");
		finderUser("soft");
	})
</script>