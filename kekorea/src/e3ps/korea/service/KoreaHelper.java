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
import e3ps.org.service.OrgHelper;
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
		ArrayList<CommonCode> maks = OrgHelper.manager.getUserMaks(sessionUser);
		if (maks.size() == 0) {
			maks = CommonCodeHelper.manager.getArrayCodeList("MAK");
		}
		Map<String, Object> map = new HashMap<String, Object>();
		List<ProjectDTO> list = new ArrayList<ProjectDTO>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Project.class, true);

		query.appendOpenParen();
		for (CommonCode mak : maks) {
			QuerySpecUtils.toEqualsOr(query, idx, Project.class, "makReference.key.id",
					mak.getPersistInfo().getObjectIdentifier().getId());
		}
		query.appendCloseParen();

		ClassAttribute ca = new ClassAttribute(Project.class, Project.P_DATE);
		OrderBy by = new OrderBy(ca, false);
		query.appendOrderBy(by, new int[] { idx });

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
}
