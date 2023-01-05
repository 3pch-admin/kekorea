<%@page import="e3ps.part.column.PartProductColumnData"%>
<%@page import="e3ps.common.ModuleKeys"%>
<%@page import="e3ps.common.util.ColumnUtils"%>
<%@page import="e3ps.part.service.PartHelper"%>
<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="java.util.HashMap"%>
<%@page import="e3ps.epm.beans.EpmViewData"%>
<%@page import="wt.epm.EPMDocument"%>
<%@page import="wt.util.WTAttributeNameIfc"%>
<%@page import="wt.fc.PagingQueryResult"%>
<%@page import="e3ps.part.beans.PartViewData"%>
<%@page import="wt.part.WTPart"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="e3ps.common.util.PageQueryUtils"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="e3ps.common.util.HtmlUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String root = PartHelper.ELEC_ROOT;
	String module = ModuleKeys.list_elec_part.name();
	PageQueryUtils pager = (PageQueryUtils) request.getAttribute("pager");
	PagingQueryResult result = pager.find();
	HashMap<String, Object> param = (HashMap<String, Object>) pager.getParam();
	HtmlUtils html = new HtmlUtils(param);
	String name = StringUtils.removeNull((String) param.get("name"));
	String number = StringUtils.removeNull((String) param.get("number"));
	String predate = (String) param.get("predate");
	String postdate = (String) param.get("postdate");
	String creators = StringUtils.removeNull((String) param.get("creators"));
	String creatorsOid = StringUtils.removeNull((String) param.get("creatorsOid"));
	String states = StringUtils.removeNull((String) param.get("states"));
	String statesStr = StringUtils.removeNull((String) param.get("statesStr"));
	String s = (String) param.get("latest");
	if (s == null) {
		s = "true";
	}
	boolean latest = Boolean.parseBoolean(s);
	String location = (String) param.get("location");
	if (StringUtils.isNull(location)) {
		location = root;
	}
%>
<script type="text/javascript">
	$(document).ready(function() {
		var url = "/Windchill/plm/part/viewPart";
		$(document).cellClick("name", url);
		$(document).cellClick("number", url);

		$("#list_view").click(function() {

			$(document).onLayer();
			document.location.href = "/Windchill/plm/part/listLibraryPart";
		})

		$("#img_view").add("#thumbnail").click(function() {

			$(document).onLayer();
			document.location.href = "/Windchill/plm/part/listLibraryThumnail";
		})

		$(".list_table").tableHeadFixer();

		$("#searchBtn").click(function() {

			$("input[name=tpage]").val(1);
			$("input[name=sessionid]").val(0);
			$(document).onLayer();
			$("form").submit();
		})

		$("input").checks();
	}).keypress(function(e) {

			$("input[name=tpage]").val(1);
			$("input[name=sessionid]").val(0);
			$(document).onLayer();
			$("form").submit();
		}
	})
</script>
<td valign="top"><input type="hidden" name="module" id="module" value="<%=module%>"> <%
 	out.println(pager.getScript());
 %> <!-- search table... -->
	<div class="header_title">
		<i class="axi axi-subtitles"></i><span>전장품 부품 조회</span>
	</div>

	<table class="container_table">
		<tr>
			<jsp:include page="/jsp/common/layouts/include_tree.jsp">
				<jsp:param value="<%=root%>" name="root" />
				<jsp:param value="LIBRARY" name="context" />
			</jsp:include>
			<td id="container_td">
				<table class="search_table">
					<colgroup>
						<col width="160">
						<col width="400">
						<col width="160">
						<col width="400">
					</colgroup>
					<tr>
						<th>전장품 저장위치</th>
						<td colspan="3"><input type="hidden" name="location" value="<%=location%>"><span id="location"> <%=location%></span></td>
					</tr>
					<tr>
						<th>전장품 부품번호</th>
						<td><input type="text" name="number" class="AXInput wid200" value="<%=number%>"></td>
						<th>전장품 부품명</th>
						<td><input type="text" name="name" class="AXInput wid200" value="<%=name%>"></td>
					</tr>
					<tr>
						<th>작성자</th>
						<td><input type="text" name="creators" id="creators" class="AXInput wid200" value="<%=creators%>" data-dbl="true"> <input type="hidden" name="creatorsOid" value="<%=creatorsOid%>" id="creatorsOid">
							<i title="삭제 (D)" class="axi axi-ion-close-circled delete-text" data-prefix="Oid" data-target="creators"></i></td>
						<th>작성일</th>
						<td><input type="text" name="predate" value="<%=StringUtils.removeNull(predate)%>" id="predate" class="AXInput"> ~ <input type="text" value="<%=StringUtils.removeNull(postdate)%>"
							name="postdate" id="postdate" class="AXInput twinDatePicker" data-start="predate"> <i title="삭제 (D)" class="axi axi-ion-close-circled delete-calendar" data-start="predate"
							data-end="postdate"></i></td>
					</tr>
					<tr>
						<th>버전</th>
						<td><label title="최신버전"> <input type="radio" value="true" name="latest" <%if (latest == true) {%> checked="checked" <%}%>>최신버전
						</label> <label title="모든버전"> <input type="radio" value="false" name="latest" <%if (latest == false) {%> checked="checked" <%}%>>모든버전
						</label></td>
						<th>상태</th>
						<td><input type="text" class="AXInput wid150" id="states" name="states" value="<%=states%>" data-key="DEFAULT_LIFECYCLE"> <input type="hidden" name="statesStr" id='statesStr'
							value="<%=statesStr%>"> <i title="삭제 (D)" class="axi axi-ion-close-circled delete-text" data-prefix="Str" data-target="states"></i></td>
					</tr>
				</table>

				<table class="btn_table">
					<tr>
						<td class="right"><input type="button" class="blueBtn" value="조회" id="searchBtn" title="조회"> <input type="button" value="초기화 (I)" id="init_table" title="초기화 (I)"></td>
					</tr>
				</table>

				<table class="sub_table">
					<tr>
						<td>
							<div class="view_layer">
								<ul>
									<li id="list_view" class="active_view" title="리스트 뷰">리스트 뷰</li>
									<li class="hidden" id="img_view" title="이미지 뷰">이미지 뷰</li>
									<li class="hidden"><span id="thumbnail" title="썸네일 리스트로 확인하세요.">썸네일 리스트로 확인하세요 </span></li>
								</ul>
							</div>

							<div class="paging_layer">
								<span class="count_span"><span id="count_text"></span></span> <span class="page_count" title="15개씩 보기">15</span> <span class="page_count" title="30개씩 보기">30</span> <span class="page_count"
									title="50개씩 보기">50</span>
							</div>
						</td>
					</tr>
				</table>

				<div class="div_scroll">
					<table class="list_table resiable">
						<%
							boolean isBox = true;
											String[] headers = ColumnUtils.getColumnHeaders(module);
											String[] keys = ColumnUtils.getColumnKeys(module);
											String[] cols = ColumnUtils.getColumnCols(module, headers);
											String[] styles = ColumnUtils.getColumnStyles(module, headers);
											out.println(html.setHeader(isBox, headers, keys, cols, styles, pager.getSort()));
						%>
						<tbody>
							<%
								int idx = 0;
													while (result.hasMoreElements()) {
														Object[] obj = (Object[]) result.nextElement();
														WTPart part = (WTPart) obj[0];
														PartProductColumnData data = new PartProductColumnData(part);
							%>
							<tr class="list_tr" data-oid="<%=data.oid%>" data-key="quickmenu<%=idx%>">
								<td><input name="oid" id="oid" value="<%=data.oid%>" type="checkbox"> <%
 	out.println(html.setQuick(idx, data.oid, module));
 %></td>
								<%
									for (int i = 0; i < styles.length; i++) {
											if (i == 1) {
												String[] thum = data.getValue(keys[i]).split("†");
												if (!StringUtils.isNull(thum[0])) {
								%>
								<td data-column="<%=keys[i]%>_column" <%=styles[i]%>><a data-magnify="gallery" data-caption="<%=data.name%>" href="<%=thum[0]%>"> <img class="pos2" src="<%=thum[1]%>"></a></td>
								<%
									} else {
								%>
								<td>&nbsp;</td>
								<%
									}
											} else if (i == 2) {
								%>
								<td class="left indent5" data-column="<%=keys[i]%>_column" <%=styles[i]%>><img class="pos3" src="<%=data.iconPath%>">&nbsp;<%=data.getValue(keys[i])%></td>
								<%
									} else {
								%>
								<td data-column="<%=keys[i]%>_column" <%=styles[i]%>><%=data.getValue(keys[i])%></td>
								<%
									}
										}
								%>
							</tr>
							<%
								idx++;
								}
								if (result.size() == 0) {
									int colspan = keys.length;
									if (isBox) {
										colspan = colspan + 1;
									}
							%>
							<tr>
								<td class="nodata_icon" colspan="<%=colspan%>"><a class="axi axi-info-outline"></a> <span> 조회 결과가 없습니다.</span></td>
							</tr>
							<%
								}
							%>
						</tbody>
					</table>
				</div>
			</td>
		</tr>
	</table> <%
 	out.println(pager.paging());
 	out.println(html.setContextmenu(module));
 	out.println(html.setQuickScript(pager.getPsize()));
 %></td>