package e3ps.common.util;

import java.sql.Timestamp;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import wt.enterprise.RevisionControlled;
import wt.fc.Persistable;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.StringValue;
import wt.org.WTUser;
import wt.query.ArrayExpression;
import wt.query.ClassAttribute;
import wt.query.ColumnExpression;
import wt.query.ConstantExpression;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SQLFunction;
import wt.query.SearchCondition;
import wt.util.WTAttributeNameIfc;
import wt.vc.ControlBranch;
import wt.vc.VersionControlHelper;
import wt.vc.wip.WorkInProgressHelper;

public class QuerySpecUtils {

	private QuerySpecUtils() {

	}

	/**
	 * 도면, 문서 기타 등 윈칠에서 관리하는 객체에 대한 최신 버전 쿼리문 작성
	 */
	public static void toLatest(QuerySpec query, int idx, Class clazz) throws Exception {
		int branchIdx = query.appendClassList(ControlBranch.class, false);
		int childBranchIdx = query.appendClassList(ControlBranch.class, false);

		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}

		SearchCondition sc = new SearchCondition(clazz, RevisionControlled.BRANCH_IDENTIFIER, ControlBranch.class,
				WTAttributeNameIfc.ID_NAME);
		query.appendWhere(sc, new int[] { idx, branchIdx });

		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}

		SearchCondition outerJoin = new SearchCondition(ControlBranch.class, WTAttributeNameIfc.ID_NAME,
				ControlBranch.class, "predecessorReference.key.id");
		outerJoin.setOuterJoin(SearchCondition.RIGHT_OUTER_JOIN);
		query.appendWhere(outerJoin, new int[] { branchIdx, childBranchIdx });

		ClassAttribute ca = new ClassAttribute(ControlBranch.class, WTAttributeNameIfc.ID_NAME);
		query.appendSelect(ca, new int[] { childBranchIdx }, false);

		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}

		sc = new SearchCondition(ca, SearchCondition.IS_NULL);
		query.appendWhere(sc, new int[] { childBranchIdx });

	}

	/**
	 * 도면, 문서 기타 등 윈칠에서 관리하는 객체에 대한 최신 이터레이션 쿼리문 작성
	 */
	public static void toIteration(QuerySpec query, int idx, Class clazz) throws Exception {
		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}
		SearchCondition sc = VersionControlHelper.getSearchCondition(clazz, true);
		query.appendWhere(sc, new int[] { idx });
	}

	/**
	 * 쿼리문에 equals (and) 조건 추가
	 */
	public static void toEqualsAnd(QuerySpec query, int idx, Class clazz, String column, Object value)
			throws Exception {
		if (value == null) {
			return;
		}

		SearchCondition sc = null;
		if (value instanceof String) {
			String param = (String) value;
			if (StringUtils.isNull(param)) {
				return;
			}
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			sc = new SearchCondition(clazz, column, SearchCondition.EQUAL, param);
		} else if (value instanceof Long) {
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			sc = new SearchCondition(clazz, column, SearchCondition.EQUAL, (long) value);
		} else if (value instanceof Integer) {
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			sc = new SearchCondition(clazz, column, SearchCondition.EQUAL, (int) value);
		} else if (value instanceof Persistable) {
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			Persistable per = (Persistable) value;
			long id = per.getPersistInfo().getObjectIdentifier().getId();
			sc = new SearchCondition(clazz, column, SearchCondition.EQUAL, id);
		}
		query.appendWhere(sc, new int[] { idx });
	}

	/**
	 * 쿼리문에 equals (or) 조건 추가
	 */
	public static void toEqualsOr(QuerySpec query, int idx, Class clazz, String column, Object value) throws Exception {
		if (value == null) {
			return;
		}

		SearchCondition sc = null;
		if (value instanceof String) {
			String param = (String) value;
			if (StringUtils.isNull(param)) {
				return;
			}
			if (query.getConditionCount() > 0) {
				query.appendOr();
			}
			sc = new SearchCondition(clazz, column, SearchCondition.EQUAL, param);
		} else if (value instanceof Long) {
			if (query.getConditionCount() > 0) {
				query.appendOr();
			}
			sc = new SearchCondition(clazz, column, SearchCondition.EQUAL, (long) value);
		} else if (value instanceof Integer) {
			if (query.getConditionCount() > 0) {
				query.appendOr();
			}
			sc = new SearchCondition(clazz, column, SearchCondition.EQUAL, (int) value);
		} else if (value instanceof Persistable) {
			if (query.getConditionCount() > 0) {
				query.appendOr();
			}
			Persistable per = (Persistable) value;
			long id = per.getPersistInfo().getObjectIdentifier().getId();
			sc = new SearchCondition(clazz, column, SearchCondition.EQUAL, id);
		}
		query.appendWhere(sc, new int[] { idx });
	}

	/**
	 * 쿼리문에 '%' like (or) 조건 추가
	 */
	public static void toLikeLeftOr(QuerySpec query, int idx, Class clazz, String column, String value)
			throws Exception {
		if (StringUtils.isNull(value)) {
			return;
		}
		if (query.getConditionCount() > 0) {
			query.appendOr();
		}
		ClassAttribute ca = new ClassAttribute(clazz, column);
		ColumnExpression ce = ConstantExpression.newExpression("%" + value.toUpperCase());
		SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
		SearchCondition sc = new SearchCondition(function, SearchCondition.LIKE, ce);
		query.appendWhere(sc, new int[] { idx });
	}

	/**
	 * 쿼리문에 like '%' (or) 조건 추가
	 */
	public static void toLikeRightOr(QuerySpec query, int idx, Class clazz, String column, String value)
			throws Exception {
		if (StringUtils.isNull(value)) {
			return;
		}
		if (query.getConditionCount() > 0) {
			query.appendOr();
		}
		ClassAttribute ca = new ClassAttribute(clazz, column);
		ColumnExpression ce = ConstantExpression.newExpression(value.toUpperCase() + "%");
		SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
		SearchCondition sc = new SearchCondition(function, SearchCondition.LIKE, ce);
		query.appendWhere(sc, new int[] { idx });
	}

	/**
	 * 쿼리문에 '%' like '%' (or) 조건 추가
	 */
	public static void toLikeOr(QuerySpec query, int idx, Class clazz, String column, String value) throws Exception {
		if (StringUtils.isNull(value)) {
			return;
		}
		if (query.getConditionCount() > 0) {
			query.appendOr();
		}
		ClassAttribute ca = new ClassAttribute(clazz, column);
		ColumnExpression ce = ConstantExpression.newExpression("%" + value.toUpperCase() + "%");
		SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
		SearchCondition sc = new SearchCondition(function, SearchCondition.LIKE, ce);
		query.appendWhere(sc, new int[] { idx });
	}

	/**
	 * 쿼리문에 '%' like (and) 조건 추가
	 */
	public static void toLikeLeftAnd(QuerySpec query, int idx, Class clazz, String column, String value)
			throws Exception {
		if (StringUtils.isNull(value)) {
			return;
		}
		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}
		ClassAttribute ca = new ClassAttribute(clazz, column);
		ColumnExpression ce = ConstantExpression.newExpression("%" + value.toUpperCase());
		SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
		SearchCondition sc = new SearchCondition(function, SearchCondition.LIKE, ce);
		query.appendWhere(sc, new int[] { idx });
	}

	/**
	 * 쿼리문에 like '%' (and) 조건 추가
	 */
	public static void toLikeRightAnd(QuerySpec query, int idx, Class clazz, String column, String value)
			throws Exception {
		if (StringUtils.isNull(value)) {
			return;
		}
		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}
		ClassAttribute ca = new ClassAttribute(clazz, column);
		ColumnExpression ce = ConstantExpression.newExpression(value.toUpperCase() + "%");
		SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
		SearchCondition sc = new SearchCondition(function, SearchCondition.LIKE, ce);
		query.appendWhere(sc, new int[] { idx });
	}

	/**
	 * 쿼리문에 '%' like '%' (and) 조건 추가
	 */
	public static void toLikeAnd(QuerySpec query, int idx, Class clazz, String column, String value) throws Exception {
		if (StringUtils.isNull(value)) {
			return;
		}

		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}
		ClassAttribute ca = new ClassAttribute(clazz, column);
		ColumnExpression ce = ConstantExpression.newExpression("%" + value.toUpperCase() + "%");
		SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
		SearchCondition sc = new SearchCondition(function, SearchCondition.LIKE, ce);
		query.appendWhere(sc, new int[] { idx });
	}

	/**
	 * 쿼리문에 order by 조건 추가
	 */
	public static void toOrderBy(QuerySpec query, int idx, Class clazz, String column, boolean sort) throws Exception {
		ClassAttribute ca = new ClassAttribute(clazz, column);
		OrderBy orderBy = new OrderBy(ca, sort);
		query.appendOrderBy(orderBy, new int[] { idx });
	}

	/**
	 * 쿼리문에 boolean 조건 추가
	 */
	public static void toBoolean(QuerySpec query, int idx, Class clazz, String column, boolean value) throws Exception {
		SearchCondition sc = null;
		if (value) {
			sc = new SearchCondition(clazz, column, SearchCondition.IS_TRUE);
		} else {
			sc = new SearchCondition(clazz, column, SearchCondition.IS_FALSE);
		}
		query.appendWhere(sc, new int[] { idx });
	}

	/**
	 * 쿼리문에 boolean (and) 조건 추가
	 */
	public static void toBooleanAnd(QuerySpec query, int idx, Class clazz, String column, boolean value)
			throws Exception {
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

	/**
	 * 쿼리문에 boolean (or) 조건 추가
	 */
	public static void toBooleanOr(QuerySpec query, int idx, Class clazz, String column, boolean value)
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

	/**
	 * 쿼리문에 inner join 조건 추가
	 */
	public static void toInnerJoin(QuerySpec query, Class left, Class right, String leftKey, String rightKey,
			int leftIdx, int rightIdx) throws Exception {
		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}
		SearchCondition sc = new SearchCondition(left, leftKey, right, rightKey);
		query.appendWhere(sc, new int[] { leftIdx, rightIdx });
	}

	/**
	 * 쿼리문에 시간 조건 추가 >=
	 */
	public static void toTimeGreaterEqualsThan(QuerySpec query, int idx, Class clazz, String column, String time)
			throws Exception {
		if (StringUtils.isNull(time)) {
			return;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = sdf.parse(time, new ParsePosition(0));
		Timestamp fromTime = new Timestamp(date.getTime());
		toTimeGreaterEqualsThan(query, idx, clazz, column, fromTime);
	}

	/**
	 * 쿼리문에 시간 조건 추가 >=
	 */
	public static void toTimeGreaterEqualsThan(QuerySpec query, int idx, Class clazz, String column, Timestamp time)
			throws Exception {
		if (time == null) {
			return;
		}
		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}
		SearchCondition sc = new SearchCondition(clazz, column, SearchCondition.GREATER_THAN_OR_EQUAL, time);
		query.appendWhere(sc, new int[] { idx });
	}

	/**
	 * 쿼리문에 시간 조건 추가 <=
	 */
	public static void toTimeLessEqualsThan(QuerySpec query, int idx, Class clazz, String column, String time)
			throws Exception {
		if (StringUtils.isNull(time)) {
			return;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = sdf.parse(time + " 23:59:59", new ParsePosition(0));
		Timestamp toTime = new Timestamp(date.getTime());
		toTimeLessEqualsThan(query, idx, clazz, column, toTime);
	}

	/**
	 * 쿼리문에 시간 조건 추가 <=
	 */
	public static void toTimeLessEqualsThan(QuerySpec query, int idx, Class clazz, String column, Timestamp time)
			throws Exception {
		if (time == null) {
			return;
		}
		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}
		SearchCondition sc = new SearchCondition(clazz, column, SearchCondition.LESS_THAN_OR_EQUAL, time);
		query.appendWhere(sc, new int[] { idx });
	}

	/**
	 * 쿼리문에 시간 조건 추가 >
	 */
	public static void toTimeGreaterThan(QuerySpec query, int idx, Class clazz, String column, String time)
			throws Exception {
		if (StringUtils.isNull(time)) {
			return;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = sdf.parse(time, new ParsePosition(0));
		Timestamp fromTime = new Timestamp(date.getTime());
		toTimeGreaterThan(query, idx, clazz, column, fromTime);
	}

	/**
	 * 쿼리문에 시간 조건 추가 >
	 */
	public static void toTimeGreaterThan(QuerySpec query, int idx, Class clazz, String column, Timestamp time)
			throws Exception {
		if (time == null) {
			return;
		}
		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}
		SearchCondition sc = new SearchCondition(clazz, column, SearchCondition.GREATER_THAN, time);
		query.appendWhere(sc, new int[] { idx });
	}

	/**
	 * 쿼리문에 시간 조건 추가 <
	 */
	public static void toTimeLessThan(QuerySpec query, int idx, Class clazz, String column, String time)
			throws Exception {
		if (StringUtils.isNull(time)) {
			return;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = sdf.parse(time + " 23:59:59", new ParsePosition(0));
		Timestamp toTime = new Timestamp(date.getTime());
		toTimeLessThan(query, idx, clazz, column, toTime);
	}

	/**
	 * 쿼리문에 시간 조건 추가 <
	 */
	public static void toTimeLessThan(QuerySpec query, int idx, Class clazz, String column, Timestamp time)
			throws Exception {
		if (time == null) {
			return;
		}
		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}
		SearchCondition sc = new SearchCondition(clazz, column, SearchCondition.LESS_THAN, time);
		query.appendWhere(sc, new int[] { idx });
	}

	/**
	 * 쿼리문에 not equals (and) 조건 추가
	 */
	public static void toNotEqualsAnd(QuerySpec query, int idx, Class clazz, String column, Object value)
			throws Exception {
		if (value == null) {
			return;
		}

		SearchCondition sc = null;
		if (value instanceof String) {
			String param = (String) value;
			if (StringUtils.isNull(param)) {
				return;
			}
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			sc = new SearchCondition(clazz, column, SearchCondition.NOT_EQUAL, param);
		} else if (value instanceof Long) {
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			sc = new SearchCondition(clazz, column, SearchCondition.NOT_EQUAL, (long) value);
		} else if (value instanceof Integer) {
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			sc = new SearchCondition(clazz, column, SearchCondition.NOT_EQUAL, (int) value);
		} else if (value instanceof Persistable) {
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			Persistable per = (Persistable) value;
			long id = per.getPersistInfo().getObjectIdentifier().getId();
			sc = new SearchCondition(clazz, column, SearchCondition.NOT_EQUAL, id);
		}
		query.appendWhere(sc, new int[] { idx });
	}

	/**
	 * 쿼리문에 체크인 된 객체만 가져오는 조건 추가
	 */
	public static void toCI(QuerySpec query, int idx, Class clazz) throws Exception {
		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}
		SearchCondition sc = WorkInProgressHelper.getSearchCondition_CI(clazz);
		query.appendWhere(sc, new int[] { idx });
	}

	/**
	 * 부품, 도면등 기타 IBA값을 사용하는곳에 잇어서 equals (and) 조건 추가
	 */
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

	/**
	 * Ownable.class 인터페이스를 구현한 객체에 대해서만 사용할 수 있는 작성사 검색 쿼리
	 */
	public static void toCreator(QuerySpec query, int idx, Class clazz, String oid) throws Exception {
		if (StringUtils.isNull(oid)) {
			return;
		}
		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}
		WTUser creator = (WTUser) CommonUtils.getObject(oid);
		SearchCondition sc = new SearchCondition(clazz, "ownership.owner.key.id", "=",
				creator.getPersistInfo().getObjectIdentifier().getId());
		query.appendWhere(sc, new int[] { idx });
	}

	/**
	 * 객체 작성일 검색 From ~ To
	 */
	public static void toTimeGreaterAndLess(QuerySpec query, int idx, Class clazz, String column, String createdFrom,
			String createdTo) throws Exception {
		if (!StringUtils.isNull(createdFrom)) {
			toTimeGreaterEqualsThan(query, idx, clazz, column, createdFrom);
		}

		if (!StringUtils.isNull(createdTo)) {
			toTimeLessEqualsThan(query, idx, clazz, column, createdTo);
		}
	}

	public static void toIn(QuerySpec query, int idx, Class clazz, String column, Object value) throws Exception {
		if (value == null) {
			return;
		}
		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}

		SearchCondition sc = null;
		ClassAttribute ca = new ClassAttribute(clazz, column);
		if (value instanceof long[]) {
			sc = new SearchCondition(ca, SearchCondition.IN, new ArrayExpression((long[]) value));
			query.appendWhere(sc, new int[] { idx });
		} else if (value instanceof String[]) {
			sc = new SearchCondition(ca, SearchCondition.IN, new ArrayExpression((String[]) value));
			query.appendWhere(sc, new int[] { idx });
		}
	}
}
