package e3ps.approval.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.approval.Notice;
import e3ps.common.util.ContentUtils;
import e3ps.common.util.MessageHelper;
import e3ps.common.util.StringUtils;
import e3ps.org.Department;
import e3ps.org.service.OrgHelper;
import wt.fc.PersistenceHelper;
import wt.fc.ReferenceFactory;
import wt.org.WTUser;
import wt.ownership.Ownership;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.WTException;

public class StandardNoticeService extends StandardManager implements NoticeService, MessageHelper {

	private static final long serialVersionUID = 787890950145750745L;

	public static StandardNoticeService newStandardNoticeService() throws WTException {
		StandardNoticeService instance = new StandardNoticeService();
		instance.initialize();
		return instance;
	}

	@Override
	public Map<String, Object> createNoticeAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		Notice notice = null;
		String name = (String) param.get("name");
		String description = (String) param.get("descriptionNotice");
		String dept = (String) param.get("dept");
		ReferenceFactory rf = new ReferenceFactory();
		Department department = null;
		Transaction trs = new Transaction();

		try {
			trs.start();

			if (StringUtils.isNull(dept)) {
				department = OrgHelper.manager.getRoot();
			} else {
				department = (Department) rf.getReference(dept).getObject();
			}

			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			Ownership ownership = Ownership.newOwnership(user);

			notice = Notice.newNotice();
			notice.setName(name);
			notice.setDescription(description);
			notice.setOwnership(ownership);
			notice.setDepartment(department);
			PersistenceHelper.manager.save(notice);

			// ContentUtils.updateSecondary(param, notice);
			ContentUtils.updateContents(param, notice);

			map.put("reload", true);
			map.put("result", SUCCESS);
			map.put("msg", "공지사항이 " + CREATE_OK);
			map.put("url", "/Windchill/plm/approval/listNotice");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("reload", false);
			map.put("result", FAIL);
			map.put("msg", "공지사항 " + CREATE_FAIL);
			map.put("url", "/Windchill/plm/approval/createNotice");
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> deleteNoticeAction(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> list = (List<String>) param.get("list");
		Notice notice = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (String oid : list) {
				notice = (Notice) rf.getReference(oid).getObject();

				// NoticeViewData data = new NoticeViewData(notice);
				//
				// if (!data.isDelete) {
				// map.put("result", SUCCESS);
				// map.put("msg", "공지사항(" + data.name + ") 삭제 권한이 없습니다.");
				// map.put("url", "/Windchill/plm/approval/listNotice");
				// return map;
				// }

				PersistenceHelper.manager.delete(notice);
			}

			map.put("result", SUCCESS);
			map.put("msg", "공지사항이 " + DELETE_OK);
			map.put("url", "/Windchill/plm/approval/listNotice");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "공지사항 " + DELETE_FAIL);
			map.put("url", "/Windchill/plm/approval/listNotice");
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> modifyNoticeAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		Notice notice = null;
		String oid = (String) param.get("oid");
		String name = (String) param.get("name");
		String description = (String) param.get("descriptionNotice");
		String dept = (String) param.get("dept");
		ReferenceFactory rf = new ReferenceFactory();
		Department department = null;
		Transaction trs = new Transaction();
		try {
			trs.start();

			notice = (Notice) rf.getReference(oid).getObject();
			if (StringUtils.isNull(dept)) {
				department = OrgHelper.manager.getRoot();
			} else {
				department = (Department) rf.getReference(dept).getObject();
			}

			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			Ownership ownership = Ownership.newOwnership(user);

			notice.setName(name);
			notice.setDescription(description);
			notice.setOwnership(ownership);
			notice.setDepartment(department);
			PersistenceHelper.manager.modify(notice);

			// ContentUtils.updateSecondary(param, notice);
			ContentUtils.updateContents(param, notice);

			map.put("reload", true);
			map.put("result", SUCCESS);
			map.put("msg", "공지사항이 " + MODIFY_OK);
			map.put("url", "/Windchill/plm/approval/listNotice");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("reload", false);
			map.put("result", FAIL);
			map.put("msg", "공지사항 " + MODIFY_FAIL);
			map.put("url", "/Windchill/plm/approval/modifyNotice?oid=" + oid);
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public void aaa() throws Exception {
		Notice notice = Notice.newNotice();
		notice.setName("공지사항");
		notice.setDescription("설명");
		PersistenceHelper.manager.save(notice);
	}
}
