package e3ps.admin.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.admin.LoginHistory;
import e3ps.admin.ParentChildLink;
import e3ps.admin.PasswordSetting;
import e3ps.common.code.CommonCode;
import e3ps.common.code.CommonCodeType;
import e3ps.common.util.MessageHelper;
import e3ps.common.util.StringUtils;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.ownership.Ownership;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ManagerException;
import wt.services.StandardManager;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.util.WTException;

public class StandardAdminService extends StandardManager implements AdminService, MessageHelper {

	private static final long serialVersionUID = -7119229681449826200L;

	public static StandardAdminService newStandardAdminService() throws WTException {
		StandardAdminService instance = new StandardAdminService();
		instance.initialize();
		return instance;
	}

	protected synchronized void performStartupProcess() throws ManagerException {
		super.performStartupProcess();
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void loginHistoryAction(String id, String ip) throws WTException {
		LoginHistory loginHistory = null;
		SessionContext prev = SessionContext.newContext();

		Transaction trs = new Transaction();
		try {
			trs.start();

			SessionHelper.manager.setAdministrator();

			loginHistory = LoginHistory.newLoginHistory();
			loginHistory.setIp(ip);
			loginHistory.setId(id);
			loginHistory = (LoginHistory) PersistenceHelper.manager.save(loginHistory);

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
	public CommonCode makeRoot(String codeType) throws WTException {
		Transaction trs = new Transaction();
		CommonCode code = null;
		boolean bool = false;
		try {
			trs.start();

			QuerySpec query = new QuerySpec();

			int idx = query.appendClassList(CommonCode.class, true);

			SearchCondition sc = new SearchCondition(CommonCode.class, CommonCode.CODE_TYPE, "=", codeType);
			query.appendWhere(sc, new int[] { idx });
			query.appendAnd();

			sc = new SearchCondition(CommonCode.class, CommonCode.SORT, "=", 0);
			query.appendWhere(sc, new int[] { idx });
			query.appendAnd();

			sc = new SearchCondition(CommonCode.class, CommonCode.DEPTH, "=", 0);
			query.appendWhere(sc, new int[] { idx });

			QueryResult result = PersistenceHelper.manager.find(query);
			if (result.hasMoreElements()) {
				bool = true;
			}

			if (!bool) {
				code = CommonCode.newCommonCode();
				code.setName(codeType);
				code.setSort(0);
				code.setCode(codeType);
				CommonCodeType cct = CommonCodeType.CUSTOMER;
				if( "CUSTOMER".equals(codeType)) {
					cct = CommonCodeType.CUSTOMER;
				}else if( "INSTALL".equals(codeType)) {
					cct = CommonCodeType.INSTALL;
				}else if( "PROJECT".equals(codeType)) {
					cct = CommonCodeType.PROJECT;
				}
				code.setCodeType(cct);
				code.setDepth(0);
				code.setDescription(codeType);
				code.setUses(true);
				code.setParent(null);

				Ownership ownership = Ownership.newOwnership(SessionHelper.manager.getPrincipal());
				code.setOwnership(ownership);
				PersistenceHelper.manager.save(code);
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
		return code;
	}

	@Override
	public Map<String, Object> deleteCodeAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String url = (String) param.get("url");
		List<String> list = (List<String>) param.get("list");
		CommonCode code = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (String oid : list) {
				code = (CommonCode) rf.getReference(oid).getObject();

				QueryResult qr = PersistenceHelper.manager.navigate(code, "child", ParentChildLink.class);

				boolean isExistData = qr.size() > 0 ? true : false;
				// 존재
				if (isExistData) {
					map.put("result", "FAIL");
					map.put("reload", false);
					map.put("msg", "삭제 하려는 코드아래 하위 코드가 존재 합니다.");
					return map;
				}
				PersistenceHelper.manager.delete(code);
			}

			map.put("result", SUCCESS);
			map.put("msg", "코드가 삭제되었습니다.");
			map.put("url", url);
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "코드 삭제 중 에러가 발생 하였습니다.\n관리자에게 문의하세요.");
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> createCodeAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		System.out.println("parm=" + param);
		String poid = (String) param.get("poid");
		String name = (String) param.get("name");
		String codeValue = (String) param.get("code");
		String text = (String) param.get("text");
		String codeType = (String) param.get("codeType");
		int depths = (int) param.get("depths");

		CommonCode pCode = null;
		CommonCode code = null;
		ReferenceFactory rf = new ReferenceFactory();
		try {
			if(poid != null ) {
				pCode = (CommonCode) rf.getReference(poid).getObject();
			}
			boolean isExist = false;
			if (!StringUtils.isNull(text)) {
				isExist = AdminHelper.manager.isCode(codeType, text, depths);
				// 존재
			} else {
				isExist = AdminHelper.manager.isCode(codeType, name, depths);
				// 존재
			}

			if (isExist) {
				map.put("result", "FAIL");
				map.put("reload", false);
				map.put("msg", "같은 레벨에 동일한 코드 값이 존재합니다.");
				return map;
			} else {
				code = CommonCode.newCommonCode();
				code.setParent(pCode);
				
				CommonCodeType cct = CommonCodeType.CUSTOMER;
				if( "CUSTOMER".equals(codeType)) {
					cct = CommonCodeType.CUSTOMER;
				}else if( "INSTALL".equals(codeType)) {
					cct = CommonCodeType.INSTALL;
				}else if( "PROJECT".equals(codeType)) {
					cct = CommonCodeType.PROJECT;
				}
				
				code.setCodeType(cct);
				code.setDepth(depths);
				code.setSort(0);

				code.setDescription(text);
				code.setName(name);
				code.setCode(codeValue);

				Ownership ownership = Ownership.newOwnership(SessionHelper.manager.getPrincipal());
				code.setOwnership(ownership);
				PersistenceHelper.manager.save(code);
			}

			map.put("coid", code.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("result", SUCCESS);
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", codeType + "코드 생성 중 에러가 발생 하였습니다.\n관리자에게 문의하세요.");
			e.printStackTrace();
		}
		return map;
	}

	@Override
	public void init() throws WTException {
		Transaction trs = new Transaction();
		try {
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
	public Map<String, Object> deteleLoginHistory(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> list = (List<String>) param.get("list");
		String url = (String) param.get("url");
		LoginHistory loginHistory = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (String oid : list) {

				loginHistory = (LoginHistory) rf.getReference(oid).getObject();

				PersistenceHelper.manager.delete(loginHistory);

			}
			map.put("result", SUCCESS);
			map.put("msg", "접속이력이 삭제 되었습니다.");
			map.put("url", url);
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("url", url);
			map.put("msg", "접속이력 삭제 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요.");
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> changePasswordSetting(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
//		List<String> list = (List<String>) param.get("list");
		String complex = (String) param.get("complex");
		String length = (String) param.get("length");
		String range = (String) param.get("range");
		String reset = (String) param.get("reset");

		Transaction trs = new Transaction();
		try {
			trs.start();

			PasswordSetting ps = AdminHelper.manager.getPasswordSetting();

			if (StringUtils.isNull(complex)) {
				ps.setComplex(false);
			} else {
				ps.setComplex(true);
			}

			if (StringUtils.isNull(length)) {
				ps.setLength(false);
			} else {
				ps.setLength(true);

				if (StringUtils.isNull(range)) {
					ps.setPrange(6);
				} else {
					ps.setPrange(Integer.parseInt(range));
				}
			}

			if (StringUtils.isNull(reset)) {
				ps.setReset(3);
			} else {
				ps.setReset(Integer.parseInt(reset));
			}

			PersistenceHelper.manager.modify(ps);

			map.put("result", SUCCESS);
//			map.put("msg", "접속이력이 삭제 되었습니다.");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
//			map.put("msg", "접속이력 삭제 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요.");
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

}
