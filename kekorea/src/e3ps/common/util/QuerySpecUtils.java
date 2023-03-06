package e3ps.common.util;

import java.sql.Timestamp;

import e3ps.admin.spec.Spec;
import e3ps.admin.spec.SpecOptionsLink;
import e3ps.epm.ViewerData;
import wt.epm.EPMDocument;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.StringValue;
import wt.query.ClassAttribute;
import wt.query.ColumnExpression;
import wt.query.ConstantExpression;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SQLFunction;
import wt.query.SearchCondition;
import wt.util.WTAttributeNameIfc;
import wt.vc.wip.WorkInProgressHelper;

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

	public static void toBooleanAnd(QuerySpec query, int idx, Class clazz, String column, boolean value) throws Exception {
		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}
		SearchCondition sc = null;
		if (value) {
			sc = new SearchCondition(clazz, column, SearchCondition.IS_TRUE);
		} else {
			sc = new SearchCondition(clazz, column, SearchCondition.IS_FALSE);
		}
		query.appendWhere(sc, new int[] { idx });
	}

	public static void toBooleanAndOr(QuerySpec query, int idx, Class clazz, String column, boolean value)
			throws Exception {
		if (query.getConditionCount() > 0) {
			query.appendOr();
		}
		SearchCondition sc = null;
		if (value) {
			sc = new SearchCondition(clazz, column, SearchCondition.IS_TRUE);
		} else {
			sc = new SearchCondition(clazz, column, SearchCondition.IS_FALSE);
		}
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

	public static void toTimeGreaterEqualsThan(QuerySpec query, int idx, Class clazz, String column, Timestamp time)
			throws Exception {
		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}
		SearchCondition sc = new SearchCondition(clazz, column, SearchCondition.GREATER_THAN_OR_EQUAL, time);
		query.appendWhere(sc, new int[] { idx });
	}

	public static void toTimeLessEqualsThan(QuerySpec query, int idx, Class clazz, String column, Timestamp time)
			throws Exception {
		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}
		SearchCondition sc = new SearchCondition(clazz, column, SearchCondition.LESS_THAN_OR_EQUAL, time);
		query.appendWhere(sc, new int[] { idx });
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

	public static void toCI(QuerySpec query, int idx, Class clazz) throws Exception {
		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}
		SearchCondition sc = WorkInProgressHelper.getSearchCondition_CI(clazz);
		query.appendWhere(sc, new int[] { idx });
		sc = WorkInProgressHelper.getSearchCondition_CI(EPMDocument.class);
	}

	public static void toIBAEquals(QuerySpec query, int idx, Class clazz, String name, String value) throws Exception {
		AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(name);
		if (aview != null) {
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}

			int _idx = query.appendClassList(StringValue.class, false);
			SearchCondition sc = new SearchCondition(
					new ClassAttribute(StringValue.class, "theIBAHolderReference.key.id"), "=",
					new ClassAttribute(clazz, "thePersistInfo.theObjectIdentifier.id"));
			sc.setFromIndicies(new int[] { _idx, idx }, 0);
			sc.setOuterJoin(0);
			query.appendWhere(sc, new int[] { _idx, idx });
			query.appendAnd();
			sc = new SearchCondition(StringValue.class, "definitionReference.key.id", "=", aview.getObjectID().getId());
			query.appendWhere(sc, new int[] { _idx });
			query.appendAnd();
			sc = new SearchCondition(StringValue.class, StringValue.VALUE, "=", value);
			query.appendWhere(sc, new int[] { _idx });
		}
	}

}