package e3ps.common.util;

import java.rmi.RemoteException;
import java.util.Locale;

import wt.access.NotAuthorizedException;
import wt.clients.folder.FolderTaskLogic;
import wt.enterprise.RevisionControlled;
import wt.fc.WTObject;
import wt.folder.Folder;
import wt.iba.definition.IBADefinitionException;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.StringValue;
import wt.introspection.ClassInfo;
import wt.introspection.LinkInfo;
import wt.introspection.WTIntrospectionException;
import wt.introspection.WTIntrospector;
import wt.org.WTUser;
import wt.pds.ClassJoinCondition;
import wt.pds.QuerySpecStatementBuilder;
import wt.query.AttributeSearchSpecification;
import wt.query.ClassAttribute;
import wt.query.ClassTableExpression;
import wt.query.ClassViewExpression;
import wt.query.ColumnExpression;
import wt.query.ConstantExpression;
import wt.query.ExistsExpression;
import wt.query.NegatedExpression;
import wt.query.OrderBy;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SQLFunction;
import wt.query.SearchCondition;
import wt.query.SearchTask;
import wt.query.StringSearch;
import wt.query.TableExpression;
import wt.util.SortedEnumeration;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.ControlBranch;

public class SearchUtils {
	public static String IBA_PREFIX = "IBA.";
	public static String USER_PREFIX = "USER.";
	public static String CODE_SEPARATOR = "|";

	/**
	 * appendLinkJoin
	 */
	public static QuerySpec appendLinkJoin(QuerySpec spec, int roleAClassPos, int roleBClassPos, String linkClassName,
			boolean isLinkOuterJoin, boolean isTargetOuterJoin)
			throws ClassNotFoundException, WTPropertyVetoException, WTIntrospectionException {
		QuerySpecStatementBuilder builder = (QuerySpecStatementBuilder) spec.getStatementBuilder();
		LinkInfo linkinfo = WTIntrospector.getLinkInfo(linkClassName);
		Class linkInfoClass = linkinfo.getBusinessClass();
		int k = spec.appendFrom(((wt.query.TableExpression) (linkinfo.isConcrete()
				? ((wt.query.TableExpression) (new ClassViewExpression(linkInfoClass)))
				: ((wt.query.TableExpression) (new ClassTableExpression(linkInfoClass))))));

		ClassJoinCondition classjoincondition = new ClassJoinCondition(k, linkinfo.getRoleA().getName(), roleAClassPos);
		classjoincondition.setLinkOuterJoin(isLinkOuterJoin);
		classjoincondition.setTargetOuterJoin(isTargetOuterJoin);
		builder.appendJoin(classjoincondition);
		classjoincondition = new ClassJoinCondition(k, linkinfo.getRoleB().getName(), roleBClassPos);
		classjoincondition.setLinkOuterJoin(isTargetOuterJoin);
		classjoincondition.setTargetOuterJoin(isLinkOuterJoin);
		builder.appendJoin(classjoincondition);
		QuerySpec returnSpec = builder.getQuerySpec();
		return returnSpec;
	}

	/**
	 * appendLinkRoleJoin
	 */
	public static QuerySpec appendLinkRoleJoin(QuerySpec spec, int targetClassPos, int linkClassPos, String rolename)
			throws ClassNotFoundException, WTPropertyVetoException {
		QuerySpecStatementBuilder builder = (QuerySpecStatementBuilder) spec.getStatementBuilder();
		ClassJoinCondition classjoincondition = new ClassJoinCondition(linkClassPos, rolename, targetClassPos);
		classjoincondition.setLinkOuterJoin(false);
		classjoincondition.setTargetOuterJoin(false);
		builder.appendJoin(classjoincondition);
		QuerySpec returnSpec = builder.getQuerySpec();
		return returnSpec;
	}

	/**
	 * getSearchCondition param count: 4
	 */
	public static SearchCondition getSearchCondition(Class targetClass, String fieldName, String searchValue)
			throws WTException {
		return getSearchCondition(targetClass, WTIntrospector.getClassInfo(targetClass), fieldName, searchValue,
				Locale.KOREA);
	}

	/**
	 * getSearchCondition param count: 5
	 */
	public static SearchCondition getSearchCondition(Class class1, ClassInfo classinfo, String s, String s1,
			Locale locale) throws WTException {
		Object obj = SearchTask.getSearchSpecification(s, s1, classinfo, locale);
		String s2 = ((AttributeSearchSpecification) obj).getSearchExpression();

		if (s2.equals(" LIKE ")) {
			try {
				String s3 = (String) ((AttributeSearchSpecification) obj).getValue();
				if (!AttributeSearchSpecification.areThereDBWildCards(s3)) {
					obj = new StringSearch(s, "=");
					((AttributeSearchSpecification) obj).setValue(s1);
				}
			} catch (WTPropertyVetoException wtpropertyvetoexception) {
				Object aobj[] = { class1.toString() + "->" + s };
				throw new WTException(wtpropertyvetoexception, "wt.query.queryResource", "26", aobj);
			}
		}

		try {
			SearchCondition searchcondition = ((AttributeSearchSpecification) obj).getSearchCondition(class1);
			if (searchcondition.getOperator().equals(" LIKE "))
				searchcondition.setOption("escape '/'");
			return searchcondition;
		} catch (WTPropertyVetoException wtpropertyvetoexception1) {
			Object aobj1[] = { class1.toString() + "->" + s };
			throw new WTException(wtpropertyvetoexception1, "wt.query.queryResource", "26", aobj1);
		} catch (QueryException queryexception) {
			Object aobj2[] = { class1.toString() + "->" + s };
			throw new WTException(queryexception, "wt.query.queryResource", "26", aobj2);
		}
	}

	public static void setOrderBy(QuerySpec spec, Class sortingClass, int tableNo, String field, boolean sortingFlag)
			throws WTPropertyVetoException, QueryException, WTIntrospectionException {
		// ClassAttribute classattribute = new ClassAttribute(sortingClass,
		// field);
		// classattribute.setColumnAlias("wtsort" + String.valueOf(0));
		// int[] fieldNoArr = { fieldNo };
		// spec.appendSelect(classattribute, fieldNoArr, false);
		// OrderBy orderby = new OrderBy(classattribute, sortingFlag, null);
		// spec.appendOrderBy(orderby, fieldNoArr);
		spec.appendOrderBy(new OrderBy(new ClassAttribute(sortingClass, field), sortingFlag), new int[] { tableNo });
	}

	public static void setOrderBy(QuerySpec spec, Class sortingClass, int tableNo, String field, String sort)
			throws WTPropertyVetoException, QueryException, WTIntrospectionException {
		boolean isDescSort = (sort == null || "desc".equalsIgnoreCase(sort)) ? true : false;
		spec.appendOrderBy(new OrderBy(new ClassAttribute(sortingClass, field), isDescSort), new int[] { tableNo });
	}

	public static void setOrderBy(QuerySpec spec, Class sortingClass, int tableNo, String field, String aliasName,
			boolean sortingFlag) throws WTPropertyVetoException, QueryException, WTIntrospectionException {
		ClassAttribute classattribute = new ClassAttribute(sortingClass, field);
		classattribute.setColumnAlias(aliasName + String.valueOf(0));
		int[] fieldNoArr = { tableNo };
		spec.appendSelect(classattribute, fieldNoArr, false);
		OrderBy orderby = new OrderBy(classattribute, sortingFlag, null);
		spec.appendOrderBy(orderby, tableNo);

		// spec.appendOrderBy(new OrderBy(new ClassAttribute(sortingClass,
		// field), sortingFlag), new int[] { fieldNo });

	}

	public static void searchInFolder(Folder folder, QuerySpec spec, Class targetClass, int targetClassPos,
			boolean isFirst) throws Exception {
		if (!isFirst)
			spec.appendOr();

		long longFolder = CommonUtils.getOIDLongValue(folder);
		spec.appendWhere(
				new SearchCondition(targetClass, "thePersistInfo.theObjectIdentifier.id", " LIKE ", longFolder),
				targetClassPos);
		SortedEnumeration en = FolderTaskLogic.getSubFolders(folder);
		while (en.hasMoreElements()) {
			Folder subFolder = (Folder) en.nextElement();
			searchInFolder(subFolder, spec, targetClass, targetClassPos, false);
		}
	}

	public static void searchInOnlyFolder(Folder folder, QuerySpec spec, Class targetClass, int targetClassPos)
			throws Exception {
		if (spec.getConditionCount() > 0)
			spec.appendAnd();
		long longFolder = CommonUtils.getOIDLongValue(folder);
		spec.appendWhere(
				new SearchCondition(targetClass, "thePersistInfo.theObjectIdentifier.id", " LIKE ", longFolder),
				targetClassPos);
	}

	/**
	 * Outer Join QuerySpec
	 * 
	 * @return : Vector
	 * @author : PTC KOREA Yang Kyu
	 * @since : 2004.04
	 */
	public static QuerySpec makeOuterJoinQuerySpec(Class targetClass, Class linkClass, boolean isRole_A)
			throws Exception {
		QuerySpec mainQuery = new QuerySpec();

		int classIndex = mainQuery.appendClassList(targetClass, true);

		QuerySpec subQuery = new QuerySpec();
		subQuery.getFromClause().setAliasPrefix("B");
		int linkIndex = subQuery.appendClassList(linkClass, false);
		subQuery.appendSelectAttribute(WTAttributeNameIfc.ID_NAME, linkIndex, true);

		TableExpression[] tables = new TableExpression[2];
		String[] aliases = new String[2];
		tables[0] = mainQuery.getFromClause().getTableExpressionAt(classIndex);
		aliases[0] = mainQuery.getFromClause().getAliasAt(classIndex);
		tables[1] = subQuery.getFromClause().getTableExpressionAt(linkIndex);
		aliases[1] = subQuery.getFromClause().getAliasAt(linkIndex);

		SearchCondition correlatedJoin = null;

		if (isRole_A) {
			correlatedJoin = new SearchCondition(targetClass, WTAttributeNameIfc.ID_NAME, linkClass,
					WTAttributeNameIfc.ROLEA_OBJECT_ID);
		} else {
			correlatedJoin = new SearchCondition(targetClass, WTAttributeNameIfc.ID_NAME, linkClass,
					WTAttributeNameIfc.ROLEB_OBJECT_ID);
		}
		subQuery.appendWhere(correlatedJoin, tables, aliases);
		mainQuery.appendWhere(new NegatedExpression(new ExistsExpression(subQuery)));

		// outer join query
		if (validatedIteration(targetClass)) {
			mainQuery.appendAnd();
			mainQuery.appendWhere(new SearchCondition(targetClass, "iterationInfo.latest", "TRUE"), classIndex);
		}

		return mainQuery;
	}

	private static boolean validatedIteration(Class targetClass) throws Exception {
		try {
//            Iterated iterated = (Iterated) targetClass.newInstance();
			return true;
		} catch (ClassCastException e) {
			return false;
		}
	}

	public static void appendLIKE(QuerySpec querySpec, Class fromClass, String fieldName, String fieldValue, int idx)
			throws QueryException {
		if (CommonUtils.checkString(fieldValue)) {
			if (querySpec.getConditionCount() > 0)
				querySpec.appendAnd();
			querySpec.appendWhere(new SearchCondition(fromClass, fieldName, " LIKE ", "%" + fieldValue + "%"),
					new int[] { idx });
		}
	}

	public static void appendEQUAL(QuerySpec querySpec, Class fromClass, String fieldName, String fieldValue, int idx)
			throws QueryException {
		if (CommonUtils.checkString(fieldValue)) {
			if (querySpec.getConditionCount() > 0)
				querySpec.appendAnd();
			querySpec.appendWhere(new SearchCondition(fromClass, fieldName, "=", fieldValue), new int[] { idx });
		}
	}

	public static void appendEQUAL(QuerySpec querySpec, Class fromClass, String fieldName, long fieldValue, int idx)
			throws QueryException {
		if (querySpec.getConditionCount() > 0)
			querySpec.appendAnd();
		querySpec.appendWhere(new SearchCondition(fromClass, fieldName, "=", fieldValue), new int[] { idx });
	}

	public static void appendEQUAL(QuerySpec querySpec, Class fromClass, String fieldName, long fieldValue, int idx,
			boolean or) throws QueryException {
		querySpec.appendWhere(new SearchCondition(fromClass, fieldName, "=", fieldValue), new int[] { idx });
	}

	public static SearchCondition getSCSQLFunction(Class targetClass, String key, String keyValue, String strCase,
			String condition) {
		SearchCondition sc = null;
		try {
			ClassAttribute attribute = new ClassAttribute(targetClass, key);
			SQLFunction function = SQLFunction.newSQLFunction(strCase);
			function.setArgumentAt((ColumnExpression) attribute, 0);
			ConstantExpression expression = new ConstantExpression(keyValue);
			sc = new SearchCondition(function, condition, expression);
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sc;
	}

	public static void addLastVersionCondition(QuerySpec _query, Class _target, int _idx) throws IBADefinitionException,
			NotAuthorizedException, RemoteException, WTException, QueryException, WTPropertyVetoException {
		AttributeDefDefaultView aview = IBADefinitionHelper.service
				.getAttributeDefDefaultViewByPath("LatestVersionFlag");
		if (aview != null) {
			if (_query.getConditionCount() > 0)
				_query.appendAnd();

			int idx = _query.appendClassList(StringValue.class, false);
			SearchCondition sc = new SearchCondition(
					new ClassAttribute(StringValue.class, "theIBAHolderReference.key.id"), "=",
					new ClassAttribute(_target, "thePersistInfo.theObjectIdentifier.id"));
			sc.setFromIndicies(new int[] { idx, _idx }, 0);
			sc.setOuterJoin(0);
			_query.appendWhere(sc, new int[] { idx, _idx });
			_query.appendAnd();
			sc = new SearchCondition(StringValue.class, "definitionReference.key.id", "=", aview.getObjectID().getId());
			_query.appendWhere(sc, new int[] { idx });
			_query.appendAnd();
			sc = new SearchCondition(StringValue.class, "value", "=", "TRUE");
			_query.appendWhere(sc, new int[] { idx });
		}
	}

	/**
	 * ControlBranch �쓽 紐⑥옄愿�怨꾨�� �궗�슜�븯�뿬 LatestRevision �쓣 寃��깋�븳�떎
	 * 
	 * @param qs
	 * @param targetClass
	 * @param idx
	 * @throws WTException
	 */
	public static void addLastVersionCondition2(QuerySpec qs, Class targetClass, int idx) throws WTException {
		try {
			int branchIdx = qs.appendClassList(ControlBranch.class, false);
			int childBranchIdx = qs.appendClassList(ControlBranch.class, false);

			// #. 媛앹껜 - Parent ControlBranch 媛� Join
			if (qs.getConditionCount() > 0)
				qs.appendAnd();
			qs.appendWhere(new SearchCondition(targetClass, RevisionControlled.BRANCH_IDENTIFIER, ControlBranch.class,
					WTAttributeNameIfc.ID_NAME), new int[] { idx, branchIdx });

			// #. ControlBranch �쓽 遺�紐� - �옄�떇 outer join
			if (qs.getConditionCount() > 0)
				qs.appendAnd();
			SearchCondition outerJoinSc = new SearchCondition(ControlBranch.class, WTAttributeNameIfc.ID_NAME,
					ControlBranch.class, "predecessorReference.key.id");
			outerJoinSc.setOuterJoin(SearchCondition.RIGHT_OUTER_JOIN);
			qs.appendWhere(outerJoinSc, new int[] { branchIdx, childBranchIdx });

			// #. �옄�떇 ControllBranch 媛� null �씠硫� 理쒖떊 Revision
			ClassAttribute childBranchIdNameCa = new ClassAttribute(ControlBranch.class, WTAttributeNameIfc.ID_NAME);
			qs.appendSelect(childBranchIdNameCa, new int[] { childBranchIdx }, false);

			if (qs.getConditionCount() > 0)
				qs.appendAnd();
			qs.appendWhere(new SearchCondition(childBranchIdNameCa, SearchCondition.IS_NULL),
					new int[] { childBranchIdx });
		} catch (WTPropertyVetoException e) {
			throw new WTException(e);
		}
	}

	public static void addIBAConditionLike(QuerySpec _query, Class _target, int _idx, String ibaName, String ibaValue)
			throws IBADefinitionException, NotAuthorizedException, RemoteException, WTException, QueryException,
			WTPropertyVetoException {
		AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(ibaName);
		if (aview != null) {
			if (_query.getConditionCount() > 0)
				_query.appendAnd();

			int idx = _query.appendClassList(StringValue.class, false);
			SearchCondition sc = new SearchCondition(
					new ClassAttribute(StringValue.class, "theIBAHolderReference.key.id"), "=",
					new ClassAttribute(_target, "thePersistInfo.theObjectIdentifier.id"));
			sc.setFromIndicies(new int[] { idx, _idx }, 0);
			sc.setOuterJoin(0);
			_query.appendWhere(sc, new int[] { idx, _idx });
			_query.appendAnd();
			sc = new SearchCondition(StringValue.class, "definitionReference.key.id", "=", aview.getObjectID().getId());
			_query.appendWhere(sc, new int[] { idx });
			_query.appendAnd();
			sc = new SearchCondition(StringValue.class, "value", SearchCondition.LIKE, ibaValue.toUpperCase());
			_query.appendWhere(sc, new int[] { idx });
		}
	}

	public static void addIBAConditionString(QuerySpec _query, Class _target, int _idx, String ibaName, String ibaValue)
			throws WTException, RemoteException, WTPropertyVetoException {

		AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(ibaName);
		if (aview != null) {
			if (_query.getConditionCount() > 0)
				_query.appendAnd();

			int idx = _query.appendClassList(StringValue.class, false);
			SearchCondition sc = new SearchCondition(
					new ClassAttribute(StringValue.class, "theIBAHolderReference.key.id"), "=",
					new ClassAttribute(_target, "thePersistInfo.theObjectIdentifier.id"));
			sc.setFromIndicies(new int[] { idx, _idx }, 0);
			sc.setOuterJoin(0);
			_query.appendWhere(sc, new int[] { idx, _idx });
			_query.appendAnd();
			sc = new SearchCondition(StringValue.class, "definitionReference.key.id", "=", aview.getObjectID().getId());
			_query.appendWhere(sc, new int[] { idx });
			_query.appendAnd();

			StringSearch stringSearch = new StringSearch("value");
			stringSearch.setValue(ibaValue);
			_query.appendWhere(stringSearch.getSearchCondition(StringValue.class), new int[] { idx });
		}
	}

	public static void materialsQueryCondition(QuerySpec _query, Class partClass, int _idx) throws QueryException {

		SearchCondition sc = null;
		ClassAttribute ca = null;
		SQLFunction upper = null;
		ColumnExpression ce = null;

		if (_query.getConditionCount() > 0) {
			_query.appendAnd();
		}

		ca = new ClassAttribute(partClass, "master>number");
		upper = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
		ce = ConstantExpression.newExpression("5%");
		sc = new SearchCondition(upper, SearchCondition.LIKE, ce);
		_query.appendWhere(sc, new int[] { _idx });

	}

	private static String[][] wildcardInfos = new String[][] { { "*", "\\*", "%" }, { "?", "\\?", "_" } };

	public static void appendWildcardWhere(QuerySpec qs, Class targetClass, String attribute, String value, int index)
			throws WTException {

		// #. �쟾泥� wildcard 蹂��솚
		String searchText = value;
		boolean isLike = false;
		for (String[] wildcardInfo : wildcardInfos) {
			String wildcard = wildcardInfo[0];
			String wildcardRegExp = wildcardInfo[1];
			String sqlWildcard = wildcardInfo[2];

			if (searchText.indexOf(wildcard) >= 0) {
				searchText = searchText.replaceAll(wildcardRegExp, sqlWildcard);
				if (!isLike) {
					isLike = true;
				}
			}
		}

		// #. 議곌굔 異붽�
		String operator = (isLike) ? SearchCondition.LIKE : SearchCondition.EQUAL;
		if (qs.getConditionCount() > 0)
			qs.appendAnd();
		qs.appendWhere(new SearchCondition(targetClass, attribute, operator, searchText), new int[] { index });
	}

	public static QuerySpec getKeywordQuery(Class targetClass, QuerySpec mainQuery, String searchValue,
			String[] ibaNames, String alias) throws WTPropertyVetoException, WTException, RemoteException {

		QuerySpec qs = new QuerySpec();
		qs.getFromClause().setAliasPrefix(alias);
		int idx = qs.addClassList(targetClass, false);
		int _idx = qs.appendClassList(StringValue.class, false);

		qs.appendSelect(new ClassAttribute(targetClass, WTAttributeNameIfc.ID_NAME), new int[] { idx }, true);

		qs.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", targetClass,
				"thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
		qs.appendAnd();

		qs.appendOpenParen();

		StringSearch stringSearch = new StringSearch("number");
		stringSearch.setValue(searchValue);
		qs.appendWhere(stringSearch.getSearchCondition(targetClass), new int[] { idx });

		qs.appendOr();

		stringSearch = new StringSearch("name");
		stringSearch.setValue(searchValue);
		qs.appendWhere(stringSearch.getSearchCondition(targetClass), new int[] { idx });

		for (String defName : ibaNames) {
			AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(defName);
			if (aview != null) {
				qs.appendOr();
				addIBACondition(qs, idx, _idx, aview, searchValue);
			}
		}
		qs.appendCloseParen();

		TableExpression[] tables = new TableExpression[2];
		String[] aliases = new String[2];
		tables[0] = mainQuery.getFromClause().getTableExpressionAt(0);
		aliases[0] = mainQuery.getFromClause().getAliasAt(0);
		tables[1] = qs.getFromClause().getTableExpressionAt(idx);
		aliases[1] = qs.getFromClause().getAliasAt(idx);
		SearchCondition correlatedJoin = new SearchCondition(targetClass, WTAttributeNameIfc.ID_NAME, targetClass,
				WTAttributeNameIfc.ID_NAME);
		qs.appendAnd();
		qs.appendWhere(correlatedJoin, tables, aliases);
		mainQuery.setAdvancedQueryEnabled(true);

		return qs;
	}

	private static void addIBACondition(QuerySpec query, int idx, int _idx, AttributeDefDefaultView aview, String value)
			throws WTException, RemoteException, WTPropertyVetoException {

		if (aview != null) {
			query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.key.id", "=",
					aview.getObjectID().getId()), new int[] { _idx });
			query.appendAnd();
			StringSearch stringSearch = new StringSearch("value");
			stringSearch.setValue(value.trim());
			query.appendWhere(stringSearch.getSearchCondition(StringValue.class), new int[] { _idx });
		}
	}

	public static void addIBACondition(QuerySpec qs, Class targetClass, int idx, String viewName, String value)
			throws WTException, RemoteException, WTPropertyVetoException {

		AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(viewName);

		if (aview != null) {

			int _idx = qs.appendClassList(StringValue.class, false);
			qs.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", targetClass,
					"thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(StringValue.class, "definitionReference.key.id", "=",
					aview.getObjectID().getId()), new int[] { _idx });
			qs.appendAnd();
			StringSearch stringSearch = new StringSearch("value");
			stringSearch.setValue(value.trim());
			qs.appendWhere(stringSearch.getSearchCondition(StringValue.class), new int[] { _idx });
		}
	}

	public static void addIBADotCondition(QuerySpec query, Class targetClass, int idx, String viewName, String value)
			throws WTException, RemoteException, WTPropertyVetoException {

		AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(viewName);

		if (aview != null) {

			int _idx = query.appendClassList(StringValue.class, false);
			query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", targetClass,
					"thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
			query.appendAnd();
			query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.key.id",
					SearchCondition.EQUAL, aview.getObjectID().getId()), new int[] { _idx });
			query.appendAnd();

			query.appendOpenParen();

			StringSearch stringSearch = new StringSearch("value");
			stringSearch.setValue(value.toUpperCase().trim());
			query.appendWhere(stringSearch.getSearchCondition(StringValue.class), new int[] { _idx });

			if (!value.toUpperCase().trim().endsWith("*")) {
				query.appendOr();
				stringSearch = new StringSearch("value");
				stringSearch.setValue(value.toUpperCase().trim() + ",%");
				query.appendWhere(stringSearch.getSearchCondition(StringValue.class), new int[] { _idx });
			}
			if (!value.toUpperCase().trim().startsWith("*")) {
				query.appendOr();
				stringSearch = new StringSearch("value");
				stringSearch.setValue("%," + value.toUpperCase().trim());
				query.appendWhere(stringSearch.getSearchCondition(StringValue.class), new int[] { _idx });
			}
			if (!value.toUpperCase().trim().startsWith("*") && !value.toUpperCase().trim().startsWith("*")) {
				query.appendOr();
				stringSearch = new StringSearch("value");
				stringSearch.setValue("%," + value.toUpperCase().trim() + ",%");
				query.appendWhere(stringSearch.getSearchCondition(StringValue.class), new int[] { _idx });
			}
			query.appendCloseParen();
		}
	}

	public static void addUserFullNameCondition(QuerySpec query, Class targetClass, int idx, String queryKey,
			String value) throws WTException, RemoteException, WTPropertyVetoException {

		int userIdx = query.appendClassList(WTUser.class, false);

		query.appendWhere(
				new SearchCondition(targetClass, queryKey, WTUser.class, "thePersistInfo.theObjectIdentifier.id"),
				new int[] { idx, userIdx });
		query.appendAnd();

		StringSearch stringSearch = new StringSearch(WTUser.FULL_NAME);
		stringSearch.setValue(value.trim());
		query.appendWhere(stringSearch.getSearchCondition(WTUser.class), new int[] { userIdx });
	}

	public static void appendOrderBy(QuerySpec query, Class targetClass, String sortColumn, int idx, boolean desc)
			throws WTException, RemoteException, WTPropertyVetoException {

		if (sortColumn == null || sortColumn.length() == 0) {
			sortColumn = WTObject.MODIFY_TIMESTAMP;
		}

		if (sortColumn.startsWith(IBA_PREFIX)) {

			sortColumn = sortColumn.substring(IBA_PREFIX.length());

			AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(sortColumn);
			if (aview != null) {

				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				int _idx = query.appendClassList(StringValue.class, false);
				int[] sortIndex = { _idx };

				SearchCondition sc = new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", targetClass,
						"thePersistInfo.theObjectIdentifier.id");
				sc.setOuterJoin(SearchCondition.LEFT_OUTER_JOIN);
				query.appendWhere(sc, new int[] { _idx, idx });
				query.appendAnd();

				sc = new SearchCondition(StringValue.class, "definitionReference.key.id", SearchCondition.EQUAL,
						aview.getObjectID().getId());
				sc.setOuterJoin(SearchCondition.LEFT_OUTER_JOIN);
				query.appendWhere(sc, sortIndex);

				// nvl(A4.value,' ')
				ClassAttribute attribute = new ClassAttribute(StringValue.class, StringValue.VALUE);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.NULL_VALUE);
				function.setArgumentAt((ColumnExpression) attribute, 0);
				function.setArgumentAt(new ConstantExpression(" "), 1);
				function.setColumnAlias("sort0");
				query.appendSelect(function, sortIndex, false);
				query.appendOrderBy(new OrderBy(function, desc), sortIndex);

			}

		} else {

//				String codeType = null;
			int separator = sortColumn.indexOf(CODE_SEPARATOR);
			if (separator > 0) {
//			   		codeType = sortColumn.substring(0, separator);
				sortColumn = sortColumn.substring(separator + CODE_SEPARATOR.length());
			}

			ClassAttribute attribute = new ClassAttribute(targetClass, sortColumn);
			SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER);
			function.setArgumentAt((ColumnExpression) attribute, 0);
			function.setColumnAlias("sort0");
			int[] sortIndex = { idx };
			query.appendSelect(function, sortIndex, false);
			query.appendOrderBy(new OrderBy(function, desc), sortIndex);
		}
	}

	public static void appendSubQuery(QuerySpec mainQuery, QuerySpec subQuery, int mainIdx, int subIdx,
			Class targetClass) throws WTPropertyVetoException, WTException, RemoteException {

		TableExpression[] tables = new TableExpression[2];
		String[] aliases = new String[2];
		tables[0] = mainQuery.getFromClause().getTableExpressionAt(mainIdx);
		aliases[0] = mainQuery.getFromClause().getAliasAt(mainIdx);
		tables[1] = subQuery.getFromClause().getTableExpressionAt(subIdx);
		aliases[1] = subQuery.getFromClause().getAliasAt(subIdx);

		SearchCondition correlatedJoin = new SearchCondition(targetClass, WTAttributeNameIfc.ID_NAME, targetClass,
				WTAttributeNameIfc.ID_NAME);
		subQuery.appendAnd();
		subQuery.appendWhere(correlatedJoin, tables, aliases);
		mainQuery.setAdvancedQueryEnabled(true);

		mainQuery.appendWhere(new ExistsExpression(subQuery), new int[] { mainIdx });
	}
}
