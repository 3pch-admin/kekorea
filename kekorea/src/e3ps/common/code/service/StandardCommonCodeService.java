package e3ps.common.code.service;

import java.util.HashMap;
import java.util.Map;

import e3ps.common.code.CommonCode;
import e3ps.common.code.CommonCodeType;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.MessageHelper;
import wt.fc.PersistenceHelper;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardCommonCodeService extends StandardManager implements CommonCodeService, MessageHelper {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4263809582550177866L;

	public static StandardCommonCodeService newStandardCommonCodeService() throws WTException {
		StandardCommonCodeService instance = new StandardCommonCodeService();
		instance.initialize();
		return instance;
	}

	@Override
	public Map<String, Object> createCodeAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String customer = (String) param.get("customer");
		String install = (String) param.get("install");
		CommonCode commonCode = null;
		Transaction trs = new Transaction();
		try {
			trs.start();

			commonCode = CommonCode.newCommonCode();
			commonCode.setName(customer);
			commonCode.setCode(install);
			commonCode.setCodeType(CommonCodeType.toCommonCodeType("INSTALL"));
			commonCode.setDepth(0);
			commonCode.setDescription(install);
			commonCode.setSort(0);

			PersistenceHelper.manager.save(commonCode);

			map.put("result", SUCCESS);
			map.put("msg", "코드가 " + CREATE_OK);
			map.put("reload", true);
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("reload", false);
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}
	
	@Override
	public Map<String, Object> modifyCodeAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String commonName = (String) param.get("commonName");
		String commonCode = (String) param.get("commonCode");
		String oid = (String) param.get("oid");
		String description =  (String) param.get("description");
		boolean uses =  (boolean) param.get("uses");
		
		CommonCode code = null;
		Transaction trs = new Transaction();
		try {
			trs.start();

			System.out.println("######## modify code##="+oid);
			System.out.println("######## modify uses##="+uses);
			System.out.println("######## modify commonCode##="+commonCode);
			System.out.println("######## modify description##="+description);
			System.out.println("######## modify commonName##="+commonName);
			
			code = (CommonCode)CommonUtils.getObject(oid);
			
			if( code != null) {
				
				code.setName(commonName);
				code.setCode(commonCode);
				code.setDescription(description);
				
				code.setUses(uses);

				PersistenceHelper.manager.modify(code);
			}

			map.put("result", SUCCESS);
			map.put("msg", "코드가 " + MODIFY_OK);
			map.put("reload", true);
			map.put("url", "/Windchill/plm/admin/manageCode");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("reload", false);
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

}
