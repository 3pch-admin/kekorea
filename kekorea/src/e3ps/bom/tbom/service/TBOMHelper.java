package e3ps.bom.tbom.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import e3ps.bom.partlist.PartListMasterProjectLink;
import e3ps.bom.partlist.dto.PartListDTO;
import e3ps.bom.tbom.TBOMData;
import e3ps.bom.tbom.TBOMMaster;
import e3ps.bom.tbom.TBOMMasterDataLink;
import e3ps.bom.tbom.TBOMMasterProjectLink;
import e3ps.bom.tbom.dto.TBOMDTO;
import e3ps.bom.tbom.dto.TBOMMasterDTO;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.DateUtils;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.epm.keDrawing.KeDrawing;
import e3ps.epm.keDrawing.KeDrawingMaster;
import e3ps.part.kePart.KePart;
import e3ps.part.kePart.KePartMaster;
import e3ps.project.Project;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
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

	/**
	 * T-BOM 조회
	 */
	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(TBOMMaster.class, true);
		QuerySpecUtils.toOrderBy(query, idx, TBOMMaster.class, TBOMMaster.CREATE_TIMESTAMP, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();

		JSONArray list = new JSONArray();

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			TBOMMaster master = (TBOMMaster) obj[0];

			JSONObject node = new JSONObject();
			node.put("oid", master.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("name", master.getName());
			QueryResult group = PersistenceHelper.manager.navigate(master, "project", TBOMMasterProjectLink.class,
					false);
			int isNode = 1;
			JSONArray children = new JSONArray();
			while (group.hasMoreElements()) {
				TBOMMasterProjectLink link = (TBOMMasterProjectLink) group.nextElement();
				TBOMDTO dto = new TBOMDTO(link);
				if (isNode == 1) {
					node.put("poid", dto.getPoid());
					node.put("projectType_name", dto.getProjectType_name());
					node.put("customer_name", dto.getCustomer_name());
					node.put("install_name", dto.getInstall_name());
					node.put("mak_name", dto.getMak_name());
					node.put("detail_name", dto.getDetail_name());
					node.put("kekNumber", dto.getKekNumber());
					node.put("keNumber", dto.getKeNumber());
					node.put("userId", dto.getUserId());
					node.put("description", dto.getDescription());
					node.put("state", dto.getState());
					node.put("model", dto.getModel());
					node.put("pdate_txt", dto.getPdate_txt());
					node.put("creator", dto.getCreator());
					node.put("createdDate_txt", dto.getCreatedDate_txt());
					node.put("modifiedDate_txt", dto.getModifiedDate_txt());
				} else {
					JSONObject data = new JSONObject();
					data.put("name", dto.getName());
					data.put("oid", dto.getOid());
					data.put("poid", dto.getPoid());
					data.put("projectType_name", dto.getProjectType_name());
					data.put("customer_name", dto.getCustomer_name());
					data.put("install_name", dto.getInstall_name());
					data.put("mak_name", dto.getMak_name());
					data.put("detail_name", dto.getDetail_name());
					data.put("kekNumber", dto.getKekNumber());
					data.put("keNumber", dto.getKeNumber());
					data.put("userId", dto.getUserId());
					data.put("description", dto.getDescription());
					data.put("state", dto.getState());
					data.put("model", dto.getModel());
					data.put("pdate_txt", dto.getPdate_txt());
					data.put("creator", dto.getCreator());
					data.put("createdDate_txt", dto.getCreatedDate_txt());
					data.put("modifiedDate_txt", dto.getModifiedDate_txt());
					children.add(data);
				}
				isNode++;
			}
			node.put("children", children);
			list.add(node);
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
		ArrayList<TBOMDTO> list = new ArrayList<>();
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
			TBOMDTO column = new TBOMDTO(data);
			list.add(column);
		}
		return JSONArray.fromObject(list);
	}

	public ArrayList<TBOMMasterDataLink> getLinks(TBOMMaster master) throws Exception {
		ArrayList<TBOMMasterDataLink> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(TBOMMasterDataLink.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, TBOMMasterDataLink.class, "roleAObjectRef.key.id",
				master.getPersistInfo().getObjectIdentifier().getId());
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			TBOMMasterDataLink link = (TBOMMasterDataLink) obj[0];
			list.add(link);
		}
		return list;
	}

	public ArrayList<TBOMData> getData(TBOMMaster master) throws Exception {
		ArrayList<TBOMData> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(TBOMMasterDataLink.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, TBOMMasterDataLink.class, "roleAObjectRef.key.id",
				master.getPersistInfo().getObjectIdentifier().getId());
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			TBOMMasterDataLink link = (TBOMMasterDataLink) obj[0];
			list.add(link.getData());
		}
		return list;
	}

	public HashMap<String, ArrayList<TBOMMasterDataLink>> getCompareData(HttpServletRequest request, int count)
			throws Exception {
		HashMap<String, ArrayList<TBOMMasterDataLink>> map = new HashMap<>();
		for (int i = 0; i < count; i++) {
			String oid = request.getParameter("oid" + i);
			TBOMMaster master = (TBOMMaster) CommonUtils.getObject(oid);
			ArrayList<TBOMMasterDataLink> link = getLinks(master);
			map.put("compareData" + i, link);
		}
		return map;
	}

	public ArrayList<Map<String, Object>> headers(HttpServletRequest request, int count) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			String poid = request.getParameter("poid" + i);
			String oid = request.getParameter("oid" + i);
			Project project = (Project) CommonUtils.getObject(poid);
			TBOMMaster master = (TBOMMaster) CommonUtils.getObject(oid);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("kekNumber" + i, project.getKekNumber());
			map.put("name" + i, master.getName());
			list.add(map);
		}
		return list;
	}

	public ArrayList<Map<String, Object>> compare(HttpServletRequest request, int count) throws Exception {

		String oid0 = request.getParameter("oid0");
		String oid1 = request.getParameter("oid1");

		TBOMMaster m = (TBOMMaster) CommonUtils.getObject(oid0);
		TBOMMaster m1 = (TBOMMaster) CommonUtils.getObject(oid1);

		ArrayList<TBOMData> link = TBOMHelper.manager.getData(m);
		ArrayList<TBOMData> link1 = TBOMHelper.manager.getData(m1);
		ArrayList<Map<String, Object>> compareData = new ArrayList<>();
		for (TBOMData data : link) {
			boolean isEquals = false;
			for (TBOMData data1 : link1) {
				String num = data.getKePart().getMaster().getKePartNumber();
				String num1 = data1.getKePart().getMaster().getKePartNumber();

				if (num.equals(num1)) {
					Map<String, Object> map = new HashMap<>();
					map.put("lotNo", data.getKePart().getMaster().getLotNo());
					map.put("kePartNumber0", data.getKePart().getMaster().getKePartNumber());
					map.put("kePartNumber1", data1.getKePart().getMaster().getKePartNumber());
					map.put("kePartName0", data.getKePart().getMaster().getKePartName());
					map.put("kePartName1", data1.getKePart().getMaster().getKePartName());
					map.put("code", data.getKePart().getMaster().getCode());
					map.put("qty0", data.getQty());
					map.put("qty1", data1.getQty());
					compareData.add(map);
					link1.remove(data1);
					isEquals = true;
					break;
				}
			}
			if (!isEquals) {
				Map<String, Object> map = new HashMap<>();
				map.put("lotNo", data.getKePart().getMaster().getLotNo());
				map.put("kePartNumber0", data.getKePart().getMaster().getKePartNumber());
				map.put("kePartNumber1", "");
				map.put("kePartName0", data.getKePart().getMaster().getKePartName());
				map.put("kePartName1", "");
				map.put("code", data.getKePart().getMaster().getCode());
				map.put("qty0", data.getQty());
				map.put("qty1", "");
				compareData.add(map);
			}
		}
		return compareData;
	}

	/**
	 * T-BOM 등록시 가져올 KE 부품들
	 */
	public Map<String, Object> getData(String number) throws Exception {
		Map<String, Object> map = new HashMap<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(KePart.class, true);
		int idx_m = query.appendClassList(KePartMaster.class, true);
		QuerySpecUtils.toInnerJoin(query, KePart.class, KePartMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);
		QuerySpecUtils.toBooleanAnd(query, idx, KePart.class, KePart.LATEST, true);
		QuerySpecUtils.toEqualsAnd(query, idx_m, KePartMaster.class, KePartMaster.KE_NUMBER, number);
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			KePart kePart = (KePart) obj[0];
			KePartMaster master = (KePartMaster) obj[1];
			map.put("name", master.getName());
			map.put("keNumber", master.getKeNumber());
			map.put("code", master.getCode());
			map.put("model", master.getModel());
			map.put("lotNo", master.getLotNo());
			map.put("oid", kePart.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("ok", true);
		}
		return map;
	}
}
