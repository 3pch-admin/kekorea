package e3ps.loader.service;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.CommonCodeType;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.org.Department;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.services.StandardManager;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.util.WTException;

public class StandardLoaderService extends StandardManager implements LoaderService {

	public static StandardLoaderService newStandardLoaderService() throws WTException {
		StandardLoaderService instance = new StandardLoaderService();
		instance.initialize();
		return instance;
	}

	@Override
	public void loaderMak(String mak, String detail) throws Exception {
		SessionContext pre = SessionContext.newContext();
		Transaction trs = new Transaction();
		try {
			trs.start();

			SessionHelper.manager.setAdministrator();

			CommonCode makCode = CommonCodeHelper.manager.getCommonCode(mak, "MAK");
			CommonCode detailCode = CommonCodeHelper.manager.getCommonCode(detail, "MAK_DETAIL");

			if (makCode == null) {
				makCode = CommonCode.newCommonCode();
				makCode.setCode(mak);
				makCode.setDescription(mak);
				makCode.setCodeType(CommonCodeType.toCommonCodeType("MAK"));
				makCode.setName(mak);
				makCode.setEnable(true);
				makCode.setOwnership(CommonUtils.sessionOwner());
				makCode = (CommonCode) PersistenceHelper.manager.save(makCode);
			}

			if (detailCode == null) {
				detailCode = CommonCode.newCommonCode();
				detailCode.setCode(detail);
				detailCode.setDescription(detail);
				detailCode.setCodeType(CommonCodeType.toCommonCodeType("MAK_DETAIL"));
				detailCode.setName(detail);
				detailCode.setEnable(true);
				detailCode.setOwnership(CommonUtils.sessionOwner());
				detailCode.setParent(makCode);
				detailCode = (CommonCode) PersistenceHelper.manager.save(detailCode);
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
			SessionContext.setContext(pre);
		}
	}

	@Override
	public void loadeInstall(String customer, String install) throws Exception {
		SessionContext pre = SessionContext.newContext();
		Transaction trs = new Transaction();
		try {
			trs.start();

			SessionHelper.manager.setAdministrator();

			CommonCode customerCode = CommonCodeHelper.manager.getCommonCode(customer, "CUSTOMER");
			CommonCode installCode = CommonCodeHelper.manager.getCommonCode(install, "INSTALL");

			if (customerCode == null) {
				customerCode = CommonCode.newCommonCode();
				customerCode.setCode(customer);
				customerCode.setDescription(customer);
				customerCode.setCodeType(CommonCodeType.toCommonCodeType("CUSTOMER"));
				customerCode.setName(customer);
				customerCode.setEnable(true);
				customerCode.setOwnership(CommonUtils.sessionOwner());
				customerCode = (CommonCode) PersistenceHelper.manager.save(customerCode);
			}

			if (installCode == null) {
				installCode = CommonCode.newCommonCode();
				installCode.setCode(install);
				installCode.setDescription(install);
				installCode.setCodeType(CommonCodeType.toCommonCodeType("INSTALL"));
				installCode.setName(install);
				installCode.setEnable(true);
				installCode.setOwnership(CommonUtils.sessionOwner());
				installCode.setParent(customerCode);
				installCode = (CommonCode) PersistenceHelper.manager.save(installCode);
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
			SessionContext.setContext(pre);
		}
	}

	@Override
	public void loaderDepartment() throws Exception {
		String[] deptCodes = new String[] { "MANAGER", "MACHINE", "ELEC", "SOFT", "GUEST" };
		String[] deptNames = new String[] { "설계관리", "기계설계", "전기설계", "SW설계", "GUEST" };
		SessionContext pre = SessionContext.newContext();
		Transaction trs = new Transaction();
		try {
			trs.start();

			SessionHelper.manager.setAdministrator();

			Department root = null;
			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(Department.class, true);
			QuerySpecUtils.toEqualsAnd(query, idx, Department.class, Department.CODE, "ROOT");
			QueryResult result = PersistenceHelper.manager.find(query);
			if (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				root = (Department) obj[0];
			}

			for (int i = 0; i < deptCodes.length; i++) {
				String deptCode = deptCodes[i];
				String deptName = deptNames[i];
				Department department = Department.newDepartment();
				department.setCode(deptCode);
				department.setName(deptName);
				department.setParent(root);
				department.setSort(i);
				department.setDepth(root.getDepth() + 1);
				PersistenceHelper.manager.save(department);
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
			SessionContext.setContext(pre);
		}
	}
}
