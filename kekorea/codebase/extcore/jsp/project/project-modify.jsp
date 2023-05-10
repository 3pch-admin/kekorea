<%@page import="e3ps.project.dto.ProjectDTO"%>
<%@page import="java.util.HashMap"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
ProjectDTO dto = (ProjectDTO) request.getAttribute("dto");
ArrayList<CommonCode> customers = (ArrayList<CommonCode>) request.getAttribute("customers");
ArrayList<CommonCode> projectTypes = (ArrayList<CommonCode>) request.getAttribute("projectTypes");
ArrayList<CommonCode> maks = (ArrayList<CommonCode>) request.getAttribute("maks");
ArrayList<HashMap<String, String>> list = (ArrayList<HashMap<String, String>>) request.getAttribute("list");
%>
<input type="hidden" name="oid" id="oid" value="<%=dto.getOid() %>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				작번 수정
			</div>
		</td>
		<td class="right">
			<input type="button" value="수정" title="수정" onclick="modify();">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>

<table class="create-table">
	<tr>
		<th class="req lb">KEK 작번</th>
		<td class="indent5">
			<input type="text" name="kekNumber" id="kekNumber" class="width-300" value="<%=dto.getKekNumber() %>">
		</td>
		<th class="req">작번 발행일</th>
		<td class="indent5">
			<input type="text" name="pdate" id="pdate" value="<%=dto.getPdate_txt() %>">
			<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('pdate')">
		</td>
	</tr>
	<tr>
		<th class="req lb">KE 작번</th>
		<td class="indent5">
			<input type="text" name="keNumber" id="keNumber" value="<%=dto.getKeNumber() %>">
		</td>
		<th class="req">요구납기일</th>
		<td class="indent5">
			<input type="text" name="customDate" id="customDate" value="<%=dto.getCustomDate_txt() %>">
			<img src="/Windchill/extcore/images/delete.png" class="delete" title="삭제" onclick="clearUser('customDate')">
		</td>
	</tr>
	<tr>
		<th class="req lb">USER ID</th>
		<td class="indent5">
			<input type="text" name="userId" id="userId" class="width-300" value="<%=dto.getUserId() %>">
		</td>
		<th class="req">모델</th>
		<td class="indent5">
			<input type="text" name="model" id="model" class="width-300" value="<%=dto.getModel() %>">
		</td>
	</tr>
	<tr>
		<th class="req lb">막종</th>
		<td class="indent5">
			<select name="mak" id="mak" class="width-200">
				<option value="">선택</option>
				<%
				for (CommonCode mak : maks) {
					String value = mak.getPersistInfo().getObjectIdentifier().getStringValue();
				%>
				<option value="<%=value%>" <%if(value.equals(dto.getMak_oid())) { %> selected="selected" <%} %>><%=mak.getName()%></option>
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
					String value = customer.getPersistInfo().getObjectIdentifier().getStringValue();
				%>
				<option value="<%=value%>" <%if(value.equals(dto.getCustomer_oid())) { %> selected="selected" <%} %>><%=customer.getName()%></option>
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
		<td class="indent5" colspan="3">
			<select name="projectType" id="projectType" class="width-200">
				<option value="">선택</option>
				<%
				for (CommonCode projectType : projectTypes) {
					String value = projectType.getPersistInfo().getObjectIdentifier().getStringValue();
				%>
				<option value="<%=value%>" <%if(value.equals(dto.getProjectType_oid())) { %> selected="selected" <%} %>><%=projectType.getName()%></option>
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
	function modify() {

		const kekNumber = document.getElementById("kekNumber");
		const keNumber = document.getElementById("keNumber");
		const pdate = document.getElementById("pdate");
		const customDate = document.getElementById("customDate");
		const userId = document.getElementById("userId");
		const model = document.getElementById("model");
		const mak = document.getElementById("mak");
		const detail = document.getElementById("detail");
		const customer = document.getElementById("customer");
		const install = document.getElementById("install");
		const projectType = document.getElementById("projectType");
		const description = document.getElementById("description");
		const oid = document.getElementById("oid").value;

		if (isNull(kekNumber.value)) {
			alert("KEK 작번을 입력하세요.");
			kekNumber.focus();
			return false;
		}

		if (isNull(keNumber.value)) {
			alert("KE 작번을 입력하세요.");
			keNumber.focus();
			return false;
		}
		if (isNull(pdate.value)) {
			alert("작번 발행일을 선택하세요.");
			pdate.focus();
			return false;
		}
		if (isNull(customDate.value)) {
			alert("요구납기일을 선택하세요.");
			customDate.focus();
			return false;
		}
		if (isNull(userId.value)) {
			alert("USER ID를 입력하세요.");
			userId.focus();
			return false;
		}
		if (isNull(model.value)) {
			alert("모델을 입력하세요.");
			model.focus();
			return false;
		}
		if (isNull(mak.value)) {
			alert("막종을 선택하세요.");
			return false;
		}
		if (isNull(detail.value)) {
			alert("막종상세를 선택하세요.");
			return false;
		}
		if (isNull(customer.value)) {
			alert("고객사를 선택하세요.");
			return false;
		}
		if (isNull(install.value)) {
			alert("설치장소를 선택하세요.");
			return false;
		}
		if (isNull(projectType.value)) {
			alert("작번 유형 선택하세요.");
			return false;
		}
		if (!confirm("등록 하시겠습니까?")) {
			return false;
		}
		const params = new Object();
		const url = getCallUrl("/project/modify");
		params.oid = oid;
		params.kekNumber = kekNumber.value;
		params.keNumber = keNumber.value;
		params.pdate = pdate.value;
		params.customDate = customDate.value;
		params.userId = userId.value;
		params.model = model.value;
		params.mak = mak.value;
		params.detail = detail.value;
		params.customer = customer.value;
		params.install = install.value;
		params.projectType = projectType.value;
		params.description = description.value;
		openLayer();
		call(url, params, function(data) {
			alert(data.msg);
			if (data.result) {
				opener.loadGridData();
				self.close();
			} else {
				closeLayer();
			}
		})
	}

	document.addEventListener("DOMContentLoaded", function() {
		document.getElementById("kekNumber").focus();
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
					setValue : "<%=dto.getDetail_oid()%>",
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
					setValue : "<%=dto.getInstall_oid()%>",
					alwaysOnChange : true,
				})
			}
		})

		selectbox("detail");
		selectbox("install");
		selectbox("reference");
		selectbox("projectType");
		date("pdate");
		date("customDate");
		$("#mak").bindSelectSetValue("<%=dto.getMak_oid()%>");
		$("#customer").bindSelectSetValue("<%=dto.getCustomer_oid()%>");
	})
</script>