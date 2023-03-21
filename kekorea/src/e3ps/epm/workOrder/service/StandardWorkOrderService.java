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
		ArrayList<Map<String, String>> _addRows = dto.get_addRows(); // 작번
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

			for (Map<String, String> _addRow : _addRows) {
				String oid = _addRow.get("oid");
				Project project = (Project) CommonUtils.getObject(oid);
				WorkOrderProjectLink link = WorkOrderProjectLink.newWorkOrderProjectLink(workOrder, project);
				PersistenceHelper.manager.save(link);
			}

			int sort = 0;
			ArrayList<WorkOrderDataLink> list = new ArrayList<>();
			// 역순...
			for (int i = addRows.size() - 1; i >= 0; i--) {
				Map<String, Object> addRow = addRows.get(i);
				String oid = (String) addRow.get("oid");
				int current = (int) addRow.get("current");
				int lotNo = (int) addRow.get("lotNo");
				String dataType = (String) addRow.get("dataType");
				String note = (String) addRow.get("note");
				Persistable persistable = (Persistable) CommonUtils.getObject(oid);
				if (persistable instanceof KeDrawing) {
					KeDrawing k = (KeDrawing) persistable;
					System.out.println("=" + k.getMaster().getName());

				}
				WorkOrderDataLink link = WorkOrderDataLink.newWorkOrderDataLink(workOrder, persistable);
				link.setSort(sort);
				link.setDataType(dataType);
				link.setLotNo(lotNo);
				link.setNote(note);
				link.setCurrent(current);
				PersistenceHelper.manager.save(link);
				sort++;
				list.add(link);
			}

			// 첨부 파일
			for (String secondary : secondarys) {
				ApplicationData dd = ApplicationData.newApplicationData(workOrder);
				dd.setRole(ContentRoleType.SECONDARY);
				PersistenceHelper.manager.save(dd);
				ContentServerHelper.service.updateContent(workOrder, dd, secondary);
			}

			// create cover workorder
			Workbook cover = WorkOrderHelper.manager.createWorkOrderCover(workOrder, list);
			File tempFile = ContentUtils.getTempFile(workOrder.getName() + "_도면일람표.xlsx");
			FileOutputStream fos = new FileOutputStream(tempFile);
			cover.write(fos);

			ApplicationData data = ApplicationData.newApplicationData(workOrder);
			data.setRole(ContentRoleType.PRIMARY);
			PersistenceHelper.manager.save(data);
			ContentServerHelper.service.updateContent(workOrder, data, tempFile.getAbsolutePath());

			WorkOrderHelper.manager.postAfterAction(workOrder.getPersistInfo().getObjectIdentifier().getStringValue());

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
