<%-- <%@page import="e3ps.common.util.CommonUtils"%>
<%@page import="wt.org.WTUser"%>
<%@page import="wt.session.SessionHelper"%>
<%@page import="e3ps.org.People"%>
<%@page import="wt.util.WTException"%>
<%@page import="wt.pom.Transaction"%>
<%@page import="java.util.ArrayList"%>
<%@page import="e3ps.project.column.ProjectColumnData"%>
<%@page import="wt.pds.StatementSpec"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="com.ptc.netmarkets.projectActivity.projectActivityResource"%>
<%@page import="wt.query.SearchCondition"%>
<%@page import="e3ps.project.Project"%>
<%@page import="wt.query.QuerySpec"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%

//wt.org.WTUser:150780
//wt.org.WTUser:150792
String result = "";
Transaction trx = null;
People p = null;
WTUser u = null;
try{
	 trx = new Transaction();
	
	u =  (WTUser)CommonUtils.getObject("wt.org.WTUser:150792");
	 p = People.newPeople();
		p.setId(u.getName());
		p.setEmail(u.getEMail());
		//p.setDuty(duty);
		//p.setRank(duty);
		p.setName(u.getFullName());
		//p.setDepartment(department);
		p.setUser(u);
		PersistenceHelper.manager.save(p);
	
	 
	 
	trx.commit();
	result = "변경 완료";
}catch(WTException wte){
	wte.printStackTrace();
	result += wte.getLocalizedMessage();
}catch(Exception e){
	e.printStackTrace();
	result += e.getLocalizedMessage();
}finally{
	if(trx!=null){
		trx.rollback();
		trx = null;
	}
}
%>
<%=result%><br/>
<label>변경 리스트</label><br/>
 --%>