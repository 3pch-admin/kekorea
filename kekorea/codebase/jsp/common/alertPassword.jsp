<%@page import="e3ps.project.Project"%>
<%@page import="e3ps.project.ProjectOutputLink"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.doc.dto.DocumentViewData"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
%>

<td valign="top">
	<script type="text/javascript">
	$(document).ready(function() {
	})
	</script>
	<table class="btn_table">
		<tr>
			<td>
				<div class="header_title">
					<i class="axi axi-subtitles"></i><span>비밀번호 변경 알림</span>
				</div>
			</td>
			<td>
				<div class="right">
					<input type="button" value="닫기" id="closeDocBtn" title="닫기" class="redBtn">
				</div>
			</td>
		</tr>
	</table>
	
	<table class="view_table">
		<tr>
			<th>제목</th>
			<td colspan="3">[정기알림]PLM 계정 비밀번호를 변경하세요.</td>
		</tr>			
		<tr>
			<th>내용</th>
			<td colspan="3">
				<textarea rows="3" cols="" class="AXTextarea bgk" style="height: 150px !important;" readonly="readonly"> 비밀번호 변경 기간이 되었습니다.
 PLM에 접속하셔서 비밀번호를 변경해주세요.
 
 ※PLM 비밀번호 변경 경로
 PLM-나의업무-비밀번호 변경</textarea>
			</td>			
		</tr>		
	</table>
</td>