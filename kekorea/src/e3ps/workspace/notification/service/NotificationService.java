package e3ps.workspace.notification.service;

import wt.method.RemoteInterface;
import wt.org.WTUser;

@RemoteInterface
public interface NotificationService {

	public abstract void sendTo(String string, String string2, WTUser wtuser) throws Exception;

}
