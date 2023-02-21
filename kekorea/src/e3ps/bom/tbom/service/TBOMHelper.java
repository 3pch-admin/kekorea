package e3ps.bom.tbom.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.bom.partlist.PartListMasterProjectLink;
import e3ps.bom.tbom.TBOMData;
import e3ps.bom.tbom.TBOMMaster;
import e3ps.bom.tbom.TBOMMasterDataLink;
import e3ps.bom.tbom.TBOMMasterProjectLink;
import e3ps.bom.tbom.beans.TBOMColumnData;
import e3ps.bom.tbom.beans.TBOMMasterColumnData;
import e3ps.bom.tbom.beans.TBOMMasterViewData;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.DateUtils;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.project.Project;
import net.sf.json.JSONArray;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;

public class TBOMHelper {

	public static final TBOMHelper manager = new TBOMHelper();
	public static final TBOMService service = ServiceFactory.getService(TBOMService.class);

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		List<TBOMMasterColumnData> list = new ArrayList<>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(TBOMMaster.class, true);
		int idx_link = query.appendClassList(TBOMMasterProjectLink.class, true);
		int idx_p = query.appendClassList(Project.class, true);

		QuerySpecUtils.toInnerJoin(query, TBOMMasterProjectLink.class, TBOMMaster.class, "roleAObjectRef.key.id",
				WTAttributeNameIfc.ID_NAME, idx_link, idx);
		QuerySpecUtils.toInnerJoin(query, TBOMMasterProjectLink.class, Project.class, "roleBObjectRef.key.id",
				WTAttributeNameIfc.ID_NAME, idx_link, idx_p);

		QuerySpecUtils.toOrderBy(query, idx, TBOMMaster.class, TBOMMaster.CREATE_TIMESTAMP, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			TBOMMaster master = (TBOMMaster) obj[0];
			Project project = (Project) obj[2];
			TBOMMasterColumnData column = new TBOMMasterColumnData(master, project);
			list.add(column);
		}
		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	public String getNextNumber(String param) throws Exception {
		String preFix = DateUtils.getTodayString();
		String number = param + "-" + preFix + "-";
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(TBOMMaster.class, true);

		SearchCondition sc = new SearchCondition(TBOMMaster.class, TBOMMaster.T_NUMBER, "LIKE",
				number.toUpperCase() + "%");
		query.appendWhere(sc, new int[] { idx });

		ClassAttribute attr = new ClassAttribute(TBOMMaster.class, TBOMMaster.T_NUMBER);
		OrderBy orderBy = new OrderBy(attr, true);
		query.appendOrderBy(orderBy, new int[] { idx });

		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			TBOMMaster master = (TBOMMaster) obj[0];

			String s = master.getTNumber().substring(master.getTNumber().lastIndexOf("-") + 1);

			int ss = Integer.parseInt(s) + 1;
			DecimalFormat d = new DecimalFormat("0000");
			number += d.format(ss);
		} else {
			number += "0001";
		}
		return number;
	}

	public JSONArray auiArray(TBOMMaster master) throws Exception {
		ArrayList<TBOMColumnData> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(TBOMMasterDataLink.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, TBOMMasterDataLink.class, "roleAObjectRef.key.id",
				master.getPersistInfo().getObjectIdentifier().getId());
		QuerySpecUtils.toOrderBy(query, idx, TBOMMasterDataLink.class, TBOMMasterDataLink.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			TBOMMasterDataLink link = (TBOMMasterDataLink) obj[0];
			TBOMData data = link.getData();
			TBOMColumnData column = new TBOMColumnData(data);
			list.add(column);
		}
		return JSONArray.fromObject(list);
	}
}
