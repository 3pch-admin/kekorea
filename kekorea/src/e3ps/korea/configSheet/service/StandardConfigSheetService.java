package e3ps.korea.configSheet.service;

import e3ps.korea.configSheet.ConfigSheetDTO;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardConfigSheetService extends StandardManager implements ConfigSheetService {

	public static StandardConfigSheetService newStandardConfigSheetService() throws WTException {
		StandardConfigSheetService instance = new StandardConfigSheetService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(ConfigSheetDTO dto) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

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
