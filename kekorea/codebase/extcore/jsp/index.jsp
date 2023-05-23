<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="row border-bottom">
	<nav class="navbar navbar-static-top" role="navigation" style="margin-bottom: 0">
		<div class="navbar-header">
			<a class="navbar-minimalize minimalize-styl-2 btn btn-primary " href="#" id="toggle" data-open="open">
				<i class="fa fa-bars"></i>
			</a>
			<span class="loc">
				<strong>홈</strong> 
				<span id="subLoc">> 메인</span>
			</span>
		</div>
		<ul class="nav navbar-top-links navbar-right">
			<li>
				<a href="javascript:logout();" title="로그아웃">
					<i class="fa fa-sign-out"></i>
					<b>로그아웃</b>
				</a>
			</li>
		</ul>
	</nav>
</div>
<iframe src="/Windchill/plm/firstPage" id="content"></iframe>