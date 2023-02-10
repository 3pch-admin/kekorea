<%@page import="e3ps.admin.commonCode.CommonCode"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.org.beans.UserData"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="wt.org.WTUser"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
UserData data = (UserData) request.getAttribute("data");
%>
<nav class="navbar-default navbar-static-side" role="navigation">
	<div class="sidebar-collapse">
		<ul class="nav metismenu" id="side-menu">
			<li class="nav-header">
				<div class="dropdown profile-element">
					<img alt="image" class="rounded-circle" src="/Windchill/jsp/images/profile_small.jpg" />
					<a data-toggle="dropdown" class="dropdown-toggle" href="#">
						<span class="block m-t-xs font-bold"><%=data.getName()%></span>
						<span class="text-muted text-xs block">
							<%=data.getDuty()%>
							<b class="caret"></b>
						</span>
					</a>
					<ul class="dropdown-menu animated fadeInRight m-t-xs">
						<li>
							<a class="dropdown-item" href="profile.html">Profile</a>
						</li>
						<li>
							<a class="dropdown-item" href="contacts.html">Contacts</a>
						</li>
						<li>
							<a class="dropdown-item" href="mailbox.html">Mailbox</a>
						</li>
						<li class="dropdown-divider"></li>
						<li>
							<a class="dropdown-item" href="login.html">Logout</a>
						</li>
					</ul>
				</div>
				<div class="logo-element">IN+</div>
			</li>
			<li>
				<a href="#">
					<i class="fa fa-envelope"></i>
					<span class="nav-label">나의업무</span>
					<span class="fa arrow"></span>
				</a>
				<ul class="nav nav-second-level collapse">
					<li>
<!-- 						<a href="graph_flot.html">공지사항</a> -->
						<a onclick="moveToPage(this, '/approval/listNotice');">공지사항</a>
					</li>
					<li>
						<a href="graph_morris.html">나의작번</a>
					</li>
					<li>
<!-- 						<a href="graph_rickshaw.html"> -->
<!-- 							검토함 -->
<!-- 							<span class="label label-info float-right">62</span> -->
<!-- 						</a> -->
						<a onclick="moveToPage(this, '/approval/listAgree');">검토함
							<span class="label label-info float-right">62</span>
						</a>
					</li>
					<li>
<!-- 						<a href="graph_chartjs.html"> -->
<!-- 							결재함 -->
<!-- 							<span class="label label-info float-right">62</span> -->
<!-- 						</a> -->
						<a onclick="moveToPage(this, '/approval/listApproval');">결재함
							<span class="label label-info float-right">62</span>
						</a>
					</li>
					<li>
<!-- 						<a href="graph_chartist.html"> -->
<!-- 							수신함 -->
<!-- 							<span class="label label-info float-right">62</span> -->
<!-- 						</a> -->
						<a onclick="moveToPage(this, '/approval/listReceive');">수신함
							<span class="label label-info float-right">62</span>
						</a>
					</li>
					<li>
<!-- 						<a href="c3.html"> -->
<!-- 							진행함 -->
<!-- 							<span class="label label-info float-right">62</span> -->
<!-- 						</a> -->
						<a onclick="moveToPage(this, '/approval/listIng');">진행함
							<span class="label label-info float-right">62</span>
						</a>
					</li>
					<li>
<!-- 						<a href="graph_peity.html"> -->
<!-- 							완료함 -->
<!-- 							<span class="label label-info float-right">62</span> -->
<!-- 						</a> -->
						<a onclick="moveToPage(this, '/approval/listComplete');">완료함
							<span class="label label-info float-right">62</span>
						</a>
					</li>
					<li>
<!-- 						<a href="graph_sparkline.html"> -->
<!-- 							반려함 -->
<!-- 							<span class="label label-info float-right">62</span> -->
<!-- 						</a> -->
						<a onclick="moveToPage(this, '/approval/listReturn');">반려함
							<span class="label label-info float-right">62</span>
						</a>
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
						<a onclick="moveToPage(this, '/project/list');">작번 조회</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/template/list');">템플릿 조회</a>
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
						<a onclick="moveToPage(this, '/jDrawing/list');">KE 도면 조회</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/numberRule/list');">KEK 도번 조회</a>
					</li>
					<li>
						<a href="form_basic.html">KEK 도면 조회</a>
					</li>
					<li>
						<a href="form_advanced.html">라이브러리 조회</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/workOrder/list');">도면 일람표 조회</a>
					</li>
					<li>
						<a href="form_wizard.html">도면 결재</a>
					</li>
					<li>
						<a href="form_file_upload.html">도면 출력</a>
					</li>
					<!-- 					<li> -->
					<!-- 						<a href="form_editors.html">뷰어 등록</a> -->
					<!-- 					</li> -->
					<li>
						<a href="form_autocomplete.html">뷰어 조회</a>
					</li>
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
						<a onclick="moveToPage(this, '/part/listPart');">부품 조회</a>
					</li>
					<li>
						<a href="profile.html">코드 생성</a>
					</li>
					<!-- 					<li> -->
					<!-- 						<a href="profile_2.html">부품 일괄 등록</a> -->
					<!-- 					</li> -->
					<!-- 					<li> -->
					<!-- 						<a href="contacts_2.html">제작사양서 등록</a> -->
					<!-- 					</li> -->
					<li>
						<a href="projects.html">UNIT BOM 조회</a>
					</li>
					<!-- 					<li> -->
					<!-- 						<a href="project_detail.html">UNIT BOM 등록</a> -->
					<!-- 					</li> -->
					<li>
						<a href="activity_stream.html">EPLAN 결재</a>
					</li>
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
						<a onclick="moveToPage(this, '/document/list');">문서 조회</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/document/listOutput');">산출물 조회</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/partlist/list');">수배표 조회</a>
					</li>
					<li>
<!-- 						<a href="login.html">의뢰서 조회</a> -->
						<a onclick="moveToPage(this, '/document/listRequestDocument');">의뢰서 조회</a>
					</li>
					<!-- 					<li> -->
					<!-- 						<a href="login_two_columns.html">문서 등록</a> -->
					<!-- 					</li> -->
					<!-- 					<li> -->
					<!-- 						<a href="forgot_password.html">산출물 등록</a> -->
					<!-- 					</li> -->
					<!-- 					<li> -->
					<!-- 						<a href="register.html">수배표 등록</a> -->
					<!-- 					</li> -->
					<!-- 					<li> -->
					<!-- 						<a href="404.html">의뢰서 등록</a> -->
					<!-- 					</li> -->
					<li>
						<a href="500.html">문서 결재</a>
					</li>
					<li>
<!-- 						<a href="empty_page.html">첨부파일 조회</a> -->
						<a onclick="moveToPage(this, '/document/listContents');">첨부파일 조회</a>
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
						<a onclick="moveToPage(this, '/korea/list');">한국 생산</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/cssheet/list');">CS SHEET 조회</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/cip/list');">CIP 조회</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/history/list');">이력 관리 조회</a>
					</li>
				</ul>
			</li>
			<li>
				<a href="css_animation.html">
					<i class="fa fa-magic"></i>
					<span class="nav-label">관리자</span>
					<span class="fa arrow"></span>
				</a>
				<ul class="nav nav-second-level collapse">
					<li>
						<a onclick="moveToPage(this, '/commonCode/list');">코드 관리</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/spec/list');">사양 관리</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/category/list');">CS 카테고리</a>
					</li>
					<li>
						<a onclick="moveToPage(this, '/password/list');">비밀번호 세팅</a>
					</li>
					<li>
						<a href="#">설치장소 생성</a>
					</li>
					<li>
						<a href="#">산출물 연동</a>
					</li>
					<li>
						<a href="#">수배표 연동</a>
					</li>
					<li>
						<a href="#">품목 연동</a>
					</li>
					<li>
						<a href="#">UNIT BOM 연동</a>
					</li>
				</ul>
			</li>
		</ul>
	</div>
</nav>
