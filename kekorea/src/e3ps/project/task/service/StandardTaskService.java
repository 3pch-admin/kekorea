package e3ps.project.task.service;

import wt.services.StandardManager;
import wt.util.WTException;

public class StandardTaskService extends StandardManager implements TaskService {

	public static StandardTaskService newStandardTaskService() throws WTException {
		StandardTaskService instance = new StandardTaskService();
		instance.initialize();
		return instance;
	}
}
