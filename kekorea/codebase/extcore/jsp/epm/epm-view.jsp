<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title></title>
<!-- CSS 공통 모듈 -->
<%@include file="/extcore/include/css.jsp"%>
<!-- 스크립트 공통 모듈 -->
<%@include file="/extcore/include/script.jsp"%>
</head>
<body>
	<input type="hidden" name="oid" id="oid" value="">
	<input type="hidden" name="popup" id="popup" value="">

	<table class="button-table">
		<tr>
			<td>
				<div class="header_title">
					<i class="axi axi-subtitles"></i>
					<span>도면 정보</span>
				</div>
			</td>
			<td class="right">
				<input type="button" value="결재이력" data-oid="" class="infoApprovalHistory" id="infoApprovalHistory" title="결재이력">
				<input type="button" value="버전정보" id="infoVersionBtn" title="버전정보">
				<input type="button" value="닫기" id="closePartBtn" title="닫기" class="red" onclick="self.close();">
			</td>
		</tr>
	</table>
	<table class="view-table">
		<colgroup>
			<col width="180">
			<col width="200">
			<col width="180">
			<col width="200">
			<col width="350">
		</colgroup>
		<tr>
			<th>도면 번호</th>
			<td colspan="3"></td>
			<td rowspan="10" class="center">
				<%-- 								<jsp:include page="/jsp/common/thumnail.jsp"> --%>
				<%-- 									<jsp:param value="" name="oid" /> --%>
				<%-- 									<jsp:param value="" name="url" /> --%>
				<%-- 								</jsp:include> --%>
			</td>
		</tr>
		<tr>
			<th>도면 이름</th>
			<td colspan="3"></td>

		</tr>
		<tr>
			<th>버전</th>
			<td colspan="3"></td>
		</tr>
		<tr>
			<th>상태</th>
			<td></td>
			<th>저장위치</th>
			<td></td>
		</tr>
		<tr>
			<th>종류</th>
			<td></td>
			<th>응용프로그램</th>
			<td></td>
		</tr>
		<tr>
			<th>작성자</th>
			<td></td>
			<th>작성일</th>
			<td></td>
		</tr>
		<tr>
			<th>수정자</th>
			<td></td>
			<th>수정일</th>
			<td></td>
		</tr>
		<tr>
			<th>도면파일</th>
			<td>
				<%-- 				<% --%>
				<!-- 					String[] primarys = data.cadData; -->
				<!-- 					String icon = ContentUtils.getOpenIcon(data.epm); -->
				<!-- 				%> -->
				<a href=" ">
					<img src=" " class="pos2">
					<%-- 					<%=//primarys[2] %> --%>
				</a>
			</td>
			<th>변환 파일</th>
			<td></td>
		</tr>
		<tr>
			<th>부품</th>
			<%-- 			<% --%>
			<!-- 				if(data.part == null) { -->
			<!-- 			%> -->
			<td colspan="3">
				<font color="red">부품이 없습니다.</font>
			</td>
			<%-- 			<% --%>
			<!-- 				} else { -->
			<!-- 					String poid = data.part.getPersistInfo().getObjectIdentifier().getStringValue(); -->
			<!-- 			%> -->
			<%-- 			<td colspan="3" class="infoPer" data-oid="<%=poid %>"><%=data.part.getNumber() %></td> --%>
			<%-- 			<% --%>
			<!-- 			} -->
			<!-- 			%> -->
		</tr>
		<tr>
			<th>설명</th>
			<td colspan="3"></td>
		</tr>
	</table>
	<%-- 	<% --%>
	<!-- 		// 가공품 속성 -->
	<!-- 		if ("PROE".equals(data.cadType)) { -->
	<!-- 	%> -->
	<div class="refAttr_wrap">
		<div class="header_title margin_top10">
			<i class="axi axi-subtitles"></i>
			<span>도면 속성</span>
		</div>

		<table class="view-table">
			<colgroup>
				<col width="250">
				<col width="400">
				<col width="250">
				<col width="400">
			</colgroup>
			<%-- 			<% --%>
			<!-- 				PROEAttr[] proeAttrs = PROEAttr.values(); -->
			<!-- 				for(int i=0; i<proeAttrs.length; i++) { -->
			<!-- 					String key = proeAttrs[i].name(); -->
			<!-- 					String value = IBAUtils.getStringValue(data.epm, key); -->

			<!-- 					if(i==proeAttrs.length-1){ -->
			<!-- 			%> -->

			<tr>
				<th>키</th>
				<td colspan="3">밸류</td>
				<%-- 					<% --%>
				<!-- 						break; -->
				<!-- 					} -->
				<!-- 					%> -->
			<tr>
				<th>키</th>
				<td>밸류</td>
				<%-- 			<% --%>
				<!-- 					i++; -->
				<!-- 					key = proeAttrs[i].name(); -->
				<!-- 					value = IBAUtils.getStringValue(data.epm, key); -->
				<!-- 			%> -->
				<th>키</th>
				<td>밸류</td>
			</tr>
			<%-- 			<% --%>
			<!-- 				} -->
			<!-- 			%> -->
		</table>
	</div>
	<%-- 	<% --%>
	<!-- 		}  else if ("ACAD".equals(data.cadType)) { -->
	<!-- 	%> -->
	<div class="refAttr_wrap">
		<div class="header_title margin_top10">
			<i class="axi axi-subtitles"></i>
			<span>도면 속성</span>
		</div>

		<table class="view-table">
			<colgroup>
				<col width="250">
				<col width="400">
				<col width="250">
				<col width="400">
			</colgroup>
			<%-- 			<% --%>
			<!-- 				ACADAttr[] acadAttrs = ACADAttr.values(); -->
			<!-- 				for(int i=0; i<acadAttrs.length; i++) { -->
			<!-- 					String key = acadAttrs[i].name(); -->
			<!-- 					String value = IBAUtils.getStringValue(data.epm, key); -->

			<!-- 					if(i==acadAttrs.length-1){ -->
			<!-- 			%> -->

			<tr>
				<th>키</th>
				<td colspan="3">밸류</td>
				<%-- 					<% --%>
				<!-- 						break; -->
				<!-- 					} -->
				<!-- 					%> -->
			</tr>
			<tr>
				<th>키</th>
				<td>밸류</td>
				<%-- 			<% --%>
				<!-- 					i++; -->
				<!-- 					key = acadAttrs[i].name(); -->
				<!-- 					value = IBAUtils.getStringValue(data.epm, key); -->
				<!-- 			%> -->
				<th>키</th>
				<td>밸류</td>
			</tr>
			<%-- 			<% --%>
			<!-- 				} -->
			<!-- 			%> -->
		</table>
	</div>
	<%-- 		<% --%>
	<!-- 		} else { -->
	<!-- 	%> -->
	<div class="refAttr_wrap">
		<div class="header_title margin_top10">
			<i class="axi axi-subtitles"></i>
			<span>도면 속성</span>
		</div>

		<table class="view-table">
			<colgroup>
				<col width="250">
				<col width="400">
				<col width="250">
				<col width="400">
			</colgroup>
			<%-- 			<% --%>
			<!-- 			PROEAttr[] proeAttrs = PROEAttr.values(); -->
			<!-- 			for (int i = 0; i < proeAttrs.length; i++) { -->
			<!-- 				String key = proeAttrs[i].name(); -->
			<!-- 				String value = IBAUtils.getStringValue(data.epm, key); -->

			<!-- 				if (i == proeAttrs.length - 1) { -->
			<!-- 			%> -->

			<tr>
				<th>키</th>
				<td colspan="3">밸류</td>
				<%-- 				<% --%>
				<!-- 				break; -->
				<!-- 				} -->
				<!-- 				%> -->
			<tr>
				<th>키</th>
				<td>밸류</td>
				<%-- 				<% --%>
				<!-- 				i++; -->
				<!-- 				key = proeAttrs[i].name(); -->
				<!-- 				value = IBAUtils.getStringValue(data.epm, key); -->
				<!-- 				%> -->
				<th>키</th>
				<td>밸류</td>
			</tr>
			<%-- 			<% --%>
			<!-- 			} -->
			<!-- 			%> -->
		</table>
	</div>
	<%-- 	<% --%>
	<!-- 	} -->
	<!-- 	if (!isPopup) { -->
	<!-- 	%> -->
	<table class="button-table">
		<tr>
			<td class="center">
				<input type="button" value="버전정보" id="infoVersionBtn" title="버전정보">
				<%-- 				<% --%>
				<!-- 				if (isProduct) { -->
				<!-- 				%> -->
				<input type="button" value="목록" id="listProductEpmBtn" title="목록" class="blueBtn">
				<%-- 				<% --%>
				<!-- 				} else if (isLibrary) { -->
				<!-- 				%> -->
				<input type="button" value="목록" id="listLibraryEpmBtn" title="목록" class="blueBtn">
				<%-- 				<% --%>
				<!-- 				} -->
				<!-- 				%> -->
			</td>
		</tr>
	</table>
	<%-- 		<% --%>
	<!-- 	} -->
	<!-- 	%> -->
</body>