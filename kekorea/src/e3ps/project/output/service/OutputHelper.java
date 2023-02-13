package e3ps.project.output.service;

import wt.services.ServiceFactory;

public class OutputHelper {

	public static final OutputHelper manager = new OutputHelper();
	public static final OutputService service = ServiceFactory.getService(OutputService.class);
}
