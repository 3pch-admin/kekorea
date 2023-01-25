package e3ps.common.code.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.admin.commonCode.CommonCode;
import e3ps.common.util.MessageHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;

public class CommonCodeHelper implements MessageHelper {

	/**
	 * access service
	 */
	public static final CommonCodeService service = ServiceFactory.getService(CommonCodeService.class);

	/**
	 * access helper
	 */
	public static final CommonCodeHelper manager = new CommonCodeHelper();

	public String getInstallCommonCodeByName(String name) throws Exception {
		String list = "[";
		QuerySpec query = null;
		try {

			query = new QuerySpec();
			int idx = query.appendClassList(CommonCode.class, true);

			SearchCondition sc = null;

			sc = new SearchCondition(CommonCode.class, CommonCode.NAME, "=", name);
			query.appendWhere(sc, new int[] { idx });

			query.appendAnd();

			sc = new SearchCondition(CommonCode.class, CommonCode.CODE_TYPE, "=", "INSTALL");
			query.appendWhere(sc, new int[] { idx });

			ClassAttribute ca = new ClassAttribute(CommonCode.class, CommonCode.NAME);
			OrderBy orderBy = new OrderBy(ca, true);
			query.appendOrderBy(orderBy, new int[] { idx });

			CommonCode commonCode = null;
			QueryResult result = PersistenceHelper.manager.find(query);
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				commonCode = (CommonCode) obj[0];
				// ['Apples','Bananas','Oranges'];
				list += "'" + commonCode.getCode() + "',";
			}

			list += "]";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public String getCommonCodeByCommonCodeType(String commonCodeType) throws Exception {
		String data = "";
		try {
			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(CommonCode.class, true);

			query.appendWhere(
					new SearchCondition(CommonCode.class, CommonCode.CODE_TYPE, SearchCondition.EQUAL, commonCodeType),
					new int[] { idx });

			ClassAttribute ca = new ClassAttribute(CommonCode.class, CommonCode.NAME);
			OrderBy orderBy = new OrderBy(ca, true);
			query.appendOrderBy(orderBy, new int[] { idx });

			QueryResult result = PersistenceHelper.manager.find(query);

			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				CommonCode code = (CommonCode) obj[0];
				data += "'" + code.getCode() + "',";

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	public ArrayList<String> getCommonCode(String type) throws Exception {
		ArrayList<String> list = new ArrayList<String>();
		try {
			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(CommonCode.class, true);

			query.appendWhere(new SearchCondition(CommonCode.class, CommonCode.CODE_TYPE, SearchCondition.EQUAL, type),
					new int[] { idx });

			ClassAttribute ca = new ClassAttribute(CommonCode.class, CommonCode.SORT);
			OrderBy orderBy = new OrderBy(ca, false);
			query.appendOrderBy(orderBy, new int[] { idx });

			QueryResult result = PersistenceHelper.manager.find(query);

			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				CommonCode code = (CommonCode) obj[0];
				list.add(code.getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public ArrayList<String> getLocationCode(String type, String name) throws Exception {

		ArrayList<String> list = new ArrayList<String>();
		try {
			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(CommonCode.class, true);

			query.appendWhere(new SearchCondition(CommonCode.class, CommonCode.CODE_TYPE, SearchCondition.EQUAL, type),
					new int[] { idx });

			query.appendAnd();

			query.appendWhere(new SearchCondition(CommonCode.class, CommonCode.NAME, SearchCondition.EQUAL, name),
					new int[] { idx });

			ClassAttribute ca = new ClassAttribute(CommonCode.class, CommonCode.NAME);
			OrderBy orderBy = new OrderBy(ca, true);
			query.appendOrderBy(orderBy, new int[] { idx });

			QueryResult result = PersistenceHelper.manager.find(query);

			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				CommonCode code = (CommonCode) obj[0];
				list.add(code.getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public Map<String, Object> getInstall(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>(); // json
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(); // data
		String name = (String) param.get("name");
		QuerySpec query = null;
		try {

			query = new QuerySpec();
			int idx = query.appendClassList(CommonCode.class, true);

			SearchCondition sc = null;

			sc = new SearchCondition(CommonCode.class, CommonCode.NAME, "=", name);
			query.appendWhere(sc, new int[] { idx });

			query.appendAnd();

			sc = new SearchCondition(CommonCode.class, CommonCode.CODE_TYPE, "=", "INSTALL");
			query.appendWhere(sc, new int[] { idx });
			
			query.appendAnd();

			sc = new SearchCondition(CommonCode.class, CommonCode.USES, SearchCondition.IS_TRUE);
			query.appendWhere(sc, new int[] { idx });

			ClassAttribute ca = new ClassAttribute(CommonCode.class, CommonCode.CODE);
			OrderBy orderBy = new OrderBy(ca, false);
			query.appendOrderBy(orderBy, new int[] { idx });

			CommonCode commonCode = null;
			QueryResult result = PersistenceHelper.manager.find(query);

			Map<String, Object> emptyMap = new HashMap<String, Object>();
			emptyMap.put("name", "선택");
			emptyMap.put("value", "");
			list.add(emptyMap);
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				commonCode = (CommonCode) obj[0];
				Map<String, Object> codeMap = new HashMap<String, Object>();
				codeMap.put("name", commonCode.getCode());
				codeMap.put("value", commonCode.getCode());
				list.add(codeMap);
			}

			map.put("result", "ok");
			map.put("list", list);
		} catch (Exception e) {
			e.printStackTrace();
			map.put("result", FAIL);
			map.put("msg", FAIL_DATA_LOAD);
		}
		return map;
	}
}
