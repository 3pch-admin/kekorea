<%@page import="e3ps.doc.E3PSDocumentMaster"%>
<%@page import="wt.query.KeywordExpression"%>
<%@page import="wt.introspection.WTIntrospector"%>
<%@page import="wt.introspection.ClassInfo"%>
<%@page import="wt.query.TableExpression"%>
<%@page import="wt.query.OrderBy"%>
<%@page import="e3ps.doc.column.DocumentColumnData"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="e3ps.doc.PRJDocument"%>
<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="wt.query.SQLFunction"%>
<%@page import="wt.query.ColumnExpression"%>
<%@page import="e3ps.common.util.CommonUtils"%>
<%@page import="wt.vc.VersionControlHelper"%>
<%@page import="wt.fc.QueryResult"%>
<%@page import="wt.fc.PersistenceHelper"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="e3ps.doc.column.OutputColumnData"%>
<%@page import="wt.fc.PagingQueryResult"%>
<%@page import="e3ps.common.util.PageQueryUtils"%>
<%@page import="wt.vc.wip.WorkInProgressHelper"%>
<%@page import="wt.util.WTAttributeNameIfc"%>
<%@page import="wt.query.ClassAttribute"%>
<%@page import="wt.query.SearchCondition"%>
<%@page import="e3ps.project.Project"%>
<%@page import="e3ps.project.Output"%>
<%@page import="e3ps.project.ProjectOutputLink"%>
<%@page import="e3ps.project.DocumentOutputLink"%>
<%@page import="wt.doc.WTDocumentMaster"%>
<%@page import="wt.doc.WTDocument"%>
<%@page import="wt.query.QuerySpec"%>
<%!
public static void addLastVersionCondition(QuerySpec query, int idx) throws Exception {

	
	
	TableExpression tableExpr = query.getFromClause().getTableExpressionAt(idx);
	Class target = tableExpr.getTableClass();

	ClassInfo var2 = WTIntrospector.getClassInfo(target);
	String tableName = var2.getDatabaseInfo().getBaseTableInfo().getTablename();
	String columnName = var2.getDatabaseInfo().getBaseTableInfo().getColumnDescriptor("versionInfo.identifier.versionSortId").getColumnName();
	if (query.getConditionCount() > 0) {
		query.appendAnd();
	}
	
	System.out.println("### alias ="+query.getFromClause().getAliasAt(idx).toString()+"##");
	System.out.println("### tableName ="+tableName+"##");
	
	if( "WTDocument".equals(tableName)){
		int edm = query.appendClassList(E3PSDocumentMaster.class, false);
		
		query.appendWhere(new SearchCondition(
				new KeywordExpression(query.getFromClause().getAliasAt(idx) + "." + columnName), "=",
				new KeywordExpression("(SELECT MAX(" + columnName + ") FROM " + tableName + " WHERE "
						+ query.getFromClause().getAliasAt(idx) + ".IDA3MASTERREFERENCE = IDA3MASTERREFERENCE)")));
		
	}else{
	
	query.appendWhere(new SearchCondition(
			new KeywordExpression(query.getFromClause().getAliasAt(idx) + "." + columnName), "=",
			new KeywordExpression("(SELECT MAX(" + columnName + ") FROM " + tableName + " WHERE "
					+ query.getFromClause().getAliasAt(idx) + ".IDA3MASTERREFERENCE = IDA3MASTERREFERENCE)")));
	}
			
}
%>
<%

ReferenceFactory rf = new ReferenceFactory();
	
QuerySpec query = new QuerySpec();

int idx = query.appendClassList(WTDocument.class, true);
int master = query.appendClassList(WTDocumentMaster.class, false);

SearchCondition sc = null;
ClassAttribute ca = null;


sc = WorkInProgressHelper.getSearchCondition_CI(WTDocument.class);
query.appendWhere(sc, new int[] { idx });
query.appendAnd();

sc = new SearchCondition(WTDocument.class, "masterReference.key.id", WTDocumentMaster.class,
		"thePersistInfo.theObjectIdentifier.id");
query.appendWhere(sc, new int[] { idx, master });


// 대소문자 구분
String number = "CM-1103-0002";
	if (query.getConditionCount() > 0)
		query.appendAnd();
	ca = new ClassAttribute(WTDocument.class, WTDocument.NUMBER);
	ColumnExpression ce = StringUtils.getUpperColumnExpression(number);
	SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
	sc = new SearchCondition(function, SearchCondition.LIKE, ce);
	query.appendWhere(sc, new int[] { idx });


	if (query.getConditionCount() > 0)
		query.appendAnd();
	sc = VersionControlHelper.getSearchCondition(WTDocument.class, true);
	query.appendWhere(sc, new int[] { idx });

	//addLastVersionCondition(query, idx);
	
	//query.setAdvancedQueryEnabled(true);
	//query.setDescendantQuery(false);


QueryResult result = PersistenceHelper.manager.find(query);
out.println("<br>"+query);
out.println("<br>"+result.size());
while (result.hasMoreElements()) {
	Object[] obj = (Object[]) result.nextElement();
	WTDocument document = (WTDocument) obj[0];
	DocumentColumnData data = new DocumentColumnData(document);
	out.println("<br>"+document.getNumber()+"=="+document);	
}


	%>
	
