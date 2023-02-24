package e3ps.event;

import wt.services.ServiceFactory;

public class EventHelper {

	public static final EventService service = ServiceFactory.getService(EventService.class);
	public static final EventHelper manager = new EventHelper();

}
