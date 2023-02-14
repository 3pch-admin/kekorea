package e3ps.common.util;

import java.util.HashMap;
import java.util.Map;

import wt.fc.PagingQueryResult;
import wt.fc.PagingSessionHelper;
import wt.query.QuerySpec;
import wt.util.WTAttributeNameIfc;

public class PageQueryUtils {

	int psize = 100;
	int cpage = 1;
	int total = 0;
	int pageCount = 10;
	int topListCount = 1;
	long sessionid = 0L;

	Map<String, Object> param = new HashMap<String, Object>();
	QuerySpec query = null;
	PagingQueryResult result = null;

	String sort = "true";
	String sortKey = WTAttributeNameIfc.CREATE_STAMP_NAME;

	public PageQueryUtils(Map<String, Object> param, QuerySpec query) {

		this.param = param;

		String sessionid = (String) this.param.get("sessionid");
		if (StringUtils.isNull(sessionid)) {
			sessionid = "0";
		}
		this.query = query;
		this.sessionid = Long.parseLong(sessionid);

		String sort = (String) this.param.get("sort");
		if (StringUtils.isNull(sort)) {
			sort = "true";
		}

		String sortKey = (String) this.param.get("sortKey");
		if (StringUtils.isNull(sortKey)) {
			// sortKey = WTAttributeNameIfc.CREATE_STAMP_NAME;
			sortKey = this.sortKey;
		}
		this.sort = sort;
		this.sortKey = sortKey;
	}

	public static PagingQueryResult openPagingSession(int i, int j, QuerySpec query) throws Exception {
		return PagingSessionHelper.openPagingSession(i, j, query);
	}

	public static PagingQueryResult fetchPagingSession(int i, int j, long sessionid) throws Exception {
		return PagingSessionHelper.fetchPagingSession(i, j, sessionid);
	}

	public PagingQueryResult find() throws Exception {
		String cpage = (String) this.param.get("tpage");
		if (StringUtils.isNull(cpage)) {
			cpage = "1";
		}

		String psize = (String) this.param.get("psize");

		if (StringUtils.isNull(psize)) {
			psize = "30";
		}

		this.cpage = Integer.parseInt(cpage);
		this.psize = Integer.parseInt(psize);

		if (this.sessionid <= 0) {
			this.result = openPagingSession(0, this.psize, this.query);
		} else {
			this.result = fetchPagingSession((this.cpage - 1) * this.psize, this.psize, this.sessionid);
		}

		if (this.result != null) {
			this.total = this.result.getTotalSize();
			this.sessionid = this.result.getSessionId();
			this.topListCount = this.total - ((this.cpage - 1) * this.psize);
		}

		return this.result;
	}

	public void setPsize(int psize) {
		this.psize = psize;
	}

	public Map<String, Object> getParam() {
		return this.param;
	}

	public int getTotalSize() {
		return this.total;
	}

	public int getTotal() {
		return this.topListCount;
	}

	public int getCpage() {
		return this.cpage;
	}

	public int getPsize() {
		return this.psize;
	}

	public String getSort() {
		return this.sort;
	}

	public String getSortKey() {
		return this.sortKey;
	}

	public long getSessionId() {
		return this.sessionid;
	}

	public int getLastPage() {
		int ksize = this.total / this.psize;
		int x = this.total % this.psize;

		if (x > 0) {
			ksize++;
		}
		return ksize;
	}

	public String getScript() throws Exception {
		StringBuffer script = new StringBuffer();
		script.append("<input type=\"hidden\" name=\"sessionid\" value=\"" + this.sessionid + "\">\n");
		script.append("<input type=\"hidden\" name=\"tpage\" value=\"" + this.cpage + "\">\n");
		script.append("<input type=\"hidden\" name=\"psize\" value=\"" + this.psize + "\">\n");
		script.append("<input type=\"hidden\" name=\"sort\" value=\"" + this.sort + "\">\n");
		script.append("<input type=\"hidden\" name=\"sortKey\" value=\"" + this.sortKey + "\">\n");
		return script.toString();
	}

	public String paging() throws Exception {

		int ksize = this.total / this.psize;
		int x = this.total % this.psize;

		if (x > 0) {
			ksize++;
		}

		int temp = this.cpage / this.pageCount;
		if ((this.cpage % this.pageCount) > 0) {
			temp++;
		}

		int start = (temp - 1) * this.pageCount + 1;
		int end = start + this.pageCount - 1;
		if (end > ksize) {
			end = ksize;
		}

		StringBuffer paging = new StringBuffer();

//		paging.append("<div id=\"page_layer\">\n");
		paging.append("<table class=\"page_table\">\n");
		paging.append("<colgroup>\n");
		paging.append("<col width=\"130\">\n");
		paging.append("<col width=\"*\">\n");
		paging.append("<col width=\"130\">\n");
		paging.append("</colgroup>\n");
		paging.append("<tr>\n");

		paging.append("<td>");
		paging.append("[전체페이지:" + ksize + "][전체개수:" + this.total + "]");
		paging.append("</td>");

		paging.append("<td>");
		if (end > 1) {
			if (this.cpage > 1) {
				paging.append("<a data-page=\"1\" title=\"처음 페이지로\" class=\"paging_left\">&lt;&lt;</a>\n");
//				paging.append("<a data-page=\"1\" title=\"처음 페이지로\" class=\"axi axi-chevron-left\"></a>\n");
				paging.append(
						"<a data-page=\"" + (this.cpage - 1) + "\" title=\"이전 페이지로\" class=\"paging_left\">&lt;</a>\n");
			}

			for (int i = start; i <= end; i++) {
				if (i == this.cpage) {
					paging.append(
							"<a class=\"active_page\" data-page=\"" + i + "\" title=\"" + i + "페이지로\">" + i + "</a>\n");
				} else {
					paging.append("<a data-page=\"" + i + "\" title=\"" + i + "페이지로\">" + i + "</a>\n");
				}
			}

			if (this.cpage < ksize) {
				paging.append("<a data-page=\"" + (this.cpage + 1)
						+ "\" title=\"다음 페이지로\" class=\"paging_right\">&gt;</a>\n");
				paging.append(
						"<a data-page=\"" + ksize + "\" title=\"마지막 페이지로\" class=\"paging_right\">&gt;&gt;</a>\n");
			}
		} else {
			paging.append("&nbsp;");
		}
		paging.append("</td>\n");
		paging.append("<td>&nbsp;");
		paging.append("</td>");
		paging.append("</tr>\n");
		paging.append("</table>\n");
//		paging.append("</div>");

		return paging.toString();
	}
}
