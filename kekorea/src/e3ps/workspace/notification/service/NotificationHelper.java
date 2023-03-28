package e3ps.workspace.notification.service;

import wt.services.ServiceFactory;

public class NotificationHelper {

	public static final NotificationService service = ServiceFactory.getService(NotificationService.class);
	public static final NotificationHelper manager = new NotificationHelper();
}
