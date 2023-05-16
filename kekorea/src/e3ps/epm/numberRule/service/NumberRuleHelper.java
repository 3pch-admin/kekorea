package e3ps.epm.numberRule.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import e3ps.admin.numberRuleCode.NumberRuleCode;
import e3ps.admin.numberRuleCode.service.NumberRuleCodeHelper;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.epm.numberRule.NumberRule;
import e3ps.epm.numberRule.NumberRuleMaster;
import e3ps.epm.numberRule.dto.NumberRuleDTO;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;

public class NumberRuleHelper {

	public static final NumberRuleHelper manager = new NumberRuleHelper();
	public static final NumberRuleService service = ServiceFactory.getService(NumberRuleService.class);

	public static HashMap<String, String> numberCache = null;
	public static final String[] alphabet = new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L",
			"M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
	static {
		if (numberCache == null) {
			numberCache = new HashMap<>();
		}
	}

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<NumberRuleDTO> list = new ArrayList<NumberRuleDTO>();

		String name = (String) params.get("name");
		String number = (String) params.get("number");
		String writtenDocuments = (String) params.get("writtenDocuments_code");
		String classificationWritingDepartments = (String) params.get("classificationWritingDepartments_code");
		String state = (String) params.get("state");
		String creatorOid = (String) params.get("creatorOid");
		String createdFrom = (String) params.get("createdFrom");
		String createdTo = (String) params.get("createdTo");
		String size = (String) params.get("size");
		String lotNo = (String) params.get("lotNo");
		String unitName = (String) params.get("unitName");
		boolean latest = (boolean) params.get("latest");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(NumberRule.class, true);
		int idx_m = query.appendClassList(NumberRuleMaster.class, false);

		QuerySpecUtils.toInnerJoin(query, NumberRule.class, NumberRuleMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);

		QuerySpecUtils.toLikeAnd(query, idx_m, NumberRuleMaster.class, NumberRuleMaster.NAME, name);
		QuerySpecUtils.toLikeAnd(query, idx_m, NumberRuleMaster.class, NumberRuleMaster.NUMBER, number);
		QuerySpecUtils.toEqualsAnd(query, idx, NumberRule.class, NumberRule.STATE, state);
		QuerySpecUtils.toLikeAnd(query, idx_m, NumberRuleMaster.class, NumberRuleMaster.LOT_NO, lotNo);
		QuerySpecUtils.toLikeAnd(query, idx_m, NumberRuleMaster.class, NumberRuleMaster.UNIT_NAME, unitName);

		QuerySpecUtils.toCreator(query, idx, NumberRule.class, creatorOid);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, NumberRule.class, NumberRule.CREATE_TIMESTAMP, createdFrom,
				createdTo);

		if (!StringUtils.isNull(size)) {
			NumberRuleCode sizeCode = NumberRuleCodeHelper.manager.getNumberRuleCode("SIZE", size);
			QuerySpecUtils.toEqualsAnd(query, idx_m, NumberRuleMaster.class, "sizeReference.key.id", sizeCode);
		}
		if (!StringUtils.isNull(writtenDocuments)) {
			NumberRuleCode writtenDocumentsCode = NumberRuleCodeHelper.manager.getNumberRuleCode("WRITTEN_DOCUMENT",
					writtenDocuments);
			QuerySpecUtils.toEqualsAnd(query, idx_m, NumberRuleMaster.class, "documentReference.key.id",
					writtenDocumentsCode);
		}

		if (!StringUtils.isNull(classificationWritingDepartments)) {
			NumberRuleCode classificationWritingDepartmentsCode = NumberRuleCodeHelper.manager
					.getNumberRuleCode("CLASSIFICATION_WRITING_DEPARTMENT", classificationWritingDepartments);
			QuerySpecUtils.toEqualsAnd(query, idx_m, NumberRuleMaster.class, "departmentReference.key.id",
					classificationWritingDepartmentsCode);
		}

		if (latest) {
			QuerySpecUtils.toBooleanAnd(query, idx, NumberRule.class, NumberRule.LATEST, true);
		} else {
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			query.appendOpenParen();
			SearchCondition sc = new SearchCondition(NumberRule.class, NumberRule.LATEST, SearchCondition.IS_TRUE);
			query.appendWhere(sc, new int[] { idx });
			QuerySpecUtils.toBooleanOr(query, idx, NumberRule.class, NumberRule.LATEST, false);
			query.appendCloseParen();
		}
		QuerySpecUtils.toOrderBy(query, idx, NumberRule.class, NumberRule.CREATE_TIMESTAMP, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			NumberRule numberRule = (NumberRule) obj[0];
			NumberRuleDTO column = new NumberRuleDTO(numberRule);
			list.add(column);
		}

		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	/**
	 * KEK 도번 마지막 도번 + 1 가져오기
	 */
	public Map<String, Object> last(String number) throws Exception {
		Map<String, Object> map = new HashMap<>();
		DecimalFormat df = new DecimalFormat("00000");
		String seq1 = "A";
		String seq2 = df.format(00001);
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(NumberRuleMaster.class, true);
		QuerySpecUtils.toLikeRightAnd(query, idx, NumberRuleMaster.class, NumberRuleMaster.NUMBER, number);
		QuerySpecUtils.toOrderBy(query, idx, NumberRuleMaster.class, NumberRuleMaster.NUMBER, true);
		QueryResult result = PersistenceHelper.manager.find(query);
		String next = "A00000";

		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			NumberRuleMaster rule = (NumberRuleMaster) obj[0];
			seq1 = rule.getNumber().substring(3, 4);
			seq2 = df.format(Integer.parseInt(rule.getNumber().substring(4, 9))); // 00001
			next = seq1 + df.format(Integer.parseInt(rule.getNumber().substring(4, 9)) + 1); // 00001
			int pos = 0;
			for (int i = 0; i < alphabet.length; i++) {
				if (seq1.equals(alphabet[i])) {
					pos = i;
					break;
				}
			}
			if (Integer.parseInt(seq2) == 99999) {
				next = alphabet[pos + 1] + df.format(00001);
			}
		}
		map.put("next", next);
		map.put("last", number + seq1 + seq2);
		return map;
	}

	/**
	 * 마지막 KEK 도번인지 확인 하는 함수
	 */
	public boolean isLast(NumberRuleMaster master) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(NumberRule.class, true);
		int idx_m = query.appendClassList(NumberRuleMaster.class, false);
		QuerySpecUtils.toInnerJoin(query, NumberRule.class, NumberRuleMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);
		QuerySpecUtils.toEqualsAnd(query, idx, NumberRule.class, "masterReference.key.id", master);
		QueryResult result = PersistenceHelper.manager.find(query);
		return result.size() == 1 ? true : false;
	}

	/**
	 * 현재버전의 KEK 도번의 이전 버전의 도면을 가져오는 함수
	 */
	public NumberRule predecessor(NumberRule latest) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(NumberRule.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, NumberRule.class, NumberRule.VERSION, latest.getVersion() - 1);
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			return (NumberRule) obj[0];
		}
		return null;
	}

	/**
	 * 등록된 KEK 도번이 있는지
	 */
	public Map<String, Object> isValid(ArrayList<NumberRuleDTO> addRow, ArrayList<NumberRuleDTO> editRow)
			throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		for (NumberRuleDTO dto : addRow) {
			String number = dto.getNumber();
			boolean isExist = exist(number);
			if (isExist) {
				result.put("isExist", true); // 존재하는거 true
				result.put("msg", "도번이 = " + number + "가 이미 존재합니다.");
				return result;
			}
		}

		for (NumberRuleDTO dto : editRow) {
			NumberRuleMaster master = (NumberRuleMaster) CommonUtils.getObject(dto.getMoid());
			String number = master.getNumber();
			String diffNumber = dto.getNumber();

			// 원본 도면의 번호 혹은 LON NO 가 변경 될시 체크만한다...
			if (!number.equals(diffNumber)) {
				boolean isExist = exist(diffNumber);
				if (isExist) {
					result.put("isExist", true); // 존재하는거 true
					result.put("msg", "도번이 = " + diffNumber + "가 이미 존재합니다.");
					return result;
				}
			}
		}
		// 아무것도 없다면 false
		result.put("isExist", false);
		return result;
	}

	/**
	 * KEK 도번이 존재하는지
	 */
	private boolean exist(String number) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx_m = query.appendClassList(NumberRuleMaster.class, true);
		int idx = query.appendClassList(NumberRule.class, true);
		QuerySpecUtils.toInnerJoin(query, NumberRuleMaster.class, NumberRule.class, WTAttributeNameIfc.ID_NAME,
				"masterReference.key.id", idx_m, idx);
		QuerySpecUtils.toEqualsAnd(query, idx_m, NumberRuleMaster.class, NumberRuleMaster.NUMBER, number);
		QueryResult result = PersistenceHelper.manager.find(query);
		return result.size() > 0 ? true : false;
	}

	/**
	 * 도번 가져오기 CREO AUTOCAD DWG AND VERSION
	 */
	public NumberRule numberRuleForNumberAndVersion(String dwgNo, String version) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(NumberRule.class, true);
		int idx_m = query.appendClassList(NumberRuleMaster.class, false);
		QuerySpecUtils.toInnerJoin(query, NumberRuleMaster.class, NumberRule.class, WTAttributeNameIfc.ID_NAME,
				"masterReference.key.id", idx_m, idx);
		QuerySpecUtils.toEqualsAnd(query, idx_m, NumberRuleMaster.class, NumberRuleMaster.NUMBER, dwgNo);
		QuerySpecUtils.toEqualsAnd(query, idx, NumberRule.class, NumberRule.VERSION, Integer.parseInt(version));
		QueryResult qr = PersistenceHelper.manager.find(query);
		if (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			NumberRule numberRule = (NumberRule) obj[0];
			return numberRule;
		}
		return null;
	}

}
