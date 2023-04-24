package e3ps.epm.numberRule.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.admin.numberRuleCode.service.NumberRuleCodeHelper;
import e3ps.common.Constants;
import e3ps.common.content.service.CommonContentHelper;
import e3ps.common.util.CommonUtils;
import e3ps.epm.keDrawing.KeDrawing;
import e3ps.epm.keDrawing.dto.KeDrawingDTO;
import e3ps.epm.keDrawing.service.KeDrawingHelper;
import e3ps.epm.numberRule.NumberRule;
import e3ps.epm.numberRule.NumberRuleMaster;
import e3ps.epm.numberRule.dto.NumberRuleDTO;
import wt.content.ApplicationData;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.fc.PersistenceHelper;
import wt.ownership.Ownership;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardNumberRuleService extends StandardManager implements NumberRuleService {

	public static StandardNumberRuleService newStandardNumberRuleService() throws WTException {
		StandardNumberRuleService instance = new StandardNumberRuleService();
		instance.initialize();
		return instance;
	}

	@Override
	public void save(HashMap<String, List<NumberRuleDTO>> dataMap) throws Exception {
		List<NumberRuleDTO> addRows = dataMap.get("addRows");
		List<NumberRuleDTO> removeRows = dataMap.get("removeRows");
		List<NumberRuleDTO> editRows = dataMap.get("editRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			Ownership ownership = CommonUtils.sessionOwner();

			for (NumberRuleDTO dto : addRows) {
				int version = dto.getVersion();
				String state = dto.getState();
				String number = dto.getNumber();
				String name = dto.getName();
				String writtenDocuments = dto.getWrittenDocuments();
				String drawingCompany = dto.getDrawingCompany();
				String businessSector = dto.getBusinessSector();
				String classificationWritingDepartments = dto.getClassificationWritingDepartments();
				String size = dto.getSize();

				NumberRuleMaster master = NumberRuleMaster.newNumberRuleMaster();
				master.setOwnership(ownership);
				master.setName(name);
				master.setNumber(number);
				master.setDocument(
						NumberRuleCodeHelper.manager.getNumberRuleCode("WRITTEN_DOCUMENT", writtenDocuments));
				master.setSize(NumberRuleCodeHelper.manager.getNumberRuleCode("SIZE", size));
				master.setCompany(NumberRuleCodeHelper.manager.getNumberRuleCode("DRAWING_COMPANY", drawingCompany));
				master.setSector(NumberRuleCodeHelper.manager.getNumberRuleCode("BUSINESS_SECTOR", businessSector));
				master.setDepartment(NumberRuleCodeHelper.manager.getNumberRuleCode("CLASSIFICATION_WRITING_DEPARTMENT",
						classificationWritingDepartments));
				PersistenceHelper.manager.save(master);

				NumberRule numberRule = NumberRule.newNumberRule();
				numberRule.setLatest(true); // 최신이 필요 없을ㄷ...
				numberRule.setVersion(version);
				numberRule.setState(state);
				numberRule.setMaster(master);
				numberRule.setOwnership(ownership);
				PersistenceHelper.manager.save(numberRule);
			}

			for (NumberRuleDTO dto : removeRows) {
				String oid = dto.getOid();
				NumberRule latest = (NumberRule) CommonUtils.getObject(oid);
				NumberRuleMaster master = latest.getMaster();
				boolean isLast = NumberRuleHelper.manager.isLast(master);
				if (isLast) {
					PersistenceHelper.manager.delete(latest);
					PersistenceHelper.manager.delete(master);
				} else {
					NumberRule pre = NumberRuleHelper.manager.predecessor(latest);
					pre.setLatest(true);
					PersistenceHelper.manager.modify(pre);
					PersistenceHelper.manager.delete(latest);
				}
			}

			for (NumberRuleDTO dto : editRows) {
				String state = dto.getState();
				String number = dto.getNumber();
				String name = dto.getName();
				String writtenDocuments = dto.getWrittenDocuments();
				String drawingCompany = dto.getDrawingCompany();
				String businessSector = dto.getBusinessSector();
				String classificationWritingDepartments = dto.getClassificationWritingDepartments();
				String size = dto.getSize();
				String oid = dto.getOid();

				NumberRule numberRule = (NumberRule) CommonUtils.getObject(oid);
				NumberRuleMaster master = numberRule.getMaster();
				master.setName(name);
				master.setNumber(number);
				master.setDocument(
						NumberRuleCodeHelper.manager.getNumberRuleCode("WRITTEN_DOCUMENT", writtenDocuments));
				master.setSize(NumberRuleCodeHelper.manager.getNumberRuleCode("SIZE", size));
				master.setCompany(NumberRuleCodeHelper.manager.getNumberRuleCode("DRAWING_COMPANY", drawingCompany));
				master.setSector(NumberRuleCodeHelper.manager.getNumberRuleCode("BUSINESS_SECTOR", businessSector));
				master.setDepartment(NumberRuleCodeHelper.manager.getNumberRuleCode("CLASSIFICATION_WRITING_DEPARTMENT",
						classificationWritingDepartments));
				PersistenceHelper.manager.modify(master);

				numberRule.setState(state);
				PersistenceHelper.manager.modify(numberRule);
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
	public void revise(HashMap<String, List<NumberRuleDTO>> dataMap) throws Exception {
		List<NumberRuleDTO> addRows = dataMap.get("addRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (NumberRuleDTO dto : addRows) {
				String oid = dto.getOid();
				int next = dto.getNext();
				String note = dto.getNote();

				NumberRule pre = (NumberRule) CommonUtils.getObject(oid);
				pre.setLatest(false);
				pre = (NumberRule) PersistenceHelper.manager.modify(pre);

				NumberRule latest = NumberRule.newNumberRule();
				latest.setLatest(true);
				latest.setVersion(next);
				latest.setMaster(pre.getMaster());
				latest.setState(Constants.KeState.USE);
				latest.setOwnership(CommonUtils.sessionOwner());
				latest.setNote(note);
				PersistenceHelper.manager.save(latest);
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
