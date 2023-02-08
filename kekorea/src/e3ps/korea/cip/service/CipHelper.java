package e3ps.korea.cip.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.korea.cip.Cip;
import e3ps.korea.cip.beans.CipColumnData;
import wt.fc.PagingQueryResult;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;

public class CipHelper {

	public static final CipHelper manager = new CipHelper();
	public static final CipService service = ServiceFactory.getService(CipService.class);

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();

		String item = (String) params.get("item");
		String improvements = (String) params.get("improvements");
		String improvement = (String) params.get("improvement");
		String apply = (String) params.get("apply");
		String makCode = (String) params.get("mak");
		String installCode = (String) params.get("install");
		String customerCode = (String) params.get("customer");
		String note = (String) params.get("note");

		List<CipColumnData> list = new ArrayList<CipColumnData>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Cip.class, true);

		if (!StringUtils.isNull(item)) {
			QuerySpecUtils.toLike(query, idx, Cip.class, Cip.ITEM, item);
		}

		if (!StringUtils.isNull(improvements)) {
			QuerySpecUtils.toLike(query, idx, Cip.class, Cip.IMPROVEMENTS, improvements);
		}

		if (!StringUtils.isNull(improvement)) {
			QuerySpecUtils.toLike(query, idx, Cip.class, Cip.IMPROVEMENT, improvement);
		}

		if (!StringUtils.isNull(apply)) {
			QuerySpecUtils.toEquals(query, idx, Cip.class, Cip.APPLY, apply);
		}

		if (!StringUtils.isNull(makCode)) {
			CommonCode mak = CommonCodeHelper.manager.getCommonCode(makCode, "MAK");
			QuerySpecUtils.toEquals(query, idx, Cip.class, "makReference.key.id",
					mak.getPersistInfo().getObjectIdentifier().getId());
		}

		if (!StringUtils.isNull(installCode)) {
			CommonCode install = CommonCodeHelper.manager.getCommonCode(installCode, "INSTALL");
			QuerySpecUtils.toEquals(query, idx, Cip.class, "installReference.key.id",
					install.getPersistInfo().getObjectIdentifier().getId());
		}

		if (!StringUtils.isNull(customerCode)) {
			CommonCode customer = CommonCodeHelper.manager.getCommonCode(customerCode, "CUSTOMER");
			QuerySpecUtils.toEquals(query, idx, Cip.class, "customerReference.key.id",
					customer.getPersistInfo().getObjectIdentifier().getId());
		}

		if (!StringUtils.isNull(note)) {
			QuerySpecUtils.toLike(query, idx, Cip.class, Cip.NOTE, note);
		}

		QuerySpecUtils.toOrderBy(query, idx, Cip.class, Cip.CREATE_TIMESTAMP, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Cip cip = (Cip) obj[0];
			CipColumnData column = new CipColumnData(cip);
			list.add(column);
		}
		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}
}
