package e3ps.part.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import e3ps.bom.partlist.PartListData;
import e3ps.common.content.service.CommonContentHelper;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.IBAUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.doc.WTDocumentWTPartLink;
import e3ps.erp.service.ErpHelper;
import wt.clients.folder.FolderTaskLogic;
import wt.content.ApplicationData;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.services.StandardManager;
import wt.util.WTException;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;

public class StandardPartService extends StandardManager implements PartService {

	public static StandardPartService newStandardPartService() throws WTException {
		StandardPartService instance = new StandardPartService();
		instance.initialize();
		return instance;
	}

	@Override
	public Map<String, Object> bundle(Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<>();
		ArrayList<Map<String, Object>> addRows = (ArrayList<Map<String, Object>>) params.get("addRows");
		ArrayList<String> secondarys = (ArrayList<String>) params.get("secondarys");
		boolean erp = (boolean) params.get("erp");
		ArrayList<String> list = new ArrayList<>();
		Transaction trs = new Transaction();
		try {
			trs.start();

			WTPart part = null;
			for (int i = 0; i < addRows.size(); i++) {
				Map<String, Object> addRow = addRows.get(i);
				String cacheId = secondarys.get(i);
				String number = (String) addRow.get("number");
				String name = (String) addRow.get("spec");
				String spec = (String) addRow.get("spec");
				String maker = (String) addRow.get("maker");
				String customer = (String) addRow.get("customer");
				String unit = (String) addRow.get("unit");
				int price = (int) addRow.get("price");
				String currency = (String) addRow.get("currency");

				part = WTPart.newWTPart();
				part.setNumber(spec);
				part.setName(name);

				View view = ViewHelper.service.getView("Engineering");
				ViewHelper.assignToView(part, view);

				Folder folder = null;
				if (erp) {
					folder = FolderTaskLogic.getFolder(PartHelper.COMMON_DEFAULT_ROOT,
							CommonUtils.getPDMLinkProductContainer());
				} else {
					folder = FolderTaskLogic.getFolder(PartHelper.NEW_DEFAULT_ROOT,
							CommonUtils.getPDMLinkProductContainer());
				}
				FolderHelper.assignLocation((FolderEntry) part, folder);

				part = (WTPart) PersistenceHelper.manager.save(part);

				IBAUtils.createIBA(part, "s", "NAME_OF_PARTS", name);
				IBAUtils.createIBA(part, "s", "MAKER", maker);
				IBAUtils.createIBA(part, "s", "DWG_NO", spec);
				IBAUtils.createIBA(part, "s", "PART_CODE", number);
				IBAUtils.createIBA(part, "s", "STD_UNIT", unit);
				IBAUtils.createIBA(part, "i", "PRICE", price);
				IBAUtils.createIBA(part, "s", "CURRNAME", currency);
				IBAUtils.createIBA(part, "s", "CUSTNAME", customer);

				ApplicationData data = ApplicationData.newApplicationData(part);
				File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
				data.setRole(ContentRoleType.PRIMARY);
				data = (ApplicationData) ContentServerHelper.service.updateContent(part, data, vault.getPath());

				if (erp) {
					part = (WTPart) PersistenceHelper.manager.refresh(part);
					String code = ErpHelper.manager.sendToErp(part);
					list.add(code);
				}

				// part <-> partlist data.. connect
				QuerySpec query = new QuerySpec();
				int idx = query.appendClassList(PartListData.class, true);
				QuerySpecUtils.toEqualsAnd(query, idx, PartListData.class, PartListData.PART_NO, number);
				QueryResult qr = PersistenceHelper.manager.find(query);
				while (qr.hasMoreElements()) {
					Object[] obj = (Object[]) qr.nextElement();
					PartListData dd = (PartListData) obj[0];
					dd.setWtPart(part);
					PersistenceHelper.manager.modify(dd);
				}
			}

			result.put("list", list);
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
		return result;
	}

	@Override
	public Map<String, Object> spec(Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<>();
		ArrayList<Map<String, Object>> addRows = (ArrayList<Map<String, Object>>) params.get("addRows");
		ArrayList<Map<String, Object>> addRows2 = (ArrayList<Map<String, Object>>) params.get("addRows2");
		ArrayList<String> list = new ArrayList<>();
		Transaction trs = new Transaction();
		try {
			trs.start();

			WTPart part = null;
			for (int i = 0; i < addRows.size(); i++) {
				Map<String, Object> addRow = addRows.get(i);
				String number = (String) addRow.get("number");
				String name = (String) addRow.get("spec");
				String spec = (String) addRow.get("spec");
				String maker = (String) addRow.get("maker");
				String customer = (String) addRow.get("customer");
				String unit = (String) addRow.get("unit");
				int price = (int) addRow.get("price");
				String currency = (String) addRow.get("currency");

				part = WTPart.newWTPart();
				part.setNumber(spec);
				part.setName(name);

				View view = ViewHelper.service.getView("Engineering");
				ViewHelper.assignToView(part, view);

				Folder folder = FolderTaskLogic.getFolder(PartHelper.SPEC_DEFAULT_ROOT,
						CommonUtils.getPDMLinkProductContainer());
				FolderHelper.assignLocation((FolderEntry) part, folder);

				part = (WTPart) PersistenceHelper.manager.save(part);

				IBAUtils.createIBA(part, "s", "NAME_OF_PARTS", name);
				IBAUtils.createIBA(part, "s", "MAKER", maker);
				IBAUtils.createIBA(part, "s", "DWG_NO", spec);
				IBAUtils.createIBA(part, "s", "PART_CODE", number);
				IBAUtils.createIBA(part, "s", "STD_UNIT", unit);
				IBAUtils.createIBA(part, "i", "PRICE", price);
				IBAUtils.createIBA(part, "s", "CURRNAME", currency);
				IBAUtils.createIBA(part, "s", "CUSTNAME", customer);

				Map<String, Object> addRow2 = addRows2.get(i);
				String oid = (String) addRow2.get("oid");
				WTDocument document = (WTDocument) CommonUtils.getObject(oid);
				WTDocumentWTPartLink link = WTDocumentWTPartLink.newWTDocumentWTPartLink(document, part);
				PersistenceHelper.manager.save(link);

				part = (WTPart) PersistenceHelper.manager.refresh(part);
				String code = ErpHelper.manager.sendToErpItem(part, document);
//				String code = "TEST";
				list.add(code);
			}

			result.put("list", list);
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
		return result;
	}
}