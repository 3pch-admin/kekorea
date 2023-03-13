package e3ps.project.output.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.project.output.dto.OutputDTO;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.fc.PagingQueryResult;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;

public class OutputHelper {

	public static final OutputHelper manager = new OutputHelper();
	public static final OutputService service = ServiceFactory.getService(OutputService.class);

	public static final String OUTPUT_ROOT = "/Default/프로젝트";

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<OutputDTO> list = new ArrayList<>();

		// 검색 변수
		boolean latest = (boolean) params.get("latest");
		// 폴더 OID
		String oid = (String) params.get("oid");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTDocument.class, true);
		int idx_master = query.appendClassList(WTDocumentMaster.class, false);

		QuerySpecUtils.toInnerJoin(query, WTDocument.class, WTDocumentMaster.class, "masterReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_master);

		if (!StringUtils.isNull(oid)) {

		}

		// 최신 이터레이션.
		if (latest) {
			QuerySpecUtils.toIteration(query, idx, WTDocument.class);
			QuerySpecUtils.toLatest(query, idx, WTDocument.class);
		}

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WTDocument output = (WTDocument) obj[0];
			OutputDTO dto = new OutputDTO(output);
			list.add(dto);
		}

		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}
}
