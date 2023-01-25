package e3ps.common.util;

import e3ps.epm.ViewerData;
import wt.query.ClassAttribute;
import wt.query.ColumnExpression;
import wt.query.ConstantExpression;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SQLFunction;
import wt.query.SearchCondition;

public class QuerySpecUtils {

	private QuerySpecUtils() {

	}

	public static void appendEquals(QuerySpec query, int idx, Class clazz, String column, String value)
			throws Exception {
		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}
		ClassAttribute ca = new ClassAttribute(clazz, column);
		ColumnExpression ce = ConstantExpression.newExpression(value.toUpperCase());
		SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
		SearchCondition sc = new SearchCondition(function, SearchCondition.LIKE, ce);
		query.appendWhere(sc, new int[] { idx });
	}

	public static void appendLike(QuerySpec query, int idx, Class clazz, String column, String value) throws Exception {
		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}
		ClassAttribute ca = new ClassAttribute(clazz, column);
		ColumnExpression ce = ConstantExpression.newExpression("%" + value.toUpperCase() + "%");
		SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
		SearchCondition sc = new SearchCondition(function, SearchCondition.LIKE, ce);
		query.appendWhere(sc, new int[] { idx });
	}

	public static void appendOrderBy(QuerySpec query, int idx, Class clazz, String column, boolean sort)
			throws Exception {
		ClassAttribute ca = new ClassAttribute(clazz, column);
		OrderBy orderBy = new OrderBy(ca, sort);
		query.appendOrderBy(orderBy, new int[] { idx });
	}

	public static void appendBoolean(QuerySpec query) throws Exception {

		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}

	}

}
