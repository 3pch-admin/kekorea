package e3ps.project.issue.service;

import java.util.HashMap;
import java.util.List;

import e3ps.workspace.notice.dto.NoticeDTO;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardIssueService extends StandardManager implements IssueService {

	public static StandardIssueService newStandardIssueService() throws WTException {
		StandardIssueService instance = new StandardIssueService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(NoticeDTO dto) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public void delete(HashMap<String, List<NoticeDTO>> dataMap) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}
}
