package e3ps.approval.notice.service;

import java.util.Map;

import wt.services.StandardManager;
import wt.util.WTException;

public class StandardNoticeService extends StandardManager implements NoticeService {

	public static StandardNoticeService newStandardNoticeService() throws WTException {
		StandardNoticeService instance = new StandardNoticeService();
		instance.initialize();
		return instance;
	}

	@Override
	public void save(Map<String, Object> params) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
