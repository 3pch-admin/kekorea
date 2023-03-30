package e3ps.workspace.notification.service;

import e3ps.common.util.CommonUtils;
import e3ps.workspace.notification.Notification;
import wt.fc.PersistenceHelper;
import wt.org.WTUser;
import wt.ownership.Ownership;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardNotificationService extends StandardManager implements NotificationService {

	public static StandardNotificationService newStandardNotificationService() throws WTException {
		StandardNotificationService instance = new StandardNotificationService();
		instance.initialize();
		return instance;
	}

	@Override
	public void sendTo(String name, String description, WTUser to) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			Notification notification = Notification.newNotification();
			notification.setName(name);
			notification.setDescription(description);
			notification.setTo(Ownership.newOwnership(to));
			notification.setOwnership(CommonUtils.sessionOwner());
			PersistenceHelper.manager.save(notification);

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
