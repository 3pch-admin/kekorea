
package e3ps.bom.partlist.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.bom.partlist.MasterDataLink;
import e3ps.bom.partlist.PartListData;
import e3ps.bom.partlist.PartListMaster;
import e3ps.bom.partlist.PartListMasterProjectLink;
import e3ps.bom.partlist.dto.PartListDTO;
import e3ps.common.content.service.CommonContentHelper;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.ContentUtils;
import e3ps.common.util.DateUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.doc.service.DocumentHelper;
import e3ps.project.Project;
import e3ps.project.output.DocumentOutputLink;
import e3ps.project.output.Output;
import e3ps.project.output.ProjectOutputLink;
import e3ps.project.output.TaskOutputLink;
import e3ps.project.service.ProjectHelper;
import e3ps.project.task.Task;
import e3ps.workspace.service.WorkspaceHelper;
import wt.clients.folder.FolderTaskLogic;
import wt.content.ApplicationData;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.org.WTUser;
import wt.ownership.Ownership;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;

public class StandardPartlistService extends StandardManager implements PartlistService {

	public static StandardPartlistService newStandardPartlistService() throws WTException {
		StandardPartlistService instance = new StandardPartlistService();
		instance.initialize();
		return instance;
	}

	@Override
	public void delete(String oid) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			PartListMaster master = (PartListMaster) CommonUtils.getObject(oid);

			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(MasterDataLink.class, true);
			QuerySpecUtils.toEqualsAnd(query, idx, MasterDataLink.class, "roleAObjectRef.key.id",
					master.getPersistInfo().getObjectIdentifier().getId());
			QuerySpecUtils.toOrderBy(query, idx, MasterDataLink.class, MasterDataLink.SORT, false);
			QueryResult result = PersistenceHelper.manager.find(query);
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				MasterDataLink link = (MasterDataLink) obj[0];
				PersistenceHelper.manager.delete(link);
			}

			QuerySpec _query = new QuerySpec();
			int _idx = _query.appendClassList(PartListMaster.class, true);
			int idx_link = _query.appendClassList(PartListMasterProjectLink.class, true);
			QuerySpecUtils.toInnerJoin(_query, PartListMaster.class, PartListMasterProjectLink.class,
					WTAttributeNameIfc.ID_NAME, "roleAObjectRef.key.id", _idx, idx_link);
			QuerySpecUtils.toEqualsAnd(_query, idx_link, PartListMasterProjectLink.class, "roleAObjectRef.key.id",
					master.getPersistInfo().getObjectIdentifier().getId());
			QueryResult qr = PersistenceHelper.manager.find(_query);
			while (qr.hasMoreElements()) {
				Object[] obj = (Object[]) qr.nextElement();
				PartListMasterProjectLink link = (PartListMasterProjectLink) obj[1];
				PersistenceHelper.manager.delete(link);
			}

			// 결재 이력 삭제해ㅑ할거..

			PersistenceHelper.manager.delete(master);

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
	public void create(PartListDTO dto) throws Exception {
		String name = dto.getName();
		String engType = dto.getEngType();
		String description = dto.getContent();
		ArrayList<String> secondarys = dto.getSecondarys();
		ArrayList<Map<String, Object>> _addRows = dto.get_addRows();
		ArrayList<Map<String, Object>> addRows = dto.getAddRows();
		ArrayList<Map<String, String>> agreeRows = dto.getAgreeRows();
		ArrayList<Map<String, String>> approvalRows = dto.getApprovalRows();
		ArrayList<Map<String, String>> receiveRows = dto.getReceiveRows();

		String location = "/Default/프로젝트/" + engType + "_수배표";

		Transaction trs = new Transaction();
		try {
			trs.start();

			String number = DocumentHelper.manager.getNextNumber("PP-");
			PartListMaster partListMaster = PartListMaster.newPartListMaster();
			partListMaster.setNumber(number);
			partListMaster.setName(name);
			partListMaster.setDescription(description);
			partListMaster.setOwnership(CommonUtils.sessionOwner());

			// 위치는 기계 수배표 전기 수배표로 몰빵..
			Folder folder = FolderTaskLogic.getFolder(location, CommonUtils.getContainer());
			FolderHelper.assignLocation((FolderEntry) partListMaster, folder);

			partListMaster = (PartListMaster) PersistenceHelper.manager.save(partListMaster);

			for (String secondary : secondarys) {
				ApplicationData applicationData = ApplicationData.newApplicationData(partListMaster);
				applicationData.setRole(ContentRoleType.SECONDARY);
				PersistenceHelper.manager.save(applicationData);
				ContentServerHelper.service.updateContent(partListMaster, applicationData, secondary);
			}

			for (Map<String, Object> _addRow : _addRows) {
				String oid = (String) _addRow.get("oid");
				Project project = (Project) CommonUtils.getObject(oid);
				PartListMasterProjectLink link = PartListMasterProjectLink.newPartListMasterProjectLink(partListMaster,
						project);
				PersistenceHelper.manager.save(link);
			}

			int sort = 0;
			for (Map<String, Object> addRow : addRows) {
				// 수배표 데이터..
				PartListData data = PartListData.newPartListData();

				int lotNo = (int) addRow.get("lotNo");
				String unitName = (String) addRow.get("unitName");
				String partNo = (String) addRow.get("partNo");
				String partName = (String) addRow.get("partName");
				String standard = (String) addRow.get("standard");
				String maker = (String) addRow.get("maker");
				String customer = (String) addRow.get("customer");
				int quantity = (int) addRow.get("quantity");
				String unit = (String) addRow.get("unit");
				int price = (int) addRow.get("price");
				String currency = (String) addRow.get("currency");
				int won = (int) addRow.get("won");
				Object exchangeRate = (Object) addRow.get("exchangeRate");
				String referDrawing = (String) addRow.get("referDrawing");
				String classification = (String) addRow.get("classification");
				String note = (String) addRow.get("note");

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
				data.setWon(won);

				if (exchangeRate instanceof Double) {
					double value = (double) exchangeRate;
					data.setExchangeRate((int) value);
				} else {
					data.setExchangeRate((int) exchangeRate);
				}

				data.setReferDrawing(referDrawing);
				data.setClassification(classification);
				data.setNote(note);
				data.setPartListDate(new Timestamp(new Date().getTime()));
				data.setSort(sort);
				PersistenceHelper.manager.save(data);

				MasterDataLink link = MasterDataLink.newMasterDataLink(partListMaster, data);
				link.setSort(sort);
				PersistenceHelper.manager.save(link);
				sort++;
			}

			// 결재시작
			if (approvalRows.size() > 0) {
				WorkspaceHelper.service.register(partListMaster, agreeRows, approvalRows, receiveRows);
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
	public void modify(PartListDTO dto) throws Exception {
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
