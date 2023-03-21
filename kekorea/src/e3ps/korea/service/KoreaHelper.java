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
		WTUser sessionUser = CommonUtils.sessionUser();
		Map<String, Object> map = new HashMap<String, Object>();
		List<ProjectDTO> list = new ArrayList<ProjectDTO>();
		String code = (String) params.get("code");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Project.class, true);

		CommonCode makCode = CommonCodeHelper.manager.getCommonCode(code, "MAK");
		QuerySpecUtils.toEqualsOr(query, idx, Project.class, "makReference.key.id",
				makCode.getPersistInfo().getObjectIdentifier().getId());

		ClassAttribute ca = new ClassAttribute(Project.class, Project.P_DATE);
		OrderBy by = new OrderBy(ca, false);
		query.appendOrderBy(by, new int[] { idx });

		System.out.println(query);

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

	public int yAxisValue(String makCode, CommonCode customer) throws Exception {
		CommonCode mak = CommonCodeHelper.manager.getCommonCode(makCode, "MAK");
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Project.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, Project.class, "makReference.key.id",
				mak.getPersistInfo().getObjectIdentifier().getId());
		QuerySpecUtils.toEqualsAnd(query, idx, Project.class, "customerReference.key.id",
				customer.getPersistInfo().getObjectIdentifier().getId());
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
	 * 막종별 고객사 차트 데이터
	 */
	public ArrayList<String> data(String code) throws Exception {
		ArrayList<String> data = new ArrayList<>();
		ArrayList<CommonCode> customers = CommonCodeHelper.manager.getArrayCodeList("CUSTOMER");

		for (CommonCode customer : customers) {

			int value = KoreaHelper.manager.yAxisValue(code, customer);
			if (value > 0) {
				String dataValue = customer.getName() + "&" + customer.getCode() + "&" + value;
				data.add(dataValue);
			}
		}
		return data;
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
}
