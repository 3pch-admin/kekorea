package e3ps.doc.service;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import e3ps.common.content.service.CommonContentHelper;
import e3ps.common.util.CommonUtils;
import e3ps.doc.WTDocumentWTPartLink;
import e3ps.doc.dto.DocumentDTO;
import e3ps.project.output.Output;
import e3ps.project.output.OutputDocumentLink;
import e3ps.workspace.ApprovalContract;
import e3ps.workspace.ApprovalContractPersistableLink;
import e3ps.workspace.service.WorkspaceHelper;
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
		ArrayList<Map<String, String>> addRows = (ArrayList<Map<String, String>>) params.get("addRows"); // 결재문서
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

			for (Map<String, String> addRow : addRows) {
				String oid = addRow.get("oid"); // document oid
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

	@Override
	public void create(DocumentDTO dto) throws Exception {
		String number = dto.getNumber();
		String name = dto.getName();
		String description = dto.getDescription();
		String location = dto.getLocation();
		boolean isSelf = dto.isSelf();
		ArrayList<String> primarys = dto.getPrimarys();
		ArrayList<Map<String, String>> addRows7 = dto.getAddRows7();
		ArrayList<Map<String, String>> agreeRows = dto.getAgreeRows();
		ArrayList<Map<String, String>> approvalRows = dto.getApprovalRows();
		ArrayList<Map<String, String>> receiveRows = dto.getReceiveRows();
		Transaction trs = new Transaction();
		try {
			trs.start();

			WTDocument document = WTDocument.newWTDocument();
			document.setName(name);
			document.setNumber(number);
			document.setDescription(description);

			Folder folder = FolderTaskLogic.getFolder(location, CommonUtils.getPDMLinkProductContainer());
			FolderHelper.assignLocation((FolderEntry) document, folder);

			document = (WTDocument) PersistenceHelper.manager.save(document);

			for (int i = 0; i < primarys.size(); i++) {
				String cacheId = (String) primarys.get(i);
				File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
				ApplicationData applicationData = ApplicationData.newApplicationData(document);
				if (i == 0) {
					applicationData.setRole(ContentRoleType.PRIMARY);
				} else {
					applicationData.setRole(ContentRoleType.SECONDARY);
				}
				PersistenceHelper.manager.save(applicationData);
				ContentServerHelper.service.updateContent(document, applicationData, vault.getPath());
			}

			for (Map<String, String> addRow7 : addRows7) {
				String oid = addRow7.get("oid");
				WTPart part = (WTPart) CommonUtils.getObject(oid);
				WTDocumentWTPartLink link = WTDocumentWTPartLink.newWTDocumentWTPartLink(document, part);
				PersistenceHelper.manager.save(link);
			}

			if (isSelf) {
				WorkspaceHelper.service.self(document.getPersistInfo().getObjectIdentifier().getStringValue());
			} else {
				// 결재시작
				if (approvalRows.size() > 0) {
					WorkspaceHelper.service.register(document, agreeRows, approvalRows, receiveRows);
				}
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
	public void delete(String oid) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			WTDocument document = (WTDocument) CommonUtils.getObject(oid);

			QueryResult qr = PersistenceHelper.manager.navigate(document, "part", WTDocumentWTPartLink.class, false);
			while (qr.hasMoreElements()) {
				WTDocumentWTPartLink link = (WTDocumentWTPartLink) qr.nextElement();
				PersistenceHelper.manager.delete(link);
			}

			qr.reset();
			qr = PersistenceHelper.manager.navigate(document, "output", OutputDocumentLink.class);
			while (qr.hasMoreElements()) {
				Output output = (Output) qr.nextElement();
				PersistenceHelper.manager.delete(output);
			}

			PersistenceHelper.manager.delete(document);

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
