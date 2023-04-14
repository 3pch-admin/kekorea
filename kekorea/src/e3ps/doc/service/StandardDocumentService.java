package e3ps.doc.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import e3ps.common.util.CommonUtils;
import e3ps.workspace.ApprovalContract;
import e3ps.workspace.ApprovalContractPersistableLink;
import e3ps.workspace.service.WorkspaceHelper;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardDocumentService extends StandardManager implements DocumentService {

	public static StandardDocumentService newStandardDocumentService() throws WTException {
		StandardDocumentService instance = new StandardDocumentService();
		instance.initialize();
		return instance;
	}

	@Override
	public void register(Map<String, Object> params) throws Exception {
		String name = (String) params.get("name"); // 제목
		String description = (String) params.get("description"); // 의견
		ArrayList<Map<String, String>> _addRows = (ArrayList<Map<String, String>>) params.get("_addRows"); // 결재문서
		ArrayList<Map<String, String>> agreeRows = (ArrayList<Map<String, String>>) params.get("agreeRows"); // 검토
		ArrayList<Map<String, String>> approvalRows = (ArrayList<Map<String, String>>) params.get("approvalRows"); // 결재
		ArrayList<Map<String, String>> receiveRows = (ArrayList<Map<String, String>>) params.get("receiveRows"); // 수신
		Transaction trs = new Transaction();
		try {
			trs.start();

			
			ApprovalContract contract = ApprovalContract.newApprovalContract();
			contract.setName(name);
			contract.setDescription(description);
			contract.setStartTime(new Timestamp(new Date().getTime()));
			contract.setState(WorkspaceHelper.STATE_APPROVAL_APPROVING);
			contract.setContractType("DOCUMENT");
			contract = (ApprovalContract) PersistenceHelper.manager.save(contract);

			for (Map<String, String> _addRow : _addRows) {
				String oid = _addRow.get("oid"); // document oid
				WTDocument document = (WTDocument) CommonUtils.getObject(oid);
				ApprovalContractPersistableLink aLink = ApprovalContractPersistableLink
						.newApprovalContractPersistableLink(contract, document);
				PersistenceHelper.manager.save(aLink);
			}

			if (approvalRows.size() > 0) {
				WorkspaceHelper.service.register(contract, agreeRows, approvalRows, receiveRows);
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
