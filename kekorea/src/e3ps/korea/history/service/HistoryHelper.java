package e3ps.korea.history.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.korea.history.History;
import e3ps.korea.history.HistoryOptionLink;
import e3ps.project.Project;
import e3ps.project.ProjectUserLink;
import e3ps.project.template.Template;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;

public class HistoryHelper {

	public static final HistoryHelper manager = new HistoryHelper();
	public static final HistoryService service = ServiceFactory.getService(HistoryService.class);

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<Map<String, Object>> dataList = new ArrayList<>();

		String kekNumber = (String) params.get("kekNumber");
		String keNumber = (String) params.get("keNumber");
		String pdateFrom = (String) params.get("pdateFrom");
		String pdateTo = (String) params.get("pdateTo");
		String userId = (String) params.get("userId");
		String kekState = (String) params.get("kekState");
		String model = (String) params.get("model");
		String customer_name = (String) params.get("customer_name");
		String install_name = (String) params.get("install_name");
		String projectType = (String) params.get("projectType");
		String machineOid = (String) params.get("machineOid");
		String elecOid = (String) params.get("elecOid");
		String softOid = (String) params.get("softOid");
		String mak_name = (String) params.get("mak_name");
		String detail_name = (String) params.get("detail_name");
		String template = (String) params.get("template");
		String description = (String) params.get("description");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(History.class, true);
		int idx_p = query.appendClassList(Project.class, true);

		SearchCondition sc = new SearchCondition(History.class, "projectReference.key.id", Project.class,
				WTAttributeNameIfc.ID_NAME);
		sc.setFromIndicies(new int[] { idx, idx_p }, 0);
		sc.setOuterJoin(1);
		query.appendWhere(sc, new int[] { idx, idx_p });

		QuerySpecUtils.toLikeAnd(query, idx_p, Project.class, Project.KEK_NUMBER, kekNumber);
		QuerySpecUtils.toLikeAnd(query, idx_p, Project.class, Project.KE_NUMBER, keNumber);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx_p, Project.class, Project.P_DATE, pdateFrom, pdateTo);
		QuerySpecUtils.toLikeAnd(query, idx_p, Project.class, Project.USER_ID, userId);
		QuerySpecUtils.toLikeAnd(query, idx_p, Project.class, Project.KEK_STATE, kekState);
		QuerySpecUtils.toLikeAnd(query, idx_p, Project.class, Project.MODEL, model);

		if (!StringUtils.isNull(customer_name)) {
			CommonCode customerCode = (CommonCode) CommonUtils.getObject(customer_name);
			QuerySpecUtils.toEqualsAnd(query, idx_p, Project.class, "customerReference.key.id",
					customerCode.getPersistInfo().getObjectIdentifier().getId());
		}

		if (!StringUtils.isNull(install_name)) {
			CommonCode installCode = (CommonCode) CommonUtils.getObject(install_name);
			QuerySpecUtils.toEqualsAnd(query, idx_p, Project.class, "installReference.key.id",
					installCode.getPersistInfo().getObjectIdentifier().getId());
		}

		if (!StringUtils.isNull(projectType)) {
			CommonCode projectTypeCode = (CommonCode) CommonUtils.getObject(projectType);
			QuerySpecUtils.toEqualsAnd(query, idx_p, Project.class, "projectTypeReference.key.id",
					projectTypeCode.getPersistInfo().getObjectIdentifier().getId());
		}

		if (!StringUtils.isNull(machineOid)) {
			WTUser machine = (WTUser) CommonUtils.getObject(machineOid);
			CommonCode machineCode = CommonCodeHelper.manager.getCommonCode("MACHINE", "USER_TYPE");
			int idx_plink = query.appendClassList(ProjectUserLink.class, false);
			int idx_u = query.appendClassList(WTUser.class, false);

			QuerySpecUtils.toInnerJoin(query, Project.class, ProjectUserLink.class, WTAttributeNameIfc.ID_NAME,
					"roleAObjectRef.key.id", idx_p, idx_plink);
			QuerySpecUtils.toInnerJoin(query, WTUser.class, ProjectUserLink.class, WTAttributeNameIfc.ID_NAME,
					"roleBObjectRef.key.id", idx_u, idx_plink);
			QuerySpecUtils.toEqualsAnd(query, idx_plink, ProjectUserLink.class, "roleBObjectRef.key.id",
					machine.getPersistInfo().getObjectIdentifier().getId());
			QuerySpecUtils.toEqualsAnd(query, idx_plink, ProjectUserLink.class, "projectUserTypeReference.key.id",
					machineCode.getPersistInfo().getObjectIdentifier().getId());
		}

		if (!StringUtils.isNull(elecOid)) {
			WTUser elec = (WTUser) CommonUtils.getObject(machineOid);
			CommonCode elecCode = CommonCodeHelper.manager.getCommonCode("MACHINE", "USER_TYPE");
			int idx_plink = query.appendClassList(ProjectUserLink.class, false);
			int idx_u = query.appendClassList(WTUser.class, false);

			QuerySpecUtils.toInnerJoin(query, Project.class, ProjectUserLink.class, WTAttributeNameIfc.ID_NAME,
					"roleAObjectRef.key.id", idx_p, idx_plink);
			QuerySpecUtils.toInnerJoin(query, WTUser.class, ProjectUserLink.class, WTAttributeNameIfc.ID_NAME,
					"roleBObjectRef.key.id", idx_u, idx_plink);
			QuerySpecUtils.toEqualsAnd(query, idx_plink, ProjectUserLink.class, "roleBObjectRef.key.id",
					elec.getPersistInfo().getObjectIdentifier().getId());
			QuerySpecUtils.toEqualsAnd(query, idx_plink, ProjectUserLink.class, "projectUserTypeReference.key.id",
					elecCode.getPersistInfo().getObjectIdentifier().getId());
		}

		if (!StringUtils.isNull(softOid)) {
			WTUser soft = (WTUser) CommonUtils.getObject(machineOid);
			CommonCode softCode = CommonCodeHelper.manager.getCommonCode("MACHINE", "USER_TYPE");
			int idx_plink = query.appendClassList(ProjectUserLink.class, false);
			int idx_u = query.appendClassList(WTUser.class, false);

			QuerySpecUtils.toInnerJoin(query, Project.class, ProjectUserLink.class, WTAttributeNameIfc.ID_NAME,
					"roleAObjectRef.key.id", idx_p, idx_plink);
			QuerySpecUtils.toInnerJoin(query, WTUser.class, ProjectUserLink.class, WTAttributeNameIfc.ID_NAME,
					"roleBObjectRef.key.id", idx_u, idx_plink);
			QuerySpecUtils.toEqualsAnd(query, idx_plink, ProjectUserLink.class, "roleBObjectRef.key.id",
					soft.getPersistInfo().getObjectIdentifier().getId());
			QuerySpecUtils.toEqualsAnd(query, idx_plink, ProjectUserLink.class, "projectUserTypeReference.key.id",
					softCode.getPersistInfo().getObjectIdentifier().getId());
		}

		if (!StringUtils.isNull(mak_name)) {
			CommonCode makCode = (CommonCode) CommonUtils.getObject(mak_name);
			QuerySpecUtils.toEqualsAnd(query, idx_p, Project.class, "makReference.key.id",
					makCode.getPersistInfo().getObjectIdentifier().getId());
		}

		if (!StringUtils.isNull(detail_name)) {
			CommonCode detailCode = (CommonCode) CommonUtils.getObject(detail_name);
			QuerySpecUtils.toEqualsAnd(query, idx_p, Project.class, "detailReference.key.id",
					detailCode.getPersistInfo().getObjectIdentifier().getId());
		}

		if (!StringUtils.isNull(template)) {
			Template t = (Template) CommonUtils.getObject(template);
			QuerySpecUtils.toEqualsAnd(query, idx_p, Project.class, "templateReference.key.id",
					t.getPersistInfo().getObjectIdentifier().getId());
		}

		QuerySpecUtils.toLikeAnd(query, idx_p, Project.class, Project.DESCRIPTION, description);
		QuerySpecUtils.toOrderBy(query, idx_p, Project.class, Project.P_DATE, false);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			History history = (History) obj[0];
			Project project = (Project) obj[1];
			ArrayList<HistoryOptionLink> data = getLinks(history);
			Map<String, Object> dataMap = new HashMap<>();
			dataMap.put("kekNumber", project.getKekNumber());
			dataMap.put("keNumber", project.getKeNumber());
			dataMap.put("mak", project.getMak() != null ? project.getMak().getName() : "");
			dataMap.put("detail", project.getDetail() != null ? project.getDetail().getName() : "");
			dataMap.put("customer", project.getCustomer() != null ? project.getCustomer().getName() : "");
			dataMap.put("install", project.getInstall() != null ? project.getInstall().getName() : "");
			dataMap.put("pdate", project.getPDate());
			dataMap.put("tuv", history != null ? history.getTuv() : "");
			dataMap.put("poid", project.getPersistInfo().getObjectIdentifier().getStringValue());
			dataMap.put("oid", history != null ? history.getPersistInfo().getObjectIdentifier().getStringValue() : "");
			for (HistoryOptionLink link : data) {
				String dataField = link.getDataField();
				String value = link.getOption().getCode();
				dataMap.put(dataField, value);
			}
			dataList.add(dataMap);
		}
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		map.put("list", dataList);
		return map;
	}

	public ArrayList<HistoryOptionLink> getLinks(History history) throws Exception {
		ArrayList<HistoryOptionLink> list = new ArrayList<HistoryOptionLink>();
		if (history == null) {
			return list;
		}

		QueryResult result = PersistenceHelper.manager.navigate(history, "option", HistoryOptionLink.class, false);
		while (result.hasMoreElements()) {
			HistoryOptionLink link = (HistoryOptionLink) result.nextElement();
			list.add(link);
		}
		return list;
	}
}
