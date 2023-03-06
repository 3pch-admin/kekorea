package e3ps.part.kePart.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.part.kePart.KePart;
import e3ps.part.kePart.KePartMaster;
import e3ps.part.kePart.beans.KePartColumnData;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;

public class KePartHelper {

	public static final KePartHelper manager = new KePartHelper();
	public static final KePartService service = ServiceFactory.getService(KePartService.class);

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<KePartColumnData> list = new ArrayList<>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(KePart.class, true);
		int idx_m = query.appendClassList(KePartMaster.class, false);

		QuerySpecUtils.toInnerJoin(query, KePart.class, KePartMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);
		QuerySpecUtils.toBooleanAnd(query, idx, KePart.class, KePart.LATEST, SearchCondition.IS_TRUE);
		QuerySpecUtils.toOrderBy(query, idx, KePart.class, KePart.CREATE_TIMESTAMP, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			KePart kePart = (KePart) obj[0];
			KePartColumnData column = new KePartColumnData(kePart);
			list.add(column);
		}
		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	public Map<String, Object> get(String kePartNumber) throws Exception {
		Map<String, Object> map = new HashMap<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(KePart.class, true);
		int idx_m = query.appendClassList(KePartMaster.class, true);

		QuerySpecUtils.toInnerJoin(query, KePart.class, KePartMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);
		QuerySpecUtils.toEqualsAnd(query, idx_m, KePartMaster.class, KePartMaster.KE_PART_NUMBER, kePartNumber);
		QuerySpecUtils.toBooleanAnd(query, idx, KePart.class, KePart.LATEST, SearchCondition.IS_TRUE);

		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			KePart kePart = (KePart) obj[0];
			KePartMaster master = (KePartMaster) obj[1];
			map.put("lotNo", master.getLotNo());
			map.put("code", master.getCode());
			map.put("kePartName", master.getKePartName());
			map.put("kePartNumber", kePartNumber);
			map.put("model", master.getModel());
			map.put("ok", true);
			map.put("oid", kePart.getPersistInfo().getObjectIdentifier().getStringValue());
		} else {
			map.put("ok", false);
			map.put("kePartNumber", kePartNumber);
		}
		return map;
	}

	public boolean isLast(KePartMaster master) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(KePart.class, true);
		int idx_m = query.appendClassList(KePartMaster.class, false);
		QuerySpecUtils.toInnerJoin(query, KePart.class, KePartMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);
		QuerySpecUtils.toEqualsAnd(query, idx, KePart.class, "masterReference.key.id",
				master.getPersistInfo().getObjectIdentifier().getId());
		QueryResult result = PersistenceHelper.manager.find(query);
		return result.size() == 1 ? true : false;
	}

	public KePart getPreKePart(KePart kePart) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(KePart.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, KePart.class, KePart.VERSION, kePart.getVersion() - 1);
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			return (KePart) obj[0];
		}
		return null;
	}
}
