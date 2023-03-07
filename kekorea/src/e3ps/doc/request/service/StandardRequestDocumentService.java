package e3ps.doc.request.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.common.util.CommonUtils;
import e3ps.doc.request.RequestDocument;
import e3ps.doc.request.RequestDocumentProjectLink;
import e3ps.doc.request.dto.RequestDocumentDTO;
import e3ps.project.Project;
import wt.clients.folder.FolderTaskLogic;
import wt.content.ApplicationData;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.fc.PersistenceHelper;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardRequestDocumentService extends StandardManager implements RequestDocumentService {

	public static StandardRequestDocumentService newStandardRequestDocumentService() throws WTException {
		StandardRequestDocumentService instance = new StandardRequestDocumentService();
		instance.initialize();
		return instance;
	}

	@Override
	public void save(HashMap<String, List<RequestDocumentDTO>> dataMap) throws Exception {
		List<RequestDocumentDTO> removeRows = dataMap.get("removeRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (RequestDocumentDTO dto : removeRows) {
				String oid = dto.getOid();
				RequestDocument requestDocument = (RequestDocument) CommonUtils.getObject(oid);

				ArrayList<RequestDocumentProjectLink> list = RequestDocumentHelper.manager.getLinks(requestDocument);
				for (RequestDocumentProjectLink link : list) {
					PersistenceHelper.manager.delete(link);
				}

				PersistenceHelper.manager.delete(requestDocument);
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
	public void create(RequestDocumentDTO dto) throws Exception {
		String name = dto.getName();
		String description = dto.getDescription();
		ArrayList<Map<String, String>> addRows = dto.getAddRows();
		String[] secondarys = dto.getSecondarys();
		Transaction trs = new Transaction();
		try {
			trs.start();

			RequestDocument requestDocument = RequestDocument.newRequestDocument();
			requestDocument.setName(name);
			requestDocument.setDescription(description);

			Folder folder = FolderTaskLogic.getFolder(RequestDocumentHelper.REQUEST_DOCUMENT_ROOT,
					CommonUtils.getContainer());
			FolderHelper.assignLocation((FolderEntry) requestDocument, folder);

			PersistenceHelper.manager.save(requestDocument);

			for (String secondary : secondarys) {
				ApplicationData applicationData = ApplicationData.newApplicationData(requestDocument);
				applicationData.setRole(ContentRoleType.SECONDARY);
				PersistenceHelper.manager.save(applicationData);
				ContentServerHelper.service.updateContent(requestDocument, applicationData, secondary);
			}

			for (Map<String, String> addRow : addRows) {

				System.out.println(addRow);

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
