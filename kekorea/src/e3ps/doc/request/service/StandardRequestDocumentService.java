package e3ps.doc.request.service;

import wt.services.StandardManager;
import wt.util.WTException;

public class StandardRequestDocumentService extends StandardManager implements RequestDocumentService {

	public static StandardRequestDocumentService newStandardRequestDocumentService() throws WTException {
		StandardRequestDocumentService instance = new StandardRequestDocumentService();
		instance.initialize();
		return instance;
	}
}
