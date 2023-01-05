<%-- <%@page import="wt.util.WTException"%>
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
/*프로젝트에서 kekstate가 '설계 변경'인 얘들을 '설계변경' */
/*쿼리스펙으로 모두 가져와서. 가져온 객체마다 modify*/
String result = "변경 실패";	

QuerySpec qs = new QuerySpec();
int idx = qs.appendClassList(Project.class, true);

SearchCondition sc = new SearchCondition(Project.class, Project.KEK_STATE, SearchCondition.EQUAL, "설계 완료");
qs.appendWhere(sc, new int[] {idx});

QueryResult qr = PersistenceHelper.manager.find((StatementSpec)qs);

ArrayList<Project> alPro = new ArrayList<Project>();
int i  = 0;
while(qr.hasMoreElements()){
	Object[] arrObj = (Object[])qr.nextElement() ;
	Project project = (Project)arrObj[0];	
	
	ProjectColumnData data = new ProjectColumnData(project);
	alPro.add(i, project);
	i++;
}

Transaction trx = null;
try{
	 trx = new Transaction();
	
	 for(int j = 0; j<alPro.size(); j++){
		 Project pro =  alPro.get(j);
		 pro.setKekState("설계완료");
		 PersistenceHelper.manager.modify(pro);
	 }
	
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
<%for(int j = 0; j<alPro.size(); j++){%>
<%=PersistenceHelper.getObjectIdentifier(alPro.get(j)).getStringValue()%><label>&nbsp;&nbsp;&nbsp;==&gt;&nbsp;&nbsp;&nbsp;</label><%=alPro.get(j).getKekNumber()%><br/>
<%}%> --%>