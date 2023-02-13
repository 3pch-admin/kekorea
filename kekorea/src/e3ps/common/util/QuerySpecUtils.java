package e3ps.common.util;

import java.sql.Timestamp;

import e3ps.admin.spec.Spec;
import e3ps.admin.spec.SpecOptionsLink;
import e3ps.epm.ViewerData;
import wt.query.ClassAttribute;
import wt.query.ColumnExpression;
import wt.query.ConstantExpression;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SQLFunction;
import wt.query.SearchCondition;
import wt.util.WTAttributeNameIfc;

public class QuerySpecUtils {

	private QuerySpecUtils() {

	}

	public static void toEqualsAnd(QuerySpec query, int idx, Class clazz, String column, Object value)
			throws Exception {
		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}

		SearchCondition sc = null;
		if (value instanceof String) {
			sc = new SearchCondition(clazz, column, SearchCondition.EQUAL, (String) value);
		} else if (value instanceof Long) {
			sc = new SearchCondition(clazz, column, SearchCondition.EQUAL, (long) value);
		} else if (value instanceof Integer) {
			sc = new SearchCondition(clazz, column, SearchCondition.EQUAL, (int) value);
		}
		query.appendWhere(sc, new int[] { idx });
	}

	public static void toEqualsOr(QuerySpec query, int idx, Class clazz, String column, Object value) throws Exception {
		if (query.getConditionCount() > 0) {
			query.appendOr();
		}

		SearchCondition sc = null;
		if (value instanceof String) {
			sc = new SearchCondition(clazz, column, SearchCondition.EQUAL, (String) value);
		} else if (value instanceof Long) {
			sc = new SearchCondition(clazz, column, SearchCondition.EQUAL, (long) value);
		} else if (value instanceof Integer) {
			sc = new SearchCondition(clazz, column, SearchCondition.EQUAL, (int) value);
		}
		query.appendWhere(sc, new int[] { idx });
	}

	public static void toLikeLeftOr(QuerySpec query, int idx, Class clazz, String column, String value)
			throws Exception {
		if (query.getConditionCount() > 0) {
			query.appendOr();
		}
		ClassAttribute ca = new ClassAttribute(clazz, column);
		ColumnExpression ce = ConstantExpression.newExpression("%" + value.toUpperCase());
		SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
		SearchCondition sc = new SearchCondition(function, SearchCondition.LIKE, ce);
		query.appendWhere(sc, new int[] { idx });
	}

	public static void toLikeRightOr(QuerySpec query, int idx, Class clazz, String column, String value)
			throws Exception {
		if (query.getConditionCount() > 0) {
			query.appendOr();
		}
		ClassAttribute ca = new ClassAttribute(clazz, column);
		ColumnExpression ce = ConstantExpression.newExpression(value.toUpperCase() + "%");
		SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
		SearchCondition sc = new SearchCondition(function, SearchCondition.LIKE, ce);
		query.appendWhere(sc, new int[] { idx });
	}

	public static void toLikeOr(QuerySpec query, int idx, Class clazz, String column, String value) throws Exception {
		if (query.getConditionCount() > 0) {
			query.appendOr();
		}
		ClassAttribute ca = new ClassAttribute(clazz, column);
		ColumnExpression ce = ConstantExpression.newExpression("%" + value.toUpperCase() + "%");
		SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
		SearchCondition sc = new SearchCondition(function, SearchCondition.LIKE, ce);
		query.appendWhere(sc, new int[] { idx });
	}

	public static void toLikeLeftAnd(QuerySpec query, int idx, Class clazz, String column, String value)
			throws Exception {
		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}
		ClassAttribute ca = new ClassAttribute(clazz, column);
		ColumnExpression ce = ConstantExpression.newExpression("%" + value.toUpperCase());
		SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
		SearchCondition sc = new SearchCondition(function, SearchCondition.LIKE, ce);
		query.appendWhere(sc, new int[] { idx });
	}

	public static void toLikeRightAnd(QuerySpec query, int idx, Class clazz, String column, String value)
			throws Exception {
		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}
		ClassAttribute ca = new ClassAttribute(clazz, column);
		ColumnExpression ce = ConstantExpression.newExpression(value.toUpperCase() + "%");
		SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
		SearchCondition sc = new SearchCondition(function, SearchCondition.LIKE, ce);
		query.appendWhere(sc, new int[] { idx });
	}

	public static void toLikeAnd(QuerySpec query, int idx, Class clazz, String column, String value) throws Exception {
		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}
		ClassAttribute ca = new ClassAttribute(clazz, column);
		ColumnExpression ce = ConstantExpression.newExpression("%" + value.toUpperCase() + "%");
		SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
		SearchCondition sc = new SearchCondition(function, SearchCondition.LIKE, ce);
		query.appendWhere(sc, new int[] { idx });
	}

	public static void toOrderBy(QuerySpec query, int idx, Class clazz, String column, boolean sort) throws Exception {
		ClassAttribute ca = new ClassAttribute(clazz, column);
		OrderBy orderBy = new OrderBy(ca, sort);
		query.appendOrderBy(orderBy, new int[] { idx });
	}

	public static void toBoolean(QuerySpec query, int idx, Class clazz, String column, String value) throws Exception {
		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}
		SearchCondition sc = new SearchCondition(clazz, column, value.toUpperCase());
		query.appendWhere(sc, new int[] { idx });
	}

	public static void toInnerJoin(QuerySpec query, Class left, Class right, String leftKey, String rightKey,
			int leftIdx, int rightIdx) throws Exception {
		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}
		SearchCondition sc = new SearchCondition(left, leftKey, right, rightKey);
		query.appendWhere(sc, new int[] { leftIdx, rightIdx });
	}

	public static void toTimeGreaterThan(QuerySpec query, int idx, Class clazz, String column, Timestamp time)
			throws Exception {
		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}
		SearchCondition sc = new SearchCondition(clazz, column, SearchCondition.GREATER_THAN, time);
		query.appendWhere(sc, new int[] { idx });
	}

	public static void toTimeLessThan(QuerySpec query, int idx, Class clazz, String column, Timestamp time)
			throws Exception {
		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}
		SearchCondition sc = new SearchCondition(clazz, column, SearchCondition.LESS_THAN, time);
		query.appendWhere(sc, new int[] { idx });
	}

	public static void toNotEquals(QuerySpec query, int idx, Class clazz, String column, String value)
			throws Exception {
		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}
		SearchCondition sc = new SearchCondition(clazz, column, SearchCondition.NOT_EQUAL, value);
		query.appendWhere(sc, new int[] { idx });
	}
}