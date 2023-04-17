package e3ps.epm.workOrder.service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;

import e3ps.common.Constants;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.ContentUtils;
import e3ps.epm.keDrawing.KeDrawing;
import e3ps.epm.workOrder.WorkOrder;
import e3ps.epm.workOrder.WorkOrderDataLink;
import e3ps.epm.workOrder.WorkOrderProjectLink;
import e3ps.epm.workOrder.dto.WorkOrderDTO;
import e3ps.project.Project;
import e3ps.workspace.service.WorkspaceHelper;
import wt.content.ApplicationData;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
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
		ArrayList<Map<String, String>> addRows8 = dto.getAddRows8();
		ArrayList<Map<String, String>> agreeRows = dto.getAgreeRows();
		ArrayList<Map<String, String>> approvalRows = dto.getApprovalRows();
		ArrayList<Map<String, String>> receiveRows = dto.getReceiveRows();
		ArrayList<String> secondarys = dto.getSecondarys();
		Transaction trs = new Transaction();
		try {
			trs.start();

			WorkOrder workOrder = WorkOrder.newWorkOrder();
			workOrder.setName(name);
			workOrder.setDescription(description);
			workOrder.setNumber(WorkOrderHelper.manager.getNextNumber("WORK-"));
			workOrder.setOwnership(CommonUtils.sessionOwner());
			workOrder.setState(Constants.State.INWORK);
			PersistenceHelper.manager.save(workOrder);

			for (Map<String, String> addRow8 : addRows8) {
				String oid = addRow8.get("oid");
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
				String dataType = (String) addRow.get("dataType");
				String note = (String) addRow.get("note");
				Persistable persistable = (Persistable) CommonUtils.getObject(oid);
				if (persistable instanceof KeDrawing) {
					KeDrawing k = (KeDrawing) persistable;

				}
				WorkOrderDataLink link = WorkOrderDataLink.newWorkOrderDataLink(workOrder, persistable);
				link.setSort(sort);
				link.setDataType(dataType);
				link.setLotNo(lotNo);
				link.setNote(note);
				link.setRev(rev);
				PersistenceHelper.manager.save(link);
				sort++;
				list.add(link);
			}

			for (int i = 0; secondarys != null && i < secondarys.size(); i++) {
				String secondary = secondarys.get(i);
				ApplicationData dd = ApplicationData.newApplicationData(workOrder);
				dd.setRole(ContentRoleType.SECONDARY);
				PersistenceHelper.manager.save(dd);
				ContentServerHelper.service.updateContent(workOrder, dd, secondary);
			}

			Workbook cover = WorkOrderHelper.manager.createWorkOrderCover(workOrder, list);
			File tempFile = ContentUtils.getTempFile(workOrder.getName() + "_도면일람표.xlsx");
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
}
