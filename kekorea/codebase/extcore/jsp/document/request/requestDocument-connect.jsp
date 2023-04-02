<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="org.json.JSONArray"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%
ArrayList<HashMap<String, String>> list = (ArrayList<HashMap<String, String>>) request.getAttribute("list");
JSONArray elecs = (JSONArray) request.getAttribute("elecs");
JSONArray softs = (JSONArray) request.getAttribute("softs");
JSONArray machines = (JSONArray) request.getAttribute("machines");
JSONArray maks = (JSONArray) request.getAttribute("maks");
JSONArray installs = (JSONArray) request.getAttribute("installs");
JSONArray customers = (JSONArray) request.getAttribute("customers");
JSONArray projectTypes = (JSONArray) request.getAttribute("projectTypes");
String toid = (String) request.getAttribute("toid");
String poid = (String) request.getAttribute("poid");
boolean connect = !StringUtils.isNull(toid) && !StringUtils.isNull(poid);
%>
<%@include file="/extcore/include/auigrid.jsp"%>
<input type="hidden" name="toid" id="toid" value="<%=toid%>">
<input type="hidden" name="poid" id="poid" value="<%=poid%>">
<input type="hidden" name="connect" id="connect" value="<%=connect%>">
<table class="button-table">
	<tr>
		<td class="left">
			<div class="header">
				<img src="/Windchill/extcore/images/header.png">
				의뢰서 등록
			</div>
		</td>
		<td class="right">
			<input type="button" value="등록" title="등록" onclick="create();">
			<input type="button" value="닫기" title="닫기" class="blue" onclick="self.close();">
		</td>
	</tr>
</table>

<div id="tabs">
	<ul>
		<li>
			<a href="#tabs-1">기본정보</a>
		</li>
	</ul>
	<div id="tabs-1">
		<table class="create-table">
			<colgroup>
				<col width="150">
				<col width="*">
			</colgroup>
			<tr>
				<th class="req lb">의뢰서 제목</th>
				<td class="indent5">
					<input type="text" name="name" id="name" class="width-300">
				</td>
			</tr>
			<tr>
				<th class="lb">내용</th>
				<td class="indent5">
					<textarea name="description" id="description" rows="6"></textarea>
				</td>
			</tr>
			<tr>
				<th class="lb">첨부파일</th>
				<td class="indent5">
					<jsp:include page="/extcore/include/primary-include.jsp">
						<jsp:param value="" name="oid" />
						<jsp:param value="create" name="mode" />
						<jsp:param value="150" name="height" />
					</jsp:include>
				</td>
			</tr>
			<tr>
				<th class="req lb">결재</th>
				<td>
					<jsp:include page="/extcore/include/register-include.jsp">
						<jsp:param value="250" name="height" />
					</jsp:include>
				</td>
			</tr>
		</table>
	</div>
</div>
<script type="text/javascript">
	function create() {
		const params = new Object();
		const url = getCallUrl("/requestDocument/create");
		const name = document.getElementById("name");
		const description = document.getElementById("description").value;
		const _addRows_ = AUIGrid.getAddedRowItems(_myGridID_);
		const toid = document.getElementById("toid").value;
		const poid = document.getElementById("poid").value;
		const connect = document.getElementById("connect").value;
		if (isNull(name.value)) {
			alert("의뢰서 제목을 입력하세요.");
			name.focus();
			return false;
		}
	
		if (_addRows_.length === 0) {
			alert("결재선을 지정하세요.");
			_register();
			return false;
		}
	
		if (!confirm("등록 하시겠습니까?")) {
			return false;
		}
	
		params.name = name.value;
		params.description = description;
		params._addRows_ = _addRows_;
		params.primarys = toArray("primarys");
		params.connect = connect;
		params.toid = toid;
		params.poid = poid;
		toRegister(params, _addRows_);
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
		$("#tabs").tabs({
			active : 0,
			create : function(event, ui) {
				const tabId = ui.panel.prop("id");
				switch (tabId) {
				case "tabs-1":
					_createAUIGrid_(_columns_);
					AUIGrid.resize(_myGridID_);
					break;
				}
			},
			activate : function(event, ui) {
				return false;
				var tabId = ui.newPanel.prop("id");
				switch (tabId) {
				case "tabs-1":
					const _isCreated_ = AUIGrid.isCreated(_myGridID_);
					if (_isCreated_) {
						AUIGrid.resize(_myGridID_);
					} else {
						_createAUIGrid_(_columns_);
					}
					break;
				}
			}
		});
		document.getElementById("name").focus();
	})

	window.addEventListener("resize", function() {
		AUIGrid.resize(_myGridID_);
	});
</script>