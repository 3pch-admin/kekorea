package e3ps.migrator;

import java.util.HashMap;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.project.Project;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.services.StandardManager;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.util.WTException;

public class StandardMigrationService extends StandardManager implements MigrationService {

	public static StandardMigrationService newStandardMigrationService() throws WTException {
		StandardMigrationService instance = new StandardMigrationService();
		instance.initialize();
		return instance;
	}

	@Override
	public void projectToMak(HashMap<String, Object> params) throws Exception {
		SessionContext prev = SessionContext.newContext();
		String orgMak = (String) params.get("orgMak");
		String mak = (String) params.get("mak");
		String detail = (String) params.get("detail");
		Transaction trs = new Transaction();
		try {
			trs.start();

			SessionHelper.manager.setAdministrator();

			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(Project.class, true);
			QuerySpecUtils.toEqualsAnd(query, idx, Project.class, Project.MAK, orgMak);
			QueryResult result = PersistenceHelper.manager.find(query);
			Project project = null;
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				project = (Project) obj[0];

				CommonCode makCode = null;
				CommonCode detailCode = null;

				if (!StringUtils.isNull(mak)) {
					makCode = CommonCodeHelper.manager.getCommonCode(mak, "MAK");
				}

//				if (!StringUtils.isNull(detail)) {
//					detailCode = CommonCodeHelper.manager.getCommonCode(detail, "MAK_DETAIL");
//				}
//
//				if (makCode != null && detailCode != null) {
//					System.out.println("KEK 작번 = " + project.getKekNumber() + ", 막종 = " + makCode.getName()
//							+ ", 막종상세 = " + detailCode.getName());
//					project.setMaks(makCode);
//					project.setDetail(detailCode);
//					PersistenceHelper.manager.modify(project);
//				}
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
			SessionContext.setContext(prev);
		}
	}

	@Override
	public void projectToCustomer() throws Exception {
		SessionContext prev = SessionContext.newContext();
		Transaction trs = new Transaction();
		try {
			trs.start();

			SessionHelper.manager.setAdministrator();

			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(Project.class, true);
			QueryResult result = PersistenceHelper.manager.find(query);
			Project project = null;
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				project = (Project) obj[0];

//				String customer = project.getCustomer();
//
//				if (!StringUtils.isNull(customer)) {
//					CommonCode customerCode = CommonCodeHelper.manager.getCommonCode(customer, "CUSTOMER");
//					project.setCustomers(customerCode);
//					PersistenceHelper.manager.modify(project);
//				} else {
//					System.out.println("거래처 없는 KEK 작번 = " + project.getKekNumber());
//				}
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
			SessionContext.setContext(prev);
		}
	}

	@Override
	public void projectToInstall() throws Exception {
		SessionContext prev = SessionContext.newContext();
		Transaction trs = new Transaction();
		try {
			trs.start();

			SessionHelper.manager.setAdministrator();

			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(Project.class, true);
			QueryResult result = PersistenceHelper.manager.find(query);
			Project project = null;
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				project = (Project) obj[0];

//				String install = project.getIns_location();
//
//				if (!StringUtils.isNull(install)) {
//					CommonCode installCode = CommonCodeHelper.manager.getCommonCode(install, "INSTALL");
//					project.setInstall(installCode);
//					PersistenceHelper.manager.modify(project);
//				} else {
//					System.out.println("설치장소 없는 KEK 작번 = " + project.getKekNumber());
//				}
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
			SessionContext.setContext(prev);
		}
	}

	@Override
	public void projectToProjectType() throws Exception {
		SessionContext prev = SessionContext.newContext();
		Transaction trs = new Transaction();
		try {
			trs.start();

			SessionHelper.manager.setAdministrator();

			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(Project.class, true);
			QueryResult result = PersistenceHelper.manager.find(query);
			Project project = null;
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				project = (Project) obj[0];

//				String projectType = project.getPType();
//
//				if (!StringUtils.isNull(projectType)) {
//					CommonCode projectTypeCode = CommonCodeHelper.manager.getCommonCode(projectType, "PROJECT_TYPE");
//					project.setProjectType(projectTypeCode);
//					PersistenceHelper.manager.modify(project);
//				} else {
//					System.out.println("프로젝트 타입 없는 KEK 작번 = " + project.getKekNumber());
//				}
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
			SessionContext.setContext(prev);
		}
	}
}
