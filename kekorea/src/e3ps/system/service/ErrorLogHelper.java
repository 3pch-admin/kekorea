package e3ps.system.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.erp.ErpSendHistory;
import e3ps.erp.dto.ErpDTO;
import e3ps.system.ErrorLog;
import e3ps.system.dto.ErrorLogDTO;
import wt.fc.PagingQueryResult;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;

public class ErrorLogHelper {

	public static final ErrorLogHelper manager = new ErrorLogHelper();
	public static final ErrorLogService service = ServiceFactory.getService(ErrorLogService.class);

	/**
	 * 에러로그 리스트 함수
	 */
	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		System.out.println("에러로그 START = " + new Timestamp(new Date().getTime()));
		Map<String, Object> map = new HashMap<String, Object>();
		String name = (String) params.get("name");
		String resultMsg = (String) params.get("resultMsg");
		String sendQuery = (String) params.get("sendQuery");

		List<ErrorLogDTO> list = new ArrayList<>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ErrorLog.class, true);

//		QuerySpecUtils.toLikeAnd(query, idx, ErpSendHistory.class, ErpSendHistory.NAME, name);
//		QuerySpecUtils.toLikeAnd(query, idx, ErpSendHistory.class, ErpSendHistory.RESULT_MSG, resultMsg);
//		QuerySpecUtils.toLikeAnd(query, idx, ErpSendHistory.class, ErpSendHistory.SEND_QUERY, sendQuery);

		QuerySpecUtils.toOrderBy(query, idx, ErrorLog.class, ErrorLog.CREATE_TIMESTAMP, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ErrorLog errorLog = (ErrorLog) obj[0];
			ErrorLogDTO column = new ErrorLogDTO(errorLog);
			list.add(column);
		}

		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		System.out.println("에러로그 END = " + new Timestamp(new Date().getTime()));
		return map;
	}

}
