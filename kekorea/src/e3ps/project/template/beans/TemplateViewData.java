package e3ps.project.template.beans;

import e3ps.common.util.DateUtils;
import e3ps.project.template.Template;
import e3ps.project.template.service.TemplateHelper;
import lombok.Getter;
import lombok.Setter;
import wt.org.WTUser;

@Getter
@Setter
public class TemplateViewData {

	private String oid;
	private Template template;
	private String name;
	private String description;
	private int duration;
	private String creator;
	private String createdDate;
	private WTUser pm;
	private WTUser subPm;

	public TemplateViewData() {

	}

	public TemplateViewData(Template template) throws Exception {
		setOid(template.getPersistInfo().getObjectIdentifier().getStringValue());
		setTemplate(template);
		setName(template.getName());
		setDescription(template.getDescription());
		setDuration(DateUtils.getDuration(template.getPlanStartDate(), template.getPlanEndDate()));
		setCreator(template.getOwnership().getOwner().getFullName());
		setCreatedDate(template.getCreateTimestamp().toString().substring(0, 10));
		setPm(TemplateHelper.manager.getUser(template, "PM"));
		setSubPm(TemplateHelper.manager.getUser(template, "SUB_PM"));
	}
}
