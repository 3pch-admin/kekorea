package e3ps.korea.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.project.Project;
import e3ps.project.dto.ProjectDTO;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;

public class KoreaHelper {

	public static final KoreaHelper manager = new KoreaHelper();
	public static final KoreaService service = ServiceFactory.getService(KoreaService.class);

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<ProjectDTO> list = new ArrayList<>();
		ArrayList<String> kekNumbers = (ArrayList<String>) params.get("kekNumbers");
		String pdateFrom = (String) params.get("pdateFrom");
		String pdateTo = (String) params.get("pdateTo");
		String projectType = (String) params.get("projectType");
		ArrayList<String> maks = (ArrayList<String>) params.get("maks");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Project.class, true);

		if (!kekNumbers.isEmpty()) {
			String[] kekNumber = new String[kekNumbers.size()];
			for (int i = 0; i < kekNumbers.size(); i++) {
				String value = (String) kekNumbers.get(i);
				kekNumber[i] = value.toUpperCase();
			}
			QuerySpecUtils.toIn(query, idx, Project.class, Project.KEK_NUMBER, kekNumber);
		}

		if (!maks.isEmpty()) {
			long[] mak = new long[maks.size()];
			for (int i = 0; i < maks.size(); i++) {
				String value = (String) maks.get(i);
				CommonCode makCode = CommonCodeHelper.manager.getCommonCode(value, "MAK");
				mak[i] = makCode.getPersistInfo().getObjectIdentifier().getId();
			}
			QuerySpecUtils.toIn(query, idx, Project.class, "makReference.key.id", mak);
		}

		QuerySpecUtils.toTimeGreaterAndLess(query, idx, Project.class, Project.P_DATE, pdateFrom, pdateTo);
		if (!StringUtils.isNull(projectType)) {
			CommonCode projectTypeCode = (CommonCode) CommonUtils.getObject(projectType);
			QuerySpecUtils.toEqualsAnd(query, idx, Project.class, "projectTypeReference.key.id", projectTypeCode);
		}

		QuerySpecUtils.toOrderBy(query, idx, Project.class, Project.P_DATE, false);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Project project = (Project) obj[0];
			ProjectDTO column = new ProjectDTO(project);
			list.add(column);
		}

		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	public int yAxisValue(String makCode, CommonCode customer, String kekNumbers, String pdateFrom, String pdateTo,
			String projectType) throws Exception {
		CommonCode mak = CommonCodeHelper.manager.getCommonCode(makCode, "MAK");
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Project.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, Project.class, "makReference.key.id", mak);
		QuerySpecUtils.toEqualsAnd(query, idx, Project.class, "customerReference.key.id", customer);

		if (!StringUtils.isNull(kekNumbers)) {
			String[] split = kekNumbers.split(",");
			String[] kekNumber = new String[split.length];
			for (int i = 0; i < split.length; i++) {
				String value = split[i];
				kekNumber[i] = value.toUpperCase();
			}
			QuerySpecUtils.toIn(query, idx, Project.class, Project.KEK_NUMBER, kekNumber);
		}

		QuerySpecUtils.toTimeGreaterAndLess(query, idx, Project.class, Project.P_DATE, pdateFrom, pdateTo);
		if (!StringUtils.isNull(projectType)) {
			CommonCode projectTypeCode = (CommonCode) CommonUtils.getObject(projectType);
			QuerySpecUtils.toEqualsAnd(query, idx, Project.class, "projectTypeReference.key.id", projectTypeCode);
		}

		QueryResult result = PersistenceHelper.manager.find(query);
		return result.size();
	}

	public int yAxisValueForInstall(String makCode, CommonCode customer, CommonCode install) throws Exception {
		CommonCode mak = CommonCodeHelper.manager.getCommonCode(makCode, "MAK");
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Project.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, Project.class, "makReference.key.id",
				mak.getPersistInfo().getObjectIdentifier().getId());
		QuerySpecUtils.toEqualsAnd(query, idx, Project.class, "customerReference.key.id",
				customer.getPersistInfo().getObjectIdentifier().getId());
		QuerySpecUtils.toEqualsAnd(query, idx, Project.class, "installReference.key.id",
				install.getPersistInfo().getObjectIdentifier().getId());
		QueryResult result = PersistenceHelper.manager.find(query);
		return result.size();
	}

	/**
	 * 고객사별 설치 장소 데이터
	 */
	public Map<String, ArrayList<String>> drillDown(String code, ArrayList<String> data) throws Exception {
		Map<String, ArrayList<String>> map = new HashMap<>();
		for (String dataValue : data) {
			String key = dataValue.split("&")[1];
			CommonCode customer = CommonCodeHelper.manager.getCommonCode(key, "CUSTOMER");
			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(CommonCode.class, true);
			QuerySpecUtils.toEqualsAnd(query, idx, CommonCode.class, CommonCode.CODE_TYPE, "INSTALL");
			QuerySpecUtils.toEqualsAnd(query, idx, CommonCode.class, "parentReference.key.id",
					customer.getPersistInfo().getObjectIdentifier().getId());
			QuerySpecUtils.toOrderBy(query, idx, CommonCode.class, CommonCode.NAME, false);
			QueryResult result = PersistenceHelper.manager.find(query);

			ArrayList<String> list = new ArrayList<>();
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				CommonCode install = (CommonCode) obj[0];
				int value = yAxisValueForInstall(code, customer, install);
				if (value > 0) {
					list.add(install.getName() + "&" + install.getCode() + "&" + value);
				}
			}
			map.put(customer.getCode(), list);
		}
		return map;
	}

	public Map<String, ArrayList<Integer>> data(String kekNumbers, String pdateFrom, String pdateTo, String projectType,
			String maks) throws Exception {
		Map<String, ArrayList<Integer>> result = new HashMap<>();
		ArrayList<CommonCode> customers = CommonCodeHelper.manager.getArrayCodeList("CUSTOMER");
		String[] mak = maks.split(",");

		for (String code : mak) {
			ArrayList<Integer> data = new ArrayList<>();
			for (CommonCode customer : customers) {
				int value = KoreaHelper.manager.yAxisValue(code, customer, kekNumbers, pdateFrom, pdateTo, projectType);
				data.add(value);
			}
			result.put(code, data);
		}
		return result;
	}
}
