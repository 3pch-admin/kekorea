package e3ps.part.service;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import e3ps.approval.service.ApprovalHelper;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.ContentUtils;
import e3ps.common.util.IBAUtils;
import e3ps.common.util.JExcelUtils;
import e3ps.common.util.MessageHelper;
import e3ps.common.util.StringUtils;
import e3ps.doc.WTDocumentWTPartLink;
import e3ps.epm.dto.PROEAttr;
import e3ps.epm.service.EpmHelper;
import e3ps.erp.service.ErpHelper;
import e3ps.part.UnitBom;
import e3ps.part.UnitBomPartLink;
import e3ps.part.UnitSubPart;
import e3ps.part.beans.PartViewData;
import e3ps.part.beans.UnitBomViewData;
import e3ps.workspace.ApprovalContract;
import e3ps.workspace.ApprovalContractPersistableLink;
import e3ps.workspace.ApprovalLine;
import e3ps.workspace.ApprovalMaster;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import wt.clients.folder.FolderTaskLogic;
import wt.clients.vc.CheckInOutTaskLogic;
import wt.doc.WTDocument;
import wt.enterprise.EnterpriseHelper;
import wt.epm.EPMApplicationType;
import wt.epm.EPMAuthoringAppType;
import wt.epm.EPMContextHelper;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.EPMDocumentMasterIdentity;
import wt.epm.EPMDocumentType;
import wt.epm.build.EPMBuildRule;
import wt.fc.BinaryLink;
import wt.fc.IdentityHelper;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.org.WTUser;
import wt.ownership.Ownership;
import wt.part.Quantity;
import wt.part.QuantityUnit;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartMasterIdentity;
import wt.part.WTPartUsageLink;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.vc.struct.StructHelper;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;
import wt.vc.wip.CheckoutLink;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.WorkInProgressServerHelper;

public class StandardPartService extends StandardManager implements PartService, MessageHelper {

	private static final long serialVersionUID = -1015333872196142557L;

	public static StandardPartService newStandardPartService() throws WTException {
		StandardPartService instance = new StandardPartService();
		instance.initialize();
		return instance;
	}

	@Override
	public Map<String, Object> addPartAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> list = (List<String>) param.get("list");
		WTPart part = null;
		ReferenceFactory rf = new ReferenceFactory();
		ArrayList<String[]> data = new ArrayList<String[]>();
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (String oid : list) {
				part = (WTPart) rf.getReference(oid).getObject();
				PartViewData pdata = new PartViewData(part);
				// context ?????? ??????
				// 0 oid, 1 number, 2 name, 3 version, 4 state, 5 modifier, 6 modifyDate
				String[] s = new String[] { pdata.oid, pdata.number, pdata.name, pdata.state + "$" + pdata.stateKey,
						pdata.version + "." + pdata.iteration, pdata.modifier, pdata.modifyDate, pdata.iconPath,
						pdata.spec, pdata.maker, pdata.master_type, String.valueOf(pdata.isProduct), pdata.createDate,
						pdata.creator, pdata.name_of_parts, ContentUtils.getOpenIcon(pdata.oid), pdata.state,
						pdata.dwg_no };
				data.add(s);
			}

			map.put("result", SUCCESS);
			map.put("list", data);
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "?????? ?????? ??? ????????? ?????????????????????.\n????????? ??????????????? ???????????????");
			map.put("url", "/Windchill/plm/part/addPart");
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> approvalLibraryPartAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		ReferenceFactory rf = new ReferenceFactory();
		ApprovalContract contract = null;
		String name = (String) param.get("name");
		List<String> partOids = (List<String>) param.get("partOids");

		Transaction trs = new Transaction();
		try {
			trs.start();

			contract = ApprovalContract.newApprovalContract();
			contract.setName(name);
			contract.setStartTime(new Timestamp(new Date().getTime()));
			contract.setState(WorkspaceHelper.LINE_APPROVING);
			contract = (ApprovalContract) PersistenceHelper.manager.save(contract);

			for (int i = 0; i < partOids.size(); i++) {
				String oid = (String) partOids.get(i);
				WTPart part = (WTPart) rf.getReference(oid).getObject();
				ApprovalContractPersistableLink aLink = ApprovalContractPersistableLink
						.newApprovalContractPersistableLink(contract, part);
				PersistenceHelper.manager.save(aLink);
			}

			WorkspaceHelper.service.submitApp(contract, param);

			map.put("result", SUCCESS);
			map.put("msg", "????????? ????????? ?????? ???????????????.");
			map.put("url", "/Windchill/plm/approval/listApproval");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "????????? ?????? ?????? ??? ????????? ?????????????????????.\n????????? ??????????????? ???????????????.");
			map.put("url", "/Windchill/plm/part/approvalLibraryPart");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> createLibraryPartAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		ReferenceFactory rf = new ReferenceFactory();
		String name = (String) param.get("name");
		String number = (String) param.get("number");
		String location = (String) param.get("location");
		List<String> docOids = (List<String>) param.get("docOids");

		// ????????? ??????
		String SPEC = (String) param.get("SPEC");
		// String PRODUCT_NAME = (String) param.get("PRODUCT_NAME");
		String MAKER = (String) param.get("MAKER");
		String BOM = (String) param.get("BOM");
		String WEIGHT = (String) param.get("WEIGHT");
		WTPart part = null;

		// erp ??????
//		String itemClassSeq = (String) param.get("ItemClassSeq");
		String itemClassName = (String) param.get("ItemClassName");

		List<String> appList = (List<String>) param.get("appList");

		boolean isApp = appList.size() > 0;

		Transaction trs = new Transaction();
		try {
			trs.start();

			boolean isNumber = PartHelper.manager.isPart(number);
			if (isNumber) {
				map.put("result", FAIL);
				map.put("isNumber", isNumber);
				map.put("msg", "??????????????? ????????? ????????? ?????? ????????? ???????????????.");
				return map;
			}

			part = WTPart.newWTPart();
			part.setName(name);
			part.setNumber(number);

			View view = ViewHelper.service.getView("Engineering");
			ViewHelper.assignToView(part, view);
			Folder folder = FolderTaskLogic.getFolder(location, CommonUtils.getLibrary());
			FolderHelper.assignLocation((FolderEntry) part, folder);

			part = (WTPart) PersistenceHelper.manager.save(part);

			IBAUtils.createIBA(part, "s", "SPEC", SPEC);
			IBAUtils.createIBA(part, "s", "PRODUCT_NAME", name);
			IBAUtils.createIBA(part, "s", "MAKER", MAKER);
			IBAUtils.createIBA(part, "s", "BOM", BOM);
			IBAUtils.createIBA(part, "s", "WEIGHT", WEIGHT);
			IBAUtils.createIBA(part, "s", "MASTER_TYPE", itemClassName);

			ContentUtils.updatePrimary(param, part);
			ContentUtils.updateSecondary(param, part);

			for (int i = 0; i < docOids.size(); i++) {
				String oid = (String) docOids.get(i);
				WTDocument document = (WTDocument) rf.getReference(oid).getObject();
				WTDocumentWTPartLink link = WTDocumentWTPartLink.newWTDocumentWTPartLink(document, part);
				PersistenceHelper.manager.save(link);
			}

			if (isApp) {
				WorkspaceHelper.service.submitApp(part, param);
			}

			EPMDocument epm = null;
			String primary = (String) param.get("primary");
			if (part != null && !StringUtils.isNull(primary)) {
				// ??????????????? ?????? ?????? ??? ?????? ??????...
				// ????????? ?????? ???..????????????

				boolean isNum = EpmHelper.manager.isNumber(number);
				// ?????? ?????? ??????...
				if (isNum) {
					// ?????? ????????? ?????? ????????? ???
					epm = EpmHelper.manager.getLatestEPM(number);

					// ????????? ?????? ??????..
					QueryResult build = PersistenceHelper.manager.navigate(epm, "buildTarget", EPMBuildRule.class);
					if (build.hasMoreElements()) {
						// ?????? ?????? ?????? ?????????..
						System.out.println("?????? ????????? ??????.. ????????? ?????? ?????? ??????..");
					} else {
						EPMBuildRule link = EPMBuildRule.newEPMBuildRule(epm, part);
						link.setBuildType(7); // ????????? ?????? 7...
						PersistenceHelper.manager.save(link);
					}
				} else {

					epm = EPMDocument.newEPMDocument();
					epm.setNumber(number);
					epm.setName(name);
					epm.setCADName(primary);

					epm.setDocType(EPMDocumentType.toEPMDocumentType("CADDRAWING"));

					EPMDocumentMaster master = (EPMDocumentMaster) epm.getMaster();
					EPMContextHelper.setApplication(EPMApplicationType.toEPMApplicationType("EPM"));
					master.setOwnerApplication(EPMContextHelper.getApplication());
					master.setAuthoringApplication(EPMAuthoringAppType.toEPMAuthoringAppType("ACAD"));

					folder = FolderTaskLogic.getFolder(location, CommonUtils.getLibrary());
					FolderHelper.assignLocation((FolderEntry) epm, folder);

					epm = (EPMDocument) PersistenceHelper.manager.save(epm);

					IBAUtils.createIBA(epm, "s", "SPEC", SPEC);
					IBAUtils.createIBA(epm, "s", "PRODUCT_NAME", name);
					IBAUtils.createIBA(epm, "s", "MAKER", MAKER);
					IBAUtils.createIBA(epm, "s", "BOM", BOM);
					IBAUtils.createIBA(epm, "s", "WEIGHT", WEIGHT);
					IBAUtils.createIBA(part, "s", "MASTER_TYPE", itemClassName);

					ContentUtils.updatePrimary(param, epm);

					EPMBuildRule link = EPMBuildRule.newEPMBuildRule(epm, part);
					PersistenceHelper.manager.save(link);

					// EpmHelper.service.dwgToPDF(epm);

				}
			}

			map.put("result", SUCCESS);
			map.put("msg", "???????????? " + CREATE_OK);

			if (isApp) {
				map.put("url", "/Windchill/plm/approval/listApproval");
			} else {
				map.put("url", "/Windchill/plm/part/listLibraryPart");
			}
			trs.commit();
			trs = null;

		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "????????? " + CREATE_FAIL);
			map.put("url", "/Windchill/plm/part/createLibraryPart");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> approvalElecPartAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		Transaction trs = new Transaction();
		try {
			trs.start();

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> createElecPartAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();

		String location = (String) param.get("location");
		String excel = (String) param.get("primary");

		Transaction trs = new Transaction();
		try {
			trs.start();

			File file = new File(excel);

			Workbook workbook = JExcelUtils.getWorkbook(file);

			Sheet[] sheets = workbook.getSheets();
			int rows = sheets[0].getRows();

			for (int i = 3; i < rows; i++) {

				Cell[] cell = sheets[0].getRow(i);

//				String level = JExcelUtils.getContent(cell, 2); // ??????
				String name = JExcelUtils.getContent(cell, 4); // ?????? ??????
				String pnumber = JExcelUtils.getContent(cell, 5);
				String number = JExcelUtils.getContent(cell, 6); // ?????? ?????? ??????
//				String spec = JExcelUtils.getContent(cell, 7); // ??????
				String unit = JExcelUtils.getContent(cell, 8); // depth
//				String type = JExcelUtils.getContent(cell, 9); // depth
//				String defaultCount = JExcelUtils.getContent(cell, 10); // depth
//				String assyCount = JExcelUtils.getContent(cell, 11); // depth
				String totalCount = JExcelUtils.getContent(cell, 12); // depth
//				String maker = JExcelUtils.getContent(cell, 13); // depth
//				String material = JExcelUtils.getContent(cell, 14); // depth

				// ?????? ??????

				WTPart part = null;

				QuerySpec query = new QuerySpec();
				int idx = query.appendClassList(WTPart.class, true);
				SearchCondition sc = new SearchCondition(WTPart.class, WTPart.NUMBER, "=", number);
				query.appendWhere(sc, new int[] { idx });

				QueryResult result = PersistenceHelper.manager.find(query);
				if (result.hasMoreElements()) {
					Object[] obj = (Object[]) result.nextElement();
					part = (WTPart) obj[0];
				} else {

					part = WTPart.newWTPart();
					part.setName(name);
					part.setNumber(number);

					View view = ViewHelper.service.getView("Engineering");
					ViewHelper.assignToView(part, view);

					Folder folder = FolderTaskLogic.getFolder(location, CommonUtils.getContainer());
					FolderHelper.assignLocation((FolderEntry) part, folder);

					part = (WTPart) PersistenceHelper.manager.save(part);

					// IBA ?????? ??????..
				}

				WTPart parent = null;

				if (!StringUtils.isNull(pnumber)) {

					// ?????? ????????????
					QuerySpec qs = new QuerySpec();
					int idx_p = qs.appendClassList(WTPart.class, true);

					sc = new SearchCondition(WTPart.class, WTPart.NUMBER, "=", pnumber);
					qs.appendWhere(sc, new int[] { idx_p });

					QueryResult qr = PersistenceHelper.manager.find(qs);
					if (qr.hasMoreElements()) {
						Object[] obj = (Object[]) qr.nextElement();
						parent = (WTPart) obj[0];
					}
				}

				if (parent != null) {

					WTPartUsageLink link = WTPartUsageLink.newWTPartUsageLink(parent, (WTPartMaster) part.getMaster());

					QuantityUnit quantityUnit = QuantityUnit.toQuantityUnit(unit.toLowerCase().trim());
					link.setQuantity(Quantity.newQuantity(Double.parseDouble(totalCount), quantityUnit));
					PersistenceHelper.manager.save(link);
				}
			}

			map.put("result", SUCCESS);
			map.put("msg", "???????????? " + CREATE_OK);
			map.put("url", "/Windchill/plm/part/listElecPart");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "????????? " + CREATE_FAIL);
			map.put("url", "/Windchill/plm/part/createElecPart");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> createProductPartAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		ReferenceFactory rf = new ReferenceFactory();
		String name = (String) param.get("name");
		String number = (String) param.get("number");
		String location = (String) param.get("location");
		List<String> docOids = (List<String>) param.get("docOids");

		// ????????? ??????
		String COLOR_FINISH = (String) param.get("COLOR_FINISH");
		String MAIN_ASSY = (String) param.get("MAIN_ASSY");
		String MAKER = (String) param.get("MAKER");
		String SPEC = (String) param.get("SPEC");
		String MATERIAL = (String) param.get("MATERIAL");
		String MODELED_BY = (String) param.get("MODELED_BY");
		String PRODUCT_NAME = (String) param.get("PRODUCT_NAME");
		String TREATMENT = (String) param.get("TREATMENT");
		String DRAWING_BY = (String) param.get("DRAWING_BY");
		String DIMENSION = (String) param.get("DIMENSION");
		String BOM = (String) param.get("BOM");
		String WEIGHT = (String) param.get("WEIGHT");
		// String MASTER_TYPE = (String) param.get("MASTER_TYPE");

		String MACHINE_TYPE = (String) param.get("MACHINE_TYPE");
		String PARALLEL = (String) param.get("PARALLEL");
		String MIN_TEMP = (String) param.get("MIN_TEMP");
		String MAX_TEMP = (String) param.get("MAX_TEMP");

		// erp ?????? == ??????????????? == ???????????????
		String itemClassName = (String) param.get("ItemClassName");

		WTPart part = null;
		List<String> appList = (List<String>) param.get("appList");
		boolean isApp = appList.size() > 0;

		Transaction trs = new Transaction();
		try {
			trs.start();

			boolean isNumber = PartHelper.manager.isPart(number);
			if (isNumber) {
				map.put("result", FAIL);
				map.put("isNumber", isNumber);
				map.put("msg", "??????????????? ????????? ????????? ?????? ????????? ???????????????.");
				return map;
			}

			part = WTPart.newWTPart();
			part.setName(name);
			part.setNumber(number);

			View view = ViewHelper.service.getView("Engineering");
			ViewHelper.assignToView(part, view);

			Folder folder = FolderTaskLogic.getFolder(location, CommonUtils.getContainer());
			FolderHelper.assignLocation((FolderEntry) part, folder);

			part = (WTPart) PersistenceHelper.manager.save(part);

			IBAUtils.createIBA(part, "s", "COLOR_FINISH", COLOR_FINISH);
			IBAUtils.createIBA(part, "s", "MAIN_ASSY", MAIN_ASSY);
			IBAUtils.createIBA(part, "s", "SPEC", SPEC);
			IBAUtils.createIBA(part, "s", "MAKER", MAKER);
			IBAUtils.createIBA(part, "s", "MATERIAL", MATERIAL);
			IBAUtils.createIBA(part, "s", "MODELED_BY", MODELED_BY);
			IBAUtils.createIBA(part, "s", "PRODUCT_NAME", PRODUCT_NAME);
			IBAUtils.createIBA(part, "s", "TREATMENT", TREATMENT);
			IBAUtils.createIBA(part, "s", "DRAWING_BY", DRAWING_BY);
			IBAUtils.createIBA(part, "s", "DIMENSION", DIMENSION);
			IBAUtils.createIBA(part, "s", "BOM", BOM);
			IBAUtils.createIBA(part, "s", "WEIGHT", WEIGHT);
			IBAUtils.createIBA(part, "s", "MASTER_TYPE", itemClassName);

			IBAUtils.createIBA(part, "s", "MACHINE_TYPE", MACHINE_TYPE);
			IBAUtils.createIBA(part, "s", "PARALLEL", PARALLEL);
			IBAUtils.createIBA(part, "s", "MIN_TEMP", MIN_TEMP);
			IBAUtils.createIBA(part, "s", "MAX_TEMP", MAX_TEMP);

			// ContentUtils.updatePrimary(param, part);
			ContentUtils.updateSecondary(param, part);

			for (int i = 0; i < docOids.size(); i++) {
				String oid = (String) docOids.get(i);
				WTDocument document = (WTDocument) rf.getReference(oid).getObject();
				WTDocumentWTPartLink link = WTDocumentWTPartLink.newWTDocumentWTPartLink(document, part);
				PersistenceHelper.manager.save(link);
			}

			if (isApp) {
				WorkspaceHelper.service.submitApp(part, param);
			}

			EPMDocument epm = null;
			if (part != null) {
				// ??????????????? ?????? ?????? ??? ?????? ??????...
				// ????????? ?????? ???..????????????
				String primary = (String) param.get("primary");

				epm = EPMDocument.newEPMDocument();
				epm.setNumber(number);
				epm.setName(name);
				epm.setCADName(primary);

				epm.setDocType(EPMDocumentType.toEPMDocumentType("CADDRAWING"));

				EPMDocumentMaster master = (EPMDocumentMaster) epm.getMaster();
				EPMContextHelper.setApplication(EPMApplicationType.toEPMApplicationType("EPM"));
				master.setOwnerApplication(EPMContextHelper.getApplication());
				master.setAuthoringApplication(EPMAuthoringAppType.toEPMAuthoringAppType("ACAD"));

				folder = FolderTaskLogic.getFolder(location, CommonUtils.getContainer());
				FolderHelper.assignLocation((FolderEntry) epm, folder);

				epm = (EPMDocument) PersistenceHelper.manager.save(epm);

				IBAUtils.createIBA(epm, "s", "COLOR_FINISH", COLOR_FINISH);
				IBAUtils.createIBA(epm, "s", "MAIN_ASSY", MAIN_ASSY);
				IBAUtils.createIBA(epm, "s", "SPEC", SPEC);
				IBAUtils.createIBA(epm, "s", "MAKER", MAKER);
				IBAUtils.createIBA(epm, "s", "MATERIAL", MATERIAL);
				IBAUtils.createIBA(epm, "s", "MODELED_BY", MODELED_BY);
				IBAUtils.createIBA(epm, "s", "PRODUCT_NAME", PRODUCT_NAME);
				IBAUtils.createIBA(epm, "s", "TREATMENT", TREATMENT);
				IBAUtils.createIBA(epm, "s", "DRAWING_BY", DRAWING_BY);
				IBAUtils.createIBA(epm, "s", "DIMENSION", DIMENSION);
				IBAUtils.createIBA(epm, "s", "BOM", BOM);
				IBAUtils.createIBA(epm, "s", "WEIGHT", WEIGHT);
				IBAUtils.createIBA(epm, "s", "MASTER_TYPE", itemClassName);

				IBAUtils.createIBA(epm, "s", "MACHINE_TYPE", MACHINE_TYPE);
				IBAUtils.createIBA(epm, "s", "PARALLEL", PARALLEL);
				IBAUtils.createIBA(epm, "s", "MIN_TEMP", MIN_TEMP);
				IBAUtils.createIBA(epm, "s", "MAX_TEMP", MAX_TEMP);

				ContentUtils.updatePrimary(param, epm);

				EPMBuildRule link = EPMBuildRule.newEPMBuildRule(epm, part);
				PersistenceHelper.manager.save(link);

				// EpmHelper.service.dwgToPDF(epm);
				//
				// ConfigSpec configspec = null;
				// PublishResult rs = Publish.doPublish(false, true, epm, configspec, null,
				// false, null, null, 1, null, 2,
				// null);
				//
				// if (rs.isSuccessful()) {
				// System.out.println("????????????");
				// }
			}

			map.put("result", SUCCESS);
			map.put("msg", "???????????? " + CREATE_OK);

			if (isApp) {
				map.put("url", "/Windchill/plm/approval/listApproval");
			} else {
				map.put("url", "/Windchill/plm/part/listProductPart");
			}

			trs.commit();
			trs = null;

		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "????????? " + CREATE_FAIL);
			map.put("url", "/Windchill/plm/part/createProductPart");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> deletePartAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> list = (List<String>) param.get("list");
		String url = (String) param.get("url");
		WTPart part = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (String oid : list) {

				part = (WTPart) rf.getReference(oid).getObject();

				EPMDocument epm = PartHelper.manager.getEPMDocument(part);

				if (epm != null) {
					PersistenceHelper.manager.delete(epm);
				}

				PersistenceHelper.manager.delete(part);

			}
			map.put("result", SUCCESS);
			map.put("msg", "????????? ?????? ???????????????.");
			map.put("url", url);
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("url", url);
			map.put("msg", "?????? ?????? ??? ????????? ?????????????????????.\n??????????????? ???????????????.");
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> saveAsPartAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		ReferenceFactory rf = new ReferenceFactory();

		List<String> oidArray = (List<String>) param.get("list");
		List<String> nameArray = (List<String>) param.get("nameArray");
		List<String> numberArray = (List<String>) param.get("numberArray");
		WTPart saveAsPart = null;
		WTPart part = null;
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (int i = 0; i < oidArray.size(); i++) {
				String oid = (String) oidArray.get(i);
				String number = (String) numberArray.get(i);
				String name = (String) nameArray.get(i);

				part = (WTPart) rf.getReference(oid).getObject();

				saveAsPart = (WTPart) EnterpriseHelper.service.newCopy(part);
				saveAsPart.setNumber(number);
				saveAsPart.setName(name);

				// ?????? ???
				View view = ViewHelper.service.getView("Engineering");
				ViewHelper.assignToView(saveAsPart, view);

				// ?????? ?????? ??????..
				boolean isLibrary = false;
				boolean isProduct = false;
				if (part.getContainer().getName().equalsIgnoreCase("Commonspace")) {
					isProduct = true;
				} else if (part.getContainer().getName().equalsIgnoreCase("LIBRARY")) {
					isLibrary = true;
				}

				Folder folder = null;
				if (isLibrary) {
					folder = FolderTaskLogic.getFolder(part.getLocation(), CommonUtils.getLibrary());
				} else if (isProduct) {
					folder = FolderTaskLogic.getFolder(part.getLocation(), CommonUtils.getContainer());
				}

				FolderHelper.assignLocation((FolderEntry) saveAsPart, folder);
				PersistenceHelper.manager.save(saveAsPart);

				QueryResult result = StructHelper.service.navigateUses(part, WTPartUsageLink.class, false);
				while (result.hasMoreElements()) {
					WTPartUsageLink l = (WTPartUsageLink) result.nextElement();
					PersistenceServerHelper.manager.copyLink((BinaryLink) l, l.getUses(), "uses", saveAsPart);
				}

				PartViewData dd = new PartViewData(part);

				EPMBuildRule link = EPMBuildRule.newEPMBuildRule(dd.epm, saveAsPart);
				link.setBuildType(7); // ????????? ?????? 7...
				PersistenceHelper.manager.save(link);
			}

			map.put("result", SUCCESS);
			map.put("msg", "???????????? ?????? ???????????????.");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "????????? ?????? ??? ????????? ?????????????????????.\n????????? ??????????????? ???????????????.");
			map.put("url", "/Windchill/plm/common/saveAsObject");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> createAllPartsAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		Transaction trs = new Transaction();
		String primary = (String) param.get("primary");
		XSSFWorkbook wb = null;
		try {
			trs.start();

			String s = primary.split("&")[0];
			File file = new File(s);
			wb = new XSSFWorkbook(new FileInputStream(file));

			XSSFCell cell; // ?????? ??????..
			XSSFSheet sheet = wb.getSheetAt(0);
			int rows = sheet.getPhysicalNumberOfRows();
			for (int i = 1; i < rows; i++) {
				XSSFRow row = sheet.getRow(i);

				cell = row.getCell(1); // ....????????????
				String bom = cell.getStringCellValue();

				cell = row.getCell(2); // ....?????? ?????????
				String itemclassname = cell.getStringCellValue();

				cell = row.getCell(3); // ....??????
				String number = cell.getStringCellValue();

				cell = row.getCell(4); // ....??????
				String name = cell.getStringCellValue();

				cell = row.getCell(5); // ....??????
				String spec = cell.getStringCellValue();

				boolean isPart = PartHelper.manager.isPart(number);
				if (isPart) {
					System.out.println("?????? ????????? ?????? =  " + number + "??? ????????? ?????? ?????? ?????????.");
					continue;
				} else {
					WTPart part = WTPart.newWTPart();
					part.setName(name.toUpperCase());
					part.setNumber(number.toUpperCase());

					View view = ViewHelper.service.getView("Engineering");
					ViewHelper.assignToView(part, view);

					Folder folder = FolderTaskLogic.getFolder("/Default/??????", CommonUtils.getLibrary());
					FolderHelper.assignLocation((FolderEntry) part, folder);

					part = (WTPart) PersistenceHelper.manager.save(part);

					IBAUtils.createIBA(part, "s", "SPEC", spec);
					IBAUtils.createIBA(part, "s", "BOM", bom);
					IBAUtils.createIBA(part, "s", "ITEMCLASSNAME", itemclassname);

				}
			}

			map.put("result", SUCCESS);
			map.put("msg", "?????? ?????? ????????? ?????? ???????????????.");
			map.put("url", "/Windchill/plm/part/createAllParts");
			trs.commit();
			trs = null;

		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "?????? ?????? ?????? ??? ????????? ?????????????????????.\n??????????????? ???????????????.");
			map.put("url", "/Windchill/plm/part/createAllParts");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> deleteLibraryPartAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> list = (List<String>) param.get("list");
		WTPart part = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			EPMDocument epm = PartHelper.manager.getEPMDocument(part);
			if (epm != null) {
				map.put("result", FAIL);
				map.put("msg", "??????????????? ???????????? ????????? ????????? ????????????.");
				map.put("url", "/Windchill/plm/part/listLibraryPart");
				return map;
			}

			for (String oid : list) {
				part = (WTPart) rf.getReference(oid).getObject();
				PersistenceHelper.manager.delete(part);
			}

			map.put("result", SUCCESS);
			map.put("msg", "????????????  " + DELETE_OK);
			map.put("url", "/Windchill/plm/part/listLibraryPart");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "????????? " + DELETE_FAIL);
			map.put("url", "/Windchill/plm/part/listLibraryPart");
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> deleteProductPartAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> list = (List<String>) param.get("list");
		WTPart part = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			EPMDocument epm = PartHelper.manager.getEPMDocument(part);
			if (epm != null) {
				map.put("result", FAIL);
				map.put("msg", "??????????????? ???????????? ????????? ????????? ????????????.");
				map.put("url", "/Windchill/plm/part/listLibraryPart");
				return map;
			}

			for (String oid : list) {
				part = (WTPart) rf.getReference(oid).getObject();
				PersistenceHelper.manager.delete(part);
			}

			map.put("result", SUCCESS);
			map.put("msg", "????????????  " + DELETE_OK);
			map.put("url", "/Windchill/plm/part/listProductPart");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "????????? " + DELETE_FAIL);
			map.put("url", "/Windchill/plm/part/listProductPart");
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> createBomAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		Transaction trs = new Transaction();
		String primary = (String) param.get("primary");
		XSSFWorkbook wb = null;
		try {
			trs.start();

			String s = primary.split("&")[0];

			File file = new File(s);
			wb = new XSSFWorkbook(new FileInputStream(file));

			XSSFCell cell; // ?????? ??????..
			XSSFSheet sheet;
			int sheets = wb.getNumberOfSheets();

			for (int i = 0; i < sheets; i++) {
				sheet = wb.getSheetAt(i);
				int rows = sheet.getPhysicalNumberOfRows();

				for (int j = 1; j < rows; j++) {
					XSSFRow row = sheet.getRow(j);

					cell = row.getCell(0); // ?????????
					String pnumber = cell.getStringCellValue().toUpperCase();

					cell = row.getCell(1); // ??????
					String number = cell.getStringCellValue().toUpperCase();

					cell = row.getCell(2); // ??????
					double totalCount = cell.getNumericCellValue();

					WTPart parent = PartHelper.manager.getLatestPart(pnumber);
					WTPart child = PartHelper.manager.getLatestPart(number);

//					String ext = FileUtil.getExtension(number);
					// String orgNumber = number.substring(0, number.lastIndexOf("_"));

					// WTPart orgPart = PartHelper.manager.getLatestPart(orgNumber + "." + ext);
					WTPart orgPart = PartHelper.manager.getLatestPart(number);
					// // ????????? ??????..

					if (child != null) {
						System.out.println("child=" + child.getNumber());
					}

					if (orgPart != null) {
						System.out.println("org==" + orgPart.getNumber());
					}

					if (j == 1 && child != null) {

						WTPart copy = null;

						// ????????? ?????? ?????? ??????...
						// ??????????????? ?????? ?????????
						if (!WorkInProgressHelper.isCheckedOut(child)) {
							Folder cFolder = CheckInOutTaskLogic.getCheckoutFolder();
							CheckoutLink clink = WorkInProgressHelper.service.checkout(child, cFolder, "");
							copy = (WTPart) clink.getWorkingCopy();
							// ?????????...
						}

						copy = (WTPart) PersistenceHelper.manager.refresh(copy);

						QueryResult result = StructHelper.service.navigateUses(copy, WTPartUsageLink.class, false);
						while (result.hasMoreElements()) {
							WTPartUsageLink l = (WTPartUsageLink) result.nextElement();
							PersistenceHelper.manager.delete(l);
						}

						// child = (WTPart) WorkInProgressHelper.service.checkin(copy, "??????BOM");

						// child = (WTPart) PersistenceHelper.manager.refresh(child);

						// child = (WTPart) LifeCycleHelper.service.setLifeCycleState(child,
						// State.toState("RELEASED"));
					}

					if (child != null) {
						QueryResult result = StructHelper.service.navigateUses(child, WTPartUsageLink.class, false);
						while (result.hasMoreElements()) {
							WTPartUsageLink l = (WTPartUsageLink) result.nextElement();
							PersistenceServerHelper.manager.remove(l);
						}
					}

					if (child == null) {

						if (j == 1) {

							QueryResult result = StructHelper.service.navigateUses(orgPart, WTPartUsageLink.class,
									false);
							while (result.hasMoreElements()) {
								WTPartUsageLink l = (WTPartUsageLink) result.nextElement();
								PersistenceServerHelper.manager.remove(l);
							}

							// top
							// child = (WTPart) EnterpriseHelper.service.newCopy(orgPart);
							// child.setName(number.toUpperCase());
							// child.setNumber(number.toUpperCase());
							//
							// // ?????? ???
							// View view = ViewHelper.service.getView("Engineering");
							// ViewHelper.assignToView(child, view);
							//
							// Folder folder = FolderTaskLogic.getFolder("/Default/??????",
							// CommonUtils.getLibrary());
							// FolderHelper.assignLocation((FolderEntry) child, folder);
							// PersistenceHelper.manager.save(child);
							//
							// QueryResult result = StructHelper.service.navigateUses(orgPart,
							// WTPartUsageLink.class,
							// false);
							// while (result.hasMoreElements()) {
							// WTPartUsageLink l = (WTPartUsageLink) result.nextElement();
							// PersistenceServerHelper.manager.copyLink((BinaryLink) l, l.getUses(), "uses",
							// child);
							// }
							//
							// PartViewData dd = new PartViewData(orgPart);
							//
							// EPMBuildRule link = EPMBuildRule.newEPMBuildRule(dd.epm, child);
							// link.setBuildType(7); // ????????? ?????? 7...
							// PersistenceHelper.manager.save(link);
							//
							// child = (WTPart) PersistenceHelper.manager.refresh(child);
							//
							// result.reset();
							// result = StructHelper.service.navigateUses(child, WTPartUsageLink.class,
							// false);
							// while (result.hasMoreElements()) {
							// WTPartUsageLink l = (WTPartUsageLink) result.nextElement();
							// PersistenceServerHelper.manager.remove(l);
							// }
							//
							// child = (WTPart) PersistenceHelper.manager.refresh(child);
							//
							// child = (WTPart) LifeCycleHelper.service.setLifeCycleState(child,
							// State.toState("RELEASED"));
						} else {

							// child = WTPart.newWTPart();
							// child.setName(number.toUpperCase());
							// child.setNumber(number.toUpperCase());
							//
							// View view = ViewHelper.service.getView("Engineering");
							// ViewHelper.assignToView(child, view);
							//
							// Folder folder = FolderTaskLogic.getFolder("/Default/??????",
							// CommonUtils.getLibrary());
							// FolderHelper.assignLocation((FolderEntry) child, folder);
							//
							// child = (WTPart) PersistenceHelper.manager.save(child);
							//
							// child = (WTPart) PersistenceHelper.manager.refresh(child);
							//
							// child = (WTPart) LifeCycleHelper.service.setLifeCycleState(child,
							// State.toState("RELEASED"));
						}

						// map.put("result", FAIL);
						// map.put("msg", "?????? " + i + "?????? ?????? " + j + "?????? " + number + " ????????? ????????? ?????? ??????
						// ????????????.");
						// map.put("url", "/Windchill/plm/part/createBom");
						// return map;
					}

					if (!StringUtils.isNull(pnumber) && parent == null) {
						// map.put("result", FAIL);
						// map.put("msg", "?????? " + i + "?????? ?????? " + j + "?????? " + pnumber + " ????????? ????????? ?????? ??????
						// ????????????.");
						// map.put("url", "/Windchill/plm/part/createBom");
						// return map;

						parent = WTPart.newWTPart();
						parent.setName(pnumber.toUpperCase());
						parent.setNumber(pnumber.toUpperCase());

						View view = ViewHelper.service.getView("Engineering");
						ViewHelper.assignToView(parent, view);

						Folder folder = FolderTaskLogic.getFolder("/Default/??????", CommonUtils.getLibrary());
						FolderHelper.assignLocation((FolderEntry) parent, folder);

						parent = (WTPart) PersistenceHelper.manager.save(parent);

						parent = (WTPart) PersistenceHelper.manager.refresh(parent);

						parent = (WTPart) LifeCycleHelper.service.setLifeCycleState(parent, State.toState("RELEASED"));

					}

					if (parent != null) {
						WTPartUsageLink usageLink = WTPartUsageLink.newWTPartUsageLink(parent,
								(WTPartMaster) child.getMaster());
						QuantityUnit quantityUnit = QuantityUnit.toQuantityUnit("ea".toLowerCase().trim());
						usageLink.setQuantity(Quantity.newQuantity(totalCount, quantityUnit));
						PersistenceServerHelper.manager.insert(usageLink);
					}
				}
			}

			map.put("result", SUCCESS);
			map.put("msg", "BOM ????????? ?????? ???????????????.");
			map.put("url", "/Windchill/plm/part/createBom");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "BOM ?????? ??? ????????? ?????????????????????.\n??????????????? ???????????????.");
			map.put("url", "/Windchill/plm/part/createBom");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
			try {
				wb.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return map;
	}

	@Override
	public void test() throws WTException {
		Transaction trs = new Transaction();
		try {
			trs.start();

			String oid = "wt.part.WTPart:511882920";
			ReferenceFactory rf = new ReferenceFactory();
			WTPart part = (WTPart) rf.getReference(oid).getObject();

			// ??? ?????? ??????...
			// WTPart child = WTPart.newWTPart();

			WTPart child = (WTPart) EnterpriseHelper.service.newCopy(part);
			child.setNumber(part.getNumber() + "_9");
			child.setName(part.getName() + "_9");

			// ?????? ???
			View view = ViewHelper.service.getView("Engineering");
			ViewHelper.assignToView(child, view);

			Folder folder = FolderTaskLogic.getFolder("/Default/??????", CommonUtils.getLibrary());
			FolderHelper.assignLocation((FolderEntry) child, folder);
			PersistenceHelper.manager.save(child);

			QueryResult result = StructHelper.service.navigateUses(part, WTPartUsageLink.class, false);
			while (result.hasMoreElements()) {
				WTPartUsageLink l = (WTPartUsageLink) result.nextElement();
				System.out.println(l.getUsedBy().getNumber());
				// System.out.println("n-=" + l.getUses().getNumber());
				System.out.println("l=" + l.getCadSynchronized());

				// SyncedWithCADStatus ss = SyncedWithCADStatus.toSyncedWithCADStatus("yes");
				// l.setCadSynchronized(ss);
				//// l = (WTPartUsageLink) PersistenceHelper.manager.modify(l);
				// PersistenceServerHelper.manager.update(l);

				PersistenceServerHelper.manager.copyLink((BinaryLink) l, l.getUses(), "uses", child);
			}

			PartViewData dd = new PartViewData(part);

			EPMBuildRule link = EPMBuildRule.newEPMBuildRule(dd.epm, child);
			link.setBuildType(7); // ????????? ?????? 7...
			PersistenceHelper.manager.save(link);

			child = (WTPart) PersistenceHelper.manager.refresh(child);

			result.reset();
			result = StructHelper.service.navigateUses(child, WTPartUsageLink.class, false);
			while (result.hasMoreElements()) {
				WTPartUsageLink l = (WTPartUsageLink) result.nextElement();
				PersistenceServerHelper.manager.remove(l);
			}

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public Map<String, Object> deleteBomPartAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String oid = (String) param.get("oid");
		String root = (String) param.get("root");
		String poid = (String) param.get("poid");
		ReferenceFactory rf = new ReferenceFactory();
		WTPart child = null;
		WTPart parent = null;
		Transaction trs = new Transaction();
		try {
			trs.start();

			child = (WTPart) rf.getReference(oid).getObject();
			parent = (WTPart) rf.getReference(poid).getObject();

			if (!WorkInProgressHelper.isCheckedOut(parent)) {
				Folder cFolder = CheckInOutTaskLogic.getCheckoutFolder();
				CheckoutLink clink = WorkInProgressHelper.service.checkout(parent, cFolder, "");
				parent = (WTPart) clink.getWorkingCopy();
			}

			if (WorkInProgressHelper.isCheckedOut(parent)) {
				if (!WorkInProgressHelper.isWorkingCopy(parent)) {
					parent = (WTPart) WorkInProgressHelper.service.workingCopyOf(parent);
				}
			}

			WTPartUsageLink link = PartHelper.manager.getUsageLink(parent, child);

			if (link != null) {

				PersistenceHelper.manager.delete(link);

				WorkInProgressServerHelper.service.checkin(parent);
			}

			map.put("result", SUCCESS);
			map.put("url", "/Windchill/plm/part/infoBom?oid=" + root + "&popup=true");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "BOM ?????? ?????? ??? ????????? ?????????????????????.\n????????? ??????????????? ???????????????");
			map.put("url", "/Windchill/plm/part/infoBom?oid=" + root + "&popup=true");
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> insertBomPartAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String poid = (String) param.get("poid");
		String root = (String) param.get("root");
		List<String> list = (List<String>) param.get("list");
		ReferenceFactory rf = new ReferenceFactory();
		WTPart child = null;
		WTPart parent = null;
		Transaction trs = new Transaction();
		try {
			trs.start();

			parent = (WTPart) rf.getReference(poid).getObject();

			if (!WorkInProgressHelper.isCheckedOut(parent)) {
				Folder cFolder = CheckInOutTaskLogic.getCheckoutFolder();
				CheckoutLink clink = WorkInProgressHelper.service.checkout(parent, cFolder, "");
				parent = (WTPart) clink.getWorkingCopy();

				parent = (WTPart) PersistenceHelper.manager.refresh(parent);
			}

			// ???????????? ?????????
			if (WorkInProgressHelper.isCheckedOut(parent)) {
				// ???????????????
				if (!WorkInProgressHelper.isWorkingCopy(parent)) {
					parent = (WTPart) WorkInProgressHelper.service.workingCopyOf(parent);
				}
			}

			for (String oid : list) {
				child = (WTPart) rf.getReference(oid).getObject();
				WTPartUsageLink link = WTPartUsageLink.newWTPartUsageLink(parent, child.getMaster());
				link.setQuantity(Quantity.newQuantity(1D, QuantityUnit.EA));
				PersistenceHelper.manager.save(link);
			}

			map.put("result", SUCCESS);
			map.put("url", "/Windchill/plm/part/infoBom?oid=" + root + "&popup=true");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "BOM ?????? ?????? ??? ????????? ?????????????????????.\n????????? ??????????????? ???????????????");
			map.put("url", "/Windchill/plm/part/infoBom?oid=" + root + "&popup=true");
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> checkinBomPartAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		// BOM
		String root = (String) param.get("root");
		// ?????? OID
		String oid = (String) param.get("oid");
		ReferenceFactory rf = new ReferenceFactory();
		WTPart part = null;
		Transaction trs = new Transaction();
		try {
			trs.start();

			part = (WTPart) rf.getReference(oid).getObject();

			if (WorkInProgressHelper.isCheckedOut(part)) {
				part = (WTPart) WorkInProgressHelper.service.checkin(part, "");
			}

			map.put("result", SUCCESS);
			map.put("url", "/Windchill/plm/part/infoBom?oid=" + root + "&popup=true");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "BOM ?????? ????????? ??? ????????? ?????????????????????.\n????????? ??????????????? ???????????????");
			map.put("url", "/Windchill/plm/part/infoBom?oid=" + root + "&popup=true");
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> checkoutBomPartAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		// BOM
		String root = (String) param.get("root");
		// ?????? OID
		String oid = (String) param.get("oid");
		ReferenceFactory rf = new ReferenceFactory();
		WTPart part = null;
		Transaction trs = new Transaction();
		try {
			trs.start();

			part = (WTPart) rf.getReference(oid).getObject();

			if (!WorkInProgressHelper.isCheckedOut(part)) {
				Folder cFolder = CheckInOutTaskLogic.getCheckoutFolder();
				CheckoutLink clink = WorkInProgressHelper.service.checkout(part, cFolder, "");
				part = (WTPart) clink.getWorkingCopy();
			}

			map.put("result", SUCCESS);
			map.put("url", "/Windchill/plm/part/infoBom?oid=" + root + "&popup=true");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "BOM ?????? ???????????? ??? ????????? ?????????????????????.\n????????? ??????????????? ???????????????");
			map.put("url", "/Windchill/plm/part/infoBom?oid=" + root + "&popup=true");
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> undocheckoutBomPartAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		// BOM
		String root = (String) param.get("root");
		// ?????? OID
		String oid = (String) param.get("oid");
		ReferenceFactory rf = new ReferenceFactory();
		WTPart part = null;
		Transaction trs = new Transaction();
		try {
			trs.start();

			part = (WTPart) rf.getReference(oid).getObject();

			if (WorkInProgressHelper.isCheckedOut(part)) {
				part = (WTPart) WorkInProgressHelper.service.undoCheckout(part);
			}

			map.put("result", SUCCESS);
			map.put("url", "/Windchill/plm/part/infoBom?oid=" + root + "&popup=true");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "BOM ?????? ???????????? ?????? ??? ????????? ?????????????????????.\n????????? ??????????????? ???????????????");
			map.put("url", "/Windchill/plm/part/infoBom?oid=" + root + "&popup=true");
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> setDndUrlAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		// BOM
//		String root = (String) param.get("root");
		String org = (String) param.get("org");
		// ?????? OID
		List<String> list = (List<String>) param.get("list");

		ReferenceFactory rf = new ReferenceFactory();
		WTPart part = null;
//		WTPart rootPart = null;
		Transaction trs = new Transaction();
		try {
			trs.start();

			// ?????? ?????? ??????
			part = (WTPart) rf.getReference(org).getObject();

			// ????????? ??????
//			rootPart = (WTPart) rf.getReference(root).getObject();

			for (String target : list) {
				// ?????? ??????
				String oid = target.split("&")[0];
				// ????????? ??????
				String poid = target.split("&")[1];

				WTPart orgPart = (WTPart) rf.getReference(oid).getObject();
				WTPartMaster child = orgPart.getMaster();

				WTPartUsageLink link = PartHelper.manager.getUsageLink(part, child);
				if (link != null) {
					PersistenceServerHelper.manager.remove(link);
				}

				WTPart parent = (WTPart) rf.getReference(poid).getObject();

				// new link..
				WTPartUsageLink newLink = WTPartUsageLink.newWTPartUsageLink(parent, child);
				newLink.setQuantity(Quantity.newQuantity(1D, QuantityUnit.EA));
				PersistenceServerHelper.manager.insert(newLink);
			}

			List<String> treeList = (List<String>) param.get("treeList");

			int findNumber = 0;
			DecimalFormat df = new DecimalFormat("000");
			for (String tree : treeList) {
				String child = tree.split("&")[0];
				String parent = tree.split("&")[1];

				if (parent.equals("undefined")) {
					continue;
				}

				WTPart childPart = (WTPart) rf.getReference(child).getObject();
				WTPart parentPart = (WTPart) rf.getReference(parent).getObject();

				WTPartUsageLink link = PartHelper.manager.getUsageLink(parentPart, childPart.getMaster());

				if (link != null) {
					link.setFindNumber(df.format(findNumber++));
					PersistenceServerHelper.manager.update(link);
				}
			}

			map.put("result", SUCCESS);
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> setIndentUrlAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();

		String org = (String) param.get("org");
		String poid = (String) param.get("poid");
		String refId = (String) param.get("refId"); // ????????? ?????????.
		// ?????? OID

		ReferenceFactory rf = new ReferenceFactory();
		WTPart part = null;
		Transaction trs = new Transaction();
		try {
			trs.start();

			// ?????? ?????? ??????
			part = (WTPart) rf.getReference(org).getObject();
			WTPartMaster child = part.getMaster();

			// ????????? ??????
			WTPart parent = (WTPart) rf.getReference(poid).getObject();

			WTPartUsageLink link = PartHelper.manager.getUsageLink(parent, child);
			if (link != null) {
				PersistenceServerHelper.manager.remove(link);
			}

			WTPart ref = (WTPart) rf.getReference(refId).getObject();
			// new link..
			WTPartUsageLink newLink = WTPartUsageLink.newWTPartUsageLink(ref, child);
			newLink.setQuantity(Quantity.newQuantity(1D, QuantityUnit.EA));
			PersistenceServerHelper.manager.insert(newLink);

			List<String> treeList = (List<String>) param.get("treeList");

			int findNumber = 0;
			DecimalFormat df = new DecimalFormat("000");
			for (String tree : treeList) {
				String childOid = tree.split("&")[0];
				String parentOid = tree.split("&")[1];

				if (parentOid.equals("undefined")) {
					continue;
				}

				WTPart childPart = (WTPart) rf.getReference(childOid).getObject();
				WTPart parentPart = (WTPart) rf.getReference(parentOid).getObject();

				WTPartUsageLink links = PartHelper.manager.getUsageLink(parentPart, childPart.getMaster());

				if (links != null) {
					links.setFindNumber(df.format(findNumber++));
					PersistenceServerHelper.manager.update(links);
				}
			}

			map.put("result", SUCCESS);
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> setOutdentUrlAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();

		String org = (String) param.get("org");
		String poid = (String) param.get("poid");// ????????? ?????????.
		String orgParent = (String) param.get("orgParent"); // ????????????..
		// ?????? OID

		ReferenceFactory rf = new ReferenceFactory();
		WTPart part = null;
		Transaction trs = new Transaction();
		try {
			trs.start();

			// ?????? ?????? ??????
			part = (WTPart) rf.getReference(org).getObject();
			WTPartMaster child = part.getMaster();

			WTPart parent = (WTPart) rf.getReference(orgParent).getObject();

			WTPartUsageLink link = PartHelper.manager.getUsageLink(parent, child);
			System.out.println("link=" + link);
			if (link != null) {
				PersistenceServerHelper.manager.remove(link);
			}

			WTPart ref = (WTPart) rf.getReference(poid).getObject();
			// new link..
			WTPartUsageLink newLink = WTPartUsageLink.newWTPartUsageLink(ref, child);
			newLink.setQuantity(Quantity.newQuantity(1D, QuantityUnit.EA));
			PersistenceServerHelper.manager.insert(newLink);

			List<String> treeList = (List<String>) param.get("treeList");

			int findNumber = 0;
			DecimalFormat df = new DecimalFormat("000");
			for (String tree : treeList) {
				String childOid = tree.split("&")[0];
				String parentOid = tree.split("&")[1];

				if (parentOid.equals("undefined")) {
					continue;
				}

				WTPart childPart = (WTPart) rf.getReference(childOid).getObject();
				WTPart parentPart = (WTPart) rf.getReference(parentOid).getObject();

				WTPartUsageLink links = PartHelper.manager.getUsageLink(parentPart, childPart.getMaster());

				if (links != null) {
					links.setFindNumber(df.format(findNumber++));
					PersistenceServerHelper.manager.update(links);
				}
			}

			map.put("result", SUCCESS);
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> modifyLibraryPartAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		WTPart part = null;
		ReferenceFactory rf = new ReferenceFactory();
		String oid = (String) param.get("oid");
		String name = (String) param.get("name");
		String number = (String) param.get("number");
		String location = (String) param.get("location");
		List<String> docOids = (List<String>) param.get("docOids");

		// ????????? ??????
		String SPEC = (String) param.get("SPEC");
		// String PRODUCT_NAME = (String) param.get("PRODUCT_NAME");
		String MAKER = (String) param.get("MAKER");
		String BOM = (String) param.get("BOM");
		String WEIGHT = (String) param.get("WEIGHT");

		// erp ??????
		String itemClassName = (String) param.get("ItemClassName");

		List<String> appList = (List<String>) param.get("appList");
		boolean isApp = appList.size() > 0;

		Transaction trs = new Transaction();
		try {
			trs.start();
			part = (WTPart) rf.getReference(oid).getObject();

			if (!number.equals(part.getNumber())) {
				boolean isNumber = PartHelper.manager.isPart(number);
				if (isNumber) {
					map.put("result", FAIL);
					map.put("isNumber", isNumber);
					map.put("url", "/Windchill/plm/part/modifyLibraryPart?oid=" + oid);
					map.put("msg", "??????????????? ????????? ????????? ?????? ????????? ???????????????.");
					return map;
				}
			}

			Folder cFolder = CheckInOutTaskLogic.getCheckoutFolder();
			CheckoutLink clink = WorkInProgressHelper.service.checkout(part, cFolder, "????????? ?????? ?????? ??????");
			part = (WTPart) clink.getWorkingCopy();

			WTPartMaster master = (WTPartMaster) part.getMaster();
			WTPartMasterIdentity identity = (WTPartMasterIdentity) master.getIdentificationObject();
			identity.setName(name);
			identity.setNumber(number);
			master = (WTPartMaster) IdentityHelper.service.changeIdentity(master, identity);

			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			String msg = user.getFullName() + " ???????????? ???????????? ?????? ???????????????.";
			// ???????????? ?????? ????????? ??????
			part = (WTPart) WorkInProgressHelper.service.checkin(part, msg);

			LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) part, State.toState("INWORK"));

			Folder folder = FolderTaskLogic.getFolder(location, CommonUtils.getLibrary());
			FolderHelper.service.changeFolder((FolderEntry) part, folder);

			IBAUtils.createIBA(part, "s", "SPEC", SPEC);
			IBAUtils.createIBA(part, "s", "PRODUCT_NAME", name);
			IBAUtils.createIBA(part, "s", "MAKER", MAKER);
			IBAUtils.createIBA(part, "s", "BOM", BOM);
			IBAUtils.createIBA(part, "s", "WEIGHT", WEIGHT);
			IBAUtils.createIBA(part, "s", "MASTER_TYPE", itemClassName);

			ContentUtils.updatePrimary(param, part);
			ContentUtils.updateSecondary(param, part);

			if (appList.size() > 0) {
				WorkspaceHelper.service.submitApp(part, param);
			}

			QueryResult result = PersistenceHelper.manager.navigate(part, "document", WTDocumentWTPartLink.class,
					false);
			while (result.hasMoreElements()) {
				WTDocumentWTPartLink link = (WTDocumentWTPartLink) result.nextElement();
				PersistenceHelper.manager.delete(link);
			}

			for (int i = 0; i < docOids.size(); i++) {
				String oids = (String) docOids.get(i);
				WTDocument document = (WTDocument) rf.getReference(oids).getObject();
				WTDocumentWTPartLink link = WTDocumentWTPartLink.newWTDocumentWTPartLink(document, part);
				PersistenceHelper.manager.save(link);
			}

			EPMDocument epm = PartHelper.manager.getEPMDocument(part);
			String primary = (String) param.get("primary");
			if (epm != null && !StringUtils.isNull(primary)) {

				cFolder = CheckInOutTaskLogic.getCheckoutFolder();
				clink = WorkInProgressHelper.service.checkout(epm, cFolder, "????????? ?????? ?????? ??????");
				epm = (EPMDocument) clink.getWorkingCopy();

				EPMDocumentMaster masters = (EPMDocumentMaster) epm.getMaster();
				EPMDocumentMasterIdentity identitys = (EPMDocumentMasterIdentity) masters.getIdentificationObject();
				identitys.setName(name);
				identitys.setNumber(number);
				masters = (EPMDocumentMaster) IdentityHelper.service.changeIdentity(masters, identitys);

				// ???????????? ?????? ????????? ??????
				epm = (EPMDocument) WorkInProgressHelper.service.checkin(epm, msg);

				LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) epm, State.toState("INWORK"));

				folder = FolderTaskLogic.getFolder(location, CommonUtils.getLibrary());
				FolderHelper.service.changeFolder((FolderEntry) epm, folder);

				IBAUtils.createIBA(epm, "s", "SPEC", SPEC);
				IBAUtils.createIBA(epm, "s", "PRODUCT_NAME", name);
				IBAUtils.createIBA(epm, "s", "MAKER", MAKER);
				IBAUtils.createIBA(epm, "s", "BOM", BOM);
				IBAUtils.createIBA(epm, "s", "WEIGHT", WEIGHT);
				IBAUtils.createIBA(epm, "s", "MASTER_TYPE", itemClassName);

				ContentUtils.updatePrimary(param, epm);
			}

			map.put("reload", true);
			map.put("result", SUCCESS);
			map.put("msg", "???????????? " + MODIFY_OK);

			if (isApp) {
				map.put("url", "/Windchill/plm/approval/listApproval");
			} else {
				map.put("url", "/Windchill/plm/part/listLibraryPart");
			}

			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("reload", false);
			map.put("result", FAIL);
			map.put("msg", "????????? " + MODIFY_FAIL);
			map.put("url", "/Windchill/plm/part/modifyLibraryPart?oid=" + oid);
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> createBundlePartAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		List<ArrayList<String>> jexcels = (List<ArrayList<String>>) param.get("jexcels");
		ArrayList<String> list = new ArrayList<String>();
		// boolean isApp = appList.size() > 0;
		Transaction trs = new Transaction();
		ArrayList<String> codes = new ArrayList<String>();
		ArrayList<WTPart> plist = new ArrayList<WTPart>();
		try {
			trs.start();

			Iterator<String> it = param.keySet().iterator();

			while (it.hasNext()) {
				String key = (String) it.next();
				if (key.contains("allContent")) {
					String value = (String) param.get(key);
					list.add(value);
				}
			}

			// 3

			for (int i = 0; i < jexcels.size(); i++) {
				WTPart part = null;

				// (??????????????????), ??????, ??????, ??????, ????????????, (???????????????), ?????????, ???????????????, ??????, ??????, (??????????????????),
				// (????????????, ????????????)

				ArrayList<String> cells = (ArrayList<String>) jexcels.get(i);

				// String types = cells.get(0); // ??????
				// String division = cells.get(1); // ??????????????????
				String number = cells.get(2); // ??????
				String name = cells.get(3); // ??????
				String spec = cells.get(4); // ??????
				String maker = cells.get(5); // ?????????
				String customer = cells.get(6);
				String unit = cells.get(7); // ????????????
				String price = cells.get(8).replaceAll(",", "");
				String currency = cells.get(9);

				if (StringUtils.isNull(spec)) {
					continue;
				}

				part = WTPart.newWTPart();
				// part.setName(name);
				part.setNumber(spec);
				part.setName(name);

				// ?????? = DWG_NO
				// ?????? PART_CODE

				View view = ViewHelper.service.getView("Engineering");
				ViewHelper.assignToView(part, view);
				Folder folder = FolderTaskLogic.getFolder(PartHelper.COMMON_PART, CommonUtils.getContainer());
				FolderHelper.assignLocation((FolderEntry) part, folder);

				part = (WTPart) PersistenceHelper.manager.save(part);

				IBAUtils.createIBA(part, "s", "NAME_OF_PARTS", name);
				IBAUtils.createIBA(part, "s", "MAKER", maker);
				IBAUtils.createIBA(part, "s", "DWG_NO", spec);
				IBAUtils.createIBA(part, "s", "PART_CODE", number);
				IBAUtils.createIBA(part, "s", "STD_UNIT", unit);
				IBAUtils.createIBA(part, "i", "PRICE", price.trim().replaceAll(",", ""));
				IBAUtils.createIBA(part, "s", "CURRNAME", currency);
				IBAUtils.createIBA(part, "s", "CUSTNAME", customer);

				System.out.println("i=" + i);
				System.out.println("list.size()=" + list.size());

				plist.add(part);

				if (i > list.size() - 1) {
					continue;
				}

				if (list.get(i) != null) {
					ContentUtils.updatePartContents(list.get(i), part);
				}
			}

			for (WTPart pp : plist) {
				pp = (WTPart) PersistenceHelper.manager.refresh(pp);
				String code = ErpHelper.service.sendPartToERP(pp);
				codes.add(code);
			}

			map.put("list", codes);
			map.put("reload", true);
			map.put("result", SUCCESS);
			map.put("msg", "?????????????????? " + CREATE_OK);

			// map.put("url", "/Windchill/plm/part/createBundlePart");
			trs.commit();
			trs = null;
		} catch (

		Exception e) {
			map.put("reload", false);
			map.put("result", FAIL);
			map.put("msg", "?????????????????? " + CREATE_FAIL);
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> createUnitBomAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
//		List<ArrayList<String>> jexcels = (List<ArrayList<String>>) param.get("jexcels");
		List<ArrayList<String>> jexcels2 = (List<ArrayList<String>>) param.get("jexcels2");

		HashMap<String, Object> parents = new HashMap<String, Object>();

		ArrayList<UnitSubPart> childs = new ArrayList<UnitSubPart>();

		int totalPrice = 0;

		Transaction trs = new Transaction();
		try {
			trs.start();

			UnitBom unitBom = UnitBom.newUnitBom();

			String upartName = (String) param.get("partName");
			String upartNo = (String) param.get("partNo");
			String uspec = (String) param.get("spec");
			String uunit = (String) param.get("unit");
			String umaker = (String) param.get("maker");
			String ucustomer = (String) param.get("customer");
			String ucurrency = (String) param.get("currency");
			String uprice = (String) param.get("price");

			unitBom.setPartName(upartName);
			unitBom.setPartNo(upartNo);
			unitBom.setSpec(uspec);
			unitBom.setUnit(uunit);
			unitBom.setMaker(umaker);
			unitBom.setCustomer(ucustomer);
			unitBom.setCurrency(ucurrency);
			unitBom.setPrice(uprice);

			unitBom = (UnitBom) PersistenceHelper.manager.save(unitBom);

			parents.put("parent", unitBom);

			System.out.println("jexcels2=" + jexcels2);

			for (int i = 0; i < jexcels2.size(); i++) {

				UnitSubPart data = UnitSubPart.newUnitSubPart();
				ArrayList<String> cells = (ArrayList<String>) jexcels2.get(i);
				String lotNo = cells.get(1);

				System.out.println("not=" + lotNo);

				if (StringUtils.isNull(lotNo)) {
					continue;
				}

				String unitName = cells.get(2);
				String partNo = cells.get(3);
				String partName = cells.get(4);
				String standard = cells.get(5);
				String maker = cells.get(6);
				String customer = cells.get(7);
				String quantity = cells.get(8);
				String unit = cells.get(9);
				String price = cells.get(10);
				String currency = cells.get(11);
				String won = cells.get(12);
				String exchangeRate = cells.get(14);
				String referDrawing = cells.get(15);
				String classification = cells.get(16);
				String note = cells.get(17);

				data.setLotNo(lotNo);
				data.setUnitName(unitName);
				data.setPartNo(partNo);
				data.setPartName(partName);
				data.setStandard(standard);
				data.setMaker(maker);
				data.setCustomer(customer);
				data.setQuantity(quantity);
				data.setUnit(unit);
				data.setPrice(price);
				data.setCurrency(currency);
				data.setWon(Double.parseDouble(won.replaceAll(",", "").replace(".", "")));
				data.setExchangeRate(exchangeRate);
				data.setReferDrawing(referDrawing);
				data.setClassification(classification);
				data.setNote(note);
				data = (UnitSubPart) PersistenceHelper.manager.save(data);

				totalPrice += Integer.parseInt(won.replaceAll(",", "").replace(".", ""));

				childs.add(data);

				UnitBomPartLink link = UnitBomPartLink.newUnitBomPartLink(unitBom, data);
				PersistenceHelper.manager.save(link);
			}

			unitBom.setPrice(Integer.toString(totalPrice));
			unitBom = (UnitBom) PersistenceHelper.manager.save(unitBom);

			ErpHelper.service.sendUnitBomToERP(unitBom, childs);

			map.put("reload", true);
			map.put("result", SUCCESS);
			map.put("msg", "UNIT BOM??? " + CREATE_OK);

			map.put("url", "/Windchill/plm/part/listUnitBom");

			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("reload", false);
			map.put("result", FAIL);
			map.put("msg", "UNIT BOM " + CREATE_FAIL);
			// map.put("url", "/Windchill/plm/part/createUnitBom");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
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

				// (??????????????????), ??????, ??????, ??????, ????????????, (???????????????), ?????????, ???????????????, ??????, ??????, (??????????????????),
				// (????????????, ????????????)

				ArrayList<String> cells = (ArrayList<String>) jexcels.get(i);

				// String types = cells.get(0); // ??????
				// String division = cells.get(1); // ??????????????????
				String number = cells.get(2); // ??????
				String name = cells.get(3); // ??????
				String spec = cells.get(4); // ??????
				String maker = cells.get(5); // ?????????
				String customer = cells.get(6);
				String unit = cells.get(7); // ????????????
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
			map.put("msg", "??????????????? " + CREATE_OK);

			// map.put("url", "/Windchill/plm/part/createSpec");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("reload", false);
			map.put("result", FAIL);
			map.put("msg", "??????????????? " + CREATE_FAIL);
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
	public Map<String, Object> createCodeAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		// List<String> appList = (List<String>) param.get("appList");
		/*
		 * String name = (String) param.get("name"); String number = (String)
		 * param.get("number"); String engType = (String) param.get("engType"); String
		 * description = (String) param.get("description");
		 */
		String name = (String) param.get("name");

		ApprovalContract contract = null;

		List<String> partOids = (List<String>) param.get("partOids");
		List<String> epmOids = (List<String>) param.get("epmOids");
		List<String> libraryOids = (List<String>) param.get("libraryOids");

		// List<String> rev = (List<String>) param.get("rev");

		ReferenceFactory rf = new ReferenceFactory();

		Transaction trs = new Transaction();
		try {
			trs.start();

			contract = ApprovalContract.newApprovalContract();
			contract.setName(name);
			contract.setStartTime(new Timestamp(new Date().getTime()));
			// contract.setState(ApprovalHelper.LINE_APPROVING);
			contract.setState(WorkspaceHelper.LINE_APPROVAL_COMPLETE);

			contract = (ApprovalContract) PersistenceHelper.manager.save(contract);

			for (int i = 0; i < partOids.size(); i++) {
				WTPart part = (WTPart) rf.getReference(partOids.get(i)).getObject();
				ApprovalContractPersistableLink aLink = ApprovalContractPersistableLink
						.newApprovalContractPersistableLink(contract, part);
				PersistenceHelper.manager.save(aLink);
				// String revStr = (String) rev.get(i);
				ErpHelper.service.sendPartToERP(part, "");

			}

			for (String epmOid : epmOids) {
				EPMDocument epm = (EPMDocument) rf.getReference(epmOid).getObject();
				ApprovalContractPersistableLink aLink = ApprovalContractPersistableLink
						.newApprovalContractPersistableLink(contract, epm);
				PersistenceHelper.manager.save(aLink);
			}

			for (String libraryOid : libraryOids) {
				// EPMDocument library = (EPMDocument) rf.getReference(libraryOid).getObject();
				WTPart library = (WTPart) rf.getReference(libraryOid).getObject();
				ApprovalContractPersistableLink aLink = ApprovalContractPersistableLink
						.newApprovalContractPersistableLink(contract, library);
				PersistenceHelper.manager.save(aLink);
			}

			// ApprovalHelper.service.selfApproval(contract);

			Timestamp startTime = new Timestamp(new Date().getTime());

			ApprovalMaster master = null;
			name = WorkspaceHelper.manager.getLineName(contract);

			Ownership ownership = Ownership.newOwnership(SessionHelper.manager.getPrincipal());

			WTUser completeUser = (WTUser) SessionHelper.manager.getPrincipal();

			// ???????????????
			master = ApprovalMaster.newApprovalMaster();
			master.setName(name);
			master.setCompleteTime(startTime);
			master.setOwnership(ownership);
			master.setPersist(contract);
			master.setStartTime(startTime);
			master.setState(WorkspaceHelper.LINE_SUBMIT_COMPLETE);
			master.setCompleteUserID(completeUser.getName());
			master = (ApprovalMaster) PersistenceHelper.manager.save(master);

			// ????????? ?????? ??????..
			ApprovalLine startLine = ApprovalLine.newApprovalLine();
			startLine.setName(master.getName());
			startLine.setOwnership(ownership);
			startLine.setMaster(master);
			startLine.setReads(true);
			startLine.setSort(-50);
			startLine.setStartTime(startTime);
			startLine.setType(WorkspaceHelper.APP_LINE);
			// ?????????
			startLine.setRole(WorkspaceHelper.WORKING_SUBMIT);
			startLine.setDescription(ownership.getOwner().getFullName() + " ???????????? ????????? ?????? ???????????????.");
			startLine.setCompleteUserID(completeUser.getName());
			startLine.setState(WorkspaceHelper.LINE_SUBMIT_COMPLETE);
			startLine.setCompleteTime(startTime);

			startLine = (ApprovalLine) PersistenceHelper.manager.save(startLine);

			ApprovalLine appLine = ApprovalLine.newApprovalLine();
			appLine.setName(master.getName());
			appLine.setOwnership(ownership);
			appLine.setCompleteTime(startTime);
			appLine.setDescription("?????? ??????");
			appLine.setMaster(master);
			appLine.setType(WorkspaceHelper.APP_LINE);
			appLine.setReads(true);
			appLine.setRole(WorkspaceHelper.WORKING_APPROVAL);
			appLine.setSort(0);
			appLine.setStartTime(startTime);
			appLine.setState(WorkspaceHelper.LINE_APPROVAL_COMPLETE);
			appLine = (ApprovalLine) PersistenceHelper.manager.save(appLine);

			map.put("reload", true);
			map.put("result", SUCCESS);
			map.put("msg", "?????? " + CREATE_OK);

			map.put("url", "/Windchill/plm/part/createCode");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("reload", false);
			map.put("result", FAIL);
			map.put("msg", "?????? " + CREATE_FAIL);
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
	public Map<String, Object> approvalEplanAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		ReferenceFactory rf = new ReferenceFactory();
		ApprovalContract contract = null;
		String name = (String) param.get("name");
		List<String> libraryOids = (List<String>) param.get("libraryOids");
		List<String> docOids = (List<String>) param.get("docOids");

		Transaction trs = new Transaction();
		try {
			trs.start();

			contract = ApprovalContract.newApprovalContract();
			contract.setName(name);
			contract.setStartTime(new Timestamp(new Date().getTime()));
			contract.setState(WorkspaceHelper.LINE_APPROVING);
			contract = (ApprovalContract) PersistenceHelper.manager.save(contract);

			for (int i = 0; i < libraryOids.size(); i++) {
				String oid = (String) libraryOids.get(i);

				WTPart part = (WTPart) rf.getReference(oid).getObject();

				PersistenceServerHelper.manager.update(part);

				ApprovalContractPersistableLink aLink = ApprovalContractPersistableLink
						.newApprovalContractPersistableLink(contract, part);
				PersistenceHelper.manager.save(aLink);
			}

			for (int i = 0; i < docOids.size(); i++) {
				String oid = (String) docOids.get(i);
				WTDocument doc = (WTDocument) rf.getReference(oid).getObject();
				ApprovalContractPersistableLink aLink = ApprovalContractPersistableLink
						.newApprovalContractPersistableLink(contract, doc);
				PersistenceHelper.manager.save(aLink);
			}

			WorkspaceHelper.service.submitApp(contract, param);

			map.put("result", SUCCESS);
			map.put("reload", true);
			map.put("msg", "EPLAN ????????? ?????? ???????????????.");
			map.put("url", "/Windchill/plm/approval/listApproval");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("reload", false);
			map.put("msg", "EPLAN ?????? ?????? ??? ????????? ?????????????????????.\n????????? ??????????????? ???????????????.");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> modifyPartAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String oid = (String) param.get("oid");
		ReferenceFactory rf = new ReferenceFactory();
		String cadType = "";

		String name_of_parts = (String) param.get("NAME_OF_PARTS");
		System.out.println("zzzz" + name_of_parts);

		Transaction trs = new Transaction();
		try {
			trs.start();

			WTPart part = (WTPart) rf.getReference(oid).getObject();

			EPMDocument epm = e3ps.part.service.PartHelper.manager.getEPMDocument(part);

			if (epm != null) {
				cadType = epm.getAuthoringApplication().toString();
			}

			ContentUtils.updatePrimary(param, part);
			ContentUtils.updateSecondary(param, part);
			// if ("PROE".equals(cadType))

			if ("ACAD".equals(cadType)) {
//				ACADAttr[] acadAttrs = ACADAttr.values();

//				for (ACADAttr acadAttr : acadAttrs) {
//					String name = acadAttr.name();

//				}

			} else {
				PROEAttr[] proeAttrs = PROEAttr.values();

				for (PROEAttr proeAttr : proeAttrs) {
					String key = proeAttr.name();
					String name = (String) param.get(key);

					if (name == null)
						name = "";

					System.out.println("key :: " + key + " name::" + name);
					// IBAUtils.deleteIBA(part, key, name);

					if ("PRICE".equals(key)) {
						IBAUtils.deletesIBA(part, key, "i");
						IBAUtils.createIBA(part, "i", key, name);
					} else {
						IBAUtils.deletesIBA(part, key, "s");
						IBAUtils.createIBA(part, "s", key, name);
					}
				}
			}

			// ContentHelper.service.createContents(param);

			map.put("result", SUCCESS);
			map.put("reload", true);
			map.put("msg", "????????? ?????? ???????????????.");
			map.put("url", "/Windchill/plm/part/listProductPart");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("reload", false);
			map.put("msg", "?????? ?????? ??? ????????? ?????????????????????.\n????????? ??????????????? ???????????????.");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> addUnitBomAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> list = (List<String>) param.get("list");
		UnitBom unitBom = null;
		ReferenceFactory rf = new ReferenceFactory();
		ArrayList<String[]> data = new ArrayList<String[]>();
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (String oid : list) {
				unitBom = (UnitBom) rf.getReference(oid).getObject();
				UnitBomViewData pdata = new UnitBomViewData(unitBom);
				// context ?????? ??????
				// 0 oid, 1 number, 2 name, 3 version, 4 state, 5 modifier, 6 modifyDate
				String[] s = new String[] { pdata.oid, pdata.partNo, pdata.partName, pdata.spec, pdata.unit,
						pdata.maker, pdata.customer, pdata.currency, pdata.price };
				data.add(s);
			}

			map.put("result", SUCCESS);
			map.put("list", data);
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "?????? ?????? ??? ????????? ?????????????????????.\n????????? ??????????????? ???????????????");
			map.put("url", "/Windchill/plm/part/addPart");
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> createUnitCodeAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String name = (String) param.get("name");
		List<String> unitBomOids = (List<String>) param.get("unitBomOids");

		ReferenceFactory rf = new ReferenceFactory();
		ApprovalContract contract = null;
		Transaction trs = new Transaction();
		try {
			trs.start();

			contract = ApprovalContract.newApprovalContract();
			contract.setName(name);
			contract.setStartTime(new Timestamp(new Date().getTime()));
			// contract.setState(ApprovalHelper.LINE_APPROVING);
			contract.setState(WorkspaceHelper.LINE_APPROVAL_COMPLETE);

			contract = (ApprovalContract) PersistenceHelper.manager.save(contract);

			for (int i = 0; i < unitBomOids.size(); i++) {
				UnitBom unitBom = (UnitBom) rf.getReference(unitBomOids.get(i)).getObject();
				ApprovalContractPersistableLink aLink = ApprovalContractPersistableLink
						.newApprovalContractPersistableLink(contract, unitBom);
				PersistenceHelper.manager.save(aLink);
				ErpHelper.service.sendUnitBomToERP(unitBom);
			}

			Timestamp startTime = new Timestamp(new Date().getTime());

			ApprovalMaster master = null;
			name = WorkspaceHelper.manager.getLineName(contract);

			Ownership ownership = Ownership.newOwnership(SessionHelper.manager.getPrincipal());

			WTUser completeUser = (WTUser) SessionHelper.manager.getPrincipal();

			// ???????????????
			master = ApprovalMaster.newApprovalMaster();
			master.setName(name);
			master.setCompleteTime(startTime);
			master.setOwnership(ownership);
			master.setPersist(contract);
			master.setStartTime(startTime);
			master.setState(WorkspaceHelper.LINE_SUBMIT_COMPLETE);
			master.setCompleteUserID(completeUser.getName());
			master = (ApprovalMaster) PersistenceHelper.manager.save(master);

			// ????????? ?????? ??????..
			ApprovalLine startLine = ApprovalLine.newApprovalLine();
			startLine.setName(master.getName());
			startLine.setOwnership(ownership);
			startLine.setMaster(master);
			startLine.setReads(true);
			startLine.setSort(-50);
			startLine.setStartTime(startTime);
			startLine.setType(WorkspaceHelper.APP_LINE);
			// ?????????
			startLine.setRole(WorkspaceHelper.WORKING_SUBMIT);
			startLine.setDescription(ownership.getOwner().getFullName() + " ???????????? ????????? ?????? ???????????????.");
			startLine.setCompleteUserID(completeUser.getName());
			startLine.setState(WorkspaceHelper.LINE_SUBMIT_COMPLETE);
			startLine.setCompleteTime(startTime);

			startLine = (ApprovalLine) PersistenceHelper.manager.save(startLine);

			ApprovalLine appLine = ApprovalLine.newApprovalLine();
			appLine.setName(master.getName());
			appLine.setOwnership(ownership);
			appLine.setCompleteTime(startTime);
			appLine.setDescription("?????? ??????");
			appLine.setMaster(master);
			appLine.setType(WorkspaceHelper.APP_LINE);
			appLine.setReads(true);
			appLine.setRole(WorkspaceHelper.WORKING_APPROVAL);
			appLine.setSort(0);
			appLine.setStartTime(startTime);
			appLine.setState(WorkspaceHelper.LINE_APPROVAL_COMPLETE);
			appLine = (ApprovalLine) PersistenceHelper.manager.save(appLine);

			map.put("reload", true);
			map.put("result", SUCCESS);
			map.put("msg", "?????? " + CREATE_OK);

			map.put("url", "/Windchill/plm/part/createCode");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("reload", false);
			map.put("result", FAIL);
			map.put("msg", "?????? " + CREATE_FAIL);
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
	public Map<String, Object> reSendAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> array = (List<String>) param.get("list");
		ArrayList<UnitSubPart> list = new ArrayList<UnitSubPart>();
		Transaction trs = new Transaction();
		try {
			trs.start();
			UnitBom unitBom = null;
			for (String oid : array) {
				Persistable per = CommonUtils.getObject(oid);
				if (per instanceof UnitBom) {
					unitBom = (UnitBom) per;
				} else {
					UnitSubPart part = (UnitSubPart) per;
					list.add(part);
				}
			}

			ErpHelper.service.sendUnitBomToERP(unitBom, list);

			map.put("reload", true);
			map.put("result", SUCCESS);
			map.put("msg", "UNIT BOM??? " + CREATE_OK);

			map.put("url", "/Windchill/plm/part/listUnitBom");

			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("reload", false);
			map.put("result", FAIL);
			map.put("msg", "UNIT BOM " + CREATE_FAIL);
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}
}