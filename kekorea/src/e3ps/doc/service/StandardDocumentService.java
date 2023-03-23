package e3ps.doc.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.approval.service.ApprovalHelper;
import e3ps.common.content.service.CommonContentHelper;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.ContentUtils;
import e3ps.common.util.DateUtils;
import e3ps.common.util.StringUtils;
import e3ps.doc.ReqDocumentProjectLink;
import e3ps.doc.RequestDocument;
import e3ps.doc.WTDocumentWTPartLink;
import e3ps.doc.dto.DocumentViewData;
import e3ps.project.Project;
import e3ps.project.enums.TaskStateType;
import e3ps.project.output.DocumentOutputLink;
import e3ps.project.output.Output;
import e3ps.project.output.ProjectOutputLink;
import e3ps.project.output.TaskOutputLink;
import e3ps.project.service.ProjectHelper;
import e3ps.project.task.Task;
import e3ps.workspace.ApprovalContract;
import e3ps.workspace.ApprovalContractPersistableLink;
import e3ps.workspace.ApprovalLine;
import e3ps.workspace.ApprovalMaster;
import e3ps.workspace.service.WorkspaceHelper;
import wt.clients.folder.FolderTaskLogic;
import wt.clients.vc.CheckInOutTaskLogic;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.doc.WTDocumentMasterIdentity;
import wt.fc.IdentityHelper;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.LifeCycleTemplate;
import wt.lifecycle.State;
import wt.org.WTUser;
import wt.ownership.Ownership;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.vc.Versioned;
import wt.vc.wip.CheckoutLink;
import wt.vc.wip.WorkInProgressHelper;

public class StandardDocumentService extends StandardManager implements DocumentService {

	private static final long serialVersionUID = -3547027986128094246L;

	public static StandardDocumentService newStandardDocumentService() throws WTException {
		StandardDocumentService instance = new StandardDocumentService();
		instance.initialize();
		return instance;
	}

	@Override
	public Map<String, Object> deleteDocumentAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> list = (List<String>) param.get("list");
		WTDocument document = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (String oid : list) {
				document = (WTDocument) rf.getReference(oid).getObject();

				WorkspaceHelper.service.deleteAllLine(document);

				String state = document.getLifeCycleState().toString();
				if (state.equalsIgnoreCase("APPROVED")) {
					map.put("result", FAIL);
					map.put("msg", "문서 삭제에 실패 했습니다.\n승인된 문서가 있습니다.\n문서번호 : " + document.getNumber() + ".");
					map.put("url", "/Windchill/plm/document/listDocument");
					return map;
				}

				ArrayList<WTDocumentWTPartLink> ll = DocumentHelper.manager.getWTDocumentWTPartLink(document);
				for (WTDocumentWTPartLink link : ll) {
					PersistenceHelper.manager.delete(link);
				}

				WorkspaceHelper.service.deleteAllLine(document);

				PersistenceHelper.manager.delete(document);
			}

			map.put("result", SUCCESS);
			map.put("msg", "문서가 " + DELETE_OK);
			map.put("url", "/Windchill/plm/document/listDocument");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "문서 " + DELETE_FAIL);
			map.put("url", "/Windchill/plm/document/listDocument");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> createDocumentAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		ReferenceFactory rf = new ReferenceFactory();
		WTDocument document = null;
		String name = (String) param.get("name");
		String number = (String) param.get("number");
		String descriptionDoc = (String) param.get("descriptionDoc");
		String location = (String) param.get("location");
		List<String> partOids = (List<String>) param.get("partOids");
		List<String> appList = (List<String>) param.get("appList");
		boolean self = (boolean) param.get("self");
		boolean isApp = appList.size() > 0;
		Transaction trs = new Transaction();
		try {
			trs.start();

			// String number = DocumentHelper.manager.get
			document = WTDocument.newWTDocument();
			document.setName(name);
			document.setNumber(number);
			document.setDescription(descriptionDoc);

			Folder folder = FolderTaskLogic.getFolder(location, CommonUtils.getContainer());
			FolderHelper.assignLocation((FolderEntry) document, folder);

			document = (WTDocument) PersistenceHelper.manager.save(document);

			ContentUtils.updateContents(param, document);

			if (isApp) {
				WorkspaceHelper.service.submitApp(document, param);
			}

			if (self) {
				WorkspaceHelper.service.selfApproval((Persistable) document);
			}

			for (int i = 0; partOids != null && i < partOids.size(); i++) {
				String partOid = (String) partOids.get(i);
				WTPart part = (WTPart) rf.getReference(partOid).getObject();
				WTDocumentWTPartLink link = WTDocumentWTPartLink.newWTDocumentWTPartLink(document, part);
				PersistenceHelper.manager.save(link);
			}

			// oid add
			if (!param.containsKey("oid")) {
				param.put("oid", document.getPersistInfo().getObjectIdentifier().getStringValue());
			}

			if (!param.containsKey("number")) {
				param.put("number", document.getNumber());
			}

			CommonContentHelper.service.createContents(param);

			map.put("result", SUCCESS);
			map.put("msg", "문서가 " + CREATE_OK);

			// if (isApp) {
			// map.put("url", "/Windchill/plm/approval/listApproval");
			// } else {
			map.put("url", "/Windchill/plm/document/listDocument");
			// }

			map.put("reload", true);
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "문서 " + CREATE_FAIL);
			map.put("reload", false);
			// map.put("url", "/Windchill/plm/document/createDocument");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> addDocumentAction(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> list = (List<String>) param.get("list");
		WTDocument document = null;
		ReferenceFactory rf = new ReferenceFactory();
		ArrayList<String[]> data = new ArrayList<String[]>();
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (String oid : list) {
				document = (WTDocument) rf.getReference(oid).getObject();
				DocumentViewData ddata = new DocumentViewData(document);
				// 0 oid, 1 number, 2 name, 3 version, 4 state, 5 creator, 6 createdate
				String[] s = new String[] { ddata.oid, ddata.number, ddata.name, ddata.state + "$" + ddata.stateKey,
						ddata.version + "." + ddata.iteration, ddata.modifier, ddata.modifyDate, ddata.iconPath };
				data.add(s);
			}

			map.put("result", SUCCESS);
			map.put("list", data);
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "문서 추가 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요");
			map.put("url", "/Windchill/plm/document/addDocument");
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> approvalDocumentAction(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		ReferenceFactory rf = new ReferenceFactory();
		ApprovalContract contract = null;
		String name = (String) param.get("name");
		List<String> docOids = (List<String>) param.get("docOids");

		Transaction trs = new Transaction();
		try {
			trs.start();

			contract = ApprovalContract.newApprovalContract();
			contract.setName(name);
			contract.setStartTime(new Timestamp(new Date().getTime()));
			contract.setState(WorkspaceHelper.LINE_APPROVING);
			contract = (ApprovalContract) PersistenceHelper.manager.save(contract);

			for (int i = 0; i < docOids.size(); i++) {
				String oid = (String) docOids.get(i);
				WTDocument document = (WTDocument) rf.getReference(oid).getObject();
				ApprovalContractPersistableLink aLink = ApprovalContractPersistableLink
						.newApprovalContractPersistableLink(contract, document);
				PersistenceHelper.manager.save(aLink);
			}

			WorkspaceHelper.service.submitApp(contract, param);

			map.put("reload", true);
			map.put("result", SUCCESS);
			map.put("msg", "문서 결재가 등록 되었습니다.");
			map.put("url", "/Windchill/plm/approval/listApproval");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("reload", false);
			map.put("result", FAIL);
			map.put("msg", "문서 결재 등록 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요.");
			map.put("url", "/Windchill/plm/document/approvalDocument");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> modifyDocumentAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		ReferenceFactory rf = new ReferenceFactory();
		WTDocument document = null;
		String oid = (String) param.get("oid");
		String name = (String) param.get("name");
		String descriptionDoc = (String) param.get("descriptionDoc");
		List<String> partOids = (List<String>) param.get("partOids");
		String location = (String) param.get("location");
		List<String> appList = (List<String>) param.get("appList");
		boolean isApp = appList.size() > 0;
		ApprovalMaster mm = null;
		Transaction trs = new Transaction();

		try {
			trs.start();

			document = (WTDocument) rf.getReference(oid).getObject();

			// 기존 결재 삭제
			if (isApp) {
				WorkspaceHelper.service.deleteAllLine((Persistable) document);
			} else {
				mm = WorkspaceHelper.manager.getMaster(document);
			}

			// 부품 문서 도면 
			// 버전이 관리
			// A.1 > A.2 > A.3
			// A.1 체크
			
			Folder cFolder = CheckInOutTaskLogic.getCheckoutFolder();
			CheckoutLink clink = WorkInProgressHelper.service.checkout(document, cFolder, "문서 수정 체크 아웃");
			document = (WTDocument) clink.getWorkingCopy();

			document.setDescription(descriptionDoc);

			WTDocumentMaster master = (WTDocumentMaster) document.getMaster();
			WTDocumentMasterIdentity identity = (WTDocumentMasterIdentity) master.getIdentificationObject();
			identity.setName(name);
			master = (WTDocumentMaster) IdentityHelper.service.changeIdentity(master, identity);

			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			String msg = user.getFullName() + " 사용자가 문서를 수정 하였습니다.";
			// 필요하면 수정 사유로 대체
			document = (WTDocument) WorkInProgressHelper.service.checkin(document, msg);

			document = (WTDocument) PersistenceHelper.manager.refresh(document);

			if (!isApp) {
				if (mm != null) {

					String appName = WorkspaceHelper.manager.getLineName(document);
					mm.setPersist(document);
					mm.setName(appName);

					ArrayList<ApprovalLine> al = WorkspaceHelper.manager.getAllLines(mm);
					for (ApprovalLine lines : al) {
						lines.setName(appName);
						PersistenceHelper.manager.modify(lines);
					}
					PersistenceHelper.manager.modify(mm);
				}
			}

			if (isApp) {
				LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) document, State.toState("INWORK"));
			}

			Folder folder = FolderTaskLogic.getFolder(location, CommonUtils.getContainer());
			FolderHelper.service.changeFolder((FolderEntry) document, folder);

			ContentUtils.updatePrimary(param, document);
			ContentUtils.updateSecondary(param, document);

			if (appList.size() > 0) {
				WorkspaceHelper.service.submitApp(document, param);
			}

			// 기존 링크 제거?
			QueryResult result = PersistenceHelper.manager.navigate(document, "part", WTDocumentWTPartLink.class,
					false);
			while (result.hasMoreElements()) {
				WTDocumentWTPartLink l = (WTDocumentWTPartLink) result.nextElement();
				PersistenceHelper.manager.delete(l);
			}

			for (int i = 0; partOids != null && i < partOids.size(); i++) {
				String partOid = (String) partOids.get(i);
				WTPart part = (WTPart) rf.getReference(partOid).getObject();
				WTDocumentWTPartLink link = WTDocumentWTPartLink.newWTDocumentWTPartLink(document, part);
				PersistenceHelper.manager.save(link);
			}

			// oid 제거후.. 다시
			param.remove("oid");

			if (!param.containsKey("oid")) {
				param.put("oid", document.getPersistInfo().getObjectIdentifier().getStringValue());
			}

			if (!param.containsKey("number")) {
				param.put("number", document.getNumber());
			}

			CommonContentHelper.service.createContents(param);

			map.put("reload", true);
			map.put("result", SUCCESS);
			map.put("msg", "문서가 " + MODIFY_OK);

			if (isApp) {
				map.put("url", "/Windchill/plm/approval/listApproval");
			} else {
				map.put("url", "/Windchill/plm/document/listDocument");
			}

			// map.put("url", "/Windchill/plm/document/listDocument");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("reload", false);
			map.put("result", FAIL);
			map.put("msg", "문서수정 " + MODIFY_FAIL);
			map.put("url", "/Windchill/plm/document/modifyDocument?oid=" + oid);
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> createRequestDocumentAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String name = (String) param.get("name");
		String description = (String) param.get("descriptionDoc");
		List<String> appList = (List<String>) param.get("appList");
		boolean isApp = appList.size() > 0;
		RequestDocument req = null;

		String poid = (String) param.get("poid");
		String toid = (String) param.get("toid");

		boolean isCreate = !StringUtils.isNull(poid) && !StringUtils.isNull(toid);

		ReferenceFactory rf = new ReferenceFactory();
		Project project = null;
		Task task = null;

		Transaction trs = new Transaction();
		try {
			trs.start();

			Ownership ownership = Ownership.newOwnership(SessionHelper.manager.getPrincipal());

			req = RequestDocument.newRequestDocument();

			String number = DocumentHelper.manager.getNextNumber("PJ-");
			req.setName(name);
			req.setNumber(number);
			req.setDescription(description);

			Folder folder = FolderTaskLogic.getFolder(DocumentHelper.REQUEST_ROOT, CommonUtils.getContainer());
			FolderHelper.assignLocation((FolderEntry) req, folder);

			LifeCycleTemplate lct = LifeCycleHelper.service.getLifeCycleTemplate("기초", CommonUtils.getContainer());
			LifeCycleHelper.setLifeCycle((LifeCycleManaged) req, lct);

			req = (RequestDocument) PersistenceHelper.manager.save(req);

//			ContentUtils.updatePrimary(param, req);

			ContentUtils.updateContents(param, req);

			if (!isCreate) {
				ProjectHelper.service.createProjectByJExcels(req, param);
			}

			if (isCreate) {
				project = (Project) rf.getReference(poid).getObject();
				task = (Task) rf.getReference(toid).getObject();

				Output output = Output.newOutput();
				output.setName(req.getName());
				output.setLocation(req.getLocation());
				output.setTask(task);
				output.setProject(project);
				output.setDocument(req);
				output.setOwnership(ownership);
				output = (Output) PersistenceHelper.manager.save(output);

				RequestDocumentProjectLink link = RequestDocumentProjectLink.newReqDocumentProjectLink(req, project);
				PersistenceHelper.manager.save(link);
			}

			if (isApp) {
				WorkspaceHelper.service.submitApp(req, param);
			}

			// oid add
			if (!param.containsKey("oid")) {
				param.put("oid", req.getPersistInfo().getObjectIdentifier().getStringValue());
			}

			if (!param.containsKey("number")) {
				param.put("number", req.getNumber());
			}

			CommonContentHelper.service.createContents(param);

			map.put("reload", true);
			map.put("result", SUCCESS);
			map.put("msg", "의뢰서가 " + CREATE_OK);
			map.put("popup", isCreate);
			// map.put("url", "/Windchill/plm/project/listProject");
			map.put("url", "/Windchill/plm/document/listRequestDocument");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("reload", false);
			map.put("result", FAIL);
			map.put("msg", "의뢰서 " + CREATE_FAIL);
			map.put("url", "/Windchill/plm/document/createRequestDocument");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> deleteRequestDocumentAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
//		String oid = (String) param.get("oid");
//		ReferenceFactory rf = new ReferenceFactory();
//		RequestDocument document = null;
		Transaction trs = new Transaction();
		try {
			trs.start();

			map.put("result", SUCCESS);
			map.put("msg", "의뢰서가 " + DELETE_OK);
			map.put("url", "/Windchill/plm/document/listRequestDocument");

			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "의뢰서 " + DELETE_FAIL);
			map.put("url", "/Windchill/plm/document/listRequestDocument");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> modifyRequestDocumentAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		List<ArrayList<String>> jexcels = (List<ArrayList<String>>) param.get("jexcels");
		Project project = null;
		List<String> appList = (List<String>) param.get("appList");
		boolean isApp = appList.size() > 0;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			Ownership ownership = Ownership.newOwnership(SessionHelper.manager.getPrincipal());

			String reqDescription = (String) param.get("reqDescription");
			String name = (String) param.get("name");
			String reqOid = (String) param.get("oid");

			RequestDocument reqDoc = null;

			reqDoc = (RequestDocument) rf.getReference(reqOid).getObject();

			Folder cFolder = CheckInOutTaskLogic.getCheckoutFolder();
			CheckoutLink clink = WorkInProgressHelper.service.checkout(reqDoc, cFolder, "의뢰서 수정 체크 아웃");
			reqDoc = (RequestDocument) clink.getWorkingCopy();

			reqDoc.setDescription(reqDescription);

			WTDocumentMaster master = (WTDocumentMaster) reqDoc.getMaster();
			WTDocumentMasterIdentity identity = (WTDocumentMasterIdentity) master.getIdentificationObject();
			identity.setName(name);
			master = (WTDocumentMaster) IdentityHelper.service.changeIdentity(master, identity);

			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			String msg = user.getFullName() + " 사용자가 의뢰서를 수정 하였습니다.";
			// 필요하면 수정 사유로 대체
			reqDoc = (RequestDocument) WorkInProgressHelper.service.checkin(reqDoc, msg);

//			ContentUtils.updatePrimary(param, reqDoc);

//			ContentUtils.updateContents(param, reqDoc);
			ContentUtils.updatePrimary(param, reqDoc);
			ContentUtils.updateSecondary(param, reqDoc);

			reqDoc = (RequestDocument) PersistenceHelper.manager.modify(reqDoc);

			for (int i = 0; i < jexcels.size(); i++) {
				// project = Project.newProject();

				ArrayList<String> cells = (ArrayList<String>) jexcels.get(i);
				String oid = cells.get(0);
				String ins_location = cells.get(3);
				String mak = cells.get(4);
				String keNumber = cells.get(6);
				String userId = cells.get(7);
				String customDate = cells.get(8);
				String description = cells.get(9);
				String model = cells.get(10);
				String pDate = cells.get(11);

				project = (Project) rf.getReference(oid).getObject();
				project.setIns_location(ins_location);
				project.setMak(mak);
				project.setKeNumber(keNumber);
				project.setUserId(userId);

				if (!StringUtils.isNull(customDate)) {
					project.setCustomDate(DateUtils.convertDate(customDate));
				} else {
					project.setCustomDate(DateUtils.getCurrentTimestamp());
				}

				project.setDescription(description);
				project.setModel(model);
				// project.setSystemInfo(systemInfo);
				if (!StringUtils.isNull(pDate)) {
					project.setPDate(DateUtils.convertDate(pDate));
				} else {
					project.setPDate(DateUtils.getCurrentTimestamp());
				}
				project = (Project) PersistenceHelper.manager.modify(project);

				RequestDocumentProjectLink link = RequestDocumentProjectLink.newReqDocumentProjectLink(reqDoc, project);
				PersistenceHelper.manager.save(link);

				Task task = ProjectHelper.manager.getReqTask(project);

				// 링크추가..
				Output output = Output.newOutput();
				output.setName(reqDoc.getName());
				output.setLocation(reqDoc.getLocation());
				output.setTask(task);
				output.setProject(project);
				output.setDocument(reqDoc);
				output.setOwnership(ownership);
				output = (Output) PersistenceHelper.manager.save(output);

			}

			if (isApp) {
				WorkspaceHelper.service.submitApp(reqDoc, param);
			}

			map.put("reload", true);
			map.put("result", SUCCESS);
			map.put("msg", "의뢰서가 " + MODIFY_OK);
			map.put("url", "/Windchill/plm/document/listRequestDocument");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("reload", false);
			map.put("result", FAIL);
			map.put("msg", "의뢰서 " + MODIFY_FAIL);
			map.put("url", "/Windchill/plm/document/modifyRequestDocument");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> createOutputAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		ReferenceFactory rf = new ReferenceFactory();
		WTDocument document = null;
		String name = (String) param.get("name");
		String descriptionDoc = (String) param.get("descriptionDoc");
		String location = (String) param.get("location");
		String taskProgress = (String) param.get("taskProgress");

		String toid = (String) param.get("toid");

		List<String> projectOids = (List<String>) param.get("projectOids");
		List<String> appList = (List<String>) param.get("appList");
		boolean isApp = appList.size() > 0;
		boolean isSelf = (boolean) param.get("self");
		Transaction trs = new Transaction();

		try {
			trs.start();

			Ownership ownership = Ownership.newOwnership(SessionHelper.manager.getPrincipal());

			// String number = DocumentHelper.manager.getNextNumber();
			String number = DocumentHelper.manager.getNextNumber("PJ-");
			document = WTDocument.newWTDocument();
			document.setName(name);
			document.setNumber(number);
			document.setDescription(descriptionDoc);

			Folder folder = FolderHelper.service.getFolder(location, CommonUtils.getContainer());

			FolderHelper.assignLocation((FolderEntry) document, folder);

			document = (WTDocument) PersistenceHelper.manager.save(document);

			ContentUtils.updateContents(param, document);

			if (isApp) {
				WorkspaceHelper.service.submitApp(document, param);
			}

			if (isSelf) {
				WorkspaceHelper.service.selfApproval((Persistable) document);
			}

			for (int i = 0; projectOids != null && i < projectOids.size(); i++) {
				String projectOid = (String) projectOids.get(i);
				Project project = (Project) rf.getReference(projectOid).getObject();

				Task task = ProjectHelper.manager.getProjectTaskByName(project, location);

				if (task == null && StringUtils.isNull(toid)) {
					map.put("reload", false);
					map.put("result", FAIL);
					map.put("msg", "해당 프로젝트에 선택한 폴더와 일치하는 태스크가 없습니다.");
					return map;
				}

				if (task != null) {

					Output output = Output.newOutput();
					output.setName(document.getName());
					output.setLocation(document.getLocation());
					output.setTask(task);
					output.setProject(project);
					output.setDocument(document);
					output.setOwnership(ownership);
					output = (Output) PersistenceHelper.manager.save(output);

					int o = 0;
					QueryResult result = PersistenceHelper.manager.navigate(project, "output", ProjectOutputLink.class);
					while (result.hasMoreElements()) {
//						Output oo = (Output) result.nextElement();

						if (task.getName().equals("의뢰서")) {
							continue;
						}
						o++;
					}

					if (o == 1) {

						task.setStartDate(DateUtils.getCurrentTimestamp());
						task.setState(TaskStateType.INWORK.getDisplay());
						PersistenceHelper.manager.modify(task);

					}

					if (!StringUtils.isNull(taskProgress)) {
						task.setProgress(Integer.parseInt(taskProgress));

						QueryResult qr = PersistenceHelper.manager.navigate(task, "output", TaskOutputLink.class);
						if (qr.size() == 1) {
							task.setState(TaskStateType.INWORK.getDisplay());
							task.setStartDate(DateUtils.getCurrentTimestamp());
						}
						if (Integer.parseInt(taskProgress) >= 100) {
							task.setState(TaskStateType.COMPLETE.getDisplay());
							task.setEndDate(DateUtils.getCurrentTimestamp());
						}

						task = (Task) PersistenceHelper.manager.modify(task);
					}
					ProjectHelper.service.setProgressCheck(project);
				}

				if (task == null) {

					if (!StringUtils.isNull(toid)) {

						task = (Task) rf.getReference(toid).getObject();

						Output output = Output.newOutput();
						output.setName(document.getName());
						output.setLocation(document.getLocation());
						output.setTask(task);
						output.setProject(project);
						output.setDocument(document);
						output.setOwnership(ownership);
						output = (Output) PersistenceHelper.manager.save(output);

						int o = 0;
						QueryResult result = PersistenceHelper.manager.navigate(project, "output",
								ProjectOutputLink.class);
						while (result.hasMoreElements()) {
							Output oo = (Output) result.nextElement();
							if (oo.getLocation().equals("/Default/프로젝트/의뢰서")) {
								continue;
							}
							o++;
						}

						if (o == 1) {

							task.setStartDate(DateUtils.getCurrentTimestamp());
							task.setState(TaskStateType.INWORK.getDisplay());
							PersistenceHelper.manager.modify(task);

						}

						if (!StringUtils.isNull(taskProgress)) {
							task.setProgress(Integer.parseInt(taskProgress));

							QueryResult qr = PersistenceHelper.manager.navigate(task, "output", TaskOutputLink.class);
							if (qr.size() == 1) {
								task.setState(TaskStateType.INWORK.getDisplay());
								task.setStartDate(DateUtils.getCurrentTimestamp());
							}
							if (Integer.parseInt(taskProgress) >= 100) {
								task.setState(TaskStateType.COMPLETE.getDisplay());
								task.setEndDate(DateUtils.getCurrentTimestamp());
							}

							task = (Task) PersistenceHelper.manager.modify(task);
						}
						ProjectHelper.service.setProgressCheck(project);
					}
				}
			}

			if (!param.containsKey("oid")) {
				param.put("oid", document.getPersistInfo().getObjectIdentifier().getStringValue());
			}

			if (!param.containsKey("number")) {
				param.put("number", document.getNumber());
			}

			CommonContentHelper.service.createContents(param);

			// erp sample
			// ErpHelper.service.sendOutputToERP(document);

			map.put("reload", true);
			map.put("result", SUCCESS);
			map.put("msg", "산출물이  " + CREATE_OK);
			map.put("url", "/Windchill/plm/document/listOutput");

			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("reload", false);
			map.put("result", FAIL);
			map.put("msg", "산출물 " + CREATE_FAIL);
			map.put("url", "/Windchill/plm/document/createOutput");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> modifyOutputAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		ReferenceFactory rf = new ReferenceFactory();
		WTDocument document = null;
		String oid = (String) param.get("oid");
		String name = (String) param.get("name");
		String descriptionDoc = (String) param.get("descriptionDoc");
		List<String> projectOids = (List<String>) param.get("projectOids");
		String location = (String) param.get("location");
		List<String> appList = (List<String>) param.get("appList");
		boolean isApp = appList.size() > 0;
		ApprovalMaster mm = null;
		Transaction trs = new Transaction();
		try {
			trs.start();
			Ownership ownership = Ownership.newOwnership(SessionHelper.manager.getPrincipal());
			document = (WTDocument) rf.getReference(oid).getObject();

			if (isApp) {
				WorkspaceHelper.service.deleteAllLine((Persistable) document);
			} else {
				mm = WorkspaceHelper.manager.getMaster(document);
			}

			Folder cFolder = CheckInOutTaskLogic.getCheckoutFolder();
			CheckoutLink clink = WorkInProgressHelper.service.checkout(document, cFolder, "문서 수정 체크 아웃");
			document = (WTDocument) clink.getWorkingCopy();

			document.setDescription(descriptionDoc);

			WTDocumentMaster master = (WTDocumentMaster) document.getMaster();
			WTDocumentMasterIdentity identity = (WTDocumentMasterIdentity) master.getIdentificationObject();
			identity.setName(name);
			master = (WTDocumentMaster) IdentityHelper.service.changeIdentity(master, identity);

			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			String msg = user.getFullName() + " 사용자가 문서를 수정 하였습니다.";
			// 필요하면 수정 사유로 대체
			document = (WTDocument) WorkInProgressHelper.service.checkin(document, msg);

			document = (WTDocument) PersistenceHelper.manager.refresh(document);

			if (!isApp) {
				if (mm != null) {
					mm.setPersist(document);

					String appName = WorkspaceHelper.manager.getLineName(document);
					mm.setPersist(document);
					mm.setName(appName);

					ArrayList<ApprovalLine> al = WorkspaceHelper.manager.getAllLines(mm);
					for (ApprovalLine lines : al) {
						lines.setName(appName);
						PersistenceHelper.manager.modify(lines);
					}
					PersistenceHelper.manager.modify(mm);
				}
			}

			// 0.0 > 0.1

//			LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) document, State.toState("INWORK"));

			Folder folder = FolderTaskLogic.getFolder(location, CommonUtils.getContainer());
			FolderHelper.service.changeFolder((FolderEntry) document, folder);

			ContentUtils.updatePrimary(param, document);
			ContentUtils.updateSecondary(param, document);

			if (appList.size() > 0) {
				WorkspaceHelper.service.submitApp(document, param);
			}

			for (int i = 0; projectOids != null && i < projectOids.size(); i++) {
				String projectOid = (String) projectOids.get(i);
				Project project = (Project) rf.getReference(projectOid).getObject();

				String reLoc = location.substring(3);

				Task task = ProjectHelper.manager.getProjectTaskByName(project, reLoc);

				if (task == null) {
					map.put("reload", false);
					map.put("result", FAIL);
					map.put("msg", "해당 프로젝트에 선택한 폴더와 일치하는 태스크가 없습니다.");
					return map;
				}

				if (task != null) {
					Output output = Output.newOutput();
					output.setName(document.getName());
					output.setLocation(document.getLocation());
					output.setTask(task);
					output.setProject(project);
					output.setDocument(document);
					output.setOwnership(ownership);
					output = (Output) PersistenceHelper.manager.save(output);
				}
			}

			map.put("reload", true);
			map.put("result", SUCCESS);
			map.put("msg", "문서가 " + MODIFY_OK);

			if (isApp) {
				map.put("url", "/Windchill/plm/approval/listApproval");
			} else {
				map.put("url", "/Windchill/plm/document/listOutput");
			}

			// map.put("url", "/Windchill/plm/document/listDocument");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("reload", false);
			map.put("result", FAIL);
			map.put("msg", "문서수정 " + MODIFY_FAIL);
			map.put("url", "/Windchill/plm/document/modifyOutput?oid=" + oid);
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public void reviseOutput(Map<String, Object> param, Versioned versioned) throws WTException {
//		Map<String, Object> map = new HashMap<String, Object>();
		ReferenceFactory rf = new ReferenceFactory();
		WTDocument document = null;
		String oid = (String) param.get("oid");
//		List<String> projectOids = (List<String>) param.get("projectOids");
		String location = (String) param.get("location");

		Transaction trs = new Transaction();
		try {
			trs.start();
			// WTDocument document = (WTDocument)versioned;
			document = (WTDocument) rf.getReference(oid).getObject();
			Ownership ownership = Ownership.newOwnership(SessionHelper.manager.getPrincipal());

			ArrayList<Project> list = new ArrayList<Project>();

			// QueryResult result = PersistenceHelper.manager.navigate(task, "output",
			// TaskOutputLink.class);
			QueryResult result = PersistenceHelper.manager.navigate(document, "output", DocumentOutputLink.class);
			while (result.hasMoreElements()) {

				Output output = (Output) result.nextElement();

				QueryResult qr = PersistenceHelper.manager.navigate(output, "project", ProjectOutputLink.class, true);
				if (qr.hasMoreElements()) {
					Project link = (Project) qr.nextElement();
					list.add(link);
				}
			}
			document = (WTDocument) CommonUtils.getLatestVersion(oid);

			for (Project project : list) {

				// for (int i = 0; projectOids != null && i < projectOids.size(); i++) {
				// String projectOid = (String) projectOids.get(i);
				// Project project = (Project) rf.getReference(projectOid).getObject();

				Task task = ProjectHelper.manager.getProjectTaskByName(project, location);

				// if (task == null) {
				// map.put("reload", false);
				// map.put("result", FAIL);
				// map.put("msg", "해당 프로젝트에 선택한 폴더와 일치하는 태스크가 없습니다.");
				// return map;
				// }

				if (task != null) {
					Output output = Output.newOutput();
					output.setName(document.getName());
					output.setLocation(document.getLocation());
					output.setTask(task);
					output.setProject(project);
					output.setDocument(document);
					output.setOwnership(ownership);
					output = (Output) PersistenceHelper.manager.save(output);
				}
			}

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public void register(Map<String, Object> params) throws Exception {
		String name = (String)params.get("name");
		ArrayList<Map<String, String>> _addRows =(ArrayList<Map<String, String>>)params.get("_addRows");
		ArrayList<Map<String, String>> _addRows_ =(ArrayList<Map<String, String>>)params.get("_addRows_");
		Transaction trs = new Transaction();
		try {
			trs.start();
			
			ApprovalContract contract = ApprovalContract.newApprovalContract();
			contract.setName(name);
			contract.setStartTime(new Timestamp(new Date().getTime()));
			contract.setState(WorkspaceHelper.APPROVAL_APPROVING);
			contract = (ApprovalContract) PersistenceHelper.manager.save(contract);

			for(Map<String, String> _addRow : _addRows) {
				String oid = _addRow.get("oid");
				WTDocument document = (WTDocument)CommonUtils.getObject(oid)
				ApprovalContractPersistableLink aLink = ApprovalContractPersistableLink
						.newApprovalContractPersistableLink(contract, document);
				PersistenceHelper.manager.save(aLink);
			}


			WorkspaceHelper.service.register(contract, _addRows_);
			
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
