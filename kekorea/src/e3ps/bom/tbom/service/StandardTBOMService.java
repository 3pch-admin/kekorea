package e3ps.bom.tbom.service;

import java.util.ArrayList;
import java.util.Map;

import e3ps.bom.tbom.TBOMData;
import e3ps.bom.tbom.TBOMMaster;
import e3ps.bom.tbom.TBOMMasterDataLink;
import e3ps.bom.tbom.TBOMMasterProjectLink;
import e3ps.bom.tbom.dto.TBOMDTO;
import e3ps.common.util.CommonUtils;
import e3ps.part.kePart.KePart;
import e3ps.project.Project;
import wt.clients.folder.FolderTaskLogic;
import wt.fc.PersistenceHelper;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardTBOMService extends StandardManager implements TBOMService {

	public static StandardTBOMService newStandardTBOMService() throws WTException {
		StandardTBOMService instance = new StandardTBOMService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(TBOMDTO dto) throws Exception {
		String name = dto.getName();
		String description = dto.getDescription();
		ArrayList<Map<String, Object>> addRows = dto.getAddRows(); // T-BOM
		ArrayList<Map<String, String>> _addRows = dto.get_addRows(); // 작번
		ArrayList<String> secondarys = dto.getSecondarys();
		Transaction trs = new Transaction();
		try {
			trs.start();

			String number = TBOMHelper.manager.getNextNumber("T-BOM");
			Folder folder = FolderTaskLogic.getFolder("/Default/프로젝트/T-BOM", CommonUtils.getContainer());

			TBOMMaster master = TBOMMaster.newTBOMMaster();
			master.setTNumber(number);
			master.setName(name);
			master.setDescription(description);
			master.setOwnership(CommonUtils.sessionOwner());
			FolderHelper.assignLocation((FolderEntry) master, folder);
			PersistenceHelper.manager.save(master);

			for (Map<String, String> _addRow : _addRows) { // project
				String oid = (String) _addRow.get("oid");
				Project project = (Project) CommonUtils.getObject(oid);
				TBOMMasterProjectLink link = TBOMMasterProjectLink.newTBOMMasterProjectLink(master, project);
				PersistenceHelper.manager.save(link);
			}

			int sort = 0;
			for (Map<String, Object> addRow : addRows) { // tbom
				String koid = (String) addRow.get("oid"); // kepart..
				String unit = (String) addRow.get("unit");
				int qty = (int) addRow.get("qty");
				int lotNo = (int) addRow.get("lotNo");
				String provide = (String) addRow.get("provide");
				String discontinue = (String) addRow.get("discontinue");

				KePart kePart = (KePart) CommonUtils.getObject(koid);
				TBOMData data = TBOMData.newTBOMData();
				data.setKePart(kePart);
				data.setQty(qty);
				data.setLotNo(lotNo);
				data.setProvide(provide);
				data.setDiscontinue(discontinue);
				data.setUnit(unit);
				PersistenceHelper.manager.save(data);

				TBOMMasterDataLink link = TBOMMasterDataLink.newTBOMMasterDataLink(master, data);
				link.setSort(sort);
				PersistenceHelper.manager.save(link);

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

	@Override
	public void save(Map<String, Object> params) throws Exception {
		ArrayList<Map<String, Object>> removeRows = (ArrayList<Map<String, Object>>) params.get("removeRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (Map<String, Object> removeRow : removeRows) {
				String oid = (String) removeRow.get("oid");
				TBOMMaster master = (TBOMMaster) CommonUtils.getObject(oid);

				ArrayList<TBOMMasterDataLink> list = TBOMHelper.manager.getLinks(master);
				for (TBOMMasterDataLink link : list) {
					PersistenceHelper.manager.delete(link);
				}

				PersistenceHelper.manager.delete(master);
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
