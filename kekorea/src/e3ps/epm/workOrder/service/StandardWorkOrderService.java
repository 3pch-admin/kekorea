package e3ps.epm.workOrder.service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;

import e3ps.common.Constants;
import e3ps.common.content.service.CommonContentHelper;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.ContentUtils;
import e3ps.epm.workOrder.WorkOrder;
import e3ps.epm.workOrder.WorkOrderDataLink;
import e3ps.epm.workOrder.WorkOrderMaster;
import e3ps.epm.workOrder.WorkOrderProjectLink;
import e3ps.epm.workOrder.dto.WorkOrderDTO;
import e3ps.project.Project;
import e3ps.workspace.service.WorkspaceHelper;
import wt.content.ApplicationData;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardWorkOrderService extends StandardManager implements WorkOrderService {

	public static StandardWorkOrderService newStandardWorkOrderService() throws WTException {
		StandardWorkOrderService instance = new StandardWorkOrderService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(WorkOrderDTO dto) throws Exception {
		String name = dto.getName();
		String description = dto.getDescription();
		ArrayList<Map<String, Object>> addRows = dto.getAddRows(); // 도면 일람표
		ArrayList<Map<String, String>> addRows9 = dto.getAddRows9();
		ArrayList<Map<String, String>> agreeRows = dto.getAgreeRows();
		ArrayList<Map<String, String>> approvalRows = dto.getApprovalRows();
		ArrayList<Map<String, String>> receiveRows = dto.getReceiveRows();
		ArrayList<String> secondarys = dto.getSecondarys();
		Transaction trs = new Transaction();
		try {
			trs.start();

			WorkOrderMaster master = WorkOrderMaster.newWorkOrderMaster();
			master.setName(name);
			master.setWorkOrderNumber(WorkOrderHelper.manager.getNextNumber("WORK-"));
			PersistenceHelper.manager.save(master);

			WorkOrder workOrder = WorkOrder.newWorkOrder();
			workOrder.setDescription(description);
			workOrder.setMaster(master);
			workOrder.setVersion(1);
			workOrder.setLatest(true);
			workOrder.setOwnership(CommonUtils.sessionOwner());
			workOrder.setState(Constants.KeState.USE);
			PersistenceHelper.manager.save(workOrder);

			for (Map<String, String> addRow9 : addRows9) {
				String oid = addRow9.get("oid");
				Project project = (Project) CommonUtils.getObject(oid);
				WorkOrderProjectLink link = WorkOrderProjectLink.newWorkOrderProjectLink(workOrder, project);
				PersistenceHelper.manager.save(link);
			}

			int sort = 0;
			ArrayList<WorkOrderDataLink> list = new ArrayList<>();
			for (int i = addRows.size() - 1; i >= 0; i--) {
				Map<String, Object> addRow = addRows.get(i);
				String oid = (String) addRow.get("oid");
				int rev = (int) addRow.get("rev");
				int lotNo = (int) addRow.get("lotNo");
				String note = (String) addRow.get("note");
				Persistable persistable = (Persistable) CommonUtils.getObject(oid);
				WorkOrderDataLink link = WorkOrderDataLink.newWorkOrderDataLink(workOrder, persistable);
				link.setSort(sort);
				link.setLotNo(lotNo);
				link.setNote(note);
				link.setRev(rev);
				PersistenceHelper.manager.save(link);
				sort++;
				list.add(link);
			}

			for (int i = 0; secondarys != null && i < secondarys.size(); i++) {
				String cacheId = (String) secondarys.get(i);
				File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
				ApplicationData applicationData = ApplicationData.newApplicationData(workOrder);
				applicationData.setRole(ContentRoleType.SECONDARY);
				PersistenceHelper.manager.save(applicationData);
				ContentServerHelper.service.updateContent(workOrder, applicationData, vault.getPath());
			}

			Workbook cover = WorkOrderHelper.manager.createWorkOrderCover(workOrder, list);
			File tempFile = ContentUtils.getTempFile(workOrder.getMaster().getName() + "_도면일람표.xlsx");
			FileOutputStream fos = new FileOutputStream(tempFile);
			cover.write(fos);

			ApplicationData data = ApplicationData.newApplicationData(workOrder);
			data.setRole(ContentRoleType.PRIMARY);
			PersistenceHelper.manager.save(data);
			ContentServerHelper.service.updateContent(workOrder, data, tempFile.getAbsolutePath());

			WorkOrderHelper.manager.postAfterAction(workOrder.getPersistInfo().getObjectIdentifier().getStringValue());

			// 결재시작
			if (approvalRows.size() > 0) {
				WorkspaceHelper.service.register(workOrder, agreeRows, approvalRows, receiveRows);
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
	public void modify(WorkOrderDTO dto) throws Exception {
		String oid = dto.getOid();
		Transaction trs = new Transaction();
		try {
			trs.start();

			WorkOrder workOrder = (WorkOrder) CommonUtils.getObject(oid);

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
	public void delete(String oid) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			WorkOrder workOrder = (WorkOrder) CommonUtils.getObject(oid);

			QueryResult result = PersistenceHelper.manager.navigate(workOrder, "project", WorkOrderProjectLink.class,
					false);
			while (result.hasMoreElements()) {
				WorkOrderProjectLink link = (WorkOrderProjectLink) result.nextElement();
				PersistenceHelper.manager.delete(link);
			}

			result.reset();
			result = PersistenceHelper.manager.navigate(workOrder, "data", WorkOrderDataLink.class, false);
			while (result.hasMoreElements()) {
				WorkOrderDataLink link = (WorkOrderDataLink) result.nextElement();
				PersistenceHelper.manager.delete(link);
			}

			PersistenceHelper.manager.delete(workOrder);
			
			// 버전 되돌려야함..

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
