package e3ps.migrator;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import e3ps.common.db.DBCPManager;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.DateUtils;
import e3ps.common.util.JExcelUtils;
import e3ps.common.util.MessageHelper;
import e3ps.common.util.StringUtils;
import e3ps.doc.PRJDocument;
import e3ps.org.Department;
import e3ps.org.People;
import e3ps.project.Project;
import e3ps.project.Task;
import e3ps.project.TaskOutputLink;
import e3ps.project.enums.TaskStateType;
import e3ps.project.service.ProjectHelper;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import wt.epm.EPMDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTList;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.LifeCycleTemplate;
import wt.lifecycle.State;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.StandardManager;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;
import wt.vc.views.ViewReference;
import wt.vc.wip.WorkInProgressHelper;

public class StandardMigratorService extends StandardManager implements MigratorService, MessageHelper {

	private static final long serialVersionUID = 1056728660407818024L;

	public static StandardMigratorService newStandardMigratorService() throws WTException {
		StandardMigratorService instance = new StandardMigratorService();
		instance.initialize();
		return instance;
	}

	@Override
	public void assignToDepartmentByUser(String path) throws WTException {
		SessionContext prev = SessionContext.newContext();
		Department department = null;
		People user = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			File excel = new File(path);

			SessionHelper.manager.setAdministrator();

			Workbook workBook = JExcelUtils.getWorkbook(excel);
			Sheet[] sheets = workBook.getSheets();
			int rows = sheets[0].getRows();

			System.out.println("assignToDepartmentByUser 진행 = " + new Timestamp(new Date().getTime()));
			for (int i = 1; i < rows; i++) {

				Cell[] cell = sheets[0].getRow(i);

				String departmentclassname = JExcelUtils.getContent(cell, 0);
				String departmentida2a2 = JExcelUtils.getContent(cell, 1);
				String userid = JExcelUtils.getContent(cell, 2);
				String userclassname = JExcelUtils.getContent(cell, 3);
				String userida2a2 = JExcelUtils.getContent(cell, 4);

				user = (People) rf.getReference(userclassname + ":" + userida2a2).getObject();

				if (StringUtils.isNull(departmentclassname)) {
					System.out.println("변경 유저 : " + userid + "  부서 없음");
					user.setDepartment(null);
					PersistenceHelper.manager.modify(user);
				} else {
					department = (Department) rf.getReference(departmentclassname + ":" + departmentida2a2).getObject();
					System.out.println("변경 유저 : " + userid + "  부서 : " + department.getName());
					user.setDepartment(department);
					PersistenceHelper.manager.modify(user);
				}
			}

			System.out.println("assignToDepartmentByUser 종료 = " + new Timestamp(new Date().getTime()));

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
			SessionContext.setContext(prev);
		}
	}

	@Override
	public Map<String, Object> replaceLifeCycleForDoc() throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		SessionContext prev = SessionContext.newContext();
		ReferenceFactory rf = new ReferenceFactory();
		// DBConnectionManager instance = null;

		Timestamp start = new Timestamp(new Date().getTime());

		Transaction trs = new Transaction();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			trs.start();
			SessionHelper.manager.setAdministrator();
			System.out.println("문서 start == " + start);

			LifeCycleTemplate lct = LifeCycleHelper.service.getLifeCycleTemplate(MigratorHelper.LIFECYCLE_NAME,
					CommonUtils.getContainer());

			// instance = DBConnectionManager.getInstance();

			con = DBCPManager.getConnection("local");

			// con = instance.getConnection("local");

			st = con.createStatement();

			StringBuffer sql = new StringBuffer();
			// sql.append("SELECT A0.IDA2A2 FROM WTDocument A0,WTDocumentMaster A1 ");
			sql.append("SELECT A0.IDA2A2 FROM PRJDocument A0,E3PSDocumentMaster A1 ");
			sql.append(
					"WHERE (A0.statecheckoutInfo = 'c/i') AND (A0.idA3masterReference = A1.idA2A2)  AND (A0.latestiterationInfo = 1) ");
			sql.append("AND (A0.versionSortIdA2versionInfo = (SELECT MAX(versionSortIdA2versionInfo) ");
			sql.append("FROM PRJDocument WHERE A0.IDA3MASTERREFERENCE = IDA3MASTERREFERENCE)) ");
			sql.append("ORDER BY A0.modifyStampA2 DESC");

			rs = st.executeQuery(sql.toString());

			while (rs.next()) {
				String oid = (String) rs.getString(1);

				PRJDocument doc = (PRJDocument) rf.getReference("e3ps.doc.PRJDocument:" + oid).getObject();

				// if checkout
				if (WorkInProgressHelper.isCheckedOut(doc)) {
					System.out.println("체크아웃된 문서...");
					doc = (PRJDocument) WorkInProgressHelper.service.undoCheckout(doc);
				}

				if (WorkInProgressHelper.isWorkingCopy(doc)) {
					System.out.println("복사본 문서...");
					doc = (PRJDocument) WorkInProgressHelper.service.originalCopyOf(doc);
				}
				LifeCycleHelper.service.reassign((LifeCycleManaged) doc, lct.getLifeCycleTemplateReference());
				System.out.println("변경 문서 : " + doc.getNumber());
			}

			trs.commit();
			trs = null;
			Timestamp end = new Timestamp(new Date().getTime());
			System.out.println("문서 end == " + end);
		} catch (Exception e) {
			DBCPManager.freeConnection(con, st, rs);
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
			SessionContext.setContext(prev);
			DBCPManager.freeConnection(con, st, rs);
		}
		return map;
	}

	@Override
	public Map<String, Object> replaceLifeCycleForPart() throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		SessionContext prev = SessionContext.newContext();
		ReferenceFactory rf = new ReferenceFactory();
		// DBConnectionManager instance = null;

		Timestamp start = new Timestamp(new Date().getTime());

		Transaction trs = new Transaction();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			trs.start();
			SessionHelper.manager.setAdministrator();
			System.out.println("부품 start == " + start);

			LifeCycleTemplate lct = LifeCycleHelper.service.getLifeCycleTemplate(MigratorHelper.LIFECYCLE_NAME,
					CommonUtils.getContainer());

			// instance = DBConnectionManager.getInstance();
			con = DBCPManager.getConnection("local");

			// con = instance.getConnection("local");

			st = con.createStatement();

			StringBuffer sql = new StringBuffer();
			sql.append("SELECT A0.IDA2A2 FROM WTPart A0,WTPartMaster A1 ");
			sql.append(
					"WHERE (A0.statecheckoutInfo = 'c/i') AND (A0.idA3masterReference = A1.idA2A2)  AND (A0.latestiterationInfo = 1) ");
			sql.append("AND (A0.versionSortIdA2versionInfo = (SELECT MAX(versionSortIdA2versionInfo) ");
			sql.append("FROM WTPart WHERE A0.IDA3MASTERREFERENCE = IDA3MASTERREFERENCE)) ");
			sql.append("ORDER BY A0.modifyStampA2 DESC");

			rs = st.executeQuery(sql.toString());
			while (rs.next()) {
				String oid = (String) rs.getString(1);

				WTPart part = (WTPart) rf.getReference("wt.part.WTPart:" + oid).getObject();

				// if checkout
				if (WorkInProgressHelper.isCheckedOut(part)) {
					System.out.println("체크아웃된 부품...");
					part = (WTPart) WorkInProgressHelper.service.undoCheckout(part);
				}

				if (WorkInProgressHelper.isWorkingCopy(part)) {
					System.out.println("복사본 부품...");
					part = (WTPart) WorkInProgressHelper.service.originalCopyOf(part);
				}
				LifeCycleHelper.service.reassign((LifeCycleManaged) part, lct.getLifeCycleTemplateReference());

				System.out.println("변경 부품 : " + part.getNumber());
			}

			trs.commit();
			trs = null;
			Timestamp end = new Timestamp(new Date().getTime());
			System.out.println("부품 end == " + end);
		} catch (Exception e) {
			DBCPManager.freeConnection(con, st, rs);
			e.printStackTrace();
			trs.rollback();
		} finally {
			DBCPManager.freeConnection(con, st, rs);
			if (trs != null)
				trs.rollback();
			SessionContext.setContext(prev);
		}
		return map;
	}

	@Override
	public Map<String, Object> replaceLifeCycleForEpm() throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		SessionContext prev = SessionContext.newContext();
		ReferenceFactory rf = new ReferenceFactory();
		// DBConnectionManager instance = null;

		Timestamp start = new Timestamp(new Date().getTime());

		Transaction trs = new Transaction();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			trs.start();
			SessionHelper.manager.setAdministrator();
			System.out.println("도면 start == " + start);

			LifeCycleTemplate lct = LifeCycleHelper.service.getLifeCycleTemplate(MigratorHelper.LIFECYCLE_NAME,
					CommonUtils.getContainer());

			// instance = DBConnectionManager.getInstance();
			con = DBCPManager.getConnection("local");
			// con = instance.getConnection("local");

			st = con.createStatement();

			StringBuffer sql = new StringBuffer();
			sql.append("SELECT A0.IDA2A2 FROM EPMDocument A0,EPMDocumentMaster A1 ");
			sql.append(
					"WHERE (A0.statecheckoutInfo = 'c/i') AND (A0.idA3masterReference = A1.idA2A2)  AND (A0.latestiterationInfo = 1) ");
			sql.append("AND (A0.versionSortIdA2versionInfo = (SELECT MAX(versionSortIdA2versionInfo) ");
			sql.append("FROM EPMDocument WHERE A0.IDA3MASTERREFERENCE = IDA3MASTERREFERENCE)) ");
			sql.append("ORDER BY A0.modifyStampA2 DESC");

			rs = st.executeQuery(sql.toString());

			while (rs.next()) {
				String oid = (String) rs.getString(1);

				EPMDocument epm = (EPMDocument) rf.getReference("wt.epm.EPMDocument:" + oid).getObject();

				String state = epm.getLifeCycleState().toString();

				System.out.println("state==" + state);

				// if checkout
				if (!WorkInProgressHelper.isCheckedOut(epm)) {
					System.out.println("체크아웃된 도면...");
					// epm = (EPMDocument) WorkInProgressHelper.service.undoCheckout(epm);

					if (!WorkInProgressHelper.isWorkingCopy(epm)) {
						System.out.println("복사본 도면...");

						epm = (EPMDocument) PersistenceHelper.manager.refresh(epm);

						// epm = (EPMDocument) WorkInProgressHelper.service.originalCopyOf(epm);
						LifeCycleHelper.service.reassign((LifeCycleManaged) epm, lct.getLifeCycleTemplateReference());

						WTList list = new WTArrayList();
						list.add(epm);
						State ss = State.toState(state);
						LifeCycleHelper.service.reassign(list, lct.getLifeCycleTemplateReference(),
								CommonUtils.getContainer(), ss);

						// epm = (EPMDocument) PersistenceHelper.manager.refresh(epm);

						// LifeCycleHelper.service.setLifeCycleState(epm, ss);

						System.out.println("변경된 도면 : " + epm.getNumber());
					}
				}
			}

			trs.commit();
			trs = null;
			Timestamp end = new Timestamp(new Date().getTime());
			System.out.println("도면 end == " + end);
		} catch (Exception e) {
			DBCPManager.freeConnection(con, st, rs);
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
			SessionContext.setContext(prev);
			DBCPManager.freeConnection(con, st, rs);
		}
		return map;
	}

	@Override
	public Map<String, Object> replaceToView() throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		SessionContext prev = SessionContext.newContext();
		ReferenceFactory rf = new ReferenceFactory();
		// DBConnectionManager instance = null;

		Timestamp start = new Timestamp(new Date().getTime());

		Transaction trs = new Transaction();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			trs.start();
			SessionHelper.manager.setAdministrator();
			System.out.println("부품 start == " + start);

//			LifeCycleTemplate lct = LifeCycleHelper.service.getLifeCycleTemplate(MigratorHelper.LIFECYCLE_NAME,
//					CommonUtils.getContainer());

			// instance = DBConnectionManager.getInstance();
			con = DBCPManager.getConnection("local");
			// con = instance.getConnection("local");

			st = con.createStatement();

			StringBuffer sql = new StringBuffer();
			sql.append("SELECT A0.IDA2A2 FROM WTPart A0,WTPartMaster A1 ");
			sql.append(
					"WHERE (A0.statecheckoutInfo = 'c/i') AND (A0.idA3masterReference = A1.idA2A2)  AND (A0.latestiterationInfo = 1) ");
			sql.append("AND (A0.versionSortIdA2versionInfo = (SELECT MAX(versionSortIdA2versionInfo) ");
			sql.append("FROM WTPart WHERE A0.IDA3MASTERREFERENCE = IDA3MASTERREFERENCE)) ");
			sql.append("ORDER BY A0.modifyStampA2 DESC");

			rs = st.executeQuery(sql.toString());
			View view = ViewHelper.service.getView("Engineering");
			ViewReference vr = ViewReference.newViewReference(view);
			while (rs.next()) {
				String oid = (String) rs.getString(1);

				WTPart part = (WTPart) rf.getReference("wt.part.WTPart:" + oid).getObject();

				// if checkout
				if (WorkInProgressHelper.isCheckedOut(part)) {
					System.out.println("체크아웃된 부품...");
					part = (WTPart) WorkInProgressHelper.service.undoCheckout(part);
				}

				if (WorkInProgressHelper.isWorkingCopy(part)) {
					System.out.println("복사본 부품...");
					part = (WTPart) WorkInProgressHelper.service.originalCopyOf(part);
				}
				ViewHelper.service.reassignView(part.getMaster(), vr);
				System.out.println("변경 부품 : " + part.getNumber());
			}

			trs.commit();
			trs = null;
			Timestamp end = new Timestamp(new Date().getTime());
			System.out.println("부품 end == " + end);
		} catch (Exception e) {
			DBCPManager.freeConnection(con, st, rs);
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
			SessionContext.setContext(prev);
			DBCPManager.freeConnection(con, st, rs);
		}
		return map;
	}

	@Override
	public Map<String, Object> createPart() throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		SessionContext prev = SessionContext.newContext();
		ReferenceFactory rf = new ReferenceFactory();
		Timestamp start = new Timestamp(new Date().getTime());

		Transaction trs = new Transaction();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			trs.start();
			SessionHelper.manager.setAdministrator();
			System.out.println("부품생성 start == " + start);

			con = DBCPManager.getConnection("local");

			st = con.createStatement();

			StringBuffer sql = new StringBuffer();
			sql.append("SELECT A0.IDA2A2 FROM EPMDocument A0,EPMDocumentMaster A1 ");
			sql.append("WHERE (A0.statecheckoutInfo = 'c/o')");
			// sql.append("SELECT A0.IDA2A2 FROM EPMDocument A0,EPMDocumentMaster A1 ");
			// sql.append(
			// "WHERE (A0.statecheckoutInfo = 'c/i') AND (A0.idA3masterReference =
			// A1.idA2A2) AND (A0.latestiterationInfo = 1) ");
			// sql.append("AND (A0.versionSortIdA2versionInfo = (SELECT
			// MAX(versionSortIdA2versionInfo) ");
			// sql.append("FROM EPMDocument WHERE A0.IDA3MASTERREFERENCE =
			// IDA3MASTERREFERENCE)) ");
			// sql.append("ORDER BY A0.modifyStampA2 DESC");

			rs = st.executeQuery(sql.toString());

//			int count = 0;
			while (rs.next()) {
				String oid = (String) rs.getString(1);

				EPMDocument epm = (EPMDocument) rf.getReference("wt.epm.EPMDocument:" + oid).getObject();

				// if checkout
				if (WorkInProgressHelper.isCheckedOut(epm)) {
					System.out.println("체크아웃된 도면...");
					epm = (EPMDocument) WorkInProgressHelper.service.undoCheckout(epm);
				}

				if (WorkInProgressHelper.isWorkingCopy(epm)) {
					System.out.println("복사본 도면...");
					epm = (EPMDocument) WorkInProgressHelper.service.originalCopyOf(epm);
				}

				// WTPart part = EpmHelper.manager.getPart(epm);
				// if (part == null) {
				// System.out.println("생성..");
				//
				// WTPart newPart = WTPart.newWTPart();
				//
				// newPart.setNumber(epm.getNumber());
				// newPart.setName(epm.getName());
				//
				// View view = ViewHelper.service.getView("Engineering");
				// ViewHelper.assignToView(newPart, view);
				// Folder folder = FolderTaskLogic.getFolder(epm.getLocation(),
				// CommonUtils.getContainer());
				// FolderHelper.assignLocation((FolderEntry) newPart, folder);
				//
				// newPart = (WTPart) PersistenceHelper.manager.save(newPart);
				//
				// count++;
				// System.out.println("생성된 부품 : " + newPart.getNumber() + ", count=" + count);
				// }
			}

			trs.commit();
			trs = null;
			Timestamp end = new Timestamp(new Date().getTime());
			System.out.println("부품생성 end == " + end);
		} catch (Exception e) {
			DBCPManager.freeConnection(con, st, rs);
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
			SessionContext.setContext(prev);
			DBCPManager.freeConnection(con, st, rs);
		}
		return map;
	}

	@Override
	public void undoCheckout() throws WTException {
//		Map<String, Object> map = new HashMap<String, Object>();
		SessionContext prev = SessionContext.newContext();
		ReferenceFactory rf = new ReferenceFactory();

		Timestamp start = new Timestamp(new Date().getTime());

		Transaction trs = new Transaction();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			trs.start();
			SessionHelper.manager.setAdministrator();
			System.out.println("도면 start == " + start);

			// LifeCycleTemplate lct =
			// LifeCycleHelper.service.getLifeCycleTemplate(MigratorHelper.LIFECYCLE_NAME,
			// CommonUtils.getContainer());

			// instance = DBConnectionManager.getInstance();

			// con = instance.getConnection("local");
			con = DBCPManager.getConnection("local");

			st = con.createStatement();

			StringBuffer sql = new StringBuffer();
			sql.append("SELECT A0.IDA2A2 FROM EPMDocument A0,EPMDocumentMaster A1 ");
			sql.append(
					"WHERE (A0.statecheckoutInfo = 'c/o') AND (A0.idA3masterReference = A1.idA2A2)  AND (A0.latestiterationInfo = 1) ");
			sql.append("AND (A0.versionSortIdA2versionInfo = (SELECT MAX(versionSortIdA2versionInfo) ");
			sql.append("FROM EPMDocument WHERE A0.IDA3MASTERREFERENCE = IDA3MASTERREFERENCE)) ");
			sql.append("ORDER BY A0.modifyStampA2 DESC");

			rs = st.executeQuery(sql.toString());

			while (rs.next()) {
				String oid = (String) rs.getString(1);

				EPMDocument epm = (EPMDocument) rf.getReference("wt.epm.EPMDocument:" + oid).getObject();

				// if checkout
				if (WorkInProgressHelper.isCheckedOut(epm)) {
					System.out.println("체크아웃된 도면...");
					epm = (EPMDocument) WorkInProgressHelper.service.undoCheckout(epm);
				}

				if (WorkInProgressHelper.isWorkingCopy(epm)) {
					System.out.println("복사본 도면...");
					epm = (EPMDocument) WorkInProgressHelper.service.originalCopyOf(epm);
				}
				// LifeCycleHelper.service.reassign((LifeCycleManaged) epm,
				// lct.getLifeCycleTemplateReference());
				System.out.println("변경된 도면 : " + epm.getNumber());
			}

			trs.commit();
			trs = null;
			Timestamp end = new Timestamp(new Date().getTime());
			System.out.println("도면 end == " + end);
		} catch (Exception e) {
			DBCPManager.freeConnection(con, st, rs);
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
			SessionContext.setContext(prev);
			DBCPManager.freeConnection(con, st, rs);
		}
	}

	@Override
	public void setProjectInfo(HashMap<String, Object> map) throws WTException {

		String pType = (String) map.get("pType");
//		String elec = (String) map.get("elec");
////		String sw = (String) map.get("sw");
//		String machine = (String) map.get("machine");
//		String pDate = (String) map.get("pDate");
		String kekState = (String) map.get("kekState");
		String kekNumber = (String) map.get("kekNumber");

		SessionContext prev = SessionContext.newContext();

		Transaction trs = new Transaction();
		try {
			trs.start();
			SessionHelper.manager.setAdministrator();

			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(Project.class, true);
			SearchCondition sc = new SearchCondition(Project.class, Project.KEK_NUMBER, "=", kekNumber.trim());
			query.appendWhere(sc, new int[] { idx });
			query.appendAnd();

			sc = new SearchCondition(Project.class, Project.P_TYPE, "=", pType.trim());
			query.appendWhere(sc, new int[] { idx });

			QueryResult result = PersistenceHelper.manager.find(query);

			Project p = null;
			if (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				p = (Project) obj[0];

//				WTUser pm = OrganizationServicesHelper.manager.getAuthenticatedUser(ProjectUserType.PM_ID);
//				ProjectUserLink pmLink = ProjectUserLink.newProjectUserLink(p, pm);
//				pmLink.setUserType(ProjectUserType.PM.name());
//				PersistenceHelper.manager.save(pmLink);
//
//				WTUser subPm = OrganizationServicesHelper.manager.getAuthenticatedUser(ProjectUserType.SUB_PM_ID);
//				ProjectUserLink subLink = ProjectUserLink.newProjectUserLink(p, subPm);
//				subLink.setUserType(ProjectUserType.SUB_PM.name());
//				PersistenceHelper.manager.save(subLink);
//
//				if (!StringUtils.isNull(elec)) {
//					People ee = OrgHelper.manager.getUserByName(elec);
//					if (ee != null) {
//						WTUser elecUser = ee.getUser();
//						if (elecUser != null) {
//							ProjectUserLink userLink = ProjectUserLink.newProjectUserLink(p, elecUser);
//							userLink.setUserType(ProjectUserType.ELEC.name());
//							PersistenceHelper.manager.save(userLink);
//						}
//					}
//				}
//
//				if (!StringUtils.isNull(machine)) {
//					People mm = OrgHelper.manager.getUserByName(machine);
//					if (mm != null) {
//						WTUser machineUser = mm.getUser();
//						if (machineUser != null) {
//							ProjectUserLink userLink = ProjectUserLink.newProjectUserLink(p, machineUser);
//							userLink.setUserType(ProjectUserType.MACHINE.name());
//							PersistenceHelper.manager.save(userLink);
//						}
//					}
//				}
//
//				if (!StringUtils.isNull(sw)) {
//					People ss = OrgHelper.manager.getUserByName(sw);
//					if (ss != null) {
//						WTUser swUser = ss.getUser();
//						if (swUser != null) {
//							ProjectUserLink userLink = ProjectUserLink.newProjectUserLink(p, swUser);
//							userLink.setUserType(ProjectUserType.SOFT.name());
//							PersistenceHelper.manager.save(userLink);
//						}
//					}
//				}
//
//				ArrayList<Task> list = new ArrayList<Task>();
//				list = ProjectHelper.manager.getterProjectTask(p, list);
//
//				if (list.size() < 10) {
//					p.setPType("견적");
//				} else {
//					p.setPType(pType);
//				}
//				p.setPDate(DateUtils.convertDate(pDate));

				p.setKekState(kekState);

				PersistenceHelper.manager.modify(p);
			}

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
			SessionContext.setContext(prev);
		}
	}

	@Override
	public void changeStateDoc(HashMap<String, Object> map) throws WTException {
		SessionContext prev = SessionContext.newContext();
		ReferenceFactory rf = new ReferenceFactory();
		String s = (String) map.get("state");

		String ida2a2 = (String) map.get("ida2a2");
		Timestamp start = new Timestamp(new Date().getTime());

		Transaction trs = new Transaction();
		try {
			trs.start();
			SessionHelper.manager.setAdministrator();
			System.out.println("문서 start == " + start);

			PRJDocument d = (PRJDocument) rf.getReference("e3ps.doc.PRJDocument:" + ida2a2).getObject();

			// if checkout
			if (!WorkInProgressHelper.isCheckedOut(d)) {

				if (!WorkInProgressHelper.isWorkingCopy(d)) {
					State ss = State.toState(s);
					LifeCycleHelper.service.setLifeCycleState(d, ss);
				}
			}
			System.out.println("변경된 문서 : " + d.getNumber());

			trs.commit();
			trs = null;
			Timestamp end = new Timestamp(new Date().getTime());
			System.out.println("문서 end == " + end);
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
			SessionContext.setContext(prev);
		}
	}

	@Override
	public void deleteNoOutputTask(String tName) throws WTException {
		SessionContext prev = SessionContext.newContext();

		Transaction trs = new Transaction();
		try {
			trs.start();

			SessionHelper.manager.setAdministrator();

			QuerySpec query = new QuerySpec();
//			int idx = query.appendClassList(Project.class, true);

			QueryResult result = PersistenceHelper.manager.find(query);

			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				Project pro = (Project) obj[0];

				ArrayList<Task> list = new ArrayList<Task>();

				list = ProjectHelper.manager.getterProjectTask(pro, list);

				for (Task tt : list) {

					QueryResult qr = PersistenceHelper.manager.navigate(tt, "output", TaskOutputLink.class);

					if (!qr.hasMoreElements()) {

						if (tName.equals(tt.getName())) {
							System.out.println("tt=" + tt.getName());
							PersistenceHelper.manager.delete(tt);
							System.out.println("싼출물 없는 의뢰서 태스크");
						}

					}
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
			SessionContext.setContext(prev);
		}
	}

	@Override
	public void createProjectTask(String tName) throws WTException {
		SessionContext prev = SessionContext.newContext();
		Transaction trs = new Transaction();
		try {
			trs.start();

			SessionHelper.manager.setAdministrator();

			QuerySpec query = new QuerySpec();
//			int idx = query.appendClassList(Project.class, true);

			QueryResult result = PersistenceHelper.manager.find(query);

			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				Project pro = (Project) obj[0];

				ArrayList<Task> list = new ArrayList<Task>();
				list = ProjectHelper.manager.getterProjectTask(pro, list);

				boolean a = true;
				for (Task tt : list) {
					String name = tt.getName();
					if (tName.equals(name)) {
						a = false;
						break;
					}
				}
				System.out.println("aaaaaaaaaaaa" + a);

				if (a) {
					Task task = Task.newTask();
					task.setName(tName);
					task.setDepth(1);
					task.setSort(3);
					task.setTaskType("공통");
					task.setParentTask(null);
					task.setProject(pro);
					task.setAllocate(30);
					task.setDescription(tName);
					task.setDuration(1);
					task.setPlanStartDate(DateUtils.getCurrentTimestamp());

					Calendar ca = Calendar.getInstance();
					ca.setTimeInMillis(task.getPlanStartDate().getTime());
					ca.add(Calendar.DATE, 1);
					Timestamp end = new Timestamp(ca.getTime().getTime());

					task.setPlanEndDate(end);

					task = (Task) PersistenceHelper.manager.save(task);

					System.out.println("연결 = " + pro.getKekNumber());
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
			SessionContext.setContext(prev);
		}
	}

	@Override
	public void setProjectProgress() throws WTException {
		SessionContext prev = SessionContext.newContext();
		Transaction trs = new Transaction();
		try {
			trs.start();

			SessionHelper.manager.setAdministrator();

			QuerySpec query = new QuerySpec();
//			int idx = query.appendClassList(Project.class, true);

			QueryResult result = PersistenceHelper.manager.find(query);

			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				Project pro = (Project) obj[0];

				// ProjectHelper.service.commit(pro);

				int pp = ProjectHelper.manager.getKekProgress(pro);

				System.out.println("==" + pp);
				pro.setProgress(pp);
				PersistenceHelper.manager.modify(pro);
			}

			trs.commit();
			trs = null;

		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
			SessionContext.setContext(prev);
		}
	}

	@Override
	public void createSubTask() throws WTException {
		SessionContext prev = SessionContext.newContext();
		Transaction trs = new Transaction();
		try {
			trs.start();

			SessionHelper.manager.setAdministrator();

			QuerySpec query = new QuerySpec();
//			int idx = query.appendClassList(Project.class, true);

			QueryResult result = PersistenceHelper.manager.find(query);

			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				Project pro = (Project) obj[0];

				ArrayList<Task> list = new ArrayList<Task>();
				list = ProjectHelper.manager.getterProjectTask(pro, list);

				for (Task tt : list) {

					if ("기계_수배표작성".equals(tt.getName())) {

						Task newTask1 = Task.newTask();
						newTask1.setName("1차_수배");
						newTask1.setDescription("1차_수배");
						newTask1.setTaskType("기계");
						newTask1.setDepth(2);
						newTask1.setSort(0);
						newTask1.setParentTask(tt);
						newTask1.setProject(pro);
						PersistenceHelper.manager.save(newTask1);

						Task newTask2 = Task.newTask();
						newTask2.setName("2차_수배");
						newTask2.setDescription("2차_수배");
						newTask2.setTaskType("기계");
						newTask2.setDepth(2);
						newTask2.setSort(1);
						newTask2.setParentTask(tt);
						newTask2.setProject(pro);
						PersistenceHelper.manager.save(newTask2);

						System.out.println("기계 작성");
					}

					if ("전기_수배표작성".equals(tt.getName())) {

						Task newTask1 = Task.newTask();
						newTask1.setName("1차_수배");
						newTask1.setDescription("1차_수배");
						newTask1.setTaskType("전기");
						newTask1.setDepth(2);
						newTask1.setSort(0);
						newTask1.setParentTask(tt);
						newTask1.setProject(pro);
						PersistenceHelper.manager.save(newTask1);

						Task newTask2 = Task.newTask();
						newTask2.setName("2차_수배");
						newTask2.setDescription("2차_수배");
						newTask2.setTaskType("전기");
						newTask2.setDepth(2);
						newTask2.setSort(1);
						newTask2.setParentTask(tt);
						newTask2.setProject(pro);
						PersistenceHelper.manager.save(newTask2);

						System.out.println("쩐기 작성");
					}
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
			SessionContext.setContext(prev);
		}
	}

	@Override
	public void setTaskType() throws WTException {
		SessionContext prev = SessionContext.newContext();
		Transaction trs = new Transaction();
		try {
			trs.start();

			SessionHelper.manager.setAdministrator();

			QuerySpec query = new QuerySpec();
//			int idx = query.appendClassList(Project.class, true);

			QueryResult result = PersistenceHelper.manager.find(query);

			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				Project pro = (Project) obj[0];

				ArrayList<Task> list = new ArrayList<Task>();
				list = ProjectHelper.manager.getterProjectTask(pro, list);

				for (Task tt : list) {

					Task parentTask = tt.getParentTask();
					if (parentTask != null) {

						if ("전기_수배표".equals(parentTask.getName())) {
							System.out.println("전기=" + tt.getName());
							tt.setTaskType("전기");
							PersistenceHelper.manager.modify(tt);
						}

						if ("기계_수배표".equals(parentTask.getName())) {
							System.out.println("기계=" + tt.getName());
							tt.setTaskType("기계");
							PersistenceHelper.manager.modify(tt);
						}
					}

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
			SessionContext.setContext(prev);
		}
	}

	@Override
	public void setPlanStartDate() throws WTException {
		SessionContext prev = SessionContext.newContext();
		Transaction trs = new Transaction();
		try {
			trs.start();

			SessionHelper.manager.setAdministrator();

			QuerySpec query = new QuerySpec();
//			int idx = query.appendClassList(Project.class, true);

			QueryResult result = PersistenceHelper.manager.find(query);

			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				Project pro = (Project) obj[0];

				ArrayList<Task> list = new ArrayList<Task>();
				list = ProjectHelper.manager.getterProjectTask(pro, list);

				for (Task tt : list) {

					tt.setPlanStartDate(pro.getPlanStartDate());
					PersistenceHelper.manager.modify(tt);
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
			SessionContext.setContext(prev);
		}
	}

	@Override
	public void setProjectUser() throws WTException {
		SessionContext prev = SessionContext.newContext();
		Transaction trs = new Transaction();
		try {
			trs.start();

			SessionHelper.manager.setAdministrator();

			QuerySpec query = new QuerySpec();
//			int idx = query.appendClassList(Project.class, true);

			QueryResult result = PersistenceHelper.manager.find(query);

			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				Project pro = (Project) obj[0];

				ArrayList<Task> list = new ArrayList<Task>();
				list = ProjectHelper.manager.getterProjectTask(pro, list);

				for (Task tt : list) {

					tt.setPlanStartDate(pro.getPlanStartDate());
					PersistenceHelper.manager.modify(tt);
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
			SessionContext.setContext(prev);
		}
	}

	@Override
	public void setProjectGateState() throws WTException {
		SessionContext prev = SessionContext.newContext();
		Transaction trs = new Transaction();
		Timestamp start = new Timestamp(new Date().getTime());
		System.out.println("일정 start=" + start);
		try {
			trs.start();

			SessionHelper.manager.setAdministrator();

			QuerySpec query = new QuerySpec();
//			int idx = query.appendClassList(Project.class, true);

			QueryResult result = PersistenceHelper.manager.find(query);

			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				Project project = (Project) obj[0];

				System.out.println("pro=" + project.getKekNumber());

				if (!"견적".equals(project.getPType())) {
					continue;
				}

				// ProjectHelper.service.setQState(pro);

				String[] QTASK = ProjectHelper.QTASK;

				ArrayList<Task> list = new ArrayList<Task>();
				list = ProjectHelper.manager.getterProjectTask(project, list);
				for (int i = 0; list != null && i < list.size(); i++) {
					Task tt = (Task) list.get(i);
					// for (Task tt : list) {

					String tname = tt.getName();

					if (tname.equals("의뢰서")) {
						continue;
					}

					System.out.println("tname=" + tname);

					if (!tname.equals(QTASK[0]) && !tname.equals(QTASK[1]) && !tname.equals(QTASK[2])
							&& !tname.equals(QTASK[3]) && !tname.equals(QTASK[4])) {
						continue;
					}

					if (tname.equals(QTASK[0])) {
						System.out.println("gate1=" + tt.getName());
						int qstate = ProjectHelper.manager.getQState(tt);
						project.setGate1(qstate);
					}

					if (tname.trim().equals(QTASK[1])) {
						System.out.println("gate2=" + tt.getName());
						int qstate = ProjectHelper.manager.getQState(tt);
						project.setGate2(qstate);
					}

					if (tname.equals(QTASK[2])) {
						System.out.println("gate3=" + tt.getName());
						int qstate = ProjectHelper.manager.getQState(tt);
						project.setGate3(qstate);
					}

					if (tname.equals(QTASK[3])) {
						System.out.println("gate4=" + tt.getName());
						int qstate = ProjectHelper.manager.getQState(tt);
						project.setGate4(qstate);
					}

					if (tname.equals(QTASK[4])) {
						System.out.println("gate5=" + tt.getName());
						int qstate = ProjectHelper.manager.getQState(tt);
						project.setGate5(qstate);
					}

				}

				PersistenceHelper.manager.modify(project);

			}

			trs.commit();
			trs = null;

		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
			SessionContext.setContext(prev);
		}
		Timestamp end = new Timestamp(new Date().getTime());
		System.out.println("일정 end=" + end);
	}

	@Override
	public void setProjectType() throws WTException {
		SessionContext prev = SessionContext.newContext();
		Transaction trs = new Transaction();
		Timestamp start = new Timestamp(new Date().getTime());
		System.out.println("일정 start=" + start);
		try {
			trs.start();

			SessionHelper.manager.setAdministrator();

			QuerySpec query = new QuerySpec();
//			int idx = query.appendClassList(Project.class, true);

			QueryResult result = PersistenceHelper.manager.find(query);

			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				Project project = (Project) obj[0];

				ArrayList<Task> list = new ArrayList<Task>();
				list = ProjectHelper.manager.getterProjectTask(project, list);

				System.out.println("사이즈=" + list.size());

				if (list.size() < 10) {
					project.setPType("견적");
				}

				PersistenceHelper.manager.modify(project);

			}

			trs.commit();
			trs = null;

		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
			SessionContext.setContext(prev);
		}
		Timestamp end = new Timestamp(new Date().getTime());
		System.out.println("일정 end=" + end);
	}

	@Override
	public void setCompleteTask() throws WTException {
		SessionContext prev = SessionContext.newContext();
		Transaction trs = new Transaction();
		Timestamp start = new Timestamp(new Date().getTime());
		System.out.println("일정 start=" + start);
		try {
			trs.start();

			SessionHelper.manager.setAdministrator();

			QuerySpec query = new QuerySpec();
//			int idx = query.appendClassList(Project.class, true);

			QueryResult result = PersistenceHelper.manager.find(query);

			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				Project project = (Project) obj[0];

//				if (project.getKekState() != null) {

				if (!StringUtils.isNull(project.getKekState())) {

					if ("작업완료".equals(project.getKekState().trim())) {
						ArrayList<Task> list = new ArrayList<Task>();
						list = ProjectHelper.manager.getterProjectTask(project, list);
						System.out.println("작업완료 = " + project.getKekNumber());
						for (Task tt : list) {
							tt.setState(TaskStateType.COMPLETE.getDisplay());
							tt.setProgress(100);
							PersistenceHelper.manager.modify(tt);
						}
					}

					if ("설계완료".equals(project.getKekState().trim())) {
						ArrayList<Task> list = new ArrayList<Task>();
						list = ProjectHelper.manager.getterProjectTask(project, list);
						if (!StringUtils.isNull(project.getPType())) {
							if ("견적".equals(project.getPType())) {
								System.out.println("설계완료 견적 = " + project.getKekNumber());
								for (Task tt : list) {
									tt.setState(TaskStateType.COMPLETE.getDisplay());
									tt.setProgress(100);
									PersistenceHelper.manager.modify(tt);
								}
							} else {
								System.out.println("설계완료 견적아님 = " + project.getKekNumber());
								for (Task tt : list) {

									if ("일반".equals(tt.getTaskType())) {
										System.out.println("일반 태스크");
										continue;
									}

									tt.setState(TaskStateType.COMPLETE.getDisplay());
									tt.setProgress(100);
									PersistenceHelper.manager.modify(tt);
								}
							}
						}
					}

					PersistenceHelper.manager.modify(project);
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
			SessionContext.setContext(prev);
		}
		Timestamp end = new Timestamp(new Date().getTime());
		System.out.println("일정 end=" + end);
	}

	@Override
	public void setParentTaskType() throws WTException {
		SessionContext prev = SessionContext.newContext();
		Transaction trs = new Transaction();
		Timestamp start = new Timestamp(new Date().getTime());
		System.out.println("일정 start=" + start);
		try {
			trs.start();

			SessionHelper.manager.setAdministrator();

			QuerySpec query = new QuerySpec();
//			int idx = query.appendClassList(Task.class, true);

			QueryResult result = PersistenceHelper.manager.find(query);

			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				Task tt = (Task) obj[0];

				if (!StringUtils.isNull(tt.getParentTask())) {
					String pname = tt.getParentTask().getName();

					if (pname.contains("전기")) {
						System.out.println("전기 수배");
						tt.setTaskType("전기");
						tt.setAllocate(20);
						PersistenceHelper.manager.modify(tt);
					} else if (pname.contains("기계")) {
						System.out.println("기계 수배");
						tt.setTaskType("기계");
						tt.setAllocate(25);
						PersistenceHelper.manager.modify(tt);
					}
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
			SessionContext.setContext(prev);
		}
		Timestamp end = new Timestamp(new Date().getTime());
		System.out.println("일정 end=" + end);
	}

	@Override
	public void setProjectState() throws WTException {
		SessionContext prev = SessionContext.newContext();
		Transaction trs = new Transaction();
		Timestamp start = new Timestamp(new Date().getTime());
		System.out.println("일정 start=" + start);
		try {
			trs.start();
			SessionHelper.manager.setAdministrator();

			QuerySpec query = new QuerySpec();
//			int idx = query.appendClassList(Project.class, true);

			QueryResult result = PersistenceHelper.manager.find(query);

			while (result.hasMoreElements()) {

				Object[] obj = (Object[]) result.nextElement();
				Project project = (Project) obj[0];

				if ("견적".equals(project.getPType())) {
					continue;
				}

				System.out.println("pro=" + project.getKekNumber());

				int gate1 = ProjectHelper.manager.gate1StateIcon(project);
				int gate2 = ProjectHelper.manager.gate2StateIcon(project);
				int gate3 = ProjectHelper.manager.gate3StateIcon(project);
				int gate4 = ProjectHelper.manager.gate4StateIcon(project);
				int gate5 = ProjectHelper.manager.gate5StateIcon(project);

				System.out.println("gate1=" + gate1);
				System.out.println("gate2=" + gate2);
				System.out.println("gate3=" + gate3);
				System.out.println("gate4=" + gate4);
				System.out.println("gate5=" + gate5);

				project.setGate1(gate1);
				project.setGate2(gate2);
				project.setGate3(gate3);
				project.setGate4(gate4);
				project.setGate5(gate5);

				PersistenceHelper.manager.modify(project);

			}
			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
			SessionContext.setContext(prev);
		}
		Timestamp end = new Timestamp(new Date().getTime());
		System.out.println("일정 end=" + end);
	}
}