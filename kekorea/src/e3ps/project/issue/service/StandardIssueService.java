package e3ps.project.issue.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.common.content.service.CommonContentHelper;
import e3ps.common.util.CommonUtils;
import e3ps.project.Project;
import e3ps.project.issue.Issue;
import e3ps.project.issue.IssueProjectLink;
import e3ps.project.issue.beans.IssueDTO;
import wt.content.ApplicationData;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardIssueService extends StandardManager implements IssueService {

	public static StandardIssueService newStandardIssueService() throws WTException {
		StandardIssueService instance = new StandardIssueService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(IssueDTO dto) throws Exception {
		String name = dto.getName();
		String description = dto.getDescription();
		ArrayList<Map<String, String>> addRows9 = dto.getAddRows9();
		ArrayList<String> secondarys = dto.getSecondarys();
		Transaction trs = new Transaction();
		try {
			trs.start();

			Issue issue = Issue.newIssue();
			issue.setName(name);
			issue.setDescription(description);
			issue.setOwnership(CommonUtils.sessionOwner());
			PersistenceHelper.manager.save(issue);

			for (Map<String, String> addRow9 : addRows9) {
				String poid = addRow9.get("oid");
				Project project = (Project) CommonUtils.getObject(poid);
				IssueProjectLink link = IssueProjectLink.newIssueProjectLink(issue, project);
				PersistenceHelper.manager.save(link);
			}

			for (int i = 0; secondarys != null && i < secondarys.size(); i++) {
				String cacheId = (String) secondarys.get(i);
				File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
				ApplicationData applicationData = ApplicationData.newApplicationData(issue);
				applicationData.setRole(ContentRoleType.SECONDARY);
				PersistenceHelper.manager.save(applicationData);
				ContentServerHelper.service.updateContent(issue, applicationData, vault.getPath());
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
	public void save(HashMap<String, List<IssueDTO>> dataMap) throws Exception {
		List<IssueDTO> removeRows = dataMap.get("removeRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (IssueDTO dto : removeRows) {
				String oid = dto.getOid();
				Issue issue = (Issue) CommonUtils.getObject(oid);
				QueryResult qr = PersistenceHelper.manager.navigate(issue, "project", IssueProjectLink.class, false);
				while (qr.hasMoreElements()) {
					IssueProjectLink link = (IssueProjectLink) qr.nextElement();
					PersistenceHelper.manager.delete(link);
				}
				PersistenceHelper.manager.delete(issue);
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

			Issue issue = (Issue) CommonUtils.getObject(oid);

			QueryResult qr = PersistenceHelper.manager.navigate(issue, "project", IssueProjectLink.class, false);
			while (qr.hasMoreElements()) {
				IssueProjectLink link = (IssueProjectLink) qr.nextElement();
				PersistenceHelper.manager.delete(link);
			}

			PersistenceHelper.manager.delete(issue);

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
	public void modify(IssueDTO dto) throws Exception {
		String name = dto.getName();
		String oid = dto.getOid();
		String description = dto.getDescription();
		ArrayList<Map<String, String>> addRows9 = dto.getAddRows9();
		ArrayList<String> secondarys = dto.getSecondarys();
		Transaction trs = new Transaction();
		try {
			trs.start();

			Issue issue = (Issue) CommonUtils.getObject(oid);
			issue.setName(name);
			issue.setDescription(description);
			issue.setOwnership(CommonUtils.sessionOwner());
			PersistenceHelper.manager.save(issue);

			QueryResult result = PersistenceHelper.manager.navigate(issue, "project", IssueProjectLink.class, false);
			while (result.hasMoreElements()) {
				IssueProjectLink link = (IssueProjectLink) result.nextElement();
				PersistenceHelper.manager.delete(link);
			}

			for (Map<String, String> addRow9 : addRows9) {
				String poid = addRow9.get("oid");
				Project project = (Project) CommonUtils.getObject(poid);
				IssueProjectLink link = IssueProjectLink.newIssueProjectLink(issue, project);
				PersistenceHelper.manager.save(link);
			}

			CommonContentHelper.manager.clear(issue);

			for (int i = 0; secondarys != null && i < secondarys.size(); i++) {
				String cacheId = (String) secondarys.get(i);
				File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
				ApplicationData applicationData = ApplicationData.newApplicationData(issue);
				applicationData.setRole(ContentRoleType.SECONDARY);
				PersistenceHelper.manager.save(applicationData);
				ContentServerHelper.service.updateContent(issue, applicationData, vault.getPath());
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
