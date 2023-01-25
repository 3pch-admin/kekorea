package e3ps.korea.service;

import wt.services.StandardManager;
import wt.util.WTException;

public class StandardKoreaService extends StandardManager implements KoreaService {

	public static StandardKoreaService newStandardKoreaService() throws WTException {
		StandardKoreaService instance = new StandardKoreaService();
		instance.initialize();
		return instance;
	}
}
