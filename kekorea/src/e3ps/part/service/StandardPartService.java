package e3ps.part.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.common.util.CommonUtils;
import e3ps.common.util.IBAUtils;
import e3ps.common.util.StringUtils;
import e3ps.doc.WTDocumentWTPartLink;
import e3ps.erp.service.ErpHelper;
import wt.clients.folder.FolderTaskLogic;
import wt.content.ApplicationData;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.part.WTPart;
import wt.pom.Transaction;
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
	public Map<String, Object> createProductSpecAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		List<ArrayList<String>> jexcels = (List<ArrayList<String>>) param.get("jexcels");

		List<String> docOids = (List<String>) param.get("docOids");
		ReferenceFactory rf = new ReferenceFactory();

		ArrayList<String> codes = new ArrayList<String>();

		// boolean isApp = appList.size() > 0;
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (int i = 0; i < jexcels.size(); i++) {
				WTPart part = null;

				// (품목자산분류), 품번, 품명, 규격, 기준단위, (내외자구분), 메이커, 기본구매처, 통화, 단가, (인수검사여부),
				// (기본창고, 품목상태)

				ArrayList<String> cells = (ArrayList<String>) jexcels.get(i);

				// String types = cells.get(0); // 구분
				// String division = cells.get(1); // 품목자산분류
				String number = cells.get(2); // 품번
				String name = cells.get(3); // 품명
				String spec = cells.get(4); // 규격
				String maker = cells.get(5); // 메이커
				String customer = cells.get(6);
				String unit = cells.get(7); // 기준단위
				String price = cells.get(8).replaceAll(",", "");
				String currency = cells.get(9);

				if (StringUtils.isNull(spec)) {
					continue;
				}
				part = WTPart.newWTPart();
				// part.setName(name);
				part.setNumber(spec);
				part.setName(name);

				View view = ViewHelper.service.getView("Engineering");
				ViewHelper.assignToView(part, view);
				Folder folder = FolderTaskLogic.getFolder(PartHelper.SPEC_PART, CommonUtils.getContainer());
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

				String oid = (String) docOids.get(i);
				WTDocument document = (WTDocument) rf.getReference(oid).getObject();
				WTDocumentWTPartLink link = WTDocumentWTPartLink.newWTDocumentWTPartLink(document, part);
				PersistenceHelper.manager.save(link);

				String code = ErpHelper.service.sendSpecPartToERP(part);
				codes.add(code);
			}

			map.put("list", codes);
			map.put("reload", true);
			map.put("result", SUCCESS);
			map.put("msg", "제작사양서 " + CREATE_OK);

			// map.put("url", "/Windchill/plm/part/createSpec");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("reload", false);
			map.put("result", FAIL);
			map.put("msg", "제작사양서 " + CREATE_FAIL);
			// map.put("url", "/Windchill/plm/part/createSpec");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
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
				String path = secondarys.get(i);
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
					folder = FolderTaskLogic.getFolder(PartHelper.COMMON_PART, CommonUtils.getContainer());
				} else {
					folder = FolderTaskLogic.getFolder(PartHelper.NEW_PART, CommonUtils.getContainer());
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
				data.setRole(ContentRoleType.PRIMARY);
				data = (ApplicationData) ContentServerHelper.service.updateContent(part, data, path);

				if (erp) {
					part = (WTPart) PersistenceHelper.manager.refresh(part);
					String code = ErpHelper.manager.sendToErp(part);
					list.add(code);
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
		ArrayList<Map<String, Object>> _addRows = (ArrayList<Map<String, Object>>) params.get("_addRows");
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

				Folder folder = FolderTaskLogic.getFolder(PartHelper.SPEC_PART, CommonUtils.getContainer());
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

				Map<String, Object> _addRow = _addRows.get(i);
				String oid = (String) _addRow.get("oid");
				WTDocument document = (WTDocument) CommonUtils.getObject(oid);
				WTDocumentWTPartLink link = WTDocumentWTPartLink.newWTDocumentWTPartLink(document, part);
				PersistenceHelper.manager.save(link);

				part = (WTPart) PersistenceHelper.manager.refresh(part);
				String code = ErpHelper.manager.sendToErpItem(part);
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