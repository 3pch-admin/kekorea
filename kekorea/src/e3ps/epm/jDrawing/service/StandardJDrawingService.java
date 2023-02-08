package e3ps.epm.jDrawing.service;

import java.util.ArrayList;
import java.util.Map;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.util.CommonUtils;
import e3ps.epm.jDrawing.JDrawing;
import e3ps.epm.jDrawing.JDrawingMaster;
import wt.content.ApplicationData;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.fc.PersistenceHelper;
import wt.ownership.Ownership;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.WTException;

public class StandardJDrawingService extends StandardManager implements JDrawingService {

	public static StandardJDrawingService newStandardJDrawingService() throws WTException {
		StandardJDrawingService instance = new StandardJDrawingService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(Map<String, Object> params) throws Exception {
		ArrayList<Map<String, Object>> addRows = (ArrayList<Map<String, Object>>) params.get("addRows");
		ArrayList<String> arr = (ArrayList<String>) params.get("arr");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (int i = 0; i < addRows.size(); i++) {
				Map<String, Object> data = (Map<String, Object>) addRows.get(i);
				String name = (String) data.get("name");
				String number = (String) data.get("number");
				int version = (int) data.get("version");
				String lot = (String) data.get("lot");
				String primaryPath = (String) data.get("primaryPath");

				JDrawingMaster master = JDrawingMaster.newJDrawingMaster();
				master.setNumber(number);
				master.setName(name);
				master.setOwnership(CommonUtils.sessionOwner());
				master = (JDrawingMaster) PersistenceHelper.manager.save(master);

				JDrawing jDrawing = JDrawing.newJDrawing();
				jDrawing.setLot(lot);
				jDrawing.setOwnership(CommonUtils.sessionOwner());
				jDrawing.setVersion(version);
				jDrawing.setMaster(master);
				jDrawing.setLatest(true);
				PersistenceHelper.manager.save(jDrawing);

				ApplicationData dd = ApplicationData.newApplicationData(jDrawing);
				dd.setRole(ContentRoleType.PRIMARY);
				dd = (ApplicationData) ContentServerHelper.service.updateContent(jDrawing, dd, primaryPath);

			}

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
