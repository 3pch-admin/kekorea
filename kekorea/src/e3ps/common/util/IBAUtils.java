package e3ps.common.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.iba.definition.BooleanDefinition;
import wt.iba.definition.BooleanDefinitionReference;
import wt.iba.definition.FloatDefinition;
import wt.iba.definition.IntegerDefinition;
import wt.iba.definition.IntegerDefinitionReference;
import wt.iba.definition.StringDefinition;
import wt.iba.definition.StringDefinitionReference;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.litedefinition.BooleanDefView;
import wt.iba.definition.litedefinition.FloatDefView;
import wt.iba.definition.litedefinition.IntegerDefView;
import wt.iba.definition.litedefinition.StringDefView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.BooleanValue;
import wt.iba.value.DefaultAttributeContainer;
import wt.iba.value.FloatValue;
import wt.iba.value.IBAHolder;
import wt.iba.value.IBAHolderReference;
import wt.iba.value.IntegerValue;
import wt.iba.value.StringValue;
import wt.iba.value.litevalue.AbstractValueView;
import wt.iba.value.litevalue.BooleanValueDefaultView;
import wt.iba.value.litevalue.FloatValueDefaultView;
import wt.iba.value.litevalue.IntegerValueDefaultView;
import wt.iba.value.litevalue.StringValueDefaultView;
import wt.iba.value.service.IBAValueHelper;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.query.ClassAttribute;
import wt.query.ColumnExpression;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SQLFunction;
import wt.query.SearchCondition;
import wt.util.WTAttributeNameIfc;

public class IBAUtils {

	private IBAUtils() {

	}

	public static String getAttrValues(IBAHolder holder, String type, String attrName) throws Exception {
		String value = "";
		if ("s".equalsIgnoreCase(type) || "string".equalsIgnoreCase(type)) {
			value = getStringValue(holder, attrName);
		} else if ("b".equalsIgnoreCase(type) || "boolean".equalsIgnoreCase(type)) {
			value = String.valueOf(getBooleanValue(holder, attrName));
		} else if ("f".equalsIgnoreCase(type) || "float".equalsIgnoreCase(type)) {
			// value = String.valueOf(getflo)
		}
		return value;
	}

	public static String getStringValue(IBAHolder holder, String attrName) throws Exception {
		if (holder == null) {
			return "";
		}

		QuerySpec query = new QuerySpec();

		int ii = query.appendClassList(holder.getClass(), false);
		int jj = query.appendClassList(StringValue.class, false);
		int kk = query.appendClassList(StringDefinition.class, false);

		query.setAdvancedQueryEnabled(true);
		query.setDescendantQuery(false);

		long key = ((Persistable) holder).getPersistInfo().getObjectIdentifier().getId();
		SearchCondition sc = new SearchCondition(holder.getClass(), "thePersistInfo.theObjectIdentifier.id", "=", key);
		query.appendWhere(sc, new int[] { ii });
		query.appendAnd();

		ClassAttribute ca = new ClassAttribute(StringValue.class, "value");
		query.appendSelect(ca, new int[] { jj }, false);

		ca = new ClassAttribute(StringValue.class, "theIBAHolderReference.key.id");
		sc = new SearchCondition(ca, "=",
				new ClassAttribute(holder.getClass(), "thePersistInfo.theObjectIdentifier.id"));
		query.appendWhere(sc, new int[] { jj, ii });
		query.appendAnd();

		ca = new ClassAttribute(StringValue.class, "definitionReference.hierarchyID");
		sc = new SearchCondition(ca, "=", new ClassAttribute(StringDefinition.class, "hierarchyID"));
		query.appendWhere(sc, new int[] { jj, kk });
		query.appendAnd();

		sc = new SearchCondition(StringDefinition.class, "name", "=", attrName);
		query.appendWhere(sc, new int[] { kk });

		ca = new ClassAttribute(StringDefinition.class, WTAttributeNameIfc.MODIFY_STAMP_NAME);
		OrderBy orderBy = new OrderBy(ca, true);
		query.appendOrderBy(orderBy, new int[] { kk });

		QueryResult result = PersistenceHelper.manager.find((StatementSpec) query);
		String value = null;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			value = (String) obj[0];
		}
		return value != null ? value : "";
	}

	public static int getIntegerValue(IBAHolder holder, String attrName) throws Exception {
		if (holder == null) {
			return 0;
		}

		QuerySpec query = new QuerySpec();

		int ii = query.appendClassList(holder.getClass(), false);
		int jj = query.appendClassList(IntegerValue.class, false);
		int kk = query.appendClassList(IntegerDefinition.class, false);

		long key = ((Persistable) holder).getPersistInfo().getObjectIdentifier().getId();
		SearchCondition sc = new SearchCondition(holder.getClass(), "thePersistInfo.theObjectIdentifier.id", "=", key);
		query.appendWhere(sc, new int[] { ii });
		query.appendAnd();

		ClassAttribute ca = new ClassAttribute(IntegerValue.class, "value");
		query.appendSelect(ca, new int[] { jj }, false);

		ca = new ClassAttribute(IntegerValue.class, "theIBAHolderReference.key.id");
		sc = new SearchCondition(ca, "=",
				new ClassAttribute(holder.getClass(), "thePersistInfo.theObjectIdentifier.id"));
		query.appendWhere(sc, new int[] { jj, ii });
		query.appendAnd();

		ca = new ClassAttribute(IntegerValue.class, "definitionReference.hierarchyID");
		sc = new SearchCondition(ca, "=", new ClassAttribute(IntegerDefinition.class, "hierarchyID"));
		query.appendWhere(sc, new int[] { jj, kk });
		query.appendAnd();

		sc = new SearchCondition(IntegerDefinition.class, "name", "=", attrName);
		query.appendWhere(sc, new int[] { kk });

		ca = new ClassAttribute(IntegerDefinition.class, WTAttributeNameIfc.MODIFY_STAMP_NAME);
		OrderBy orderBy = new OrderBy(ca, true);
		query.appendOrderBy(orderBy, new int[] { kk });

		QueryResult result = PersistenceHelper.manager.find((StatementSpec) query);
		int value = 0;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			value = ((BigDecimal) obj[0]).intValue();
		}
		return value;
	}

	public static boolean getBooleanValue(IBAHolder holder, String attrName) throws Exception {
		if (holder == null) {
			return false;
		}
		QuerySpec query = new QuerySpec();

		int ii = query.appendClassList(holder.getClass(), false);
		int jj = query.appendClassList(BooleanValue.class, false);
		int kk = query.appendClassList(BooleanDefinition.class, false);

		long key = ((Persistable) holder).getPersistInfo().getObjectIdentifier().getId();
		SearchCondition sc = new SearchCondition(holder.getClass(), "thePersistInfo.theObjectIdentifier.id", "=", key);
		query.appendWhere(sc, new int[] { ii });
		query.appendAnd();

		ClassAttribute ca = new ClassAttribute(BooleanValue.class, "value");
		query.appendSelect(ca, new int[] { jj }, false);

		ca = new ClassAttribute(BooleanValue.class, "theIBAHolderReference.key.id");
		sc = new SearchCondition(ca, "=",
				new ClassAttribute(holder.getClass(), "thePersistInfo.theObjectIdentifier.id"));
		query.appendWhere(sc, new int[] { jj, ii });
		query.appendAnd();

		ca = new ClassAttribute(BooleanValue.class, "definitionReference.hierarchyID");
		sc = new SearchCondition(ca, "=", new ClassAttribute(BooleanDefinition.class, "hierarchyID"));
		query.appendWhere(sc, new int[] { jj, kk });
		query.appendAnd();

		sc = new SearchCondition(BooleanDefinition.class, "name", "=", attrName);
		query.appendWhere(sc, new int[] { kk });

		QueryResult result = PersistenceHelper.manager.find((StatementSpec) query);
		boolean value = false;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();

			// value = (Boolean)obj[0];
			BigDecimal bd = (BigDecimal) obj[0];
			value = bd.intValue() == 1 ? true : false;
		}
		return value;
	}

	public static HashMap<String, Object> getAttributes(IBAHolder holder) throws Exception {
		IBAHolder iba = IBAValueHelper.service.refreshAttributeContainer(holder, null, null, null);
		DefaultAttributeContainer container = (DefaultAttributeContainer) iba.getAttributeContainer();
		HashMap<String, Object> map = new HashMap<String, Object>();
		if (container == null) {
			return map;
		}

		AbstractValueView[] avv = container.getAttributeValues();
		for (int i = 0; i < avv.length; i++) {
			if (avv[i] instanceof StringValueDefaultView) {
				StringValueDefaultView sv = (StringValueDefaultView) avv[i];
				map.put(sv.getDefinition().getName(), sv.getValueAsString());
			} else if (avv[i] instanceof FloatValueDefaultView) {

			}
		}
		return map;
	}

	public static void createIBA(IBAHolder holder, String type, String attrName, Object value) throws Exception {
		if (value == null) {
			return;
		}

		if ("s".equalsIgnoreCase(type) || "string".equalsIgnoreCase(type)) {
			StringValue sv = new StringValue();
			StringDefinition sd = getStringDefinition(attrName);
			if (sd == null) {
				return;
			}
			sv.setValue((String) value);
			sv.setDefinitionReference((StringDefinitionReference) sd.getAttributeDefinitionReference());
			sv.setIBAHolderReference(IBAHolderReference.newIBAHolderReference(holder));
			sv = (StringValue) PersistenceHelper.manager.save(sv);
		} else if ("b".equalsIgnoreCase(type) || "boolean".equalsIgnoreCase(type)) {
			BooleanValue bv = new BooleanValue();
			BooleanDefinition bd = getBooleanDefinition(attrName);
			if (bd == null) {
				return;
			}
			bv.setValue((boolean) value);
			bv.setDefinitionReference((BooleanDefinitionReference) bd.getAttributeDefinitionReference());
			bv.setIBAHolderReference(IBAHolderReference.newIBAHolderReference(holder));
			bv = (BooleanValue) PersistenceHelper.manager.save(bv);
		} else if ("i".equalsIgnoreCase(type) || "int".equalsIgnoreCase(type)) {
			IntegerValue iv = new IntegerValue();
			IntegerDefinition id = getIntegerDefinition(attrName);
			if (id == null) {
				return;
			}
			iv.setValue((int) value);
			iv.setDefinitionReference((IntegerDefinitionReference) id.getAttributeDefinitionReference());
			iv.setIBAHolderReference(IBAHolderReference.newIBAHolderReference(holder));
			iv = (IntegerValue) PersistenceHelper.manager.save(iv);
		}
	}

	private static ArrayList<StringDefinition> getStringDefinitions(String attrName) throws Exception {
		ArrayList<StringDefinition> list = new ArrayList<StringDefinition>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(StringDefinition.class, true);
		SearchCondition sc = new SearchCondition(StringDefinition.class, StringDefinition.NAME, "=", attrName);
		query.appendWhere(sc, new int[] { idx });
		QueryResult result = PersistenceHelper.manager.find((StatementSpec) query);
		StringDefinition sd = null;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			sd = (StringDefinition) obj[0];
			list.add(sd);
		}
		return list;
	}

	private static ArrayList<FloatDefinition> getFloatDefinitions(String attrName) throws Exception {
		ArrayList<FloatDefinition> list = new ArrayList<FloatDefinition>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(FloatDefinition.class, true);
		SearchCondition sc = new SearchCondition(FloatDefinition.class, FloatDefinition.NAME, "=", attrName);
		query.appendWhere(sc, new int[] { idx });
		QueryResult result = PersistenceHelper.manager.find((StatementSpec) query);
		FloatDefinition fd = null;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			fd = (FloatDefinition) obj[0];
			list.add(fd);
		}
		return list;
	}

	private static ArrayList<IntegerDefinition> getIntegerDefinitions(String attrName) throws Exception {
		ArrayList<IntegerDefinition> list = new ArrayList<IntegerDefinition>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(IntegerDefinition.class, true);
		SearchCondition sc = new SearchCondition(IntegerDefinition.class, IntegerDefinition.NAME, "=", attrName);
		query.appendWhere(sc, new int[] { idx });
		QueryResult result = PersistenceHelper.manager.find((StatementSpec) query);
		IntegerDefinition id = null;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			id = (IntegerDefinition) obj[0];
			list.add(id);
		}
		return list;
	}

	private static ArrayList<BooleanDefinition> getBooleanDefinitions(String attrName) throws Exception {
		ArrayList<BooleanDefinition> list = new ArrayList<BooleanDefinition>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(BooleanDefinition.class, true);
		SearchCondition sc = new SearchCondition(BooleanDefinition.class, BooleanDefinition.NAME, "=", attrName);
		query.appendWhere(sc, new int[] { idx });
		QueryResult result = PersistenceHelper.manager.find((StatementSpec) query);
		BooleanDefinition bd = null;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			bd = (BooleanDefinition) obj[0];
			list.add(bd);
		}
		return list;
	}

	public static void deletesIBA(IBAHolder holder, String varName, String type) throws Exception {
		if (holder == null) {
			return;
		}

		ArrayList list = new ArrayList();

		HashMap<String, Object> my = getAttributesHidNViewObject(holder);

		if (type.equals("s") || type.equals("string")) {
			list = getStringDefinitions(varName);
		} else if (type.equals("f") || type.equals("float")) {
			list = getFloatDefinitions(varName);
		} else if (type.equals("b") || type.equals("boolean")) {
			list = getBooleanDefinitions(varName);
		} else if (type.equals("i") || type.equals("integer")) {
			list = getIntegerDefinitions(varName);
		}

		for (Object oo : list) {
			String hid = null;
			if (oo instanceof StringDefinition) {
				StringDefinition sd = (StringDefinition) oo;
				hid = sd.getHierarchyID();
			} else if (oo instanceof BooleanDefinition) {
				BooleanDefinition sd = (BooleanDefinition) oo;
				hid = sd.getHierarchyID();
			} else if (oo instanceof FloatDefinition) {
				FloatDefinition sd = (FloatDefinition) oo;
				hid = sd.getHierarchyID();
			} else if (oo instanceof IntegerDefinition) {
				IntegerDefinition sd = (IntegerDefinition) oo;
				hid = sd.getHierarchyID();
			}

			System.out.println("hid=" + hid);
			System.out.println("my=" + my);
			if (my.containsKey(hid)) {
				Object obj = my.get(hid);
				ReferenceFactory rf = new ReferenceFactory();
				if (obj instanceof StringValueDefaultView) {
					StringValueDefaultView sv = (StringValueDefaultView) obj;
					StringValue s = (StringValue) rf.getReference(sv.getObjectID().toString()).getObject();
					PersistenceHelper.manager.delete(s);
				} else if (obj instanceof BooleanValueDefaultView) {
					BooleanValueDefaultView bv = (BooleanValueDefaultView) obj;
					BooleanValue b = (BooleanValue) rf.getReference(bv.getObjectID().toString()).getObject();
					PersistenceHelper.manager.delete(b);
				} else if (obj instanceof IntegerValueDefaultView) {
					IntegerValueDefaultView iv = (IntegerValueDefaultView) obj;
					IntegerValue i = (IntegerValue) rf.getReference(iv.getObjectID().toString()).getObject();
					PersistenceHelper.manager.delete(i);
				}
			}
		}
	}

	public static void deleteIBA(IBAHolder holder, String varName, String type) throws Exception {
		if (holder == null) {
			return;
		}

		HashMap<String, Object> my = getAttributesHidNViewObject(holder);
		String hid = null;
		if (type.equals("s") || type.equals("string")) {
			StringDefinition sd = getStringDefinition(varName);
			hid = sd.getHierarchyID();
		} else if (type.equals("f") || type.equals("float")) {
			FloatDefinition fd = getFloatDefinition(varName);
			hid = fd.getHierarchyID();
		} else if (type.equals("b") || type.equals("boolean")) {
			BooleanDefinition bd = getBooleanDefinition(varName);
			hid = bd.getHierarchyID();
		} else if (type.equals("i") || type.equals("integer")) {
			IntegerDefinition id = getIntegerDefinition(varName);
			hid = id.getHierarchyID();
		}

		if (my.containsKey(hid)) {
			Object obj = my.get(hid);
			ReferenceFactory rf = new ReferenceFactory();
			if (obj instanceof StringValueDefaultView) {
				StringValueDefaultView sv = (StringValueDefaultView) obj;
				StringValue s = (StringValue) rf.getReference(sv.getObjectID().toString()).getObject();
				PersistenceHelper.manager.delete(s);
			} else if (obj instanceof BooleanValueDefaultView) {
				BooleanValueDefaultView bv = (BooleanValueDefaultView) obj;
				BooleanValue b = (BooleanValue) rf.getReference(bv.getObjectID().toString()).getObject();
				PersistenceHelper.manager.delete(b);
			} else if (obj instanceof IntegerValueDefaultView) {
				IntegerValueDefaultView iv = (IntegerValueDefaultView) obj;
				IntegerValue i = (IntegerValue) rf.getReference(iv.getObjectID().toString()).getObject();
				PersistenceHelper.manager.delete(i);
			}
		}
	}

	public static StringValue getStringValue(IBAHolder holder, String varName, String type) throws Exception {
		QuerySpec query = new QuerySpec();
		int ii = query.appendClassList(StringValue.class, true);
		int jj = query.appendClassList(StringDefinition.class, false);

		ClassAttribute ca = new ClassAttribute(StringValue.class, "definitionReference.key.id");
		SearchCondition sc = new SearchCondition(ca, "=",
				new ClassAttribute(StringDefinition.class, "thePersistInfo.theObjectIdentifier.id"));
		query.appendWhere(sc, new int[] { ii, jj });
		query.appendAnd();

		sc = new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", "=",
				((Persistable) holder).getPersistInfo().getObjectIdentifier().getId());
		query.appendWhere(sc, new int[] { ii });
		query.appendAnd();
		sc = new SearchCondition(StringDefinition.class, "name", "=", varName);
		query.appendWhere(sc, new int[] { jj });

		QueryResult result = PersistenceHelper.manager.find((StatementSpec) query);
		StringValue sv = null;
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			sv = (StringValue) obj[0];
		}
		return sv;
	}

	public static HashMap<String, Object> getAttributesHidNViewObject(IBAHolder holder) throws Exception {
		IBAHolder iba = IBAValueHelper.service.refreshAttributeContainer(holder, null, null, null);
		DefaultAttributeContainer container = (DefaultAttributeContainer) iba.getAttributeContainer();
		HashMap<String, Object> map = new HashMap<String, Object>();
		if (container == null) {
			return map;
		}

		AbstractValueView[] avv = container.getAttributeValues();

		for (int i = 0; i < avv.length; i++) {
			if (avv[i] instanceof StringValueDefaultView) {
				StringValueDefaultView sv = (StringValueDefaultView) avv[i];
				map.put(sv.getDefinition().getHierarchyID(), sv);
			} else if (avv[i] instanceof BooleanValueDefaultView) {
				BooleanValueDefaultView bv = (BooleanValueDefaultView) avv[i];
				map.put(bv.getDefinition().getHierarchyID(), bv);
			} else if (avv[i] instanceof IntegerValueDefaultView) {
				IntegerValueDefaultView iv = (IntegerValueDefaultView) avv[i];
				map.put(iv.getDefinition().getHierarchyID(), iv);
			}
		}
		return map;
	}

	private static StringDefinition getStringDefinition(String attrName) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(StringDefinition.class, true);
		SearchCondition sc = new SearchCondition(StringDefinition.class, StringDefinition.NAME, "=", attrName);
		query.appendWhere(sc, new int[] { idx });
		QueryResult result = PersistenceHelper.manager.find((StatementSpec) query);
		StringDefinition sd = null;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			sd = (StringDefinition) obj[0];
		}
		return sd;
	}

	private static BooleanDefinition getBooleanDefinition(String attrName) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(BooleanDefinition.class, true);
		SearchCondition sc = new SearchCondition(BooleanDefinition.class, BooleanDefinition.NAME, "=", attrName);
		query.appendWhere(sc, new int[] { idx });
		QueryResult result = PersistenceHelper.manager.find((StatementSpec) query);
		BooleanDefinition bd = null;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			bd = (BooleanDefinition) obj[0];
		}
		return bd;
	}

	private static IntegerDefinition getIntegerDefinition(String attrName) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(IntegerDefinition.class, true);
		SearchCondition sc = new SearchCondition(IntegerDefinition.class, IntegerDefinition.NAME, "=", attrName);
		query.appendWhere(sc, new int[] { idx });
		QueryResult result = PersistenceHelper.manager.find((StatementSpec) query);
		IntegerDefinition id = null;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			id = (IntegerDefinition) obj[0];
		}
		return id;
	}

	private static FloatDefinition getFloatDefinition(String attrName) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(FloatDefinition.class, true);
		SearchCondition sc = new SearchCondition(FloatDefinition.class, FloatDefinition.NAME, "=", attrName);
		query.appendWhere(sc, new int[] { idx });
		QueryResult result = PersistenceHelper.manager.find((StatementSpec) query);
		FloatDefinition fd = null;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			fd = (FloatDefinition) obj[0];
		}
		return fd;
	}

	public static void setIBAStringValue(QuerySpec query, String ibaName, Class ibaClass, Class objClass, String values,
			int idx) throws Exception {
		if (!StringUtils.isNull(values)) {
			AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(ibaName);
			if (aview != null) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				int ss = query.appendClassList(ibaClass, false);
				ClassAttribute ca = new ClassAttribute(ibaClass, "theIBAHolderReference.key.id");
				SearchCondition sc = new SearchCondition(ca, "=",
						new ClassAttribute(objClass, "thePersistInfo.theObjectIdentifier.id"));
				sc.setFromIndicies(new int[] { ss, idx }, 0);
				sc.setOuterJoin(0);
				query.appendWhere(sc, new int[] { ss, idx });
				query.appendAnd();
				sc = new SearchCondition(ibaClass, "definitionReference.hierarchyID", "=", aview.getHierarchyID());
				query.appendWhere(sc, new int[] { ss });
				query.appendAnd();
				sc = new SearchCondition(ibaClass, "value", SearchCondition.LIKE,
						"%" + values.toUpperCase().trim() + "%");
				query.appendWhere(sc, new int[] { ss });
			}
		}
	}

	public static void setIBABooleanValue(QuerySpec query, String ibaName, Class ibaClass, Class objClass,
			String values, int idx) throws Exception {
		if (!StringUtils.isNull(values)) {
			AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(ibaName);
			if (aview != null) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}

				int ss = query.appendClassList(ibaClass, false);
				ClassAttribute ca = new ClassAttribute(ibaClass, "theIBAHolderReference.key.id");
				SearchCondition sc = new SearchCondition(ca, "=",
						new ClassAttribute(objClass, "thePersistInfo.theObjectIdentifier.id"));
				sc.setFromIndicies(new int[] { ss, idx }, 0);
				sc.setOuterJoin(0);
				query.appendWhere(sc, new int[] { ss, idx });
				query.appendAnd();
				sc = new SearchCondition(ibaClass, "definitionReference.hierarchyID", "=", aview.getHierarchyID());
				query.appendWhere(sc, new int[] { ss });
				query.appendAnd();
				String condition = SearchCondition.IS_FALSE;
				if (Boolean.parseBoolean(values) == true) {
					condition = SearchCondition.IS_TRUE;
				}
				sc = new SearchCondition(ibaClass, "value", condition);
				query.appendWhere(sc, new int[] { ss });
			}
		}
	}

	public static String getAttrValue(IBAHolder ibaHolder, String attrName) throws Exception {
		String returnStr = "";
		Class target = ibaHolder.getClass();

		QuerySpec select = new QuerySpec();

		int idx = select.addClassList(target, false);
		int idx_StrVal = select.addClassList(StringValue.class, false);
		int idx_StrDef = select.addClassList(StringDefinition.class, false);
		long lon = ((Persistable) ibaHolder).getPersistInfo().getObjectIdentifier().getId();
		select.appendWhere(new SearchCondition(target, "thePersistInfo.theObjectIdentifier.id", "=", lon),
				new int[] { idx });
		select.appendAnd();
		select.appendSelect(new ClassAttribute(StringValue.class, "value"), new int[] { idx_StrVal }, false);

		SearchCondition where = new SearchCondition(
				new ClassAttribute(StringValue.class, "theIBAHolderReference.key.id"), "=",
				new ClassAttribute(target, "thePersistInfo.theObjectIdentifier.id"));
		select.appendWhere(where, new int[] { idx_StrVal, idx });
		select.appendAnd();
		where = new SearchCondition(new ClassAttribute(StringValue.class, "definitionReference.key.id"), "=",
				new ClassAttribute(StringDefinition.class, "thePersistInfo.theObjectIdentifier.id"));
		select.appendWhere(where, new int[] { idx_StrVal, idx_StrDef });
		select.appendAnd();
		select.appendWhere(new SearchCondition(StringDefinition.class, "name", "=", attrName),
				new int[] { idx_StrDef });

		QueryResult re = PersistenceHelper.manager.find(select);
		while (re.hasMoreElements()) {
			Object[] obj = (Object[]) re.nextElement();
			returnStr = (String) obj[0];
		}
		return returnStr;
	}

	public static void changeIBAValue(IBAHolder ibaHolder, String varName, String value) throws Exception {
		changeIBAValue(ibaHolder, varName, value,
				IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(varName));
	}

	public static void changeIBAValue(IBAHolder ibaHolder, String varName, String value, AttributeDefDefaultView aview)
			throws Exception {
		if (value == null) {
			return;
		} else if (value.length() == 0) {
			deleteIBA(ibaHolder, varName, "s");
			return;
		}
		if (aview == null) {
			aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(varName);
		}
		if (aview instanceof StringDefView)
			changeIBAValue(ibaHolder, varName, value, "string");
		else if (aview instanceof FloatDefView)
			changeIBAValue(ibaHolder, varName, value, "float");
		else if (aview instanceof BooleanDefView)
			changeIBAValue(ibaHolder, varName, value, "boolean");
		else if (aview instanceof IntegerDefView)
			changeIBAValue(ibaHolder, varName, value, "integer");
	}

	public static void changeIBAValue(IBAHolder ibaHolder, String varName, String value, String ibaType) {

		try {
			if (value == null) {
				return;
			} else if (value.length() == 0) {
				deleteIBA(ibaHolder, varName, ibaType);
				return;
			}

			Object obj = getIBAObject(ibaHolder, varName, ibaType);
			if (obj == null) {
				if ("string".equals(ibaType)) {
					createIBA(ibaHolder, "string", varName, value);
				} else if ("float".equals(ibaType)) {
					createIBA(ibaHolder, "float", varName, value);
				} else if ("boolean".equals(ibaType)) {
					createIBA(ibaHolder, "boolean", varName, value);
				} else if ("integer".equals(ibaType)) {
					createIBA(ibaHolder, "integer", varName, value);
				}
			} else {
				if (obj instanceof StringValue) {
					StringValue sv = (StringValue) obj;
					sv.setValue(value);
					PersistenceHelper.manager.modify(sv);
				} else if (obj instanceof FloatValue) {
					FloatValue fv = (FloatValue) obj;
					fv.setValue(Double.parseDouble(value));
					PersistenceHelper.manager.modify(fv);
				} else if (obj instanceof BooleanValue) {
					boolean boolval = value.equals("Y") ? true : false;
					BooleanValue bv = (BooleanValue) obj;
					bv.setValue(boolval);
					PersistenceHelper.manager.modify(bv);
				} else if (obj instanceof IntegerValue) {
					IntegerValue iv = (IntegerValue) obj;
					iv.setValue(Integer.parseInt(value));
					PersistenceHelper.manager.modify(iv);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Object getIBAObject(IBAHolder ibaHolder, String varName, String ibaType) {

		Object obj = null;
		try {
			Class cls = null;
			Class cls2 = null;
			ClassAttribute ca = null;
			ClassAttribute ca2 = null;
			QuerySpec qs = new QuerySpec();

			if ("string".equals(ibaType)) {
				cls = StringValue.class;
				cls2 = StringDefinition.class;
			} else if ("float".equals(ibaType)) {
				cls = FloatValue.class;
				cls2 = FloatDefinition.class;
			} else if ("boolean".equals(ibaType)) {
				cls = BooleanValue.class;
				cls2 = BooleanDefinition.class;
			} else {
				return obj;
			}

			long longOid = 0;
			if (ibaHolder instanceof WTPart) {
				WTPart part = (WTPart) ibaHolder;
				longOid = part.getPersistInfo().getObjectIdentifier().getId();
			} else if (ibaHolder instanceof EPMDocument) {
				EPMDocument epm = (EPMDocument) ibaHolder;
				longOid = epm.getPersistInfo().getObjectIdentifier().getId();
			} else if (ibaHolder instanceof WTDocument) {
				WTDocument doc = (WTDocument) ibaHolder;
				longOid = doc.getPersistInfo().getObjectIdentifier().getId();

			} else {
				return obj;
			}

			int idx = qs.appendClassList(cls, true);
			int idx2 = qs.appendClassList(cls2, false);

			ca = new ClassAttribute(cls, "definitionReference.key.id");
			ca2 = new ClassAttribute(cls2, "thePersistInfo.theObjectIdentifier.id");
			SearchCondition sc2 = new SearchCondition(ca, "=", ca2);
			qs.appendWhere(sc2, new int[] { idx, idx2 });

			qs.appendAnd();
			qs.appendWhere(new SearchCondition(cls, "theIBAHolderReference.key.id", SearchCondition.EQUAL, longOid),
					new int[] { idx });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(cls2, "name", SearchCondition.EQUAL, varName), new int[] { idx2 });
			QueryResult rt = PersistenceHelper.manager.find(qs);

			while (rt.hasMoreElements()) {
				Object[] objs = (Object[]) rt.nextElement();
				obj = objs[0];
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

	public static void queryNumber(QuerySpec _query, Class _target, int _idx, String number) throws Exception {
		AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath("DWG_NO");
		AttributeDefDefaultView aview1 = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath("DWG_No");

		if ((aview != null) || (aview1 != null)) {
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
			_query.appendOpenParen();
			sc = new SearchCondition(StringValue.class, "definitionReference.hierarchyID", "=",
					aview1.getHierarchyID());

			_query.appendWhere(sc, new int[] { idx, _idx });
			_query.appendOr();
			sc = new SearchCondition(StringValue.class, "definitionReference.hierarchyID", "=", aview.getHierarchyID());

			_query.appendWhere(sc, new int[] { idx });
			_query.appendCloseParen();

			_query.appendAnd();

			String[] str = number.split(";");
			// System.out.println("str.length = " + str.length);
			if (str.length == 1) {
				sc = new SearchCondition(StringValue.class, "value2", SearchCondition.LIKE,
						"%" + str[0].toUpperCase() + "%");
				_query.appendWhere(sc, new int[] { idx });
			} else if (str.length >= 2) {
				_query.appendOpenParen();
				sc = new SearchCondition(StringValue.class, "value2", SearchCondition.LIKE,
						"%" + str[0].toUpperCase() + "%");
				_query.appendWhere(sc, new int[] { idx });
				for (int i = 1; i < str.length; i++) {
					_query.appendOr();
					sc = new SearchCondition(StringValue.class, "value2", SearchCondition.LIKE,
							"%" + str[i].toUpperCase() + "%");
					_query.appendWhere(sc, new int[] { idx });
				}
				_query.appendCloseParen();
			}

		}
	}

	public static void queryName(QuerySpec _query, Class _target, int _idx, String name) throws Exception {
		AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath("TITLE1");
		AttributeDefDefaultView aview1 = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath("TITLE2");
		AttributeDefDefaultView aview2 = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath("NAME_OF_PARTS");

		if ((aview != null) || (aview1 != null) || (aview2 != null)) {
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
			_query.appendOpenParen();
			sc = new SearchCondition(StringValue.class, "definitionReference.hierarchyID", "=",
					aview2.getHierarchyID());

			_query.appendWhere(sc, new int[] { idx, _idx });
			_query.appendOr();

			sc = new SearchCondition(StringValue.class, "definitionReference.hierarchyID", "=",
					aview1.getHierarchyID());

			_query.appendWhere(sc, new int[] { idx, _idx });
			_query.appendOr();
			sc = new SearchCondition(StringValue.class, "definitionReference.hierarchyID", "=", aview.getHierarchyID());

			_query.appendWhere(sc, new int[] { idx });
			_query.appendCloseParen();

			_query.appendAnd();

			String[] str = name.split(";");
			// System.out.println("str.length = " + str.length);
			if (str.length == 1) {
				sc = new SearchCondition(StringValue.class, "value2", SearchCondition.LIKE,
						"%" + str[0].toUpperCase() + "%");
				_query.appendWhere(sc, new int[] { idx });
			} else if (str.length >= 2) {
				_query.appendOpenParen();
				sc = new SearchCondition(StringValue.class, "value2", SearchCondition.LIKE,
						"%" + str[0].toUpperCase() + "%");
				_query.appendWhere(sc, new int[] { idx });
				for (int i = 1; i < str.length; i++) {
					_query.appendOr();
					sc = new SearchCondition(StringValue.class, "value2", SearchCondition.LIKE,
							"%" + str[i].toUpperCase() + "%");
					_query.appendWhere(sc, new int[] { idx });
				}
				_query.appendCloseParen();
			}

		}
	}

}
