package e3ps.project.dto;

import e3ps.common.util.ContentUtils;
import e3ps.common.util.DateUtils;
import e3ps.common.util.StringUtils;
import e3ps.project.Template;
import e3ps.project.service.TemplateHelper;
import wt.org.WTUser;

public class TemplateViewData {

	public Template template;
	public String oid;
	public String name;
	public String description;
	public String state;
	public String duration;
	public String creator;
	public String createDate;
	public String modifier;
	public String modifyDate;
	public String iconPath;

	public WTUser pm;

	public WTUser subPm;

	public TemplateViewData(Template template) throws Exception {
		this.template = template;
		this.oid = template.getPersistInfo().getObjectIdentifier().getStringValue();
		this.name = template.getName();
		this.description = StringUtils.replaceToValue(template.getDescription());
		this.state = template.getState();
		this.duration = DateUtils.getDuration(template.getPlanStartDate(), template.getPlanEndDate()) + "일";
		this.creator = template.getOwnership().getOwner().getFullName();
		this.createDate = template.getCreateTimestamp().toString().substring(0, 16);
		this.modifier = template.getUpdateUser().getOwner().getFullName();
		this.modifyDate = template.getModifyTimestamp().toString().substring(0, 16);
		this.iconPath = ContentUtils.getStandardIcon(template);
		this.pm = TemplateHelper.manager.getPMByTemplate(template);
		this.subPm = TemplateHelper.manager.getSubPMByTemplate(template);
	}
}