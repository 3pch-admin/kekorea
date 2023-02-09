package e3ps.approval.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.approval.Notice;
import e3ps.approval.column.NoticeColumnData;
import e3ps.common.util.DateUtils;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.StringUtils;
import e3ps.org.People;
import wt.fc.PagingQueryResult;
import wt.fc.PagingSessionHelper;
import wt.fc.ReferenceFactory;
import wt.query.ClassAttribute;
import wt.query.ColumnExpression;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SQLFunction;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;

public class NoticeHelper {

	public static final NoticeService service = ServiceFactory.getService(NoticeService.class);
	public static final NoticeHelper manager = new NoticeHelper();

	/**
	 * @param param
	 * @return QuerySpec
	 */
	public Map<String, Object> find(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<NoticeColumnData> list = new ArrayList<NoticeColumnData>();
		QuerySpec query = null;

		String description = (String) param.get("description");
		String name = (String) param.get("name");
		String creatorsOid = (String) param.get("creatorsOid");
		String predate = (String) param.get("predate");
		String postdate = (String) param.get("postdate");

		// 정렬
		String sort = (String) param.get("sort");
		String sortKey = (String) param.get("sortKey");
		ReferenceFactory rf = new ReferenceFactory();
		try {

			query = new QuerySpec();
			int idx = query.appendClassList(Notice.class, true);

			SearchCondition sc = null;

			// 대소문자 구분
			if (!StringUtils.isNull(name)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ClassAttribute ca = new ClassAttribute(Notice.class, Notice.NAME);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(name);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(description)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ClassAttribute ca = new ClassAttribute(Notice.class, Notice.DESCRIPTION);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(description);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(creatorsOid)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				People user = (People) rf.getReference(creatorsOid).getObject();
				long ids = user.getUser().getPersistInfo().getObjectIdentifier().getId();
				sc = new SearchCondition(Notice.class, "ownership.owner.key.id", SearchCondition.EQUAL, ids);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(predate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp start = DateUtils.convertStartDate(predate);
				sc = new SearchCondition(Notice.class, WTAttributeNameIfc.CREATE_STAMP_NAME,
						SearchCondition.GREATER_THAN_OR_EQUAL, start);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(postdate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp end = DateUtils.convertEndDate(postdate);
				sc = new SearchCondition(Notice.class, WTAttributeNameIfc.CREATE_STAMP_NAME,
						SearchCondition.LESS_THAN_OR_EQUAL, end);
				query.appendWhere(sc, new int[] { idx });
			}

			if (StringUtils.isNull(sort)) {
				sort = "true";
			}

			if (StringUtils.isNull(sortKey)) {
				sortKey = WTAttributeNameIfc.CREATE_STAMP_NAME;
			}

			ClassAttribute ca = new ClassAttribute(Notice.class, sortKey);
			OrderBy orderBy = new OrderBy(ca, Boolean.parseBoolean(sort));
			query.appendOrderBy(orderBy, new int[] { idx });

			PageQueryUtils pager = new PageQueryUtils(param, query);
			PagingQueryResult result = pager.find();
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				Notice notice = (Notice) obj[0];
				NoticeColumnData data = new NoticeColumnData(notice);
				list.add(data);
			}
			map.put("list", list);
			map.put("lastPage", pager.getLastPage());
			map.put("topListCount", pager.getTotal());
			map.put("sessionid", pager.getSessionId());
			map.put("curPage", pager.getCpage());
			map.put("total", pager.getTotalSize());
			map.put("result", "SUCCESS");
		} catch (Exception e) {
			map.put("result", "FAIL");
			e.printStackTrace();
		}
		return map;
	}

	public PagingQueryResult getMainNoticeList() {
		QuerySpec query = null;
		PagingQueryResult result = null;
		try {

			query = new QuerySpec();
			int idx = query.appendClassList(Notice.class, true);

			ClassAttribute ca = new ClassAttribute(Notice.class, WTAttributeNameIfc.CREATE_STAMP_NAME);
			OrderBy orderBy = new OrderBy(ca, true);
			query.appendOrderBy(orderBy, new int[] { idx });

			result = PagingSessionHelper.openPagingSession(0, 6, query);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
