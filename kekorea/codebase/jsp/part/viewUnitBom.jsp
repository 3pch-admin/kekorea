<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	String oid = (String) request.getAttribute("oid");
%>
<td valign="top"><input type="hidden" name="oid" value="<%=oid%>">
	<script type="text/javascript">
	$(function() {
		var array = new Array();
		$("#bom_table").fancytree({
			dblclick : function(e, data) {
			},
			init : function(e, data) {
			},
			collapse : function(e, data) {
			},
			expand : function(e, data) {
			},
			click : function(e, data) {
			},
			extensions : [ "table", "persist" ],
			loadChildren : function(event, data) {
			},
			table : {
				nodeColumnIdx : 1
			},
			select : function(event, data) {
			},
// 			extensions : [ "" ],
			persist : {
				expandLazy : true,
				store : "auto" // 'cookie', 'local': use localStore,
			},
			renderColumns : function(event, data) {
				var node = data.node;
				console.log(data);
				$tdList = $(node.tr).find(">td");
				$tdList.eq(0).text(node.getIndexHier());
				$tdList.eq(2).text(node.data.name);
				$tdList.eq(3).text(node.data.lotNo);
				$tdList.eq(4).text(node.data.unitName);
				$tdList.eq(5).text(node.data.spec);
// 				$tdList.eq(4).text(node.data.material);
				$tdList.eq(6).text(node.data.quantity);
				$tdList.eq(7).text(node.data.maker);
				$tdList.eq(8).text(node.data.customer);
				
				$tdList.eq(9).text(node.data.unit);
				$tdList.eq(10).text(node.data.price);
				$tdList.eq(11).text(node.data.currency);
				$tdList.eq(12).text(node.data.won);
				$tdList.eq(13).text(node.data.classification);
				$tdList.eq(14).text(node.data.note);
// 				$tdList.eq(8).text(node.data.surface);
// 				$tdList.eq(9).text(node.data.libraryAttr);
// 				$tdList.eq(10).text(node.data.createDate);
				array.push(node.data.id);
			},
			source : $.ajax({
				url : "/Windchill/plm/part/getBomUnitBomTree?oid=<%=oid%>",
							type : "POST"
						})
					})
					
			$("#reSendBtn").click(function() {
				var dialogs = $(document).setOpen();
				var url = "/Windchill/plm/part/reSendAction";
				var box = $(document).setNonOpen();
				box.confirm({
					theme : "info",
					title : "??????",
					msg : "????????? ???????????????????"
				}, function() {
					// ??????
					if (this.key == "ok") {
// 						var oid = $("input[name=oid]").val();
// 						var arr = new Array();
						var params = new Object();
// 						arr.push(oid);
						params.list = array;
						// params.list = value List<String> ??????
						$(document).ajaxCallServer(url, params, function(data) {
							dialogs.alert({
								theme : "alert",
								title : "??????",
								msg : data.msg
							}, function() {
								if (this.key == "ok" || this.state == "close") {
									opener.parent.reloadPage();
// 									self.close();
// 									$("#loading_layer").hide();
// 									$popup = $("input[name=popup]").val();
// 									if ($popup == "true") {
// 										self.close();
// 										opener.document.location.href = data.url;
// 									} else if ($popup == "false") {
// 										document.location.href = data.url;
// 									}
								}
							})
						}, true);
						// ?????? or esc
					} else if (this.key == "cancel" || this.state == "close") {
						mask.close();
					}
				})
			})
		})
	</script>
	<table class="btn_table">
		<tr>
			<td>
				<div class="header_title">
					<i class="axi axi-subtitles"></i><span>UNITBOM ??????</span>
				</div>
			</td>
			<td>
				<div class="right">
					<input type="button" value="?????????" id="reSendBtn"  name="reSendBtn" title="?????????" class="blueBtn">
					<input type="button" value="??????" id="closeDocBtn" title="??????" class="redBtn">
				</div>
			</td>
		</tr>
	</table>
	<table data-table="bom" id="bom_table">
		<colgroup>
			<col width="100">
			<col width="300">
			<col width="100">
			<col width="100">
			<col width="300">
			<col width="300">
<!-- 			<col width="130"> -->
			<col width="80">
			<col width="130">
			<col width="150">
			<col width="150">
			<col width="80">
			<col width="80">
			<col width="80">
			<col width="80">
			<col width="150">
			<col width="120">
<!-- 			<col width="120"> -->
		</colgroup>
		<thead>
			<tr>
				<th></th>
				<th>????????????</th>
				<th>?????????</th>
				<th>LOT_NO</th>
				<th>UNIT_NAME</th>
				<th>??????</th>
<!-- 				<th>??????</th> -->
				<th>??????</th>
				<th>?????????</th>
				<th>?????????</th>
				<th>??????</th>
				<th>??????</th>
				<th>??????</th>
				<th>????????????</th>
				<th>????????????</th>
				<th>??????</th>
<!-- 				<th>???????????????</th> -->
<!-- 				<th>???????????????</th> -->
<!-- 				<th>?????????</th> -->
			</tr>
		</thead>
		<tbody>
			<tr>
				<td data-align="left"></td>
				<td data-align="left"></td>
				<td data-align="center"></td>
				<td data-align="center"></td>
				<td data-align="center"></td>
				<td data-align="center"></td>
				<td data-align="center"></td>
				<td data-align="center"></td>
				<td data-align="center"></td>
				<td data-align="center"></td>
				<td data-align="center"></td>
				<td data-align="center"></td>
				<td data-align="center"></td>
				<td data-align="center"></td>
				<td data-align="center"></td>
			</tr>
		</tbody>
	</table>
	</td>