package e3ps.project.template.service;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import e3ps.common.util.DateUtils;
import e3ps.common.util.StringUtils;
import e3ps.project.template.Template;
import wt.fc.PersistenceHelper;
import wt.org.WTUser;
import wt.ownership.Ownership;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.WTException;

public class StandardTemplateService extends StandardManager implements TemplateService {

	public static StandardTemplateService newStandardTemplateService() throws WTException {
		StandardTemplateService instance = new StandardTemplateService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(Map<String, Object> params) throws Exception {
		String name = (String) params.get("name");
		String duration = (String) params.get("duration");
		String description = (String) params.get("description");
		String enable = (String) params.get("enable");
		Transaction trs = new Transaction();
		try {
			trs.start();

			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			Ownership ownership = Ownership.newOwnership(user);

			Template template = Template.newTemplate();
			template.setName(name);
			template.setEnable(Boolean.parseBoolean(enable));
			template.setOwnership(ownership);
			template.setDuration(1);
			template.setDescription(description);

			Timestamp start = DateUtils.getPlanStartDate();
			template.setPlanStartDate(start);

			Calendar eCa = Calendar.getInstance();
			eCa.setTimeInMillis(start.getTime());
			eCa.add(Calendar.DATE, 1);

			Timestamp end = new Timestamp(eCa.getTime().getTime());
			template.setPlanEndDate(end);

			PersistenceHelper.manager.save(template);

//			if (!StringUtils.isNull(templateOid)) {
//				// 템플릿 복사...
//				copy = (Template) rf.getReference(templateOid).getObject();
//				copyTasksInfo(template, copy);
//
//				copyTemplateInfo(template, copy);
//			}
//
//			commit(template);

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
