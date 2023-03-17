package e3ps.epm.keDrawing.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.epm.keDrawing.KeDrawing;
import e3ps.epm.keDrawing.KeDrawingMaster;
import e3ps.epm.keDrawing.dto.KeDrawingDTO;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;

public class KeDrawingHelper {

	public static final KeDrawingHelper manager = new KeDrawingHelper();
	public static final KeDrawingService service = ServiceFactory.getService(KeDrawingService.class);

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		ArrayList<KeDrawingDTO> list = new ArrayList<>();
		Map<String, Object> map = new HashMap<String, Object>();
		String keNumber = (String) params.get("keNumber");
		String name = (String) params.get("name");
		int lotNo = (int) params.get("lotNo");
		boolean latest = (boolean) params.get("latest");
		String creator = (String) params.get("creator");
		String modifier = (String) params.get("modifier");
		String createdFrom = (String) params.get("createdFrom");
		String createdTo = (String) params.get("createdTo");
		String modifiedFrom = (String) params.get("modifiedFrom");
		String modifiedTo = (String) params.get("modifiedTo");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(KeDrawing.class, true);

		if (latest) {
			QuerySpecUtils.toBooleanAnd(query, idx, KeDrawing.class, KeDrawing.LATEST, true);
		} else {
			query.appendOpenParen();
			QuerySpecUtils.toBooleanAndOr(query, idx, KeDrawing.class, KeDrawing.LATEST, true);
			QuerySpecUtils.toBooleanAndOr(query, idx, KeDrawing.class, KeDrawing.LATEST, false);
			query.appendCloseParen();
		}

		QuerySpecUtils.toOrderBy(query, idx, KeDrawing.class, KeDrawing.CREATE_TIMESTAMP, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			KeDrawing keDrawing = (KeDrawing) obj[0];
			KeDrawingDTO column = new KeDrawingDTO(keDrawing);
			list.add(column);
		}

		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	public boolean isLast(KeDrawingMaster master) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(KeDrawing.class, true);
		int idx_m = query.appendClassList(KeDrawingMaster.class, false);
		QuerySpecUtils.toInnerJoin(query, KeDrawing.class, KeDrawingMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_m);
		QuerySpecUtils.toEqualsAnd(query, idx, KeDrawing.class, "masterReference.key.id",
				master.getPersistInfo().getObjectIdentifier().getId());
		QueryResult result = PersistenceHelper.manager.find(query);
		return result.size() == 1 ? true : false;
	}

	public KeDrawing getPreKeDrawing(KeDrawing keDrawing) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(KeDrawing.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, KeDrawing.class, KeDrawing.VERSION, keDrawing.getVersion() - 1);
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			return (KeDrawing) obj[0];
		}
		return null;
	}

	/**
	 * KE 도면 등록, 수정중 중복 체크
	 * 
	 * @param addRow  : 추가 도면 데이터
	 * @param editRow : 수정 도면 데이터
	 * @return
	 * @throws Exception
	 */

	public boolean isValid(ArrayList<KeDrawingDTO> addRow, ArrayList<KeDrawingDTO> editRow) {
		// TODO Auto-generated method stub
		return false;
	}
}
