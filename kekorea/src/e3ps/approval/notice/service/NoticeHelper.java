package e3ps.approval.notice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import e3ps.approval.notice.Notice;
import e3ps.approval.notice.beans.NoticeColumnData;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.korea.cip.Cip;
import e3ps.korea.cip.beans.CipColumnData;
import wt.fc.PagingQueryResult;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;

public class NoticeHelper {

	public static final NoticeHelper manager = new NoticeHelper();
	public static final NoticeService service = ServiceFactory.getService(NoticeService.class);
	
	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<NoticeColumnData> list = new ArrayList<>();
		
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Notice.class, true);
		
		QuerySpecUtils.toOrderBy(query, idx, Notice.class, Notice.CREATE_TIMESTAMP, false);
		
		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Notice notice = (Notice) obj[0];
			NoticeColumnData column = new NoticeColumnData(notice);
			list.add(column);
		}
		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}
}
