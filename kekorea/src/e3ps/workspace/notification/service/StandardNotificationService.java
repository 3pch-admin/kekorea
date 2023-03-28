package e3ps.workspace.notification.service;

import wt.services.StandardManager;
import wt.util.WTException;

public class StandardNotificationService extends StandardManager implements NotificationService {

	public static StandardNotificationService newStandardNotificationService() throws WTException {
		StandardNotificationService instance = new StandardNotificationService();
		instance.initialize();
		return instance;
	}
}
