<%@page import="java.util.HashMap"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ArrayList<CommonCode> customers = (ArrayList<CommonCode>) request.getAttribute("customers");
ArrayList<CommonCode> projectTypes = (ArrayList<CommonCode>) request.getAttribute("projectTypes");
ArrayList<CommonCode> maks = (ArrayList<CommonCode>) request.getAttribute("maks");
ArrayList<HashMap<String, String>> list = (ArrayList<HashMap<String, String>>) request.getAttribute("list");
%>
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				작번 등록
			</div>
		</td>
		<td class="right">
			<input type="button" value="등록" title="등록" onclick="create();">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>

<table class="create-table">
	<tr>
		<th class="req lb">KEK 작번</th>
		<td class="indent5">
			<input type="text" name="kekNumber" id="kekNumber" class="width-300">
		</td>
		<th class="req">작번 발행일</th>
		<td class="indent5">
			<input type="text" name="pdate" id="pdate">
		</td>
	</tr>
	<tr>
		<th class="req lb">KE 작번</th>
		<td class="indent5">
			<input type="text" name="keNumber" id="keNumber" class="">
		</td>
		<th class="req">요구납기일</th>
		<td class="indent5">
			<input type="text" name="customDate" id="customDate" class="">
		</td>
	</tr>
	<tr>
		<th class="req lb">USER ID</th>
		<td class="indent5">
			<input type="text" name="userId" id="userId" class="width-300">
		</td>
		<th class="req">모델</th>
		<td class="indent5">
			<input type="text" name="model" id="model" class="width-300">
		</td>
	</tr>
	<tr>
		<th class="req lb">막종</th>
		<td class="indent5">
			<select name="mak" id="mak" class="width-200">
				<option value="">선택</option>
				<%
				for (CommonCode mak : maks) {
				%>
				<option value="<%=mak.getPersistInfo().getObjectIdentifier().getStringValue()%>"><%=mak.getName()%></option>
				<%
				}
				%>
			</select>
		</td>
		<th class="req">막종상세</th>
		<td class="indent5">
			<select name="detail" id="detail" class="width-200">
				<option value="">선택</option>
			</select>
		</td>
	</tr>
	<tr>
		<th class="req lb">거래처</th>
		<td class="indent5">
			<select name="customer" id="customer" class="width-200">
				<option value="">선택</option>
				<%
				for (CommonCode customer : customers) {
				%>
				<option value="<%=customer.getName()%>"><%=customer.getName()%></option>
				<%
				}
				%>
			</select>
		</td>
		<th class="req">설치 장소</th>
		<td class="indent5">
			<select name="install" id="install" class="width-200">
				<option value="">선택</option>
			</select>
		</td>
	</tr>
	<tr>
		<th class="req lb">작번 유형</th>
		<td class="indent5">
			<select name="projectType" id="projectType" class="width-200">
				<option value="">선택</option>
				<%
				for (CommonCode projectType : projectTypes) {
				%>
				<option value="<%=projectType.getName()%>"><%=projectType.getName()%></option>
				<%
				}
				%>
			</select>
		</td>
		<th>작번 템플릿</th>
		<td colspan="3" class="indent5">
			<select name="pTemplate" id="pTemplate" class="width-300">
				<option value="">선택</option>
				<%
				for (HashMap<String, String> map : list) {
					String key = (String) map.get("value");
					String value = (String) map.get(key);
				%>
				<option value="<%=key%>"><%=value%></option>
				<%
				}
				%>
			</select>
		</td>
	</tr>
	<tr>
		<th class="lb">작업 내용</th>
		<td colspan="3" class="indent5">
			<textarea class="description" name="description" id="description" rows="9"></textarea>
		</td>
	</tr>
</table>
<script type="text/javascript">

	function create(){
		if(!confirm("등록 하시겠습니까?")) {
			return false;
		}
		
		
	}

	document.addEventListener("DOMContentLoaded", function() {
		$("#mak").bindSelect({
			onchange : function() {
				const oid = this.optionValue;
				$("#detail").bindSelect({
					ajaxUrl : getCallUrl("/commonCode/getChildrens?parentOid=" + oid),
					reserveKeys : {
						options : "list",
						optionValue : "value",
						optionText : "name"
					},
					setValue : this.optionValue,
					alwaysOnChange : true,
				})
			}
		})

		$("#customer").bindSelect({
			onchange : function() {
				const oid = this.optionValue;
				$("#install").bindSelect({
					ajaxUrl : getCallUrl("/commonCode/getChildrens?parentOid=" + oid),
					reserveKeys : {
						options : "list",
						optionValue : "value",
						optionText : "name"
					},
					setValue : this.optionValue,
					alwaysOnChange : true,
				})
			}
		})

		selectbox("detail");
		selectbox("install");
		selectbox("template");
		selectbox("projectType");
		date("pdate");
		date("customDate");
	})
</script>