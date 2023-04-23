<%@page import="e3ps.org.dto.UserDTO"%>
<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
UserDTO data = (UserDTO) request.getAttribute("data");
boolean isAdmin = (boolean) request.getAttribute("isAdmin");
%>
<nav class="navbar-default navbar-static-side" role="navigation">
	<div class="sidebar-collapse">
		<ul class="nav metismenu" id="side-menu">
			<li class="nav-header">
				<div class="dropdown profile-element">
					<a data-toggle="dropdown" class="dropdown-toggle" href="#">
						<span class="block m-t-xs font-bold"><%=data.getName()%></span>
						<span class="text-muted text-xs block">
							<font color="white"><%=data.getDepartment_name()%>-<%=data.getDuty()%></font>
						</span>
					</a>
				</div>
			</li>
			<li>
				<a href="#">
					<i class="fa fa-envelope"></i>
					<span class="nav-label">나의 업무</span>
					<span class="fa arrow"></span>
				</a>
				<ul class="nav nav-second-level collapse">
					<li>
						<a onclick="moveToPage(this, '/notice/list', '나의 업무 > 공지사항');">공지사항</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/workspace/agree', '나의 업무 > 검토함');">
							검토함
							<span class="label label-info float-right">62</span>
						</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/workspace/approval', '나의 업무 > 결재함');">
							결재함
							<span class="label label-info float-right">62</span>
						</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/workspace/receive', '나의 업무 > 수신함');">
							수신함
							<span class="label label-info float-right">62</span>
						</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/workspace/progress', '나의 업무 > 진행함');">
							진행함
							<span class="label label-info float-right">62</span>
						</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/workspace/complete', '나의 업무 > 완료함');">
							완료함
							<span class="label label-info float-right">62</span>
						</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/workspace/reject', '나의 업무 > 반려함');">
							반려함
							<span class="label label-info float-right">62</span>
						</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/org/organization', '나의 업무 > 조직도');">조직도</a>
					</li>
				</ul>
			</li>
			<li>
				<a href="metrics.html">
					<i class="fa fa-pie-chart"></i>
					<span class="nav-label">작번 관리</span>
					<span class="fa arrow"></span>
				</a>
				<ul class="nav nav-second-level collapse">
					<li>
						<a onclick="moveToPage(this, '/project/list', '작번 관리 > 작번 조회');">작번 조회</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/project/my', '나의 작번 > 나의 작번');">나의 작번</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/template/list', '작번 관리 > 템플릿 조회');">템플릿 조회</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/issue/list', '작번 관리 > 특이사항 조회');">특이사항 조회</a>
					</li>
				</ul>
			</li>
			<li>
				<a href="#">
					<i class="fa fa-edit"></i>
					<span class="nav-label">도면 관리</span>
					<span class="fa arrow"></span>
				</a>
				<ul class="nav nav-second-level collapse">
					<li>
						<a onclick="moveToPage(this, '/keDrawing/list', '도면 관리 > KE 도면 조회');">KE 도면 조회</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/numberRule/list', '도면 관리 > KEK 도번 조회');">KEK 도번 조회</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/epm/list', '도면 관리 > KEK 도면 조회');">KEK 도면 조회</a>
					</li>
					<!-- 					<li> -->
					<!-- 						<a href="form_advanced.html">라이브러리 조회</a> -->
					<!-- 					</li> -->
					<li>
						<a onclick="moveToPage(this, '/workOrder/list', '도면 관리 > 도면 일람표 조회');">도면 일람표 조회</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/epm/register', '도면 관리 > 도면 결재')">도면 결재</a>
					</li>
					<!-- 					<li> -->
					<!-- 						<a href="form_file_upload.html">도면 출력</a> -->
					<!-- 					</li> -->
					<!-- 					<li> -->
					<!-- 						<a href="form_editors.html">뷰어 등록</a> -->
					<!-- 					</li> -->
					<!-- 					<li> -->
					<!-- 						<a href="form_autocomplete.html">뷰어 조회</a> -->
					<!-- 					</li> -->
				</ul>
			</li>
			<li>
				<a href="#">
					<i class="fa fa-desktop"></i>
					<span class="nav-label">부품 관리</span>
					<span class="fa arrow"></span>
				</a>
				<ul class="nav nav-second-level collapse">
					<li>
						<a onclick="moveToPage(this, '/part/list', '부품 관리 > 부품 조회');">부품 조회</a>
					</li>
					<li>
						<a href="profile.html">코드 생성</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/part/bundle', '부품 관리 > 부품 일괄 등록');">부품 일괄 등록</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/part/spec', '부품 관리 > 제작사양서 등록');">제작사양서 등록</a>
					</li>
					<!-- 					<li> -->
					<!-- 						<a href="contacts_2.html">제작사양서 등록</a> -->
					<!-- 					</li> -->
					<!-- 					<li> -->
					<!-- 						<a href="projects.html">UNIT BOM 조회</a> -->
					<!-- 					</li> -->
					<li>
						<a onclick="moveToPage(this, '/kePart/list', '부품 관리 > KE 부품 조회');">KE 부품 조회</a>
					</li>
					<!-- 					<li> -->
					<!-- 						<a href="project_detail.html">UNIT BOM 등록</a> -->
					<!-- 					</li> -->
					<!-- 					<li> -->
					<!-- 						<a href="activity_stream.html">EPLAN 결재</a> -->
					<!-- 					</li> -->
				</ul>
			</li>
			<li>
				<a href="#">
					<i class="fa fa-files-o"></i>
					<span class="nav-label">문서 관리</span>
					<span class="fa arrow"></span>
				</a>
				<ul class="nav nav-second-level collapse">
					<li>
						<a onclick="moveToPage(this, '/doc/list', '문서 관리 > 문서 조회');">문서 조회</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/output/list', '문서 관리 > 산출물 조회');">산출물 조회</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/requestDocument/list', '문서 관리 > 의뢰서 조회');">의뢰서 조회</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/meeting/list', '문서 관리 > 회의록 조회');">회의록 조회</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/document/register', '문서 관리 > 문서 결재');">문서 결재</a>
					</li>
				</ul>
			</li>
			<li>
				<a href="#">
					<i class="fa fa-files-o"></i>
					<span class="nav-label">BOM 관리</span>
					<span class="fa arrow"></span>
				</a>
				<ul class="nav nav-second-level collapse">
					<li>
						<a onclick="moveToPage(this, '/partlist/list', 'BOM 관리 > 수배표 조회');">수배표 조회</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/tbom/list', 'BOM 관리 > T-BOM 조회');">T-BOM 조회</a>
					</li>
				</ul>
			</li>
			<li>
				<a href="#">
					<i class="fa fa-sitemap"></i>
					<span class="nav-label">한국 생산</span>
					<span class="fa arrow"></span>
				</a>
				<ul class="nav nav-second-level collapse">
					<li>
						<a onclick="moveToPage(this, '/korea/list', '한국 생산 > 한국 생상');">한국 생산</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/configSheet/list', '한국 생산 > CONFIG SHEET 조회');">CONFIG SHEET 조회</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/cip/list', '한국 생산 > CIP 조회');">CIP 조회</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/history/list', '한국 생산 > 이력 관리 조회');">이력 관리 조회</a>
					</li>
				</ul>
			</li>
			<li>
				<a href="#">
					<i class="fa fa-sitemap"></i>
					<span class="nav-label">ERP 로그</span>
					<span class="fa arrow"></span>
				</a>
				<ul class="nav nav-second-level collapse">
					<li>
						<a onclick="moveToPage(this, '/erp/list', 'ERP 로그 > ERP 전송 로그');">ERP 전송 로그</a>
					</li>
				</ul>
			</li>
			<%
			if (isAdmin) {
			%>
			<li>
				<a href="css_animation.html">
					<i class="fa fa-magic"></i>
					<span class="nav-label">관리자</span>
					<span class="fa arrow"></span>
				</a>
				<ul class="nav nav-second-level collapse">
					<li>
						<a onclick="moveToPage(this, '/commonCode/list', '관리자 > 코드 관리');">코드 관리</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/specCode/list', '관리자 > 이력 관리 컬럼');">이력 관리 컬럼</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/configSheetCode/list', '관리자 > CONFIG SHEET 카테고리');">CONFIG SHEET 카테고리</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/numberRuleCode/list', '관리자 > KEK 도번 관리');">KEK 도번 관리</a>
					</li>
					<!-- 					<li> -->
					<!-- 						<a onclick="moveToPage(this, '/password/list', '관리자 > 비밀번호 세팅');">비밀번호 세팅</a> -->
					<!-- 					</li> -->
					<li>
						<a onclick="moveToPage(this, '/meeting/template', '관리자 > 회의록 템플릿');">회의록 템플릿</a>
					</li>
				</ul>
			</li>
			<%
			}
			%>
		</ul>
	</div>
</nav>
