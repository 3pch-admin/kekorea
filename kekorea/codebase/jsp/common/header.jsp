<%@page import="e3ps.org.service.OrgHelper"%>
<%@page import="e3ps.org.Department"%>
<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();

	Department dept = OrgHelper.manager.getDepartment(sessionUser);

	// KEK
	boolean isKEK = false;
	if ("kek1".equals(sessionUser.getName())) {
		isKEK = true;
	}

	boolean isKO = true;
	String locale = (String) request.getParameter("locale");

	if (!StringUtils.isNull(locale)) {
		session.removeAttribute("locales");
		session.setAttribute("locales", locale);
	}

	String ll = (String) session.getAttribute("locales");

	if ("ko".equals(ll)) {
		isKO = true;
	} else if ("ja".equals(ll)) {
		isKO = false;
	}
%>
<%
	if (isKEK) {
%>
<script>
	
</script>
<%
	}
%>

<%
	if (isKO) {
%>
<table id="header_table">
	<tr>
		<td id="logo" title="메인으로"><img
			src="/Windchill/jsp/images/logo2.gif" class="logoImg"></td>
		<td style="min-width: 600px;">
			<ul class="header_ul">
				<%
					if (isKEK) {
				%>
				<li class="epm" title="도면관리"
					data-url="/Windchill/plm/epm/listViewer">도면관리
					<ul class="sub_epm">
						<li title="뷰어 조회"><a class="sub_url"
							href="/Windchill/plm/epm/listViewer">뷰어 조회</a></li>
					</ul>
				</li>
				<%
					} else {
				%>
				<li class="approval" title="나의업무"
					data-url="/Windchill/plm/approval/listIng">나의업무
					<ul class="sub_approval">
						<li title="공지사항"><a class="sub_url"
							href="/Windchill/plm/approval/listNotice">공지사항</a></li>
						<li title="결재함"><a class="sub_url"
							href="/Windchill/plm/approval/listApproval">결재함</a></li>
						<li title="비밀번호 변경"><a class="sub_url"
							href="/Windchill/plm/org/changePassword">비밀번호 변경</a></li>
					</ul>
				</li>
				<li class="project" title="프로젝트"
					data-url="/Windchill/plm/project/listProject">프로젝트
					<ul class="sub_project">
						<li title="작번 검색"><a class="sub_url"
							href="/Windchill/plm/project/listProject">작번 조회</a></li>
						<li title="작번 등록"><a class="sub_url"
							href="/Windchill/plm/project/createProject">작번 등록</a></li>
						<li title="템플릿 조회"><a class="sub_url"
							href="/Windchill/plm/template/listTemplate">템플릿 조회</a></li>
						<li title="템플릿 등록"><a class="sub_url"
							href="/Windchill/plm/template/createTemplate">템플릿 등록</a></li>
					</ul>
				</li>
				<li class="epm" title="도면관리"
					data-url="/Windchill/plm/epm/listProductEpm">도면관리
					<ul class="sub_epm">
						<li title="도면조회"><a class="sub_url"
							href="/Windchill/plm/epm/listProductEpm">도면 조회</a></li>
						<li title="라이브러리 조회"><a class="sub_url"
							href="/Windchill/plm/epm/listLibraryEpm">라이브러리 조회</a></li>
						<li title="도면결재"><a class="sub_url"
							href="/Windchill/plm/epm/approvalEpm">도면 결재</a></li>
						<li title="도면출력"><a class="sub_url"
							href="/Windchill/plm/epm/printEpm">도면 출력</a></li>
					</ul>
				</li>
				<!-- 				<li class="part" title="BOM" data-url="/Windchill/plm/part/listProductPart">부품관리 -->
				<li class="part" title="부품관리"
					data-url="/Windchill/plm/part/listProductPart">부품관리
					<ul class="sub_part">
						<li title="부품 조회"><a class="sub_url"
							href="/Windchill/plm/part/listProductPart">부품 조회</a></li>
						<!-- 						<li title="구매품"><a class="sub_url" href="/Windchill/plm/part/listLibraryPart">라이브러리 조회</a></li> -->
						<!-- 						<li title="구매품 결재"><a class="sub_url" href="/Windchill/plm/part/approvalLibraryPart">구매품 결재</a></li> -->
					</ul>
				</li>
				<li class="document" title="문서관리"
					data-url="/Windchill/plm/document/listOutput">문서관리
					<ul class="sub_document">
						<li title="문서조회"><a class="sub_url"
							data-url="/Windchill/plm/document/listDocument">문서 조회</a></li>
						<li title="수배표 조회"><a class="sub_url"
							data-url="/Windchill/plm/partList/listPartList">수배표 조회</a></li>
						<li title="의뢰서 조회"><a class="sub_url"
							data-url="/Windchill/plm/document/listRequestDocument">의뢰서 조회</a></li>
						<li title="첨부파일조회"><a class="sub_url"
							data-url="/Windchill/plm/document/listContents">첨부파일조회</a></li>
					</ul>
				</li>
								<li class="admin" title="관리자" data-url="/Windchill/plm/admin/setPassword">관리자
									<ul class="sub_admin">
										<li title="비밀번호세팅"><a class="sub_url" data-url="/Windchill/plm/admin/setPassword">비밀번호세팅</a></li>
<!-- 										<li title="접속이력"><a class="sub_url" data-url="/Windchill/plm/admin/loginHistory">접속이력</a></li> -->
<!-- 										<li title="사용자 관리"><a class="sub_url" data-url="/Windchill/plm/admin/manageUser">사용자 관리</a></li> -->
 										<li title="코드관리"><a class="sub_url" data-url="/Windchill/plm/admin/manageCode">코드관리</a></li>
 									</ul>
								</li>			
				<%
					}
				%>
			</ul>
		</td>
		<td style="text-align: right; position: relative; bottom: 3px;">
			<select name="locale" id="locale" class="AXSelect"
			style="width: 120px;">
				<option value="">LANGUAGE</option>
				<option value="ko">한국어</option>
				<option value="ja">日本語</option>
		</select>
		</td>
		<td class="my_info right" style="width: 100px;"><i
			class="axi axi-user"></i><span class="user_span"><%=sessionUser.getFullName()%>&nbsp;[<%=dept != null ? dept.getName() : ""%>]
		</span></td>
		<td class="right" style="padding-right: 20px;"><i
			class="axi axi-ion-log-out"></i></td>
	</tr>
</table>
<%
	} else {
%>
<table id="header_table">
	<tr>
		<td id="logo" title="메인으로"><img
			src="/Windchill/jsp/images/logo2.gif" class="logoImg"></td>
		<td>
			<ul class="header_ul">
				<%
					if (isKEK) {
				%>
				<li class="epm" title="도면관리"
					data-url="/Windchill/plm/epm/listViewer">도면관리
					<ul class="sub_epm">
						<li title="뷰어 조회"><a class="sub_url"
							href="/Windchill/plm/epm/listViewer">뷰어 조회</a></li>
					</ul>
				</li>
				<%
					} else {
				%>
				<li class="approval" title="私の業務"
					data-url="/Windchill/plm/approval/listIng">私の業務
					<ul class="sub_approval">
						<li title="公知事項"><a class="sub_url"
							href="/Windchill/plm/approval/listNotice">公知事項</a></li>
						<li title="決済箱"><a class="sub_url"
							href="/Windchill/plm/approval/listApproval">決済箱</a></li>
						<li title="パスワード 変更"><a class="sub_url"
							href="/Windchill/plm/org/changePassword">パスワード 変更</a></li>
					</ul>
				</li>
				<li class="project" title="プロジェクト"
					data-url="/Windchill/plm/project/listProject"
					style="margin-right: 24px;">プロジェクト
					<ul class="sub_project">
						<li title="作番 檢索"><a class="sub_url"
							href="/Windchill/plm/project/listProject">作番 檢索</a></li>
						<li title="作番 登録"><a class="sub_url"
							href="/Windchill/plm/project/createProject">作番 登録</a></li>
						<li title="テンプレート 檢索"><a class="sub_url"
							href="/Windchill/plm/template/listTemplate">テンプレート 檢索</a></li>
						<li title="テンプレート 登録"><a class="sub_url"
							href="/Windchill/plm/template/createTemplate">テンプレート 登録</a></li>
					</ul>
				</li>
				<li class="epm" title="図面管理"
					data-url="/Windchill/plm/epm/listProductEpm">図面管理
					<ul class="sub_epm">
						<li title="図面 照会"><a class="sub_url"
							href="/Windchill/plm/epm/listProductEpm">図面 照会</a></li>
						<li title="ライブラリ 照会"><a class="sub_url"
							href="/Windchill/plm/epm/listLibraryEpm">ライブラリ 照会</a></li>
						<li title="図面 決裁"><a class="sub_url"
							href="/Windchill/plm/epm/approvalEpm">図面 決裁</a></li>
						<li title="図面出力"><a class="sub_url"
							href="/Windchill/plm/epm/printEpm">図面出力</a></li>
					</ul>
				</li>
				<li class="part" title="部品管理"
					data-url="/Windchill/plm/part/listProductPart">部品管理
					<ul class="sub_part">
						<li title="部品 檢索"><a class="sub_url"
							href="/Windchill/plm/part/listProductPart">部品 檢索</a></li>
						<li title="ライブラリ 檢索"><a class="sub_url"
							href="/Windchill/plm/part/listLibraryPart">ライブラリ 檢索</a></li>
						<!-- 						<li title="구매품 결재"><a class="sub_url" href="/Windchill/plm/part/approvalLibraryPart">구매품 결재</a></li> -->
					</ul>
				</li>
				<li class="document" title="文書管理"
					data-url="/Windchill/plm/document/listOutput">文書管理
					<ul class="sub_document">
						<li title="文書 檢索"><a class="sub_url"
							data-url="/Windchill/plm/document/listDocument">文書 檢索</a></li>
						<li title="手配表 統合檢索"><a class="sub_url"
							data-url="/Windchill/plm/partList/listPartList">手配表 統合檢索</a></li>
						<li title="依頼書檢索"><a class="sub_url"
							data-url="/Windchill/plm/document/listRequestDocument">依頼書檢索</a></li>
						<li title="添付ファイル檢索"><a class="sub_url"
							data-url="/Windchill/plm/document/listContents">添付ファイル檢索</a></li>
					</ul>
				</li>
				<!-- 				<li class="admin" title="관리자" data-url="/Windchill/plm/admin/loginHistory">管理者 -->
				<!-- 					<ul class="sub_admin"> -->
				<!-- 						<li title="접속이력"><a class="sub_url" data-url="/Windchill/plm/admin/loginHistory">접속이력</a></li> -->
				<!-- 						<li title="사용자 관리"><a class="sub_url" data-url="/Windchill/plm/admin/manageUser">사용자 관리</a></li> -->
				<!-- 						<li title="코드관리"><a class="sub_url" data-url="/Windchill/plm/admin/manageCode">코드관리</a></li> -->
				<!-- 					</ul> -->
				<!-- 				</li>			 -->
				<%
					}
				%>
			</ul>
		</td>
		<td
			style="text-align: right; position: relative; left: 50px; bottom: 3px;">
			<select name="locale" id="locale" class="AXSelect"
			style="width: 120px;">
				<option value="">LANGUAGE</option>
				<option value="ko">한국어</option>
				<option value="ja">日本語</option>
		</select>
		</td>
		<td class="right" style="padding-right: 20px;"><i
			class="axi axi-ion-log-out"></i></td>
	</tr>
</table>
<%
	}
%>