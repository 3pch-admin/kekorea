package e3ps.approval.service;

import java.util.Map;

import wt.method.RemoteInterface;
import wt.util.WTException;

@RemoteInterface
public interface NoticeService {

	public abstract Map<String, Object> createNoticeAction(Map<String, Object> param) throws WTException;

	public abstract Map<String, Object> deleteNoticeAction(Map<String, Object> param) throws WTException;

	public abstract Map<String, Object> modifyNoticeAction(Map<String, Object> param) throws WTException;
}
