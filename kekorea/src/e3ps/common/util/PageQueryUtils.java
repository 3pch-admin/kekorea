package e3ps.common.util;

import java.util.HashMap;
import java.util.Map;

import wt.fc.PagingQueryResult;
import wt.fc.PagingSessionHelper;
import wt.query.QuerySpec;

public class PageQueryUtils {

	int psize = 30;
	int cpage = 1;
	int total = 0;
	int pageCount = 10;
	int topListCount = 1;
	long sessionid = 0L;

	Map<String, Object> param = new HashMap<String, Object>();
	QuerySpec query = null;
	PagingQueryResult result = null;

	public PageQueryUtils(Map<String, Object> param, QuerySpec query) {
		this.param = param;
		String sessionid = (String) this.param.get("sessionid");
		if (StringUtils.isNull(sessionid)) {
			sessionid = "0";
		}
		this.query = query;
		this.sessionid = Long.parseLong(sessionid);
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
			psize = "100";
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

	public long getSessionId() {
		return this.sessionid;
	}
}
