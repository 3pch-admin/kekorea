package e3ps.workspace.notice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import e3ps.common.util.CommonUtils;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.workspace.notice.Notice;
import e3ps.workspace.notice.dto.NoticeDTO;
import net.sf.json.JSONArray;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
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
		String description = (String) params.get("description"); // 내용
		String creatorOid = (String) params.get("creatorOid"); // 작성자
		String createdFrom = (String) params.get("createdFrom");
		String createdTo = (String) params.get("createdTo");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Notice.class, true);

		QuerySpecUtils.toLikeAnd(query, idx, Notice.class, Notice.NAME, name);
		QuerySpecUtils.toLikeAnd(query, idx, Notice.class, Notice.DESCRIPTION, description);
		QuerySpecUtils.toTimeGreaterEqualsThan(query, idx, Notice.class, Notice.CREATE_TIMESTAMP, createdFrom);
		QuerySpecUtils.toTimeLessEqualsThan(query, idx, Notice.class, Notice.CREATE_TIMESTAMP, createdTo);
		QuerySpecUtils.toCreator(query, idx, Notice.class, creatorOid);
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

	/**
	 * 메인페이지 공지사항 리스트
	 */
	public JSONArray firstPageData() throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Notice.class, true);
		QuerySpecUtils.toOrderBy(query, idx, Notice.class, Notice.CREATE_TIMESTAMP, true);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Notice notice = (Notice) obj[0];
			Map<String, String> map = new HashMap<>();
			map.put("name", notice.getName());
			map.put("oid", notice.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("createdDate_txt", CommonUtils.getPersistableTime(notice.getCreateTimestamp(), 16));
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}
}
