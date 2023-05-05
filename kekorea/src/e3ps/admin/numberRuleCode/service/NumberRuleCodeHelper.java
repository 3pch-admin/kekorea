package e3ps.admin.numberRuleCode.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import e3ps.admin.numberRuleCode.NumberRuleCode;
import e3ps.admin.numberRuleCode.NumberRuleCodeType;
import e3ps.admin.numberRuleCode.dto.NumberRuleCodeDTO;
import e3ps.common.util.QuerySpecUtils;
import net.sf.json.JSONArray;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;

public class NumberRuleCodeHelper {

	public static final NumberRuleCodeHelper manager = new NumberRuleCodeHelper();
	public static final NumberRuleCodeService service = ServiceFactory.getService(NumberRuleCodeService.class);

	/**
	 * KEK 도번(NumberRuleCode) 가져 오는 함수
	 */
	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<NumberRuleCodeDTO> list = new ArrayList<>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(NumberRuleCode.class, true);
		QuerySpecUtils.toOrderBy(query, idx, NumberRuleCode.class, NumberRuleCode.CODE_TYPE, false);
		QuerySpecUtils.toOrderBy(query, idx, NumberRuleCode.class, NumberRuleCode.SORT, false);

		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			NumberRuleCode numberRuleCode = (NumberRuleCode) obj[0];
			NumberRuleCodeDTO column = new NumberRuleCodeDTO(numberRuleCode);
			list.add(column);
		}
		map.put("list", list);
		return map;
	}

	/**
	 * 배열형태로 코드타입값이 일치하는 NumberRuleCode 가져오는 함수
	 */
	public ArrayList<NumberRuleCode> getArrayNumberRuleCode(String codeType) throws Exception {
		ArrayList<NumberRuleCode> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(NumberRuleCode.class, true);

		QuerySpecUtils.toEqualsAnd(query, idx, NumberRuleCode.class, NumberRuleCode.CODE_TYPE, codeType);
		QuerySpecUtils.toBooleanAnd(query, idx, NumberRuleCode.class, NumberRuleCode.ENABLE, true);
		QuerySpecUtils.toOrderBy(query, idx, NumberRuleCode.class, NumberRuleCode.SORT, false);

		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			NumberRuleCode numberRuleCode = (NumberRuleCode) obj[0];
			list.add(numberRuleCode);
		}
		return list;
	}

	/**
	 * NumberRuleCodeType 전체를 JSONArray 로 변경 후리턴
	 */
	public JSONArray parseJson() throws Exception {
		NumberRuleCodeType[] codeTypes = NumberRuleCodeType.getNumberRuleCodeTypeSet();
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		for (NumberRuleCodeType codeType : codeTypes) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("key", codeType.toString());
			map.put("value", codeType.getDisplay());
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * NumberRuleCodeType 과 일치하는 데이터 JSONArray 로 변경 후리턴
	 */
	public JSONArray parseJson(String numberRuleCodeType) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(NumberRuleCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, NumberRuleCode.class, NumberRuleCode.CODE_TYPE, numberRuleCodeType);
		QuerySpecUtils.toBooleanAnd(query, idx, NumberRuleCode.class, NumberRuleCode.ENABLE, true);
		QuerySpecUtils.toOrderBy(query, idx, NumberRuleCode.class, NumberRuleCode.SORT, false);
		ArrayList<Map<String, String>> list = new ArrayList<>();
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			NumberRuleCode numberRuleCode = (NumberRuleCode) obj[0];
			Map<String, String> map = new HashMap<>();
			map.put("key", numberRuleCode.getCode());
			map.put("value", numberRuleCode.getName());
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 코드타입과 코드가 일치하는 NumberRuleCode 가져오기
	 */
	public NumberRuleCode getNumberRuleCode(String numberRuleCodeType, String code) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(NumberRuleCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, NumberRuleCode.class, NumberRuleCode.CODE_TYPE, numberRuleCodeType);
		QuerySpecUtils.toEqualsAnd(query, idx, NumberRuleCode.class, NumberRuleCode.CODE, code);
		QuerySpecUtils.toBooleanAnd(query, idx, NumberRuleCode.class, NumberRuleCode.ENABLE, true);
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			NumberRuleCode numberRuleCode = (NumberRuleCode) obj[0];
			return numberRuleCode;
		}
		return null;
	}
}
