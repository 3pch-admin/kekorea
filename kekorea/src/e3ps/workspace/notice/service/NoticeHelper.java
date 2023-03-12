package e3ps.workspace.notice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.workspace.notice.Notice;
import e3ps.workspace.notice.dto.NoticeDTO;
import wt.fc.PagingQueryResult;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;

public class NoticeHelper {

	public static final NoticeHelper manager = new NoticeHelper();
	public static final NoticeService service = ServiceFactory.getService(NoticeService.class);

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<NoticeDTO> list = new ArrayList<>();

		// key -value
		String name = (String) params.get("name"); // 공지사항 제목
		String description = (String) params.get("description"); // 공지사항 제목
		
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Notice.class, true);

		if (!StringUtils.isNull(name)) {
			QuerySpecUtils.toLikeAnd(query, idx, Notice.class, Notice.NAME, name);
		}
		
		if (!StringUtils.isNull(description)) {
			QuerySpecUtils.toLikeAnd(query, idx, Notice.class, Notice.DESCRIPTION, description);
		}

		QuerySpecUtils.toOrderBy(query, idx, Notice.class, Notice.CREATE_TIMESTAMP, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Notice notice = (Notice) obj[0];
			NoticeDTO column = new NoticeDTO(notice);
			list.add(column);
		}
		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}
}
