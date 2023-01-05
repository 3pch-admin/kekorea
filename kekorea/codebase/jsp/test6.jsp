<%@page import="wt.util.WTPropertyVetoException"%>
<%@page import="wt.enterprise.RevisionControlled"%>
<%@page import="wt.util.WTException"%>
<%@page import="wt.vc.ControlBranch"%>
<%@page import="wt.query.SubSelectExpression"%>
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
			
public static void addLastVersionCondition2(QuerySpec qs, Class targetClass, int idx) throws WTException {
	try {
		int branchIdx = qs.appendClassList(ControlBranch.class, false);
		int childBranchIdx = qs.appendClassList(ControlBranch.class, false);
		
		if (qs.getConditionCount() > 0) qs.appendAnd();
		qs.appendWhere(new SearchCondition(
				targetClass, RevisionControlled.BRANCH_IDENTIFIER, 
				ControlBranch.class, WTAttributeNameIfc.ID_NAME), 
			new int[] {idx, branchIdx});
		
		if (qs.getConditionCount() > 0) qs.appendAnd();
		SearchCondition outerJoinSc = new SearchCondition(
				ControlBranch.class, WTAttributeNameIfc.ID_NAME,
				ControlBranch.class, "predecessorReference.key.id");
		outerJoinSc.setOuterJoin(SearchCondition.RIGHT_OUTER_JOIN);
		qs.appendWhere(outerJoinSc, new int[] {branchIdx, childBranchIdx});
		
		ClassAttribute childBranchIdNameCa = 
				new ClassAttribute(ControlBranch.class, WTAttributeNameIfc.ID_NAME);
		qs.appendSelect(childBranchIdNameCa, new int[] {childBranchIdx}, false);
		
		if (qs.getConditionCount() > 0) qs.appendAnd();
		qs.appendWhere(new SearchCondition(childBranchIdNameCa, SearchCondition.IS_NULL), 
			new int[] {childBranchIdx});
	} catch (WTPropertyVetoException e) {
		throw new WTException(e);
	}
}
%>
<%

ReferenceFactory rf = new ReferenceFactory();
	
QuerySpec query = new QuerySpec();

int idx = query.appendClassList(WTDocument.class, true);
int master = query.appendClassList(WTDocumentMaster.class, false);


query.appendWhere(WorkInProgressHelper.getSearchCondition_CI(WTDocument.class), new int[] { idx });

query.appendAnd();

SearchCondition sc2 = new SearchCondition(WTDocument.class, "masterReference.key.id", WTDocumentMaster.class, "thePersistInfo.theObjectIdentifier.id");
query.appendWhere(sc2, new int[] { idx, master });


// 대소문자 구분
//String number = "CM-1903-0013";
/*	
if (query.getConditionCount() > 0)
		query.appendAnd();
	ClassAttribute ca2 = new ClassAttribute(WTDocument.class, WTDocument.NUMBER);
	ColumnExpression ce = StringUtils.getUpperColumnExpression(number);
	SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca2);
	SearchCondition sc3 = new SearchCondition(function, SearchCondition.LIKE, ce);
	query.appendWhere(sc3, new int[] { idx });
*/
	if (query.getConditionCount() > 0)
		query.appendAnd();
	SearchCondition sc4 = VersionControlHelper.getSearchCondition(WTDocument.class, true);
	query.appendWhere(sc4, new int[] { idx });

	//addLastVersionCondition(query, idx);
	addLastVersionCondition2(query,WTDocument.class,  idx);
	
	query.setAdvancedQueryEnabled(true);
	//query.setDescendantQuery(false);
	query.setDescendantsIncluded(false, master);
	
	String sortKey = "";
	if (StringUtils.isNull(sortKey)) {
		sortKey = WTDocument.MODIFY_TIMESTAMP;
	}
	String sort = "true";

	ClassAttribute ca = new ClassAttribute(WTDocument.class, sortKey);
	OrderBy orderBy = new OrderBy(ca, Boolean.parseBoolean(sort));
	query.appendOrderBy(orderBy, new int[] { idx });

	System.out.println("<br>"+query);
	out.println("<br>"+query);
	QueryResult result = PersistenceHelper.manager.find(query);
	
	out.println("<br>"+result.size());
	while (result.hasMoreElements()) {
		Object[] obj = (Object[]) result.nextElement();
		WTDocument document = (WTDocument) obj[0];
		DocumentColumnData data = new DocumentColumnData(document);
		
		out.println("<br>"+document.getNumber()+"=="+document+"=="+VersionControlHelper.getIterationDisplayIdentifier(document).toString()+"//"+document.getMaster());	
	}


	%>
	
