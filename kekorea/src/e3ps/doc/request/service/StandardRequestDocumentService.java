package e3ps.doc.request.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.DateUtils;
import e3ps.common.util.StringUtils;
import e3ps.doc.request.RequestDocument;
import e3ps.doc.request.RequestDocumentProjectLink;
import e3ps.doc.request.dto.RequestDocumentDTO;
import e3ps.doc.service.DocumentHelper;
import e3ps.project.Project;
import e3ps.project.ProjectUserLink;
import e3ps.project.output.Output;
import e3ps.project.service.ProjectHelper;
import e3ps.project.task.Task;
import wt.clients.folder.FolderTaskLogic;
import wt.content.ApplicationData;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.fc.PersistenceHelper;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.org.OrganizationServicesHelper;
import wt.org.WTUser;
import wt.ownership.Ownership;
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
		String oid = dto.getOid();
		String name = dto.getName();
		String template = dto.getTemplate();
		String description = dto.getDescription();
		ArrayList<Map<String, String>> addRows = dto.getAddRows();
		ArrayList<Map<String, String>> _addRows_ = dto.get_addRows_();
		ArrayList<String> primarys = dto.getPrimarys();
		Transaction trs = new Transaction();
		try {
			trs.start();

			RequestDocument requestDocument = RequestDocument.newRequestDocument();
			String number = DocumentHelper.manager.getNextNumber("PJ-");
			requestDocument.setNumber(number);
			requestDocument.setName(name);
			requestDocument.setDescription(description);
			requestDocument.setOwnership(CommonUtils.sessionOwner());

			Folder folder = FolderTaskLogic.getFolder(RequestDocumentHelper.REQUEST_DOCUMENT_ROOT,
					CommonUtils.getContainer());
			FolderHelper.assignLocation((FolderEntry) requestDocument, folder);

			PersistenceHelper.manager.save(requestDocument);

			for (int i = 0; i < primarys.size(); i++) {
				String primary = (String) primarys.get(i);
				ApplicationData applicationData = ApplicationData.newApplicationData(requestDocument);
				if (i == 0) {
					applicationData.setRole(ContentRoleType.PRIMARY);
				} else {
					applicationData.setRole(ContentRoleType.SECONDARY);
				}
				PersistenceHelper.manager.save(applicationData);
				ContentServerHelper.service.updateContent(requestDocument, applicationData, primary);
			}

			if (StringUtils.isNull(oid)) {
				auiGridDataSave(requestDocument, template, addRows);
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

	private void auiGridDataSave(RequestDocument requestDocument, String template,
			ArrayList<Map<String, String>> addRows) throws Exception {
		Ownership ownership = CommonUtils.sessionOwner();
		Timestamp start = new Timestamp(new Date().getTime());
		Calendar ca = Calendar.getInstance();
		ca.setTime(start);
		ca.add(Calendar.DATE, 1);
		Timestamp end = new Timestamp(ca.getTime().getTime());
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (Map<String, String> addRow : addRows) {
				String projectType_code = addRow.get("projectType_code");
				String customer_code = addRow.get("customer_code");
				String install_code = addRow.get("install_code");
				String mak_code = addRow.get("mak_code");
				String detail_code = addRow.get("detail_code");
				String kekNumber = addRow.get("kekNumber");
				String keNumber = addRow.get("keNumber");
				String userId = addRow.get("userId");
				String customDate = addRow.get("customDate");
				String description = addRow.get("description");
				String model = addRow.get("model");
				String pdate = addRow.get("pdate");
				String machine = addRow.get("machine");
				String elec = addRow.get("elec");
				String soft = addRow.get("soft");

				CommonCode projectTypeCode = null;
				CommonCode customerCode = null;
				CommonCode installCode = null;
				CommonCode makCode = null;
				CommonCode detailCode = null;

				Project project = Project.newProject();

				if (!StringUtils.isNull(projectType_code)) {
					projectTypeCode = CommonCodeHelper.manager.getCommonCode(projectType_code, "PROJECT_TYPE");
				}
				project.setProjectType(projectTypeCode);

				if (!StringUtils.isNull(customer_code)) {
					customerCode = CommonCodeHelper.manager.getCommonCode(customer_code, "CUSTOMER");
				}
				project.setCustomer(customerCode);

				if (!StringUtils.isNull(install_code)) {
					installCode = CommonCodeHelper.manager.getCommonCode(install_code, "INSTALL");
				}
				project.setInstall(installCode);

				if (!StringUtils.isNull(mak_code)) {
					makCode = CommonCodeHelper.manager.getCommonCode(mak_code, "MAK");
				}
				project.setMak(makCode);

				if (!StringUtils.isNull(detail_code)) {
					detailCode = CommonCodeHelper.manager.getCommonCode(detail_code, "MAK_DETAIL");
				}
				project.setDetail(detailCode);
				
				project.setKekNumber(kekNumber);
				project.setKeNumber(keNumber);
				project.setUserId(userId);
				project.setCustomDate(DateUtils.convertDate(customDate));
				project.setDescription(description);
				project.setModel(model);
				project.setPDate(DateUtils.convertDate(pdate));
				project.setKekState("준비");
				project.setOwnership(ownership);
				project.setPlanStartDate(start);
				project.setPlanEndDate(end);

				project = (Project) PersistenceHelper.manager.save(project);

				WTUser machineUser = null;
				WTUser elecUser = null;
				WTUser softUser = null;

				if (!StringUtils.isNull(machine)) {
					machineUser = (WTUser) CommonUtils.getObject(machine);
					ProjectUserLink userLink = ProjectUserLink.newProjectUserLink(project, machineUser);
					userLink.setProjectUserType(CommonCodeHelper.manager.getCommonCode("MACHINE", "USER_TYPE"));
					PersistenceHelper.manager.save(userLink);
				}

				if (!StringUtils.isNull(elec)) {
					elecUser = (WTUser) CommonUtils.getObject(elec);
					ProjectUserLink userLink = ProjectUserLink.newProjectUserLink(project, elecUser);
					userLink.setProjectUserType(CommonCodeHelper.manager.getCommonCode("ELEC", "USER_TYPE"));
					PersistenceHelper.manager.save(userLink);
				}

				if (!StringUtils.isNull(soft)) {
					softUser = (WTUser) CommonUtils.getObject(soft);
					ProjectUserLink userLink = ProjectUserLink.newProjectUserLink(project, softUser);
					userLink.setProjectUserType(CommonCodeHelper.manager.getCommonCode("SOFT", "USER_TYPE"));
					PersistenceHelper.manager.save(userLink);
				}

				// 템플릿 선택여부
				if (!StringUtils.isNull(template)) {

				} else {

					// 템플릿 없을 경우..
					Task task = Task.newTask();

					task.setName("의뢰서");
					task.setDepth(1);
					task.setDescription("의뢰서 태스크");
					task.setSort(0);
					task.setProject(project);
					task.setTemplate(null);
					task.setPlanStartDate(start);
					task.setPlanEndDate(end);
					task.setStartDate(start);
					task.setEndDate(null);
					task.setDuration(1);
					task.setTaskType(CommonCodeHelper.manager.getCommonCode("NORMAL", "TASK_TYPE"));
					task.setOwnership(ownership);
					task.setProgress(0);
					task.setState("작업 중");
					task.setAllocate(0);
					task = (Task) PersistenceHelper.manager.save(task);

					Output output = Output.newOutput();
					output.setName(requestDocument.getName());
					output.setLocation(requestDocument.getLocation());
					output.setTask(task);
					output.setProject(project);
					output.setDocument(requestDocument);
					output.setOwnership(ownership);
					output = (Output) PersistenceHelper.manager.save(output);

					WTUser pm = OrganizationServicesHelper.manager.getAuthenticatedUser(ProjectHelper.PM_ID);
					ProjectUserLink pmLink = ProjectUserLink.newProjectUserLink(project, pm);
					pmLink.setProjectUserType(CommonCodeHelper.manager.getCommonCode("PM", "USER_TYPE"));
					PersistenceHelper.manager.save(pmLink);

					WTUser subPm = OrganizationServicesHelper.manager.getAuthenticatedUser(ProjectHelper.SUB_PM_ID);
					ProjectUserLink subPmLink = ProjectUserLink.newProjectUserLink(project, subPm);
					subPmLink.setProjectUserType(CommonCodeHelper.manager.getCommonCode("SUB_PM", "USER_TYPE"));
					PersistenceHelper.manager.save(subPmLink);
				}

				RequestDocumentProjectLink link = RequestDocumentProjectLink
						.newRequestDocumentProjectLink(requestDocument, project);
				PersistenceHelper.manager.save(link);
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
