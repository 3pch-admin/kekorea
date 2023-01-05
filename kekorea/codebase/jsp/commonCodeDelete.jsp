<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.query.*"%>
<%@page import="e3ps.common.code.service.CommonCodeHelper"%>
<%@page import="e3ps.common.code.CommonCode"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%!
public CommonCode getCommonCode(String type,String code) throws Exception {
	try {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(CommonCode.class, true);

		query.appendWhere(new SearchCondition(CommonCode.class, CommonCode.CODE_TYPE, SearchCondition.EQUAL, type),
				new int[] { idx });
		query.appendAnd();
		query.appendWhere(new SearchCondition(CommonCode.class, CommonCode.CODE, SearchCondition.EQUAL, code),
				new int[] { idx });

		ClassAttribute ca = new ClassAttribute(CommonCode.class, CommonCode.SORT);
		OrderBy orderBy = new OrderBy(ca, false);
		query.appendOrderBy(orderBy, new int[] { idx });

		QueryResult result = PersistenceHelper.manager.find(query);
		if(result.size()==1){
			if (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				CommonCode rst = (CommonCode) obj[0];
				return rst;
			}
		}
	} catch (Exception e) {
		e.printStackTrace();
	}
	return null;
}
%>
<%

	String oid = "";
	CommonCode code =getCommonCode("INSTALL","S2-PJT");
	if(null!=code){
		PersistenceHelper.manager.delete(code);
	}

%>