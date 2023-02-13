package e3ps.loader.service;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.CommonCodeType;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.util.CommonUtils;
import wt.fc.PersistenceHelper;
import wt.pom.Transaction;
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
}
