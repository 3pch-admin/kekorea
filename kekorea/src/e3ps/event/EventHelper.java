package e3ps.event;

import wt.services.ServiceFactory;

public class EventHelper {

	/**
	 * access service
	 */
	public static final EventService service = ServiceFactory.getService(EventService.class);

	/**
	 * access helper
	 */
	public static final EventHelper manager = new EventHelper();

}
